package com.rgk.android.translator.database;

import android.util.SparseArray;

import com.rgk.android.translator.database.beans.ThreadsBean;

import java.util.List;

public interface ThreadsDao {
    SparseArray<ThreadsBean> getAllThreads();
    int delete(ThreadsBean threadsBean);
    int delete(List<ThreadsBean> threadsBeans);
    int deleteAllThreads();
}
