package com.hyphenate.fluter.robot_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.UserInfor;
import com.hyphenate.fluter.impl.ZszlLoginListener;
import com.hyphenate.fluter.robot_presenter.LoginPresenter;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.fluter.ui.MainActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends BaseActivity implements ZszlLoginListener {
    private LoginPresenter loginPresenter;

    @InjectView(R.id.edit_user_account)
    public EditText editAccount;

    @InjectView(R.id.edit_user_pwd)
    public EditText editPwd;

    @InjectView(R.id.button_login)
    public Button buttonLogin;

    @InjectView(R.id.linear_show_pwd)
    public LinearLayout linearShowPwd;

    @InjectView(R.id.image_login_show_pwd_or_not)
    public ImageView imageShowPwdOrNot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#F5F6F7"), true);
        Utils.changeStatusBarTextImgColor(this, true);
        ButterKnife.inject(this);
        initAll();
    }

    @Override
    public void init() {
        loginPresenter = new LoginPresenter(LoginActivity.this);
        loginPresenter.setZszlLoginListener(this);
    }

    @Override
    public void setData() {
        DemoApplication.getInstance().userInfor = Utils.getUserInfor();
        if (DemoApplication.getInstance().userInfor != null &&
                (!TextUtils.isEmpty(DemoApplication.getInstance().userInfor.getMsg()))) {
            Log.i("shiyangsetData", DemoApplication.getInstance().userInfor.getMsg());
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void listener() {

    }

    @Override
    public void loginSuccess(final UserInfor userInfor) {
        DemoApplication.getInstance().userInfor = userInfor;
        Utils.saveUserInfor(userInfor);
        cancelDialog();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String errorMsg) {
        cancelDialog();
        Toast.makeText(context, "登录失败！" + errorMsg, Toast.LENGTH_SHORT).show();
    }

}
