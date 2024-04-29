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
package com.vitorpamplona.netra.test.flow;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.CalibrationManager;
import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.activity.settings.AppSettings;
import com.vitorpamplona.netra.model.ExamResults;
import com.vitorpamplona.netra.model.db.objects.DebugExam;
import com.vitorpamplona.netra.test.BasicExamFlow;
import com.vitorpamplona.netra.test.ExamResultBuilder;
import com.vitorpamplona.netra.test.TestActivity;
import com.vitorpamplona.netra.test.flow.FullTestState.Stage;
import com.vitorpamplona.netra.test.view.BaseTestView;
import com.vitorpamplona.netra.test.view.NETRAView;
import com.vitorpamplona.netra.test.view.NETRAView.Screen;
import com.vitorpamplona.netra.utils.ControllerListener;

import java.util.Date;
import java.util.UUID;

public class FullExamFlow extends BasicExamFlow implements ControllerListener {

    private ExamResultBuilder mResultsBuilder;

    private Float mLastAngle;
    private float mLastControllerPD;

    private boolean mPowerChanged = false;
    private boolean mLastOne;

    private FullTestState mTestState;
    private NETRAView mView;

    public FullExamFlow(TestActivity examContext, NETRAView view) {
        super(examContext);

        mView = view;

        if (is18Repetitions()) {
            mTestState = new FullTestState(this.getContext());
            mResultsBuilder = new FullExamResultBuilder(getDevice());
        } else {
            mTestState = new DoubleTestState(this.getContext());
            mResultsBuilder = new DoubleExamResultBuilder(getDevice());
        }
    }

    private float checkAngleRange(float angle) {
        return mResultsBuilder.checkAngleRange(angle);
    }

    public boolean is18Repetitions() {
        return NetraGApplication.get().getSettings().is18Repetitions();
    }

    @Override
    public void onCalibrationDone(float angle, float pd, final long barcodeID) {
        mLastAngle = angle;

        super.onCalibrationDone(angle, pd, barcodeID);
    }

    @Override
    public void startExam() {
        mResultsBuilder.switchEye(false);
        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(mLastAngle));

        getContext().setEye(false);
        mTestState.updateProgress(Stage.NONE, Stage.READY_TO_START, 0, 1, 5);

        // Starts directly in the test.
        if (NetraGApplication.get().getSettings().isTrainingAngles())
            trainingAngles();
        else
            endPreTest();
    }

    @Override
    public void resumeExam() {
        getContext().setEye(mTestState.isRightEye());
        mView.setAngle(mLastAngle);
        if (!mTestState.wasPDMeasured()) {
            mView.setPD(BaseTestView.DEFAULT_PD);
        } else {
            mView.setPD(mResultsBuilder.getPD());
        }
        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(mLastAngle));

        if (mTestState.getCurrentStage() == Stage.ANGLE_LEFT || mTestState.getCurrentStage() == Stage.ANGLE_RIGHT) {
            mView.setSliderDisplacement(mLastControllerPD);
            //getContext().getSnapshot().requestUpdatePd(mResultsBuilder.getPD());
        }

        mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
        mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);
    }

    @Override
    public void setNosePupilDistance(float pd) {
        if (mTestState.getCurrentStage() == Stage.PD_LEFT || mTestState.getCurrentStage() == Stage.PD_RIGHT) {
            mResultsBuilder.setNosePupilDistance(pd);
            mResultsBuilder.getCurrentHistory().logSlidingChange(mLastAngle, pd);
        }

        mResultsBuilder.logPDChanged(pd);
    }


    @Override
    public void setCurrentPower(float power) {
        mPowerChanged = true;

        if (mTestState.getCurrentStage() == Stage.ANGLE_LEFT || mTestState.getCurrentStage() == Stage.ANGLE_RIGHT
                || mTestState.getCurrentStage() == Stage.PD_LEFT || mTestState.getCurrentStage() == Stage.PD_RIGHT) {
            mResultsBuilder.addResult(checkAngleRange(mLastAngle), power);
        }
    }

    @Override
    public ExamResultBuilder getResultsBuilder() {
        return mResultsBuilder;
    }

    @Override
    public void setCurrentAngle(float angle) {
        boolean newBucket = mResultsBuilder.getCurrentAlgorithm().willItBeANewBucket(angle);
        Log.d("FullExamFlow", "Is it new Bucket " + newBucket + " " + checkAngleRange(angle) + " current " + checkAngleRange(mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian()));
        mResultsBuilder.getCurrentHistory().addAction("Is it new Bucket " + newBucket);

        if (newBucket && AngleDiff.isSmaller180(angle, mLastAngle) && AngleDiff.diff180(mLastAngle, angle) > 5) {
            mResultsBuilder.getCurrentHistory().addAction("Angle Change " + checkAngleRange(angle));

            mResultsBuilder.checkIfDone();

            Log.d("FullExamFlow", "Got In " + AngleDiff.diff180(mLastAngle, angle));

            switch (mTestState.getCurrentStage()) {

                case TRAINING_RIGHT:
                    // PD_RIGHT => PD_LEFT
                    // avoids running changing angle twice in a row.
                    if (mPowerChanged) {
                        // Duplicated, It sets when the wheel changes.
                        setLastControllerPD();

                        mResultsBuilder.getCurrentHistory().addAction("Training Right Done");
                        mResultsBuilder.switchEye(false);
                        getContext().setEye(false);


                        Log.d("FullExamFlow", "Set Working Meridian to " + checkAngleRange(angle));
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        Log.d("FullExamFlow", "Set Working Meridian Accepted " + mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian());

                        mTestState.updateProgress(Stage.TRAINING_RIGHT, Stage.TRAINING_LEFT, 0, 1, 1);
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));

                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (TRAINING RIGHT)");
                    }

                    mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
                    mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);

                    mLastOne = false;

                    break;

                case TRAINING_LEFT:
                    // PD_LEFT => ANGLE_LEFT
                    // avoids running changing angle twice in a row.
                    if (mPowerChanged) {
                        // Duplicated, It sets when the wheel changes.
                        setLastControllerPD();

                        mResultsBuilder.logChangeAPD(mResultsBuilder.getPD());
                        mResultsBuilder.clearCapturedData();

                        mResultsBuilder.switchEye(true);
                        getContext().setEye(true);

                        mTestState.updateProgress(Stage.TRAINING_LEFT, Stage.READY_TO_START, 0, 1, mResultsBuilder.getTotalMeridians());
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (TRAINING LEFT)");
                    }

                    mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
                    mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);

                    mLastOne = false;

                    break;

                case READY_TO_START:

                    if (Math.abs(angle - mLastAngle) > 9) {
                        // PRE_TEST => PD_RIGHT
                        endPreTest();
                    }

                    break;

                case PD_RIGHT:
                    // PD_RIGHT => PD_LEFT
                    // avoids running changing angle twice in a row.
                    if (mPowerChanged) {
                        // Duplicated, It sets when the wheel changes.
                        setLastControllerPD();

                        mResultsBuilder.getCurrentHistory().addAction("Right PD Done");
                        mResultsBuilder.switchEye(false);
                        getContext().setEye(false);


                        Log.d("FullExamFlow", "Set Working Meridian to " + checkAngleRange(angle));
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        Log.d("FullExamFlow", "Set Working Meridian Accepted " + mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian());

                        mTestState.updateProgress(Stage.PD_RIGHT, Stage.PD_LEFT, 0, 1, 1);
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));

                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (PD RIGHT)");
                    }

                    mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
                    mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);

                    mLastOne = false;

                    break;

                case PD_LEFT:
                    // PD_LEFT => ANGLE_LEFT
                    // avoids running changing angle twice in a row.
                    if (mPowerChanged) {
                        // Duplicated, It sets when the wheel changes.
                        setLastControllerPD();

                        mResultsBuilder.logChangeAPD(mResultsBuilder.getPD());
                        mResultsBuilder.clearCapturedData();

                        mView.setPD(mResultsBuilder.getPD());

                        mTestState.updateProgress(Stage.PD_LEFT, Stage.ANGLE_LEFT, 0, 1, mResultsBuilder.getTotalMeridians());
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (PD LEFT)");
                    }

                    mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
                    mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);

                    mLastOne = false;

                    break;

                case ANGLE_LEFT:
                    // ANGLE_LEFT => ANGLE_RIGHT
                    // avoids running changing angle twice in a row.
                    if (mPowerChanged) {
                        if (mResultsBuilder.isCurrentEyeDone()) {
                            mResultsBuilder.getCurrentHistory().testFinished();

                            mResultsBuilder.switchEye(true);
                            getContext().setEye(true);

                            Log.d("FullExamFlow", "Set Working Meridian to " + checkAngleRange(angle));
                            mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                            Log.d("FullExamFlow", "Set Working Meridian Accepted " + mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian());

                            mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
                            mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);

                            mTestState.updateProgress(Stage.ANGLE_LEFT, Stage.ANGLE_RIGHT, 0, 1, mResultsBuilder.getTotalMeridians());

                            // new angle in ANGLE_LEFT
                        } else {
                            mResultsBuilder.logResetPower(mResultsBuilder.getCurrentAlgorithm().getCurrentPower() + 3);
                            mView.resetPowerClosestTo(mResultsBuilder.getCurrentAlgorithm().getCurrentPower() + 3);

                            mTestState.updateProgress(Stage.ANGLE_LEFT, Stage.ANGLE_LEFT,
                                    mTestState.getCurrentStep(), mResultsBuilder.getNumAnglesTestedLeft() + 1, mResultsBuilder.getTotalMeridians());
                        }
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (ANGLE LEFT)");
                    }

                    break;

                case ANGLE_RIGHT:
                    // ANGLE_RIGHT => POST_TEST
                    if (mPowerChanged) {
                        if (mResultsBuilder.isCurrentEyeDone() || mLastOne) {

                            getContext().findViewById(R.id.progress).setVisibility(View.VISIBLE);

                            mResultsBuilder.getCurrentHistory().testFinished();

                            mTestState.updateProgress(Stage.ANGLE_RIGHT, Stage.POST_TEST, 0, 1, 1);

                            mResultsBuilder.examComplete();

                            showResultScreen();

                            // new angle in ANGLE_RIGHT
                        } else {
                            mResultsBuilder.logResetPower(mResultsBuilder.getCurrentAlgorithm().getCurrentPower() + 3);
                            mView.resetPowerClosestTo(mResultsBuilder.getCurrentAlgorithm().getCurrentPower() + 3);

                            mTestState.updateProgress(Stage.ANGLE_RIGHT, Stage.ANGLE_RIGHT,
                                    mTestState.getCurrentStep(), mResultsBuilder.getNumAnglesTestedRight() + 1, mResultsBuilder.getTotalMeridians());

                            if (mResultsBuilder.getNumAnglesTestedRight() + 1 == mResultsBuilder.getTotalMeridians()) {
                                mLastOne = true;
                                //getSnapshot().requestBatteryUpdate();
                            }

                        }
                    } else {
                        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(angle));
                        mTestState.playWrong();
                        mResultsBuilder.getCurrentAlgorithm().addFail();
                        mResultsBuilder.getCurrentHistory().addAction("Dong! (ANGLE RIGHT)");
                    }

                    break;
            }
            mPowerChanged = false;
        }

        mLastAngle = angle;
    }

    private void showResultScreen() {
        AppSettings settings = NetraGApplication.get().getSettings();
        ExamResults e = getExamResults();
        e.setId(UUID.randomUUID());
        e.setExamDate(new Date());
        if (e.getSequenceNumber() == 0) {
            e.setSequenceNumber(settings.getSequenceNumber());
            settings.setSequenceNumber(settings.getSequenceNumber() + 1);
        }
        if (NetraGApplication.get().getSettings().isShowNumbers())
            e.setEnvironment("NETRA 2");
        else
            e.setEnvironment("NETRA 2 - No Numbers");
        e.setUserToken(settings.getLoggedInUserToken());
        e.setUserName(settings.getLoggedInUsername());

        e.setAppVersion(NetraGApplication.get().getVersionName());

        DebugExam table = new DebugExam(e);

        NetraGApplication.get().setLastResult(table);

        new SaveExam().execute(table);
    }

    public boolean wasPDMeasured() {
        return mTestState.wasPDMeasured();
    }

    public int anglesMeasured() {
        return mResultsBuilder.getCurrentAlgorithm().anglesMeasured();
    }

    public void forceFinishExamWithPDResults() {
        mResultsBuilder.clearCapturedData();
        mResultsBuilder.getCurrentHistory().testFinished();
        showResultScreen();
    }

    public class SaveExam extends AsyncTask<DebugExam, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(DebugExam... params) {
            for (DebugExam e : params) {
                NetraGApplication.get().getSqliteHelper().saveDebugExam(e);
                NetraGApplication.get().getSqliteHelper().debugExamTable.setToSyncDebug(e);
                NetraGApplication.get().getSqliteHelper().debugExamTable.setToSyncInsight(e);
            }
            return true;
        }
    }

    protected void trainingAngles() {
        mResultsBuilder.switchEye(true);
        getContext().setEye(true);

        mTestState.updateProgress(Stage.READY_TO_START, Stage.TRAINING_RIGHT, 0, 1, 1);

        Log.d("FullExamFlow", "Set Working Meridian to " + checkAngleRange(mLastAngle));
        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(mLastAngle));
        Log.d("FullExamFlow", "Set Working Meridian Accepted " + mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian());

        mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
        mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);
    }

    protected void endPreTest() {
        mResultsBuilder.clearCapturedData();

        mResultsBuilder.switchEye(true);
        getContext().setEye(true);

        mTestState.updateProgress(mTestState.getCurrentStage(), Stage.PD_RIGHT, 0, 1, 1);

        Log.d("FullExamFlow", "Set Working Meridian to " + checkAngleRange(mLastAngle));
        mResultsBuilder.getCurrentAlgorithm().setWorkingMeridian(checkAngleRange(mLastAngle));
        Log.d("FullExamFlow", "Set Working Meridian Accepted " + mResultsBuilder.getCurrentAlgorithm().getWorkingMeridian());

        mResultsBuilder.logResetPower(mResultsBuilder.getDeviceId().defaultStartingPower);
        mView.resetPowerClosestTo(mResultsBuilder.getDeviceId().defaultStartingPower);
    }


    // Controller Listeners.

    @Override
    public void setDevice(Device device) {
        mView.setDevice(device);
    }

    @Override
    public void onControllerRequestsCalibrationWhiteScreen() {
        mView.setScreen(Screen.CALIBRATION);
    }

    @Override
    public void onControllerRequestsBlueScreen() {
        mView.setScreen(Screen.BLUE_SCREEN);
    }

    public void onControllerRequestsTestScreen() {
        mView.setTestingRightEye(mTestState.isRightEye());
        mTestState.setCurrentStageAsScreen();
    }

    @Override
    public void setControllerStartingPositions(float pd, float angle, long deviceID) {
        mLastControllerPD = pd;
        mLastAngle = angle;

        mView.setAngle(angle);
        mView.setSliderDisplacement(pd);

        onCalibrationDone(angle, pd, deviceID);
    }

    @Override
    public void onControllerPDChanged(float pd) {
        mLastControllerPD = pd;
        setLastControllerPD();
    }

    private void setLastControllerPD() {
        if (mTestState.isRightEye()) {
            setNosePupilDistance((float) ((mView.getDevice().defaultPD / 2.0f + mLastControllerPD)));
        } else {
            setNosePupilDistance((float) ((mView.getDevice().defaultPD / 2.0f - mLastControllerPD)));
        }

        mView.setSliderDisplacement(mLastControllerPD);
    }

    @Override
    public void onControllerMeridianChanged(float angle) {
        mResultsBuilder.logAngleChanged(angle);
        mView.setAngle(angle);
        setCurrentAngle(angle);
    }

    @Override
    public void onControllerMoveFurther() {
        setCurrentPower(mView.decreasePitch());
    }

    @Override
    public void onControllerMoveCloser() {
        setCurrentPower(mView.increasePitch());
    }

    @Override
    public void onBadTest() {
        mResultsBuilder.logBadTest();
        getContext().restartExam();
    }

    @Override
    public void onBadFrame(FrameDebugData frameDebugData) {
        mResultsBuilder.logBadFrame(frameDebugData);
    }

    @Override
    public void onControlsFound(float angle, float pd, int deviceID, CalibrationManager.DeviceCalibration parameters) {
        mResultsBuilder.logCalibrationDone(angle, pd, deviceID, parameters);
        mResultsBuilder.logShowNumbers(NetraGApplication.get().getSettings().isShowNumbers());
    }


}