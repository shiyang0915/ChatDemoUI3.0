package com.hyphenate.fluter.robot_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.NormalDialog;


public class MainActivity extends BaseActivity {

    private NormalDialog normalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAll();
    }

    @Override
    public void init() {
        normalDialog = new NormalDialog(this, bindRobbtDialogInit());
    }

    @Override
    public void setData() {
        normalDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void listener() {
        findViewById(R.id.linear_call_robbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处是伪代码。。。
                if (0 == 0) {
                    //检测到未绑定机器人
                    normalDialog.show();
                } else {
                    //已经绑定机器人
                }
            }
        });
    }


    private View bindRobbtDialogInit() {
        View view = getLayoutInflater().inflate(R.layout.dialog_tongyong, null);
        TextView textLeftBottom = view.findViewById(R.id.tv_dialog_left_bottom);
        TextView textBottomRight = view.findViewById(R.id.tv_dialog_bottom_right);
        textLeftBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalDialog.cancel();
            }
        });

        textBottomRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                normalDialog.cancel();
                Intent intent = new Intent(MainActivity.this, BindProcessActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }


}
