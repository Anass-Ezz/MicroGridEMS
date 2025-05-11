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
				.persistencePriority(PersistencePriority.MEDIUM)), //
		
	
		DC_PV_CURRENT(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.AMPERE) //
				.persistencePriority(PersistencePriority.MEDIUM)), //
		
		DC_PV_POWER(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.KILOWATT) //
				.persistencePriority(PersistencePriority.MEDIUM)), //
		
		
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



	/**
	 * Gets the Channel for {@link ChannelId#MAX_ACTIVE_POWER}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getDcPvVolatgeChannel() {
		return this.channel(ChannelId.DC_PV_VOLTAGE);
	}

	/**
	 * Gets the Maximum Active Power in [WATT], range "&gt;= 0". See
	 * {@link ChannelId#MAX_ACTIVE_POWER}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getDcPvVoltage() {
		return this.getDcPvVolatgeChannel().value();
	}


	/**
	 * Gets the Channel for {@link ChannelId#MAX_REACTIVE_POWER}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getDcPvCurrentChannel() {
		return this.channel(ChannelId.DC_PV_CURRENT);
	}

	/**
	 * Gets the Maximum Reactive Power in [VAR], range "&gt;= 0". See
	 * {@link ChannelId#MAX_REACTIVE_POWER}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getDcPvCurrent() {
		return this.getDcPvCurrentChannel().value();
	}


	/**
	 * Gets the Channel for {@link ChannelId#MAX_APPARENT_POWER}.
	 *
	 * @return the Channel
	 */
	public default IntegerReadChannel getDcPvPowerChannel() {
		return this.channel(ChannelId.DC_PV_POWER);
	}

	/**
	 * Gets the Maximum Apparent Power in [VA], range "&gt;= 0". See
	 * {@link ChannelId#MAX_APPARENT_POWER}.
	 *
	 * @return the Channel {@link Value}
	 */
	public default Value<Integer> getDcPvPower() {
		return this.getDcPvPowerChannel().value();
	}


	/**
	 * Used for Modbus/TCP Api Controller. Provides a Modbus table for the Channels
	 * of this Component.
	 *
	 * @param accessMode filters the Modbus-Records that should be shown
	 * @return the {@link ModbusSlaveNatureTable}
	 */
	public static ModbusSlaveNatureTable getModbusSlaveNatureTable(AccessMode accessMode) {
		return ModbusSlaveNatureTable.of(SunnyCentralPvInverter.class, accessMode, 100) //
				.build();
	}
}
