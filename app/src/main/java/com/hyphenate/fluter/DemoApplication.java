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
package com.hyphenate.fluter;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.hyphenate.fluter.domain.UserInfor;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.domain.MyFriends;
import com.hyphenate.push.EMPushHelper;
import com.hyphenate.push.EMPushType;
import com.hyphenate.push.PushListener;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.List;

public class DemoApplication extends Application {

    public static Context applicationContext;
    private static DemoApplication instance;
    public UserInfor userInfor;
    public MyFriends myFriends;
    //联系人列表是否发生变化
    public boolean contactListIsChange;
    // login user name
    public final String PREF_USERNAME = "username";
    public EaseUser easeUser;

    public float chatViewX;
    public float chatViewY;
    public float mTouchStartX;
    public float mTouchStartY;

    public boolean voiceCallActivityDestory = true;

    /**
     * 为了做退出登录，将MainActivity和SettingActivity放入集合
     */
    public List<Activity> activityList = new ArrayList<>();


    /**
     * nickname for current user, the nickname instead of ID be shown when user receive notification from APNs
     */
    public static String currentUserNick = "";

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;

        //init demo helper
        DemoHelper.getInstance().init(applicationContext);

        // 请确保环信SDK相关方法运行在主进程，子进程不会初始化环信SDK（该逻辑在EaseUI.java中）
        if (EaseUI.getInstance().isMainProcess(this)) {
            // 初始化华为 HMS 推送服务, 需要在SDK初始化后执行
//            HMSPushHelper.getInstance().initHMSAgent(instance);

            EMPushHelper.getInstance().setPushListener(new PushListener() {
                @Override
                public void onError(EMPushType pushType, long errorCode) {
                    // TODO: 返回的errorCode仅9xx为环信内部错误，可从EMError中查询，其他错误请根据pushType去相应第三方推送网站查询。
                    EMLog.e("PushClient", "Push client occur a error: " + pushType + " - " + errorCode);
                }
            });
        }
    }

    public static DemoApplication getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static final String shiyangTest = "haha";
}