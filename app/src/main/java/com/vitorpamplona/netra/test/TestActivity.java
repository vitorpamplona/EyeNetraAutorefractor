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
package com.vitorpamplona.netra.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.audio.AudioPlayer;
import com.vitorpamplona.netra.test.flow.FullExamFlow;
import com.vitorpamplona.netra.test.view.UmbrellaTestView;
import com.vitorpamplona.netra.utils.ControllerManager;
import com.vitorpamplona.netra.utils.DeviceSensorManager;
import com.vitorpamplona.netra.utils.DeviceSensorManager.PhoneInserted;
import com.vitorpamplona.netra.utils.HardwareUtil;

/**
 * Controls Camera, Audio, Test flow and Light Sensor.
 *
 */
public class TestActivity extends Activity implements ITestActivity {

    private UmbrellaTestView mAstigmatismView;
    private FrameLayout mPreviewFrame;

    private AudioPlayer mPlayer;

    private DeviceSensorManager mDeviceManager;
    private ControllerManager mUserController;
    private FullExamFlow mFlow;

    private TextView mInstructions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAppLocale();

        setContentView(R.layout.activity_device);

        if (getActionBar() != null)
            getActionBar().hide();

        NetraGApplication.get().setLastResult(null);

        mAstigmatismView = (UmbrellaTestView) findViewById(R.id.astigmatism);
        mPreviewFrame = (FrameLayout) findViewById(R.id.camera_preview);
        mDeviceManager = new DeviceSensorManager(this,
                new PhoneInserted() {
                    @Override
                    public void run() {
                        startExam();
                    }
                },
                new PhoneInserted() {
                    @Override
                    public void run() {
                        stopExam();
                    }
                }
        );

        mInstructions = (TextView) findViewById(R.id.text_waiting_for_connection);
        mInstructions.setText(R.string.insert_into_device_test);

        mFlow = new FullExamFlow(this, mAstigmatismView);

        mUserController = new ControllerManager(this, mPreviewFrame, mFlow);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mPlayer = new AudioPlayer(this);
    }

    public void setAppLocale() {
        // restore original locale
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = NetraGApplication.get().getSettings().getAppLocale();
        ;
        res.updateConfiguration(conf, null);
    }


    @Override
    public void onResume() {
        super.onResume();
        mPlayer.initializeAll();

        mDeviceManager.activate();

        //TODO: these flags cause the dimming, but we probably need them
        HardwareUtil.disableSleep(this);
        HardwareUtil.turnScreenMax(this);
    }

    @Override
    public void onPause() {
        mUserController.disableCamera();
        mAstigmatismView.setVisibility(View.GONE);

        mPlayer.releaseAll();

        mDeviceManager.deactivate();

        HardwareUtil.enableSleep(this);
        HardwareUtil.turnScreenAuto(this);

        super.onPause();
    }

    public int anglesMeasured() {
        if (mFlow != null)
            return mFlow.anglesMeasured();
        return -1;
    }

    @Override
    public void onBackPressed() {
        if (NetraGApplication.get().getLastResult() != null) {
            stopExam();
        } else {

            if (mFlow != null && mFlow.wasPDMeasured()) {
                mFlow.forceFinishExamWithPDResults();
                setResult(Activity.RESULT_OK);
                finish();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    public void restartExam() {
        mUserController.stopController();
        mUserController.resumeController();
    }

    private void startExam() {
        try {
            mAstigmatismView.setVisibility(View.VISIBLE);
            mUserController.resumeController();
        } catch (RuntimeException e) {
            stopExam();
            cameraBusyAlert();
        }
    }

    public void cameraBusyAlert() {
        AlertDialog.Builder authDialog = new AlertDialog.Builder(this);
        authDialog.setTitle(R.string.camera_locked_title);
        authDialog.setMessage(R.string.camera_locked_desc);
        authDialog.setCancelable(true);
        authDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        authDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        authDialog.show();
    }

    private void stopExam() {
        if (NetraGApplication.get().getLastResult() != null) {
            // Test is finished.
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            mUserController.stopController();
            mAstigmatismView.setVisibility(View.GONE);

            if (mFlow != null && mFlow.wasPDMeasured()) {
                mInstructions.setText(R.string.insert_into_device_or_back_to_see_PD_results);
            } else {
                mInstructions.setText(R.string.insert_into_device_test);
            }

        }
    }

    public void destroyCamera() {
        mUserController.disableCamera();
    }

    //----------INTERFACE DEVICE ACTIVITY----------//
    @Override
    public void setEye(boolean isRight) {
        mAstigmatismView.setTestingRightEye(isRight);
    }

    public AudioPlayer getAudioPlayer() {
        return mPlayer;
    }

    public UmbrellaTestView getTestView() {
        return mAstigmatismView;
    }
}
