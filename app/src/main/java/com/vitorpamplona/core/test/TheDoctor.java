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

public class TheDoctor {

    private static final double MINIMUM_NOTICIABLE_SPHERICAL_EQUIVALENT = 0.50;
    private static final double MINIMUM_NOTICIABLE_CYLINDER = 1.00;

    public TheDoctor() {

    }

    public static boolean smallSphericalEquivalent(float sphere, float cylinder) {
        float sphEq = SphericalEquivalent.compute(sphere, cylinder);
        return Math.abs(sphEq) < MINIMUM_NOTICIABLE_SPHERICAL_EQUIVALENT;
    }

    public static boolean smallCylinder(float cylinder) {
        return Math.abs(cylinder) < MINIMUM_NOTICIABLE_CYLINDER;
    }

    public static boolean isInNeedOfGlassesForDistanceView(float sphere, float cylinder) {
        return !(smallSphericalEquivalent(sphere, cylinder) && smallCylinder(cylinder));
    }

    public static boolean hasMyopia(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && sphere < 0;
    }

    public static boolean hasHyperopia(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && sphere > 0;
    }

    public static boolean hasAstigmatism(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && Math.abs(cylinder) >= 0.25;
    }

    public static boolean isLightCorrection(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && Math.abs(sphere) < 1.6;
    }

    public static boolean isMediumCorrection(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && Math.abs(sphere) >= 1.6 && Math.abs(sphere) < 4.6;
    }

    public static boolean isHighCorrection(float sphere, float cylinder) {
        return isInNeedOfGlassesForDistanceView(sphere, cylinder) && Math.abs(sphere) >= 4.6;
    }
}
