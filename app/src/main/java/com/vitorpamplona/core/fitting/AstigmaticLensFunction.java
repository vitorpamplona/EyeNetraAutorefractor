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
package com.vitorpamplona.core.fitting;

import com.vitorpamplona.core.models.SinusoidalFunction;

/**
 * The sin2 function we best fit over the data. 
 * Used for the Jama Library.
 */
public class AstigmaticLensFunction implements LMfunc {
    public static final int SPHERICAL = 0;
    public static final int CYLINDRICAL = 1;
    public static final int AXIS = 2;

    public AstigmaticLensFunction() {
    }

    /**
     * Power(angle) = Sphere + Cylinder * sin2(Axis - angle)
     */
    public double val(double[] x, double[] a) {
        return SinusoidalFunction.interpolate(a[SPHERICAL], a[CYLINDRICAL], a[AXIS], x[0]);
    }

    public double grad(double[] x, double[] a, int a_k) {
        if (a_k == CYLINDRICAL)
            return Math
                    .pow(Math.sin(Math.toRadians(a[AXIS])
                            - Math.toRadians(x[0])), 2);

        else if (a_k == AXIS)
            return a[CYLINDRICAL]
                    * Math.sin(2 * (Math.toRadians(a[AXIS]) - Math
                    .toRadians(x[0])));

        else if (a_k == SPHERICAL)
            return 1;

        else {
            assert false;
            return 0.;
        }
    } // grad

    public double[] initial() {
        double[] ret = new double[3];
        ret[SPHERICAL] = -5;
        ret[CYLINDRICAL] = -2;
        ret[AXIS] = 20;
        return ret;
    }

    public Object[] testdata() {
        double[] parameters = new double[3];
        parameters[SPHERICAL] = -3;
        parameters[CYLINDRICAL] = -1;
        parameters[AXIS] = 10;

        int nAngles = 11;
        double[][] angle = new double[nAngles][1];
        double[] correction = new double[nAngles];
        double[] s = new double[nAngles];
        for (int i = 0; i < nAngles; i++) {
            angle[i][0] = 18 * (double) i / nAngles;
            correction[i] = val(angle[i], parameters);
            s[i] = 1.;
        }

        Object[] o = new Object[4];
        o[0] = angle;
        o[1] = parameters;
        o[2] = correction;
        o[3] = s;

        return o;
    } // test
}
