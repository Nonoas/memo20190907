package com.hlnote;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import Utils.DateUtils;
import Utils.MyDatePiker;
import Utils.TimeFormatUtils;

public class MainActivity extends AppCompatActivity {

    private Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    private int year = cal.get(Calendar.YEAR);//当前年
    private int month = cal.get(Calendar.MONTH) + 1;//当前月
    private int day = cal.get(Calendar.DAY_OF_MONTH);//当前日
    private TextView record_title;
    private DateUtils dateUtils = new DateUtils();
    final private int REF_YEAR_S = dateUtils.REF_YEAR_S;
    final private int REF_YEAR_E = dateUtils.REF_YEAR_E;
    private DBOpenHelper dbOpenHelper;
    private ArrayList<Map<String, Object>> memo_List = new ArrayList<>();
    private SimpleAdapter adapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();//实例化一个MenuInflater对象
        inflater.inflate(R.menu.menu_in_main, menu);//解析菜单资源文件
        return super.onCreateOptionsMenu(menu);
    }

    /**************实现菜单*************************/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//实现菜单
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.today:
                year = cal.get(Calendar.YEAR);//转到今年
                month = cal.get(Calendar.MONTH) + 1;//转到当月
                initDate();
                break;
            case R.id.setting:
                Intent intent1 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21 && getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }
        this.initDate();//调用创建日历的方法

        String[] from = {"title", "start_datetime", "end_datetime"};
        int[] to = {R.id.list_title, R.id.start_time, R.id.end_time};
        adapter = new SimpleAdapter(this, memo_List, R.layout.memo_list_item, from, to);
        db_load_item();

        /***************快速选择日期*****************/
        record_title = findViewById(R.id.record_title);

        record_title.setOnClickListener(view -> {
            //创建MyTimePiker对象,参数(content,当前年,当前月,当前日,显示形式)
            final MyDatePiker piker = new MyDatePiker(MainActivity.this, year, month, day, "y-m");
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            view = piker.myDatePickerView();//获取时间选择器的view
            Button btn_enter = view.findViewById(R.id.datepiker_enter);//确认按钮
            Button btn_cancel = view.findViewById(R.id.datepiker_cancel);//取消按钮

            btn_enter.setOnClickListener(view1 -> {
                Toast.makeText(getApplication(), piker.getDateStr(), Toast.LENGTH_SHORT).show();
                year = Integer.parseInt(piker.getYear());
                month = Integer.parseInt(piker.getMonth());
                initDate();//刷新日历视图
                dialog.dismiss();
            });

            btn_cancel.setOnClickListener(view1 -> dialog.dismiss());
            dialog.setView(view);
            dialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setAction("com.hlnote.MemoWidget");
        sendBroadcast(intent);
    }
    /**************创建日历*************************/
    /**
     * DateUtils自定义的阳历工具类，位置come.hlnote
     *
     * @author Nonoas
     */
    public void initDate() {
        DateUtils dateUtils = new DateUtils();
        GridView gridView = findViewById(R.id.record_gridView);//网格视图组件
        TextView textView = findViewById(R.id.record_title);//标题的文本组件
        textView.setText(String.format(Locale.CHINA, "%d年%d月", year, month));//设置标题显示当前页的日期
        List dayNum = dateUtils.datePrint(year, month);//日历数组的值
        //实例化适配器对象
        MyBaseAdapter myBaseAdapter = new MyBaseAdapter(getApplicationContext(), dayNum, year, month, day);
        gridView.setAdapter(myBaseAdapter);//为gridview加载自定义适配器
    }

    /**
     * 从数据库加载备忘录信息到UI
     */

    private void db_load_item() {
        dbOpenHelper = new DBOpenHelper(MainActivity.this, "memo.db", null, 1);
        String start_date = String.format(Locale.CHINA, "%1$s-%2$02d-%3$02d", year, month, day);
        //将年月日转为数据库可以识别的格式：yyyy-MM-dd
        Cursor cursor = dbOpenHelper.getReadableDatabase().query(
                "memo_table", new String[]{"create_datetime", "memo_title", "start_datetime", "end_datetime"},
                "date(start_datetime) like date(?)",
                new String[]{start_date}, null, null, null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put("title", cursor.getString(1));
            map.put("start_datetime", TimeFormatUtils.DBToTime12(cursor.getString(2)));//从数据库读取时间，格式为12小时制
            map.put("end_datetime", TimeFormatUtils.DBToTime12(cursor.getString(3)));//从数据库读取时间，格式为12小时制
            memo_List.add(map);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbOpenHelper.close();
    }

    //左按钮
    public void preMonth(View view) {
        month--;
        if (month < 1) {//月份小于1，切换至上一年
            month += 12;
            year--;
            if (year < REF_YEAR_S) {
                year = REF_YEAR_E;
            }
        }
        initDate();//调用创建日历方法，刷新显示的日期
    }

    //右按钮
    public void nextMonth(View view) {
        month++;
        if (month > 12) {//月份大于12切换至下一年
            month -= 12;
            year++;
            if (year > REF_YEAR_E) {
                year = REF_YEAR_S;
            }
        }
        initDate();//调用创建日历方法，刷新显示的日期
    }

    public void to_account(View view) {
        Intent intent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intent);
    }

    public void to_memo(View view) {
        Toast.makeText(getApplication(), "功能暂未开发", Toast.LENGTH_SHORT).show();
    }

}
