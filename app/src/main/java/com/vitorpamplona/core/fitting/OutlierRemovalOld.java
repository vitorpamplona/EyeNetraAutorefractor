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
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.CollectionUtils.AvgStdPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OutlierRemovalOld {

    public OutlierRemovalOld() {
        // TODO Auto-generated constructor stub
    }

    public AstigmaticLensParams run(Collection<MeridianPower> measuredMeridians, AstigmaticLensParams fitted, StringBuilder history) {

        clearOutliers(measuredMeridians);

        if (measuredMeridians.size() <= 6) {
            return fitted;
        }

        List<Outlier> options = new SinusoidalFitting().compileAllOutlierOptions(measuredMeridians, 0, fitted);

        Outlier lowerCyl = removeWhatIsPushingTheCyl(options, fitted);

        if (lowerCyl != null) {
            if (history != null)
                history.append("Lowering Cyl " + lowerCyl.removed.getAngle() + " because it was pushing the cyl higher.\n");
            lowerCyl.removed.setOutlier(true);
            return lowerCyl.getFitted();
        }

        if (Math.abs(fitted.getCylinder()) > 0.51) {
            // axis only matters if cyl is high.
            Outlier rightAxis = removeWhatIsPushingTheAxis(options, fitted);

            if (rightAxis != null) {
                if (history != null)
                    history.append("Removing Outlier " + rightAxis.removed.getAngle() + " because it was changing the axis away from the average of the other points.\n");
                rightAxis.removed.setOutlier(true);
                return rightAxis.getFitted();
            }
        }

        Outlier bigOutlier = removeWhatIsFarFromTheCurve(options, fitted);

        if (bigOutlier != null) {
            if (history != null)
                history.append("Removing Big Outlier " + bigOutlier.removed.getAngle() + ".\n");
            bigOutlier.removed.setOutlier(true);
            return bigOutlier.getFitted();
        }

        Outlier badFitting = removeWhatIsDeformingTheFittingQuality(options, fitted);

        if (badFitting != null) {
            if (history != null)
                history.append("Removing Bad Fitting Point " + badFitting.removed.getAngle() + ".\n");
            badFitting.removed.setOutlier(true);
            return badFitting.getFitted();
        }

        // Current prescription with outliers
        return fitted;
    }

    private void clearOutliers(Collection<MeridianPower> measuredMeridians) {
        for (MeridianPower p : measuredMeridians) {
            p.setOutlier(false);
        }
    }

    private Outlier removeWhatIsPushingTheAxis(List<Outlier> options, AstigmaticLensParams fitted) {
        //DecimalFormat formatter = new DecimalFormat("0");

        // Remove if pushing the axis.
        // Computes the difference of each pair of axis.
        float[][] diffs = new float[options.size()][options.size()];
        int i = 0, j = 0;
        for (Outlier candidate1 : options) {
            j = 0;
            for (Outlier candidate2 : options) {
                diffs[i][j] = AngleDiff.diff180(candidate1.getFitted().getAxis(), candidate2.getFitted().getAxis());
                if (diffs[i][j] > 179) {
                    diffs[i][j] = 0;
                }
                //System.out.print(formatter.format(diffs[i][j]) + "\t");
                j++;
            }
            //System.out.print("\n");
            i++;
        }

        // Makes the average of the difference
        Float[] averagesDiffs = new Float[options.size()];
        for (i = 0; i < averagesDiffs.length; i++) {
            averagesDiffs[i] = 0.0f;
            for (float diff : diffs[i]) {
                averagesDiffs[i] += diff;
            }
            averagesDiffs[i] = averagesDiffs[i] / (averagesDiffs.length - 1);
        }

        List<Integer> outliers = new ArrayList<Integer>();

        AvgStdPair overall = new CollectionUtils<Float>().avgSTD(Arrays.asList(averagesDiffs));
        for (i = 0; i < averagesDiffs.length; i++) {
            float avgDiff = averagesDiffs[i];
            if (Math.abs(avgDiff - overall.avg) > 2 * overall.std) {
                outliers.add(i);
            }
        }

        if (outliers.size() > 1) {
            // better repeat the test.
        }
        if (outliers.size() == 1) {
            Outlier candidate = options.get(outliers.get(0));
            // accepts up to -0.25 worse cyl.
            if (candidate.getFitted().getCylinder() < fitted.getCylinder() + 0.12) {
                // if it makes things equal or worse, don't correct.
                return null;
            }

            return options.get(outliers.get(0));
        }
        return null;
    }

    private Outlier removeWhatIsPushingTheCyl(List<Outlier> options, AstigmaticLensParams fitted) {
        float biggestDifferenceInCyl = 0.4f;

        Outlier fittingValuesOfTheBiggestDifference = null;

        //System.out.println("Fitted: " + fitted);

        // Remove if pushing the cyl.
        for (Outlier candidate : options) {
            //System.out.println("Option: " + candidate + " - Diff: " + (candidate.getFitted().getCylinder() - fitted.getCylinder()));
            // Has a significant decrease in cyl:
            // Remove the point that is pulling the curve to a higher cyl.
            if (candidate.getFitted().getCylinder() - fitted.getCylinder() > biggestDifferenceInCyl) {
                fittingValuesOfTheBiggestDifference = candidate;
                biggestDifferenceInCyl = (float) (candidate.getFitted().getCylinder() - fitted.getCylinder());
            }
        }

        return fittingValuesOfTheBiggestDifference;
    }

    private Outlier removeWhatIsFarFromTheCurve(List<Outlier> options, AstigmaticLensParams fitted) {
        float differenceToConsiderOutlier = 1.5f;
        float biggestDifferenceInCyl = 0f;

        Outlier fittingValuesOfTheBiggestDifference = null;

        // Remove if pushing the cyl.
        for (Outlier candidate : options) {
            // Remove everybody that is > 1D off.
            float diff = Math.abs(candidate.removed.getPower() - candidate.getFitted().interpolate(candidate.removed.getAngle()));
            if (diff > differenceToConsiderOutlier && diff > biggestDifferenceInCyl) {
                fittingValuesOfTheBiggestDifference = candidate;
                biggestDifferenceInCyl = diff;
            }
        }


        if (fittingValuesOfTheBiggestDifference != null) {
            // accepts up to -0.25 worse cyl.
            if (fittingValuesOfTheBiggestDifference.getFitted().getCylinder() < fitted.getCylinder() - 0.75) {
                return null;
            }
        }

        return fittingValuesOfTheBiggestDifference;
    }

    /** search for isolated points that makes the fit bad. */
    private Outlier removeWhatIsDeformingTheFittingQuality(List<Outlier> options, AstigmaticLensParams fitted) {
        float bestFit = 0.15f;

        Outlier fittingValuesOfTheBiggestDifference = null;

        List<Float> qualityOfFits = new ArrayList<Float>();

        //System.out.println("removeWhatIsDeformingTheFittingQuality");

        // Remove if pushing the cyl.
        for (Outlier candidate : options) {
            //System.out.println(candidate.angleRemoved + " " + candidate.qualityOfFit);

            qualityOfFits.add(candidate.qualityOfFit);
            // Remove everybody that is > 1D off.
            if (candidate.qualityOfFit < bestFit) {
                fittingValuesOfTheBiggestDifference = candidate;
                bestFit = candidate.qualityOfFit;
            }
        }

        AvgStdPair pair = new CollectionUtils<Float>().avgSTD(qualityOfFits);

        //System.out.println("P " + pair.avg + " " + pair.std + " proposal " + fittingValuesOfTheBiggestDifference);

        if (pair.avg < 0.2 && pair.std < 0.1) {
            return null;
        }

        return fittingValuesOfTheBiggestDifference;
    }

}