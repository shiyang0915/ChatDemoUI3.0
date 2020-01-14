package com.hyphenate.fluter.robot_activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hyphenate.fluter.R;
import com.hyphenate.fluter.robot_utils.MyActionBar;

import static com.hyphenate.fluter.robot_utils.Utils.readBitMap;


public class BindProcessActivity extends BaseActivity {
    private ImageView imageGuide;
    private Bitmap bitmap;
    private Button btn_start_bind;
    private MyActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunxin_guide);
        initAll();
    }

    @Override
    public void init() {
        imageGuide = findViewById(R.id.image_guide);
        btn_start_bind = findViewById(R.id.btn_start_bind);
        bitmap = readBitMap(context, R.drawable.huanxin_robbt_yindao);
        imageGuide.setImageBitmap(bitmap);
        actionBar = findViewById(R.id.action_huanxing_guide);
    }

    @Override
    public void setData() {
    }

    @Override
    public void listener() {
        btn_start_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new IntentIntegrator(BindProcessActivity.this)
                        .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)// 扫码的类型,可选：一维码，二维码，一/二维码
                        //.setPrompt("请对准二维码")// 设置提示语
                        .setCameraId(0)// 选择摄像头,可使用前置或者后置
                        .setBeepEnabled(true)// 是否开启声音,扫完码之后会"哔"的一声
                        .setCaptureActivity(QrCodeActivity.class)//自定义扫码界面
                        .initiateScan();// 初始化扫码
            }
        });


        actionBar.setActionBar(R.mipmap.back, "绑定流程",
                Color.parseColor("#000000"), false, 0, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, null, true);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫码结果
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                //扫码失败
            } else {
                String result = intentResult.getContents();//返回值
                Log.i("SHIYANG_TAG", result);
                Intent intent = new Intent(BindProcessActivity.this, WeakUpRobotActivity.class);
                startActivity(intent);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
        System.gc();
    }


}
