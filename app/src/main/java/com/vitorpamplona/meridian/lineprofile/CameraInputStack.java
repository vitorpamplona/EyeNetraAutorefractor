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
package com.vitorpamplona.meridian.lineprofile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.widget.FrameLayout;

import com.vitorpamplona.core.testdevice.DeviceDataset.CalibrationType;
import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.DeviceModelSettings;
import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.CollectionUtils;
import com.vitorpamplona.core.utils.DeviceModelParser;
import com.vitorpamplona.meridian.CameraInput;
import com.vitorpamplona.meridian.CameraInputListener;
import com.vitorpamplona.meridian.imgproc.lineprofile.AutoCalibration;
import com.vitorpamplona.meridian.imgproc.lineprofile.AutoCalibration.Circle;
import com.vitorpamplona.meridian.imgproc.lineprofile.CalibrationTools;
import com.vitorpamplona.meridian.imgproc.lineprofile.CameraPreview;
import com.vitorpamplona.meridian.imgproc.lineprofile.ErrorCode;
import com.vitorpamplona.meridian.imgproc.lineprofile.SignalFinder;
import com.vitorpamplona.meridian.imgproc.lineprofile.SlitAngle2;
import com.vitorpamplona.meridian.input.lineprofile.CalibrationComponent;
import com.vitorpamplona.meridian.input.lineprofile.RatchetComponent;
import com.vitorpamplona.meridian.input.lineprofile.RatchetComponent.RatchetListener;
import com.vitorpamplona.meridian.input.lineprofile.ScrollyWheelComponent;
import com.vitorpamplona.meridian.input.lineprofile.ScrollyWheelComponent.WheelListener;
import com.vitorpamplona.meridian.input.lineprofile.SliderComponent;
import com.vitorpamplona.meridian.input.lineprofile.SliderComponent.SliderListener;
import com.vitorpamplona.meridian.utils.ByteArrayReadWrite;
import com.vitorpamplona.meridian.utils.CalibrationManager;
import com.vitorpamplona.meridian.utils.CalibrationManager.DeviceCalibration;
import com.vitorpamplona.meridian.utils.ImageSequenceRecorder;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.SignalNormalizer;
import com.vitorpamplona.meridian.utils.SortHashMap;
import com.vitorpamplona.meridian.utils.Stopwatch;
import com.vitorpamplona.meridian.utils.YuvConverter;
import com.vitorpamplona.meridian.utils.YuvFilter;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraInputStack implements PreviewCallback, CameraInput {

    protected static final String TAG = "CameraInputManager";

    private CalibrationType mCalibrationType = CalibrationType.HARDCODED; // default
    // if
    // not
    // defined
    // by
    // constructor

    private int NUMBER_OF_ERROR_FRAMES_TO_ABORT = 30;
    protected Context mContext;
    protected Device mDevice;
    protected FrameLayout mPreviewFrame;

    protected Camera mCamera;
    protected byte[] mBuffer;

    protected int mPreviewWidth;
    protected int mPreviewHeight;

    protected SignalFinder mSignalFinder;
    protected RatchetComponent mRatchetComponent;
    protected ScrollyWheelComponent mScrollyWheelComponent;
    protected SliderComponent mSliderComponent;
    protected CalibrationComponent mCalibrationComponent;
    protected boolean mIsFirstFrame = true;
    FrameDebugData mDebugInfo = new FrameDebugData();

    protected ImageSequenceRecorder mImageSequenceRecorder = new ImageSequenceRecorder();
    protected AutoCalibration mAutoCalibration;
    protected ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60);

    protected int mImagePNGSequenceNumber = 0;
    protected int mImageArraySequenceNumber = 0;
    boolean firstRun = true;
    protected Stopwatch clock = new Stopwatch("CameraInputManager");
    Point2D ratchetPosition = null;
    private boolean firstTone = true;

    DescriptiveStatistics stats = new DescriptiveStatistics(10);
    private boolean calculateMaskAngle = false;

    SlitAngle2 slitAngleFinder;
    double maskSlitAngle = 0;
    byte[] R, G, B;

    protected CameraInputListener mCameraInputListener;
    protected List<OnPreviewFrameListener> mOnPreviewFrameListeners = new ArrayList<OnPreviewFrameListener>();

    // Runs the static (auto) calibration until parameters were found. It then
    // sets these values and
    // then re-initializes the components with the parameters.
    CalibrationManager calibrationValues;

    public CameraInputStack(Device device) {
        mPreviewWidth = device.previewFrameSize.WIDTH;
        mPreviewHeight = device.previewFrameSize.HEIGHT;
        mAutoCalibration = new AutoCalibration(mPreviewWidth, mPreviewHeight, device);
        mDevice = device;
        mCalibrationType = mDevice.calibrationType;
    }

    public CameraInputStack(Context context, Device device, FrameLayout previewFrame) {
        mPreviewWidth = device.previewFrameSize.WIDTH;
        mPreviewHeight = device.previewFrameSize.HEIGHT;
        mAutoCalibration = new AutoCalibration(mPreviewWidth, mPreviewHeight, device);
        initialize(context, device, previewFrame);
    }

    protected void initialize(Context context, Device device, FrameLayout previewFrame) {
        mContext = context;
        mDevice = device;
        mCalibrationType = mDevice.calibrationType;
        mPreviewFrame = previewFrame;
        calibrationValues = new CalibrationManager(mContext);

        slitAngleFinder = new SlitAngle2(mPreviewWidth, mPreviewHeight, 40);
        R = new byte[mPreviewWidth * mPreviewHeight];
        G = new byte[mPreviewWidth * mPreviewHeight];
        B = new byte[mPreviewWidth * mPreviewHeight];

        initializeCamera();
        initializeComponents();
    }

    public void setDevice(Device device) {
        mDevice = device;
    }

    // Start camera object to receive image frames in onPreviewFrame()
    protected void initializeCamera() {
        mIsFirstFrame = true;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(i);
                break;
            }
        }

        Camera.Parameters params = mCamera.getParameters();


        params.setPreviewSize(mPreviewWidth, mPreviewHeight);

        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);

        List<Camera.Area> areaList = new ArrayList<Camera.Area>();

        params.setPreviewFpsRange(30000, 30000);
        params.setPreviewFrameRate(30);
        mCamera.setParameters(params);

        int size = params.getPreviewSize().width * params.getPreviewSize().height;
        size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
        mBuffer = new byte[size];

        CameraPreview mPreview = new CameraPreview(mContext, mCamera);
        mPreviewFrame.removeAllViews();
        mPreviewFrame.addView(mPreview);

        mCamera.addCallbackBuffer(mBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        mCamera.startPreview();
    }

    // Initialize the default Optical Recognition components from DeviceDataset
    // in Core
    public void initializeComponents() {

        String deviceModel = DeviceModelParser.getDeviceName();
        Rect bounds = new DeviceModelSettings(deviceModel).getYuvMaskBounds();
        YuvFilter colorFilter = new YuvFilter(bounds);

        mCalibrationComponent = new CalibrationComponent(mDevice.calibrationRectTopLeft, mDevice.calibrationRectBottomRight, mDevice.intensityThreshold, mPreviewWidth, mPreviewHeight, mDebugInfo, colorFilter);
        mSliderComponent = new SliderComponent(mDevice.deltaPDRectTopLeftFromCalibration, mDevice.deltaPDRectBottomRigthFromCalibration, mDevice.intensityThreshold, mPreviewWidth, mPreviewHeight, mDebugInfo, colorFilter);
        mScrollyWheelComponent = new ScrollyWheelComponent(mDevice.deltaScrollyFromCalibration, (int) mDevice.scrollyRadius, (int) mDevice.scrollyThickness, mDevice.intensityThreshold, mPreviewWidth, mPreviewHeight, mDevice.calibrationType, mDebugInfo, colorFilter);
        mRatchetComponent = new RatchetComponent(mDevice, mDebugInfo, colorFilter);
    }

    // Initialize the component listeners
    protected void initializeListeners() {
        mScrollyWheelComponent.add(new WheelListener() {
            @Override
            public void scrollUp() {
                Logr.d(TAG, "scrollUp");
                mCameraInputListener.onMoveCloser();
            }

            @Override
            public void scrollDown() {
                Logr.d(TAG, "scrollDown");
                mCameraInputListener.onMoveFurther();
            }
        });

        mRatchetComponent.add(new RatchetListener() {
            @Override
            public void meridianChange(double to) {
                mCameraInputListener.onMeridianChanged((float) convertAngleToDoctor(to));
            }
        });

        mSliderComponent.add(new SliderListener() {
            @Override
            public void changed(double toMM) {
                // Log.i(TAG, "Changed PD to " + toMM);
                mCameraInputListener.onPDChanged(toMM);
                // mRatchetComponent.reset();
            }
        });

        mCalibrationComponent.reset();
        mSliderComponent.reset();
        mRatchetComponent.reset();
    }

    @Override
    public void resume() {
        if (!mScrollyWheelComponent.isReady() || !mRatchetComponent.isReady() || !mCalibrationComponent.isReady()
                || !mSliderComponent.isReady()) {

            mIsFirstFrame = true;
            mRatchetComponent.reset();
            mCalibrationComponent.reset();
            mSliderComponent.reset();
        }

        mCamera.addCallbackBuffer(mBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        mCamera.startPreview();
    }

    @Override
    public void pause() {
        mRatchetComponent.release();
        mCalibrationComponent.release();
        mSliderComponent.release();

        mCamera.setPreviewCallbackWithBuffer(null);
        mCamera.stopPreview();
    }

    @Override
    public void destroy() {
        // Avoiding the surface calling start preview after the camera is
        // released.
        mPreviewFrame.removeAllViews();
        mCamera.release();
        mRatchetComponent.release();
        mScrollyWheelComponent.release();
        mSliderComponent.release();
        mCalibrationComponent.release();
    }

    // Camera preview frame (byte[]). Calibration and Optical Recognition are
    // launched from here.
    double start;
    boolean calibrated = false;
    int calibrationFrameCounter = 0, testFrameCounter = 0;
    int skipper = 0;

    byte[] lastData;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

//		 savePNGToPhone(data) ; // This is for saving image to disk
//		 saveByteArrayToPhone(data); // Saves byte array frame data to disk

        if (calculateMaskAngle) {
            findSlits(data);
        }

        mDebugInfo.reset(); // reset values

        logcatFPS(mDebugInfo);

        skipper++;
        if (skipper > 10) { // Skip n frames while camera adjusts

            if (calibrated) { // run the optical recognition once calibrated

                testFrameCounter++;

//                savePNGToPhone(data) ; // This is for saving image to disk
//                saveByteArrayToPhone(data); // Saves byte array frame data to disk
//                Log.d("SAVE","saved frame: " + testFrameCounter);

                // start = System.currentTimeMillis();

                ErrorCode Status = runOpticalRecognition(data, mDebugInfo);

                // test if enough light
                if (Status == ErrorCode.NOT_ENOUGH_LIGHT) {
                    if (firstTone) {
                        mToneGenerator.startTone(ToneGenerator.TONE_DTMF_4, 100);
                        firstTone = false;
                    }
                } else {
                    firstTone = true;
                }

                // Debug stuffs
                populateErrorCodeHistory(Status.toString(), testFrameCounter);
                populateErrorCodeCounter(Status);
                // savePNGToPhoneWithErrorCode(data,Status);
                // saveByteArrayToPhoneWithErrorCode(data,Status); // save
                // failed image data to disk

                mDebugInfo.numberOfFramesInTest = testFrameCounter;
                mDebugInfo.numberOfFramesDiscarded = (int) (discardedFrameRatio.average() * 100);
                mDebugInfo.errorCode = Status.toString();

                // IntegerArrayWrite.appendLog((System.currentTimeMillis()-start)+"",
                // "FPSlog.txt");

            } else { // calibrate the device

                calibrationFrameCounter++;

                ErrorCode Status = calibrateOpticalRecognition(data);

                // Debug stuffs
                populateErrorCodeHistory(Status.toString(), calibrationFrameCounter);
                // savePNGToPhoneWithErrorCode(data,Status);
                // saveByteArrayToPhoneWithErrorCode(data,Status); // save
                // failed image data to disk

                mDebugInfo.numberOfFramesUsedForCalibration = calibrationFrameCounter;
                mDebugInfo.errorCode = Status.toString();

            }

        }

        for (OnPreviewFrameListener listener : mOnPreviewFrameListeners) {
            listener.onPreviewFrame(data, mDebugInfo, mAutoCalibration.getYc());
        }

        mCamera.addCallbackBuffer(mBuffer);

    }

    private ErrorCode calibrateOpticalRecognition(byte[] data) {
        if (!runStaticCalibration(data))
            return ErrorCode.CAL_STATIC_NO_SUCCESS; // try to calibrate, returns if it can't

        // try to find initial angle and pd values TODO: re-run autocalibration
        // if this fails more than 10 times
        if (!findInitialValues(data)) return ErrorCode.CAL_CANT_FIND_INITIAL;

        // initialize done
        populateErrorCodeHistory("CALIBRATION COMPLETE", 0);

        // start-up optical recognition listeners
        initializeListeners();

        // flag as calibration completed
        calibrated = true;
        return ErrorCode.SUCCESS;
    }

    private int initialValueCounter = 0;

    private boolean findInitialValues(byte[] data) {

        ErrorCode Status;

        // Notify activity that calibration is done, so it can set to blue
        // screen and notify of device ID if found
        if (firstRun) {
            mCameraInputListener.onCalibrationDone();
            firstRun = false;
        }

        initialValueCounter++;
        mDebugInfo.numberOfFramesUsedForInitialValues = initialValueCounter;

        // run to get initial angle and PD values
        Status = runOpticalRecognition(data, null);

        float angle = (float) mRatchetComponent.getAngle();
        float pd = (float) mSliderComponent.getMMoffsetFromCenter();
        float pdPx = (float) mSliderComponent.getAbsoluteValuePX();

        // calculate ratchetX_normalized to represent the most left position of ratchet
        parameters.ratchetX_normalized = parameters.ratchetX - (pdPx - parameters.sliderP1X);
        parameters.ratchetY_normalized = Float.NaN;

        // notify Activity with angle and pd initial location
        if (!Float.isNaN(angle) && !Float.isInfinite(angle) && !Float.isNaN(pd) && !Float.isInfinite(pd)) {
            mCameraInputListener.onControlsFound((float) convertAngleToDoctor(angle), pd, mAutoCalibration.getDeviceNumber(), parameters);
            return true;
        } else {
            Logr.d("STATIC", "Bad values...    angle: " + angle + " pd: " + pd);
            return false;
        }
    }

    DeviceCalibration parameters = new DeviceCalibration();
    boolean calibrationFinished = false;

    public boolean runStaticCalibration(byte[] data) {
        if (!calibrationFinished) {

            // attempt calibration
            Logr.d("STATIC", "-----------------------------------------");
            Logr.d("STATIC", "Attempt autocalibration...");
            mAutoCalibration.process(data);

            // if calibration completed, set values to components/finders,
            // otherwise try again with next frame
            if (mAutoCalibration.completed()) {

//                    mImagePNGSequenceNumber = -1;
//                    savePNGToPhone(data);
//                    mImageArraySequenceNumber = -1;
//                    saveByteArrayToPhone(data);
//                    Log.d("SAVE","saved calibration image");


                // Get parameters found in calibration
                Rect calibrationBox = mAutoCalibration.getCalibrationBox();
                Circle scrollyParameters = mAutoCalibration.getScrollyParameters();
                Circle ratchetParameters = mAutoCalibration.getRatchetParameters();
                Rect sliderBox = mAutoCalibration.getSliderBox();
                List<Point2D> satellitePoints = mAutoCalibration.getSatelliteDots();

                // Save calibration parameters
                parameters.success_rate = 100;
                parameters.deviceId = mDevice.id;

                parameters.sliderP1X = sliderBox.left;
                parameters.sliderP1Y = sliderBox.top;
                parameters.sliderP2X = sliderBox.right;
                parameters.sliderP2Y = sliderBox.bottom;

                parameters.calibrationP1X = calibrationBox.left;
                parameters.calibrationP1Y = calibrationBox.top;
                parameters.calibrationP2X = calibrationBox.right;
                parameters.calibrationP2Y = calibrationBox.bottom;

                parameters.ratchetX = ratchetParameters.center.x;
                parameters.ratchetY = ratchetParameters.center.y;
                parameters.ratchetR = ratchetParameters.radius;

                parameters.scrollyX = scrollyParameters.center.x;
                parameters.scrollyY = scrollyParameters.center.y;
                parameters.scrollyR1 = scrollyParameters.radius;

                if (calibrationValues != null)
                    calibrationValues.put(parameters);

                // Set parameters to components for optical recognition
                Point2D calibrationRectTopLeft = new Point2D(calibrationBox.right, calibrationBox.top);
                Point2D calibrationRectBottomRight = new Point2D(calibrationBox.left, calibrationBox.bottom); // TODO
                // flipped
                // for
                // vertical
                // calibration
                // markers
                Point2D deltaPDRectTopLeftFromCalibration = new Point2D(sliderBox.left
                        + mDevice.sliderCenterPerspectiveOffset, sliderBox.top); // TODO
                // perspective
                // offset
                // for
                // bookends
                Point2D deltaPDRectBottomRigthFromCalibration = new Point2D(sliderBox.right
                        + mDevice.sliderCenterPerspectiveOffset, sliderBox.bottom); // TODO
                // perspective
                // offset
                // for
                // bookends

                String nameOfDevice = DeviceModelParser.getDeviceName();
                int signalLevel = new DeviceModelSettings(nameOfDevice).getSignalLevel();

                mSignalFinder = new SignalFinder(mPreviewWidth, mPreviewHeight, 5, signalLevel, 20, mAutoCalibration);
//                mSignalFinder = new SignalFinder(mPreviewWidth, mPreviewHeight, 5, 60, 20, mAutoCalibration);

                mCalibrationComponent.setParameters(calibrationRectTopLeft, calibrationRectBottomRight, mDevice.intensityThreshold);
                mSliderComponent.setParameters(deltaPDRectTopLeftFromCalibration, deltaPDRectBottomRigthFromCalibration, mDevice.intensityThreshold, mAutoCalibration);

                int overrideScrollyRadius = 26;
//                int overrideScrollyRadius = (int) mDevice.scrollyRadius;
                mScrollyWheelComponent.setParameters(scrollyParameters.center, overrideScrollyRadius, (int) mDevice.scrollyThickness, null, mDevice.intensityThreshold);


                mRatchetComponent.setParameters(ratchetParameters.center, ratchetParameters.radius, ratchetParameters.thickness, satellitePoints, mDevice.intensityThreshold);

                // Display Values
                Logr.d("STATIC", " ");
                Logr.d("STATIC", "[Autocalibration V1 values]");
                Logr.d("STATIC", "Calibration: Top Left (" + calibrationBox.left + "," + calibrationBox.top + "), "
                        + "Bottom Right (" + calibrationBox.right + "," + calibrationBox.bottom + ")");
                Logr.d("STATIC", "Ratchet: Center (" + ratchetParameters.center.x + "," + ratchetParameters.center.y
                        + "), " + "Radius " + ratchetParameters.radius + ", Thickness " + ratchetParameters.thickness);
                Logr.d("STATIC", "Scrolly: Center (" + scrollyParameters.center.x + "," + scrollyParameters.center.y
                        + "), " + "Radius " + scrollyParameters.radius + ", Thickness " + scrollyParameters.thickness);
                Logr.d("STATIC", "Slider: Top Left (" + sliderBox.left + "," + sliderBox.top + "), " + "Bottom Right ("
                        + sliderBox.right + "," + sliderBox.bottom + ")");

                return calibrationFinished = true; // we're calibrated now, and
                // the values have been set

            } else {
                return calibrationFinished = false; // not calibrated yet, so
                // try again with next frame
            }

        } else
            return true; // we're already calibrated, so keep returning true

    }

    DecimalFormat df = new DecimalFormat("0.0");
    SignalNormalizer signalNormalizer;

    FixedSizeList discardedFrameRatio = new FixedSizeList(NUMBER_OF_ERROR_FRAMES_TO_ABORT);

    public class FixedSizeList {
        public List<Double> values = new ArrayList();
        public static final int DEFAULT_STACK_SIZE = 3;
        public int stackSize;

        public FixedSizeList(int stackSize) {
            this.stackSize = stackSize;
        }

        public synchronized void add(double value) {
            if (Double.isNaN(value)) {
                throw new RuntimeException("Value is NaN");
            } else {
                this.values.add(0, Double.valueOf(value));
                if (this.values.size() > this.stackSize) {
                    this.values.remove(this.values.size() - 1);
                }

            }
        }

        public boolean isReady() {
            return this.values.size() == this.stackSize && !Double.isNaN(this.average());
        }

        public void reset() {
            this.values.clear();
        }

        public synchronized double average() {
            if (this.values.isEmpty()) {
                return 0.0D / 0.0;
            } else {
                return (new CollectionUtils()).average(this.values);
            }
        }
    }

    // Run the optical recognition with the set calibration values
    public ErrorCode runOpticalRecognition(byte[] grayscale, FrameDebugData debugInfo) {

        if (debugInfo != null) {
            debugInfo.processingTime = timediff;
            debugInfo.calibrated = calibrated;
        }

        synchronized (this) {
            ErrorCode Status;

            // IF more than 50% of the last frames failed. Recalibrate
            if (discardedFrameRatio.isReady() && discardedFrameRatio.average() > 0.50) {
                mCameraInputListener.onRestartCalibration();
                discardedFrameRatio.reset();
            }

            // run signal level finder
            if ((Status = mSignalFinder.process(grayscale)) != ErrorCode.SUCCESS) {
                discardedFrameRatio.add(1);
                Logr.d("FAIL", Status.toString());
                mScrollyWheelComponent.reset();

                return Status;

            } else {
                signalNormalizer = mSignalFinder.getSignalNormalizer();
            }

            // update MM per pixel calibration
            updateMMperPx();


            // run slider component
            if ((Status = mSliderComponent.process(grayscale, signalNormalizer)) != ErrorCode.SUCCESS) {

                discardedFrameRatio.add(1);
                Logr.d("FAIL", Status.toString());

                // Resets the scrolly to avoid triggering angle change events
                // when the component gets back.
                // The following use case is the problem:
                // 1. Person align the lines.
                // 2. Person press ratchet.
                // 3. For some reason, the image processing does not pick the
                // ratchet changed and fails to recognize the controls.
                // 4. Subject moves PD and Scrolly to try to get the Ratchet
                // back.
                // 5. When the image processing finally finds the ratchet, the
                // scrolly has being moved, and thus if you remove this line, it
                // triggers the angle change events, setting a wrong power for
                // that angle.
                // The following line, clears the previous angle and thus the
                // next processing wont trigger any event.
                mScrollyWheelComponent.reset();
                return Status; // if can't find sliding marks, stops
            }

            if (debugInfo != null)
                debugInfo.sliderValueMM = (float) mSliderComponent.getRelativeValueMM();

            ratchetPosition = updateRatchetPosition(debugInfo);

            if ((Status = mRatchetComponent.process(grayscale, signalNormalizer)) != ErrorCode.SUCCESS) {

                discardedFrameRatio.add(1);
                Logr.d("FAIL", Status.toString());

                // Resets the scrolly to avoid triggering angle change events
                // when the component gets back.
                // The following use case is the problem:
                // 1. Person align the lines.
                // 2. Person press ratchet.
                // 3. For some reason, the image processing does not pick the
                // ratchet changed and fails to recognize the controls.
                // 4. Subject moves PD and Scrolly to try to get the Ratchet
                // back.
                // 5. When the image processing finally finds the ratchet, the
                // scrolly has being moved, and thus if you remove this line, it
                // triggers the angle change events, setting a wrong power for
                // that angle.
                // The following line, clears the previous angle and thus the
                // next processing wont trigger any event.
                mScrollyWheelComponent.reset();
                return Status; // if can't find meridian, stops
            }

            if (debugInfo != null) debugInfo.ratchetAngle = (float) mRatchetComponent.getAngle();

            // Avoid processing the scroll if can't find the ratchet. It may
            // have changed the angle and the screen won't update.

            if ((Status = mScrollyWheelComponent.process(grayscale, signalNormalizer)) != ErrorCode.SUCCESS) {

                discardedFrameRatio.add(1);
                Logr.d("FAIL", Status.toString());

                // Resets the scrolly to avoid triggering angle change events
                // when the component gets back.
                // The following use case is the problem:
                // 1. Person align the lines.
                // 2. Person press ratchet.
                // 3. For some reason, the image processing does not pick the
                // ratchet changed and fails to recognize the controls.
                // 4. Subject moves PD and Scrolly to try to get the Ratchet
                // back.
                // 5. When the image processing finally finds the ratchet, the
                // scrolly has being moved, and thus if you remove this line, it
                // triggers the angle change events, setting a wrong power for
                // that angle.
                // The following line, clears the previous angle and thus the
                // next processing wont trigger any event.
                mScrollyWheelComponent.reset();
                return Status; // if can't update scrolly, stops
            }

            if (debugInfo != null)
                debugInfo.scrollyWheelAngle = (float) mScrollyWheelComponent.getAngle();

            if (mIsFirstFrame) {
                mSliderComponent.refreshListeners();
                mRatchetComponent.refreshListeners();
                mIsFirstFrame = false;
            }

            discardedFrameRatio.add(0);

            return ErrorCode.SUCCESS;
        }
    }

    private void findSlits(byte[] yuv) {

        byte[] R = new byte[mPreviewWidth * mPreviewHeight];
        byte[] G = new byte[mPreviewWidth * mPreviewHeight];
        byte[] B = new byte[mPreviewWidth * mPreviewHeight];
        YuvConverter.toByteRGBArrays(yuv, R, G, B, mPreviewWidth, mPreviewHeight);

        CalibrationTools tools = new CalibrationTools(mPreviewWidth, mPreviewHeight);
        Point2D COM = tools.centerOfMass(R, new Rect(0, 0, mPreviewWidth - 1, mPreviewHeight - 1));  // TODO should prob use maxima

        Double angleR, angleG, angle;
        if (COM != null) {
            angleR = slitAngleFinder.process(R, COM);
            angleG = slitAngleFinder.process(G, COM);
            angle = (angleR + angleG) / 2;
            maskSlitAngle = angle;
            stats.addValue(angle);
        } else {
            angleR = null;
            angleG = null;
            angle = null;
        }

        Logr.d("SLIT", "R: " + angleR + "  G: " + angleG + "  ANGLE: " + angle + "  std: " + stats.getStandardDeviation());

    }

    private ErrorCode testIfDeviceOutsideShell(byte[] grayscale, int box, int emptyRegionThreshold) {

        // Use empty space in corners for light metering
        int xpos, ypos, emptyRegion;
        CalibrationTools tools = new CalibrationTools(mPreviewWidth, mPreviewHeight);

        xpos = 50;
        ypos = 50;
        emptyRegion = tools.average(grayscale, new Rect(xpos - box, ypos - box, xpos + box, ypos + box));
        xpos = mPreviewWidth - 50;
        ypos = 50;
        emptyRegion += tools.average(grayscale, new Rect(xpos - box, ypos - box, xpos + box, ypos + box));
        xpos = mPreviewWidth - 50;
        ypos = mPreviewHeight - 50;
        emptyRegion += tools.average(grayscale, new Rect(xpos - box, ypos - box, xpos + box, ypos + box));

        if (emptyRegion > emptyRegionThreshold) { // device is not in shell while image processing is running!
            return ErrorCode.DEVICE_OUTSIDE_SHELL;
        } else { // device is in shell, yehaw! let's go!
            return ErrorCode.SUCCESS;
        }

    }

    private void updateMMperPx() {
        // calculate mm/pixel and send to slider component
        mSliderComponent.setMMperPX(mAutoCalibration.getMMperPx());
    }

    // Part of optical recognition, the ratchet position is updated from here.
    // TODO: Must refactor this sucker and change components accordingly.
    private Point2D updateRatchetPosition(FrameDebugData debugInfo) {

        Point2D calculatedPoint = null;

        // deal with static or manual calibration
        switch (mCalibrationType) {

            case HARDCODED:
                // slider position from center (px location) /// FOR MANUAL
                // CALIBRATION
                double sliderPositionPXfromCenter = mSliderComponent.getRelativeValuePX();
                mRatchetComponent.setCenter(new Point2D(
                        mDevice.deltaMeridianFromCalibration.x + sliderPositionPXfromCenter,
                        mDevice.deltaMeridianFromCalibration.y)); // Add slider
                // offset

                if (debugInfo != null) {
                    debugInfo.ratchetCenterX = (int) (mDevice.deltaMeridianFromCalibration.x + sliderPositionPXfromCenter);
                    debugInfo.ratchetCenterY = (int) mDevice.deltaMeridianFromCalibration.y;
                }
                break;

            case STATIC:

                calculatedPoint = new Point2D(mSliderComponent.getAbsoluteValuePX() +
                        mAutoCalibration.getSliderRatchetOffsetPx(),
                        mAutoCalibration.getRatchetParameters().center.y);
                mRatchetComponent.setCenter(calculatedPoint); // Add slider offset

                if (debugInfo != null) {
                    debugInfo.ratchetCenterX = (int) calculatedPoint.x;
                    debugInfo.ratchetCenterY = (int) calculatedPoint.y;
                }

                break;

            case DYNAMIC:
                // empty for now
                break;
        }

        return calculatedPoint;

    }

    // DEBUG: Saves image to disk
    private void savePNGToPhone(byte[] data) {

        mImagePNGSequenceNumber++;

        // write image from byte array
        Bitmap bmp = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, Bitmap.Config.ARGB_8888);
        bmp = YuvConverter.toBitmapRGB(data, mPreviewWidth, mPreviewHeight);
        mImageSequenceRecorder.saveBitmap(bmp, mImagePNGSequenceNumber);

    }

    // DEBUG: Saves image to disk with error code
    private void savePNGToPhoneWithErrorCode(byte[] data, ErrorCode Status) {

        if (Status == ErrorCode.SUCCESS) return;

        if (data.length > 0) {
            data[0] = (byte) Status.errorCode; // overwrite first pixel with
            // errorcode (erroCode should be
            // -128 to +127)
        }

        savePNGToPhone(data);

    }

    // DEBUG: Saves byte array frame data to disk
    private void saveByteArrayToPhone(byte[] data) {

        mImageArraySequenceNumber++;

        // write whole array to SD
        ByteArrayReadWrite.writeAndroid(data, "A", mImageArraySequenceNumber);

    }

    // DEBUG: Saves byte array frame data to disk, replaces first pixel with
    // error code
    private void saveByteArrayToPhoneWithErrorCode(byte[] data, ErrorCode Status) {

        if (Status == ErrorCode.SUCCESS) return;

        if (data.length > 0) {
            data[0] = (byte) Status.errorCode; // overwrite first pixel with
            // errorcode (erroCode should be
            // -128 to +127)
        }

        saveByteArrayToPhone(data);

    }

    // DEBUG: Creates an error code history
    int historyLength = 15;
    List<String> errorCodeHistory = new ArrayList<String>();

    private void populateErrorCodeHistory(String codeString, int frameNumber) {

        // Adds failed error codes to canvas
        if (!codeString.contentEquals(ErrorCode.SUCCESS.toString())) {

            String msg = (frameNumber + ": " + codeString);
            errorCodeHistory.add(0, msg); // add last errorCode

            if (errorCodeHistory.size() > historyLength) {
                errorCodeHistory.remove(historyLength);
            }

        }

    }

    // DEBUG: Creates an error code counter
    int countLength = 15;
    private Map<ErrorCode, Integer> errorCodeCounts = new HashMap<ErrorCode, Integer>();

    private void populateErrorCodeCounter(ErrorCode code) {

        int count = 0;

        // check if error code is mapped
        if (errorCodeCounts.containsKey(code)) {

            count = errorCodeCounts.get(code);
            count++;
            // increment error code count by one and replace value
            errorCodeCounts.put(code, count);

        }
        // add new error code with count=1 if not listed yet
        else {

            errorCodeCounts.put(code, 1);

        }

    }

    public void adjustCameraParameters(Camera.Parameters params) {
        params.setPreviewSize(mPreviewWidth, mPreviewHeight);

        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        params.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);

        params.setPreviewFpsRange(30000, 30000);
        params.setPreviewFrameRate(30);
    }

    // DEBUG: Displays lineprofile parameters on image
    public void displayDebugInfo(Canvas canvas) {

        mCalibrationComponent.writeDebugInfo(canvas);
        mSliderComponent.writeDebugInfo(canvas);
        mScrollyWheelComponent.writeDebugInfo(canvas);
        mRatchetComponent.writeDebugInfo(canvas);

        Point2D historyPosition = new Point2D(520, 12);
        Paint paint = new Paint();
        paint.setTextSize(12);
        int yOff = 0, yInc = 12;
        boolean first = true;

        // display current test frame number
        paint.setColor(Color.WHITE);
        canvas.drawText(testFrameCounter + ": ", historyPosition.x, historyPosition.y, paint);

        // display error code history in debug screen
        for (String code : errorCodeHistory) {
            if (first) {
                paint.setColor(Color.MAGENTA);
                first = false;
            } else {
                paint.setColor(Color.WHITE);
            }
            yOff += yInc;
            canvas.drawText(code, historyPosition.x, historyPosition.y + yOff, paint);
        }

        // display error code count in debug screen
        yOff += 60;
        paint.setColor(Color.MAGENTA);
        canvas.drawText("error code counts: ", historyPosition.x, historyPosition.y + yOff, paint);

        errorCodeCounts = SortHashMap.sortByValue(errorCodeCounts); // sort
        // hashmap
        // by values
        paint.setColor(Color.WHITE);
        for (Map.Entry<ErrorCode, Integer> entry : errorCodeCounts.entrySet()) {
            yOff += yInc;
            canvas.drawText(entry.getValue() + ":  " + entry.getKey().toString(), historyPosition.x, historyPosition.y
                    + yOff, paint);
        }

        // display success rate
        yOff += 40;
        paint.setTextSize(15);
        paint.setColor(Color.CYAN);
        DecimalFormat df = new DecimalFormat("#.00");

        if (errorCodeCounts.containsKey(ErrorCode.SUCCESS)) {
            float success = errorCodeCounts.get(ErrorCode.SUCCESS) / ((float) testFrameCounter) * 100;
            canvas.drawText("Success Rate: " + df.format(success) + "%", historyPosition.x, historyPosition.y + yOff,
                    paint);
        }

        // display device id from tablet
        yOff += 30;
        paint.setTextSize(14);
        paint.setColor(Color.WHITE);
        canvas.drawText("Shell ID: " + mDevice.id, historyPosition.x, historyPosition.y + yOff, paint);

        // display device id from barcode
        yOff += 20;
        paint.setTextSize(14);
        paint.setColor(Color.WHITE);
        canvas.drawText("Barcode ID: " + mAutoCalibration.getDeviceNumber(), historyPosition.x, historyPosition.y + yOff, paint);

        // display device model and manufacturing info
        String model = DeviceModelParser.getDeviceName();
        yOff += 20;
        paint.setTextSize(14);
        paint.setColor(Color.WHITE);
        canvas.drawText("Model: " + model, historyPosition.x, historyPosition.y + yOff, paint);


        // display compensation factors over screen
        if (signalNormalizer != null) {

            paint.setTextSize(16);
            paint.setColor(Color.CYAN);
            for (int y = 0; y < mPreviewHeight; y += 40) {
                for (int x = 0; x < mPreviewWidth; x += 40) {
                    canvas.drawText(this.df.format(signalNormalizer.factor(x, y)), x, y, paint);
                }
            }

        }

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        if (mAutoCalibration.getCalibrationBox() != null)
            canvas.drawRect(mAutoCalibration.getCalibrationBox(), paint);

        if (mAutoCalibration.calibDots != null)
            canvas.drawRect(mAutoCalibration.calibDots, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);

        if (mAutoCalibration.calibBarcode != null)
            canvas.drawRect(mAutoCalibration.calibBarcode, paint);

        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);

        if (mAutoCalibration.bumps != null) {
            for (Integer i : mAutoCalibration.bumps)
                canvas.drawLine(mPreviewWidth - i, 0, mPreviewWidth - i, 50, paint);
        }

        // draw mask slit angle if available
        if (calculateMaskAngle) {

            double offset = -30;
            double slitAngle = 180 - AngleDiff.angle0to180((float) (maskSlitAngle + offset));
            double ratchetAngle = mRatchetComponent.getAngle();

            int x = 600;
            int y = 350;
            int w = 100;
            double rx, ry;

            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(new Rect(x - w, y - w, x + w, y + w), paint);

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(new Rect(x - w, y - w, x + w, y + w), paint);

            paint.setColor(Color.GREEN);
            rx = w * Math.cos(Math.toRadians(slitAngle));
            ry = w * Math.sin(Math.toRadians(slitAngle));
            canvas.drawLine((float) (x - rx), (float) (y - ry), (float) (x + rx), (float) (y + ry), paint);

            paint.setColor(Color.MAGENTA);
            rx = w * Math.cos(Math.toRadians(ratchetAngle));
            ry = w * Math.sin(Math.toRadians(ratchetAngle));
            canvas.drawLine(x, y, (float) (x + rx), (float) (y + ry), paint);
        }

    }

    // DEBUG: Displays FPS in logcat
    long lasttime = 0, timediff = 0;
    int FPSbufferLength = 10;
    List<Integer> tval = new ArrayList<Integer>();

    protected void logcatFPS(FrameDebugData debugInfo) {

        timediff = System.currentTimeMillis() - lasttime;
        timediff = (timediff == 0 ? 1 : timediff);
        int fps = Math.round(1000 / timediff);

        tval.add(0, fps);
        if (tval.size() > FPSbufferLength) {
            tval.remove(FPSbufferLength);
        }

        int FPSave = 0;
        for (int p = 0; p < tval.size(); p++) {
            FPSave += tval.get(p);
        }

        debugInfo.averageFPS = FPSave / FPSbufferLength;
        Logr.d("TIME", "FPS: " + FPSave / FPSbufferLength);
        lasttime = System.currentTimeMillis();
    }

    public double convertAngleToDoctor(double angle) {
        angle = AngleDiff.angle0to360((float) ((angle) + 45 + mDevice.slitsLinesAlignmentDelta));
        return angle;
    }

    public void setListener(CameraInputListener listener) {
        mCameraInputListener = listener;
    }

    public void setSkipper(int value) {
        skipper = value;
    }

    public void addSliderListener(SliderListener listener) {
        mSliderComponent.add(listener);
    }

    public void removeSliderListener(SliderListener listener) {
        mSliderComponent.remove(listener);
    }

    public void addWheelListener(WheelListener listener) {
        mScrollyWheelComponent.add(listener);
    }

    public void removeWheelListener(WheelListener listener) {
        mScrollyWheelComponent.remove(listener);
    }

    public void addRatchetListener(RatchetListener listener) {
        mRatchetComponent.add(listener);
    }

    public void removeRatchetListener(RatchetListener listener) {
        mRatchetComponent.remove(listener);
    }

    public void addOnPreviewFrameListener(OnPreviewFrameListener listener) {
        mOnPreviewFrameListeners.add(listener);
    }

    public void removeOnPreviewFrameListener(OnPreviewFrameListener listener) {
        mOnPreviewFrameListeners.remove(listener);
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
    }

    public interface OnPreviewFrameListener {
        public void onPreviewFrame(byte[] data, FrameDebugData debug, byte[] calibrationFrame);
    }

    public boolean isCalibrationDone() {
        return mAutoCalibration.completed();
    }

    public void calculateMaskAngle(boolean turnOn) {
        calculateMaskAngle = turnOn;
    }

    public FrameDebugData getDebugInfo() {
        return mDebugInfo;
    }

}
