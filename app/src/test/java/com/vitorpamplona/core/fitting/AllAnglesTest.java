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
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class AllAnglesTest {

    public static class RawData {
        private double sph;
        private double cyl;
        private double axis;
        private double a10;
        private double a20;
        private double a30;
        private double a40;
        private double a50;
        private double a60;
        private double a70;
        private double a80;
        private double a90;
        private double a100;
        private double a110;
        private double a120;
        private double a130;
        private double a140;
        private double a150;
        private double a160;
        private double a170;
        private double a180;

        public RawData(double d, double e, double f, double g, double h, double i, double j, double k,
                       double l, double m, double n, double o, double p, double q, double r, double s, double t,
                       double u, double v, double w, double x) {
            super();
            this.sph = d;
            this.cyl = e;
            this.axis = f;
            this.a10 = g;
            this.a20 = h;
            this.a30 = i;
            this.a40 = j;
            this.a50 = k;
            this.a60 = l;
            this.a70 = m;
            this.a80 = n;
            this.a90 = o;
            this.a100 = p;
            this.a110 = q;
            this.a120 = r;
            this.a130 = s;
            this.a140 = t;
            this.a150 = u;
            this.a160 = v;
            this.a170 = w;
            this.a180 = x;
        }
    }

    public static List<RawData> data = new ArrayList<RawData>() {{
        add(new RawData(-5.50, -0.75, 44.00, -6.02, -5.56, -5.37, -5.47, -5.49, -5.75, -5.54, -5.44, -6.24, -5.73, -6.12, -6.05, -6.26, -6.19, -6.12, -5.98, -5.77, -5.92));
        add(new RawData(-3.25, -1.25, 72.00, -3.94, -3.79, -3.43, -4.03, -3.31, -3.18, -3.16, -3.46, -3.49, -3.48, -3.49, -4.49, -3.76, -4.05, -3.91, -4.24, -4.95, -4.70));
        add(new RawData(0.50, -0.75, 56.00, 0.69, 0.58, 0.44, 0.09, 0.70, 0.36, 0.09, 0.41, -0.01, 0.39, 0.06, 0.36, -0.22, -0.35, -0.27, -0.95, 0.34, -0.73));
        add(new RawData(0.25, -0.75, 83.00, 0.03, -0.95, 0.44, -0.53, 0.28, 0.36, 0.43, 0.25, -0.33, 0.22, 0.06, 0.36, -0.22, -0.14, -0.45, -0.28, -0.47, -1.50));
        add(new RawData(1.00, -3.75, 24.00, 0.53, 0.40, 0.44, 0.30, 0.70, 0.92, -0.42, -1.35, -2.47, -3.77, -3.49, -1.98, -2.23, -2.55, -1.18, -0.28, 0.50, 0.06));
        add(new RawData(0.75, -1.50, 43.00, 0.53, 0.40, 0.82, 0.73, 0.49, 0.73, 0.43, 0.58, 0.31, -0.74, -0.95, -0.74, -1.04, -0.35, 0.10, -0.61, -0.15, -0.57));
        add(new RawData(-1.75, -1.50, 9.00, -2.01, -1.92, -1.73, -1.74, -2.55, -2.84, -2.85, -3.17, -3.21, -3.19, -3.18, -3.18, -2.43, -2.55, -2.23, -2.24, -2.04, -2.40));
        add(new RawData(-10.00, -3.25, 175.00, -10.39, -10.30, -11.03, -12.20, -12.08, -12.71, -13.17, -13.21, -13.42, -12.59, -12.75, -12.22, -11.99, -11.37, -10.50, -10.18, -9.82, -9.65));
        add(new RawData(-5.25, -4.50, 169.00, -5.89, -6.12, -7.33, -8.16, -9.13, -9.10, -9.17, -9.41, -9.82, -9.31, -8.55, -8.54, -6.93, -6.53, -5.19, -5.84, -4.53, -5.51));
        add(new RawData(-3.25, 0.00, 0.00, -3.21, -3.18, -3.43, -2.91, -3.68, -3.18, -2.85, -3.75, -3.35, -2.89, -2.87, -3.51, -3.01, -2.93, -3.25, -2.71, -2.94, -3.57));
        add(new RawData(-0.75, -2.00, 33.00, -0.77, -0.95, -1.02, -0.94, 0.28, -1.46, -1.58, -1.96, -1.57, -2.29, -2.56, -3.51, -3.01, -2.55, -1.88, -1.60, -1.73, -1.50));
        add(new RawData(-1.25, -1.00, 8.00, -1.08, -0.95, -1.38, -1.74, -1.76, -1.81, -1.90, -1.96, -2.18, -2.29, -2.24, -2.16, -2.23, -1.76, -1.53, -0.95, -1.42, -1.50));
        add(new RawData(-2.25, -1.25, 52.00, -2.62, -2.56, -2.42, -2.71, -2.55, -2.16, -2.53, -2.27, -2.47, -2.89, -3.18, -3.18, -3.76, -3.68, -3.08, -3.03, -3.23, -2.99));
        add(new RawData(0.50, -3.00, 13.00, 0.53, 0.75, -0.30, -0.12, -0.56, -1.10, -1.58, -1.96, -2.77, -2.29, -2.40, -2.16, -1.84, -1.76, -0.82, -0.61, -0.15, 0.55));
        add(new RawData(-5.25, 0.00, 0.00, -5.48, -5.41, -5.52, -5.47, -5.49, -5.12, -5.54, -5.44, -5.03, -5.18, -4.98, -5.12, -5.56, -5.49, -5.50, -5.27, -5.23, -5.51));
        add(new RawData(0.25, 0.00, 0.00, -0.29, 0.06, 0.63, 0.30, 0.70, -0.01, 0.43, 0.58, -0.17, 0.55, 0.06, 0.73, 0.40, 0.28, 0.28, 0.06, 0.34, -0.10));
        add(new RawData(0.50, 0.00, 0.00, 0.03, 0.40, 0.82, 0.73, 0.28, 0.36, 0.43, 0.25, -0.01, 0.39, 0.40, 0.73, 0.61, 0.49, 0.28, 0.75, 0.83, 0.71));
        add(new RawData(0.25, -3.50, 170.00, 0.53, -1.60, -1.02, -1.74, -1.96, -3.18, -1.41, -3.75, -4.06, -3.19, -2.56, -2.16, 0.19, -0.96, 0.28, 0.06, 0.17, 0.06));
        add(new RawData(-0.25, 0.00, 0.00, -0.45, 0.06, -0.66, -0.12, -0.14, -0.74, -0.75, -0.88, 0.31, -0.10, 0.06, -0.01, -0.64, -0.96, -0.45, 0.40, -0.47, 0.38));
        add(new RawData(-1.75, -0.75, 8.00, -1.40, -1.60, -1.73, -1.74, -2.55, -2.16, -2.53, -2.57, -2.62, -2.29, -2.24, -3.18, -1.84, -1.96, -2.57, -1.60, -1.88, -2.70));
        add(new RawData(-9.25, -4.75, 178.00, -9.44, -9.69, -10.64, -11.64, -12.08, -12.95, -13.61, -13.83, -14.02, -13.43, -13.73, -13.19, -11.43, -10.79, -10.76, -9.94, -9.34, -9.30));
        add(new RawData(-5.00, -4.50, 172.00, -4.93, -6.26, -6.89, -7.18, -8.66, -8.96, -9.04, -9.05, -8.76, -9.19, -8.68, -8.82, -7.10, -6.53, -4.87, -4.69, -4.67, -5.24));
        add(new RawData(-2.00, -1.25, 79.00, -2.62, -2.87, -2.76, -2.52, -2.35, -2.16, -2.53, -2.12, -1.87, -2.59, -2.24, -2.50, -2.62, -2.93, -2.91, -3.49, -3.23, -3.28));
        add(new RawData(-1.50, -1.50, 160.00, -1.40, -1.92, -2.76, -2.52, -2.55, -2.84, -3.16, -2.87, -3.35, -2.29, -2.24, -2.16, -1.84, -1.37, -1.88, -1.27, -1.73, -1.81));
        add(new RawData(-1.75, 0.00, 0.00, -1.40, -1.27, -1.38, -1.74, -1.76, -1.46, -1.58, -1.66, -2.18, -1.68, -1.60, -2.16, -1.84, -2.16, -1.53, -1.60, -1.73, -2.11));
        add(new RawData(0.50, -3.00, 175.00, 0.03, 0.40, -0.66, -0.94, -1.37, -2.50, -2.22, -2.57, -2.47, -2.29, -1.92, -1.46, -1.45, -0.96, 0.28, 0.23, 0.50, 0.38));
        add(new RawData(0.25, -1.00, 18.00, -0.13, 0.40, 0.44, -0.12, 0.28, 0.36, -0.92, -1.03, -0.33, -0.42, -0.61, -0.38, -1.04, -0.96, -0.09, 0.06, 0.50, 0.06));
    }};

    @Test
    public void testAll() {
        for (RawData r : data) {
            testAllPowers(r);
        }
    }

    @Test
    public void test5Angles() {
        for (RawData r : data) {
            test5Angles(r);
        }
    }

    public void testAllPowers(RawData point) {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams((float) point.sph, (float) point.cyl, (float) point.axis);

        //System.out.println("Testing " + real.toString());

        myPoints.put(10, (float) point.a10);
        myPoints.put(20, (float) point.a20);
        myPoints.put(30, (float) point.a30);
        myPoints.put(40, (float) point.a40);
        myPoints.put(50, (float) point.a50);
        myPoints.put(60, (float) point.a60);
        myPoints.put(70, (float) point.a70);
        myPoints.put(80, (float) point.a80);
        myPoints.put(90, (float) point.a90);
        myPoints.put(100, (float) point.a100);
        myPoints.put(110, (float) point.a110);
        myPoints.put(120, (float) point.a120);
        myPoints.put(130, (float) point.a130);
        myPoints.put(140, (float) point.a140);
        myPoints.put(150, (float) point.a150);
        myPoints.put(160, (float) point.a160);
        myPoints.put(170, (float) point.a170);
        myPoints.put(180, (float) point.a180);

        FloatHashMap<MeridianPower> mapOfPoints = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(mapOfPoints.values());

        //System.out.println("Basic: " + basicFit);

        StringBuilder builder = new StringBuilder();

        //AstigmaticLensParams enhancedFit = new OutlierRemoval().run(mapOfPoints, basicFit, builder);

        //System.out.println("Reason: " + builder.toString());

        AstigmaticLensParams rounded = new BestRounding().roundNoAstCompensation(basicFit, mapOfPoints.values(), null);


        assertEquals(real.getSphere(), rounded.getSphere(), 0.26);
        assertEquals(real.getCylinder(), rounded.getCylinder(), 0.26);
        assertTrue(AngleDiff.diff180(real.getAxis(), rounded.getAxis()) <= 5);
    }

    public void test5Angles(RawData point) {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams((float) point.sph, (float) point.cyl, (float) point.axis);

        //System.out.println("Testing " + real.toString());

        // 180, 150, 110, 70, 30

        myPoints.put(180, (float) point.a180);
        myPoints.put(150, (float) point.a150);
        myPoints.put(110, (float) point.a110);
        myPoints.put(70, (float) point.a70);
        myPoints.put(30, (float) point.a30);

        FloatHashMap<MeridianPower> mapOfPoints = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(mapOfPoints.values());
        AstigmaticLensParams rounded = new BestRounding().roundNoAstCompensation(basicFit, mapOfPoints.values(), null);

        System.out.println();

        assertEquals(real.getSphere() + "\t" + real.getCylinder() + "\t" + real.getAxis(), real.getSphere(), rounded.getSphere(), 0.51);
        assertEquals(real.getSphere() + "\t" + real.getCylinder() + "\t" + real.getAxis(), real.getCylinder(), rounded.getCylinder(), 1.5);
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
