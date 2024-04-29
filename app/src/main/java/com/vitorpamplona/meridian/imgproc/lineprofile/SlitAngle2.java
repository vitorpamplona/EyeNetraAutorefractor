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
import com.vitorpamplona.meridian.utils.YuvPixel;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;

public class SlitAngle2 {

    private int window;
    private int bins = 15;
    private YuvPixel pixel;

    /**
     *
     */
    public SlitAngle2(int width, int height, int processingWindowWidth) {

        this.pixel = new YuvPixel(width, height);

        if ((processingWindowWidth & 1) == 1) { // odd
            processingWindowWidth = processingWindowWidth - 1;
        }

        this.window = processingWindowWidth;

    }


    public double process(byte[] image, Point2D center) {

        ArrayList<Point3D> observations = new ArrayList<Point3D>();
        ArrayList<Point3D> contracted = new ArrayList<Point3D>();
        ArrayList<Point3D> totalObservations = new ArrayList<Point3D>();

        // prepare the data
        collectObservationPoints(image, center, observations);
        contractZaxisIntoBins(bins, observations, contracted);
        createObservationForEachBinCount(contracted, totalObservations);

        // check if enough data
        if (totalObservations.size() < 5) {
            return Double.NaN;
        }

        // orthogonal linear regression through total least squares (TLS)
        double[] P = totalLeastSquares(totalObservations);

        // return found angle
        return Math.toDegrees(Math.atan(P[0]));

    }


    private double[] totalLeastSquares(ArrayList<Point3D> ob) {

        int len = ob.size();
        double[] P = new double[2];

        // sums of y and x
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumSqX = 0;
        double sumSqY = 0;
        double X[] = new double[len];
        double Y[] = new double[len];

        for (int i = 0; i < len; i++) {
            int x = ob.get(i).x;
            int y = ob.get(i).y;

            X[i] = x;
            Y[i] = y;

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumSqX += x * x;
            sumSqY += y * y;
        }
        sumX = sumX / len;
        sumY = sumY / len;

        double B = 0.5 * ((sumSqY - len * (sumY * sumY)) - (sumSqX - len * (sumX * sumX))) / (len * sumX * sumY - sumXY);
        double b1 = -B + Math.sqrt(B * B + 1);
        double b2 = -B - Math.sqrt(B * B + 1);
        double a1 = sumY - b1 * sumX;
        double a2 = sumY - b2 * sumX;
        double corrCoeff = new PearsonsCorrelation().correlation(X, Y);

        if (corrCoeff > 0) {
            P[0] = b1; // slope
            P[1] = a1; // intercept
        } else {
            P[0] = b2; // slope
            P[1] = a2; // intercept
        }

        return P;

    }


    private void createObservationForEachBinCount(ArrayList<Point3D> contracted, ArrayList<Point3D> totalObservations) {

        Point3D ob;

        for (int i = 0; i < contracted.size(); i++) {
            ob = contracted.get(i);

            // copy for each bin count
            for (int j = 0; j < ob.z; j++) {
                totalObservations.add(ob);
            }
        }
    }


    private void collectObservationPoints(byte[] image, Point2D center, ArrayList<Point3D> observations) {

        int mx = (int) center.x;
        int my = (int) center.y;

        for (int x = mx - window / 2; x <= mx + window / 2; x++) {
            for (int y = my - window / 2; y <= my + window / 2; y++) {

                observations.add(new Point3D(x, y, pixel.getY(image, x, y)));

            }
        }
    }


    private void contractZaxisIntoBins(int bins, ArrayList<Point3D> observations, ArrayList<Point3D> contracted) {

        int p;
        Point3D ob;

        MinMax m = minMaxZ(observations);
        double dif = m.max - m.min + 0d;

        for (int i = 0; i < observations.size(); i++) {

            ob = observations.get(i);
            p = (int) Math.round((ob.z - m.min) / dif * (bins - 1));

            if (p > 0) {
                contracted.add(new Point3D(ob.x, ob.y, p));
            }
        }
    }


    private MinMax minMaxZ(ArrayList<Point3D> observations) {

        int tmin = Integer.MAX_VALUE;
        int tmax = Integer.MIN_VALUE;

        int p;

        for (int i = 0; i < observations.size(); i++) {
            p = observations.get(i).z;
            if (p < tmin)
                tmin = p;
            if (p > tmax)
                tmax = p;
        }

        return new MinMax(tmin, tmax);
    }


    public class MinMax {

        public int min;
        public int max;

        public MinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }
    }


    public class Point3D {

        public int x;
        public int y;
        public int z;

        public Point3D(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}










