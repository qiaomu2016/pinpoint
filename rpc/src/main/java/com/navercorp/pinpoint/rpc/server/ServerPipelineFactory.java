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

package com.navercorp.pinpoint.rpc.server;


import com.navercorp.pinpoint.rpc.codec.PacketDecoder;
import com.navercorp.pinpoint.rpc.codec.PacketEncoder;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * @author emeroad
 */
public class ServerPipelineFactory implements ChannelPipelineFactory {
    private PinpointServerSocket serverSocket;

    public ServerPipelineFactory(PinpointServerSocket pinpointServerSocket) {
        if (pinpointServerSocket == null) {
            throw new NullPointerException("pinpointServerSocket");
        }
        this.serverSocket = pinpointServerSocket;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("decoder", new PacketDecoder());
        pipeline.addLast("encoder", new PacketEncoder());
        pipeline.addLast("handler", serverSocket);

        return pipeline;
    }
}
