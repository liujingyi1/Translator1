package com.rgk.android.translator.microsoft.tts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.rgk.android.translator.R;
import com.rgk.android.translator.tts.ITTS;
import com.rgk.android.translator.tts.ITTSListener;
import com.rgk.android.translator.tts.TTSFactory;
import com.rgk.android.translator.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextMessageActivity extends AppCompatActivity {

    private static final String TAG = "wuwu";
    ITTS tts;

    @BindView(R.id.zh_btn)
    Button zhBtn;
    @BindView(R.id.en_btn)
    Button enBtn;
    @BindView(R.id.fr_btn)
    Button frBtn;
    @BindView(R.id.ru_btn)
    Button ruBtn;
    @BindView(R.id.es_btn)
    Button esBtn;
    @BindView(R.id.hi_btn)
    Button hiBtn;
    @BindView(R.id.stop_btn)
    Button stopBtn;
    @BindView(R.id.vi_btn)
    Button viBtn;
    @BindView(R.id.cn_sc_btn)
    Button cnScBtn;
    @BindView(R.id.cn_db_btn)
    Button cnDbBtn;
    @BindView(R.id.cn_hen_btn)
    Button cnHenBtn;
    @BindView(R.id.cn_hun_btn)
    Button cnHunBtn;
    @BindView(R.id.cn_sx_btn)
    Button cnSxBtn;
    @BindView(R.id.cn_tw_btn)
    Button cnTwBtn;
    @BindView(R.id.cn_hk_btn)
    Button cnHkBtn;
    @BindView(R.id.cn_pt_btn)
    Button cnPtBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        ButterKnife.bind(this);

        //microsoft
        //tts = TTSFactory.createTTS(this,"microsoft");
        //tts.setTTSEventListener(mITTSListener);
        //tts.setVoice("ar-EG", false,true);

        //iflytek
        tts = TTSFactory.createTTS(this, "iflytek");
        tts.setTTSEventListener(mITTSListener);

    }

    ITTSListener mITTSListener = new ITTSListener() {
        @Override
        public void onTTSEvent(int status) {
            Logger.d(TAG, "onTTSEvent : " + status);
            switch (status) {
                case MSConstants.STATE_SPEAK_ERROR:
                    break;
                case MSConstants.STATE_SPEAK_SUCCESS:
                    break;
                case MSConstants.STATE_SYN_ERROR:
                    break;
                case MSConstants.STATE_SYN_SUCCESS:
                    break;
                case MSConstants.STATE_TIMEOUT:
                    break;
                default:
                    break;
            }
        }
    };

    @OnClick(R.id.zh_btn)
    public void onZhBtnClicked() {
        tts.setVoice("zh-CN", false, true);
        tts.speak(getString(R.string.tts_text_zh), "test", "ttszh");
    }

    @OnClick(R.id.en_btn)
    public void onEnBtnClicked() {
        tts.setVoice("en-US", false, true);
        tts.speak(getString(R.string.tts_text), "testen", "tts");
    }

    @OnClick(R.id.fr_btn)
    public void onFrBtnClicked() {
        tts.setVoice("fr-FR", false, true);
        tts.speak(getString(R.string.tts_text_fr), "testfr", "tts");
    }

    @OnClick(R.id.ru_btn)
    public void onRuBtnClicked() {
        tts.setVoice("ru-RU", false, true);
        tts.speak(getString(R.string.tts_text_ru), "testru", "tts");
    }

    @OnClick(R.id.es_btn)
    public void onEsBtnClicked() {
        tts.setVoice("es-ES", false, true);
        tts.speak(getString(R.string.tts_text_es), "testes", "tts");
    }

    @OnClick(R.id.hi_btn)
    public void onHiBtnClicked() {
        tts.setVoice("hi-IN", false, true);
        tts.speak(getString(R.string.tts_text_hi), "testhi", "tts");
    }

    @OnClick(R.id.stop_btn)
    public void onStopBtnClicked() {
        tts.stop();
    }

    @OnClick(R.id.vi_btn)
    public void onViBtnClicked() {
        tts.setVoice("vi-VN", false, true);
        tts.speak(getString(R.string.tts_text_vi), "testvi", "tts");
    }

    @OnClick(R.id.cn_sc_btn)
    public void onCnScBtnClicked() {
        tts.setVoice("zh-cn_SC", false, true);
        tts.speak(getString(R.string.tts_text_zh), "testcnsc", "tts");
    }

    @OnClick(R.id.cn_db_btn)
    public void onCnDbBtnClicked() {
    }

    @OnClick(R.id.cn_hen_btn)
    public void onCnHenBtnClicked() {
    }

    @OnClick(R.id.cn_hun_btn)
    public void onCnHunBtnClicked() {
    }

    @OnClick(R.id.cn_sx_btn)
    public void onCnSxBtnClicked() {
    }

    @OnClick(R.id.cn_tw_btn)
    public void onCnTwBtnClicked() {
        tts.setVoice("zh-TW", false, true);
        tts.speak(getString(R.string.tts_text_tw), "testcntw", "tts");
    }

    @OnClick(R.id.cn_hk_btn)
    public void onCnHkBtnClicked() {
        tts.setVoice("zh-HK", false, true);
        tts.speak(getString(R.string.tts_text_hk), "testcnhk", "tts");
    }

    @OnClick(R.id.cn_pt_btn)
    public void onCnPtBtnClicked() {
        tts.setVoice("zh-CN_PT", false, true);
        tts.speak(getString(R.string.tts_text_zh), "testcnpt", "tts");
    }
}
