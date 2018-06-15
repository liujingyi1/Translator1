package com.rgk.android.translator.mpush;

import android.util.Log;

import com.mpush.api.http.HttpResponse;

public interface HttpProxyCallback {
    public void onResponse(HttpResponse httpResponse);
    public void onCancelled();
}
