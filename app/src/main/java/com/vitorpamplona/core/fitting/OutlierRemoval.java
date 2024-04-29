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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OutlierRemoval {

    public static final double BIGGEST_PUSH_SMALL_CYLS = 0.50f;
    public static final double BIGGEST_PUSH_LARGE_CYLS = 0.75f;
    public static final double MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS = 1.8;
    public static final double DIFFERENCE_TO_CONSIDER_AN_OUTLIER = 0.65f;
    public static final double MIN_DEFORMING_FIT_QUALITY = 0.15f;

    //public static final double BIGGEST_PUSH_SMALL_CYLS = 0.40f;
    //public static final double BIGGEST_PUSH_LARGE_CYLS = 0.50f;
    //public static final double MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS = 3;
    //public static final double DIFFERENCE_TO_CONSIDER_AN_OUTLIER = 1.85f;
    //public static final double MIN_DEFORMING_FIT_QUALITY = 0.15f;

    //public static final double BIGGEST_PUSH_SMALL_CYLS = 0.40f;
    //public static final double BIGGEST_PUSH_LARGE_CYLS = 0.70f;
    //public static final double MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS = 2.5;
    //public static final double DIFFERENCE_TO_CONSIDER_AN_OUTLIER = 1.00f;
    //public static final double MIN_DEFORMING_FIT_QUALITY = 0.42f;

    public OutlierRemoval() {
        // TODO Auto-generated constructor stub
    }

    private AstigmaticLensParams run(Collection<MeridianPower> measuredMeridians, AstigmaticLensParams fitted, StringBuilder history) {
        return run(measuredMeridians, 0, fitted, history, BIGGEST_PUSH_SMALL_CYLS, BIGGEST_PUSH_LARGE_CYLS,
                MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS, DIFFERENCE_TO_CONSIDER_AN_OUTLIER, MIN_DEFORMING_FIT_QUALITY);
    }

    public AstigmaticLensParams run(Collection<MeridianPower> measuredMeridians, int dongs, AstigmaticLensParams fitted, StringBuilder history) {
        return run(measuredMeridians, dongs, fitted, history, BIGGEST_PUSH_SMALL_CYLS, BIGGEST_PUSH_LARGE_CYLS,
                MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS, DIFFERENCE_TO_CONSIDER_AN_OUTLIER, MIN_DEFORMING_FIT_QUALITY);
    }

    public List<Outlier> calculateOptions(Collection<MeridianPower> measuredMeridians, int dongs, AstigmaticLensParams fitted) {
        clearOutliers(measuredMeridians);

        if (measuredMeridians.size() <= 6) {
            return null;
        }

        return new SinusoidalFitting().compileAllOutlierOptions(measuredMeridians, dongs, fitted);
    }

    public AstigmaticLensParams run(Collection<MeridianPower> measuredMeridians, int dongs, AstigmaticLensParams fitted, StringBuilder history, double biggestPushSmallCyls, double biggestPushLargeCyls, double minStdDevMultiplierToPushAxis, double differenceToConsiderAnOutlier, double minDeformingFitQuality) {
        double fit = new QualityOfFit().compute(measuredMeridians, dongs, fitted);

        List<Outlier> options = calculateOptions(measuredMeridians, dongs, fitted);

        if (options == null) {
            return fitted;
        }

        if (options != null) {
            Collections.sort(options);

            //System.out.println("Full  : Fit: " + String.format("%1.2f", fit) + "\t\t\t\t\t\t\t\t\t\t\t=>\t" + fitted.toString());
            //for (Outlier candidate : options) {
            //	System.out.println("Option: " + candidate);
            //}
        }

        if (history != null) {
            history.append("Quality of Fit: " + fit + "\n");
        }

        return run(options, fitted, fit, history, biggestPushSmallCyls, biggestPushLargeCyls, minStdDevMultiplierToPushAxis, differenceToConsiderAnOutlier, minDeformingFitQuality);
    }

    public AstigmaticLensParams run(List<Outlier> options, AstigmaticLensParams fitted, double qualityOfFit, StringBuilder history) {
        return run(options, fitted, qualityOfFit, history, BIGGEST_PUSH_SMALL_CYLS, BIGGEST_PUSH_LARGE_CYLS,
                MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS, DIFFERENCE_TO_CONSIDER_AN_OUTLIER, MIN_DEFORMING_FIT_QUALITY);
    }

    public AstigmaticLensParams run(List<Outlier> options, AstigmaticLensParams fitted, double qualityOfFit, StringBuilder history, double biggestPushSmallCyls, double biggestPushLargeCyls, double minStdDevMultiplierToPushAxis, double differenceToConsiderAnOutlier, double minDeformingFitQuality) {

        Outlier lowerCyl = removeWhatIsPushingTheCyl(options, fitted, biggestPushSmallCyls, biggestPushLargeCyls);

        if (lowerCyl != null) {
            if (history != null)
                history.append("Lowering Cyl because it was pushing the cyl higher: " + lowerCyl + ".\n");
            lowerCyl.setOutliers();

            return lowerCyl.getFitted();
        }

        if (qualityOfFit > 0.40) {
            if (Math.abs(fitted.getCylinder()) > 0.51) {
                // axis only matters if cyl is high.
                Outlier rightAxis = removeWhatIsPushingTheAxis(options, fitted, minStdDevMultiplierToPushAxis);

                if (rightAxis != null) {
                    if (history != null)
                        history.append("Removing Outlier because it was changing the axis away from the average of the other points." + rightAxis + "\n");
                    rightAxis.setOutliers();
                    return rightAxis.getFitted();
                }
            }
        }

        if (qualityOfFit > 0.40) {
            Outlier bigOutlier = removeWhatIsFarFromTheCurve(options, fitted, minStdDevMultiplierToPushAxis);

            if (bigOutlier != null) {
                if (history != null)
                    history.append("Removing Big Outlier " + bigOutlier + ".\n");
                bigOutlier.setOutliers();
                return bigOutlier.getFitted();
            }
        }

        Outlier badFitting = removeWhatIsDeformingTheFittingQuality(options, fitted, qualityOfFit, minDeformingFitQuality);

        if (badFitting != null) {
            if (history != null)
                history.append("Removing Bad Fitting Point " + badFitting + ".\n");
            badFitting.setOutliers();
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

    private Outlier removeWhatIsPushingTheAxis(List<Outlier> options, AstigmaticLensParams fitted, double howManyStds) {
        DecimalFormat formatter = new DecimalFormat("0");

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
        //System.out.println("Overall variance: " + overall.avg +" +/- "+ overall.std);
        for (i = 0; i < averagesDiffs.length; i++) {
            float avgDiff = averagesDiffs[i];
            //System.out.println("Average diff: " + i +" +/- "+ avgDiff );
            if (Math.abs(avgDiff - overall.avg) > howManyStds * overall.std) {
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

    private Outlier removeWhatIsPushingTheCyl(List<Outlier> options, AstigmaticLensParams fitted, double biggestPushSmallCyls, double biggestPushHighCyls) {
        double biggestDifferenceInCyl = biggestPushSmallCyls;

        if (Math.abs(fitted.getCylinder()) > 2.5)
            biggestDifferenceInCyl = biggestPushHighCyls;

        Outlier fittingValuesOfTheBiggestDifference = null;

        //System.out.println("Fitted: " + fitted);

        // Remove if one candidate pushing the cyl.
        for (Outlier candidate : options) {
            if (candidate.removed2nd == null) {
                // System.out.println("Option: " + candidate + " - Diff: " + (candidate.getFitted().getCylinder() - fitted.getCylinder()));
                // Has a significant decrease in cyl:
                // Remove the point that is pulling the curve to a higher cyl.
                if (candidate.getFitted().getCylinder() - fitted.getCylinder() > biggestDifferenceInCyl) {
                    //System.out.println("Option: " + candidate + " is bigger");
                    fittingValuesOfTheBiggestDifference = candidate;
                    biggestDifferenceInCyl = (candidate.getFitted().getCylinder() - fitted.getCylinder());
                }
            }
        }

        // Remove if two candidates are pushing the cyl.
        for (Outlier candidate : options) {
            if (candidate.removed2nd != null) {
                // System.out.println("Option: " + candidate + " - Diff: " + (candidate.getFitted().getCylinder() - fitted.getCylinder()));
                // Has a significant decrease in cyl:
                // Remove the point that is pulling the curve to a higher cyl.
                if (candidate.getFitted().getCylinder() - fitted.getCylinder() > biggestDifferenceInCyl + biggestPushSmallCyls) { // it has to be significantly better to remove 2 datapoints.
                    //System.out.println("Option: " + candidate + " is bigger");
                    fittingValuesOfTheBiggestDifference = candidate;
                    biggestDifferenceInCyl = (candidate.getFitted().getCylinder() - fitted.getCylinder());
                }
            }
        }

        return fittingValuesOfTheBiggestDifference;
    }

    private Outlier removeWhatIsFarFromTheCurve(List<Outlier> options, AstigmaticLensParams fitted, double howManyStds) {
        double furthestFromCurve = 0f;

        Outlier fittingValuesOfTheBiggestDifference = null;

        List<Double> differences = new ArrayList<>();

        // Remove if pushing the cyl.
        for (Outlier candidate : options) {
            if (candidate.removed2nd != null) continue;
            // Remove everybody that is > 1D off.
            double diff = Math.abs(candidate.removed.getPower() - candidate.getFitted().interpolate(candidate.removed.getAngle()));
            differences.add(diff);
        }

        // If removing any point is virtually the same, don't remove anything
        AvgStdPair overall = new CollectionUtils().avgSTD(differences);
        if (overall.std < 0.05) {
            return null;
        }

        //System.out.println("Overall variance: " + overall.avg +" +/- "+ overall.std);
        for (Outlier candidate : options) {
            if (candidate.removed2nd != null) continue;

            double diff = Math.abs(candidate.removed.getPower() - candidate.getFitted().interpolate(candidate.removed.getAngle()));

            //System.out.println("Overall variance for " + candidate + ": diff " + Math.abs(diff - overall.avg) + " compared to " +howManyStds*(overall.std) );

            if (diff > 0.50 && Math.abs(diff - overall.avg) > howManyStds * (overall.std) && diff > furthestFromCurve) {
                fittingValuesOfTheBiggestDifference = candidate;
                furthestFromCurve = diff;
            }
        }

        for (Outlier candidate : options) {
            if (candidate.removed2nd == null) continue;

            // only hit 2 outliers if it is much worse (Average of the two has same difference as one alone
            double diff = (Math.abs(candidate.removed.getPower() - candidate.getFitted().interpolate(candidate.removed.getAngle()))
                    + Math.abs(candidate.removed2nd.getPower() - candidate.getFitted().interpolate(candidate.removed2nd.getAngle()))) / 2;

            //System.out.println("Overall double variance for " + candidate + ": diff " + Math.abs(diff - overall.avg) + " compared to " +howManyStds*(overall.std) );

            if (diff > 0.50 && Math.abs(diff - overall.avg) > howManyStds * (overall.std) && diff > furthestFromCurve) {
                fittingValuesOfTheBiggestDifference = candidate;
                furthestFromCurve = diff;
            }
        }

        if (fittingValuesOfTheBiggestDifference != null) {
            // accepts up to -0.25 worse cyl.
            if (fittingValuesOfTheBiggestDifference.getFitted().getCylinder() < fitted.getCylinder() - 0.25) {
                return null;
            }
        }

        return fittingValuesOfTheBiggestDifference;
    }

    /** search for isolated points that makes the fit bad. */
    private Outlier removeWhatIsDeformingTheFittingQuality(List<Outlier> options, AstigmaticLensParams fitted, double currentFit, double minFitDifference) {
        Outlier fittingValuesOfTheBiggestDifference = null;

        List<Float> qualityOfFits = new ArrayList<Float>();

        //System.out.println("removeWhatIsDeformingTheFittingQuality");

        double bestFit = currentFit;

        // Remove if pushing the cyl.
        for (Outlier candidate : options) {
            if (candidate.removed2nd == null) {
                // System.out.println(candidate.removed.getAngle() + " as outlier has fitting of " + candidate.qualityOfFit + " compared with " + currentFit);

                qualityOfFits.add(candidate.qualityOfFit);
                // Remove everybody that is > 1D off.
                if (currentFit - candidate.qualityOfFit > minFitDifference && candidate.qualityOfFit < bestFit) {
                    fittingValuesOfTheBiggestDifference = candidate;
                    bestFit = candidate.qualityOfFit;
                }
            }
        }

        // Most be significantly better to opt for two outlier removal
        for (Outlier candidate : options) {
            if (candidate.removed2nd != null) {
                // System.out.println(candidate.removed.getAngle() + " and " + candidate.removed2nd.getAngle() + " as outliers have fitting of " + candidate.qualityOfFit + " compared with " + currentFit);

                if (currentFit - candidate.qualityOfFit > minFitDifference * 1.5 && candidate.qualityOfFit < bestFit - 0.2) {
                    fittingValuesOfTheBiggestDifference = candidate;
                    bestFit = candidate.qualityOfFit;
                }
            }
        }

        AvgStdPair pair = new CollectionUtils<Float>().avgSTD(qualityOfFits);

        //System.out.println("P " + pair.avg + " " + pair.std + " proposal " + fittingValuesOfTheBiggestDifference);

        // If removing any point is virtually the same, don't remove anything
        if (pair.std < 0.05) {
            return null;
        }

        return fittingValuesOfTheBiggestDifference;
    }

}
