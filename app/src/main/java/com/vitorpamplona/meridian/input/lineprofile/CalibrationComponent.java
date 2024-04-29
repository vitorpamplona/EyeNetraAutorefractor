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
package com.vitorpamplona.meridian.input.lineprofile;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.imgproc.lineprofile.Calibration;
import com.vitorpamplona.meridian.imgproc.lineprofile.CalibrationFinder;
import com.vitorpamplona.meridian.imgproc.lineprofile.ErrorCode;
import com.vitorpamplona.meridian.imgproc.lineprofile.ICalibrationFinder;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.NoiseRemovalStackForPoints;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.YuvFilter;

public class CalibrationComponent implements Component {
    private static final int CALIBRATION_CENTER_STACK_SIZE = 10;

    private static final String TAG = CalibrationComponent.class.getSimpleName();

    private ICalibrationFinder finder;

    private NoiseRemovalStackForPoints calibrationCenterAverage;
    Calibration calibrationPoints = null;
    private boolean success = false;
    FrameDebugData mDebugInfo = new FrameDebugData();

    private double distanceCalibrationMarksMM;

    public CalibrationComponent(Point2D topLeft, Point2D bottomRight, int threshold, int width, int height, FrameDebugData debugInfo, YuvFilter colorFilter) {
        finder = new CalibrationFinder(new Point2D(topLeft.x, topLeft.y), new Point2D(bottomRight.x, bottomRight.y), threshold, width, height, debugInfo, colorFilter);
        this.mDebugInfo = debugInfo;
        reset();
    }

    public boolean process(byte[] grayscale) {
        if (!isReady()) {
            return success = false;
        }

        // find the calibration params.
        calibrationPoints = finder.find(grayscale, distanceCalibrationMarksMM);
        if (calibrationPoints == null || calibrationPoints.center() == null) {
            Logr.e(TAG, "Couldn't Find Calibration: " + calibrationPoints);
            return success = false; // Could not find the slider position. Probably moving the eyeshield.
        }

        return success = true;
    }


    public boolean isReady() {
        return calibrationCenterAverage != null;
    }

    public void reset() {
        calibrationCenterAverage = new NoiseRemovalStackForPoints(3, CALIBRATION_CENTER_STACK_SIZE);
    }

    public boolean isSuccess() {
        return success;
    }

    public void writeDebugInfo(Canvas canvas) {
        finder.writeDebugInfo(canvas);
    }

    public Point2D center() {
        return calibrationCenterAverage.average();
    }

    public Calibration calibrationPoints() {
        return calibrationPoints;
    }

    public void release() {
        calibrationCenterAverage = null;
    }

    public void setParameters(Point2D point1, Point2D point2, int threshold) {
        finder.setRect(point1, point2, threshold);
    }

    public Rect getParameters() {
        return finder.getRect();
    }

    @Override
    public ErrorCode processWheel(byte[] grayscale, SignalNormalizer signalNormalizer) {
        // TODO Auto-generated method stub
        return null;
    }
}
