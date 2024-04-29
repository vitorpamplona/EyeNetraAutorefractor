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
package com.vitorpamplona.core.testdevice;


public class RequiredCorrectionMaskLens implements RequiredCorrection {

    public float lensEyeDistance;
    public float lensFocalLens;
    public float lensPhoneDistance;
    public float pixelPitch;

    public RequiredCorrectionMaskLens(float lensEyeDistance, float lensPhoneDistance, float lensFocalLens, float pixelPitch) {
        this.lensEyeDistance = lensEyeDistance;
        this.lensFocalLens = lensFocalLens;
        this.lensPhoneDistance = lensPhoneDistance;
        this.pixelPitch = pixelPitch;
    }

    public float computeDiopters(Pair p) {
        float screenDistance = p.changedDistance();

        if (p.distance(p.oP1, p.p1) < p.distance(p.oP1, p.p2))
            screenDistance = -screenDistance;

        float c = screenDistance * pixelPitch;
        float a = p.originalDistance() * pixelPitch;

        return computeDiopters(a, c);
    }

    public float computeDiopters(float a, float c) {
        float virtualDistance = lensEyeDistance + (a * lensFocalLens) / (((c + a) / lensPhoneDistance) * lensFocalLens - a);

        if (Math.abs(virtualDistance) < 0.000001) {
            return 0;
        }

        return -1000 / virtualDistance;
    }
}
