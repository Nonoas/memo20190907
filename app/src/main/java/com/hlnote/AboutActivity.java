package com.hlnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import Utils.UI_Utils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(Build.VERSION.SDK_INT>=21) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }

        if(NavUtils.getParentActivityName(AboutActivity.this)!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }
        TextView textView=findViewById(R.id.version_num);
        textView.append(UI_Utils.packageName(AboutActivity.this));
    }
    /*****************分享应用********************/
    public void toShare(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);//向其他应用发送消息
            sendIntent.putExtra(Intent.EXTRA_TEXT, "我发现一款不错的备忘软件");//发送内容
            sendIntent.setType("text/plain");//MIME Type类型
        if(hasApplication(sendIntent)){
            startActivity(sendIntent);
        }else{
            Toast.makeText(getApplicationContext(),"没有可分享的第三方平台",Toast.LENGTH_LONG).show();
        }
    }
    /************判断系统中是否有可发送消息的应用******************/
    public boolean hasApplication(Intent intent){
        PackageManager packageManager = getPackageManager();//查询是否有该Intent的Activity         
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);//activities里面不为空就有，否则就没有         
        return activities.size() > 0 ? true : false;
    }
    /****************检查更新**************************/
    public void toUpdata(View view) {
        Toast.makeText(getApplicationContext(),"当前为最新版本",Toast.LENGTH_SHORT).show();
    }

    public void toIntroduction(View view) {
        Toast.makeText(getApplicationContext(),"暂未开发",Toast.LENGTH_SHORT).show();
    }

    public void toFeedback(View view) {
        Toast.makeText(getApplicationContext(),"暂未开发",Toast.LENGTH_SHORT).show();
    }

    public void toAgreement(View view) {
        final AlertDialog dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("用户协议");
        dialog.setMessage(Html.fromHtml(getString(R.string.agreement)));//使用html显示带格式的文本
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
