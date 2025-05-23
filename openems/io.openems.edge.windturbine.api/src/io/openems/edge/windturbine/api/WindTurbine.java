package io.openems.edge.windturbine.api;

import org.osgi.annotation.versioning.ProviderType;

import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;

@ProviderType
public interface WindTurbine extends OpenemsComponent {

	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		
//		Characteristics
		
		CUT_IN_SPEED(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		RATED_SPEED(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		CUT_OUT_SPEED(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		MAX_POWER(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.WATT)
				.persistencePriority(PersistencePriority.HIGH)),
		
		MAX_TORQUE(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		MIN_PITCH_ANGLE(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		MAX_PITCH_ANGLE(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
//		Wind Summary
		
		WIND_SPEED(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		MAX_WIND_POWER(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.WATT)
				.persistencePriority(PersistencePriority.HIGH)),
		
		ROTOR_SPEED(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),

		PITCH_ANGLE(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		ELECTROMAGNETIC_TORQUE(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.NONE)
				.persistencePriority(PersistencePriority.HIGH)),
		
		GENERATED_POWER(Doc.of(OpenemsType.INTEGER)
				.unit(Unit.WATT)
				.persistencePriority(PersistencePriority.HIGH)),
		
		
		
		
		
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
	
	public default IntegerReadChannel getCutInSpeedChannel() {
		return this.channel(ChannelId.CUT_IN_SPEED);
	}
	
	public default IntegerReadChannel getRatedSpeedChannel() {
        return this.channel(ChannelId.RATED_SPEED);
    }

    public default IntegerReadChannel getCutOutSpeedChannel() {
        return this.channel(ChannelId.CUT_OUT_SPEED);
    }

    public default IntegerReadChannel getMaxPowerChannel() {
        return this.channel(ChannelId.MAX_POWER);
    }

	
	public default IntegerReadChannel getMaxTorqueChannel() {
        return this.channel(ChannelId.MAX_TORQUE);
    }

    public default IntegerReadChannel getMinPitchAngleChannel() {
        return this.channel(ChannelId.MIN_PITCH_ANGLE);
    }

    public default IntegerReadChannel getMaxPitchAngleChannel() {
        return this.channel(ChannelId.MAX_PITCH_ANGLE);
    }

    public default IntegerReadChannel getWindSpeedChannel() {
        return this.channel(ChannelId.WIND_SPEED);
    }

    public default IntegerReadChannel getMaxWindPowerChannel() {
        return this.channel(ChannelId.MAX_WIND_POWER);
    }

    public default IntegerReadChannel getRotorSpeedChannel() {
        return this.channel(ChannelId.ROTOR_SPEED);
    }

    public default IntegerReadChannel getPitchAngleChannel() {
        return this.channel(ChannelId.PITCH_ANGLE);
    }

    public default IntegerReadChannel getElectromagneticTorqueChannel() {
        return this.channel(ChannelId.ELECTROMAGNETIC_TORQUE);
    }

    public default IntegerReadChannel getGeneratedPowerChannel() {
        return this.channel(ChannelId.GENERATED_POWER);
    }
    
    
	public default IntegerReadChannel getActiveEnergyChannel() {
		return this.channel(ChannelId.ACTIVE_ENERGY);
	}
	
	public default IntegerReadChannel getReactiveEnergyChannel() {
		return this.channel(ChannelId.REACTIVE_ENERGY);
	}



}
