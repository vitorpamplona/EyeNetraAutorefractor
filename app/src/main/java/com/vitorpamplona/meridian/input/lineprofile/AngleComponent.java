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

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.core.utils.AngleChange360Dispatcher;
import com.vitorpamplona.core.utils.NoiseRemovalStack;
import com.vitorpamplona.meridian.imgproc.lineprofile.ErrorCode;
import com.vitorpamplona.meridian.imgproc.lineprofile.IAngleFinder;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.SignalNormalizer;

import java.util.List;

public abstract class AngleComponent implements Component {

    private static final String TAG = AngleComponent.class.getSimpleName();

    private IAngleFinder finder;
    private boolean success = false;

    private NoiseRemovalStack angleStack;
    private AngleChange360Dispatcher angleChangeTrigger;

    public AngleComponent(IAngleFinder finder) {
        this.finder = finder;
        reset();
    }

    public void reset(NoiseRemovalStack stack, AngleChange360Dispatcher trigger) {
        angleStack = stack;
        angleChangeTrigger = trigger;
    }

    public boolean isReady() {
        if (angleStack == null) {
            Logr.e(TAG, "AngleStack is missing");
        }
        if (angleChangeTrigger == null) {
            Logr.e(TAG, "AngleChangeTrigger is missing");
        }
        if (finder == null) {
            Logr.e(TAG, "Finder is missing");
        }

        return (angleStack != null && angleChangeTrigger != null && finder != null && finder.isReady());
    }

    public void setCenter(Point2D center) {
        finder.setCenter(center);
    }

    public void setRadius(int radiusPX) {
        finder.setRadius(radiusPX);
    }

    public void setThickness(int thicknessPX) {
        finder.setThickness(thicknessPX);
    }

    public void setParameters(Point2D center, int radiusPX, int thicknessPX, List<Point2D> satellitePoints, int threshold) {
        finder.setParameters(center, radiusPX, thicknessPX, satellitePoints, threshold);
    }


    ErrorCode Status;

    @Override
    public ErrorCode processWheel(byte[] grayscale, SignalNormalizer signalNormalizer) {

        if (!isReady()) return ErrorCode.C_NOT_READY;

        // search for the wheel position.  TODO do this test in finder
        if ((Status = finder.find(grayscale, signalNormalizer)) != ErrorCode.SUCCESS) {
            Logr.e(TAG, "Angle Component not found");
            return Status;
        }

        // get wheel position
        Double wheel = finder.getWheelPosition();
        if (wheel == null || Double.isNaN(wheel)) {
            return ErrorCode.C_NULL_NAN;
        }

        angleStack.add(wheel);

        // if there is an average of measurements, may send to clients.
        if (angleStack.isReady()) {
            //Logr.d(TAG, "Average "+ angleStack.average());
            // check if changed, send message to clients.
            angleChangeTrigger.newValue(angleStack.average());
        } else {
            // This was considered an error before. Spuring RAC_CHANGE_TRIGGER due to the lack of numbers in the stack.
            // Since it was triggering an error message, the app would clear the stack and restart it,
            // removing the benefits of using the moving average and generating an Intermittent Scrolly.
            // From now on, this is considered a sucess reading.
            Logr.e(TAG, "Angle Component Not ready " + angleStack.values.size() + " - Needed " + angleStack.stackSize + " Average " + wheel + " - " + angleStack.average());
            return ErrorCode.SUCCESS;
        }

        return ErrorCode.SUCCESS;
    }

    public double getAngle() {
        if (angleChangeTrigger == null) return Double.NaN;
        return angleChangeTrigger.getValue();
    }

    public void release() {
        angleStack = null;
        angleChangeTrigger = null;
    }

    public boolean isSuccess() {
        return success;
    }

    public void writeDebugInfo(Canvas canvas) {
        finder.writeDebugInfo(canvas);
    }


}
