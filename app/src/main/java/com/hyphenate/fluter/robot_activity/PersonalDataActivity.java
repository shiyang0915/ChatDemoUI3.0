package com.hyphenate.fluter.robot_activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.domain.BeFriend;
import com.hyphenate.fluter.robot_utils.GlideRoundTransform;
import com.hyphenate.easeui.MyActionBar;
import com.hyphenate.easeui.widget.EaseAlertDialog;

public class PersonalDataActivity extends BaseActivity {
    ProgressDialog progressDialog;
    private TextView textName;
    private TextView textZhangHao;
    private ImageView imageView;
    private BeFriend beFriend;
    private MyActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        initAll();
    }

    @Override
    public void init() {
        beFriend = (BeFriend) getIntent().getSerializableExtra("befriend");
        textName = findViewById(R.id.tv_personal_data_name);
        textZhangHao = findViewById(R.id.tv_personal_data_zhanghao);
        imageView = findViewById(R.id.image_persional_data);

        textName.setText(beFriend.getData().get(0).getName());
        textZhangHao.setText(beFriend.getData().get(0).getFriend());
        Glide.with(this)
                .load(beFriend.getData().get(0).getAvatar())
                .placeholder(R.drawable.msg_head_bg)
                .error(R.drawable.msg_head_bg)
                .transform(new GlideRoundTransform(this, 6))
                .into(imageView);

        findViewById(R.id.tv_add_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(beFriend.getData().get(0).getName());
            }
        });

        actionBar = findViewById(R.id.action_bar_personal_data);
        actionBar.setActionBar(R.mipmap.back,
                "个人资料", Color.parseColor("#030303"), false,
                -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);


    }

    @Override
    public void setData() {

    }

    @Override
    public void listener() {

    }


    /**
     * add contact
     */
    public void addContact(final String name) {
        if (EMClient.getInstance().getCurrentUser().equals(name)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

        if (DemoHelper.getInstance().getContactList().containsKey(name)) {
            //let the user know the contact already in your contact list
            if (EMClient.getInstance().contactManager().getBlackListUsernames().contains(name)) {
//                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                Toast.makeText(context, beFriend.getData().get(0).getName() + "已经是你的好友!", Toast.LENGTH_SHORT).show();
                return;
            }
//            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            Toast.makeText(context, beFriend.getData().get(0).getName() + "已经是你的好友!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(name, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), "申请发送成功!", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), "申请发送失败!" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

}
