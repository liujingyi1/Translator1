package com.rgk.android.translator;

import java.util.HashMap;

public class HomeLanguageItem {
    public static final String CODE_TYPE_MICROSOFT = "ms";

    private int iconRes;

    private String languageName;

    private HashMap<String, String> languageCodeMap = new HashMap<>();

    public HomeLanguageItem(int iconRes, String code, String languageName) {
        this.iconRes = iconRes;

        languageCodeMap.put(CODE_TYPE_MICROSOFT, code);
        this.languageName = languageName;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getCode() {
        return languageCodeMap.get(CODE_TYPE_MICROSOFT);
    }

    public String getCode(String type) {
        return languageCodeMap.get(type);
    }

    public void setCode(String code) {
        languageCodeMap.put(CODE_TYPE_MICROSOFT, code);
    }
    public void setCode(String type, String code) {
        languageCodeMap.put(type, code);
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}
