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
public class Dataset1338Test {
    ArrayList<TestUtils.Case> groupGoodFit = new ArrayList<TestUtils.Case>() {{
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

    TestUtils.Case c2315OD = new TestUtils.Case(
            2315,
            0,
            new HashMap<Integer, Float>() {{
                put(12, 7.0236425f);
                put(33, 5.61716f);
                put(125, -4.6365995f);
                put(80, -3.2903368f);
                put(103, -4.171001f);
                put(169, -3.793323f);
                put(147, -4.0916357f);
                put(57, -3.5302558f);
            }},
            new AstigmaticLensParams(-0.25f, -3.5f, 30.0f),
            new AstigmaticLensParams(-3.25f, -1.00f, 40.0f), 12, 33
    );

    @Test
    public void testCaseBadFit2315OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2315OD);
    }

    TestUtils.Case c2315OS = new TestUtils.Case(
            2315,
            0,
            new HashMap<Integer, Float>() {{
                put(32, -4.4751496f);
                put(80, -3.9806907f);
                put(127, -5.572217f);
                put(103, -4.4392514f);
                put(169, -5.6572065f);
                put(149, -5.709599f);
                put(12, -4.8483243f);
                put(57, -3.8573234f);
            }},
            new AstigmaticLensParams(-4.0f, -1.75f, 65.0f),
            new AstigmaticLensParams(-1.75f, -0.25f, 100.0f)
    );

    @Test
    public void testCaseBadFit2315OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c2315OS);
    }

    TestUtils.Case c1037OD = new TestUtils.Case(
            1037,
            0,
            new HashMap<Integer, Float>() {{
                put(123, -8.407947f);
                put(166, -4.980613f);
                put(86, 5.576653f);
                put(145, -7.9154263f);
                put(9, -7.1644745f);
                put(56, -8.981497f);
                put(34, -8.442163f);
                put(79, -10.080543f);
            }},
            new AstigmaticLensParams(-4.5f, -3.0f, 100.0f),
            new AstigmaticLensParams(-6.75f, -2.25f, 165.0f), 87, 166
    );

    @Test
    public void testCaseBadFit1037OD() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1037OD);
    }

    TestUtils.Case c1037OS = new TestUtils.Case(
            1037,
            0,
            new HashMap<Integer, Float>() {{
                put(124, -8.487281f);
                put(55, -8.520511f);
                put(168, -5.796267f);
                put(146, -6.8716965f);
                put(103, -8.692167f);
                put(11, -6.6226125f);
                put(33, -7.524629f);
                put(78, -9.341836f);
            }},
            new AstigmaticLensParams(-6.25f, -3.0f, 175.0f),
            new AstigmaticLensParams(-7.0f, -2.5f, 170.0f)
    );

    @Test
    public void testCaseBadFit1037OS() {
        TestUtils.assertRoundingIsNotWorseThanFitting(c1037OS);
    }


    ArrayList<TestUtils.Case> groupBadFit = new ArrayList<TestUtils.Case>() {{
        add(c2315OD);
        add(c2315OS);
        add(c1037OD);
        add(c1037OS);
    }};


    ArrayList<TestUtils.Case> similarEyes = new ArrayList<TestUtils.Case>() {{
        add(c2315OD);
        add(c2315OS);
        add(c1037OD);
        add(c1037OS);
    }};

    ArrayList<TestUtils.Case> eightMeasurements = new ArrayList<TestUtils.Case>() {{
        add(c2315OD);
        add(c2315OS);
        add(c1037OD);
        add(c1037OS);
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
    public void testVDDAverageAbsoluteError() {
        ArrayList<TestUtils.Case> groupAll = new ArrayList<>();
        groupAll.addAll(groupBadFit);
        groupAll.addAll(groupGoodFit);
        TestUtils.assertVDDListOfCases(groupAll);
    }

    @Test
    public void testSphEqAverageAbsoluteError() {
        ArrayList<TestUtils.Case> groupAll = new ArrayList<>();
        groupAll.addAll(groupBadFit);
        groupAll.addAll(groupGoodFit);
        TestUtils.assertSphEqListOfCases(groupAll);
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


