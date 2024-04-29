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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AngleDiffTest {

    @Test
    public void testAngleConvention() {
        assertEquals(0, AngleDiff.angle0to180(0), 0.01);
        assertEquals(10, AngleDiff.angle0to180(190), 0.01f);
        assertEquals(175, AngleDiff.angle0to180(-5), 0.01f);
        assertEquals(0, AngleDiff.angle0to180(180), 0.01f);
        assertEquals(150, AngleDiff.angle0to180(150), 0.01f);
        assertEquals(90, AngleDiff.angle0to180(270), 0.01f);
        assertEquals(10, AngleDiff.angle0to180(-170), 0.01f);
        assertEquals(10, AngleDiff.angle0to180(10), 0.01f);
    }

    @Test
    public void testDifferences() {
        assertEquals(1, AngleDiff.diff180(0, 179), 0.01f);
        assertEquals(0, AngleDiff.diff180(0, 0), 0.01f);
        assertEquals(90, AngleDiff.diff180(90, 0), 0.01f);
        assertEquals(90, AngleDiff.diff180(0, 90), 0.01f);
        assertEquals(90, AngleDiff.diff180(180, 90), 0.01f);
        assertEquals(90, AngleDiff.diff180(90, 180), 0.01f);
        assertEquals(65, AngleDiff.diff180(45, 110), 0.01f);
        assertEquals(0, AngleDiff.diff180(270, 90), 0.01f);
        assertEquals(0, AngleDiff.diff180(90, 270), 0.01f);
        assertEquals(65, AngleDiff.diff180(110, 45), 0.01f);
        assertEquals(0, AngleDiff.diff180(0, 180), 0.01f);
        assertEquals(0, AngleDiff.diff180(180, 0), 0.01f);
        assertEquals(10, AngleDiff.diff180(0, 170), 0.01f);
        assertEquals(10, AngleDiff.diff180(170, 0), 0.01f);
        assertEquals(20, AngleDiff.diff180(10, 170), 0.01f);
        assertEquals(20, AngleDiff.diff180(170, 10), 0.01f);
        assertEquals(15, AngleDiff.diff180(-5, 190), 0.01f);
        assertEquals(15, AngleDiff.diff180(190, -5), 0.01f);
        assertEquals(15, AngleDiff.diff180(10, 175), 0.01f);
        assertEquals(15, AngleDiff.diff180(175, 10), 0.01f);
        assertEquals(5, AngleDiff.diff180(-5, 170), 0.01f);
        assertEquals(5, AngleDiff.diff180(170, -5), 0.01f);
        assertEquals(5, AngleDiff.diff180(80, 75), 0.01f);
        assertEquals(5, AngleDiff.diff180(75, 80), 0.01f);
        assertEquals(0, AngleDiff.diff180(90, 90), 0.01f);
    }


    @Test
    public void testIsSmallerThan360() {
        assertTrue(AngleDiff.isSmaller180(359, 1));
        assertTrue(AngleDiff.isSmaller180(360, 1));
        assertTrue(AngleDiff.isSmaller180(359, 0));
        assertTrue(AngleDiff.isSmaller180(360, 15));
        assertTrue(AngleDiff.isSmaller180(270, 310));
        assertTrue(AngleDiff.isSmaller180(185, 270));

        assertTrue(AngleDiff.isSmaller180(350, 5));
        assertTrue(AngleDiff.isSmaller180(340, 0));
        assertTrue(AngleDiff.isSmaller180(340, 350));

        assertTrue(AngleDiff.isSmaller180(160, 183.0f));
        assertTrue(AngleDiff.isSmaller180(158.125f, 182.0f));

        assertTrue(AngleDiff.isSmaller180(160, 184));
        assertTrue(AngleDiff.isSmaller180(170, 190));
        assertTrue(AngleDiff.isSmaller180(170, 180));
        assertTrue(AngleDiff.isSmaller180(180, 190));

        assertTrue(AngleDiff.isSmaller180(159, 181));

        assertFalse(AngleDiff.isSmaller180(190, 170));
        assertFalse(AngleDiff.isSmaller180(180, 170));
        assertFalse(AngleDiff.isSmaller180(190, 180));
    }

    @Test
    public void testSignedDiff180() {
        assertEquals(-5, AngleDiff.signedDiff180(30, 35), 0.01);
        assertEquals(5, AngleDiff.signedDiff180(35, 30), 0.01);

        assertEquals(-5, AngleDiff.signedDiff180(89, 94), 0.01);
        assertEquals(5, AngleDiff.signedDiff180(94, 89), 0.01);

        assertEquals(-5, AngleDiff.signedDiff180(4, 179), 0.01);
        assertEquals(5, AngleDiff.signedDiff180(179, 4), 0.01);
    }

    @Test
    public void testMean180() {
        assertEquals(35, AngleDiff.mean180(30 + 360, 35 - 180, 40 + 180), 0.01);
    }

    @Test
    public void testMean360() {
        assertEquals(35, AngleDiff.mean360(30 + 360, 35 - 360, 40 + 360), 0.01);
    }
}
