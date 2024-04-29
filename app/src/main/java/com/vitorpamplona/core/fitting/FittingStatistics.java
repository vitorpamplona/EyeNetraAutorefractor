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
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.utils.FloatHashMap;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Additional Data for the prescription object.
 */
public class FittingStatistics implements Serializable {
    private static final long serialVersionUID = 1L;
    public double averageAbsoluteError;
    public double averageError;
    public double stddev;
    public double summedDifference = 0;
    public double summedSquaredDifference = 0;
    public double summedAbsDifference = 0;

    public List<AngleGoodness> sortedWorstCases;
    public double[] diff;

    public static class AngleGoodness implements Comparable<AngleGoodness>,
            Serializable {
        private static final long serialVersionUID = 1L;
        public float angle;
        public double absDiff;

        public AngleGoodness(float angle, double d) {
            super();
            this.angle = angle;
            this.absDiff = d;
        }

        @Override
        public int compareTo(AngleGoodness another) {
            return -Double.compare(absDiff, another.absDiff);
        }
    }

    public FittingStatistics(AstigmaticLensParams fittedPrescription,
                             FloatHashMap<MeridianPower> measuredMeridians) {

        compute(fittedPrescription, measuredMeridians.values());
    }

    public FittingStatistics(AstigmaticLensParams fittedPrescription,
                             Collection<MeridianPower> measuredMeridians) {
        compute(fittedPrescription, measuredMeridians);
    }

    public FittingStatistics(
            Collection<MeridianPower> measuredMeridians) {

        SinusoidalFitting fit = new SinusoidalFitting();
        AstigmaticLensParams fittedPrescription = fit.curveFitting(measuredMeridians);

        compute(fittedPrescription, measuredMeridians);
    }

    public void compute(AstigmaticLensParams fittedPrescription,
                        Collection<MeridianPower> measuredMeridians) {
        sortedWorstCases = new ArrayList<FittingStatistics.AngleGoodness>();

        summedDifference = 0;
        summedSquaredDifference = 0;
        summedAbsDifference = 0;
        diff = new double[measuredMeridians.size()];

        int i = 0;
        for (MeridianPower key : measuredMeridians) {
            if (key.isOutlier())
                continue;

            diff[i] = key.getPower()
                    - fittedPrescription.interpolate(key.getAngle());

            summedDifference += diff[i];
            summedSquaredDifference += diff[i] * diff[i];
            summedAbsDifference += Math.abs(diff[i]);

            sortedWorstCases.add(new AngleGoodness(key.getAngle(), Math.abs(diff[i])));

            i++;
        }

        Collections.sort(sortedWorstCases);

        averageAbsoluteError = summedAbsDifference / measuredMeridians.size();
        averageError = summedDifference / measuredMeridians.size();

        // Getting standard deviation of the difference
        double sumSquaredError = 0;
        for (i = 0; i < measuredMeridians.size(); i++) {
            sumSquaredError += (Math.abs(diff[i]) - averageError) * (Math.abs(diff[i]) - averageError);
        }
        stddev = Math.sqrt(sumSquaredError / measuredMeridians.size());
    }

    public String shortToString() {
        NumberFormat formatter = new DecimalFormat("0.000");

        return formatter.format(averageAbsoluteError) + "D +/- "
                + formatter.format(stddev);
    }

    public String toString() {
        NumberFormat formatter = new DecimalFormat("0.000");

        String diffStr = " | ";
        for (int i = 0; i < diff.length; i++) {
            diffStr += (formatter.format(diff[i]) + "; ");
        }

        if (sortedWorstCases.size() > 2) {
            return "~ " + formatter.format(averageAbsoluteError) + "D +/- "
                    + formatter.format(stddev) + diffStr + " Worsts: "
                    + (sortedWorstCases.get(0).angle) + " ("
                    + formatter.format(sortedWorstCases.get(0).absDiff) + "D)"
                    + ", " + (sortedWorstCases.get(1).angle) + " ("
                    + formatter.format(sortedWorstCases.get(1).absDiff) + "D)";
        } else {
            return "~ " + formatter.format(averageAbsoluteError) + "D +/- "
                    + formatter.format(stddev) + diffStr;
        }
    }

    public float getWorstAngle(int i) {
        return sortedWorstCases.get(i).angle;
    }
}