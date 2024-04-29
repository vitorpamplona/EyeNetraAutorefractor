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
package com.vitorpamplona.core.testdevice.ui.pdmeasurement;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Pair;
import com.vitorpamplona.core.testdevice.ui.CanvasPainter;

public class PdTestPainter implements CanvasPainter {

    protected static float PD_TEST_CIRCLE_RADIUS = 270;
    protected static float PD_TEST_RING_RADIUS = 180;
    protected static float PD_TEST_RING_THICKNESS = 90;

    protected RectF mRectF = new RectF();
    protected Paint mPaint = new Paint();

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX, float testY, float idleY, float alpha) {
        drawPdTest(canvas, testingRightEye, middleX, testY, idleY, Color.YELLOW);
        return true;
    }

    /**
     * Draws the light for image processing close to the camera position. The light from the screen retroreflects back to the camera
     * allowing for an accurate image processing.
     *
     * @param canvas
     * @param mPaint
     * @param backgroundSize
     */
    public void drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(Canvas canvas, Paint mPaint, float backgroundSize) {
        mPaint.setColor(Color.rgb(60, 60, 255));
        mPaint.setStyle(Style.FILL);
        canvas.drawArc(new RectF(backgroundSize - 600, -backgroundSize - 350, backgroundSize + 600, -backgroundSize + 350), 90, 90, true, mPaint);
        canvas.drawArc(new RectF(backgroundSize - 350, -backgroundSize - 600, backgroundSize + 350, -backgroundSize + 600), 90, 90, true, mPaint);
    }

    protected void drawPdTest(Canvas canvas, boolean isRight, float centerX, float leftY, float rightY, int ringColor) {
        canvas.drawColor(Color.rgb(0, 0, 100));

        float backgroundSize = canvas.getWidth() / 2.0f;

        canvas.save();
        canvas.translate(centerX, leftY);

        drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(canvas, mPaint, backgroundSize);

        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.BLACK);

        canvas.drawCircle(0, 0, PD_TEST_CIRCLE_RADIUS, mPaint);
        canvas.restore();

        canvas.save();
        canvas.translate(centerX, rightY);

        drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(canvas, mPaint, backgroundSize);

        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.BLACK);

        canvas.drawCircle(0, 0, PD_TEST_CIRCLE_RADIUS, mPaint);
        canvas.restore();

        canvas.save();
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(PD_TEST_RING_THICKNESS);
        mPaint.setColor(ringColor);
        if (isRight) {
            canvas.translate(centerX, rightY);
        } else {
            canvas.translate(centerX, leftY);
        }
        canvas.drawCircle(0, 0, PD_TEST_RING_RADIUS, mPaint);
        canvas.restore();
    }

}
