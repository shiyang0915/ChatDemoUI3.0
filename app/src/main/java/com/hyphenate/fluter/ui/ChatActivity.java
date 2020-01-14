package com.hyphenate.fluter.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.MyActionBar;
import com.hyphenate.fluter.runtimepermissions.PermissionsManager;
import com.hyphenate.easeui.domain.MyFriends;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;

import java.util.List;

import static com.hyphenate.easeui.utils.EaseCommonUtils.getContactFriend;

/**
 * chat activity，EaseChatFragment was used {@link }
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    public String titleName;
    public MyActionBar actionBar;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        titleName = getIntent().getExtras().getString("titleName");
        if (TextUtils.isEmpty(titleName)) {
            titleName = getTitleName(toChatUsername);
        }
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
        actionBar = findViewById(R.id.action_bar_chat);
        actionBar.setActionBar(R.mipmap.back, titleName, Color.parseColor("#030303"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);
    }


    private String getTitleName(String username) {
        MyFriends myFriends = null;
        List<MyFriends.DataBean.RobotBean> robotBeanList = null;
        List<MyFriends.DataBean.UserBean> userBeanList = null;

        myFriends = getContactFriend(this);
        if (myFriends != null) {
            robotBeanList = myFriends.getData().getRobot();
            userBeanList = myFriends.getData().getUser();
        }
        if (userBeanList != null && robotBeanList != null) {
            boolean isFind = false;
            for (int i = 0; i < userBeanList.size(); i++) {
                if (userBeanList.get(i).getFriend().trim().equals(username.trim())) {
                    isFind = true;
                    return userBeanList.get(i).getFriend_name();
                }
            }

            if (!isFind) {
                for (int i = 0; i < robotBeanList.size(); i++) {
                    if (robotBeanList.get(i).getFriend().trim().equals(username.trim())) {
                        return robotBeanList.get(i).getFriend_name();
                    }
                }
            }
        }
        return "";
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
