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

import java.util.ArrayList;
import java.util.List;


/**
 * Simple Listener for value change that dispatches msgs only when the value changes by a step size.
 */
public class NumberChangeDispatcher {

    double value = Double.NaN;
    double lastDispatchedValue = Double.NaN;

    double stepSize;

    List<NumberChangedListener> listeners = new ArrayList<NumberChangedListener>();

    boolean forceNextUpdate = false;

    public NumberChangeDispatcher(double stepSize) {
        this.stepSize = stepSize;
    }

    public void newValue(double value) {
        if (Double.isNaN(value)) {
            throw new RuntimeException("Value is NaN");
        }

        this.value = value;

        // If wasn't initialized, initialize without triggering any change.
        if (Double.isNaN(lastDispatchedValue)) {
            lastDispatchedValue = value;
        }

        if (difference() > stepSize || forceNextUpdate) {
            refreshListeners();
            lastDispatchedValue = value;
        }
    }

    public double difference() {
        return Math.abs(value - lastDispatchedValue);
    }

    public void refreshListeners() {
        for (NumberChangedListener l : listeners) {
            double diff = difference();
            l.changed(lastDispatchedValue, value, (int) (diff / stepSize));
        }
    }

    public void reset() {
        this.value = Double.NaN;
        this.lastDispatchedValue = Double.NaN;
    }

    public double getValue() {
        return value;
    }

    public interface NumberChangedListener {
        public void changed(double from, double to, int stepsChanged);
    }

    ;

    public void add(NumberChangedListener listener) {
        this.listeners.add(listener);
    }

    public void remove(NumberChangedListener listener) {
        this.listeners.remove(listener);
    }
}
