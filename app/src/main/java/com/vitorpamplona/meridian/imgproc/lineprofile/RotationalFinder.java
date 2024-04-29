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

import com.vitorpamplona.core.testdevice.DeviceDataset.CalibrationType;
import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.LineProfile;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.RegionOfInterest.Do;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.StatisticalReport;
import com.vitorpamplona.meridian.utils.YuvFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;


public class RotationalFinder implements IAngleFinder {

    private static final int MINMAX_ALPHA = 10;  // alpha for local min-max method (peak changes below this threshold are ignored)
    private int ARC_LINES;  // amount of parallel tracks
    private int ARC_POINTS;  // amount of points to compute in one track
    private int ANGLE_BUMPER; // degrees to look beyond line ends
    private int START_ANGLE;
    private int END_ANGLE;

    private LineProfile scrollyTrack;
    private Integer[] profileWithBumper;
    private Integer[] previousProfileWithBumper;
    private int noiseThreshold = 10;  // default
    private YuvFilter colorFilter;
    private Float angularPosition = null;
    private int width, height;

    private FrameDebugData mDebugInfo;
    private DescriptiveStatistics statistics;


    public RotationalFinder(Point2D center, int outerRadius, int thickness, int threshold, int width, int height, CalibrationType calibrationType, FrameDebugData debugInfo, YuvFilter colorFilter) {

        this.width = width;
        this.height = height;
        this.mDebugInfo = debugInfo;
        this.colorFilter = colorFilter;

        ARC_LINES = 2;
        ANGLE_BUMPER = 180; // degrees to look beyond line ends
        START_ANGLE = 1 - ANGLE_BUMPER;
        END_ANGLE = 360 + ANGLE_BUMPER;
        ARC_POINTS = 360;

        statistics = new DescriptiveStatistics();
        statistics.setWindowSize(30);

        profileWithBumper = new Integer[ARC_POINTS + 2 * ANGLE_BUMPER];
        //previousProfileWithBumper = new Integer[ARC_POINTS+2*ANGLE_BUMPER];

        setParameters(center, outerRadius, thickness, null, threshold);
    }

    public void setParameters(Point2D center, double radiusPX, double thicknessPX, List<Point2D> satellitePoints, int threshold) {
        this.noiseThreshold = threshold;
        initializeArc(center, radiusPX, thicknessPX);
    }

    private void initializeArc(Point2D center, double outerRadius, double thickness) {
        scrollyTrack = new LineProfile(center, outerRadius, thickness,
                ARC_LINES,
                START_ANGLE,
                END_ANGLE,
                ARC_POINTS + 2 * ANGLE_BUMPER,
                width, height, colorFilter);
        Logr.d("PARAMETERS", "RotationalFinder: (" + center.x + "," + center.y + ")  / " + outerRadius + " / " + thickness + " ");
    }

    @Override
    public ErrorCode find(byte[] grayscale, SignalNormalizer signalNormalizer) {

        // get profile of inside and outside scrolly track
        profileWithBumper = scrollyTrack.getProfile(grayscale, noiseThreshold);

        // run profile through spatial boxcar smoother
        //profileWithBumper = LineProfileUtils.singlePoleLowpassFilter(profileWithBumper, 0.93f);
        profileWithBumper = LineProfileUtils.linearMovingAverage(profileWithBumper, 20);

        // temporal smoothing.
        if (previousProfileWithBumper != null) {
            for (int i = 0; i < profileWithBumper.length; i++) {
                profileWithBumper[i] += previousProfileWithBumper[i];
                profileWithBumper[i] /= 2;
            }
        }

        // find maxima index within valid zone (between the left and right bumper)
        int startIndex = ANGLE_BUMPER;
        int endIndex = profileWithBumper.length - ANGLE_BUMPER;
        int maximaIndex = LineProfileUtils.maximaIndex(profileWithBumper, startIndex, endIndex).intValue();
        angularPosition = (float) (maximaIndex - ANGLE_BUMPER);

        // Debug report
        mDebugInfo.signalQualityScrolly = report.signalQualitySNR(profileWithBumper, Math.round(angularPosition), MINMAX_ALPHA);
        statistics.addValue(angularPosition);
        mDebugInfo.standardDeviationScrollyLast30 = (float) statistics.getStandardDeviation();

        previousProfileWithBumper = profileWithBumper;

        // just in case
        if (angularPosition == null) {
            return ErrorCode.SCF_ANGLE_IS_NULL;
        }
        if (Double.isNaN(angularPosition)) {
            return ErrorCode.SCF_ANGLE_IS_NAN;
        }

        // flip scroll direction
        angularPosition = 360 - angularPosition;

        // fix angle range between 0-359
        angularPosition = AngleDiff.angle0to360(angularPosition);

        Logr.d("SCROLLY", Math.round(angularPosition) + "");


        return ErrorCode.SUCCESS;
    }


    //// this here is to plot to debug screen
    StatisticalReport report = new StatisticalReport();

    @Override
    public void writeDebugInfo(final Canvas canvas) {   //  Debug of the scrolly wheel is super huge right now.  Once we fully switch to single dot, I will refactor

        final Paint paint = new Paint();

        // display scrolly profile
        if (angularPosition != null) {

            float points = ARC_POINTS;
            int plotwidth = 200;
            Point2D startposition = new Point2D(100, 110);
            paint.setColor(Color.WHITE);
            paint.setTextSize(15);
            canvas.drawText(" scrolly track ", startposition.x, startposition.y + 15, paint);


            // display coordinates of line points
            paint.setColor(Color.RED);
            scrollyTrack.lineCoordinates.each(new Do() {
                public void process(int valueX, int valueY) {
                    canvas.drawPoint(valueX, valueY, paint);
                }
            });

            // scrolly profile
            paint.setColor(Color.GREEN);

            for (int t = ANGLE_BUMPER; t < (points + ANGLE_BUMPER) - 1; t++) { // inside track
                int idx = Math.round(startposition.x + (t - ANGLE_BUMPER) * plotwidth / points);
                canvas.drawLine(idx, startposition.y - profileWithBumper[t], idx + 1, startposition.y - profileWithBumper[t + 1], paint);
            }

            // plot the dot
            if (angularPosition != null) {
                paint.setColor(Color.YELLOW);
                int idx = Math.round(angularPosition + ANGLE_BUMPER);
                canvas.drawCircle(scrollyTrack.lineCoordinates.posX(0, idx), scrollyTrack.lineCoordinates.posY(0, idx), 5, paint);
                int idxPlot = Math.round(startposition.x + (idx - ANGLE_BUMPER) * plotwidth / points);
                canvas.drawCircle(idxPlot, startposition.y - profileWithBumper[idx], 5, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(Math.round(angularPosition) + "", idxPlot + 10, startposition.y - profileWithBumper[idx] + 5, paint);
            }


        }

    }

    @Override
    public void setCenter(Point2D center) {

    }

    @Override
    public void setRadius(int radiusPX) {

    }

    @Override
    public void setThickness(int thicknessPX) {

    }

    @Override
    public boolean isReady() {
        return true;  // always ready, baby
    }

    @Override
    public Double getWheelPosition() {
        return angularPosition.doubleValue();
    }

}







