package com.rgk.android.translator.tts;

public class TTSUtils {

    public static boolean isIflytekSupport(String language, String male) {
        if ("en-US".equals(language) || "zh-CN".equals(language) || "zh-TW".equals(language)
                || "zh-HK".equals(language) || "es-ES".equals(language) || "hi-IN".equals(language)
                || "vi-VN".equals(language) || "ru-RU".equals(language) || "fr-FR".equals(language)) {
            return true;
        }
        return false;
    }
}

