package com.rgk.android.translator.database.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.rgk.android.translator.database.DbConstants;
import com.rgk.android.translator.database.DatabaseHelper;
import com.rgk.android.translator.database.ThreadsDao;
import com.rgk.android.translator.database.beans.ThreadsBean;

import java.util.List;

public class ThreadsDaoImpl implements ThreadsDao {
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    public ThreadsDaoImpl(Context context, DatabaseHelper databaseHelper) {
        mContext = context;
        mDatabaseHelper = databaseHelper;
    }
    @Override
    public SparseArray<ThreadsBean> getAllThreads() {
        String sql = "SELECT * FROM threads;";
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, null);
        if (cursor == null) {
            return null;
        }
        SparseArray<ThreadsBean> sparseArray = new SparseArray<>();
        while (cursor.moveToNext()) {
            ThreadsBean threadsBean = getThreadsBean(cursor);
            sparseArray.put(threadsBean.getServerThreadId(), threadsBean);
        }
        cursor.close();
        return sparseArray;
    }

    @NonNull
    private ThreadsBean getThreadsBean(Cursor cursor) {
        ThreadsBean threadsBean = new ThreadsBean();
        threadsBean.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.ID)));
        threadsBean.setServerThreadId(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.SERVER_THREAD_ID)));
        //TODO Init member hashmap
        //threadsBean.setMemberId(cursor.getInt(
        //        cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.MEMBER_ID)));
        threadsBean.setDate(cursor.getLong(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.DATE)));
        threadsBean.setMessageCount(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.MESSAGE_COUNT)));
        threadsBean.setTitle(cursor.getString(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.TITLE)));
        threadsBean.setRead(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.ThreadsColumns.READ)) == 1);
        return threadsBean;
    }

    @Override
    public int delete(ThreadsBean threadsBean) {
        if (threadsBean == null) {
            return 0;
        }
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        String where = DbConstants.ThreadsColumns.ID + "=" + threadsBean.getId();
        return database.delete(DbConstants.Tables.TABLE_THREADS, where, null);
    }

    @Override
    public int delete(List<ThreadsBean> threadsBeans) {
        if (threadsBeans == null || threadsBeans.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        String where = DbConstants.ThreadsColumns.ID + " IN (";
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM threads WHERE ");
        builder.append(where);
        for (ThreadsBean threadsBean : threadsBeans) {
            builder.append(threadsBean.getId());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(");");
        database.execSQL(builder.toString());
        return threadsBeans.size();
    }

    @Override
    public int deleteAllThreads() {
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        return database.delete(DbConstants.Tables.TABLE_THREADS, null, null);
    }
}
