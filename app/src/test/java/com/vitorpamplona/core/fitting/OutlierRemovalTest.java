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
import static org.junit.Assert.assertTrue;

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class OutlierRemovalTest {

    @Test
    public void testCase1TwoOutliers() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(2.75f, -1.00f, 8f);

        // -6.25 -1.25 53
        myPoints.put(180, 3.7f);
        myPoints.put(30, 2.8f);
        myPoints.put(50, 2.6f);
        myPoints.put(70, 2.3f);
        myPoints.put(90, 1.9f);
        myPoints.put(120, 3.5f);
        myPoints.put(140, 1.2f);
        myPoints.put(160, 3.0f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, builder);

        System.out.println("Reason: " + builder.toString());

        TestUtils.checkOutliers(data, 180);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);
        assertEquals(real.getSphere(), enhancedFit.getSphere(), 0.2);

        assertTrue("Worse Cyl", Math.abs(basicFit.getCylinder()) > Math.abs(enhancedFit.getCylinder()));

        assertEquals(real.getAxis(), enhancedFit.getAxis(), 50);
    }

    @Test
    public void testCase2OneOutlier() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(2.50f, -0.75f, 160f);
        System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(180, 1.8f);
        myPoints.put(30, 3.9f);
        myPoints.put(50, 2.0f);
        myPoints.put(70, 2.2f);
        myPoints.put(90, 2.4f);
        myPoints.put(120, 2.2f);
        myPoints.put(140, 2.4f);
        myPoints.put(160, 2.3f);

        FloatHashMap data = map(myPoints);
        StringBuilder log = new StringBuilder();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, log);

        System.out.println("Corrected: " + enhancedFit);

        System.out.println(log.toString());

        TestUtils.checkOutliers(data, 30);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals(real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertTrue("Worse Cyl", Math.abs(basicFit.getCylinder()) >= Math.abs(enhancedFit.getCylinder()));
        assertEquals(real.getAxis(), enhancedFit.getAxis(), 50);
    }

    @Test
    public void testCaseOneOutlierNoCyl() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(0f, 0f, 0f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(180, -11f);
        myPoints.put(30, 0.3f);
        myPoints.put(50, -0.033f);
        myPoints.put(70, 0.048f);
        myPoints.put(90, 0.49f);
        myPoints.put(120, 0.11f);
        myPoints.put(140, -0.45f);
        myPoints.put(160, 0.23f);

        FloatHashMap<MeridianPower> powers = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(powers.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(powers.values(), 0, basicFit, null);

        AstigmaticLensParams rounded = new BestRounding().round25(enhancedFit, powers.values(), 0, null);

        // for (MeridianmyPoints.put(p : powers.values()) {
        // 	System.out.println(p);
        // }

        System.out.println("Corrected: " + enhancedFit);

        System.out.println("Rounded: " + rounded);

        TestUtils.checkOutliers(powers, 180);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertTrue("Rounded method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals(real.getSphere(), rounded.getSphere(), 0.1);
        assertTrue("Worse Cyl", Math.abs(basicFit.getCylinder()) >= Math.abs(rounded.getCylinder()));
        assertEquals(real.getAxis(), rounded.getAxis(), 10);
    }

    @Test
    public void testCaseGuiOneOutlier() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(0.5f, -0.25f, 15f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(180, -7.3f);
        myPoints.put(30, 0.62f);
        myPoints.put(50, 0.32f);
        myPoints.put(70, 0.048f);
        myPoints.put(90, -0.58f);
        myPoints.put(120, -.20f);
        myPoints.put(140, 0.25f);
        myPoints.put(160, -0.057f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        //System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, null);

        //System.out.println("Corrected: " + enhancedFit);

        TestUtils.checkOutliers(data, 180);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals(real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertTrue("Worse Cyl", Math.abs(basicFit.getCylinder()) >= Math.abs(enhancedFit.getCylinder()));
        assertEquals(real.getAxis(), enhancedFit.getAxis(), 50);
    }

    @Test
    public void testCase3OneOutlier() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(0.25f, -1.75f, 60f);

        myPoints.put(30, -0.19f);
        myPoints.put(50, 0.11f);
        myPoints.put(70, 0.31f);
        myPoints.put(90, -1.6f);
        myPoints.put(120, 1.0f); // outlier
        myPoints.put(140, -2.0f);
        myPoints.put(160, -1.2f);
        myPoints.put(180, -1.3f);

        FloatHashMap<MeridianPower> data = map(myPoints);
        StringBuilder why = new StringBuilder();

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(data, 90, 120);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

    }

    @Test
    public void testCaseOutlierFarFromTheCurve() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-1.75f, -1.21f, 33f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(180, -1.9f);
        myPoints.put(30, -1.7f);
        myPoints.put(50, -4.2f);
        myPoints.put(70, -2.5f);
        myPoints.put(90, -2.2f);
        myPoints.put(120, -2.8f);
        myPoints.put(140, -3.1f);
        myPoints.put(160, -2.5f);

        StringBuilder strBuilder = new StringBuilder();

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, strBuilder);

        System.out.println("Corrected: " + enhancedFit + " " + strBuilder.toString());

        TestUtils.checkOutliers(data, 50);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals(real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals(real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals(real.getAxis(), enhancedFit.getAxis(), 10);
    }

    @Test
    public void testCaseOutlierFarFromTheCurve2() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-2f, -0.5f, 90f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(180, -2.7f);
        myPoints.put(30, -2.1f);
        myPoints.put(50, -2.1f);
        myPoints.put(70, -2.2f);
        myPoints.put(90, -2.1f);
        myPoints.put(120, 5.0f);
        myPoints.put(140, -2.1f);
        myPoints.put(160, -2.7f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        //System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, null);

        //System.out.println("Corrected: " + enhancedFit);

        TestUtils.checkOutliers(data, 120);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals(real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals(real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals(real.getAxis(), enhancedFit.getAxis(), 10);
    }

    @Test
    public void testCaseOutlierFarFromTheCurve3() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-0.75f, -1f, 95f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(3, -1.9f);
        myPoints.put(28, -1.3f);
        myPoints.put(54, -1.3f);
        myPoints.put(80, -0.80f);
        myPoints.put(105, -0.59f);
        myPoints.put(131, -1.1f);
        myPoints.put(157, -4.7f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, null);

        System.out.println("Corrected: " + enhancedFit);

        TestUtils.checkOutliers(data, 157);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 10);
    }

    @Test
    public void testCaseOutlierFarFromTheCurve4() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-4.0f, -0.75f, 176f);
        //System.out.println("Real: " + real);

        // -6.25 -1.25 53
        myPoints.put(3, -4.3f);
        myPoints.put(28, -3.9f);
        myPoints.put(54, -4.5f);
        myPoints.put(80, -4.8f);
        myPoints.put(105, -4.8f);
        myPoints.put(131, -4.1f);
        myPoints.put(157, -2.8f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, null);

        System.out.println("Corrected: " + enhancedFit);

        TestUtils.checkOutliers(data, 157);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 10);
    }

    @Test
    public void testCaseOutlierFarFromTheCurve5() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-3.25f, -1.25f, 55f);
        //System.out.println("Real: " + real);

        myPoints.put(3, -3.3f);
        myPoints.put(28, -4.3f);
        myPoints.put(54, -3.0f);
        myPoints.put(80, -2.9f);
        myPoints.put(105, -4.4f);
        myPoints.put(131, -4.9f);
        myPoints.put(157, -6.3f);

        FloatHashMap data = map(myPoints);

        StringBuilder why = new StringBuilder();
        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());
        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, why);

        System.out.println("Basic: " + basicFit);
        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);


        TestUtils.checkOutliers(data, 131, 157);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 10);
    }

    @Test
    public void testBadFittingOutlier() {
        FloatHashMap<MeridianPower> myPoints = new FloatHashMap<MeridianPower>();

        AstigmaticLensParams real = new AstigmaticLensParams(-0.5f, -1.00f, 100f);

        myPoints.put(39.0f, new MeridianPower(39, -1.34f));
        myPoints.put(58.0f, new MeridianPower(58, -1.04f));
        myPoints.put(135.0f, new MeridianPower(135, -0.90f));
        myPoints.put(81.0f, new MeridianPower(81, -0.63f));
        myPoints.put(104.0f, new MeridianPower(104, -2.20f));
        myPoints.put(151.0f, new MeridianPower(151, -1.21f));
        myPoints.put(175.0f, new MeridianPower(175, -1.55f));
        myPoints.put(18.0f, new MeridianPower(18, -1.5f));

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(myPoints.values());

        System.out.println("Basic: " + basicFit);

        StringBuilder why = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(myPoints.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(myPoints, 104);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.15);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.15);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 5);
    }

    @Test
    public void testAxisSmallOutlier() {
        FloatHashMap<MeridianPower> myPoints = new FloatHashMap<MeridianPower>();

        AstigmaticLensParams real = new AstigmaticLensParams(-0.0f, -0.70f, 100f);


        myPoints.put(38.0f, new MeridianPower(38, -0.48f));
        myPoints.put(57.0f, new MeridianPower(57, -0.52f));
        myPoints.put(80.0f, new MeridianPower(80, -0.11f));
        myPoints.put(105.0f, new MeridianPower(105, 0.10f));
        myPoints.put(123.0f, new MeridianPower(123, -0.11f));
        myPoints.put(150.0f, new MeridianPower(150, -0.47f));
        myPoints.put(171.0f, new MeridianPower(171, -0.64f));
        myPoints.put(18.0f, new MeridianPower(18, -0.65f));

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(myPoints.values());

        System.out.println("Basic: " + basicFit);

        StringBuilder why = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(myPoints.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(myPoints);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.15);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.15);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 5);
    }

    @Test
    public void testLoweringCylOutlier() {
        FloatHashMap<MeridianPower> myPoints = new FloatHashMap<MeridianPower>();

        AstigmaticLensParams real = new AstigmaticLensParams(-0.15f, -0.78f, 105f);

        myPoints.put(37.0f, new MeridianPower(37, -0.78f));
        myPoints.put(124.0f, new MeridianPower(124, -0.17f));
        myPoints.put(60.0f, new MeridianPower(60, -0.40f));
        myPoints.put(80.0f, new MeridianPower(80, -0.41f));
        myPoints.put(104.0f, new MeridianPower(104, -0.08f));
        myPoints.put(150.0f, new MeridianPower(150, -0.73f));
        myPoints.put(175.0f, new MeridianPower(175, -0.79f));
        myPoints.put(15.0f, new MeridianPower(15, -1.81f));

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(myPoints.values());

        System.out.println("Basic: " + basicFit);

        StringBuilder why = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(myPoints.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(myPoints, 15);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.16);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.16);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 5);
    }

    @Test
    public void testHardToFindOutlierat39() {
        FloatHashMap<MeridianPower> myPoints = new FloatHashMap<MeridianPower>();

        AstigmaticLensParams real = new AstigmaticLensParams(-0.35f, -0.25f, 145f);

        myPoints.put(22.0f, new MeridianPower(22, -0.41f));
        myPoints.put(39.0f, new MeridianPower(39, -0.77f));
        myPoints.put(66.0f, new MeridianPower(66, -0.57f));
        myPoints.put(85.0f, new MeridianPower(85, -0.52f));
        myPoints.put(112.0f, new MeridianPower(112, -0.46f));
        myPoints.put(135.0f, new MeridianPower(135, -0.44f));
        myPoints.put(151.0f, new MeridianPower(151, -0.32f));
        myPoints.put(178.0f, new MeridianPower(178, -1.00f));


        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(myPoints.values());

        System.out.println("Basic: " + basicFit);

        StringBuilder why = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(myPoints.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(myPoints);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.16);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.16);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 35);
    }

    @Test
    public void testHardToFindOutlierat91() {
        FloatHashMap<MeridianPower> myPoints = new FloatHashMap<MeridianPower>();

        AstigmaticLensParams real = new AstigmaticLensParams(-3.45f, -1.00f, 156f);

        myPoints.put(113.f, new MeridianPower(113, -3.95f));
        myPoints.put(91.f, new MeridianPower(91, -5.94f));
        myPoints.put(46.f, new MeridianPower(46, -4.23f));
        myPoints.put(67.f, new MeridianPower(67, -4.54f));
        myPoints.put(139.0f, new MeridianPower(139, -3.52f));
        myPoints.put(177.0f, new MeridianPower(177, -3.31f));
        myPoints.put(22.0f, new MeridianPower(22, -4.28f));
        myPoints.put(155f, new MeridianPower(155, -3.50f));


        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(myPoints.values());

        System.out.println("Basic: " + basicFit);

        StringBuilder why = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(myPoints.values(), 0, basicFit, why);

        System.out.println("Corrected: " + enhancedFit);
        System.out.println("Why: " + why);

        TestUtils.checkOutliers(myPoints, 91);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.16);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.16);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 5);
    }

    @Test
    public void testCaseOutlierFarFromTheCurve6() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-3.0f, -1.75f, 48f);
        //System.out.println("Real: " + real);

        myPoints.put(3, -3.3f);
        myPoints.put(28, -3.3f);
        myPoints.put(54, -3.2f);
        myPoints.put(80, -2.9f);
        myPoints.put(105, -4.9f);
        myPoints.put(131, -4.6f);
        myPoints.put(157, -5.6f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());

        System.out.println("Basic: " + basicFit);

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, basicFit, null);

        System.out.println("Corrected: " + enhancedFit);

        TestUtils.checkOutliers(data, 157);

        assertTrue("Enhanced method is worse. " + debug(enhancedFit, basicFit, real),
                closestToTheReal(enhancedFit, basicFit, real) == enhancedFit);

        assertEquals("Sphere accuracy", real.getSphere(), enhancedFit.getSphere(), 0.2);
        assertEquals("Cylinder accuracy", real.getCylinder(), enhancedFit.getCylinder(), 0.2);
        assertEquals("Axis accuracy", real.getAxis(), enhancedFit.getAxis(), 10);
    }


    public String debug(AstigmaticLensParams enhancedFit, AstigmaticLensParams basicFit, AstigmaticLensParams real) {
        FourierDomainAnalysis f = new FourierDomainAnalysis();
        DecimalFormat formatter = new DecimalFormat("0.00");

        return "\n Real: \t" + real + " \t MSE:" + formatter.format(f.fourierMSE(real)) + " \t J0:" + formatter.format(f.fourierJ0(real)) + " \t J45:" + formatter.format(f.fourierJ45(real)) +
                "\n Fitted: \t" + basicFit + " \t MSE:" + formatter.format(f.fourierMSE(basicFit)) + " \t J0:" + formatter.format(f.fourierJ0(basicFit)) + " \t J45:" + formatter.format(f.fourierJ45(basicFit)) +
                "\n Enhanced: \t" + enhancedFit + " \t MSE:" + formatter.format(f.fourierMSE(enhancedFit)) + " \t J0:" + formatter.format(f.fourierJ0(enhancedFit)) + " \t J45:" + formatter.format(f.fourierJ45(enhancedFit));
    }

    public AstigmaticLensParams closestToTheReal(AstigmaticLensParams p1, AstigmaticLensParams p2, AstigmaticLensParams real) {
        FourierDomainAnalysis fourier = new FourierDomainAnalysis();

        float p1Real = fourier.diff(p1, real);
        float p2Real = fourier.diff(p2, real);

        //System.out.println(p1Real + " , " + p2Real);

        return p1Real < p2Real ? p1 : p2;
    }

    public FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
    }
}
