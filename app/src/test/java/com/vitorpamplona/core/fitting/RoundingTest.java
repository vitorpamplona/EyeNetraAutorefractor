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
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class RoundingTest {

    public RoundingTest() {
        // TODO Auto-generated constructor stub
    }


    @Test
    public void testCase25() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(2.75f, -1.00f, 8f);

        // -6.25 -1.25 53
        myPoints.put(180, 3.7f);
        myPoints.put(30, 2.8f);
        myPoints.put(50, 2.6f);
        myPoints.put(70, 2.3f);
        myPoints.put(90, 1.9f);
        myPoints.put(120, 1.9f);
        myPoints.put(140, 1.2f);
        myPoints.put(160, 2.0f);

        Collection<MeridianPower> meridians = map(myPoints).values();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(meridians);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams roundedFit025 = new BestRounding().round25(basicFit, meridians, 0, builder);

        System.out.println("Reason: " + builder.toString());

        assertEquals(0, roundedFit025.getSphere() % 0.25, 0.2);
        assertEquals(0, roundedFit025.getCylinder() % 0.25, 0.2);
        assertEquals(0, roundedFit025.getAxis() % 5, 0.2);
    }

    @Test
    public void testCaseWeird() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-7f, 0.00f, 0f);

        // -6.25 -1.25 53

        myPoints.put(35, -7.12f);
        myPoints.put(125, -7.10f);
        myPoints.put(59, -7.11f);
        myPoints.put(82, -7.04f);
        myPoints.put(104, -6.79f);
        myPoints.put(170, -7.35f);
        myPoints.put(12, -7.10f);
        myPoints.put(148, -6.67f);

        Collection<MeridianPower> meridians = map(myPoints).values();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(meridians);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams outlierFit0625 = new OutlierRemoval().run(meridians, 0, basicFit, builder);

        AstigmaticLensParams roundedFit0625 = new BestRounding().round25(outlierFit0625, meridians, 0, builder);

        System.out.println("Reason: " + builder.toString());

        assertEquals(real.getSphere(), roundedFit0625.getSphere(), 0.2);
        assertEquals(real.getCylinder(), roundedFit0625.getCylinder(), 0.2);
        assertEquals(real.getAxis(), roundedFit0625.getAxis(), 1);
    }


    @Test
    public void testCase6() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(2.75f, -1.00f, 24f);

        // -6.25 -1.25 53
        myPoints.put(180, 3.7f);
        myPoints.put(30, 2.8f);
        myPoints.put(50, 2.6f);
        myPoints.put(70, 2.3f);
        myPoints.put(90, 1.9f);
        myPoints.put(120, 1.9f);
        myPoints.put(140, 1.2f);
        myPoints.put(160, 2.0f);

        Collection<MeridianPower> meridians = map(myPoints).values();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(meridians);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams roundedFit0625 = new BestRounding().round0625(basicFit, meridians, 0, builder);

        System.out.println("Reason: " + builder.toString());

        assertEquals(0, roundedFit0625.getSphere() % 0.0625, 0.2);
        assertEquals(0, roundedFit0625.getCylinder() % 0.0625, 0.2);
        assertEquals(real.getAxis(), roundedFit0625.getAxis(), 1);
    }

    @Test
    public void testCase12() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(2.75f, -1.00f, 24f);

        // -6.25 -1.25 53
        myPoints.put(180, 3.7f);
        myPoints.put(30, 2.8f);
        myPoints.put(50, 2.6f);
        myPoints.put(70, 2.3f);
        myPoints.put(90, 1.9f);
        myPoints.put(120, 1.9f);
        myPoints.put(140, 1.2f);
        myPoints.put(160, 2.0f);

        Collection<MeridianPower> meridians = map(myPoints).values();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(meridians);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams roundedFit12 = new BestRounding().round125(basicFit, meridians, 0, builder);

        System.out.println("Reason: " + builder.toString());

        assertEquals(0, roundedFit12.getSphere() % 0.12, 0.2);
        assertEquals(0, roundedFit12.getCylinder() % 0.12, 0.2);
        assertEquals(real.getAxis(), roundedFit12.getAxis(), 1);
    }

    @Test
    public void testCase62() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(0.83f, -1.42f, 77f);

        // -6.25 -1.25 53
        myPoints.put(6, -2.6f);
        myPoints.put(28, 1.1f);
        myPoints.put(51, -2.2f);
        myPoints.put(84, 4.5f);
        myPoints.put(96, 1.3f);
        myPoints.put(118, -2.4f);
        myPoints.put(129, -5.7f);
        myPoints.put(174, 1.7f);

        Collection<MeridianPower> meridians = map(myPoints).values();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(meridians);

        System.out.println("Basic fit " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams noOutliers = new OutlierRemoval().run(meridians, 0, basicFit, builder);

        System.out.println("No Outliers " + noOutliers);

        AstigmaticLensParams roundedFit0625 = new BestRounding().round0625(noOutliers, meridians, 0, builder);

        System.out.println("Rounded " + roundedFit0625);


        System.out.println("Reason: " + builder.toString());

        assertEquals(0, roundedFit0625.getSphere() % 0.0625, 0.2);
        assertEquals(0, roundedFit0625.getCylinder() % 0.0625, 0.2);
        //assertEquals(real.getAxis(), roundedFit0625.getAxis(), 1); // Axis doesn't matter
    }

    public FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
    }
}
