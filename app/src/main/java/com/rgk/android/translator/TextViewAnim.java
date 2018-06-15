package com.rgk.android.translator;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.rgk.android.translator.utils.Logger;

import java.util.List;

public class TextViewAnim {
    private static final String TAG = "RTranslator/TextViewAnim";

    private static final int MSG_PLAY = 1;
    private static final int MSG_STOP = 2;

    private static TextViewAnim instance = null;

    private static Context mContext;
    private TextView textView;
    private String text;
    private SpannableString spannableString;
    private Status status = Status.IDLE;

    private List<Integer> animSrcList;
    private int idleImgSrc;
    private int animSrcListSize;

    private Thread animThread;
    private TextViewAnim() {}

    public synchronized static TextViewAnim getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new TextViewAnim();
        }

        return instance;
    }

    Handler H = new Handler() {
        int i = 0;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAY: {
                    if (i >= animSrcListSize) {
                        i = 0;
                    }

                    ImageSpan imageSpan = new ImageSpan(mContext, animSrcList.get(i).intValue());
                    int l = text.length();
                    spannableString.setSpan(imageSpan, l, l + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);

                    i++;
                    H.sendEmptyMessageDelayed(MSG_PLAY, 200);
                    break;
                }

                case MSG_STOP: {
                    Logger.v(TAG, "MSG_STOP");
                    ImageSpan imageSpan = new ImageSpan(mContext, idleImgSrc);
                    int l = text.length();
                    spannableString.setSpan(imageSpan, l, l + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                    break;
                }
            }
        }
    };

    public void startPlayAnim(TextView textView, String text, List<Integer> animSrcList, int idleImgSrc) {
        Logger.v(TAG, "startPlayAnim");
        if (status == Status.PLAYING) {
            stopPlayAnim();
        }

        status = Status.PLAYING;
        this.textView = textView;
        this.text = text;
        this.animSrcList = animSrcList;
        this.idleImgSrc = idleImgSrc;
        this.spannableString  = new SpannableString(text + " ");
        this.animSrcListSize = animSrcList.size();


        H.sendEmptyMessageDelayed(MSG_PLAY, 200);
    }

    public void stopPlayAnim() {
        Logger.v(TAG, "stopPlayAnim");
        if(status == Status.PLAYING) {
            H.removeMessages(MSG_PLAY);
            ImageSpan imageSpan = new ImageSpan(mContext, idleImgSrc);
            int l = text.length();
            spannableString.setSpan(imageSpan, l, l + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
        }
        status = Status.IDLE;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }




    enum Status {
        IDLE,
        PLAYING
    }
}
