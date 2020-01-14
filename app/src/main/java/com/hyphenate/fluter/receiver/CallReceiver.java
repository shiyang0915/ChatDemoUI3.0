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

package com.hyphenate.fluter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.fluter.DemoHelper;
import com.hyphenate.fluter.RemoteService;
import com.hyphenate.fluter.ui.VideoCallActivity;
import com.hyphenate.fluter.ui.VoiceCallActivity;
import com.hyphenate.util.EMLog;

public class CallReceiver extends BroadcastReceiver {

    private static Context context;
    private CallStateChangeListener callStateChangeListener;

    public void setCallStateChangeListener(CallStateChangeListener callStateChangeListener) {
        this.callStateChangeListener = callStateChangeListener;
    }

    public interface CallStateChangeListener {
        void voiceCallDisconnect();
    }


    final static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            context.getApplicationContext().startActivity(new Intent(context.getApplicationContext(), VoiceCallActivity.class).
                    putExtra("username", (String) msg.obj).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    };

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        if (!DemoHelper.getInstance().isLoggedIn())
            return;
        //username
        final String from = intent.getStringExtra("from");
        //call type
        final String type = intent.getStringExtra("type");

        //获取扩展内容
        String callExt = EMClient.getInstance().callManager().getCurrentCallSession().getExt();
        Log.e("测试", from + "//" + type + "//" + callExt);
        if (callExt != null && callExt.equals("remote")) {
            Log.e("测试", from + "//" + type + "//" + callExt);
            context.startService(new Intent(context, RemoteService.class).
                    putExtra("username", from).putExtra("isComingCall", true));
        } else if ("video".equals(type)) { //video call
            context.startActivity(new Intent(context, VideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else { //voice call
            Log.i("hfjshdkjfhfhsfshfk", "voice_call");
//            EMCallStateChangeListener callStateListener = new EMCallStateChangeListener() {
//                @Override
//                public void onCallStateChanged(CallState callState, CallError callError) {
//                    switch (callState) {
//                        case CONNECTED:
//                            if(!"video".equals(type)){
//                                //该状态表示已经建立连接，这个时候接起语音才能正常通话。
//                                Log.i("connectstate", "CONNECTED");
//                                Message message = Message.obtain();
//                                message.obj = from;
//                                handler.sendMessage(message);
//                            }
//                            break;
//                        case DISCONNECTED:
//                            if(!"video".equals(type)){
//                                EventBusCarrier eventBusCarrier = new EventBusCarrier();
//                                eventBusCarrier.setEventType("1");
//                                eventBusCarrier.setObject("我是TwoActivity发布的事件");
//                                EventBus.getDefault().post(eventBusCarrier); //普通事件发布
//                                Log.i("connectstate", "DISCONNECTED----");
//                            }
////                            callStateChangeListener.voiceCallDisconnect();
//                            break;
//                    }
//                }
//            };
//
//            EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
            context.startActivity(new Intent(context, VoiceCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        EMLog.d("CallReceiver", "app received a incoming call");
    }



}
