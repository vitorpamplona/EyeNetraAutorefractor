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
import static org.junit.Assert.fail;

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.ComputedPrescription;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;


/**
 * NGVG016
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class SinusoidalFittingTest {

    @Test
    public void testGuessing() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-6.25f, -1.25f, 53f);

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

        AstigmaticLensParams basicGuess = new SinusoidalFitting().guessPrescription(map(myPoints).values());

        assertEquals(real.getSphere(), basicGuess.getSphere(), 1);
        assertEquals(real.getCylinder(), basicGuess.getCylinder(), 2.0);
        assertEquals(real.getAxis(), basicGuess.getAxis(), 10);
    }

    @Test
    public void testFitting() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-6.10f, -2.42f, 67f);

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

        AstigmaticLensParams fitted = new SinusoidalFitting().curveFitting(map(myPoints).values());

        assertEquals(real.getSphere(), fitted.getSphere(), 0.1);
        assertEquals(real.getCylinder(), fitted.getCylinder(), 0.1);
        assertEquals(real.getAxis(), fitted.getAxis(), 2);
    }

    @Test
    public void testRounding() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-6.00f, -2.50f, 65f);

        // -6.25 -1.25 53
        myPoints.put(0, -8.33285f);
        myPoints.put(10, -7.741766f);
        myPoints.put(30, -6.6901917f);
        myPoints.put(50, -6.4947286f);
        myPoints.put(60, -6.236386f);
        myPoints.put(90, -6.4539013f);
        myPoints.put(100, -6.7043343f);
        myPoints.put(120, -7.867673f);
        myPoints.put(140, -7.6672754f);
        myPoints.put(150, -8.999811f);

        FloatHashMap data = map(myPoints);

        AstigmaticLensParams fitted = new SinusoidalFitting().curveFitting(data.values());
        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), 0, fitted, null);
        AstigmaticLensParams rounded = new BestRounding().round25(enhancedFit, data.values(), 0, null);

        System.out.println("Fitted   " + fitted.toString());
        System.out.println("Enhanced " + enhancedFit.toString());
        System.out.println("Rounded  " + rounded.toString());

        assertEquals(real.getSphere(), rounded.getSphere(), 0.1);
        assertEquals(real.getCylinder(), rounded.getCylinder(), 0.1);
        assertEquals(real.getAxis(), rounded.getAxis(), 5);
    }

    @Test
    public void testTwoPoints() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        myPoints.put(0, -2.5f);
        myPoints.put(90, -1.5f);

        AstigmaticLensParams p = new SinusoidalFitting().curveFitting(map(myPoints).values());

        assertEquals(-1.5f, p.getSphere(), 0.001);
        assertEquals(-1.0f, p.getCylinder(), 0.001);
        assertEquals(90, p.getAxis(), 0.001);
    }

    @Test
    public void testThreePoints() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        myPoints.put(0, -2.8f);
        myPoints.put(90, -1.8f);
        myPoints.put(45, -2.3f);

        AstigmaticLensParams p = new SinusoidalFitting().curveFitting(map(myPoints).values());

        assertEquals(-1.8f, p.getSphere(), 0.001);
        assertEquals(-1.0f, p.getCylinder(), 0.001);
        assertEquals(90, p.getAxis(), 0.001);
    }

    public FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
    }

    @Test
    public void testFullSet() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        myPoints.put(0, -3.501369526f);
        myPoints.put(10, -3.507426068f);
        myPoints.put(20, -3.542740607f);
        myPoints.put(30, -3.603053687f);
        myPoints.put(40, -3.681090661f);
        myPoints.put(50, -3.767439118f);
        myPoints.put(60, -3.851684161f);
        myPoints.put(70, -3.923664593f);
        myPoints.put(80, -3.974698512f);
        myPoints.put(90, -3.998630474f);
        myPoints.put(100, -3.992573932f);
        myPoints.put(110, -3.957259393f);
        myPoints.put(120, -3.896946313f);
        myPoints.put(130, -3.818909339f);
        myPoints.put(140, -3.732560882f);
        myPoints.put(150, -3.648315839f);
        myPoints.put(160, -3.576335407f);
        myPoints.put(170, -3.525301488f);
        myPoints.put(180, -3.501369526f);

        AstigmaticLensParams p = new SinusoidalFitting().curveFitting(map(myPoints).values());

        assertEquals(-3.5f, p.getSphere(), 0.01);
        assertEquals(-0.5f, p.getCylinder(), 0.01);
        assertEquals(2, p.getAxis(), 1);
    }

    public AstigmaticLensParams fit(ComputedPrescription currentPrescription) {
        return new SinusoidalFitting().curveFitting(currentPrescription.testResults().values());
    }

    @Test
    public void testAllSamePowers() {
        float sph = 2;
        float cyl = -4;
        float axis = 25;

        AstigmaticLensParams real = new AstigmaticLensParams(sph, cyl, axis);

        ComputedPrescription prescription = new ComputedPrescription();

        prescription.add(new MeridianPower(0, 1.29f));
        prescription.add(new MeridianPower(90, -1.29f));
        prescription.add(new MeridianPower(140, -1.29f));
        prescription.add(new MeridianPower(50, 1.29f));

        prescription.setFitted(fit(prescription));

        // Better prescription
        assertPrescriptionEquals("All powers are the same value: \n" + diff(real, prescription.getFitted()) + "\n", real, prescription.getFitted());
    }

    @Test
    public void testWeirdCyl() {
        float sph = 2;
        float cyl = -3.75f;
        float axis = 39;

        AstigmaticLensParams real = new AstigmaticLensParams(sph, cyl, axis);

        ComputedPrescription prescription = new ComputedPrescription();

        prescription.add(new MeridianPower(0, 0.37f));
        prescription.add(new MeridianPower(90, -0.23f));
        prescription.add(new MeridianPower(140, -1.83f));
        prescription.add(new MeridianPower(50, 1.82f));

        prescription.setFitted(fit(prescription));

        // Better prescription
        assertPrescriptionEquals("All powers are the same value: \n" + diff(real, prescription.getFitted()) + "\n", real, prescription.getFitted());
    }

    public void assertPrescriptionEquals(String str, AstigmaticLensParams real, AstigmaticLensParams computed) {
        assertEquals(str + " - Spherical is not accurate", real.getSphere(), computed.getSphere(),
                0.2);

        if (Math.abs(computed.getCylinder()) < 0.6)
            assertEquals(str + " - Cylindrical must be 0", 0, computed.getCylinder(),
                    0.2);
        else if (Math.abs(real.getCylinder()) > 1)
            assertEquals(str + " - Cylindrical is not accurate", real.getCylinder(), computed.getCylinder(),
                    0.2);
        else
            assertEquals(str + " - Cylindrical is not accurate", real.getCylinder(), computed.getCylinder(),
                    0.2);

        if (Math.abs(real.getCylinder()) > 0.5 && Math.abs(computed.getCylinder()) > 0) {
            float diff = AngleDiff.diff180(real.getAxis(), computed.getAxis());
            if (diff > 10)
                fail(str + " - Axis is not accurate: " + real.getAxis() + " cmp to " + computed.getAxis() + " is "
                        + diff);
        }
    }

    protected String diff(AstigmaticLensParams real, AstigmaticLensParams computed) {
        return "Real: " + real.toString() + " eq " + real.sphEquivalent() + "\nCmp: " + computed.toString() + " eq "
                + computed.sphEquivalent();
    }

    //@Test
    public void testFittingWithRandomErrors() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-3.5f, -0.5f, 2f);
        for (int i = 0; i < 180; i += 10) {
            myPoints.put(i, real.interpolate(i) + ((float) Math.random() - 0.5f) / 2);
        }

        AstigmaticLensParams p = new SinusoidalFitting().curveFitting(map(myPoints).values());

        assertEquals(real.getSphere(), p.getSphere(), 0.2);
        assertEquals(real.getCylinder(), p.getCylinder(), 0.12 * 2);

        if (real.getAxis() < 45 && p.getAxis() > 135)
            assertEquals(real.getAxis(), p.getAxis() - 180, 8f);
        else if (real.getAxis() > 135 && p.getAxis() < 25)
            assertEquals(real.getAxis() - 180, p.getAxis(), 8f);
        else
            assertEquals(real.getAxis(), p.getAxis(), 11);
    }


    @Test
    public void testGuessedAstigmaticLensParams() {
        Map<Integer, Float> myPoints = new HashMap<Integer, Float>();

        AstigmaticLensParams real = new AstigmaticLensParams(-3.5f, -0.5f, 13f);
        for (int i = 0; i < 180; i += 10) {
            myPoints.put(i, real.interpolate(i) + ((float) Math.random() - 0.5f) / 2);
        }

        AstigmaticLensParams p = new SinusoidalFitting().guessPrescription(map(myPoints).values());

        assertEquals(real.getSphere(), p.getSphere(), 0.3);
        assertEquals(real.getCylinder(), p.getCylinder(), 0.3 * 2);
        assertTrue(AngleDiff.diff180(real.getAxis(), p.getAxis()) < 35f);
    }
}
