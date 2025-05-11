#include <SimpleModbusSlave.h>
#include <math.h>

enum {
  // Aggregated channels
  REG_FREQUENCY = 0,
  REG_ACTIVE_POWER,
  REG_REACTIVE_POWER,
  REG_ACTIVE_PRODUCTION_ENERGY,
  REG_ACTIVE_CONSUMPTION_ENERGY,
  REG_VOLTAGE,
  REG_CURRENT,
  
  // Individual phase channels
  REG_ACTIVE_POWER_L1,
  REG_ACTIVE_POWER_L2,
  REG_ACTIVE_POWER_L3,
  
  REG_REACTIVE_POWER_L1,
  REG_REACTIVE_POWER_L2,
  REG_REACTIVE_POWER_L3,
  
  REG_VOLTAGE_L1,
  REG_VOLTAGE_L2,
  REG_VOLTAGE_L3,
  
  REG_CURRENT_L1,
  REG_CURRENT_L2,
  REG_CURRENT_L3,
  
  REG_ACTIVE_PRODUCTION_ENERGY_L1,
  REG_ACTIVE_PRODUCTION_ENERGY_L2,
  REG_ACTIVE_PRODUCTION_ENERGY_L3,
  
  REG_ACTIVE_CONSUMPTION_ENERGY_L1,
  REG_ACTIVE_CONSUMPTION_ENERGY_L2,
  REG_ACTIVE_CONSUMPTION_ENERGY_L3,
  
  REG_TOTAL_ERRORS,
  HOLDING_REGS_SIZE
};

unsigned int holdingRegs[HOLDING_REGS_SIZE];

float productionEnergy = 0.0;
float consumptionEnergy = 0.0;

unsigned long previousMillis = 0;
const unsigned long interval = 100; // Update every 100 ms

void setup() {
  modbus_configure(&Serial, 9600, SERIAL_8E1, 1, 2, HOLDING_REGS_SIZE, holdingRegs);
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;
    float t = currentMillis / 1000.0;  // Time in seconds for sine functions

    // ----- Dynamic Simulated Measurements -----

    // Frequency: Slight fluctuations around 60000 mHz (60 Hz)
    unsigned int frequency = 60000 + (int)(50 * sin(t / 10.0));

    // Active power generation (W) around 10 kW (10,000 W)
    float activePowerTotal = 10000 + random(-500, 500);  // Random fluctuation within ±500 W of 10 kW
    // Reactive power (var), typically less than active power (e.g., 50% to 70%)
    float reactivePowerTotal = activePowerTotal * random(50, 70) / 1000;

    // Voltage per phase (mV) fluctuates around 480V (480,000 mV)
    unsigned int voltageL1 = 480000 + random(-5000, 5000);
    unsigned int voltageL2 = 480000 + random(-5000, 5000);
    unsigned int voltageL3 = 480000 + random(-5000, 5000);
    
    // Calculate current per phase using:
    int currentL1 = (int)(activePowerTotal * 1000 / voltageL1);  // mA
    int currentL2 = (int)(activePowerTotal * 1000 / voltageL2);  // mA
    int currentL3 = (int)(activePowerTotal * 1000 / voltageL3);  // mA
    int currentTotal = currentL1 + currentL2 + currentL3;

    // Energy Integration (Wh) - Assume active power is positive for production
    float dt = interval / 1000.0;  // seconds
    float deltaEnergy = activePowerTotal * (dt / 3600.0);  // Energy in Wh

    if (activePowerTotal > 0) {
      productionEnergy += deltaEnergy;
    } else {
      consumptionEnergy += deltaEnergy;
    }

    // Separate production (power > 0) and consumption (power < 0)
    int prodPhase = (int)(productionEnergy / 3);
    int consPhase = (int)(consumptionEnergy / 3);

    // ----- Update Modbus Registers -----
    holdingRegs[REG_FREQUENCY] = frequency;
    holdingRegs[REG_ACTIVE_POWER] = (int)activePowerTotal;
    holdingRegs[REG_REACTIVE_POWER] = (int)reactivePowerTotal;
    holdingRegs[REG_ACTIVE_PRODUCTION_ENERGY] = (int)productionEnergy;
    holdingRegs[REG_ACTIVE_CONSUMPTION_ENERGY] = (int)consumptionEnergy;
    holdingRegs[REG_VOLTAGE] = (voltageL1 + voltageL2 + voltageL3) / 3;
    holdingRegs[REG_CURRENT] = currentTotal;
    
    holdingRegs[REG_ACTIVE_POWER_L1] = (int)(activePowerTotal / 3);
    holdingRegs[REG_ACTIVE_POWER_L2] = (int)(activePowerTotal / 3);
    holdingRegs[REG_ACTIVE_POWER_L3] = (int)(activePowerTotal / 3);
    
    holdingRegs[REG_REACTIVE_POWER_L1] = (int)(reactivePowerTotal / 3);
    holdingRegs[REG_REACTIVE_POWER_L2] = (int)(reactivePowerTotal / 3);
    holdingRegs[REG_REACTIVE_POWER_L3] = (int)(reactivePowerTotal / 3);
    
    holdingRegs[REG_VOLTAGE_L1] = voltageL1;
    holdingRegs[REG_VOLTAGE_L2] = voltageL2;
    holdingRegs[REG_VOLTAGE_L3] = voltageL3;
    
    holdingRegs[REG_CURRENT_L1] = currentL1;
    holdingRegs[REG_CURRENT_L2] = currentL2;
    holdingRegs[REG_CURRENT_L3] = currentL3;
    
    holdingRegs[REG_ACTIVE_PRODUCTION_ENERGY_L1] = prodPhase;
    holdingRegs[REG_ACTIVE_PRODUCTION_ENERGY_L2] = prodPhase;
    holdingRegs[REG_ACTIVE_PRODUCTION_ENERGY_L3] = prodPhase;
    
    holdingRegs[REG_ACTIVE_CONSUMPTION_ENERGY_L1] = consPhase;
    holdingRegs[REG_ACTIVE_CONSUMPTION_ENERGY_L2] = consPhase;
    holdingRegs[REG_ACTIVE_CONSUMPTION_ENERGY_L3] = consPhase;

    holdingRegs[REG_TOTAL_ERRORS] = modbus_update();  // Update modbus communication
  }
}
