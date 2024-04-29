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
package com.vitorpamplona.utils;

import java.text.NumberFormat;
import java.util.Arrays;

public class ConsoleProgressBar {
    static final int WIDTH = 50; // size of the bar in chars
    static char[] progBar = new char[WIDTH];

    static char[] signs = {'|', '/', '-', '\\'};
    static int lastRotatingSignIdx = 0;

    public static void printProgBar(double percent) {
        int progressMark = (int) (percent * progBar.length);

        Arrays.fill(progBar, 0, progressMark, '=');

        if (percent < 1) {
            lastRotatingSignIdx = (lastRotatingSignIdx + 1) % signs.length;
            progBar[progressMark] = signs[lastRotatingSignIdx];
            Arrays.fill(progBar, progressMark + 1, progBar.length, ' ');
        }

        System.out.print("\r[" + String.valueOf(progBar) + "]   " + NumberFormat.getPercentInstance().format(percent) + "     ");
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i <= 100; i++) {
            printProgBar(i / 100.0f);
            Thread.sleep(200);
        }
    }
}
