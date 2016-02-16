/**
 *  Plex Siren
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
 

 
metadata {
	definition (name: "Plex Siren", namespace: "livemac05", author: "Brandon Reeves") {
		capability "Alarm"

		attribute "hubactionMode", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		// TODO: define your main and details tiles here
        standardTile("test", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"alarm.both", icon:"st.secondary.test"
        }
        standardTile("siren", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"alarm.siren", icon:"st.secondary.siren"
        }
        standardTile("strobe", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"alarm.strobe", icon:"st.secondary.strobe"
        }
        standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"alarm.off", icon:"st.secondary.off"
        }
        
        main "off"
        details(["test", "siren", "strobe", "off"])
        //details(["test", "siren", "strobe", "off"])
	}
}

// handle commands
def off() {
	log.debug "Executing 'off'"
    sendEvent(name: 'siren', value: 'off')
    sendEvent(name: 'strobe', value: 'off')
}
def both() {
	log.debug "Executing 'strobe'"
	// TODO: handle 'strobe' command
    sendEvent(name: 'strobe', value: 'on')
    sendEvent(name: 'siren', value: 'on')
}

def strobe() {
	log.debug "Executing 'strobe'"
    sendEvent(name: 'strobe', value: 'on')
}

def siren() {
	log.debug "Executing 'siren'"
    sendEvent(name: 'siren', value: 'on')
}