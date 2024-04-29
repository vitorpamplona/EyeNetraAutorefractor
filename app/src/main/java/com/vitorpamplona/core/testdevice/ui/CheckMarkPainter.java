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
package com.vitorpamplona.core.testdevice.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.Pair;

public class CheckMarkPainter implements CanvasPainter {

    protected static float CHECK_MARK_LINE_WIDTH = 100;
    protected static float CHECK_MARK_LINE_LENGTH_SHORT = 240;
    protected static float CHECK_MARK_LINE_LENGTH_LONG = 420;
    protected static float CHECK_MARK_LINE_ANGLE_SHORT = -135 - 90;
    protected static float CHECK_MARK_LINE_ANGLE_LONG = -45 - 90;
    protected static float CHECK_MARK_LINE_ROUNDING = 25;

    protected RectF mRectF = new RectF();
    protected Paint mPaint = new Paint();

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX, float testY, float idleY, float alpha) {
        drawCheckMark(canvas, middleX, testY, idleY);
        return true;
    }

    public void drawCheckMark(Canvas canvas, float centerX, float leftY, float rightY) {
        mRectF.top = 0;
        mRectF.left = 0;
        mRectF.right = CHECK_MARK_LINE_WIDTH;

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.FILL);

        canvas.save();
        canvas.translate(leftY, centerX);
        canvas.save();

        canvas.rotate(CHECK_MARK_LINE_ANGLE_SHORT);
        canvas.translate(-CHECK_MARK_LINE_WIDTH / 2, -CHECK_MARK_LINE_WIDTH / 2);
        mRectF.bottom = CHECK_MARK_LINE_LENGTH_SHORT;
        canvas.drawRoundRect(mRectF, CHECK_MARK_LINE_ROUNDING, CHECK_MARK_LINE_ROUNDING, mPaint);
        canvas.restore();

        canvas.rotate(CHECK_MARK_LINE_ANGLE_LONG);
        canvas.translate(-CHECK_MARK_LINE_WIDTH / 2, -CHECK_MARK_LINE_WIDTH / 2);
        mRectF.bottom = CHECK_MARK_LINE_LENGTH_LONG;
        canvas.drawRoundRect(mRectF, CHECK_MARK_LINE_ROUNDING, CHECK_MARK_LINE_ROUNDING, mPaint);
        canvas.restore();


        canvas.save();
        canvas.translate(rightY, centerX);
        canvas.save();

        canvas.rotate(CHECK_MARK_LINE_ANGLE_SHORT);
        canvas.translate(-CHECK_MARK_LINE_WIDTH / 2, -CHECK_MARK_LINE_WIDTH / 2);
        mRectF.bottom = CHECK_MARK_LINE_LENGTH_SHORT;
        canvas.drawRoundRect(mRectF, CHECK_MARK_LINE_ROUNDING, CHECK_MARK_LINE_ROUNDING, mPaint);
        canvas.restore();

        canvas.rotate(CHECK_MARK_LINE_ANGLE_LONG);
        canvas.translate(-CHECK_MARK_LINE_WIDTH / 2, -CHECK_MARK_LINE_WIDTH / 2);
        mRectF.bottom = CHECK_MARK_LINE_LENGTH_LONG;
        canvas.drawRoundRect(mRectF, CHECK_MARK_LINE_ROUNDING, CHECK_MARK_LINE_ROUNDING, mPaint);
        canvas.restore();

    }

}
