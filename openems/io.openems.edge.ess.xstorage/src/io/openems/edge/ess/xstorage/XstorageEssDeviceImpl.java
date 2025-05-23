package io.openems.edge.ess.xstorage;

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
import io.openems.edge.ess.api.XstorageEss;
import io.openems.edge.meter.api.ElectricityMeter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.ess.xstorage", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class XstorageEssDeviceImpl extends AbstractOpenemsModbusComponent implements XstorageEss, ElectricityMeter, XstorageEssDevice, ModbusComponent, OpenemsComponent {

	@Reference
	private ConfigurationAdmin cm;

	@Reference(policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MANDATORY)
	protected void setModbus(BridgeModbus modbus) {
		super.setModbus(modbus);
	}

	private Config config = null;

	public XstorageEssDeviceImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ModbusComponent.ChannelId.values(), //
				XstorageEssDevice.ChannelId.values(), //
				XstorageEss.ChannelId.values(), //
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
					    m(XstorageEss.ChannelId.REQUESTED_ACTIVE_POWER, new SignedWordElement(0x0000)), // requestedActivePower
					    m(XstorageEss.ChannelId.ACTUAL_ACTIVE_POWER, new SignedWordElement(0x0001)), // actualActivePower
					    m(XstorageEss.ChannelId.REQUESTED_REACTIVE_POWER, new SignedWordElement(0x0002)), // requestedReactivePower
					    m(XstorageEss.ChannelId.ACTUAL_REACTIVE_POWER, new SignedWordElement(0x0003)), // actualReactivePower
					    m(XstorageEss.ChannelId.GLOBAL_CHARGE_POWER_LIMIT, new SignedWordElement(0x0004)), // globalChargePowerLimit
					    m(XstorageEss.ChannelId.GLOBAL_DISCHARGE_POWER_LIMIT, new SignedWordElement(0x0005)), // globalDischargePowerLimit
					    m(XstorageEss.ChannelId.GLOBAL_MAX_SOC, new SignedWordElement(0x0006)), // globalMaxSoC
					    m(XstorageEss.ChannelId.GLOBAL_MIN_SOC, new SignedWordElement(0x0007)), // globalMinSoC
					    m(XstorageEss.ChannelId.RAMP_RATE, new SignedWordElement(0x0008)) // rampRate
						),

				new FC3ReadRegistersTask(0x000A, Priority.HIGH,
					    m(XstorageEss.ChannelId.BATTERY_PACK_POWER, new SignedWordElement(0x000A)), // power
					    m(XstorageEss.ChannelId.BATTERY_PACK_VOLTAGE, new SignedWordElement(0x000B)), // voltage
					    m(XstorageEss.ChannelId.BATTERY_PACK_CURRENT, new SignedWordElement(0x000C)), // current
					    m(XstorageEss.ChannelId.CAPACITY, new SignedWordElement(0x000D)), // batteryPackCapacity
					    m(XstorageEss.ChannelId.MAX_CELL_VOLTAGE, new SignedWordElement(0x000E)), // maxCellVoltage
					    m(XstorageEss.ChannelId.MIN_CELL_VOLTAGE, new SignedWordElement(0x000F)), // minCellVoltage
					    m(XstorageEss.ChannelId.MAX_CELL_TEMPERATURE, new SignedWordElement(0x0010)), // maxCellTemperature
					    m(XstorageEss.ChannelId.MIN_CELL_TEMPERATURE, new SignedWordElement(0x0011)), // minCellTemperature
					    m(XstorageEss.ChannelId.SOC, new SignedWordElement(0x0012)) // batterySoc
						),

				new FC3ReadRegistersTask(0x0014, Priority.HIGH,
					    m(XstorageEss.ChannelId.PCS_INPUT_POWER, new SignedWordElement(0x0014)), // inputPower
					    m(XstorageEss.ChannelId.PCS_OUTPUT_POWER, new SignedWordElement(0x0015)) // outputPower
						),
				

				new FC3ReadRegistersTask(0x001E, Priority.HIGH,
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L1, new SignedWordElement(0x001E)), // ph1ActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L2, new SignedWordElement(0x001F)), // ph2ActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER_L3, new SignedWordElement(0x0020)), // ph3ActivePower
					    m(ElectricityMeter.ChannelId.ACTIVE_POWER, new SignedWordElement(0x0021)), // totalActivePower
					    m(ElectricityMeter.ChannelId.REACTIVE_POWER_L1, new SignedWordElement(0x0022)), // ph1ApparentPower
					    m(ElectricityMeter.ChannelId.REACTIVE_POWER_L2, new SignedWordElement(0x0023)), // ph2ApparentPower
					    m(ElectricityMeter.ChannelId.REACTIVE_POWER_L3, new SignedWordElement(0x0024)), // ph3ApparentPower
					    m(ElectricityMeter.ChannelId.REACTIVE_POWER, new SignedWordElement(0x0025)), // totalApparentPower
					    m(ElectricityMeter.ChannelId.VOLTAGE_L1, new SignedWordElement(0x0026)), // ph1Voltage
					    m(ElectricityMeter.ChannelId.VOLTAGE_L2, new SignedWordElement(0x0027)), // ph2Voltage
					    m(ElectricityMeter.ChannelId.VOLTAGE_L3, new SignedWordElement(0x0028)), // ph3Voltage
					    m(ElectricityMeter.ChannelId.CURRENT_L1, new SignedWordElement(0x0029)), // ph1Current
					    m(ElectricityMeter.ChannelId.CURRENT_L2, new SignedWordElement(0x002A)), // ph2Current
					    m(ElectricityMeter.ChannelId.CURRENT_L3, new SignedWordElement(0x002B)), // ph3Current
					    m(ElectricityMeter.ChannelId.CURRENT, new SignedWordElement(0x002C)), // totalCurrent
					    m(ElectricityMeter.ChannelId.FREQUENCY, new SignedWordElement(0x002D)), // frequency
					    m(ElectricityMeter.ChannelId.POWER_FACTOR_L1, new SignedWordElement(0x002E)), // ph1PowerFactor
					    m(ElectricityMeter.ChannelId.POWER_FACTOR_L2, new SignedWordElement(0x002F)), // ph2PowerFactor
					    m(ElectricityMeter.ChannelId.POWER_FACTOR_L3, new SignedWordElement(0x0030)), // ph3PowerFactor
					    m(ElectricityMeter.ChannelId.POWER_FACTOR, new SignedWordElement(0x0031)) // ph3PowerFactor
						),
				
				new FC3ReadRegistersTask(0x0063, Priority.HIGH,
		        		m(XstorageEss.ChannelId.CHARGE_ACTIVE_ENERGY, new SignedDoublewordElement(0x00063).wordOrder(WordOrder.LSWMSW)),
			            m(XstorageEss.ChannelId.CHARGE_REACTIVE_ENERGY, new SignedDoublewordElement(0x0065).wordOrder(WordOrder.LSWMSW)),
			            m(XstorageEss.ChannelId.DISCHARGE_ACTIVE_ENERGY, new SignedDoublewordElement(0x00067).wordOrder(WordOrder.LSWMSW)),
			            m(XstorageEss.ChannelId.DISCHARGE_REACTIVE_ENERGY, new SignedDoublewordElement(0x0069).wordOrder(WordOrder.LSWMSW))
		            ),


				new FC16WriteRegistersTask(0x0032, //
						m(XstorageEss.ChannelId.SET_MAX_CHARGE_POWER, new UnsignedWordElement(0x0032)),
						m(XstorageEss.ChannelId.SET_MAX_DISCHARGE_POWER, new UnsignedWordElement(0x0033)),
						m(XstorageEss.ChannelId.SET_MIN_SOC, new UnsignedWordElement(0x0034)),
						m(XstorageEss.ChannelId.SET_MAX_SOC, new UnsignedWordElement(0x0035)),
						m(XstorageEss.ChannelId.SET_OPERATING_MODE, new UnsignedWordElement(0x0036)),
						m(XstorageEss.ChannelId.REQUEST_ACTIVE_POWER, new UnsignedWordElement(0x0037)),
						m(XstorageEss.ChannelId.REQUEST_REACTIVE_POWER, new UnsignedWordElement(0x0038)),
						m(XstorageEss.ChannelId.REQUEST_RAMP_RATE, new UnsignedWordElement(0x0039))
						) //
				);
	}

	@Override
	public String debugLog() {
		return "BESS SoC: " + this.getSoc().toString();
	}

	@Override
	public MeterType getMeterType() {
		// TODO Auto-generated method stub
		return MeterType.PRODUCTION_AND_CONSUMPTION;
	}

}
