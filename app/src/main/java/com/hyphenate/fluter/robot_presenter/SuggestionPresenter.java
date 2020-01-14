package com.hyphenate.fluter.robot_presenter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.impl.ZszlSuggestionBackListener;
import com.hyphenate.fluter.robot_activity.SuggestionBackActivity;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SuggestionPresenter implements RequestCommand.RequestListener {

    private SuggestionBackActivity activity;
    private EditText editSuggestion;
    private TextView textInputLength;
    private Button buttonSubmit;
    private ZszlSuggestionBackListener zszlSuggestionBackListener;

    public void setZszlSuggestionBackListener(ZszlSuggestionBackListener zszlSuggestionBackListener) {
        this.zszlSuggestionBackListener = zszlSuggestionBackListener;
        init();
    }

    public SuggestionPresenter(SuggestionBackActivity activity) {
        this.activity = activity;
        this.editSuggestion = activity.editSuggestion;
        this.textInputLength = activity.textInputLength;
        this.buttonSubmit = activity.buttonSubmit;
    }


    private void init() {
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String suggestion = editSuggestion.getText().toString().trim();
                if (!TextUtils.isEmpty(suggestion)) {
                    activity.showDialog();
                    Map<String, String> map = new HashMap<>();
                    map.put("content", suggestion);
                    map.put("zsm", "butler");
                    map.put("zsc", "property_user");
                    map.put("zsa", "feedback");
                    map.put("zsv", "v1");
                    map.put("timestamp", Utils.getTime());
                    map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
                    map.put("platform", "android");
                    map.put("app_type", "butler");
                    map.put("sign", RequestCommand.getSign(map));
                    OkHttpRequestUtils.sendPostRequest(RequestCommand.SUGGESTION_BACK,
                            map, SuggestionPresenter.this, null);
                } else {
                    Toast.makeText(activity, "输入的数据不合法!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editSuggestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLength.setText(s.length() + "/200");
                if (!TextUtils.isEmpty(s.toString())) {
                    buttonSubmit.setClickable(true);
                    buttonSubmit.setBackgroundResource(R.drawable.login_data_ok_bg);
                } else {
                    buttonSubmit.setClickable(false);
                    buttonSubmit.setBackgroundResource(R.drawable.login_no_data_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        buttonSubmit.setClickable(false);

    }


    @Override
    public void onResponse(final Object o) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(o.toString());
                    if (obj.getInt("code") == RequestCommand.RESPONSE_SUCCESS) {
                        zszlSuggestionBackListener.onSuggestionBackSuccessed(obj.getString("msg"));
                    } else {
                        zszlSuggestionBackListener.onSuggestionBackFailed(obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void failure(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                zszlSuggestionBackListener.onSuggestionBackFailed(message);
            }
        });
    }
}
