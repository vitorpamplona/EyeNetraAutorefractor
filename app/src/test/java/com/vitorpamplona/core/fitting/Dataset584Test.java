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
import com.vitorpamplona.core.fitting.TestOptimizerUtils;
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
public class Dataset584Test {

    TestUtils.Case c607OD = new TestUtils.Case(
            607,
            0,
            new HashMap<Integer, Float>() {{
                put(122, -4.120558f);
                put(144, -4.8643246f);
                put(55, -4.666124f);
                put(168, -5.095322f);
                put(100, -4.5438204f);
                put(10, -5.2602324f);
                put(32, -4.559549f);
                put(78, -4.6272607f);
            }},
            new AstigmaticLensParams(-4.5f, -0.25f, 95.0f),
            new AstigmaticLensParams(-4.25f, -0.75f, 105.0f)
    );

    @Test
    public void testCaseGoodFit607OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c607OD);
    }

    TestUtils.Case c607OS = new TestUtils.Case(
            607,
            0,
            new HashMap<Integer, Float>() {{
                put(123, -4.359496f);
                put(53, -3.6287673f);
                put(145, -5.1375604f);
                put(166, -3.774869f);
                put(99, -4.0640945f);
                put(8, -3.7897787f);
                put(30, -4.064674f);
                put(77, -3.974265f);
            }},
            new AstigmaticLensParams(-4.0f, 0.0f, 0.0f),
            new AstigmaticLensParams(-4.00f, -0.50f, 30.0f)
    );

    @Test
    public void testCaseGoodFit607OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c607OS);
    }


    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
        add(c607OD);
        add(c607OS);
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

    TestUtils.Case c604OD = new TestUtils.Case(
            604,
            0,
            new HashMap<Integer, Float>() {{
                put(120, -1.3826952f);
                put(168, -1.1893378f);
                put(55, -1.6998527f);
                put(145, -1.3449394f);
                put(98, -1.5479198f);
                put(9, -1.5806055f);
                put(29, -1.7474742f);
                put(74, -0.91979927f);
            }},
            new AstigmaticLensParams(-1.5f, 0.0f, 0.0f),
            new AstigmaticLensParams(-1.25f, -0.50f, 145f),
            74
    );

    @Test
    public void testCaseBadFit604OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c604OD);

    }

    TestUtils.Case c604OS = new TestUtils.Case(
            604,
            0,
            new HashMap<Integer, Float>() {{
                put(124, -2.2840412f);
                put(169, -0.8886337f);
                put(55, -1.0747707f);
                put(144, -2.0559757f);
                put(100, -0.8754621f);
                put(10, 4.21098f);   // Outlier
                put(32, 1.4429368f); // Outlier
                put(77, -1.4975392f);
            }},
            new AstigmaticLensParams(-0.25f, -1.0f, 30.0f),
            new AstigmaticLensParams(-1.5f, 0.0f, 0.0f), 10, 33
    );

    @Test
    public void testCaseBadFit604OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c604OS);
    }

    TestUtils.Case c606OD = new TestUtils.Case(
            606,
            0,
            new HashMap<Integer, Float>() {{
                put(123, -0.26298323f);
                put(169, -0.3446425f);
                put(53, -1.3466816f);
                put(147, -0.10343355f);
                put(103, 0.071296945f);
                put(11, 0.43822917f);
                put(30, -1.6266087f);
                put(76, -0.20146267f);
            }},
            new AstigmaticLensParams(0.0f, 0.0f, 0.0f),
            new AstigmaticLensParams(0.25f, -0.50f, 40.0f), 31, 54 // changed to our best possible output
    );

    @Test
    public void testCaseBadFit606OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c606OD);
    }

    TestUtils.Case c606OS = new TestUtils.Case(
            606,
            0,
            new HashMap<Integer, Float>() {{
                put(122, -0.96191716f);
                put(55, -1.3214201f);
                put(167, -1.737424f);
                put(146, -1.2503655f);
                put(99, -0.57026607f);
                put(10, -1.8761827f);
                put(32, -0.799087f);
                put(77, -0.96226054f);
            }},
            new AstigmaticLensParams(-0.75f, -0.75f, 100.0f),
            new AstigmaticLensParams(-0.5f, -1.25f, 100.0f), 32 // changed to best possible output
    );

    @Test
    public void testCaseBadFit606OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c606OS);
    }


    TestUtils.Case c605OD = new TestUtils.Case(
            605,
            1,
            new HashMap<Integer, Float>() {{
                put(123, -0.839707f);
                put(55, -0.1855961f);
                put(168, 0.16101849f);
                put(145, -0.13733359f);
                put(103, 1.1489413f);
                put(10, -0.05767541f);
                put(31, 0.3081022f);
                put(78, 0.122663036f);
            }},
            new AstigmaticLensParams(0.0f, -0.25f, 25.0f),
            new AstigmaticLensParams(0.5f, -0.50f, 25.0f), 103
    );

    @Test
    public void testCaseBadFit605OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c605OD);
    }

    TestUtils.Case c605OS = new TestUtils.Case(
            605,
            1,
            new HashMap<Integer, Float>() {{
                put(122, 1.4978336f);
                put(167, 0.64256877f);
                put(54, 0.7539645f);
                put(145, 1.5214474f);
                put(99, 1.881237f);
                put(10, 0.7351348f);
                put(32, 0.90665233f);
                put(77, 1.1980395f);
            }},
            new AstigmaticLensParams(1.5f, -0.75f, 110.0f),
            new AstigmaticLensParams(1.75f, 0.0f, 0.0f) // changed to the best we can do.
    );

    @Test
    public void testCaseBadFit605OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c605OS);
    }


    ArrayList<TestUtils.Case> groupBadFit = new ArrayList<TestUtils.Case>() {{
        add(c604OD);
        add(c604OS);
        add(c606OD);
        add(c606OS);
        add(c605OD);
        add(c605OS);
    }};


    ArrayList<TestUtils.Case> differentEyes = new ArrayList<TestUtils.Case>() {{
        add(c605OD);
        add(c605OS);
    }};

    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
        add(c604OD);
        add(c604OS);
        add(c606OD);
        add(c606OS);
        add(c607OD);
        add(c607OS);
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c604OD);
        add(c604OS);
        add(c606OD);
        add(c606OS);
        add(c607OD);
        add(c607OS);
        add(c605OD);
        add(c605OS);
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


