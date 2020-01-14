package com.hyphenate.fluter.robot_activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;

import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.MyActionBar;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QrCodeActivity extends BaseActivity
        implements DecoratedBarcodeView.TorchListener {

    private DecoratedBarcodeView decoratedBarcodeView;
    private CaptureManager captureManager;
    private MyActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        initAll();
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();
    }

    @Override
    public void init() {
        decoratedBarcodeView = findViewById(R.id.decoratedBarcodeView);
        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, decoratedBarcodeView);
        actionBar = findViewById(R.id.action_bar_scan);
    }

    @Override
    public void setData() {

    }

    @Override
    public void listener() {
        decoratedBarcodeView.setTorchListener(this);
        actionBar.setActionBar(R.mipmap.back_whrite, "扫一扫",
                Color.parseColor("#FFFFFF"), true, R.mipmap.sacn_actionbar_right, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, false);
    }

    @Override
    public void onTorchOn() {

    }

    @Override
    public void onTorchOff() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return decoratedBarcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
