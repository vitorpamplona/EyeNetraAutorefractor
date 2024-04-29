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
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * NGVG017
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class QualityOfFitTest {

    public QualityOfFitTest() {
        // TODO Auto-generated constructor stub
    }

    @Test
    public void test() {
        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(183f, new MeridianPower(183f, 0.65f));
        data.put(25.5f, new MeridianPower(25f, 0.14f));
        data.put(48f, new MeridianPower(48f, 0.97f));
        data.put(70f, new MeridianPower(70f, 0.92f));
        data.put(93f, new MeridianPower(93f, 1.8f));
        data.put(115f, new MeridianPower(115f, 1.8f));
        data.put(138f, new MeridianPower(138f, 1.2f));
        data.put(160f, new MeridianPower(160f, -27f));

        SinusoidalFitting fit = new SinusoidalFitting();
        BestRounding rounding = new BestRounding();
        OutlierRemoval outliers = new OutlierRemoval();

        StringBuilder why = new StringBuilder();
        StringBuilder debug = new StringBuilder();

        AstigmaticLensParams params = fit.curveFitting(data.values());

        QualityOfFit r = new QualityOfFit();

        float outlier = r.compute(data.values(), 0, params);

        params = rounding.round25(outliers.run(data.values(), 0, params, debug), data.values(), 0, why);

        assertEquals(7.26f, outlier, 0.01);

        float noOutlier = r.computeRemovingKnownOutliers(data.values());


        assertEquals(0.37f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

    @Test
    public void testPerfect() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(183f, new MeridianPower(183f, params.interpolate(183)));
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25)));
        data.put(48f, new MeridianPower(48f, params.interpolate(48)));
        data.put(70f, new MeridianPower(70f, params.interpolate(70)));
        data.put(93f, new MeridianPower(93f, params.interpolate(93)));
        data.put(115f, new MeridianPower(115f, params.interpolate(115)));
        data.put(138f, new MeridianPower(138f, params.interpolate(138)));
        data.put(160f, new MeridianPower(160f, params.interpolate(160)));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());


        assertEquals(0.04f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

    @Test
    public void testOneOff() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25)));
        data.put(48f, new MeridianPower(48f, params.interpolate(48)));
        data.put(70f, new MeridianPower(70f, params.interpolate(70) + 5));
        data.put(93f, new MeridianPower(93f, params.interpolate(93)));
        data.put(115f, new MeridianPower(115f, params.interpolate(115)));
        data.put(138f, new MeridianPower(138f, params.interpolate(138)));
        data.put(160f, new MeridianPower(160f, params.interpolate(160)));
        data.put(183f, new MeridianPower(183f, params.interpolate(183)));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());

        assertTrue(data.getClosestTo(70).isOutlier());
        assertFalse(data.getClosestTo(25.5).isOutlier());
        assertFalse(data.getClosestTo(48).isOutlier());
        assertFalse(data.getClosestTo(93).isOutlier());
        assertFalse(data.getClosestTo(115).isOutlier());
        assertFalse(data.getClosestTo(138).isOutlier());
        assertFalse(data.getClosestTo(160).isOutlier());
        assertFalse(data.getClosestTo(183).isOutlier());

        assertEquals(0.05f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

    @Test
    public void testTwoOff() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25)));
        data.put(48f, new MeridianPower(48f, params.interpolate(48) + 6));
        data.put(70f, new MeridianPower(70f, params.interpolate(70) + 5));
        data.put(93f, new MeridianPower(93f, params.interpolate(93)));
        data.put(115f, new MeridianPower(115f, params.interpolate(115)));
        data.put(138f, new MeridianPower(138f, params.interpolate(138)));
        data.put(160f, new MeridianPower(160f, params.interpolate(160)));
        data.put(183f, new MeridianPower(183f, params.interpolate(183)));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());

        assertTrue(data.getClosestTo(70).isOutlier());
        assertTrue(data.getClosestTo(48).isOutlier());
        assertFalse(data.getClosestTo(25.5).isOutlier());
        assertFalse(data.getClosestTo(93).isOutlier());
        assertFalse(data.getClosestTo(115).isOutlier());
        assertFalse(data.getClosestTo(138).isOutlier());
        assertFalse(data.getClosestTo(160).isOutlier());
        assertFalse(data.getClosestTo(183).isOutlier());

        assertEquals(0.05f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }


    @Test
    public void test025Noise() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25) + 0.25f));
        data.put(48f, new MeridianPower(48f, params.interpolate(48) - 0.25f));
        data.put(70f, new MeridianPower(70f, params.interpolate(70) + 0.25f));
        data.put(93f, new MeridianPower(93f, params.interpolate(93) - 0.25f));
        data.put(115f, new MeridianPower(115f, params.interpolate(115) + 0.25f));
        data.put(138f, new MeridianPower(138f, params.interpolate(138) - 0.25f));
        data.put(160f, new MeridianPower(160f, params.interpolate(160) + 0.25f));
        data.put(183f, new MeridianPower(183f, params.interpolate(183) - 0.25f));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());


        assertEquals(0.5f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

    @Test
    public void test050Noise() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25) + 0.50f));
        data.put(48f, new MeridianPower(48f, params.interpolate(48) - 0.50f));
        data.put(70f, new MeridianPower(70f, params.interpolate(70) + 0.50f));
        data.put(93f, new MeridianPower(93f, params.interpolate(93) - 0.50f));
        data.put(115f, new MeridianPower(115f, params.interpolate(115) + 0.50f));
        data.put(138f, new MeridianPower(138f, params.interpolate(138) - 0.50f));
        data.put(160f, new MeridianPower(160f, params.interpolate(160) + 0.50f));
        data.put(183f, new MeridianPower(183f, params.interpolate(183) - 0.50f));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());

        assertFalse(data.getClosestTo(25).isOutlier());
        assertFalse(data.getClosestTo(48).isOutlier());
        assertFalse(data.getClosestTo(70).isOutlier());
        assertFalse(data.getClosestTo(93).isOutlier());
        assertFalse(data.getClosestTo(115).isOutlier());
        assertFalse(data.getClosestTo(138).isOutlier());
        assertFalse(data.getClosestTo(160).isOutlier());
        assertFalse(data.getClosestTo(183).isOutlier());

        assertEquals(1.00f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

    @Test
    public void test050NoiseWithOutlier() {
        QualityOfFit r = new QualityOfFit();

        AstigmaticLensParams params = new AstigmaticLensParams(-1.5f, -0.5f, 30f);

        FloatHashMap<MeridianPower> data = new FloatHashMap<MeridianPower>();
        data.put(25.5f, new MeridianPower(25f, params.interpolate(25) + 0.50f));
        data.put(48f, new MeridianPower(48f, params.interpolate(48) - 0.50f));
        data.put(70f, new MeridianPower(70f, params.interpolate(70) + 0.50f));
        data.put(93f, new MeridianPower(93f, params.interpolate(93) - 1.75f));
        data.put(115f, new MeridianPower(115f, params.interpolate(115) + 0.50f));
        data.put(138f, new MeridianPower(138f, params.interpolate(138) - 0.50f));
        data.put(160f, new MeridianPower(160f, params.interpolate(160) + 0.50f));
        data.put(183f, new MeridianPower(183f, params.interpolate(183) - 0.50f));

        float outlier = r.compute(data.values(), 0, params);
        float noOutlier = r.computeRemovingKnownOutliers(data.values());

        assertFalse(data.getClosestTo(25).isOutlier());
        assertFalse(data.getClosestTo(48).isOutlier());
        assertFalse(data.getClosestTo(70).isOutlier());
        assertTrue(data.getClosestTo(93).isOutlier());
        assertFalse(data.getClosestTo(115).isOutlier());
        assertFalse(data.getClosestTo(138).isOutlier());
        assertFalse(data.getClosestTo(160).isOutlier());
        assertFalse(data.getClosestTo(183).isOutlier());

        assertEquals(0.90f, noOutlier, 0.01);
        assertTrue(noOutlier <= outlier);
    }

}
