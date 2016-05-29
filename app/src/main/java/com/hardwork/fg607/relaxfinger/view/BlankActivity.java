package com.hardwork.fg607.relaxfinger.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.hardwork.fg607.relaxfinger.R;
import com.hardwork.fg607.relaxfinger.service.FloatingBallService;
import com.hardwork.fg607.relaxfinger.utils.Config;

public class BlankActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout);
        layout.setClickable(true);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("what",Config.CLOSE_MENU);
                intent.setClass(BlankActivity.this, FloatingBallService.class);
                startService(intent);

            }
        });



    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("finish",false)){

            finish();

        }
    }
}
