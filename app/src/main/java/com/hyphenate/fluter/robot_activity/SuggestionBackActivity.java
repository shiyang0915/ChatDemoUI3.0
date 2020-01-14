package com.hyphenate.fluter.robot_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.impl.ZszlSuggestionBackListener;
import com.hyphenate.fluter.robot_presenter.SuggestionPresenter;
import com.hyphenate.fluter.robot_utils.MyActionBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SuggestionBackActivity extends BaseActivity implements ZszlSuggestionBackListener {

    @InjectView(R.id.action_bar_suggestion)
    MyActionBar actionBar;

    @InjectView(R.id.edit_suggestion)
    public EditText editSuggestion;

    @InjectView(R.id.tv_present_input_length)
    public TextView textInputLength;

    @InjectView(R.id.button_submit)
    public Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_back);
        ButterKnife.inject(this);
        initAll();
    }

    @Override
    public void init() {
        actionBar.setActionBar(R.mipmap.back_whrite, "意见反馈", Color.parseColor("#FFFFFF"),
                false, -1, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, false);
    }

    @Override
    public void setData() {
        SuggestionPresenter presenter = new SuggestionPresenter(this);
        presenter.setZszlSuggestionBackListener(this);
    }

    @Override
    public void listener() {

    }

    @Override
    public void onSuggestionBackSuccessed(String successMsg) {
        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show();
        cancelDialog();
    }

    @Override
    public void onSuggestionBackFailed(String errorMsg) {
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
        cancelDialog();
    }
}
