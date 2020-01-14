/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hyphenate.fluter.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.githang.statusbar.StatusBarCompat;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.EventBusCarrier;
import com.hyphenate.fluter.receiver.CallReceiver;
import com.hyphenate.fluter.robot_utils.NormalDialog;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.easeui.ui.EaseBaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressLint("Registered")
public class BaseActivity extends EaseBaseActivity implements CallReceiver.CallStateChangeListener {

    public int screenWidth, screenHeight;
//    public ChatView chatView;
    private CallReceiver receiver;
    public NormalDialog loadingDialog;
    public ImageView imageLoading;
    public AnimationDrawable anim;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#4285F4"), true);
        Utils.changeStatusBarTextImgColor(this, false);
        EventBus.getDefault().register(this);
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
        receiver = new CallReceiver();
        receiver.setCallStateChangeListener(this);


        View view = View.inflate(this, R.layout.loading_dialog_bg, null);
        imageLoading = view.findViewById(R.id.image_loading);
        loadingDialog = new NormalDialog(this, view);
        loadingDialog.setCanceledOnTouchOutside(false);
        anim = (AnimationDrawable) imageLoading.getDrawable();
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        String content = (String) carrier.getObject();
//        chatView.hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
//            chatView.refreshLocation();
//            chatView.show();
//        } else {
//            chatView.hide();
//        }
    }


    public void showDialog() {
        loadingDialog.show();
        anim.start();
    }

    public void cancelDialog() {
        loadingDialog.cancel();
        anim.stop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
//            chatView.refreshLocation();
//            chatView.show();
//        } else {
//            chatView.hide();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    //弹出软键盘
    public void showKeyboard(EditText editText) {
        //其中editText为dialog中的输入框的 EditText
        if (editText != null) {
            //设置可获得焦点
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            //请求获得焦点
            editText.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }


    @Override
    public void voiceCallDisconnect() {
//        chatView.hide();
    }
}