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

import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.EyeGlassesUsageType;

public class Acceptance {

    public static float MINIMUM_CYL_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE = 0.25f;
    public static float MINIMUM_SPH_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE = 0.5f;

    public static float CYL_ADAPTATION_THRESHOLD = 1f;
    public static float SPH_ADAPTATION_THRESHOLD = 1.5f;

    public static float AGE_FOR_MYOPIA_CONTROL_PROCEDURES = 20f;

    public static float[] PRODUCTIVE_AGES_BETWEEN = {20f, 40f};

    public static float ADAPTATION_COEFFICIENT_NON_PRODUCTIVE = 0.33f;
    public static float ADAPTATION_COEFFICIENT_PRODUCTIVE = 0.1f;

    // * less than the first, sum the second as a reading glasses.
    public static float[][] presbiopiaStandardAdd = new float[][]{
            {38, 0},
            {41, 0.50f},
            {43, 1.00f},
            {45, 1.25f},
            {47, 1.50f},
            {49, 1.75f},
            {52, 2.00f},
            {55, 2.25f},
            {200, 2.50f}
    };

    public AstigmaticLensParams compute(AstigmaticLensParams current, boolean usingGlasses,
                                        AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {

        if (current == null) {
            current = new AstigmaticLensParams(0, 0, 0);
        }

        // If netra says that they don't need glasses, they dont need it.
        if (!netra.isInNeedOfGlasses()) {
            return new AstigmaticLensParams(0, 0, 0);
        }

        // If the person has glasses, but does not know the AstigmaticLensParams.
        // We cannot risk: report NETRA back.
        if (usingGlasses && !current.isInNeedOfGlasses()) {
            return netra;
        }

        AstigmaticLensParams newNetra = netra;
        if (netra.isMyopia()) {
            newNetra = adjustForMyopia(current, netra, usage, age);
        } else if (netra.isHyperopia()) {
            newNetra = adjustHyperopia(current, netra, usage, age);
        }

        if (netra.isAstigmat()) {
            newNetra = adjustCyl(current, newNetra, usage, age);
        }

        // Add reading glasses if usage is for NEAR and over 40.
        newNetra = readingGlasses(current, newNetra, usage, age);

        return newNetra;
    }


    public static AstigmaticLensParams readingGlasses(AstigmaticLensParams current, AstigmaticLensParams newNetra, EyeGlassesUsageType usage, int age) {
        if (age < 40) {
            return newNetra;
        }

        if (usage == EyeGlassesUsageType.FAR) {
            return newNetra;
        }

        for (float[] agePower : presbiopiaStandardAdd) {
            if (age < agePower[0]) {
                AstigmaticLensParams newRet = new AstigmaticLensParams(newNetra.getSphere(), newNetra.getCylinder(), newNetra.getAxis());
                newRet.setAddLens(agePower[1]);
                return newRet;
            }
        }

        return newNetra;
    }

    private AstigmaticLensParams adjustForMyopia(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {

        float diff = netra.getSphere() - current.getSphere();

        if (Math.abs(diff) < MINIMUM_SPH_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE) {
            return netra;
        }

        // If it is undercorrecting.. just prescribe NETRA or an adaptation.
        if (diff > 0) {
            if (Math.abs(diff) < SPH_ADAPTATION_THRESHOLD) {
                return netra;
            } else {
                return facilitatePerceptualAdaptationForSph(current, netra, usage, age);
            }
        }

        if (Math.abs(diff) < SPH_ADAPTATION_THRESHOLD) {

            // Bigger than 0.5, Smaller than 1.5D

            if (age < AGE_FOR_MYOPIA_CONTROL_PROCEDURES) {
                return myopiaControl(current, netra, usage, age);
            } else
                return netra;

        } else {

            return facilitatePerceptualAdaptationForSph(current, netra, usage, age);

        }
    }

    private AstigmaticLensParams adjustHyperopia(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {

        float diff = netra.getSphere() - current.getSphere();

        if (Math.abs(diff) < MINIMUM_SPH_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE) {
            return netra;
        }

        // If it is undercorrecting.. just prescribe NETRA or an adaptation.
        if (diff > 0) {
            if (Math.abs(diff) < SPH_ADAPTATION_THRESHOLD) {
                return netra;
            } else {
                return facilitatePerceptualAdaptationForSph(current, netra, usage, age);
            }
        }

        if (Math.abs(diff) < SPH_ADAPTATION_THRESHOLD) {

            return reduceNearPointForHyperopia(current, netra, usage, age);

        } else {

            // you cannot undercorrect a hyperope with NEAR usage.
            if (usage == EyeGlassesUsageType.NEAR && age < 20) {
                return netra;
            }

            return facilitatePerceptualAdaptationForSph(current, netra, usage, age);

        }
    }

    private AstigmaticLensParams adjustCyl(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {
        float diff = netra.getCylinder() - current.getCylinder();

        if (Math.abs(diff) < MINIMUM_CYL_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE) {
            return netra;
        }

        // If it is undercorrecting.. just prescribe NETRA or an adaptation.
        if (diff > 0) {
            return netra;
            //if (Math.abs(diff) < CYL_ADAPTATION_THRESHOLD) {
            //	return netra;
            //} else {
            //	return facilitatePerceptualAdaptationForCyl(current, netra, usage, age);
            //}
        }

        if (Math.abs(diff) < CYL_ADAPTATION_THRESHOLD) {
            return undercorrectCyl(netra);
        } else {
            return facilitatePerceptualAdaptationForCyl(current, netra, usage, age);
        }
    }

    private AstigmaticLensParams myopiaControl(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {

        if (usage == EyeGlassesUsageType.NEAR) {
            return new AstigmaticLensParams(netra.getSphere() + 0.5f, netra.getCylinder(), netra.getAxis());
        } else if (usage == EyeGlassesUsageType.FAR) {
            return netra;
        } else if (usage == EyeGlassesUsageType.BOTH) {
            return new AstigmaticLensParams(netra.getSphere() + 0.25f, netra.getCylinder(), netra.getAxis());
        }

        return netra;
    }

    private AstigmaticLensParams reduceNearPointForHyperopia(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {
        // Reduce near point of focus creating myopia
        if (usage == EyeGlassesUsageType.NEAR || usage == EyeGlassesUsageType.BOTH) {

            if (age > 40)
                // dont correct because he will be corrected by the reading glasses value.
                return netra;
            else
                return new AstigmaticLensParams(netra.getSphere() + 0.5f, netra.getCylinder(), netra.getAxis());

        } else if (usage == EyeGlassesUsageType.FAR) {
            return netra;
        } else if (usage == EyeGlassesUsageType.BOTH) {

            if (age < 20 || age > 40)
                return new AstigmaticLensParams(netra.getSphere() + 0.25f, netra.getCylinder(), netra.getAxis());
            else
                return new AstigmaticLensParams(netra.getSphere() + 0.5f, netra.getCylinder(), netra.getAxis());

        }

        return netra;
    }

    private AstigmaticLensParams facilitatePerceptualAdaptationForSph(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {
        float diff = current.getSphere() - netra.getSphere();

        if (isInProductiveYears(age))
            return new AstigmaticLensParams(round(diff * ADAPTATION_COEFFICIENT_PRODUCTIVE + netra.getSphere()), netra.getCylinder(), netra.getAxis());
        else
            return new AstigmaticLensParams(round(diff * ADAPTATION_COEFFICIENT_NON_PRODUCTIVE + netra.getSphere()), netra.getCylinder(), netra.getAxis());
    }

    public boolean isInProductiveYears(float age) {
        return age > PRODUCTIVE_AGES_BETWEEN[0] && age < PRODUCTIVE_AGES_BETWEEN[1];
    }

    private AstigmaticLensParams facilitatePerceptualAdaptationForCyl(AstigmaticLensParams current, AstigmaticLensParams netra, EyeGlassesUsageType usage, int age) {
        float diff = current.getCylinder() - netra.getCylinder();
        float amountCutted = diff * ADAPTATION_COEFFICIENT_NON_PRODUCTIVE;
        float adjustSphere = ((int) ((amountCutted / 2.0f) / 0.25f)) * 0.25f;

        // if is in a productive age
        //if (isInProductiveYears(age))
        //	return new AstigmaticLensParams(netra.getSphere(), round(diff * ADAPTATION_COEFFICIENT_PRODUCTIVE + netra.getCylinder()), netra.getAxis());
        //else
        return new AstigmaticLensParams(netra.getSphere() - adjustSphere, round(diff * ADAPTATION_COEFFICIENT_NON_PRODUCTIVE + netra.getCylinder()), netra.getAxis());
    }

    private AstigmaticLensParams undercorrectCyl(AstigmaticLensParams p) {
        return new AstigmaticLensParams(p.getSphere(), p.getCylinder() + MINIMUM_CYL_DIFFERENCE_TO_CHALLENGE_ACCEPTANCE, p.getAxis());
    }

    private float round(float power) {
        //return power;
        return Math.round(power / 0.25f) * 0.25f;
    }
}
