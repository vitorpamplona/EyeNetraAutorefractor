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
package com.vitorpamplona.core.testdevice;

import com.vitorpamplona.core.utils.AngleDiff;

/**
 * This class holds a pair of points that represent where the lines should be drawn.
 * They move closer together and away form each other as the control requests.
 * Their angle is an internal angle that may not represent the final angle the doctor wants.
 */
public class Pair {
    public Point2D oP1;
    public Point2D oP2;
    public Point2D p1;
    public Point2D p2;
    public float angle;

    private float computeAngle(Point2D reference, Point2D selected) {
        if (Math.abs(selected.x - reference.x) < 0.00001) {
            return 90;
        }
        float angle = (float) (Math.atan((reference.y - selected.y) / (reference.x - selected.x)) * 180 / Math.PI);

        // starting at horizontal
        //angle = angle - 90;
        // removing +180 degrees
        if (angle < 0) angle = angle + 360;
        if (angle > 360) angle = angle - 360;

        return angle;
    }

    public Pair(Pair p) {
        this(p.p1, p.p2, p.oP1, p.oP2);
    }

    public Pair(Point2D _p1, Point2D _p2, float angle) {
        oP1 = new Point2D(_p1.x, _p1.y);
        oP2 = new Point2D(_p2.x, _p2.y);
        p1 = new Point2D(_p1.x, _p1.y);
        p2 = new Point2D(_p2.x, _p2.y);

        this.angle = AngleDiff.angle0to360(angle);
    }

    public Pair(Point2D _p1, Point2D _p2, Point2D _op1, Point2D _op2) {
        oP1 = new Point2D(_op1.x, _op1.y);
        oP2 = new Point2D(_op2.x, _op2.y);
        p1 = new Point2D(_p1.x, _p1.y);
        p2 = new Point2D(_p2.x, _p2.y);

        angle = computeAngle(oP1, oP2);
    }

    public float distance(Point2D p1, Point2D p2) {
        return (float) Math.sqrt(Math.pow(p2.y - p1.y, 2) + Math.pow(p2.x - p1.x, 2));
    }

    public float originalDistance() {
        return distance(oP1, oP2);
    }

    public float changedDistance() {
        return distance(p1, p2);
    }

    public float sizeP1() {
        return p1.length();
    }

    public float sizeP2() {
        return p2.length();
    }

    public float distanceFromOriginP1() {
        return distance(p1, oP1);
    }

    public float distanceFromOriginP2() {
        return distance(p2, oP2);
    }


    public Point2D p1p2NormVector() {
        float vector_x = p1.x - p2.x;
        float vector_y = p1.y - p2.y;
        float maximum = Math.max(Math.abs(vector_x), Math.abs(vector_y));
        vector_x = (vector_x / maximum);
        vector_y = (vector_y / maximum);
        return new Point2D(vector_x, vector_y);
    }

    public Point2D originalp1p2NormVector() {
        float vector_x = oP1.x - oP2.x;
        float vector_y = oP1.y - oP2.y;
        float maximum = Math.max(Math.abs(vector_x), Math.abs(vector_y));
        vector_x = (vector_x / maximum);
        vector_y = (vector_y / maximum);
        return new Point2D(vector_x, vector_y);
    }

    //Step lines further apart
    public void increaseDotPitch() {
        Point2D vector = originalp1p2NormVector();

        // 45 Degrees can use super resolution.
        //if (Math.abs(Math.abs(angle) % 45) < 1 && !(Math.abs(Math.abs(angle) % 90) < 1)) {
        //    vector.x =  vector.x/2;
        //    vector.y =  vector.y/2;
        //}

        if (distanceFromOriginP1() >= distanceFromOriginP2()) {
            p1.x += vector.x; // move away from P2
            p1.y += vector.y; // move away from P2
        } else {
            p2.x -= vector.x; // move away from P1
            p2.y -= vector.y; // move away from P1
        }
    }

    //Step lines closer together
    public void reduceDotPitch() {
        Point2D vector = originalp1p2NormVector();

        // 45 Degrees can use super resolution.
        //if (Math.abs(Math.abs(angle) % 45) < 1 && !(Math.abs(Math.abs(angle) % 90) < 1)) {
        //    vector.x =  vector.x/2f;
        //    vector.y =  vector.y/2f;
        //}

        if (distanceFromOriginP2() > distanceFromOriginP1()) {
            p1.x -= vector.x;
            p1.y -= vector.y;
        } else {
            p2.x += vector.x;
            p2.y += vector.y;
        }
    }


    //Increase Dot Pitch with special optimizations
    public void increaseDotPitchSuper() {
        Point2D vector = originalp1p2NormVector();

        // 45 Degrees can use super resolution.
        if (Math.abs(Math.abs(angle) % 45) < 1
                && !(Math.abs(Math.abs(angle) % 90) < 1)) {
            vector.x = vector.x / 4.0f;
            vector.y = vector.y / 4.0f;
        }

        if (distanceFromOriginP1() >= distanceFromOriginP2()) {
            p1.x += vector.x / 2.0f; // move away from P2
            p1.y += vector.y / 2.0f; // move away from P2
        } else {
            p2.x -= vector.x / 2.0f; // move away from P1
            p2.y -= vector.y / 2.0f; // move away from P1
        }
    }

    //Decrease Dot Pitch with special optimizations
    public void reduceDotPitchSuper() {
        Point2D vector = originalp1p2NormVector();

        // 45 Degrees can use super resolution.
        if (Math.abs(Math.abs(angle) % 45) < 1
                && !(Math.abs(Math.abs(angle) % 90) < 1)) {
            vector.x = vector.x / 4f;
            vector.y = vector.y / 4f;
        }

        if (distanceFromOriginP2() > distanceFromOriginP1()) {
            p1.x -= vector.x / 2f;
            p1.y -= vector.y / 2f;
        } else {
            p2.x += vector.x / 2f;
            p2.y += vector.y / 2f;
        }
    }

    public void reset() {
        p1.x = oP1.x;
        p1.y = oP1.y;
        p2.x = oP2.x;
        p2.y = oP2.y;
    }


}
