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
package com.vitorpamplona.netra.utils;

import com.vitorpamplona.core.utils.AngleDiff;
import com.vitorpamplona.netra.model.db.objects.Refraction;

public class PrescriptionParser {

    private String correctSigns(String param) {
        return param.replaceAll("one", "1")
                .replaceAll("two", "2")
                .replaceAll("three", "3")
                .replaceAll("four", "4")
                .replaceAll("five", "5")
                .replaceAll("six", "6")
                .replaceAll("seven", "7")
                .replaceAll("eight", "8")
                .replaceAll("nine", "9")
                .replaceAll("ten", "10")
                .replaceAll("eleven", "11")
                .replaceAll("twelve", "12")
                .replaceAll("thirteen", "13")
                .replaceAll("fourteen", "14")
                .replaceAll("fifteen", "15")
                .replaceAll("sixteen", "16")
                .replaceAll("seventeen", "17")
                .replaceAll("eightteen", "18")
                .replaceAll("nineteen", "19")
                .replaceAll("twenty", "20")
                .replaceAll("minus", "-")
                .replaceAll("plus", "+")
                .replaceAll("\\+ ", "+")
                .replaceAll("- ", "-");
    }

    /**
     * try reading a presciption
     *
     *   [sph] [cyl] at [axis] [sph] [cyl] at [axis] [pdmm]
     *
     * @param args
     * @return
     */
    public Refraction parseLeftEye(String args) {
        Refraction r = new Refraction();
        parseLeftEyeInto(args, r);
        return r;
    }

    public void parseLeftEyeInto(String args, Refraction r) {
        String[] splited = correctSigns(args).split(" ");

        for (int i = 0; i < splited.length; i++) {

            if (r.getLeftSphere() == null) {
                r.setLeftSphere(readSphere(splited, i));
                continue;
            }
            if (r.getLeftCylinder() == null) {
                r.setLeftCylinder(readCylinder(splited, i));
                if (r.getLeftCylinder() != null && Math.abs(r.getLeftCylinder()) < 0.05) {
                    r.setLeftAxis(0f);
                }
                continue;
            }

            if (r.getLeftAxis() == null) {
                r.setLeftAxis(readAxis(splited, i));
                continue;
            }

            if (r.getRightSphere() == null) {
                r.setRightSphere(readSphere(splited, i));
                continue;
            }

            if (r.getRightCylinder() == null) {
                r.setRightCylinder(readCylinder(splited, i));
                if (r.getRightCylinder() != null && Math.abs(r.getRightCylinder()) < 0.05) {
                    r.setRightAxis(0f);
                }
                continue;
            }

            if (r.getRightAxis() == null) {
                r.setRightAxis(readAxis(splited, i));
                continue;
            }
        }
    }

    public Refraction parseRightEye(String args) {
        Refraction r = new Refraction();
        parseRightEyeInto(args, r);
        return r;
    }

    public void parseRightEyeInto(String args, Refraction r) {
        String[] splited = correctSigns(args).split(" ");

        for (int i = 0; i < splited.length; i++) {

            if (r.getRightSphere() == null) {
                r.setRightSphere(readSphere(splited, i));
                continue;
            }

            if (r.getRightCylinder() == null) {
                r.setRightCylinder(readCylinder(splited, i));
                if (r.getRightCylinder() != null && Math.abs(r.getRightCylinder()) < 0.05) {
                    r.setRightAxis(0f);
                }
                continue;
            }

            if (r.getRightAxis() == null) {
                r.setRightAxis(readAxis(splited, i));
                continue;
            }

            if (r.getLeftSphere() == null) {
                r.setLeftSphere(readSphere(splited, i));
                continue;
            }
            if (r.getLeftCylinder() == null) {
                r.setLeftCylinder(readCylinder(splited, i));
                if (r.getLeftCylinder() != null && Math.abs(r.getLeftCylinder()) < 0.05) {
                    r.setLeftAxis(0f);
                }
                continue;
            }

            if (r.getLeftAxis() == null) {
                r.setLeftAxis(readAxis(splited, i));
                continue;
            }
        }
    }

    public Float readSphere(String[] splited, int index) {
        try {

            if (splited[index].contains("plano"))
                return 0.0f;

            float tentative = Float.parseFloat(splited[index]);
            if (Math.abs(tentative) > 20) {
                tentative = tentative / 100;
            }

            // 0.15 to 0.50
            if (Math.abs(tentative) > 0.14 && Math.abs(tentative) < 0.16) {
                if (tentative < 0)
                    tentative = -0.50f;
                else
                    tentative = 0.50f;
            }

            return tentative;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Float readCylinder(String[] splited, int index) {
        try {
            if (splited[index].contains("sphere") || splited[index].contains("spherical"))
                return 0.0f;

            float tentative = Float.parseFloat(splited[index]);
            if (Math.abs(tentative) > 20) {
                tentative = tentative / 100;
            }

            // 0.15 to 0.50
            if (Math.abs(tentative) > 0.14 && Math.abs(tentative) < 0.16) {
                if (tentative < 0)
                    tentative = -0.50f;
                else
                    tentative = 0.50f;
            }

            return tentative;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Float readAxis(String[] splited, int index) {
        try {

            float tentative = Float.parseFloat(splited[index]);
            return AngleDiff.angle0to180(tentative);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
