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


import static junit.framework.Assert.assertEquals;

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
public class SofteningCylTest {

    @Test
    public void testCaseWeird() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        myPoints.put(35, -7.12f);
        myPoints.put(125, -7.10f);
        myPoints.put(59, -7.11f);
        myPoints.put(82, -7.04f);
        myPoints.put(104, -6.79f);
        myPoints.put(170, -7.35f);
        myPoints.put(12, -7.10f);
        myPoints.put(148, -6.67f);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(myPoints).values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(map(myPoints).values(), 0, basicFit, builder);
        AstigmaticLensParams roundedFit = new BestRounding().round25(enhancedFit, map(myPoints).values(), 0, builder);

        AstigmaticLensParams softedCyls = new BestRounding().softenCylinder(enhancedFit, map(myPoints).values(), 0);

        assertEquals("Checking Sph", roundedFit.getSphere(), softedCyls.getSphere(), 0.25);
        assertEquals("Checking Cyl", roundedFit.getCylinder(), softedCyls.getCylinder(), 0.25);
        //assertEquals("Checking Axis", roundedFit.getAxis(), softedCyls.getAxis(), 5);

        System.out.println("Case Weird");
        System.out.println("Fitted " + enhancedFit.toString());
        System.out.println("Soft Cyls " + softedCyls.toString());
        System.out.println("Rounded " + roundedFit.toString());
    }

    @Test
    public void testCase6() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        // -6.25 -1.25 53
        myPoints.put(180, 3.7f);
        myPoints.put(30, 2.8f);
        myPoints.put(50, 2.6f);
        myPoints.put(70, 2.3f);
        myPoints.put(90, 1.9f);
        myPoints.put(120, 1.9f);
        myPoints.put(140, 1.2f);
        myPoints.put(160, 2.0f);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(myPoints).values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(map(myPoints).values(), 0, basicFit, builder);
        AstigmaticLensParams roundedFit = new BestRounding().round25(enhancedFit, map(myPoints).values(), 0, builder);

        AstigmaticLensParams softedCyls = new BestRounding().softenCylinder(enhancedFit, map(myPoints).values(), 0);

        //assertEquals("Checking Sph", roundedFit.getSphere(), softedCyls.getSphere(), 0.25);
        //assertEquals("Checking Cyl", roundedFit.getCylinder(), softedCyls.getCylinder(), 0.25);
        //assertEquals("Checking Axis", roundedFit.getAxis(), softedCyls.getAxis(), 5);

        System.out.println("Case 6");
        System.out.println("Fitted " + enhancedFit.toString());
        System.out.println("Soft Cyls " + softedCyls.toString());
        System.out.println("Rounded " + roundedFit.toString());
    }

    @Test
    public void testCase62() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        // -6.25 -1.25 53
        myPoints.put(6, -2.6f);
        myPoints.put(28, 1.1f);
        myPoints.put(51, -2.2f);
        myPoints.put(84, 4.5f);
        myPoints.put(96, 1.3f);
        myPoints.put(118, -2.4f);
        myPoints.put(129, -5.7f);
        myPoints.put(174, 1.7f);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(myPoints).values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(map(myPoints).values(), 0, basicFit, builder);
        AstigmaticLensParams roundedFit = new BestRounding().round25(enhancedFit, map(myPoints).values(), 0, builder);

        AstigmaticLensParams softedCyls = new BestRounding().softenCylinder(enhancedFit, map(myPoints).values(), 0);

        //assertEquals("Checking Sph", roundedFit.getSphere(), softedCyls.getSphere(), 0.25);
        //assertEquals("Checking Cyl", roundedFit.getCylinder(), softedCyls.getCylinder(), 0.25);
        //assertEquals("Checking Axis", roundedFit.getAxis(), softedCyls.getAxis(), 5);

        System.out.println("Case 62");
        System.out.println("Fitted " + enhancedFit.toString());
        System.out.println("Soft Cyls " + softedCyls.toString());
        System.out.println("Rounded " + roundedFit.toString());
    }

    @Test
    public void testCase3() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        // -6.25 -1.25 53
        myPoints.put(50, -6.4947286f);
        myPoints.put(100, -6.7043343f);
        myPoints.put(140, -7.6672754f);
        myPoints.put(10, -7.741766f);
        myPoints.put(120, -7.867673f);
        myPoints.put(30, -6.6901917f);
        myPoints.put(60, -6.236386f);
        myPoints.put(150, -8.999811f);
        myPoints.put(90, -6.4539013f);
        myPoints.put(0, -8.33285f);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(myPoints).values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(map(myPoints).values(), 0, basicFit, builder);
        AstigmaticLensParams roundedFit = new BestRounding().round25(enhancedFit, map(myPoints).values(), 0, builder);

        AstigmaticLensParams softedCyls = new BestRounding().softenCylinder(enhancedFit, map(myPoints).values(), 0);

        //assertEquals("Checking Sph", roundedFit.getSphere(), softedCyls.getSphere(), 0.25);
        //assertEquals("Checking Cyl", roundedFit.getCylinder(), softedCyls.getCylinder(), 0.25);
        //assertEquals("Checking Axis", roundedFit.getAxis(), softedCyls.getAxis(), 5);

        System.out.println("Case 3");
        System.out.println("Fitted " + enhancedFit.toString());
        System.out.println("Soft Cyls " + softedCyls.toString());
        System.out.println("Rounded " + roundedFit.toString());
    }

    public FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
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
}
