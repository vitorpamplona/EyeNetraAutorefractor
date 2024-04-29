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
package com.vitorpamplona.netra.printer;

import static android.text.format.DateFormat.getDateFormat;
import static android.text.format.DateFormat.getTimeFormat;

import android.content.Context;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.model.Prescription;
import com.vitorpamplona.netra.model.RefractionType;
import com.vitorpamplona.netra.model.db.objects.DebugExam;
import com.vitorpamplona.netra.model.db.objects.Refraction;
import com.vitorpamplona.netra.utils.AgeCalculator;

import java.nio.CharBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ResultsFormatter {
    public static final int SIZE_PAPER = 32;

    private DecimalFormat sphereCylFormatter = new DecimalFormat("+0.00;-0.00");
    private DecimalFormat mPdFormatter = new DecimalFormat("0.0");

    private DateFormat dateFormatter;
    private DateFormat timeFormatter;

    private Context context;

    public ResultsFormatter(Context context) {
        this.context = context;
        this.dateFormatter = getDateFormat(context.getApplicationContext());
        this.timeFormatter = getTimeFormat(context.getApplicationContext());

        if (dateFormatter == null) {
            dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        }
        if (timeFormatter == null) {
            timeFormatter = new SimpleDateFormat("HH:mm");
        }
    }

    public Integer getFormattedAge(DebugExam table) {
        return AgeCalculator.calculateAge(table.getDateOfBirth());
    }

    private boolean isNegativeCylModel() {
        return NetraGApplication.get().getSettings().isNegativeCylModel();
    }

    public List<String> getFormattedResults(DebugExam nextToPrint2) {
        ArrayList lines = new ArrayList();

        if (nextToPrint2 == null) return lines;

        Refraction ref = nextToPrint2.getRefraction(RefractionType.NETRA);
        Refraction subj = nextToPrint2.getRefraction(RefractionType.SUBJECTIVE);

        if (isNegativeCylModel()) {
            if (ref != null) ref.putInNegativeCilinder();
            if (subj != null) subj.putInNegativeCilinder();
        } else {
            if (ref != null) ref.putInPositiveCilinder();
            if (subj != null) subj.putInPositiveCilinder();
        }

        String notes = nextToPrint2.getStudyName();
        String age = null;
        if (nextToPrint2.getDateOfBirth() != null) {
            age = dateFormatter.format(nextToPrint2.getDateOfBirth());
        }

        if (nextToPrint2.getDateOfBirth() != null)
            notes = nextToPrint2.getStudyName() + " - " + getFormattedAge(nextToPrint2) + "y";

        if (notes == null || notes.isEmpty()) {
            notes = context.getResources().getString(R.string.number_prefix_reading_card_empty_note) + nextToPrint2.getSequenceNumber();
        }

        String odNetra = context.getResources().getString(R.string.right_eye_short);
        String osNetra = context.getResources().getString(R.string.left_eye_short);

        String odSubj = context.getResources().getString(R.string.right_eye_short);
        String osSubj = context.getResources().getString(R.string.left_eye_short);

        String date = "" + centralize(dateFormatter.format(nextToPrint2.getTested()) + " "
                + timeFormatter.format(nextToPrint2.getTested()), SIZE_PAPER);

        String titleNetra = "    "
                + context.getResources().getString(R.string.sph) + "   "
                + context.getResources().getString(R.string.cyl) + "   "
                + centralize(context.getResources().getString(R.string.axis_label), 4) + " |  "
                + context.getResources().getString(R.string.result_add) + " ";


        String titleSubj = "    "
                + context.getResources().getString(R.string.sph) + "   "
                + context.getResources().getString(R.string.cyl) + "   "
                + centralize(context.getResources().getString(R.string.axis_label), 4) + " |  "
                + context.getResources().getString(R.string.result_add) + " ";

        if (isRightValid(ref))
            odNetra = odNetra + " "
                    + formatSphere(ref.getRightSphere()) + " " +
                    formatCylinder(ref.getRightCylinder()) + " @ " +
                    formatAxis(ref.getRightCylinder(), ref.getRightAxis()) + " | " +
                    formatAdd(ref.getRightAdd());
        else
            odNetra = odNetra + " " + " -.--" + " " +
                    " -.--" + " @ " +
                    "---" + " | " +
                    " ---";

        if (isLeftValid(ref))
            osNetra = osNetra + " "
                    + formatSphere(ref.getLeftSphere()) + " " +
                    formatCylinder(ref.getLeftCylinder()) + " @ " +
                    formatAxis(ref.getLeftCylinder(), ref.getLeftAxis()) + " | " +
                    formatAdd(ref.getLeftAdd());
        else
            osNetra = osNetra + " " + " -.--" + " " +
                    " -.--" + " @ " +
                    "---" + " | " +
                    " ---";

        if (isRightValid(subj))
            odSubj = odSubj + " "
                    + formatSphere(subj.getRightSphere()) + " " +
                    formatCylinder(subj.getRightCylinder()) + " @ " +
                    formatAxis(subj.getRightCylinder(), subj.getRightAxis()) + " | " +
                    formatAdd(subj.getRightAdd());
        else
            odSubj = odSubj + " " + " -.--" + " " +
                    " -.--" + " @ " +
                    "---" +
                    "-.--";

        if (isLeftValid(subj))
            osSubj = osSubj + " "
                    + formatSphere(subj.getLeftSphere()) + " " +
                    formatCylinder(subj.getLeftCylinder()) + " @ " +
                    formatAxis(subj.getLeftCylinder(), subj.getLeftAxis()) + " | " +
                    formatAdd(subj.getLeftAdd());

        else
            osSubj = osSubj + " " + " -.--" + " " +
                    " -.--" + " @ " +
                    "---";

        int max = Math.max(titleNetra.length(), Math.max(Math.max(osNetra.length(), odNetra.length()), Math.max(osSubj.length(), odSubj.length())));
        int centerBlock = (SIZE_PAPER - max) / 2;

        String leftSpace = spaces(centerBlock);

        if (notes != null && !notes.isEmpty())
            lines.add(centralize(notes, SIZE_PAPER));

        if (age != null) {
            lines.add(centralize("DoB: " + age, SIZE_PAPER));
        }
        if (nextToPrint2.getPrescriptionEmail() != null) {
            lines.add(centralize(nextToPrint2.getPrescriptionEmail(), SIZE_PAPER));
        }
        if (nextToPrint2.getPrescriptionPhone() != null) {
            try {
                Phonenumber.PhoneNumber number = PhoneNumberUtil.getInstance().parse(nextToPrint2.getPrescriptionPhone(), null);
                lines.add(centralize(PhoneNumberUtil.getInstance().format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL), SIZE_PAPER));
            } catch (Exception e) {
                lines.add(centralize(nextToPrint2.getPrescriptionPhone(), SIZE_PAPER));
            }
        }

        lines.add("\n");

        if (subj != null) {
            if (ref != null && ref.hasAnything()) {
                lines.add(centralize(context.getResources().getString(R.string.subj_refraction), SIZE_PAPER));
                lines.add(" ");
            }
            lines.add(leftSpace + titleSubj);
            lines.add(leftSpace + odSubj);
            lines.add(leftSpace + osSubj);
            lines.add(" ");
            lines.add(leftSpace + context.getResources().getString(R.string.pd_label2) + " " + formatPd(subj.getLeftPd(), subj.getRightPd()));
            lines.add(" ");
        }

        if (ref != null && ref.hasAnything()) {
            if (subj != null) {
                lines.add(centralize(context.getResources().getString(R.string.netra_results), SIZE_PAPER));
                lines.add(" ");
            }
            lines.add(leftSpace + titleNetra);
            lines.add(leftSpace + odNetra);
            lines.add(leftSpace + osNetra);
            lines.add(" ");
            lines.add(leftSpace + context.getResources().getString(R.string.pd_label2) + " " + formatPd(ref.getLeftPd(), ref.getRightPd()));
            lines.add(" ");
        }

        lines.add(date);

        lines.add("\n");

        if (ref != null) ref.putInNegativeCilinder();
        if (subj != null) subj.putInNegativeCilinder();

        return lines;
    }

    public boolean isValid(Prescription p) {
        return (p.getSphere() != null);
    }

    public boolean isLeftValid(Refraction r) {
        return r != null && (r.getLeftSphere() != null);
    }

    public boolean isRightValid(Refraction r) {
        return r != null && (r.getRightSphere() != null);
    }

    public String spaces(int spaces) {
        return CharBuffer.allocate(spaces).toString().replace('\0', ' ');
    }

    public String centralize(String text, int size) {
        float sideSize = (size - text.length()) / 2;
        if (sideSize < 1) return text;

        boolean addSpace = text.length() % 2 > 0 ^ size % 2 > 0;
        boolean addSpaceRight = addSpace && text.length() % 2 > 0;
        boolean addSpaceLeft = addSpace && size % 2 > 0;
        String side = spaces((int) sideSize);
        return side + (addSpaceLeft ? " " : "") + text + (addSpaceRight ? " " : "") + side;
    }

    protected String formatPd(Float right, Float left) {
        if (right == null || left == null) return "-.--";
        return mPdFormatter.format((double) right + left) + "mm";
    }

    protected String formatSphere(Float f) {
        if ((f == null || f > 98)) {
            return " -.--";
        } else if (Math.abs(f) < 0.01) {
            return " 0.00"; // remove signal.
        } else
            return sphereCylFormatter.format(f);
    }

    protected String formatAdd(Float f) {
        if (f == null) return "-.--";
        if (f > 98) return "-.--";
        if (Math.abs(f) < 0.01) return " 0.00";
        return sphereCylFormatter.format(f);
    }

    protected String formatCylinder(Float f) {
        if (f == null) return " -.--";
        if (Math.abs(f.floatValue()) < 0.01) return " 0.00";

        return sphereCylFormatter.format(f == null ? 0 : f);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    protected String formatAxis(Float cyl, Float axis) {
        if (cyl == null) return "---";
        if (Math.abs(cyl.floatValue()) < 0.01) return "---";

        float fl = axis == null ? 0 : axis;
        if (Math.abs(fl) < 0.0001)
            fl = 180;

        return padLeft(Integer.toString((int) fl), 3);
    }
}
