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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.BeFriend;
import com.hyphenate.fluter.robot_activity.PersonalDataActivity;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.easeui.MyActionBar;
import com.hyphenate.easeui.widget.EaseAlertDialog;

import java.util.HashMap;
import java.util.Map;

public class AddContactActivity extends BaseActivity {
    private EditText editText;
    private RelativeLayout searchedUserLayout;
    private TextView nameText;
    //    private Button searchBtn;
    private String toAddUsername;
    //    private ProgressDialog progressDialog;
    private MyActionBar actionBar;


    private RequestCommand.RequestListener requestListener = new RequestCommand.RequestListener() {
        @Override
        public void onResponse(final Object o) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelDialog();
                    BeFriend beFriend = (BeFriend) o;
                    if (beFriend != null && beFriend.getData() != null) {
                        if (beFriend.getCode() == RequestCommand.RESPONSE_SUCCESS &&
                                beFriend.getData().size() > 0) {
                            startActivity(new Intent(AddContactActivity.this, PersonalDataActivity.class).
                                    putExtra("befriend", beFriend));
                        } else {
                            Toast.makeText(AddContactActivity.this, "没有查到该用户!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddContactActivity.this, "没有查到该用户!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public void failure(final String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddContactActivity.this, message, Toast.LENGTH_SHORT).show();
                    cancelDialog();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_add_contact);
        TextView mTextView = (TextView) findViewById(R.id.add_list_friends);
//		editText = (EditText) findViewById(R.id.edit_note);
        editText = (EditText) findViewById(R.id.query);
        String strAdd = getResources().getString(R.string.add_friend);
        mTextView.setText(strAdd);
        String strUserName = getResources().getString(R.string.user_name);
        editText.setHint(strUserName);
        searchedUserLayout = (RelativeLayout) findViewById(R.id.ll_user);
        nameText = (TextView) findViewById(R.id.name);
//        searchBtn = (Button) findViewById(R.id.search);
        actionBar = findViewById(R.id.action_bar_add_friend);
        actionBar.setActionBar(R.mipmap.back, "添加好友", Color.parseColor("#000000"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);

        findViewById(R.id.tv_search_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        goToSearchActivity();
    }


    private void goToSearchActivity() {
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                        Toast.makeText(AddContactActivity.this, "用户名不能为空!", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        // 先隐藏键盘
                        ((InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(AddContactActivity.this
                                                .getCurrentFocus()
                                                .getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                        showDialog();
                        search(editText.getText().toString().trim());
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private void search(String huanxin_user_num) {
        Map<String, String> map = new HashMap<>();
        map.put("friend", huanxin_user_num);
        map.put("zsm", "butler");
        map.put("zsc", "huanxin_user_friend");
        map.put("zsa", "search_user");
        map.put("zsv", "v1");
        map.put("timestamp", Utils.getTime());
        map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
        map.put("platform", "android");
        map.put("app_type", "butler");
        map.put("sign", RequestCommand.getSign(map));
        OkHttpRequestUtils.sendPostRequest(RequestCommand.ALL_COMMON, map, requestListener, BeFriend.class);
    }


    /**
     * search contact
     *
     * @param v
     */
    public void searchContact(View v) {
        final String name = editText.getText().toString().toLowerCase();
//        String saveText = searchBtn.getText().toString();

//        if (getString(R.string.button_search).equals(saveText)) {
        toAddUsername = name;
        if (TextUtils.isEmpty(name)) {
            new EaseAlertDialog(this, R.string.Please_enter_a_username).show();
            return;
        }

        Intent intent = new Intent(AddContactActivity.this, PersonalDataActivity.class);
        intent.putExtra("name", toAddUsername);
        startActivity(intent);
        // TODO you can search the user from your app server here.

        //show the userame and add button if user exist
//        searchedUserLayout.setVisibility(View.VISIBLE);
//        nameText.setText(toAddUsername);

//        }
    }

    /**
     * add contact
     *
     * @param view
     */
    public void addContact(View view) {
        if (EMClient.getInstance().getCurrentUser().equals(nameText.getText().toString())) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(nameText.getText().toString())) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())) {
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }

//        progressDialog = new ProgressDialog(this);
//        String stri = getResources().getString(R.string.Is_sending_a_request);
//        progressDialog.setMessage(stri);
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(toAddUsername, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    public void back(View v) {
        finish();
    }
}
