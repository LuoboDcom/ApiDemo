package com.ys.yoosir.animationsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.ys.yoosir.animationsample.animation.Rotate3dAnimation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView iv = (ImageView) findViewById(R.id.ic_iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start3dAnimation(view);
            }
        });
    }

    private void start3dAnimation(View v){
        Rotate3dAnimation rotation = new Rotate3dAnimation(MainActivity.this,90,360,v.getWidth()/2,v.getHeight()/2,310.0f,false);
        rotation.setDuration(2000);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        v.startAnimation(rotation);
    }
}
