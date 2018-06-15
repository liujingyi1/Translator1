package com.rgk.android.translator.database.beans;

import com.rgk.android.translator.utils.Logger;

import java.util.HashMap;
import java.util.List;

public class ThreadsBean {
    private static final String TAG = "RTranslator/ThreadsBean";

    private int id;
    private int serverThreadId;
    private long date;
    private int messageCount;
    private String title;
    private boolean read;
    private List<MessageBean> messageBeans;
    //Key: deviceId
    private HashMap<String, MemberBean> members = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerThreadId() {
        return serverThreadId;
    }

    public void setServerThreadId(int serverThreadId) {
        this.serverThreadId = serverThreadId;
    }

    public void addMember(MemberBean memberBean) {
        if (members.containsKey(memberBean.getDeviceId())) {
            Logger.w(TAG, "Member exist - " + memberBean.getDeviceId());
        } else {
            members.put(memberBean.getDeviceId(), memberBean);
        }
    }

    public void removeMember(MemberBean memberBean) {
        removeMember(memberBean.getDeviceId());
    }

    public void removeMember(String deviceId) {
        members.remove(deviceId);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public List<MessageBean> getMessageBeans() {
        return messageBeans;
    }

    public void setMessageBeans(List<MessageBean> messageBeans) {
        this.messageBeans = messageBeans;
    }
}
