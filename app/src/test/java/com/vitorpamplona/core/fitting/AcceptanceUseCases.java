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

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.EyeGlassesUsageType;
import com.vitorpamplona.core.test.Acceptance;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AcceptanceUseCases {


    // MYOPIA Diff < 1.5D

    @Test
    public void testMyopiaWithLackofRX() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = null;
        AstigmaticLensParams netra = new AstigmaticLensParams(-2.5f, 0, 0);

        // If the did had glasses, bit does not know what it is, just prescribe NETRA
        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 25);

        assertEquals(-2.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 25);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 25);

        assertEquals(-2.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        // If he do not use glasses, prescribe a factor.
        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.NEAR, 25);

        assertEquals(-2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.FAR, 25);

        assertEquals(-2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.BOTH, 25);

        assertEquals(-2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

    @Test
    public void testMyopiaWithLackofRX2() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = null;
        AstigmaticLensParams netra = new AstigmaticLensParams(-2.25f, 0, 0);

        // If the did had glasses, bit does not know what it is, just prescribe NETRA
        AstigmaticLensParams acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.NEAR, 25);

        assertEquals(-2.0, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

    }

    @Test
    public void testHyperopiaWithLackofRX() {
        Acceptance acc = new Acceptance();

        AstigmaticLensParams current = null;
        AstigmaticLensParams netra = new AstigmaticLensParams(2.5f, 0, 0);

        // If the did had glasses, bit does not know what it is, just prescribe NETRA
        AstigmaticLensParams acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.NEAR, 25);

        assertEquals(2.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.FAR, 25);

        assertEquals(netra.getSphere(), acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, true, netra, EyeGlassesUsageType.BOTH, 25);

        assertEquals(2.5, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        // If he do not use glasses, prescribe a factor.
        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.NEAR, 25);

        assertEquals(2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.FAR, 25);

        assertEquals(2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);

        acceptable = acc.compute(current, false, netra, EyeGlassesUsageType.BOTH, 25);

        assertEquals(2.25, acceptable.getSphere(), 0.1);
        assertEquals(netra.getCylinder(), acceptable.getCylinder(), 0.1);
        assertEquals(netra.getAxis(), acceptable.getAxis(), 1);
        assertEquals(0, acceptable.getAddLens(), 0.1);
    }

}
