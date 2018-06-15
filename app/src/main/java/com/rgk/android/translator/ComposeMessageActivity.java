package com.rgk.android.translator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mpush.api.Client;
import com.mpush.api.http.HttpResponse;
import com.rgk.android.translator.database.TranslatorStorage;
import com.rgk.android.translator.database.beans.MessageBean;
import com.rgk.android.translator.database.beans.UserBean;
import com.rgk.android.translator.mpush.HttpClientListener;
import com.rgk.android.translator.mpush.HttpProxyCallback;
import com.rgk.android.translator.mpush.IMPushApi;
import com.rgk.android.translator.mpush.MPushApi;
import com.rgk.android.translator.stt.ISTT;
import com.rgk.android.translator.stt.ISTTFinishedListener;
import com.rgk.android.translator.stt.ISTTVoiceLevelListener;
import com.rgk.android.translator.stt.STTFactory;
import com.rgk.android.translator.translate.ITranslate;
import com.rgk.android.translator.translate.ITranslateFinishedListener;
import com.rgk.android.translator.tts.ITTS;
import com.rgk.android.translator.tts.ITTSListener;
import com.rgk.android.translator.tts.TTSFactory;
import com.rgk.android.translator.utils.Logger;
import com.rgk.android.translator.utils.NetUtil;
import com.rgk.android.translator.utils.Utils;
import com.rgk.android.translator.view.AudioRecorderButton;
import com.rgk.android.translator.database.DbConstants.MessageType;
import com.rgk.android.translator.youdao.YDTranslate;


import java.util.ArrayList;
import java.util.List;

public class ComposeMessageActivity extends AppCompatActivity {
    private static final String TAG = "RTranslator/ComposeMessageActivity";
    private static final int MSG_RECEIVED = 1001;
    private static final int MSG_PLAY_ANIMI = 1002;

    private List<String> permissions = new ArrayList<>();

    private RecyclerView mComposeList;

    private ComposeListAdapter mComposeLiseAdapter;
    private List<MessageBean> mDatas = new ArrayList<>();

    private NetUtil mNetUtil = new NetUtil();

    private AudioRecorderButton mAudioRecorderButton;
    private TextViewAnim mTextViewAnim;
    private List<Integer> mTextViewAnimList = new ArrayList<>();
    private ImageView mEndConversationBtn;
    private TextView mLanguageNameText;
    private LinearLayoutManager mLayoutManager;

    private ISTT mSTT;
    private ITTS mTTS;
    private MessageBean tmpMessage;
    private TranslatorStorage mTranslatorStorage;
    private UserBean mUserInfo;
    private StringBuilder sttString = new StringBuilder();
    private IMPushApi mPushApi;
    private int mThreadId = 0;

    private ITranslate mTranslate;

    ISTTFinishedListener mSTTFinishedListener = new ISTTFinishedListener() {
        @Override
        public void onSTTFinish(ISTT.FinalResponseStatus status, String text) {
            Logger.v(TAG, "onSTTFinish, status=" + status + ", text=" + text);

            if (ISTT.FinalResponseStatus.OK == status) {
                sttString.append(text);
            } else if (ISTT.FinalResponseStatus.NotReceived == status) {
                sttString.append(text);
            } else if (ISTT.FinalResponseStatus.Timeout == status) {
                sttString.append("");
            } else if (ISTT.FinalResponseStatus.Finished == status) {
                tmpMessage.setText(sttString.toString());
                tmpMessage.setType(MessageType.TYPE_TEXT);
                tmpMessage.setLanguage(mUserInfo.getLanguage());
                tmpMessage.setDate(System.currentTimeMillis());
                mPushApi.sendPush(tmpMessage);
                mComposeLiseAdapter.addItem(tmpMessage);
                mComposeList.smoothScrollToPosition(mDatas.size() - 1);
                mAudioRecorderButton.dismissRecordingDialog();
            } else if (ISTT.FinalResponseStatus.Error == status) {
                Toast.makeText(getApplicationContext(), R.string.final_response_error, Toast.LENGTH_LONG).show();
                mAudioRecorderButton.dismissRecordingDialog();
            }
        }
    };

    ITTSListener mTTSEventListener = new ITTSListener() {
        @Override
        public void onTTSEvent(int status) {
            Logger.v(TAG, "onTTSEvent-"+status);
            mTextViewAnim.stopPlayAnim();
        }
    };
	
    ISTTVoiceLevelListener mSTTVoiceLevelListener = new ISTTVoiceLevelListener() {
        @Override
        public void updateVoiceLevel(int level) {
            mAudioRecorderButton.updateVoiceLevel(level);
        }
    };

    ITranslateFinishedListener mTranslateFinishedListener = new ITranslateFinishedListener() {
        @Override
        public void onTranslateFinish(MessageBean messageBean, int i) {
            mTTS.setVoice(messageBean.getLanguage(), false, true);
            mTTS.speak(messageBean.getText(), ""+i, AppContext.SOUND_FILE_DIR);
            messageBean.setType(MessageType.TYPE_SOUND_TEXT);
            messageBean.setMemberId(1);
            messageBean.setUrl(AppContext.SOUND_FILE_DIR + "/" + i);
            Message message = new Message();
            message.what = MSG_RECEIVED;
            message.obj = messageBean;
            H.sendMessage(message);
        }
    };

    private Handler H = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECEIVED: {
                    MessageBean messageBean = (MessageBean) msg.obj;
                    mComposeLiseAdapter.addItem(messageBean);
                    int currentPos = mDatas.size() - 1;
                    Logger.w(TAG, "currentPos = " + currentPos);
                    mComposeList.smoothScrollToPosition(currentPos);
                    Message message = new Message();
                    message.what = MSG_PLAY_ANIMI;
                    message.arg1 = currentPos;
                    sendMessageDelayed(message, 200);
                    break;
                }

                case MSG_PLAY_ANIMI: {
                    View item = mLayoutManager.findViewByPosition(msg.arg1);
                    //播放动画
                    if (item == null) {
                        Logger.w(TAG, "itemView is NULL");
                    } else {
                        mTextViewAnim.startPlayAnim((TextView) item.findViewById(R.id.id_msg_txt),
                                mDatas.get(msg.arg1).getText(), mTextViewAnimList, R.mipmap.ic_play_recordor_wave_receive_v3);
                    }
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        mTranslatorStorage = TranslatorStorage.getInstance();

        Intent intent = getIntent();

        mUserInfo = mTranslatorStorage.getUser();
        mSTT = STTFactory.createSTT(this, "iflytek");
        mSTT.setSTTFinishedListener(mSTTFinishedListener);
        mSTT.setSTTVoiceLevelListener(mSTTVoiceLevelListener);
        mSTT.setLanguageCode(mUserInfo.getLanguage());

        mTTS = TTSFactory.createTTS(this,"iflytek");
        mTTS.setTTSEventListener(mTTSEventListener);

        mTranslate = new YDTranslate();
        mTranslate.setTranslateFinishedListener(mTranslateFinishedListener);

        mTextViewAnim = TextViewAnim.getInstance(this);
        mTextViewAnimList.clear();
        mTextViewAnimList.add(R.mipmap.ic_play_recordor_wave_receive_v1);
        mTextViewAnimList.add(R.mipmap.ic_play_recordor_wave_receive_v2);
        mTextViewAnimList.add(R.mipmap.ic_play_recordor_wave_receive_v3);

        mPushApi = MPushApi.get(getApplicationContext());
        mPushApi.startPush(Utils.getDeviceId(getApplicationContext()));
        mPushApi.setHttpCallBack(new HttpProxyCallback() {
            @Override
            public void onResponse(HttpResponse httpResponse) {
                Logger.i(TAG, "MPushApi - onResponse");
            }

            @Override
            public void onCancelled() {
                Logger.i(TAG, "MPushApi - onCancelled");
            }
        });

        mPushApi.setHttpClientListener(new HttpClientListener() {
            @Override
            public void onReceivePush(Client client, MessageBean messageBean, int i) {
                Logger.v(TAG, "mPushApi - onReceivePush:"+messageBean.getThreadId());
                if (messageBean.getThreadId() == mThreadId) {
                    Logger.v(TAG, "message language:"+messageBean.getLanguage());
                    Logger.v(TAG, "my language:"+mUserInfo.getLanguage());
                    mTranslate.doTranslate(messageBean, mUserInfo.getLanguage(), i);
                }
            }
        });

        //View的初始化
        mAudioRecorderButton = findViewById(R.id.id_recorder_btn);
        mAudioRecorderButton.setAudioRecorderStateListener(new AudioRecorderButton.onAudioRecorderStateListener() {
            @Override
            public void onFinish(boolean isToShort, int stats) {
                Logger.i(TAG, "AudioRecorderStateListener - onFinish");
                String time = Utils.getCurrentTime();
                mSTT.stopWithMicrophone();
                tmpMessage = new MessageBean();
            }

            @Override
            public void onStart() {
                Logger.i(TAG, "AudioRecorderStateListener - onStart");
                sttString = new StringBuilder();
                mSTT.startWithMicrophone();
            }
        });

        mComposeList = findViewById(R.id.id_compose_list);
        mLayoutManager = new LinearLayoutManager(this);
        mComposeList.setLayoutManager(mLayoutManager);
        mComposeLiseAdapter = new ComposeListAdapter(this, mDatas);
        mComposeList.setAdapter(mComposeLiseAdapter);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        mComposeList.setItemAnimator(itemAnimator);
        mComposeLiseAdapter.setOnItemClickListener(new ComposeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Logger.v(TAG, "onItemClick:" + position);
                if ((mDatas.get(position).getType() == MessageType.TYPE_SOUND
                        || mDatas.get(position).getType() == MessageType.TYPE_SOUND_TEXT)
                        && !mDatas.get(position).isSend()) {
                    //播放动画
                    mTextViewAnim.startPlayAnim((TextView) view,
                            mDatas.get(position).getText(), mTextViewAnimList, R.mipmap.ic_play_recordor_wave_receive_v3);


                    //播放音频
                    MediaManager.playSound(mDatas.get(position).getUrl(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mTextViewAnim.stopPlayAnim();
                        }
                    });
                }
            }
        });
        mLanguageNameText = findViewById(R.id.id_language_name);
        mLanguageNameText.setText(intent.getStringExtra("LanguageName"));
        mEndConversationBtn = findViewById(R.id.id_compose_end_img);
        mEndConversationBtn.setOnClickListener(mSlideButtonOnClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        mSTT.onResume();
        super.onResume();
        MediaManager.resume();
        checkPermission();
        mPushApi.resumePush();
    }

    @Override
    protected void onPause() {
        mSTT.onPause();
        super.onPause();
        MediaManager.pause();
        mPushApi.pausePush();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        mSTT.onDestroy();
        mTTS.release();
    }

    View.OnClickListener mSlideButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.id_compose_end_img: {
                    finish();
                    break;
                }
            }
        }
    };

    protected void checkPermission() {
        permissions.clear();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!permissions.isEmpty()) {
            String[] ps = new String[permissions.size()];
            permissions.toArray(ps);
            ActivityCompat.requestPermissions(this, ps, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
