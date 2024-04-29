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
package com.vitorpamplona.netra.test.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.SlitPattern;
import com.vitorpamplona.core.testdevice.ui.BlueScreenPainter;
import com.vitorpamplona.core.testdevice.ui.CalibrationPainter;
import com.vitorpamplona.core.testdevice.ui.CanvasPainter;
import com.vitorpamplona.core.testdevice.ui.CheckMarkPainter;
import com.vitorpamplona.core.testdevice.ui.LoadDevicePainter;
import com.vitorpamplona.core.testdevice.ui.PlayIconPainter;
import com.vitorpamplona.core.testdevice.ui.TestPainterAccReady;
import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.utils.HardwareUtil;

public class UmbrellaTestView extends BaseTestView implements NETRAView {

    Screen mScreen = Screen.ALIEN_TEST;

    float mSliderDisplacement = 0f;

    protected SlitPattern mUmbrellaPosition;
    protected TestPainterAccReady mUmbrellaPainter;
    protected Paint mPaint;
    protected CanvasPainter mCanvasPainter;
    protected PlayIconPainter playIcon = new PlayIconPainter();

    protected ToneGenerator mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);

    boolean training = false;

    public UmbrellaTestView(Context context) {
        super(context);
        init(context);
    }

    public UmbrellaTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UmbrellaTestView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        mUmbrellaPosition = new SlitPattern(HardwareUtil.getDeviceDPI(context));
        mUmbrellaPainter = new TestPainterAccReady(this.getResources(), HardwareUtil.getDeviceDPI(this.getContext()));
        mPaint = new Paint();
        playIcon.setMsg(this.getResources().getString(R.string.play_icon_msg));

        mSliderDisplacement = 0;
    }


    public void setSliderDisplacement(float value) {
        mUmbrellaPainter.activatePDFinder();
        mSliderDisplacement = value;
    }

    public void redraw() {
        invalidateView();
    }

    public float getAngle() {
        if (mUmbrellaPosition == null || mUmbrellaPosition.getWorkingPair() == null)
            return Float.NaN;

        return mUmbrellaPosition.getWorkingMeridian().floatValue();
    }

    public void setAngle(float angle) {
        Float power = mUmbrellaPosition.computeMeridianPower();
        mUmbrellaPosition.setAngle(angle);
        if (power != null)
            power = setPowerClosestTo(power);

        invalidateView();
    }

    public void invalidateView() {
        ((Activity) this.getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }

    public float setPowerClosestTo(float power) {
        mUmbrellaPosition.setGivenValue(power);
        float finalPower = mUmbrellaPosition.computeMeridianPower();
        invalidateView();
        return finalPower;
    }

    public float resetPowerClosestTo(float power) {
        if (NetraGApplication.get().getSettings().isShowNumbers())
            mUmbrellaPainter.changedAngle();

        return setPowerClosestTo(power);
    }

    //Step lines closer together
    public float decreasePitch() {
        if (!checkPowerLowerBoundaries()) {
            return mUmbrellaPosition.computeMeridianPower();
        }

        if (mUmbrellaPosition.isActive()) {
            mUmbrellaPainter.activateLineAligner();
            mUmbrellaPosition.reducePitch();
            return mUmbrellaPosition.computeMeridianPower();
        }
        return Float.NaN;
    }

    public boolean isRunningTheTest() {
        return mScreen == Screen.ALIEN_TEST
                && mUmbrellaPosition != null
                && mUmbrellaPosition.isActive()
                && getDevice() != null
                && mUmbrellaPosition.computeMeridianPower() != null;
    }

    public boolean checkPowerLowerBoundaries() {
        if (!isRunningTheTest()) return true;

        if (mUmbrellaPosition.computeMeridianPower() < getDevice().lowestPower) {
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
            return false;
        }

        return true;
    }

    public boolean checkPowerUpperBoundaries() {
        if (!isRunningTheTest()) return true;

        if (mUmbrellaPosition.computeMeridianPower() > getDevice().highestPower) {
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
            return false;
        }

        return true;
    }

    //Step lines further apart
    public float increasePitch() {
        if (!checkPowerUpperBoundaries()) {
            return mUmbrellaPosition.computeMeridianPower();
        }

        if (mUmbrellaPosition.isActive()) {
            mUmbrellaPainter.activateLineAligner();
            mUmbrellaPosition.increasePitch();
            return mUmbrellaPosition.computeMeridianPower();
        }
        return Float.NaN;
    }

    public int spToPx(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sp,
                getResources().getDisplayMetrics());
    }

    public void writeCentered(Canvas canvas, Paint mPaint, String text, float f) {
        canvas.drawText(text,
                (canvas.getWidth() - mPaint.measureText(text)) / 2.0f,
                f, mPaint);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.rgb(0, 0, 0));

        if (getDevice() == null) {
            return;
        }

        if (mScreen == Screen.ALIEN_TEST) {
            if (!isRunningTheTest()) {
                // Blue screen to start camera.
                canvas.drawColor(Color.rgb(0, 0, 150));

                invalidateView();
                return;
            }
        }

        mCanvasPainter.paint(canvas, getDevice(), testingRightEye(), mUmbrellaPosition.getWorkingPair(), positionYPX(),
                toPX(mSliderDisplacement) + testPositionXPX(), toPX(mSliderDisplacement) + idlePositionXPX(), 1);

        if (training) {
            drawTraining(canvas);
        }

        if (mScreen == Screen.CHECK_MARK) {
            drawFinish(canvas);
        } else {
            drawCancel(canvas);
        }

        invalidateView();
    }

    public static int dpToPixels(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
        // S4 scale == 3.
    }

    public void drawMessage(Canvas canvas, String msg, int color) {
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        paint.setAntiAlias(true);
        paint.setDither(true);

        float centerX = getWidth() / 2 - 10;
        float radiusXOnHalfY = dpToPixels(this.getContext(), 140 / 3);
        float radiusY = dpToPixels(this.getContext(), 105 / 3);
        float radiusX = dpToPixels(this.getContext(), 180 / 3);

        Path path = new Path();
        path.moveTo(centerX, getHeight() - radiusY);
        path.lineTo(centerX - radiusXOnHalfY, getHeight() - radiusY / 2);
        path.lineTo(centerX - radiusX, getHeight());
        path.lineTo(centerX + radiusX, getHeight());
        path.lineTo(centerX + radiusXOnHalfY, getHeight() - radiusY / 2);
        path.lineTo(centerX, getHeight() - radiusY);

        canvas.drawPath(path, paint);

        paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(dpToPixels(this.getContext(), 16));
        canvas.save();
        canvas.translate(centerX, getHeight() - 10);
        float size = paint.measureText(msg, 0, msg.length());
        canvas.drawText(msg, -size / 2, 0, paint);

        canvas.restore();
    }

    public void drawMessageOtherSide(Canvas canvas, String msg, int color) {
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        paint.setAntiAlias(true);
        paint.setDither(true);

        float centerX = getWidth() / 2 - 10;
        float radiusXOnHalfY = dpToPixels(this.getContext(), 140 / 3);
        float radiusY = dpToPixels(this.getContext(), 105 / 3);
        float radiusX = dpToPixels(this.getContext(), 180 / 3);

        Path path = new Path();
        path.moveTo(centerX, radiusY);
        path.lineTo(centerX - radiusXOnHalfY, radiusY / 2);
        path.lineTo(centerX - radiusX, 0);
        path.lineTo(centerX + radiusX, 0);
        path.lineTo(centerX + radiusXOnHalfY, radiusY / 2);
        path.lineTo(centerX, radiusY);

        canvas.drawPath(path, paint);

        paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(dpToPixels(this.getContext(), 16));
        canvas.save();
        canvas.translate(centerX, 40);
        float size = paint.measureText(msg, 0, msg.length());
        canvas.drawText(msg, -size / 2, 0, paint);

        canvas.restore();
    }

    public void drawCancel(Canvas canvas) {
        drawMessage(canvas, getResources().getString(R.string.cancel_test), Color.rgb(125, 0, 0));
    }

    public void drawFinish(Canvas canvas) {
        drawMessage(canvas, getResources().getString(R.string.finish_test), Color.rgb(0, 125, 0));
    }

    public void drawTraining(Canvas canvas) {
        drawMessageOtherSide(canvas, getResources().getString(R.string.training), Color.rgb(0, 125, 0));
    }

    @Override
    public void setDevice(Device d) {
        super.setDevice(d);
        reset();
        invalidateView();
        mUmbrellaPainter.resetParticles();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mUmbrellaPainter.stopCreatingParticles();
    }

    @Override
    public void reset() {
        super.reset();
        setScreen(Screen.ALIEN_TEST);

        if (getDevice() != null) {
            mUmbrellaPosition.reset(HardwareUtil.getDeviceDPI(this.getContext()), getDevice().tubeLength,
                    getDevice().lensFocalLength, getDevice().lensEyeDistance,
                    getDevice().slitDistance);
            this.setPD(getDevice().defaultPD);
            mUmbrellaPainter.startCreatingParticles();
        }
    }

    public void setDoingPDMeasurement(boolean doing) {
        mUmbrellaPainter.setAvoidConvergence(doing);
    }

    public void setScreen(Screen s) {
        training = false;
        mScreen = s;
        switch (mScreen) {
            case ALIEN_TEST:
                mCanvasPainter = mUmbrellaPainter;
                break;
            case TRAINING_TEST:
                mCanvasPainter = mUmbrellaPainter;
                training = true;
                break;
            case BLUE_SCREEN:
                mCanvasPainter = new BlueScreenPainter();
                break;
            case PLAY_ICON:
                mCanvasPainter = playIcon;
                break;
            case CHECK_MARK:
                mCanvasPainter = new CheckMarkPainter();
                break;
            case CALIBRATION:
                mCanvasPainter = new CalibrationPainter();
                break;
            case LOAD_DEVICE:
                mCanvasPainter = new LoadDevicePainter();
                break;
            default:
                break;
        }
        invalidate();
    }


}