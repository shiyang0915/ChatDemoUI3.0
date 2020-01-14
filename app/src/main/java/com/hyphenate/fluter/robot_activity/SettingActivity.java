package com.hyphenate.fluter.robot_activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.MyActionBar;
import com.hyphenate.fluter.robot_utils.Utils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    @InjectView(R.id.action_bar_setting)
    MyActionBar actionBar;

    @InjectView(R.id.tv_exit_login)
    TextView textExitLogin;

    @InjectView(R.id.tv_setting_cache_num)
    TextView textCacheNum;

    private Dialog exitLoginDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.inject(this);
        initAll();
    }


    @OnClick({R.id.linear_update_mima, R.id.linear_clear_cache, R.id.linear_yijian_fankui,
            R.id.tv_exit_login})
    void clickListener(View view) {
        switch (view.getId()) {
            case R.id.linear_update_mima:
                Intent intent = new Intent(SettingActivity.this, UpdatePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.linear_clear_cache:

                break;
            case R.id.linear_yijian_fankui:
                Intent intent_suggestion = new Intent(SettingActivity.this, SuggestionBackActivity.class);
                startActivity(intent_suggestion);
                break;
            case R.id.tv_exit_login:
                exitLoginDialog.show();
                break;
        }
    }

    void logout() {
//        final ProgressDialog pd = new ProgressDialog(getActivity());
//        String st = getResources().getString(R.string.Are_logged_out);
//        pd.setMessage(st);
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
        DemoHelper.getInstance().logout(true, new EMCallBack() {

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
                        Intent intentLogin = new Intent(SettingActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                        finish();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
//                        pd.dismiss();
//                        Toast.makeText(getActivity(), "unbind devicetokens failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public void init() {
        actionBar.setActionBar(R.mipmap.back_whrite, "设置", Color.parseColor("#FFFFFF"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, false);
        exitLoginDialog = initDeleteDialog();
    }




    private Dialog initDeleteDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_friend, null);
        TextView textMsg = contentView.findViewById(R.id.tv_dialog_delete_friend_msg);
        TextView textExitLogin = contentView.findViewById(R.id.tv_dialog_delete);
        textMsg.setText("退出登录");
        textExitLogin.setText("确定");
        textExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                bottomDialog.cancel();
                logout();
            }
        });

        contentView.findViewById(R.id.tv_dialog_delete_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.cancel();
            }
        });
        bottomDialog.setCanceledOnTouchOutside(false);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        return bottomDialog;
    }



    @Override
    public void setData() {
        DemoApplication.getInstance().activityList.add(this);

    }

    @Override
    public void listener() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoApplication.getInstance().activityList.remove(this);
    }
}
