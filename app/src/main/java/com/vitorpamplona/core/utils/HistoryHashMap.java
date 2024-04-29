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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keeps the history of all changes in a Map.
 *
 * @param <K>
 *            Keys
 * @param <V>
 *            Values
 */
public class HistoryHashMap<K, V> implements Map<K, V>, Serializable {

    public static int MAX_HISTORY_SIZE = 25;

    /**
     *
     */
    private static final long serialVersionUID = 2092519313274217452L;

    HashMap<K, V> data = new HashMap<K, V>();
    HashMap<K, List<V>> history = new HashMap<K, List<V>>();

    public HistoryHashMap() {
        super();
    }

    @Override
    public void clear() {
        data.clear();
        history.clear();
    }

    public void clearHistory() {
        history.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return data.entrySet();
    }

    @Override
    public V get(Object key) {
        return data.get(key);
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return data.keySet();
    }

    public void store(K key, V value) {
        // store value.
        if (!history.containsKey(key)) history.put(key, new ArrayList<V>());

        List<V> history = getHistory(key);
        history.add(value);
        if (history.size() > MAX_HISTORY_SIZE) history.remove(0);
    }

    @Override
    public V put(K key, V value) {
        store(key, value);

        // overide the current value.
        return data.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> arg0) {
        for (K key : arg0.keySet()) {
            store(key, arg0.get(key));
        }

        data.putAll(arg0);
    }

    @Override
    public V remove(Object key) {
        history.remove(key);
        return data.remove(key);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Collection<V> values() {
        return data.values();
    }

    public List<V> getHistory(K key) {
        return history.get(key);
    }

    public HashMap<K, V> getData() {
        return data;
    }

    public void setData(HashMap<K, V> data) {
        this.data = data;
    }

    public HashMap<K, List<V>> getHistory() {
        return history;
    }

    public void setHistory(HashMap<K, List<V>> history) {
        this.history = history;
    }


}
