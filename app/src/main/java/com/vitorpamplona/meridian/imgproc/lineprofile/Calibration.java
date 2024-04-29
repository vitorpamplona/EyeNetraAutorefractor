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

import com.vitorpamplona.core.testdevice.Point2D;


public class Calibration {
    public Point2D p1;
    public Point2D p2;
    public Point2D p3;

    private double lengthPX;
    private double lengthMM;
    private Point2D center;

    private int FUDGEFACTOR = 15;  // distance difference (in px) between calibration dots
    private int MIN_PIXEL_DISTANCE = 15;  // minimum calibration dot distance (in px)  ---- WARNING, THIS IS DEVICE AND RESOLUTION DEPENDENT

    public Calibration(Point2D p1, Point2D p2, Point2D p3, double lengthMM) {
        super();
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.lengthMM = lengthMM;

        double lenP1P2 = distance(p1, p2);
        double lenP2P3 = distance(p2, p3);
        double lenP1P3 = distance(p1, p3);

        if (lenP1P2 <= MIN_PIXEL_DISTANCE || lenP1P3 <= MIN_PIXEL_DISTANCE || lenP2P3 <= MIN_PIXEL_DISTANCE) {
            center = null;
            lengthPX = Double.NaN;
            return;
        }

        if (Math.abs(lenP1P2 - lenP1P3) < FUDGEFACTOR) {
            lengthPX = (lenP1P2 + lenP1P3) / 2;
            center = p1;
        } else if (Math.abs(lenP1P2 - lenP2P3) < FUDGEFACTOR) {
            lengthPX = (lenP1P2 + lenP2P3) / 2;
            center = p2;
        } else if (Math.abs(lenP1P3 - lenP2P3) < FUDGEFACTOR) {
            lengthPX = (lenP1P3 + lenP2P3) / 2;
            center = p3;
        } else if (lenP1P2 != Double.NaN) {
            lengthPX = lenP1P2;
            center = p2;
        } else {
            lengthPX = Double.NaN;
            center = null;

        }


    }

    private double distance(Point2D p1, Point2D p2) {
        if (p1 == null || p2 == null) {
            return Double.NaN;
        } else {
            return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
        }
    }

    public double lengthPX() {
        return lengthPX;
    }

    public double pxPerMM() {
        return lengthPX / lengthMM;
    }

    public Point2D center() {
        return center;
    }

    public String toString() {
        return p1 + " " + p2 + " " + p3;
    }
}
