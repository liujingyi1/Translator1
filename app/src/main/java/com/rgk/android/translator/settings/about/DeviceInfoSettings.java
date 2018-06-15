package com.rgk.android.translator.settings.about;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.rgk.android.translator.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DeviceInfoSettings {
    private Context mContext;
    private TextView mDeviceName;
    private TextView mDeviceModel;
    private TextView mSerialNumber;
    private TextView mMacAddress;
    private TextView mDeviceVersion;

    private WifiManager mWifiManager;

    private IntentFilter mConnectivityIntentFilter;
    // Broadcasts to listen to for connectivity changes.
    private static final String[] CONNECTIVITY_INTENTS = {
            WifiManager.NETWORK_STATE_CHANGED_ACTION,
    };
    private static final int EVENT_UPDATE_CONNECTIVITY = 600;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_UPDATE_CONNECTIVITY:
                    break;
            }
        }
    };

    private final BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                mHandler.sendEmptyMessage(EVENT_UPDATE_CONNECTIVITY);
            }
        }
    };

    public DeviceInfoSettings(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public void onCreateView(View v) {
        mDeviceName = v.findViewById(R.id.device_name);
        mDeviceModel = v.findViewById(R.id.device_model);
        mSerialNumber = v.findViewById(R.id.serial_number);
        mMacAddress = v.findViewById(R.id.mac_address);
        mDeviceVersion = v.findViewById(R.id.device_version);
    }

    public void onStart() {

    }

    public void onResume() {
        mConnectivityIntentFilter = new IntentFilter();
        mConnectivityIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mConnectivityReceiver, mConnectivityIntentFilter,
                android.Manifest.permission.CHANGE_NETWORK_STATE, null);

        mDeviceName.setText("乐译云手持翻译机");
        mDeviceModel.setText(Build.MODEL);
        mSerialNumber.setText(Build.SERIAL);
        setWifiStatus();
        mDeviceVersion.setText(get("ro.mediatek.version.release"));
    }

    public void onPause() {
        mContext.unregisterReceiver(mConnectivityReceiver);
    }

    public void onStop() {

    }

    public static String get(String key) {
        String result="";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");

            Method get = c.getMethod("get", String.class);
            result=(String)get.invoke(c, key);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setWifiStatus() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        String macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
        mMacAddress.setText(macAddress);
    }
}
