package com.hyphenate.fluter.robot_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.EventBusCarrier;
import com.hyphenate.fluter.receiver.CallReceiver;
import com.hyphenate.fluter.robot_utils.NormalDialog;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.fluter.ui.VideoCallActivity;
import com.hyphenate.fluter.ui.VoiceCallActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseActivity extends AppCompatActivity implements
        CallReceiver.CallStateChangeListener {
    public Context context;
    protected InputMethodManager inputMethodManager;
//    public ChatView chatView;
    private CallReceiver receiver;

    public ImageView imageLoading;
    public AnimationDrawable anim;
    public NormalDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#4285F4"), true);
        Utils.changeStatusBarTextImgColor(this, false);
        EventBus.getDefault().register(this);
        context = this;
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

//        chatView = new ChatView(this, R.mipmap.float_voice_call);
//        chatView.hide();
//        chatView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(BaseActivity.this, VoiceCallActivity.class);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.setAction(Intent.ACTION_MAIN);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//                startActivity(intent);
//            }
//        });
        receiver = new CallReceiver();
        receiver.setCallStateChangeListener(this);
    }

    public void initAll() {
        View view = View.inflate(this, R.layout.loading_dialog_bg, null);
        imageLoading = view.findViewById(R.id.image_loading);
        loadingDialog = new NormalDialog(this, view);
        loadingDialog.setCanceledOnTouchOutside(false);
        anim = (AnimationDrawable) imageLoading.getDrawable();

        init();
        setData();
        listener();
    }

    public void showDialog() {
        loadingDialog.show();
        anim.start();
    }

    public void cancelDialog() {
        loadingDialog.cancel();
        anim.stop();
    }

    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        String content = (String) carrier.getObject();
//        show.setText(content);
//        chatView.hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
//            chatView.refreshLocation();
//            chatView.show();
        } else {
//            chatView.hide();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
//            chatView.refreshLocation();
//            chatView.show();
        } else {
//            chatView.hide();
        }
    }

    public abstract void init();

    public abstract void setData();

    public abstract void listener();


    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
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


    /**
     * make a voice call
     */
    protected void startVoiceCall(String toChatUsername) {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(this, VoiceCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // voiceCallBtn.setEnabled(false);
        }
    }

    /**
     * make a video call
     */
    protected void startVideoCall(String toChatUsername) {
        if (!EMClient.getInstance().isConnected())
            Toast.makeText(this, R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, VideoCallActivity.class).putExtra("username", toChatUsername)
                    .putExtra("isComingCall", false));
            // videoCallBtn.setEnabled(false);
        }
    }


    @Override
    public void voiceCallDisconnect() {
//        chatView.hide();
    }
}
