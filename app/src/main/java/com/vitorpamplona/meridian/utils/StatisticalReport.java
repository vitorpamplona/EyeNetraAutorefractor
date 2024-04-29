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
package com.vitorpamplona.meridian.utils;

public class StatisticalReport {


    public StatisticalReport() {
        // TODO Auto-generated constructor stub
    }

    public Integer signalQualitySNR(Integer[] profile, Integer featureIndex, int noiseLevel) {

        Integer signalAtPeak, signalQuality;

        if (featureIndex == null) return 0;

        signalAtPeak = circularValueGrabber(profile, featureIndex);

        if (signalAtPeak == null) return 0;

        signalQuality = (int) ((signalAtPeak - noiseLevel) / (signalAtPeak + 0.0) * 100);

        return (signalQuality < 0) ? 0 : signalQuality;  // signal quality from 0 to 100%
    }

    public Integer signalQualitySideband(Integer[] profile, Integer featureIndex, int featureWidth) {

        Integer signalAtPeak, signalAtBaseLeft, signalAtBaseRight, signalQuality;

        if (featureIndex == null) return 0;

        signalAtPeak = circularValueGrabber(profile, featureIndex);
        signalAtBaseLeft = circularValueGrabber(profile, featureIndex - (int) Math.floor(featureWidth / 2));
        signalAtBaseRight = circularValueGrabber(profile, featureIndex + (int) Math.floor(featureWidth / 2));

        Logr.d("REPORT", "signalAtPeak: " + signalAtPeak + " signalAtBaseLeft: " + signalAtBaseLeft + " signalAtBaseRight: " + signalAtBaseRight);

        if (signalAtPeak == null || signalAtBaseLeft == null || signalAtBaseRight == null) return 0;

        signalQuality = (int) ((signalAtPeak - (signalAtBaseLeft + signalAtBaseRight) / 2) / (signalAtPeak + 0.0) * 100);

        return (signalQuality < 0) ? 0 : signalQuality;  // signal quality from 0 to 100%
    }

    private Integer circularValueGrabber(Integer[] buffer, int index) {
        int bufferLength = buffer.length;
        if (index >= bufferLength) {
            return buffer[index - bufferLength];
        } else if (index < 0) {
            return buffer[index + bufferLength];
        } else {
            return buffer[index];
        }
    }

}
