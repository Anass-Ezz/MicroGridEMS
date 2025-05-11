package io.openems.edge.windturbine;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.metatype.annotations.Designate;

import io.openems.common.exceptions.OpenemsException;
import io.openems.common.types.MeterType;
import io.openems.edge.bridge.modbus.api.AbstractOpenemsModbusComponent;
import io.openems.edge.bridge.modbus.api.BridgeModbus;
import io.openems.edge.bridge.modbus.api.ModbusComponent;
import io.openems.edge.bridge.modbus.api.ModbusProtocol;
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.windturbine.api.WindTurbine;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.windturbine", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class WindTurbineDeviceImpl extends AbstractOpenemsModbusComponent implements WindTurbineDevice, WindTurbine, ElectricityMeter, ModbusComponent, OpenemsComponent {

	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	private Config config = null;

	public WindTurbineDeviceImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				WindTurbineDevice.ChannelId.values(), //
				WindTurbine.ChannelId.values(),
				ElectricityMeter.ChannelId.values()
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) throws OpenemsException {
		if(super.activate(context, config.id(), config.alias(), config.enabled(), config.modbusUnitId(), this.cm, "Modbus",
				config.modbus_id())) {
			return;
		}
		this.config = config;
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected ModbusProtocol defineModbusProtocol(){
	    return new ModbusProtocol(this, //
	        
	        new FC3ReadRegistersTask(0x0000, Priority.HIGH,
	            m(WindTurbine.ChannelId.CUT_IN_SPEED, new SignedWordElement(0x0000)),
	            m(WindTurbine.ChannelId.RATED_SPEED, new SignedWordElement(0x0001)),
	            m(WindTurbine.ChannelId.CUT_OUT_SPEED, new SignedWordElement(0x0002)),
	            m(WindTurbine.ChannelId.MAX_POWER, new SignedWordElement(0x0003)),
	            m(WindTurbine.ChannelId.MAX_TORQUE, new SignedWordElement(0x0004)),
	            m(WindTurbine.ChannelId.MIN_PITCH_ANGLE, new SignedWordElement(0x0005)),
	            m(WindTurbine.ChannelId.MAX_PITCH_ANGLE, new SignedWordElement(0x0006))
	        ),
	        
	        new FC3ReadRegistersTask(0x000A, Priority.HIGH,
	            m(WindTurbine.ChannelId.WIND_SPEED, new SignedWordElement(0x000A)),
	            m(WindTurbine.ChannelId.MAX_WIND_POWER, new SignedWordElement(0x000B)),
	            m(WindTurbine.ChannelId.ROTOR_SPEED, new SignedWordElement(0x000C)),
	            m(WindTurbine.ChannelId.PITCH_ANGLE, new SignedWordElement(0x000D)),
	            m(WindTurbine.ChannelId.ELECTROMAGNETIC_TORQUE, new SignedWordElement(0x000E)),
	            m(WindTurbine.ChannelId.GENERATED_POWER, new SignedWordElement(0x000F))
	        ),
	        
	        new FC3ReadRegistersTask(0x0014, Priority.HIGH,
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER, new SignedWordElement(0x0014)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L1, new SignedWordElement(0x0015)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L2, new SignedWordElement(0x0016)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L3, new SignedWordElement(0x0017)),
	            m(ElectricityMeter.ChannelId.REACTIVE_POWER, new SignedWordElement(0x0018)),
	            m(ElectricityMeter.ChannelId.POWER_FACTOR, new SignedWordElement(0x0019)),
	            m(ElectricityMeter.ChannelId.FREQUENCY, new SignedWordElement(0x001A)),
	            m(ElectricityMeter.ChannelId.VOLTAGE, new SignedWordElement(0x001B)),
	            m(ElectricityMeter.ChannelId.CURRENT_L1, new SignedWordElement(0x001C)),
	            m(ElectricityMeter.ChannelId.CURRENT_L2, new SignedWordElement(0x001D)),
	            m(ElectricityMeter.ChannelId.CURRENT_L3, new SignedWordElement(0x001E)),
	            m(ElectricityMeter.ChannelId.CURRENT, new SignedWordElement(0x001F))
	        )
	    );
	}

	@Override
	public String debugLog() {
		return this.getActivePowerChannel().value().toString();
	}

	@Override
	public MeterType getMeterType() {
		// TODO Auto-generated method stub
		return MeterType.PRODUCTION;
	}
}
