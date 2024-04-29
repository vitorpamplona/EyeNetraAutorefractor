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
package com.vitorpamplona.core.test.selftestflexiblemeridians;

import com.vitorpamplona.core.fitting.OutlierRemoval;
import com.vitorpamplona.core.fitting.SinusoidalFitting;
import com.vitorpamplona.core.models.AstigmaticLensParams;
import com.vitorpamplona.core.models.ComputedPrescription;
import com.vitorpamplona.core.models.EyeGlassesUsageType;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.test.Acceptance;
import com.vitorpamplona.core.test.BestRounding;
import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.utils.AngleDiff;

import java.util.ArrayList;
import java.util.List;

/**
 * Stateful class (ComputedPrescription curPrescription) for performing a single eye test with
 * A&B options and controlling the angles to measure.
 *
 * This class encapsulates:
 * - Determines what is the starting power for each angle
 *
 * When ready for a new test.
 * 1. Instantiate Algorithms.
 * 2. Use setWorkingMeridian to update the class.
 * 3. Allow the person to move the lines. For each movement do newTestResult(angle, power).
 * 4. When the person overlaps the lines call setWorkingMeridian.
 * 8. Call updateFitAndRound() to update the CurrentPrescription object.
 * 9. Repeat until the isTestDone equals to true.
 *
 */
public abstract class SingleEyeBuilder {

    // Always starts at +1D
    private static int DEFAULT_STARTING_POWER = 1;

    private static int MINIMUM_SET_OF_MERIDIANS_TO_TRUST_THE_FITTING = 3;

    // TODO: This value is
    // number of steps per click of move button during lines alignment phase
    private static final int NUM_STEP_PER_MOVE = 1;

    private MeridianPower lastValidDataPoint;
    private float startingPower = DEFAULT_STARTING_POWER;
    private int movingAngles = MINIMUM_SET_OF_MERIDIANS_TO_TRUST_THE_FITTING;

    private float currentBucket;

    private boolean testFinished = false;

    /** Result of this Class */
    protected ComputedPrescription currentPrescription;

    protected List<Float> possibleAngles = new ArrayList<Float>();

    public static interface BucketChangeListener {
        public void bucketChanged(float newAngle);
    }

    private List<BucketChangeListener> listeners = new ArrayList<BucketChangeListener>();

    /**
     * Initialization, for now just takes angle data, possibly past prescriptions in future?
     */
    public SingleEyeBuilder() {
        // should call init later
    }

    public SingleEyeBuilder(float angleStep, float angleMin, float angleMax, ComputedPrescription currentPrescription, Device device) {
        init(angleStep, angleMin, angleMax, device, currentPrescription);
    }

    public SingleEyeBuilder(float angleStep, float angleMin, float angleMax, Device device) {
        init(angleStep, angleMin, angleMax, device, new ComputedPrescription());
    }

    public void init(float angleStep, float angleMin, float angleMax, Device device, ComputedPrescription presc) {
        this.currentPrescription = presc;
        if (device != null) {
            startingPower = device.defaultStartingPower;
            movingAngles = device.movingAngles;
        }

        // * Produces twice the amount of buckets to avoid losing something.
        //Log.i("SingleEyeBuilder: ", " Start ");

        for (float i = angleMin; i < checkAngleRange(angleMax); i += angleStep / 2) {
            possibleAngles.add(checkAngleRange(i));

            //Log.i("SingleEyeBuilder: ", " Adding angle " + checkAngleRange(i));
        }

        //Log.i("SingleEyeBuilder: ", " Finish ");
    }

    public int numberOfMeridiansRequiredToComplete() {
        return (int) (possibleAngles.size() / 2.0f);
    }

    public abstract float checkAngleRange(float angle);

    public float findClosestPossibleAngleTo(float angleToFind) {
        float closest = -99999;
        float minDiff = 99999;
        for (Float angle : possibleAngles) {
            if (AngleDiff.diff360(angle, angleToFind) < minDiff) {
                minDiff = AngleDiff.diff360(angle, angleToFind);
                closest = checkAngleRange(angle);
            }
        }
        return closest;
    }

    public float findClosestPossibleGroupToCurrentAngle() {
        if (lastValidDataPoint != null)
            return findClosestPossibleAngleTo(lastValidDataPoint.getAngle());
        else
            return Float.NaN;
    }

    public boolean checkIfDone() {
        // if passed trhough all angles.... finish the test.
        if (hasEnoughAngles()) {
            testFinished = true;
        }
        return testFinished;
    }

    public void saveCurrentResult() {
        if (lastValidDataPoint != null) {
            currentPrescription.saveResult(lastValidDataPoint);
        }
    }

    public void setWorkingMeridian(float angle) {
        float newBucket = findClosestPossibleAngleTo(checkAngleRange(angle));
        if (lastValidDataPoint != null) {
            boolean changeBuckets = Math.abs(newBucket - currentBucket) > 5;
            boolean bigChange = AngleDiff.diff360(lastValidDataPoint.getAngle(), angle) > 5;

            if (changeBuckets && bigChange) {
                // activally changed the angle.

                currentBucket = newBucket;

                // if passed trhough all angles.... finish the test.
                checkIfDone();

                refreshListeners();
            } else {
                // did n't changed
                return;
            }
        } else {
            currentBucket = newBucket;

            refreshListeners();
        }

        if (!testFinished) {
            lastValidDataPoint = currentPrescription.testResults().get(newBucket);
            if (lastValidDataPoint == null) {
                lastValidDataPoint = new MeridianPower(angle, getStartingPower(angle));
            }
        }
    }

    public void addFail() {
        currentPrescription.addFail();
    }

    public boolean willItBeANewBucket(float newAngle) {
        float newBucket = findClosestPossibleAngleTo(checkAngleRange(newAngle));
        boolean changeBuckets = Math.abs(newBucket - currentBucket) > 5;

        //Log.d("Algorithms", "New Bucket " + newBucket + " Current " + currentBucket + " New " + newBucket + " is new " + changeBuckets);
        //Log.d("Algorithms", "LastValidaDataPoint " + (lastValidDataPoint != null ? lastValidDataPoint.getAngle() : "null") + " New Angle " + newAngle);

        if (lastValidDataPoint == null) return changeBuckets;

        //Log.d("Algorithms", "New Bucket " + newBucket + " Current " + currentBucket + " is new " + changeBuckets);

        boolean bigChange = AngleDiff.diff360(lastValidDataPoint.getAngle(), newAngle) > 9;

        //Log.d("Algorithms", "BigChange " + bigChange);

        return changeBuckets && bigChange;
    }

    private void refreshListeners() {
        for (BucketChangeListener bucket : listeners) {
            bucket.bucketChanged(currentBucket);
        }
    }

    public int anglesMeasured() {
        return currentPrescription.testResults().size();
    }

    protected boolean hasEnoughAngles() {
        return (anglesMeasured() >= numberOfMeridiansRequiredToComplete());
    }

    public float getCurrentPower() {
        if (lastValidDataPoint != null) {
            return lastValidDataPoint.getPower();
        }
        return Float.NaN;
    }

    public float getWorkingMeridian() {
        if (lastValidDataPoint != null) {
            return lastValidDataPoint.getAngle();
        }
        return Float.NaN;
    }

    public ComputedPrescription getCurrentPrescription() {
        return currentPrescription;
    }

    /**
     * Fits the measured meridians, rounds it to steps of 0.25D and updates the
     * final Prescription
     * @return
     */
    public ComputedPrescription updateFitAndRound(StringBuilder debug) {
        AstigmaticLensParams fitted = fit();
        currentPrescription.setFitted(fitted);

        if (debug != null)
            debug.append("Fitted: " + fitted + "\n");

        AstigmaticLensParams noOutliers = removeOutliers(fitted, debug);
        currentPrescription.setFitted(noOutliers);

        AstigmaticLensParams softedCyl = softedCyls(noOutliers);
        currentPrescription.setSoftedCyls(softedCyl);

        if (debug != null)
            debug.append("NoOutilers: " + noOutliers + "\n");

        AstigmaticLensParams rounded = rounds(noOutliers);
        currentPrescription.setRounded(rounded);

        if (debug != null)
            debug.append("Rounded: " + rounded + "\n");

        return currentPrescription;
    }

    public AstigmaticLensParams fitAndRound() {
        return rounds(fit());
    }

    public AstigmaticLensParams fitRemoveOutliersAndRound(StringBuilder debug) {
        return rounds(removeOutliers(fit(), debug));
    }

    /**
     * Best fits the raw data.
     * @return the raw prescription
     */
    public AstigmaticLensParams fit() {
        return new SinusoidalFitting().curveFitting(currentPrescription.allResults());
    }

    /**
     * Remove outliers.
     *
     * @return the raw prescription
     */
    public AstigmaticLensParams removeOutliers(AstigmaticLensParams fitted, StringBuilder debug) {
        if (currentPrescription.testResults().size() <= 7) {
            if (debug != null)
                debug.append("Skipping outliers removal due to having less than 8 angles.");
            return fitted;
        }

        return new OutlierRemoval().run(currentPrescription.allResults(),
                currentPrescription.getFails(), fitted, debug);
    }

    /**
     * Softens the Cylinder Value.
     *
     * @return the 0.01-stepped prescription
     */
    private AstigmaticLensParams softedCyls(AstigmaticLensParams fitted) {
        return new BestRounding().softenCylinder(fitted, currentPrescription.allResults(), currentPrescription.getFails());
    }

    /**
     * Best fits the raw data and rounds to steps of 0.25f.
     *
     * @return the 0.25-stepped prescription
     */
    private AstigmaticLensParams rounds(AstigmaticLensParams fitted) {
        return new BestRounding().round25(fitted, currentPrescription.allResults(), currentPrescription.getFails(), null);
    }

    /**
     * Best fits the raw data and rounds to steps of 0.0625.
     *
     * @return the 0.06-stepped prescription
     */
    private AstigmaticLensParams rounds625(AstigmaticLensParams fitted) {
        return new BestRounding().round0625(fitted, currentPrescription.allResults(), currentPrescription.getFails(), null);
    }

    /**
     * Best fits the raw data and rounds to steps of 0.0625.
     * @return the 0.12-stepped prescription
     */
    private AstigmaticLensParams rounds125(AstigmaticLensParams fitted) {
        return new BestRounding().round125(fitted, currentPrescription.allResults(), currentPrescription.getFails(), null);
    }

    /**
     * Computes the best acceptance based on the new prescription.
     *
     * @param currentRX
     *            current glasses RX
     * @param fitted
     *            current raw fitted prescription
     * @param usage
     *            usage of eye glasses
     * @param age
     *            age of the patient.
     * @return prescription adjusted for the Patients neeed.
     */
    private AstigmaticLensParams acceptance(AstigmaticLensParams currentRX, boolean usingGlasses,
                                            AstigmaticLensParams fitted, EyeGlassesUsageType usage, int age) {
        return new Acceptance().compute(currentRX, usingGlasses, fitted, usage, age);
    }

    /**
     * Computes the best acceptance based on the new prescription and updates
     * the current Prescription.
     *
     * @param currentRX
     *            current glasses RX
     * @param usage
     *            usage of eye glasses
     * @param age
     *            age of the patient.
     * @return prescription adjusted for the Patients neeed.
     */
    public ComputedPrescription updateFitAndAcceptance(
            AstigmaticLensParams currentRX, boolean usingGlasses, EyeGlassesUsageType usage, int age) {

        updateFitAndRound(null);

        AstigmaticLensParams curr = new AstigmaticLensParams(currentRX.getSphere(), currentRX.getCylinder(),
                currentRX.getAxis());

        currentPrescription.setAccepted(acceptance(curr, usingGlasses, currentPrescription.getFitted(), usage, age));

        return currentPrescription;
    }

    /**
     * Returns the starting power for the meridian.
     *
     * @param angle
     *            the meridian that is being tested.
     * @return starting power to be used with new angle
     */
    private float getStartingPower(float angle) {
        MeridianPower closest = currentPrescription.testResults().getClosestToAngle180(angle);
        if (closest != null && AngleDiff.diff360(closest.getAngle(), angle) < 50) {
            return closest.getPower();
        }

        return startingPower;
    }

    public boolean isDoingRoughAlignmentFirst() {
        return currentPrescription.getNumAnglesTested() < movingAngles;
    }

    // Returns whether test is done (possibly will not be used!)
    public boolean isTestDone() {
        return testFinished;
    }

    public void clearCapturedData() {
        getCurrentPrescription().testResults().clear();
        getCurrentPrescription().testResults2().clear();
        testFinished = false;
    }

    public void newTestResult(float angle, float power) {
        setWorkingMeridian(angle);

        //Log.i("SingleEyeBuilder: ", " Adding " + angle + " " + power);

        if (!testFinished)
            currentPrescription.add(currentBucket, lastValidDataPoint = new MeridianPower(checkAngleRange(angle), power));


        //Log.i("SingleEyeBuilder: ", " Adding " + angle + " " + power);
    }

    public int getNumStepsPerMove() {
        return NUM_STEP_PER_MOVE;
    }

    public void addBucketListener(BucketChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeBucketListener(BucketChangeListener listener) {
        this.listeners.remove(listener);
    }
}
