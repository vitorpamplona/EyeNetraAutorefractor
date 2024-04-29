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

import com.vitorpamplona.core.utils.RefRounding;

public class AddSuggestions {
    public static float[][] AVERAGE_ADD_PER_AGE = new float[][]{
            {38.0F, 0.0F},
            {39.5F, 0.0F},
            {40.0F, 0.50F},
            {41.0F, 0.50F},
            {42.0F, 0.75F},
            {43.0F, 1.00F},
            {44.0F, 1.25F},
            {45.0F, 1.25F},
            {46.0F, 1.50F},
            {47.0F, 1.50F},
            {48.0F, 1.75F},
            {49.0F, 1.75F},
            {50.0F, 2.00F},
            {51.0F, 2.00F},
            {52.0F, 2.00F},
            {53.0F, 2.00F},
            {54.0F, 2.25F},
            {55.0F, 2.25F},
            {60.0F, 2.50F},
            {70.0F, 2.75F},
            {200.F, 3.00F}
    };

    public static float[][] MEAN_ACCOMODATION_PER_AGE = new float[][]{
            {10.0F, 13.75F, 0}, // Age, Mean Accommodation Range, Added for Confort
            {20.0F, 11F, 0},
            {30.0F, 8.6F, 0},
            {35.0F, 8.6F, 0},
            {40.0F, 6F, 0.75F},
            {42.5F, 5F, 1.25F},
            {45.0F, 4F, 1.50F},
            {47.5F, 3F, 1.75F},
            {50.0F, 1.75F, 0.50F},
            {55.0F, 1.40F, 0.50F},
            {60.0F, 1.25F, 0.75F},
            {65.0F, 1.10F, 0.75F},
            {70.0F, 1.0F, 0.50F},
            {90.0F, 0.50F, 0.25F},
            {150.0F, 0.50F, 0.25F},
    };

    public static float suggestedAddPowerByAgeOnly(float age) {
        for (float[] agePower : AVERAGE_ADD_PER_AGE) {
            if (age < agePower[0]) {
                return agePower[1];
            }
        }
        return 0;
    }

    // Linear Interpolation
    public static float accommodationAmplitudeByAge(float age) {
        float[] previousAgePower = {0, 15F};
        for (float[] agePower : MEAN_ACCOMODATION_PER_AGE) {
            if (age <= agePower[0]) {
                float amplitudeDiopter = agePower[1] - previousAgePower[1];
                float percentageAge = (age - previousAgePower[0]) / (agePower[0] - previousAgePower[0]);
                return (amplitudeDiopter * percentageAge + previousAgePower[1]);
            }
            previousAgePower = agePower;
        }
        return 0;//error.
    }

    // confort Factor
    public static float comfortAddByAge(float age) {
        float[] previousAgePower = {0, 15F, 0.0f};
        for (float[] agePower : MEAN_ACCOMODATION_PER_AGE) {
            if (age <= agePower[0]) {
                float amplitudeDiopter = agePower[2] - previousAgePower[2];
                float percentageAge = (age - previousAgePower[0]) / (agePower[0] - previousAgePower[0]);
                return (amplitudeDiopter * percentageAge + previousAgePower[2]);
            }
            previousAgePower = agePower;
        }
        return 0;//error.
    }

    // Reading positions from 7in to 30in. 0.17m to 0.76m
    // From the accommodation ranges.
    public static float wantToReadAt(float age, float distanceInMeters) {
        // Assumes vision corrected for distance (so for the Add power).
        float dioptersNeededToReadAtPreferredDistance = 1 / distanceInMeters;
        float accommodativePowerLeft = accommodationAmplitudeByAge(age);

        float additionalHelp = dioptersNeededToReadAtPreferredDistance - accommodativePowerLeft;

        // Allows additional accomodation range closer to the desired.
        float confortAdd = comfortAddByAge(age);

        if (additionalHelp < 0.25) {
            return RefRounding.roundTo(confortAdd, 0.25f);
        }

        return RefRounding.roundTo(additionalHelp + confortAdd, 0.25f);
    }
}
