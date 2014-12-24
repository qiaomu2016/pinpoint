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

package com.navercorp.pinpoint.bootstrap;

import junit.framework.Assert;

import org.junit.Test;

import com.navercorp.pinpoint.bootstrap.PinpointBootStrap;

/**
 * @author emeroad
 */
public class PinpointBootStrapTest {
    @Test
    public void testDuplicatedLoadCheck() throws Exception {
        PinpointBootStrap.premain("test", new DummyInstrumentation());
        String exist = System.getProperty(PinpointBootStrap.BOOT_STRAP_LOAD_STATE);
        Assert.assertTrue(exist != null);

        PinpointBootStrap.premain("test", new DummyInstrumentation());
        // 중복 된경우를 체크 할수 있는 방법이 로그 확인 뿐이 없나??

        String recheck = System.getProperty(PinpointBootStrap.BOOT_STRAP_LOAD_STATE);
        Assert.assertEquals(exist, recheck);
    }
}
