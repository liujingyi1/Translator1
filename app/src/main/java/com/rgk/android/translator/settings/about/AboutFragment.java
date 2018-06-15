package com.rgk.android.translator.settings.about;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rgk.android.translator.R;

public class AboutFragment extends Fragment {
    private DeviceInfoSettings mDeviceInfoSettings;
    private FeedBackSettings mFeedBackSettings;
    private UserProtocol mUserProtocol;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceInfoSettings = new DeviceInfoSettings(getContext());
        mFeedBackSettings = new FeedBackSettings(getContext());
        mUserProtocol = new UserProtocol(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_about, container);
        mDeviceInfoSettings.onCreateView(v);
        mFeedBackSettings.onCreateView(v);
        mUserProtocol.onCreateView(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDeviceInfoSettings.onStart();
        mFeedBackSettings.onStart();
        mUserProtocol.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDeviceInfoSettings.onResume();
        mFeedBackSettings.onResume();
        mUserProtocol.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mDeviceInfoSettings.onPause();
        mFeedBackSettings.onPause();
        mUserProtocol.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mDeviceInfoSettings.onStop();
        mFeedBackSettings.onStop();
        mUserProtocol.onStop();
    }
}
