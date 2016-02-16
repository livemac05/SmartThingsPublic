/**
 *  Copyright 2014 SmartThings 
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
 */
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Better Simulated Thermostat", author: "SmartThings") {
		capability "Thermostat"
 
		command "tempUp"
		command "tempDown"
		command "heatUp"
		command "heatDown"
		command "coolUp"
		command "coolDown"
        command "setTemperature", ["number"]
        
        command "disable"
        command "enable"
        
        command "setThermostatOperatingState"
        command "setSelectedSensor"
        command "setBothSetpoints"
        
        command "setRealSensor"
        command "setLrSensor"
        command "setBSensor"
        
        attribute "selectedSensor", "string"
        
        attribute "realSensor", "number"
        attribute "bSensor", "number"
        attribute "lrSensor", "number"
	}

	tiles (scale:2) {
    
    	multiAttributeTile(name:"thermostatMulti", type:"lighting", width:6, height:4) {
          tileAttribute("device.temperature", key: "SECONDARY_CONTROL") {
            attributeState("default", label:'Current Sensor Temperature: ${currentValue}˚', unit:"dF")
         
          }
          tileAttribute("device.thermostatSetpoint", key: "VALUE_CONTROL") {
            attributeState("default", action: "setBothSetpoints")
          }
          /*tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
            attributeState("default", label:'${currentValue}%', unit:"%")
          }*/
          /*tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
            attributeState("idle", backgroundColor:"#44b621")
            attributeState("heating", backgroundColor:"#ffa81e")
            attributeState("cooling", backgroundColor:"#269bd2")
          }*/
          tileAttribute("device.thermostatOperatingState", key: "PRIMARY_CONTROL") {
            attributeState("disabled", backgroundColor:"#e0b000", label: 'Disconnected', icon: 'st.locks.lock.unknown')
            attributeState("idle", backgroundColor:"#44b621", icon:"st.thermostat.heating-cooling-off")
            attributeState("heating", backgroundColor:"#ff2323", icon:"st.thermostat.heat")
            attributeState("cooling", backgroundColor:"#269bd2", icon:"st.thermostat.cool")
          }
          /*tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
            attributeState("off", label:'${name}')
            attributeState("heat", label:'${name}')
            attributeState("cool", label:'${name}')
            attributeState("auto", label:'${name}')
          }*/
          /*tileAttribute("device.thermostatOperatingState", key: "SECONDARY_CONTROL") {
            attributeState("off", label:'Thermostat OFF')
            attributeState("heat", label:'Heating')
            attributeState("cool", label:'Cooling')
            attributeState("auto", label:'Auto')
          }*/
          tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
            attributeState("default", label:'${currentValue}', unit:"dF")
          }
          tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
            attributeState("default", label:'${currentValue}', unit:"dF")
          }
		}
    
    
		valueTile("temperature", "device.temperature", width: 1, height: 1) {
			state("temperature", label:'${currentValue}', unit:"dF",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        
        
		standardTile("mode", "device.thermostatMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "off", label:'', action:"thermostat.heat", icon:"st.thermostat.heating-cooling-off"
			state "heat", label:'', action:"thermostat.cool", icon:"st.thermostat.heat"
			//state "emergencyHeat", label:'', action:"switchMode", icon:"st.thermostat.emergency-heat"
			state "cool", label:'', action:"thermostat.off", icon:"st.thermostat.cool"
			//state "auto", label:'', action:"thermostat.off", icon:"st.thermostat.auto"
		}
        
		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "fanAuto", label:'', action:"thermostat.fanOn", icon:"st.thermostat.fan-auto"
			state "fanOn", label:'', action:"thermostat.fanCirculate", icon:"st.thermostat.fan-on"
			state "fanCirculate", label:'  ', action:"thermostat.fanAuto", icon:"st.thermostat.fan-circulate"
		}
        
       /* standardTile("operatingState", "device.thermostatOperatingState") {
			state "idle", label:'${name}', backgroundColor:"#ffffff"
			state "heating", label:'${name}', backgroundColor:"#ffa81e"
			state "cooling", label:'${name}', backgroundColor:"#269bd2"
		}*/
        
        
        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat", width:2,height:2) {
			state "heat", label:'${currentValue}° heat', unit:"F", backgroundColor:"#ffffff"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat", width:2,height:2) {
			state "cool", label:'${currentValue}° cool', unit:"F", backgroundColor:"#ffffff"
		}
        
        standardTile("heatUp", "device.temperature", canChangeIcon: false, inactiveLabel: false, decoration: "flat", width:2,height:1) {
                        state "default", label:'  ', action:"heatUp", icon:"st.thermostat.thermostat-up"
        }
        standardTile("heatDown", "device.temperature", canChangeIcon: false, inactiveLabel: false, decoration: "flat", width:2,height:1) {
                        state "default", label:'  ', action:"heatDown", icon:"st.thermostat.thermostat-down"
        }
        standardTile("coolUp", "device.temperature", canChangeIcon: false, inactiveLabel: false, decoration: "flat", width:2,height:1) {
                        state "default", label:'  ', action:"coolUp", icon:"st.thermostat.thermostat-up"
        }
        standardTile("coolDown", "device.temperature", canChangeIcon: false, inactiveLabel: false, decoration: "flat", width:2,height:1) {
                        state "default", label:'  ', action:"coolDown", icon:"st.thermostat.thermostat-down"
        }
        
        
        valueTile("selectedSensorLabel", "device.selectedSensor", decoration: "flat", inactiveLabel: false, width: 2, height: 1) {
        	state "selectedSensor", label: 'Governed By:'
        }
        
        valueTile("selectedSensor", "device.selectedSensor", decoration: "flat", inactiveLabel: false, width: 4, height: 1) {
        	state "selectedSensor", label: '${currentValue}'
        }
        
        
        standardTile("realSensorLabel", "device.realSensor", width: 2, height: 1, decoration: "flat") {
        	state("default", label: "Hallway")
        }
    
		valueTile("realSensor", "device.realSensor", width: 2, height: 2) {
			state("temperature", label:'${currentValue}', unit:"dF",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        
        standardTile("bSensorLabel", "device.bSensor", width: 2, height: 1, decoration: "flat") {
        	state("default", label: "Bedroom")
        }
    
		valueTile("bSensor", "device.bSensor", width: 2, height: 2) {
			state("temperature", label:'${currentValue}', unit:"dF",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        
        standardTile("lrSensorLabel", "device.lrSensor", width: 2, height: 1, decoration: "flat") {
        	state("default", label: "Living Room")
        }
    
		valueTile("lrSensor", "device.lrSensor", width: 2, height: 2) {
			state("temperature", label:'${currentValue}', unit:"dF",
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
        
        
		/*standardTile("mode", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "off", label:'${name}', action:"thermostat.heat", backgroundColor:"#ffffff"
			state "heat", label:'${name}', action:"thermostat.cool", backgroundColor:"#ffa81e"
			state "cool", label:'${name}', action:"thermostat.auto", backgroundColor:"#269bd2"
			state "auto", label:'${name}', action:"thermostat.off", backgroundColor:"#79b821"
		}*/
        
		/*standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat") {
			state "fanAuto", label:'${name}', action:"thermostat.fanOn", backgroundColor:"#ffffff"
			state "fanOn", label:'${name}', action:"thermostat.fanCirculate", backgroundColor:"#ffffff"
			state "fanCirculate", label:'${name}', action:"thermostat.fanAuto", backgroundColor:"#ffffff"
		}*/
        
        /*
		standardTile("tempDown", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'down', action:"tempDown"
		}
		standardTile("tempUp", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'up', action:"tempUp"
		}

		valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, decoration: "flat") {
			state "heat", label:'${currentValue} heat', unit: "F", backgroundColor:"#ffffff"
		}
		standardTile("heatDown", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'down', action:"heatDown"
		}
		standardTile("heatUp", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'up', action:"heatUp"
		}

		valueTile("coolingSetpoint", "device.coolingSetpoint", inactiveLabel: false, decoration: "flat") {
			state "cool", label:'${currentValue} cool', unit:"F", backgroundColor:"#ffffff"
		}
		standardTile("coolDown", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'down', action:"coolDown"
		}
		standardTile("coolUp", "device.temperature", inactiveLabel: false, decoration: "flat") {
			state "default", label:'up', action:"coolUp"
		}

		standardTile("fanMode", "device.thermostatFanMode", inactiveLabel: false, decoration: "flat") {
			state "fanAuto", label:'${name}', action:"thermostat.fanOn", backgroundColor:"#ffffff"
			state "fanOn", label:'${name}', action:"thermostat.fanCirculate", backgroundColor:"#ffffff"
			state "fanCirculate", label:'${name}', action:"thermostat.fanAuto", backgroundColor:"#ffffff"
		}*/


		main("thermostatMulti")
		details([
        	"thermostatMulti",
			/*"temperature", "mode", "operatingState", 
			"heatingSetpoint", "heatDown", "heatUp",
			"coolingSetpoint", "coolDown", "coolUp",*/
            "selectedSensorLabel", "selectedSensor",
            "bSensorLabel", "realSensorLabel", "lrSensorLabel",
            "bSensor", "realSensor", "lrSensor",
            "mode", "heatUp", "coolUp", 
             "heatingSetpoint", "coolingSetpoint", "fanMode",
            "heatDown", "coolDown"
		])
	}
}

def currentTemperature () {
	return device.currentValue('temperature')
}

def installed() {
	sendEvent(name: "temperature", value: 72, unit: "F")
	sendEvent(name: "heatingSetpoint", value: 70, unit: "F")
	sendEvent(name: "thermostatSetpoint", value: 70, unit: "F")
	sendEvent(name: "coolingSetpoint", value: 76, unit: "F")
	sendEvent(name: "thermostatMode", value: "off")
	sendEvent(name: "thermostatFanMode", value: "fanAuto")
	sendEvent(name: "thermostatOperatingState", value: "idle")
}

def parse(String description) {
}

def evaluate(temp, heatingSetpoint, coolingSetpoint) {
	log.debug "evaluate($temp, $heatingSetpoint, $coolingSetpoint"
	def threshold = 1.0
	def current = device.currentValue("thermostatOperatingState")
	def mode = device.currentValue("thermostatMode")
    
    state.currentTemperature = temp

	def heating = false
	def cooling = false
	def idle = false
	if (mode in ["heat","emergency heat","auto"]) {
		if (heatingSetpoint - temp >= threshold) {
			heating = true
			sendEvent(name: "thermostatOperatingState", value: "heating")
		}
		else if (temp - heatingSetpoint >= threshold) {
			idle = true
		}
		sendEvent(name: "thermostatSetpoint", value: heatingSetpoint)
	}
	if (mode in ["cool","auto"]) {
		if (temp - coolingSetpoint >= threshold) {
			cooling = true
			sendEvent(name: "thermostatOperatingState", value: "cooling")
		}
		else if (coolingSetpoint - temp >= threshold && !heating) {
			idle = true
		}
		sendEvent(name: "thermostatSetpoint", value: coolingSetpoint)
	}
	else {
		sendEvent(name: "thermostatSetpoint", value: heatingSetpoint)
	}
	if (idle && !heating && !cooling) {
		sendEvent(name: "thermostatOperatingState", value: "idle")
	}
}

def setHeatingSetpoint(Double degreesF) {
	//log.debug "setHeatingSetpoint($degreesF)"
	sendEvent(name: "heatingSetpoint", value: degreesF)
	evaluate(device.currentValue("temperature"), degreesF, device.currentValue("coolingSetpoint"))
}

def setCoolingSetpoint(Double degreesF) {
	//log.debug "setCoolingSetpoint($degreesF)"
	sendEvent(name: "coolingSetpoint", value: degreesF)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), degreesF)
}

def setThermostatMode(String value) {
	sendEvent(name: "thermostatMode", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setThermostatFanMode(String value) {
	sendEvent(name: "thermostatFanMode", value: value)
}

def off() {
	sendEvent(name: "thermostatMode", value: "off")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def heat() {
	sendEvent(name: "thermostatMode", value: "heat")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def auto() {
	sendEvent(name: "thermostatMode", value: "auto")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def emergencyHeat() {
	sendEvent(name: "thermostatMode", value: "emergency heat")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def cool() {
	sendEvent(name: "thermostatMode", value: "cool")
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def fanOn() {
	sendEvent(name: "thermostatFanMode", value: "fanOn")
}

def fanAuto() {
	sendEvent(name: "thermostatFanMode", value: "fanAuto")
}

def fanCirculate() {
	sendEvent(name: "thermostatFanMode", value: "fanCirculate")
}

def poll() {
	null
}

def tempUp() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue + 1 : 72
	sendEvent(name:"temperature", value: value)
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def tempDown() {
	def ts = device.currentState("temperature")
	def value = ts ? ts.integerValue - 1 : 72
	sendEvent(name:"temperature", value: value)
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setTemperature(value) {
	def ts = device.currentState("temperature")
	sendEvent(name:"temperature", value: value, unit: "fahrenheit",)
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setBothSetpoints(value) {
	def ts = device.currentState("temperature")
    //log.debug "Mode: ${device.currentValue("thermostatMode")}"
    if (device.currentValue("thermostatMode") == "cool") {
		sendEvent(name:"coolingSetpoint", value: value)
    } else if (device.currentValue("thermostatMode") == "heat") {
		sendEvent(name:"heatingSetpoint", value: value)
    }
	evaluate(value, device.currentValue("heatingSetpoint"), device.currentValue("coolingSetpoint"))
}

def setThermostatOperatingState (value) {
	if (state.disabled) {
    	return
    }
	if (value) {
    	//log.debug "Sending operating state event to ${value}"
		sendEvent(name: 'thermostatOperatingState', value: value)
    }
}

def setRealSensor (value) {
	if (value) {
    	//log.debug "Sending real sensor value to ${value}"
		sendEvent(name: 'realSensor', value: value)
    }
}
def setLrSensor (value) {
	if (value) {
    	//log.debug "Sending lr sensor value to ${value}"
		sendEvent(name: 'lrSensor', value: value)
    }
}
def setBSensor (value) {
	if (value) {
    	//log.debug "Sending bedroom sensor value to ${value}"
		sendEvent(name: 'bSensor', value: value)
    }
}

def setSelectedSensor (value) {
	sendEvent(name: 'selectedSensor', value: value)
}

def disable () {
	state.disabled = true
	sendEvent(name: 'thermostatOperatingState', value: 'disabled')
}

def enable () {
	state.disabled = false
	sendEvent(name: 'thermostatOperatingState', value: 'idle')
}

def heatUp() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts ? ts.integerValue + 1 : 68
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}

def heatDown() {
	def ts = device.currentState("heatingSetpoint")
	def value = ts ? ts.integerValue - 1 : 68
	sendEvent(name:"heatingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), value, device.currentValue("coolingSetpoint"))
}


def coolUp() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts ? ts.integerValue + 1 : 76
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}

def coolDown() {
	def ts = device.currentState("coolingSetpoint")
	def value = ts ? ts.integerValue - 1 : 76
	sendEvent(name:"coolingSetpoint", value: value)
	evaluate(device.currentValue("temperature"), device.currentValue("heatingSetpoint"), value)
}