package com.rgk.android.translator.mpush;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mpush.api.Constants;
import com.mpush.api.http.HttpCallback;
import com.mpush.api.http.HttpMethod;
import com.mpush.api.http.HttpRequest;
import com.mpush.api.http.HttpResponse;
import com.mpush.client.ClientConfig;
import com.rgk.android.translator.BuildConfig;
import com.rgk.android.translator.database.beans.MessageBean;
import com.rgk.android.translator.mpush.sdk.MPush;
import com.rgk.android.translator.mpush.sdk.MPushLog;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MPushApiProxy implements IMPushApi {

//    private static IMPushApi mInstance = null;

    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCghPCWCobG8nTD24juwSVataW7iViRxcTkey/B792VZEhuHjQvA3cAJgx2Lv8GnX8NIoShZtoCg3Cx6ecs+VEPD2fBcg2L4JK7xldGpOJ3ONEAyVsLOttXZtNXvyDZRijiErQALMTorcgi79M5uVX9/jMv2Ggb2XAeZhlLD28fHwIDAQAB";
    public static String SERVER_IP = "http://116.62.168.13:9999";
    private String mUserId;
    private String mPairUserId;

    private HttpProxyCallback httpCallback;
    private HttpClientListener httpClientListener;
    private Context mContext;
    String mAllocServer = null;

    public MPushApiProxy(Context context) {
        mContext = context;
    }
//
//    public synchronized static IMPushApi getInstance(Context context) {
//        if (mInstance == null)
//            mInstance = new MPushApiProxy(context);
//        return mInstance;
//    }

    public void setPairUser(String id) {
        mPairUserId = id;
    }

    public String getPairUser() {
        return mPairUserId;
    }

    private void initPush(String allocServer, String userId) {
        //公钥有服务端提供和私钥对应

        MPushLog pushLog = new MPushLog();
        pushLog.enable(true);

        ClientConfig cc = ClientConfig.build()
                .setPublicKey(PUBLIC_KEY)
                .setAllotServer(allocServer)
                .setDeviceId(getDeviceId())
                .setClientVersion(BuildConfig.VERSION_NAME)
                .setLogger(pushLog)
                .setLogEnabled(BuildConfig.DEBUG)
                .setEnableHttpProxy(true)
                .setUserId(userId);
        MPush.I.checkInit(mContext.getApplicationContext()).setClientConfig(cc);
    }

    private String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Activity.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (TextUtils.isEmpty(deviceId)) {
            String time = Long.toString((System.currentTimeMillis() / (1000 * 60 * 60)));
            deviceId = time + time;
        }
        return deviceId;
    }

    @Override
    public void bindUser(String userId) {

        Log.i("jingyi", "bindUser userId="+userId);
        if (!TextUtils.isEmpty(userId)) {

            mUserId = userId;
            MPush.I.bindAccount(mUserId, "mpush:" + (int) (Math.random() * 10));
        }
    }

    @Override
    public void startPush(String userId) {

        if (TextUtils.isEmpty(SERVER_IP)) {
            Toast.makeText(mContext, "请填写正确的alloc地址", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!SERVER_IP.startsWith("http://")) {
            SERVER_IP = "http://" + SERVER_IP;
        }

        mUserId = userId;
        initPush(SERVER_IP, mUserId);

        getServerIp();

        MPush.I.checkInit(mContext).startPush();
        Toast.makeText(mContext.getApplicationContext(), "start push", Toast.LENGTH_SHORT).show();
    }

    public void getServerIp() {
        try {
            HttpRequest request = new HttpRequest(HttpMethod.GET, SERVER_IP);
            request.setBody(null, "text/plain;charset=utf-8");
            request.setTimeout((int) TimeUnit.SECONDS.toMillis(10));
            request.setCallback(new HttpCallback() {
                @Override
                public void onResponse(HttpResponse httpResponse) {
                    byte[] body = httpResponse.body;
                    String message = new String(body, Constants.UTF_8);
                    String[] ipList = message.split(",");
                }

                @Override
                public void onCancelled() {

                }
            });
            MPush.I.sendHttpProxy(request);
        } catch (Exception e) {
        }
    }

    @Override
    public void sendPush(MessageBean messageBean) {

//        String sendText = text.trim();

//        if (TextUtils.isEmpty(messageBean)) sendText = "hello";
        if (messageBean == null)
            return;

        try {
            Gson gson = new Gson();
            String message = gson.toJson(messageBean);

            JSONObject params = new JSONObject();
            params.put("userId", mPairUserId);
//            params.put("hello", mUserId + " say:" + sendText);
            params.put("hello", mUserId + " say:" + message);
            params.put("title", "Pair deviceId");

            Log.i("jingyi", "MPushApiProxy text="+message+" mPairUserId="+mPairUserId);

            final Context context = mContext.getApplicationContext();
            HttpRequest request = new HttpRequest(HttpMethod.POST, SERVER_IP + "/push");
            byte[] body = params.toString().getBytes(Constants.UTF_8);
            request.setBody(body, "application/json; charset=utf-8");
            request.setTimeout((int) TimeUnit.SECONDS.toMillis(10));
            request.setCallback(new HttpCallback() {
                @Override
                public void onResponse(HttpResponse httpResponse) {
                    if (httpCallback != null) {
                        httpCallback.onResponse(httpResponse);
                    }
                }

                @Override
                public void onCancelled() {
                    if (httpCallback != null) {
                        httpCallback.onCancelled();
                    }
                }
            });
            MPush.I.sendHttpProxy(request);
        }catch (Exception e) {
            Log.i("jingyi", "send Exception e="+e.getMessage());
        }

    }

    @Override
    public void stopPush() {
        MPush.I.stopPush();
        Toast.makeText(mContext, "stop push", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void pausePush() {
        MPush.I.pausePush();
        Toast.makeText(mContext, "pause push", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void resumePush() {
        MPush.I.resumePush();
        Toast.makeText(mContext, "resume push", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void unbindUser() {
        MPush.I.unbindAccount();
        Toast.makeText(mContext, "unbind user", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setHttpCallBack(HttpProxyCallback httpCallBack) {
        this.httpCallback = httpCallBack;
    }
    @Override
    public void setHttpClientListener(HttpClientListener httpClientListener) {
        MPush.I.setHttpClientListener(httpClientListener);
    }
}
