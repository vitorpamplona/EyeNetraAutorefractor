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

import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class SignalNormalizer {

    private int target;
    private Plane plane;
    private Vector3D direction, normal;
    private double dot, originOffset;

    public SignalNormalizer(int targetSignalLevel) {
        target = targetSignalLevel;  // normalize to this level
        direction = new Vector3D(0, 0, 1.0); // unit normal pointing in z direction
    }

    // define the illumination plane with three points in the image and
    // pre-calculate constants defined by plane.
    public void define(Vector3D point1, Vector3D point2, Vector3D point3) {
        plane = new Plane(point1, point2, point3);  // define the illumination plane
        normal = plane.getNormal(); // get the unit normal describing the plane
        dot = normal.dotProduct(direction);  // project the plane normal onto the pointing direction
        originOffset = plane.getOffset(Vector3D.ZERO);  // offset of plane to origin
    }

    // calculates the compensation factor to apply for a given pixel based on
    // the illumination plane and a target signal level
    public double factor(int x, int y) {
        Vector3D point = new Vector3D(x, y, 0);  // point at known x and y..  we want to find z on plane
        double k = -(originOffset + normal.dotProduct(point)) / dot;  // crazy shit
        Vector3D arrow = new Vector3D(1.0, point, k, direction);  // define vector starting at (x,y,0) and ending at (x,y,z)

        return target / arrow.getZ();
    }

}
