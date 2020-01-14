package com.hyphenate.fluter.robot_activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.db.InviteMessgeDao;
import com.hyphenate.fluter.db.UserDao;
import com.hyphenate.fluter.robot_utils.MyActionBar;
import com.hyphenate.fluter.robot_utils.NormalDialog;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.fluter.ui.ChatActivity;
import com.hyphenate.easeui.domain.MyFriends;
import com.hyphenate.easeui.ui.GlideRoundTransform;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hyphenate.easeui.utils.EaseCommonUtils.getContactFriend;
import static com.hyphenate.easeui.utils.EaseCommonUtils.saveContactMyFriend;

public class MyFriendPersonalDataActivity extends BaseActivity implements View.OnClickListener, RequestCommand.RequestListener {
    private String username;
    private ImageView imageHead;
    private ImageView imageSetMarker;
    private TextView textMarkerName;
    private TextView textUserName;
    private TextView textAccount;
    private TextView textDeleteFriend;
    //    private EaseUser easeUser;
    private NormalDialog dialog;
    private EditText editInput;
    private ImageView imageDelete;
    private TextView textOK;
    public static Handler handler = new Handler();
    private Dialog deleteDialog;
    private String titleName;
    MyFriends.DataBean.RobotBean robotUser;
    MyFriends.DataBean.UserBean user;
    String markerName;
    String account;
    String flag;
    String imageUrl;
    TextView textDialogDeleteMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend_personal_data);
        initAll();
    }

    @Override
    public void init() {
        flag = getIntent().getStringExtra("flag");
        if (flag.equals("robot")) {
            robotUser = (MyFriends.DataBean.RobotBean) getIntent().getSerializableExtra("friends");
            titleName = robotUser.getFriend_name();
            markerName = robotUser.getFriend_name();
            username = robotUser.getName();
            account = robotUser.getFriend();
            imageUrl = robotUser.getAvatar();
        } else {
            user = (MyFriends.DataBean.UserBean) getIntent().getSerializableExtra("friends");
            titleName = user.getFriend_name();
            markerName = user.getFriend_name();
            username = user.getName();
            account = user.getFriend();
            imageUrl = user.getAvatar();
        }
//        easeUser = DemoApplication.getInstance().easeUser;
        imageHead = findViewById(R.id.image_my_friend_head);
        textMarkerName = findViewById(R.id.tv_my_friend_marker);
        textUserName = findViewById(R.id.tv_my_friend_username);
        textAccount = findViewById(R.id.tv_my_friend_zhanghu);
        imageSetMarker = findViewById(R.id.image_set_marker);
        textDeleteFriend = findViewById(R.id.tv_delete_friend);

        textMarkerName.setText(markerName);
        textUserName.setText(username);
        textAccount.setText(account);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(com.hyphenate.easeui.R.drawable.msg_head_bg)
                .error(com.hyphenate.easeui.R.drawable.msg_head_bg)
                .transform(new GlideRoundTransform(context, 6))
                .into(imageHead);

        View dialogView = getLayoutInflater().inflate(R.layout.set_remarks_dialog, null);
        bindRobbtDialogInit(dialogView);
        dialog = new NormalDialog(this, dialogView);
        dialog.setCanceledOnTouchOutside(false);
        deleteDialog = initDeleteDialog();
    }


    private void bindRobbtDialogInit(View viewDialog) {
        editInput = viewDialog.findViewById(R.id.edit_makers);
        imageDelete = viewDialog.findViewById(R.id.image_input_delete);
        textOK = viewDialog.findViewById(R.id.tv_dialog_set_marks_ok);
        imageDelete.setVisibility(View.GONE);
        editInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    imageDelete.setVisibility(View.VISIBLE);
                } else {
                    imageDelete.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editInput.getText().toString();
                if (data.length() > 0) {
                    editInput.setText(data.substring(0, data.length() - 1));
                    editInput.setSelection(editInput.getText().toString().length());
                }
            }
        });

        textOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkers(account, editInput.getText().toString().trim());
                dialog.cancel();
                showDialog();
            }
        });
    }


    /**
     * @param friendAccount 环信好友账号
     * @param friend_name   好友备注
     */
    private void setMarkers(String friendAccount, String friend_name) {
        Map<String, String> map = new HashMap<>();
        map.put("owner", DemoApplication.getInstance().userInfor.getData().getUsername());
        map.put("friend", friendAccount);
        map.put("friend_name", friend_name);
        map.put("zsm", "butler");
        map.put("zsc", "huanxin_user_friend");
        map.put("zsa", "edit_friend_name");
        map.put("zsv", "v1");
        map.put("timestamp", Utils.getTime());
        map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
        map.put("platform", "android");
        map.put("app_type", "butler");
        map.put("sign", RequestCommand.getSign(map));
        OkHttpRequestUtils.sendPostRequest(RequestCommand.ALL_COMMON, map, this, null);
    }


    @Override
    public void setData() {
        ((MyActionBar) findViewById(R.id.action_my_friend_personal_data)).
                setActionBar(R.mipmap.back, "个人资料", Color.parseColor("#333333"),
                        false, -1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }, null, true);
    }

    @Override
    public void listener() {
        findViewById(R.id.linear_my_friend_send_msg).setOnClickListener(this);
        findViewById(R.id.linear_my_friend_call).setOnClickListener(this);
        findViewById(R.id.linear_my_friend_video_call).setOnClickListener(this);
        textDeleteFriend.setOnClickListener(this);
        imageSetMarker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_my_friend_send_msg:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("userId", account);
//                EaseConstant.EXTRA_USER_ID
                intent.putExtra("titleName", titleName);
                startActivity(intent);
                break;
            case R.id.linear_my_friend_call:
                startVoiceCall(account);
                break;
            case R.id.linear_my_friend_video_call:
                startVideoCall(account);
                break;
            case R.id.tv_delete_friend:
                textDialogDeleteMsg.setText("将联系人" + "\""+"" + markerName + "\"" + "删除,同时删除与该联系人的聊天记录");
                deleteDialog.show();
                break;
            case R.id.image_set_marker:
                dialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showKeyboard(editInput);
                    }
                }, 150);
                break;
        }
    }

    private void deleteFriend() {
        showDialog();
        delete(account);
    }


    private void delete(String friendAccount) {
        Map<String, String> map = new HashMap<>();
        map.put("owner", DemoApplication.getInstance().userInfor.getData().getUsername());
        map.put("friend", friendAccount);
        map.put("zsm", "butler");
        map.put("zsc", "huanxin_user_friend");
        map.put("zsa", "del_friend");
        map.put("zsv", "v1");
        map.put("timestamp", Utils.getTime());
        map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
        map.put("platform", "android");
        map.put("app_type", "butler");
        map.put("sign", RequestCommand.getSign(map));
        OkHttpRequestUtils.sendPostRequest(RequestCommand.ALL_COMMON, map, listener, null);
    }

    RequestCommand.RequestListener listener = new RequestCommand.RequestListener() {
        @Override
        public void onResponse(Object o) {
            try {
                JSONObject jsonObject = new JSONObject(o.toString());
                if (jsonObject.getInt("code") == RequestCommand.RESPONSE_SUCCESS) {
                    deleteContact(account);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InviteMessgeDao dao = new InviteMessgeDao(MyFriendPersonalDataActivity.this);
                            dao.deleteMessage(account);
                            Intent intent = new Intent(MyFriendPersonalDataActivity.this, ContactListActivity.class);
                            setResult(1, intent);

                            MyFriends contactFriend = getContactFriend(context);

                            if (flag.equals("robot")) {
                                List<MyFriends.DataBean.RobotBean> robotBeanList = contactFriend.getData().getRobot();
                                for (MyFriends.DataBean.RobotBean robotBean : robotBeanList) {
                                    if (robotBean.getFriend().equals(account)) {
                                        robotBeanList.remove(robotBean);
                                        break;
                                    }
                                }
                                contactFriend.getData().setRobot(robotBeanList);
                            } else {
                                List<MyFriends.DataBean.UserBean> userBeanList = contactFriend.getData().getUser();
                                for (MyFriends.DataBean.UserBean userBean : userBeanList) {
                                    if (userBean.getFriend().equals(account)) {
                                        userBeanList.remove(userBean);
                                        break;
                                    }
                                }
                                contactFriend.getData().setUser(userBeanList);
                            }
                            saveContactMyFriend(context, contactFriend);
                            DemoApplication.getInstance().myFriends = contactFriend;
                            Log.i("hahahaa", JSON.toJSONString(contactFriend));


                            Toast.makeText(context, "删除成功!", Toast.LENGTH_SHORT).show();
                            cancelDialog();
                            finish();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failure(String message) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cancelDialog();
                    Toast.makeText(context, "删除失败!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };


    /**
     * delete contact
     *
     * @param account
     */
    public void deleteContact(final String account) {
        try {
            UserDao dao = new UserDao(MyFriendPersonalDataActivity.this);
            dao.deleteContact(account);
            DemoHelper.getInstance().getContactList().remove(account);
        } catch (final Exception e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(MyFriendPersonalDataActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private Dialog initDeleteDialog() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_friend, null);
        textDialogDeleteMsg = contentView.findViewById(R.id.tv_dialog_delete_friend_msg);
        contentView.findViewById(R.id.tv_dialog_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFriend();
                bottomDialog.cancel();
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
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResponse(final Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(o.toString());
                    if (obj.getInt("code") == RequestCommand.RESPONSE_SUCCESS) {
                        //修改备注成功
                        MyFriends myFriends = getContactFriend(context);
                        List<MyFriends.DataBean.UserBean> userList = myFriends.getData().getUser();
                        List<MyFriends.DataBean.RobotBean> robotList = myFriends.getData().getRobot();
                        boolean flag = false;
                        for (MyFriends.DataBean.UserBean user : userList) {
                            if (user.getFriend().equals(account)) {
                                //修改本地备注
                                flag = true;
                                user.setFriend_name(editInput.getText().toString().trim());
                                break;
                            }
                        }

                        if (!flag) {
                            for (MyFriends.DataBean.RobotBean robotBean : robotList) {
                                if (robotBean.getFriend().equals(account)) {
                                    //修改本地备注
                                    robotBean.setFriend_name(editInput.getText().toString().trim());
                                }
                            }
                        }
                        DemoApplication.getInstance().myFriends = myFriends;
                        DemoApplication.getInstance().contactListIsChange = true;
                        saveContactMyFriend(context, myFriends);
                        Toast.makeText(context, "修改备注成功!", Toast.LENGTH_SHORT).show();
                        textMarkerName.setText(editInput.getText().toString().trim());
                        markerName = editInput.getText().toString().trim();
                        editInput.setText("");
                        cancelDialog();
                        Log.i("shiyangTest", "修改\n" + JSON.toJSONString(getContactFriend(context)));
                    } else {
                        Toast.makeText(context, "修改备注失败!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "修改备注失败!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void failure(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "修改备注失败!" + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
