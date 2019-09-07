package com.hlnote;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if(NavUtils.getParentActivityName(SettingActivity.this)!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }

    }
}
