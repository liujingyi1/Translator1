package com.rgk.android.translator.settings;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rgk.android.translator.R;

public class SettingButton extends LinearLayout {
    private ImageView mIcon;
    private TextView mLabel;

    public SettingButton(Context context) {
        this(context, null);
    }

    public SettingButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SettingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        LayoutInflater.from(context).inflate(R.layout.setting_button, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mIcon = (ImageView) findViewById(R.id.icon);
        mLabel = (TextView) findViewById(R.id.label);
    }

    public void setIcon(int resId) {
        mIcon.setImageResource(resId);
    }

    public void setLabel(int resId) {
        mLabel.setText(resId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mIcon.setEnabled(enabled);
        mLabel.setEnabled(enabled);
    }
}
