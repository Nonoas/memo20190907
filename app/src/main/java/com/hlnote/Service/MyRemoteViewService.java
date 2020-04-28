package com.hlnote.Service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.hlnote.DBOpenHelper;
import com.hlnote.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Utils.TimeFormatUtils;

public class MyRemoteViewService extends RemoteViewsService {
    public MyRemoteViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewFactory(this.getApplicationContext(),intent);
    }

    /**
     * 用于设置MemoWidget的界面及更新
     * @author Nonoas
     */
    class  MyRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mcontext;
        private int widgetId;
        private List<String> titleList,s_timeList,e_timeList;
        private DBOpenHelper dbOpenHelper;
        private int this_year,this_month,today;
        private Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));

        //构造器
        private MyRemoteViewFactory(Context context, Intent intent) {
            this.mcontext = context;
            widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            this.this_year=calendar.get(Calendar.YEAR);
            this.this_month=calendar.get(Calendar.MONTH)+1;
            this.today=calendar.get(Calendar.DAY_OF_MONTH);
        }
        @Override
        public void onCreate() {
            Log.i("MyRemoteViewService","onCreate调用");
        }

        /*当告知AppWidgetManager，remote View List要发生变化时，触发onDataSetChanged()，
        我们可以在provider中，通过广播接收等方式得知要求更新List数据，
        然后通过appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.listwidget_list_view);
        来触发onDataSetChanged()，并由此更新list的数据。*/
        @Override
        public void onDataSetChanged() {
            Log.i("MyRemoteViewService", "onDataSetChanged: "+widgetId);
            titleList=new ArrayList<>();
            s_timeList=new ArrayList<>();
            e_timeList=new ArrayList<>();
            dbOpenHelper=new DBOpenHelper(mcontext,"memo.db",null,1);
            String start_date=String.format(Locale.CHINA,"%1$d-%2$02d-%3$02d",this_year,this_month,today);
            Cursor cursor =dbOpenHelper.getReadableDatabase().query(
                    "memo_table",new String[]{"create_datetime","memo_title","start_datetime","end_datetime"},
                    "date(start_datetime) like ?",
                    new String[]{start_date},null,null,null);
            while (cursor.moveToNext()) {
                titleList.add(cursor.getString(1));
                //TODO 格式转化错误
                s_timeList.add(TimeFormatUtils.DBToTime12(cursor.getString(2)));//从数据库读取时间，格式为12小时制
                e_timeList.add(TimeFormatUtils.DBToTime12(cursor.getString(3)));//从数据库读取时间，格式为12小时制
            }
            cursor.close();
            dbOpenHelper.close();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        //返回item对应的子Remote View，本例有20个item，即会调用20次，
        // 每个子remote view在后台线程中运行，检查结果如下图 。
        // 在本例，layout/test_list_item.xml定义list中子view，只含有一个TextView，id为R.id.testview_item_id.
        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews=new RemoteViews(mcontext.getPackageName(), R.layout.widget_list_item);
            remoteViews.setTextViewText(R.id.widget_item_title,titleList.get(position));
            remoteViews.setTextViewText(R.id.widget_s_time,s_timeList.get(position));
            remoteViews.setTextViewText(R.id.widget_e_time,e_timeList.get(position));
            return remoteViews;
        }

        @Override/* 在返回getCount()时得知item的数目，然后通过getViewAt()对具体的item进行绘制，
    在getViewAt()尚未有return时，即正在loading子view的时候，
    可由getLoadingView()设置item在显示某些内容，如果返回null，则采用缺省的模式，对于TextView，缺省显示“Loading…”*/
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override/* 返回remote list中的子view的类型数目，本例返回1 */
        public int getViewTypeCount() {
            return 1;
        }

        @Override/* 根据position获取内部ID */
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
