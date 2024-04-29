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

import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.Stopwatch;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

public class SignalFinder {

    private int previewWidth, previewHeight;
    private AutoCalibration calibration;
    private CalibrationTools tools;
    private int search;
    private SignalNormalizer signalNormalizer;
    private DescriptiveStatistics ZBufferPoint1 = new DescriptiveStatistics();
    private DescriptiveStatistics ZBufferPoint2 = new DescriptiveStatistics();
    private DescriptiveStatistics ZBufferPoint3 = new DescriptiveStatistics();
    private Stopwatch watch = new Stopwatch("TIME2");


    public SignalFinder(int width, int height, int searchLength, int targetSignalLevel, int ZBufferLength, AutoCalibration calibrationParameters) {
        previewWidth = width;
        previewHeight = height;
        search = searchLength;
        calibration = calibrationParameters;
        signalNormalizer = new SignalNormalizer(targetSignalLevel);
        tools = new CalibrationTools(previewWidth, previewHeight);
        ZBufferPoint1.setWindowSize(ZBufferLength);
        ZBufferPoint2.setWindowSize(ZBufferLength);
        ZBufferPoint3.setWindowSize(ZBufferLength);
    }

    public ErrorCode process(byte[] frame) {  /// TODO USE THIS METHOD TO DETERMINE IF "NOT ENOUGH LIGHT"

        // find slider box point
        Point2D p1XY = calibration.findSliderPosition(frame);  // TODO this could be pretty slow!
        if (p1XY == null) return ErrorCode.UNDEFINED;
        double p1Y = tools.average(frame, new Rect((int) (p1XY.x - search), (int) (p1XY.y - search), (int) (p1XY.x + search), (int) (p1XY.y + search)));
        ZBufferPoint1.addValue(p1Y);

        // get calibration dots points
        List<Point2D> positions = calibration.getCalibrationDotPositions();
        if (positions == null || positions.size() != 5) return ErrorCode.UNDEFINED;

        Point2D p2XY = positions.get(0);
        double p2Y = tools.average(frame, new Rect((int) (p2XY.x - search), (int) (p2XY.y - search), (int) (p2XY.x + search), (int) (p2XY.y + search)));
        ZBufferPoint2.addValue(p2Y);

        Point2D p3XY = positions.get(4);
        double p3Y = tools.average(frame, new Rect((int) (p3XY.x - search), (int) (p3XY.y - search), (int) (p3XY.x + search), (int) (p3XY.y + search)));
        ZBufferPoint3.addValue(p3Y);

        // define vector points
        Vector3D point1 = new Vector3D(p1XY.x, p1XY.y, ZBufferPoint1.getMean());
        Vector3D point2 = new Vector3D(p2XY.x, p2XY.y, ZBufferPoint2.getMean());
        Vector3D point3 = new Vector3D(p3XY.x, p3XY.y, ZBufferPoint3.getMean());

        // now define signal normalizer
        signalNormalizer.define(point1, point2, point3);

//		Logr.d("SIGNAL",point1.getZ() + "  " + point2.getZ() + "  " + point3.getZ());

        return ErrorCode.SUCCESS;

    }

    public SignalNormalizer getSignalNormalizer() {
        return signalNormalizer;
    }

}
