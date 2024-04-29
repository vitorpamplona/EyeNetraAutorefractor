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

import com.vitorpamplona.meridian.utils.LocalMinMax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Barcode {

    private int dataBits;
    private int checksumBits;
    private int totalBits;

    public int barcodeLengthPx = 0;
    public Integer[] crossOversPx;
    public Double[] dProfile;

    // start sequence '101' (msb)
    // end sequence '101' (lsb)
    private int borderBits = 3;

    public Barcode(int numDataBits, int numChecksumBits) {
        this.dataBits = numDataBits;
        this.checksumBits = numChecksumBits;
        this.totalBits = dataBits + checksumBits + 2 * borderBits;
    }

    public int decode(Integer[] profile) {

        // normalize input line from 0 to 100
        normalize(profile, 100);

        // do local normalizer based of minima/maxima segments
        dProfile = minMaxNormalizer(profile, 100);
        if (dProfile == null) return -1;

        // unwrap to Integer[] -- wtf
        Integer[] iProfile = new Integer[dProfile.length];
        for (int i = 0; i < dProfile.length; i++) {
            iProfile[i] = (int) Math.round(dProfile[i]);
        }

        // get bumps
        Integer[] bumps = new LocalMinMax(iProfile, 50).getMaxtabIndex();

        // start/end position
        int start = bumps[0];
        int end = bumps[bumps.length - 1];
        int difference = end - start;

        // spacing
        double spacing = difference / (totalBits - 1.0);

        // fill up bitstring
        String bitString = "";

        for (int i = 0; i < totalBits; i++) {
            int index = (int) (start + Math.round(i * spacing));

            if (iProfile[index] < 50) {
                bitString += "0";
            } else {
                bitString += "1";
            }
        }

        // define barcode position/length in pixels
        barcodeLengthPx = difference;
        Integer[] cross = {start, end};
        crossOversPx = cross;


        // check start bits for "101" start sequence
        String startBits = bitString.substring(0, 3);
        if (!(startBits.equals("101"))) return -5;
        // check end bits for "101" end sequence
        String endBits = bitString.substring(bitString.length() - 3, bitString.length());
        if (!(endBits.equals("101"))) return -50;


        // get data bits & checksum bits
        String dataInBits = bitString.substring(3, 3 + dataBits);
        String checksumInBits = bitString.substring(3 + dataBits, 3 + dataBits + checksumBits);

        // convert to decimals
        int value = Integer.parseInt(dataInBits, 2);
        int checksum = Integer.parseInt(checksumInBits, 2);

        // chicka-de check da checksum!
        if ((value % 31) != checksum) {
            return -6;
        } else {
            return value;
        }

    }

    private Integer[] findCrossOverIndexes(Double[] profile, int crossOver) {

        List<Integer> crossOvers = new ArrayList<Integer>();
        boolean level = false; // start with bit state zero, (level=0)

        for (int i = 1; i < profile.length; i++) {
            if (profile[i - 1] < crossOver && profile[i] >= crossOver && level == false) {
                crossOvers.add(i);
                level = true;
            } else if (profile[i - 1] > crossOver && profile[i] <= crossOver && level == true) {
                crossOvers.add(i);
                level = false;
            }
        }

        return crossOvers.toArray(new Integer[crossOvers.size()]);
    }

    private void normalize(Integer[] profile, int ceiling) {
        double min = minima(profile);
        double max = maxima(profile);
        for (int i = 0; i < profile.length; i++) {
            profile[i] = (int) Math.round((profile[i] - min) / (max - min) * ceiling);
        }
    }

    private Double[] minMaxNormalizer(Integer[] profile, int ceil) {

        // first, find local minima and maxima
        LocalMinMax local = new LocalMinMax(profile, 50);
        Integer[] maxIndex = local.getMaxtabIndex();
        Integer[] minIndex = local.getMintabIndex();

        // test
        if (maxIndex == null || maxIndex.length < 3 || minIndex == null || minIndex.length < 3) {
            return null;
        }

        // make sure minima is first and last
        if (maxIndex[0] < minIndex[0]) {
            minIndex = insertToFront(minIndex, 0);
        }

        if (maxIndex[maxIndex.length - 1] > minIndex[minIndex.length - 1]) {
            minIndex = insertToBack(minIndex, profile.length - 1);
        }

        // re-nomalize each min-max-min segment
        int leftIndex, rightIndex, leftValue, rightValue, min, max;
        double val;
        Double[] normProfile = new Double[profile.length];
        Arrays.fill(normProfile, 0.0);
        for (int i = 0; i < maxIndex.length; i++) {

            leftIndex = minIndex[i];
            rightIndex = minIndex[i + 1];
            leftValue = profile[leftIndex];    // TODO: test if minIndex is +1 longer than maxIndex (MUST be!)
            rightValue = profile[rightIndex];  // TODO: test if maxima is in between them (MUST be!)
            max = profile[maxIndex[i]];

            if (leftValue < rightValue) {
                min = rightValue;
            } else {
                min = leftValue;
            }

            for (int j = leftIndex; j < rightIndex; j++) {
                // normalize here
                val = (profile[j] - min) / (max - min + 0.0) * ceil;
                // clip negatives to 0
                val = (val < 0) ? 0 : val;
                // fill up
                normProfile[j] = val;
            }


        }

        return normProfile;
    }

    private Integer[] insertToFront(Integer[] minIndex, int value) {
        Integer[] temp = new Integer[minIndex.length + 1];
        Arrays.fill(temp, value);
        System.arraycopy(minIndex, 0, temp, 1, minIndex.length);
        return temp;
    }

    private Integer[] insertToBack(Integer[] minIndex, int value) {
        Integer[] temp = new Integer[minIndex.length + 1];
        Arrays.fill(temp, value);
        System.arraycopy(minIndex, 0, temp, 0, minIndex.length);
        return temp;
    }

    private int minima(Integer[] data) {
        int tmin = Integer.MAX_VALUE;
        for (int i = 0; i < data.length; i++) {
            tmin = (data[i] < tmin) ? data[i] : tmin;
        }
        return tmin;
    }

    private int maxima(Integer[] data) {
        int tmax = Integer.MIN_VALUE;
        for (int i = 0; i < data.length; i++) {
            tmax = (data[i] > tmax) ? data[i] : tmax;
        }
        return tmax;
    }

    public int getBarcodeLengthPx() {
        return barcodeLengthPx;
    }

    public Integer[] getCrossOvers() {
        return crossOversPx;
    }

    public Double[] getMinMaxProfile() {
        return dProfile;
    }
}

