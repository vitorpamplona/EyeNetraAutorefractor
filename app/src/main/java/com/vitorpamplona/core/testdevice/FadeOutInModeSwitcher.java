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


import android.annotation.SuppressLint;

public class FadeOutInModeSwitcher {

    long fadeInDuration;
    long fadeOutDuration;

    long startingTime;
    float currentAlpha;
    int modeDirection;

    Clock clock = new SystemClock();

    public FadeOutInModeSwitcher(long fadeOutDuration, long fadeInDuration, float startingAlpha) {
        super();
        this.fadeInDuration = fadeInDuration;
        this.fadeOutDuration = fadeOutDuration;
        this.currentAlpha = startingAlpha;
        this.startingTime = clock.time() - fadeInDuration - fadeOutDuration;
        resetModeDirection();
    }

    public void setClock(Clock clock) {
        this.clock = clock;
        this.startingTime = clock.time() - fadeInDuration - fadeOutDuration;
    }

    public void resetModeDirection() {
        if (currentAlpha < 0)
            modeDirection = 1;
        else
            modeDirection = -1;
    }

    public void start() {
        if (timeSinceAnimationStarted() >= fadeInFinishes()) {
            startingTime = clock.time();
            resetModeDirection();
        }
    }

    public void start(int towards) {
        if (timeSinceAnimationStarted() >= fadeInFinishes()) {
            startingTime = clock.time();
            modeDirection = towards;
        } else if (modeDirection != towards) {
            // changing direction in the middle of an animation
            computeAlpha();

            modeDirection = towards;

            if (needsFadingOut()) {
                // remove the current state from the starting time so that the animation runs smoothly.
                startingTime = (long) (clock.time() - ((1 - Math.abs(currentAlpha)) * fadeOutDuration));
            } else {
                // already faded out, so remove from time.
                startingTime = (long) (clock.time() - fadeOutDuration);
                // now use the current state to remove time from the fade in process.
                startingTime = startingTime - (long) (Math.abs(currentAlpha) * fadeInDuration);
            }

            validateClockInThePast();
        }
    }

    public void validateClockInThePast() {
        if (startingTime > clock.time()) {
            startingTime = clock.time();
            System.out.println("Clock In The Future. ");
        }
    }

    public long timeSinceAnimationStarted() {
        return clock.time() - startingTime;
    }

    public boolean needsFadingOut() {
        return (modeDirection > 0 && currentAlpha < 0
                || modeDirection < 0 && currentAlpha > 0);
    }

    public boolean isLeadingTowardsNegativeAlpha() {
        return modeDirection < 0;
    }

    public float fadeOut(long timeSinceStart) {
        // Fading out
        float alpha = timeSinceStart / (float) fadeOutDuration;

        //System.out.println("Fading Out Time Percentage " + alpha + " Time since start "+ timeSinceStart);

        if (isLeadingTowardsNegativeAlpha()) {
            //System.out.println("Fading Out Leading towards Negative " + modeDirection);
            // decreasing alpha
            alpha = 1 - alpha;
            if (alpha < currentAlpha) {
                return alpha;
            }
        } else {
            //System.out.println("Fading Out Leading towards Positive " + modeDirection + " from " + currentAlpha);

            // increasing alpha
            alpha = alpha - 1;
            //System.out.println("Fading Out new Alpha " + alpha);
            if (alpha > currentAlpha) {
                //System.out.println("Fading Out new Alpha " + alpha);
                return alpha;
            }
        }

        return currentAlpha;
    }

    private float fadeIn(long timeSinceStart) {
        // Fading out
        float fadingTime = timeSinceStart - fadeOutDuration;
        float alpha = (fadingTime / (float) fadeInDuration);

        if (isLeadingTowardsNegativeAlpha()) {
            alpha = -alpha;
            if (alpha < currentAlpha) {
                return alpha;
            }
        } else {
            if (alpha > currentAlpha) {
                return alpha;
            }
        }

        return currentAlpha;
    }

    /**
     * Returns a two state fade [-1, 1]. Positive values indicate first mode, Negative values indicate a second mode.
     * @return
     */
    public float computeAlpha() {
        long timeSinceStart = timeSinceAnimationStarted();

        if (timeSinceStart <= fadeOutFinishes()) {
            currentAlpha = fadeOut(timeSinceStart);
            //System.out.println("Fading Out " + currentAlpha);
        } else if (timeSinceStart <= fadeInFinishes()) {
            currentAlpha = fadeIn(timeSinceStart);
            //System.out.println("Fading In " + currentAlpha);
        } else {
            //System.out.println("Stable Limits");
            currentAlpha = modeDirection;
        }

        return currentAlpha = validateCurrentAlpha(currentAlpha);
    }

    @SuppressLint("SuspiciousIndentation")
    private float validateCurrentAlpha(float currentAlpha) {
        if (currentAlpha > 1) return 1;
        if (currentAlpha < -1) return -1;
        return currentAlpha;
    }

    private float fadeOutFinishes() {
        return fadeOutDuration;
    }

    private float fadeInFinishes() {
        return fadeOutDuration + fadeInDuration;
    }

}
