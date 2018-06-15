package com.rgk.android.translator;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.rgk.android.translator.microsoft.stt.MSSTT;
import com.rgk.android.translator.stt.ISTT;
import com.rgk.android.translator.utils.Logger;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RTranslator/MainActivity";

    private TextView code;
    private EditText inputCode;
    private Button findBtn;
    private Button testBtn;
    private ListView connectDevicesList;

    private ISTT mSTT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RequestPermissionsActivity.startPermissionActivity(this)) {
            Logger.i(TAG,"[onCreate]startPermissionActivity,return.");
            return;
        }
        setContentView(R.layout.activity_main);
        code = findViewById(R.id.id_code);
        findBtn = findViewById(R.id.id_find_btn);
        findBtn.setOnClickListener(this);
        inputCode = findViewById(R.id.id_input_code);
        connectDevicesList = findViewById(R.id.id_connect_devices_list);

        testBtn = findViewById(R.id.id_test);
        testBtn.setOnClickListener(this);
        mSTT = new MSSTT(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.v(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.v(TAG, "onDestroy");
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.id_find_btn: {
                Logger.v(TAG, "find_btn");
                //mSTT.stopWithMicrophone();
                break;
            }

            case R.id.id_test: {
                //mSTT.startWithMicrophone();
                break;
            }
        }
    }



}
