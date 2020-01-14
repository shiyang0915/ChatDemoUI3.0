package com.hyphenate.fluter.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.fluter.Constant;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.db.InviteMessgeDao;
import com.hyphenate.fluter.robot_activity.ContactListActivity;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easeui.ui.EaseConversationListFragment;
import com.hyphenate.util.NetUtils;

public class ConversationListFragment extends EaseConversationListFragment {

    private TextView errorText;
    private SwipeMenuCreator creator;
    private ImageView imageContect;

    @Override
    protected void initView() {
        super.initView();
        View errorView = (LinearLayout) View.inflate(getActivity(), R.layout.em_chat_neterror_item, null);
        errorItemContainer.addView(errorView);
        errorText = (TextView) errorView.findViewById(R.id.tv_connect_errormsg);
        imageContect = getView().findViewById(R.id.image_contact);
        imageContect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactListActivity.class);
                startActivity(intent);
            }
        });


        imageAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
                NetUtils.hasDataConnection(getActivity());
            }
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // register context menu
        registerForContextMenu(conversationListView);
        conversationListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = conversationListView.getItem(position);
                String username = conversation.conversationId();
                if (username.equals(EMClient.getInstance().getCurrentUser()))
                    Toast.makeText(getActivity(), R.string.Cant_chat_with_yourself, Toast.LENGTH_SHORT).show();
                else {
                    // start chat acitivity
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversationType.ChatRoom) {
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        } else {
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }

                    }
                    // it's single chat
//                    intent.putExtra("titleName", titleName);
                    intent.putExtra(Constant.EXTRA_USER_ID, username);
                    startActivity(intent);
                }
            }
        });

        creatSwipeMenu();
        // set creator
        conversationListView.setMenuCreator(creator);
        onClickListener(conversationListView);
        super.setUpView();
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
        if (NetUtils.hasNetwork(getActivity())) {
            errorText.setText(R.string.can_not_connect_chat_server_connection);
        } else {
            errorText.setText(R.string.the_current_network);
        }
    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
//    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        boolean deleteMessage = false;
//        if (item.getItemId() == R.id.delete_message) {
//            deleteMessage = true;
//        } else if (item.getItemId() == R.id.delete_conversation) {
//            deleteMessage = false;
//        }
//        EMConversation tobeDeleteCons = conversationListView.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
//        if (tobeDeleteCons == null) {
//            return true;
//        }
//        if (tobeDeleteCons.getType() == EMConversationType.GroupChat) {
//            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
//        }
//        try {
//            // delete conversation
//            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
//            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
//            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
//            // To delete the native stored adked users in this conversation.
//            if (deleteMessage) {
//                EaseDingMessageHelper.get().delete(tobeDeleteCons);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        refresh();
//
//        // update unread count
//        ((MainActivity) getActivity()).updateUnreadLabel();
//        return true;
//    }


    /**
     * 创建策划的Menu
     */
    private void creatSwipeMenu() {
        creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext().
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px((int) (px2dip(((MainActivity) getActivity()).screenWidth) * 107.0) / 500));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(dp2px(px2dip(19)));
                deleteItem.setTitleColor(Color.parseColor("#FFFFFF"));
                // set a icon dp2px(90)
//                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
    }


    /**
     * 点击SwipMenu触发的事件
     *
     * @param swipeMenuListView
     */
    private void onClickListener(SwipeMenuListView swipeMenuListView) {
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                ApplicationInfo item = mAppList.get(position);
                switch (index) {
                    case 0:
                        // open
//                        open(item);
                        slideDeleteItem(position);
                        Log.i("shiyang", "delete");
                        break;
                    case 1:
                        // delete
//                        delete(item);
//                        mAppList.remove(position);
//                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        swipeMenuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // swipe start
//                Toast.makeText(getContext().getApplicationContext(),
//                        position + " swipe start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
//                Toast.makeText(getContext().getApplicationContext(),
//                        position + " swipe end", Toast.LENGTH_SHORT).show();
            }
        });

        // set MenuStateChangeListener
        swipeMenuListView.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
//                Toast.makeText(getContext().getApplicationContext(),
//                        position + " onMenuOpen", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMenuClose(int position) {
//                Toast.makeText(getContext().getApplicationContext(),
//                        position + " onMenuClose", Toast.LENGTH_SHORT).show();
            }
        });

        // other setting
//		listView.setCloseInterpolator(new BounceInterpolator());
        // test item long click
        swipeMenuListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
//                Toast.makeText(getContext().getApplicationContext(),
//                        position + " long click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    private void slideDeleteItem(int postion) {
        boolean deleteMessage = true;
        EMConversation tobeDeleteCons = conversationListView.getItem(postion);
        if (tobeDeleteCons == null) {
            return;
        }
        if (tobeDeleteCons.getType() == EMConversationType.GroupChat) {
            EaseAtMessageHelper.get().removeAtMeGroup(tobeDeleteCons.conversationId());
        }
        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
            InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
            // To delete the native stored adked users in this conversation.
            if (deleteMessage) {
                EaseDingMessageHelper.get().delete(tobeDeleteCons);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        // update unread count
        ((MainActivity) getActivity()).updateUnreadLabel();
    }


}
