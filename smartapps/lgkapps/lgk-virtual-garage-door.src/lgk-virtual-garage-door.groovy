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
 *  Author: LGKahn kahn-st@lgk.com
 *  version 2 user defineable timeout before checking if door opened or closed correctly. Raised default to 25 secs. You can reduce it to 15 secs. if you have custom simulated door with < 6 sec wait.
 */
 
definition(
    name: "LGK Virtual Garage Door",
    namespace: "lgkapps",
    author: "lgkahn kahn-st@lgk.com",
    description: "Sync the Simulated garage door device with 2 actual devices, either a tilt or contact sensor and a switch or relay. The simulated device will then control the actual garage door. In addition, the virtual device will sync when the garage door is opened manually, \n It also attempts to double check the door was actually closed in case the beam was crossed. ",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/garage_contact@2x.png"
)

preferences {
	section("Enter the publicly accessible IP or Domain Name") {
    	input "address", "string", title: "Garage Controller address", required: true
    }
    
    section("Enter the endpoint for pulsing the garage door") {
    	input "endpoint", "string", title: "Garage Pulse endpoint", required: true
    }
    section("Enter the endpoint for toggling the garage light") {
    	input "lightEndpoint", "string", title: "Garage Light endpoint", required: true
    }

	section("Choose the sensor that senses if the garage is open closed? "){
		input "sensor", "capability.contactSensor", title: "Physical Garage Door Open/Closed?", required: true
	}
    
	section("Choose the Virtual Garage Door Device? "){
		input "virtualgd", "capability.doorControl", title: "Virtual Garage Door?", required: true
	}
    
	section("Choose the Virtual Garage Door Device sensor (same as above device)?"){
		input "virtualgdbutton", "capability.contactSensor", title: "Virtual Garage Door Open/Close Sensor?", required: true
	}
    
    section("Garage Door Light") {
    	input "garageLight", "capability.switch", title: "Garage Door Light Switch", required: true
        input "garageLightDelay", "number", title: "Minutes before light turns off automatically", required: true
    }
    
    section("Timeout before checking if the door opened or closed correctly?"){
		input "checkTimeout", "number", title: "Door Operation Check Timeout?", required: true, defaultValue: 25
	}
    
     section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phone1", "phone", title: "Send a Text Message?", required: false
        }
    }

}

def installed()
{
def realgdstate = sensor.currentContact
def virtualgdstate = virtualgd.currentContact
//log.debug "in installed ... current state=  $realgdstate"
//log.debug "gd state= $virtualgd.currentContact"

	subscribe(sensor, "contact", contactHandler)
    subscribe(virtualgd, "door", virtualgdcontactHandler)
    subscribe(garageLight, "switch", sendLightPulse)
    state.noLightToggle = true
    garageLight?.off()
    
    // sync them up if need be set virtual same as actual
    if (realgdstate != virtualgdstate)
     {
        if (realgdstate == "open")
           {
             virtualgd.open()
            }
         else virtualgd.close()
      }
 }

def updated()
{
def realgdstate = sensor.currentContact
def virtualgdstate = virtualgd.currentContact
//log.debug "in updated ... current state=  $realgdstate"
//log.debug "in updated ... gd state= $virtualgd.currentContact"


	unsubscribe()
	subscribe(sensor, "contact", contactHandler)
    subscribe(virtualgd, "door", virtualgdcontactHandler)
    subscribe(garageLight, "switch", sendLightPulse)
    state.noLightToggle = true
    garageLight?.off()
    
    // sync them up if need be set virtual same as actual
    if (realgdstate != virtualgdstate)
     {
        if (realgdstate == "open")
           {
             log.debug "opening virtual door"
             mysend("Virtual Garage Door Opened!")     
             virtualgd.open()
            }
         else {
              virtualgd.close()
              log.debug "closing virtual door"
              mysend("Virtual Garage Door Closed!")   
     		 }
      }
  // for debugging and testing uncomment  temperatureHandlerTest()
}

def contactHandler(evt) 
{
def virtualgdstate = virtualgd.currentContact
// how to determine which contact
//log.debug "in contact handler for actual door open/close event. event = $evt"

    state.noLightToggle = true
    garageLight?.on()
    runIn(garageLightDelay * 60, garageLightAutoOff)


  if("open" == evt.value)
    {
    
    
    // contact was opened, turn on a light maybe?
    log.debug "Contact is in ${evt.value} state"
    // reset virtual door if necessary
    if (virtualgdstate != "open")
      {
        mysend("Garage Door Opened Manually syncing with Virtual Garage Door!")   
        virtualgd.open()
      }
      
     
 }  
  if("closed" == evt.value)
   {
   // contact was closed, turn off the light?
    log.debug "Contact is in ${evt.value} state"
    //reset virtual door
     if (virtualgdstate != "closed")
      {
       mysend("Garage Door Closed Manually syncing with Virtual Garage Door!")   
       virtualgd.close()
      }
      
   }
}

def garageLightAutoOff () {
	if (garageLight.currentSwitch == "on") {
    	log.debug "Garage door light auto-resetting to off state"
        state.noLightToggle = true
        garageLight?.off()
    } else {
    	log.debug "Garage light state not on, not auto-resetting to off"
    }
}
def garageLightTimedOff () {
	log.debug "Garage light actually toggling, trying to turn off"
	state.noLightToggle = false
	garageLight?.off()
}

def virtualgdcontactHandler(evt) {
// how to determine which contact
def realgdstate = sensor.currentContact
//log.debug "in virtual gd contact/button handler event = $evt"
//log.debug "in virtualgd contact handler check timeout = $checkTimeout"

  if("opening" == evt.value)
    {
    // contact was opened, turn on a light maybe?
    log.debug "Contact is in ${evt.value} state"
    // check to see if door is not in open state if so open
    if (realgdstate != "open")
      {
        log.debug "opening real gd to correspond with button press"
         sendOpenerPulse()
         mysend("Virtual Garage Door Opened syncing with Actual Garage Door!")   
         runIn(checkTimeout, checkIfActuallyOpened)
        
      }
     }
  if("closing" == evt.value)
   {
    // contact was closed, turn off the light?
    log.debug "Contact is in ${evt.value} state"
    if (realgdstate != "closed")
      {
        log.debug "closing real gd to correspond with button press"
        sendOpenerPulse()
        mysend("Virtual Garage Door Closed syncing with Actual Garage Door!")   
        runIn(checkTimeout, checkIfActuallyClosed)
      }
   }
}

private sendOpenerPulse() {
	log.debug "Sending Pulse to Garage Opener"
    
    def params = [
    	uri: "http://${address}",
        path: "/${endpoint}"
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send pulse: $e"
    }
}
def sendLightPulse(evt) {
	if (state.noLightToggle) {
    	log.debug "Skipping light toggle, it's automatic"
        state.noLightToggle = false
        return
    }
	log.debug "Sending Pulse to Garage Light"
    
    def params = [
    	uri: "http://${address}",
        path: "/${lightEndpoint}"
    ]
    try {
        httpGet(params) { resp ->
        }
    } catch (e) {
		log.error "Unable to send light toggle pulse: $e"
    }
    
    if (evt.value == "on") {
    	runIn(garageLightDelay * 60, garageLightTimedOff, [overwrite: true])
    }
}

private mysend(msg) {
    if (location.contactBookEnabled) {
        log.debug("sending notifications to: ${recipients?.size()}")
        sendNotificationToContacts(msg, recipients)
    }
    else {
        if (sendPushMessage != "No") {
            log.debug("sending push message")
            sendPush(msg)
        }

        if (phone1) {
            log.debug("sending text message")
            sendSms(phone1, msg)
        }
    }

    log.debug msg
}


def checkIfActuallyClosed()
{
def realgdstate = sensor.currentContact
def virtualgdstate = virtualgd.currentContact
//log.debug "in checkifopen ... current state=  $realgdstate"
//log.debug "in checkifopen ... gd state= $virtualgd.currentContact"

   
    // sync them up if need be set virtual same as actual
    if (realgdstate == "open" && virtualgdstate == "closed")
     {
             log.debug "opening virtual door as it didnt close.. beam probably crossed"
             mysend("Resetting Virtual Garage Door to Open as real door didn't close (beam probably crossed)!")   
             virtualgd.open()
    }   
}



def checkIfActuallyOpened()
{
def realgdstate = sensor.currentContact
def virtualgdstate = virtualgd.currentContact
//log.debug "in checkifopen ... current state=  $realgdstate"
//log.debug "in checkifopen ... gd state= $virtualgd.currentContact"

   
    // sync them up if need be set virtual same as actual
    if (realgdstate == "closed" && virtualgdstate == "open")
     {
             log.debug "opening virtual door as it didnt open.. track blocked?"
             mysend("Resetting Virtual Garage Door to Closed as real door didn't open! (track blocked?)")   
             virtualgd.close()
    }   
}