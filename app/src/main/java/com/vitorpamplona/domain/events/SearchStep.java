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
package com.vitorpamplona.domain.events;

import com.vitorpamplona.core.models.AstigmaticLensParams;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

public class SearchStep extends Event implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public float angle;
    public float power;
    public AstigmaticLensParams resultingIn2;

    public SearchStep() {
    }

    public SearchStep(Date when, float angle, float power, AstigmaticLensParams resultingIn) {
        super(when);
        this.angle = angle;
        this.power = power;
        this.resultingIn2 = resultingIn;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public AstigmaticLensParams getResultingIn() {
        return resultingIn2;
    }

    public void setResultingIn(AstigmaticLensParams resultingIn) {
        this.resultingIn2 = resultingIn;
    }

    public String getDesc() {
        return "Power " + ((int) angle) + "º: " + new DecimalFormat("0.00").format(power) + "D";
    }
}
