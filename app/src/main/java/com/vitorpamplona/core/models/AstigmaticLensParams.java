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
package com.vitorpamplona.core.models;

import com.vitorpamplona.core.test.TheDoctor;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.RefRounding;

import java.io.Serializable;
import java.text.DecimalFormat;

public class AstigmaticLensParams implements Serializable {

    static DecimalFormat sphereCylFormatter = new DecimalFormat("+0.00;-0.00");

    private static final long serialVersionUID = 1L;

    private float sphere;
    private float cylinder;
    private float axis;
    private float addLens;

    public AstigmaticLensParams(float sphere, float cylinder, float axis) {
        super();
        this.sphere = sphere;
        this.cylinder = cylinder;
        this.axis = axis;

        putInNegativeCilinder();
    }

    public AstigmaticLensParams() {
        this.sphere = 0;
        this.cylinder = 0;
        this.axis = 0;

        putInNegativeCilinder();
    }

    public void putInNegativeCilinder() {
        if (cylinder > 0.001) {
            this.sphere = this.sphere + this.cylinder;
            this.cylinder = -cylinder;
            this.axis = this.axis + 90;
        }

        checkAxisOutOfBounds();
    }

    public void checkAxisOutOfBounds() {
        this.axis = AngleDiff.angle0to180(this.axis);
    }

    public float getSphere() {
        return sphere;
    }

    public void setSphere(float sphere) {
        this.sphere = sphere;
    }

    public float getCylinder() {
        return cylinder;
    }

    public void setCylinder(float cylinder) {
        this.cylinder = cylinder;
    }

    public float getAxis() {
        return axis;
    }

    public void setAxis(float axis) {
        this.axis = axis;
    }

    public float sphEquivalent() {
        return sphere + cylinder / 2;
    }

    /**
     * Returns the interpolated power of a given angle.
     */
    public float interpolate(float angleDegrees) {
        return SinusoidalFunction.interpolate(sphere, cylinder, axis, angleDegrees);
    }

    public float getAddLens() {
        return addLens;
    }

    public void setAddLens(float addLens) {
        this.addLens = addLens;
    }

    public boolean isMyopia() {
        return TheDoctor.hasMyopia(sphere, cylinder);
    }

    public boolean isHyperopia() {
        return TheDoctor.hasHyperopia(sphere, cylinder);
    }

    public boolean isAstigmat() {
        return TheDoctor.hasAstigmatism(sphere, cylinder);
    }

    public boolean isInNeedOfGlasses() {
        return TheDoctor.isInNeedOfGlassesForDistanceView(sphere, cylinder);
    }

    public boolean needsReadingPower() {
        return addLens > 0;
    }

    public String toString() {
        return sphereCylFormatter.format(sphere) + " " + sphereCylFormatter.format(cylinder) + " @ " + (String.format("%3.0f°", axis)) + " (" + sphereCylFormatter.format(sphEquivalent()) + ")";
    }

    public String toRoundedString() {
        float newCyl = RefRounding.roundTo(cylinder, 0.25f);
        float newSph = RefRounding.roundTo(sphere, 0.25f);
        return sphereCylFormatter.format(newSph) + " " + sphereCylFormatter.format(newCyl) + " @ " + (String.format("%3.0f°", axis)) + " (" + sphereCylFormatter.format(sphEquivalent()) + ")";
    }
}
