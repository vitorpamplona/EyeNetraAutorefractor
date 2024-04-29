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
package com.vitorpamplona.netra.test;

import com.vitorpamplona.core.testdevice.DeviceDataset;
import com.vitorpamplona.netra.activity.NetraGApplication;
import com.vitorpamplona.netra.model.ExamResults;
import com.vitorpamplona.netra.model.Prescription;
import com.vitorpamplona.netra.model.RecommendedUseType;
import com.vitorpamplona.netra.model.RefractionType;
import com.vitorpamplona.netra.model.db.objects.DebugExam;
import com.vitorpamplona.netra.printer.ResultsFormatter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = com.vitorpamplona.netra.activity.NetraGTestApplication.class)
public class ResultsFormatterTest {

    @Test
    public void testFormatter() {
        ResultsFormatter formatter = new ResultsFormatter(NetraGApplication.get().getBaseContext());

        ExamResults e = new ExamResults();
        e.setId(UUID.randomUUID());
        e.setExamDate(new Date());
        e.setDevice(DeviceDataset.get(404));
        if (e.getSequenceNumber() == 0) {
            e.setSequenceNumber(1);
        }
        e.setEnvironment("NETRA 2");
        e.setShareWith("User To Share");
        e.setUserToken("User Token");
        e.setUserName("User Name");
        e.setAppVersion("1.60");

        e.setLongitude(46.0000);
        e.setLatitude(46.0000);

        Prescription rRE = e.getRightEye().getOrCreate(RefractionType.NETRA);
        rRE.set(-2.00f, -0.50f, 180f, +1.00f, 0.8f, 31f);

        Prescription rLE = e.getLeftEye().getOrCreate(RefractionType.NETRA);
        rLE.set(-2.50f, -0.75f, 170f, +1.00f, 0.8f, 31f);

        Prescription sRE = e.getRightEye().getOrCreate(RefractionType.SUBJECTIVE);
        sRE.set(-2.25f, -0.50f, 180f, +1.50f, 1f, 31.5f);

        Prescription sLE = e.getLeftEye().getOrCreate(RefractionType.SUBJECTIVE);
        sLE.set(-2.25f, -0.50f, 180f, +1.50f, 1f, 31.5f);

        DebugExam e1 = new DebugExam(e);

        e1.setPrescriptionEmail("vitor@eyenetra.com");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 32);
        e1.setDateOfBirth(c.getTime());

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.YEAR, c2.get(Calendar.YEAR) + 2);
        e1.setPrescriptionExpiration(c2.getTime());

        List<RecommendedUseType> arr = new ArrayList<RecommendedUseType>();
        arr.add(RecommendedUseType.CONSTANT_USE);
        e1.setPrescriptionRecommendedUse(arr);

        for (String line : formatter.getFormattedResults(e1)) {
            System.out.println(line);
        }
    }

}
