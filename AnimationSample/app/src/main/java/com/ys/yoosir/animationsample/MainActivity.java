package com.ys.yoosir.animationsample;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.ys.yoosir.animationsample.animation.Rotate3dAnimation;
import com.ys.yoosir.animationsample.weights.Roll3dView;

public class MainActivity extends AppCompatActivity {

    private Roll3dView r3dView1,r3dView2,r3dView3,r3dView4;

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

        r3dView1 = (Roll3dView) findViewById(R.id.roll_3d_view_1);
        addBitmap(r3dView1);
        r3dView1.setRollMode(Roll3dView.RollMode.Whole3D);

        findViewById(R.id.anim_btn_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r3dView1.setRotateOrientation(1);
                r3dView1.toNext();
            }
        });

        r3dView2 = (Roll3dView) findViewById(R.id.roll_3d_view_2);
        addBitmap(r3dView2);
        r3dView2.setPartNumber(5);
        r3dView2.setRollMode(Roll3dView.RollMode.SepartConbine);
        findViewById(R.id.anim_btn_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r3dView2.setRotateOrientation(1);
                r3dView2.toNext();
            }
        });

        r3dView3 = (Roll3dView) findViewById(R.id.roll_3d_view_3);
        addBitmap(r3dView3);
        r3dView3.setPartNumber(5);
        r3dView3.setRollMode(Roll3dView.RollMode.RollInTurn);
        findViewById(R.id.anim_btn_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r3dView3.setRotateOrientation(1);
                r3dView3.toPre();
            }
        });

        r3dView4 = (Roll3dView) findViewById(R.id.roll_3d_view_4);
        addBitmap(r3dView4);
        r3dView4.setPartNumber(5);
        r3dView4.setRollMode(Roll3dView.RollMode.Jalousie);
        findViewById(R.id.anim_btn_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r3dView4.setRotateOrientation(1);
                r3dView4.toPre();
            }
        });
    }

    private void addBitmap(Roll3dView r3dView){
        BitmapDrawable bgDrawable1 = (BitmapDrawable) getResources().getDrawable(R.drawable.img1);
        BitmapDrawable bgDrawable2 = (BitmapDrawable) getResources().getDrawable(R.drawable.img2);
        BitmapDrawable bgDrawable3 = (BitmapDrawable) getResources().getDrawable(R.drawable.img3);
        BitmapDrawable bgDrawable4 = (BitmapDrawable) getResources().getDrawable(R.drawable.img4);
        BitmapDrawable bgDrawable5 = (BitmapDrawable) getResources().getDrawable(R.drawable.img5);

        r3dView.addImageBitmap(bgDrawable1.getBitmap());
        r3dView.addImageBitmap(bgDrawable2.getBitmap());
        r3dView.addImageBitmap(bgDrawable3.getBitmap());
        r3dView.addImageBitmap(bgDrawable4.getBitmap());
        r3dView.addImageBitmap(bgDrawable5.getBitmap());
    }

    private void start3dAnimation(View v){
        Rotate3dAnimation rotation = new Rotate3dAnimation(MainActivity.this,90,360,v.getWidth()/2,v.getHeight()/2,310.0f,false);
        rotation.setDuration(2000);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        v.startAnimation(rotation);
    }
}
