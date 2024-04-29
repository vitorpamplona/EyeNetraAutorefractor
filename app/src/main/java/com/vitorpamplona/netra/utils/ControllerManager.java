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
package com.vitorpamplona.netra.utils;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.vitorpamplona.core.testdevice.DeviceDataset;
import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.meridian.CameraInputListener;
import com.vitorpamplona.meridian.lineprofile.CameraInputStack;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.CalibrationManager.DeviceCalibration;
import com.vitorpamplona.netra.activity.NetraGApplication;

public class ControllerManager implements CameraInputListener, CameraInputStack.OnPreviewFrameListener {
    private boolean mControlsFound = false;
    private CameraInputStack mCameraInputStack;

    private Device mDevice;
    private ControllerListener mControllerListener;

    private FrameLayout mPreviewFrame;
    private Context mCtx;

    public ControllerManager(Context ctx, FrameLayout cameraPreview, ControllerListener view) {
        this.mControllerListener = view;
        this.mPreviewFrame = cameraPreview;
        this.mCtx = ctx;
        this.mDevice = DeviceDataset.get(NetraGApplication.get().getSettings().getDeviceId());
    }

    public boolean isTheCalibratedDeviceID(long id) {
        NetraGApplication.get().getSettings().setDeviceId(id);
        long newID = NetraGApplication.get().getSettings().getDeviceId();

        if (mDevice == null || mDevice.id != newID) {
            Device newDevice = DeviceDataset.get(newID);
            if (newDevice != null) {
                mDevice = newDevice;
                calibrate(newDevice);
                return false;
            }
        }
        return true;
    }

    private void calibrate(Device device) {
        //TODO: move this by disabling setDevice automatically setting screen to Alien
        mControllerListener.setDevice(device);
        mControllerListener.onControllerRequestsCalibrationWhiteScreen();

        disableCamera();
        enableCamera();
    }

    public void resumeController() {
        calibrate(mDevice);
    }

    public void stopController() {
        disableCamera();
    }

    private void enableCamera() {
        mCameraInputStack = new com.vitorpamplona.meridian.lineprofile.CameraInputStack(mCtx, mDevice, mPreviewFrame);
        mCameraInputStack.setListener(this);
        mCameraInputStack.addOnPreviewFrameListener(this);
        mCameraInputStack.resume();
    }

    public void disableCamera() {
        if (mCameraInputStack != null) {
            mCameraInputStack.destroy();
        }
    }


    @Override
    public void onCalibrationDone() {
        Log.d("SandBox", "onCalibrationDone");
        mControllerListener.onControllerRequestsBlueScreen();
    }

    @Override
    public void onControlsFound(float angle, float pd, int deviceID, DeviceCalibration parameters) {
        Log.d("SandBox", "onControlsFound");
        mControllerListener.onControlsFound(angle, pd, deviceID, parameters);
        mControllerListener.onControllerRequestsTestScreen();

        // Change to new device if different.
        if (isTheCalibratedDeviceID(deviceID)) {
            // All OK. Let's start the test.
            mControlsFound = true;

            // Reset Screens.
            mControllerListener.setControllerStartingPositions(pd, angle, deviceID);
        }
    }

    @Override
    public void onRestartCalibration() {
        mControllerListener.onBadTest();
    }

    @Override
    public void onMoveFurther() {
        if (!mControlsFound) {
            return;
        }

        Log.d("SandBox", "onMoveFurther");

        mControllerListener.onControllerMoveFurther();
    }

    @Override
    public void onMoveCloser() {
        if (!mControlsFound) {
            return;
        }

        Log.d("SandBox", "onMoveCloser");

        mControllerListener.onControllerMoveCloser();
    }

    @Override
    public void onMeridianChanged(float angle) {
        if (Float.isNaN(angle) || !mControlsFound) {
            return;
        }

        Log.d("SandBox", "onMeridianChanged");

        mControllerListener.onControllerMeridianChanged(angle);
    }

    @Override
    public void onPDChanged(double to) {
        if (Double.isNaN(to) || !mControlsFound) {
            return;
        }

        Log.d("SandBox", "onPDChanged");

        mControllerListener.onControllerPDChanged((float) to);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, FrameDebugData frameDebugData, byte[] bytes1) {
        if (isAnErrorFrame(frameDebugData)) {
            mControllerListener.onBadFrame(frameDebugData);
        }
    }

    private boolean isAnErrorFrame(FrameDebugData processedInfo) {
        return (Float.isNaN(processedInfo.ratchetAngle)
                || Float.isNaN(processedInfo.scrollyWheelAngle)
                || Float.isNaN(processedInfo.sliderValueMM))
                && processedInfo.calibrated
                && mControlsFound;
    }

}
