package com.rgk.android.translator.translate;

import com.rgk.android.translator.database.beans.MessageBean;

public interface ITranslateFinishedListener {
    void onTranslateFinish(MessageBean messageBean, int i);
}
