package com.example.android.likeeatapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    private ImageView iv;

    //ImageView tvSplash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);

        iv = (ImageView) findViewById(R.id.imageView);
        Context context	=	getApplicationContext();
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        iv.startAnimation(myanim);

        //tvSplash = (TextView) findViewByID(R.id.tvSplash)
        final Handler handler = new Handler();
        Thread timer = new Thread(){
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e ) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();;
                }

            }
        }; timer.start();
    }
}
