/**
 * Copyright (c) 2024 Vitor Pamplona
 *
 * This program is offered under a commercial and under the AGPL license.
 * For commercial licensing, contact me at vitor@vitorpamplona.com.
 * For AGPL licensing, see below.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * This application has not been clinically tested, approved by or registered in any health agency.
 * Even though this repository grants licenses to use to any person that follow it's license,
 * any clinical or commercial use must additionally follow the laws and regulations of the
 * pertinent jurisdictions. Having a license to use the source code does not imply on having
 * regulatory approvals to use or market any part of this code.
 */
package com.vitorpamplona.core.utils;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AngleChange360DispatcherTest extends BasicDispatcher {

    public final int STEPS_OF = 8; // degrees;

    @Test
    public void testAngleChange() {
        AngleChange360Dispatcher dispatcher = new AngleChange360Dispatcher(STEPS_OF);
        dispatcher.add(getStateListener());

        testNoTrigger(dispatcher, 100);

        testTrigger(dispatcher, 100, 120, 2);
        testTrigger(dispatcher, 120, 130, 1);
        testTrigger(dispatcher, 130, 140, 1);

        testTrigger(dispatcher, 140, 10, -16);

        testTrigger(dispatcher, 10, 170, 20);
        testNoTrigger(dispatcher, 171);
        testNoTrigger(dispatcher, 172);
        testNoTrigger(dispatcher, 173);
        testTrigger(dispatcher, 170, 240, 8);
        testTrigger(dispatcher, 240, 170, -8);
        testTrigger(dispatcher, 170, 10, -20);

        // half a loop
        testTrigger(dispatcher, 10, 190, 22);
        // half a loop
        testTrigger(dispatcher, 190, 10, -22);
    }
}


