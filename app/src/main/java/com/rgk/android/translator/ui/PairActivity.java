package com.rgk.android.translator.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.mpush.api.Client;
import com.mpush.api.Constants;
import com.mpush.api.http.HttpResponse;
import com.rgk.android.translator.R;
import com.rgk.android.translator.database.beans.MessageBean;
import com.rgk.android.translator.mpush.HttpClientListener;
import com.rgk.android.translator.mpush.HttpProxyCallback;
import com.rgk.android.translator.mpush.IMPushApi;
import com.rgk.android.translator.mpush.MPushApi;
import com.rgk.android.translator.utils.Utils;
import com.rgk.android.translator.view.NumberBoardView;
import com.rgk.android.translator.view.PairCodeInput;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class PairActivity extends AppCompatActivity {

    @BindView(R.id.user_id)
    TextView userIdView;
    @BindView(R.id.pair_id)
//    EditText pairIdView;
    PairCodeInput pairIdView;
    @BindView(R.id.info)
    TextView infoView;
    @BindView(R.id.my_device_id)
    TextView myDeviceId;
    @BindView(R.id.pair_device_id)
    TextView pairDeviceId;
    @BindView(R.id.pairing_group)
    ViewGroup pairingGroup;
    @BindView(R.id.paired_group)
    ViewGroup pairedGroup;
    @BindView(R.id.pair_btn)
    Button pairBtn;
    @BindView(R.id.code_time)
    TextView codeTimeView;
    KeyboardView keyboardView;


    String mUserId;
    String mDeviceId;
    String mPairDeviceId;
    String mPairUserId;
    IMPushApi mPushApi;
    StringBuilder msb = new StringBuilder();
    Boolean isPairOK = false;
    private static int SECOND = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        //绑定ButterKnife
        ButterKnife.bind(this);
        keyboardView = (KeyboardView)findViewById(R.id.keyboard_view);
        initViews();

        mDeviceId = Utils.getDeviceId(getApplicationContext());

        mPushApi = MPushApi.get(this);
        isPairOK = !TextUtils.isEmpty(mPushApi.getPairUser());
        showView();

    }

    private void initViews() {

        NumberBoardView boardView = new NumberBoardView(PairActivity.this, keyboardView, pairIdView.getEditText());
        boardView.showKeyboard();
//        NumberBoardView.shared(PairActivity.this, keyboardView, pairIdView.getEditText()).showKeyboard();

        RxView.clicks(pairBtn)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        String pairUserId = pairIdView.getInputContent();
                        mPushApi.setPairUser(pairUserId);

                        MessageBean sendMessageBean = new MessageBean();
                        sendMessageBean.setText("RequestId DeviceId:"+mDeviceId+","+mUserId);
                        mPushApi.sendPush(sendMessageBean);
                    }
                });

        pairIdView.setInputCompleteListener(new PairCodeInput.InputCompleteListener() {
            @Override
            public void inputComplete() {
//                onClick();
            }

            @Override
            public void deleteContent() {

            }
        });
    }

    private void showView() {
        if (!isPairOK) {
            pairedGroup.setVisibility(View.GONE);
            pairingGroup.setVisibility(View.VISIBLE);
            startPairing();
        } else {
            pairedGroup.setVisibility(View.VISIBLE);
            pairingGroup.setVisibility(View.GONE);
            showPaired();
        }
    }

    private void showPaired() {
        myDeviceId.setText(mDeviceId);
        pairDeviceId.setText(mPushApi.getPairUser());
    }

    private void startPairing() {
        mUserId = getRandomCode();
        userIdView.setText(mUserId);
        pairIdView.clearInputContent();

        Subscription subscription =  Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .take(SECOND)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        mUserId = null;
                        RxTextView.text(codeTimeView).call("重新获取配对码");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("jingyi", e.toString());
                    }

                    @Override
                    public void onNext(Long aLong) {
                        RxTextView.text(codeTimeView).call("剩余" + (SECOND - aLong) + "秒");
                    }
                });

        mPushApi.startPush(mUserId);
        mPushApi.bindUser(mUserId);

        mPushApi.setHttpCallBack(new HttpProxyCallback() {
            @Override
            public void onResponse(HttpResponse httpResponse) {
                if(httpResponse.reasonPhrase.compareTo("OK") != 0) {
                    Toast.makeText(getApplicationContext(), "配对失败", Toast.LENGTH_LONG);
                    showView();
                }
            }

            @Override
            public void onCancelled() {
                Toast.makeText(getApplicationContext(),"发送失败", Toast.LENGTH_LONG);
            }
        });
        mPushApi.setHttpClientListener(new HttpClientListener(){
            @Override
            public void onReceivePush(Client client, MessageBean messageBean, int i) {
                super.onReceivePush(client, messageBean, i);

                String content = messageBean.getText();

                Log.i("jingyi", "PairActivity deviceId="+mPairDeviceId);
                Log.i("jingyi", "PairActivity content="+messageBean.getText());

                if (content.indexOf("RequestId") != -1){

                    String[] idlist = content.split("DeviceId:")[1].split(",");
                    mPairDeviceId = idlist[0];
                    mPairUserId = idlist[1];

                    Log.i("jingyi", "receive RequestId mPairDeviceId:"+mPairDeviceId+" mPairUserId="+mPairUserId);

                    MessageBean sendMessageBean = new MessageBean();
                    sendMessageBean.setText("DeviceId:"+mDeviceId+","+mUserId);
                    mPushApi.setPairUser(mPairUserId);
                    mPushApi.sendPush(sendMessageBean);

                } else if (content.indexOf("DeviceId:") != -1) {
                    String[] idlist = content.split("DeviceId:")[1].split(",");
                    mPairDeviceId = idlist[0];
                    mPairUserId = idlist[1];

                    mPushApi.setPairUser(mPairUserId);
                    MessageBean sendMessageBean = new MessageBean();
                    sendMessageBean.setText("Pair OK");
                    mPushApi.sendPush(sendMessageBean);

                    Log.i("jingyi", "receive mPairDeviceId="+mPairDeviceId+" mPairUserId="+mPairUserId);
                } else if (content.indexOf("Pair OK") != -1){
                    if (!isPairOK) {
                        isPairOK = true;
                        MessageBean sendMessageBean = new MessageBean();
                        sendMessageBean.setText("Pair OK");
                        mPushApi.sendPush(sendMessageBean);

                        mPushApi.bindUser(mDeviceId);
                        mPushApi.setPairUser(mPairDeviceId);

                        H.sendEmptyMessageDelayed(MSG_SHOW_VIEW, 200);
                    }
                }
            }
        });
    }

    private static final int MSG_SHOW_VIEW = 1;
    Handler H = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_VIEW: {
                    showView();
                }
            }
        }
    };

    private String getRandomCode() {

        StringBuilder codeStr = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 4; i++) {
            codeStr.append(random.nextInt(9));
        }
        return codeStr.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @OnClick(R.id.pair_btn)
//    public void onClick() {
//        if (mPushApi == null) {
//            mPushApi = MPushApi.get(this);
//        }
////        String pairUserId = pairIdView.getText().toString();
//        String pairUserId = pairIdView.getInputContent();
//        mPushApi.setPairUser(pairUserId);
//
//        MessageBean sendMessageBean = new MessageBean();
//        sendMessageBean.setText("RequestId DeviceId:"+mDeviceId+","+mUserId);
//        mPushApi.sendPush(sendMessageBean);
//    }

    @OnClick(R.id.unpair_btn)
    public void onClickUnpair() {
        mPushApi.setPairUser(null);
        isPairOK = false;
        showView();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isPairOK) {
            mPushApi.setPairUser(null);
        }
    }
}
