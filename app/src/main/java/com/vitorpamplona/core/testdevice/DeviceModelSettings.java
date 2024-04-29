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

import android.graphics.Rect;


public class DeviceModelSettings {

    private String calibrationScreenColor;
    private String componentVerificationScreenColor;
    private String butterflyColor;
    private int signalLevel;
    private Rect yuvMaskBounds;
    private String playIconColor;
    private DeviceDataset.PreviewFrameSize frameSize;

    public int getLowLightThreshold() {
        return lowLightThreshold;
    }

    public int getHighLightThreshold() {
        return highLightThreshold;
    }

    private int lowLightThreshold;
    private int highLightThreshold;


    public DeviceModelSettings(String deviceModel) {
        selectSettings(deviceModel);
    }

    private void selectSettings(String deviceModel) {

        // 'shellID' is not used currently, but can be used to separate different future shells
        //
        //
        //  parameters:
        //  -----------
        //
        //  calibrationScreenColor - color of the calibration screen (should be white-ish)
        //  componentVerificationScreenColor - color of the screen that tests the validity of the found components (should be blue-ish)
        //  butterflyColor - color of the butterfly ovals
        //  playIconColor - color of the 'play icon' (may require adjustment on a few phones to proceed to test)
        //  signalLevel - this is the general signal level (standard is 60-100, use more to adjust for more sensitive cameras)
        //  yuvMaskBounds - this is a 2d color mask filter in the YUV space (check instructions for more detail).  'null' means using blue chroma channel (U) with no thresholds.
        //  frameSize - should be 720x480 for now, until different resolutions may be used
        //
        //  yuvMaskBounds parameters:
        //  -------------------------
        //
        //  new Rect(Umin, Umax, Vmin, Vmax);   <-- levels go from 0 - 255
        //

        switch (deviceModel) {
            case "Samsung GT-I9505G":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung GT-I9505":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung GT-I9500":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung GT-I9508":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SAMSUNG-SGH-I337":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SGH-I337M":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung GT-I9515L":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SCH-I545":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SGH-M919N":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SGH-M919":
                set("#FFFFFFFF", "#FF000090", "#FF3C3CFF", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            case "Samsung SM-G920T":
                set("#FF888888", "#FF000090", "#FF000000", "#FF0000FF", 60, null, DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
            default:
                set("#FF6969FF", "#FF000000", "#FF000000", "#FF0000FF", 60, new Rect(0, 255, 0, 255), DeviceDataset.PreviewFrameSize.R720x480, 1, 10);
                break;
        }
    }

    private void set(String calibrationScreenColor,
                     String componentVerificationScreenColor,
                     String butterflyColor,
                     String playIconColor,
                     int signalLevel,
                     Rect yuvMaskBounds,
                     DeviceDataset.PreviewFrameSize frameSize,
                     int lowLightThreshold,
                     int highLightThreshold) {
        this.calibrationScreenColor = calibrationScreenColor;
        this.componentVerificationScreenColor = componentVerificationScreenColor;
        this.butterflyColor = butterflyColor;
        this.playIconColor = playIconColor;
        this.signalLevel = signalLevel;
        this.yuvMaskBounds = yuvMaskBounds;
        this.frameSize = frameSize;
        this.lowLightThreshold = lowLightThreshold;
        this.highLightThreshold = highLightThreshold;
    }

    public String getCalibrationScreenColor() {
        return calibrationScreenColor;
    }

    public String getComponentVerificationScreenColor() {
        return componentVerificationScreenColor;
    }

    public String getButterflyColor() {
        return butterflyColor;
    }

    public int getSignalLevel() {
        return signalLevel;
    }

    public Rect getYuvMaskBounds() {
        return yuvMaskBounds;
    }

    public String getPlayIconColor() {
        return playIconColor;
    }

    public DeviceDataset.PreviewFrameSize getFrameSize() {
        return frameSize;
    }
}

