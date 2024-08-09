package software.amazon.opentelemetry.javaagent.providers;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScopeBasedMetricExporter implements MetricExporter {
    private final MetricExporter defaultMetricExporter;
    private final MetricExporter applicationSignalsExporter;

    public ScopeBasedMetricExporter(MetricExporter defaultMetricExporter, MetricExporter applicationSignalsExporter) {
        this.defaultMetricExporter = defaultMetricExporter;
        this.applicationSignalsExporter = applicationSignalsExporter;
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> collection) {
        List<MetricData> rerouteMetrics = new LinkedList<>();
        Iterator<MetricData> iter = collection.iterator();
        while (iter.hasNext()) {
            MetricData   metricData = iter.next();
            String scopeName = metricData.getInstrumentationScopeInfo().getName();
            if ("io.opentelemetry.jmx".equals(scopeName)) {
                rerouteMetrics.add(metricData);
                iter.remove();
            }
        }
        defaultMetricExporter.export(collection);
        applicationSignalsExporter.export(rerouteMetrics);
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        defaultMetricExporter.flush();
        applicationSignalsExporter.flush();
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        defaultMetricExporter.shutdown();
        applicationSignalsExporter.shutdown();
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return defaultMetricExporter.getAggregationTemporality(instrumentType);
    }
}
