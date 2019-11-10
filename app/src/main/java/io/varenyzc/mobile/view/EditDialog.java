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
import android.widget.EditText;
import android.widget.TextView;

import io.varenyzc.mobile.R;

public class EditDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private int layoutResID;
    private int[] listenedItem;

    private TextView tv_speed;
    private EditText et_speed;

    public EditDialog(Context context, int layoutResID, int[] listenedItem) {
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
        et_speed = findViewById(R.id.et_speed);
        tv_speed = findViewById(R.id.tv_speed);
    }

    private OnConfirmClickListener listener;
    public interface  OnConfirmClickListener{
        void OnClick(EditDialog dialog, View view,String speed);
    }

    public void setOnConfirmListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public void setSpeedText(String s) {
        tv_speed.setText(s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_comfirm:
                listener.OnClick(this,v,et_speed.getText().toString());
                break;
        }
    }
}
