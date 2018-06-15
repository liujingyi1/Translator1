package com.rgk.android.translator.stt;

import android.app.Activity;

import com.rgk.android.translator.iflytek.stt.FTSTT;
import com.rgk.android.translator.microsoft.stt.MSSTT;

public class STTFactory {

    public static ISTT createSTT(Activity activity, String type) {

        if ("microsoft".equals(type)) {
            return new MSSTT(activity);
        } else if ("iflytek".equals(type)) {
            return new FTSTT(activity);
        }

        return null;
    }
}
