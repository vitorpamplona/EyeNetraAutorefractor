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

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;


public class CollectionUtils<T extends Number> {
    public float min(Collection<T> numbers) {
        float minValue = Float.MAX_VALUE;
        for (T f : numbers) {
            if (f.floatValue() < minValue) minValue = f.floatValue();
        }
        return minValue;
    }

    public float max(Collection<T> numbers) {
        float maxValue = Float.MIN_VALUE;
        for (T f : numbers) {
            if (f.floatValue() > maxValue) maxValue = f.floatValue();
        }
        return maxValue;
    }

    public float sum(Collection<T> numbers) {
        float sum = 0;
        for (T f : numbers) {
            sum += f.floatValue();
        }
        return sum;
    }

    public float average(Collection<T> numbers) {
        return sum(numbers) / numbers.size();
    }

    public float sumSquaredError(float groundTruth, Collection<T> numbers) {
        float sum = 0;
        for (T f : numbers) {
            float absError = Math.abs(f.floatValue() - groundTruth);
            absError = absError * absError;
            sum += absError;
        }
        return sum;
    }

    public float sumSquaredError(List<T> groundTruth, List<T> numbers) {
        float sum = 0;
        for (int i = 0; i < numbers.size(); i++) {
            float absError = Math.abs(numbers.get(i).floatValue() - groundTruth.get(i).floatValue());
            absError = absError * absError;
            sum += absError;
        }
        return sum;
    }

    public AvgStdPair avgSTD(Collection<T> numbers) {
        float average = average(numbers);
        float stdDev = (float) Math.sqrt(sumSquaredError(average, numbers) / numbers.size());

        return new AvgStdPair(average, stdDev);
    }

    public MinMaxPair minMax(Collection<T> data) {
        return new MinMaxPair(min(data), max(data));
    }

    public class MinMaxPair {
        public float min;
        public float max;

        public MinMaxPair(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }

    public static class AvgStdPair {
        static DecimalFormat f = new DecimalFormat(" 0.00;-0.00");

        public float avg;
        public float std;

        public AvgStdPair(float avg, float std) {
            this.avg = avg;
            this.std = std;
        }

        public String toString() {
            return f.format(avg) + " ± " + f.format(std);
        }
    }

    public float percentUnder(Collection<T> numbers, T limit) {
        int count = 0;
        for (T f : numbers) {
            if (f.doubleValue() < limit.doubleValue()) count++;
        }
        return count / (float) numbers.size();
    }

    public double correlation(List<T> xs, List<T> ys) {
        double sx = 0.0;
        double sy = 0.0;
        double sxx = 0.0;
        double syy = 0.0;
        double sxy = 0.0;

        int n = xs.size();

        for (int i = 0; i < n; ++i) {
            T x = xs.get(i);
            T y = ys.get(i);

            sx += x.doubleValue();
            sy += y.doubleValue();
            sxx += x.doubleValue() * x.doubleValue();
            syy += y.doubleValue() * y.doubleValue();
            sxy += x.doubleValue() * y.doubleValue();
        }

        // covariation
        double cov = sxy / n - sx * sy / n / n;
        // standard error of x
        double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
        // standard error of y
        double sigmay = Math.sqrt(syy / n - sy * sy / n / n);

        // correlation is just a normalized covariation
        return cov / sigmax / sigmay;
    }
}
