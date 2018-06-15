package com.rgk.android.translator.database.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.rgk.android.translator.database.DbConstants;
import com.rgk.android.translator.database.DatabaseHelper;
import com.rgk.android.translator.database.MessageDao;
import com.rgk.android.translator.database.beans.MessageBean;

import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    public MessageDaoImpl(Context context, DatabaseHelper databaseHelper) {
        mContext = context;
        mDatabaseHelper = databaseHelper;
    }

    @Override
    public MessageBean getMessageById(int id) {
        String sql = "SELECT * FROM message WHERE _id=?;";
        String[] selectionArgs = {String.valueOf(id)};
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        if (cursor == null) {
            return null;
        }
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        MessageBean messageBean = getMessageBean(cursor);
        cursor.close();
        return messageBean;
    }

    @Override
    public List<MessageBean> getMessageByThreadId(int threadId) {
        String sql = "SELECT * FROM message WHERE thread_id=?;";
        String[] selectionArgs = {String.valueOf(threadId)};
        return query(sql, selectionArgs);
    }

    @Override
    public List<MessageBean> getMessageByMemberId(int memberId) {
        String sql = "SELECT * FROM message WHERE member_id=?;";
        String[] selectionArgs = {String.valueOf(memberId)};
        return query(sql, selectionArgs);
    }

    @Override
    public List<MessageBean> getMessageByThreadIdAndMemberId(int threadId, int memberId) {
        String sql = "SELECT * FROM message WHERE thread_id=? AND member_id=?;";
        String[] selectionArgs = {String.valueOf(threadId), String.valueOf(memberId)};
        return query(sql, selectionArgs);
    }

    private List<MessageBean> query(String sql, String[] selectionArgs) {
        SQLiteDatabase database = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(sql, selectionArgs);
        if (cursor == null) {
            return null;
        }
        ArrayList<MessageBean> list = new ArrayList<MessageBean>();
        while (cursor.moveToNext()) {
            MessageBean messageBean = getMessageBean(cursor);
            list.add(messageBean);
        }
        cursor.close();
        return list;
    }

    @NonNull
    private MessageBean getMessageBean(Cursor cursor) {
        MessageBean messageBean = new MessageBean();
        messageBean.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.ID)));
        messageBean.setThreadId(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.THREAD_ID)));
        messageBean.setMemberId(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.MEMBER_ID)));
        messageBean.setDate(cursor.getLong(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.DATE)));
        messageBean.setRead(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.READ)));
        messageBean.setType(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.TYPE)));
        messageBean.setText(cursor.getString(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.TEXT)));
        messageBean.setUrl(cursor.getString(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.URL)));
        messageBean.setLanguage(cursor.getString(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.LANGUAGE)));
        messageBean.setErrorCode(cursor.getInt(
                cursor.getColumnIndexOrThrow(DbConstants.MessageColumns.ERROR_CODE)));
        return messageBean;
    }

    @Override
    public long insert(MessageBean messageBean) {
        ContentValues values = buildContentValues(messageBean);
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        return database.insert(DbConstants.Tables.TABLE_MESSAGE,
                DbConstants.MessageColumns.MEMBER_ID, values);
    }

    @NonNull
    private ContentValues buildContentValues(MessageBean messageBean) {
        ContentValues values = new ContentValues();
        if (messageBean.getThreadId() > 0) {
            values.put(DbConstants.MessageColumns.THREAD_ID, messageBean.getThreadId());
        }
        values.put(DbConstants.MessageColumns.MEMBER_ID, messageBean.getMemberId());
        if (messageBean.getDate() > 0) {
            values.put(DbConstants.MessageColumns.DATE, messageBean.getDate());
        }
        values.put(DbConstants.MessageColumns.READ, messageBean.getRead());
        if (messageBean.getType() >= DbConstants.MessageType.TYPE_SOUND &&
                messageBean.getType() <= DbConstants.MessageType.TYPE_SOUND_TEXT) {
            values.put(DbConstants.MessageColumns.TYPE, messageBean.getType());
        }
        if (messageBean.getText() != null) {
            values.put(DbConstants.MessageColumns.TEXT, messageBean.getText());
        }
        if (messageBean.getUrl() != null) {
            values.put(DbConstants.MessageColumns.URL, messageBean.getUrl());
        }
        values.put(DbConstants.MessageColumns.ERROR_CODE, messageBean.getErrorCode());
        return values;
    }

    @Override
    public int update(MessageBean messageBean) {
        ContentValues values = new ContentValues();
        values.put(DbConstants.MessageColumns.READ, messageBean.getRead());
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        String where = DbConstants.MessageColumns.ID + "=" + messageBean.getId();
        return database.update(DbConstants.Tables.TABLE_MESSAGE,
                values, where, null);
    }

    @Override
    public int delete(MessageBean messageBean) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        String where = DbConstants.MessageColumns.ID + "=" + messageBean.getId();
        return database.delete(DbConstants.Tables.TABLE_MESSAGE, where, null);
    }

    @Override
    public int deleteMessageByThreadId(int threadId) {
        SQLiteDatabase database = mDatabaseHelper.getWritableDatabase();
        String where = DbConstants.MessageColumns.THREAD_ID + "=" + threadId;
        return database.delete(DbConstants.Tables.TABLE_MESSAGE, where, null);
    }
}
