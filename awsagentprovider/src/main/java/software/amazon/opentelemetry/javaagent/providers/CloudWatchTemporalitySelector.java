package software.amazon.opentelemetry.javaagent.providers;

import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector;

public class CloudWatchTemporalitySelector {
    static AggregationTemporalitySelector alwaysDelta() {
        return (instrumentType) -> {
            return AggregationTemporality.DELTA;
        };
    }
}
