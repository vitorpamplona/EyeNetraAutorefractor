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
package com.vitorpamplona.meridian.utils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class LineProfileUtils {

    private static Stopwatch clock = new Stopwatch("blah");  // use tic() and toc() to display time difference in logcat (tag:STOPWATCH)

    private LineProfileUtils() {
    }

    private static void applyKernel(Integer[] outputArray, Integer[] inputArray, Integer[] kernel, int n) {

        outputArray[n] = 0;
        int numberCounter = 0;
        int inputIdx = 0;

        for (int s = 0; s < kernel.length; s++) {
            inputIdx = n + s - kernel.length / 2;
            if (inputIdx > 0 && inputIdx < inputArray.length) {
                outputArray[n] += inputArray[inputIdx] * kernel[s];
                numberCounter++;
            }
        }

        outputArray[n] /= numberCounter;
    }

    public static Integer[] applyKernel(Integer[] inputArray, Integer[] kernel) {
        Integer[] outputArray = new Integer[inputArray.length];

        for (int n = 0; n < inputArray.length; n++) {
            applyKernel(outputArray, inputArray, kernel, n);
        }

        return outputArray;
    }

    /**
     * linear boxcar moving average filter for arrays
     *
     * @param inputArray
     * @param smoothingWidth Preferably an odd number (kernel size). If an even number is inputted, the procedure will consider half of the width for each side of the array.
     * @return
     */

    public static Integer[] linearMovingAverage(Integer[] inputArray, int smoothingWidth) {
        return linearMovingAverage(inputArray, smoothingWidth, 1);
    }


    public static Integer[] linearMovingAverage(Integer[] inputArray, int smoothingWidth, int kernelBase) {
        if (smoothingWidth <= 1) {
            return Arrays.copyOf(inputArray, inputArray.length);
        }

        // if odd, add one.
        Integer[] kernel = new Integer[smoothingWidth % 2 == 0 ? smoothingWidth + 1 : smoothingWidth];
        Arrays.fill(kernel, kernelBase);

        return applyKernel(inputArray, kernel);
    }

    /**
     * find center of mass of an array (Integer[])
     */
    public static Float centerOfMass(Integer[] inputArray) {
        return centerOfMass(inputArray, 0, 0, inputArray.length);
    }

    public static Float centerOfMass(Integer[] inputArray, int floorCrop) {
        return centerOfMass(inputArray, floorCrop, 0, inputArray.length);
    }

    public static Float centerOfMass(Integer[] inputArray, int floorCrop, int start, int end) {
        int totalMoment = 0;
        int totalMass = 0;
        int p;

        // clip index at borders
        if (start >= end) {
            return null;
        }

        // protection
        if (start < 0) {
            start = 0;
        }
        if (end > inputArray.length - 1) {
            end = inputArray.length;
        }

        for (int n = start; n < end; n++) {
            p = inputArray[n];
            if (p <= floorCrop) continue;
            totalMoment += inputArray[n] * n;
            totalMass += inputArray[n];
        }

        if (totalMass > 0) { // to prevent dividing by 0
            return totalMoment / ((float) totalMass); // calculate center of mass
        } else {
            return null; // array is all zeros.  no center of mass available
        }
    }


    /**
     * find center of mass of an array (Integer[])
     */
    public static Float centerOfMass(double[] inputArray) {
        return centerOfMass(inputArray, 0, inputArray.length);
    }

    public static Float centerOfMass(double[] inputArray, int start, int end) {

        int totalMoment = 0;
        int totalMass = 0;

        // clip index at borders
        if (start >= end) {
            return null;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > inputArray.length - 1) {
            end = inputArray.length;
        }

        for (int n = start; n < end; n++) {
            totalMoment += inputArray[n] * n;
            totalMass += inputArray[n];
        }

        if (totalMass > 0) { // to prevent dividing by 0
            return totalMoment / ((float) totalMass); // calculate center of mass
        } else {
            return null; // array is all zeros.  no center of mass available
        }
    }


    //
    // find total of mass of an array
    //
    public static Float totalMass(Integer[] inputArray) {
        return totalMass(inputArray, 0, inputArray.length);
    }

    public static Float totalMass(Integer[] inputArray, int start, int length) {

        if (inputArray == null) return null;

        float totalMass = 0;

        for (int n = start; n < length; n++) {
            if (inputArray[n] == null) return null;

            totalMass += inputArray[n];
        }
        return totalMass;
    }


    //
    // find the maxima of an array
    //
    public static float minimaValue(Integer[] inputArray) {
        return minimaValue(inputArray, 0, inputArray.length);
    }

    public static float minimaValue(Integer[] inputArray, int start, int length) {

        float minima = Integer.MAX_VALUE;

        for (int n = start; n < length; n++) {
            if (inputArray[n] < minima) {
                minima = inputArray[n];
            }
        }
        return minima;
    }


    //
    // find the maxima of an array
    //
    public static float maximaValue(Integer[] inputArray) {
        return maximaValue(inputArray, 0, inputArray.length);
    }

    public static float maximaValue(Integer[] inputArray, int start, int length) {

        float maxima = Integer.MIN_VALUE;

        for (int n = start; n < length; n++) {
            if (inputArray[n] > maxima) {
                maxima = inputArray[n];
            }
        }
        return maxima;
    }


    //
    // find the index of the maxima of an array
    //
    public static Float maximaIndex(Integer[] inputArray) {
        return maximaIndex(inputArray, 0, inputArray.length);
    }

    public static Float maximaIndex(Integer[] inputArray, int start, int end) {

        int maxima = Integer.MIN_VALUE;
        float maximaIndex = 0;

        for (int n = start; n < end; n++) {
            if (inputArray[n] > maxima) {
                maxima = inputArray[n];
                maximaIndex = n;
            }
        }
        return maximaIndex;
    }


    /**
     * Normalizes the Array between 0 to ceilingValue on the range of start to length.
     *
     * Mutable array. This method changes the input
     *
     * @param inputArray
     * @return
     */
    public static Integer[] normalize(Integer[] inputArray, int ceilingValue, int start, int length) {

        float maxValue = maximaValue(inputArray, 0, inputArray.length);

        for (int n = 0; n < inputArray.length; n++) {
            inputArray[n] = (int) (inputArray[n] / maxValue * ceilingValue);
        }

        return inputArray;
    }

    /**
     * Normalizes the Array between 0 to ceilingValue.
     *
     * Mutable array. This method changes the input
     *
     * @param inputArray
     * @return
     */
    public static Integer[] normalize(Integer[] inputArray, int ceilingValue) {
        return normalize(inputArray, ceilingValue, 0, inputArray.length);
    }

    /**
     * Normalizes the Array between 0 to 1.
     *
     * Mutable array. This method changes the input
     *
     * @param inputArray
     * @return
     */
    public static Integer[] normalize(Integer[] inputArray) {
        return normalize(inputArray, 1, 0, inputArray.length);
    }


    //
    // find the index of top N values in array
    //
    // Only use if N is very small.
    //
    public static Integer[] findTopValuesIndex(Integer[] inputArray, int N) {
        Set<Integer> indexForSearch = new HashSet<Integer>();
        List<Integer> sortedIndex = new ArrayList<Integer>();

        Integer idx = null;
        int testval;

        for (int p = 0; p < N; p++) {
            idx = 0;
            testval = Integer.MIN_VALUE;
            for (int n = 0; n < inputArray.length; n++) {
                if (indexForSearch.contains(n))
                    continue;  // skip if this index has been found before

                if (inputArray[n] > testval) {
                    testval = inputArray[n];
                    idx = n;
                }
            }
            sortedIndex.add(idx);
            indexForSearch.add(idx);
        }

        return sortedIndex.toArray(new Integer[N]);
    }


    //
    // fast but crude linear moving average
    //
    public static void fastLinearMovingAverage(Integer[] inputArray, Integer[] outputArray, int smoothingWidth) {

        if (inputArray.length != outputArray.length) return;

        int len = inputArray.length;
        int half = smoothingWidth / 2;  // rounds down
        int width = 2 * half + 1;

        // Create a DescriptiveStats instance and set the window size to +/- (smoothingWidth/2)
        DescriptiveStatistics smoother = new DescriptiveStatistics();
        smoother.setWindowSize(width);

        // Fill first section
        int value, idx = -half;
        for (int i = 0; i < width; i++) {
            value = circularAccess(inputArray, idx);
            smoother.addValue(value);
            idx++;
        }
        outputArray[0] = (int) Math.round(smoother.getMean());

        // Do the rest
        for (int i = 1; i < len; i++) {
            value = circularAccess(inputArray, idx);
            smoother.addValue(value);
            idx++;
            outputArray[i] = (int) Math.round(smoother.getMean());
        }

    }

    private static int circularAccess(Integer[] inputArray, int index) {

        int len = inputArray.length;

        if (index >= len) {
            index = index % len;
        } else if (index < 0) {
            index = (index + len) % len;
        }

        return inputArray[index];

    }

    // simple recursive single-pole highpass filter
    public static Integer[] singlePoleHighpassFilter(Integer[] x, float t) {

        Integer[] y = new Integer[x.length];
        float a0 = (1 + t) / 2;
        float a1 = -(1 + t) / 2;
        float b1 = t;

        for (int i = 0; i < x.length; i++) {
            if (i < 1) {
                y[i] = (int) (a0 * x[i]);
            } else {
                y[i] = (int) (a0 * x[i] + a1 * x[i - 1] + b1 * y[i - 1]);
            }
            if (y[i] < 0) y[i] = 0;
        }

        return y;
    }

    // simple recursive single-pole lowpass filter
    public static Integer[] singlePoleLowpassFilter(Integer[] x, float t) {

        Integer[] y = new Integer[x.length];
        float a0 = 1 - t;
        float b1 = t;

        for (int i = 0; i < x.length; i++) {
            if (i < 1) {
                y[i] = (int) (a0 * x[i]);
            } else {
                y[i] = (int) (a0 * x[i] + b1 * y[i - 1]);
            }
            if (y[i] < 0) y[i] = 0;
        }

        return y;
    }


}




