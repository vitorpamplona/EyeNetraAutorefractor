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
package com.vitorpamplona.netra.audio;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.CountDownTimer;

import com.vitorpamplona.netra.activity.NetraGApplication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

public class AudioPlayer {

    //TODO:relative volume settings for sfx, speech\

    public enum ConflictResolution {
        INTERRUPT, WAIT;
    }

    protected Context mContext;

    protected SoundPool mSfx;
    protected Map<Integer, Integer> mSfxIds;

    protected MediaPlayer mCurrentSpeech;
    protected Queue<MediaPlayer> mSpeechQueue = new LinkedList<MediaPlayer>();//TODO here?
    protected CountDownTimer mSpeechDelay;

    protected OnCompletionListener mSpeechCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.release();
            if (!mSpeechQueue.isEmpty()) {
                mCurrentSpeech = mSpeechQueue.remove();
                mCurrentSpeech.start();
            } else {
                mCurrentSpeech = null;
            }
        }
    };

    public AudioPlayer(Context context) {
        mContext = context;
    }

    public void initializeAll() {
        mSfx = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSfxIds = new HashMap<Integer, Integer>();

        mSpeechQueue = new LinkedList<MediaPlayer>();
    }

    public void releaseAll() {
        mSfx.release();
    }

    public void playSfx(int rawId) {
        if (!mSfxIds.containsKey(rawId)) {
            int poolId = mSfx.load(mContext, rawId, 1);
            mSfxIds.put(rawId, poolId);
            mSfx.setOnLoadCompleteListener(new OnLoadCompleteListener() {

                @Override
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    soundPool.play(sampleId, 1, 1, 0, 0, 1);
                }
            });
        } else {
            mSfx.play(mSfxIds.get(rawId), 1, 1, 0, 0, 1);
        }
    }

    public void pushPatientLocale() {
        Resources res = mContext.getResources();
        Configuration conf = res.getConfiguration();
        Locale savedLocale = conf.locale;
        conf.locale = NetraGApplication.get().getSettings().getPatientLocale(); // whatever you want here
        res.updateConfiguration(conf, null); // second arg null means don't change
    }

    public void restorePatientLocale() {
        // restore original locale
        Resources res = mContext.getResources();
        Configuration conf = res.getConfiguration();
        conf.locale = NetraGApplication.get().getSettings().getAppLocale();
        res.updateConfiguration(conf, null);
    }

    public void playSpeech(int rawId) {
        playSpeech(rawId, ConflictResolution.INTERRUPT);
    }

    public void playSpeech(int rawId, int delay) {
        playSpeech(rawId, ConflictResolution.INTERRUPT, delay);
    }

    public void queueSpeech(int rawId) {
        playSpeech(rawId, ConflictResolution.WAIT);
    }

    public void playSpeech(int rawId, ConflictResolution cs) {
        pushPatientLocale();
        MediaPlayer mp = MediaPlayer.create(mContext, rawId);
        restorePatientLocale();

        // Some failure.
        if (mp == null) return;

        mp.setOnCompletionListener(mSpeechCompletionListener);

        if (mCurrentSpeech == null) {
            mCurrentSpeech = mp;
            mCurrentSpeech.start();
        } else {
            switch (cs) {
                case INTERRUPT:
                    if (mSpeechDelay != null) mSpeechDelay.cancel();
                    mSpeechQueue.clear();
                    mCurrentSpeech.stop();
                    mCurrentSpeech.release();
                    mCurrentSpeech = mp;
                    mCurrentSpeech.start();
                    break;
                case WAIT:
                    mSpeechQueue.add(mp);
                    break;
            }
        }
    }

    public void playSpeech(int rawId, ConflictResolution cs, int delay) {
        final int finalRawId = rawId;
        final ConflictResolution finalCs = cs;

        mSpeechDelay = new CountDownTimer(delay, delay) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                pushPatientLocale();
                playSpeech(finalRawId, finalCs);
                restorePatientLocale();
            }
        }.start();
    }

    public void stopAll() {
        mSpeechQueue.clear();
        if (mCurrentSpeech != null) {
            mCurrentSpeech.stop();
            mCurrentSpeech.release();
            mCurrentSpeech = null;
        }
    }
}
