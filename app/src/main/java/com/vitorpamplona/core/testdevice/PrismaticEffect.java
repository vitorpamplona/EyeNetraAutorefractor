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

public class PrismaticEffect {

    private float centralLensFocalLength; // mm
    private float borderLensFocalLength; // mm
    private float devicePD; // mm
    private float tubeLength; // mm
    private float DEFAULT_BIAS = 1.60f;

    public PrismaticEffect(float tubeLength, float lensFocalLength, float devicePD) {
        this.tubeLength = tubeLength;
        this.centralLensFocalLength = lensFocalLength;
        this.borderLensFocalLength = lensFocalLength;
        this.devicePD = devicePD;
    }

    public PrismaticEffect(float tubeLength, float centralLensFocalLength, float borderLensFocalLength, float devicePD) {
        this.tubeLength = tubeLength;
        this.centralLensFocalLength = centralLensFocalLength;
        this.borderLensFocalLength = borderLensFocalLength;
        this.devicePD = devicePD;
    }

    public float localFocalLength(float pd) {
        float aberrations = borderLensFocalLength - centralLensFocalLength;

        return (Math.abs(devicePD - pd) / 10) * (aberrations) + centralLensFocalLength;
    }

    public float prism(float pd) {
        return (((devicePD - pd) * DEFAULT_BIAS) / 10.0f) / (localFocalLength(pd) / 1000.0f);
    }

    /**
     * Equation diopters = 1.64023 * displacement
     *
     * This comes from the lensometer measurements of the lens we are using on the Beta desgin.
     *
     * @param pd
     * @return
     */
    //public float prismActual75mmLens(float pd) {
    //	return (devicePD-pd) * 1.64023f;
    //}
    public float testPrismShift(float pd) {
        return testPrismShiftGiven(prism(pd), pd);
    }

    //public float testPrismShiftActualLens(float pd) {
    //	return testPrismShiftGiven(prismActual75mmLens(pd), pd);
    //}

    public float testPrismShiftGiven(float prism, float pd) {
        return (prism / 100) * (tubeLength - localFocalLength(pd));
    }

}
