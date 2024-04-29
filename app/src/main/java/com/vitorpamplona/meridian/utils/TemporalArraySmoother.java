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

import java.util.ArrayList;
import java.util.List;

public class TemporalArraySmoother {

    private List<Integer[]> arrayRingbuffer = new ArrayList<Integer[]>();
    private int buffersize;

    public TemporalArraySmoother(int buffersize) {
        this.buffersize = buffersize;
    }

    private void push(Integer[] inputArray) {
        arrayRingbuffer.add(0, inputArray);
        if (arrayRingbuffer.size() > buffersize) {
            arrayRingbuffer.remove(arrayRingbuffer.size() - 1);
        }
    }

    public int sumItem(int n) {
        int tval = 0;
        for (Integer[] temp : arrayRingbuffer) {
            tval += temp[n];
        }
        return tval;
    }

    public int averageItem(int n) {
        return Math.round(sumItem(n) / (float) arrayRingbuffer.size());
    }

    public Integer[] addAndProcess(Integer[] inputArray) {
        push(inputArray);

        Integer[] outputArray = new Integer[inputArray.length];
        for (int n = 0; n < inputArray.length; n++) {
            outputArray[n] = averageItem(n);
        }
        return outputArray;
    }

    public int arraysInBuffer() {
        return arrayRingbuffer.size();
    }

}
