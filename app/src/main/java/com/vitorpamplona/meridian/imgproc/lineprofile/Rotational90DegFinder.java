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

import com.vitorpamplona.core.testdevice.DeviceDataset;
import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.LineProfile;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.LocalMinMax;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.RegionOfInterest.Do;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.StatisticalReport;
import com.vitorpamplona.meridian.utils.Stopwatch;
import com.vitorpamplona.meridian.utils.YuvFilter;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Rotational90DegFinder implements IAngleFinder {

    private int MINMAX_ALPHA = 20;  // alpha for local min-max method (peak changes below this threshold are ignored)
    private int SPREAD = 10;  // how wide to consider center of mass left/right SPREAD from maxima
    private int CENTER_ERROR_TOLERANCE = 8;  // tolerance for how far ratchet center and theoretical center are apart

    private int ARC_LINES = 4;  // amount of parallel tracks
    private int START_ANGLE = 1;
    private int END_ANGLE = 360;
    private int ARC_POINTS = 360;  // amount of points to compute in one track

    private int TARGET_INTENSITY = 40;

    private Integer[] wProfile = new Integer[3 * ARC_POINTS];

    protected Point2D center;
    private Point2D theoreticalCenter;
    private double outerRadius;
    private double thickness;
    private int noiseThreshold = 10;  // default, overwritten by constructor
    private int width, height;
    private RatchetClick button = new RatchetClick();
    private FrameDebugData mDebugInfo;
    private DescriptiveStatistics statistics;
    private DeviceDataset.Device mDevice;
    private double bestOffset, lastOffset = Double.MAX_VALUE;
    private int ringIntensitySum = 0;
    List<Point2D> satellitePoints = null;
    private CalibrationTools tools;
    private List<Integer> meanSatelliteIntensity = new ArrayList<Integer>();
    private Stopwatch stopwatch = new Stopwatch("WATCH");
    private final YuvFilter colorFilter;

    Rect blackOutZone;

    LineProfile ratchetTrack;
    Double currentAngle = null, lastAngle = null;
    DescriptiveStatistics angleBuffer = new DescriptiveStatistics();
    Double debugAngle;
    Integer[] validIndices;

    public Rotational90DegFinder(DeviceDataset.Device device, FrameDebugData debugInfo, YuvFilter colorFilter) {
        this.width = device.previewFrameSize.WIDTH;
        this.height = device.previewFrameSize.HEIGHT;
        this.center = device.deltaMeridianFromCalibration;
        this.outerRadius = device.meridianRadius;
        this.thickness = device.meridianThickness;
        this.noiseThreshold = device.intensityThreshold;
        this.colorFilter = colorFilter;

        this.mDebugInfo = debugInfo;

        this.mDevice = device;

        this.blackOutZone = new Rect(0, height - 20, width, 2 * height);

        setParameters(center, outerRadius, thickness, satellitePoints, noiseThreshold);

        statistics = new DescriptiveStatistics();
        statistics.setWindowSize(30);

        angleBuffer.setWindowSize(30);

        this.tools = new CalibrationTools(width, height);
    }

    @Override
    public void setCenter(Point2D center) {
        this.center = new Point2D(center.x, center.y);
        initializeArc(this.center, outerRadius, thickness, width, height);
    }

    public void setRadius(int radiusPX) {
        // not yet used
    }

    public void setThickness(int thicknessPX) {
        // not yet used
    }

    public void setParameters(Point2D center, double radiusPX, double thicknessPX, List<Point2D> satellitePoints, int threshold) {
        this.center = center;
        this.outerRadius = radiusPX;
        this.thickness = thicknessPX;
        this.noiseThreshold = threshold;
        this.satellitePoints = satellitePoints;
        this.MINMAX_ALPHA = (threshold < 20) ? 10 : (threshold - 10);  // TODO: need to refactor this code !

        Logr.d("PARAMETERS", "Rotational90DegFinder: (" + center.x + "," + center.y + ")  / " + radiusPX + " / " + thicknessPX + " ");

        initializeArc(this.center, this.outerRadius, this.thickness, width, height);
    }

    private void initializeArc(Point2D center, double outerRadius, double thickness, int width, int height) {
        ratchetTrack = new LineProfile(center, outerRadius, thickness,
                ARC_LINES,
                START_ANGLE,
                END_ANGLE,
                ARC_POINTS,
                width, height,
                colorFilter);
    }

    public void fillWideProfile(Integer[] profile, Integer[] wProfile) {
        int len = profile.length;
        // Wrap profile around for continuity (three profiles back-to-back) This may be quite costly
        System.arraycopy(profile, 0, wProfile, 0, len);
        System.arraycopy(profile, 0, wProfile, len, len);
        System.arraycopy(profile, 0, wProfile, len * 2, len);
    }

    public void cleanOutOfRangeMaximas(Integer[] wMaximaIndex, Integer[] wMaximaVals, int len) {
        for (int n = 0; n < wMaximaIndex.length; n++) {
            if (wMaximaIndex[n] < len || wMaximaIndex[n] > (2 * len - 1)) { // not in center zone
                wMaximaVals[n] = 0;
            }
        }
    }

    public int countPoints(Integer[] wMaximaIndex, Integer[] wMaximaVals, int len) {
        int pointCount = 0;

        if (wMaximaIndex.length != wMaximaVals.length) {
            return pointCount = 0;
        }

        for (int n = 0; n < wMaximaIndex.length; n++) {
            if (wMaximaIndex[n] >= len && wMaximaIndex[n] < 2 * len) {
                pointCount++;
            }
        }

        return pointCount;
    }

    public boolean moreThan2PointsFound(Integer[] wMaximaIndex, Integer[] wMaximaVals, int len) {
        return countPoints(wMaximaIndex, wMaximaVals, len) != 2;
    }

    public int refineIndexViaCenterOfMass(Integer[] wProfile, int mxIndex, int len, int spread) {
        Integer[] subarray = new Integer[2 * spread + 1];

        // faulty array setup, return unrefined indices
        if (mxIndex - spread < 0 || mxIndex + spread >= wProfile.length) {
            return mxIndex - len;
        }

        System.arraycopy(wProfile, mxIndex - spread, subarray, 0, 2 * spread + 1);
        return LineProfileUtils.centerOfMass(subarray, 0, 0, subarray.length).intValue() - spread + mxIndex - len;
    }

    Integer[] profile;
    Integer[] pointsFound;
    Integer[] anglesFound = null;
    DecimalFormat df = new DecimalFormat("#.0");

    @Override
    public ErrorCode find(byte[] grayscale, SignalNormalizer signalNormalizer) {

        bestOffset = Double.NaN;
        ErrorCode Status;

        // Get the profile along the ratchet tracks
        profile = ratchetTrack.getProfile(grayscale, noiseThreshold);

        // Get total sum and length
        ringIntensitySum = ratchetTrack.lineTotalSum();
        int len = profile.length;

        stopwatch.tic();
        // adjust signal intensity
        if (signalNormalizer != null) {
            int x, y;
            for (int i = 0; i < profile.length; i++) {
                x = ratchetTrack.lineCoordinates.posX(0, i);
                y = ratchetTrack.lineCoordinates.posY(0, i);
                profile[i] = (int) (profile[i] * signalNormalizer.factor(x, y));
                //System.out.print(profile[i] + " ");
            }
            //System.out.print("\n");
        }
        stopwatch.toc();


        // Run profile through spatial boxcar smoother
        fillWideProfile(profile, wProfile);
        wProfile = LineProfileUtils.linearMovingAverage(wProfile, 10, 2); // Base 2 to avoid reducing the value below threshold

        //for (int i=0; i<wProfile.length; i++) {
        //	System.out.print(wProfile[i] + " ");
        //}
        //System.out.print("\n");

        // Adjust profile intensity through light metering using satellite points
        if (mDevice.deviceType == DeviceDataset.Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION && satellitePoints != null) {

//			Status = adjustProfileThroughLightMetering(grayscale, wProfile, satellitePoints, meanSatelliteIntensity, 10);
//			if (Status != ErrorCode.SUCCESS && Status != ErrorCode.UNDEFINED) return Status;

            Status = checkIfRatchetIsCentered(grayscale, center, satellitePoints, 10, CENTER_ERROR_TOLERANCE);
//			if (Status != ErrorCode.SUCCESS && Status != ErrorCode.UNDEFINED) return Status;

        }

        // Find local maximas
        Integer[] wMaximaVals = new LocalMinMax(wProfile, MINMAX_ALPHA).getMaxtab();
        Integer[] wMaximaIndex = new LocalMinMax(wProfile, MINMAX_ALPHA).getMaxtabIndex();

        //System.out.println(wMaximaIndex.length);

        if (wMaximaVals.length != wMaximaIndex.length) {
            return ErrorCode.RAF_MIN_MAX_ERROR;
        }
        pointsFound = wMaximaIndex;

        // Isolate indexes in center zone and check if enough points found
        cleanOutOfRangeMaximas(wMaximaIndex, wMaximaVals, len);


        // Device dependent Ratchet Dots
        if (mDevice.deviceType == DeviceDataset.Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA ||
                mDevice.deviceType == DeviceDataset.Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION) {


            // Refine the center of each valid index for accuracy   TODO: check if this really helps or not TODO: 			/// MUST BE SORTED
            List<Integer> refineIndices = new ArrayList<Integer>();
            for (int i = 0; i < wMaximaVals.length; i++) {
                //System.out.println("- " + wMaximaVals[i]);

                if (wMaximaVals[i] > noiseThreshold) {

                    Integer refinedIndex = refineIndexViaCenterOfMass(wProfile, wMaximaIndex[i], len, SPREAD);

                    if (refinedIndex == null) {
                        return ErrorCode.RAF_NULL_INDEX_REFINE;
                    }

                    refineIndices.add(refinedIndex);
                }
            }

            validIndices = refineIndices.toArray(new Integer[refineIndices.size()]);
            pointsFound = validIndices;

            // Force either 2, 3, or 4 valid points
            int amountOfPoints = validIndices.length;
            if (amountOfPoints == 0) {
                return ErrorCode.RAF_NO_DOTS;
            } else if (amountOfPoints == 1) {
                return ErrorCode.RAF_ONE_DOT;
            } else if (amountOfPoints > 4) {
                return ErrorCode.RAF_MORE_THAN_4_DOTS;
            }

            // Use black-out zone to discard border points
            blackOutZone(validIndices, blackOutZone, ratchetTrack);


            // second check
            if (validIndices.length < 2) {
                return ErrorCode.RAF_NOT_ENOUGH_INDEX;
            }

            Integer[] angles = new Integer[validIndices.length];
            // get angle differences between found dots.
            // loop through all found indices (except last)
            for (int i = 0; i < validIndices.length - 1; i++) {
                angles[i] = validIndices[i + 1] - validIndices[i];
            }
            // now deal with last dot
            angles[validIndices.length - 1] = 360 - validIndices[validIndices.length - 1] + validIndices[0];
            anglesFound = angles;

            int tol = 8;
            DescriptiveStatistics ratchetAngleCollection = new DescriptiveStatistics();
            List<Double> angleOffsets = new ArrayList<Double>();

            // loop through angles to find anchor point
            for (int i = 0; i < angles.length; i++) {

                int diffAngle = angles[i]; // test this angular difference
                int indAngle = validIndices[i]; // corresponding true angle

                // angle offsets
                double angle45offset = Math.abs(diffAngle - 45);
                double angle135offset = Math.abs(diffAngle - 135);
                double angle67offset = Math.abs(diffAngle - 67.5);
                double angle112offset = Math.abs(diffAngle - 112.5);

                if (angle45offset < tol) {

                    ratchetAngleCollection.addValue(AngleDiff.angle0to360(indAngle + (45)));
                    angleOffsets.add(angle45offset);  // TODO could use a hashmap for this

                } else if (angle135offset < tol) {

                    ratchetAngleCollection.addValue(AngleDiff.angle0to360(indAngle + (180)));
                    angleOffsets.add(angle135offset);

                } else if (angle67offset < tol) {

                    ratchetAngleCollection.addValue(AngleDiff.angle0to360(indAngle + (180 + 67.5f)));
                    angleOffsets.add(angle67offset);

                } else if (angle112offset < tol) {

                    ratchetAngleCollection.addValue(AngleDiff.angle0to360(indAngle + (0)));
                    angleOffsets.add(angle112offset);

                }
            }

            // check if any valid angles found
            if (angleOffsets.isEmpty()) {
                return ErrorCode.RAF_NO_ACCURA_ANGLES;
            }

            // get most accurate angle
            double angleTemp = Double.MAX_VALUE;
            int mostAccurateAngleIndex = 0;
            for (int i = 0; i < angleOffsets.size(); i++) {
                if (angleOffsets.get(i) < angleTemp) {
                    angleTemp = angleOffsets.get(i);
                    mostAccurateAngleIndex = i;
                }
            }

            bestOffset = angleOffsets.get(mostAccurateAngleIndex);
            if (bestOffset > 7) {
                return ErrorCode.RAF_STD_TOO_HIGH;
            }

            currentAngle = ratchetAngleCollection.getElement(mostAccurateAngleIndex);


            // test the results
            if (currentAngle == null) {
                return ErrorCode.RAF_ANGLE_IS_NULL;
            }
            if (Double.isNaN(currentAngle)) {
                return ErrorCode.RAF_ANGLE_IS_NAN;
            }

            // refine angle
            currentAngle = (double) AngleDiff.angle0to360(currentAngle.floatValue());

            // reverse direction
            currentAngle = 360 - currentAngle;

            // refine angle
            currentAngle = (double) AngleDiff.angle0to360(currentAngle.floatValue());

            // initialize lastAngle
            if (lastAngle == null) {
                lastAngle = currentAngle;
            }

            // forbid large angle changes and large negative changes
            Status = rejectBadAngles(currentAngle.floatValue(), lastAngle.floatValue());
            if (Status != ErrorCode.SUCCESS) {
                Logr.d("BADANGLE", "currentAngle: " + currentAngle + "  lastAngle: " + lastAngle);
                lastAngle = currentAngle;
                return Status;
            }

            // Smoothing buffer
            int RESET_TOL = 8;
            if (AngleDiff.diff360(lastAngle.floatValue(), currentAngle.floatValue()) > RESET_TOL) {
                angleBuffer.clear(); // reset
            }
            angleBuffer.addValue(currentAngle);
            currentAngle = AngleDiff.mean360(angleBuffer.getValues());
            lastAngle = currentAngle;

            // add manual angle offset
            currentAngle -= (45 - 90);

            // refine angle
            currentAngle = (double) AngleDiff.angle0to360(currentAngle.floatValue());

        } else if (mDevice.deviceType == DeviceDataset.Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP ||
                mDevice.deviceType == DeviceDataset.Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE) {


            // Number of valid points
            int validCount = 0;
            for (int i = 0; i < wMaximaVals.length; i++) {
                if (wMaximaVals[i] > 0) {
                    validCount++;
                }
            }

            // Force either 1 or 2 valid points
            if (validCount < 1 || validCount > 2) {
                return ErrorCode.RAF_NOT_1_OR_2_DOTS;
            }

            // Refine the center of each valid index for accuracy
            validIndices = new Integer[validCount];
            int cnt = 0;
            for (int i = 0; i < wMaximaVals.length; i++) {
                if (wMaximaVals[i] > 0) {
                    validIndices[cnt] = refineIndexViaCenterOfMass(wProfile, wMaximaIndex[i], len, SPREAD);
                    cnt++;
                }
            }

            // Deal with 1 or 2 dots
            if (validIndices.length == 1) {

                if (validIndices[0] < 180) {
                    currentAngle = validIndices[0].doubleValue() + 45.0;
                } else {
                    currentAngle = validIndices[0].doubleValue() - 45.0;
                }

            } else if (validIndices.length == 2) {

                // Find correct angle (wrapped)  // TODO: fix these values to be in terms of ARC_POINTS
                double diff = Math.abs(validIndices[0] - validIndices[1]);

                if (diff > 70 && diff < 110) {
                    currentAngle = (validIndices[0] + validIndices[1]) / 2d;
                } else if (diff > 250 && diff < 290) {
                    double aveIndex = (validIndices[0] + validIndices[1] + len) / 2d;
                    if (aveIndex > len) {
                        currentAngle = (aveIndex - len);
                    } else {
                        currentAngle = aveIndex;
                    }
                } else {
                    return ErrorCode.RAF_ANGLE_NOT_VALID;
                }

            }

            // just in case
            if (currentAngle == null) {
                return ErrorCode.RAF_ANGLE_IS_NULL;
            }
            if (Double.isNaN(currentAngle)) {
                return ErrorCode.RAF_ANGLE_IS_NAN;
            }

            // add manual angle offset
            currentAngle -= 90;


            // reverse direction
            currentAngle = 360 - currentAngle;


        } else {
            return ErrorCode.RAF_DEVICE_NOT_SUPPOR;
        }


        // fix angle to 0-360 range
        currentAngle = (double) AngleDiff.angle0to360(currentAngle.floatValue());


        Logr.d("RATCHET", df.format(currentAngle) + "");


        return ErrorCode.SUCCESS;

    }

    public ErrorCode rejectBadAngles(float currentAngle, float lastAngle) {

        int BACK_TOL = 5;
        int FORWARD_TOL = 30;
        float diff, absDiff, trueDiff;

        // guarantee 0-360 range
        currentAngle = AngleDiff.angle0to360(currentAngle);
        lastAngle = AngleDiff.angle0to360(lastAngle);

        // calculate differences
        diff = currentAngle - lastAngle;
        absDiff = Math.abs(diff);
        trueDiff = AngleDiff.diff360(lastAngle, currentAngle);

        // discard angles that are too far apart
        if (trueDiff > FORWARD_TOL) {
            return ErrorCode.RAF_MOVE_TOO_MUCH; // angle difference too large
        }

        // discard angles that go backwards more than BACK_TOL
        if ((diff < 0 && absDiff >= 180 || diff >= 0 && absDiff < 180) && trueDiff > BACK_TOL) {
            return ErrorCode.RAF_MOVE_BACKWARDS; // backward!
        }

        // all good!
        return ErrorCode.SUCCESS;

    }

    private ErrorCode blackOutZone(Integer[] validIndices, Rect blackOutZone, LineProfile ratchetTrack) {

        // Black-out zone:  Dots detected in this zone are discarded.
        List<Integer> keepTheseDots = new ArrayList<Integer>();
        for (Integer idx : validIndices) {

            // fix range (just to be sure)
            idx = (int) AngleDiff.angle0to360(idx);

            // paranoia check
            if (idx < 0 || idx > 359) {
                return ErrorCode.RAF_INVALID_ANGLE;
            }

            // Using outside-most track
            int xPos = ratchetTrack.lineCoordinates.posX(0, idx);
            int yPos = ratchetTrack.lineCoordinates.posY(0, idx);

            if (!(yPos >= blackOutZone.top && yPos <= blackOutZone.bottom &&
                    xPos >= blackOutZone.left && xPos <= blackOutZone.right)) {
                keepTheseDots.add(idx);
            }
        }
        validIndices = keepTheseDots.toArray(new Integer[keepTheseDots.size()]);

        return ErrorCode.SUCCESS;

    }

    private ErrorCode adjustProfileThroughLightMetering(byte[] grayscale, Integer[] profile,
                                                        List<Point2D> satellitePoints,
                                                        List<Integer> meanSatelliteIntensity,
                                                        int box) {
        float Y_INTENSITY_ADJUST = 40f;
        double thirdPointManualIntensity;

        // Get the intensities in the light metering points
        meanSatelliteIntensity.clear();
        for (Point2D dot : satellitePoints) {

            int xpos = (int) Math.round(center.x + dot.x);
            int ypos = (int) Math.round(center.y + dot.y);
            int m = tools.average(grayscale, new Rect(xpos - box, ypos - box, xpos + box, ypos + box));
            meanSatelliteIntensity.add(m);

        }
        if (meanSatelliteIntensity.size() != 2) return ErrorCode.RAF_NOT_ENOUGH_SATEL;

        // Create plane describing intensity manifold
        thirdPointManualIntensity = (meanSatelliteIntensity.get(0) + meanSatelliteIntensity.get(1)) / 2 - Y_INTENSITY_ADJUST;
        thirdPointManualIntensity = (thirdPointManualIntensity < 0) ? 1 : thirdPointManualIntensity;

        Vector3D point1 = new Vector3D(satellitePoints.get(0).x,
                satellitePoints.get(0).y,
                meanSatelliteIntensity.get(0));
        Vector3D point2 = new Vector3D(satellitePoints.get(1).x,
                satellitePoints.get(1).y,
                meanSatelliteIntensity.get(1));
        Vector3D point3 = new Vector3D((satellitePoints.get(0).x + satellitePoints.get(1).x) / 2,
                (satellitePoints.get(0).y + satellitePoints.get(1).y) / 2 + 2 * outerRadius,
                thirdPointManualIntensity);
        Plane manifold = new Plane(point1, point2, point3);


        // Adjust line profile with gradient
        int xpos, ypos, point;
        double intensity;
        for (int i = 0; i < ARC_POINTS; i++) {
            xpos = ratchetTrack.lineCoordinates.posX(0, i);
            ypos = ratchetTrack.lineCoordinates.posY(0, i);

            // find point in plane using x,y coords
            Vector3D XYcoord1 = new Vector3D(xpos, ypos, 0);
            Vector3D XYcoord2 = new Vector3D(xpos, ypos, 255);
            intensity = manifold.intersection(new Line(XYcoord1, XYcoord2)).getZ();

            point = (int) Math.round(profile[i] * TARGET_INTENSITY / intensity);
            profile[i] = (point < 0) ? 0 : (point > 255) ? 255 : point;
        }

        return ErrorCode.SUCCESS;

    }


    private ErrorCode checkIfRatchetIsCentered(byte[] grayscale, Point2D center, List<Point2D> satellitePoints, int box, int tolerance) {

        // do only if both satellite points found during calibration
        if (satellitePoints.size() != 2) return ErrorCode.UNDEFINED;

        List<Point2D> satellitePointCenter = new ArrayList<Point2D>();

        // Get the intensities in the light metering points
        for (Point2D dot : satellitePoints) {

            int xpos = (int) Math.round(center.x + dot.x);
            int ypos = (int) Math.round(center.y + dot.y);
            Point2D centerOfMass = tools.centerOfMass(grayscale, new Rect(xpos - box, ypos - box, xpos + box, ypos + box));
            if (centerOfMass == null) return ErrorCode.RAF_NOT_ENOUGH_SATEL;
            satellitePointCenter.add(centerOfMass);

        }
        if (satellitePointCenter.size() != 2) return ErrorCode.RAF_NOT_ENOUGH_SATEL;

        // Calculate theoretical center position using 90 deg. rotation matrix method
        Point2D p1 = satellitePointCenter.get(0);
        Point2D p2 = satellitePointCenter.get(1);
        Point2D between = p1.add(p2).divide(2);
        theoreticalCenter = new Point2D(between.x + p2.y - between.y, between.y - p2.x + between.x);

        double distance = Math.sqrt(Math.pow(center.x - theoreticalCenter.x, 2) + Math.pow(center.y - theoreticalCenter.y, 2));

        if (distance > tolerance) {
            return ErrorCode.RAF_CENTER_IS_OFF;
        } else {
            return ErrorCode.SUCCESS;
        }

    }


    public void drawCentralized(String txt, float x, float y, Canvas canvas, Paint paint) {
        float width = paint.measureText(txt);
        float height = paint.getTextSize();
        canvas.drawText(txt, x - width / 2, y - height, paint);
    }

    StatisticalReport report = new StatisticalReport();

    @Override
    public void writeDebugInfo(final Canvas canvas) {

        final Paint paint = new Paint();

        paint.setColor(Color.WHITE);
        paint.setTextSize(15);
        canvas.drawText(" ratchet track ", (float) (center.x - outerRadius), (float) (center.y + outerRadius + 15), paint);

        // display coordinates of line points
        paint.setColor(Color.RED);
        ratchetTrack.lineCoordinates.each(new Do() {
            public void process(int valueX, int valueY) {
                canvas.drawPoint(valueX, valueY, paint);
            }
        });

        // draw center
        paint.setColor(Color.WHITE);
        canvas.drawCircle(center.x, center.y, 2, paint);
        paint.setTextSize(15);
        String toDraw = "(" + (int) center.x + "," + (int) center.y + ")";
        drawCentralized(toDraw, center.x, center.y, canvas, paint);

        // draw ratchet line-profile inside tracks (fancy!)
        double innerRadius = outerRadius - thickness;
        float thetaInc = 360 / ARC_POINTS, theta1, theta2, x1, x2, y1, y2, rx, ry;
        double r1, r2;
        float thetaShift = -90; // rotate the data plot around center (+ is clockwise)

        Integer[] profileForPlot = wProfile;
        for (int t = 2 * ARC_POINTS - 1; t >= ARC_POINTS; t--) { // reverse
            theta1 = thetaInc * (t - 1) + thetaShift;
            theta2 = thetaInc * t + thetaShift;

            if (profileForPlot[t - 1] == null)
                continue;
            if (profileForPlot[t] == null)
                continue;

            r1 = (innerRadius - profileForPlot[t - 1]);
            r2 = (innerRadius - profileForPlot[t]);
            r1 = (r1 < 0) ? 0 : r1;
            r2 = (r2 < 0) ? 0 : r2;
            x1 = (float) (center.x + r1 * Math.cos(Math.toRadians(theta1)));
            x2 = (float) (center.x + r2 * Math.cos(Math.toRadians(theta2)));
            y1 = (float) (center.y + r1 * Math.sin(Math.toRadians(theta1)));
            y2 = (float) (center.y + r2 * Math.sin(Math.toRadians(theta2)));
            paint.setColor(Color.GREEN);
            canvas.drawLine(x1, y1, x2, y2, paint);

            rx = (float) (center.x + (innerRadius - noiseThreshold) * Math.cos(Math.toRadians(theta1)));
            ry = (float) (center.y + (innerRadius - noiseThreshold) * Math.sin(Math.toRadians(theta1)));
            paint.setColor(Color.MAGENTA);
            canvas.drawPoint(rx, ry, paint);
        }

        // draw dot for detected points
        if (pointsFound != null && pointsFound.length > 0) {

            paint.setColor(Color.RED);
            for (int pos : pointsFound) {
                canvas.drawCircle(ratchetTrack.lineCoordinates.posX(ARC_LINES - 1, (int) AngleDiff.angle0to360((int) Math.round(pos))),
                        ratchetTrack.lineCoordinates.posY(ARC_LINES - 1, (int) AngleDiff.angle0to360((int) Math.round(pos))), 4, paint);
            }

        }

        if (currentAngle != null) {

            // draw dot for detected ratchet position
            paint.setColor(Color.YELLOW);
            canvas.drawCircle(ratchetTrack.lineCoordinates.posX(ARC_LINES - 1, (int) AngleDiff.angle0to360((int) Math.round(360 - currentAngle) + thetaShift)),
                    ratchetTrack.lineCoordinates.posY(ARC_LINES - 1, (int) AngleDiff.angle0to360((int) Math.round(360 - currentAngle) + thetaShift)), 8, paint);

        }

        // draw satellite dots
        if (satellitePoints != null && satellitePoints.size() == 2) {

            paint.setTextSize(15);
            for (int i = 0; i < satellitePoints.size(); i++) {
                int x = Math.round(center.x + satellitePoints.get(i).x);
                int y = Math.round(center.y + satellitePoints.get(i).y);
                paint.setColor(Color.MAGENTA);
                paint.setStrokeWidth(3);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(x, y, 10, paint);

                if (meanSatelliteIntensity.size() == 2) {
                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(1);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawText(meanSatelliteIntensity.get(i) + "", x + 13, y, paint);
                }

            }

        }

//		// draw blackout box
//		paint.setStyle(Paint.Style.FILL);
//		paint.setColor(Color.RED);
//	    paint.setAlpha(100);
//		canvas.drawRect(blackOutZone,paint);
//	    paint.setAlpha(255);

        // draw theoretical center calculated by satellite dots
        if (theoreticalCenter != null && center != null) {
            paint.setColor(Color.RED);
            canvas.drawLine(center.x, center.y, theoreticalCenter.x, theoreticalCenter.y, paint);
            canvas.drawCircle(theoreticalCenter.x, theoreticalCenter.y, 4, paint);
            double centerOffset = Math.sqrt(Math.pow(center.x - theoreticalCenter.x, 2) + Math.pow(center.y - theoreticalCenter.y, 2));

            // display offset number
            paint.setStrokeWidth(1);
            paint.setTextSize(14);
            paint.setColor(Color.WHITE);
            canvas.drawText("center offset: " + Math.round(centerOffset), 320, 44, paint);
        }

        // draw angle offset (accuracy)
        paint.setStrokeWidth(1);
        paint.setTextSize(14);
        paint.setColor(Color.WHITE);
        canvas.drawText("best angle variance: " + bestOffset, 320, 12, paint);

        // draw angle offset (accuracy)
        paint.setStrokeWidth(1);
        paint.setTextSize(14);
        paint.setColor(Color.WHITE);
        canvas.drawText("angles: " + Arrays.toString(anglesFound), 320, 28, paint);

    }

    @Override
    public boolean isReady() {
        return ratchetTrack != null;
    }

    @Override
    public Double getWheelPosition() {
        return currentAngle;
    }

    public int getRingSum() {
        return ringIntensitySum;
    }

}
