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

import android.graphics.Point;
import android.graphics.Rect;

/**
 Algorithm stolen and adapted from ImageJ open source library... okay thanks.
 **/

public class CircularHoughTransform {

    public int radiusMin;  // Find circles with radius grater or equal radiusMin
    public int radiusMax;  // Find circles with radius less or equal radiusMax
    public int radiusInc;  // Increment used to go from radiusMin to radiusMax
    public int maxCircles; // Numbers of circles to be found
    public int threshold = -1; // An alternative to maxCircles. All circles with
    // a value in the hough space greater then threshold are marked. Higher thresholds
    // results in fewer circles.
    byte imageValues[]; // Raw image
    byte houghImage[];  // hough image
    double houghValues[][][]; // Hough Space Values
    public int width; // Hough Space width (depends on image width)
    public int height;  // Hough Space heigh (depends on image height)
    public int depth;  // Hough Space depth (depends on radius interval)
    public int offset; // Image Width
    public int offx;   // ROI x offset
    public int offy;   // ROI y offset
    Point centerPoint[]; // Center Points of the Circles Found.
    Integer Radius[];
    private int vectorMaxSize = 500;
    boolean useThreshold = false;
    int lut[][][]; // LookUp Table for rsin e rcos values
    private int imageWidth;
    private int imageHeight;

    public CircularHoughTransform(int radiusMin, int radiusMax, int radiusInc, int numCircles, int imageWidth, int imageHeight) {

        this.radiusMin = radiusMin;
        this.radiusMax = radiusMax;
        this.radiusInc = radiusInc;
        this.maxCircles = numCircles;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.depth = ((radiusMax - radiusMin) / radiusInc) + 1;

    }


    public void run(byte[] data, Rect ROI) {

        imageValues = data;
        Rect r = ROI;

        offx = r.left;  // double check this
        offy = r.top;
        width = r.width();
        height = r.height();
        offset = imageWidth;


        houghTransform();

        // Create image View for Hough Transform.
        houghImage = new byte[data.length];
        createHoughPixels(houghImage);

        getCentersAndBestRadii(maxCircles);

    }


    /** The parametric equation for a circle centered at (a,b) with
     radius r is:

     a = x - r*cos(theta)
     b = y - r*sin(theta)

     In order to speed calculations, we first construct a lookup
     table (lut) containing the rcos(theta) and rsin(theta) values, for
     theta varying from 0 to 2*PI with increments equal to
     1/8*r. As of now, a fixed increment is being used for all
     different radius (1/8*radiusMin). This should be corrected in
     the future.

     Return value = Number of angles for each radius

     */
    private int buildLookUpTable() {

        int i = 0;
        int incDen = Math.round(8F * radiusMin);  // increment denominator

        lut = new int[2][incDen][depth];

        for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
            i = 0;
            for (int incNun = 0; incNun < incDen; incNun++) {
                double angle = (2 * Math.PI * (double) incNun) / (double) incDen;
                int indexR = (radius - radiusMin) / radiusInc;
                int rcos = (int) Math.round((double) radius * Math.cos(angle));
                int rsin = (int) Math.round((double) radius * Math.sin(angle));
                if ((i == 0) | (rcos != lut[0][i][indexR]) & (rsin != lut[1][i][indexR])) {
                    lut[0][i][indexR] = rcos;
                    lut[1][i][indexR] = rsin;
                    i++;
                }
            }
        }

        return i;
    }

    private void houghTransform() {

        int lutSize = buildLookUpTable();

        houghValues = new double[width][height][depth];

        int k = width - 1;
        int l = height - 1;

        for (int y = 1; y < l; y++) {
            for (int x = 1; x < k; x++) {
                for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
                    if (imageValues[(x + offx) + (y + offy) * offset] != 0) {// Edge pixel found
                        int indexR = (radius - radiusMin) / radiusInc;
                        for (int i = 0; i < lutSize; i++) {

                            int a = x + lut[1][i][indexR];
                            int b = y + lut[0][i][indexR];
                            if ((b >= 0) & (b < height) & (a >= 0) & (a < width)) {
                                houghValues[a][b][indexR] += 1;
                            }
                        }

                    }
                }
            }

        }

    }


    // Convert Values in Hough Space to an 8-Bit Image Space.
    private void createHoughPixels(byte houghPixels[]) {
        double d = -1D;
        for (int j = 0; j < height; j++) {
            for (int k = 0; k < width; k++)
                if (houghValues[k][j][0] > d) {
                    d = houghValues[k][j][0];
                }

        }

        for (int l = 0; l < height; l++) {
            for (int i = 0; i < width; i++) {
                houghPixels[i + l * width] = (byte) Math.round((houghValues[i][l][0] * 255D) / d);
            }

        }
    }


    public Point nthMaxCenter(int i) {
        return centerPoint[i];
    }


    /** Search for a fixed number of circles.

     @param maxCircles The number of circles that should be found.
     */
    private void getCentersAndBestRadii(int maxCircles) {

        centerPoint = new Point[maxCircles];
        Radius = new Integer[maxCircles];
        int xMax = 0;
        int yMax = 0;
        int rMax = 0;

        for (int c = 0; c < maxCircles; c++) {
            double counterMax = -1;
            int rBest = 0;
            for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {

                int indexR = (radius - radiusMin) / radiusInc;
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        if (houghValues[x][y][indexR] > counterMax) {
                            counterMax = houghValues[x][y][indexR];
                            xMax = x;
                            yMax = y;
                            rMax = radius;
                            rBest = radius;
                        }
                    }

                }
            }

            centerPoint[c] = new Point(xMax, yMax);
            Radius[c] = rBest;

            clearNeighbours(xMax, yMax, rMax);
        }
    }


    /** Search circles having values in the hough space higher than a threshold

     @param threshold The threshold used to select the higher point of Hough Space
     */
    private void getCenterPointsByThreshold(int threshold) {

        centerPoint = new Point[vectorMaxSize];
        int xMax = 0;
        int yMax = 0;
        int countCircles = 0;

        for (int radius = radiusMin; radius <= radiusMax; radius = radius + radiusInc) {
            int indexR = (radius - radiusMin) / radiusInc;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {


                    if (houghValues[x][y][indexR] > threshold) {


                        if (countCircles < vectorMaxSize) {


                            centerPoint[countCircles] = new Point(x, y);

                            clearNeighbours(xMax, yMax, radius);

                            ++countCircles;
                        } else
                            break;
                    }
                }
            }
        }

        maxCircles = countCircles;
    }

    /** Clear, from the Hough Space, all the counter that are near (radius/2) a previously found circle C.

     @param x The x coordinate of the circle C found.
     @param x The y coordinate of the circle C found.
     @param x The radius of the circle C found.
     */
    private void clearNeighbours(int x, int y, int radius) {


        // The following code just clean the points around the center of the circle found.


        double halfRadius = radius / 2.0F;
        double halfSquared = halfRadius * halfRadius;


        int y1 = (int) Math.floor((double) y - halfRadius);
        int y2 = (int) Math.ceil((double) y + halfRadius) + 1;
        int x1 = (int) Math.floor((double) x - halfRadius);
        int x2 = (int) Math.ceil((double) x + halfRadius) + 1;


        if (y1 < 0)
            y1 = 0;
        if (y2 > height)
            y2 = height;
        if (x1 < 0)
            x1 = 0;
        if (x2 > width)
            x2 = width;


        for (int r = radiusMin; r <= radiusMax; r = r + radiusInc) {
            int indexR = (r - radiusMin) / radiusInc;
            for (int i = y1; i < y2; i++) {
                for (int j = x1; j < x2; j++) {
                    if (Math.pow(j - x, 2D) + Math.pow(i - y, 2D) < halfSquared) {
                        houghValues[j][i][indexR] = 0.0D;
                    }
                }
            }
        }

    }


    public byte[] getHoughImage() {
        return houghImage;
    }


    public Point[] getCenterPoint() {
        return centerPoint;
    }


    public Integer[] getRadius() {
        return Radius;
    }

}


