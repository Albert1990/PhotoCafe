package com.brain_socket.photocafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brain_socket.photocafe.data.DataStore;
import com.brain_socket.photocafe.data.ServerResult;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        init();
    }

    private void init(){
        DataStore.getInstance().requestCategories(new DataStore.DataRequestCallback() {
            @Override
            public void onDataReady(ServerResult result, boolean success) {
                if(success){
                    Intent i = new Intent(SplashScreenActivity.this,SelectLanguageActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}
