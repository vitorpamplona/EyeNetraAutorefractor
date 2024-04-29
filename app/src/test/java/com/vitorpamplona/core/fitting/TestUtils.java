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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.FloatHashMap;

import org.junit.Assert;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestUtils {

    static DecimalFormat formatter = new DecimalFormat("+0.00;-0.00");
    static DecimalFormat formatter2 = new DecimalFormat("0.00");
    static FourierDomainAnalysis f = new FourierDomainAnalysis();

    public static void assertRoundingIsNotWorseThanFitting(Case c) {
        assertRoundingIsNotWorseThanFitting(c.netraRaw, c.dongs, c.netraResult, c.subjAdjustment, c.knownOutliers);
    }

    public static ArrayList<TestUtils.Case> select(ArrayList<TestUtils.Case> data, float minDiff, float maxDiff) {
        FourierDomainAnalysis f = new FourierDomainAnalysis();
        ArrayList<TestUtils.Case> ret = new ArrayList<TestUtils.Case>();
        for (TestUtils.Case c : data) {
            float diff = f.diff(c.netraResult, c.subjAdjustment);
            if (diff >= minDiff && diff < maxDiff) {
                ret.add(c);
            }
        }
        return ret;
    }

    public static void checkOutliers(FloatHashMap<MeridianPower> data, int... knownOutliers) {
        for (int angle : knownOutliers) {
            assertTrue("Angle " + angle + " is supposed to be an outlier", data.getClosestTo(angle).isOutlier());
        }

        for (Float angle : data.keySet()) {
            boolean shouldBeOutlier = false;
            for (int outlier : knownOutliers) {
                if (Math.abs(angle - outlier) < 5) {
                    shouldBeOutlier = true;
                }
            }

            if (shouldBeOutlier)
                assertTrue("Angle " + angle + " is supposed to be an outlier", data.getClosestTo(angle).isOutlier());
            else
                assertFalse("Angle " + angle + " is not supposed to be an outlier", data.getClosestTo(angle).isOutlier());
        }
    }

    public static void assertRoundingIsNotWorseThanFitting(Map<Integer, Float> myPoints, int dongs, AstigmaticLensParams result, AstigmaticLensParams subj, int[] knownOutliers) {
        StringBuilder builder = new StringBuilder();

        FloatHashMap<MeridianPower> data = map(myPoints);

        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());
        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), dongs, basicFit, builder);
        AstigmaticLensParams rounded = new BestRounding().round25(enhancedFit, data.values(), dongs, builder);

        System.out.println("Reason: " + builder.toString());

        System.out.println(debug(result, rounded, enhancedFit, basicFit, subj));

        //assertEquals("Algorithm is new? We were not able to reproduce past results", rounded, result);

        AstigmaticLensParams roundedOrBasicFit = closestToTheReal(rounded, basicFit, subj, 0.26); // 0.25D or worse to fail

        checkOutliers(data, knownOutliers);

        if (roundedOrBasicFit != null)
            assertTrue("NRounded method is worse than basic when compared to Subj",
                    roundedOrBasicFit == rounded);

        AstigmaticLensParams roundedOrOldResult = closestToTheReal(rounded, result, subj, 0.26); // 0.25D or worse to fail

        if (roundedOrOldResult != null)
            assertTrue("NRounded method is worse than original when compared to Subj",
                    roundedOrOldResult == rounded);
    }

    public static void processWhoIsBetter(String procName, Collection<Float> vectorDifferencesOriginal, Collection<Float> vectorDifferencesRecomputed) {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();

        CollectionUtils.AvgStdPair orig = utils.avgSTD(vectorDifferencesOriginal);
        CollectionUtils.AvgStdPair recomp = utils.avgSTD(vectorDifferencesRecomputed);

        System.out.println("Original Absolute " + procName + " Diff Average " + orig.toString() + "D");
        System.out.println("Computed Absolute " + procName + " Diff Average " + recomp.toString() + "D");

        assertTrue(procName + ": new method has to be equal or better", orig.avg >= recomp.avg);
    }

    public static void checkAmountUnder(String procName, Collection<Float> vectorDifferencesOriginal, Collection<Float> vectorDifferencesRecomputed, float value) {
        CollectionUtils<Float> utils = new CollectionUtils<Float>();

        float underOriginal = utils.percentUnder(vectorDifferencesOriginal, value);
        float underRecomputed = utils.percentUnder(vectorDifferencesRecomputed, value);

        if (underRecomputed - underOriginal >= 0)
            System.out.println("New Algo is " + formatter2.format((underRecomputed - underOriginal) * 100)
                    + "% better for " + procName + " under " + formatter2.format(value) + "D (new: "
                    + formatter2.format(underRecomputed * 100) + "% vs orig: " + formatter2.format(underOriginal * 100) + "%)");
        else
            System.out.println("New Algo is " + formatter2.format((underRecomputed - underOriginal) * 100)
                    + "% worse for " + procName + " under " + formatter2.format(value) + "D (new: "
                    + formatter2.format(underRecomputed * 100) + "% vs orig: " + formatter2.format(underOriginal * 100) + "%)");

        assertTrue(procName + ": new method has to be equal or better", underRecomputed >= underOriginal);
    }

    public static AstigmaticLensParams fit(Map<Integer, Float> myPoints) {
        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(map(myPoints).values());
        return basicFit;
    }

    public static AstigmaticLensParams enhance(Map<Integer, Float> myPoints, int dongs) {
        StringBuilder builder = new StringBuilder();
        FloatHashMap<MeridianPower> data = map(myPoints);
        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data.values());
        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data.values(), dongs, basicFit, builder);
        return enhancedFit;
    }

    public static AstigmaticLensParams compute(Map<Integer, Float> myPoints, int dongs) {
        return compute(map(myPoints).values(), dongs);
    }

    public static AstigmaticLensParams compute(Collection<MeridianPower> data, int dongs) {
        StringBuilder builder = new StringBuilder();
        AstigmaticLensParams basicFit = new SinusoidalFitting().curveFitting(data);
        AstigmaticLensParams enhancedFit = new OutlierRemoval().run(data, dongs, basicFit, builder);
        AstigmaticLensParams rounded = new BestRounding().round25(enhancedFit, data, dongs, builder);
        return rounded;
    }

    public static void assertEquals(String str, AstigmaticLensParams rounded, AstigmaticLensParams real) {
        Assert.assertEquals(str + ": Sphere accuracy", real.getSphere(), rounded.getSphere(), 0.25f);
        Assert.assertEquals(str + ": Cylinder accuracy", real.getCylinder(), rounded.getCylinder(), 0.50f);
        Assert.assertEquals(str + ": Axis accuracy", real.getAxis(), rounded.getAxis(), 5f);
    }

    public static String debug(AstigmaticLensParams currentNetraResult,
                               AstigmaticLensParams rounded,
                               AstigmaticLensParams enhancedFit,
                               AstigmaticLensParams basicFit,
                               AstigmaticLensParams real) {
        return
                "\n Subj Ref: \t" + desc(real, real) +
                        "\n CurrentN: \t" + desc(currentNetraResult, real) +
                        "\n NRounded: \t" + desc(rounded, real) +
                        "\n NEnhance: \t" + desc(enhancedFit, real) +
                        "\n N Fitted: \t" + desc(basicFit, real);


    }

    public static String desc(AstigmaticLensParams newResult, AstigmaticLensParams real) {
        float p1Real = f.diff(newResult, real);
        return newResult + " \t MSE:" + formatter.format(f.fourierMSE(newResult)) +
                " \t J0:" + formatter.format(f.fourierJ0(newResult)) +
                " \t J45:" + formatter.format(f.fourierJ45(newResult)) +
                " \t Diff:" + p1Real;
    }


    public static AstigmaticLensParams closestToTheReal(AstigmaticLensParams p1, AstigmaticLensParams p2, AstigmaticLensParams real, double p2Offset) {
        FourierDomainAnalysis fourier = new FourierDomainAnalysis();

        float p1Real = fourier.diff(p1, real);
        float p2Real = fourier.diff(p2, real);

        // If it's to far from the real, does it matter?
        if (p1Real > 2 && p2Real > 2)
            return null;

        return p1Real <= p2Real + p2Offset ? p1 : p2;
    }

    public static FloatHashMap<MeridianPower> map(Map<Integer, Float> myPoints) {
        FloatHashMap<MeridianPower> powers = new FloatHashMap<MeridianPower>();
        for (int i : myPoints.keySet()) {
            powers.put((float) i, new MeridianPower(i, myPoints.get(i)));
        }
        return powers;
    }

    public static float calculateOriginalVDDDiff(Case c) {
        return f.diff(c.netraResult, c.subjAdjustment);
    }

    public static float calculateFittedVDDDiff(Case c) {
        return f.diff(fit(c.netraRaw), c.subjAdjustment);
    }

    public static float calculateEnhancedVDDDiff(Case c) {
        return f.diff(enhance(c.netraRaw, c.dongs), c.subjAdjustment);
    }

    public static float calculateRecomputedVDDDiff(Case c) {
        return f.diff(compute(c.netraRaw, c.dongs), c.subjAdjustment);
    }

    public static float calculateOriginalSphEqDiff(Case c) {
        return c.netraResult.sphEquivalent() - c.subjAdjustment.sphEquivalent();
    }

    public static float calculateOriginalSphDiff(Case c) {
        return c.netraResult.getSphere() - c.subjAdjustment.getSphere();
    }

    public static float calculateOriginalCylDiff(Case c) {
        return c.netraResult.getCylinder() - c.subjAdjustment.getCylinder();
    }

    public static float calculateRecomputedSphEqDiff(Case c) {
        return compute(c.netraRaw, c.dongs).sphEquivalent() - c.subjAdjustment.sphEquivalent();
    }

    public static float calculateRecomputedSphDiff(Case c) {
        return compute(c.netraRaw, c.dongs).getSphere() - c.subjAdjustment.getSphere();
    }

    public static float calculateRecomputedCylDiff(Case c) {
        return compute(c.netraRaw, c.dongs).getCylinder() - c.subjAdjustment.getCylinder();
    }


    public static void assertVDDListOfCases(Collection<Case> data) {
        if (data.isEmpty()) return;

        List<Float> vectorDifferencesOriginal = new ArrayList<Float>();
        List<Float> vectorDifferencesFitted = new ArrayList<Float>();
        List<Float> vectorDifferencesEnhanced = new ArrayList<Float>();
        List<Float> vectorDifferencesRecomputed = new ArrayList<Float>();
        List<Integer> mobIds = new ArrayList<>();

        for (Case c : data) {
            mobIds.add(c.mobId);
            vectorDifferencesOriginal.add(TestUtils.calculateOriginalVDDDiff(c));
            vectorDifferencesFitted.add(TestUtils.calculateFittedVDDDiff(c));
            vectorDifferencesEnhanced.add(TestUtils.calculateEnhancedVDDDiff(c));
            vectorDifferencesRecomputed.add(TestUtils.calculateRecomputedVDDDiff(c));
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        CollectionUtils.AvgStdPair orig = utils.avgSTD(vectorDifferencesOriginal);
        CollectionUtils.AvgStdPair fitted = utils.avgSTD(vectorDifferencesFitted);
        CollectionUtils.AvgStdPair enhanced = utils.avgSTD(vectorDifferencesEnhanced);
        CollectionUtils.AvgStdPair recomp = utils.avgSTD(vectorDifferencesRecomputed);

        System.out.println("Original Absolute VDD Diff Average " + orig.toString() + "D");
        System.out.println("Rounded  Absolute VDD Diff Average " + recomp.toString() + "D");
        System.out.println("Enhanced Absolute VDD Diff Average " + enhanced.toString() + "D");
        System.out.println("Fitted   Absolute VDD Diff Average " + fitted.toString() + "D");

        //assertTrue( "VDD: new method has to be equal or better", orig.avg * orig.std >= recomp.avg * recomp.std);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 0.26f);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 0.51f);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 0.76f);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 1.01f);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 1.51f);
        TestUtils.checkAmountUnder("VDD", vectorDifferencesOriginal, vectorDifferencesRecomputed, 10.01f);
    }

    public static void assertSphEqListOfCases(Collection<Case> data) {
        if (data.isEmpty()) return;

        List<Float> sphEqDifferencesOriginal = new ArrayList<Float>();
        List<Float> sphEqDifferencesRecomputed = new ArrayList<Float>();

        for (Case c : data) {
            sphEqDifferencesOriginal.add(Math.abs(TestUtils.calculateOriginalSphEqDiff(c)));
            sphEqDifferencesRecomputed.add(Math.abs(TestUtils.calculateRecomputedSphEqDiff(c)));
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        CollectionUtils.AvgStdPair orig = utils.avgSTD(sphEqDifferencesOriginal);
        CollectionUtils.AvgStdPair recomp = utils.avgSTD(sphEqDifferencesRecomputed);

        System.out.println("Original SphEq Diff Average " + orig.toString() + "D");
        System.out.println("Rounded  SphEq Diff Average " + recomp.toString() + "D");

        //TestUtils.processWhoIsBetter("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 0.26f);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 0.51f);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 0.76f);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 1.01f);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 1.51f);
        TestUtils.checkAmountUnder("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed, 10.01f);
    }

    public static void assertSphListOfCases(Collection<Case> data) {
        if (data.isEmpty()) return;

        List<Float> sphDifferencesOriginal = new ArrayList<Float>();
        List<Float> sphDifferencesRecomputed = new ArrayList<Float>();

        for (Case c : data) {
            sphDifferencesOriginal.add(Math.abs(TestUtils.calculateOriginalSphDiff(c)));
            sphDifferencesRecomputed.add(Math.abs(TestUtils.calculateRecomputedSphDiff(c)));
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        CollectionUtils.AvgStdPair orig = utils.avgSTD(sphDifferencesOriginal);
        CollectionUtils.AvgStdPair recomp = utils.avgSTD(sphDifferencesRecomputed);

        System.out.println("Original Sph Diff Average " + orig.toString() + "D");
        System.out.println("Rounded  Sph Diff Average " + recomp.toString() + "D");

        //TestUtils.processWhoIsBetter("SphEq", sphEqDifferencesOriginal, sphEqDifferencesRecomputed);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 0.26f);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 0.51f);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 0.76f);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 1.01f);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 1.51f);
        TestUtils.checkAmountUnder("Sph", sphDifferencesOriginal, sphDifferencesRecomputed, 10.01f);
    }

    public static void assertCylListOfCases(Collection<Case> data) {
        if (data.isEmpty()) return;

        List<Float> cylDifferencesOriginal = new ArrayList<Float>();
        List<Float> cylDifferencesRecomputed = new ArrayList<Float>();

        for (Case c : data) {
            cylDifferencesOriginal.add(Math.abs(TestUtils.calculateOriginalCylDiff(c)));
            cylDifferencesRecomputed.add(Math.abs(TestUtils.calculateRecomputedCylDiff(c)));
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        CollectionUtils.AvgStdPair orig = utils.avgSTD(cylDifferencesOriginal);
        CollectionUtils.AvgStdPair recomp = utils.avgSTD(cylDifferencesRecomputed);

        System.out.println("Original cyl Diff Average " + orig.toString() + "D");
        System.out.println("Rounded  cyl Diff Average " + recomp.toString() + "D");

        //TestUtils.processWhoIsBetter("cylEq", cylEqDifferencesOriginal, cylEqDifferencesRecomputed);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 0.26f);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 0.51f);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 0.76f);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 1.01f);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 1.51f);
        TestUtils.checkAmountUnder("cyl", cylDifferencesOriginal, cylDifferencesRecomputed, 10.01f);
    }

    public static class Case {
        int mobId;
        int dongs;
        Map<Integer, Float> netraRaw;
        AstigmaticLensParams netraResult;
        AstigmaticLensParams subjAdjustment;
        int[] knownOutliers;

        public Case(int mobId, int dongs, Map<Integer, Float> netraRaw, AstigmaticLensParams netraResult, AstigmaticLensParams subjAdjustment, int... knownOutliers) {
            this.mobId = mobId;
            this.dongs = dongs;
            this.netraRaw = netraRaw;
            this.netraResult = netraResult;
            this.subjAdjustment = subjAdjustment;
            this.knownOutliers = knownOutliers;
        }
    }

    public static Collection<Case> onlyGoodVDDs(Collection<Case> data) {
        List<Case> result = new ArrayList<Case>();
        for (Case c : data) {
            if (TestUtils.calculateOriginalVDDDiff(c) < 1) {
                result.add(c);
            }
        }
        return result;
    }

    public static void assertHorribleFit(Case c) {
        FloatHashMap<MeridianPower> raw = map(c.netraRaw);

        AstigmaticLensParams newResult = compute(raw.values(), c.dongs);

        //checkOutliers(raw, c.knownOutliers);

        double fit = new QualityOfFit().compute(raw.values(), c.dongs, newResult);

        assertTrue("Fit should worse than 0.90 (bad): " + fit, fit > 0.90);
    }

    public static void assertBadFit(Case c) {
        FloatHashMap<MeridianPower> raw = map(c.netraRaw);

        AstigmaticLensParams newResult = compute(raw.values(), c.dongs);

        //checkOutliers(raw, c.knownOutliers);

        double fit = new QualityOfFit().compute(raw.values(), c.dongs, newResult);

        assertTrue("Fit should worse than 0.90 (bad): " + fit, fit > 0.60);
    }

    public static void testQualityOfFit(Collection<Case> data) {
        if (data.size() <= 1) return;

        List<Double> oldQualityOfFit = new ArrayList<Double>();
        List<Double> newQualityOfFit = new ArrayList<Double>();
        List<Double> vectorDifferencesOriginal = new ArrayList<Double>();
        List<Integer> mobIds = new ArrayList<>();

        int badOldCases = 0;
        int badNewCases = 0;

        for (Case c : data) {
            mobIds.add(c.mobId);
            vectorDifferencesOriginal.add((double) TestUtils.calculateOriginalVDDDiff(c));


            double oldFit = new QualityOfFit().compute(map(c.netraRaw).values(), 0, c.netraResult);
            oldQualityOfFit.add(oldFit);

            double newFit = new QualityOfFit().compute(map(c.netraRaw).values(), c.dongs, c.netraResult);
            newQualityOfFit.add(newFit);

            if (c.dongs > 0) {
                //System.out.println("Fit " + oldFit + "\t" + newFit + "\t" + vectorDifferencesOriginal.get(vectorDifferencesOriginal.size()-1));
            }

            if (oldFit > 0.58) {
                badOldCases++;
            }
            if (newFit > 0.58) {
                badNewCases++;
            }
        }

        double corrOld = new CollectionUtils<Double>().correlation(oldQualityOfFit, vectorDifferencesOriginal);
        double corrNew = new CollectionUtils<Double>().correlation(newQualityOfFit, vectorDifferencesOriginal);


        System.out.println("Bad cases (new " + NumberFormat.getPercentInstance().format(badNewCases / (float) data.size())
                + ", old " + NumberFormat.getPercentInstance().format(badOldCases / (float) data.size()) + ")");
        System.out.println("Correlations New must be better than old (new " + corrNew + ", old " + corrOld + ")");

        assertTrue("Correlations New must be better than old (new " + corrNew + ", old " + corrOld + "): ", corrNew + 0.04 >= corrOld);
    }

    public static void assertRepeatability(ArrayList<TestUtils.Case> cases) {
        List<Float> vectorDifferencesOriginalSph = new ArrayList<Float>();
        List<Float> vectorDifferencesRecomputedSph = new ArrayList<Float>();
        List<Float> vectorDifferencesOriginalCyl = new ArrayList<Float>();
        List<Float> vectorDifferencesRecomputedCyl = new ArrayList<Float>();
        List<Float> vectorDifferencesOriginalAxis = new ArrayList<Float>();
        List<Float> vectorDifferencesRecomputedAxis = new ArrayList<Float>();
        List<Float> vectorDifferencesOriginalSphEq = new ArrayList<Float>();
        List<Float> vectorDifferencesRecomputedSphEq = new ArrayList<Float>();

        for (TestUtils.Case c : cases) {
            AstigmaticLensParams recomp = TestUtils.compute(c.netraRaw, c.dongs);
            System.out.println("Cases X: " + recomp);

            vectorDifferencesOriginalSph.add(c.netraResult.getSphere());
            vectorDifferencesRecomputedSph.add(recomp.getSphere());

            vectorDifferencesOriginalCyl.add(c.netraResult.getCylinder());
            vectorDifferencesRecomputedCyl.add(recomp.getCylinder());

            if (Math.abs(c.netraResult.getCylinder()) > 0.05) {
                vectorDifferencesOriginalAxis.add(c.netraResult.getAxis());
            }
            if (Math.abs(recomp.getCylinder()) > 0.05) {
                vectorDifferencesRecomputedAxis.add(recomp.getAxis());
            }

            vectorDifferencesOriginalSphEq.add(c.netraResult.getSphere() + c.netraResult.getCylinder() / 2);
            vectorDifferencesRecomputedSphEq.add(recomp.getSphere() + recomp.getCylinder() / 2);
        }

        CollectionUtils<Float> utils = new CollectionUtils<Float>();
        CollectionUtils.AvgStdPair origSph = utils.avgSTD(vectorDifferencesOriginalSph);
        CollectionUtils.AvgStdPair recompSph = utils.avgSTD(vectorDifferencesRecomputedSph);

        CollectionUtils.AvgStdPair origCyl = utils.avgSTD(vectorDifferencesOriginalCyl);
        CollectionUtils.AvgStdPair recompCyl = utils.avgSTD(vectorDifferencesRecomputedCyl);

        CollectionUtils.AvgStdPair origAxis = utils.avgSTD(vectorDifferencesOriginalAxis);
        CollectionUtils.AvgStdPair recompAxis = utils.avgSTD(vectorDifferencesRecomputedAxis);

        CollectionUtils.AvgStdPair origSphEq = utils.avgSTD(vectorDifferencesOriginalSphEq);
        CollectionUtils.AvgStdPair recompSphEq = utils.avgSTD(vectorDifferencesRecomputedSphEq);

        System.out.println("Original Sph Average " + origSph.toString() + "D");
        System.out.println("Rounded  Sph Average " + recompSph.toString() + "D");
        System.out.println("Original Cyl Average " + origCyl.toString() + "D");
        System.out.println("Rounded  Cyl Average " + recompCyl.toString() + "D");
        System.out.println("Original Axis Average " + origAxis.toString() + "D");
        System.out.println("Rounded  Axis Average " + recompAxis.toString() + "D");
        System.out.println("Original SphEq Average " + origSphEq.toString() + "D");
        System.out.println("Rounded  SphEq Average " + recompSphEq.toString() + "D");

        assertTrue(recompSph.std < origSph.std + 0.05);     // Better or not much worse
        assertTrue(recompCyl.std < origCyl.std + 0.05);     // Better or not much worse
        assertTrue(recompAxis.std < origAxis.std + 4);      // Better or not much worse
        assertTrue(recompSphEq.std < origSphEq.std + 0.05); // Better or not much worse

    }

}




