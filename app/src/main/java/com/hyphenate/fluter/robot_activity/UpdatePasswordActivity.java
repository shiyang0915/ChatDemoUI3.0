package com.hyphenate.fluter.robot_activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.impl.ZszlUpdatePwdListener;
import com.hyphenate.fluter.robot_presenter.UpdatePasswordPresenter;
import com.hyphenate.fluter.robot_utils.MyActionBar;
import com.hyphenate.fluter.robot_utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdatePasswordActivity extends BaseActivity implements ZszlUpdatePwdListener {

    @InjectView(R.id.action_bar_update_pwd)
    MyActionBar actionBar;

    @InjectView(R.id.edit_old_pwd)
    public EditText editOldPwd;

    @InjectView(R.id.edit_new_pwd)
    public EditText editNewPwd;

    @InjectView(R.id.edit_confirm_pwd)
    public EditText editConfirmPwd;

    @InjectView(R.id.tv_make_sure)
    public TextView textMakeSure;

    private UpdatePasswordPresenter updatePasswordPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.inject(this);
        initAll();
    }

    @Override
    public void init() {
        actionBar.setActionBar(R.mipmap.back_whrite, "修改登录密码",
                Color.parseColor("#FFFFFF"), false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, false);

    }





    @Override
    public void setData() {
        updatePasswordPresenter = new UpdatePasswordPresenter(this);
        updatePasswordPresenter.setZszlUpdatePwdListener(this);
    }

    @Override
    public void listener() {
//        for (Activity activiy : DemoApplication.getInstance().activityList) {
//            Log.i("shiyang", "listener: " + activiy.getClass().getName());
//        }
    }

    @Override
    public void onPwdUpdateSuccessed(final String successMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show();
                cancelDialog();
                logout();
            }
        });

        Log.i("hshshhshhhshshshshshshs", "onPwdUpdateSuccessed: " + successMsg);
//        logout();
    }


    void logout() {
//        final ProgressDialog pd = new ProgressDialog(getActivity());
//        String st = getResources().getString(R.string.Are_logged_out);
//        pd.setMessage(st);
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
        DemoHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
//                        pd.dismiss();
                        // show login screen
                        DemoApplication.getInstance().userInfor = null;
                        cancelDialog();
                        Utils.saveUserInfor(null);
                        for (Activity activity : DemoApplication.getInstance().activityList) {
                            activity.finish();
                        }
                        Intent intentLogin = new Intent(UpdatePasswordActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "修改密码失败," + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void onPwdUpdateFailed(final String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDialog();
                Toast.makeText(context, "修改密码失败," + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("hshshhshhhshshshshshshs", "onPwdUpdateFailed: " + errorMsg);
    }
}
