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

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {

    SUCCESS(0),
    UNDEFINED(-1),
    DEVICE_OUTSIDE_SHELL(-3),
    NOT_ENOUGH_LIGHT(-4),

    // Calibration
    CAL_STATIC_NO_SUCCESS(-10),
    CAL_CANT_FIND_INITIAL(-11),

    // Slider (SLC: component, SLF: finder)
    SLC_NOT_READY(-20),
    SLC_CHANGE_TRIGGER(-21),
    SLF_POINTS_NULL(-22),
    SLF_NO_DOT_FOUND(-23),

    // Ratchet (RAC: component, RAF: finder)
    RAC_NOT_READY(-30),
    RAC_NULL_NAN(-31),
    // RAC_CHANGE_TRIGGER   (-32),  Not used anymore.
    RAF_MIN_MAX_ERROR(-33),
    RAF_NO_DOTS(-34),
    RAF_ONE_DOT(-35),
    RAF_MORE_THAN_4_DOTS(-36),
    RAF_STD_TOO_HIGH(-37),
    RAF_ANGLE_IS_NULL(-38),
    RAF_ANGLE_IS_NAN(-39),
    RAF_NOT_1_OR_2_DOTS(-40),
    RAF_ANGLE_NOT_VALID(-41),
    RAF_DEVICE_NOT_SUPPOR(-42),
    RAF_NULL_INDEX_REFINE(-43),
    RAF_INVALID_ANGLE(-44),
    RAF_NO_ACCURA_ANGLES(-45),
    RAF_NOT_ENOUGH_INDEX(-46),
    RAF_NOT_ENOUGH_SATEL(-47),
    RAF_CENTER_IS_OFF(-48),
    RAF_MOVE_TOO_MUCH(-49),
    RAF_MOVE_BACKWARDS(-50),

    // Scrolly (SCC: component, SCF: finder)
    SCC_NOT_READY(-60),
    SCC_NULL_NAN(-61),
    // SCC_CHANGE_TRIGGER   (-62), Not used anymore.
    SCF_ANGLE_IS_NULL(-63),
    SCF_ANGLE_IS_NAN(-64),
    SCF_NO_DOT_FOUND(-65),

    // Intermediate cases for shared component code
    C_NOT_READY(-80),
    // C_CHANGE_TRIGGER 	 (-81), Not used anymore.
    C_NULL_NAN(-82);

    public final int errorCode;

    // Map the integer values to the enum error codes
    private static Map<Integer, ErrorCode> map = new HashMap<Integer, ErrorCode>();

    static {
        for (ErrorCode codeEnum : ErrorCode.values()) {
            map.put(codeEnum.errorCode, codeEnum);
        }
    }

    // Constructor
    ErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    // Get code from int value
    public static ErrorCode valueOf(int legNo) {
        return map.get(legNo);
    }

}