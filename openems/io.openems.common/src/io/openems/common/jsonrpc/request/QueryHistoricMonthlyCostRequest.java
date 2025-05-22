package io.openems.common.jsonrpc.request;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonObject;

import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.types.ChannelAddress;
import io.openems.common.utils.JsonUtils;

/**
 * JSON-RPC request for monthly cost.
 */
public class QueryHistoricMonthlyCostRequest extends JsonrpcRequest {
    public static final String METHOD = "queryHistoricMonthlyCost";

    private final Instant fromDate;
    private final Instant toDate;
    private final List<ChannelAddress> channels;
    private final String timezone;

    public QueryHistoricMonthlyCostRequest(UUID id,
            Instant fromDate, Instant toDate,
            List<ChannelAddress> channels,
            String timezone) {
        super(id, METHOD);
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.channels = channels;
        this.timezone = timezone;
    }

    public Instant getFromDate() { return fromDate; }
    public Instant getToDate()   { return toDate;   }
    public List<ChannelAddress> getChannels() { return channels; }
    public String getTimezone()  { return timezone; }

    /**
     * Factory to parse params from generic JSON-RPC payload.
     */
    public static QueryHistoricMonthlyCostRequest from(JsonrpcRequest r) {
        JsonObject p = r.getParams().getAsJsonObject();
        return new QueryHistoricMonthlyCostRequest(
            r.getId(),
            Instant.parse(p.get("fromDate").getAsString()),
            Instant.parse(p.get("toDate").getAsString()),
            JsonUtils.getAsList(p, "channels", ChannelAddress::new),
            p.get("timezone").getAsString()
        );
    }

	@Override
	public JsonObject getParams() {
		// TODO Auto-generated method stub
		return null;
	}
}