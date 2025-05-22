package io.openems.edge.meter.innovx;

import org.osgi.service.event.EventHandler;

import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.meter.api.ElectricityMeter.ChannelId;

public interface InnovxMeterDevice extends OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
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

	
	
	public default IntegerReadChannel getActiveEnergyChannel() {
		return this.channel(ChannelId.ACTIVE_ENERGY);
	}
	
	public default IntegerReadChannel getReactiveEnergyChannel() {
		return this.channel(ChannelId.REACTIVE_ENERGY);
	}

	
	
	public default Value<Integer> getActiveEnergy() {
		return this.getActiveEnergyChannel().value();
	}
	public default Value<Integer> getReactiveEnergy() {
		return this.getReactiveEnergyChannel().value();
	}
}
