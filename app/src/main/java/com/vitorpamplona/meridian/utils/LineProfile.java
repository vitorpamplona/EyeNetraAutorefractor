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

import java.util.Arrays;


public class LineProfile {

    //	private double INTENSITY_MULTIPLIER = 30;  // US phone
    private double INTENSITY_MULTIPLIER = 5;  // low cost phone
    public Integer[] profile;
    private int IMAGE_X;
    private int IMAGE_Y;
    private int LENGTH;
    private int profileSum = 0;
    private int minima, maxima, minimaIdx, maximaIdx;
    private int validTracks;
    private YuvFilter mYuvFilter;
    private YuvPixel mPixel;

    public RegionOfInterest lineCoordinates;

    // Constructor for arcs / circles
    // (angle in degrees; 0 degrees is north; start_angle to end_angle is clockwise)
    public LineProfile(Point2D center, double outerRadius, double thickness, int tracks, int start_angle, int end_angle, double numberOfPoints, int imageWidth, int imageHeight, YuvFilter colorFilter) {
        this.IMAGE_X = imageWidth;
        this.IMAGE_Y = imageHeight;
        this.LENGTH = IMAGE_X * IMAGE_Y;
        this.lineCoordinates = new RegionOfInterest(tracks, (int) numberOfPoints);
        this.mYuvFilter = colorFilter;
        this.mPixel = new YuvPixel(IMAGE_X, IMAGE_Y);


        double theta_increment = (end_angle - start_angle) * Math.PI / 180 / (numberOfPoints);  // converted to radians
        double theta = start_angle * Math.PI / 180 - Math.PI / 2;  // offset to start_angle, then increment until end_angle
        // rotate by 90deg to make 0deg=north

        // calculate the radii of each track.
        double[] radius = new double[tracks];
        for (int p = 0; p < tracks; p++) {
            double step = thickness / (tracks - 1);
            radius[p] = outerRadius - p * step;
        }

        for (int n = 0; n < numberOfPoints; n++) {
            for (int p = 0; p < tracks; p++) {
                // find x,y points in circle (nearest pixel)
                lineCoordinates.set(p, n,
                        (int) Math.round(center.x + radius[p] * Math.cos(theta)),
                        (int) Math.round(center.y + radius[p] * Math.sin(theta)));
            }

            theta += theta_increment;
        }

        profile = new Integer[(int) numberOfPoints];
        Arrays.fill(profile, 0);
    }

    // Constructor for lines
    public LineProfile(Point2D point1, Point2D point2, int tracks, int points, int imageWidth, int imageHeight, YuvFilter colorFilter) {
        this.IMAGE_X = imageWidth;
        this.IMAGE_Y = imageHeight;
        this.LENGTH = IMAGE_X * IMAGE_Y;
        this.lineCoordinates = new RegionOfInterest(tracks, points);
        this.mYuvFilter = colorFilter;
        this.mPixel = new YuvPixel(IMAGE_X, IMAGE_Y);

        if (point1.x < point2.x) {  // determines vertical or horizontal lines

            double x_increment = (point2.x - point1.x) / (points);  // should be points-1!
            double y_step = (point2.y - point1.y) / (tracks - 1);
            double point_x = point1.x, point_y = point1.y;

            for (int n = 0; n < points; n++) {

                int yOffset = 0;

                for (int p = 0; p < tracks; p++) {
                    // find x,y points in line (nearest pixel)
                    lineCoordinates.set(p, n,
                            (int) Math.round(point_x),
                            (int) Math.round(point_y + yOffset)); //d
                    yOffset += y_step;
                }
                point_x += x_increment;
            }

        } else {

            double y_increment = (point2.y - point1.y) / (points);  // should be points-1!
            double x_step = (point2.x - point1.x) / (tracks - 1);
            double point_x = point1.x, point_y = point1.y;

            for (int n = 0; n < points; n++) {

                int xOffset = 0;

                for (int p = 0; p < tracks; p++) {
                    // find x,y points in line (nearest pixel)
                    lineCoordinates.set(p, n,
                            (int) Math.round(point_x + xOffset),
                            (int) Math.round(point_y));

                    xOffset += x_step;
                }
                point_y += y_increment;
            }

        }

        profile = new Integer[(int) points];
        Arrays.fill(profile, 0);
    }

    /**
     * Get pixel value from image for the given track and points.
     * @param track select track
     * @param point select point
     * @return index in profile array
     */
    public int computeIndex(int track, int point) {
        return (lineCoordinates.posY(track, point) * IMAGE_X) +
                lineCoordinates.posX(track, point);
    }

    /**
     * Tests if given coordinates are out-of-bounds in image
     * @param x coordinate along width of image
     * @param y coordinate along height of image
     * @return true if out-of-bounds
     */
    private boolean isBadCoord(int x, int y) {
        if (x < 0 || y < 0 || x >= IMAGE_X || (y >= IMAGE_Y)) return true;
        else return false;
    }

    /**
     * Computes the average among the tracks for a given point
     * @param yuv input image in YUV420 format
     * @param point given point in profile
     * @return average among track of a given point
     */
    public double averageAmongTracks(byte[] yuv, int point) {

        double temp_pixel = 0;
        int x, y;
        int value = 0;
        validTracks = 0;

        for (int track = 0; track < lineCoordinates.numberOfTracks; track++) {

            x = lineCoordinates.posX(track, point);
            y = lineCoordinates.posY(track, point);

            if (mYuvFilter.isValidFilter()) {
                value = 0xff & mPixel.getFiltered(yuv, x, y, mYuvFilter);
            } else {
                value = 0xff & mPixel.getU(yuv, x, y);
            }

            if (value != 0x00) {
                validTracks++;
                temp_pixel += value;
            }

        }

        if (validTracks == 0) {
            return -1.0; // indicates out-of-bounds
        } else {
            return temp_pixel / validTracks; // return average among tracks
        }

    }

    /**
     * Get the intensity profile using the defined line coordinates from constructor
     * @param yuv input image in YUV420 format
     * @param threshold set everything below this threshold to 0
     * @return line profile
     */
    public Integer[] getProfile(byte[] yuv, int threshold) {

        int pixel;
        profileSum = 0;
        minima = Integer.MAX_VALUE;
        maxima = Integer.MIN_VALUE;

        // extract profile data
        for (int point = 0; point < lineCoordinates.numberOfPoints; point++) {

            pixel = (int) Math.round(averageAmongTracks(yuv, point) * INTENSITY_MULTIPLIER);

            if (pixel >= 0) {  // valid pixel (in-bounds)

                if (pixel < minima) {
                    minima = pixel;
                    minimaIdx = point;
                }
                if (pixel > maxima) {
                    maxima = pixel;
                    maximaIdx = point;
                }
                profileSum += pixel;
                profile[point] = pixel;

            } else {  // invalid pixel (out-of-bounds)

                profile[point] = 0;

            }

        }

        // remove floor bias
        int p;
        for (int i = 0; i < profile.length; i++) {

            p = profile[i];
            p -= minima;

            if (p < threshold) {
                profile[i] = 0;
            } else {
                profile[i] = p;
            }

        }

        return profile;
    }

    public int lineTotalSum() {
        return profileSum;
    }

    public int getMaxima() {
        return maxima;
    }

    public int getMinima() {
        return minima;
    }

    public int getMaximaIdx() {
        return maximaIdx;
    }

    public int getMinimaIdx() {
        return minimaIdx;
    }


}

















