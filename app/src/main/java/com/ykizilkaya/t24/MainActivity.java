package com.ykizilkaya.t24;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by YasirKizilkaya on 8.09.16.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** Duration of wait **/
        final int SPLASH_DISPLAY_LENGTH = 1000;

        /** run for a sec **/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /** intent that will start news class **/

                Intent mainIntent = new Intent(MainActivity.this, NewsActivity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

}
