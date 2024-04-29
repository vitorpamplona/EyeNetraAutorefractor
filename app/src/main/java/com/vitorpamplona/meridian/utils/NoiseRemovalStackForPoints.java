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

import com.vitorpamplona.core.testdevice.Point2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Noise filtering by averaging last X results.
 */
public class NoiseRemovalStackForPoints {
    public List<Point2D> values = new ArrayList<Point2D>();
    public static final int DEFAULT_STACK_SIZE = 3;

    private int stackSize;
    private Point2D stableValue;
    private double requiredStdDevToUpdateAverage;

    public NoiseRemovalStackForPoints(double requiredStdDevToUpdateAverage) {
        this.stackSize = DEFAULT_STACK_SIZE;
        this.requiredStdDevToUpdateAverage = requiredStdDevToUpdateAverage;
        this.stableValue = null;
    }

    public NoiseRemovalStackForPoints(double requiredStdDevToUpdateAverage, int stackSize) {
        this.stackSize = stackSize;
        this.requiredStdDevToUpdateAverage = requiredStdDevToUpdateAverage;
        this.stableValue = null;
    }

    public synchronized void add(Point2D value) {
        if (value == null) return;

        values.add(0, value);
        if (values.size() > stackSize) {
            values.remove(values.size() - 1);
        }
    }

    public boolean isReady() {
        return values.size() == stackSize;
    }

    protected Point2D sub(Point2D p1, Point2D p2) {
        return new Point2D(p1.x - p2.x, p1.y - p2.y);
    }

    public double distance(Point2D p1, Point2D p2) {
        Point2D vector = sub(p1, p2);
        return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }

    public synchronized Point2D average() {
        if (values.isEmpty()) return null;

        Point2D avg = new Point2D(0, 0);
        for (Point2D p : values) {
            avg.x += p.x;
            avg.y += p.y;
        }
        avg.x /= values.size();
        avg.y /= values.size();


        float sum = 0;
        for (Point2D p : values) {
            double absError = distance(p, avg);
            absError = absError * absError;
            sum += absError;
        }

        float stdDev = (float) Math.sqrt(sum / values.size());

        if (stdDev > requiredStdDevToUpdateAverage) {
            return stableValue;
        }

        return stableValue = avg;
    }
}
