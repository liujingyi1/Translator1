package com.rgk.android.translator.view;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rgk.android.translator.R;

public class NumberBoardView {
    private KeyboardView keyboardView;
    private Keyboard keyboard;// 字母键盘
    private ViewGroup rootView;
    private EditText ed;

    public NumberBoardView(Activity activity, KeyboardView keyboardView, EditText editText) {
        this.ed = editText;

        keyboard = new Keyboard(activity, R.xml.number_keyboard);

        this.keyboardView = keyboardView;
        keyboardView.setKeyboard(keyboard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);
        keyboardView.setOnKeyboardActionListener(onKeyboardActionListener);

        rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    KeyboardView.OnKeyboardActionListener onKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
//                hideKeyboard();
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
                KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL);
                ed.dispatchKeyEvent(keyEvent);
//                editable.delete(start - 1, start);
//                if (editable != null && editable.length() > 0) {
//                    if (start > 0) {
//                        editable.delete(start - 1, start);
//                    }
//                }
            } else {
                String str = Character.toString((char) primaryCode);
                editable.insert(start, str);
            }
        }
    };

    private boolean isShow = false;

    public void showKeyboard() {
//        if (!isShow) {
//            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
//            rootView.addView(keyboardView, layoutParams);
        Log.i("jingyi", "showKeyboard....");
            keyboardView.setVisibility(View.VISIBLE);
            isShow = true;
//        }
    }

    private void hideKeyboard() {
        if (rootView != null && keyboardView != null && isShow) {
            isShow = false;
            keyboardView.setVisibility(View.GONE);
//            rootView.removeView(keyboardView);
        }
        mInstance = null;
    }

    private boolean isWord(String str) {
        return str.matches("[a-zA-Z]");
    }

    private static NumberBoardView mInstance;

    public static NumberBoardView shared(Activity activity, KeyboardView keyboardView, EditText edit) {
        if (mInstance == null) {
            mInstance = new NumberBoardView(activity, keyboardView, edit);
        }
        mInstance.ed = edit;
        return mInstance;
    }
}
