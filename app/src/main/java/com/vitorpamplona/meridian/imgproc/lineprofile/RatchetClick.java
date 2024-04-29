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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class RatchetClick {

    private DescriptiveStatistics statistics;
    private static List<Double> fifoAngleBuffer = new ArrayList<Double>();
    private int BUFFERLENGTH;
    private int THRESHOLD;
    private boolean toggle = false;
    private boolean clicked = false;
    private boolean released = false;
    private double newAngle = Double.NaN;


    public RatchetClick() {
        this.BUFFERLENGTH = 5;
        this.THRESHOLD = 10;
    }


    public RatchetClick(int steadyLength, int threshold) {
        this.BUFFERLENGTH = steadyLength;
        this.THRESHOLD = threshold;
    }


    public void addAngle(double angle) {

        statistics = updateStatistics(fifoAngleBuffer);

        double std = statistics.getStandardDeviation();
        double mean = statistics.getMean();

        // my special signal to detect directional button change
        double signal = std * (angle - mean);

        // adjust special case where angle jumps from 0 to 360
        if (signal > 10 * THRESHOLD) {
            signal = -10 * THRESHOLD;
        }

        // determine if clicked or released
        clicked = false;
        released = false;
        newAngle = Double.NaN;

        if (signal < -THRESHOLD) {
            if (toggle == false) {
                clicked = true;
            }
            toggle = true;
        } else {
            if (toggle == true) {
                released = true;
                newAngle = angle;
            }
            toggle = false;
        }

        // update the angle buffer
        updateAngleBuffer(fifoAngleBuffer, angle, BUFFERLENGTH);

    }


    private void updateAngleBuffer(List<Double> buffer, double angle, int length) {

        buffer.add(0, angle);
        if (buffer.size() > length) {
            buffer.remove(length);
        }

    }


    private DescriptiveStatistics updateStatistics(List<Double> doubles) {

        DescriptiveStatistics stats = new DescriptiveStatistics();
        Iterator<Double> iterator = doubles.iterator();
        for (int i = 0; i < doubles.size(); i++) {
            stats.addValue(iterator.next().doubleValue());
        }

        return stats;

    }


    public boolean clicked() {
        return clicked == true;
    }


    public boolean released() {
        return released == true;
    }


    public double newAngle() {
        return newAngle;
    }

}
