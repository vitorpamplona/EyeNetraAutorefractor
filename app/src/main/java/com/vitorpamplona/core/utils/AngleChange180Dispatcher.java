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


/**
 * Simple Listener for value change that dispatches msgs only when the value changes by a step size.
 */
public class AngleChange180Dispatcher extends NumberChangeDispatcher {

    public AngleChange180Dispatcher(double stepSize) {
        super(stepSize);
    }

    public double difference() {
        return Math.abs(AngleDiff.diff180((float) value, (float) lastDispatchedValue));
    }

    public void refreshListeners() {
        for (NumberChangedListener l : listeners) {
            double diff = difference();

            if (value < lastDispatchedValue) {
                diff = -diff;
            }
            if (Math.abs(value - lastDispatchedValue) > 90) {
                diff = -diff;
            }

            l.changed(lastDispatchedValue, value, (int) (diff / stepSize));
        }
    }

}
