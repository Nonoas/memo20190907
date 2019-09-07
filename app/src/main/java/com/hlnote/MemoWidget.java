package com.hlnote;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class MemoWidget extends AppWidgetProvider {
    private static final String TAG="MemoWidget";
    public static final String AppWidget_Updata_Action = "com.hlnote.MemoWidget";
    private static RemoteViews remoteViews;
    private static ComponentName componentName;
    public MemoWidget() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        Log.i(TAG,action);
        //接收到click点击事件发送的广播，启动主界面（响应点击事件）
        if(action.equals(AppWidget_Updata_Action)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    refreshView(context);//更新桌面组件视图
                }
            }).start();
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        refreshView(context);//更新桌面组件视图
        Intent intent=new Intent(context,MemoWidgetService.class);
        context.startService(intent);
        Log.i("Memo","enable");

        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Intent intent=new Intent(context,MemoWidgetService.class);
        context.stopService(intent);
        Log.i("Memo","销毁");
    }

    private void refreshView(Context context){
        componentName=new ComponentName(context,MemoWidget.class);
        remoteViews=new RemoteViews(context.getPackageName(),R.layout.widget_memo);
        remoteViews.setEmptyView(R.id.lv_widget_today,R.id.tv_widget_empty);//设置空视图

        Intent intent= new Intent(context, MyRemoteViewService.class);//启动service
        remoteViews.setRemoteAdapter(R.id.lv_widget_today,intent);//设置适配器

        //更新RemoteViews
        AppWidgetManager manager=AppWidgetManager.getInstance(context);
        manager.updateAppWidget(componentName,remoteViews);
        int[] widgetId= manager.getAppWidgetIds(new ComponentName(context.getPackageName(), MemoWidget.class.getName()));
        manager.notifyAppWidgetViewDataChanged(widgetId, R.id.lv_widget_today);
    }
}
