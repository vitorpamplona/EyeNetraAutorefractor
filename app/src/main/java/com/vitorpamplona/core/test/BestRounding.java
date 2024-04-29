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
package com.vitorpamplona.core.test;

import com.vitorpamplona.core.fitting.FittingStatistics;
import com.vitorpamplona.core.fitting.QualityOfFit;
import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.RefRounding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BestRounding {

    public static final double IGNORE_CYL_IF_UNDER = 0.45f;

    // ABSOLUTE ERRORS
    //public static final double REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN = 0.15f;
    //public static final double REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN = 0.30f;
    //public static final double REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN = 0.50f;

    // QUALITY OF FIT
    public static final double REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN = 0.62f;
    public static final double REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN = 0.90f;
    public static final double REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN = 1.25f;

    // SUM OF ERRORS
    //ublic static final double IGNORE_CYL_IF_UNDER = 0.15f;
    //public static final double REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN = 0.50f;
    //public static final double REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN = 1.00f;
    //public static final double REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN = 1.75f; // cannot be less than 70

    public float squaredError(AstigmaticLensParams p, Collection<MeridianPower> angles) {
        float sum = 0;
        for (MeridianPower power : angles) {
            if (power.isOutlier()) continue;
            sum += (float) Math.pow(p.interpolate(power.getAngle()) - power.getPower(), 2);
        }
        return sum;
    }

    public AstigmaticLensParams roundAstigmatism(AstigmaticLensParams fitted, Collection<MeridianPower> angles, float roundingStep, float roundingStepAxis) {
        List<AstigmaticLensParams> options = new ArrayList<AstigmaticLensParams>();

        float newAxisCeil = (float) (Math.ceil(fitted.getAxis() / roundingStepAxis) * roundingStepAxis);
        float newAxisFloor = newAxisCeil - roundingStepAxis;

        float newCylCeil = RefRounding.ceilTo(fitted.getCylinder(), roundingStep);
        float newCylFloor = newCylCeil - roundingStep;

        float newSphCeil = RefRounding.ceilTo(fitted.getSphere(), roundingStep);
        float newSphFloor = newSphCeil - roundingStep;

        options.add(new AstigmaticLensParams(newSphCeil, newCylCeil, newAxisCeil));
        options.add(new AstigmaticLensParams(newSphFloor, newCylFloor, newAxisCeil));
        options.add(new AstigmaticLensParams(newSphCeil, newCylFloor, newAxisCeil));
        options.add(new AstigmaticLensParams(newSphFloor, newCylCeil, newAxisCeil));

        options.add(new AstigmaticLensParams(newSphCeil, newCylCeil, newAxisFloor));
        options.add(new AstigmaticLensParams(newSphFloor, newCylFloor, newAxisFloor));
        options.add(new AstigmaticLensParams(newSphCeil, newCylFloor, newAxisFloor));
        options.add(new AstigmaticLensParams(newSphFloor, newCylCeil, newAxisFloor));

        List<Integer> errors = new ArrayList<Integer>();

        //System.out.println("Original: " + fitted);
        for (AstigmaticLensParams params : options) {
            int error = (int) (squaredError(params, angles) * 100);
            //System.out.println("Rounding option: " + params + " with fitting error: " + error);
            errors.add(error);
        }

        CollectionUtils<Integer>.MinMaxPair pair = new CollectionUtils<Integer>().minMax(errors);
        int index = errors.indexOf(Math.round(pair.min));
        return options.get(index);
    }

    public AstigmaticLensParams roundSphere(AstigmaticLensParams fitted, Collection<MeridianPower> angles, float roundingStep) {
        AstigmaticLensParams p1 = new AstigmaticLensParams(RefRounding.ceilTo(fitted.getSphere(), roundingStep), fitted.getCylinder(), (int) fitted.getAxis());
        AstigmaticLensParams p2 = new AstigmaticLensParams(RefRounding.ceilTo(fitted.getSphere(), roundingStep) - roundingStep, fitted.getCylinder(), (int) fitted.getAxis());
        AstigmaticLensParams p3 = new AstigmaticLensParams(RefRounding.ceilTo(fitted.getSphere(), roundingStep) + roundingStep, fitted.getCylinder(), (int) fitted.getAxis());

        float p1Error = squaredError(p1, angles);
        float p2Error = squaredError(p2, angles);
        float p3Error = squaredError(p3, angles);

        int option = 1;
        float pivot = p1Error;
        if (p2Error - 0.05 < pivot) { // P2 is Preferred
            pivot = p2Error;
            option = 2;
        }
        if (p3Error < pivot) {
            pivot = p3Error;
            option = 3;
        }

        switch (option) {
            case 1:
                return p1;
            case 2:
                return p2;
            case 3:
                return p3;
        }

        return null;
    }

    public float average(Collection<MeridianPower> measuredMeridians) {
        float sum = 0;
        int cont = 0;
        for (MeridianPower f : measuredMeridians) {
            if (!f.isOutlier()) {
                sum += f.getPower();
                cont++;
            }
        }

        return sum / cont;
    }

    public AstigmaticLensParams round25(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails, StringBuilder details) {
        return round(fitted, angles, fails, details, 0.25f, 5);
    }

    public AstigmaticLensParams round125(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails, StringBuilder details) {
        return round(fitted, angles, fails, details, 0.125f, 3);
    }

    public AstigmaticLensParams round0625(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails, StringBuilder details) {
        return round(fitted, angles, fails, details, 0.0625f, 1);
    }

    public float averagePower(Collection<MeridianPower> powers) {
        float sum = 0;
        int cont = 0;
        for (MeridianPower power : powers) {
            if (power.isOutlier()) continue;
            sum += power.getPower();
            cont++;
        }
        if (cont == 0) {
            return 0;
        }

        return sum / cont;
    }

    public AstigmaticLensParams round(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails, StringBuilder details, float roundingSize, float roundingSizeAxis) {
        return round(fitted, angles, fails, details, roundingSize, roundingSizeAxis, IGNORE_CYL_IF_UNDER,
                REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN, REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN,
                REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN);
    }

    public AstigmaticLensParams round(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails, StringBuilder details,
                                      float roundingSize, float roundingSizeAxis, double ignoreCylCut, double cut1, double cut2, double cut3) {
        if (fitted == null) return null;

        if (details != null)
            details.append("Your best correction is " + fitted.toString());

        // < 0.4 is not enough resolution to prescribe.
        if (Math.abs(fitted.getCylinder()) < ignoreCylCut) {
            //System.out.println("Low cyl");

            float newSphere = averagePower(angles);
            AstigmaticLensParams rounded = roundSphere(new AstigmaticLensParams(newSphere, 0, 0), angles, roundingSize);

            //System.out.println(newSphere);

            if (details != null)
                details.append(", but since your astigmatism is very low, you may not need to wear it. Since lenses come in steps of 0.25D, the closest available option is " + rounded.toString());

            return rounded;
        } else {
            AstigmaticLensParams rounded = roundAstigmatism(fitted, angles, roundingSize, roundingSizeAxis);

            softenCylinder(fitted, rounded, angles, fails, cut1, cut2, cut3);

            if (details != null)
                details.append(". Since lenses come in steps of 0.25D, the closest available option is " + rounded.toString());

            return rounded;
        }
    }

    /**
     * This method adjusts the fitted parameters to match the rounding mechanism that prefers the stronger sph and weaker cyl.
     * @param fitted
     */
    public AstigmaticLensParams fakeRounding(AstigmaticLensParams fitted) {
        float newCylCeil = fitted.getCylinder() < -0.25f ? fitted.getCylinder() + 0.25f : 0;
        float newSphFloor = fitted.getSphere() - 0.25f;

        return new AstigmaticLensParams(newSphFloor, newCylCeil, fitted.getAxis());
    }

    public AstigmaticLensParams softenCylinder(AstigmaticLensParams fitted, Collection<MeridianPower> angles, int fails) {
        AstigmaticLensParams fakeRounded = fakeRounding(fitted);
        softenCylinder(fitted, fakeRounded, angles, fails);
        fakeRounded.putInNegativeCilinder();
        return fakeRounded;
    }

    public void softenCylinder(AstigmaticLensParams fitted, AstigmaticLensParams rounded, Collection<MeridianPower> angles, int fails) {
        softenCylinder(fitted, rounded, angles, fails, REDUCE_CYL_BY_0_25_WITH_FIT_MORE_THAN, REDUCE_CYL_BY_0_50_WITH_FIT_MORE_THAN, REDUCE_CYL_BY_HALF_WITH_FIT_MORE_THAN);
    }

    public int countOutliers(Collection<MeridianPower> angles) {
        int cont = 0;
        for (MeridianPower m : angles) {
            cont += m.isOutlier() ? 1 : 0;
        }
        return cont;
    }

    public void softenCylinder(AstigmaticLensParams fitted, AstigmaticLensParams rounded, Collection<MeridianPower> angles, int fails,
                               double cut1, double cut2, double cut3) {
        // Gotta be average because of the dual mode 16 and 34 meridians
        double avgSquaredDifference = new FittingStatistics(fitted, angles).summedSquaredDifference / angles.size();
        double qualityOfFit = new QualityOfFit().compute(angles, fails, fitted);

        //System.out.println("Check to Soft Cylinder " + avgSquaredDifference + " fit " + qualityOfFit + " " + fails + " outliers " + countOutliers(angles));

        // If the fit is dead on, do not change.
        if (qualityOfFit > 0.60) {
            // Try to minimize errors
            if (Math.abs(fitted.getCylinder()) > 0.249) {

                // forcing decrease on cyl for everybody.
                rounded.setCylinder(rounded.getCylinder() + 0.25f);

                // Hyperopes around zero cut by half
                if (fitted.getSphere() > 0 && // Hyperopes
                        Math.abs(fitted.getCylinder()) > 0.50 && // With astigmatism
                        (fitted.getSphere() + fitted.getCylinder()) < 0.25) { // And zero is inside the range.
                    //System.out.println("Doesn't need glasses: Cutting cyl by Half (" + avgSquaredDifference + ")");
                    rounded.setCylinder(RefRounding.roundTo(rounded.getCylinder() / 2.0f, 0.25f));
                    rounded.setSphere(rounded.getSphere() + RefRounding.roundTo(rounded.getCylinder() / 2.0f, 0.25f));
                } else if (qualityOfFit > cut3) { // The highest the error, the more we decrease on Cyl.
                    //System.out.println("Softening Cylinder: Cutting cyl by half (" + avgSquaredDifference + ")");
                    rounded.setCylinder(RefRounding.roundTo(rounded.getCylinder() / 2.0f, 0.25f));
                    rounded.setSphere(rounded.getSphere() + RefRounding.roundTo(rounded.getCylinder() / 2.0f, 0.25f));
                } else if (qualityOfFit > cut2) {
                    if (Math.abs(rounded.getCylinder()) > 0.55) {
                        //System.out.println("Softening Cylinder: reducing 0.50D cyl and increasing 0.25 sph (" + avgSquaredDifference + ")");
                        rounded.setCylinder(rounded.getCylinder() + 0.50f);
                        rounded.setSphere(rounded.getSphere() - 0.25f);
                    } else if (Math.abs(rounded.getCylinder()) > 0.24) {
                        //System.out.println("Softening Cylinder 1: reducing 0.25D cyl (" + avgSquaredDifference + ")");
                        rounded.setCylinder(rounded.getCylinder() + 0.25f);
                    }
                } else if (qualityOfFit > cut1 && Math.abs(rounded.getCylinder()) > 0.26) {
                    //System.out.println("Softening Cylinder 2: reducing 0.25D cyl (" + avgSquaredDifference+ ")");
                    rounded.setCylinder(rounded.getCylinder() + 0.25f);
                }
            }
        }

        // Keeping the same Sph Eq (Most reliable measurement we have).
        if (Math.abs(rounded.getCylinder()) > 0.249) {
            //    -3.25                - -3.00 = -0.25
            if (fitted.sphEquivalent() - rounded.sphEquivalent() < -0.20) { // Prefers sligly overcorrecting.
                //System.out.println("Softening Cylinder: -0.25D sph");
                rounded.setSphere(rounded.getSphere() - 0.25f);
            }
            if (fitted.sphEquivalent() - rounded.sphEquivalent() > 0.24) {
                //System.out.println("Softening Cylinder: +0.25D sph");
                rounded.setSphere(rounded.getSphere() + 0.25f);
            }
        }

        // Zeros the axis if cyl is zero.
        if (Math.abs(rounded.getCylinder()) < 0.12) {
            //System.out.println("Softening Cylinder: zeroing small cyls");
            rounded.setAxis(0);
            rounded.setCylinder(0);
        }
    }

    public AstigmaticLensParams roundNoAstCompensation(AstigmaticLensParams fitted, Collection<MeridianPower> angles, StringBuilder details) {
        if (fitted == null) return null;

        if (details != null)
            details.append("Rounding: Your best correction is " + fitted.toString());

        if (Math.abs(fitted.getCylinder()) < 0.6) {
            float newSphere = averagePower(angles);
            AstigmaticLensParams rounded = roundSphere(new AstigmaticLensParams(newSphere, 0, 0), angles, 0.25f);

            if (details != null)
                details.append(", but since your astigmatism is very low, you may not need to wear it. Since lenses come in steps of 0.25D, the closest available option is " + rounded.toString());

            return rounded;
        } else {
            AstigmaticLensParams rounded = roundAstigmatism(fitted, angles, 0.25f, 5);

            if (details != null)
                details.append(". Since lenses come in steps of 0.25D, the closest available option is " + rounded.toString());

            return rounded;
        }
    }
}
