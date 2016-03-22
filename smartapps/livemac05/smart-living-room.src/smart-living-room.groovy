/**
 *  Smart Living Room
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
    name: "Smart Living Room",
    namespace: "livemac05",
    author: "Brandon Reeves",
    description: "Controls entertainment devices, an integrated alarm, and lights.",
    category: "My Apps",
    iconUrl: "http://cdn7.staztic.com/app/a/318/318113/plex-for-android-3164-l-124x124.png",
    iconX2Url: "https://plex.tv/assets/android-icon-7dd76d9ccd8a0cc512f0055d15803cfb.png",
    iconX3Url: "http://a1.mzstatic.com/us/r30/Purple/v4/49/5b/c7/495bc75d-2cd3-3793-2a5b-618482a114b1/mzl.ryrrxdqt.png",
    oauth: true)


preferences {
	section("Alarm System") {
		// TODO: put inputs here
        paragraph "A RESTful alarm controller that accepts ST alarm GET requests: /siren and /off"
        input "alarmServerIP", "string", title:"Alarm Controller IP Address", description: "IP Address", defaultValue: '10.0.1.16', required: false, displayDuringSetup: true
        input "alarmServerPort", "string", title:"Alarm Controller Port", description: "Port", defaultValue: 9999 , required: false, displayDuringSetup: true
        input "alarmDevice", "capability.alarm", title:"Alarm Device", description: "SmartThings Alarm Device", required: false, displayDuringSetup: true
    	input "alarmSwitches", "capability.switch", title:"Alarm Strobe Lights", description: "Lights to flash during alarm event", multiple: true, required: false, displayDuringSetup: true
    	input "alarmLightsOff", "capability.switch", title:"Alarm Off Lights", description: "Lights to off during alarm event", multiple: true, required: false, displayDuringSetup: true
    	input "alarmOffSwitch", "capability.switch", title: "Alarm Killswitch", description: "Optional device to disable alarm via switch", required:false, displayDuringSetup:true
	}
    
    section("Automatic Lights") {
    	paragraph "Lights to be controlled via entertainment events"
        input "watchAnyLightsDim", "capability.switch", title:"Dim these lights when playing anything", required: false, multiple: true, displayDuringSetup: true
        input "watchAnyLightsOff", "capability.switch", title:"Turn these lights off when playing anything", required: false, multiple: true, displayDuringSetup: true
        input "watchMovieLightsOff", "capability.switch", title:"Turn these lights off during movies", required: false, multiple: true, displayDuringSetup: true
        input "watchPauseLightsOn", "capability.switch", title:"Turn these lights during pause", required: false, multiple: true, displayDuringSetup: true
        input "watchPauseLightsOnDim", "capability.switch", title:"Turn these lights on dimly during pause", required: false, multiple: true, displayDuringSetup: true
        input "watchPauseLightsOnLIFX", "capability.switch", title:"Turn these LIFX lights on during pause", required: false, multiple: true, displayDuringSetup: true
        input "watchPauseLightsOnLIFXDim", "capability.switch", title:"Turn these LIFX lights on dimly during pause", required: false, multiple: true, displayDuringSetup: true
        input "watchEndLightsOn", "capability.switch", title:"Turn these lights after playback is done", required: false, multiple: true, displayDuringSetup: true
		input "watchEndLightsOnLIFX", "capability.switch", title:"Turn these LIFX lights after playback is done", required: false, multiple: true, displayDuringSetup: true
		input "keepModes", "mode", title: "Do not overwrite these modes", multiple: true
        input "noLightModes", "mode", title: "No lights on stop in these modes", multiple: true, required:false
        input "lowLightModes", "mode", title: "Dim lights only in these modes", multiple: true, required:false
   }
    
    
    section("Automatic Lights (2nd)") {
    	paragraph "Lights to be controlled via entertainment events"
        input "watchSecondaryAnyLightsDim", "capability.switch", title:"Dim these lights when playing anything", required: false, multiple: true, displayDuringSetup: true
        input "watchSecondaryAnyLightsOff", "capability.switch", title:"Turn these lights off when playing anything", required: false, multiple: true, displayDuringSetup: true
        input "watchSecondaryMovieLightsOff", "capability.switch", title:"Turn these lights off during movies", required: false, multiple: true, displayDuringSetup: true
        input "watchSecondaryPauseLightsOn", "capability.switch", title:"Turn these lights during pause", required: false, multiple: true, displayDuringSetup: true
        input "watchSecondaryEndLightsOn", "capability.switch", title:"Turn these lights after playback is done", required: false, multiple: true, displayDuringSetup: true
        input "secondaryNoLightModes", "mode", title: "No lights on stop in these modes", multiple: true, required:false
        input "secondaryLowLightModes", "mode", title: "Dim lights only in these modes", multiple: true, required:false
    }
    
    section("TV Device") {
		paragraph "SmartThings TV Device"
        input "television", "capability.switch", title: "TV or Projector", required: false, displayDuringSetup: true
        input "televisionIP", "string", title: "TV Control Address", defaultValue: '10.0.1.80', required: false, displayDuringSetup: true
        input "televisionPort", "string", title: "TV Control Port", defaultValue: 80, required: false, displayDuringSetup: true
    }
    
    section("Receiver Device") {
		paragraph "SmartThings Audio Receiver Device"
        input "receiver", "capability.switch", title: "Audio Receiver", required: false, displayDuringSetup: true
        input "receiverIP", "string", title: "Receiver Control Address", defaultValue: '10.0.1.30', required: false, displayDuringSetup: true
        input "receiverPort", "string", title: "Receiver Control Port", defaultValue: 80, required: false, displayDuringSetup: true
    }
    
    section("Automatic Media Center") {
    	paragraph "Automatically power media center off via events"
        input "plexAddr", "string", title: "Address of PHT/PMP", required: false, displayDuringSetup:true
        input "livingRoomSensor", "capability.motionSensor", title: "Living Room Motion Sensors", required: false, displayDuringSetup: true
        input "livingRoomInactiveTimeout", "number", title: "Minutes of Inactivity Before Auto Off", required:false, displayDuringSetup: true, default: 5
        input "livingRoomActiveTimeout", "number", title: "While Watchng, Minutes of Inactivity Before Auto Off", required:false, displayDuringSetup: true, default: 20
	}
    
    section("Anti Fall Asleep") {
        input "livingRoomPauseTimeout", "number", title: "While Watchng, Minutes of Inactivity Before Pausing", required:false, displayDuringSetup: true, default: 5
    }
    
    section("Interruption Pausing") {
    	input "pauseMotionSensors", "capability.motionSensor", title: "Motion sensors to pause plex", required: false, multiple: true
    	input "pauseContactSensors", "capability.contactSensor", title: "Contact sensors to pause plex", required: false, multiple: true
    }
        
}

mappings {
	path("/room/:room/:scene") {
    	action: [
        	PUT: "setLightingScene"
        ]
    }
    path("/test") {
    	action: [
        	GET: "testEndpoint"
        ]
    }
    path("/receiverState/:powered") {
    	action: [
        	PUT: "setReceiverState"
        ]
    }
}

def testEndpoint() {
	return [test: true]
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
	subscribe(alarmDevice, "siren", sirenHandler)
    subscribe(alarmDevice, "strobe", flashLights)
    subscribe(alarmDevice, "off", flashLights)
    subscribe(alarmOffSwitch, "switch", offHandler)
    
    subscribe(receiver, "switch", receiverHandler)
    subscribe(receiver, "music Player.pause", plexPause)
    //subscribe(television, "switch", televisionHandler)
    
    subscribe(livingRoomSensor, "motion", livingRoomMotionHandler)
    
    subscribe(pauseMotionSensors, "motion.active", plexPause)
    subscribe(pauseContactSensors, "contact.open", plexPause)
    
    receiverPoll()
    
    schedule("0 * * * * ?", receiverPoll)
    //televisionPoll()
    state.roomOneInactive = null
    state.roomOnePauseTime = null
}
    	


/************************ ALARM CONTROLLER *************************/
def sirenHandler(evt) {
	if (evt.value == "on") {
        startSiren()
	} else {
       	stopSiren()
    }
}

def offHandler(evt) {
	if (evt.value == "off") {
    	stopSiren()
    }
}

def startSiren() {
	log.debug "Starting siren"
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/siren",
        headers: [
            HOST: "${alarmServerIP}:${alarmServerPort}"
        ]
    )
    sendHubCommand(result)
}

def stopSiren () { 
    log.debug "Stopping siren"
    def result = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/off",
        headers: [
            HOST: "${alarmServerIP}:${alarmServerPort}"
        ]
    )
    sendHubCommand(result)
    alarmOffSwitch.on()
    alarmDevice.off()
}

def flashLights(evt) {

	if (evt.value == 'on') {
        state.doFlash = true
        state.numFlashes = 20

        //log.debug "FLASHING ${state.numFlashes} times"
        
        flashOff()
        alarmLightsOff?.off()
        /*for (def i = 4; i < state.numFlashes; i+=4) {
        	log.debug "Setting flash for ${i} and ${i + 2}"
        	runIn(i, flashOn)
            runIn(i + 2, flashOff)
        }*/
    } else {
    	state.doFlash = false
        log.debug "STOPPING FLASH"
    }
}

def flashOn(evt) {
	if (state.doFlash && state.numFlashes) {
    	state.numFlashes = state.numFlashes - 1
    	log.debug("FLASH ON")
        try{
            alarmSwitches?.on()
        } catch (th) {
        	log.debug "Exception when trying to set alarm switch levels"
        }
        runIn(10, flashOff)
        alarmLightsOff?.off()
    }
}

def flashOff(evt) {
	if (state.doFlash && state.numFlashes) {
    	log.debug("FLASH OFF")
    	state.numFlashes = state.numFlashes - 1
        try {
       		alarmSwitches?.off()
        } catch (th) {
        	log.debug "Exception when trying to set alarm switch levels"
        }
        flashOn()
        //runIn(1, flashOn)
    }
}

/************************ RECEIVER CONTROLLER *************************/

def receiverHandler(evt) {
	if (evt.value == 'on') {
    	receiverOn(evt)
    } else {
    	receiverOff(evt)
    }
}

def receiverPoll() {
	log.debug "Receiver Poll"
    def params = [
    	uri: "http://${receiverIP}:${receiverPort}",
        path: "/StatusHandler.asp",
        contentType: "application/json"
    ]
    try {
        httpGet(params) { resp ->
        	def powered = resp.data?.Z[0]?.P
            log.debug "Receiver Power state: ${powered}"
            if (powered) {
            	receiver.on()
            } else {
        		receiver.off()
            }
        }
    } catch (e) {
		log.error "Unable to send poll to receiver: $e"
    }
}

def setReceiverState (evt) {
	def powered = params.powered
    log.debug "Received receiver state from endpoint ${powered}"
    if (powered == "true") {
    	log.debug "Updating receiver to on"
        receiver?.on()
    } else {
    	log.debug "Updating receiver to off"
        receiver?.off()
    }
}

def receiverOn(evt) {
	log.debug "Receiver On"
    
    def params = [
    	uri: "http://${receiverIP}:${receiverPort}",
        path: "/EventHandler.asp",
        contentType: "application/json",
        query: [
        	WebToHostItem: 'PO'
        ]
    ]
    try {
        httpGet(params) { resp ->
        	receiver?.on()
        }
    } catch (e) {
		log.error "Unable to send poll to receiver: $e"
    }
    
    if (state.roomOneScene != "music") {
        televisionOn()
    } else {
    	log.debug "Not turning tv on since music"
    }
}

def receiverOff(evt) {
	log.debug "Reciever off"
    
    
    def params = [
    	uri: "http://${receiverIP}:${receiverPort}",
        path: "/EventHandler.asp",
        contentType: "application/json",
        query: [
        	WebToHostItem: 'PF'
        ]
    ]
    try {
        httpGet(params) { resp ->
        	receiver?.off()
        }
    } catch (e) {
		log.error "Unable to send poll to receiver: $e"
    }
    
    //television.off()
    runIn(10, receiverOffTelevisionOff)
}

def receiverOffTelevisionOff() {
	def params = [
    	uri: "http://${receiverIP}:${receiverPort}",
        path: "/StatusHandler.asp",
        contentType: "application/json"
    ]
    try {
        httpGet(params) { resp ->
        	def powered = resp.data?.Z[0]?.P
            log.debug "Receiver Power state: ${powered}"
            if (powered) {
            	
                log.debug "Receiver turned back on, not turning projector off"
            } else {
                log.debug "Receiver stayed off, turning projector off"
        		televisionOff()
            }
        }
    } catch (e) {
		log.error "Unable to send poll to receiver: $e"
    }
/*	if (receiver.currentState('switch') == "off") {
    	televisionOff()
    } else {
    }*/
}

/************************ TELEVISION CONTROLLER *************************/
/*
def televisionHandler(evt) {
	if (evt.value == 'on') {
    	televisionOn(evt)
    } else {
    	televisionOff(evt)
    }
}

def televisionPoll() {
	log.debug "Television Poll"
    def params = [
    	uri: "http://${televisionIP}:${televisionPort}",
        path: "/power/getJSON",
        contentType: "application/json"
    ]
    try {
        httpGet(params) { resp ->
        	def powered = resp.data?.power
            log.debug "TV Power state: ${powered}"
            if (powered) {
            	television.on()
            } else {
        		television.off()
            }
        }
    } catch (e) {
		log.error "Unable to send poll to television: $e"
    }
    
    runIn(30, televisionPoll)
}*/


def televisionOn(evt) {
	log.debug "Television On"
    
    def params = [
    	uri: "http://${televisionIP}:${televisionPort}",
        path: "/power/on"
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send on to television: $e"
    }
}

def televisionOff(evt) {
	log.debug "Television off"
    
    
    def params = [
    	uri: "http://${televisionIP}:${televisionPort}",
        path: "/power/off"
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send off to television: $e"
    }
}

def musicOnTelevisionOff(evt) {
	log.debug "Checking if music playing"
    
    if (state.roomOneScene == "music") {
    	log.debug "music playing, turning tv off"
    	televisionOff()
    }
}

/************************ AUTO MEDIA CENTER *************************/
def livingRoomMotionHandler (evt) {
	if (evt.value == "active") {
    	log.debug "Room One Motion"
        def now = new Date()
        if (state.roomOnePauseTime) {
        	def pauseTime = Date.parse("YYYY-MM-dd'T'HH:mm:ssZ", state.roomOnePauseTime)
            def diffMillis = now.getTime() - pauseTime.getTime()
            if (diffMillis < 10*1000) {
                log.debug "Motion soon enough after auto-pause, playing"
                plexPlay()
            } else {
            	log.debug "Motion stale, not playing" + diffMillis
            }
    	}
    } else {
    	log.debug "Room One Inactive"
        state.roomOneInactive = new Date()
        
        if (state.roomOneScene == "stopped") {
        	log.debug "Room One has become inactive with no media playing. Starting inactive timer"
        	runIn(livingRoomInactiveTimeout*60+1, livingRoomInactiveTimer, [overwrite: true])
        } else {
        	log.debug "Room One has become inactive while media is playing. Starting active and pause timer"
            runIn(livingRoomActiveTimeout*60+1, livingRoomActiveTimer, [overwrite: true])
            runIn(livingRoomPauseTimeout*60+1, livingRoomPauseTimer, [overwrite: true])
        }
    }
}

def livingRoomInactiveTimer () {
    if (livingRoomSensor.currentValue('motion') == "active") {
        log.debug "Room One inactive timer expired, but motion, so returning"
    	// Motion became active again
    	return
    } 
    
    if (!state.roomOneInactive) {
    	log.warn "No inactive time, can't check"
        return
    }
    
    def inactiveTime = Date.parse("YYYY-MM-dd'T'HH:mm:ssZ", state.roomOneInactive)
    def now = new Date()
    def diffMillis = now.getTime() - inactiveTime.getTime()
    
    if (diffMillis < livingRoomInactiveTimeout*60*1000) {
    	log.warn "Room one timer expired before timeout. Cancelling"
        return
    }
    
    if (state.roomOneScene != "stopped") {
    	log.debug "Room One has started playing media. Cancelling timer"
        return
    }
    
    log.debug "Room One inactive timer expired, motion, so turning off"
    log.info "Room One Turning Off Due To Inactivity w/o media playing"
    // Motion isn't active and hasn't between start of runIn
    receiverOff()
    televisionOff()
}

def livingRoomActiveTimer () {
    if (livingRoomSensor.currentValue('motion') == "active") {
        log.debug "Room One active timer expired, but motion, so returning"
    	// Motion became active again
    	return
    }
    
    if (!state.roomOneInactive) {
    	log.warn "No inactive time, can't check"
        return
    }
    
    def inactiveTime = Date.parse("YYYY-MM-dd'T'HH:mm:ssZ", state.roomOneInactive)
    def now = new Date()
    def diffMillis = now.getTime() - inactiveTime.getTime()
    
    if (diffMillis < livingRoomActiveTimeout*60*1000) {
    	log.warn "Room one timer expired before timeout. Cancelling"
        return
    }
    
    if (state.roomOneScene == "stopped") {
    	log.debug "Room One has stopped playing media. Cancelling timer"
        return
    }
    
    if (state.roomOneScene == "music") {
    	log.debug "Room one is playing music, presence not required, not turning off"
        return
    }
    
    log.debug "Room One inactive timer expired, motion, so turning off"
    log.info "Room One Turning Off Due To Inactivity with media playing"
    // Motion isn't active and hasn't between start of runIn
    receiverOff()
    // Give chance to turn back on in case of mistake
    //televisionOff()
}

def livingRoomPauseTimer () {
    if (livingRoomSensor.currentValue('motion') == "active") {
        log.debug "Room One pause timer expired, but motion, so returning"
    	// Motion became active again
    	return
    }
    
    if (!state.roomOneInactive) {
    	log.warn "No inactive time, can't check"
        return
    }
    
    def inactiveTime = Date.parse("YYYY-MM-dd'T'HH:mm:ssZ", state.roomOneInactive)
    def now = new Date()
    def diffMillis = now.getTime() - inactiveTime.getTime()
    
    if (diffMillis < livingRoomPauseTimeout*60*1000) {
    	log.warn "Room one timer expired before timeout. Cancelling"
        return
    }
    
    if (state.roomOneScene == "stopped") {
    	log.debug "Room One has stopped playing media. Cancelling pause timer"
        return
    }
    
    log.debug "Room One pause timer expired, motion, so pausing"
    log.info "Room One pausing media playing due to inactivity"
    // Motion isn't active and hasn't between start of runIn
    plexPause()
    state.roomOnePauseTime = new Date()
}

def plexPause(evt) {
	log.info "Pausing Plex"
    
    def params = [
    	uri: "http://${plexAddr}:3005",
        path: "/player/playback/pause",
        contentType: "application/xml",
        query: [
        	commandID: 1
        ]
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send pause to plex: $e"
    }
}

def plexPlay(evt) {
	log.info "Playing Plex"
    
    def params = [
    	uri: "http://${plexAddr}:3005",
        path: "/player/playback/play",
        contentType: "application/xml",
        query: [
        	commandID: 1
        ]
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send play to plex: $e"
    }
}

/*
	if (receiver.currentSwitch == 'on') {
    	log.debug "Path: Ignoring motion, receiver already on"
        return
    }
	if (evt.value == "active") {    	
        def currSensors = livingRoomSensors.currentValue('motion')
        def onSensors = currSensors.findAll { sensorVal ->
            sensorVal == "active" ? true : false
        }
        
        if (state.pathCompleting) {

            if (onSensors.size() == pathOneSensors.size()) {
                log.info "Path: PATH - Completed"
                state.pathCompleting = false
                state.pathCompleted = true
                if (aggressivePath) {
                    log.debug "Aggressive path, turning receiver on"
                    receiver?.on()
                }
                runIn(pathCancelWindow, pathCompletionChecker)
            } else {
                log.debug "Path: PATH - Partially Completed"
            }
            
    	} else {
        	log.debug "Path: Not completing"
        }
	}
}

def pathOneMotionHandler (evt) {
	if (receiver.currentSwitch == 'on' && !state.pathCompleted) {
    	log.debug "Path: Ignoring motion, receiver already on"
        return
    }
	if (evt.value == "active") { 
    	if (state.pathCompleted) {
        	log.info "Path: Path re-activated, cancelling"
        	cancelPath()
        }
        
        def currSensors = pathOneSensors.currentValue('motion')
        def onSensors = currSensors.findAll { sensorVal ->
            sensorVal == "active" ? true : false
        }
        
        if ((state.pathCompleting || state.pathCompleted) && state.path != 1) {
        	log.info "Path: PATH EXTENDED - Cancelling path"
            cancelPath()
        }

        if (onSensors.size() == pathOneSensors.size()) {
            log.info "Path: PATH 1 - Completing"
            state.pathCompleting = true
            state.path = 1
            runIn(pathTimeoutWindow, pathTimeoutChecker)
        } else {
            log.debug "Path: PATH 1 - Partially Inactive"
        }
	}
}

def pathTwoMotionHandler (evt) {
	if (receiver.currentSwitch == 'on' && !state.pathCompleted) {
    	log.debug "Path: Ignoring motion, receiver already on"
        return
    }
	if (evt.value == "active") { 
    	if (state.pathCompleted) {
        	log.info "Path: Path re-activated, cancelling"
        	cancelPath()
        }
        
        def currSensors = pathTwoSensors.currentValue('motion')
        def onSensors = currSensors.findAll { sensorVal ->
            sensorVal == "active" ? true : false
        }
        
        if ((state.pathCompleting || state.pathCompleted) && state.path != 2) {
        	log.info "Path: PATH EXTENDED - Cancelling path"
            cancelPath()
        }

        if (onSensors.size() == pathTwoSensors.size()) {
            log.info "Path: PATH 2 - Completing"
            state.pathCompleting = true
            state.path = 2
            runIn(pathTimeoutWindow, pathTimeoutChecker)
        } else {
            log.debug "Path: PATH 2 - Partially Inactive"
        }
	}
}
def pathCompletionChecker() {
	if (state.pathCompleted) {
        log.info "Path: Path Completed, turning receiver on"
        receiver?.on()
        state.pathCompleted = false
        state.pathCompleting = false
        state.path = 0
    }
}

def pathTimeoutChecker() {
	if (state.pathCompleting && state.path > 0) {
        log.info "Path: Path Not Completed, cancelling"
        cancelPath()
    }
}
        

def offPathMotionHandler (evt) {
	if (receiver.currentSwitch == 'on') {
    	log.debug "Path: Ignoring motion, receiver already on"
        return
    }
	if (evt.value == "active") {
        log.info "Path: OFF PATH - Cancelling Path"
        cancelPath()
    }
}

def cancelPath () {
	state.pathCompleted = false
    state.pathCompleting = false
    state.path = 0
    if (aggressivePath) {
        log.debug "Aggressive path on, turning receiver back off"
        receiver?.off()
        runIn(10, receiverOffTelevisionOff)
    }
}*/

/************************ AUTO LIGHTS CONTROLLER *************************/

def setLightingScene() {
    def sun = getSunriseAndSunset()
    def now = new Date()
    
    def room = params.room
	def scene = params.scene
    
	log.debug "Setting lights in room ${room} to ${scene} scene"
    if (room == "living-room") {
    	def lastScene = state.roomOneScene
    	state.roomOneScene = scene
    	switch (scene) {
        	case "gaming":
            case "sports":
            case "tv-long-form":
            case "tv":
                log.debug "TV Mode and lights"
                if (!(location.mode in keepModes))
               		setLocationMode("TV Mode")
                watchAnyLightsOff?.off()
                if (scene == 'tv-long-form') {
                	log.debug "Also setting to movie lights"
                    watchMovieLightsOff?.off()
                } else {
                    watchAnyLightsDim?.setLevel(30)
                }
                break
            case "movie":
                log.debug "Movie Mode and lights"
                if (!(location.mode in keepModes))
               		setLocationMode("Movie Mode")
                watchMovieLightsOff?.off()
                break
            case "paused":
                log.debug "Paused lights"
                if (location.mode in lowLightModes) {
                    watchPauseLightsOn?.on()
                    watchPauseLightsOn?.setLevel(1)
                    /*watchPauseLightsOnDim?.on()
                    watchPauseLightsOnDim?.setLevel(1)*/
                    watchPauseLightsOnLIFXDim?.setLevel(25)
                    watchPauseLightsOnLIFXDim?.on()
                    watchPauseLightsOnLIFX?.setLevel(25)
                    watchPauseLightsOnLIFX?.on()
                } else {
                    watchPauseLightsOn?.on()
                    watchPauseLightsOn?.setLevel(30)
                    watchPauseLightsOnDim?.on()
                    watchPauseLightsOnDim?.setLevel(1)
                    watchPauseLightsOnLIFXDim?.setLevel(25)
                    watchPauseLightsOnLIFXDim?.on()
                    watchPauseLightsOnLIFX?.setLevel(40)
                    watchPauseLightsOnLIFX?.on()
                }
                break
            case "stopped":
                
                if (lastScene == 'music') {
                	log.debug "In Music mode, no light or mode change required (on stop)"
                    break
                }
                
            	log.debug "Stopped Mode Change"
                
                if (now.after(sun.sunset) || now.before(sun.sunrise)) {
                    // Nighttime
                    log.debug "After dark, setting to Night"
                    if (!(location.mode in keepModes))
						location.setMode("Night")
                } else {
                    // Daytime
                    log.debug "Before dark, setting to Home"
                    if (!(location.mode in keepModes))
						location.setMode("Home")
                }
                
                log.debug "Stopped lights"
                
                if (location.mode in noLightModes) {
                	log.debug "Not turning any lights on"
                } else if (location.mode in lowLightModes) {
                	log.debug "Turning lights on dimly"
                    watchEndLightsOn?.on()
                    watchEndLightsOn?.setLevel(1)
                    watchEndLightsOnLIFX?.setLevel(25)
                    watchEndLightsOnLIFX?.on()
                } else {
                    watchEndLightsOn?.on()
                    watchEndLightsOn?.setLevel(30)
                    watchEndLightsOnLIFX?.setLevel(50)
                    watchEndLightsOnLIFX?.on()
                }
                break
            case "music":
            	log.debug "No light change required"
                runIn(30, musicOnTelevisionOff)
                break
            default:
                log.debug "No Scene Match"
                break
        }
    } else if (room == "brandons-room") {
    	state.roomTwoScene = scene
    	switch (scene) {
        	case "gaming":
            case "sports":
            case "tv-long-form":
            case "tv":
                log.debug "Secondary TV Mode and lights"
                //setLocationMode("TV Mode")
                watchSecondaryAnyLightsOff?.off()
                if (scene == 'tv-long-form') {
                	log.debug "Also setting to secondary movie lights"
                    watchSecondaryMovieLightsOff?.off()
                } else {
                    watchSecondaryAnyLightsDim?.setLevel(10)
                }
                break
            case "movie":
                log.debug "Secondary Movie Mode and lights"
                //setLocationMode("Movie Mode")
                watchSecondaryMovieLightsOff?.off()
                break
            case "paused":
                log.debug "Secondary Paused lights"
                watchSecondaryPauseLightsOn?.on()
                watchSecondaryPauseLightsOn?.setLevel(30)
                break
            case "stopped":
                log.debug "Secondary Stopped lights"
                if (location.mode in secondaryNoLightModes) {
                	log.debug "Secondary Not turning any lights on"
                } else if (location.mode in secondaryLowLightModes) {
                	log.debug "Secondary Turning lights on dimly"
                    watchSecondaryEndLightsOn?.on()
                    watchSecondaryEndLightsOn?.setLevel(1)
                } else {
                    watchSecondaryEndLightsOn?.on()
                    watchSecondaryEndLightsOn?.setLevel(30)
                }
                break
            case "music":
            	log.debug "No light change required"
                break
            default:
                log.debug "No Match"
                break
        }
    } else {
    	log.debug "Unknown room ${room}. Ignoring"
    }
}