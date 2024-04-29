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
package com.vitorpamplona.netra.model;

import com.vitorpamplona.core.fitting.QualityOfFit;
import com.vitorpamplona.core.models.ComputedPrescription;
import com.vitorpamplona.core.models.MeridianPower;
import com.vitorpamplona.core.models.SinusoidalFunction;
import com.vitorpamplona.core.test.SphericalEquivalent;
import com.vitorpamplona.core.test.TheDoctor;
import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.domain.events.EventHistory;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Prescription data from any device.
 */
public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;

    RefractionType procedure;

    private UUID id;
    private Float sphere;
    private Float cylinder;
    private Float axis;
    private Float addLens;
    private Float vaCorrected;
    private Float nosePupilDistance;
    private Boolean cycloplegia = false;

    private ComputedPrescription originalData;
    private EventHistory history;

    public Prescription(UUID id) {
        this.id = id;
        procedure = RefractionType.NETRA;
    }

    public Prescription(Float spherical, Float cylindrical, Float axis, RefractionType proc) {
        sphere = spherical;
        cylinder = cylindrical;
        this.axis = axis;
        procedure = proc;

        putInNegativeCilinder();
    }

    public void set(Float spherical, Float cylindrical, Float axis, Float add, Float VA, Float nosePupilDistance) {
        this.sphere = spherical;
        this.cylinder = cylindrical;
        this.axis = axis;
        this.addLens = add;
        this.vaCorrected = VA;
        this.nosePupilDistance = nosePupilDistance;
        putInNegativeCilinder();
    }

    protected Prescription(Prescription p) {
        this.cycloplegia = p.cycloplegia;
        this.procedure = p.procedure;
        this.id = p.id;
        this.sphere = p.sphere;
        this.cylinder = p.cylinder;
        this.axis = p.axis;
        this.addLens = p.addLens;
        this.vaCorrected = p.vaCorrected;
        this.history = p.history;
        this.originalData = p.originalData;
        this.nosePupilDistance = p.nosePupilDistance;
    }

    public Prescription(RefractionType proc) {
        procedure = proc;
    }

    /**
     * Positive Notation to Negative Notation.
     */
    public void putInNegativeCilinder() {
        if (cylinder > 0.001) {
            sphere = sphere + cylinder;
            cylinder = -cylinder;
            axis = axis + 90;
        }

        checkAxisOutOfBounds();
    }

    /**
     * Positive Notation to Negative Notation.
     */
    public void putInPositiveCilinder() {
        if (cylinder < -0.001) {
            sphere = sphere + cylinder;
            cylinder = -cylinder;
            axis = axis + 90;
        }

        checkAxisOutOfBounds();
    }

    /**
     * Positive Notation to Negative Notation.
     */
    public Prescription copyInNegativeCilinder() {
        Prescription p = new Prescription(this);
        p.putInNegativeCilinder();
        p.checkAxisOutOfBounds();

        return p;
    }

    /**
     * Positive Notation to Negative Notation.
     */
    public Prescription copyInPositiveCilinder() {
        Prescription p = new Prescription(this);
        p.putInPositiveCilinder();
        p.checkAxisOutOfBounds();

        return p;
    }

    public Float getNosePupilDistance() {
        return nosePupilDistance;
    }

    public void setNosePupilDistance(Float nosePupilDistance) {
        this.nosePupilDistance = nosePupilDistance;
    }

    /**
     * Keep the axis between 0 and 180.
     */
    public void checkAxisOutOfBounds() {
        axis = AngleDiff.angle0to180(axis);
    }

    /**
     * Returns the spherical equivalent (Sphere + Cylinder / 2)
     */
    public Float sphEquivalent() {
        return SphericalEquivalent.compute(sphere, cylinder);
    }

    /**
     * Returns the interpolated power of a given angle.
     */
    public float interpolate(Float angleDegrees) {
        return SinusoidalFunction.interpolate(sphere, cylinder, axis, angleDegrees);
    }

    @Override
    public String toString() {
        if (sphere == null) {
            return "Null Object";
        }

        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(sphere) + " " + formatter.format(cylinder) + " @ " + axis.intValue();
    }

    public EventHistory history() {
        if (history == null)
            history = new EventHistory();
        return history;
    }

    public Float getSphere() {
        return sphere;
    }

    public void setSphere(Float spherical) {
        sphere = spherical;
    }

    public Float getCylinder() {
        return cylinder;
    }

    public void setCylinder(Float cylindrical) {
        cylinder = cylindrical;
    }

    public Float getAxis() {
        return axis;
    }

    public void setAxis(Float axis) {
        this.axis = axis;
    }

    public Float getAddLens() {
        return addLens;
    }

    public void setAddLens(Float plusLens) {
        addLens = plusLens;
    }

    public Float getVaCorrected() {
        return vaCorrected;
    }

    public void setVaCorrected(Float va) {
        vaCorrected = va;
    }

    public boolean getCyclo() {
        return cycloplegia;
    }

    public void setCyclo(boolean c) {
        cycloplegia = c;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID i) {
        id = i;
    }

    public RefractionType getProcedure() {
        return procedure;
    }

    public void setProcedure(RefractionType pt) {
        procedure = pt;
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

    public boolean needsReadingPower() {
        return addLens > 0;
    }

    public boolean isInNeedOfGlasses() {
        if (sphere == null || cylinder == null) return false;

        return TheDoctor.isInNeedOfGlassesForDistanceView(sphere.floatValue(), cylinder.floatValue());
    }

    public boolean isLightCorrection() {
        if (sphere == null || cylinder == null) return false;

        return TheDoctor.isLightCorrection(sphere.floatValue(), cylinder.floatValue());
    }

    public boolean isMediumCorrection() {
        if (sphere == null || cylinder == null) return false;

        return TheDoctor.isMediumCorrection(sphere.floatValue(), cylinder.floatValue());
    }

    public boolean isHighCorrection() {
        if (sphere == null || cylinder == null) return false;

        return TheDoctor.isHighCorrection(sphere.floatValue(), cylinder.floatValue());
    }

    public ComputedPrescription getOriginalData() {
        if (procedure.equals(RefractionType.NETRA) && originalData == null)
            originalData = new ComputedPrescription();
        return originalData;
    }


    public float getFittingQuality() {
        List<MeridianPower> newMeridians2 = new ArrayList<MeridianPower>();
        for (MeridianPower p : getOriginalData().allResults()) {
            newMeridians2.add(new MeridianPower(p.getAngle(), p.getPower()));
        }

        return new QualityOfFit().compute(newMeridians2, originalData.getFails(), originalData.getRounded());
    }

    public void setOriginalData(ComputedPrescription originalData) {
        this.originalData = originalData;
    }

    protected EventHistory getHistory() {
        return history;
    }

    public void setHistory(EventHistory history) {
        this.history = history;
    }

    public void updateFromOriginalData() {
        sphere = getOriginalData().getRounded().getSphere();
        cylinder = getOriginalData().getRounded().getCylinder();
        axis = getOriginalData().getRounded().getAxis();
        addLens = getOriginalData().getRounded().getAddLens();
    }

    public void updateFromFittedOriginalData() {
        sphere = getOriginalData().getSoftedCyls().getSphere();
        cylinder = getOriginalData().getSoftedCyls().getCylinder();
        axis = getOriginalData().getSoftedCyls().getAxis();
        addLens = getOriginalData().getSoftedCyls().getAddLens();
    }

}
