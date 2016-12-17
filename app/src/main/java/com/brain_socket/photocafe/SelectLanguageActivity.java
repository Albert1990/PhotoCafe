package com.brain_socket.photocafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        init();
    }

    void init(){
        Button btnArabic = (Button)findViewById(R.id.btnArabic);
        Button btnEnglish = (Button)findViewById(R.id.btnEnglish);

        btnArabic.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnArabic:
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.AR);
                break;
            case R.id.btnEnglish:
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.EN);
                break;
        }
        Intent i = new Intent(SelectLanguageActivity.this,SelectCategoryActivity.class);
        startActivity(i);
    }
}
