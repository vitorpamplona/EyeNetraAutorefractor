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
package com.vitorpamplona.core.testdevice.ui.lines;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Pair;
import com.vitorpamplona.core.testdevice.ui.AnimatedStringPainter;

public class GunLinesPainterAccReady extends LinesPainter {

    protected Paint mPaint = new Paint();

    protected static final float LINE_X_HALF_WIDTH = 7f; // mm
    protected static final float LINE_X_OVERLAP = 2.0f;     // mm

    AnimatedStringPainter painter = new AnimatedStringPainter("5");
    long resetTimeInMilis = 0;

    public GunLinesPainterAccReady(float displayDPI) {
        super(displayDPI);
    }

    public void startNumbers() {
        resetTimeInMilis = System.currentTimeMillis();
        painter = new AnimatedStringPainter(Integer.toString(6 + (int) (Math.random() * 4)));
    }

    /**
     * Drawing the gun analogy where red (R) and green (G) lines are this: 
     *
     *       RRRRRRRR
     *            GGGGGGGG
     *       RRRRRRRR
     *
     * @param canvas
     * @param device
     * @param testingRightEye
     * @param workingPair
     * @param middleX
     * @param testY
     * @param idleY
     */

    private static final int CIRCLE_STEP = 20;

    public boolean paint(Canvas canvas, Device device, boolean testingRightEye,
                         Pair workingPair, float middleX, float testY, float idleY, float alpha) {

        if (System.currentTimeMillis() - resetTimeInMilis < 2000) {
            painter.paint(canvas, device, testingRightEye, workingPair, middleX, testY, idleY, alpha);
            return true;
        } else {

            canvas.save();

            translateToTheTestPosition(canvas, device, testingRightEye, middleX, testY, idleY);

            canvas.rotate(180);

            mPaint.setColor(Color.argb((int) (alpha * 255), 0, 255, 0));

            canvas.save();

            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(1.5f);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));

            canvas.translate(workingPair.p1.x, workingPair.p1.y);
            canvas.rotate(90 - (180 - workingPair.angle));

            canvas.drawLine(-toPX(LINE_X_HALF_WIDTH), 0.0f, toPX(LINE_X_OVERLAP), 0.0f, mPaint);

            for (int i = CIRCLE_STEP; i < toPX(LINE_X_HALF_WIDTH) - CIRCLE_STEP; i += CIRCLE_STEP) {
                canvas.drawLine(-getXinCircle(toPX(LINE_X_HALF_WIDTH), i), i, -toPX(LINE_X_OVERLAP) * 1.5f, i, mPaint);
                canvas.drawLine(-getXinCircle(toPX(LINE_X_HALF_WIDTH), i), -i, -toPX(LINE_X_OVERLAP) * 1.5f, -i, mPaint);
            }

            mPaint.setStrokeWidth(4);
            canvas.drawArc(new RectF(-toPX(LINE_X_HALF_WIDTH), -toPX(LINE_X_HALF_WIDTH), toPX(LINE_X_HALF_WIDTH), toPX(LINE_X_HALF_WIDTH)), 120, 120, false, mPaint);


            canvas.restore();

            mPaint.setColor(Color.argb((int) (alpha * 255), 255, 0, 0));

            canvas.save();

            mPaint.setAntiAlias(true);
            mPaint.setStrokeWidth(3.5f);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));

            canvas.translate(workingPair.p2.x, workingPair.p2.y);
            canvas.rotate(90 - (180 - workingPair.angle));

            // Samsung and Sony Phones adds a pixel for Vertical and Horizontal meridians.
            canvas.drawLine(-toPX(LINE_X_OVERLAP), 0, toPX(LINE_X_HALF_WIDTH), 0, mPaint);

            for (int i = CIRCLE_STEP; i < toPX(LINE_X_HALF_WIDTH) - CIRCLE_STEP; i += CIRCLE_STEP) {
                canvas.drawLine(toPX(LINE_X_OVERLAP) * 1.5f, i, getXinCircle(toPX(LINE_X_HALF_WIDTH), i), i, mPaint);
                canvas.drawLine(toPX(LINE_X_OVERLAP) * 1.5f, -i, getXinCircle(toPX(LINE_X_HALF_WIDTH), i), -i, mPaint);
            }

            mPaint.setStrokeWidth(4);
            canvas.drawArc(new RectF(-toPX(LINE_X_HALF_WIDTH), -toPX(LINE_X_HALF_WIDTH), toPX(LINE_X_HALF_WIDTH), toPX(LINE_X_HALF_WIDTH)), -60, 120, false, mPaint);

            mPaint.setXfermode(null);

            canvas.restore();
            canvas.restore();
        }

        return true;
    }

    public float getXinCircle(double r, double y) {
        return (float) Math.sqrt(Math.pow(r, 2) - Math.pow(y, 2));
    }
}
