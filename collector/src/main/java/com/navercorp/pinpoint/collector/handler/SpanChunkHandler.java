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

package com.navercorp.pinpoint.collector.handler;

import java.util.List;

import org.apache.thrift.TBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.navercorp.pinpoint.collector.dao.TracesDao;
import com.navercorp.pinpoint.common.ServiceType;
import com.navercorp.pinpoint.common.util.SpanEventUtils;
import com.navercorp.pinpoint.thrift.dto.TSpanChunk;
import com.navercorp.pinpoint.thrift.dto.TSpanEvent;

import org.springframework.stereotype.Service;

/**
 * @author emeroad
 */
@Service
public class SpanChunkHandler implements SimpleHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TracesDao traceDao;

	@Autowired
	private StatisticsHandler statisticsHandler;

	@Override
	public void handleSimple(TBase<?, ?> tbase) {

		if (!(tbase instanceof TSpanChunk)) {
			throw new IllegalArgumentException("unexpected tbase:" + tbase + " expected:" + this.getClass().getName());
		}

		try {
			TSpanChunk spanChunk = (TSpanChunk) tbase;

			if (logger.isDebugEnabled()) {
				logger.debug("Received SpanChunk={}", spanChunk);
			}

			traceDao.insertSpanChunk(spanChunk);

			List<TSpanEvent> spanEventList = spanChunk.getSpanEventList();
			if (spanEventList != null) {
				logger.debug("SpanChunk Size:{}", spanEventList.size());
				// TODO 껀바이 껀인데. 나중에 뭔가 한번에 업데이트 치는걸로 변경해야 될듯.
				for (TSpanEvent spanEvent : spanEventList) {
					final ServiceType serviceType = ServiceType.findServiceType(spanEvent.getServiceType());

					if (!serviceType.isRecordStatistics()) {
						continue;
					}

					// if terminal update statistics
					final int elapsed = spanEvent.getEndElapsed();
					final boolean hasException = SpanEventUtils.hasException(spanEvent);

					/**
					 * 통계정보에 기반한 서버맵을 그리기 위한 정보 저장.
					 */
					// 내가 호출한 정보 저장. (span이 호출한 spanevent)
					statisticsHandler.updateCaller(spanChunk.getApplicationName(), spanChunk.getServiceType(), spanChunk.getAgentId(), spanEvent.getDestinationId(), serviceType.getCode(), spanEvent.getEndPoint(), elapsed, hasException);

					// 나를 호출한 정보 저장 (spanevent를 호출한 span)
					statisticsHandler.updateCallee(spanEvent.getDestinationId(), spanEvent.getServiceType(), spanChunk.getApplicationName(), spanChunk.getServiceType(), spanChunk.getEndPoint(), elapsed, hasException);
				}
			}
		} catch (Exception e) {
			logger.warn("SpanChunk handle error Caused:{}", e.getMessage(), e);
		}
	}
}