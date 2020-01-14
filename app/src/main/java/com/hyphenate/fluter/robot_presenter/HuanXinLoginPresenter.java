package com.hyphenate.fluter.robot_presenter;

import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.db.DemoDBManager;
import com.hyphenate.fluter.domain.UserInfor;
import com.hyphenate.fluter.impl.ZszlLoginListener;
import com.hyphenate.fluter.robot_activity.LoginActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;

public class HuanXinLoginPresenter {

    private EditText editAccount;
    private EditText editPwd;
    private LoginActivity loginActivity;

    private ZszlLoginListener zszlLoginListener;
    private UserInfor infor;

    public void setZszlLoginListener(ZszlLoginListener zszlLoginListener, UserInfor infor) {
        this.zszlLoginListener = zszlLoginListener;
        this.infor = infor;
        init();
    }

    public HuanXinLoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.editAccount = loginActivity.editAccount;
        this.editPwd = loginActivity.editPwd;
    }


    private void init() {
        if (DemoHelper.getInstance().isLoggedIn()) {
            zszlLoginListener.loginSuccess(infor);
            return;
        }
        login();
    }


    /**
     * login
     */
    public void login() {
        if (!EaseCommonUtils.isNetWorkConnected(loginActivity)) {
            Toast.makeText(loginActivity, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUsername = editAccount.getText().toString().trim();
        String currentPassword = editPwd.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(loginActivity, R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(loginActivity, R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
        // close it before login to make sure DemoDB not overlap
        DemoDBManager.getInstance().closeDB();

        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(currentUsername);
        // call login method
        EMClient.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // ** manually load all local groups and conversation
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                // update current user's display name for APNs
                boolean updatenick = EMClient.getInstance().pushManager().updatePushNickname(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        zszlLoginListener.loginSuccess(infor);
                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(final int code, final String message) {
                loginActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        zszlLoginListener.loginFail(message);
                    }
                });
            }
        });
    }


}
