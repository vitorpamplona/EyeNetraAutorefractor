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
public class Dataset1531VitorTest {

    TestUtils.Case c6OD = new TestUtils.Case(
            6,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -0.8822179f);
                put(123, -0.31430915f);
                put(78, -1.1906114f);
                put(100, -0.58780515f);
                put(170, -0.8448952f);
                put(145, -0.99700814f);
                put(56, -1.5257018f);
                put(9, -1.5806055f);
            }},
            new AstigmaticLensParams(-0.75f, -0.75f, 120.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 145.0f), 33, 170
    );

    @Test
    public void testCaseGoodFit6OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c6OD);
    }

    TestUtils.Case c6OS = new TestUtils.Case(
            6,
            0,
            new HashMap<Integer, Float>() {{
                put(34, -0.7451866f);
                put(123, 0.045002647f);
                put(79, -0.35827723f);
                put(103, 0.018444676f);
                put(168, 0.14557676f);
                put(146, -0.0012004861f);
                put(55, -0.5062494f);
                put(10, 0.21245606f);
            }},
            new AstigmaticLensParams(0.0f, -0.5f, 145.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit6OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c6OS);
    }

    TestUtils.Case c5OD = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(33, -1.5595965f);
                put(78, -0.94248796f);
                put(123, -0.2606966f);
                put(101, -0.9127002f);
                put(167, -0.72938466f);
                put(145, -0.18765269f);
                put(8, -1.2883396f);
                put(54, -1.9244465f);
            }},
            new AstigmaticLensParams(-0.25f, -1.25f, 135.0f),
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
                put(32, -0.12673195f);
                put(78, -0.1402153f);
                put(126, -0.038644407f);
                put(101, 0.14349492f);
                put(170, -0.035111308f);
                put(145, -0.058624614f);
                put(12, 0.09876818f);
                put(56, 0.09543843f);
            }},
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit5OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5OS);
    }

    TestUtils.Case c2OD = new TestUtils.Case(
            2,
            0,
            new HashMap<Integer, Float>() {{
                put(30, -1.9865025f);
                put(123, -1.2661905f);
                put(77, -2.0016313f);
                put(100, -1.6506983f);
                put(168, -0.9162654f);
                put(146, -0.835633f);
                put(55, -2.0544987f);
                put(10, -0.8635887f);
            }},
            new AstigmaticLensParams(-1.0f, -1.25f, 155.0f),
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
                put(10, 0.20096724f);
                put(31, 0.34805885f);
                put(54, 1.0553577f);
                put(76, 1.1648214f);
                put(99, 1.0667126f);
                put(121, 1.0351249f);
                put(145, 0.583008f);
                put(170, 0.23030582f);
            }},
            new AstigmaticLensParams(1.0f, -0.75f, 90.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit2OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2OS);
    }

    TestUtils.Case c1OD = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -0.72738427f);
                put(78, -1.9730821f);
                put(122, -0.77126074f);
                put(100, -1.629548f);
                put(168, -0.4141934f);
                put(145, -0.73829836f);
                put(10, -0.35770467f);
                put(56, -1.863348f);
            }},
            new AstigmaticLensParams(-0.25f, -1.5f, 170.0f),
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
                put(32, -0.7728785f);
                put(77, 1.1741642f);
                put(122, 1.4806013f);
                put(100, 1.2696718f);
                put(169, 0.19320802f);
                put(145, 0.4754183f);
                put(9, -0.05208786f);
                put(54, -0.6766086f);
            }},
            new AstigmaticLensParams(1.25f, -2.0f, 115.0f),
            new AstigmaticLensParams(1.25f, -1.25f, 105.0f), 32, 54 // best we can do.
    );

    @Test
    public void testCaseGoodFit1OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1OS);
    }

    TestUtils.Case c3OD = new TestUtils.Case(
            3,
            0,
            new HashMap<Integer, Float>() {{
                put(31, -2.065446f);
                put(79, -1.9218434f);
                put(123, -0.60371995f);
                put(101, -1.4519533f);
                put(167, -0.96881694f);
                put(146, -0.2278118f);
                put(10, -1.1138257f);
                put(56, -1.5322542f);
            }},
            new AstigmaticLensParams(-0.5f, -1.5f, 145.0f),
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
                put(30, -0.06530194f);
                put(122, 1.1553533f);
                put(77, 0.8967887f);
                put(100, 1.2696718f);
                put(167, 0.34363952f);
                put(145, 0.5409739f);
                put(9, -0.042607598f);
                put(54, 0.39973935f);
            }},
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f),
            new AstigmaticLensParams(1.25f, -1.25f, 105.0f) // best we can do
    );

    @Test
    public void testCaseGoodFit3OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c3OS);
    }

    TestUtils.Case c4OD = new TestUtils.Case(
            4,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -1.6744593f);
                put(122, -0.16850148f);
                put(78, -1.4613285f);
                put(100, -1.1192917f);
                put(169, -0.62700623f);
                put(145, -0.42046446f);
                put(9, -1.3251109f);
                put(55, -1.9773124f);
            }},
            new AstigmaticLensParams(-0.5f, -1.5f, 140.0f),
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
                put(32, 0.45132917f);
                put(77, 0.9306668f);
                put(123, 0.3658563f);
                put(99, 1.8869965f);
                put(168, 0.43858156f);
                put(145, 0.26023364f);
                put(11, 0.69237185f);
                put(54, 0.7809099f);
            }},
            new AstigmaticLensParams(0.75f, -0.25f, 65.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f), 99
    );

    @Test
    public void testCaseGoodFit4OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c4OS);
    }


    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
        add(c6OD);
        add(c6OS);
        add(c5OD);
        add(c5OS);
        add(c2OD);
        add(c2OS);
        add(c1OD);
        add(c1OS);
        add(c3OD);
        add(c3OS);
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
        add(c6OD);
        add(c6OS);
        add(c5OD);
        add(c5OS);
        add(c2OD);
        add(c2OS);
        add(c1OD);
        add(c1OS);
        add(c3OD);
        add(c3OS);
        add(c4OD);
        add(c4OS);
    }};

    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c6OD);
        add(c6OS);
        add(c5OD);
        add(c5OS);
        add(c2OD);
        add(c2OS);
        add(c1OD);
        add(c1OS);
        add(c3OD);
        add(c3OS);
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


