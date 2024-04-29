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

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.selftestflexiblemeridians.SingleEyeBuilder;
import com.vitorpamplona.core.test.selftestflexiblemeridians.SingleEyeFullExamBuilder;
import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.domain.events.EventHistory;
import com.vitorpamplona.meridian.lineprofile.FrameDebugData;
import com.vitorpamplona.meridian.utils.CalibrationManager;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.model.ExamResults;

import java.util.Date;

/**
 * Stateful builder for patient eye exams.
 */
public class ExamResultBuilder implements SingleEyeFullExamBuilder.BucketChangeListener {

    private static final float defaultAngleStep = 22.5f;
    private static final float defaultAngleMin = 0;
    private static final float defaultAngleMax = 359;

    protected ExamResults results;

    private final EventHistory rightEyeHistory;
    private final EventHistory leftEyeHistory;
    private final SingleEyeBuilder rightEyeAlgorithms;
    private final SingleEyeBuilder leftEyeAlgorithms;

    private int testingEye = -1;

    public ExamResultBuilder(Device device, SingleEyeBuilder builderRight, SingleEyeBuilder builderLeft) {
        results = new ExamResults();

        rightEyeHistory = results.newEventHistory(ExamResults.RIGHT);
        rightEyeAlgorithms = builderRight;
        rightEyeAlgorithms.init(defaultAngleStep, defaultAngleMin, defaultAngleMax, device, results.newTestResult(ExamResults.RIGHT));
        rightEyeAlgorithms.addBucketListener(this);

        leftEyeHistory = results.newEventHistory(ExamResults.LEFT);
        leftEyeAlgorithms = builderLeft;
        leftEyeAlgorithms.init(defaultAngleStep, defaultAngleMin, defaultAngleMax, device, results.newTestResult(ExamResults.LEFT));
        leftEyeAlgorithms.addBucketListener(this);

        testingEye = ExamResults.RIGHT;

        setDeviceId(device);
    }

    public ExamResults getExamResults() {
        return results;
    }

    public void switchEye(boolean isRight) {
        if (isRight) {
            testingEye = ExamResults.RIGHT;
        } else {
            testingEye = ExamResults.LEFT;
        }
    }

    public void logChangeAPD(float value) {
        leftEyeHistory.changeAPD(value);
        rightEyeHistory.changeAPD(value);
    }

    public void logResetPower(float value) {
        getCurrentHistory().addAction("Reseting power to " + value);
    }

    public String getExportedEventHistory() {
        return rightEyeHistory.exportEventHistory(new Date()) + leftEyeHistory.exportEventHistory(new Date());
    }

    public float checkAngleRange(float angle) {
        return getCurrentAlgorithm().checkAngleRange(angle);
    }

    public SingleEyeBuilder getCurrentAlgorithm() {
        if (testingEye == ExamResults.RIGHT) {
            return rightEyeAlgorithms;
        } else {
            return leftEyeAlgorithms;
        }
    }

    public EventHistory getCurrentHistory() {
        if (testingEye == ExamResults.RIGHT) {
            return rightEyeHistory;
        } else {
            return leftEyeHistory;
        }
    }

    public void setLocation(double latitude, double longitude) {
        results.setLatitude(latitude);
        results.setLongitude(longitude);
    }

    public void measurePD(float leftCode, float rightCode) {
        float pd = results.getDevice().measurePD(leftCode, rightCode);

        results.getLeftEye().getNetra().setNosePupilDistance(pd / 2);
        results.getRightEye().getNetra().setNosePupilDistance(pd / 2);

        rightEyeHistory.inputAPD(rightCode, true);
        rightEyeHistory.inputAPD(leftCode, false);
        rightEyeHistory.changeAPD(pd);

        leftEyeHistory.inputAPD(rightCode, true);
        leftEyeHistory.inputAPD(leftCode, false);
        leftEyeHistory.changeAPD(pd);
    }

    public boolean isCurrentEyeDone() {
        return getCurrentAlgorithm().isTestDone();
    }

    public boolean isDoingMovingFirst() {
        return getCurrentAlgorithm().isDoingRoughAlignmentFirst();
    }

    public boolean isAlreadyAligned() {
        return !isDoingMovingFirst();
    }

    public void saveLastResult() {
        getCurrentHistory().addAction("Saving " + getCurrentAlgorithm().getWorkingMeridian() + " " + getCurrentAlgorithm().getCurrentPower());
        getCurrentAlgorithm().saveCurrentResult();
    }

    public void clearCapturedData() {
        leftEyeAlgorithms.clearCapturedData();
        rightEyeAlgorithms.clearCapturedData();

        rightEyeHistory.addAction("PD Done Clear Data " + rightEyeAlgorithms.getCurrentPrescription().testResults().size() + " angles");
        leftEyeHistory.addAction("PD Done Clear Data " + leftEyeAlgorithms.getCurrentPrescription().testResults().size() + " angles");
    }

    public void updatePrescription() {
        rightEyeHistory.addAction("Fitting Curve with " + rightEyeAlgorithms.getCurrentPrescription().testResults().size() + " angles");
        for (MeridianPower p : rightEyeAlgorithms.getCurrentPrescription().testResults().values()) {
            rightEyeHistory.addNewStep(p.getAngle(), p.getPower(), null);
        }

        rightEyeHistory.addAction("Time Results " + rightEyeAlgorithms.getCurrentPrescription().testResults2().size() + " angles");
        for (MeridianPower p : rightEyeAlgorithms.getCurrentPrescription().testResults2()) {
            rightEyeHistory.addNewStep(p.getAngle(), p.getPower(), null);
        }

        leftEyeHistory.addAction("Fitting Curve with " + leftEyeAlgorithms.getCurrentPrescription().testResults().size() + " angles");
        for (MeridianPower p : leftEyeAlgorithms.getCurrentPrescription().testResults().values()) {
            leftEyeHistory.addNewStep(p.getAngle(), p.getPower(), null);
        }

        leftEyeHistory.addAction("Time Results " + leftEyeAlgorithms.getCurrentPrescription().testResults2().size() + " angles");
        for (MeridianPower p : leftEyeAlgorithms.getCurrentPrescription().testResults2()) {
            leftEyeHistory.addNewStep(p.getAngle(), p.getPower(), null);
        }

        StringBuilder debugRight = new StringBuilder();
        StringBuilder debugLeft = new StringBuilder();

        rightEyeAlgorithms.updateFitAndRound(debugRight);
        leftEyeAlgorithms.updateFitAndRound(debugLeft);

        rightEyeHistory.addActions(debugRight.toString());
        leftEyeHistory.addActions(debugLeft.toString());
    }

    public void persistPrescription() {
        rightEyeHistory.addAction("Persisting exam results");
        leftEyeHistory.addAction("Persisting exam results");
    }

    public AstigmaticLensParams getCurrentRoundedPrescription() {
        return getCurrentAlgorithm().getCurrentPrescription().getRounded();
    }

    public AstigmaticLensParams getLeftRoundedPrescription() {
        return leftEyeAlgorithms.getCurrentPrescription().getRounded();
    }

    public AstigmaticLensParams getRightRoundedPrescription() {
        return rightEyeAlgorithms.getCurrentPrescription().getRounded();
    }

    public int getNumAnglesTestedLeft() {
        return leftEyeAlgorithms.getCurrentPrescription().getNumAnglesTested();
    }


    public int getNumAnglesTestedRight() {
        return rightEyeAlgorithms.getCurrentPrescription().getNumAnglesTested();
    }

    public int getTotalMeridians() {
        return rightEyeAlgorithms.numberOfMeridiansRequiredToComplete();
    }

    public boolean checkIfDone() {
        return getCurrentAlgorithm().checkIfDone();
    }

    public void addResult(float angle, float power) {
        if (isCurrentEyeDone()) return;

        getCurrentAlgorithm().newTestResult(angle, power);

        if (!isCurrentEyeDone()) {
            getCurrentHistory().addNewStep(angle, power, null);
            getCurrentHistory().addAction("{\"cmd\":\"OnTestPowerChanged\",\"data\":" + power + "}");
        }
    }

    @Override
    public void bucketChanged(float newAngle) {
        if (isCurrentEyeDone()) return;

        getCurrentHistory().addAction("One Two");
        getCurrentHistory().askSetupNewAngle(newAngle);
        getCurrentHistory().addAction("LookingToTheBackGround");
        getCurrentHistory().addAction("Stepping");
    }

    public void requestBatteryUpdate() {
        if (isCurrentEyeDone()) return;
        getCurrentHistory().requestBatteryUpdate();
    }

    public void showingScreen(String name) {
        if (isCurrentEyeDone()) return;
        getCurrentHistory().addAction(name);
    }

    public void showingMoving() {
        if (isCurrentEyeDone()) return;
        getCurrentHistory().addAction("Stepping");
    }

    public int getNumAnglesTested() {
        return getCurrentAlgorithm().getCurrentPrescription().getNumAnglesTested();
    }

    public float getPD() {
        return results.getInterpupillaryDistanceMM();
    }

    public void setNosePupilDistance(float pd) {
        if (testingEye == ExamResults.RIGHT) {
            results.getRightEye().getNetra().setNosePupilDistance(pd);
        } else {
            results.getLeftEye().getNetra().setNosePupilDistance(pd);
        }
    }

    // ---------------------
    // Events
    public void examComplete() {
        rightEyeHistory.addAction("Computing Results");
        leftEyeHistory.addAction("Computing Results");

        updatePrescription();

        ExamResults e = getExamResults();

        if (NetraGApplication.get().getSettings().isRoundResults()) {
            e.getRightEye().getNetra().updateFromOriginalData();
            e.getLeftEye().getNetra().updateFromOriginalData();
        } else {
            e.getRightEye().getNetra().updateFromFittedOriginalData();
            e.getLeftEye().getNetra().updateFromFittedOriginalData();
        }
    }

    public void examCanceled() {
        rightEyeHistory.cancelTest("Nobody knows");
        leftEyeHistory.cancelTest("Nobody knows");
    }


    public Device getDeviceId() {
        return results.getDevice();
    }

    public void setDeviceId(Device device) {
        results.setDevice(device);
        rightEyeHistory.addDeviceID((int) device.id);
        leftEyeHistory.addDeviceID((int) device.id);
    }

    public void logBadTest() {
        getCurrentHistory().addAction("Bad Test");
    }

    public void logAngleChanged(float angle) {
        // TODO: Remove JSon Struccture.
        getCurrentHistory().addAction("{\"cmd\":\"OnTestAngleChanged\",\"data\":" + angle + "}");
    }

    public void logPDChanged(float angle) {
        // TODO: Remove JSon Struccture.
        getCurrentHistory().addAction("{\"cmd\":\"OnTestPDChanged\",\"data\":" + angle + "}");
    }

    public void logAppVersions(String deviceVersion) {
        rightEyeHistory.addVersions(deviceVersion, deviceVersion);
        leftEyeHistory.addVersions(deviceVersion, deviceVersion);
    }

    public void logFailedBarcodeIDReading(long barcodeID) {
        getCurrentHistory().addAction("Invalid Barcode Reading " + barcodeID);
    }

    public void logBarcodeIDNotInDataset(long barcodeID) {
        getCurrentHistory().addAction("Device " + barcodeID + " not found in DeviceDataset");
    }

    public void logBadFrame(FrameDebugData frameDebugData) {
        getCurrentHistory().addAction("{\"cmd\":\"OnNewDebugInfo\",\"data\":" + newDebugInfo(frameDebugData) + "}");
    }

    public void logCalibrationDone(float angle, float pd, int deviceID, CalibrationManager.DeviceCalibration parameters) {
        getCurrentHistory().addAction("{\"cmd\":\"OnCalibrationDone\",\"angle\":" + angle + ",\"pd\":" + pd + ",\"deviceId\":" + deviceID + "}");
        getCurrentHistory().addAction("{\"cmd\":\"OnNewCalibration\",\"data\":" + newCalibration(parameters) + "}");
    }

    public void logShowNumbers(boolean showing) {
        getCurrentHistory().addAction("ShowNumbers " + showing);
    }

    public static String newDebugInfo(FrameDebugData f) {

        JsonArray data = new JsonArray();

        data.add(new JsonPrimitive(f.ratchetAngle));
        data.add(new JsonPrimitive(f.scrollyWheelAngle));
        data.add(new JsonPrimitive(f.ratchetCenterX));
        data.add(new JsonPrimitive(f.ratchetCenterY));
        data.add(new JsonPrimitive(f.sliderValueMM));
        data.add(new JsonPrimitive(f.processingTime));
        data.add(new JsonPrimitive(f.calibrated));
        data.add(new JsonPrimitive(f.calibrationIsGood));
        data.add(new JsonPrimitive(f.numberOfFramesUsedForCalibration));
        data.add(new JsonPrimitive(f.numberOfFramesUsedForInitialValues));
        data.add(new JsonPrimitive(f.numberOfFramesInTest));
        data.add(new JsonPrimitive(f.numberOfFramesDiscarded));
        data.add(new JsonPrimitive(f.averageFPS));

        // signal quality
        data.add(new JsonPrimitive(f.signalQualitySlider));
        data.add(new JsonPrimitive(f.signalQualityScrolly));

        JsonArray qualityCalibrationDots = new JsonArray(); // signal quality of calibration dots (max 3 signals)
        for (int i : f.signalQualityCalibrationDots) {
            qualityCalibrationDots.add(new JsonPrimitive(i));
        }
        data.add(qualityCalibrationDots);

        JsonArray qualityRatchet = new JsonArray(); // signal quality of ratchet dots (max 4 signals)
        for (int i : f.signalQualityRatchet) {
            qualityRatchet.add(new JsonPrimitive(i));
        }
        data.add(qualityRatchet);

        data.add(new JsonPrimitive(f.errorCode));
        data.add(new JsonPrimitive(f.standardDeviationSliderLast30));
        data.add(new JsonPrimitive(f.standardDeviationScrollyLast30));
        data.add(new JsonPrimitive(f.standardDeviationCalibrationDotsLast30));
        data.add(new JsonPrimitive(f.standardDeviationRatchetLast30));

        return data.toString();
    }

    public static String newCalibration(CalibrationManager.DeviceCalibration p) {

        JsonArray data = new JsonArray();

        data.add(new JsonPrimitive(p.success_rate));
        data.add(new JsonPrimitive(p.deviceId));

        data.add(new JsonPrimitive(p.sliderP1X));
        data.add(new JsonPrimitive(p.sliderP1Y));
        data.add(new JsonPrimitive(p.sliderP2X));
        data.add(new JsonPrimitive(p.sliderP2Y));

        data.add(new JsonPrimitive(p.calibrationP1X));
        data.add(new JsonPrimitive(p.calibrationP1Y));
        data.add(new JsonPrimitive(p.calibrationP2X));
        data.add(new JsonPrimitive(p.calibrationP2Y));

        data.add(new JsonPrimitive(p.ratchetX));
        data.add(new JsonPrimitive(p.ratchetY));
        data.add(new JsonPrimitive(p.ratchetR));

        data.add(new JsonPrimitive(p.scrollyX));
        data.add(new JsonPrimitive(p.scrollyY));
        data.add(new JsonPrimitive(p.scrollyR1));

        return data.toString();
    }
}