package com.rgk.android.translator.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;

public class Utils {
    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    public static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public static int pxToDp(float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 随机生成文件名称
     * @return
     */
    public static String generateAmrFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    public static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    public static String getPcmAbsolutePath(String dir, String fileName) {
        File file = new File(dir, fileName + ".pcm");
        return file.getAbsolutePath();
    }

    public static String getWavAbsolutePath(String dir, String fileName) {
        File file = new File(dir, fileName + ".wav");
        return file.getAbsolutePath();
    }

    public static String getCurrentTime() {
        return getCurrentTime("HH:mm");
    }

    public static String getCurrentTime(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    public static String getTimeString(String format, long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(time));
    }

    private static final String SP_FILE_NAME = "translator.cfg";
    private static final String SP_KEY_DI = "deviceId";
    public static String getDeviceId(Context context) {

        //Build.getSerial();

        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String deviceId = sp.getString(SP_KEY_DI, null);

        if (TextUtils.isEmpty(deviceId)) {
//            String time = Long.toString((System.currentTimeMillis() / (1000 * 60 * 60)));
            String time = Long.toString(System.currentTimeMillis());
            deviceId = time.substring(time.length() - 11);
//            deviceId = time + time;
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SP_KEY_DI, deviceId);
            editor.apply();
        }

        return deviceId;

    }
}
