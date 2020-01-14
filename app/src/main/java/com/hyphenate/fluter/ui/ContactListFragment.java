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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
//import com.hyphenate.chatuidemo.conference.ConferenceActivity;
//import com.hyphenate.chatuidemo.conference.LiveActivity;
import com.hyphenate.fluter.impl.ZszlFriendsContactChangeListener;
import com.hyphenate.fluter.robot_fragment.OwnerContactFragment;
import com.hyphenate.fluter.robot_fragment.RobotContactFragment;
import com.hyphenate.fluter.robot_presenter.ContactListFragmentPresenter;
import com.hyphenate.easeui.ui.EaseBaseFragment;

/**
 * contact list
 */
public class ContactListFragment extends EaseBaseFragment {

    public View view;
    private ContactListFragmentPresenter presenter;
    public RobotContactFragment robotContactFragment;
    public OwnerContactFragment ownerContactFragment;
    public TextView textNewFriendsNum;
    private RelativeLayout relativeNewFriend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ease_fragment_contact_list, container, false);
        robotContactFragment = new RobotContactFragment();
        ownerContactFragment = new OwnerContactFragment();
        presenter = new ContactListFragmentPresenter(this);
        presenter.getContactList();
        presenter.setZszlFriendsContactChangeListener(zszlFriendsContactChangeListener);
        textNewFriendsNum = view.findViewById(R.id.unread_msg_number);
        relativeNewFriend = view.findViewById(R.id.relative_new_firend);
        return view;
    }


    public ZszlFriendsContactChangeListener zszlFriendsContactChangeListener = new ZszlFriendsContactChangeListener() {
        @Override
        public void contactChange() {
            robotContactFragment.refresh();
            if (ownerContactFragment.contactListLayout != null) {
                ownerContactFragment.contactListLayout.refresh();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (DemoApplication.getInstance().contactListIsChange) {
            presenter.getContactList();
            DemoApplication.getInstance().contactListIsChange = false;
        }
    }

    @Override
    protected void initView() {
        relativeNewFriend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewFriendsMsgActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void setUpView() {

    }


}
