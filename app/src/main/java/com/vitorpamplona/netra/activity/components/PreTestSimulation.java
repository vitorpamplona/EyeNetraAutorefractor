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
package com.vitorpamplona.netra.activity.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.vitorpamplona.core.testdevice.ui.CachedBitmapFactory;
import com.vitorpamplona.netra.R;


public class PreTestSimulation extends View {

    private static final double VIEW_ASPECT_RATIO = 1.46f;

    private static final float LINE_LENGHT_DP = 100;//500;
    private static final float LINE_OVERLAP_DP = 42;//125;

    private static final int GREEN_CIRCLE_MIN_ALPHA = 0;
    private static final int GREEN_CIRCLE_MAX_ALPHA = 255;

    private static final int CIRCLE_STEP_DP = 7;
    public static final int STROKE_WIDTH_DP = 2;
    public static final int CIRCLE_WIDTH_DP = 360;
    public static final int CIRCLE_SHADOW_WIDTH_DP = 180;

    protected Paint mPaint = new Paint();
    protected PorterDuffXfermode mPorterDuffAdd;
    protected PorterDuffXfermode mPorterDuffDstOut;

    protected Bitmap mCrossBackground, mCrossBinocular, mCrossBinocularTransparent;

    private ViewAspectRatioMeasurer varm;

    protected LinearGradient mGreenGradient;

    protected LinearGradient mRedGradient;

    protected RadialGradient mSliderGradient;

    protected float mCenterPd = 31;
    protected float mPd = 31;
    protected float mPower;
    protected float mAngle;

    protected float mCenterX;
    protected float mCenterY;

    protected Canvas tempCanvas;
    protected Bitmap tempBitmap;

    private float LINE_LENGHT;
    private float LINE_OVERLAP;
    private int CIRCLE_STEP;
    private int STROKE_WIDTH;
    private int CIRCLE_WIDTH;
    private int CIRCLE_SHADOW_WIDTH;

    private boolean isDone = false;

    public PreTestSimulation(Context context) {
        super(context);
        init();
    }

    public PreTestSimulation(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreTestSimulation(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public int dpToPixels(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
        // S4 scale == 3.
    }

    public void setMeridian(float angle) {
        mAngle = angle;
    }

    protected void init() {
        isDone = false;

        mCrossBackground = CachedBitmapFactory.getInstance().decodeResource(getResources(), R.drawable.tutorialcross);
        mCrossBinocular = CachedBitmapFactory.getInstance().decodeResource(getResources(), R.drawable.mask_binocular);
        mCrossBinocularTransparent = CachedBitmapFactory.getInstance().decodeResource(getResources(), R.drawable.mask_binocular_transparent);
        mPorterDuffAdd = new PorterDuffXfermode(PorterDuff.Mode.ADD);
        mPorterDuffDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

        mCenterPd = 31;
        mPd = 31;
        mAngle = 0;

        LINE_LENGHT = dpToPixels(this.getContext(), LINE_LENGHT_DP);
        LINE_OVERLAP = dpToPixels(this.getContext(), LINE_OVERLAP_DP);
        CIRCLE_STEP = dpToPixels(this.getContext(), CIRCLE_STEP_DP);
        STROKE_WIDTH = dpToPixels(this.getContext(), STROKE_WIDTH_DP);
        CIRCLE_WIDTH = dpToPixels(this.getContext(), CIRCLE_WIDTH_DP);
        CIRCLE_SHADOW_WIDTH = dpToPixels(this.getContext(), CIRCLE_SHADOW_WIDTH_DP);

        varm = new ViewAspectRatioMeasurer(VIEW_ASPECT_RATIO);

        mGreenGradient = new LinearGradient(0, 0, -LINE_LENGHT, 0,
                new int[]{Color.TRANSPARENT, Color.GREEN, Color.GREEN, Color.TRANSPARENT},
                new float[]{0f, .1f, .8f, 1f}, Shader.TileMode.CLAMP);

        mRedGradient = new LinearGradient(0, 0, LINE_LENGHT, 0,
                new int[]{Color.TRANSPARENT, Color.RED, Color.RED, Color.TRANSPARENT},
                new float[]{0f, .1f, .8f, 1f}, Shader.TileMode.CLAMP);

        mSliderGradient = new RadialGradient(0, 0, CIRCLE_SHADOW_WIDTH,
                new int[]{Color.TRANSPARENT, Color.BLACK},
                new float[]{.4f, .6f}, Shader.TileMode.CLAMP);

        mPaint = new Paint();

    }

    public void setCenterPd(float pd) {
        mCenterPd = pd;
    }

    public void onPdChanged(Float newPd) {
        mPd = newPd;
        invalidate();
    }

    public void onPowerChanged(Float newPower) {
        mPower = newPower;
        invalidate();
    }

    public void onAngleChanged(Float newAngle) {
        mAngle = newAngle;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        varm.measure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(varm.getMeasuredWidth(), varm.getMeasuredHeight());

        tempBitmap = Bitmap.createBitmap(varm.getMeasuredWidth(), varm.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        tempCanvas = new Canvas(tempBitmap);
    }

    protected void onDraw(Canvas canvas) {
        mCenterX = canvas.getWidth() / 2f;
        mCenterY = canvas.getHeight() / 2f;

        drawSimulation(tempCanvas);

        Paint p = new Paint();
        canvas.drawBitmap(tempBitmap, 0, 0, p);
    }

    protected void drawSimulation(Canvas canvas) {
        float adjustedBlueCenterX = mCenterX + (mPd - mCenterPd) * 8;
        float adjustedRedGreenCenterX = mCenterX;// - (mPd - mCenterPd)*8;

        mPaint.setAlpha(255);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Style.FILL);
        canvas.drawPaint(mPaint);

        mPaint.setStyle(Style.STROKE);

        int mGreenCircleAlpha = (int) ((GREEN_CIRCLE_MAX_ALPHA - GREEN_CIRCLE_MIN_ALPHA) + GREEN_CIRCLE_MIN_ALPHA);

        if (!isDone) {
            drawLines(canvas, adjustedRedGreenCenterX, mGreenCircleAlpha);
            drawCircleOccluder(canvas, mCenterX);
            drawCross(canvas, adjustedBlueCenterX, mCenterX * 2, mGreenCircleAlpha);
        } else {
            drawCheckMark(canvas);
        }

        drawBinocular(canvas);

        invalidate();
    }

    private void drawCircleOccluder(Canvas canvas, float adjustedCenterX) {
        canvas.save();

        mPaint.setAlpha(255);
        mPaint.setXfermode(mPorterDuffDstOut);
        mPaint.setMaskFilter(null);
        mPaint.setShader(mSliderGradient);
        mPaint.setStyle(Style.FILL);
        canvas.translate(adjustedCenterX - (mPd - mCenterPd) * 60, mCenterY);
        canvas.drawCircle(0, 0, CIRCLE_WIDTH, mPaint);

        canvas.restore();
    }

    private void drawBinocular(Canvas canvas) {
        canvas.save();

        mPaint.setColor(Color.argb(255, 255, 255, 255));
        mPaint.setStyle(Style.FILL);
        mPaint.setXfermode(null);
        mPaint.setStrokeWidth(1);
        mPaint.setShader(null);
        mPaint.setMaskFilter(null);

        Rect rI = new Rect(0, 0, mCrossBinocular.getWidth(), mCrossBinocular.getHeight());

        Rect rS = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());

        canvas.drawBitmap(mCrossBinocular, rI, rS, mPaint);

        canvas.restore();

		/* TODO: Make the outside of the simulation transparent.
		Paint paint = new Paint();
		paint.setXfermode(new AvoidXfermode(tempBitmap.getPixel(2,2), 0, AvoidXfermode.Mode.TARGET));
		paint.setColor(Color.TRANSPARENT);
		canvas.drawPaint(paint); */
    }

    private void drawCross(Canvas canvas, float adjustedCenterX, float backgroundSize, int mGreenCircleAlpha) {
        canvas.save();

        canvas.translate(adjustedCenterX, mCenterY);
        canvas.rotate(90);

        mPaint.setColor(Color.argb(mGreenCircleAlpha, 0, 0, 255));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(40);
        mPaint.setShader(null);

        Rect rect = new Rect((int) -backgroundSize / 2, (int) -backgroundSize / 2, (int) backgroundSize / 2, (int) backgroundSize / 2);

        mPaint.setMaskFilter(null);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        canvas.rotate(90);
        canvas.drawBitmap(mCrossBackground, null, rect, mPaint);

        canvas.restore();
    }


    public void drawLines(Canvas canvas, float adjustedCenterX, int mGreenCircleAlpha) {
        mPaint.setAlpha(255);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        mPaint.setShader(null);
        mPaint.setMaskFilter(new BlurMaskFilter(6, BlurMaskFilter.Blur.NORMAL));
        mPaint.setXfermode(mPorterDuffAdd);

        float shorterLineLength = LINE_LENGHT / 2f;

        canvas.save();

        canvas.translate(adjustedCenterX, mCenterY);
        canvas.rotate(90 - (180 - mAngle));
        canvas.translate(-LINE_OVERLAP / 2.0f, -mPower * 3);
        mPaint.setColor(Color.argb(mGreenCircleAlpha, 0, 255, 0));
        mPaint.setShader(mRedGradient);
        canvas.drawLine(LINE_LENGHT, 0, 0, 0, mPaint);

        for (int i = CIRCLE_STEP; i < shorterLineLength; i += CIRCLE_STEP) {
            LinearGradient mShorterRedGradient = new LinearGradient(getXinCircle(shorterLineLength, i) + LINE_OVERLAP, 0, LINE_OVERLAP, 0,
                    new int[]{Color.TRANSPARENT, Color.RED, Color.RED, Color.RED},
                    new float[]{0f, .2f, .6f, 1f}, Shader.TileMode.CLAMP);

            mPaint.setShader(mShorterRedGradient);

            canvas.drawLine(getXinCircle(shorterLineLength, i) + LINE_OVERLAP, i, LINE_OVERLAP, i, mPaint);
            canvas.drawLine(getXinCircle(shorterLineLength, i) + LINE_OVERLAP, -i, LINE_OVERLAP, -i, mPaint);
        }

        mPaint.setShader(null);
        mPaint.setColor(Color.argb(mGreenCircleAlpha, 255, 0, 0));
        canvas.drawArc(new RectF(-shorterLineLength + LINE_OVERLAP * 0.85f, -shorterLineLength, shorterLineLength + LINE_OVERLAP * 0.85f, shorterLineLength), -80, 160, false, mPaint);

        canvas.restore();

        canvas.save();

        canvas.translate(adjustedCenterX, mCenterY);
        canvas.rotate(90 - (180 - mAngle));
        canvas.translate(LINE_OVERLAP / 2.0f, mPower * 3);
        mPaint.setColor(Color.argb(mGreenCircleAlpha, 255, 0, 0));
        mPaint.setShader(mGreenGradient);
        canvas.drawLine(0, 0, -LINE_LENGHT, 0, mPaint);

        for (int i = CIRCLE_STEP; i < shorterLineLength; i += CIRCLE_STEP) {
            LinearGradient mShorterGreenGradient = new LinearGradient(-LINE_OVERLAP, 0, -LINE_OVERLAP + -getXinCircle(shorterLineLength, i), 0,
                    new int[]{Color.GREEN, Color.GREEN, Color.GREEN, Color.TRANSPARENT},
                    new float[]{0.0f, .4f, .8f, 1f}, Shader.TileMode.CLAMP);
            mPaint.setShader(mShorterGreenGradient);

            canvas.drawLine(-LINE_OVERLAP, i, -LINE_OVERLAP + -getXinCircle(shorterLineLength, i), i, mPaint);
            canvas.drawLine(-LINE_OVERLAP, -i, -LINE_OVERLAP + -getXinCircle(shorterLineLength, i), -i, mPaint);
        }

        mPaint.setShader(null);
        mPaint.setColor(Color.argb(mGreenCircleAlpha, 0, 255, 0));
        canvas.drawArc(new RectF(-shorterLineLength - LINE_OVERLAP * 0.85f, -shorterLineLength, shorterLineLength - LINE_OVERLAP * 0.85f, shorterLineLength), 100, 160, false, mPaint);

        canvas.restore();

        mPaint.setMaskFilter(null);
    }

    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    protected static float CHECK_MARK_LINE_WIDTH = 100;
    protected static float CHECK_MARK_LINE_LENGTH_SHORT = 240;
    protected static float CHECK_MARK_LINE_LENGTH_LONG = 420;
    protected static float CHECK_MARK_LINE_ANGLE_SHORT = -135 - 90;
    protected static float CHECK_MARK_LINE_ANGLE_LONG = -45 - 90;
    protected static float CHECK_MARK_LINE_ROUNDING = 25;

    protected RectF mRectF = new RectF();

    public void drawCheckMark(Canvas canvas) {
        mRectF.top = 0;
        mRectF.left = 0;
        mRectF.right = CHECK_MARK_LINE_WIDTH;

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.FILL);

        canvas.save();
        canvas.translate(mCenterX - 64, mCenterY + 100);
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

    public float getXinCircle(double r, double y) {
        return (float) Math.sqrt(Math.pow(r, 2) - Math.pow(y, 2));
    }
}
