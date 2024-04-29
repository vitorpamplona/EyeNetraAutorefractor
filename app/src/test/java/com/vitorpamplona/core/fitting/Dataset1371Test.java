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

import com.vitorpamplona.core.fitting.TestUtils;
import com.vitorpamplona.core.models.AstigmaticLensParams;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = 21)
public class Dataset1371Test {
    TestUtils.Case c1006OD = new TestUtils.Case(
            1006,
            0,
            new HashMap<Integer, Float>() {{
                put(125, -1.1343689f);
                put(83, -0.19550602f);
                put(169, -0.07509197f);
                put(147, -0.18213063f);
                put(12, -0.14853966f);
                put(57, 0.12674542f);
                put(103, -0.5013318f);
                put(34, -0.4613959f);
            }},
            new AstigmaticLensParams(-0.25f, -0.5f, 30.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseGoodFit1006OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1006OD);
    }

    TestUtils.Case c1006OS = new TestUtils.Case(
            1006,
            0,
            new HashMap<Integer, Float>() {{
                put(126, -0.6802335f);
                put(57, -0.42738694f);
                put(167, -0.41822457f);
                put(146, -0.5007295f);
                put(102, -0.99493057f);
                put(12, -0.7065449f);
                put(34, -0.9725749f);
                put(81, -0.51677465f);
            }},
            new AstigmaticLensParams(-0.75f, 0.0f, 0.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseGoodFit1006OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1006OS);
    }


    TestUtils.Case c1007OD = new TestUtils.Case(
            1007,
            1,
            new HashMap<Integer, Float>() {{
                put(123, 1.4042268f);
                put(85, 0.9176471f);
                put(166, 0.606688f);
                put(144, 1.0092691f);
                put(11, 0.42732924f);
                put(57, 0.76406115f);
                put(102, -0.1666865f);
                put(34, 0.59959596f);
            }},
            new AstigmaticLensParams(1.25f, -0.75f, 110.0f),
            new AstigmaticLensParams(1.25f, 0.0f, 0.0f), 100
    );

    @Test
    public void testCaseGoodFit1007OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1007OD);
    }

    TestUtils.Case c1007OS = new TestUtils.Case(
            1007,
            1,
            new HashMap<Integer, Float>() {{
                put(123, 1.340419f);
                put(57, 0.8604131f);
                put(167, 1.7848994f);
                put(144, 2.02704f);
                put(101, 1.2415303f);
                put(10, 1.5862193f);
                put(34, 0.8143221f);
                put(79, 1.5870005f);
            }},
            new AstigmaticLensParams(1.75f, -0.75f, 150.0f),
            new AstigmaticLensParams(1.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseGoodFit1007OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1007OS);
    }


    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
        add(c1006OD);
        add(c1006OS);
        add(c1007OD);
        add(c1007OS);
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

    TestUtils.Case c528OD = new TestUtils.Case(
            528,
            2,
            new HashMap<Integer, Float>() {{
                put(123, 0.96545166f);
                put(55, 1.5307313f);
                put(167, 0.9345044f);
                put(145, 1.2256476f);
                put(100, 1.5729254f);
                put(9, 1.5900152f);
                put(33, 1.3656777f);
                put(78, 1.7867349f);
            }},
            new AstigmaticLensParams(1.5f, -0.25f, 75.0f),
            new AstigmaticLensParams(1.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit528OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c528OD);
    }

    TestUtils.Case c528OS = new TestUtils.Case(
            528,
            2,
            new HashMap<Integer, Float>() {{
                put(123, 0.75798583f);
                put(167, 0.3657022f);
                put(57, 1.4767963f);
                put(144, 1.1083497f);
                put(102, 1.470167f);
                put(11, 0.42818162f);
                put(34, 1.9115071f);
                put(79, 2.1169367f);
            }},
            new AstigmaticLensParams(1.75f, -1.25f, 70.0f),
            new AstigmaticLensParams(1.0f, 0.0f, 0.0f), 34
    );

    @Test
    public void testCaseBadFit528OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c528OS);
    }

    TestUtils.Case c2303OD = new TestUtils.Case(
            2303,
            3,
            new HashMap<Integer, Float>() {{
                put(33, 0.055610973f);
                put(125, 0.74688345f);
                put(79, 0.239004f);
                put(102, -0.096927024f);
                put(170, -0.23998898f);
                put(148, 0.76714903f);
                put(12, -0.37967375f);
                put(56, 0.35180786f);
            }},
            new AstigmaticLensParams(0.25f, -0.25f, 120.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f), 148, 125
    );

    @Test
    public void testCaseBadFit2303OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2303OD);
    }

    TestUtils.Case c2318OD = new TestUtils.Case(
            2318,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -2.6174037f);
                put(124, -3.639564f);
                put(80, -2.0624256f);
                put(102, -3.4015667f);
                put(170, -3.0485039f);
                put(147, -3.5635803f);
                put(13, -1.7260504f);
                put(57, -3.0856988f);
            }},
            new AstigmaticLensParams(-2.5f, -1.25f, 35.0f),
            new AstigmaticLensParams(-2.0f, 0.0f, 0.0f), 80
    );

    @Test
    public void testCaseBadFit2318OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2318OD);
    }

    TestUtils.Case c2318OS = new TestUtils.Case(
            2318,
            0,
            new HashMap<Integer, Float>() {{
                put(34, -0.044334117f);
                put(80, 7.4191065f);
                put(124, -3.4349694f);
                put(102, -3.8776917f);
                put(170, -1.0247507f);
                put(147, -2.0881605f);
                put(13, -0.43269482f);
                put(57, 7.60669f);
            }},
            new AstigmaticLensParams(1.75f, -3.25f, 60.0f),
            new AstigmaticLensParams(-2.0f, 0.0f, 0.0f), 58, 80
    );

    @Test
    public void testCaseBadFit2318OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2318OS);
    }


    TestUtils.Case c2298OD = new TestUtils.Case(
            2298,
            2,
            new HashMap<Integer, Float>() {{
                put(34, 0.35180688f);
                put(80, -0.77689826f);
                put(126, -0.31313378f);
                put(104, -0.463062f);
                put(171, 0.58663493f);
                put(149, 0.2063386f);
                put(13, 0.6766894f);
                put(57, -0.62071913f);
            }},
            new AstigmaticLensParams(0.5f, -1.0f, 0.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit2298OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2298OD);
    }

    TestUtils.Case c2298OS = new TestUtils.Case(
            2298,
            2,
            new HashMap<Integer, Float>() {{
                put(35, 0.8146755f);
                put(81, 0.05491232f);
                put(125, -0.56702894f);
                put(103, -0.9362672f);
                put(170, 1.1109686f);
                put(148, -0.28401205f);
                put(12, 0.99411f);
                put(56, 0.8111773f);
            }},
            new AstigmaticLensParams(1.0f, -1.5f, 30.0f),
            new AstigmaticLensParams(-0.25f, 0.0f, 0.0f), 170, 103
    );

    @Test
    public void testCaseBadFit2298OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2298OS);
    }

    TestUtils.Case c2311OD = new TestUtils.Case(
            2311,
            0,
            new HashMap<Integer, Float>() {{
                put(34, 0.35180688f);
                put(123, 1.0854809f);
                put(79, -1.5814333f);
                put(102, 1.7971323f);
                put(168, 0.18351576f);
                put(148, 1.3073803f);
                put(57, -0.42046723f);
                put(10, -0.2720494f);
            }},
            new AstigmaticLensParams(1.0f, -1.25f, 135.0f),
            new AstigmaticLensParams(-0.25f, 0.0f, 0.0f), 79
    );

    @Test
    public void testCaseBadFit2311OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2311OD);
    }

    TestUtils.Case c2311OS = new TestUtils.Case(
            2311,
            0,
            new HashMap<Integer, Float>() {{
                put(35, -1.4094493f);
                put(78, -1.3710301f);
                put(123, -0.51502466f);
                put(102, -0.88022226f);
                put(169, -0.5636262f);
                put(145, -0.920093f);
                put(55, -1.6800961f);
                put(13, -0.44500563f);
            }},
            new AstigmaticLensParams(-0.5f, -0.75f, 155.0f),
            new AstigmaticLensParams(-0.25f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit2311OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2311OS);
    }

    TestUtils.Case c2317OD = new TestUtils.Case(
            2317,
            1,
            new HashMap<Integer, Float>() {{
                put(33, -3.7024093f);
                put(78, -2.8773198f);
                put(125, 5.40069f);
                put(101, -2.1533415f);
                put(169, -3.2907243f);
                put(147, -1.8725336f);
                put(10, -3.3330967f);
                put(57, -3.0046322f);
            }},
            new AstigmaticLensParams(-2.0f, -1.75f, 120.0f),
            new AstigmaticLensParams(-3.5f, 0.0f, 0.0f), 125
    );

    @Test
    public void testCaseBadFit2317OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2317OD);
    }

    TestUtils.Case c2317OS = new TestUtils.Case(
            2317,
            1,
            new HashMap<Integer, Float>() {{
                put(32, -3.225577f);
                put(124, -3.5757897f);
                put(78, -3.8197303f);
                put(101, -4.329671f);
                put(168, 9.75694f);
                put(146, -3.1442027f);
                put(11, -2.903487f);
                put(55, -3.9422946f);
            }},
            new AstigmaticLensParams(-3.0f, -1.25f, 0.0f),
            new AstigmaticLensParams(-3.5f, 0.0f, 0.0f), 168
    );

    @Test
    public void testCaseBadFit2317OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2317OS);
    }


    TestUtils.Case c2309OD = new TestUtils.Case(
            2309,
            6,
            new HashMap<Integer, Float>() {{
                put(35, -1.4146646f);
                put(81, 0.04403459f);
                put(125, -0.8236593f);
                put(103, -0.15639387f);
                put(169, -1.3383267f);
                put(147, -1.6502378f);
                put(13, -1.7109709f);
                put(58, 0.6472279f);
            }},
            new AstigmaticLensParams(-0.25f, -1.25f, 80.0f),
            new AstigmaticLensParams(-0.25f, 0.0f, 0.0f), 58
    );

    @Test
    public void testCaseBadFit2309OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2309OD);
    }

    TestUtils.Case c2309OS = new TestUtils.Case(
            2309,
            6,
            new HashMap<Integer, Float>() {{
                put(34, -0.39583492f);
                put(126, -1.5566413f);
                put(80, -0.49630204f);
                put(103, -0.69438434f);
                put(170, -1.0279714f);
                put(147, -2.1207669f);
                put(11, -0.03879663f);
                put(58, 0.06396685f);
            }},
            new AstigmaticLensParams(0.0f, -1.25f, 50.0f),
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f)
    );

    @Test
    public void testCaseBadFit2309OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2309OS);
    }


    ArrayList<TestUtils.Case> groupBadFit = new ArrayList<TestUtils.Case>() {{
        add(c528OD);
        add(c528OS);
        add(c2303OD);
        //add(c2303OS);
        add(c2318OD);
        add(c2318OS);
        add(c2298OD);
        add(c2298OS);
        add(c2311OD);
        add(c2311OS);
        add(c2317OD);
        add(c2317OS);
        add(c2309OD);
        add(c2309OS);
    }};


    ArrayList<TestUtils.Case> differentEyes = new ArrayList<TestUtils.Case>() {{
    }};

    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
        add(c528OD);
        add(c528OS);
        add(c2303OD);
        //add(c2303OS);
        add(c2318OD);
        add(c2318OS);
        add(c1006OD);
        add(c1006OS);
        add(c2298OD);
        add(c2298OS);
        add(c2311OD);
        add(c2311OS);
        add(c2317OD);
        add(c2317OS);
        add(c1007OD);
        add(c1007OS);
        add(c2309OD);
        add(c2309OS);
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c528OD);
        add(c528OS);
        add(c2303OD);
        //add(c2303OS);
        add(c2318OD);
        add(c2318OS);
        add(c1006OD);
        add(c1006OS);
        add(c2298OD);
        add(c2298OS);
        add(c2311OD);
        add(c2311OS);
        add(c2317OD);
        add(c2317OS);
        add(c1007OD);
        add(c1007OS);
        add(c2309OD);
        add(c2309OS);
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

}


