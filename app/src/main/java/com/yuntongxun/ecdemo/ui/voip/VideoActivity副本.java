//package com.yuntongxun.ecdemo.ui.voip;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffXfermode;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Chronometer;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.yuntongxun.ecdemo.R;
//import com.yuntongxun.ecdemo.common.CCPAppManager;
//import com.yuntongxun.ecdemo.common.dialog.ECAlertDialog;
//import com.yuntongxun.ecdemo.common.utils.DateUtil;
//import com.yuntongxun.ecdemo.common.utils.ECPreferenceSettings;
//import com.yuntongxun.ecdemo.common.utils.ECPreferences;
//import com.yuntongxun.ecdemo.common.utils.LogUtil;
//import com.yuntongxun.ecdemo.common.utils.ToastUtil;
//import com.yuntongxun.ecsdk.CameraCapability;
//import com.yuntongxun.ecsdk.CameraInfo;
//import com.yuntongxun.ecsdk.ECDevice;
//import com.yuntongxun.ecsdk.ECError;
//import com.yuntongxun.ecsdk.ECVoIPCallManager;
//import com.yuntongxun.ecsdk.ECVoIPSetupManager;
//import com.yuntongxun.ecsdk.SdkErrorCode;
//import com.yuntongxun.ecsdk.VideoRatio;
//import com.yuntongxun.ecsdk.voip.video.ECCaptureView;
//import com.yuntongxun.ecsdk.voip.video.ECOpenGlView;
//import com.yuntongxun.ecsdk.voip.video.OnCameraInitListener;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// *
// */
//public class VideoActivity extends ECVoIPBaseActivity implements View.OnClickListener {
//
//    private static final String TAG = "VideoActivity";
//    private static long lastClickTime;
//    private Button mVideoStop;
//    private Button mVideoBegin;
//    private Button mVideoCancle;
//    private ImageView mVideoIcon;
//    private RelativeLayout mVideoTipsLy;
//    private ImageView mDiaerpadBtn;
//    public LinearLayout daiLayout;
//
//    private TextView mVideoTopTips;
//    private TextView mVideoCallTips;
//    private TextView mCallStatus;
//    private ECOpenGlView mRemoteView;
//    private ECOpenGlView mSelfGlView;
//    private ECOpenGlView mThirdGlView;
//    // Remote Video
//    private FrameLayout mVideoLayout;
//    private Chronometer mChronometer;
//
//    private View mCameraSwitch;
//    private View start;
//    private View stop;
//    private View invite;
//    private View kill;
//    private View video_switch;
//    private View request;
//    private View zhuanjie;
//    private View pause;
//    private View resume;
//    private ECCaptureView mCaptureView;
//    /**
//     * 当前呼叫类型对应的布局
//     */
//    RelativeLayout mCallRoot;
//    private boolean mMaxSizeRemote = true;
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ec_video_call;
//    }
//
//    @Override
//    protected boolean isEnableSwipe() {
//        return false;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        initVideoLayout();
//        isCreated = true;
//    }
//
//    private void initVideoLayout() {
//        if (mIncomingCall) {
//            // 来电
//            mCallId = getIntent().getStringExtra(ECDevice.CALLID);
//            mCallNumber = getIntent().getStringExtra(ECDevice.CALLER);
//        } else {
//            // 呼出
//            mCallName = getIntent().getStringExtra(EXTRA_CALL_NAME);
//            mCallNumber = getIntent().getStringExtra(EXTRA_CALL_NUMBER);
//        }
//
//        initResourceRefs();
//
//
//        setCaptureView(mCaptureView);
//        attachGlView();
//        if (!mIncomingCall) {
//            mVideoTopTips.setText(R.string.ec_voip_call_connecting_server);
//            mCallId = VoIPCallHelper.makeCall(mCallType, mCallNumber);
//        } else {
//            mVideoCancle.setVisibility(View.GONE);
//            mVideoTipsLy.setVisibility(View.VISIBLE);
//            mVideoBegin.setVisibility(View.VISIBLE);
//            mVideoTopTips.setText((mCallName == null ? mCallNumber : mCallName) + getString(R.string.ec_voip_invited_video_tip));
//            mVideoTopTips.setVisibility(View.VISIBLE);
//        }
//
//        if (mIncomingCall) {
//            mVideoStop.setEnabled(true);
//        }
//    }
//
//    public void setCaptureView(ECCaptureView captureView) {
//        ECVoIPSetupManager setUpMgr = ECDevice.getECVoIPSetupManager();
//        if (setUpMgr != null) {
//            setUpMgr.setCaptureView(captureView);
//        }
//        addCaptureView(captureView);
//    }
//
//    /**
//     * 添加预览到视频通话界面上
//     *
//     * @param captureView 预览界面
//     */
//    private void addCaptureView(ECCaptureView captureView) {
//        if (mCallRoot != null && captureView != null) {
//            mCallRoot.removeView(mCaptureView);
//            mCaptureView = null;
//            mCaptureView = captureView;
//            mCallRoot.addView(captureView, new RelativeLayout.LayoutParams(1, 1));
//            mCaptureView.setVisibility(View.VISIBLE);
//            LogUtil.d(TAG, "CaptureView added");
//        }
//    }
//
//    private void attachGlView() {
//        ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
//        if(setupManager == null) {
//            return ;
//        }
//        if(mMaxSizeRemote) {
//            setupManager.setGlDisplayWindow(mSelfGlView , mRemoteView);
//        } else {
//            setupManager.setGlDisplayWindow(mRemoteView, mSelfGlView);
//        }
//    }
//
//    private void initResourceRefs() {
//        mCallRoot = (RelativeLayout) findViewById(R.id.video_root);
//        mVideoTipsLy = (RelativeLayout) findViewById(R.id.video_call_in_ly);
//        mVideoIcon = (ImageView) findViewById(R.id.video_icon);
//
//        mVideoTopTips = (TextView) findViewById(R.id.notice_tips);
//        mVideoCallTips = (TextView) findViewById(R.id.video_call_tips);
//        mVideoCancle = (Button) findViewById(R.id.video_botton_cancle);
//        mVideoBegin = (Button) findViewById(R.id.video_botton_begin);
//        mVideoStop = (Button) findViewById(R.id.video_stop);
//        mDmfInput = (EditText) findViewById(R.id.dial_input_numer_TXT);
//        mDiaerpadBtn = (ImageView) findViewById(R.id.layout_call_dialnum);
//        mDiaerpadBtn.setOnClickListener(this);
//        daiLayout = (LinearLayout) findViewById(R.id.layout_dial_panel);
//
//        setupKeypad();
//
//        mVideoStop.setEnabled(false);
//
//        mVideoCancle.setOnClickListener(this);
//        mVideoBegin.setOnClickListener(this);
//        mVideoStop.setOnClickListener(this);
//
//        mRemoteView = (ECOpenGlView) findViewById(R.id.video_view);
//        mRemoteView.setVisibility(View.INVISIBLE);
//        mRemoteView.setGlType(ECOpenGlView.RenderType.RENDER_REMOTE);
//        mRemoteView.setAspectMode(ECOpenGlView.AspectMode.CROP);
//
//
//        mThirdGlView = (ECOpenGlView) findViewById(R.id.three_view);
//        mThirdGlView.setVisibility(View.VISIBLE);
//        mThirdGlView.setZOrderOnTop(true);
//        mThirdGlView.setZOrderMediaOverlay(true);
//
//
//
//        mSelfGlView = (ECOpenGlView) findViewById(R.id.localvideo_view);
//        mSelfGlView.setGlType(ECOpenGlView.RenderType.RENDER_PREVIEW);
//        mSelfGlView.setAspectMode(ECOpenGlView.AspectMode.CROP);
//        mSelfGlView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMaxSizeRemote = !mMaxSizeRemote;
//                attachGlView();
//            }
//        });
//
//
//
//
//
//        mCaptureView = new ECCaptureView(this);
//        mCaptureView.setOnCameraInitListener(new OnCameraInitListener() {
//            @Override
//            public void onCameraInit(boolean result) {
//                if (!result) ToastUtil.showMessage("摄像头被占用");
//            }
//        });
//        mVideoLayout = (FrameLayout) findViewById(R.id.Video_layout);
//        mCameraSwitch = findViewById(R.id.camera_switch);
//        mCameraSwitch.setOnClickListener(this);
//
//
//        start = findViewById(R.id.start_recorder);
//        start.setOnClickListener(this);
//
//        stop = findViewById(R.id.stop_recorder);
//        stop.setOnClickListener(this);
//
//        invite = findViewById(R.id.camera_invite);
//        invite.setOnClickListener(this);
//
//        zhuanjie = findViewById(R.id.zhuanjie_voip);
//        zhuanjie.setOnClickListener(this);
//
//
//        request = findViewById(R.id.three_voip);
//        request.setOnClickListener(this);
//
//        kill = findViewById(R.id.kill_voip);
//        kill.setOnClickListener(this);
//
//        pause = findViewById(R.id.three_pause);
//        pause.setOnClickListener(this);
//
//        resume = findViewById(R.id.three_resume);
//        resume.setOnClickListener(this);
//
//
//
//
//
//
//
//        video_switch = findViewById(R.id.video_switch);
//        video_switch.setOnClickListener(this);
//
//        mCallStatus = (TextView) findViewById(R.id.call_status);
//        mCallStatus.setVisibility(View.GONE);
//    }
//
//    private void initResVideoSuccess() {
//        isConnect = true;
//        mVideoLayout.setVisibility(View.VISIBLE);
//        mVideoIcon.setVisibility(View.GONE);
//        mVideoTopTips.setVisibility(View.GONE);
//        mCameraSwitch.setVisibility(View.VISIBLE);
//        mVideoTipsLy.setVisibility(View.VISIBLE);
//        mVideoBegin.setVisibility(View.GONE);
//        // bottom ...
//        mVideoCancle.setVisibility(View.GONE);
//        mVideoCallTips.setVisibility(View.VISIBLE);
//        mVideoCallTips.setText(getString(R.string.str_video_bottom_time, mCallNumber));
//        mVideoStop.setVisibility(View.VISIBLE);
//        mVideoStop.setEnabled(true);
//
//        mCaptureView.setVisibility(View.VISIBLE);
//
//        mChronometer = (Chronometer) findViewById(R.id.chronometer);
//        mChronometer.setBase(SystemClock.elapsedRealtime());
//        mChronometer.setVisibility(View.VISIBLE);
//        mChronometer.start();
////         mDiaerpadBtn.setVisibility(View.VISIBLE);
//        mDiaerpadBtn.setEnabled(false);
//
//
//        start.setVisibility(View.VISIBLE);
//        stop.setVisibility(View.VISIBLE);
//        invite.setVisibility(View.VISIBLE);
//        kill.setVisibility(View.VISIBLE);
//        zhuanjie.setVisibility(View.VISIBLE);
//        request.setVisibility(View.VISIBLE);
//        pause.setVisibility(View.VISIBLE);
//        resume.setVisibility(View.VISIBLE);
//
//    }
//
//
//    /**
//     * 根据状态,修改按钮属性及关闭操作
//     */
//    private void finishCalling() {
//        try {
//            // mChronometer.setVisibility(View.GONE);
//
//            mVideoTopTips.setVisibility(View.VISIBLE);
//            mCameraSwitch.setVisibility(View.GONE);
//            mVideoTopTips.setText(R.string.ec_voip_calling_finish);
//
//            if (isConnect) {
//                // set Chronometer view gone..
//                mChronometer.stop();
//                mVideoLayout.setVisibility(View.GONE);
//                mVideoIcon.setVisibility(View.VISIBLE);
//
//                mCaptureView.setVisibility(View.GONE);
//
//                // bottom can't click ...
//                mVideoStop.setEnabled(false);
//            } else {
//                mVideoCancle.setEnabled(false);
//            }
//            finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            isConnect = false;
//        }
//    }
//
//    private void finishCalling(int reason) {
//        try {
//            mVideoTopTips.setVisibility(View.VISIBLE);
//            mCameraSwitch.setVisibility(View.GONE);
//            mCaptureView.setVisibility(View.GONE);
//            mDiaerpadBtn.setVisibility(View.GONE);
//            if (isConnect) {
//                mChronometer.stop();
//                mVideoLayout.setVisibility(View.GONE);
//                mVideoIcon.setVisibility(View.VISIBLE);
//                isConnect = false;
//                // bottom can't click ...
//                mVideoStop.setEnabled(false);
//            } else {
//                mVideoCancle.setEnabled(false);
//            }
//            isConnect = false;
//            mVideoTopTips.setText(CallFailReason.getCallFailReason(reason));
//            VoIPCallHelper.releaseCall(mCallId);
//            finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (mCallType == ECVoIPCallManager.CallType.VIDEO) {
//            String ratio = ECPreferences
//                    .getSharedPreferences()
//                    .getString(
//                            ECPreferenceSettings.SETTINGS_RATIO_CUSTOM.getId(),
//                            (String) ECPreferenceSettings.SETTINGS_RATIO_CUSTOM
//                                    .getDefaultValue());
//
//            if (!TextUtils.isEmpty(ratio)) {
//                String[] arr = ratio.split("\\*");
//                int capIndex = getCampIndex(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
//                if (mCaptureView != null) {
//                    mCaptureView.setLocalResolutionRatio(Integer.parseInt(arr[2]), capIndex);
//                }
//            } else {
//                if (mCaptureView != null) {
//                    mCaptureView.onResume();
//                }
//            }
//        }
//    }
//
//    private int getCampIndex(int width, int height, int index) {
//        int sum = 0;
//        ECVoIPSetupManager voIPSetupManager = ECDevice.getECVoIPSetupManager();
//        if (voIPSetupManager == null) {
//            return -1;
//        }
//        CameraInfo[] infos = voIPSetupManager.getCameraInfos();
//        for (int i = 0; i < infos.length; i++) {
//
//            CameraCapability[] arr = infos[i].caps;
//
//            for (int j = 0; j < arr.length; j++) {
//
//                if (index == i && width == arr[j].width && height == arr[j].height) {
//                    sum = j;
//                }
//            }
//        }
//        return sum;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        VoIPCallHelper.mHandlerVideoCall = false;
//        isThree = false;
//        isCreated = false;
//        if(mThirdGlView!=null){
//            mThirdGlView.destroyDrawingCache();
//        }
//        mThirdGlView = null;
//
//    }
//
//    @Override
//    public void onCallProceeding(String callId) {
//        if (callId != null && callId.equals(mCallId)) {
//            mVideoTopTips.setText(getString(R.string.ec_voip_call_connect));
//        }
//    }
//
//    @Override
//    public void onCallAlerting(String callId) {
//        if (callId != null && callId.equals(mCallId)) {// 等待对方接受邀请...
//            mVideoTopTips.setText(getString(R.string.str_tips_wait_invited));
//        }
//    }
//
//    @Override
//    public void onCallAnswered(String callId) {
//        if (callId != null && callId.equals(mCallId) && !isConnect) {
//            initResVideoSuccess();
//            ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
//            get();
//
//        }
//    }
//
//    @Override
//    public void onMakeCallFailed(String callId, int reason) {
//        if (callId != null && callId.equals(mCallId)) {
//            finishCalling(reason);
//
//        }
//    }
//
//    @Override
//    public void onCallReleased(String callId) {
//        if(isThree) {
//            ECDevice.getECVoIPCallManager().cancelCurrentThreePartMemberVideo(new ECVoIPCallManager.OnThirdMemberVideoListener() {
//                @Override
//                public void onResult(ECError ecError) {
//
//                    if (mThirdGlView != null) {
//                    }
//                    if (ecError.errorCode == 0) {
//                        LogUtil.e("取消视频成功----");
//                    } else {
//                        LogUtil.e("取消视频失败----");
//                    }
//                }
//            });
//        }
//        if (callId != null && callId.equals(mCallId)) {
//            VoIPCallHelper.releaseMuteAndHandFree();
//            finishCalling();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.video_botton_begin:
//                VoIPCallHelper.acceptCall(mCallId);
//                break;
//
//            case R.id.video_stop:
//            case R.id.video_botton_cancle:
//
//                doHandUpReleaseCall();
//                break;
//            case R.id.camera_switch:
//                mCameraSwitch.setEnabled(false);
//                if (mCaptureView != null) {
//                    mCaptureView.switchCamera();
//                }
//                mCameraSwitch.setEnabled(true);//selftest
//                break;
//            case R.id.layout_call_dialnum:
//                setDialerpadUI();
//                break;
//
//            case R.id.start_recorder:
//                JSONObject o = new JSONObject();
//                try {
//                    o.put("callSid",map.get("sid"));//必选
//                    o.put("recordName", DateUtil.formatNowDate(new Date())+".mp4");//文件名 必选
//                    o.put("resolution","1280 720"); //分辨率 可选
//                    o.put("recordPath","/www/app"); //分辨率 可选
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ECDevice.getECVoIPCallManager().sendCmdToRest(3, o.toString(), "", new ECVoIPCallManager.OnSendCmdListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                    }
//                });
//                break;
//            case R.id.stop_recorder:
//                JSONObject o2 = new JSONObject();
//                try {
//                    o2.put("callSid",map.get("sid"));//必选
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ECDevice.getECVoIPCallManager().sendCmdToRest(4, o2.toString(), "", new ECVoIPCallManager.OnSendCmdListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                    }
//                });
//
//                break;
//            case R.id.camera_invite:
//
//                showInputCodeDialog(null,
//                        getString(R.string.videomeeting_invite_member), false);
//                break;
//            case R.id.zhuanjie_voip:
//
//                showInputCodeDialog2(null,
//                        getString(R.string.videomeeting_invite_member), false);
//                break;
//            case R.id.three_voip:
//
//
//                if(mThirdGlView!=null){
//                    clearScreen(mThirdGlView);
//
//                }
//                    try{
//                        get();
//                        if(mThirdGlView!=null){
//                            mThirdGlView.setVisibility(View.VISIBLE);
//                        }
//                        ECDevice.getECVoIPCallManager().requestCurrentThreePartMemberVideo(mThirdGlView, new ECVoIPCallManager.OnThirdMemberVideoListener() {
//                            @Override
//                            public void onResult(ECError ecError) {
//
//                                if(mThirdGlView!=null){
//                                    mThirdGlView.setVisibility(View.VISIBLE);
//                                }
//
//                                if (ecError.errorCode == SdkErrorCode.REQUEST_SUCCESS) {
//                                    LogUtil.e("请求视频成功----");
//                                } else {
//                                    LogUtil.e("请求视频失败----");
//                                }
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//
//                break;
//
//            case R.id.kill_voip:
//
//                JSONObject o3 = new JSONObject();
//                try {
//                    o3.put("callSid",map.get("sid"));//必选
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ECDevice.getECVoIPCallManager().sendCmdToRest(5, o3.toString(), "", new ECVoIPCallManager.OnSendCmdListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                    }
//                });
//
//                break;
//
//
//            case R.id.three_pause:
//
//                ECDevice.getECVoIPCallManager().pauseCall(mCallId, new ECVoIPCallManager.OnPauseCallListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//                        Log.e("aaaaaa",ecError.toString());
//                    }
//                });
//
//                break;
//
//            case R.id.three_resume:
//
//                ECDevice.getECVoIPCallManager().resumeCall(mCallId, new ECVoIPCallManager.OnResumeCallListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                        Log.e("aaaaa",ecError.toString());
//                    }
//                });
//
//
//                break;
//
//
//            default:
//                onKeyBordClick(v.getId());
//                break;
//        }
//    }
//
//    protected void showInputCodeDialog( String title, String message , final boolean isLandCall) {
//        View view = View.inflate(this , R.layout.dialog_edit_context , null);
//        final EditText editText = (EditText) view.findViewById(R.id.sendrequest_content);
//        ((TextView) view.findViewById(R.id.sendrequest_tip)).setText(message);
//        ECAlertDialog dialog = ECAlertDialog.buildAlert(this, message, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                handleInput(editText , isLandCall);
//            }
//        });
//        dialog.setContentView(view);
//        dialog.setTitle(title);
//        dialog.show();
//    }
//    protected void showInputCodeDialog2( String title, String message , final boolean isLandCall) {
//        View view = View.inflate(this , R.layout.dialog_edit_context , null);
//        final EditText editText = (EditText) view.findViewById(R.id.sendrequest_content);
//        ((TextView) view.findViewById(R.id.sendrequest_tip)).setText(message);
//        ECAlertDialog dialog = ECAlertDialog.buildAlert(this, message, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                handleInput2(editText, isLandCall);
//            }
//        });
//        dialog.setContentView(view);
//        dialog.setTitle(title);
//        dialog.show();
//    }
//
//    protected void handleInput(android.widget.EditText editText , boolean isLandCall) {
//
//        if(editText!=null){
//            String text=editText.getText().toString().trim();
//            if(!TextUtils.isEmpty(text)){
//                ECDevice.getECVoIPCallManager().inviteJoinThreePartConf(mCallId, text, CCPAppManager.getUserId(), new ECVoIPCallManager.OnThreeInviteListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                        Log.e("aa", ecError.toString());
//                    }
//                });
//            }
//        }
//
//    };
//    protected void handleInput2(android.widget.EditText editText , boolean isLandCall) {
//
//        if(editText!=null){
//            String text=editText.getText().toString().trim();
//            if(!TextUtils.isEmpty(text)){
//                JSONObject o = new JSONObject();
//                try {
//                    o.put("callSid",map.get("sid"));
//                    o.put("number",text);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                ECDevice.getECVoIPCallManager().sendCmdToRest(6, o.toString(), "", new ECVoIPCallManager.OnSendCmdListener() {
//                    @Override
//                    public void onResult(ECError ecError) {
//
//                    }
//                });
//
//
//            }
//        }
//
//    };
//
//
//    private void onKeyBordClick(int id) {
//        switch (id) {
//            case R.id.zero: {
//                keyPressed(KeyEvent.KEYCODE_0);
//                return;
//            }
//            case R.id.one: {
//                keyPressed(KeyEvent.KEYCODE_1);
//                return;
//            }
//            case R.id.two: {
//                keyPressed(KeyEvent.KEYCODE_2);
//                return;
//            }
//            case R.id.three: {
//                keyPressed(KeyEvent.KEYCODE_3);
//                return;
//            }
//            case R.id.four: {
//                keyPressed(KeyEvent.KEYCODE_4);
//                return;
//            }
//            case R.id.five: {
//                keyPressed(KeyEvent.KEYCODE_5);
//                return;
//            }
//            case R.id.six: {
//                keyPressed(KeyEvent.KEYCODE_6);
//                return;
//            }
//            case R.id.seven: {
//                keyPressed(KeyEvent.KEYCODE_7);
//                return;
//            }
//            case R.id.eight: {
//                keyPressed(KeyEvent.KEYCODE_8);
//                return;
//            }
//            case R.id.nine: {
//                keyPressed(KeyEvent.KEYCODE_9);
//                return;
//            }
//            case R.id.star: {
//                keyPressed(KeyEvent.KEYCODE_STAR);
//                return;
//            }
//            case R.id.pound: {
//                keyPressed(KeyEvent.KEYCODE_POUND);
//                return;
//            }
//        }
//    }
//
//    private EditText mDmfInput;
//
//    void keyPressed(int keyCode) {
//        KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
//        mDmfInput.getText().clear();
//        mDmfInput.onKeyDown(keyCode, event);
//        sendDTMF(mDmfInput.getText().toString().toCharArray()[0]);
//    }
//
//
//    protected void doHandUpReleaseCall() {
//
//        // Hang up the video call...
//        LogUtil.d(TAG,
//                "[VideoActivity] onClick: Voip talk hand up, CurrentCallId " + mCallId);
//        try {
//            if (mCallId != null) {
//
//                if (mIncomingCall && !isConnect) {
//                    VoIPCallHelper.rejectCall(mCallId);
//                } else {
//                    VoIPCallHelper.releaseCall(mCallId);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (!isConnect) {
//            finish();
//        }
//    }
//
//    private void setupKeypad() {
//        /** Setup the listeners for the buttons */
//        findViewById(R.id.zero).setOnClickListener(this);
//        findViewById(R.id.one).setOnClickListener(this);
//        findViewById(R.id.two).setOnClickListener(this);
//        findViewById(R.id.three).setOnClickListener(this);
//        findViewById(R.id.four).setOnClickListener(this);
//        findViewById(R.id.five).setOnClickListener(this);
//        findViewById(R.id.six).setOnClickListener(this);
//        findViewById(R.id.seven).setOnClickListener(this);
//        findViewById(R.id.eight).setOnClickListener(this);
//        findViewById(R.id.nine).setOnClickListener(this);
//        findViewById(R.id.star).setOnClickListener(this);
//        findViewById(R.id.pound).setOnClickListener(this);
//    }
//
//    @Override
//    public void onMakeCallback(ECError arg0, String arg1, String arg2) {
//
//    }
//
//    public boolean isCreated = false;
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//
//        if (!isCreated) {
//            setIntent(intent);
//            super.onNewIntent(intent);
//            initVideoLayout();
//        }
//    }
//
//    /**
//     * 远端视频分辨率到达，标识收到视频图像
//     *
//     * @param videoRatio 视频分辨率信息
//     */
//    @Override
//    public void onVideoRatioChanged(VideoRatio videoRatio) {
//        super.onVideoRatioChanged(videoRatio);
//        /*if(mVideoView != null && videoRatio != null) {
//            mVideoView.getHolder().setFixedSize(videoRatio.getWidth() , videoRatio.getHeight());
//        }*/
//        if (videoRatio == null) {
//            return;
//        }
//        int width = videoRatio.getWidth();
//        int height = videoRatio.getHeight();
//        if (width == 0 || height == 0) {
//            LogUtil.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
//            return;
//        }
//        mRemoteView.setVisibility(View.VISIBLE);
//        if (width > height) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int mSurfaceViewWidth = dm.widthPixels;
//            int mSurfaceViewHeight = dm.heightPixels;
//            int w = mSurfaceViewWidth * height / width;
//            int margin = (mSurfaceViewHeight - mVideoTipsLy.getHeight() - w) / 2;
//            LogUtil.d(TAG, "margin:" + margin);
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            lp.setMargins(0, margin, 0, margin);
//            mRemoteView.setLayoutParams(lp);
//        }
//    }
//
//    private Map map = new HashMap<String,String>();
//
//
//    private void get(){
//        String s = ECDevice.getECVoIPCallManager().getUserData(3,mCallId);
//        if(!TextUtils.isEmpty(s)){
//            if(s.contains(";")) {
//                String[] arr = s.split("\\;");
//                if (arr != null) {
//                    for (String item : arr) {
//                        if (!TextUtils.isEmpty(item) && item.startsWith("servercallid")){
//                            String[] arr2 = item.split("\\=");
//                            if (arr2 != null && arr2.length == 2) {
//                                map.put("sid", arr2[1]);
//                            }
//                        }
//                    }
//                }
//            }else {
//                if(s.startsWith("servercallid=")){
//                    String [] arr = s.split("\\=");
//                    if(arr!=null&&arr.length==2){
//                        map.put("sid",arr[1]);
//                    }
//                }
//            }
//        }
//    }
//
//
//    public  boolean isThree =false;
//
//
//
//    @Override
//    public void onReceiveThreePartConfNotify(String member, int var) {//selftest
//        Log.e(TAG, "member = " + member + ",var = " + var);
//
//        if(var==601){
//            isThree = true;
//        }
//        if(mThirdGlView!=null){
//            mThirdGlView.clearAnimation();
//            clearScreen(mThirdGlView);
//            mThirdGlView.destroyDrawingCache();
//            mThirdGlView.refreshDrawableState();
//        }
//        if(var==602){
//            ECDevice.getECVoIPCallManager().cancelCurrentThreePartMemberVideo(new ECVoIPCallManager.OnThirdMemberVideoListener() {
//                @Override
//                public void onResult(ECError ecError) {
//
//                    if(mThirdGlView!=null){
//                        mThirdGlView.setVisibility(View.GONE);
//                    }
//                    if(ecError.errorCode== 0){
//                        LogUtil.e("取消视频成功----");
//                    }else {
//                        LogUtil.e("取消视频失败----");
//                    }
//                }
//            });
//        }
//    }
//
//    private String thirdCallId;
//
//    @Override
//    public void onIncomingCallReceived(String callId, int type, String caller) {
//
//        thirdCallId = callId;
//
//    }
//
//
//    private void clearScreen(SurfaceView surfaceView) {
//        if(surfaceView == null) {
//            return ;
//        }
//        Paint p = new Paint();
//        // 清屏
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        Canvas lockCanvas = surfaceView.getHolder().lockCanvas();
//        if(lockCanvas == null) {
//            return ;
//        }
//        lockCanvas.drawPaint(p);
//        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
//        surfaceView.getHolder().unlockCanvasAndPost(lockCanvas);
//        surfaceView.invalidate();
//        Log.e("aaaaaaaaa","clear sv--------");
//    }
//
//    public void setDialerpadUI() {
//        daiLayout.setVisibility(daiLayout.getVisibility() != View.GONE ? View.GONE : View.VISIBLE);
//    }
//
//
//}
