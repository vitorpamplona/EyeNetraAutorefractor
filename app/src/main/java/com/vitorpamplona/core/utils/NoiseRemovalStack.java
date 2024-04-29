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

import com.vitorpamplona.core.utils.CollectionUtils.AvgStdPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Noise filtering by only setting the value change when the standard deviation of the readings is below a given threshold.
 */
public class NoiseRemovalStack {
    public List<Double> values = new ArrayList<Double>();
    public static final int DEFAULT_STACK_SIZE = 3;

    public int stackSize;
    private double requiredStdDevToUpdateAverage;

    private double stableValue;

    public NoiseRemovalStack(double requiredStdDevToUpdateAverage) {
        this.stackSize = DEFAULT_STACK_SIZE;
        this.requiredStdDevToUpdateAverage = requiredStdDevToUpdateAverage;
        this.stableValue = Double.NaN;
    }

    public NoiseRemovalStack(double requiredStdDevToUpdateAverage, int stackSize) {
        this.stackSize = stackSize;
        this.requiredStdDevToUpdateAverage = requiredStdDevToUpdateAverage;
        this.stableValue = Double.NaN;
    }

    public synchronized void add(double value) {
        if (Double.isNaN(value)) {
            throw new RuntimeException("Value is NaN");
        }

        values.add(0, value);
        if (values.size() > stackSize) {
            values.remove(values.size() - 1);
        }
    }

    public boolean isReady() {
        return values.size() == stackSize && !Double.isNaN(average());
    }

    public void reset() {
        values.clear();
        stableValue = Double.NaN;
    }

    public synchronized double average() {
        if (values.isEmpty()) return Double.NaN;

        AvgStdPair stdDev = new CollectionUtils<Double>().avgSTD(values);

        if (stdDev.std > requiredStdDevToUpdateAverage) {
            return stableValue;
        }

        return stableValue = stdDev.avg;
    }
}
