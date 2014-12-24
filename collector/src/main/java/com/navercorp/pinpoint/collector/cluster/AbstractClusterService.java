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

package com.navercorp.pinpoint.collector.cluster;

import com.navercorp.pinpoint.collector.config.CollectorConfiguration;

/**
 * @author koo.taejin <kr14910>
 */
public abstract class AbstractClusterService implements ClusterService {

	protected final CollectorConfiguration config;
	protected final ClusterPointRouter clusterPointRouter;

	public AbstractClusterService(CollectorConfiguration config, ClusterPointRouter clusterPointRouter) {
		this.config = config;
		this.clusterPointRouter = clusterPointRouter;
	}

}
