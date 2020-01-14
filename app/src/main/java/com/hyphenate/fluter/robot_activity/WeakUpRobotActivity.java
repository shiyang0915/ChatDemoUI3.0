package com.hyphenate.fluter.robot_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.MyActionBar;


public class WeakUpRobotActivity extends BaseActivity {

    private MyActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huan_xin_robbet);
        initAll();
    }

    @Override
    public void init() {
        actionBar = findViewById(R.id.action_bar_huan_xin_robbt);
    }

    @Override
    public void setData() {

    }

    @Override
    public void listener() {
        actionBar.setActionBar(R.mipmap.back, "机器人",
                Color.parseColor("#000000"), false, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);
    }
}
