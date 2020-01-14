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
package com.hyphenate.fluter.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.db.InviteMessgeDao;
import com.hyphenate.fluter.domain.InviteMessage;
import com.hyphenate.fluter.domain.InviteMessage.InviteMessageStatus;
import com.hyphenate.fluter.robot_utils.NormalDialog;
import com.hyphenate.fluter.robot_utils.OkHttpRequestUtils;
import com.hyphenate.fluter.robot_utils.RequestCommand;
import com.hyphenate.fluter.robot_utils.Utils;
import com.hyphenate.fluter.ui.NewFriendsMsgActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage>
        implements RequestCommand.RequestListener {

    private static Context context;
    private InviteMessgeDao messgeDao;
    private NormalDialog dialog;
    private View viewDialog;

    private static EditText editInput;
    private ImageView imageDelete;
    private TextView textOK;
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //请求成功
                try {
                    JSONObject obj = new JSONObject((String) msg.obj);
                    if (obj.getInt("code") == RequestCommand.RESPONSE_SUCCESS) {
                        DemoApplication.getInstance().contactListIsChange = true;
//                        //修改备注成功
//                        MyFriends myFriends = getContactFriend(context);
//                        List<MyFriends.DataBean.UserBean> userList = myFriends.getData().getUser();
//                        List<MyFriends.DataBean.RobotBean> robotList = myFriends.getData().getRobot();
//                        boolean flag = false;
//                        for (MyFriends.DataBean.UserBean user : userList) {
//                            if (user.getFriend().equals(presentMsg.getFrom())) {
//                                //修改本地备注
//                                flag = true;
//                                user.setFriend_name(editInput.getText().toString().trim());
//                                break;
//                            }
//                        }
//
//                        if (!flag) {
//                            for (MyFriends.DataBean.RobotBean robotBean : robotList) {
//                                if (robotBean.getFriend().equals(presentMsg.getFrom())) {
//                                    //修改本地备注
//                                    robotBean.setFriend_name(editInput.getText().toString().trim());
//                                }
//                            }
//                        }
//                        DemoApplication.getInstance().myFriends = myFriends;
//                        saveContactMyFriend(context, myFriends);
//                        Log.i("shiyangTest", "修改\n" + JSON.toJSONString(getContactFriend(context)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == 2) {
                //请求失败
                Toast.makeText(context, "添加好友失败!", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private NewFriendsMsgActivity activity;
    private Button presentButton;
    private static InviteMessage presentMsg;

    public NewFriendsMsgAdapter(Context context, int textViewResourceId,
                                List<InviteMessage> objects, NormalDialog dialog,
                                View viewDialog, WeakReference<NewFriendsMsgActivity> activityWeakReference) {
        super(context, textViewResourceId, objects);
        this.context = context;
        messgeDao = new InviteMessgeDao(context);
        this.dialog = dialog;
        this.viewDialog = viewDialog;
        activity = activityWeakReference.get();
        bindRobbtDialogInit();
    }


    private void bindRobbtDialogInit() {
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

        textOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                acceptInvitation(holder.agreeBtn, msg);
                if (TextUtils.isEmpty(editInput.getText().toString().trim())) {
                    Toast.makeText(context, "备注不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }
                acceptInvitation(presentButton, presentMsg);
                dialog.cancel();
            }
        });
    }


    @Override
    public InviteMessage getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.em_row_invite_msg, null);
            holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.agreeBtn = (Button) convertView.findViewById(R.id.btn_agree);
//			holder.refuseBtn = (Button) convertView.findViewById(R.id.refuse);
            // holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final InviteMessage msg = getItem(position);
        if (msg != null) {
            holder.agreeBtn.setVisibility(View.GONE);
//            holder.refuseBtn.setVisibility(View.GONE);


            holder.message.setText(msg.getReason());
            holder.name.setText(msg.getFrom());
            // holder.time.setText(DateUtils.getTimestampString(new
            // Date(msg.getTime())));
            if (msg.getStatus() == InviteMessageStatus.BEAGREED) {
                holder.agreeBtn.setVisibility(View.GONE);
//                holder.refuseBtn.setVisibility(View.GONE);
                holder.message.setText(context.getResources().getString(R.string.Has_agreed_to_your_friend_request));
            } else if (msg.getStatus() == InviteMessageStatus.BEINVITEED || msg.getStatus() == InviteMessageStatus.BEAPPLYED ||
                    msg.getStatus() == InviteMessageStatus.GROUPINVITATION) {
                holder.agreeBtn.setVisibility(View.VISIBLE);
//                holder.refuseBtn.setVisibility(View.VISIBLE);
                if (msg.getStatus() == InviteMessageStatus.BEINVITEED) {
                    if (msg.getReason() == null) {
                        // use default text
                        holder.message.setText(context.getResources().getString(R.string.Request_to_add_you_as_a_friend));
                    }
                } else if (msg.getStatus() == InviteMessageStatus.BEAPPLYED) { //application to join group
                    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.message.setText(context.getResources().getString(R.string.Apply_to_the_group_of) + msg.getGroupName());
                    }
                } else if (msg.getStatus() == InviteMessageStatus.GROUPINVITATION) {
                    if (TextUtils.isEmpty(msg.getReason())) {
                        holder.message.setText(context.getResources().getString(R.string.invite_join_group) + msg.getGroupName());
                    }
                }

                // set click listener
                holder.agreeBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // accept invitation
                        presentButton = holder.agreeBtn;
                        presentMsg = msg;
                        dialog.show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                activity.showKeyboard(editInput);
                            }
                        }, 150);

                    }
                });
//                holder.refuseBtn.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // decline invitation
//                        refuseInvitation(holder.agreeBtn, holder.refuseBtn, msg);
//                    }
//                });
            } else {
                String str = "";
                InviteMessageStatus status = msg.getStatus();
                switch (status) {
                    case AGREED:
                        str = context.getResources().getString(R.string.Has_agreed_to);
                        break;
                    case REFUSED:
                        str = context.getResources().getString(R.string.Has_refused_to);
                        break;
                    case GROUPINVITATION_ACCEPTED:
                        str = context.getResources().getString(R.string.accept_join_group);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case GROUPINVITATION_DECLINED:
                        str = context.getResources().getString(R.string.refuse_join_group);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_CONTACT_ADD:
                        str = context.getResources().getString(R.string.multi_device_contact_add);
                        str = String.format(str, msg.getFrom());
                        break;
                    case MULTI_DEVICE_CONTACT_BAN:
                        str = context.getResources().getString(R.string.multi_device_contact_ban);
                        str = String.format(str, msg.getFrom());
                        break;
                    case MULTI_DEVICE_CONTACT_ALLOW:
                        str = context.getResources().getString(R.string.multi_device_contact_allow);
                        str = String.format(str, msg.getFrom());
                        break;
                    case MULTI_DEVICE_CONTACT_ACCEPT:
                        str = context.getResources().getString(R.string.multi_device_contact_accept);
                        str = String.format(str, msg.getFrom());
                        break;
                    case MULTI_DEVICE_CONTACT_DECLINE:
                        str = context.getResources().getString(R.string.multi_device_contact_decline);
                        str = String.format(str, msg.getFrom());
                        break;
                    case MULTI_DEVICE_GROUP_CREATE:
                        str = context.getResources().getString(R.string.multi_device_group_create);
                        break;
                    case MULTI_DEVICE_GROUP_DESTROY:
                        str = context.getResources().getString(R.string.multi_device_group_destroy);
                        break;
                    case MULTI_DEVICE_GROUP_JOIN:
                        str = context.getResources().getString(R.string.multi_device_group_join);
                        break;
                    case MULTI_DEVICE_GROUP_LEAVE:
                        str = context.getResources().getString(R.string.multi_device_group_leave);
                        break;
                    case MULTI_DEVICE_GROUP_APPLY:
                        str = context.getResources().getString(R.string.multi_device_group_apply);
                        break;
                    case MULTI_DEVICE_GROUP_APPLY_ACCEPT:
                        str = context.getResources().getString(R.string.multi_device_group_apply_accept);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_APPLY_DECLINE:
                        str = context.getResources().getString(R.string.multi_device_group_apply_decline);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_INVITE:
                        str = context.getResources().getString(R.string.multi_device_group_invite);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_INVITE_ACCEPT:
                        str = context.getResources().getString(R.string.multi_device_group_invite_accept);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_INVITE_DECLINE:
                        str = context.getResources().getString(R.string.multi_device_group_invite_decline);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_KICK:
                        str = context.getResources().getString(R.string.multi_device_group_kick);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_BAN:
                        str = context.getResources().getString(R.string.multi_device_group_ban);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_ALLOW:
                        str = context.getResources().getString(R.string.multi_device_group_allow);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_BLOCK:
                        str = context.getResources().getString(R.string.multi_device_group_block);
                        break;
                    case MULTI_DEVICE_GROUP_UNBLOCK:
                        str = context.getResources().getString(R.string.multi_device_group_unblock);
                        break;
                    case MULTI_DEVICE_GROUP_ASSIGN_OWNER:
                        str = context.getResources().getString(R.string.multi_device_group_assign_owner);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_ADD_ADMIN:
                        str = context.getResources().getString(R.string.multi_device_group_add_admin);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_REMOVE_ADMIN:
                        str = context.getResources().getString(R.string.multi_device_group_remove_admin);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_ADD_MUTE:
                        str = context.getResources().getString(R.string.multi_device_group_add_mute);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    case MULTI_DEVICE_GROUP_REMOVE_MUTE:
                        str = context.getResources().getString(R.string.multi_device_group_remove_mute);
                        str = String.format(str, msg.getGroupInviter());
                        break;
                    default:
                        break;
                }
                holder.message.setText(str);
            }
        }

        return convertView;
    }

    /**
     * accept invitation
     *
     * @param buttonAgree
     * @param msg
     */
    private void acceptInvitation(final Button buttonAgree, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_agree_with);
        final String str2 = context.getResources().getString(R.string.Has_agreed_to);
        final String str3 = context.getResources().getString(R.string.Agree_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMessageStatus.BEINVITEED) {//accept be friends
                        EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessageStatus.BEAPPLYED) { //accept application to join group
                        EMClient.getInstance().groupManager().acceptApplication(msg.getFrom(), msg.getGroupId());
                    } else if (msg.getStatus() == InviteMessageStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().acceptInvitation(msg.getGroupId(), msg.getGroupInviter());
                    }
                    msg.setStatus(InviteMessageStatus.AGREED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //修改备注
                            setMarkers(msg.getFrom(), editInput.getText().toString().trim());
                            Log.i("shiyangTest", "msg.getFrom(): " + msg.getFrom());
                            Log.i("shiyangTest", "editInput: " + editInput.getText().toString().trim());
                            pd.dismiss();
                            buttonAgree.setText(str2);
                            buttonAgree.setBackground(null);
                            buttonAgree.setEnabled(false);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }).start();
    }


    /**
     * 添加好友
     *
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
        map.put("zsa", "add_friend");
        map.put("zsv", "v1");
        map.put("timestamp", Utils.getTime());
        map.put("login_user_id", DemoApplication.getInstance().userInfor.getData().getId());
        map.put("platform", "android");
        map.put("app_type", "butler");
        map.put("sign", RequestCommand.getSign(map));
        OkHttpRequestUtils.sendPostRequest(RequestCommand.ALL_COMMON, map, this, null);
    }

    @Override
    public void onResponse(Object o) {
        Message msg = Message.obtain();
        msg.obj = o;
        msg.what = 1;
        handler.sendMessage(msg);
        Log.i("shiyangTest", "onResponse: " + o.toString());
    }

    @Override
    public void failure(String message) {
        Message msg = Message.obtain();
        msg.obj = message;
        msg.what = 2;
        handler.sendMessage(msg);
        Log.i("shiyangTest", "failure: " + message);
    }


    /**
     * decline invitation
     *
     * @param buttonAgree
     * @param buttonRefuse
     * @param msg
     */
    private void refuseInvitation(final Button buttonAgree, final Button buttonRefuse, final InviteMessage msg) {
        final ProgressDialog pd = new ProgressDialog(context);
        String str1 = context.getResources().getString(R.string.Are_refuse_with);
        final String str2 = context.getResources().getString(R.string.Has_refused_to);
        final String str3 = context.getResources().getString(R.string.Refuse_with_failure);
        pd.setMessage(str1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        new Thread(new Runnable() {
            public void run() {
                // call api
                try {
                    if (msg.getStatus() == InviteMessageStatus.BEINVITEED) {//decline the invitation
                        EMClient.getInstance().contactManager().declineInvitation(msg.getFrom());
                    } else if (msg.getStatus() == InviteMessageStatus.BEAPPLYED) { //decline application to join group
                        EMClient.getInstance().groupManager().declineApplication(msg.getFrom(), msg.getGroupId(), "");
                    } else if (msg.getStatus() == InviteMessageStatus.GROUPINVITATION) {
                        EMClient.getInstance().groupManager().declineInvitation(msg.getGroupId(), msg.getGroupInviter(), "");
                    }
                    msg.setStatus(InviteMessageStatus.REFUSED);
                    // update database
                    ContentValues values = new ContentValues();
                    values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
                    messgeDao.updateMessage(msg.getId(), values);
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            buttonRefuse.setText(str2);
                            buttonRefuse.setBackground(null);
                            buttonRefuse.setEnabled(false);

                            buttonAgree.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (final Exception e) {
                    ((Activity) context).runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(context, str3 + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        }).start();
    }


    private static class ViewHolder {
        ImageView avator;
        TextView name;
        TextView message;
        Button agreeBtn;
        TextView textAlreadyAgree;
//        Button refuseBtn;
    }

}
