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

import java.util.Arrays;

/**
 * Histogram normalization for contrast enhancement
 *
 */

public class Histogram {

    private int imageWidth;
    private int imageHeight;
    private int length;


    public Histogram(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.length = imageWidth * imageHeight;
    }


    public byte[] histogramEqualization(byte[] image) {

        byte[] out = new byte[length];
        int p;
        int newP;

        // Get the CDF Lookup table for histogram equalization
        int[] histLUT = cumulativeDistributionFunctionLUT(image);

        for (int i = 0; i < length; i++) {

            // Get pixel
            p = 0xff & (int) image[i];

            // Get equalized pixel intensity
            newP = histLUT[p];

            // Write into image
            out[i] = (byte) newP;

        }

        return out;

    }


    // Get threshold at given intensity percentile
    public int getThresholdAtPercentile(byte[] image, int percentile) {

        // Prepare the CDF Lookup table for finding threshold of given percentile level
        int[] cdf = cumulativeDistributionFunctionLUT(image);

        // Return the threshold at given percentile
        return getThresholdAtPercentile(cdf, percentile);

    }


    // Get threshold at given intensity percentile
    public int getThresholdAtPercentile(int[] cdf, int percentile) {

        int threshold = 255;
        double percentile8Bit = percentile / 100.0 * 255.0;

        // Find threshold
        for (int i = 0; i < cdf.length; i++) {
            if (cdf[i] >= percentile8Bit) {
                threshold = i;
                break;
            }
        }

        // Return the threshold at given percentile
        return threshold;
    }


    // Prepare histogram equalization lookup table
    public int[] cumulativeDistributionFunctionLUT(byte[] image) {

        long sumr = 0;

        // Get the histogram
        int[] histogram = imageHistogram(image);

        // Calculate scale factor
        float scale_factor = (float) (255.0 / length);

        // Fill the CDF lookup table
        int[] cdf = new int[256];
        Arrays.fill(cdf, 0);

        for (int i = 0; i < histogram.length; i++) {
            sumr += histogram[i];
            int valr = (int) (sumr * scale_factor);
            if (valr > 255) {
                cdf[i] = 255;
            } else cdf[i] = valr;
        }

        return cdf;

    }


    // Return the count of each pixel intensity level (256 bins)
    public int[] imageHistogram(byte[] image) {

        int p;
        int[] histogram = new int[256];

        // iterate through all pixels
        for (int i = 0; i < length; i++) {
            p = 0xff & (int) image[i];
            histogram[p]++;
        }

        return histogram;

    }

}
