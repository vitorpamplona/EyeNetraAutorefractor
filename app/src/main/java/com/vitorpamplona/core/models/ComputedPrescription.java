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

import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.core.utils.FloatHashMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComputedPrescription {
    private static final long serialVersionUID = 1L;

    @Deprecated
    private FloatHashMap<MeridianPower> testResults;
    private List<MeridianPower> testResults2;
    private int fails = 0;

    public ComputedPrescription() {
        this.testResults = new FloatHashMap<MeridianPower>();
    }

    private AstigmaticLensParams fitted = new AstigmaticLensParams(0, 0, 0);
    private AstigmaticLensParams softedCyls = new AstigmaticLensParams(0, 0, 0);
    private AstigmaticLensParams rounded = new AstigmaticLensParams(0, 0, 0);
    private AstigmaticLensParams accepted = new AstigmaticLensParams(0, 0, 0);

    public void clear() {
        testResults = new FloatHashMap<MeridianPower>();
        testResults2 = new ArrayList<MeridianPower>();
        fitted = new AstigmaticLensParams(0, 0, 0);
        softedCyls = new AstigmaticLensParams(0, 0, 0);
        rounded = new AstigmaticLensParams(0, 0, 0);
        accepted = new AstigmaticLensParams(0, 0, 0);
        fails = 0;
    }

    /**
     * Lazy Initialization
     *
     * @return
     */
    public FloatHashMap<MeridianPower> testResults() {
        if (testResults == null)
            testResults = new FloatHashMap<MeridianPower>();
        return testResults;
    }

    /**
     * Lazy Initialization
     *
     * @return
     */
    public List<MeridianPower> testResults2() {
        if (testResults2 == null)
            testResults2 = new ArrayList<MeridianPower>();
        return testResults2;
    }

    public Collection<MeridianPower> allResults() {
        List<MeridianPower> powers = new ArrayList(testResults().values());
        powers.addAll(testResults2());
        return powers;
    }

    /**
     * Lazy Initialization
     *
     * @return
     */
    public String testResultsDebugInfo() {
        StringBuilder builder = new StringBuilder();

        for (MeridianPower p : testResults().values()) {
            builder.append(p.getAngle() + ": " + p.getPower() + "\n");
        }

        return builder.toString();
    }

    public void saveResult(MeridianPower result) {
        testResults2().add(result);
    }

    public void add(MeridianPower result) {
        result.setAngle(AngleDiff.angle0to360(result.getAngle()));
        testResults().put(result.getAngle(), result);
    }

    public void add(float toAngleGroup, MeridianPower result) {
        result.setAngle(AngleDiff.angle0to360(result.getAngle()));
        testResults().put(toAngleGroup, result);
    }

    public AstigmaticLensParams getFitted() {
        return fitted;
    }

    public void setFitted(AstigmaticLensParams fitted) {
        this.fitted = fitted;
    }

    public AstigmaticLensParams getSoftedCyls() {
        return softedCyls;
    }

    public void setSoftedCyls(AstigmaticLensParams softedCyls) {
        this.softedCyls = softedCyls;
    }

    public AstigmaticLensParams getRounded() {
        return rounded;
    }

    public void setRounded(AstigmaticLensParams rounded) {
        this.rounded = rounded;
    }

    public AstigmaticLensParams getAccepted() {
        return accepted;
    }

    public void setAccepted(AstigmaticLensParams accepted) {
        this.accepted = accepted;
    }

    public int getNumAnglesTested() {
        return testResults.size();
    }

    public String toString() {
        DecimalFormat formatter = new DecimalFormat("0.00");
        return formatter.format(rounded.getSphere()) + " " + formatter.format(rounded.getCylinder()) + " @ " + ((int) (rounded.getAxis()));
    }

    public void addFail() {
        this.fails++;
    }

    public void setFails(int fails) {
        this.fails = fails;
    }

    public int getFails() {
        return this.fails;
    }

    public void setTestResults(FloatHashMap<MeridianPower> data) {
        this.testResults = data;
    }
}
