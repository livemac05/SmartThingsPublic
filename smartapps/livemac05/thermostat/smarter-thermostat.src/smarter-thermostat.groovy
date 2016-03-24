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
 *  Keep Me Cozy II
 *
 *  Author: SmartThings
 */

definition(
    name: "Smarter Thermostat",
    namespace: "livemac05/thermostat",
    author: "livemac05@gmail.com",
    description: "Controls a simulated thermostat to enable decoupled mode<>sensor control",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png",
    singleInstance: false
)

preferences() {
    section("Choose real thermostat... ") {
        input "realThermostat", "capability.thermostat"
    }
    section("Choose simulated thermostat... ") {
        input "simulatedThermostat", "capability.thermostat"
    }

    section("Alternate Governor Sensors") {
        input "lrSensor", "capability.temperatureMeasurement", title: "LR Temp Sensor 1", required: false
        input "lrSensor2", "capability.temperatureMeasurement", title: "LR Temp Sensor 2", required: false
        input "bSensor", "capability.temperatureMeasurement", title: "B Temp Sensor", required: false
        input "olrSensor", "capability.temperatureMeasurement", title: "OLR Temp Sensor", required: false
        input "weatherSensor", "capability.temperatureMeasurement", title: "Weather Sensor", required: false
    }
    
    section("Relative Thresholds") {
    	input "auxiliaryThreshold", "number", title: "Thermostat diff setting", defaultValue: 4, required: true
        input "swapThreshold", "number", title: "Swap mode if >x in other direction", defaultValue: 6, required: true
    }
    
    section("Absolute Thresholds") {
    	input "presentHeatTrigger", "number", title: "Min Temperature When Home", defaultValue: 62, required: true
        input "presentCoolTrigger", "number", title: "Max Temperature When Home", defaultValue: 78, required: true
    }
    
    section("Emergency Mode Settings") {
    	input "emergencyHeat", "number", title: "Min Temperature (Emergency)", defaultValue: 57, required: true
        input "emergencyCool", "number", title: "Max Temperature (Emergency)", defaultValue: 83, required: true
    }
    
    section("Climate Mode Settings") {
    	input "outdoorHeatTrigger", "number", title: "Heat Mode When Outdoor <=", defaultValue: 40, required: true
        input "outdoorCoolTrigger", "number", title: "Cool Mode When Outdoor >=", defaultValue: 85, required: true
    }
}

def installed()
{
	log.debug "enter installed, state: $state"
    initialize()
}

def updated()
{
	log.debug "enter updated, state: $state"
	unsubscribe()
    initialize()
}

def initialize () {
	subscribeToEvents()
    enableSync()
    setInitialSetpoints()
    state.autoSettingSetpoint = false
    state.thermoDelay = 3
}

def enableSync () {
	log.info "Enabling smarter thermostat sync"
	state.syncEnabled = true
    simulatedThermostat.enable()
}

def disableSync () {
	log.info "Disabling smarter thermostat sync"
	state.syncEnabled = false
    simulatedThermostat.disable()
}

def getSetpoints() {
	switch (location.currentMode) {
    
    	case 'Bedtime':
        	return [heat: 66, cool: 70]
        case 'Morning':
        	return [heat: 69, cool: 71]
        
        case 'Home':
        case 'WFH':
        	return [heat: 70, cool: 70]
        
        case 'Night':
        	return [heat: 71, cool: 72]
            
            
            
        case 'Party':
        	return [heat: 65, cool: 67]
        case 'Guest':
        	return [heat: 69, cool: 71]
            
            
        
        case 'Away':
        	return [heat: 60, cool: 80]
            
        default:
        	return [heat: null, cool: null];
    }
}
def getSensor () {
	switch (location.currentMode) {
    	case 'Bedtime':
        case 'WFH':
        	return bSensor
            
        case 'Guest':
        case 'Night':
        case 'TV Mode':
        case 'Movie':
        	return lrSensor
            
        case 'Party':
        	return olrSensor
            
        case 'Morning':
        case 'Home':
        default:
        	return null;
    }
}

def getHeatingSetpoint () {
	return getSetpoints().heat
}
def getCoolingSetpoint () {
	return getSetpoints().cool
}

def getSensorTemp () {
	def sensor = getSensor()
    if (sensor == lrSensor) {
    	return getLrTemp()
    } else {
    	return sensor.currentTemperature
    }
}

def getLrTemp() {
    float temp1 = lrSensor.currentTemperature
    float temp2 = lrSensor2.currentTemperature
    return (temp1 + temp2) / 2.0;
}

def subscribeToEvents()
{
	subscribe(location, changedLocationMode)
    
	subscribe(bSensor, "temperature", sensorHandler)
    subscribe(lrSensor, "temperature", sensorHandler)
    subscribe(lrSensor2, "temperature", sensorHandler)
    subscribe(olrSensor, "temperature", sensorHandler)
    
    subscribe(weatherSensor, "temperature", weatherHandler)
    
    subscribe(realThermostat, "temperature", realTempHandler)
    subscribe(realThermostat, "humidity", realHumidityHandler)
    
    subscribe(realThermostat, "thermostatMode", temperatureHandler)
    subscribe(simulatedThermostat, "thermostatMode", temperatureHandler)

    subscribe(simulatedThermostat, "heatingSetpoint", simulatedSetpointSet)
    subscribe(simulatedThermostat, "coolingSetpoint", simulatedSetpointSet)
    subscribe(realThermostat, "heatingSetpoint", realSetpointSet)
    subscribe(realThermostat, "coolingSetpoint", realSetpointSet)

    //subscribe(realThermostat, "thermostatMode", modeSync)
    //subscribe(simulatedThermostat, "thermostatMode", modeSync)

    subscribe(realThermostat, "thermostatOperatingState", operatingStateSync)
    
	subscribe(app, appTouch)
	evaluate()
}

def sensorHandler (evt) {
	if (getSensor() != null) {
    	temperatureHandler()
    }
    
    if (evt.floatValue > emergencyCool) {
        sendPush("EMERGENCY A/C Activated. Indoor temperature too high")
        realThermostat.setThermostatMode('cool')
        disableSync()
        realThermostat.setCoolingSetpoint(67)
    }
    if (evt.floatValue < emergencyHeat) {
        sendPush("EMERGENCY Heat Activated. Indoor temperature too low")
        realThermostat.setThermostatMode('heat')
        disableSync()
        realThermostat.setHeatingSetpoint(75)
    }
    
    if (location.currentMode != "Away") {
        if (evt.floatValue > presentCoolTrigger) {
            log.info "Present Cool Trigger Activated. Temp too high"
            def newSetpoint = simulatedThermostat.currentTemperature - 2
            //log.info "Changing thermostat to cool mode due to trigger"
            //simulatedThermostat.setThermostatMode('cool')
            if (newSetpoint >= simulatedThermostat.currentCoolingSetpoint) {
            	log.debug "Setpoint already low enough, not changing"
                return
            }
            log.info "Bumping temp down to ${newSetpoint}"
            simulatedThermostat.setCoolingSetpoint(newSetpoint)
        }
    	if (evt.floatValue < presentHeatTrigger) {
            log.info "Present Heat Trigger Activated. Temp Too Low"
            def newSetpoint = simulatedThermostat.currentTemperature + 2
            //log.info "Changing thermostat to heat mode due to trigger"
            //simulatedThermostat.setThermostatMode('heat')
            if (newSetpoint <= simulatedThermostat.currentHeatingSetpoint) {
            	log.debug "Setpoint already high enough, not changing"
                return
            }
            log.info "Bumping temp up to ${newSetpoint}"
            simulatedThermostat.setHeatingSetpoint(newSetpoint)
        }
    }
    
    log.debug "Sensor got ${evt.device}"
    
    if (evt.deviceId == lrSensor.id || evt.deviceId == lrSensor2.id) {
    	simulatedThermostat.setLrSensor(getLrTemp())
    } else if (evt.deviceId == bSensor.id) {
    	simulatedThermostat.setBSensor(evt.floatValue)
    } else if (evt.deviceId == realThermostat.id) {
    	simulatedThermostat.setRealSensor(evt.floatValue)
    }
}

def weatherHandler (evt) {
	if (evt.floatValue <= outdoorHeatTrigger) {
    	log.info "Setting to heat mode based on outdoor temperature"
    	simulatedThermostat.heat()
    } else if (evt.floatValue >= outdoorCoolTrigger) {
    	log.info "Setting to cool mode based on outdoor temperature"
    	simulatedThermostat.cool()
    }
}

def realTempHandler (evt) {
	sensorHandler(evt)
    temperatureHandler(evt)
}

def realHumidityHandler (evt) {
	log.debug "Recieved humidity event: ${realThermostat.currentHumidity}"
	simulatedThermostat.setHumidity(realThermostat.currentHumidity)
}

def changedLocationMode(evt)
{
	log.debug "changedLocationMode mode: $evt.value, heat: $heat, cool: $cool"
    setInitialSetpoints()
	evaluate()
}

def setInitialSetpoints (evt) {
	def heatingSP = getHeatingSetpoint()
	def coolingSP = getCoolingSetpoint()
	enableSync()
    syncValues()
    if (!heatingSP || !coolingSP) {
    	log.info "No setpoint for mode, not setting"
        return
    }
	log.info "Setting to initial setpoints H:${heatingSP}, C:${coolingSP} "
	simulatedThermostat.setCoolingSetpoint(coolingSP)
	simulatedThermostat.setHeatingSetpoint(heatingSP)
}

def simulatedSetpointSet (evt) {
	enableSync()
	temperatureHandler()
}

def realSetpointSet (evt) {
	runIn(state.thermoDelay + 1, autoClear, [overwrite: true])
    if (!state.syncEnabled) {
    	log.debug "Sync disabled, not backsyncing setpoint"
        return
    }
    setpointBacksync()
}

def setpointBacksync (evt) {
	if (!state.syncEnabled) {
    	log.warn "Smart mode disabled, not syncing temperatures"
        return
    }
    	
	if (state.autoSettingSetpoint) {
    	log.debug "Not backsyncing setpoint, setpoint was auto-set"
        return
    }
    
    if (getSensor()) {
        //disableSync()
        evaluate()
        return
    }
    
    log.info "Backsyncing setpoints from real to simulated thermostat H:${realThermostat.currentHeatingSetpoint}, C:${realThermostat.currentCoolingSetpoint}"
    
	if (state.autoSettingSetpoint) {
    	log.debug "Not backsyncing setpoint, setpoint was auto-set"
        return
    }
    simulatedThermostat.setCoolingSetpoint(realThermostat.currentCoolingSetpoint)
    simulatedThermostat.setHeatingSetpoint(realThermostat.currentHeatingSetpoint)
}

def autoClear (evt) {
	state.autoSettingSetpoint = false
}

def sensorSync (evt) {
	def temp = (float)89
	if (getSensor()) {
        temp = (float)getSensorTemp()
    	log.debug "Updating thermostat temperature from sensor ${temp}"
    } else {
        temp = (float)realThermostat.currentTemperature
    	log.debug "Updating thermostat temperature from real thermostat ${temp}"
    }
    simulatedThermostat.setTemperature(temp)
}

def modeSync (evt) {
	if (!state.syncEnabled) {
    	log.debug "Sync disabled, not syncing modes"
        return
    }
	if (evt && evt.deviceId == simulatedThermostat.id) {
    	// Change 
        if (realThermostat.currentThermostatMode != simulatedThermostat.currentThermostatMode) {
        	log.info "Changing real thermostat mode to ${simulatedThermostat.currentThermostatMode}"
        	realThermostat.setThermostatMode(simulatedThermostat.currentThermostatMode)
        } else {
        	log.debug "Thermostat modes equal"
        }
    } else {
    	// Update
    	log.debug "Updating displayed thermostat mode to ${realThermostat.currentThermostatMode}"
        simulatedThermostat.setThermostatMode(realThermostat.currentThermostatMode)
    }
}

def operatingStateSync (evt) {
	if (!state.syncEnabled) {
    	log.debug "Sync disabled, not syncing operating state"
        return
    }
	// Update
	log.debug "Updating displayed thermostat operating mode to ${realThermostat.currentThermostatOperatingState}"
	simulatedThermostat.setThermostatOperatingState(realThermostat.currentThermostatOperatingState)
}

def selectedSensorSync (evt) {
	def selectedSensor = realThermostat.displayName
	if (getSensor()) {
    	selectedSensor = getSensor().displayName
    }
    
	simulatedThermostat.setSelectedSensor(selectedSensor)
}

def temperatureHandler(evt)
{
	if (!state.syncEnabled) {
    	log.debug "Sync disabled, not handling temperature"
        return
    }
	syncValues(evt)
	evaluate()
}

def syncValues (evt) {
	sensorSync(evt)
	modeSync(evt)
    operatingStateSync(evt) 
    selectedSensorSync(evt)
}

private evaluate()
{
    
    def tm = realThermostat.currentThermostatMode
    
	if (getSensor()) {
    	if (!state.syncEnabled) {
        	log.warn "Smart mode disabled, not evaluating temperatures"
        } else {
            def threshold = 0.5
            def ct = realThermostat.currentTemperature
            def currentTemp = getSensorTemp()
            log.debug("evaluate:, mode: $tm -- temp: $ct, heat: $realThermostat.currentHeatingSetpoint, cool: $realThermostat.currentCoolingSetpoint -- "  +
                "sensor: $currentTemp, heat: $simulatedThermostat.currentHeatingSetpoint, cool: $simulatedThermostat.currentCoolingSetpoint")
            if (tm in ["cool","auto"]) {
                // air conditioner
                if (currentTemp - simulatedThermostat.currentCoolingSetpoint >= threshold) {
                    state.coolingSetpoint = ct - 2
                    setCoolingSetpoint()
                    log.debug "realThermostat.setCoolingSetpoint(${ct - 2}), ON"
                } else if (simulatedThermostat.currentCoolingSetpoint - currentTemp >= threshold && ct - realThermostat.currentCoolingSetpoint >= threshold) {
                    state.coolingSetpoint = ct + 2
                    setCoolingSetpoint()
                    log.debug "realThermostat.setCoolingSetpoint(${ct + 2}), OFF"
                    log.debug "Testing ${simulatedThermostat.currentCoolingSetpoint - currentTemp} against >= ${swapThreshold} for swap"
                    if (simulatedThermostat.currentCoolingSetpoint - currentTemp >= swapThreshold) {
                    	log.info "setpoint - sensor > swap threshold. Swapping mode to heat"
                        simulatedThermostat.setThermostatMode('heat')
                    }
                } else {
                	state.coolingSetpoint = ct + 2
                    setCoolingSetpoint()
                    log.debug "realThermostat.setCoolingSetpoint(${ct + 2}), OFF @ Target Temp (for heat)"
                    log.debug "Testing ${simulatedThermostat.currentCoolingSetpoint - currentTemp} against >= ${swapThreshold} for swap"
                    if (simulatedThermostat.currentCoolingSetpoint - currentTemp >= swapThreshold) {
                    	log.info "setpoint - sensor > swap threshold. Swapping mode to heat"
                        simulatedThermostat.setThermostatMode('heat')
                    }
                }
            }
            if (tm in ["heat","emergency heat","auto"]) {
                // heater
                if (simulatedThermostat.currentHeatingSetpoint - currentTemp >= auxiliaryThreshold) {
                	log.info "Diff > aux threshold. Bumping by auxiliary threshold"
                    state.heatingSetpoint = ct + auxiliaryThreshold
                    setHeatingSetpoint()
                    log.debug "realThermostat.setHeatingSetpoint(${ct + auxiliaryThreshold}), ON"
                } else if (simulatedThermostat.currentHeatingSetpoint - currentTemp >= threshold) {
                    state.heatingSetpoint = ct + 2
                    setHeatingSetpoint()
                    log.debug "realThermostat.setHeatingSetpoint(${ct + 2}), ON"
                } else if (currentTemp - simulatedThermostat.currentHeatingSetpoint >= threshold && realThermostat.currentHeatingSetpoint - ct >= threshold) {
                    state.heatingSetpoint = ct - 2
                    setHeatingSetpoint()
                    log.debug "realThermostat.setHeatingSetpoint(${ct - 2}), OFF"
                    log.debug "Testing ${currentTemp - simulatedThermostat.currentHeatingSetpoint} against >= ${swapThreshold} for swap"
                    if (currentTemp - simulatedThermostat.currentHeatingSetpoint >= swapThreshold) {
                    	log.info "sensor - setpoint > swap threshold. Swapping mode to cool"
                        simulatedThermostat.setThermostatMode('cool')
                    }
                } else {
                    state.heatingSetpoint = ct - 2
                    setHeatingSetpoint()
                    log.debug "realThermostat.setHeatingSetpoint(${ct - 2}), OFF @ Target Temp (for cool)"
                    log.debug "Testing ${currentTemp - simulatedThermostat.currentHeatingSetpoint} against >= ${swapThreshold} for swap"
                    if (currentTemp - simulatedThermostat.currentHeatingSetpoint >= swapThreshold) {
                    	log.info "sensor - setpoint > swap threshold. Swapping mode to cool"
                        simulatedThermostat.setThermostatMode('cool')
                    }
                }
            }
        }
	}
	else {
		state.coolingSetpoint = simulatedThermostat.currentCoolingSetpoint
		state.heatingSetpoint = simulatedThermostat.currentHeatingSetpoint
        def ct = realThermostat.currentTemperature
        if (tm in ["cool", "auto"]) {
            setCoolingSetpoint()
            runIn(state.thermoDelay, setHeatingSetpoint)
            if (simulatedThermostat.currentCoolingSetpoint - ct >= swapThreshold) {
                log.info "setpoint - sensor > swap threshold. Swapping mode to heat"
                simulatedThermostat.setThermostatMode('heat')
            }
        } else {
        	setHeatingSetpoint()
            runIn(state.thermoDelay, setCoolingSetpoint)
            if (ct - simulatedThermostat.currentHeatingSetpoint >= swapThreshold) {
                log.info "sensor - setpoint > swap threshold. Swapping mode to cool"
                simulatedThermostat.setThermostatMode('cool')
            }
        }
	}
    realThermostat.poll()
}

def appTouch(evt) {
	log.debug "Apptouch, setting to setpoints"
    
    if (getCoolingSetpoint() && getHeatingSetpoint()) {
        state.coolingSetpoint = getCoolingSetpoint()
        state.heatingSetpoint = getHeatingSetpoint()
        setCoolingSetpoint()
        runIn(state.thermoDelay, setHeatingSetpoint)
    }
}

def setCoolingSetpoint() {
	state.autoSettingSetpoint = true
    realThermostat.setCoolingSetpoint(state.coolingSetpoint)
    log.info "Changing real cooling setpoint to ${state.coolingSetpoint} for ${simulatedThermostat.currentCoolingSetpoint}"
   /* if (state.coolingSetpoint < state.heatingSetpoint) {
    	// Set to same
        log.debug "Cooling setpoint lower than heating, setting equal"
        state.heatingSetpoint = state.coolingSetpoint
        runIn(state.thermoDelay, setHeatingSetpoint)
    }*/
}

def setHeatingSetpoint() {
	state.autoSettingSetpoint = true
	realThermostat.setHeatingSetpoint(state.heatingSetpoint)
    log.info "Changing real heating setpoint to ${state.heatingSetpoint} for ${simulatedThermostat.currentHeatingSetpoint}"
    /*if (state.coolingSetpoint < state.heatingSetpoint) {
    	// Set to same
        log.debug "Heating setpoint higher than cooling, setting equal"
        state.coolingSetpoint = state.heatingSetpoint
        runIn(state.thermoDelay, setCoolingSetpoint)
    }*/
}

// for backward compatibility with existing subscriptions
def coolingSetpointHandler(evt) {
	log.debug "coolingSetpointHandler()"
}
def heatingSetpointHandler (evt) {
	log.debug "heatingSetpointHandler ()"
}