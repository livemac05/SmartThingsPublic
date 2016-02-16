/**
 *  Copyright 2015 SmartThings
 *  Copyright 2016 Brandon Reeves (modifications)
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
 *  Garage Door Monitor
 *
 *  Author: SmartThings
 */
definition(
    name: "Door Monitor",
    namespace: "livemac05",
    author: "livemac05@gmail.com",
    description: "Monitor your garage door and get a text message if it is open too long",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact@2x.png"
)

preferences {
	section("When the door is open...") {
		input "contactSensor", "capability.contactSensor", title: "Which?"
	}
	section("For too long...") {
		input "maxOpenTime", "number", title: "Seconds?"
	}
	section("Text me at (optional, sends a push notification if not specified)...") {
        input("recipients", "contact", title: "Notify", description: "Send notifications to") {
            input "phone", "phone", title: "Phone number?", required: false
        }
	}
}

def installed()
{
	subscribe(contactSensor, "contact", contactHandler)
}

def updated()
{
	unsubscribe()
	subscribe(contactSensor, "contact", contactHandler)
}

def contactHandler(evt) {
	def latestState = evt.value // e.g.: 0,0,-1000
	if (latestState) {
		def isOpen = (latestState == "open") // TODO: Test that this value works in most cases...
		def isNotScheduled = state.status != "scheduled"

		if (!isOpen) {
			clearSmsHistory()
			clearStatus()
		}

		if (isOpen && isNotScheduled) {
			runIn(maxOpenTime, takeAction, [overwrite: false])
			state.status = "scheduled"
		}

	}
	else {
		log.warn "COULD NOT FIND LATEST STATE FOR: ${contactSensor}"
	}
}

def takeAction(){
	if (state.status == "scheduled")
	{
		def deltaMillis = 1000 * maxOpenTime
		def timeAgo = new Date(now() - deltaMillis)
		def openTooLong = contactSensor?.currentState("contact").date < timeAgo

		def recentTexts = state.smsHistory.find { it.sentDate.toSystemDate() > timeAgo }

		if (!recentTexts) {
			sendTextMessage()
		}
		runIn(maxOpenTime, takeAction, [overwrite: false])
	} else {
		log.trace "Status is no longer scheduled. Not sending text."
	}
}

def sendTextMessage() {
	log.debug "$contactSensor was open too long, texting $phone"

	updateSmsHistory()
	def openMinutes = maxOpenTime * (state.smsHistory?.size() ?: 1)
	def msg = "Your ${contactSensor.label ?: contactSensor.name} has been open for more than ${openMinutes} seconds!"
    if (location.contactBookEnabled) {
        sendNotificationToContacts(msg, recipients)
    }
    else {
        if (phone) {
            sendSms(phone, msg)
        } else {
            sendPush msg
        }
    }
}

def updateSmsHistory() {
	if (!state.smsHistory) state.smsHistory = []

	if(state.smsHistory.size() > 9) {
		log.debug "SmsHistory is too big, reducing size"
		state.smsHistory = state.smsHistory[-9..-1]
	}
	state.smsHistory << [sentDate: new Date().toSystemFormat()]
}

def clearSmsHistory() {
	state.smsHistory = null
}

def clearStatus() {
	state.status = null
}
