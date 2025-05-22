package io.openems.common.jsonrpc.response;

import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonObject;

import io.openems.common.types.ChannelAddress;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;

/**
 * JSON-RPC response carrying monthly cost per channel.
 */
public class QueryHistoricMonthlyCostResponse extends JsonrpcResponseSuccess {
    private final Map<String, Map<ChannelAddress, Number>> costMap;

    public QueryHistoricMonthlyCostResponse(UUID id,
            Map<String, Map<ChannelAddress, Number>> costMap) {
        super(id);
        this.costMap = costMap;
    }

    public Map<String, Map<ChannelAddress, Number>> getCostMap() {
        return costMap;
    }

	@Override
	public JsonObject getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}