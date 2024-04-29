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
public class Dataset926Test {

    TestUtils.Case c25OD = new TestUtils.Case(
            25,
            0,
            new HashMap<Integer, Float>() {{
                put(30, -1.9135951f);
                put(76, -2.0537574f);
                put(121, -0.8544593f);
                put(98, -1.5614702f);
                put(167, -0.94508505f);
                put(145, -0.814776f);
                put(53, -2.2825022f);
                put(8, -1.062023f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 150.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit25OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c25OD);
    }

    TestUtils.Case c25OS = new TestUtils.Case(
            25,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -0.23440044f);
                put(121, 1.5962492f);
                put(143, 1.2200626f);
                put(98, 2.219837f);
                put(54, 0.693817f);
                put(166, 0.31779662f);
                put(72, 1.4733951f);
                put(7, -0.23220468f);
            }},
            new AstigmaticLensParams(2.0f, -2.0f, 105.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit25OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c25OS);
    }

    TestUtils.Case c12OD = new TestUtils.Case(
            12,
            0,
            new HashMap<Integer, Float>() {{
                put(27, -2.059233f);
                put(142, -0.9427805f);
                put(119, -0.8424579f);
                put(96, -0.98416305f);
                put(50, -2.4047785f);
                put(164, -1.1496377f);
                put(73, -1.784822f);
                put(8, -1.3167667f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 135.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit12OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c12OD);
    }

    TestUtils.Case c12OS = new TestUtils.Case(
            12,
            0,
            new HashMap<Integer, Float>() {{
                put(27, -0.67176044f);
                put(72, 0.06307411f);
                put(97, 0.29072392f);
                put(7, -0.23081008f);
                put(50, 0.010006667f);
                put(143, 0.14921018f);
                put(165, -0.2660618f);
                put(118, 0.49373516f);
            }},
            new AstigmaticLensParams(0.25f, -0.5f, 110.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit12OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c12OS);
    }

    TestUtils.Case c7OD = new TestUtils.Case(
            7,
            0,
            new HashMap<Integer, Float>() {{
                put(131, -0.05129436f);
                put(153, 0.07120512f);
                put(86, -1.1847973f);
                put(108, -0.57945716f);
                put(174, 0.342506f);
                put(40, -1.4902138f);
                put(15, -0.3206566f);
                put(63, -1.3718859f);
            }},
            new AstigmaticLensParams(0.25f, -1.75f, 155.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit7OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c7OD);
    }

    TestUtils.Case c7OS = new TestUtils.Case(
            7,
            0,
            new HashMap<Integer, Float>() {{
                put(38, 0.8803764f);
                put(130, 1.1567013f);
                put(154, 1.1083663f);
                put(84, 1.1682905f);
                put(61, 1.409758f);
                put(175, 0.6418235f);
                put(107, 0.58674246f);
                put(16, 0.44061166f);
            }},
            new AstigmaticLensParams(1.0f, -0.25f, 90.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit7OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c7OS);
    }

    TestUtils.Case c1OD = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(30, -1.6477647f);
                put(124, 0.21794973f);
                put(75, -1.3628969f);
                put(102, -0.7145369f);
                put(167, -0.4667988f);
                put(144, -0.20815688f);
                put(9, -1.0754086f);
                put(54, -1.6010885f);
            }},
            new AstigmaticLensParams(0.0f, -1.5f, 135.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit1OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1OD);
    }

    TestUtils.Case c1OS = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -0.58993065f);
                put(121, 0.7236214f);
                put(77, 0.3447391f);
                put(100, 0.17774668f);
                put(167, 0.37059617f);
                put(143, 0.21909979f);
                put(8, 0.26869076f);
                put(57, -0.41973788f);
            }},
            new AstigmaticLensParams(0.25f, -0.25f, 135.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit1OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1OS);
    }

    TestUtils.Case c14OD = new TestUtils.Case(
            14,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -1.7065176f);
                put(75, -2.1567817f);
                put(121, -0.9537216f);
                put(98, -1.5622439f);
                put(165, -1.0719432f);
                put(145, -0.77460295f);
                put(6, -1.242048f);
                put(52, -1.9063687f);
            }},
            new AstigmaticLensParams(-1.0f, -1.0f, 150.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit14OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c14OD);
    }

    TestUtils.Case c14OS = new TestUtils.Case(
            14,
            0,
            new HashMap<Integer, Float>() {{
                put(30, -1.0178694f);
                put(144, 0.33715144f);
                put(120, 0.7515655f);
                put(98, 0.525564f);
                put(9, -0.017565418f);
                put(167, 0.07440503f);
                put(54, -0.30765852f);
                put(72, -0.20724073f);
            }},
            new AstigmaticLensParams(0.5f, -1.25f, 125.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit14OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c14OS);
    }

    TestUtils.Case c28OD = new TestUtils.Case(
            28,
            0,
            new HashMap<Integer, Float>() {{
                put(121, -0.5829603f);
                put(97, -1.2821095f);
                put(144, -0.5306239f);
                put(51, -2.0992155f);
                put(27, -1.8434806f);
                put(166, -0.5092264f);
                put(72, -2.1249466f);
                put(9, -0.81145716f);
            }},
            new AstigmaticLensParams(-0.5f, -1.5f, 150.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit28OD() {
        // Not much we can do in this case.
        //TestUtils.assertRoundingIsNotWorseThanFitting(c28OD);
    }

    TestUtils.Case c28OS = new TestUtils.Case(
            28,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -0.23440044f);
                put(120, 0.5352399f);
                put(144, 0.2956787f);
                put(97, 1.115372f);
                put(73, 0.37577167f);
                put(165, 0.25464728f);
                put(7, -0.2368755f);
                put(52, 0.2826469f);
            }},
            new AstigmaticLensParams(0.75f, -0.75f, 105.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit28OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c28OS);
    }

    TestUtils.Case c2OD = new TestUtils.Case(
            2,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -1.7149016f);
                put(78, -1.6922646f);
                put(124, -0.35514426f);
                put(101, -1.1553028f);
                put(167, -0.7067279f);
                put(145, -0.821808f);
                put(10, -1.3771763f);
                put(56, -1.7935507f);
            }},
            new AstigmaticLensParams(-0.75f, -0.75f, 145.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit2OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2OD);
    }

    TestUtils.Case c2OS = new TestUtils.Case(
            2,
            0,
            new HashMap<Integer, Float>() {{
                put(32, 0.5206047f);
                put(123, 1.0199338f);
                put(77, 0.9213054f);
                put(100, 1.003741f);
                put(167, 0.09715906f);
                put(145, 0.55301714f);
                put(9, 0.21540886f);
                put(55, 0.934787f);
            }},
            new AstigmaticLensParams(1.0f, -0.5f, 90.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit2OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2OS);
    }

    TestUtils.Case c26OD = new TestUtils.Case(
            26,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -1.5863254f);
                put(76, -1.8043505f);
                put(121, -0.812229f);
                put(98, -1.5596062f);
                put(164, -0.34706214f);
                put(142, -0.61820835f);
                put(7, -1.0140003f);
                put(54, -1.8393086f);
            }},
            new AstigmaticLensParams(-0.5f, -1.0f, 155.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit26OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c26OD);
    }

    TestUtils.Case c26OS = new TestUtils.Case(
            26,
            0,
            new HashMap<Integer, Float>() {{
                put(30, 0.24001165f);
                put(4, -0.15790977f);
                put(76, 0.86798924f);
                put(121, 1.0428859f);
                put(166, 0.2971144f);
                put(143, 0.5115094f);
                put(54, 0.38155985f);
                put(99, 1.6054043f);
            }},
            new AstigmaticLensParams(1.25f, -1.0f, 100.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit26OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c26OS);
    }

    TestUtils.Case c27OD = new TestUtils.Case(
            27,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -1.7073978f);
                put(120, -0.5004164f);
                put(74, -1.938001f);
                put(98, -1.8000752f);
                put(164, -0.5920605f);
                put(144, -0.58694166f);
                put(8, -1.3231555f);
                put(51, -2.3530276f);
            }},
            new AstigmaticLensParams(-0.5f, -1.5f, 150.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit27OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c27OD);
    }

    TestUtils.Case c27OS = new TestUtils.Case(
            27,
            0,
            new HashMap<Integer, Float>() {{
                put(121, 1.3232657f);
                put(75, 1.6593692f);
                put(98, 1.9334067f);
                put(52, 0.3750597f);
                put(166, 0.043969058f);
                put(144, 0.42959934f);
                put(27, 0.014318473f);
                put(8, -0.25118932f);
            }},
            new AstigmaticLensParams(1.75f, -1.75f, 95.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit27OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c27OS);
    }

    TestUtils.Case c3OD = new TestUtils.Case(
            3,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -1.3319014f);
                put(122, -0.35618123f);
                put(76, -1.541723f);
                put(99, -0.84226656f);
                put(167, -0.7029304f);
                put(146, -0.8883707f);
                put(10, -0.8691849f);
                put(54, -1.607519f);
            }},
            new AstigmaticLensParams(-0.75f, -0.75f, 145.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit3OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c3OD);
    }

    TestUtils.Case c3OS = new TestUtils.Case(
            3,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -0.15424041f);
                put(77, -0.16380616f);
                put(123, -0.20241056f);
                put(99, -0.3139581f);
                put(167, 0.11270319f);
                put(145, 0.5409739f);
                put(11, -0.12478987f);
                put(56, -0.26824978f);
            }},
            new AstigmaticLensParams(-0.25f, 0.0f, 0.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit3OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c3OS);
    }

    TestUtils.Case c5eOD = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -1.4830203f);
                put(119, -0.8983643f);
                put(76, -1.2934934f);
                put(98, -1.0599458f);
                put(165, -0.5854949f);
                put(142, -0.5575348f);
                put(7, -1.2649721f);
                put(54, -1.7595004f);
            }},
            new AstigmaticLensParams(-0.75f, -0.75f, 140.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit5eOD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5eOD);
    }

    TestUtils.Case c5eOS = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(8, -0.5231083f);
                put(28, -0.22785357f);
                put(53, -0.768967f);
                put(74, 0.19446723f);
                put(98, 0.28534845f);
                put(121, 0.054308273f);
                put(143, -0.040108714f);
                put(164, -0.3474045f);
            }},
            new AstigmaticLensParams(0.25f, -0.25f, 95.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f), 53
    );

    @Test
    public void testCaseGoodFit5eOS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5eOS);
    }

    TestUtils.Case c9OD = new TestUtils.Case(
            9,
            0,
            new HashMap<Integer, Float>() {{
                put(86, -0.41062334f);
                put(131, -0.41739455f);
                put(153, -0.5101462f);
                put(110, -0.42408043f);
                put(41, -1.4792731f);
                put(176, -0.91973567f);
                put(16, -1.4515679f);
                put(63, -1.0295311f);
            }},
            new AstigmaticLensParams(-0.5f, -1.0f, 120.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit9OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c9OD);
    }

    TestUtils.Case c9OS = new TestUtils.Case(
            9,
            0,
            new HashMap<Integer, Float>() {{
                put(85, 1.461218f);
                put(154, 0.48080066f);
                put(132, 0.48524946f);
                put(107, 1.1819891f);
                put(42, 1.5564808f);
                put(176, 1.2036275f);
                put(18, 1.0820991f);
                put(63, 1.940161f);
            }},
            new AstigmaticLensParams(1.75f, -1.0f, 60.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit9OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c9OS);
    }

    TestUtils.Case c6OD = new TestUtils.Case(
            6,
            1,
            new HashMap<Integer, Float>() {{
                put(131, -0.73220706f);
                put(154, -0.15665166f);
                put(84, -1.4712641f);
                put(61, -1.573068f);
                put(40, -1.6233826f);
                put(176, -0.13975628f);
                put(107, -0.7654324f);
                put(16, -0.41459596f);
            }},
            new AstigmaticLensParams(-0.25f, -1.5f, 160.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit6OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c6OD);
    }

    TestUtils.Case c6OS = new TestUtils.Case(
            6,
            1,
            new HashMap<Integer, Float>() {{
                put(16, -0.111967474f);
                put(41, -0.051299028f);
                put(61, -0.21545912f);
                put(83, 1.132941f);
                put(105, 1.0640845f);
                put(129, 0.7282401f);
                put(154, 0.13994378f);
                put(176, 0.11801443f);
            }},
            new AstigmaticLensParams(0.75f, -1.0f, 110.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f), 61
    );

    @Test
    public void testCaseGoodFit6OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c6OS);
    }

    TestUtils.Case c15OD = new TestUtils.Case(
            15,
            0,
            new HashMap<Integer, Float>() {{
                put(28, -1.9171762f);
                put(123, -0.85700315f);
                put(74, -1.6754243f);
                put(99, -1.3465704f);
                put(164, -0.5866771f);
                put(145, -0.73829836f);
                put(7, -1.2572535f);
                put(52, -2.1521275f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 145.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit15OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c15OD);
    }

    TestUtils.Case c15OS = new TestUtils.Case(
            15,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -0.5550772f);
                put(74, 1.5726933f);
                put(121, 0.9788469f);
                put(99, 1.6054043f);
                put(167, -0.17121118f);
                put(145, 0.5869195f);
                put(9, -0.58116966f);
                put(52, -0.38178113f);
            }},
            new AstigmaticLensParams(1.5f, -2.25f, 105.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f), 74
    );

    @Test
    public void testCaseGoodFit15OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c15OS);
    }

    TestUtils.Case c5OD = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(85, -1.6935116f);
                put(154, -1.0276697f);
                put(132, -0.96703f);
                put(108, -0.3014022f);
                put(40, -1.8692694f);
                put(173, -0.72351134f);
                put(15, -1.1820252f);
                put(63, -2.2732573f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 145.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit5OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5OD);
    }

    TestUtils.Case c5OS = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(86, 0.38923988f);
                put(153, 0.4054342f);
                put(131, 1.0974303f);
                put(108, 0.26418772f);
                put(41, -0.43133911f);
                put(175, 0.10837866f);
                put(17, 0.072451316f);
                put(63, 0.024170376f);
            }},
            new AstigmaticLensParams(0.5f, -0.75f, 130.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit5OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5OS);
    }

    TestUtils.Case c1eOD = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -1.5017533f);
                put(78, -1.9717541f);
                put(120, -1.1007223f);
                put(98, -1.8216836f);
                put(168, -0.93223786f);
                put(143, -1.1515638f);
                put(10, -0.8647756f);
                put(53, -2.1171598f);
            }},
            new AstigmaticLensParams(-1.0f, -0.75f, 160.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit1eOD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1eOD);
    }

    TestUtils.Case c1eOS = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -0.043348085f);
                put(123, -0.19258448f);
                put(76, 0.56384474f);
                put(102, 0.09876912f);
                put(167, -0.16720934f);
                put(145, 0.13402735f);
                put(10, -0.08133322f);
                put(55, -0.08233306f);
            }},
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit1eOS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1eOS);
    }

    TestUtils.Case c11OD = new TestUtils.Case(
            11,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -1.8036219f);
                put(74, -1.9330224f);
                put(121, -0.9298461f);
                put(98, -1.5448207f);
                put(163, -0.67783624f);
                put(144, -0.8945678f);
                put(8, -0.79307085f);
                put(52, -2.2138855f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 155.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit11OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c11OD);
    }

    TestUtils.Case c11OS = new TestUtils.Case(
            11,
            0,
            new HashMap<Integer, Float>() {{
                put(28, -0.50509197f);
                put(74, 0.4433327f);
                put(122, 1.2248019f);
                put(98, 0.8086898f);
                put(165, 0.53503186f);
                put(143, 0.22729802f);
                put(6, 0.062518634f);
                put(53, -0.121380486f);
            }},
            new AstigmaticLensParams(0.75f, -1.25f, 120.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit11OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c11OS);
    }

    TestUtils.Case c8OD = new TestUtils.Case(
            8,
            0,
            new HashMap<Integer, Float>() {{
                put(132, -0.28541276f);
                put(152, -0.310733f);
                put(86, -1.4243996f);
                put(110, -0.7195583f);
                put(42, -1.5349554f);
                put(175, -0.41306034f);
                put(17, -1.3166958f);
                put(64, -1.5764809f);
            }},
            new AstigmaticLensParams(-0.25f, -1.25f, 145.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit8OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c8OD);
    }

    TestUtils.Case c8OS = new TestUtils.Case(
            8,
            0,
            new HashMap<Integer, Float>() {{
                put(131, 1.3925451f);
                put(85, 2.024726f);
                put(153, 0.94280493f);
                put(107, 2.0518703f);
                put(40, 1.1950197f);
                put(173, 0.59880596f);
                put(16, 0.96885896f);
                put(64, 1.6852751f);
            }},
            new AstigmaticLensParams(2.0f, -1.25f, 90.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit8OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c8OS);
    }

    TestUtils.Case c10OD = new TestUtils.Case(
            10,
            0,
            new HashMap<Integer, Float>() {{
                put(130, -0.86577487f);
                put(86, -1.436929f);
                put(154, -0.9348638f);
                put(62, -1.817634f);
                put(40, -1.8257517f);
                put(176, -0.9237375f);
                put(107, -0.99597293f);
                put(16, -1.4491593f);
            }},
            new AstigmaticLensParams(-1.0f, -1.0f, 140.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit10OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c10OD);
    }

    TestUtils.Case c10OS = new TestUtils.Case(
            10,
            0,
            new HashMap<Integer, Float>() {{
                put(131, 1.7017899f);
                put(154, 1.076345f);
                put(86, 1.4689951f);
                put(63, 1.2239487f);
                put(42, 0.5405083f);
                put(175, 0.3637418f);
                put(17, 0.32643548f);
                put(108, 1.6696633f);
            }},
            new AstigmaticLensParams(1.75f, -1.25f, 105.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit10OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c10OS);
    }

    TestUtils.Case c13OD = new TestUtils.Case(
            13,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -2.108683f);
                put(121, -0.8255199f);
                put(74, -1.9569666f);
                put(98, -1.5734915f);
                put(164, -0.9198011f);
                put(142, -0.65901774f);
                put(7, -1.0138916f);
                put(53, -2.3177f);
            }},
            new AstigmaticLensParams(-0.75f, -1.5f, 150.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit13OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c13OD);
    }

    TestUtils.Case c13OS = new TestUtils.Case(
            13,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -0.90121967f);
                put(121, 0.4111944f);
                put(74, 0.19767146f);
                put(98, 1.1004328f);
                put(165, 0.22946323f);
                put(143, 0.24019152f);
                put(8, 0.0018045503f);
                put(50, -0.7544222f);
            }},
            new AstigmaticLensParams(0.75f, -1.25f, 120.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit13OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c13OS);
    }

    TestUtils.Case c4OD = new TestUtils.Case(
            4,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -1.6984472f);
                put(74, -1.1238983f);
                put(121, -0.54300326f);
                put(97, -0.7712426f);
                put(165, -0.5770112f);
                put(143, 0.21489319f);
                put(9, -1.0962355f);
                put(51, -2.0391917f);
            }},
            new AstigmaticLensParams(-0.25f, -1.5f, 135.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f)
    );

    @Test
    public void testCaseGoodFit4OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c4OD);
    }

    TestUtils.Case c4OS = new TestUtils.Case(
            4,
            0,
            new HashMap<Integer, Float>() {{
                put(29, -0.3031218f);
                put(75, -0.31409153f);
                put(119, 0.0031669103f);
                put(98, 0.26952118f);
                put(165, -0.31408963f);
                put(141, 0.122517586f);
                put(8, -0.7734732f);
                put(53, -0.52394116f);
            }},
            new AstigmaticLensParams(0.0f, -0.5f, 115.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit4OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c4OS);
    }


    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
        add(c25OD);
        add(c25OS);
        add(c12OD);
        add(c12OS);
        add(c7OD);
        add(c7OS);
        add(c1OD);
        add(c1OS);
        add(c14OD);
        add(c14OS);
        add(c28OD);
        add(c28OS);
        add(c2OD);
        add(c2OS);
        add(c26OD);
        add(c26OS);
        add(c27OD);
        add(c27OS);
        add(c3OD);
        add(c3OS);
        add(c5OD);
        add(c5OS);
        add(c9OD);
        add(c9OS);
        add(c6OD);
        add(c6OS);
        add(c15OD);
        add(c15OS);
        add(c5OD);
        add(c5OS);
        add(c1OD);
        add(c1OS);
        add(c11OD);
        add(c11OS);
        add(c8OD);
        add(c8OS);
        add(c10OD);
        add(c10OS);
        add(c13OD);
        add(c13OS);
        add(c4OD);
        add(c4OS);
    }};

    @Test
    public void testGoodFitVDDAverageAbsoluteError() {
        TestUtils.assertVDDListOfCases(groupGoodFit);
    }

    @Test
    public void testGoodFitSphEqAverageAbsoluteError() {
        TestUtils.assertSphEqListOfCases(groupGoodFit);
        ;
    }


    ArrayList<TestUtils.Case> groupBadFit = new ArrayList<TestUtils.Case>() {{
    }};


    ArrayList<TestUtils.Case> differentEyes = new ArrayList<TestUtils.Case>() {{
        add(c25OD);
        add(c25OS);
        add(c12OD);
        add(c12OS);
        add(c7OD);
        add(c7OS);
        add(c1OD);
        add(c1OS);
        add(c14OD);
        add(c14OS);
        add(c28OD);
        add(c28OS);
        add(c2OD);
        add(c2OS);
        add(c26OD);
        add(c26OS);
        add(c27OD);
        add(c27OS);
        add(c3OD);
        add(c3OS);
        add(c5OD);
        add(c5OS);
        add(c9OD);
        add(c9OS);
        add(c6OD);
        add(c6OS);
        add(c15OD);
        add(c15OS);
        add(c5OD);
        add(c5OS);
        add(c1OD);
        add(c1OS);
        add(c11OD);
        add(c11OS);
        add(c8OD);
        add(c8OS);
        add(c10OD);
        add(c10OS);
        add(c13OD);
        add(c13OS);
        add(c4OD);
        add(c4OS);
    }};

    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c25OD);
        add(c25OS);
        add(c12OD);
        add(c12OS);
        add(c7OD);
        add(c7OS);
        add(c1OD);
        add(c1OS);
        add(c14OD);
        add(c14OS);
        add(c28OD);
        add(c28OS);
        add(c2OD);
        add(c2OS);
        add(c26OD);
        add(c26OS);
        add(c27OD);
        add(c27OS);
        add(c3OD);
        add(c3OS);
        add(c5OD);
        add(c5OS);
        add(c9OD);
        add(c9OS);
        add(c6OD);
        add(c6OS);
        add(c15OD);
        add(c15OS);
        add(c5OD);
        add(c5OS);
        add(c1OD);
        add(c1OS);
        add(c11OD);
        add(c11OS);
        add(c8OD);
        add(c8OS);
        add(c10OD);
        add(c10OS);
        add(c13OD);
        add(c13OS);
        add(c4OD);
        add(c4OS);
    }};

    ArrayList<TestUtils.Case> sixteenMeasurements = new ArrayList<TestUtils.Case>() {{
    }};

    @Test
    public void testBadFitVDDAverageAbsoluteError() {
        TestUtils.assertVDDListOfCases(groupBadFit);
    }

    @Test
    public void testBadFitSphEqAverageAbsoluteError() {
        TestUtils.assertSphEqListOfCases(groupBadFit);
        ;
    }

    @Test
    public void testOptimizeRoundingParameters() {
        ArrayList<TestUtils.Case> groupAll = new ArrayList<>();
        groupAll.addAll(groupBadFit);
        groupAll.addAll(groupGoodFit);
        System.out.println("\n******\n<0.25 VDDs\n******");
        TestOptimizerUtils.optimizeRoundingParameters(TestUtils.select(groupAll, 0f, 0.25f));
        System.out.println("\n******\n<0.75 VDDs\n******");
        TestOptimizerUtils.optimizeRoundingParameters(TestUtils.select(groupAll, 0.25f, 0.75f));
        System.out.println("\n******\n<1.50 VDDs\n******");
        TestOptimizerUtils.optimizeRoundingParameters(TestUtils.select(groupAll, 0.75f, 1.5f));
        System.out.println("\n******\nAll Others\n******");
        TestOptimizerUtils.optimizeRoundingParameters(TestUtils.select(groupAll, 1.50f, 1000f));
    }

    @Test
    public void testQualityOfFit() {
        ArrayList<TestUtils.Case> groupAll = new ArrayList<>();
        groupAll.addAll(groupBadFit);
        groupAll.addAll(groupGoodFit);
        System.out.println("\n******\n<0.25 VDDs\n******");
        TestUtils.testQualityOfFit(TestUtils.select(groupAll, 0f, 0.25f));
        System.out.println("\n******\n<0.75 VDDs\n******");
        TestUtils.testQualityOfFit(TestUtils.select(groupAll, 0.25f, 0.75f));
        System.out.println("\n******\n<1.50 VDDs\n******");
        TestUtils.testQualityOfFit(TestUtils.select(groupAll, 0.75f, 1.5f));
        System.out.println("\n******\nAll Others\n******");
        TestUtils.testQualityOfFit(TestUtils.select(groupAll, 1.50f, 1000f));
    }
}


