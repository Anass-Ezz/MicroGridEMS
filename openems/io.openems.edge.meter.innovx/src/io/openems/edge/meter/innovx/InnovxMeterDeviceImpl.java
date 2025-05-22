package io.openems.edge.meter.innovx;

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
import io.openems.edge.bridge.modbus.api.element.WordOrder;
import io.openems.edge.bridge.modbus.api.task.FC3ReadRegistersTask;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.taskmanager.Priority;
import io.openems.edge.meter.api.ElectricityMeter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.meter.innovx", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class InnovxMeterDeviceImpl extends AbstractOpenemsModbusComponent implements InnovxMeterDevice, ElectricityMeter, ModbusComponent, OpenemsComponent {

	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	private Config config = null;

	public InnovxMeterDeviceImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				InnovxMeterDevice.ChannelId.values(), //
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
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER, new SignedWordElement(0x0000)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L1, new SignedWordElement(0x0001)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L2, new SignedWordElement(0x0002)),
	            m(ElectricityMeter.ChannelId.ACTIVE_POWER_L3, new SignedWordElement(0x0003)),
	            m(ElectricityMeter.ChannelId.REACTIVE_POWER, new SignedWordElement(0x0004)),
	            m(ElectricityMeter.ChannelId.POWER_FACTOR, new SignedWordElement(0x0005)),
	            m(ElectricityMeter.ChannelId.FREQUENCY, new SignedWordElement(0x0006)),
	            m(ElectricityMeter.ChannelId.VOLTAGE, new SignedWordElement(0x0007)),
	            
	            m(ElectricityMeter.ChannelId.VOLTAGE_L1, new SignedWordElement(0x0008)),
	            m(ElectricityMeter.ChannelId.VOLTAGE_L2, new SignedWordElement(0x0009)),
	            m(ElectricityMeter.ChannelId.VOLTAGE_L3, new SignedWordElement(0x000A)),
	            
	            m(ElectricityMeter.ChannelId.CURRENT_L1, new SignedWordElement(0x000B)),
	            m(ElectricityMeter.ChannelId.CURRENT_L2, new SignedWordElement(0x000C)),
	            m(ElectricityMeter.ChannelId.CURRENT_L3, new SignedWordElement(0x000D)),
	            m(ElectricityMeter.ChannelId.CURRENT, new SignedWordElement(0x000E))
	        ),
	        
	        new FC3ReadRegistersTask(0x0063, Priority.HIGH,
        		m(InnovxMeterDevice.ChannelId.ACTIVE_ENERGY, new SignedDoublewordElement(0x00063).wordOrder(WordOrder.LSWMSW)),
	            m(InnovxMeterDevice.ChannelId.REACTIVE_ENERGY, new SignedDoublewordElement(0x0065).wordOrder(WordOrder.LSWMSW))
            )
	    );
	}

	@Override
	public String debugLog() {
		return this.getActiveEnergy().toString();
	}

	@Override
	public MeterType getMeterType() {
		return MeterType.CONSUMPTION_METERED;
	}
}