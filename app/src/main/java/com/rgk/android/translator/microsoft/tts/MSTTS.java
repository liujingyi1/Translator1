package com.rgk.android.translator.microsoft.tts;

import android.content.Context;

import com.rgk.android.translator.R;
import com.rgk.android.translator.tts.ITTS;
import com.rgk.android.translator.tts.ITTSListener;
import com.rgk.android.translator.utils.Logger;

import java.util.HashMap;

public class MSTTS implements ITTS {

    private static final String TAG = "wuwu";
    private Synthesizer m_syn;
    ITTSListener mListener;
    private static final int STATE_TIMEOUT = 0;
    private static final int STATE_FINISHED = 1;
    private static final int STATE_ERROR = 2;
    private HashMap<String, String> speakerMap = new HashMap<>();

    public MSTTS(Context context) {

        initData();

        if (m_syn == null) {
            // Create Text To Speech Synthesizer.
            //m_syn = new Synthesizer(context.getString(R.string.primaryKey));
            m_syn = Synthesizer.getInstance(context.getString(R.string.primaryKey));
        }
        m_syn.setServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);
    }


    @Override
    public void setVoice(String language, boolean male, boolean isServiceVoice) {
        String voiceName =  getVoiceName(language,male);
        Logger.d(TAG," setVoice language : " + language + " voiceName : " + voiceName);
        Voice v = new Voice(language, voiceName, male ? Voice.Gender.Male : Voice.Gender.Female, isServiceVoice);
        if (m_syn != null) {
            m_syn.setVoice(v, null);
        }
    }

    @Override
    public void speak(String text,String name,String path) {
        if (m_syn != null) {
            m_syn.speakToAudio(text, name, path, mListener,new Runnable() {
                @Override
                public void run() {
                    Logger.d(TAG,"speak finished");
                    mListener.onTTSEvent(MSConstants.STATE_SPEAK_SUCCESS);
                }
            });
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void stop() {
        if (m_syn != null) {
            m_syn.stopSound();
        }
    }

    @Override
    public void release() {
        if (m_syn != null) {
            m_syn.release();
        }
    }

    @Override
    public String synthesizeToUri(String text, String fileName, String filePath) {
        if (m_syn != null) {
            return m_syn.synthesizeToUri(text, fileName, filePath,mListener);
        }
        return null;
    }

    @Override
    public byte[] getSpeak(String text) {
        if (m_syn != null) {
            return m_syn.speak(text);
        }
        return null;
    }

    @Override
    public void setTTSEventListener(ITTSListener ittsListener) {
        this.mListener = ittsListener;
    }


    private void initData() {
        if (speakerMap != null) {
            speakerMap.clear();
            speakerMap.put("ar-EG", MSConstants.VOICE_AR_EG_FEMALE);
            speakerMap.put("ar-SA_M", MSConstants.VOICE_AR_SA_MALE);
            speakerMap.put("bg-BG_M", MSConstants.VOICE_BG_BG_MALE);
            speakerMap.put("ca-ES", MSConstants.VOICE_CA_ES_FEMALE);
            speakerMap.put("cs-CZ_M", MSConstants.VOICE_CS_CZ_Male);
            speakerMap.put("da-DK", MSConstants.VOICE_DA_DK_FEMALE);
            speakerMap.put("de-AT_M", MSConstants.VOICE_DE_AT_MALE);
            speakerMap.put("de-CH_M", MSConstants.VOICE_DE_CH_MALE);
            speakerMap.put("de-DE", MSConstants.VOICE_DE_DE_FEMALE);
            //speakerMap.put("de-DE1", TTSConstants.VOICE_DE_DE_FEMALE1);
            speakerMap.put("de-DE_M", MSConstants.VOICE_DE_DE_MALE);
            speakerMap.put("el-GR_M", MSConstants.VOICE_EL_GR_MALE);
            speakerMap.put("en-AU", MSConstants.VOICE_EN_AU_FEMALE);
            //speakerMap.put("en-AU1", TTSConstants.VOICE_EN_AU_FEMALE1);
            speakerMap.put("en-CA", MSConstants.VOICE_EN_CA_FEMALE);
            //speakerMap.put("en-CA1", TTSConstants.VOICE_EN_CA_FEMALE1);
            speakerMap.put("en-GB", MSConstants.VOICE_EN_GB_FEMALE);
            speakerMap.put("en-GB1", MSConstants.VOICE_EN_GB_FEMALE1);
            speakerMap.put("en-GB_M", MSConstants.VOICE_EN_GB_MALE);
            speakerMap.put("en-IE_M", MSConstants.VOICE_EN_IE_MALE);
            speakerMap.put("en-IN_M", MSConstants.VOICE_EN_IN_MALE);
            speakerMap.put("en-IN", MSConstants.VOICE_EN_IN_FEMALE);
            //speakerMap.put("en-IN2", TTSConstants.VOICE_EN_IN_FEMALE1);
            speakerMap.put("en-US", MSConstants.VOICE_EN_US_FEMALE);
            //speakerMap.put("en-US1", TTSConstants.VOICE_EN_US_FEMALE1);
            //speakerMap.put("en-US2", TTSConstants.VOICE_EN_US_FEMALE2);
            speakerMap.put("en-US_M", MSConstants.VOICE_EN_US_MALE);
            //speakerMap.put("en-US_M1", TTSConstants.VOICE_EN_US_MALE1);
            speakerMap.put("es-ES_M", MSConstants.VOICE_ES_ES_MALE);
            speakerMap.put("es-ES", MSConstants.VOICE_ES_ES_FEMALE);
            //speakerMap.put("es-ES1", TTSConstants.VOICE_ES_ES_FEMALE1);
            speakerMap.put("es-MX", MSConstants.VOICE_ES_MX_FEMALE);
            speakerMap.put("es-MX_M", MSConstants.VOICE_ES_MX_MALE);
            speakerMap.put("fi-FI", MSConstants.VOICE_FI_FI_FEMALE);
            speakerMap.put("fr-CA", MSConstants.VOICE_FR_CA_FEMALE);
            speakerMap.put("fr-CA1", MSConstants.VOICE_FR_CA_FEMALE1);
            speakerMap.put("fr-CH_M", MSConstants.VOICE_FR_CH_MALE);
            speakerMap.put("fr-FR_M", MSConstants.VOICE_FR_FR_MALE);
            speakerMap.put("fr-FR", MSConstants.VOICE_FR_FR_FEMALE);
            speakerMap.put("he-IL_M", MSConstants.VOICE_HE_IL_MALE);
            speakerMap.put("hi-IN_M", MSConstants.VOICE_HI_IN_MALE);
            speakerMap.put("hi-IN", MSConstants.VOICE_HI_IN_FEMALE);
            //speakerMap.put("hi-IN1", TTSConstants.VOICE_HI_IN_FEMALE1);
            speakerMap.put("hr-HR_M", MSConstants.VOICE_HR_HR_MALE);
            speakerMap.put("hu-HU_M", MSConstants.VOICE_HU_HU_MALE);
            speakerMap.put("id-ID_M", MSConstants.VOICE_ID_ID_MALE);
            speakerMap.put("it-IT_M", MSConstants.VOICE_IT_IT_MALE);
            speakerMap.put("it-IT", MSConstants.VOICE_IT_IT_FEMALE);
            speakerMap.put("ja-JP", MSConstants.VOICE_JA_JP_FEMALE);
            //speakerMap.put("ja-JP1", TTSConstants.VOICE_JA_JP_FEMALE1);
            speakerMap.put("ja-JP_M", MSConstants.VOICE_JA_JP_MALE);
            speakerMap.put("ko-KR", MSConstants.VOICE_KO_KR_FEMALE);
            speakerMap.put("ms-MY_M", MSConstants.VOICE_MS_MY_MALE);
            speakerMap.put("nb-NO", MSConstants.VOICE_NB_NO_FEMALE);
            speakerMap.put("nl-NL", MSConstants.VOICE_NL_NL_FEMALE);
            speakerMap.put("pl-PL", MSConstants.VOICE_PL_PL_FEMALE);
            speakerMap.put("pt-BR", MSConstants.VOICE_PT_BR_FEMALE);
            speakerMap.put("pt-BR_M", MSConstants.VOICE_PT_BR_MALE);
            speakerMap.put("pt-PT", MSConstants.VOICE_PT_PT_FEMALE);
            speakerMap.put("ro-RO_M", MSConstants.VOICE_RO_RO_MALE);
            speakerMap.put("ru-RU_M", MSConstants.VOICE_RU_RU_MALE);
            speakerMap.put("ru-RU", MSConstants.VOICE_RU_RU_FEMALE);
            //speakerMap.put("ru-RU1", TTSConstants.VOICE_RU_RU_FEMALE1);
            speakerMap.put("sk-SK", MSConstants.VOICE_SK_SK_MALE);
            speakerMap.put("sl-SI_M", MSConstants.VOICE_SL_SI_MALE);
            speakerMap.put("sv-SE", MSConstants.VOICE_SV_SE_FEMALE);
            speakerMap.put("ta-IN_M", MSConstants.VOICE_TA_IN_MALE);
            speakerMap.put("th-TH_M", MSConstants.VOICE_TH_TH_MALE);
            speakerMap.put("tr-TR", MSConstants.VOICE_TR_TR_FEMALE);
            speakerMap.put("vi-VN", MSConstants.VOICE_VI_VN_MALE);
            speakerMap.put("zh-CN_M", MSConstants.VOICE_ZH_CN_MALE);
            speakerMap.put("zh-CN", MSConstants.VOICE_ZH_CN_FEMALE);
            //speakerMap.put("zh-CN1", TTSConstants.VOICE_ZH_CN_FEMALE1);
            speakerMap.put("zh-HK", MSConstants.VOICE_ZH_HK_FEMALE1);
            //speakerMap.put("zh-HK1", TTSConstants.VOICE_ZH_HK_FEMALE);
            speakerMap.put("zh-HK_M", MSConstants.VOICE_ZH_HK_MALE);
            speakerMap.put("zh-TW_M", MSConstants.VOICE_ZH_TW_MALE);
            speakerMap.put("zh-TW", MSConstants.VOICE_ZH_TW_FEMALE);
            //speakerMap.put("zh-TW1", TTSConstants.VOICE_ZH_TW_FEMALE1);
        }
    }

    private String getVoiceName(String language, boolean male) {
        String voiceName = speakerMap.get(language + (male ? "_M" : ""));
        if (voiceName == null) {
            voiceName = speakerMap.get(language + (male ? "" : "_M"));
        }
        if (voiceName == null) {
            return male ? MSConstants.VOICE_EN_US_MALE : MSConstants.VOICE_EN_US_FEMALE;
        }
        return voiceName;

    };
}
