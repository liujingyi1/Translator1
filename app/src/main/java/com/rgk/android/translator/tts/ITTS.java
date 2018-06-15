package com.rgk.android.translator.tts;

public interface ITTS {
    void speak(String text,String fileName,String filePath);
    void pause();
    void resume();
    void stop();
    void release();
    String synthesizeToUri(String text, String fileName, String filePath);
    void setVoice(String language, boolean male, boolean isCloudServiceVoice);
    byte[] getSpeak(String text);
    void setTTSEventListener(ITTSListener ittsListener);
}
