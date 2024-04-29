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

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.utils.Histogram;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.LocalMinMax;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.Stopwatch;
import com.vitorpamplona.meridian.utils.YuvConverter;
import com.vitorpamplona.meridian.utils.YuvDraw;
import com.vitorpamplona.meridian.utils.YuvPixel;

import java.util.ArrayList;
import java.util.List;

public class AutoCalibration {

    private boolean completed = false;


    // parameters
    private double RED_CHROMA_CHANNEL_CUT = 0.6;
    private int CENTER_REFINEMENT_SEARCH = 15; // in px
    private int CALIBRATION_BOX_WIDTH = 20;
    private int MIN_MAX_ALPHA = 40;
    private float MIN_MAX_ALPHA_CUT = 0.55f;
    private int FIRST_BUMP_THRESHOLD = 30;
    private int SATELLITE_SCAN_LENGTH = 35;

    // setup
    private CalibrationTools tools;
    private YuvPixel pixel;
    private YuvDraw yuvdraw;
    private int width;
    private int height;
    private byte[] Y, U, V;
    private byte[] Yc, Uc, Vc;
    private Rect CalibrationBox = null;
    private Circle RatchetParameters = null;
    private Integer[] topMarkersX = null;
    private Integer[] topMarkersY = null;
    private Circle ScrollyParameters = null;
    private Rect SliderBox = null;
    private Point2D SliderPosition = null;
    private Float sliderRatchetOffset = null;
    private byte[] previousFrame, currentFrame;
    private Device mDevice;
    private double calibrationFeatureLengthPx = 1; // default calibration feature length
    private int deviceNumber = 0;  // default
    private Stopwatch stopwatch = new Stopwatch("STATIC");
    private List<Point2D> satelliteDots = new ArrayList<Point2D>();
    private List<Point2D> calibrationDots;
    private Histogram hist;
    private byte[] processedFrame;

    public AutoCalibration(int width, int height, Device device) {
        this.width = width;
        this.height = height;
        this.tools = new CalibrationTools(width, height);
        this.pixel = new YuvPixel(width, height);
        this.yuvdraw = new YuvDraw(width, height);
        this.previousFrame = new byte[width * height];
        this.mDevice = device;
        this.hist = new Histogram(width, height);
    }


    public void process(byte[] data) {

        // Extract YUV data
        log("\n(1) Extract YUV data ");
        stopwatch.tic();
        Y = new byte[width * height];
        U = new byte[width * height];
        V = new byte[width * height];
        YuvConverter.toByteArrays(data, Y, U, V, width, height);


        // Normalize images
        int[] cdf;
        int min;
        int max;

        min = tools.minima(Y);
        max = tools.maxima(Y);
        tools.normalizeByteArray(Y, min, max);  // use basic min-max normalize

        min = tools.minima(U);
        max = tools.maxima(U);
        tools.normalizeByteArray(U, min, max);  // use basic min-max normalize

        cdf = hist.cumulativeDistributionFunctionLUT(V);
        min = hist.getThresholdAtPercentile(cdf, 2);
        max = hist.getThresholdAtPercentile(cdf, 98);
        tools.normalizeByteArray(V, min, max);  // discard top-bottom 2% pixels to normalize

        // Clone images to write on for debug display
        Yc = Y.clone();
        Uc = U.clone();
        Vc = V.clone();
        stopwatch.toc();

        processedFrame = Yc;

        // Find the calibration box
        log("(2) Find calibration box ");
        stopwatch.tic();
        if ((setCalibrationBox(findCalibrationBox(Y))) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();
        yuvdraw.drawBox(Yc, getCalibrationBox());


        // Find the ratchet parameters
        log("(3) Find ratchet parameters ");
        stopwatch.tic();
        if ((setRatchetParameters(findRatchetParameters(V))) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Estimate the scrolly/slider y-location
        log("(4) Top markers estimate ");
        stopwatch.tic();
        if (estimateTopMarkersPosition(V) == false) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Find the scrolly parameters
        log("(5) Find scrolly parameters ");
        stopwatch.tic();
        if ((setScrollyParameters(findScrollyParameters(V))) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Find the slider box
        log("(6) Find slider box ");
        stopwatch.tic();
        if ((setSliderBox(findSliderBox(V))) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Find the slider position in box
        log("(7) Find slider position ");
        stopwatch.tic();
        if ((setSliderPosition(findSliderPosition(Y))) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Calculate the pixel offset between PD slider and ratchet center
        log("(8) Calculate slider-ratchet offset ");
        stopwatch.tic();
        if (setSliderRatchetOffsetPx(calculateSliderRatchetOffset(SliderPosition, RatchetParameters)) == null) {
            log("failed.\n");
            completed = false;
            return;
        }
        stopwatch.toc();


        // Find satellite dots
        if (mDevice.deviceType == Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION) {
            log("(9) (Optional) Find satellite dots ");
            stopwatch.tic();
            List<Point2D> dots = findSatelliteDots(Y, RatchetParameters.center, RatchetParameters.radius, SATELLITE_SCAN_LENGTH);
            if (dots == null) log("not found");
            setSatelliteDots(dots);
            stopwatch.toc();
        }


        // Passed all checks, so calibration is done. yay!
        completed = true;

    }


    private Float calculateSliderRatchetOffset(Point2D sliderPosition, Circle ratchetParameters) {
        return (sliderPosition == null || ratchetParameters == null) ? null : (ratchetParameters.center.x - sliderPosition.x);
    }

    public class Circle {

        public Point2D center;
        public int radius;
        public int thickness;

        public Circle(Point2D center, int radius, int thickness) {
            this.center = center;
            this.radius = radius;
            this.thickness = thickness;
        }

        public String toString() {
            return "[" + center.x + ", " + center.y + "]" + radius + " " + thickness;
        }
    }

    private boolean estimateTopMarkersPosition(byte[] frame) {

        // set everything below threshold to 0
        tools.cropByteArray(frame, (int) (255 * (RED_CHROMA_CHANNEL_CUT + 0.1)));
        tools.cropByteArray(Vc, (int) (255 * (RED_CHROMA_CHANNEL_CUT + 0.1)));

        // define search box
        Rect markerBox = new Rect(0,
                (int) (getRatchetParameters().center.y - 2.5 * getRatchetParameters().radius),
                (int) (getRatchetParameters().center.x + 3 * getRatchetParameters().radius),
                (int) (getRatchetParameters().center.y - 1.2 * getRatchetParameters().radius));

        yuvdraw.drawBox(Yc, markerBox);

        // estimate x-location of markers using projection
        Integer[] Xprojection = tools.projectionX(frame, markerBox);
        if (Xprojection == null || Xprojection[0] == null || Xprojection[Xprojection.length - 1] == null || Xprojection.length == 0)
            return false;
        topMarkersX = new LocalMinMax(Xprojection, MIN_MAX_ALPHA).getMaxtabIndex();
        topMarkersY = new Integer[topMarkersX.length];

        for (int i = 0; i < topMarkersX.length; i++) {
            topMarkersX[i] += markerBox.left;
            Integer[] line = tools.getLineY(frame, new Point2D(topMarkersX[i], markerBox.top), new Point2D(topMarkersX[i], markerBox.bottom));
            topMarkersY[i] = Math.round(LineProfileUtils.centerOfMass(line)) + markerBox.top;

            yuvdraw.drawCircle(Yc, new Point2D(topMarkersX[i], topMarkersY[i]), 10);
        }

        // should be three
        return (topMarkersX.length == 3) ? true : false;
    }


    private Rect findSliderBox(byte[] frame) {

        // define estimate of slider markers
        Point2D sliderLeftEstimate = new Point2D(topMarkersX[0], topMarkersY[0]);
        Point2D sliderRightEstimate = new Point2D(topMarkersX[1], topMarkersY[1]);

        // refine estimate of slider markers
        Point2D sliderLeftRefined = tools.refineCenter(frame, sliderLeftEstimate, CENTER_REFINEMENT_SEARCH + 10);
        Point2D sliderRightRefined = tools.refineCenter(frame, sliderRightEstimate, CENTER_REFINEMENT_SEARCH + 10);

        // check if refined points are valid
        if (sliderLeftRefined == null || sliderRightRefined == null) {
            log("Error 1");
            return null;
        }

        yuvdraw.drawCircle(Yc, sliderLeftRefined, 10);
        yuvdraw.drawCircle(Yc, sliderRightRefined, 10);

        // define guessed slider box
        Rect SliderBoxGuess = new Rect((int) sliderLeftRefined.x - 20,
                (int) sliderLeftRefined.y + 10,  // hard set values
                (int) sliderRightRefined.x + 20,
                (int) (getRatchetParameters().center.y - 1.1 * getRatchetParameters().radius));  // hard set values

        // search for slider inside box
        Point2D sliderPos = tools.maximaIndex(Y, SliderBoxGuess);
        sliderPos = tools.refineCenter(Y, sliderPos, CENTER_REFINEMENT_SEARCH + 10);
        if (sliderPos == null) {
            log("Error 1");
            return null;
        }

        Rect SliderBox = new Rect(SliderBoxGuess.left, ((int) sliderPos.y) - 10, SliderBoxGuess.right, ((int) sliderPos.y) + 10);
        yuvdraw.drawBox(Yc, SliderBox);

        return SliderBox;
    }


    public Point2D findSliderPosition(byte[] frame) {
        // search for slider inside box
        int min = tools.minima(frame, SliderBox);
        int max = tools.maxima(frame, SliderBox);
        int threshold = Math.round((max - min) * MIN_MAX_ALPHA_CUT);
        Point2D sliderPos = tools.centerOfMass(frame, SliderBox, threshold);
        sliderPos = tools.refineCenter(frame, sliderPos, CENTER_REFINEMENT_SEARCH); // 1st iteration
        sliderPos = tools.refineCenter(frame, sliderPos, CENTER_REFINEMENT_SEARCH); // 2nd iteration
        if (sliderPos == null) {
            log("Error 1");
            return null;
        }
        yuvdraw.drawCircle(Yc, sliderPos, 10);

        return sliderPos;
    }


    private Circle findScrollyParameters(byte[] frame) {

        // define estimate of scrolly center
        Point2D scrollyEstimate = new Point2D(topMarkersX[2], topMarkersY[2]);

        // refine center of scrolly
        Point2D ScrollyWheelRefined = tools.refineCenter(frame, scrollyEstimate, CENTER_REFINEMENT_SEARCH);
        if (ScrollyWheelRefined == null) return null;
        yuvdraw.drawCircle(Yc, ScrollyWheelRefined, 10);

        return new Circle(ScrollyWheelRefined, 32, 4);  // hard set values for now
    }


    private Circle findRatchetParameters(byte[] frame) {

        // set everything below threshold to 0
        tools.cropByteArray(frame, (int) (255 * RED_CHROMA_CHANNEL_CUT));

        // define search box (defined by Calibration box)
        Rect searchBox;
        switch (mDevice.deviceType) {

            case Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MVP:
                searchBox = new Rect(0, getCalibrationBox().top, getCalibrationBox().left, height);
                break;

            case Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION:
            case Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA:
                searchBox = new Rect(0, getCalibrationBox().centerY() - 75, getCalibrationBox().left, height - 0);
                break;

            default:
                searchBox = new Rect(0, getCalibrationBox().top, getCalibrationBox().left, height);
                break;
        }

        yuvdraw.drawBox(Yc, searchBox);

        // first guess of ratchet center, based on center of mass of four dots
        Point2D FirstGuess = tools.centerOfMass(frame, searchBox);
        if (FirstGuess == null) return null;


//		tools.toBlackAndWhiteByteArray(frame, (int) (255*RED_CHROMA_CHANNEL_CUT));
        tools.cropByteArray(frame, (int) (255 * RED_CHROMA_CHANNEL_CUT));
        Point2D RatchetCenter = tools.bubbleFit(frame, FirstGuess, searchBox);
        if (RatchetCenter == null) return null;

        int radius = Math.round(tools.radius);

        for (int i = 0; i < tools.bubbleCenters.size(); i++) {
            yuvdraw.drawCircle(Yc, tools.bubbleCenters.get(i), 2);
            yuvdraw.drawCircle(Yc, tools.bubbleCenters.get(i), tools.bubbleRadii.get(i));
            yuvdraw.drawCircle(Vc, tools.bubbleCenters.get(i), 2);
            yuvdraw.drawCircle(Vc, tools.bubbleCenters.get(i), tools.bubbleRadii.get(i));
        }
        tools.bubbleCenters.clear();
        tools.bubbleRadii.clear();


        yuvdraw.drawCircle(Yc, RatchetCenter, 2);
        yuvdraw.drawCircle(Yc, RatchetCenter, radius + 24 - 16);
        yuvdraw.drawCircle(Yc, RatchetCenter, radius + 24);

//		return new Circle(new Point2D(RatchetCenter.x,RatchetCenter.y),radius+20,16);  // OLD RADIUS UNTIL 5/9/2014
        return new Circle(new Point2D(RatchetCenter.x, RatchetCenter.y), radius + 24, 16);  // thickness is hard set, radius is offset to account for outer ring radius
    }

    private Barcode barcode = new Barcode(24, 5);
    public Rect calibDots;
    public Rect calibBarcode;
    public Integer[] bumps;

    private Rect findCalibrationBox(byte[] frame) {

        // collapse y axis to sum
        Integer[] projection = tools.normalizedProjectionXreverse(frame, 255);

        // filter projection to remove low-frequency offsets
        projection = tools.singlePoleHighpassFilter(projection, 0.93f);

        // find first bump on projection which should be that of the calibration dots
        bumps = new LocalMinMax(projection, FIRST_BUMP_THRESHOLD).getMaxtabIndex();

        // quick check if projection is legit
        if (bumps.length == 0 || bumps == null) {
            log("\nNo bumps in line projection");
            return null;
        }

        // try the first five spots.
        Rect searchBox = null;
        int numTries = 0;
        while (searchBox == null && numTries < 5 && numTries < bumps.length) {
            int tryingBump = bumps[numTries];

            // calculate first guess of calibration dots x-location
            int xLocationOfCalibrationDots = width - tryingBump;  // (width - index), because it's inverted on x-axis

            // try finding barcode
            boolean barcode = lookForBarcode(frame, xLocationOfCalibrationDots);

            // try finding dots
            if ((searchBox = lookForDots(frame, xLocationOfCalibrationDots)) != null) {
                // DEBUG
                calibBarcode = new Rect(xLocationOfCalibrationDots - 160, 0, xLocationOfCalibrationDots, height);
                calibDots = new Rect(xLocationOfCalibrationDots, 0, xLocationOfCalibrationDots, height);

                yuvdraw.drawBox(Yc, calibDots);

                return searchBox;
            }

            numTries++;
        }
        // none found
        return searchBox;

    }


    private boolean lookForBarcode(byte[] frame, int xLocation) {

        Rect searchBox = new Rect(xLocation - 160, 0, xLocation, height);
        yuvdraw.drawBox(Yc, searchBox);

        int code = 0;

        // scan lines within search box
        for (int p = searchBox.left; p <= searchBox.right; p++) {

            // get scan line
            Integer[] line = tools.getLineY(frame, new Point2D(p, 0), new Point2D(p, height));

            // invert
            Integer[] lineInverse = new Integer[line.length];
            for (int i = 0; i < line.length; i++) {
                lineInverse[line.length - 1 - i] = line[i];
            }

            // decode profile in line
            code = barcode.decode(lineInverse);

            // check what was found
            if (code > 0) {

                log("Barcode found, value: " + code);

                // set device number from barcode value
                deviceNumber = code;

                return true;
            }

        }

        // if no barcode found, return null :-(
        log("No barcode found" + code);
        return false;

    }

    private Rect lookForDots(byte[] frame, int xLocation) {

        // find the calibration dots
        Integer[] line = tools.getLineY(frame, new Point2D(xLocation, 0), new Point2D(xLocation, height));
        float max = LineProfileUtils.maximaValue(line);
        float min = LineProfileUtils.minimaValue(line);
        int threshold = Math.round((max - min) * MIN_MAX_ALPHA_CUT);
        Integer[] indexOfBumpsInY = new LocalMinMax(line, threshold).getMaxtabIndex();
        Integer numberOfBumpsInY = indexOfBumpsInY.length;

        // check if either two or three calibration dots were found
        switch (mDevice.deviceType) {

            case Device.DEVICE_TYPE_SCROLL_CAMERA_INJECTION:
            case Device.DEVICE_TYPE_SCROLL_CAMERA_INHOUSE_MANTA:
                if (indexOfBumpsInY == null || numberOfBumpsInY != 5) {
                    log("\n" + numberOfBumpsInY + " calibration dots found (should be 5)");
                    return null;
                }
                break;

            default:
                if (indexOfBumpsInY == null || numberOfBumpsInY < 2 || numberOfBumpsInY > 3) {
                    log("\n" + numberOfBumpsInY + " calibration dots found (should be 2 or 3)");
                    return null;
                }
                break;

        }

        // define and refine the calibration dots
        List<Point2D> dots = new ArrayList<Point2D>();
        for (int i = 0; i < numberOfBumpsInY; i++) {
            // first guess of a dot position
            Point2D dotPositionEstimate = new Point2D(xLocation, indexOfBumpsInY[i]);
            // center refinement of a dot position
            Point2D refinedPositionEstimate = dotPositionEstimate;
            refinedPositionEstimate = tools.refineCenter(frame, refinedPositionEstimate, CENTER_REFINEMENT_SEARCH); // 1st iteration
            refinedPositionEstimate = tools.refineCenter(frame, refinedPositionEstimate, CENTER_REFINEMENT_SEARCH); // 2nd iteration
            // safety check
            if (refinedPositionEstimate == null) {
                log("\nCalibration dot number " + i + " is bad");
                return null;
            }
            // add to found calibration dot collection (either 2 or 3)
            dots.add(refinedPositionEstimate);
            // draw dots for debug
            yuvdraw.drawCircle(Yc, dots.get(i), 10);
        }

        // save dot position to public list
        setCalibrationDotPositions(dots);

        // set pixel length from dots for pixel/millimeter calibration
        calibrationFeatureLengthPx = Math.abs(dots.get(0).y - dots.get(1).y);

        // define calibration dot box (from top dot to bottom of image
        return new Rect((int) dots.get(0).x - CALIBRATION_BOX_WIDTH / 2,    // left
                (int) dots.get(0).y - CALIBRATION_BOX_WIDTH,        // top
                (int) dots.get(0).x + CALIBRATION_BOX_WIDTH / 2 - 1,    // right
                (int) height);                                                // bottom
    }


    // find the satellite dots in the top left and top right corner of the ratchet center
    // (return is the position relative to ratchet center);
    private List<Point2D> findSatelliteDots(byte[] frame, Point2D centerPosition, int radius, int scanLength) {

        int R_OFF = 15;
        List<Point2D> dots = new ArrayList<Point2D>();
        Integer[] line1 = new Integer[scanLength];
        Integer[] line2 = new Integer[scanLength];
        Integer idx;
        int x, y;
        float min, max;
        int threshold;

        // (0 deg is horizontal on right; counter-clock-wise)
        for (int i = 0; i < scanLength; i++) {

            // dot at 45 deg
            x = (int) Math.round(centerPosition.x + (radius + R_OFF + i) * Math.cos(Math.toRadians(45)));
            y = (int) Math.round(centerPosition.y - (radius + R_OFF + i) * Math.sin(Math.toRadians(45)));
            line1[i] = 0xff & (int) pixel.getY(frame, x, y);
            yuvdraw.drawCircle(Yc, new Point2D(x, y), 1);

            // dot at 135 deg
            x = (int) Math.round(centerPosition.x + (radius + R_OFF + i) * Math.cos(Math.toRadians(135)));
            y = (int) Math.round(centerPosition.y - (radius + R_OFF + i) * Math.sin(Math.toRadians(135)));
            line2[i] = 0xff & (int) pixel.getY(frame, x, y);
            yuvdraw.drawCircle(Yc, new Point2D(x, y), 1);

        }

        min = LineProfileUtils.minimaValue(line1);
        max = LineProfileUtils.maximaValue(line1);
        threshold = Math.round((max - min) * 0.80f);
        idx = Math.round(LineProfileUtils.centerOfMass(line1, threshold));
        if (idx == null) return null;
        Point2D tempPoint1 = new Point2D(Math.round(centerPosition.x + (radius + R_OFF + idx) * Math.cos(Math.toRadians(45))),
                Math.round(centerPosition.y - (radius + R_OFF + idx) * Math.sin(Math.toRadians(45))));
        Point2D point1 = tools.refineCenter(frame, tempPoint1, 15);  // 1st iteration
        point1 = tools.refineCenter(frame, point1, 15); // 2nd iteration

        min = LineProfileUtils.minimaValue(line2);
        max = LineProfileUtils.maximaValue(line2);
        threshold = Math.round((max - min) * 0.80f);
        Float centerOfMass = LineProfileUtils.centerOfMass(line2, threshold);
        if (centerOfMass == null) return null;
        idx = Math.round(centerOfMass);
        if (idx == null) return null;
        Point2D tempPoint2 = new Point2D(Math.round(centerPosition.x + (radius + R_OFF + idx) * Math.cos(Math.toRadians(135))),
                Math.round(centerPosition.y - (radius + R_OFF + idx) * Math.sin(Math.toRadians(135))));
        Point2D point2 = tools.refineCenter(frame, tempPoint2, 15);  // 1st iteration
        point2 = tools.refineCenter(frame, point2, 15); // 2nd iteration

        // backtest found satellite dots to center
        Point2D between = point1.add(point2).divide(2);
        Point2D theoreticalCenter = new Point2D(between.x + point2.y - between.y, between.y - point2.x + between.x);

        double distance = Math.sqrt(Math.pow(centerPosition.x - theoreticalCenter.x, 2) + Math.pow(centerPosition.y - theoreticalCenter.y, 2));
        if (distance > 7)
            return null;

        yuvdraw.drawCircle(Yc, point1, 10);
        yuvdraw.drawCircle(Yc, point2, 10);

        // add found dot locations, relative to center
        dots.add(point1.minus(centerPosition));
        dots.add(point2.minus(centerPosition));

        return dots;

    }


    public boolean completed() {
        return completed;
    }


    private void log(String msg) {
        System.out.println(msg);
        Logr.d("AUTOCALIBRATE", msg);
    }

    ///// SETTERS /////


    private Float setSliderRatchetOffsetPx(Float sliderRatchetOffset) {
        this.sliderRatchetOffset = sliderRatchetOffset;  // offset in x-direction
        return sliderRatchetOffset;
    }

    public void setDeviceType(Device device) {
        this.mDevice = device;
    }

    public Rect setCalibrationBox(Rect calibrationBox) {
        CalibrationBox = calibrationBox;
        return calibrationBox;
    }

    public Circle setScrollyParameters(Circle scrollyParameters) {
        ScrollyParameters = scrollyParameters;
        return scrollyParameters;
    }

    public Circle setRatchetParameters(Circle ratchetParameters) {
        RatchetParameters = ratchetParameters;
        return ratchetParameters;
    }

    public Rect setSliderBox(Rect findSliderBox) {
        SliderBox = findSliderBox;
        return findSliderBox;
    }

    public Point2D setSliderPosition(Point2D findSliderPosition) {
        SliderPosition = findSliderPosition;
        return findSliderPosition;
    }

    private List<Point2D> setSatelliteDots(List<Point2D> satelliteDots) {
        this.satelliteDots = satelliteDots;
        return satelliteDots;
    }

    private List<Point2D> setCalibrationDotPositions(List<Point2D> dots) {
        this.calibrationDots = dots;
        return dots;
    }


    ///// GETTERS //////

    public byte[] getProcessedFrame() {
        return processedFrame;
    }

    public Float getSliderRatchetOffsetPx() {
        return sliderRatchetOffset;
    }

    public Rect getCalibrationBox() {
        return CalibrationBox;
    }

    public Point2D getSliderPosition() {
        return SliderPosition;
    }

    public Circle getScrollyParameters() {
        return ScrollyParameters;
    }

    public Circle getRatchetParameters() {
        return RatchetParameters;
    }

    public double getMMperPx() {
        return mDevice.distanceBetweenCalibrationMarks / calibrationFeatureLengthPx;
    }

    public int getDeviceNumber() {
        return deviceNumber;
    }

    public byte[] getLumaArray() {
        return Y;
    }

    public byte[] getBlueChromaArray() {
        return U;
    }

    public byte[] getRedChromaArray() {
        return V;
    }

    public byte[] getYc() {
        return Yc;
    }

    public byte[] getUc() {
        return Uc;
    }

    public byte[] getVc() {
        return Vc;
    }

    public List<Point2D> getSatelliteDots() {
        return satelliteDots;
    }

    public Rect getSliderBox() {
        return SliderBox;
    }

    public List<Point2D> getCalibrationDotPositions() {
        return calibrationDots;
    }

}
