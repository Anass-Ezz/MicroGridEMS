package io.openems.edge.controller.evse.cluster;

import static io.openems.edge.controller.evse.cluster.Utils.HYSTERESIS;
import static io.openems.edge.controller.evse.cluster.Utils.calculate;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.sum.Sum;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.controller.evse.single.ControllerEvseSingle;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Evse.Controller.Cluster", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class ControllerEvseClusterImpl extends AbstractOpenemsComponent
		implements OpenemsComponent, ControllerEvseCluster, Controller {

	private final Logger log = LoggerFactory.getLogger(ControllerEvseClusterImpl.class);
	private final Map<String, TreeMap<Instant, Integer>> applyHistories = new HashMap<>();

	@Reference
	private ConfigurationAdmin cm;

	@Reference
	private Sum sum;

	// TODO sort by configuration
	@Reference(policy = DYNAMIC, policyOption = GREEDY, cardinality = MULTIPLE)
	private volatile List<ControllerEvseSingle> ctrls = new CopyOnWriteArrayList<ControllerEvseSingle>();

	private Config config;

	public ControllerEvseClusterImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				ControllerEvseCluster.ChannelId.values(), //
				Controller.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) throws OpenemsNamedException {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() {
		for (var output : calculate(this.config.distributionStrategy(), this.sum, this.ctrls, this.applyHistories,
				this::logDebug)) {
			// Apply current & commands
			output.ctrl().apply(output.current(), output.commands());

			// Update apply history
			var history = this.applyHistories.computeIfAbsent(output.ctrl().id(),
					id -> new TreeMap<Instant, Integer>());
			var now = Instant.now();
			history.put(now, output.current());
			history.headMap(now.minusSeconds(HYSTERESIS)).clear(); // Clear outdated entries
		}
	}

	protected void logDebug(String message) {
		if (this.config.debugMode()) {
			// TODO LogVerbosity
			this.logInfo(this.log, message);
		}
	}
}
