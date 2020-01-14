package com.hyphenate.fluter.robot_presenter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.impl.ZszlUpdatePwdListener;
import com.hyphenate.fluter.robot_activity.UpdatePasswordActivity;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordPresenter implements RequestCommand.RequestListener {

    private UpdatePasswordActivity updatePasswordActivity;
    private EditText editOldPwd;
    private EditText editNewPwd;
    private EditText editConfirmPwd;
    private TextView textMakeSure;
    private ZszlUpdatePwdListener zszlUpdatePwdListener;

    public void setZszlUpdatePwdListener(ZszlUpdatePwdListener zszlUpdatePwdListener) {
        this.zszlUpdatePwdListener = zszlUpdatePwdListener;
        init();
    }

    public UpdatePasswordPresenter(UpdatePasswordActivity updatePasswordActivity) {
        this.updatePasswordActivity = updatePasswordActivity;
        this.editOldPwd = updatePasswordActivity.editOldPwd;
        this.editNewPwd = updatePasswordActivity.editNewPwd;
        this.editConfirmPwd = updatePasswordActivity.editConfirmPwd;
        this.textMakeSure = updatePasswordActivity.textMakeSure;
    }


    private void init() {
        editOldPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setMakeSureBackGround();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setMakeSureBackGround();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editConfirmPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setMakeSureBackGround();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textMakeSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = editOldPwd.getText().toString().trim();
                String newPwd = editNewPwd.getText().toString().trim();
                String confirmPwd = editConfirmPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(oldPwd) && !TextUtils.isEmpty(newPwd) && !TextUtils.isEmpty(confirmPwd)) {
                    if (!newPwd.equals(confirmPwd)) {
                        Toast.makeText(updatePasswordActivity, "两次输入的密码不一致!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updatePasswordActivity.showDialog();
                    //请求
                    Map<String, String> map = new HashMap<>();
                    map.put("old_pwd", oldPwd);
                    map.put("new_pwd", newPwd);
                    map.put("confirm_pwd", confirmPwd);
                    map.put("zsm", "butler");
                    map.put("zsc", "property_user");
                    map.put("zsa", "change_pwd");
                    map.put("zsv", "v1");
                    map.put("timestamp", Utils.getTime());
                    map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
                    map.put("platform", "android");
                    map.put("app_type", "butler");
                    map.put("sign", RequestCommand.getSign(map));
                    OkHttpRequestUtils.sendPostRequest(RequestCommand.UPDATE_PWD, map, UpdatePasswordPresenter.this,
                            null);
                } else {
                    Toast.makeText(updatePasswordActivity, "请输入完整!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textMakeSure.setClickable(false);
    }


    private void setMakeSureBackGround() {
        if (!TextUtils.isEmpty(editOldPwd.getText().toString().trim()) &&
                !TextUtils.isEmpty(editNewPwd.getText().toString().trim()) &&
                !TextUtils.isEmpty(editConfirmPwd.getText().toString().trim())) {
            textMakeSure.setBackgroundResource(R.drawable.login_data_ok_bg);
            textMakeSure.setClickable(true);
        } else {
            textMakeSure.setBackgroundResource(R.drawable.login_no_data_bg);
            textMakeSure.setClickable(false);
        }
    }


    @Override
    public void onResponse(final Object o) {
//        {"code":605,"msg":"\u539f\u5bc6\u7801\u4e0d\u6b63\u786e","data":[]}
        Log.i("shiyangTest", "shiyang: " + o.toString());
        updatePasswordActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(o.toString());
                    if (obj.getInt("code") == RequestCommand.RESPONSE_SUCCESS) {
                        zszlUpdatePwdListener.onPwdUpdateSuccessed(obj.getString("msg"));
                    } else {
                        zszlUpdatePwdListener.onPwdUpdateFailed(obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failure(final String message) {
        updatePasswordActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                zszlUpdatePwdListener.onPwdUpdateFailed(message);
                Log.i("shiyangTest", "shiyang: " + message);
            }
        });

    }
}
