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
import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.LineProfile;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.LocalMinMax;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.RegionOfInterest.Do;
import com.vitorpamplona.meridian.utils.StatisticalReport;
import com.vitorpamplona.meridian.utils.Stopwatch;
import com.vitorpamplona.meridian.utils.TemporalArraySmoother;
import com.vitorpamplona.meridian.utils.YuvFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;


public class CalibrationFinder implements ICalibrationFinder {

    private static final int MINMAX_ALPHA = 10;  // alpha for local min-max method (peak changes below this threshold are ignored)
    private int SPREAD = 3;  // peak refinement spread (+/- SPREAD)
    private int CALIBRATION_TRACKS = 10;  // amount of parallel tracks
    private int CALIBRATION_POINTS = 250;  // amount of points to compute in one track

    private int noiseThreshold = 10;  // default, overwritten by constructor
    private int width = 0, height = 0;
    private YuvFilter colorFilter;
    private FrameDebugData mDebugInfo;
    StatisticalReport report = new StatisticalReport();

    private Integer[] pointsDebug = {0, 0, 0};
    private boolean isVertical = true;
    private Point2D markerpoint1px;
    private Point2D markerpoint2px;
    private Point2D markerpoint3px;

    Integer[] maximaVals;
    Integer[] maximaIndex;

    Calibration mark = null;
    Integer[] profile = new Integer[CALIBRATION_POINTS];
    Integer[] smoothProfile = new Integer[CALIBRATION_POINTS];

    private Point2D point1, point2;
    private LineProfile calibrationTrack;
    private DescriptiveStatistics statisticsDot1, statisticsDot2;


    public CalibrationFinder(Point2D point1, Point2D point2, int threshold, int width, int height, FrameDebugData debugInfo, YuvFilter colorFilter) {
        this.width = width;
        this.height = height;
        this.mDebugInfo = debugInfo;
        this.colorFilter = colorFilter;

        statisticsDot1 = new DescriptiveStatistics();
        statisticsDot1.setWindowSize(30);
        statisticsDot2 = new DescriptiveStatistics();
        statisticsDot2.setWindowSize(30);

        setRect(point1, point2, threshold);
    }

    public void setRect(Point2D point1, Point2D point2, int threshold) {
        this.noiseThreshold = threshold;
        initializeLine(point1, point2);
    }

    public Rect getRect() {
        return new Rect((int) point1.x, (int) point1.y, (int) point2.x, (int) point2.y);
    }

    private void initializeLine(Point2D point1, Point2D point2) {
        this.point1 = point1;
        this.point2 = point2;

        // check if horizontal or vertical calibration marks (for backward compatibility)
        if (point1.x > point2.x) isVertical = true;
        else isVertical = false;

        calibrationTrack = new LineProfile(point1, point2, CALIBRATION_TRACKS, CALIBRATION_POINTS, width, height, colorFilter);
        Logr.d("PARAMETERS", "CalibrationFinder: (" + point1.x + "," + point1.y + ")  (" + point2.x + "," + point2.y + ")");
    }

    Stopwatch clock = new Stopwatch(" ");
    int BOXCAR = 10;
    int ringcounter = 0;
    boolean firstrun = true;
    private TemporalArraySmoother temporalSmoother = new TemporalArraySmoother(BOXCAR);
    private Barcode barcode = new Barcode(24, 5);

    @Override
    public Calibration find(byte[] grayscale, double distanceMM) {

        clock.tic();

        // get the averaged profile along the calibration lines
        profile = calibrationTrack.getProfile(grayscale, noiseThreshold);

        // run through temporal smoother   /////////// TODO:  Try to remove temporal smoother... causes frequent lagging (decreases FPS)
        profile = temporalSmoother.addAndProcess(profile);

        // run through spatial smoother  /////////// TODO:  Fix spatial smoother... causes frequent lagging (decreases FPS)
        profile = LineProfileUtils.linearMovingAverage(profile, 15);

        // Find local maximas
        maximaVals = new LocalMinMax(profile, MINMAX_ALPHA).getMaxtab();
        maximaIndex = new LocalMinMax(profile, MINMAX_ALPHA).getMaxtabIndex();

        int valsLength = maximaVals.length;
        int indexLength = maximaIndex.length;

        // Check if calibration points found are valid
        if (maximaVals == null || maximaIndex == null || valsLength != indexLength ||
                valsLength < 2 || valsLength > 3) {
            Logr.e("CalibrationFinder", "Calibration Dots Indexes: " + Arrays.toString(maximaIndex));
            Logr.e("CalibrationFinder", "Calibration Dots Values: " + Arrays.toString(maximaVals));
            return mark = null;
        }

        // Refine peaks based on Center of Mass
        Integer[] points = new Integer[valsLength];
        for (int i = 0; i < valsLength; i++) {
            points[i] = LineProfileUtils.centerOfMass(profile, 0, maximaIndex[i] - SPREAD, maximaIndex[i] + SPREAD).intValue();
        }

        // Sort to make sure the indices are lined up in order
        Arrays.sort(points);
        pointsDebug = points;

        // Find actual pixel locations
        if (!isVertical) {  // horizontal calibration points
            float lengthInPx = point2.x - point1.x;
            markerpoint1px = new Point2D(point1.x + points[0] * (lengthInPx / CALIBRATION_POINTS), point1.y);
            markerpoint2px = new Point2D(point1.x + points[1] * (lengthInPx / CALIBRATION_POINTS), point1.y);
            if (points.length == 3) {
                markerpoint3px = new Point2D(point1.x + points[2] * (lengthInPx / CALIBRATION_POINTS), point1.y);
            } else {
                markerpoint3px = null; // no third point available
            }
        } else {  // vertical calibration points
            float lengthInPx = point2.y - point1.y;
            markerpoint1px = new Point2D(point1.x, point1.y + points[0] * (lengthInPx / CALIBRATION_POINTS));
            markerpoint2px = new Point2D(point1.x, point1.y + points[1] * (lengthInPx / CALIBRATION_POINTS));
            if (points.length == 3) {
                markerpoint3px = new Point2D(point1.x, point1.y + points[2] * (lengthInPx / CALIBRATION_POINTS));
            } else {
                markerpoint3px = null; // no third point available
            }
        }

        // Debug report
        if (maximaIndex.length == 2) {
            mDebugInfo.signalQualityCalibrationDots[0] = report.signalQualitySNR(profile, maximaIndex[0], MINMAX_ALPHA);
            mDebugInfo.signalQualityCalibrationDots[1] = report.signalQualitySNR(profile, maximaIndex[1], MINMAX_ALPHA);
        } else if (maximaIndex.length == 3) {
            mDebugInfo.signalQualityCalibrationDots[0] = report.signalQualitySNR(profile, maximaIndex[0], MINMAX_ALPHA);
            mDebugInfo.signalQualityCalibrationDots[1] = report.signalQualitySNR(profile, maximaIndex[1], MINMAX_ALPHA);
            mDebugInfo.signalQualityCalibrationDots[2] = report.signalQualitySNR(profile, maximaIndex[2], MINMAX_ALPHA);
        }

        statisticsDot1.addValue(points[0]);
        statisticsDot2.addValue(points[1]);

        mDebugInfo.standardDeviationCalibrationDotsLast30 = (float) ((statisticsDot1.getStandardDeviation() + statisticsDot1.getStandardDeviation()) / 2);

        clock.toc();

        return mark = new Calibration(markerpoint1px, markerpoint2px, markerpoint3px, distanceMM);
    }

    @Override
    public void writeDebugInfo(final Canvas canvas) {

        final Paint paint = new Paint();
        float plotInc;

        if (isVertical) {
            plotInc = (point2.y - point1.y) / (float) profile.length;
        } else {
            plotInc = (point2.x - point1.x) / (float) profile.length;
        }

        // display coordinates of line points
        paint.setColor(Color.RED);
        calibrationTrack.lineCoordinates.each(new Do() {
            public void process(int valueX, int valueY) {
                canvas.drawPoint(valueX, valueY, paint);
            }
        });

        if (mark != null /*&& success==true*/) {

            paint.setColor(Color.WHITE);
            paint.setTextSize(15);
            canvas.drawText(" calibration track ", point2.x, point2.y + 15, paint);


            // draw line-profile

            paint.setColor(Color.GREEN);


            for (int t = 1; t < profile.length; t++) {
                if (isVertical) {
                    canvas.drawLine(point1.x + profile[t - 1], point1.y + (t - 1) * plotInc, point1.x + profile[t], point1.y + (t) * plotInc, paint);
                } else {
                    canvas.drawLine(point1.x + (t - 1) * plotInc, point1.y - profile[t - 1], point1.x + (t) * plotInc, point1.y - profile[t], paint);
                }
            }

            // draw circles in detected points
            if (isVertical) {

                paint.setColor(Color.YELLOW);
                canvas.drawCircle(mark.p1.x + profile[(int) pointsDebug[0]], mark.p1.y, 5, paint);
                canvas.drawCircle(mark.p2.x + profile[(int) pointsDebug[1]], mark.p2.y, 5, paint);
                if (mark.p3 != null)
                    canvas.drawCircle(mark.p3.x + profile[(int) pointsDebug[2]], mark.p3.y, 5, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(15);
                canvas.drawText(Math.round(mark.p1.y) + "", mark.p1.x + profile[(int) pointsDebug[0]] + 10, mark.p1.y, paint);
                canvas.drawText(Math.round(mark.p2.y) + "", mark.p2.x + profile[(int) pointsDebug[1]] + 10, mark.p2.y, paint);
                if (mark.p3 != null)
                    canvas.drawText(Math.round(mark.p3.y) + "", mark.p3.x + profile[(int) pointsDebug[2]] + 10, mark.p3.y, paint);

            } else {

                paint.setColor(Color.YELLOW);
                canvas.drawCircle(mark.p1.x, mark.p1.y - profile[(int) pointsDebug[0]], 5, paint);
                canvas.drawCircle(mark.p2.x, mark.p2.y - profile[(int) pointsDebug[1]], 5, paint);
                if (mark.p3 != null)
                    canvas.drawCircle(mark.p3.x, mark.p3.y - profile[(int) pointsDebug[2]], 5, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(15);
                canvas.drawText(Math.round(mark.p1.y) + "", mark.p1.x, mark.p1.y - profile[(int) pointsDebug[0]] - 10, paint);
                canvas.drawText(Math.round(mark.p2.y) + "", mark.p2.x, mark.p2.y - profile[(int) pointsDebug[1]] - 10, paint);
                if (mark.p3 != null)
                    canvas.drawText(Math.round(mark.p3.y) + "", mark.p3.x, mark.p3.y - profile[(int) pointsDebug[2]] - 10, paint);

            }

            // report signal quality
            paint.setColor(Color.WHITE);
            paint.setTextSize(20);
            if (maximaIndex.length == 2) {
                canvas.drawText("Calib.Dots Signal: " + report.signalQualitySNR(profile, maximaIndex[0], MINMAX_ALPHA) + "/" +
                        report.signalQualitySNR(profile, maximaIndex[1], MINMAX_ALPHA) + "%", 350, 40, paint);
            } else if (maximaIndex.length == 3) {
                canvas.drawText("Calib.Dots Signal: " + report.signalQualitySNR(profile, maximaIndex[0], MINMAX_ALPHA) + "/" +
                        report.signalQualitySNR(profile, maximaIndex[1], MINMAX_ALPHA) + "/" +
                        report.signalQualitySNR(profile, maximaIndex[2], MINMAX_ALPHA) + "%", 350, 40, paint);
            }

        }

    }

    @Override
    public void release() {
        // nothing
    }

}
