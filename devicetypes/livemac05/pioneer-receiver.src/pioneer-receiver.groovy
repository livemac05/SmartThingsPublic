/**
 *  Pioneer Receiver
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
	definition (name: "Pioneer Receiver", namespace: "livemac05", author: "Brandon Reeves") {
		capability "Music Player"
		capability "Switch"
	}

    preferences {
        input("ReceiverIP", "string", title:"Receiver IP Address", description: "IP Address", defaultValue: '10.0.1.30', required: true, displayDuringSetup: true)
        input("ReceiverPort", "string", title:"Receiver Port", description: "Port", defaultValue: 80 , required: true, displayDuringSetup: true)
    }

	simulator {
		// TODO: define status and reply messages here
	}
    
    tiles(scale: 2) {
		standardTile("button", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
            state "turningOn", label:'Turning', icon:"st.switches.switch.on", backgroundColor:"#79b821"
        	state "turningOff", label:'Turning', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
   
		}
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        main "button"
        details(["button", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'status' attribute
	// TODO: handle 'level' attribute
	// TODO: handle 'trackDescription' attribute
	// TODO: handle 'trackData' attribute
	// TODO: handle 'mute' attribute
	// TODO: handle 'switch' attribute

}

// handle commands
def play() {
	log.debug "Executing 'play'"
	// TODO: handle 'play' command
    sendEvent(name: "play")
}

def pause() {
	log.debug "Executing 'pause'"
	// TODO: handle 'pause' command
    sendEvent(name: "pause")
}

def stop() {
	log.debug "Executing 'stop'"
	// TODO: handle 'stop' command
    sendEvent(name: "stop")
}

def nextTrack() {
	log.debug "Executing 'nextTrack'"
	// TODO: handle 'nextTrack' command
}

def playTrack() {
	log.debug "Executing 'playTrack'"
	// TODO: handle 'playTrack' command
}

def setLevel(level) {
	log.debug "Executing 'setLevel'"
	// TODO: handle 'setLevel' command
    sendEvent(name: "setLevel", value: level)
}

def playText() {
	log.debug "Executing 'playText'"
	// TODO: handle 'playText' command
}

def mute() {
	log.debug "Executing 'mute'"
	// TODO: handle 'mute' command
}

def previousTrack() {
	log.debug "Executing 'previousTrack'"
	// TODO: handle 'previousTrack' command
}

def unmute() {
	log.debug "Executing 'unmute'"
	// TODO: handle 'unmute' command
}

def setTrack() {
	log.debug "Executing 'setTrack'"
	// TODO: handle 'setTrack' command
}

def resumeTrack() {
	log.debug "Executing 'resumeTrack'"
	// TODO: handle 'resumeTrack' command
}

def restoreTrack() {
	log.debug "Executing 'restoreTrack'"
	// TODO: handle 'restoreTrack' command
}

def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
    sendEvent(name: 'switch', value: 'on')
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
    sendEvent(name: 'switch', value: 'off')
}



def setupRequest() {

	def host = ReceiverIP
    def port = ReceiverPort
    def hostHex = convertIPtoHex(host)
    def portHex = convertPortToHex(port)
    device.deviceNetworkId = "$hostHex:$portHex"
    
}

def serverRequest (method, path, query, headers) {
	def host = ReceiverIP
    def port = ReceiverPort
    
    setupRequest()
    
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    headers.put("HOST", "$host:$port")
    
    
   	try {
        def hubAction = new physicalgraph.device.HubAction(
            method: method,
            path: path,
            headers: headers,
            query: query
            )

        log.debug hubAction
        hubAction
    }
    catch (Exception e) {
    	log.debug "Hit Exception $e on $hubAction"
    }
}

def parse(description) {
    log.debug "Parsing '${description}'"
    def map = [:]
	def retResult = []
	def descMap = parseDescriptionAsMap(description)
	//Image
	log.debug "Check it"
}

def parseDescriptionAsMap(description) {
    description.split(",").inject([:]) { map, param ->
        def nameAndValue = param.split(":")
        map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    }
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}