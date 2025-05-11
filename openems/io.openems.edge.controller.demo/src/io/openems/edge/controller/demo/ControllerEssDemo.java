package io.openems.edge.controller.demo;

import io.openems.edge.common.channel.value.Value;

import io.openems.common.channel.AccessMode;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;

public interface ControllerEssDemo extends Controller, OpenemsComponent {



	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		TEST_CHANNEL(Doc.of(OpenemsType.INTEGER) //
				.unit(Unit.NONE) //
				.accessMode(AccessMode.READ_WRITE)),
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

	  public default IntegerWriteChannel getTestChannelChannel() {
	        return this.channel(ChannelId.TEST_CHANNEL);
	    }

	    public default Value<Integer> getTestChannelValue() {
	        return this.getTestChannelChannel().value();
	    }

}
