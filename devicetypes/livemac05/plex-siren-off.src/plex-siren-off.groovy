/**
 *  Plex Siren Off
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
	definition (name: "Plex Siren Off", namespace: "livemac05", author: "Brandon Reeves") {
		capability "Momentary"
	}

    preferences {
        input("AlarmOffServerIP", "string", title:"Alarm Server IP Address", description: "IP Address", defaultValue: '10.0.1.16', required: true, displayDuringSetup: true)
        input("AlarmOffServerPort", "string", title:"Alarm Server Port", description: "Port", defaultValue: 9999 , required: true, displayDuringSetup: true)
    }

	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		// TODO: define your main and details tiles here
        standardTile("off", "device.momentary", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'OFF', action:"momentary.off", icon:"st.switches.switch.off"
        }
        
        main "off"
        details(["off"])
        //details(["test", "siren", "strobe", "off"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute

}

// handle commands
def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
    
    sendEvent(name: 'momentary', value: 'on')
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
    //serverRequest('GET', '/siren', [:], [:])
    sendEvent(name: 'momentary', value: 'off')
}



def setupRequest() {

	def host = AlarmOffServerIP
    def port = AlarmOffServerPort
    def hostHex = convertIPtoHex(host)
    def portHex = convertPortToHex(port)
    device.deviceNetworkId = "$hostHex:$portHex"
    
}

def serverRequest (method, path, query, headers) {
	def host = AlarmOffServerIP
    def port = AlarmOffServerPort
    
    setupRequest()
        
    log.debug "The device id configured is: $device.deviceNetworkId"
    
    headers.put("HOST", "$host:$port")
    
    
   	try {
        def hubAction = new physicalgraph.device.HubAction(
            method: method,
            path: path,
            headers: headers
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
