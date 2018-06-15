package com.rgk.android.translator.iflytek.tts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import com.rgk.android.translator.R;
import com.rgk.android.translator.tts.ITTS;
import com.rgk.android.translator.tts.ITTSListener;
import com.rgk.android.translator.utils.Logger;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import java.util.HashMap;

public class FTTTS implements ITTS, InitListener, SynthesizerListener {

    private static final String TAG = "wuwu";
    private boolean isInitSuccess = false;
    private SpeechSynthesizer mTts;
    ITTSListener mListener;
    private String voicer = "xiaoyan"; // 默认发音人
    private Context mContext;

    private HashMap<String, String> speakerMap = new HashMap<>();


    public FTTTS(Activity activity) {
        this.mContext = activity;
        initData();
        SpeechUtility.createUtility(activity, "appid=" + activity.getString(R.string.iflytek_key));
        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(activity, this);
        init();
    }

    private void init() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 设置在线合成引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
    }

    @Override
    public void speak(String msg,String name,String path) {
        Logger.d(TAG,"speak start FT : " + isInitSuccess + " msg : " + msg);
        if (isInitSuccess) {
            if (mTts.isSpeaking()) {
                stop();
            }
            mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, getAudioPath(path) + name + ".wav");
            mTts.startSpeaking(msg, this);
        } else {
            init();
        }
        Logger.d(TAG,"speak end FT ");
    }

    @Override
    public void pause() {
        if (mTts != null) {
            mTts.pauseSpeaking();
        }
    }

    private SynthesizerListener synListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            Logger.d(TAG,"Synthesizer speechError :" + speechError);
            if (speechError != null) {
                if (mListener != null) {
                    mListener.onTTSEvent(FTConstants.STATE_SYN_ERROR/*,speechError.getPlainDescription(true)*/);
                }
            } else {
                if (mListener != null) {
                    mListener.onTTSEvent(FTConstants.STATE_SYN_SUCCESS);
                }
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

    @Override
    public void resume() {
        if (mTts != null) {
            mTts.resumeSpeaking();
        }
    }

    @Override
    public void stop() {
        if (mTts != null) {
            mTts.stopSpeaking();
        }
    }

    @Override
    public void release() {
        if (null != mTts) {
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }

    @Override
    public String synthesizeToUri(String text, String fileName, String filePath) {
        int status = -1;
        String fileAbsolutePath = getAudioPath(filePath) + fileName + ".wav";
        if (mTts != null) {
            mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, fileAbsolutePath);
            status = mTts.synthesizeToUri(text,fileAbsolutePath,synListener);
            return fileAbsolutePath;
        }
        return null;
    }


    @Override
    public void setVoice(String language, boolean male, boolean isServiceVoice) {
        if (mTts != null) {
            String voiceName = getVoiceName(language,male);
            Logger.d(TAG,"language : " + language + " voiceName : " + voiceName);
            mTts.setParameter(SpeechConstant.VOICE_NAME, voiceName);
        }
    }

    @Override
    public byte[] getSpeak(String text) {
        return null;
    }

    @Override
    public void setTTSEventListener(ITTSListener ittsListener) {
        this.mListener = ittsListener;
    }

    @Override
    public void onInit(int code) {
        if (code == ErrorCode.SUCCESS) {
            isInitSuccess = true;
        }
    }

    @Override
    public void onSpeakBegin() {
        // 开始播放
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        // 合成进度
    }

    @Override
    public void onSpeakPaused() {
        // 暂停播放
    }

    @Override
    public void onSpeakResumed() {
        // 继续播放
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
        //Logger.d(TAG, "onSpeakProgress ");
        // 播放进度
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        if (speechError != null) {
            Logger.d(TAG, "onCompleted: " + speechError.getPlainDescription(true));
            if (mListener != null) {
                mListener.onTTSEvent(FTConstants.STATE_SPEAK_ERROR);
            }
        } else {
            if (mListener != null) {
                mListener.onTTSEvent(FTConstants.STATE_SPEAK_SUCCESS);
            }
        }
    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        //以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
//        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
//            String sid = bundle.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
//            Logger.d(TAG, "session id =" + sid);
//        }
    }

    private boolean isSdcardExist() {
        return Environment.isExternalStorageEmulated();
    }

    private String getAudioPath(String path) {
        if (isSdcardExist()) {
            return Environment.getExternalStorageDirectory()  + "/" + path + "/";
        }
        return "/sdcard/";
    }

    private String getVoiceName(String language, boolean male) {
        String voiceName = FTConstants.VOICE_NAME_EN;
        if (speakerMap != null) {
            voiceName = speakerMap.get(language);
        }
        return voiceName;
    }

    private void initData() {
        if (speakerMap != null) {
            speakerMap.clear();
            speakerMap.put("fr-FR", FTConstants.VOICE_NAME_FR);
            speakerMap.put("ru-RU", FTConstants.VOICE_NAME_RU);
            speakerMap.put("en-US", FTConstants.VOICE_NAME_EN);
            speakerMap.put("zh-HK", FTConstants.VOICE_NAME_HK);
            speakerMap.put("zh-TW", FTConstants.VOICE_NAME_TW);
            speakerMap.put("es-ES", FTConstants.VOICE_NAME_ES);
            speakerMap.put("hi-IN", FTConstants.VOICE_NAME_IN);
            speakerMap.put("vi-VN", FTConstants.VOICE_NAME_VI);
            speakerMap.put("zh-CN", FTConstants.VOICE_NAME_CN);
            speakerMap.put("zh-CN_SC", FTConstants.VOICE_NAME_CN_SC);
            speakerMap.put("zh-CN_DB", FTConstants.VOICE_NAME_CN_DB);
            speakerMap.put("zh-CN_HEN", FTConstants.VOICE_NAME_CN_HEN);
            speakerMap.put("zh-CN_HUN", FTConstants.VOICE_NAME_CN_HUN);
            speakerMap.put("zh-CN_SX", FTConstants.VOICE_NAME_CN_SX);
            speakerMap.put("zh-CN_PT", FTConstants.VOICE_NAME_CN_PT);
        }
    }

}
