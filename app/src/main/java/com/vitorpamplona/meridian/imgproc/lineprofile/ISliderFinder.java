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

import android.graphics.Canvas;

import com.vitorpamplona.core.testdevice.Point2D;
import com.vitorpamplona.meridian.utils.SignalNormalizer;

public interface ISliderFinder {

    /**
     * Set the Rect in which the Slider can be found.
     *
     * @param topLeft
     * @param bottomRight
     */
    public abstract void setRect(Point2D topLeft, Point2D bottomRight);

    public abstract void setParameters(Point2D point1, Point2D point2, int threshold, AutoCalibration mAutoCalibration);

    /**
     * Returns the x position of the slider in pixels.
     **/
    public abstract ErrorCode find(byte[] grayscale, SignalNormalizer signalNormalizer);

    /**
     * Writes the debug information on the mRGA image using the debugColor.
     *
     * @param mRgba Full RGBA image to be shown on screen.
     * @param debugColor Color to paint the debug information.
     *
     * @return the new RGBA image to display.
     */
    public abstract void writeDebugInfo(Canvas canvas);

    /**
     * Indicates if the slider is moving or not. If true, it will avoid
     * the search for the meridian and mouse wheel.
     *
     * @return if the eyeshield is moving or not.
     */
    public abstract boolean isMoving();

    /**
     * Return the center point (the zero displacement) for the particular
     * rect this finder is searching.
     *
     * @return The X coordinate of the center of the rect.
     */
    public abstract double getCenter();

    public abstract Point2D getSliderPosition();


}