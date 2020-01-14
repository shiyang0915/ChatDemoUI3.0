package com.example.animator_test;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.Keep;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.annotation.WorkerThread;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

public class MainActivity extends Activity {
    private Scene scene1;
    private Scene scene2;
    private boolean isScene2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.mContainer);
        scene1 = Scene.getSceneForLayout(((PercentRelativeLayout) findViewById(R.id.mContainer)),
                R.layout.scene_layout_one, this);
        scene2 = Scene.getSceneForLayout(((PercentRelativeLayout) findViewById(R.id.mContainer)),
                R.layout.scene_layout_two, this);

        final ChangeBounds changeBounds = new ChangeBounds();

//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mHandler.postDelayed(this, 1000);
//                if (isScene2) {
//                    TransitionManager.go(scene1, changeBounds);
//                } else {
//                    TransitionManager.go(scene2, changeBounds);
//                }
//                isScene2 = !isScene2;
//            }
//        }, 3000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Snackbar.make(findViewById(R.id.mContainer),"this is snackbar",Snackbar.LENGTH_LONG).show();
        String str[]=new String[]{"1","2","shiyang"};
        getName(str);
    }

    Handler mHandler = new Handler();

    @Keep
    private void getName(@Size(2) String[] str) {
        Log.d("shiyangTest", str + "");
        TextView textView = new TextView(this);
        textView.setTextColor(Color.BLACK);
    }

}
