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
package com.vitorpamplona.core.testdevice.ui.convergence;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;

import com.vitorpamplona.core.testdevice.DeviceDataset.Device;
import com.vitorpamplona.core.testdevice.DeviceModelSettings;
import com.vitorpamplona.core.testdevice.Pair;
import com.vitorpamplona.core.testdevice.Particle;
import com.vitorpamplona.core.testdevice.ui.CachedBitmapFactory;
import com.vitorpamplona.core.testdevice.ui.CanvasPainter;
import com.vitorpamplona.core.utils.DeviceModelParser;
import com.vitorpamplona.netra.R;

import java.util.ArrayList;
import java.util.List;


public class ConvergenceLockPainter implements CanvasPainter {

    protected final Paint mPaint = new Paint();

    protected static final int CREATE_NEW_PARTICLES_AT_EVERY = 500; // ms

    protected List<Particle> mParticleList = new ArrayList<Particle>();
    protected List<Particle> mRecycleList = new ArrayList<Particle>();

    protected static final float PARTICLE_SYSTEM_OUTER_LIMIT = 500f;
    protected static final float PARTICLE_SYSTEM_INNER_LIMIT = 30f;
    protected static final float PARTICLE_SYSTEM_START_FADING_AWAY = 440f;
    protected static final float RADIUS_OF_PARTICLES = 2; // mm

    protected static final float ALIGNMENT_CIRCLE_THICKNESS_MM = 2f;
    protected static final float ALIGNMENT_CIRCLE_RADIUS_MM = 8.0f;

    private float displayDPI;
    protected Bitmap mask;
    private int butterflyColor = 0;

    private float dyMM = 0;

    private final Runnable newParticles = new Runnable() {
        @Override
        public void run() {
            createParticles();
        }
    };
    Handler handler = new Handler();

    public ConvergenceLockPainter(Resources r, float displayDPI) {
        this.displayDPI = displayDPI;
        this.mask = CachedBitmapFactory.getInstance().decodeResource(r, R.drawable.mask);
        resetParticles();
    }

    public float getPixelSizeMM() {
        return 25.4f / displayDPI;
    }

    public float toPX(float mm) {
        return mm / getPixelSizeMM();
    }

    @Override
    public boolean paint(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleXPX, float testYPX, float idleYPX, float alpha) {
        if (alpha > 0.9) {
            //dy += 0.2;

            if (dyMM > 2) {
                dyMM = 2;
            }
        } else if (alpha < 0.2) {
            dyMM = 0;
        }

        drawConvergenceSystem(canvas, device, mPaint, middleXPX, testYPX - toPX(dyMM), alpha, true);
        drawConvergenceSystem(canvas, device, mPaint, middleXPX, idleYPX + toPX(dyMM), alpha, false);

        drawGaussianMaskToDarkenTheCenter(canvas, mPaint, middleXPX, testYPX - toPX(dyMM), mask, ALIGNMENT_CIRCLE_THICKNESS_MM / 2f, ALIGNMENT_CIRCLE_RADIUS_MM);
        drawGaussianMaskToDarkenTheCenter(canvas, mPaint, middleXPX, idleYPX + toPX(dyMM), mask, ALIGNMENT_CIRCLE_THICKNESS_MM / 2f, ALIGNMENT_CIRCLE_RADIUS_MM);

        return true;
    }

    public boolean paintJustImgProc(Canvas canvas, Device device, boolean testingRightEye, Pair workingPair, float middleX, float testY, float idleY, float alpha) {
        float backgroundSize = canvas.getHeight() / 2.0f;

        canvas.save();
        canvas.translate(testY, middleX);

        drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(canvas, device, mPaint, backgroundSize);

        drawTopBottonRailLinesToHelpOnTilt(canvas, mPaint, backgroundSize);

        canvas.restore();

        canvas.save();
        canvas.translate(idleY, middleX);

        drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(canvas, device, mPaint, backgroundSize);

        drawTopBottonRailLinesToHelpOnTilt(canvas, mPaint, backgroundSize);

        canvas.restore();

        return true;
    }

    /**
     * Draws everything needed for the convergence system to work 
     *
     * @param canvas
     * @param mPaint
     * @param xCenterPX
     * @param yCenterPX
     */
    public void drawConvergenceSystem(Canvas canvas, Device device, Paint mPaint, float xCenterPX, float yCenterPX, float alpha, boolean testingEye) {
        canvas.save();
        canvas.translate(yCenterPX, xCenterPX);
        canvas.rotate(-90);

        float backgroundSize = canvas.getHeight() / 2.0f;

        drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(canvas, device, mPaint, backgroundSize);

        drawParticleSystem(canvas, mPaint, alpha);

        drawCrossToHelpCenterAndConvergence(canvas, mPaint, backgroundSize, alpha, testingEye);

        drawTopBottonRailLinesToHelpOnTilt(canvas, mPaint, backgroundSize);

        canvas.restore();
    }


    /**
     * Draws the light for image processing close to the camera position. The light from the screen retroreflects back to the camera
     * allowing for an accurate image processing.
     *
     * @param canvas
     * @param mPaint
     * @param backgroundSize
     */
    public void drawLightEmmissionForImageProcessingPhotoreflectiveMarkers(Canvas canvas, Device device, Paint mPaint, float backgroundSize) {

        if (butterflyColor == 0) {
            String nameOfDevice = DeviceModelParser.getDeviceName();
            String color = new DeviceModelSettings(nameOfDevice).getButterflyColor();
            butterflyColor = Color.parseColor(color);
        }

        mPaint.setColor(butterflyColor);
//        mPaint.setColor(Color.rgb(60, 60, 255));  // old butterfly
        mPaint.setStyle(Style.FILL);
        canvas.drawArc(new RectF(backgroundSize - toPX(34.5f), -backgroundSize - toPX(14.5f), backgroundSize + toPX(34.5f), -backgroundSize + toPX(14.5f)), 90, 90, true, mPaint);
        canvas.drawArc(new RectF(backgroundSize - toPX(14.5f), -backgroundSize - toPX(34.5f), backgroundSize + toPX(14.5f), -backgroundSize + toPX(34.5f)), 90, 90, true, mPaint);
    }

    /**
     * Draws the top and bottom lines to help the vertical alignment. 
     * The idea is that people will try to see both of them and thus be in the center of the lines 
     *
     * @param canvas
     * @param mPaint
     * @param backgroundSize
     */
    public void drawTopBottonRailLinesToHelpOnTilt(Canvas canvas, Paint mPaint, float backgroundSize) {
        mPaint.setColor(Color.rgb(0, 0, 255));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(60);
        canvas.drawLine(-backgroundSize, -backgroundSize, -backgroundSize, backgroundSize, mPaint);
        canvas.drawLine(backgroundSize, -backgroundSize, backgroundSize, backgroundSize, mPaint);
    }

    public void drawCrossToHelpCenterAndConvergence(Canvas canvas, Paint mPaint, float backgroundSize, float alpha, boolean testingEye) {
        mPaint.setColor(Color.argb((int) (255 * alpha), 0, 0, 255));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(30);

        //if (testingEye)
        //    canvas.drawLine(-backgroundSize, 0, 0, 0, mPaint);
        //else
        //    canvas.drawLine(0, 0, backgroundSize, 0, mPaint);

        canvas.drawLine(-backgroundSize, 0, backgroundSize, 0, mPaint);
        canvas.drawLine(-backgroundSize, -backgroundSize, backgroundSize, backgroundSize, mPaint);
        canvas.drawLine(-backgroundSize, +backgroundSize, backgroundSize, -backgroundSize, mPaint);

        mPaint.setColor(Color.rgb(0, 0, 255));
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(30);
        canvas.drawCircle(0, 0, backgroundSize, mPaint);

        //canvas.drawCircle(0, 0, toPX(9.0f), mPaint);
    }

    /**
     * Draws the particle system on the background. 
     *
     * @param canvas
     * @param mPaint
     */
    long timeInMillis = 0;

    public void drawParticleSystem(Canvas canvas, Paint mPaint, float alpha) {
        canCreateMoreParticles = true;

        double deltaTimeInMillis = 100;
        if (timeInMillis < 10) {
            timeInMillis = System.currentTimeMillis();
        } else {
            deltaTimeInMillis = System.currentTimeMillis() - timeInMillis;
            timeInMillis = System.currentTimeMillis();
        }

        synchronized (mParticleList) {
            for (int i = 0; i < mParticleList.size(); i++) {
                Particle p = mParticleList.get(i);
                p.move(deltaTimeInMillis);

                int blue = (int) (p.color * 255);
                int redGreen = (int) (p.color * 50);

                mPaint.setColor(Color.argb((int) (Math.min(getFadingFactor(p), alpha) * 255), redGreen, redGreen, blue));
                mPaint.setStyle(Style.FILL);
                canvas.drawCircle(p.x - 10, p.y - 10, p.size, mPaint);

                if (isOutsideRangeInversed(p)) {
                    mRecycleList.add(mParticleList.remove(i));
                    i--;
                }
            }
        }
    }

    public void resetStartingPosition() {
        canCreateMoreParticles = true;
        justCreateTheParticles();
        for (int frames = 0; frames < 200; frames++) {
            for (int i = 0; i < mParticleList.size(); i++) {
                Particle p = mParticleList.get(i);
                p.move(10);
                if (isOutsideRangeInversed(p)) {
                    mRecycleList.add(mParticleList.remove(i));
                    i--;
                }
            }

            if (frames % 20 == 0) {
                canCreateMoreParticles = true;
                justCreateTheParticles();
            }

            //Log.i("Reset Bubbles", "Frame " +  frames + " Num Particles " + mParticleList.size());
        }
    }

    public boolean isOutsideRange(Particle p) {
        return p.x < -PARTICLE_SYSTEM_OUTER_LIMIT
                || p.x > PARTICLE_SYSTEM_OUTER_LIMIT
                || p.y < -PARTICLE_SYSTEM_OUTER_LIMIT
                || p.y > PARTICLE_SYSTEM_OUTER_LIMIT;
    }

    public boolean isOutsideRangeInversed(Particle p) {
        return Math.abs(p.x) < PARTICLE_SYSTEM_INNER_LIMIT
                && Math.abs(p.y) < PARTICLE_SYSTEM_INNER_LIMIT;
    }

    public float linearInterpolation(float start, float end, float position) {
        float value = ((position - start) / (end - start));
        if (value > 1) value = 1;
        if (value < 0) value = 0;
        return 1 - value;
    }

    public float getFadingFactor(Particle p) {
        float dist = (float) Math.sqrt(p.x * p.x + p.y * p.y);

        if (dist < PARTICLE_SYSTEM_START_FADING_AWAY) {
            return 1;
        }

        if (dist > PARTICLE_SYSTEM_OUTER_LIMIT) {
            return 0;
        }

        return linearInterpolation(PARTICLE_SYSTEM_START_FADING_AWAY, PARTICLE_SYSTEM_OUTER_LIMIT, dist);
    }

    public void drawGaussianMaskToDarkenTheCenter(Canvas canvas, Paint mPaint, float xCenterPX, float yCenterPX, Bitmap img, float lineSizeMM, float radiusMM) {
        canvas.save();
        canvas.translate(yCenterPX, xCenterPX);

        canvas.drawBitmap(img, -img.getWidth() / 2, -img.getHeight() / 2, mPaint);

        drawBlackHoleToDarkenTheCenter(canvas, mPaint, lineSizeMM, radiusMM / 2);

        canvas.restore();
    }

    public void drawBlackHoleToDarkenTheCenter(Canvas canvas, Paint mPaint, float lineSizeMM, float radiusMM) {
        mPaint.setColor(Color.rgb(0, 0, 0));
        mPaint.setStrokeWidth(toPX(lineSizeMM));
        mPaint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(0, 0, toPX(radiusMM + 4), mPaint);
    }

    // ////PARTICLE MANAGEMENT \\\\\\\
    boolean canCreateMoreParticles = true;

    private void createParticles() {
        if (!mParticleList.isEmpty()) {
            Particle p = mParticleList.get(mParticleList.size() - 1);
            double dist = Math.sqrt(p.x * p.x + p.y * p.y);

            // Allow the bubbles to scatter more.
            if (dist < 10) {
                handler.postDelayed(newParticles, CREATE_NEW_PARTICLES_AT_EVERY);
                return;
            }
        }


        if (!canCreateMoreParticles) {
            handler.postDelayed(newParticles, CREATE_NEW_PARTICLES_AT_EVERY);
            return;
        }

        if (mParticleList.size() > 50) {
            handler.postDelayed(newParticles, CREATE_NEW_PARTICLES_AT_EVERY);
            return;
        }

        canCreateMoreParticles = false;

        justCreateTheParticles();

        handler.postDelayed(newParticles, CREATE_NEW_PARTICLES_AT_EVERY);
    }

    public void justCreateTheParticles() {
        Particle p;
        int recycleCount = 0;

        if (mRecycleList.size() > 1)
            recycleCount = 2;
        else
            recycleCount = mRecycleList.size();

        for (int i = 0; i < recycleCount; i++) {
            p = mRecycleList.remove(0);
            p.init(0, 0);
            mParticleList.add(p);
        }

        for (int i = 0; i < 2 - recycleCount; i++) {
            mParticleList.add(new Particle(0, 0, toPX(RADIUS_OF_PARTICLES)));
        }
    }

    public void resetParticles() {
        mParticleList = new ArrayList<Particle>();
        mRecycleList = new ArrayList<Particle>();
        resetStartingPosition();
    }

    public void stopCreatingParticles() {
        handler.removeCallbacks(newParticles);
    }

    public void startCreatingParticles() {
        handler.postDelayed(newParticles, 1);
    }
}
