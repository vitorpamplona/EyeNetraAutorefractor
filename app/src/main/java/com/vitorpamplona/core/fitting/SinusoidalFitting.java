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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Prepare our data for the Jama library.
 */
public class SinusoidalFitting {

    public AstigmaticLensParams guessPrescription(Collection<MeridianPower> measuredMeridians) {
        if (measuredMeridians.size() <= 0) {
            return new AstigmaticLensParams();
        }

        float minDiopter = 999;
        float maxDiopter = -999;
        float minAbsDiopter = 999;
        float maxAbsDiopter = -999;
        float minAxis = 0;

        float sum = 0;
        for (MeridianPower meridian : measuredMeridians) {
            if (meridian.getPower() > maxAbsDiopter) {
                maxAbsDiopter = meridian.getPower();
                maxDiopter = meridian.getPower();
            }

            if (meridian.getPower() < minAbsDiopter) {
                minAbsDiopter = meridian.getPower();
                minDiopter = meridian.getPower();

                minAxis = meridian.getAngle();
            }

            sum += meridian.getPower();
        }

        float cyl = (maxDiopter - minDiopter);
        float avg = sum / measuredMeridians.size();
        float sph = avg - cyl / 2;
        float axis = minAxis;

        return new AstigmaticLensParams(sph, cyl, axis);
    }

    public AstigmaticLensParams curveFitting(Collection<MeridianPower> measuredMeridians) {
        LMfunc f = new AstigmaticLensFunction();
        AstigmaticLensParams guessedPrescription = guessPrescription(measuredMeridians);

        if (measuredMeridians.size() < 3 || Math.abs(guessedPrescription.getCylinder()) < 0.001) {
            return guessedPrescription;
        }

        // Preparing guessed values
        double[] aguess = new double[3];
        aguess[0] = guessedPrescription.getSphere();
        aguess[1] = guessedPrescription.getCylinder();
        aguess[2] = AngleDiff.angle0to180(guessedPrescription.getAxis());

        // Preparing data
        double[][] x = new double[measuredMeridians.size()][1];
        double[] y = new double[measuredMeridians.size()];
        double[] s = new double[measuredMeridians.size()];
        int j = 0;

        for (MeridianPower power : measuredMeridians) {
            x[j][0] = AngleDiff.angle0to180(power.getAngle());
            y[j] = power.getPower();
            s[j] = 1.f;
            j++;
        }

        // Preparing items that can vary: ALL
        boolean[] vary = new boolean[aguess.length];
        for (int i = 0; i < aguess.length; i++) {
            vary[i] = true;
        }

        // Solving
        try {
            LM.solve(x, aguess, y, s, vary, f, 0.01, 0.000001, 300, 0);
        } catch (Exception ex) {
            System.err.println("Exception caught: " + ex.getMessage());
            throw new Error(ex);
        }

        AstigmaticLensParams fitted = new AstigmaticLensParams((float) aguess[0], (float) aguess[1], (float) aguess[2]);

        //if (measuredMeridians.size() <= 7 || Math.abs(fitted.getCylinder()) < 0.51f) {
        //	return fitted;
        //}

        //AstigmaticLensParams enhancedFit = new OutlierRemoval().run(measuredMeridians, fitted);

        // Current prescription with outliers
        return fitted;
    }

    public List<Outlier> compileAllOutlierOptions(Collection<MeridianPower> measuredMeridians, int dongs, AstigmaticLensParams fitted) {
        LMfunc f = new AstigmaticLensFunction();

        // Preparing guessed values
        double[] originalFit = new double[3];
        originalFit[0] = fitted.getSphere();
        originalFit[1] = fitted.getCylinder();
        originalFit[2] = fitted.getAxis();

        List<Outlier> options = new ArrayList<Outlier>();
        List<MeridianPower> backup = new ArrayList<MeridianPower>();

        // Preparing data
        double[][] x = new double[measuredMeridians.size()][1];
        double[] y = new double[measuredMeridians.size()];
        double[] s = new double[measuredMeridians.size()];
        int j = 0;

        for (MeridianPower power : measuredMeridians) {
            x[j][0] = AngleDiff.angle0to180(power.getAngle());
            y[j] = power.getPower();
            s[j] = 1.f;
            backup.add(power);
            j++;
        }

        // Preparing items that can vary: ALL
        boolean[] vary = new boolean[originalFit.length];
        for (int i = 0; i < originalFit.length; i++) {
            vary[i] = true;
        }

        QualityOfFit qualityOfFit = new QualityOfFit();

        for (int i = 0; i < y.length; i++) {
            double[] newFit = Arrays.copyOf(originalFit, originalFit.length);

            double[][] newX = removeElement(x, i);
            double[] newY = removeElement(y, i);
            double[] newS = removeElement(s, i);

            try {
                LM.solve(newX, newFit, newY, newS, vary, f, 0.01, 0.0000001, 300, 0);
            } catch (Exception ex) {
                for (int k = 0; k < newY.length; k++) {
                    System.out.println(newX[k][0] + "," + newY[k]);
                }

                System.err.println("Exception caught: " + ex.getMessage());
                throw new Error(ex);
            }

            AstigmaticLensParams option = new AstigmaticLensParams(
                    (float) newFit[0],
                    (float) newFit[1],
                    (float) newFit[2]);
            option.putInNegativeCilinder();

            //System.out.println("Quality of Fit of " + (float)x[i][0] + " " + (float)y[i]);

            float fitting = qualityOfFit.compute(newX, newY, dongs);

            options.add(new Outlier(option, fitting, backup.get(i), null));
            // System.out.println(options.get(options.size()-1));

            // Trying to see if there are 2 outliers.
            for (int k = 0; k < y.length; k++) {
                if (i >= k) continue;

                newFit = Arrays.copyOf(originalFit, originalFit.length);

                // Preparing data
                newX = new double[measuredMeridians.size() - 2][1];
                newY = new double[measuredMeridians.size() - 2];
                newS = new double[measuredMeridians.size() - 2];

                int cont = 0;
                j = 0;
                for (MeridianPower power : measuredMeridians) {
                    if (cont == i || cont == k) {
                        cont++;
                        continue;
                    }
                    newX[j][0] = AngleDiff.angle0to180(power.getAngle());
                    newY[j] = power.getPower();
                    newS[j] = 1.f;
                    cont++;
                    j++;
                }

                try {
                    LM.solve(newX, newFit, newY, newS, vary, f, 0.01, 0.0000001, 300, 0);
                } catch (Exception ex) {
                    for (int c = 0; c < newY.length; c++) {
                        System.out.println(newX[c][0] + "," + newY[c]);
                    }

                    System.err.println("Exception caught: " + ex.getMessage());
                    throw new Error(ex);
                }

                option = new AstigmaticLensParams(
                        (float) newFit[0],
                        (float) newFit[1],
                        (float) newFit[2]);
                option.putInNegativeCilinder();

                //System.out.println("Quality of Fit of " + (float)x[i][0] + " and " +  (float)x[k][0] + " " + (float)y[i]);

                fitting = qualityOfFit.compute(newX, newY, dongs);

                options.add(new Outlier(option, fitting, backup.get(i), backup.get(k)));

                //System.out.println(options.get(options.size()-1));
            }
        }

        return options;
    }

    public static double[] removeElement(double[] original, int element) {
        double[] n = new double[original.length - 1];
        System.arraycopy(original, 0, n, 0, element);
        System.arraycopy(original, element + 1, n, element, original.length - element - 1);
        return n;
    }

    public static double[][] removeElement(double[][] original, int element) {
        double[][] n = new double[original.length - 1][1];
        System.arraycopy(original, 0, n, 0, element);
        System.arraycopy(original, element + 1, n, element, original.length - element - 1);
        return n;
    }


}
