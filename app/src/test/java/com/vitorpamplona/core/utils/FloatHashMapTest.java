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
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class FloatHashMapTest {

    @Test
    public void testFloatHashMap() {
        FloatHashMap<Integer> test = new FloatHashMap<Integer>();

        test.put(1 / 4f, 3);
        assertEquals(1, test.size());
        assertEquals(3, test.get(0.25f).intValue());

        assertTrue(test.containsKey(0.25f));
        assertTrue(test.containsKey(2 / 8f));
        assertTrue(test.containsKey(0.2500001f));

        assertTrue(test.containsValue(3));

        test.put(0.25f, 4);
        assertEquals(1, test.size());
        assertEquals(4, test.get(0.25f).intValue());

        test.put(2 / 8f, 5);
        assertEquals(1, test.size());
        assertEquals(5, test.get(0.25f).intValue());

        test.put(0.1f + 0.1f + 0.05f, 6);

        assertEquals(1, test.size());
        assertEquals(6, test.get(0.25f).intValue());

        test.put(0.1f + 0.1f + 0.0500001f, 7);
        assertEquals(1, test.size());
        assertEquals(7, test.get(0.25f).intValue());

        List<Integer> history = test.getHistoryFor(0.25f);
        assertEquals(3, history.get(0).intValue());
        assertEquals(4, history.get(1).intValue());
        assertEquals(5, history.get(2).intValue());
        assertEquals(6, history.get(3).intValue());
        assertEquals(7, history.get(4).intValue());

        List<Float> floats = new ArrayList<Float>();
        floats.add(0.25f);
        List<List<Integer>> history2 = test.getHistoryFor(floats);

        assertEquals(3, history2.get(0).get(0).intValue());
        assertEquals(4, history2.get(0).get(1).intValue());
        assertEquals(5, history2.get(0).get(2).intValue());
        assertEquals(6, history2.get(0).get(3).intValue());
        assertEquals(7, history2.get(0).get(4).intValue());

        try {
            byte[] serialized = pickle(test);
            FloatHashMap<Integer> test2 = (FloatHashMap<Integer>) unpickle(serialized, FloatHashMap.class);

            assertEquals(1, test.size());
            assertEquals(7, test.get(0.25f).intValue());

            history = test.getHistoryFor(0.25f);
            assertEquals(3, history.get(0).intValue());
            assertEquals(4, history.get(1).intValue());
            assertEquals(5, history.get(2).intValue());
            assertEquals(6, history.get(3).intValue());
            assertEquals(7, history.get(4).intValue());

        } catch (IOException e) {
            e.printStackTrace();
            fail();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testKeySet() {
        FloatHashMap<Integer> test = new FloatHashMap<Integer>();
        for (float value = 0; value < 100; value += Math.random() / 10) {
            test.put(value, 10);
        }

        test.put(18.302137f, 10);
        test.put(18.327257f, 10);
        test.put(18.34031f, 10);
        test.put(18.3882f, 10);

        for (Float v : test.keySet()) {
            assertTrue("Key not found " + v, test.containsKey(v));
        }

    }

    public static <T extends Serializable> byte[] pickle(T obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    public static <T extends Serializable> T unpickle(byte[] b, Class<T> cl) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return cl.cast(o);
    }

}
