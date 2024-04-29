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
package com.vitorpamplona.netra.test.flow;

import com.vitorpamplona.netra.R;
import com.vitorpamplona.netra.test.TestActivity;

public class DoubleTestState extends FullTestState {

    public DoubleTestState(TestActivity activity) {
        super(activity);
    }

    public void playIntermidiarySongs(Stage currentStage, int currentStep) {
        mActivity.getAudioPlayer().playSfx(R.raw.next);

        switch (currentStage) {
            case ANGLE_LEFT:
                switch (currentStep) {
                    case 2:
                        mActivity.getAudioPlayer().playSpeech(R.raw.excellent);
                        break;
                    case 3:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 4:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                    case 5:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 6:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                    case 7:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great2);
                        break;
                    case 8:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;

                    case 9:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_6_excellent_quarter_of_way_done);
                        break;

                    case 10:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                    case 11:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great2);
                        break;
                    case 12:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 13:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_please_continue);
                        break;
                    case 14:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great2);
                        break;
                    case 15:
                        mActivity.getAudioPlayer().playSpeech(R.raw.excellent);
                        break;
                    case 16:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                }
                break;
            case ANGLE_RIGHT:
                switch (currentStep) {
                    case 2:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great);
                        break;
                    case 3:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 4:
                        mActivity.getAudioPlayer().playSpeech(R.raw.please_continue);
                        break;
                    case 5:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great);
                        break;
                    case 6:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 7:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_job);
                        break;
                    case 9:
                        mActivity.getAudioPlayer().playSpeech(R.raw.excellent);
                        break;
                    case 8:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_8_three_quarters);
                        break;
                    case 10:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great2);
                        break;
                    case 11:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 12:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_please_continue);
                        break;
                    case 13:
                        mActivity.getAudioPlayer().playSpeech(R.raw.great_job);
                        break;
                    case 14:
                        mActivity.getAudioPlayer().playSpeech(R.raw.good_job);
                        break;
                    case 15:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_9_almost_done);
                        break;
                    case 16:
                        mActivity.getAudioPlayer().playSpeech(R.raw.exam_10_last_one);
                        break;
                }
                break;
            default:
                break;
        }

    }
}
