package com.rgk.android.translator;

import android.app.Application;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;

import com.rgk.android.translator.database.TranslatorStorage;

public class TranslatorApp extends Application {
    public StorageManager mStorageManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //这个要放在第一个
        TranslatorStorage.init(this);

        mStorageManager = StorageManager.getInstance();
    }
}
