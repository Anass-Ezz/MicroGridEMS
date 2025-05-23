package io.openems.edge.controller.demo;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.google.common.base.Optional;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.XstorageEss;


@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Controller.io.openems.edge.controller.demo", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class ControllerEssDemoImpl extends AbstractOpenemsComponent implements XstorageEss, ControllerEssDemo, Controller, OpenemsComponent {

	
	
	@Reference
	private ConfigurationAdmin cm;
	
	@Reference
	private XstorageEss ess;

	
	private Config config = null;

	
	
	public ControllerEssDemoImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				ControllerEssDemo.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
			return;
		}
	}

	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	
	
//	************************** For confirming the writing in a channel ********************************
//	@Override
//	public void run() throws OpenemsNamedException {
//		// Retrieve the next value set via JSON-RPC
//        Optional<Integer> nextValue = this.getTestChannelChannel().getNextWriteValueAndReset();
//        // If a value is present, set it as the current value to confirm the write
//        nextValue.ifPresent(value -> this.getTestChannelChannel().setNextValue(value));
//	}
	
	
	@Override
	public void run() throws OpenemsNamedException {
		ess.getRequestActivePowerChannel().setNextWriteValue(0);
	}
	
	
	@Override
	public String debugLog() {
		return "L:" + ess.getRequestedActivePower().asString();
	}
	
}
