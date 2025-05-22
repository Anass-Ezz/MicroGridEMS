package io.openems.edge.pvinverter.api;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.types.MeterType;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerDoc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.modbusslave.ModbusSlaveNatureTable;
import io.openems.edge.meter.api.ElectricityMeter;

/**
 * Represents a 3-Phase, symmetric PV-Inverter.
 */
public interface SunnyCentralPvInverter extends OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		
		
		DC_PV_VOLTAGE(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
	
		DC_PV_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
		DC_PV_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
		
		
		
		
		ACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.WATT_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
		REACTIVE_ENERGY(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.VOLT_AMPERE_REACTIVE_HOURS) //
				.persistencePriority(PersistencePriority.HIGH)), //
		
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




	public default IntegerReadChannel getDcPvVolatgeChannel() {
		return this.channel(ChannelId.DC_PV_VOLTAGE);
	}
	


	public default Value<Integer> getDcPvVoltage() {
		return this.getDcPvVolatgeChannel().value();
	}



	public default IntegerReadChannel getDcPvCurrentChannel() {
		return this.channel(ChannelId.DC_PV_CURRENT);
	}


	public default Value<Integer> getDcPvCurrent() {
		return this.getDcPvCurrentChannel().value();
	}



	public default IntegerReadChannel getDcPvPowerChannel() {
		return this.channel(ChannelId.DC_PV_POWER);
	}

	
	
	public default Value<Integer> getDcPvPower() {
		return this.getDcPvPowerChannel().value();
	}
	
	
	public default IntegerReadChannel getActiveEnergyChannel() {
		return this.channel(ChannelId.ACTIVE_ENERGY);
	}
	
	public default Value<Integer> getActiveEnergy() {
		return this.getActiveEnergyChannel().value();
	}
	
	public default IntegerReadChannel getReactiveEnergyChannel() {
		return this.channel(ChannelId.REACTIVE_ENERGY);
	}
	public default Value<Integer> getReactiveEnergy() {
		return this.getReactiveEnergyChannel().value();
	}


	
	public static ModbusSlaveNatureTable getModbusSlaveNatureTable(AccessMode accessMode) {
		return ModbusSlaveNatureTable.of(SunnyCentralPvInverter.class, accessMode, 100) //
				.build();
	}
}
