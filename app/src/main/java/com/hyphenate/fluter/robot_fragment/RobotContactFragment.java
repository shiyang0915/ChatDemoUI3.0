package com.hyphenate.fluter.robot_fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_activity.MyFriendPersonalDataActivity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.MyFriends;
import com.hyphenate.easeui.ui.EaseBaseFragment;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.easeui.widget.EaseContactList;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hyphenate.easeui.widget.EaseContactList.ROBOT_CONTACT;

/**
 * 机器人联系列表
 */
public class RobotContactFragment extends EaseBaseFragment {

    private static final String TAG = "EaseContactListFragment";
    private List<MyFriends.DataBean.RobotBean> robotContactList = new ArrayList<>();

    protected ListView listView;
    protected boolean hidden;
    protected ImageButton clearSearch;
    protected EditText query;
    protected Handler handler = new Handler();
    public EaseContactList contactListLayout;
    protected boolean isConflict;
    private Map<String, EaseUser> contactsMap;
    private ContactSyncListener contactSyncListener;
    private BlackListSyncListener blackListSyncListener;
    private ContactInfoSyncListener contactInfoSyncListener;
    private View loadingView;
    //    private InviteMessgeDao inviteMessgeDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_robot_contact, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //to avoid crash when open app after long time stay in background after user logged into another device
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        contactListLayout = (EaseContactList) getView().findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
        Log.i(TAG, "initView: " + "contactListLayout创建了。。。。");
        query = (EditText) getView().findViewById(com.hyphenate.easeui.R.id.query);
        clearSearch = (ImageButton) getView().findViewById(com.hyphenate.easeui.R.id.search_clear);
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contactListLayout.init(DemoApplication.getInstance().myFriends, ROBOT_CONTACT);
    }


    protected void setUpViewFirst() {
        EMClient.getInstance().addConnectionListener(connectionListener);
        if (listItemClickListener != null) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    EaseUser user = (EaseUser) listView.getItemAtPosition(position);
//                    listItemClickListener.onListItemClicked(user);
                }
            });
        }

        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                contactListLayout.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
//            refresh();
            Log.i(TAG, "onHiddenChanged: robot");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        refresh();
        contactListLayout.refresh();
    }


    /**
     * move user to blacklist
     */
    protected void moveToBlacklist(final String username) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        String st1 = getResources().getString(com.hyphenate.easeui.R.string.Is_moved_into_blacklist);
        final String st2 = getResources().getString(com.hyphenate.easeui.R.string.Move_into_blacklist_success);
        final String st3 = getResources().getString(com.hyphenate.easeui.R.string.Move_into_blacklist_failure);
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    //move to blacklist
                    EMClient.getInstance().contactManager().addUserToBlackList(username, false);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                            refresh();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getActivity(), st3, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }


    public void refresh() {
        if(contactListLayout!=null){
            contactListLayout.refresh();
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void setUpView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyFriends.DataBean.RobotBean user = (MyFriends.DataBean.RobotBean) listView.getItemAtPosition(position);
                if (user != null) {
//                    String friend = user.getFriend();
//                    这个是别名
//                    String titleName = user.getFriend_name();
                    Intent intent = new Intent(getActivity(), MyFriendPersonalDataActivity.class);
//                    intent.putExtra("userId", friend);
//                    intent.putExtra("titleName", titleName);
                    intent.putExtra("friends", user);
                    intent.putExtra("flag", "robot");
                    startActivity(intent);
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

        EMClient.getInstance().removeConnectionListener(connectionListener);
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
        super.onDestroy();
    }


    class ContactSyncListener implements DemoHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (success) {
                                loadingView.setVisibility(View.GONE);
                                refresh();
                            } else {
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, Toast.LENGTH_LONG).show();
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
            getActivity().runOnUiThread(new Runnable() {

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
            getActivity().runOnUiThread(new Runnable() {

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


//    /**
//     * get contact list and sort, will filter out users in blacklist
//     */
//    protected void getContactList() {
////        contactList.clear();
//        if (contactsMap == null) {
//            return;
//        }
//        synchronized (this.contactsMap) {
//            Iterator<Map.Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
//            List<String> blackList = EMClient.getInstance().contactManager().getBlackListUsernames();
//            while (iterator.hasNext()) {
//                Map.Entry<String, EaseUser> entry = iterator.next();
//                // to make it compatible with data in previous version, you can remove this check if this is new app
//                if (!entry.getKey().equals("item_new_friends")
//                        && !entry.getKey().equals("item_groups")
//                        && !entry.getKey().equals("item_chatroom")
//                        && !entry.getKey().equals("item_robots")) {
//                    if (!blackList.contains(entry.getKey())) {
//                        //filter out users in blacklist
//                        EaseUser user = entry.getValue();
//                        EaseCommonUtils.setUserInitialLetter(user);
////                        contactList.add(user);
//                    }
//                }
//            }
//        }

//        // sorting
//        Collections.sort(contactList, new Comparator<EaseUser>() {
//
//            @Override
//            public int compare(EaseUser lhs, EaseUser rhs) {
//                if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
//                    return lhs.getNickname().compareTo(rhs.getNickname());
//                } else {
//                    if ("#".equals(lhs.getInitialLetter())) {
//                        return 1;
//                    } else if ("#".equals(rhs.getInitialLetter())) {
//                        return -1;
//                    }
//                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
//                }
//
//            }
//        });

//    }


    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED) {
                isConflict = true;
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        onConnectionDisconnected();
                    }

                });
            }
        }

        @Override
        public void onConnected() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    onConnectionConnected();
                }

            });
        }
    };
    private EaseContactListFragment.EaseContactListItemClickListener listItemClickListener;


    protected void onConnectionDisconnected() {

    }

    protected void onConnectionConnected() {

    }

    /**
     * set contacts map, key is the hyphenate id
     *
     * @param contactsMap
     */
    public void setContactsMap(Map<String, EaseUser> contactsMap) {
        this.contactsMap = contactsMap;
    }

    public interface EaseContactListItemClickListener {
        /**
         * on click event for item in contact list
         *
         * @param user --the user of item
         */
        void onListItemClicked(EaseUser user);
    }

    /**
     * set contact list item click listener
     *
     * @param listItemClickListener
     */
    public void setContactListItemClickListener(EaseContactListFragment.EaseContactListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

}
