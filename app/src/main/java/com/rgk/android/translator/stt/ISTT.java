package com.rgk.android.translator.stt;

public interface ISTT {
    public enum FinalResponseStatus { NotReceived, OK, Timeout, Finished, Error }
    public void start(String wavFilePath);
    public void setSTTFinishedListener(ISTTFinishedListener listener);
    public void setSTTVoiceLevelListener(ISTTVoiceLevelListener listener);
    public void startWithMicrophone();
    public void stopWithMicrophone();

    void setLanguageCode(String languageCode);

    public void onDestroy();
    public void onResume();
    public void onPause();
}
