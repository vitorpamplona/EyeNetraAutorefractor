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

import android.util.Log;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.utils.LineProfileUtils;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;

public class SlitAngle {

    private int width;
    private int height;
    private int window;

    ArrayList<Point2D[][]> LUTcollection = new ArrayList<Point2D[][]>(360);

    /**
     *
     */
    public SlitAngle(int width, int height, int processingWindowWidth) {

        this.width = width;
        this.height = height;

        if ((processingWindowWidth & 1) == 1) { // odd
            processingWindowWidth = processingWindowWidth - 1;
        }

        this.window = processingWindowWidth;

        precomputeLUT();
    }

    public void process(byte[] image, Point2D center) {

        Integer[] rotationMap = new Integer[180];

        for (int t = 0; t < 180; t++) {
            rotationMap[t] = (int) getProjectionInX(image, center, t).getStandardDeviation();
        }

        rotationMap = LineProfileUtils.linearMovingAverage(rotationMap, 5);
        int loc = LineProfileUtils.maximaIndex(rotationMap).intValue();
        Float angle = LineProfileUtils.centerOfMass(rotationMap, 0, loc - 5, loc + 5);

        log("ANGLE: " + angle);

    }


    public DescriptiveStatistics getProjectionInX(byte[] image, Point2D center, int theta) {

        DescriptiveStatistics line = new DescriptiveStatistics();

        // define center coordinate
        int x0 = Math.round(center.x);
        int y0 = Math.round(center.y);

        // define search bounds
        int left = x0 - window / 2;
        int right = x0 + window / 2;
        int top = y0 - window / 2;
        int bottom = y0 + window / 2;

        int Ii = 0;
        for (int x = left; x <= right; x++) {

            int tmp = 0;
            int Ij = 0;

            for (int y = top; y <= bottom; y++) {

                // get precomputed coordinates from lookup table
                Point2D Cr = LUTcollection.get(theta)[Ii][Ij];

                // check if rotated index is in window (discards some corner pixels)
                if (Cr.x >= 0 && Cr.x <= window && Cr.y >= 0 && Cr.y <= window) {

                    // get transformed image coordinates
                    int xr = (int) (x + Cr.x - Ii);
                    int yr = (int) (y + Cr.y - Ij);

                    // check if coordinates are in image (in case window is close to image border)
                    if (xr >= 0 && xr < width && yr >= 0 && yr < height) {

                        // finally, get that sexy pixel value
                        tmp = tmp + (0xff & (int) image[(yr * width) + xr]);

                    }

                }

                Ij++;

            }

            line.addValue(tmp);
            Ii++;

        }
        return line;
    }


    private void precomputeLUT() {
        // compute lookup table for each rotation angle
        for (int i = 0; i < 360; i++) {
            LUTcollection.add(transformCoords(i));
        }
    }

    private Point2D[][] transformCoords(int theta) {
        Point2D coordsTransformed;
        Point2D[][] LUT = new Point2D[window + 1][window + 1];

        double thetaRad = Math.toRadians(theta);

        for (int x = 0; x <= window; x++) {
            for (int y = 0; y <= window; y++) {

                // move center of window to origin
                int xi = x - window / 2;
                int yi = y - window / 2;

                // transform coordinates by rotation
                coordsTransformed = rotateAboutOrigin(new Point2D(xi, yi), thetaRad);

                // back to image coordinates
                int xr = Math.round(coordsTransformed.x + window / 2);
                int yr = Math.round(coordsTransformed.y + window / 2);

                // save coordinates in lookup table
                LUT[x][y] = new Point2D(xr, yr);

            }
        }
        return LUT;
    }

    private Point2D rotateAboutOrigin(Point2D in, double thetaRad) {
        return new Point2D(in.x * Math.cos(thetaRad) + in.y * Math.sin(thetaRad),
                in.y * Math.cos(thetaRad) - in.x * Math.sin(thetaRad));
    }

    private void log(String string) {
        System.out.println(string);
        Log.d("LOG", string);
    }

}










