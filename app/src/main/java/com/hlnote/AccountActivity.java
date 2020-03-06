package com.hlnote;

import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        if(Build.VERSION.SDK_INT>=21&& getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }

        if(NavUtils.getParentActivityName(AccountActivity.this)!=null&&getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }


    }
}
