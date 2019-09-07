package com.hlnote;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import Utils.MyDatePiker;
import Utils.MyTimePiker;
import Utils.TimeFormatUtils;

public class Edit_MemoActivity extends AppCompatActivity {

    private EditText edt_title;
    private EditText edt_content;
    private TextView tv_st_time, tv_ed_time;
    private String key;
    private String position;//用于回传修改的item的位置

    /**************解析并实现菜单*************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();//实例化一个MenuInflater对象
        inflater.inflate(R.menu.menu_edit_activity,menu);//解析菜单资源文件
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//实现菜单
        switch (item.getItemId()){
            case R.id.m_edit:
                this.activateUI();//激活所有组件，以便于编辑
                break;
            case R.id.e_save:
                String title=edt_title.getText().toString();//获取标题
                String st=tv_st_time.getText().toString().trim();//获取开始时间
                String et=tv_ed_time.getText().toString().trim();//获取结束时间
                String content=edt_content.getText().toString();//获取内容
                String start_datetime=TimeFormatUtils.datetimeToDB(st);//将开始时间转换为存进数据库的格式
                String end_datetime=TimeFormatUtils.datetimeToDB(et);//将结束时间转换为存进数据库的格式
                if(!title.equals("")) {//如果标题不为空，完成添加操作
                    Intent intent = new Intent();
                    intent.putExtra("title", title);
                    intent.putExtra("position",position);
                    //取出显示日期的时间部分 xxHH:mm
                    intent.putExtra("start_datetime",st.substring(st.length()-7,st.length()));
                    intent.putExtra("end_datetime",et.substring(st.length()-7,st.length()));
                    setResult(RESULT_OK, intent);
                    db_updata(key,title,start_datetime,end_datetime,content);//插入到数据库
                    Toast.makeText(getApplication(),"添加成功",Toast.LENGTH_SHORT).show();
                    finish();
                }else{//如果标题为空，提示用户
                    Toast.makeText(getApplicationContext(),"标题不能为空",Toast.LENGTH_SHORT).show();
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__memo);

        if(Build.VERSION.SDK_INT>=21&&getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }

        if(NavUtils.getParentActivityName(Edit_MemoActivity.this)!=null&&getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }

        edt_title=findViewById(R.id.ed_title_0);
        tv_st_time=findViewById(R.id.st_time_0);
        tv_ed_time=findViewById(R.id.ed_time_0);
        edt_content=findViewById(R.id.ed_content_0);
        Intent intent=getIntent();
        key=intent.getStringExtra("key");
        position=intent.getStringExtra("item_position");
        this.db_load_data(key);
        /**************开始时间监听**********************/
        tv_st_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int a[] = TimeFormatUtils.showDateTimeDivide24(tv_st_time.getText().toString().trim());
                //分解显示的时间用于传参int[]：0.yyyy, 1.MM 2.dd 3.HH 4.mm
                final MyTimePiker timePiker = new MyTimePiker(Edit_MemoActivity.this, a[3], a[4]);//传入当前时间，初始化时间选择器
                final AlertDialog dialog = new AlertDialog.Builder(Edit_MemoActivity.this).create();
                view = timePiker.myTimerPickerView();
                view.findViewById(R.id.time_piker_enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String st = a[0] + "年" + a[1] + "月" + a[2] + "日 " + timePiker.getTimeStr();//从选择器上获取日期时间
                        tv_st_time.setText(st);//将获取的日期时间显示在文本框
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.time_piker_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(view);
                dialog.show();
            }
        });
        /******************结束时间监听********************/
        tv_ed_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] dt_strings=new String[6];//用于存放分解字符串的日期时间
                final int[] a=TimeFormatUtils.showDateTimeDivide24(tv_ed_time.getText().toString().trim());
                //分解显示的时间用于传参int[]：0.yyyy, 1.MM 2.dd 3.HH 4.mm
                final MyDatePiker myDatePiker=new MyDatePiker(Edit_MemoActivity.this,a[0],a[1],a[2],"y-m-d");
                //选择日期
                final AlertDialog alertDialog=new AlertDialog.Builder(Edit_MemoActivity.this).create();
                View v=myDatePiker.myDatePickerView();
                v.findViewById(R.id.datepiker_enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dt_strings[0]=String.valueOf(myDatePiker.getYear());//选中的年
                        dt_strings[1]=String.valueOf(myDatePiker.getMonth());//选中的月
                        dt_strings[2]=String.valueOf(myDatePiker.getDay());//选中的日
                        alertDialog.dismiss();
                        //选择日期结束，开始选择时间

                        final MyTimePiker myTimePiker=new MyTimePiker(Edit_MemoActivity.this,a[3],a[4]);
                        final AlertDialog alertDialog1=new AlertDialog.Builder(Edit_MemoActivity.this).create();
                        View v1=myTimePiker.myTimerPickerView();
                        v1.findViewById(R.id.time_piker_enter).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dt_strings[3]=myTimePiker.getM_N();//选中上下午
                                dt_strings[4]=String.valueOf(myTimePiker.getHour());//选中的小时
                                dt_strings[5]=String.valueOf(myTimePiker.getMinute());//选中的分
                                tv_ed_time.setText(String.format(getResources().getString(R.string.datetime0),//将选中时间显示在
                                        dt_strings[0],dt_strings[1],dt_strings[2],
                                        dt_strings[3],Integer.parseInt(dt_strings[4]),Integer.parseInt(dt_strings[5])));
                                //通过字符串格式来进行设置：%1$s年%2$s月%3$s日%4$s%5$02d:%6$02d
                                alertDialog1.dismiss();
                            }
                        });
                        v1.findViewById(R.id.time_piker_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog1.dismiss();
                            }
                        });
                        alertDialog1.setView(v1);
                        alertDialog1.show();
                    }
                });
                v.findViewById(R.id.datepiker_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.setView(v);
                alertDialog.show();
            }
        });
        tv_st_time.setClickable(false);//设置开始时间不可点击
        tv_ed_time.setClickable(false);//设置结束时间不可点击
        if(edt_content.getText().toString().equals("")){
            edt_content.setEnabled(false);
        }
    }

    /**
     * 从数据库加载指定的备忘录信息到UI
     * @param key 备忘录主键
     */
    private void db_load_data(String key){
        String []data=new String[4];
        DBOpenHelper dbOpenHelper=new DBOpenHelper(Edit_MemoActivity.this,"memo.db",null,1);
        Cursor cursor =dbOpenHelper.getReadableDatabase().query(
                "memo_table",new String[]{"memo_title","start_datetime","end_datetime","memo_content"},
                "create_datetime=?",
                new String[]{key},null,null,null);
        while (cursor.moveToNext()) {
            data[0] = cursor.getString(0);//标题
            data[1] = TimeFormatUtils.DBToDatetime(cursor.getString(1));//开始时间
            data[2] = TimeFormatUtils.DBToDatetime(cursor.getString(2));//结束时间
            data[3] = cursor.getString(3);//内容
        }
        this.edt_title.setText(data[0]);
        this.edt_title.setSelection(this.edt_title.getText().length());
        this.tv_st_time.setText(data[1]);
        this.tv_ed_time.setText(data[2]);
        this.edt_content.setText(data[3]);
        cursor.close();
        dbOpenHelper.close();
    }

    /**
     * 将修改后的数据，在数据库中更新
     * @param key 当前编辑的备忘信息的主键（即创建时间）
     * @param title 新标题
     * @param start_datetime 新开始时间
     * @param end_datetime 新结束时间
     * @param content 新内容
     */
    private void db_updata(String key,String title,String start_datetime,String end_datetime,String content){
        DBOpenHelper dbOpenHelper=new DBOpenHelper(Edit_MemoActivity.this,"memo.db",null,1);
        SQLiteDatabase sqLiteDatabase=dbOpenHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("memo_title",title);//标题
        values.put("start_datetime",start_datetime);//开始日期
        values.put("end_datetime",end_datetime);//结束日期
        //Log.i("24Hour:",String.valueOf(to24Hour(start_time)));
        values.put("memo_content",content);//内容
        sqLiteDatabase.update("memo_table",values,"create_datetime =?",new String[]{key});
        dbOpenHelper.close();
    }

    /**
     * 激活所有组件，使其处于可操作状态
     */
    private void activateUI(){
        this.edt_title.setFocusable(true);
        this.edt_title.setFocusableInTouchMode(true);
        this.edt_title.requestFocus();
        this.tv_st_time.setClickable(true);
        this.tv_ed_time.setClickable(true);
        this.edt_content.setFocusable(true);
        this.edt_content.setFocusableInTouchMode(true);
        edt_content.setEnabled(true);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm!=null) {
            imm.showSoftInput(edt_title, InputMethodManager.SHOW_IMPLICIT);
        }
    }
}
