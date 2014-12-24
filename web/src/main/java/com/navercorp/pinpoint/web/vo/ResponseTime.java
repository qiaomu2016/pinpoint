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

package com.navercorp.pinpoint.web.vo;

import com.navercorp.pinpoint.common.ServiceType;
import com.navercorp.pinpoint.web.applicationmap.histogram.Histogram;
import com.navercorp.pinpoint.web.applicationmap.histogram.TimeHistogram;

import java.util.*;

/**
 * @author emeroad
 */
public class ResponseTime {
    // rowKey
    private final String applicationName;
    private final ServiceType applicationServiceType;
    private final long timeStamp;

    // agentId 가 key임.
    private final Map<String, TimeHistogram> responseHistogramMap = new HashMap<String, TimeHistogram>();


    public ResponseTime(String applicationName, short applicationServiceType, long timeStamp) {
        if (applicationName == null) {
            throw new NullPointerException("applicationName must not be null");
        }
        this.applicationName = applicationName;
        this.applicationServiceType = ServiceType.findServiceType(applicationServiceType);
        this.timeStamp = timeStamp;
    }


    public String getApplicationName() {
        return applicationName;
    }

    public short getApplicationServiceType() {
        return applicationServiceType.getCode();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Histogram findHistogram(String agentId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        return responseHistogramMap.get(agentId);
    }

    private Histogram getHistogram(String agentId) {
        if (agentId == null) {
            throw new NullPointerException("agentId must not be null");
        }
        TimeHistogram histogram = responseHistogramMap.get(agentId);
        if (histogram == null) {
            histogram = new TimeHistogram(applicationServiceType, timeStamp);
            responseHistogramMap.put(agentId, histogram);
        }
        return histogram;
    }

    public void addResponseTime(String agentId, short slotNumber, long count) {
        Histogram histogram = getHistogram(agentId);
        histogram.addCallCount(slotNumber, count);
    }


    public void addResponseTime(String agentId, Histogram copyHistogram) {
        if (copyHistogram == null) {
            throw new NullPointerException("copyHistogram must not be null");
        }
        Histogram histogram = getHistogram(agentId);
        histogram.add(copyHistogram);
    }

    public void addResponseTime(String agentId, int elapsedTime) {
        Histogram histogram = getHistogram(agentId);
        histogram.addCallCountByElapsedTime(elapsedTime);
    }

    public Collection<TimeHistogram> getAgentResponseHistogramList() {
        return responseHistogramMap.values();
    }

    public Histogram getApplicationResponseHistogram() {
        Histogram result = new Histogram(applicationServiceType);
        for (Histogram histogram : responseHistogramMap.values()) {
            result.add(histogram);
        }
        return result;
    }

    public Set<Map.Entry<String, TimeHistogram>> getAgentHistogram() {
        return this.responseHistogramMap.entrySet();
    }

    @Override
    public String toString() {
        return "ResponseTime{" +
                "applicationName='" + applicationName + '\'' +
                ", applicationServiceType=" + applicationServiceType +
                ", timeStamp=" + timeStamp +
                ", responseHistogramMap=" + responseHistogramMap +
                '}';
    }

}
