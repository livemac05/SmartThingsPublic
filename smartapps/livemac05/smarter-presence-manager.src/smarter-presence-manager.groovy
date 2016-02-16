/**
 *  Smarter Presence Manager
 * 
 *  Copyright 2015 Brandon Reeves
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Smarter Presence Manager",
    namespace: "livemac05",
    author: "Brandon Reeves",
    description: "Manage modes, lights, and more based on presence and time of day",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Presence Monitoring") {
		input "people", "capability.presenceSensor", multiple: true
	}

	section("Mode Settings") {
		input "newAwayMode",    "mode", title: "Everyone is away"
		input "newNightMode",  "mode", title: "At least one person home and nighttime"
		input "newMorningMode", "mode", title: "At least one person home and morning"
		input "newAfternoonMode", "mode", title: "At least one person home and daytime"
		input "keepModes", "mode", title: "Do not overwrite these modes, except when everyone leaves", multiple: true
	}
	
	section("Away threshold (defaults to 10 min)") {
		input "awayThreshold", "decimal", title: "Number of minutes", required: false
	}
	
	section("AWAY Security") {
		input "awayLightsOn", "capability.switch", title: "Turn these on when everyone leaves", multiple: true, required:false
		input "awayLightsOnAfterSunset", "capability.switch", title: "Also turn these on after sunset", multiple: true, required:false
		input "awayLightsOff", "capability.switch", title: "Turn these off when everyone leaves", multiple: true, required:false
	}
	
	section("AWAY Security Arrival"){
		input "awayLightsArriveOn", "capability.switch", title: "Turn these on when someone arrives", multiple: true, required:false
		input "awayLightsArriveOnAfterSunset", "capability.switch", title: "Also turn these on after sunset", multiple: true, required:false
		input "awayLightsArriveOff", "capability.switch", title: "Turn these off when someone arrives", multiple: true, required:false
	}
    
    section("Times") {
        input "noonTime", "time", title: "Noon", required: true
    }

	section("Notifications") {
		input "sendPushMessage", "enum", title: "Send a push notification?", metadata:[values:["Yes","No"]], required:false
	}
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	log.debug "Initializing"
	subscribe(people,   "presence", presence)
	subscribe(location, "sunrise",  setSunrise)
	subscribe(location, "sunset",   setSunset)
	
	schedule("0 0 12 * * ?", setAfternoon)

	state.sunMode = location.mode
    state.nightBeforeMidnight = false
	
	updateSunMode()
    log.debug "Initialized"
}

def setSunrise(evt) {
	log.debug "Sunrise event"
	changeSunMode(newMorningMode)
}

def setSunset(evt) {
	log.debug "Sunset event"
    sendPushMessage("Sunset Event!")
	changeSunMode(newNightMode)
}

def setAfternoon(evt) {
	log.debug "Noon event"
	changeSunMode(newAfternoonMode)
}

def changeSunMode(newMode) {
	state.sunMode = newMode

	if (everyoneIsAway() && (location.mode == newAwayMode)) {
		log.debug("Mode is away, not evaluating")
		if (newMode == newNightMode) {
			log.info "Applying AWAY Security after sunset"
            setAwayLights()
		}
	} else if (location.mode != newMode) {
    	log.debug "Determining whether or not to overwrite mode"
		// Determining whether to overwrite mode
		if (newMode != newMorningMode && location.mode in keepModes) {
            def message = "'${newMode}' does not override '${location.mode}'. Declining to change mode"
            send(message)
            return
		}
		def message = "Changed mode to '${newMode}'"
		send(message)
		setLocationMode(newMode)
	} else {
		log.debug("Mode is the same, not evaluating")
	}
}

def presence(evt) {
	if (evt.value == "not present") {
		log.debug("Checking if everyone is away")

		if (everyoneIsAway()) {
			log.info("Starting ${newAwayMode} sequence")
			def delay = (awayThreshold != null && awayThreshold != "") ? awayThreshold * 60 : 10 * 60
			runIn(delay, "setAway")
		}
	} else {
		if (location.mode != state.sunMode) {
			log.debug("Checking if anyone is home")
			
			if (location.mode == newAwayMode) {
                // AWAY Security Lights Off
                log.info "AWAY Security Arrival: setting lights"
                awayLightsArriveOn?.on()
                awayLightsArriveOff?.off()
                // AWAY Security Lights After Sunset
				updateSunMode()
                if (state.sunMode == newNightMode && state.nightBeforeMidnight) {
                    log.info "AWAY Security Arrival: setting nighttime lights"
                    awayLightsArriveOnAfterSunset?.on()
                }
			}

			if (anyoneIsHome()) {
				log.info("Starting ${state.sunMode} sequence")
				updateSunMode()
				changeSunMode(state.sunMode)
			}
		} else {
			log.debug("Mode is the same, not evaluating")
		}
	}
}

def updateSunMode () {
    def now = new Date()
    def sun = getSunriseAndSunset()
    state.nightBeforeMidnight = false

    if (now > getNoon() && now >= sun.sunset) {
        log.debug "Between sunset and midnight, updating sun mode to Night mode"
        state.sunMode = newNightMode
        state.nightBeforeMidnight = true
    } else if (now < sun.sunrise) {
        log.debug "Between midnight and sunrise, updating sun mode to Night mode"
        state.sunMode = newNightMode
    } else if (now >= sun.sunrise && now < getNoon()) {
        log.debug "Between sunrise and noon, updating sun mode to Morning mode"
    	state.sunMode = newMorningMode
    } else if (now >= getNoon() && now < sun.sunset) {
		log.debug "Between noon and sunset, updating sun mode to Afternoon mode"
		state.sunMode = newAfternoonMode
    } else {
    	log.error "Unknown time of day. Logic error. Defaulting to Home"
        state.sunMode = newAfternoonMode
    }
}

// Run AWAY security setup
def setAwayLights() {
    // AWAY Security Lights
    log.info "AWAY Security: setting lights"
    awayLightsOn?.on()
    for (light in awayLightsOn) {
        try {
                light?.setLevel(100)
        } catch (e) {}
    }
    awayLightsOff?.off()
    // AWAY Security Lights After Sunset
    updateSunMode()
    if (state.sunMode == newNightMode && state.nightBeforeMidnight) {
        log.info "AWAY Security: setting nighttime lights"
        awayLightsOnAfterSunset?.on()
        for (light in awayLightsOnAfterSunset) {
            try {
                    light?.setLevel(100)
            } catch (e) {}
        }
    }
    
    // Run again in 5 minutes + 10 seconds
    runIn(5*60+10, setAwayLightsRecurse)
}

def setAwayLightsRecurse() {
	log.debug "5 minutes passed, checking if Away"
    if (location.mode == newAwayMode) {
    	log.debug "AWAY Security: In Away mode, running AWAY Security again"
    	setAwayLights()
    } else {
    	log.debug "AWAY Security: No longer in Away mode. Not setting AWAY lights, and stopping schedule"
	}
}

def setAway() {
	if (everyoneIsAway()) {
		if (location.mode != newAwayMode) {
			def message = "Changed mode to '${newAwayMode}' because everyone left home"
			send(message)
			setLocationMode(newAwayMode)
		} else {
			log.debug("Mode is the same, not evaluating")
		}
        setAwayLights()
	} else {
		log.info("Somebody returned home before we set to '${newAwayMode}'")
	}
}

private everyoneIsAway() {
	def result = true

	if (people.findAll { it?.currentPresence == "present" }) {
		result = false
	}

	log.debug("everyoneIsAway: ${result}")

	return result
}

private anyoneIsHome() {
	def result = false

	if (people.findAll { it?.currentPresence == "present" }) {
		result = true
	}

	log.debug("anyoneIsHome: ${result}")

	return result
}

private send(msg) {
	if (sendPushMessage != "No") {
		log.debug("Sending push message")
		sendPush(msg)
	}

	log.info(msg)
}

private getNoon () {
	def time = noonTime
	return new Date(timeToday(time, location?.timeZone).time)
}