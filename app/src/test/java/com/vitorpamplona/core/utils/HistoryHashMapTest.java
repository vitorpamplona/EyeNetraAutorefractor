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
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class HistoryHashMapTest {

    @Test
    public void testHistoryHashMap() {
        HistoryHashMap<Integer, Integer> test = new HistoryHashMap<Integer, Integer>();

        test.put(1, 3);

        assertEquals(1, test.size());
        assertEquals(3, test.get(1).intValue());
        assertTrue(test.containsValue(3));

        test.put(1, 4);
        assertEquals(1, test.size());
        assertEquals(4, test.get(1).intValue());

        test.put(1, 5);
        assertEquals(1, test.size());
        assertEquals(5, test.get(1).intValue());

        assertEquals(3, test.getHistory(1).size());

        assertEquals(3, test.getHistory(1).get(0).intValue());
        assertEquals(4, test.getHistory(1).get(1).intValue());
        assertEquals(5, test.getHistory(1).get(2).intValue());

        test.remove(1);

        assertEquals(null, test.get(1));
        assertEquals(0, test.size());
        assertEquals(null, test.getHistory(1));
    }


}
