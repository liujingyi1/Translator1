package com.rgk.android.translator.translate;

import com.rgk.android.translator.database.beans.MessageBean;

public interface ITranslate {
    void setTranslateFinishedListener(ITranslateFinishedListener listener);

    void doTranslate(MessageBean messageBean, String targetLanguage, int i);
}
