package com.hyphenate.fluter.robot_presenter;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.UserInfor;
import com.hyphenate.fluter.impl.ZszlLoginListener;
import com.hyphenate.fluter.robot_activity.LoginActivity;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.PasswordCharSequenceStyle;
import com.hyphenate.fluter.robot_utils.RequestCommand;

import java.util.HashMap;


public class LoginPresenter implements RequestCommand.RequestListener {
    private EditText editUserAccount;
    private EditText editPwd;
    private Button buttonLogin;
    private LinearLayout linearPwdShowOrNot;
    private ImageView pwdShowOrNot;
    private boolean isShow;
    private LoginActivity loginActivity;
    private ZszlLoginListener zszlLoginListener;

    public void setZszlLoginListener(ZszlLoginListener zszlLoginListener) {
        this.zszlLoginListener = zszlLoginListener;
    }

    public LoginPresenter(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        this.editUserAccount = loginActivity.editAccount;
        this.editPwd = loginActivity.editPwd;
        this.buttonLogin = loginActivity.buttonLogin;
        this.linearPwdShowOrNot = loginActivity.linearShowPwd;
        this.pwdShowOrNot = loginActivity.imageShowPwdOrNot;
        init();
    }

    private void init() {
        editPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    linearPwdShowOrNot.setVisibility(View.GONE);
                    buttonLogin.setBackgroundResource(R.drawable.login_no_data_bg);
                } else {
                    linearPwdShowOrNot.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(editUserAccount.getText().toString().trim())) {
                        //将登录按钮置为激活状态
                        buttonLogin.setBackgroundResource(R.drawable.login_data_ok_bg);
                    } else {
                        buttonLogin.setBackgroundResource(R.drawable.login_no_data_bg);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editUserAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    buttonLogin.setBackgroundResource(R.drawable.login_no_data_bg);
                } else {
                    if (!TextUtils.isEmpty(editPwd.getText().toString().trim())) {
                        //将登录按钮置为激活状态
                        buttonLogin.setBackgroundResource(R.drawable.login_data_ok_bg);
                    } else {
                        buttonLogin.setBackgroundResource(R.drawable.login_no_data_bg);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        linearPwdShowOrNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShow = !isShow;
                if (isShow) {
                    pwdShowOrNot.setImageResource(R.mipmap.login_pwd_show);
                    editPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    editPwd.setSelection(editPwd.getText().toString().length());
                } else {
                    pwdShowOrNot.setImageResource(R.mipmap.login_pwd_hide);
                    editPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    setEditTextPassWordShowStyle(editPwd);
                    editPwd.setSelection(editPwd.getText().toString().length());
                }
            }
        });

        editPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setEditTextPassWordShowStyle(editPwd);
        loginActivity.showKeyboard(editUserAccount);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = editUserAccount.getText().toString().trim();
                String pwd = editPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pwd)) {
                    loginActivity.showDialog();
                    //执行登录操作
                    HashMap<String, String> mapLogin = new HashMap<>();
                    mapLogin.put("username", account);
                    mapLogin.put("password", pwd);
                    OkHttpRequestUtils.sendPostRequest(RequestCommand.URL_LOGIN, mapLogin,
                            LoginPresenter.this, UserInfor.class);
                } else {
                    Toast.makeText(loginActivity, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void setEditTextPassWordShowStyle(EditText et) {
        et.setTransformationMethod(new PasswordCharSequenceStyle());
    }


    @Override
    public void onResponse(Object o) {
        final UserInfor infor = (UserInfor) o;
        if (infor.getCode() == RequestCommand.RESPONSE_SUCCESS) {
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HuanXinLoginPresenter huanXinLoginPresenter = new HuanXinLoginPresenter(loginActivity);
                    huanXinLoginPresenter.setZszlLoginListener(zszlLoginListener, infor);
                }
            });

        } else {
            loginActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    zszlLoginListener.loginFail(infor.getMsg());
                }
            });
        }
        Log.i("shiyangTest", infor.getCode() + "   " + infor.getMsg());
    }

    @Override
    public void failure(final String message) {
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                zszlLoginListener.loginFail(message);
            }
        });

    }
}
