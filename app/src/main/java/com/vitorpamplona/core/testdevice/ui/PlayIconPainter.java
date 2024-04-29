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
import android.graphics.Path;
import android.graphics.RectF;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.DeviceModelSettings;
import com.vitorpamplona.core.testdevice.Pair;
import com.vitorpamplona.core.utils.DeviceModelParser;

import java.util.StringTokenizer;


public class PlayIconPainter implements CanvasPainter {

    protected static float PLAY_ICON_CIRCLE_RADIUS = 300;
    protected static float PLAY_ICON_CIRCLE_STROKE_WIDTH = 100;
    protected static float PLAY_ICON_TRIANGLE_SIDE_LENGTH = 250;
    protected static float PLAY_ICON_TRIANGLE_STROKE_WIDTH = 25;

    protected RectF mRectF = new RectF();
    protected Paint mPaint = new Paint();
    protected Path mPath = new Path();

    protected String msg;

    public PlayIconPainter() {
        float a = PLAY_ICON_TRIANGLE_SIDE_LENGTH;
        float r = (float) (a * Math.sqrt(3) / 6f);
        mPath.moveTo(-r, -a / 2f);
        mPath.lineTo(-r, a / 2f);
        mPath.lineTo(2 * r, 0);
        mPath.lineTo(-r, -a / 2f);
        mPath.close();
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX, float testY, float idleY, float alpha) {

        String nameOfDevice = DeviceModelParser.getDeviceName();
        DeviceModelSettings colors = new DeviceModelSettings(nameOfDevice);
        String colorBackground = colors.getComponentVerificationScreenColor();
        String colorPlayIcon = colors.getPlayIconColor();

        canvas.drawColor(Color.parseColor(colorBackground));
        paintPlayIcon(canvas, colorPlayIcon, testY, middleX);
        paintPlayIcon(canvas, colorPlayIcon, idleY, middleX);
        return true;
    }

    protected void paintPlayIcon(Canvas canvas, String colorPlayIcon, float x, float y) {
        mPaint.setColor(Color.parseColor(colorPlayIcon));

        canvas.save();
        canvas.translate(x, y);
        //canvas.rotate(90);

        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(PLAY_ICON_CIRCLE_STROKE_WIDTH);
        canvas.drawCircle(0, 0, PLAY_ICON_CIRCLE_RADIUS, mPaint);

        mPaint.setStyle(Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawPath(mPath, mPaint);

        if (msg != null) {
            Paint paint = new Paint();
            paint.setColor(Color.rgb(0, 0, 0));
            paint.setTextSize(60);
            StringTokenizer tokenizer = new StringTokenizer(msg, "\n");
            for (int i = -tokenizer.countTokens() / 2; i < tokenizer.countTokens() / 2 + 2; i++) {
                String msg_cut = tokenizer.nextToken();
                float size = paint.measureText(msg_cut, 0, msg_cut.length());
                canvas.drawText(msg_cut, -size / 2, i * 80, paint);
            }
        }
        canvas.restore();
    }

}
