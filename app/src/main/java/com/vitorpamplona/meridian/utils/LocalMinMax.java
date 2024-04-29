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
package com.vitorpamplona.meridian.utils;

import java.util.ArrayList;
import java.util.List;


public class LocalMinMax {
    private int delta;  // peak threshold
    private Integer[] v;
    List<Integer> maxtab = new ArrayList<Integer>();  // local maxima values
    List<Integer> mintab = new ArrayList<Integer>();  // local minima values
    List<Integer> maxtabindex = new ArrayList<Integer>();  // local maxima indices (of input array)
    List<Integer> mintabindex = new ArrayList<Integer>();  // local minima indices (of input array)


    public LocalMinMax(Integer[] v, int delta) {
        this.v = v;
        this.delta = delta;
        calcpeaks();
    }

    // peakdetect algorithm recoded from an old Matlab code (probably not the Java way, but works for now)
    private void calcpeaks() {
        int mn = Integer.MAX_VALUE;
        int mx = Integer.MIN_VALUE;
        int mnpos = -1;
        int mxpos = -1;
        boolean lookformax = true;
        int dis = 0;

        for (int i = 0; i < v.length; ++i) {
            dis = v[i];
            if (dis > mx) {
                mx = dis;
                mxpos = i;
            }
            if (dis < mn) {
                mn = dis;
                mnpos = i;
            }

            if (lookformax) {
                if (dis < mx - delta) {
                    maxtab.add(mx);
                    maxtabindex.add(mxpos);
                    mn = dis;
                    mnpos = i;
                    lookformax = false;
                }
            } else {
                if (dis > mn + delta) {
                    mintab.add(mx);
                    mintabindex.add(mnpos);
                    mx = dis;
                    mxpos = i;
                    lookformax = true;
                }
            }
        }
    }


    public Integer[] getMaxtab() {
        return maxtab.toArray(new Integer[maxtab.size()]);
    }

    public Integer[] getMintab() {
        return mintab.toArray(new Integer[mintab.size()]);
    }

    public Integer[] getMaxtabIndex() {
        return maxtabindex.toArray(new Integer[maxtabindex.size()]);
    }

    public Integer[] getMintabIndex() {
        return mintabindex.toArray(new Integer[mintabindex.size()]);
    }

}
