package io.varenyzc.mobile.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.TextView;

import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;

import java.util.ArrayList;
import java.util.List;

import io.varenyzc.mobile.R;
import io.varenyzc.mobile.service.KeepLiveService;
import io.varenyzc.mobile.utils.AEvent;
import io.varenyzc.mobile.utils.IEventListener;
import io.varenyzc.mobile.utils.MLOC;

/**
 * @author varenyzc
 */
public class SplashActivity extends AppCompatActivity implements IEventListener {

    private boolean isLogin = false;
    private TextView start;

    private final String carId = "mycar";
    private String waitCarId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        start = findViewById(R.id.star_car_0);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waitCarId = carId;
                XHClient.getInstance().getChatManager().sendOnlineMessage("MyCarStart", carId, new IXHResultCallback() {
                    @Override
                    public void success(Object o) {

                    }

                    @Override
                    public void failed(final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MLOC.showMsg(SplashActivity.this,s);
                            }
                        });
                    }
                });
            }
        });
        AEvent.setHandler(new Handler());
        addListener();
        checkPermission();
    }

    private void addListener() {
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG, this);
    }

    private void removeListener() {
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG, this);
    }

    private int times = 0;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private void checkPermission() {
        times++;
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if ((checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.CAMERA);
            if ((checkSelfPermission(Manifest.permission.BLUETOOTH)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.BLUETOOTH);
            if ((checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0){
                if(times==1){
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_PHONE_PERMISSIONS);
                }else{
                    new AlertDialog.Builder(this)
                            .setCancelable(true)
                            .setTitle("提示")
                            .setMessage("获取不到授权，APP将无法正常使用，请允许APP获取权限！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_PHONE_PERMISSIONS);
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).show();
                }
            }else{
                initSDK();
            }
        }else{
            initSDK();
        }
    }

    private void initSDK(){
        startService();
        startAnimation();
    }

    private void startService(){
        Intent intent = new Intent(SplashActivity.this, KeepLiveService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermission();
    }

    @SuppressLint("WrongConstant")
    private void startAnimation(){
        isLogin = true;
        final View eye = findViewById(R.id.logo_black);
        eye.setAlpha(0.2f);
        final View black = findViewById(R.id.black_view);
        final View white = findViewById(R.id.white_view);

        final ObjectAnimator va = ObjectAnimator.ofFloat(eye,"alpha",0.2f,1f);
        va.setDuration(1000);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(Animation.REVERSE);
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if(isLogin){
                    va.cancel();
                    ObjectAnimator va1 = ObjectAnimator.ofFloat(white,"alpha",0f,1f);
                    ObjectAnimator va2 = ObjectAnimator.ofFloat(black,"alpha",1f,0f);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.setDuration(1500);
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            new Handler(){
                                @Override
                                public void handleMessage(Message msg){
                                    findViewById(R.id.star_car_0).setVisibility(View.VISIBLE);
                                }

                            }.sendEmptyMessageDelayed(0,500);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animatorSet.playTogether(va1,va2);
                    animatorSet.start();
                }
            }
        });
        va.start();
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, Object eventObj) {
        MLOC.d("1", aEventID);
        switch (aEventID) {
            case AEvent.AEVENT_C2C_REV_MSG:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MLOC.showMsg(SplashActivity.this, "小车已成功启动");
                    }
                });
                XHIMMessage message = (XHIMMessage) eventObj;
                if (message.fromId.equals(waitCarId)) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra(MainActivity.LIVE_ID, message.contentData);
                    intent.putExtra(MainActivity.LIVE_NAME, waitCarId);
                    intent.putExtra(MainActivity.CREATER_ID, waitCarId);
                    startActivity(intent);
                    removeListener();
                    finish();
                }

        }
    }
}
