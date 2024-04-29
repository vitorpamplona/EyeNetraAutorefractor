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

import android.graphics.Rect;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.meridian.imgproc.lineprofile.CalibrationTools.PolarPoints.Polar;
import com.vitorpamplona.meridian.utils.LineProfileUtils;
import com.vitorpamplona.meridian.utils.Logr;
import com.vitorpamplona.meridian.utils.YuvPixel;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CalibrationTools {

    private int width;
    private int height;
    private boolean done = false;
    private Rect imageBox;
    private YuvPixel frame;
    public float radius = 0;
    public List<Point2D> bubbleCenters = new ArrayList<Point2D>();
    public List<Integer> bubbleRadii = new ArrayList<Integer>();
    public DecimalFormat df = new DecimalFormat("0.00");

    public CalibrationTools(int width, int height) {
        this.width = width;
        this.height = height;
        this.frame = new YuvPixel(width, height);
        this.imageBox = new Rect(0, 0, width - 1, height - 1);
    }

    public boolean isDone() {
        return done;
    }

    public void cropByteArray(byte[] data, int crop) {
        for (int n = 0; n < data.length; n++) {
            int p = 0xff & ((int) data[n]);
            p = (p < crop ? 0 : p);
            data[n] = (byte) p;
        }
    }

    public void toBlackAndWhiteByteArray(byte[] data, int level) {
        for (int n = 0; n < data.length; n++) {
            int p = 0xff & ((int) data[n]);
            p = (p < level ? 0 : 255);
            data[n] = (byte) p;
        }
    }

    public Integer[] getLineX(byte[] data, Point2D left, Point2D right) {
        int cnt = 0;
        Integer[] out = new Integer[(int) (right.x - left.x)];
        for (int x = (int) left.x; x < right.x; x++) {
            int p = 0xff & (int) frame.getY(data, x, (int) left.y);
            out[cnt] = p;
            cnt++;
        }
        return out;
    }

    public Integer[] getLineY(byte[] data, Point2D top, Point2D bottom) {
        int cnt = 0;
        Integer[] out = new Integer[(int) (bottom.y - top.y)];
        for (int y = (int) top.y; y < bottom.y; y++) {
            int p = 0xff & (int) frame.getY(data, (int) top.x, y);
            out[cnt] = p;
            cnt++;
        }
        return out;
    }

    public Integer[] projectionXreverse(byte[] data) {  // note: reversed!

        Integer[] projection = new Integer[width];
        int cnt = 0;
        for (int x = width - 1; x >= 0; x--) {
            int tval = 0;
            for (int y = 0; y < height; y++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                tval = p > tval ? p : tval;
            }
            projection[cnt] = tval;
            cnt++;
        }
        return projection;
    }

    public Integer[] normalizedProjectionXreverse(byte[] data, int level) {  // note: reversed!

        Integer[] projection = new Integer[width];
        int cnt = 0;

        // get reversed sum projection
        for (int x = width - 1; x >= 0; x--) {
            int tval = 0;
            for (int y = 0; y < height; y++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                tval = tval + p;
            }
            projection[cnt] = tval;
            cnt++;
        }

        // get sum
        double sum = 0;
        for (int n : projection) {
            sum += n;
        }

        // normalize by total weight and rescale (range is roughly 0-300)
        double t;
        for (int i = 0; i < projection.length; i++) {
            t = projection[i] / sum * 1e5;
            projection[i] = (int) t;
        }

        return projection;
    }

    public Integer[] projectionX(byte[] data, Rect box) {

        Integer[] projection = new Integer[box.right - box.left];
        int cnt = 0;
        for (int x = box.left; x < box.right; x++) {
            int tval = 0;
            for (int y = box.top; y < box.bottom; y++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                tval = p > tval ? p : tval;
            }
            projection[cnt] = tval;
            cnt++;
        }
        return projection;
    }

    public Integer[] projectionY(byte[] data, Rect box) {

        Integer[] projection = new Integer[box.bottom - box.top];
        int cnt = 0;
        for (int y = box.top; y < box.bottom; y++) {
            int tval = 0;
            for (int x = box.left; x < box.right; x++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                tval = p > tval ? p : tval;
            }
            projection[cnt] = tval;
            cnt++;
        }
        return projection;
    }

    public int average(byte[] data, Rect box) {

        int sum = 0, cnt = 0;
        for (int y = box.top; y < box.bottom; y++) {
            for (int x = box.left; x < box.right; x++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                sum += p;
            }
            cnt++;
        }
        return Math.round(sum / cnt);
    }

    public Integer[] sumY(byte[] data, Rect box) {

        Integer[] sum = new Integer[box.bottom - box.top];
        int cnt = 0;
        for (int y = box.top; y < box.bottom; y++) {
            int tval = 0;
            for (int x = 0; x < width; x++) {
                int p = (0xff & (int) frame.getY(data, x, y));
                tval += p;
            }
            sum[cnt] = tval;
            cnt++;
        }
        return sum;
    }

    public void invertByteArray(byte[] data) {
        for (int n = 0; n < data.length; n++) {
            int p = 0xff & ((int) data[n]);
            data[n] = (byte) (255 - p);
        }
    }

    public void normalizeByteArray(byte[] data, int min, int max) {
        for (int n = 0; n < data.length; n++) {
            int p = 0xff & ((int) data[n]);
            p = (p < min ? min : (p > max ? max : p));
            data[n] = (byte) ((p - min) / ((max - min) * 1f) * 255);
        }
    }

    public void normalizeIntegerArray(Integer[] data, int min, int max) {

        int dmin = Integer.MAX_VALUE;
        int dmax = Integer.MIN_VALUE;
        int p;
        float t;

        // find min and max
        for (int n = 0; n < data.length; n++) {
            p = data[n];
            dmin = (p < dmin) ? p : dmin;
            dmax = (p > dmin) ? p : dmax;
        }

        // normalize
        for (int n = 0; n < data.length; n++) {
            p = data[n];
            t = (p - dmin) / (dmax - dmin + 0f);
            data[n] = (int) Math.round(t * (max - min) + min);
        }

    }

    public int minima(byte[] data) {
        return minima(data, imageBox);
    }

    public int minima(byte[] data, Rect box) {
        int tmin = Integer.MAX_VALUE;
        int p;
        for (int y = box.top; y < box.bottom; y++) {
            for (int x = box.left; x < box.right; x++) {
                p = 0xff & (int) frame.getY(data, x, y);
                if (p < tmin) {
                    tmin = p;
                }
            }
        }
        return tmin;
    }

    public int maxima(byte[] data) {
        return maxima(data, imageBox);
    }

    public int maxima(byte[] data, Rect box) {
        int tmax = Integer.MIN_VALUE;
        int p;
        for (int y = box.top; y < box.bottom; y++) {
            for (int x = box.left; x < box.right; x++) {
                p = 0xff & (int) frame.getY(data, x, y);
                if (p > tmax) {
                    tmax = p;
                }
            }
        }
        return tmax;
    }

    public Point2D maximaIndex(byte[] data, Rect box) {
        int tmin = 0, p, xloc = 0, yloc = 0;
        for (int y = box.top; y < box.bottom; y++) {
            for (int x = box.left; x < box.right; x++) {
                p = 0xff & (int) frame.getY(data, x, y);
                if (p > tmin) {
                    tmin = p;
                    xloc = x;
                    yloc = y;
                }
            }
        }
        return tmin > 0 ? new Point2D(xloc, yloc) : null;
    }

    public void subtractArray(byte[] data, byte[] sub) {
        int m, s, d;
        for (int i = 0; i < data.length; i++) {
            m = 0xff & ((int) data[i]);
            s = 0xff & ((int) sub[i]);
            d = m - s;
            d = (d < 0) ? 0 : ((d > 255) ? 255 : d);
            data[i] = (byte) d;
        }
    }


    public Point2D centerOfMass(byte[] data, Rect zone) {
        return centerOfMass(data, zone, 0);
    }

    public Point2D centerOfMass(byte[] data, Rect zone, int floorCrop) {  // get center of mass in original image coordinates (not that of the ROI)
        int p, moment_x = 0, moment_y = 0;
        double totalMass = 0;
        int left, right, top, bottom;

        // safety crop at borders
        left = zone.left < 0 ? 0 : zone.left;
        right = zone.right >= width ? (width - 1) : zone.right;
        top = zone.top < 0 ? 0 : zone.top;
        bottom = zone.bottom >= height ? (height - 1) : zone.bottom;

        // find center of mass
        for (int y = top; y < bottom; y++) {
            for (int x = left; x < right; x++) {
                p = 0xff & (int) frame.getY(data, x, y);
                if (p <= floorCrop) continue;
                moment_x += p * x;
                moment_y += p * y;
                totalMass += p;
            }
        }

        if (totalMass == 0) {
            System.out.println("\nnothing in zone for center of mass");
        }
        return totalMass == 0 ? null : new Point2D(moment_x / totalMass, moment_y / totalMass);
    }

    public Point2D refineCenter(byte[] data, Point2D center, int SEARCH) {
        if (center == null) {
            Logr.d("STATIC", "\ncenter is null");
            return null;
        }
        // check if not too close to border
//		if (center.x <= SEARCH || center.x >= (width-SEARCH) || center.y <= SEARCH || center.y >= (height-SEARCH)) return null;


        int px = (int) center.x;
        int py = (int) center.y;
        return centerOfMass(data, new Rect(px - SEARCH, py - SEARCH, px + SEARCH, py + SEARCH));
    }

    public Point2D ratchetCenter(byte[] data, Point2D FirstGuess, int scanLength) {

        int MIN_RADIUS = 70;
        int MAX_RADIUS = 110;
        int SPACING = 1;
        double minStd = 0;
        Point2D ratchetCenter = null;

        for (int xoff = -scanLength; xoff < scanLength; xoff += 2) {
            for (int yoff = -scanLength; yoff < scanLength; yoff += 2) {
                DescriptiveStatistics statistics = new DescriptiveStatistics();

                for (int id = MIN_RADIUS; id < MAX_RADIUS; id += SPACING) {

                    double sum = 0;

                    for (int scan = 0; scan < 360; scan += 10) {
                        double xinc = Math.cos(Math.toRadians(scan));
                        double yinc = Math.sin(Math.toRadians(scan));

                        int pixelLocationX = (int) Math.round(FirstGuess.x + xoff + id * xinc);
                        int pixelLocationY = (int) Math.round(FirstGuess.y + yoff + id * yinc);
                        int p = 0xff & (int) frame.getY(data, pixelLocationX, pixelLocationY);
                        sum += p;
                    }

                    statistics.addValue(sum);

                }

                double std = statistics.getStandardDeviation();
                double[] radar = statistics.getValues();

                Float com = LineProfileUtils.centerOfMass(radar, 0, radar.length);

                if (std > minStd) {
                    ratchetCenter = new Point2D(FirstGuess.x + xoff, FirstGuess.y + yoff);
                    radius = MIN_RADIUS + com * SPACING;
                    minStd = std;
                }

            }
        }

        return ratchetCenter;
    }


    public List<Point2D> findCircleBoundry(byte[] data, Point2D initialPosition, Rect searchBox) {

        List<Point2D> positions = new ArrayList<Point2D>();
        int thres = 10;
        int step = 20;

        // circular tendrils
        int x, y, p;
        for (int theta = 0; theta < 360; theta += 20) {
            for (int r = 0; r < 300; r++) {
                x = (int) Math.round(r * Math.cos(Math.toRadians(theta)) + initialPosition.x);
                y = (int) Math.round(r * Math.sin(Math.toRadians(theta)) + initialPosition.y);

                if (x < searchBox.left || x >= searchBox.right || y < searchBox.top || y >= searchBox.bottom)
                    break;  // out of bounds

                p = 0xff & (int) frame.getY(data, x, y);

                if (p > thres) {
                    positions.add(new Point2D(x, y));
                    break;  // got what we want
                }
            }
        }

        // how big?
        System.out.println("pixels: " + positions.size());

        return positions;

    }


    public Point2D ratchetCenterBisector(List<Point2D> positions) {

        double xc = 0, yc = 0;
        BisectorsIntersection b = new BisectorsIntersection();
        // Get the combinations of 3 points and estimate blob center
        double cnt = 0;
        for (int idx = 0; idx < positions.size() - 2; idx++)
            for (int jdx = idx + 1; jdx < positions.size() - 1; jdx++)
                for (int kdx = jdx + 1; kdx < positions.size(); kdx++) {
                    Point2D p1 = positions.get(idx);
                    Point2D p2 = positions.get(jdx);
                    Point2D p3 = positions.get(kdx);

                    Point2D cross = BisectorsIntersection.circleCenter(p1, p2, p3);

                    if (Float.isNaN(cross.x) || Float.isNaN(cross.y) ||
                            cross.x >= width || cross.x < 0 ||
                            cross.y >= height || cross.y < 0)
                        break;

                    System.out.println("pos: [" + cross.x + "," + cross.y + "]");

                    xc = xc + cross.x;
                    yc = yc + cross.y;
                    cnt++;
                }

        // Get mean of blob center
        xc = xc / cnt;
        yc = yc / cnt;

        System.out.println("pos: [" + xc + "," + yc + "]" + " cnt: " + cnt);

        return new Point2D(xc, yc);
    }


    public Point2D bubbleFit(byte[] data, Point2D firstGuess, Rect searchBox) {

        Point2D center = new Point2D(0, 0);
        int sumThres = 100;
        int minRadius = 1;
        int maxRadius = 120;
        int incT = 1;
        int incR = 1;

        PolarPoints ring = new PolarPoints();
        int x, y, xOff = 0, yOff = 0;
        double p;

        bubbleCenters.clear();
        bubbleRadii.clear();

        for (int r = minRadius; r < maxRadius; r += incR) {

            ring.clear();

            for (int theta = 0; theta < 360; theta += incT) {
                x = (int) Math.round(r * Math.cos(Math.toRadians(theta)) + firstGuess.x);
                y = (int) Math.round(r * Math.sin(Math.toRadians(theta)) + firstGuess.y);

                if (!(x < searchBox.left || x >= searchBox.right || y < searchBox.top || y >= searchBox.bottom ||
                        x < 0 || x >= width || y < 0 || y >= height)) {

                    p = 0xff & (int) frame.getY(data, x, y);
                    ring.add(new Polar(r, Math.toRadians(theta), p));

                }

            }


            // increase radius until it touches something
            if (ring.getValueSum() < sumThres) {
                continue;
            }

            bubbleCenters.add(firstGuess);
            bubbleRadii.add(r);

            if (ring.arrayBalance() > 0.4) {

                // go opposite direction
                float direction = AngleDiff.angle0to360((float) (Math.toDegrees(ring.arrayAngle()) - 180));
                int rStep = incR + 1; // play with this
                xOff = (int) Math.round(rStep * Math.cos(Math.toRadians(direction)));
                yOff = (int) Math.round(rStep * Math.sin(Math.toRadians(direction)));

                firstGuess = new Point2D(firstGuess.x + xOff, firstGuess.y + yOff); // update position

            } else {

                if (bubbleRadii.size() < 5) return null;  // need at least 5 iterations
                radius = bubbleRadii.get(bubbleRadii.size() - 2); // use previous one
                center = bubbleCenters.get(bubbleCenters.size() - 2);
                ;  // use previous one
                break;  // got what we want, let's get outta here.

            }


        }

        return center;
    }


    public static class PolarPoints {

        public List<Polar> points = new ArrayList<Polar>();
        private double valueSum = 0;

        public void add(Polar point) {
            points.add(point);
            valueSum += point.value;
        }

        public Polar getPoint(int index) {
            return points.get(index);
        }

        public void clear() {
            points.clear();
            valueSum = 0;
        }

        public double getValueSum() {
            return valueSum;
        }

        // cartesian 2d-centroid based array balance
        public double arrayBalance() {

            double step = Math.toRadians(360.0 / points.size());
            double theta = 0;
            double m_x = 0, m_y = 0, tm = 0, p;

            for (int i = 0; i < points.size(); i++) {
                p = points.get(i).value;
                m_x += p * Math.cos(theta);
                m_y += p * Math.sin(theta);
                tm += p;
                theta += step;
            }

            if (tm > 0) {
                double cx = m_x / tm;
                double cy = m_y / tm;
                return Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2));
            } else {
                return Double.NaN;
            }

        }


        public double arrayAngle() {

            double m_x = 0, m_y = 0, tm = 0, p;

            for (int i = 0; i < points.size(); i++) {
                p = points.get(i).value;
                m_x += p * Math.cos(points.get(i).theta);
                m_y += p * Math.sin(points.get(i).theta);
                tm += p;
            }

            if (tm > 0) {
                double cx = m_x / tm;
                double cy = m_y / tm;
                return Math.atan2(cy, cx);
            } else {
                return Double.NaN;
            }

        }


        public static class Polar {
            public double radius;
            public double theta;
            public double value;

            public Polar(double radius, double theta, double value) {
                this.radius = radius;
                this.theta = theta;
                this.value = value;
            }
        }

    }


    // simple recursive single-pole highpass filter
    public static Integer[] singlePoleHighpassFilter(Integer[] x, float t) {

        Integer[] y = new Integer[x.length];
        float a0 = (1 + t) / 2;
        float a1 = -(1 + t) / 2;
        float b1 = t;

        for (int i = 0; i < x.length; i++) {
            if (i < 1) {
                y[i] = (int) (a0 * x[i]);
            } else {
                y[i] = (int) (a0 * x[i] + a1 * x[i - 1] + b1 * y[i - 1]);
            }
            if (y[i] < 0) y[i] = 0;
        }

        return y;
    }


}



