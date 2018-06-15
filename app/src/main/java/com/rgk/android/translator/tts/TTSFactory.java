package com.rgk.android.translator.tts;

import android.app.Activity;

import com.rgk.android.translator.iflytek.tts.FTTTS;
import com.rgk.android.translator.microsoft.tts.MSTTS;


public class TTSFactory {

    public static ITTS createTTS(Activity activity, String type) {

        if ("microsoft".equals(type)) {
            return (ITTS) new MSTTS(activity);
        } else if ("iflytek".equals(type)) {
            return (ITTS) new FTTTS(activity);
        }
        return null;
    }
}
