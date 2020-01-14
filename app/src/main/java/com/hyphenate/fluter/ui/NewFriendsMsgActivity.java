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

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.adapter.NewFriendsMsgAdapter;
import com.hyphenate.fluter.db.InviteMessgeDao;
import com.hyphenate.fluter.domain.InviteMessage;
import com.hyphenate.fluter.robot_utils.NormalDialog;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Application and notification
 */
public class NewFriendsMsgActivity extends BaseActivity {
    private com.hyphenate.easeui.MyActionBar actionBar;
    private NormalDialog dialog;
    private WeakReference<NewFriendsMsgActivity> activityWeakReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_friends_msg);
        activityWeakReference = new WeakReference<NewFriendsMsgActivity>(this);
        View dialogView = getLayoutInflater().inflate(R.layout.set_remarks_dialog, null);
        dialog = new NormalDialog(this, dialogView);
        dialog.setCanceledOnTouchOutside(false);
        final ListView listView = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        Collections.reverse(msgs);

        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1,
                msgs, dialog, dialogView, activityWeakReference);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);

        actionBar = findViewById(R.id.action_bar_new_friends);
        actionBar.setActionBar(R.mipmap.back, "新朋友", Color.parseColor("#030303"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                InviteMessage msg = (InviteMessage) listView.getAdapter().getItem(position);
//                editInput.setText(msg.getGroupName());
            }
        });


    }


    public void back(View view) {
        finish();
    }
}
