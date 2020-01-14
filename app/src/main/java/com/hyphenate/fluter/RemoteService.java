package com.hyphenate.fluter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
//import android.hardware.RobotManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVideoCallHelper;
import com.hyphenate.fluter.utils.PhoneStateManager;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.List;
import java.util.UUID;

/**
 * @author lck
 * @data 2019/2/25
 */
public class RemoteService extends Service {
//        private RemoteActivity.BrightnessDataProcess dataProcessor = new RemoteActivity.BrightnessDataProcess();
    private Handler uiHandler;
    private String msgid;
    private boolean isInComingCall;
    private String username;
    private EMCallStateChangeListener callStateListener;
    private int surfaceState = -1;
    private int outgoing;
    private int streamID = -1;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private Ringtone ringtone;
    private EMVideoCallHelper callHelper;
    private int callType = 0;
    protected final int MSG_CALL_MAKE_VIDEO = 0;
    protected final int MSG_CALL_MAKE_VOICE = 1;
    protected final int MSG_CALL_ANSWER = 2;
    protected final int MSG_CALL_REJECT = 3;
    protected final int MSG_CALL_END = 4;
    protected final int MSG_CALL_RELEASE_HANDLER = 5;
    protected final int MSG_CALL_SWITCH_CAMERA = 6;
    protected CallingState callingState = CallingState.CANCELLED;
    private boolean isMuteState;
//    private RobotManager robotManager;

    // dynamic adjust brightness
//    class BrightnessDataProcess implements EMCallManager.EMCameraDataProcessor {
//        byte yDelta = 0;
//
//        synchronized void setYDelta(byte yDelta) {
//            Log.d("VideoCallActivity", "brigntness uDelta:" + yDelta);
//            this.yDelta = yDelta;
//        }
//
//        // data size is width*height*2
//        // the first width*height is Y, second part is UV
//        // the storage layout detailed please refer 2.x demo CameraHelper.onPreviewFrame
//        @Override
//        public synchronized void onProcessData(byte[] data, Camera camera, final int width, final int height, final int rotateAngel) {
//            int wh = width * height;
//            for (int i = 0; i < wh; i++) {
//                int d = (data[i] & 0xFF) + yDelta;
//                d = d < 16 ? 16 : d;
//                d = d > 235 ? 235 : d;
//                data[i] = (byte) d;
//            }
//        }
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand invoke");
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        msgid = UUID.randomUUID().toString();
        isInComingCall = intent.getBooleanExtra("isComingCall", false);
        username = intent.getStringExtra("username");
        Log.e("im", isInComingCall + "//" + username);
        // set call state listener
        addCallStateListener(intent);
        handler.removeCallbacks(timeoutHangup);
        callHelper = EMClient.getInstance().callManager().getVideoCallHelper();

//        handler.sendEmptyMessage(MSG_CALL_ANSWER);


        EMClient.getInstance().chatManager().addMessageListener(msgListener);
//        robotManager = (RobotManager) getSystemService(Context.ROBOT_SERVICE);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        System.out.println("onDestroy invoke");
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDestroy();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
//            Log.e("透传消息","afhahf");
//            for(int a=0;a<messages.size();a++){
//                String action = messages.get(a).getBody().toString().substring(5, messages.get(a).getBody().toString().length() - 1);
//                if (action.equals("GOON")){
//                    robotManager.robot_motor_control(1,0);
//                }else if (action.equals("GOBACK")){
//                    robotManager.robot_motor_control(2,0);
//                }else if (action.equals("GOLEFT")){
//                    robotManager.robot_motor_control(3,0);
//                }else if (action.equals("GORIGHT")){
//                    robotManager.robot_motor_control(4,0);
//                }else if (action.equals("GOSTOP")){
//                    robotManager.robot_motor_control(5,0);
//                }else if (action.equals("STOPSERVICE")){
//                    Log.e("关闭服务","fdks");
//
//                }
//                Log.e("透传消息",messages.get(a).getBody().describeContents()+"//"+messages.get(a).getBody().toString().substring(5,messages.get(a).getBody().toString().length()-1));
//            }
//            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    void addCallStateListener(final Intent intent) {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(final CallState callState, final CallError error) {
                Log.e("正在接通中","fdff/"+callState);
                switch (callState) {
                    case CONNECTING: // is connecting
                        Log.e("正在接通中","fdff");
                        break;
                    case CONNECTED: // connected
                        handler.sendEmptyMessage(MSG_CALL_ANSWER);
                        break;

                    case ACCEPTED: // call is accepted
//                    surfaceState = 0;
                        handler.removeCallbacks(timeoutHangup);
                        try {
                            if (soundPool != null)
                                soundPool.stop(streamID);
                            EMLog.d("EMCallManager", "soundPool stop ACCEPTED");
                        } catch (Exception e) {
                        }

                        PhoneStateManager.get(getApplicationContext()).addStateCallback(phoneStateCallback);


                        break;
                    case NETWORK_DISCONNECTED:

                        break;
                    case NETWORK_UNSTABLE:

                        break;
                    case NETWORK_NORMAL:

                        break;
                    case VIDEO_PAUSE:

                        break;
                    case VIDEO_RESUME:

                        break;
                    case VOICE_PAUSE:

                        break;
                    case VOICE_RESUME:

                        break;
                    case DISCONNECTED: // call is disconnected
                        EMClient.getInstance().callManager().removeCallStateChangeListener(callStateListener);
                        handler.removeCallbacks(timeoutHangup);
                        PhoneStateManager.get(getApplicationContext()).removeStateCallback(phoneStateCallback);
                        saveCallRecord();
                        stopService(new Intent(RemoteService.this, RemoteService.class));

                        break;

                    default:
                        break;
                }

            }
        };
        EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
    }

    Runnable timeoutHangup = new Runnable() {

        @Override
        public void run() {
            new Handler().sendEmptyMessage(MSG_CALL_REJECT);
        }
    };

    HandlerThread callHandlerThread = new HandlerThread("callHandlerThread");

    {
        callHandlerThread.start();
    }

    protected Handler handler = new Handler(callHandlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            EMLog.d("EMCallManager CallActivity", "handleMessage ---enter block--- msg.what:" + msg.what);
            switch (msg.what) {
                case MSG_CALL_MAKE_VIDEO:
                case MSG_CALL_MAKE_VOICE:
                    try {
                        if (msg.what == MSG_CALL_MAKE_VIDEO) {
                            EMClient.getInstance().callManager().makeVideoCall(username);
                        } else {
                            EMClient.getInstance().callManager().makeVoiceCall(username);
                        }
                    } catch (final EMServiceNotReadyException e) {
                        e.printStackTrace();

                        String st2 = e.getMessage();
                        if (e.getErrorCode() == EMError.CALL_REMOTE_OFFLINE) {
                            st2 = getResources().getString(R.string.The_other_is_not_online);
                        } else if (e.getErrorCode() == EMError.USER_NOT_LOGIN) {
                            st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
                        } else if (e.getErrorCode() == EMError.INVALID_USER_NAME) {
                            st2 = getResources().getString(R.string.illegal_user_name);
                        } else if (e.getErrorCode() == EMError.CALL_BUSY) {
                            st2 = getResources().getString(R.string.The_other_is_on_the_phone);
                        } else if (e.getErrorCode() == EMError.NETWORK_ERROR) {
                            st2 = getResources().getString(R.string.can_not_connect_chat_server_connection);
                        }


                    }
                    break;
                case MSG_CALL_ANSWER:
                    if (ringtone != null)
                        ringtone.stop();
                    if (isInComingCall) {
                        try {
                            EMClient.getInstance().callManager().answerCall();
                            // meizu MX5 4G, hasDataConnection(context) return status is incorrect
                            // MX5 con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() return false in 4G
                            // so we will not judge it, App can decide whether judge the network status

//                        if (NetUtils.hasDataConnection(CallActivity.this)) {
//                            EMClient.getInstance().callManager().answerCall();
//                            isAnswered = true;
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
//                                    Toast.makeText(CallActivity.this, st2, Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            throw new Exception();
//                        }
                        } catch (Exception e) {
                            e.printStackTrace();
                            saveCallRecord();
                            return;
                        }
                    } else {
                    }
                    break;
                case MSG_CALL_REJECT:
                    if (ringtone != null)
                        ringtone.stop();
                    try {
                        EMClient.getInstance().callManager().rejectCall();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        saveCallRecord();
                    }
                    callingState = CallingState.REFUSED;
                    break;
                case MSG_CALL_END:
                    if (soundPool != null)
                        soundPool.stop(streamID);
                    EMLog.d("EMCallManager", "soundPool stop MSG_CALL_END");
                    try {
                        EMClient.getInstance().callManager().endCall();
                    } catch (Exception e) {
                        saveCallRecord();
                    }

                    break;
                case MSG_CALL_RELEASE_HANDLER:
                    try {
                        EMClient.getInstance().callManager().endCall();
                    } catch (Exception e) {
                    }
                    handler.removeCallbacks(timeoutHangup);
                    handler.removeMessages(MSG_CALL_MAKE_VIDEO);
                    handler.removeMessages(MSG_CALL_MAKE_VOICE);
                    handler.removeMessages(MSG_CALL_ANSWER);
                    handler.removeMessages(MSG_CALL_REJECT);
                    handler.removeMessages(MSG_CALL_END);
                    callHandlerThread.quit();
                    break;
                case MSG_CALL_SWITCH_CAMERA:
                    EMClient.getInstance().callManager().switchCamera();
                    break;
                default:
                    break;
            }
            EMLog.d("EMCallManager CallActivity", "handleMessage ---exit block--- msg.what:" + msg.what);
        }
    };

    /**
     * save call record
     */
    protected void saveCallRecord() {
        @SuppressWarnings("UnusedAssignment") EMMessage message = null;
        @SuppressWarnings("UnusedAssignment") EMTextMessageBody txtBody = null;
        if (!isInComingCall) { // outgoing call
            message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setTo(username);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(username);
        }

        String st1 = getResources().getString(R.string.call_duration);
        String st2 = getResources().getString(R.string.Refused);
        String st3 = getResources().getString(R.string.The_other_party_has_refused_to);
        String st4 = getResources().getString(R.string.The_other_is_not_online);
        String st5 = getResources().getString(R.string.The_other_is_on_the_phone);
        String st6 = getResources().getString(R.string.The_other_party_did_not_answer);
        String st7 = getResources().getString(R.string.did_not_answer);
        String st8 = getResources().getString(R.string.Has_been_cancelled);
        switch (callingState) {
            case NORMAL:
//                txtBody = new EMTextMessageBody(st1 + callDruationText);
                txtBody = new EMTextMessageBody(st1);
                break;
            case REFUSED:
                txtBody = new EMTextMessageBody(st2);
                break;
            case BEREFUSED:
                txtBody = new EMTextMessageBody(st3);
                break;
            case OFFLINE:
                txtBody = new EMTextMessageBody(st4);
                break;
            case BUSY:
                txtBody = new EMTextMessageBody(st5);
                break;
            case NO_RESPONSE:
                txtBody = new EMTextMessageBody(st6);
                break;
            case UNANSWERED:
                txtBody = new EMTextMessageBody(st7);
                break;
            case VERSION_NOT_SAME:
                txtBody = new EMTextMessageBody(getString(R.string.call_version_inconsistent));
                break;
            default:
                txtBody = new EMTextMessageBody(st8);
                break;
        }
        // set message extension
        if (callType == 0)
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, true);
        else
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, true);

        // set message body
        message.addBody(txtBody);
        message.setMsgId(msgid);
        message.setStatus(EMMessage.Status.SUCCESS);

        // save
        EMClient.getInstance().chatManager().saveMessage(message);
    }

    enum CallingState {
        CANCELLED, NORMAL, REFUSED, BEREFUSED, UNANSWERED, OFFLINE, NO_RESPONSE, BUSY, VERSION_NOT_SAME
    }

    PhoneStateManager.PhoneStateCallback phoneStateCallback = new PhoneStateManager.PhoneStateCallback() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:   // 电话响铃
                    break;
                case TelephonyManager.CALL_STATE_IDLE:      // 电话挂断
                    // resume current voice conference.
                    if (isMuteState) {
                        try {
                            EMClient.getInstance().callManager().resumeVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().resumeVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:   // 来电接通 或者 去电，去电接通  但是没法区分
                    // pause current voice conference.
                    if (!isMuteState) {
                        try {
                            EMClient.getInstance().callManager().pauseVoiceTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        try {
                            EMClient.getInstance().callManager().pauseVideoTransfer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    };

}
