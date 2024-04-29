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

import com.vitorpamplona.core.models.AstigmaticLensParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class Dataset1371BlindTest {
    TestUtils.Case c564OD = new TestUtils.Case(
            564,
            0,
            new HashMap<Integer, Float>() {{
                put(124, 3.2453277f);
                put(55, -0.4119199f);
                put(169, 1.567677f);
                put(146, -0.3432748f);
                put(101, 1.226399f);
                put(10, 0.18613507f);
                put(31, -0.8681675f);
                put(78, 0.39578068f);
            }},
            new AstigmaticLensParams(0.75f, -0.25f, 135.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit564OD() {
        TestUtils.assertHorribleFit(c564OD);
    }

    TestUtils.Case c564OS = new TestUtils.Case(
            564,
            0,
            new HashMap<Integer, Float>() {{
                put(124, 0.8167966f);
                put(169, -0.89322084f);
                put(55, -2.5081925f);
                put(145, -1.3301718f);
                put(101, -3.6637495f);
                put(11, -1.1807712f);
                put(34, 0.9379645f);
                put(80, -3.334795f);
            }},
            new AstigmaticLensParams(-0.5f, -0.25f, 125.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit564OS() {
        TestUtils.assertHorribleFit(c564OS);
    }

    TestUtils.Case c561OD = new TestUtils.Case(
            561,
            4,
            new HashMap<Integer, Float>() {{
                put(101, -1.2026167f);
                put(169, -2.9044244f);
                put(146, -3.4721746f);
                put(56, -4.968059f);
                put(33, -4.0979395f);
                put(11, -4.1256948f);
                put(79, -4.5950103f);
            }},
            new AstigmaticLensParams(-2.75f, -1.0f, 130.0f),
            new AstigmaticLensParams(-0.5f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit561OD() {
        TestUtils.assertHorribleFit(c561OD);
    }

    TestUtils.Case c561OS = new TestUtils.Case(
            561,
            4,
            new HashMap<Integer, Float>() {{
                put(123, -4.0549064f);
                put(168, -1.4275738f);
                put(57, -2.4440145f);
                put(145, 0.21540794f);
                put(100, -4.343308f);
                put(11, -3.8901203f);
                put(34, -3.1517248f);
                put(80, 1.5898546f);
            }},
            new AstigmaticLensParams(1.25f, -3.25f, 110.0f),
            new AstigmaticLensParams(-0.5f, -0.5f, 90.0f)
    );

    @Test
    public void testCaseBadFit561OS() {
        TestUtils.assertHorribleFit(c561OS);
    }

    TestUtils.Case c568OD = new TestUtils.Case(
            568,
            2,
            new HashMap<Integer, Float>() {{
                put(122, -2.298083f);
                put(168, -2.19864f);
                put(58, -3.7976108f);
                put(147, 4.8819127f);
                put(100, -4.1162887f);
                put(10, -3.3847253f);
                put(34, -2.518415f);
                put(81, -4.236153f);
            }},
            new AstigmaticLensParams(-2.5f, -1.0f, 165.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit568OD() {
        TestUtils.assertHorribleFit(c568OD);
    }

    TestUtils.Case c568OS = new TestUtils.Case(
            568,
            2,
            new HashMap<Integer, Float>() {{
                put(125, -3.1347723f);
                put(56, -4.0434704f);
                put(168, -0.6414395f);
                put(146, 0.02913626f);
                put(102, -3.734244f);
                put(10, -1.6457646f);
                put(31, -2.615368f);
                put(79, -3.862096f);
            }},
            new AstigmaticLensParams(-1.5f, -2.75f, 170.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit568OS() {
        TestUtils.assertHorribleFit(c568OS);
    }

    TestUtils.Case c562OS = new TestUtils.Case(
            562,
            0,
            new HashMap<Integer, Float>() {{
                put(124, -1.8809502f);
                put(55, -3.0925856f);
                put(146, -0.26519457f);
                put(167, -3.2767859f);
                put(102, -3.9508786f);
                put(10, -3.6310778f);
                put(30, -3.1081905f);
                put(76, 0.8660319f);
            }},
            new AstigmaticLensParams(-3.0f, -0.25f, 135.0f),
            new AstigmaticLensParams(-0.25f, -0.25f, 25.0f)
    );

    @Test
    public void testCaseBadFit562OS() {
        TestUtils.assertBadFit(c562OS);
    }

    TestUtils.Case c570OD = new TestUtils.Case(
            570,
            6,
            new HashMap<Integer, Float>() {{
                put(122, -11.059198f);
                put(169, -8.880096f);
                put(55, -13.775531f);
                put(146, -10.167635f);
                put(101, -13.093078f);
                put(12, -9.982439f);
                put(32, -12.036959f);
                put(78, -14.136898f);
            }},
            new AstigmaticLensParams(-10.25f, -2.5f, 165.0f),
            new AstigmaticLensParams(-2.75f, -0.5f, 15.0f)
    );

    @Test
    public void testCaseBadFit570OD() {
        TestUtils.assertHorribleFit(c570OD);
    }

    TestUtils.Case c570OS = new TestUtils.Case(
            570,
            6,
            new HashMap<Integer, Float>() {{
                put(122, -12.103976f);
                put(170, -6.7542176f);
                put(58, -12.455171f);
                put(147, -9.582639f);
                put(100, -13.351628f);
                put(11, -9.929472f);
                put(32, -10.70376f);
                put(79, -12.646006f);
            }},
            new AstigmaticLensParams(-10.0f, -3.0f, 0.0f),
            new AstigmaticLensParams(-2.75f, -0.5f, 10.0f)
    );

    @Test
    public void testCaseBadFit570OS() {
        TestUtils.assertHorribleFit(c570OS);
    }

    TestUtils.Case c567OD = new TestUtils.Case(
            567,
            12,
            new HashMap<Integer, Float>() {{
                put(121, -4.0614595f);
                put(54, 6.0137386f);
                put(166, -4.254961f);
                put(144, -3.4226036f);
                put(99, -1.5806055f);
                put(9, -4.264607f);
                put(32, 6.7602596f);
                put(77, 5.6669393f);
            }},
            new AstigmaticLensParams(1.75f, -4.75f, 60.0f),
            new AstigmaticLensParams(-1.5f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit567OD() {
        TestUtils.assertHorribleFit(c567OD);
    }

    TestUtils.Case c567OS = new TestUtils.Case(
            567,
            12,
            new HashMap<Integer, Float>() {{
                put(122, -3.0966988f);
                put(54, -2.8015506f);
                put(166, -4.5078063f);
                put(144, -4.5829053f);
                put(99, 6.743669f);
                put(8, -4.471345f);
                put(33, -3.2067099f);
                put(76, -2.7932305f);
            }},
            new AstigmaticLensParams(-3.0f, -0.75f, 75.0f),
            new AstigmaticLensParams(-1.5f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit567OS() {
        TestUtils.assertHorribleFit(c567OS);
    }


    TestUtils.Case c556OD = new TestUtils.Case(
            556,
            1,
            new HashMap<Integer, Float>() {{
                put(124, 7.384299f);
                put(56, -11.401766f);
                put(167, 3.5201724f);
                put(145, 0.8400789f);
                put(102, 2.9367237f);
                put(10, 3.314413f);
                put(33, 1.2882265f);
                put(80, -0.30693135f);
            }},
            new AstigmaticLensParams(3.75f, -1.0f, 120.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit556OD() {
        TestUtils.assertHorribleFit(c556OD);
    }

    TestUtils.Case c556OS = new TestUtils.Case(
            556,
            1,
            new HashMap<Integer, Float>() {{
                put(124, -10.937904f);
                put(56, -11.812747f);
                put(169, 6.701156f);
                put(146, -0.8835644f);
                put(102, -12.938433f);
                put(10, 7.7046256f);
                put(31, -9.351597f);
                put(78, -15.115151f);
            }},
            new AstigmaticLensParams(-4.75f, -7.0f, 170.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit556OS() {
        TestUtils.assertHorribleFit(c556OS);
    }

    TestUtils.Case c598OD = new TestUtils.Case(
            598,
            0,
            new HashMap<Integer, Float>() {{
                put(31, -4.59732f);
                put(121, -5.133998f);
                put(76, -4.487442f);
                put(100, -5.272089f);
                put(168, -6.8997254f);
                put(144, -0.8527498f);
                put(9, -4.7525764f);
                put(54, -4.217786f);
            }},
            new AstigmaticLensParams(-4.25f, -1.25f, 50.0f),
            new AstigmaticLensParams(0.0f, -0.25f, 135.0f)
    );

    @Test
    public void testCaseBadFit598OD() {
        TestUtils.assertBadFit(c598OD);
    }

    TestUtils.Case c598OS = new TestUtils.Case(
            598,
            0,
            new HashMap<Integer, Float>() {{
                put(31, -3.9575224f);
                put(77, -5.146991f);
                put(122, -3.1254072f);
                put(100, -0.05837635f);
                put(167, -4.443164f);
                put(144, -5.410071f);
                put(8, -4.015881f);
                put(54, -3.131707f);
            }},
            new AstigmaticLensParams(-4.0f, -0.25f, 55.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit598OS() {
        TestUtils.assertHorribleFit(c598OS);
    }

}


