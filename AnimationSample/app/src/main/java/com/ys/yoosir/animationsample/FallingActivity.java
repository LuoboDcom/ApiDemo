package com.ys.yoosir.animationsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ys.yoosir.animationsample.weights.FallingLayout;

/**
 *  红包雨动画
 */
public class FallingActivity extends AppCompatActivity {

    FallingLayout mFallingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falling);

        mFallingLayout = (FallingLayout) findViewById(R.id.falling_layout);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mFallingLayout.addFallingBody();
                mFallingLayout.addFallingBody(100);
            }
        });
    }
}
