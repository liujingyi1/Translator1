package com.rgk.android.translator.stt;

import com.rgk.android.translator.stt.ISTT;

public interface ISTTFinishedListener {
    void onSTTFinish(ISTT.FinalResponseStatus status, String text);
}
