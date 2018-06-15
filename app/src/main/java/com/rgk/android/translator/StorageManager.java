package com.rgk.android.translator;

import android.util.SparseArray;

import com.rgk.android.translator.database.TranslatorStorage;
import com.rgk.android.translator.database.beans.MemberBean;
import com.rgk.android.translator.database.beans.ThreadsBean;
import com.rgk.android.translator.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageManager {
    private static final String TAG = "RTranslator/StorageManager";

    //key: deviceId
    HashMap<String, MemberBean> mMembers = new HashMap<>();
    //key: serverThreadId
    SparseArray<ThreadsBean> mThreads = new SparseArray<>();

    TranslatorStorage mTranslatorStorage;

    private static StorageManager instance;
    private StorageManager() {
        mTranslatorStorage = TranslatorStorage.getInstance();
    }



    public static synchronized StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public ThreadsBean getThreadBean(int serverThreadId) {
        ThreadsBean bean = mThreads.get(serverThreadId);
        if (null != bean) {
            Logger.v(TAG, "find serverThreadId " + serverThreadId);
            return bean;
        } else {
            Logger.v(TAG, "create serverThreadId " + serverThreadId);

            ThreadsBean threadsBean = new ThreadsBean();

            threadsBean.setServerThreadId(serverThreadId);
            //mTranslatorStorage
            mThreads.put(serverThreadId, threadsBean);

            return threadsBean;

        }
    }
}
