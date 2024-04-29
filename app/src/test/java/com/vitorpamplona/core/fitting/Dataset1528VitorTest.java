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
public class Dataset1528VitorTest {

    /**
     Tests generated for 5 people that participated in a side-by-side evaluation comparing NETRA
     (Device ID #1528 and software version 2.05)   to Subjective.

     Per Subjective measurements, refractive errors ranged from -1.12D to 0.38D on spherical equivalent
     (Mean + SD of -0.38D ±0.75) and from -1.25D to -1.25D on cylinder (Mean + SD of -1.25D ±0.00).

     NETRA   originally yielded an average absolute difference of 0.32D on spherical equivalent against
     the Subjective. NETRA   presented a systematic bias of -0.28D on spherical equivalent.

     The linear regression fit presented a slope of 1.13
     NETRA   measures pupillary distance with average absolute error of 0.0mm ±0.0.
     **/

    TestUtils.Case c1OD = new TestUtils.Case(
            1,
            0,
            new HashMap<Integer, Float>() {{
                put(123, -1.1345496f);
                put(54, -2.0703092f);
                put(167, -0.4667988f);
                put(144, -0.6766124f);
                put(98, -1.8024466f);
                put(9, -0.80805665f);
                put(30, -1.6902344f);
                put(75, -1.6144814f);
            }},
            new AstigmaticLensParams(-0.75f, -1.25f, 160.0f),
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
                put(119, 0.8778714f);
                put(52, -0.9466552f);
                put(167, 0.35834134f);
                put(144, 0.7073924f);
                put(97, 0.5561655f);
                put(8, -0.2553954f);
                put(31, -1.1853755f);
                put(75, -0.002784239f);
            }},
            new AstigmaticLensParams(1.0f, -1.75f, 130.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit1OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1OS);
    }

    TestUtils.Case c2OD = new TestUtils.Case(
            2,
            0,
            new HashMap<Integer, Float>() {{
                put(121, -0.57991284f);
                put(167, -0.46451512f);
                put(53, -2.027049f);
                put(144, -0.83030736f);
                put(100, -1.1084121f);
                put(8, -1.0321295f);
                put(31, -1.7699888f);
                put(76, -2.0910888f);
            }},
            new AstigmaticLensParams(-0.5f, -1.5f, 150.0f),
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
                put(120, 0.5481365f);
                put(168, -0.122499295f);
                put(53, -0.11358415f);
                put(143, 0.12511335f);
                put(99, 0.5210976f);
                put(10, -0.6079751f);
                put(29, -0.53246796f);
                put(75, 0.50512165f);
            }},
            new AstigmaticLensParams(0.5f, -1.0f, 105.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit2OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2OS);
    }

    TestUtils.Case c5OD = new TestUtils.Case(
            5,
            0,
            new HashMap<Integer, Float>() {{
                put(122, -1.2581111f);
                put(144, -0.9096375f);
                put(53, -2.8906908f);
                put(167, -0.9693136f);
                put(98, -1.8238995f);
                put(8, -1.566899f);
                put(30, -2.201064f);
                put(76, -2.3450935f);
            }},
            new AstigmaticLensParams(-1.0f, -1.75f, 150.0f),
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
                put(121, 0.3727073f);
                put(54, 0.3362561f);
                put(143, 0.20778838f);
                put(166, 0.32689905f);
                put(99, 0.49131554f);
                put(8, 0.017556902f);
                put(30, 0.24001165f);
                put(75, 0.8141689f);
            }},
            new AstigmaticLensParams(0.5f, -0.25f, 90.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit5OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c5OS);
    }

    TestUtils.Case c3OD = new TestUtils.Case(
            3,
            0,
            new HashMap<Integer, Float>() {{
                put(122, -1.2581111f);
                put(165, -0.81966907f);
                put(54, -2.5388737f);
                put(143, -1.0660048f);
                put(99, -1.8665864f);
                put(9, -1.3470541f);
                put(30, -2.1591306f);
                put(77, -2.022934f);
            }},
            new AstigmaticLensParams(-1.0f, -1.5f, 150.0f),
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
                put(121, 0.32865146f);
                put(166, 0.288568f);
                put(54, -0.28196788f);
                put(143, 0.60214615f);
                put(99, 0.7714139f);
                put(8, 0.017827258f);
                put(31, -0.031085562f);
                put(75, 0.25938278f);
            }},
            new AstigmaticLensParams(0.5f, -0.75f, 125.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit3OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c3OS);
    }

    TestUtils.Case c4OD = new TestUtils.Case(
            4,
            0,
            new HashMap<Integer, Float>() {{
                put(122, -1.3605105f);
                put(168, -0.6524534f);
                put(52, -2.1989057f);
                put(146, -0.61653435f);
                put(98, -1.5513246f);
                put(11, -1.677701f);
                put(30, -1.8828211f);
                put(78, -2.1773188f);
            }},
            new AstigmaticLensParams(-0.75f, -1.5f, 150.0f),
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
                put(120, 0.5481365f);
                put(168, 0.4202124f);
                put(53, 0.17317592f);
                put(144, 0.0497881f);
                put(97, 0.31557098f);
                put(9, -0.2827568f);
                put(30, -0.12341312f);
                put(76, 0.06400432f);
            }},
            new AstigmaticLensParams(0.25f, -0.25f, 120.0f),
            new AstigmaticLensParams(1.0f, -1.25f, 105.0f)
    );

    @Test
    public void testCaseGoodFit4OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c4OS);
    }


    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
        add(c1OD);
        add(c1OS);
        add(c2OD);
        add(c2OS);
        add(c5OD);
        add(c5OS);
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
        add(c1OD);
        add(c1OS);
        add(c2OD);
        add(c2OS);
        add(c5OD);
        add(c5OS);
        add(c3OD);
        add(c3OS);
        add(c4OD);
        add(c4OS);
    }};

    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c1OD);
        add(c1OS);
        add(c2OD);
        add(c2OS);
        add(c5OD);
        add(c5OS);
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

    @Test
    public void testEightVsSixteenParameters() {
        System.out.println("\n******\n<0.25 VDDs\n******");
        TestUtils.assertVDDListOfCases(TestUtils.select(eightMeasurements, 0f, 0.25f));
        TestUtils.assertVDDListOfCases(TestUtils.select(sixteenMeasurements, 0f, 0.25f));
        System.out.println("\n******\n<0.75 VDDs\n******");
        TestUtils.assertVDDListOfCases(TestUtils.select(eightMeasurements, 0.25f, 0.75f));
        TestUtils.assertVDDListOfCases(TestUtils.select(sixteenMeasurements, 0.25f, 0.75f));
        System.out.println("\n******\n<1.50 VDDs\n******");
        TestUtils.assertVDDListOfCases(TestUtils.select(eightMeasurements, 0.75f, 1.5f));
        TestUtils.assertVDDListOfCases(TestUtils.select(sixteenMeasurements, 0.75f, 1.5f));
        System.out.println("\n******\nAll Others\n******");
        TestUtils.assertVDDListOfCases(TestUtils.select(eightMeasurements, 1.50f, 1000f));
        TestUtils.assertVDDListOfCases(TestUtils.select(sixteenMeasurements, 1.50f, 1000f));
    }
}


