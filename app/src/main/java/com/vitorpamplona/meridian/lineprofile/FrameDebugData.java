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
package com.vitorpamplona.meridian.lineprofile;

import com.vitorpamplona.meridian.utils.Logr;

import java.text.DecimalFormat;
import java.util.Arrays;

public class FrameDebugData {

    public float ratchetAngle;
    public float scrollyWheelAngle;

    public int ratchetCenterX;
    public int ratchetCenterY;
    public float sliderValueMM;
    public long processingTime;

    public boolean calibrated;
    public boolean calibrationIsGood;


    public int numberOfFramesUsedForCalibration;
    public int numberOfFramesUsedForInitialValues;
    public int numberOfFramesInTest;
    public int numberOfFramesDiscarded;

    public int averageFPS;

    // signal quality
    public Integer signalQualitySlider; // has 1 dot
    public Integer signalQualityScrolly; // has 1 dot
    public Integer[] signalQualityCalibrationDots = new Integer[3]; // has between 2 and 3 dots
    public Integer[] signalQualityRatchet = new Integer[4]; // has between 1 and 2 dots (3 and 4 with new asymmetric rings)

    // what failed
    public String errorCode;

    // component specific stats
    public float standardDeviationSliderLast30;
    public float standardDeviationScrollyLast30;
    public float standardDeviationCalibrationDotsLast30;
    public float standardDeviationRatchetLast30;


    public FrameDebugData() {

        reset();

    }

    public void reset() {

        sliderValueMM = Float.NaN;
        scrollyWheelAngle = Float.NaN;
        ratchetAngle = Float.NaN;
        ratchetCenterX = Integer.MAX_VALUE;
        ratchetCenterY = Integer.MAX_VALUE;
        processingTime = 0;

        calibrated = false;
        calibrationIsGood = false;

        numberOfFramesUsedForCalibration = 0;
        numberOfFramesUsedForInitialValues = 0;
        numberOfFramesInTest = 0;
        numberOfFramesDiscarded = 0;

        averageFPS = 0;

        signalQualitySlider = 0;
        signalQualityScrolly = 0;
        Arrays.fill(signalQualityCalibrationDots, 0);
        Arrays.fill(signalQualityRatchet, 0);

        errorCode = "";

        standardDeviationSliderLast30 = 0;
        standardDeviationScrollyLast30 = 0;
        standardDeviationCalibrationDotsLast30 = 0;
        standardDeviationRatchetLast30 = 0;

    }

    DecimalFormat formatter = new DecimalFormat("  +0.00;  -0.00");

    public String flatParams() {
        return "Slider: " + formatter.format(sliderValueMM) + "mm " +
                "\tScrolly: " + (Float.isNaN(scrollyWheelAngle) ? " NaNNaN " : formatter.format(scrollyWheelAngle)) + " degrees " +
                "\tRatchet: " + (Float.isNaN(ratchetAngle) ? " NaNNaN " : formatter.format(ratchetAngle)) + " degrees ";
    }

    public void logcat(String tag) {

        Logr.d(tag,

                sliderValueMM + " " +
                        scrollyWheelAngle + " " +
                        ratchetAngle + " " +
                        ratchetCenterX + " " +
                        ratchetCenterY + " " +
                        processingTime + " " +

                        calibrated + " " +
                        calibrationIsGood + " " +

                        numberOfFramesUsedForCalibration + " " +
                        numberOfFramesUsedForInitialValues + " " +
                        numberOfFramesInTest + " " +
                        numberOfFramesDiscarded + " " +

                        averageFPS + " " +

                        signalQualitySlider + " " +
                        signalQualityScrolly + " " +
                        Arrays.toString(signalQualityCalibrationDots) + " " +
                        Arrays.toString(signalQualityRatchet) + " " +

                        errorCode + " " +

                        standardDeviationSliderLast30 + " " +
                        standardDeviationScrollyLast30 + " " +
                        standardDeviationCalibrationDotsLast30 + " " +
                        standardDeviationRatchetLast30 + " " +

                        "");
    }

}
