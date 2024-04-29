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

/**
 * Describes a set of pixels of interest in a given image. 
 * Each track represents a different point in the image to capture the same information.
 */
public class RegionOfInterest {
    public static final int X = 0;
    public static final int Y = 1;

    public double numberOfPoints, numberOfTracks;
    private int[][][] coordinates; // [0:x,1:y][track][points]

    public RegionOfInterest(int numberOfTracks, int numberOfPoints) {
        this.numberOfTracks = numberOfTracks;
        this.numberOfPoints = numberOfPoints;
        coordinates = new int[2][numberOfTracks][numberOfPoints];  // [0:x,1:y][track][points]
    }

    public int posX(int track, int point) {
        return coordinates[X][track][point];
    }

    public int posY(int track, int point) {
        return coordinates[Y][track][point];
    }

    public void set(int track, int point, int valueX, int valueY) {
        coordinates[X][track][point] = valueX;
        coordinates[Y][track][point] = valueY;
    }

    /**
     * Loops through the buffer, calling the function Do for every point in the buffer.
     * @param command executes for each point in the array.
     */
    public void each(int track, Do command) {
        for (int point = 0; point < numberOfPoints; point++) {
            command.process(posX(track, point), posY(track, point));
        }
    }

    /**
     * Loops through the buffer, calling the function Do for every point in the buffer.
     * @param command executes for each point in the array.
     */
    public void each(Do command) {
        for (int track = 0; track < numberOfTracks; track++) { // inside track
            each(track, command);
        }
    }

    /**
     * Loops through the buffer, setting each point on the given track.
     * @param command computes the point in X and Y.
     */
    public void each(int track, Set command) {
        for (int point = 0; point < numberOfPoints; point++) {
            set(track, point, command.runX(track, point), command.runY(track, point));
        }
    }

    /**
     * Loops through the buffer, setting each point on each track.
     * @param command computes the point in X and Y.
     */
    public void each(Set command) {
        for (int track = 0; track < numberOfTracks; track++) { // inside track
            each(track, command);
        }
    }

    /**
     * Use this interface to read values in the lineCoordinates
     */
    public interface Do {
        void process(int valueX, int valueY);
    }

    /**
     * Use this interface in the method @each to set values in the lineCoordinates
     */
    public interface Set {
        // return the value to be set in track and point
        int runX(int track, int point);

        int runY(int track, int point);
    }
}