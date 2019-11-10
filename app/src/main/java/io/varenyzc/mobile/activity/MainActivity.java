package io.varenyzc.mobile.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telephony.VisualVoicemailService;
import android.telephony.VisualVoicemailSms;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.api.XHConstants;
import com.starrtc.starrtcsdk.api.XHCustomConfig;
import com.starrtc.starrtcsdk.api.XHLiveManager;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.player.IStarVideoCallback;
import com.starrtc.starrtcsdk.core.player.StarPlayer;
import com.starrtc.starrtcsdk.core.player.StarPlayerScaleType;
import com.starrtc.starrtcsdk.core.player.StarWhitePanel;
import com.starrtc.starrtcsdk.core.pusher.XHCameraRecorder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.varenyzc.mobile.HandleView;
import io.varenyzc.mobile.R;
import io.varenyzc.mobile.listener.XHLiveManagerListener;
import io.varenyzc.mobile.utils.AEvent;
import io.varenyzc.mobile.utils.DensityUtils;
import io.varenyzc.mobile.utils.IEventListener;
import io.varenyzc.mobile.utils.MLOC;
import io.varenyzc.mobile.view.CircularCoverView;
import io.varenyzc.mobile.view.EditDialog;
import io.varenyzc.mobile.view.MyDialog;
import io.varenyzc.mobile.view.ViewPosition;

/**
 * @author varenyzc
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IEventListener {

    public static String CREATER_ID         = "CREATER_ID";          //创建者ID
    public static String LIVE_ID            = "LIVE_ID";            //直播ID
    public static String LIVE_NAME          = "LIVE_NAME";          //直播名称

    private XHLiveManager liveManager;
    private String createrId;
    private String liveId;

    private HandleView cameraOp;
    private StarPlayer mStarPlayer;
    private ArrayList<ViewPosition> mPlayerList;
    private int borderW = 0;
    private int borderH = 0;

    private RelativeLayout vPlayerView;
    private Boolean isPortrait = false;

    private TextView close;
    private TextView reset;

    private String currentSpeed = "200";
    private String tmpSpeed = "200";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initContrlPanel();
        close = findViewById(R.id.ctrl_close);
        reset = findViewById(R.id.ctrl_reset);
        vPlayerView = findViewById(R.id.view1);
        mStarPlayer = findViewById(R.id.player);
        mPlayerList = new ArrayList<>();
        borderW = DensityUtils.screenWidth(this);
        borderH = borderW/3*4;

        close.setOnClickListener(this);
        reset.setOnClickListener(this);
        createrId = getIntent().getStringExtra(CREATER_ID);
        liveId = getIntent().getStringExtra(LIVE_ID);
        Log.d("varenyzc1", liveId);
        addListener();
        liveManager = XHClient.getInstance().getLiveManager(this);
        liveManager.setRtcMediaType(XHConstants.XHRtcMediaTypeEnum.STAR_RTC_MEDIA_TYPE_VIDEO_ONLY);
        liveManager.setRecorder(new XHCameraRecorder());
        liveManager.addListener(new XHLiveManagerListener());
        joinLive();
    }

    private void addListener() {
        AEvent.addListener(AEvent.AEVENT_LIVE_ADD_UPLOADER,this);
    }

    private void joinLive() {
        liveManager.watchLive(liveId, new IXHResultCallback() {
            @Override
            public void success(Object o) {

            }

            @Override
            public void failed(String s) {

            }
        });
    }

    private void initContrlPanel() {
        cameraOp = findViewById(R.id.cameraOp);
        cameraOp.setHandleReaction(new HandleView.HandleReaction() {
            String lastData = "camera==";
            @Override
            public void report(float h, float v) {
                MLOC.d("IOTCAR","h:"+h+" v:"+v);

                String data;
                if(h<0.33){
                    if(v<0.33){
                        data  = "camera+-";
                    }else if(v>0.66){
                        data  = "camera++";
                    }else{
                        data  = "camera+=";
                    }
                }else if(h>0.66){
                    if(v<0.33){
                        data  = "camera--";
                    }else if(v>0.66){
                        data  = "camera-+";
                    }else{
                        data  = "camera-=";
                    }
                }else{
                    if(v<0.33){
                        data  = "camera=-";
                    }else if(v>0.66){
                        data  = "camera=+";
                    }else{
                        data  = "camera==";
                    }
                }
                if(!data.equals(lastData)){
                    lastData = data;
                    byte[] dataBytes = data.getBytes();
                    //StarRtcCore.getInstance().a(dataBytes,dataBytes.length);
                    liveManager.sendMessage(data, null);
                }
            }
        });


        View.OnTouchListener touchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()){
                            case R.id.ctrl_up:
                                byte[] up = "up".getBytes();
                                liveManager.sendMessage("1", null);
                                break;
                            case R.id.ctrl_down:
                                byte[] down = "down".getBytes();
                                liveManager.sendMessage("2", null);
                                break;
                            case R.id.ctrl_left:
                                byte[] left = "left".getBytes();
                                liveManager.sendMessage("3", null);
                                break;
                            case R.id.ctrl_right:
                                byte[] right = "right".getBytes();
                                liveManager.sendMessage("4", null);
                                break;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        byte[] stop = "stop".getBytes();
                        liveManager.sendMessage("0", null);
                        break;
                }
                return true;
            }
        };

        findViewById(R.id.ctrl_up).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_down).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_left).setOnTouchListener(touchListener);
        findViewById(R.id.ctrl_right).setOnTouchListener(touchListener);

        TextView speed = findViewById(R.id.ctrl_speed);
        speed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog myDialog = new MyDialog(MainActivity.this, R.layout.speed_dialog, new int[]{R.id.tv_comfirm,R.id.tv_cancel});
                myDialog.setOnConfirmListener(new MyDialog.OnConfirmClickListener() {
                    @Override
                    public void OnClick(MyDialog dialog, View view,int progress) {
                        currentSpeed = tmpSpeed;
                        if(progress+70<100){
                            liveManager.sendMessage("0"+currentSpeed, null);
                        }else{
                            liveManager.sendMessage(currentSpeed, null);
                        }
                        Toast.makeText(MainActivity.this, "速度修改成功！", Toast.LENGTH_SHORT).show();
                    }
                });
                myDialog.setOnSeekBarChangeListener(new MyDialog.OnSeekBarChangeListener() {
                    @Override
                    public void OnChange(MyDialog dialog, View view, int progress) {
                        tmpSpeed = ((Integer)(progress+70)).toString();
                        dialog.setSpeedText(tmpSpeed+"0");
                    }
                });
                myDialog.show();
                if(Integer.valueOf(currentSpeed)>300){
                    currentSpeed = "300";
                }
                myDialog.setProgress(currentSpeed);
            }
        });
        speed.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EditDialog dialog = new EditDialog(MainActivity.this, R.layout.edit_dialog, new int[]{R.id.tv_comfirm, R.id.tv_cancel});
                dialog.setOnConfirmListener(new EditDialog.OnConfirmClickListener() {
                    @Override
                    public void OnClick(EditDialog dialog, View view, String speed) {
                        tmpSpeed = speed.substring(0, speed.length() - 1);
                        //Log.d("vareenyzc1", "OnClick: "+currentSpeed);
                        if (Integer.valueOf(tmpSpeed) < 70 || Integer.valueOf(tmpSpeed) > 999) {
                            Toast.makeText(MainActivity.this, "数值错误，请重试", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            currentSpeed = tmpSpeed;
                        }
                        if (Integer.valueOf(currentSpeed) < 100) {
                            liveManager.sendMessage("0" + currentSpeed, null);
                        } else {
                            liveManager.sendMessage(currentSpeed, null);
                        }
                        Toast.makeText(MainActivity.this, "速度修改成功！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.setSpeedText(currentSpeed+"0");
                return true;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctrl_close:
                //liveManager.sendMessage("close", null);
                finish();
                break;
            case R.id.ctrl_reset:
                liveManager.sendMessage("cameraReset", null);
                break;
        }
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        MLOC.d("1", "dispatch" + aEventID + eventObj);

        switch (aEventID) {
            case AEvent.AEVENT_LIVE_ADD_UPLOADER:
                try {
                    JSONObject data = (JSONObject) eventObj;
                    final String addId = data.getString("actorID");
                    addPlayer(addId);
                    /*runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            liveManager.attachPlayerView(addId,mStarPlayer,true);
                        }
                    });*/
                } catch (JSONException e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void addPlayer(String addUserID){
        ViewPosition newOne = new ViewPosition();
        newOne.setUserId(addUserID);
        StarPlayer player = new StarPlayer(this);
        newOne.setVideoPlayer(player);
        mPlayerList.add(newOne);
        vPlayerView.addView(player);
        CircularCoverView coverView = new CircularCoverView(this);
        coverView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        coverView.setCoverColor(Color.BLACK);
        coverView.setRadians(35, 35, 35, 35,10);
        player.addView(coverView);
        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout(v);
            }
        });
        resetLayout();
        player.setZOrderMediaOverlay(true);
        player.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);

        if(mPlayerList.size()==1){
            liveManager.attachPlayerView(addUserID,player,true);
        }else{
            liveManager.attachPlayerView(addUserID,player,false);
        }
    }

    private void resetLayout(){
        if(isPortrait){
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:
                {
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3*2,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/3*2);
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i == 0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW-borderW/3,borderH-borderH/4);
                            player.setLayoutParams(lp);
                        }else if(i>0&&i<3){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                            player.setLayoutParams(lp);
                            player.setX(borderW-borderW/3);
                            player.setY((i-1)*borderH/4);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/3,borderH/4);
                            player.setLayoutParams(lp);
                            player.setX((i-3)*borderW/3);
                            player.setY(borderH-borderH/4);
                        }
                    }
                    break;
                }
            }
        }else{
            switch (mPlayerList.size()){
                case 1:{
                    StarPlayer player = mPlayerList.get(0).getVideoPlayer();
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW,borderH);
                    player.setLayoutParams(lp);
                    player.setY(0);
                    player.setX(0);
                    break;
                }
                case 2:
                case 3:
                case 4:
                {
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*3,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*3);
                            player.setScalType(StarPlayerScaleType.DRAW_TYPE_TOTAL_GRAPH);
                        }
                    }
                    break;
                }
                case 5:
                case 6:
                case 7:{
                    for(int i = 0;i<mPlayerList.size();i++){
                        if(i==0){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4*2,borderH);
                            player.setLayoutParams(lp);
                            player.setY(0);
                            player.setX(0);
                        }else if(i>0&&i<3){
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-1)*borderH/3);
                            player.setX(borderW/4*2);
                        }else{
                            StarPlayer player = mPlayerList.get(i).getVideoPlayer();
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(borderW/4,borderH/3);
                            player.setLayoutParams(lp);
                            player.setY((i-3)*borderH/3);
                            player.setX(borderW/4*3);
                        }
                    }
                    break;
                }
            }
        }


    }

    private boolean isRuning = false;
    private void changeLayout(View v){
        if(isRuning) return;
        if(v == mPlayerList.get(0).getVideoPlayer())return;
        ViewPosition clickPlayer = null;
        int clickIndex = 0;
        for (int i=0;i<mPlayerList.size();i++){
            if(mPlayerList.get(i).getVideoPlayer()==v){
                clickIndex = i;
                clickPlayer = mPlayerList.remove(i);
                liveManager.changeToBig(clickPlayer.getUserId());
                break;
            }
        }
        final ViewPosition mainPlayer = mPlayerList.remove(0);
        liveManager.changeToSmall(mainPlayer.getUserId());
        mPlayerList.remove(clickPlayer);
        mPlayerList.add(0, clickPlayer);
        mPlayerList.add(clickIndex,mainPlayer);

        final ViewPosition finalClickPlayer = clickPlayer;
        startAnimation(finalClickPlayer.getVideoPlayer(),mainPlayer.getVideoPlayer());
    }

    private void startAnimation(final StarPlayer clickPlayer, final StarPlayer mainPlayer){
        final float clickStartW = clickPlayer.getWidth();
        final float clickStartH = clickPlayer.getHeight();
        final float clickEndW = mainPlayer.getWidth();
        final float clickEndH = mainPlayer.getHeight();
        final float mainStartW = mainPlayer.getWidth();
        final float mainStartH = mainPlayer.getHeight();
        final float mainEndW = clickPlayer.getWidth();
        final float mainEndH = clickPlayer.getHeight();

        final float clickStartX = clickPlayer.getX();
        final float clickStartY = clickPlayer.getY();
        final float clickEndX = mainPlayer.getX();
        final float clickEndY = mainPlayer.getY();
        final float mainStartX = mainPlayer.getX();
        final float mainStartY = mainPlayer.getY();
        final float mainEndX = clickPlayer.getX();
        final float mainEndY = clickPlayer.getY();

        if(XHCustomConfig.getInstance().getOpenGLESEnable()){
            clickPlayer.setX(clickEndX);
            clickPlayer.setY(clickEndY);
            clickPlayer.getLayoutParams().width = (int) clickEndW;
            clickPlayer.getLayoutParams().height = (int)clickEndH;
            clickPlayer.requestLayout();

            mainPlayer.setX(mainEndX);
            mainPlayer.setY(mainEndY);
            mainPlayer.getLayoutParams().width = (int) mainEndW;
            mainPlayer.getLayoutParams().height = (int) mainEndH;
            mainPlayer.requestLayout();
        }else{

            final ValueAnimator valTotal  = ValueAnimator.ofFloat(0f,1f);
            valTotal.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    clickPlayer.setX(clickStartX + (Float) animation.getAnimatedValue()*(clickEndX - clickStartX));
                    clickPlayer.setY(clickStartY + (Float) animation.getAnimatedValue()*(clickEndY - clickStartY));
                    clickPlayer.getLayoutParams().width = (int) (clickStartW + (Float) animation.getAnimatedValue()*(clickEndW - clickStartW));
                    clickPlayer.getLayoutParams().height = (int) (clickStartH + (Float) animation.getAnimatedValue()*(clickEndH - clickStartH));
                    clickPlayer.requestLayout();

                    mainPlayer.setX(mainStartX + (Float) animation.getAnimatedValue()*(mainEndX - mainStartX));
                    mainPlayer.setY(mainStartY + (Float) animation.getAnimatedValue()*(mainEndY - mainStartY));
                    mainPlayer.getLayoutParams().width = (int) (mainStartW + (Float) animation.getAnimatedValue()*(mainEndW - mainStartW));
                    mainPlayer.getLayoutParams().height = (int) (mainStartH + (Float) animation.getAnimatedValue()*(mainEndH - mainStartH));
                    mainPlayer.requestLayout();
                }
            });

            valTotal.setDuration(300);
            valTotal.setInterpolator(new LinearInterpolator());
            valTotal.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isRuning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isRuning = false;
                    clickPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                    mainPlayer.setScalType(StarPlayerScaleType.DRAW_TYPE_CENTER);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    isRuning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valTotal.start();
        }
    }
}
