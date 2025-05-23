package io.openems.edge.ess.api;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.common.modbusslave.ModbusType;
import io.openems.edge.ess.api.SymmetricEss.ChannelId;

@ProviderType
public interface XstorageEss extends OpenemsComponent {

    public enum ChannelId implements io.openems.edge.common.channel.ChannelId {

        REQUESTED_ACTIVE_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Requested active power setpoint in watts")),

        ACTUAL_ACTIVE_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Measured active power in watts")),

        REQUESTED_REACTIVE_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.VOLT_AMPERE_REACTIVE)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Requested reactive power setpoint in VAR")),

        ACTUAL_REACTIVE_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.VOLT_AMPERE_REACTIVE)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Measured reactive power in VAR")),

        GLOBAL_CHARGE_POWER_LIMIT(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Global maximum charging power limit in watts")),

        GLOBAL_DISCHARGE_POWER_LIMIT(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Global maximum discharging power limit in watts")),

        GLOBAL_MAX_SOC(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.PERCENT)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Global maximum state of charge in percent")),

        GLOBAL_MIN_SOC(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.PERCENT)
                .persistencePriority(PersistencePriority.HIGH)
                .text("Global minimum state of charge in percent")),

        RAMP_RATE(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.PERCENT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Ramp rate in percent per second")),
        
        
        
        
        CHARGE_ACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
        CHARGE_REACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT_AMPERE_REACTIVE_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
        
        DISCHARGE_ACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
        DISCHARGE_REACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT_AMPERE_REACTIVE_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
        
        
        
        
        
        
        
        
        
        
        

        BATTERY_PACK_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Battery pack power in watts")),

        BATTERY_PACK_VOLTAGE(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.VOLT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Battery pack voltage in volts")),

        BATTERY_PACK_CURRENT(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.AMPERE)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Battery pack current in amperes")),
        
        
        SOC(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text("State of Charge of the energy storage system")), //
        
        CAPACITY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)),
        
        
        MIN_CELL_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text("Minimum cell voltage")), //
        
        MAX_CELL_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.MILLIVOLT) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text("Maximum cell voltage")), //
        
        
        MIN_CELL_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text("Minimum cell temperature")), //

        
        MAX_CELL_TEMPERATURE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.DEGREE_CELSIUS) //
				.persistencePriority(PersistencePriority.HIGH) //
				.text("Maximum cell temperature")), //
        
        
        PCS_INPUT_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Power conversion system input power in watts")),

        PCS_OUTPUT_POWER(Doc.of(OpenemsType.INTEGER)
                .unit(Unit.WATT)
                .persistencePriority(PersistencePriority.MEDIUM)
                .text("Power conversion system output power in watts")),

        
        SET_MAX_CHARGE_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
        SET_MAX_DISCHARGE_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
        
        SET_MIN_SOC(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
        SET_MAX_SOC(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.PERCENT) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
        
        SET_OPERATING_MODE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.NONE) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
    	
		REQUEST_ACTIVE_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
		
		REQUEST_REACTIVE_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOVOLT_AMPERE_REACTIVE) //
				.accessMode(AccessMode.WRITE_ONLY)), //
		
		
		REQUEST_RAMP_RATE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT_PER_SECOND) //
				.accessMode(AccessMode.WRITE_ONLY)), //

		
		
		;
        private final Doc doc;

        private ChannelId(Doc doc) {
            this.doc = doc;
        }

        @Override
        public Doc doc() {
            return this.doc;
        }
    }

    public static ModbusSlaveNatureTable getModbusSlaveNatureTable(AccessMode accessMode) {
        return ModbusSlaveNatureTable.of(XstorageEss.class, accessMode, 100)
                .channel(0, ChannelId.REQUESTED_ACTIVE_POWER, ModbusType.UINT32)
                .channel(1, ChannelId.ACTUAL_ACTIVE_POWER, ModbusType.UINT32)
                .channel(2, ChannelId.REQUESTED_REACTIVE_POWER, ModbusType.UINT32)
                .channel(3, ChannelId.ACTUAL_REACTIVE_POWER, ModbusType.UINT32)
                .channel(4, ChannelId.GLOBAL_CHARGE_POWER_LIMIT, ModbusType.UINT32)
                .channel(5, ChannelId.GLOBAL_DISCHARGE_POWER_LIMIT, ModbusType.UINT32)
                .channel(6, ChannelId.GLOBAL_MAX_SOC, ModbusType.UINT32)
                .channel(7, ChannelId.GLOBAL_MIN_SOC, ModbusType.UINT32)
                .channel(8, ChannelId.RAMP_RATE, ModbusType.UINT32)
                .channel(9, ChannelId.BATTERY_PACK_POWER, ModbusType.UINT32)
                .channel(10, ChannelId.BATTERY_PACK_VOLTAGE, ModbusType.UINT32)
                .channel(11, ChannelId.BATTERY_PACK_CURRENT, ModbusType.UINT32)
                .channel(12, ChannelId.PCS_INPUT_POWER, ModbusType.UINT32)
                .channel(13, ChannelId.PCS_OUTPUT_POWER, ModbusType.UINT32)
                .build();
    }

    // Accessors for channels

    default IntegerReadChannel getRequestedActivePowerChannel() {
        return this.channel(ChannelId.REQUESTED_ACTIVE_POWER);
    }

    default Value<Integer> getRequestedActivePower() {
        return this.getRequestedActivePowerChannel().value();
    }

    default IntegerReadChannel getActualActivePowerChannel() {
        return this.channel(ChannelId.ACTUAL_ACTIVE_POWER);
    }

    default Value<Integer> getActualActivePower() {
        return this.getActualActivePowerChannel().value();
    }

    default IntegerReadChannel getRequestedReactivePowerChannel() {
        return this.channel(ChannelId.REQUESTED_REACTIVE_POWER);
    }

    default Value<Integer> getRequestedReactivePower() {
        return this.getRequestedReactivePowerChannel().value();
    }

    default IntegerReadChannel getActualReactivePowerChannel() {
        return this.channel(ChannelId.ACTUAL_REACTIVE_POWER);
    }

    default Value<Integer> getActualReactivePower() {
        return this.getActualReactivePowerChannel().value();
    }

    default IntegerReadChannel getGlobalChargePowerLimitChannel() {
        return this.channel(ChannelId.GLOBAL_CHARGE_POWER_LIMIT);
    }

    default Value<Integer> getGlobalChargePowerLimit() {
        return this.getGlobalChargePowerLimitChannel().value();
    }

    default IntegerReadChannel getGlobalDischargePowerLimitChannel() {
        return this.channel(ChannelId.GLOBAL_DISCHARGE_POWER_LIMIT);
    }

    default Value<Integer> getGlobalDischargePowerLimit() {
        return this.getGlobalDischargePowerLimitChannel().value();
    }

    default IntegerReadChannel getGlobalMaxSoCChannel() {
        return this.channel(ChannelId.GLOBAL_MAX_SOC);
    }

    default Value<Integer> getGlobalMaxSoC() {
        return this.getGlobalMaxSoCChannel().value();
    }

    default IntegerReadChannel getGlobalMinSoCChannel() {
        return this.channel(ChannelId.GLOBAL_MIN_SOC);
    }

    default Value<Integer> getGlobalMinSoC() {
        return this.getGlobalMinSoCChannel().value();
    }

    default IntegerReadChannel getRampRateChannel() {
        return this.channel(ChannelId.RAMP_RATE);
    }

    default Value<Integer> getRampRate() {
        return this.getRampRateChannel().value();
    }

    default IntegerReadChannel getBatteryPackPowerChannel() {
        return this.channel(ChannelId.BATTERY_PACK_POWER);
    }

    default Value<Integer> getBatteryPackPower() {
        return this.getBatteryPackPowerChannel().value();
    }

    default IntegerReadChannel getBatteryPackVoltageChannel() {
        return this.channel(ChannelId.BATTERY_PACK_VOLTAGE);
    }

    default Value<Integer> getBatteryPackVoltage() {
        return this.getBatteryPackVoltageChannel().value();
    }

    default IntegerReadChannel getBatteryPackCurrentChannel() {
        return this.channel(ChannelId.BATTERY_PACK_CURRENT);
    }

    default Value<Integer> getBatteryPackCurrent() {
        return this.getBatteryPackCurrentChannel().value();
    }
    
    
	public default IntegerReadChannel getSocChannel() {
		return this.channel(ChannelId.SOC);
	}


	public default Value<Integer> getSoc() {
		return this.getSocChannel().value();
	}
		

    default IntegerReadChannel getPcsInputPowerChannel() {
        return this.channel(ChannelId.PCS_INPUT_POWER);
    }

    default Value<Integer> getPcsInputPower() {
        return this.getPcsInputPowerChannel().value();
    }

    default IntegerReadChannel getPcsOutputPowerChannel() {
        return this.channel(ChannelId.PCS_OUTPUT_POWER);
    }

    default Value<Integer> getPcsOutputPower() {
        return this.getPcsOutputPowerChannel().value();
    }
    
    /////////////////// Control Channels ////////////////////

    default IntegerWriteChannel getSetMaxChargePowerChannel() {
    	return this.channel(ChannelId.SET_MAX_CHARGE_POWER);
    }
    
    default IntegerWriteChannel getSetMaxDischargePowerChannel() {
    	return this.channel(ChannelId.SET_MAX_DISCHARGE_POWER);
    }
    
    
    default IntegerWriteChannel getSetMaxSocChannel() {
    	return this.channel(ChannelId.SET_MAX_SOC);
    }
    
    
    
    default IntegerWriteChannel getSetMinSocChannel() {
    	return this.channel(ChannelId.SET_MIN_SOC);
    }
    
    
    
    default IntegerWriteChannel getSetOperatingModeChannel() {
    	return this.channel(ChannelId.SET_OPERATING_MODE);
    }
    
    
    default IntegerWriteChannel getRequestActivePowerChannel() {
    	return this.channel(ChannelId.REQUEST_ACTIVE_POWER);
    }
    
    
    
    
    default IntegerWriteChannel getRequestReactivePowerChannel() {
    	return this.channel(ChannelId.REQUEST_REACTIVE_POWER);
    }
    
    default IntegerWriteChannel getRequestRampRateChannel() {
    	return this.channel(ChannelId.REQUEST_RAMP_RATE);
    }
    
  
    
}
