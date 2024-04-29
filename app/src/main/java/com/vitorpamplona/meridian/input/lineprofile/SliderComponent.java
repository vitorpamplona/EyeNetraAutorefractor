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
import com.vitorpamplona.core.utils.NumberChangeDispatcher;
import com.vitorpamplona.core.utils.NumberChangeDispatcher.NumberChangedListener;
import com.vitorpamplona.meridian.imgproc.lineprofile.AutoCalibration;
import com.vitorpamplona.meridian.imgproc.lineprofile.ErrorCode;
import com.vitorpamplona.meridian.imgproc.lineprofile.ISliderFinder;
import com.vitorpamplona.meridian.imgproc.lineprofile.SliderFinder;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.YuvFilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks a sliding marker and returns its relative position to the center of the rect in which he is in. 
 *
 * The return can be in PX or MM and already smooths out the result to avoid noise issues.
 */
public class SliderComponent implements Component {

    private static final String TAG = SliderComponent.class.getSimpleName();

    private ISliderFinder finder;

    private NumberChangeDispatcher positionChangeMMTrigger;

    private double MMperPX = Double.NaN;
    private double absolutePositionPX = Double.NaN;
    private double pixelOffsetFromCenter = Double.NaN;
    private double mmOffsetFromCenter = Double.NaN;
    private Point2D sliderPosition;
    private boolean success = false;
    private FrameDebugData mDebugInfo = new FrameDebugData();


    private List<SliderListener> listeners = new ArrayList<SliderListener>();

    public SliderComponent(Point2D topLeft, Point2D bottomRight, int threshold, int width, int height, FrameDebugData debugInfo, YuvFilter colorFilter) {
        finder = new SliderFinder(topLeft, bottomRight, threshold, width, height, debugInfo, colorFilter);
        reset();
    }

    public double getSliderCenter() {
        return finder.getCenter();
    }

    public boolean isReady() {
        return positionChangeMMTrigger != null;
    }

    DecimalFormat df = new DecimalFormat("#.000");
    ErrorCode Status;

    public ErrorCode process(byte[] grayscale, SignalNormalizer signalNormalizer) {

        if (!isReady()) {
            return ErrorCode.SLC_NOT_READY;
        }
        if (positionChangeMMTrigger == null || Double.isNaN(MMperPX)) {
            return ErrorCode.SLC_CHANGE_TRIGGER;
        }

        // find PD sticky tape marker
        if ((Status = finder.find(grayscale, signalNormalizer)) != ErrorCode.SUCCESS) {
            Logr.e(TAG, "Slider not found");
            return Status;
        }

        // get found slider position
        sliderPosition = finder.getSliderPosition();

        // absolute slider position (px location)
        absolutePositionPX = sliderPosition.x;

        // pixel offset from center
        pixelOffsetFromCenter = absolutePositionPX - finder.getCenter();

        // mm offset from center
        mmOffsetFromCenter = -pixelOffsetFromCenter * MMperPX;

        Logr.d("OFFSET", "Offset (mm): " + df.format(mmOffsetFromCenter) +
                "  Offset (px): " + df.format(pixelOffsetFromCenter) +
                "  MMperPx: " + df.format(MMperPX));

        // check if the average changed, send message to clients.
        positionChangeMMTrigger.newValue(mmOffsetFromCenter);

        return ErrorCode.SUCCESS;
    }

    public boolean isStable() {
        return !finder.isMoving();
    }

    public void reset() {
        positionChangeMMTrigger = new NumberChangeDispatcher(0.5); // Change slider update sensitivity here (no movement = StdDev <0.3)
        positionChangeMMTrigger.add(new NumberChangedListener() {
            @Override
            public void changed(double from, double to, int stepsChanged) {
                refreshListenersSliderChanged(to);
            }
        });
    }

    public boolean isSuccess() {
        return success;
    }

    public void writeDebugInfo(Canvas canvas) {
        finder.writeDebugInfo(canvas);
    }

    public void release() {
        positionChangeMMTrigger = null;
    }

    public interface SliderListener {
        public void changed(double toMM);
    }

    public void refreshListeners() {
        refreshListenersSliderChanged(positionChangeMMTrigger.getValue());
    }

    private void refreshListenersSliderChanged(double toMM) {
        for (SliderListener l : listeners) {
            l.changed(toMM);
        }
    }

    public void add(SliderListener listener) {
        this.listeners.add(listener);
    }

    public void remove(SliderListener listener) {
        this.listeners.remove(listener);
    }

    public void setMMperPX(double MMperPX) {
        this.MMperPX = MMperPX;
    }

    public double getRelativeValueMM() {
        if (positionChangeMMTrigger == null) return Double.NaN;
        return positionChangeMMTrigger.getValue();
    }

    public double getRelativeValuePX() {
        return pixelOffsetFromCenter;
    }

    public double getMMoffsetFromCenter() {
        return mmOffsetFromCenter;
    }

    public double getAbsoluteValuePX() {
        return absolutePositionPX;
    }

    public Point2D getIndexPositionOfSlider() {
        return sliderPosition;
    }

    public void setParameters(Point2D point1, Point2D point2, int threshold, AutoCalibration mAutoCalibration) {
        finder.setParameters(point1, point2, threshold, mAutoCalibration);
    }

    @Override
    public ErrorCode processWheel(byte[] grayscale, SignalNormalizer signalNormalizer) {
        // TODO Auto-generated method stub
        return null;
    }

}
