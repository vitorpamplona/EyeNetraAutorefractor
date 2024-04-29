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
package com.vitorpamplona.meridian.imgproc.lineprofile;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.LineProfile;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.RegionOfInterest.Do;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.StatisticalReport;
import com.vitorpamplona.meridian.utils.YuvFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;


public class SliderFinder implements ISliderFinder {

    private static final int MINMAX_ALPHA = 20;  // alpha for local min-max method (peak changes below this threshold are ignored)
    private int LIGHT_THRESHOLD = 100;  // warn image processing that not enough light below this threshold
    private int SLIDER_TRACKS = 10;  // amount of parallel tracks
    private int SLIDER_POINTS = 70;  // amount of points to compute in one track

    private int noiseThreshold = 10;  // default, overwritten by constructor
    public Point2D markerPosition = null; // the position of the sticky tape
    public Float centerOfMass = null;
    public Integer[] profile = new Integer[SLIDER_POINTS];

    private int width, height;
    private FrameDebugData mDebugInfo;

    private Point2D point1, point2, lineCenter;
    private float pdiff;
    private LineProfile sliderTrack;
    private DescriptiveStatistics statistics;
    private AutoCalibration calibrationParameters;
    private CalibrationTools tools;
    private YuvFilter colorFilter;

    public SliderFinder(Point2D point1, Point2D point2, int threshold, int width, int height, FrameDebugData debugInfo, YuvFilter colorFilter) {
        this.width = width;
        this.height = height;
        this.noiseThreshold = threshold;
        this.mDebugInfo = debugInfo;
        this.colorFilter = colorFilter;

        setRect(point1, point2);

        statistics = new DescriptiveStatistics();
        statistics.setWindowSize(15);
    }

    @Override
    public void setRect(Point2D point1, Point2D point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.pdiff = point2.x - point1.x;
        this.lineCenter = new Point2D((point2.x - point1.x) / 2 + point1.x, (point2.y - point1.y) / 2 + point1.y);
        initializeLine(point1, point2);
    }

    @Override
    public void setParameters(Point2D point1, Point2D point2, int threshold, AutoCalibration calibrationParameters) {
        this.calibrationParameters = calibrationParameters;
        this.noiseThreshold = threshold;
        setRect(point1, point2);
    }

    private void initializeLine(Point2D point1, Point2D point2) {
        sliderTrack = new LineProfile(point1, point2, SLIDER_TRACKS, SLIDER_POINTS, width, height, colorFilter);
        Logr.d("PARAMETERS", "SliderFinder: (" + point1.x + "," + point1.y + ")  (" + point2.x + "," + point2.y + ")");
    }


    DecimalFormat df = new DecimalFormat("#.000");

    @Override
    public ErrorCode find(byte[] grayscale, SignalNormalizer signalNormalizer) {
        if (point1 == null || point2 == null) {
            return ErrorCode.SLF_POINTS_NULL;
        }

        // get profile of slider track
        profile = sliderTrack.getProfile(grayscale, 0);

        // run profile through spatial boxcar smoother
        profile = LineProfileUtils.linearMovingAverage(profile, 8);

        // adjust signal intensity
        if (signalNormalizer != null) {
            for (int i = 0; i < profile.length; i++) {
                int x = sliderTrack.lineCoordinates.posX(0, i);
                int y = sliderTrack.lineCoordinates.posY(0, i);
                profile[i] = (int) (profile[i] * signalNormalizer.factor(x, y));
            }
        }

        // test if there is enough light
        if (sliderTrack.lineTotalSum() < LIGHT_THRESHOLD) {
            Logr.e("SLIDER", sliderTrack.lineTotalSum() + " of total Energy is less than " + LIGHT_THRESHOLD);
            System.out.println("NEL: " + sliderTrack.lineTotalSum() + " of total Energy is less than " + LIGHT_THRESHOLD);
            return ErrorCode.NOT_ENOUGH_LIGHT;
        }

        // get centroid of peak (index in profile array)
        centerOfMass = LineProfileUtils.centerOfMass(profile, 0, sliderTrack.getMaximaIdx() - 15, sliderTrack.getMaximaIdx() + 15);

        // slider track must contain data
        if (centerOfMass == null) {
            markerPosition = null;
            return ErrorCode.SLF_NO_DOT_FOUND;
        }

        // position of the found sticky tape (pixel position)
        markerPosition = new Point2D(point1.x + centerOfMass * pdiff / SLIDER_POINTS, point1.y);

        // Debug
        statistics.addValue(markerPosition.x);
        Logr.d("SLIDER", "Position: " + df.format(markerPosition.x) +
                "  StdDev (last 15): " + df.format(statistics.getStandardDeviation()) +
                "  Mean (last 15): " + df.format(statistics.getMean()));

        // return actual pixel location!
        return ErrorCode.SUCCESS;
    }


    StatisticalReport report = new StatisticalReport();

    @Override
    public void writeDebugInfo(final Canvas canvas) {

        final Paint paint = new Paint();

        if (markerPosition != null /*&& success == true*/) {

            paint.setColor(Color.WHITE);
            paint.setTextSize(15);
            canvas.drawText(" slider track ", point1.x, point1.y + 35, paint);

            // display coordinates of line points
            paint.setColor(Color.RED);
            sliderTrack.lineCoordinates.each(new Do() {
                public void process(int valueX, int valueY) {
                    canvas.drawPoint(valueX, valueY, paint);
                }
            });

            // draw the pixel profile
            paint.setColor(Color.GREEN);

            for (int t = 1; t < SLIDER_POINTS; t++) {
                canvas.drawLine(sliderTrack.lineCoordinates.posX(0, t - 1), lineCenter.y - profile[t - 1],
                        sliderTrack.lineCoordinates.posX(0, t), lineCenter.y - profile[t], paint);
            }

            // draw circles in detected points
            if (centerOfMass != null) {
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(sliderTrack.lineCoordinates.posX(0, (int) Math.round(centerOfMass)),
                        lineCenter.y - profile[(int) Math.round(centerOfMass)], 5, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(15);
                canvas.drawText((int) Math.round(centerOfMass) + "", sliderTrack.lineCoordinates.posX(0, (int) Math.round(centerOfMass)) - 30,
                        lineCenter.y - profile[(int) Math.round(centerOfMass)] + 5, paint);
            }

            // display offset number
            paint.setStrokeWidth(1);
            paint.setTextSize(14);
            paint.setColor(Color.WHITE);
            canvas.drawText("slider sum: " + sliderTrack.lineTotalSum(), 320, 60, paint);

        }

    }

    @Override
    public boolean isMoving() {  // not used
        return (markerPosition == null);
    }

    @Override
    public double getCenter() {
        return this.lineCenter.x;
    }

    @Override
    public Point2D getSliderPosition() {
        return markerPosition;
    }

}
