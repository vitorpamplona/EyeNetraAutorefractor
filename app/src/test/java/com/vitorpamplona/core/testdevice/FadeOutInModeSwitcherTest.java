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
package com.vitorpamplona.core.testdevice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class FadeOutInModeSwitcherTest {

    @Test
    public void test() {
        MockClock clock = new MockClock(1000);
        FadeOutInModeSwitcher switcher = new FadeOutInModeSwitcher(100, 50, -1);
        switcher.setClock(clock);


        // Test fading from -1 to 1.
        switcher.start(1);

        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);

        // Test staying stable at 1.

        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);

        // Test fading from 1 to -1.
        switcher.start();

        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);

        // Test staying stable at 1.

        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);

        // Double checking fading from -1 to 1.

        switcher.start();

        clock.pass(10);
        assertEquals(-0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
    }

    @Test
    public void testDoubleStarting() {
        MockClock clock = new MockClock(1000);
        FadeOutInModeSwitcher switcher = new FadeOutInModeSwitcher(100, 50, -1);
        switcher.setClock(clock);


        // Test fading from -1 to 1.
        switcher.start();

        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);

        // Test fading from 1 to -1.
        switcher.start();

        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.7, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.3, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        switcher.start();
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
    }


    @Test
    public void testStartingTowards() {
        MockClock clock = new MockClock(1000);
        FadeOutInModeSwitcher switcher = new FadeOutInModeSwitcher(100, 50, -1);
        switcher.setClock(clock);


        // Test fading from -1 to 1.
        switcher.start();

        assertEquals(-1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        switcher.start(1);
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        switcher.start(-1);
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.7, switcher.computeAlpha(), 0.01);
        switcher.start(1);
        clock.pass(10);
        assertEquals(-0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);


        // Test fading from 1 to -1.
        switcher.start(-1);

        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.7, switcher.computeAlpha(), 0.01);
        switcher.start(1);
        clock.pass(10);
        assertEquals(0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        switcher.start(1);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(1.0, switcher.computeAlpha(), 0.01);
        switcher.start(-1);
        clock.pass(10);
        assertEquals(0.9, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);
        switcher.start(1);
        clock.pass(10);
        assertEquals(-0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.8, switcher.computeAlpha(), 0.01);
        switcher.start(-1);
        clock.pass(10);
        assertEquals(0.7, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.6, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.5, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.4, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.3, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.1, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(0.0, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.2, switcher.computeAlpha(), 0.01);
        clock.pass(10);
        assertEquals(-0.4, switcher.computeAlpha(), 0.01);

    }
}
