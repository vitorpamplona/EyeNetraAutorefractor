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

/**
 * Test Group NGVG015.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class RequiredCorrectionTest {

    public static final float INCH_IN_MM = 25.4f;
    public static final float DPI = 450f;

    public static final float lensEyeDistance = 10;// mm
    public static final float lensFocalLens = 75;// mm
    public static final float lensPhoneDistance = 130;// mm

    public static final float SLIT_RADIUS = 0.85f; // mm
    public static final float SLIT_DISTANCE = SLIT_RADIUS * 2;

    @Test
    public void testOpticalComputation() {
        RequiredCorrection correction = new RequiredCorrectionMaskLens(lensEyeDistance, lensPhoneDistance, lensFocalLens, (float) (INCH_IN_MM / DPI));

        assertEquals(-11.48, correction.computeDiopters(SLIT_DISTANCE, 4.11f), 0.1);
        assertEquals(-2.39, correction.computeDiopters(SLIT_DISTANCE, 1.79f), 0.1);
        assertEquals(3.78, correction.computeDiopters(SLIT_DISTANCE, 0.44f), 0.1);

        float slitRadiusInPixels = DPI / INCH_IN_MM * SLIT_RADIUS;

        // Simulates the slits in Vertical.
        Point2D p1 = new Point2D(0, slitRadiusInPixels);
        Point2D p2 = new Point2D(0, -slitRadiusInPixels);

        Pair p = new Pair(p1, p2, 0);

        assertEquals(15.38, correction.computeDiopters(p), 0.01);

        p.reduceDotPitch();
        p.reduceDotPitch();
        p.reduceDotPitch();
        p.reduceDotPitch();

        assertEquals(14.04, correction.computeDiopters(p), 0.01);
    }

    @Test
    public void testSetupFromDeviceDataset() {
        DeviceDataset.Device dev = DeviceDataset.get(333L);


        RequiredCorrection correction = new RequiredCorrectionMaskLens(dev.lensEyeDistance,
                dev.tubeLength, dev.lensFocalLength, (float) (INCH_IN_MM / DPI));

        assertEquals(-11.06, correction.computeDiopters(dev.slitDistance * 2, 4.11f), 0.1);
        assertEquals(-2.54, correction.computeDiopters(dev.slitDistance * 2, 1.79f), 0.1);
        assertEquals(3.78, correction.computeDiopters(dev.slitDistance * 2, 0.44f), 0.1);

        float slitRadiusInPixels = DPI / INCH_IN_MM * dev.slitDistance;

        // Simulates the slits in Vertical.
        Point2D p1 = new Point2D(0, slitRadiusInPixels);
        Point2D p2 = new Point2D(0, -slitRadiusInPixels);

        Pair p = new Pair(p1, p2, 0);

        assertEquals(16.66, correction.computeDiopters(p), 0.01);

        p.reduceDotPitch();
        p.reduceDotPitch();
        p.reduceDotPitch();
        p.reduceDotPitch();

        assertEquals(15.08, correction.computeDiopters(p), 0.01);
    }
}
