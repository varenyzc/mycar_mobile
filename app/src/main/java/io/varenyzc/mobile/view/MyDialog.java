package io.varenyzc.mobile.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import io.varenyzc.mobile.R;

public class MyDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private int layoutResID;
    private int[] listenedItem;

    private SeekBar mSeekBar;
    private TextView tv_speed;

    public MyDialog(Context context, int layoutResID, int[] listenedItem) {
        super(context, R.style.MyDialog);
        this.mContext = context;
        this.layoutResID = layoutResID;
        this.listenedItem = listenedItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        setContentView(layoutResID);
        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失
        //遍历控件id添加点击注册
        for(int id:listenedItem){
            findViewById(id).setOnClickListener(this);
        }
        mSeekBar = findViewById(R.id.sb_speed);
        tv_speed = findViewById(R.id.tv_speed);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sb_listener.OnChange(MyDialog.this, seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private OnConfirmClickListener listener;
    public interface  OnConfirmClickListener{
        void OnClick(MyDialog dialog, View view,int progress);
    }

    public void setOnConfirmListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public void setSpeedText(String s) {
        tv_speed.setText(s);
    }

    private OnSeekBarChangeListener sb_listener;
    public interface OnSeekBarChangeListener{
        void OnChange(MyDialog dialog, View view, int progress);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener){
        this.sb_listener = listener;
    }

    public void setProgress(String speed) {
        int tmp = Integer.valueOf(speed)-70;
        mSeekBar.setProgress(tmp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_comfirm:
                listener.OnClick(this,v,mSeekBar.getProgress());
                dismiss();
                break;
        }
    }
}
