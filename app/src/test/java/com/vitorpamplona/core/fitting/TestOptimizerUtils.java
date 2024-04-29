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
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.FloatHashMap;
import com.vitorpamplona.utils.CascadingFors;
import com.vitorpamplona.utils.ConsoleProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestOptimizerUtils {
    static DecimalFormat formatter = new DecimalFormat("+0.00;-0.00");

    static FourierDomainAnalysis f = new FourierDomainAnalysis();

    public static FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
    }

    public static String debug(int mobId, AstigmaticLensParams currentNetraResult,
                               AstigmaticLensParams rounded,
                               AstigmaticLensParams enhancedFit,
                               AstigmaticLensParams basicFit,
                               AstigmaticLensParams real) {
        return " Mob   Id: \t" + mobId +
                "\n Subj Ref: \t" + desc(real, real) +
                "\n CurrentN: \t" + desc(currentNetraResult, real) +
                "\n NRounded: \t" + desc(rounded, real) +
                "\n NEnhance: \t" + desc(enhancedFit, real) +
                "\n N Fitted: \t" + desc(basicFit, real) + "\n\n";


    }

    public static String desc(AstigmaticLensParams newResult, AstigmaticLensParams real) {
        float p1Real = f.diff(newResult, real);
        return newResult + " \t MSE:" + formatter.format(f.fourierMSE(newResult)) +
                " \t J0:" + formatter.format(f.fourierJ0(newResult)) +
                " \t J45:" + formatter.format(f.fourierJ45(newResult)) +
                " \t Diff:" + p1Real;
    }

    public static void optimizeRoundingParameters(Collection<TestUtils.Case> data) {
        // Base Calculations
        List<Float> vectorDifferencesOriginal = new ArrayList<Float>();
        List<Float> sphEqDifferencesOriginal = new ArrayList<Float>();
        List<Float> absSphEqDifferencesOriginal = new ArrayList<Float>();

        List<Float> vectorDifferencesCurrentDefaults = new ArrayList<Float>();
        List<Float> sphEqDifferencesCurrentDefaults = new ArrayList<Float>();
        List<Float> absSphEqDifferencesCurrentDefaults = new ArrayList<Float>();

        List<Float> vectorDifferencesJustDefaultFitting = new ArrayList<Float>();
        List<Float> sphEqDifferencesJustDefaultFitting = new ArrayList<Float>();
        List<Float> absSphEqDifferencesJustDefaultFitting = new ArrayList<Float>();

        List<Integer> mobIds = new ArrayList<>();
        Map<TestUtils.Case, AstigmaticLensParams> basicFits = new HashMap<TestUtils.Case, AstigmaticLensParams>();
        Map<TestUtils.Case, List<Outlier>> options = new HashMap<TestUtils.Case, List<Outlier>>();

        Map<TestUtils.Case, AstigmaticLensParams> enhancedFits = new HashMap<TestUtils.Case, AstigmaticLensParams>();
        for (TestUtils.Case c : data) {
            mobIds.add(c.mobId);

            vectorDifferencesOriginal.add(TestUtils.calculateOriginalVDDDiff(c));
            sphEqDifferencesOriginal.add(TestUtils.calculateOriginalSphEqDiff(c));
            absSphEqDifferencesOriginal.add(Math.abs(TestUtils.calculateOriginalSphEqDiff(c)));

            AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(c.netraRaw).values());

            // CACHE
            basicFits.put(c, basicFit);

            options.put(c, new OutlierRemoval().calculateOptions(map(c.netraRaw).values(), c.dongs, basicFit));

            AstigmaticLensParams enhancedFit = new OutlierRemoval().run(options.get(c), basicFit, new QualityOfFit().compute(map(c.netraRaw).values(), c.dongs, basicFit), null);
            enhancedFits.put(c, enhancedFit);

            AstigmaticLensParams rounded = new BestRounding().round25(enhancedFit, map(c.netraRaw).values(), c.dongs, null);

            vectorDifferencesCurrentDefaults.add(f.diff(rounded, c.subjAdjustment));
            sphEqDifferencesCurrentDefaults.add(rounded.sphEquivalent() - c.subjAdjustment.sphEquivalent());
            absSphEqDifferencesCurrentDefaults.add(Math.abs(rounded.sphEquivalent() - c.subjAdjustment.sphEquivalent()));

            vectorDifferencesJustDefaultFitting.add(f.diff(basicFit, c.subjAdjustment));
            sphEqDifferencesJustDefaultFitting.add(basicFit.sphEquivalent() - c.subjAdjustment.sphEquivalent());
            absSphEqDifferencesJustDefaultFitting.add(Math.abs(basicFit.sphEquivalent() - c.subjAdjustment.sphEquivalent()));

            /*
            if (vectorDifferencesCurrentDefaults.get(vectorDifferencesCurrentDefaults.size()-1) > 0.10
                    && (Math.abs(c.netraResult.getSphere() - rounded.getSphere()) > 0.26
                        || Math.abs(c.netraResult.getCylinder() - rounded.getCylinder()) > 0.26 ))
                System.out.println(debug(c.mobId, c.netraResult, rounded, enhancedFit, basicFit, c.subjAdjustment));
            else
                System.out.println("\n");
                */
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();

        Mins minVDD = new Mins();
        Mins minSphEq = new Mins();
        Mins minAbsSphEq = new Mins();

        final CollectionUtils.AvgStdPair[] minRecompVDD = {new CollectionUtils.AvgStdPair(999, 999)};
        final CollectionUtils.AvgStdPair[] minRecompSphEq = {new CollectionUtils.AvgStdPair(999, 999)};
        final CollectionUtils.AvgStdPair[] minRecompABSSphEq = {new CollectionUtils.AvgStdPair(999, 999)};

        // Defaults
        //public static final double IGNORE_CYL_IF_UNDER = 0.35f;
        //public static final double REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN = 0.06f;
        //public static final double REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN = 0.25f;
        //public static final double REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN = 0.50f;
        CascadingFors.For ignoreCylIfUnderFor = new CascadingFors.For(0.35, 0.35, 0.1);
        CascadingFors.For reduceCylBy025 = new CascadingFors.For(0.06, 0.06, 0.01);
        CascadingFors.For reduceCylBy050 = new CascadingFors.For(0.25, 0.25, 0.05);
        CascadingFors.For reduceCylByHalfAfter = new CascadingFors.For(0.25, 0.50, 0.25);

        //Defaults
        //public static final double BIGGEST_PUSH_SMALL_CYLS = 0.40f;
        //public static final double BIGGEST_PUSH_LARGE_CYLS = 0.70f;
        //public static final double MIN_STD_DEV_MULTIPLIER_TO_PUSH_AXIS = 2.5;
        //public static final double DIFFERENCE_TO_CONSIDER_AN_OUTLIER = 1.25f;
        //public static final double MIN_DEFORMING_FIT_QUALITY = 0.42f;
        CascadingFors.For biggestPushSmallCyls = new CascadingFors.For(0.40, 0.40, 0.10);
        CascadingFors.For biggestPushLargeCyls = new CascadingFors.For(0.70, 0.70, 0.10);
        CascadingFors.For minStdDevMultiplierToPushAxis = new CascadingFors.For(2.5, 2.5, 0.50);
        CascadingFors.For differenceToConsiderAnOutlier = new CascadingFors.For(1.25, 1.25, 0.25);
        CascadingFors.For minDeformingFitQuality = new CascadingFors.For(0.42, 0.42, 0.02);

        CascadingFors fs = new CascadingFors(
                ignoreCylIfUnderFor, reduceCylBy025, reduceCylBy050, reduceCylByHalfAfter,
                biggestPushSmallCyls, biggestPushLargeCyls, minStdDevMultiplierToPushAxis,
                differenceToConsiderAnOutlier, minDeformingFitQuality);

        Collection<TestUtils.Case> finalData = data;
        fs.run(() -> {
            List<Float> vectorDifferencesRecomputed = new ArrayList<Float>();
            List<Float> sphEqDifferencesRecomputed = new ArrayList<Float>();
            List<Float> absSphEqDifferencesRecomputed = new ArrayList<Float>();
            for (TestUtils.Case c : finalData) {
                AstigmaticLensParams enhancedFit = new OutlierRemoval().run(
                        options.get(c), basicFits.get(c), new QualityOfFit().compute(map(c.netraRaw).values(), c.dongs, basicFits.get(c)), null,
                        biggestPushSmallCyls.value, biggestPushLargeCyls.value, minStdDevMultiplierToPushAxis.value,
                        differenceToConsiderAnOutlier.value, minDeformingFitQuality.value);

                AstigmaticLensParams rounded = new BestRounding().round(enhancedFit, map(c.netraRaw).values(), c.dongs,
                        null, 0.25f, 5,
                        ignoreCylIfUnderFor.value, reduceCylBy025.value, reduceCylBy050.value, reduceCylByHalfAfter.value);

                vectorDifferencesRecomputed.add(f.diff(rounded, c.subjAdjustment));
                sphEqDifferencesRecomputed.add(rounded.sphEquivalent() - c.subjAdjustment.sphEquivalent());
                absSphEqDifferencesRecomputed.add(Math.abs(rounded.sphEquivalent() - c.subjAdjustment.sphEquivalent()));
            }
            CollectionUtils.AvgStdPair recompVDD = utils.avgSTD(vectorDifferencesRecomputed);
            CollectionUtils.AvgStdPair recompSphEq = utils.avgSTD(sphEqDifferencesRecomputed);
            CollectionUtils.AvgStdPair recompABSSphEq = utils.avgSTD(absSphEqDifferencesRecomputed);

            if (recompVDD.avg * recompVDD.std < (minRecompVDD[0].avg * minRecompVDD[0].std)) {
                minRecompVDD[0] = recompVDD;
                minVDD.minIgnoreCylIfUnder = ignoreCylIfUnderFor.value;
                minVDD.minReduceCylByHalfAfter = reduceCylByHalfAfter.value;
                minVDD.minReduceCylBy050 = reduceCylBy050.value;
                minVDD.minReduceCylBy025 = reduceCylBy025.value;
                minVDD.minBiggestPushSmallCyls = biggestPushSmallCyls.value;
                minVDD.minBiggestPushLargeCyls = biggestPushLargeCyls.value;
                minVDD.minMinStdDevMultiplierToPushAxis = minStdDevMultiplierToPushAxis.value;
                minVDD.minDifferenceToConsiderAnOutlier = differenceToConsiderAnOutlier.value;
                minVDD.minMinDeformingFitQuality = minDeformingFitQuality.value;
            }

            if (Math.abs(recompSphEq.avg) * recompSphEq.std < (Math.abs(minRecompSphEq[0].avg) * minRecompSphEq[0].std)) {
                minRecompSphEq[0] = recompSphEq;
                minSphEq.minIgnoreCylIfUnder = ignoreCylIfUnderFor.value;
                minSphEq.minReduceCylByHalfAfter = reduceCylByHalfAfter.value;
                minSphEq.minReduceCylBy050 = reduceCylBy050.value;
                minSphEq.minReduceCylBy025 = reduceCylBy025.value;
                minSphEq.minBiggestPushSmallCyls = biggestPushSmallCyls.value;
                minSphEq.minBiggestPushLargeCyls = biggestPushLargeCyls.value;
                minSphEq.minMinStdDevMultiplierToPushAxis = minStdDevMultiplierToPushAxis.value;
                minSphEq.minDifferenceToConsiderAnOutlier = differenceToConsiderAnOutlier.value;
                minSphEq.minMinDeformingFitQuality = minDeformingFitQuality.value;
            }

            if (recompABSSphEq.avg * recompABSSphEq.std < (minRecompABSSphEq[0].avg * minRecompABSSphEq[0].std)) {
                minRecompABSSphEq[0] = recompABSSphEq;
                minAbsSphEq.minIgnoreCylIfUnder = ignoreCylIfUnderFor.value;
                minAbsSphEq.minReduceCylByHalfAfter = reduceCylByHalfAfter.value;
                minAbsSphEq.minReduceCylBy050 = reduceCylBy050.value;
                minAbsSphEq.minReduceCylBy025 = reduceCylBy025.value;
                minAbsSphEq.minBiggestPushSmallCyls = biggestPushSmallCyls.value;
                minAbsSphEq.minBiggestPushLargeCyls = biggestPushLargeCyls.value;
                minAbsSphEq.minMinStdDevMultiplierToPushAxis = minStdDevMultiplierToPushAxis.value;
                minAbsSphEq.minDifferenceToConsiderAnOutlier = differenceToConsiderAnOutlier.value;
                minAbsSphEq.minMinDeformingFitQuality = minDeformingFitQuality.value;
            }

            ConsoleProgressBar.printProgBar(fs.progress());
        });

        System.out.println("");
        System.out.println("OrigVDD      " + utils.avgSTD(vectorDifferencesOriginal).toString() + "D");
        System.out.println("CurrVDD      " + utils.avgSTD(vectorDifferencesCurrentDefaults).toString() + "D");
        System.out.println("BFitVDD      " + utils.avgSTD(vectorDifferencesJustDefaultFitting).toString() + "D");
        System.out.println("MinVDD       " + minRecompVDD[0].toString() + "D" + " with " + minVDD.toString());
        System.out.println("");
        System.out.println("OrigSphEq    " + utils.avgSTD(sphEqDifferencesOriginal).toString() + "D");
        System.out.println("CurrSphEq    " + utils.avgSTD(sphEqDifferencesCurrentDefaults).toString() + "D");
        System.out.println("BFitSphEq    " + utils.avgSTD(sphEqDifferencesJustDefaultFitting).toString() + "D");
        System.out.println("MinSphEq     " + minRecompSphEq[0].toString() + "D" + " with " + minSphEq.toString());
        System.out.println("");
        System.out.println("OrigAbsSphEq " + utils.avgSTD(absSphEqDifferencesOriginal).toString() + "D");
        System.out.println("CurrAbsSphEq " + utils.avgSTD(absSphEqDifferencesCurrentDefaults).toString() + "D");
        System.out.println("BFitAbsSphEq " + utils.avgSTD(absSphEqDifferencesJustDefaultFitting).toString() + "D");
        System.out.println("MinAbsSphEq  " + minRecompABSSphEq[0].toString() + "D" + " with " + minAbsSphEq.toString());
        System.out.println("");
    }

    public static class Mins {
        double minIgnoreCylIfUnder = 0;
        double minReduceCylByHalfAfter = 0;
        double minReduceCylBy050 = 0;
        double minReduceCylBy025 = 0;
        double minBiggestPushSmallCyls = 0;
        double minBiggestPushLargeCyls = 0;
        double minMinStdDevMultiplierToPushAxis = 0;
        double minDifferenceToConsiderAnOutlier = 0;
        double minMinDeformingFitQuality = 0;

        static DecimalFormat formatter2 = new DecimalFormat(" 0.00;-0.00");

        public String toString() {
            return formatter2.format(minIgnoreCylIfUnder) + "\t" +
                    formatter2.format(minReduceCylBy025) + "\t" +
                    formatter2.format(minReduceCylBy050) + "\t" +
                    formatter2.format(minReduceCylByHalfAfter) + "\t" +
                    formatter2.format(minBiggestPushSmallCyls) + "\t" +
                    formatter2.format(minBiggestPushLargeCyls) + "\t" +
                    formatter2.format(minMinStdDevMultiplierToPushAxis) + "\t" +
                    formatter2.format(minDifferenceToConsiderAnOutlier) + "\t" +
                    formatter2.format(minMinDeformingFitQuality);
        }
    }

}
