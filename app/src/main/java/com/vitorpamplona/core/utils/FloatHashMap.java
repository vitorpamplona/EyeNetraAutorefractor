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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FloatHashMap<V> implements Map<Float, V>, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2092519313274217452L;

    HistoryHashMap<Integer, V> data = new HistoryHashMap<Integer, V>();

    public static int MULTIPLIER = 100;

    public FloatHashMap() {
        super();
    }

    @Override
    public void clear() {
        synchronized (this) {
            data.clear();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        synchronized (this) {
            return data.containsKey(toInt(((Number) key).floatValue()));
        }
    }

    @Override
    public boolean containsValue(Object value) {
        synchronized (this) {
            return data.containsValue(value);
        }
    }

    @Override
    public Set<java.util.Map.Entry<Float, V>> entrySet() {
        synchronized (this) {
            Set<java.util.Map.Entry<Float, V>> keys = new HashSet<java.util.Map.Entry<Float, V>>();
            for (java.util.Map.Entry<Integer, V> pair : data.entrySet()) {
                keys.add(new java.util.AbstractMap.SimpleEntry<Float, V>(toFloat(pair.getKey()), pair.getValue()));
            }
            return keys;
        }
    }

    @Override
    public V get(Object key) {
        synchronized (this) {
            return data.get(toInt(((Number) key).floatValue()));
        }
    }

    public V getClosestTo(Number key) {
        synchronized (this) {
            Integer iKey = toInt(((Number) key).floatValue());
            int minDiff = 99999;
            int minKey = 0;

            for (Integer i : data.keySet()) {
                if (Math.abs(i - iKey) < minDiff) {
                    minKey = i;
                    minDiff = Math.abs(i - iKey);
                }
            }

            return data.get(minKey);
        }
    }

    public V getClosestToAngle180(Number key) {
        synchronized (this) {
            float minDiff = 99999;
            int minKey = 0;

            float diff;

            for (Integer i : data.keySet()) {
                diff = AngleDiff.diff180(toFloat(i), ((Number) key).floatValue());

                if (diff < minDiff) {
                    minKey = i;
                    minDiff = diff;
                }
            }

            return data.get(minKey);
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (this) {
            return data.isEmpty();
        }
    }

    @Override
    public Set<Float> keySet() {
        synchronized (this) {
            Set<Float> keys = new HashSet<Float>();
            for (Integer a : data.keySet()) {
                keys.add(toFloat(a));
            }
            return keys;
        }
    }

    public Set<Integer> internalKeySet() {
        synchronized (this) {
            return data.keySet();
        }
    }

    public V getInternal(Integer internalAngle) {
        synchronized (this) {
            return data.get(internalAngle);
        }
    }


    @Override
    public V put(Float key, V value) {
        synchronized (this) {
            return data.put(toInt(key), value);
        }
    }

    @Override
    public void putAll(Map<? extends Float, ? extends V> arg0) {
        for (Float a : arg0.keySet()) {
            put(a, arg0.get(a));
        }
    }

    public List<List<V>> getHistoryFor(List<Float> keys) {
        List<List<V>> ret = new ArrayList<List<V>>();
        for (Float f : keys) {
            List<V> toAdd = getHistoryFor(f);
            if (toAdd == null) {
                toAdd = new ArrayList<V>();
            }
            ret.add(toAdd);
        }
        return ret;
    }

    public List<V> getHistoryFor(Float key) {
        synchronized (this) {
            return data.getHistory(toInt(key));
        }
    }

    @Override
    public V remove(Object key) {
        synchronized (this) {
            return data.remove(toInt(((Number) key).floatValue()));
        }
    }

    public Integer toInt(Float key) {
        //System.out.println("Storing " + (int) ((key.floatValue() * MULTIPLIER) + 0.001));
        // Fixed Floating point imprecisions
        return (int) ((key.floatValue() * MULTIPLIER) + 0.001);
    }

    public Float toFloat(Integer key) {
        return (key.floatValue() / MULTIPLIER);
    }

    @Override
    public int size() {
        synchronized (this) {
            return data.size();
        }
    }

    @Override
    public Collection<V> values() {
        synchronized (this) {
            return data.values();
        }
    }

    public HistoryHashMap<Integer, V> getData() {
        return data;
    }

    public void setData(HistoryHashMap<Integer, V> data) {
        this.data = data;
    }


}
