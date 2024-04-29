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
package com.vitorpamplona.core.utils;


import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class CollectionUtilsTest {

    public static final Collection<CollectionTestData> TEST_DATA = new ArrayList<CollectionTestData>() {{
        add(new CollectionTestData(new Float[]{1f, 2f, 3f, 4f, 5f}, 1, 5, 3, 1.41f, 15, 3, 10));
        add(new CollectionTestData(new Float[]{0f, 0f, 0f, 0f, 0f}, 0, 0, 0, 0, 0, 0, 0));
        add(new CollectionTestData(new Float[]{2f, 4f, 4f, 4f, 5f, 5f, 7f, 9f}, 2, 9, 5, 2, 40, 5, 32));
    }};

    public CollectionUtilsTest() {

    }

    @Test
    public void testMin() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.min, utils.min(testItem.data()), 0.01f);
        }
    }

    @Test
    public void testMax() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.max, utils.max(testItem.data()), 0.01f);
        }
    }

    @Test
    public void testSum() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.sum, utils.sum(testItem.data()), 0.01f);
        }
    }

    @Test
    public void testAvg() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.avg, utils.average(testItem.data()), 0.01f);
        }
    }

    @Test
    public void testSqError() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.sqError, utils.sumSquaredError(testItem.truth, testItem.data()), 0.01f);
        }
    }

    @Test
    public void testAvgStd() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.avg, utils.avgSTD(testItem.data()).avg, 0.01f);
            assertEquals(testItem.std, utils.avgSTD(testItem.data()).std, 0.01f);
        }
    }

    @Test
    public void testMinMax() {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        for (CollectionTestData testItem : TEST_DATA) {
            assertEquals(testItem.max, utils.minMax(testItem.data()).max, 0.01f);
            assertEquals(testItem.min, utils.minMax(testItem.data()).min, 0.01f);
        }
    }

    static class CollectionTestData {
        Float[] data;
        float min;
        float max;
        float avg;
        float sum;
        float std;
        float truth;
        float sqError;

        public CollectionTestData(Float[] data, float min, float max, float avg, float std, float sum, float truth, float sqError) {
            super();
            this.data = data;
            this.min = min;
            this.max = max;
            this.avg = avg;
            this.sum = sum;
            this.std = std;
            this.truth = truth;
            this.sqError = sqError;
        }

        public Collection<Float> data() {
            return Arrays.asList(data);
        }
    }


}

