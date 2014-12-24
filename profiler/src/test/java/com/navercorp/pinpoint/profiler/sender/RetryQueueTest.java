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

package com.navercorp.pinpoint.profiler.sender;

import junit.framework.Assert;

import org.junit.Test;

import com.navercorp.pinpoint.profiler.sender.RetryMessage;
import com.navercorp.pinpoint.profiler.sender.RetryQueue;

/**
 * @author emeroad
 */
public class RetryQueueTest {
    @Test
    public void size() {

        RetryQueue retryQueue = new RetryQueue(1, 1);
        retryQueue.add(new RetryMessage(0, new byte[0]));
        retryQueue.add(new RetryMessage(0, new byte[0]));

        Assert.assertEquals(1, retryQueue.size());

    }

    @Test
    public void size2() {

        RetryQueue retryQueue = new RetryQueue(1, 1);
        RetryMessage retryMessage = retryQueue.get();
        Assert.assertNull(retryMessage);
    }

    @Test
    public void maxRetryTest() {

        RetryQueue retryQueue = new RetryQueue(3, 2);
        RetryMessage retryMessage = new RetryMessage(0, new byte[0]);
        retryMessage.fail();
        retryMessage.fail();


        retryQueue.add(retryMessage);
        retryQueue.add(retryMessage);

        Assert.assertEquals(retryQueue.size(), 0);
    }

    @Test
    public void add() {

        RetryQueue retryQueue = new RetryQueue(3, 2);
        retryQueue.add(new RetryMessage(0, new byte[0]));
        // 하나 넣고.실패한 메시지 넣으면 반이상이라서 버려야됨.
        RetryMessage retryMessage = new RetryMessage(0, new byte[0]);
        retryMessage.fail();
        retryQueue.add(retryMessage);

        Assert.assertEquals(retryQueue.size(), 1);
    }
}
