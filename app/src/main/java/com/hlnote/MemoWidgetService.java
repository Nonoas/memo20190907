package com.hlnote;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * 该Service子类用于在每天凌晨00:00发送消息，刷新备忘录桌面组件
 */
public class MemoWidgetService extends Service {
    private String TAG="MemoWidgetService";
    private static final int SERVICE_ID = 1;

    public MemoWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG,"启动");
                Intent intent0=new Intent();
                intent0.setAction("com.hlnote.MemoWidget");
                sendBroadcast(intent0);
            }
        }).start();
        Calendar calendar=getDayNow();
        AlarmManager alarm= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent1=new Intent(MemoWidgetService.this,MemoWidgetService.class);
        PendingIntent pendingIntent=PendingIntent.getService(MemoWidgetService.this,0,intent1,0);
        //Log.i("Sevice时间",String.valueOf(i));
        if(Build.VERSION.SDK_INT>=19) {
            alarm.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);//有延迟，api19以后可用setExact
        }else{
            alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"关闭");
        super.onDestroy();
    }

    /**
     * 获取明天的日期
     * @return
     */
    private Calendar getDayNow(){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int i=calendar.get(Calendar.DAY_OF_MONTH)+1;//设置时间为明天凌晨00:00
        //int i=calendar.get(Calendar.SECOND)+5;//设置时间为明天凌晨00:00
        calendar.set(Calendar.DAY_OF_MONTH,i);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return calendar;
    }
}
