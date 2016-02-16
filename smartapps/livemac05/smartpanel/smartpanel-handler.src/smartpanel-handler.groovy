/**
 *  SmartPanel Handler
 *
 *  Copyright 2016 Brandon Reeves
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
    name: "SmartPanel Handler",
    namespace: "livemac05/smartpanel",
    author: "Brandon Reeves",
    description: "Controls in-wall smart panel",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
	section("Title") {
		// TODO: put inputs here
        input "smartPanel", "capability.switch", title: "Smart Panel", description: "Device representing the panel", required:true, displayDuringSetup:true
	}
    
    section("Address") {
    	input "address", "string", title: "Publicly accessible web address", required: true
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
	// TODO: subscribe to attributes, devices, locations, etc.
    subscribe(smartPanel, 'switch', switchHandler)
	subscribe(location, changedLocationMode)
    changedLocationMode();
}

def changedLocationMode (evt) {
	if (location.currentMode == "Party") {
    	sendRequest('/readOnly')
    } else {
    	sendRequest('/readWrite')
    }
}

def switchHandler (evt) {
	if (evt.value == 'on') {
    	sendRequest('/on')
    } else {
    	sendRequest('/off')
    }
}

def sendRequest (path) {
	log.debug "Smart Panel Request"
    def params = [
    	uri: "http://${address}:80",
        path: path,
        contentType: "application/json"
    ]
    //try {
        httpGet(params) { resp ->
        	log.debug 'successful request to $path'
        }
    /*} catch (e) {
		log.error "Unable to send request to smart panel: $e"
    }*/
}

// TODO: implement event handlers