package com.hyphenate.fluter.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.fluter.DemoApplication;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_activity.SettingActivity;
import com.hyphenate.fluter.robot_utils.GlideRoundTransform;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PersonalCenterFragment extends Fragment {

    private View view;

    @InjectView(R.id.image_personal_head)
    ImageView imageHead;

    @InjectView(R.id.tv_personal_username)
    TextView textUserName;

    @InjectView(R.id.tv_personal_account)
    TextView textAccount;

    @InjectView(R.id.linear_personal_right_arrow)
    LinearLayout linearRightArrow;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        ButterKnife.inject(this, view);
        init();
        return view;
    }


    @OnClick(R.id.linear_personal_right_arrow)
    void rightArrowClickListener(View view) {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    private void init() {
        textUserName.setText("用户名:" + DemoApplication.getInstance().userInfor.getData().getReal_name());
        textAccount.setText("账号:" + DemoApplication.getInstance().userInfor.getData().getUsername());
        Glide.with(getActivity())
                .load(DemoApplication.getInstance().userInfor.getData().getAvatar())
                .placeholder(R.drawable.msg_head_bg)
                .error(R.drawable.msg_head_bg)
                .transform(new GlideRoundTransform(getActivity(), 6))
                .into(imageHead);
    }


}
