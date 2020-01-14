package com.hyphenate.fluter.robot_activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
//import com.hyphenate.chatuidemo.conference.ConferenceActivity;
import com.hyphenate.fluter.db.InviteMessgeDao;
import com.hyphenate.fluter.domain.EventBusCarrier;
import com.hyphenate.fluter.robot_utils.ChatView;
import com.hyphenate.fluter.ui.ContactListFragment;
import com.hyphenate.fluter.ui.NewFriendsMsgActivity;
import com.hyphenate.fluter.ui.VoiceCallActivity;
import com.hyphenate.easeui.MyActionBar;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListBaseActivity;
import com.hyphenate.util.EMLog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Hashtable;
import java.util.Map;

public class ContactListActivity extends EaseContactListBaseActivity {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    private ContactSyncListener contactSyncListener;
    private BlackListSyncListener blackListSyncListener;
    private ContactInfoSyncListener contactInfoSyncListener;
    private View loadingView;
    //    private ContactItemView applicationItem;
    private View NewFriendView;
    private TextView textApplicationCount;
    private InviteMessgeDao inviteMessgeDao;
    private MyActionBar actionBar;
    public ChatView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contact_list);
        EventBus.getDefault().register(this);
        chatView = new ChatView(this, R.mipmap.float_voice_call);
        chatView.hide();
        chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, VoiceCallActivity.class);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        });
    }


    @SuppressLint("InflateParams")
    @Override
    public void initView() {
        super.initView();
        @SuppressLint("InflateParams") View headerView = LayoutInflater.from(this).inflate(R.layout.em_contacts_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        NewFriendView = (View) headerView.findViewById(R.id.application_item);
        textApplicationCount = (TextView) NewFriendView.findViewById(R.id.unread_msg_number);
        NewFriendView.setOnClickListener(clickListener);
        headerView.findViewById(R.id.group_item).setOnClickListener(clickListener);
        headerView.findViewById(R.id.chat_room_item).setOnClickListener(clickListener);
        headerView.findViewById(R.id.robot_item).setOnClickListener(clickListener);
        headerView.findViewById(R.id.conference_item).setOnClickListener(clickListener);
        listView.addHeaderView(headerView);
        //add loading view
        loadingView = LayoutInflater.from(this).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);
        registerForContextMenu(listView);
        actionBar = findViewById(R.id.action_bar_contact);
        actionBar.setActionBar(R.mipmap.back, "通讯录", Color.parseColor("#030303"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);
    }


    // 普通事件的处理
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        String content = (String) carrier.getObject();
        chatView.hide();
    }

    @Override
    public void refresh() {
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            //noinspection unchecked
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        super.refresh();
        if (inviteMessgeDao == null) {
            inviteMessgeDao = new InviteMessgeDao(this);
        }
        if (inviteMessgeDao.getUnreadMessagesCount() > 0) {
            Log.i("shiyang_hahaha", "" + inviteMessgeDao.getUnreadMessagesCount());
//            applicationItem.showUnreadMsgView();
            textApplicationCount.setVisibility(View.VISIBLE);
            textApplicationCount.setText(inviteMessgeDao.getUnreadMessagesCount() + "");
        } else {
            textApplicationCount.setVisibility(View.GONE);
            Log.i("shiyang_hahaha", "《=0");
//            applicationItem.hideUnreadMsgView();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
            chatView.refreshLocation();
            chatView.show();
        } else {
            chatView.hide();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!DemoApplication.getInstance().voiceCallActivityDestory) {
            chatView.refreshLocation();
            chatView.show();
        } else {
            chatView.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            contactList.remove(toBeProcessUser);
//            contactListLayout.refresh();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setUpView() {
//        titleBar.setRightImageResource(R.drawable.em_add);
//        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                startActivity(new Intent(getActivity(), AddContactActivity.class));
//                NetUtils.hasDataConnection(getActivity());
//            }
//        });
        //设置联系人数据
        Map<String, EaseUser> m = DemoHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
        }
        setContactsMap(m);
        super.setUpView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toBeProcessUser = (EaseUser) listView.getItemAtPosition(position);
                if (toBeProcessUser != null) {
                    String username = toBeProcessUser.getUsername();
                    Intent intent = new Intent(ContactListActivity.this, MyFriendPersonalDataActivity.class);
                    intent.putExtra("userId", username);
                    DemoApplication.getInstance().easeUser = toBeProcessUser;
                    startActivityForResult(intent, 1);
                }
            }
        });


        // 进入添加好友页
//        titleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), AddContactActivity.class));
//            }
//        });


        contactSyncListener = new ContactSyncListener();
        DemoHelper.getInstance().addSyncContactListener(contactSyncListener);

        blackListSyncListener = new BlackListSyncListener();
        DemoHelper.getInstance().addSyncBlackListListener(blackListSyncListener);

        contactInfoSyncListener = new ContactInfoSyncListener();
        DemoHelper.getInstance().getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);

        if (DemoHelper.getInstance().isContactsSyncedWithServer()) {
            loadingView.setVisibility(View.GONE);
        } else if (DemoHelper.getInstance().isSyncingContactsWithServer()) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (contactSyncListener != null) {
            DemoHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }

        if (blackListSyncListener != null) {
            DemoHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }

        if (contactInfoSyncListener != null) {
            DemoHelper.getInstance().getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
    }


    protected class HeaderItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.application_item:
                    // 进入新朋友页面
                    startActivity(new Intent(ContactListActivity.this, NewFriendsMsgActivity.class));
                    break;
//                case R.id.group_item:
//                    // 进入群聊列表页面
//                    startActivity(new Intent(ContactListActivity.this, GroupsActivity.class));
//                    break;
//                case R.id.chat_room_item:
//                    //进入聊天室列表页面
//                    startActivity(new Intent(ContactListActivity.this, PublicChatRoomsActivity.class));
//                    break;
//                case R.id.robot_item:
//                    //进入Robot列表页面
//                    startActivity(new Intent(ContactListActivity.this, RobotsActivity.class));
//                    break;
//                case R.id.conference_item: // 创建音视频会议
//                    ConferenceActivity.startConferenceCall(ContactListActivity.this, null);
//                    break;
//                default:
//                    break;
            }
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);
        toBeProcessUsername = toBeProcessUser.getUsername();
        getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.delete_contact) {
//            try {
//                // delete contact
//                deleteContact(toBeProcessUser);
//                // remove invitation message
//                InviteMessgeDao dao = new InviteMessgeDao(ContactListActivity.this);
//                dao.deleteMessage(toBeProcessUser.getUsername());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        } else if (item.getItemId() == R.id.add_to_blacklist) {
//            moveToBlacklist(toBeProcessUsername);
//            return true;
//        }
        return super.onContextItemSelected(item);
    }


//    /**
//     * delete contact
//     *
//     * @param tobeDeleteUser
//     */
//    public void deleteContact(final EaseUser tobeDeleteUser) {
//        String st1 = getResources().getString(R.string.deleting);
//        final String st2 = getResources().getString(R.string.Delete_failed);
//        final ProgressDialog pd = new ProgressDialog(ContactListActivity.this);
//        pd.setMessage(st1);
//        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        new Thread(new Runnable() {
//            public void run() {
//                try {
//                    EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getUsername());
//                    // remove user from memory and database
//                    UserDao dao = new UserDao(ContactListActivity.this);
//                    dao.deleteContact(tobeDeleteUser.getUsername());
//                    DemoHelper.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            contactList.remove(tobeDeleteUser);
//                            contactListLayout.refresh();
//
//                        }
//                    });
//                } catch (final Exception e) {
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            pd.dismiss();
//                            Toast.makeText(ContactListActivity.this, st2 + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                }
//
//            }
//        }).start();
//
//    }

    class ContactSyncListener implements DemoHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            runOnUiThread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (success) {
                                loadingView.setVisibility(View.GONE);
                                refresh();
                            } else {
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(ContactListActivity.this, s1, Toast.LENGTH_LONG).show();
                                loadingView.setVisibility(View.GONE);
                            }
                        }

                    });
                }
            });
        }
    }

    class BlackListSyncListener implements DemoHelper.DataSyncListener {

        @Override
        public void onSyncComplete(boolean success) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    refresh();
                }
            });
        }
    }

    class ContactInfoSyncListener implements DemoHelper.DataSyncListener {

        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    loadingView.setVisibility(View.GONE);
                    if (success) {
                        refresh();
                    }
                }
            });
        }

    }


}
