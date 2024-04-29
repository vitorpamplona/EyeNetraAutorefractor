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
package com.vitorpamplona.core.testdevice;

public class SlitPattern {

    //Default values
    private static final float DEF_DIST_MASK_DISP = 65f;
    private static final float DEF_DIST_EYE_MASK = 10f;

    //Working with masks with radius 1.2mm
    private static final float INCH_IN_MM = 25.4f;

    public float mDotPitchInTheMaskat0;
    public float mDotPitchInTheMaskat90;

    public Pair mSelectedPair; //Current two points working with ( also defines angle)

    float mPixelSize; //Per device pixel size

    public RequiredCorrection mEstimator; //Estimator to take Pairs and return diopters

    public SlitPattern(float dpi) {
        mSelectedPair = null;
        init(dpi);
    }

    public void init(float dpi) {
        init(dpi, dpi, -1, -1, -1);
    }

    public void init(float xDPI, float yDPI, float distanceMaskDisplay, float lensFocalLength, float slitRadius) {
        init(xDPI, yDPI, distanceMaskDisplay, lensFocalLength, -1, slitRadius);
    }

    public void init(float xDPI, float yDPI,
                     float distanceMaskDisplay, float lensFocalLength,
                     float distanceEyeMask, float slitRadius) {

        if (distanceMaskDisplay < 0) distanceMaskDisplay = DEF_DIST_MASK_DISP;
        if (lensFocalLength < 0) distanceMaskDisplay = DEF_DIST_MASK_DISP;
        if (distanceEyeMask < 0) distanceEyeMask = DEF_DIST_EYE_MASK;

        float dotsPerMMat0 = xDPI / INCH_IN_MM;
        float dotsPerMMat90 = yDPI / INCH_IN_MM;

        mPixelSize = INCH_IN_MM / xDPI;
        mEstimator = new RequiredCorrectionMaskLens(distanceEyeMask, distanceMaskDisplay,
                lensFocalLength, mPixelSize); // Binocular.

        mDotPitchInTheMaskat0 = dotsPerMMat0 * slitRadius;
        mDotPitchInTheMaskat90 = dotsPerMMat90 * slitRadius;

        mSelectedPair = null;
    }

    //Set the test for a specific angle
    public void setAngle(float angle) {
        //		angle = InternalAngleToDoc.fromDocToInternalAngle(angle);

        Point2D p1 = new Point2D(
                (float) Math.cos((float) Math.toRadians(angle)) * mDotPitchInTheMaskat0,
                (float) Math.sin((float) Math.toRadians(angle)) * mDotPitchInTheMaskat90);

        Point2D p2 = new Point2D(
                (float) Math.cos((float) Math.toRadians(angle + 180)) * mDotPitchInTheMaskat0,
                (float) Math.sin((float) Math.toRadians(angle + 180)) * mDotPitchInTheMaskat90);

        mSelectedPair = new Pair(p1, p2, angle);
        setGivenValue(1);
    }

    public boolean isActive() {
        return mSelectedPair != null;
    }

    public Pair getWorkingPair() {
        return mSelectedPair;
    }

    //Step lines further apart
    public void increasePitch() {
        Pair workingPair = getWorkingPair();
        workingPair.increaseDotPitch();
    }

    //Step lines closer together
    public void reducePitch() {
        Pair workingPair = getWorkingPair();
        workingPair.reduceDotPitch();
    }

    //Increase Dot Pitch with special optimizations
    public void increaseHalfPitch() {
        Pair workingPair = getWorkingPair();
        workingPair.increaseDotPitchSuper();
    }

    //Decrease Dot Pitch with special optimizations
    public void reduceHalfPitch() {
        Pair workingPair = getWorkingPair();
        workingPair.reduceDotPitchSuper();
    }

    public void restart() {
        mSelectedPair = null;
    }

    public void stepN(float origin, float N) {
        setGivenValue(origin);
        stepN(N);
    }

    public void stepN(float N) {
        if (N < 0) {
            for (int i = 0; i < Math.abs((int) N); i++) {
                increasePitch();
            }
            if (Math.abs((Math.abs(N) - Math.abs((int) N)) - 0.5) < 0.1) {
                increaseHalfPitch();
            }
        } else if (N > 0) {
            for (int i = 0; i < Math.abs((int) N); i++) {
                reducePitch();
            }
            if (Math.abs((Math.abs(N) - Math.abs((int) N)) - 0.5) < 0.1) {
                reduceHalfPitch();
            }
        }
    }

    public float stepNAndReturnPower(float N) {
        stepN(N);

        return computeMeridianPower();
    }

    public float stepNAndReturnPower(float origin, float N) {
        setGivenValue(origin);

        stepN(N);

        return computeMeridianPower();
    }

    //Set the lines to be a specific amount of diopters apart
    public void setGivenValue(float diopters) {

        if (!isActive()) {
            return;
        }

        float presc = computeMeridianPower();

        if (Math.abs(diopters - presc) < 0.1) {
            return;
        }

        if (diopters - presc < 0) {
            while (diopters - presc < 0) {
                reducePitch();
                presc = computeMeridianPower();
            }

            float diffPos = Math.abs(presc - diopters);

            increasePitch();
            presc = computeMeridianPower();

            float diffNeg = Math.abs(presc - diopters);

            if (diffNeg < diffPos) {
                presc = computeMeridianPower();
                return;
            } else {
                reducePitch();
                presc = computeMeridianPower();
            }
        } else {
            while (diopters - presc > 0) {
                increasePitch();
                presc = computeMeridianPower();
            }

            float diffNeg = Math.abs(presc - diopters);

            reducePitch();
            presc = computeMeridianPower();

            float diffPos = Math.abs(presc - diopters);

            if (diffPos < diffNeg) {
                presc = computeMeridianPower();
                return;
            } else {
                increasePitch();
                presc = computeMeridianPower();
            }
        }
    }

    public void reset(float dpi, float distanceMaskPhone, float lensFocalLength, float eyemask, float slitRadius) {
        init(dpi, dpi, distanceMaskPhone, lensFocalLength, eyemask, slitRadius);
    }

    //Returns current diopters
    public Float computeMeridianPower() {
        if (isActive()) {
            return mEstimator.computeDiopters(getWorkingPair());
        }
        return null;
    }

    //Returns current angle
    public Float getWorkingMeridian() {
        if (isActive()) {
            return mSelectedPair.angle;
        }
        return null;
    }
}