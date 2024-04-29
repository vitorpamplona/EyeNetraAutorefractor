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
package com.vitorpamplona.core.fitting;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.EyeGlassesUsageType;
import com.vitorpamplona.core.test.Acceptance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AcceptanceUndercorrectTest {

    @Test
    public void testInProductiveYears() {
        Acceptance acc = new Acceptance();
        assertFalse(acc.isInProductiveYears(15));
        assertTrue(acc.isInProductiveYears(35));
        assertFalse(acc.isInProductiveYears(45));
    }

    // MYOPIA Diff < 1.5D

    @Test
    public void testMyopiaControlForUnder20() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        assertEquals(-1.0, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(-1.0, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(-1.0, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testMyopia20To40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 30);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 30);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 30);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testMyopiaOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);
    }


    // Myopia DIFF + 1.5


    @Test
    public void testMyopiaFacilitateAdaptationForUnder20NonProductive() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2.5f, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        //TODO: 0.5 or -1.15?
        assertEquals(-1.16, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(-1.16, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(-1.16, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testMyopiaFacilitateAdaptationFor20to40Productive() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 30);

        //TODO: -0.5 or -0.6?
        assertEquals(-0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 30);

        assertEquals(-0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 30);

        assertEquals(-0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testMyopiaFacilitateAdaptationForOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 45);

        //TODO: -0.5 or -0.99?
        assertEquals(-1.00f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 45);

        assertEquals(-1.00f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 45);

        assertEquals(-1.00f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);
    }


    // Hyperopia diff < 1.5D


    @Test
    public void testHyperopiaControlForUnder20() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        //TODO: 1 or 1.5?
        assertEquals(1.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(1.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testHyperopia20To40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 30);

        assertEquals(1.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 30);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 30);

        assertEquals(1.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testHyperopiaOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(1, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 45);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);
    }


    // Hyperopia diff > 1.5

    @Test
    public void testHyperopiaFacilitateAdaptationForUnder20NonProductive() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        assertEquals(0.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(1, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(1, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testHyperopiaFacilitateAdaptationFor20to40Productive() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 30);

        assertEquals(0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 30);

        assertEquals(0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 30);

        assertEquals(0.75f, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testHyperopiaFacilitateAdaptationForOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(2, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(0.5f, -1, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 45);

        assertEquals(0.9, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 45);

        assertEquals(0.9, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 45);

        assertEquals(0.9, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(1.5, acceptable.getAddLens(), 0.1);
    }

    // Reducing Cyl

    @Test
    public void testAstigmatismPerceptualAdaptationSmallDiffForUnder20() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        assertEquals(-1.5, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(-1.75, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testAstigmatismPerceptualAdaptationSmallDiffFor20to40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 35);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 35);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 35);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testAstigmatismPerceptualAdaptationSmallDiffForOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -1, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(2, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(2, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testAstigmatismPerceptualAdaptationBigDiffForUnder20() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -3, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 15);

        assertEquals(-1.5, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 15);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 15);

        assertEquals(-1.75, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testAstigmatismPerceptualAdaptationBigDiffFor20to40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -3, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 35);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 35);

        assertEquals(-2.00, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 35);

        assertEquals(-2.00, acceptable.getSphere(), 0.1);
        assertEquals(-0.5f, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }


    @Test
    public void testAstigmatismPerceptualAdaptationBigDiffForOver40() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = new AstigmaticLensParams(-1, -3, 90);
        AstigmaticLensParams netra = new AstigmaticLensParams(-2, -0.5f, 90);

        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(2, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 50);

        assertEquals(-2, acceptable.getSphere(), 0.1);
        assertEquals(-0.5, acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(2, acceptable.getAddLens(), 0.1);
    }
}
