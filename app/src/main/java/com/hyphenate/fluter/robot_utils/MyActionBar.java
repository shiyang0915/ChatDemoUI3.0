package com.hyphenate.fluter.robot_utils;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.fluter.R;


public class MyActionBar extends RelativeLayout {

    private LayoutInflater inflater;
    private View view;
    private ImageView imageLeft, imageRight;
    private TextView textCenterText;
    private LinearLayout linearBack;

    public MyActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.title_bar, this);
        imageLeft = view.findViewById(R.id.image_left);
        imageRight = view.findViewById(R.id.image_right);
        textCenterText = view.findViewById(R.id.tv_center_msg);
        linearBack = view.findViewById(R.id.linear_action_bar_back);
    }


    public void setActionBar(int leftImgId, String centerText, int textColor, boolean isShowRightImg, int rightImageId,
                             OnClickListener leftImageClickListener, OnClickListener rightImageClickListener,
                             boolean isNeedBackGround) {
        textCenterText.setText(centerText);
        textCenterText.setTextColor(textColor);
        if (isShowRightImg) {
            imageRight.setVisibility(View.VISIBLE);
            imageRight.setImageResource(rightImageId);
            imageRight.setOnClickListener(rightImageClickListener);
        } else {
            imageRight.setVisibility(View.GONE);
        }
        imageLeft.setImageResource(leftImgId);
        linearBack.setOnClickListener(leftImageClickListener);
        if (isNeedBackGround) {
            //为actionBar设置背景
            this.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

    }


}
