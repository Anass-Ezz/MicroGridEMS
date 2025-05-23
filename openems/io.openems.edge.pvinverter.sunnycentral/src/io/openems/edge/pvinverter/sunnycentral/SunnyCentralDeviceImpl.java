package io.openems.edge.pvinverter.sunnycentral;

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
import io.openems.edge.bridge.modbus.api.element.SignedDoublewordElement;
import io.openems.edge.bridge.modbus.api.element.SignedWordElement;
import io.openems.edge.bridge.modbus.api.element.UnsignedWordElement;
import io.openems.edge.bridge.modbus.api.element.WordOrder;
import io.openems.edge.bridge.modbus.api.task.FC16WriteRegistersTask;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.pvinverter.api.ManagedSymmetricPvInverter;
import io.openems.edge.pvinverter.api.SunnyCentralPvInverter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.pvinverter.sunnycentral", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class SunnyCentralDeviceImpl extends AbstractOpenemsModbusComponent implements SunnyCentralDevice, SunnyCentralPvInverter, ManagedSymmetricPvInverter, ElectricityMeter, ModbusComponent, OpenemsComponent {
 
	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	private Config config = null;

	public SunnyCentralDeviceImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				SunnyCentralDevice.ChannelId.values(), //
				SunnyCentralPvInverter.ChannelId.values(), //
				ManagedSymmetricPvInverter.ChannelId.values(), //
				ElectricityMeter.ChannelId.values() //
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
					    m(ManagedSymmetricPvInverter.ChannelId.MAX_ACTIVE_POWER, new SignedWordElement(0x0000)) // requestedActivePower
					    
						),

				new FC3ReadRegistersTask(0x000A, Priority.HIGH,
					    m(SunnyCentralPvInverter.ChannelId.DC_PV_VOLTAGE, new SignedWordElement(0x000A)), // power
					    m(SunnyCentralPvInverter.ChannelId.DC_PV_CURRENT, new SignedWordElement(0x000B)), // voltage
					    m(SunnyCentralPvInverter.ChannelId.DC_PV_POWER, new SignedWordElement(0x000C)) // current
					    
						),


				new FC3ReadRegistersTask(0x0014, Priority.HIGH,
						m(ElectricityMeter.ChannelId.ACTIVE_POWER, new SignedWordElement(0x0014)), // totalActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L1, new SignedWordElement(0x0015)), // ph1ActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L2, new SignedWordElement(0x0016)), // ph2ActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L3, new SignedWordElement(0x0017)), // ph3ActivePower
					    m(ElectricityMeter.ChannelId.REACTIVE_POWER, new SignedWordElement(0x0018)), // totalApparentPower
					    m(ElectricityMeter.ChannelId.POWER_FACTOR, new SignedWordElement(0x0019)), // ph3PowerFactor
					    m(ElectricityMeter.ChannelId.FREQUENCY, new SignedWordElement(0x001A)), // frequency
					    m(ElectricityMeter.ChannelId.VOLTAGE, new SignedWordElement(0x001B)),
					    m(ElectricityMeter.ChannelId.CURRENT, new SignedWordElement(0x001C))
						),
				new FC3ReadRegistersTask(0x0063, Priority.HIGH,
		        		m(SunnyCentralPvInverter.ChannelId.ACTIVE_ENERGY, new SignedDoublewordElement(0x00063).wordOrder(WordOrder.LSWMSW)),
			            m(SunnyCentralPvInverter.ChannelId.REACTIVE_ENERGY, new SignedDoublewordElement(0x0065).wordOrder(WordOrder.LSWMSW))
		            ),


				new FC16WriteRegistersTask(0x001E, //
						m(ManagedSymmetricPvInverter.ChannelId.ACTIVE_POWER_LIMIT, new UnsignedWordElement(0x001E))

						) //
				);
	}


	@Override
	public String debugLog() {
		return "PV Power: " + this.getDcPvPower().toString();
	}

	@Override
	public MeterType getMeterType() {
		return MeterType.PRODUCTION;
	}
}
