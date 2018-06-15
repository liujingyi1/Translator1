package com.rgk.android.translator.database;

import android.content.Context;
import android.util.SparseArray;

import com.rgk.android.translator.database.beans.MemberBean;
import com.rgk.android.translator.database.beans.MessageBean;
import com.rgk.android.translator.database.beans.ThreadsBean;
import com.rgk.android.translator.database.beans.UserBean;
import com.rgk.android.translator.database.impl.MemberDaoImpl;
import com.rgk.android.translator.database.impl.MessageDaoImpl;
import com.rgk.android.translator.database.impl.ThreadsDaoImpl;
import com.rgk.android.translator.database.impl.UserDaoImpl;

import java.util.List;

public class TranslatorStorage implements UserDao,
        MemberDao, MessageDao, ThreadsDao {
    private static TranslatorStorage mInstance;
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;

    private UserDao mUserDao;
    private MemberDao mMemberDao;
    private MessageDao mMessageDao;
    private ThreadsDao mThreadsDao;

    private TranslatorStorage(Context context) {
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
        mDatabaseHelper.getReadableDatabase();
        mUserDao = new UserDaoImpl(context, mDatabaseHelper);
        mMemberDao = new MemberDaoImpl(context, mDatabaseHelper);
        mMessageDao = new MessageDaoImpl(context, mDatabaseHelper);
        mThreadsDao = new ThreadsDaoImpl(mContext, mDatabaseHelper);
    }

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new TranslatorStorage(context);
        }
    }

    public static TranslatorStorage getInstance() {
        return mInstance;
    }

    @Override
    public MessageBean getMessageById(int id) {
        return mMessageDao.getMessageById(id);
    }

    @Override
    public List<MessageBean> getMessageByThreadId(int threadId) {
        return mMessageDao.getMessageByThreadId(threadId);
    }

    @Override
    public List<MessageBean> getMessageByMemberId(int memberId) {
        return mMessageDao.getMessageByMemberId(memberId);
    }

    @Override
    public List<MessageBean> getMessageByThreadIdAndMemberId(int threadId, int memberId) {
        return mMessageDao.getMessageByThreadIdAndMemberId(threadId, memberId);
    }

    @Override
    public long insert(MessageBean messageBean) {
        return mMessageDao.insert(messageBean);
    }

    @Override
    public int update(MessageBean messageBean) {
        return mMessageDao.update(messageBean);
    }

    @Override
    public int delete(MessageBean messageBean) {
        return mMessageDao.delete(messageBean);
    }

    @Override
    public int deleteMessageByThreadId(int threadId) {
        return mMessageDao.deleteMessageByThreadId(threadId);
    }

    @Override
    public UserBean getUser() {
        return mUserDao.getUser();
    }

    @Override
    public UserBean getUserById(int id) {
        return mUserDao.getUserById(id);
    }

    @Override
    public int update(UserBean userBean) {
        return mUserDao.update(userBean);
    }

    @Override
    public List<MemberBean> getAllMembers() {
        return mMemberDao.getAllMembers();
    }

    @Override
    public MemberBean getMemberById(int id) {
        return mMemberDao.getMemberById(id);
    }

    @Override
    public MemberBean getMemberByDeviceId(String deviceId) {
        return mMemberDao.getMemberByDeviceId(deviceId);
    }

    @Override
    public long insert(MemberBean memberBean) {
        return mMemberDao.insert(memberBean);
    }

    @Override
    public int update(MemberBean memberBean) {
        return mMemberDao.update(memberBean);
    }

    @Override
    public int delete(MemberBean memberBean) {
        return mMemberDao.delete(memberBean);
    }

    @Override
    public SparseArray<ThreadsBean> getAllThreads() {
        return mThreadsDao.getAllThreads();
    }

    @Override
    public int delete(ThreadsBean threadsBean) {
        return mThreadsDao.delete(threadsBean);
    }

    @Override
    public int delete(List<ThreadsBean> threadsBeans) {
        return mThreadsDao.delete(threadsBeans);
    }

    @Override
    public int deleteAllThreads() {
        return mThreadsDao.deleteAllThreads();
    }
}
