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
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.FloatHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QualityOfFit {

    public QualityOfFit() {
        // TODO Auto-generated constructor stub
    }

    public float computeRemovingKnownOutliers(Collection<MeridianPower> newMeridians2) {
        SinusoidalFitting fit = new SinusoidalFitting();
        BestRounding rounding = new BestRounding();
        OutlierRemoval outliers = new OutlierRemoval();

        StringBuilder why = new StringBuilder();
        StringBuilder debug = new StringBuilder();

        AstigmaticLensParams rNMeridians = fit.curveFitting(newMeridians2);
        rNMeridians = rounding.round25(outliers.run(newMeridians2, 0, rNMeridians, debug), newMeridians2, 0, why);

        //System.out.println(debug);

        return compute(newMeridians2, rNMeridians);
    }

    public MeridianPower get(Collection<MeridianPower> data, float angle) {
        for (MeridianPower p : data) {
            if (AngleDiff.diff180(p.getAngle(), angle) < 12) {
                return p;
            }
        }
        return null;
    }

    public int countOutliers(Collection<MeridianPower> newMeridians) {
        int cont = 0;
        for (MeridianPower data : newMeridians) {
            if (data.isOutlier()) {
                cont++;
            }
        }
        return cont;
    }

    public float compute(Collection<MeridianPower> newMeridians2, int numberOfUserErrors, AstigmaticLensParams fitCurve) {
        float fit = compute(newMeridians2, fitCurve);

        if (numberOfUserErrors > 0) {
            fit += (0.23f * numberOfUserErrors);
        }

        int outliers = countOutliers(newMeridians2);

        if (outliers == 1) {
            fit += 0.10f;
        } else if (outliers == 2) {
            if (fit < 0.60)
                fit += 0.35f; // Moves to the yellow sign for sure. (Highlights a weird result)
            else
                fit += 0.10f;
        }

        return fit;
    }

    private float compute(Collection<MeridianPower> newMeridians2, AstigmaticLensParams fitCurve) {
        List<Float> sortedAngles = new ArrayList<Float>();
        for (MeridianPower p : newMeridians2) {
            sortedAngles.add(AngleDiff.angle0to180(p.getAngle()));
        }
        Collections.sort(sortedAngles);


        List<Float> errors = new ArrayList<Float>();
        List<Float> absErrors = new ArrayList<Float>();

        CollectionUtils<Float> utils = new CollectionUtils<Float>();

        for (Float angle : sortedAngles) {
            MeridianPower current = get(newMeridians2, angle);

            if (current.isOutlier()) {
                continue;
            }

            MeridianPower powerPrev = get(newMeridians2, AngleDiff.angle0to180(angle - 22.5f));
            MeridianPower powerNext = get(newMeridians2, AngleDiff.angle0to180(angle + 22.5f));

            if (powerPrev == null || powerPrev.isOutlier()) {
                powerPrev = new MeridianPower(angle - 22.5f, fitCurve.interpolate(angle - 22.5f));
                //powerPrev = get(newMeridians2, AngleDiff.angle0to180(angle-45.0f));
            }

            if (powerNext == null || powerNext.isOutlier()) {
                powerNext = new MeridianPower(angle + 22.5f, fitCurve.interpolate(angle + 22.5f));
                //powerNext = get(newMeridians2, AngleDiff.angle0to180(angle+45.0f));
            }

            if (powerNext == null || powerPrev == null) {
                continue;
            }

            float power = get(newMeridians2, angle).getPower();

            //System.out.println("Fitting Angle " + angle + " - " + power + " is off by " + ((((powerPrev.getPower() + powerNext.getPower()) / 2)) - power));

            float error = ((powerPrev.getPower() + powerNext.getPower()) / 2) - power;

            errors.add(error);
            absErrors.add(Math.abs(error));
        }

        CollectionUtils.AvgStdPair avgError = utils.avgSTD(errors);
        CollectionUtils.AvgStdPair avgAbsError = utils.avgSTD(absErrors);

        //System.out.println("Fitting Avg Std " + avgError.toString() + " " + avgAbsError.toString());

        return avgAbsError.avg;
    }

    public float compute(double[][] x, double[] y, int numberOfUserErrors) {
        float fit = compute(x, y);

        if (numberOfUserErrors > 0) {
            fit += (0.23f * numberOfUserErrors);
        }

        return fit;
    }

    private float compute(double[][] x, double[] y) {
        FloatHashMap<MeridianPower> newMeridians2 = new FloatHashMap<MeridianPower>();
        for (int i = 0; i < y.length; i++) {
            newMeridians2.put((float) x[i][0], new MeridianPower((float) x[i][0], (float) y[i]));
        }

        List<Float> sortedAngles = new ArrayList<Float>(newMeridians2.keySet());
        Collections.sort(sortedAngles);


        List<Float> errors = new ArrayList<Float>();
        List<Float> absErrors = new ArrayList<Float>();

        CollectionUtils<Float> utils = new CollectionUtils<Float>();

        for (Float angle : sortedAngles) {
            MeridianPower current = newMeridians2.getClosestToAngle180(angle);

            if (current.isOutlier()) {
                //System.out.println(angle +  " is outlier");
                continue;
            }

            MeridianPower powerPrev = newMeridians2.getClosestToAngle180(angle - 22.5f);
            MeridianPower powerNext = newMeridians2.getClosestToAngle180(angle + 22.5f);

            if (powerPrev.isOutlier()) {
                powerPrev = newMeridians2.getClosestToAngle180(angle - 45.0f);
            }

            if (powerNext.isOutlier()) {
                powerNext = newMeridians2.getClosestToAngle180(angle + 45.0f);
            }

            float power = current.getPower();

            //System.out.println("\t\t " + angle + " " + " ("+ powerPrev.getAngle()+ ","+ powerNext.getAngle() + ") " + Math.abs(((powerPrev.getPower() + powerNext.getPower()) / 2) - power));

            float error = ((powerPrev.getPower() + powerNext.getPower()) / 2) - power;

            errors.add(error);
            absErrors.add(Math.abs(error));
        }

        CollectionUtils.AvgStdPair avgError = utils.avgSTD(errors);
        CollectionUtils.AvgStdPair avgAbsError = utils.avgSTD(absErrors);

        //System.out.println("Fitting Avg Std " + avgError.toString() + " " + avgAbsError.toString());

        return avgAbsError.avg;
    }
}
