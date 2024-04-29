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

import java.io.Serializable;

public class FourierDomainAnalysis implements Serializable {

    public FourierDomainAnalysis() {
        // TODO Auto-generated constructor stub
    }

    public float fourierMSE(float sph, float cyl) {
        return sph + cyl / 2.0f;
    }

    public float fourierMSE(AstigmaticLensParams p) {
        return fourierMSE(p.getSphere(), p.getCylinder());
    }

    public float fourierJ0(float cyl, float axis) {
        return (float) (cyl / 2.0f * Math.cos(2 * Math.toRadians(axis)));
    }

    public float fourierJ0(AstigmaticLensParams p) {
        return fourierJ0(p.getCylinder(), p.getAxis());
    }

    public float fourierJ45(float cyl, float axis) {
        return (float) (cyl / 2.0f * Math.sin(2 * Math.toRadians(axis)));
    }

    public float fourierJ45(AstigmaticLensParams p) {
        return fourierJ45(p.getCylinder(), p.getAxis());
    }

    public float mod(AstigmaticLensParams measured, AstigmaticLensParams real) {
        float MSE = fourierMSE(measured) - fourierMSE(real);
        float J0 = fourierJ0(measured) - fourierJ0(real);
        float J45 = fourierJ45(measured) - fourierJ45(real);
        return (float) Math.sqrt(MSE * MSE + J0 * J0 + J45 * J45);
    }

    /**
     * Tolerance of measured to the real correction.
     * @param measured
     * @param real
     * @return
     */
    public float vdd(AstigmaticLensParams measured, AstigmaticLensParams real) {
        return (float) (Math.sqrt(2.0f) * mod(measured, real));
    }

    /**
     * Tolerance of measured to the real correction.
     * @param measured
     * @param real
     * @return
     */
    public float vddSign(AstigmaticLensParams measured, AstigmaticLensParams real) {
        return vdd(measured, real) * (measured.sphEquivalent() > real.sphEquivalent() ? 1 : -1);
    }

    public float diff(AstigmaticLensParams measured, AstigmaticLensParams real) {
        return vdd(measured, real);
    }

}
