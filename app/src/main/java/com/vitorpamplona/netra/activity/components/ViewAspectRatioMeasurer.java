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

import android.view.View;

/**
 * This class is a helper to measure views that require a specific aspect ratio.<br />
 * <br />
 * The measurement calculation is differing depending on whether the height and width
 * are fixed (match_parent or a dimension) or not (wrap_content)
 *
 * <pre>
 *                | Width fixed | Width dynamic |
 * ---------------+-------------+---------------|
 * Height fixed   |      1      |       2       |
 * ---------------+-------------+---------------|
 * Height dynamic |      3      |       4       |
 * </pre>
 * Everything is measured according to a specific aspect ratio.<br />
 * <br />
 * <ul>
 * <li>1: Both width and height fixed:   Fixed (Aspect ratio isn't respected)</li>
 * <li>2: Width dynamic, height fixed:   Set width depending on height</li>
 * <li>3: Width fixed, height dynamic:   Set height depending on width</li>
 * <li>4: Both width and height dynamic: Largest size possible</li>
 * </ul>
 */
public class ViewAspectRatioMeasurer {

    private double aspectRatio;

    /**
     * Create a ViewAspectRatioMeasurer instance.<br/>
     * <br/>
     * Note: Don't construct a new instance everytime your <tt>View.onMeasure()</tt> method
     * is called.<br />
     * Instead, create one instance when your <tt>View</tt> is constructed, and
     * use this instance's <tt>measure()</tt> methods in the <tt>onMeasure()</tt> method.
     * @param aspectRatio
     */
    public ViewAspectRatioMeasurer(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    /**
     * Measure with the aspect ratio given at construction.<br />
     * <br />
     * After measuring, get the width and height with the {@link #getMeasuredWidth()}
     * and {@link #getMeasuredHeight()} methods, respectively.
     * @param widthMeasureSpec The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
     * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
     */
    public void measure(int widthMeasureSpec, int heightMeasureSpec) {
        measure(widthMeasureSpec, heightMeasureSpec, this.aspectRatio);
    }

    /**
     * Measure with a specific aspect ratio<br />
     * <br />
     * After measuring, get the width and height with the {@link #getMeasuredWidth()}
     * and {@link #getMeasuredHeight()} methods, respectively.
     * @param widthMeasureSpec The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
     * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
     * @param aspectRatio The aspect ratio to calculate measurements in respect to 
     */
    public void measure(int widthMeasureSpec, int heightMeasureSpec, double aspectRatio) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = widthMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = heightMode == View.MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : View.MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == View.MeasureSpec.EXACTLY && widthMode == View.MeasureSpec.EXACTLY) {
            /*
             * Possibility 1: Both width and height fixed
             */
            measuredWidth = widthSize;
            measuredHeight = heightSize;

        } else if (heightMode == View.MeasureSpec.EXACTLY) {
            /*
             * Possibility 2: Width dynamic, height fixed
             */
            measuredWidth = (int) Math.min(widthSize, heightSize * aspectRatio);
            measuredHeight = (int) (measuredWidth / aspectRatio);

        } else if (widthMode == View.MeasureSpec.EXACTLY) {
            /*
             * Possibility 3: Width fixed, height dynamic
             */
            measuredHeight = (int) Math.min(heightSize, widthSize / aspectRatio);
            measuredWidth = (int) (measuredHeight * aspectRatio);

        } else {
            /*
             * Possibility 4: Both width and height dynamic
             */
            if (widthSize > heightSize * aspectRatio) {
                measuredHeight = heightSize;
                measuredWidth = (int) (measuredHeight * aspectRatio);
            } else {
                measuredWidth = widthSize;
                measuredHeight = (int) (measuredWidth / aspectRatio);
            }

        }
    }

    private Integer measuredWidth = null;

    /**
     * Get the width measured in the latest call to <tt>measure()</tt>.
     */
    public int getMeasuredWidth() {
        if (measuredWidth == null) {
            throw new IllegalStateException("You need to run measure() before trying to get measured dimensions");
        }
        return measuredWidth;
    }

    private Integer measuredHeight = null;

    /**
     * Get the height measured in the latest call to <tt>measure()</tt>.
     */
    public int getMeasuredHeight() {
        if (measuredHeight == null) {
            throw new IllegalStateException("You need to run measure() before trying to get measured dimensions");
        }
        return measuredHeight;
    }

}