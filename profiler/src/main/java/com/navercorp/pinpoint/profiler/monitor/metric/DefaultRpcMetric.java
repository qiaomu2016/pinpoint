/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.monitor.metric;

import com.navercorp.pinpoint.common.ServiceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author emeroad
 */
public class DefaultRpcMetric implements RpcMetric {

    private final ServiceType serviceType;
    // TODO lru cache로 변경할것. lru로 변경할 경우 counting이 틀려질 가능성이 있으나, oom이 발생하는것을 막을수 있음.
    private final ConcurrentMap<String, Histogram> histogramMap = new ConcurrentHashMap<String, Histogram>();

    public DefaultRpcMetric(ServiceType serviceType) {
        if (serviceType == null) {
            throw new NullPointerException("serviceType must not be null");
        }
        this.serviceType = serviceType;
    }

    @Override
    public void addResponseTime(String destinationId, int millis) {
        if (destinationId == null) {
            throw new NullPointerException("destinationId must not be null");
        }
        Histogram histogram = getHistogram0(destinationId);
        histogram.addResponseTime(millis);
    }

    private Histogram getHistogram0(String destinationId) {
        final Histogram hit = histogramMap.get(destinationId);
        if (hit != null) {
            return hit;
        }
        final Histogram histogram = createHistogram();

        final Histogram exist = histogramMap.putIfAbsent(destinationId, histogram);
        if (exist != null) {
            return exist;
        }
        return histogram;
    }

    private LongAdderHistogram createHistogram() {
        return new LongAdderHistogram(serviceType);
    }

    public List<HistogramSnapshot> createSnapshotList() {
        final List<HistogramSnapshot> histogramSnapshotList = new ArrayList<HistogramSnapshot>(histogramMap.size() + 4);
        for (Histogram histogram : histogramMap.values()) {
            final HistogramSnapshot snapshot = histogram.createSnapshot();
            histogramSnapshotList.add(snapshot);
        }
        return histogramSnapshotList;
    }
}
