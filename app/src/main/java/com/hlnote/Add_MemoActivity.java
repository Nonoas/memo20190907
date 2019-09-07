package com.hlnote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import Utils.MyDatePiker;
import Utils.MyTimePiker;
import Utils.TimeFormatUtils;

import static android.media.CamcorderProfile.get;

public class Add_MemoActivity extends AppCompatActivity {

    private String TAG="Add_MemoActivity";
    private EditText ed_title;
    private EditText ed_content;
    private TextView tv_st_time, tv_ed_time;
    private String title;//标题
    private String content;//内容
    private String start_datetime,end_datetime;//开始时间，结束时间
    //private String classification;//分类
    private String this_year,this_month,this_day;
    private Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));

    /**************解析并实现菜单*************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();//实例化一个MenuInflater对象
        inflater.inflate(R.menu.menu_add_activity,menu);//解析菜单资源文件
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//实现菜单
        switch (item.getItemId()){
            case R.id.save://保存数据，上传到数据库
                title=ed_title.getText().toString();//获取标题
                String st=tv_st_time.getText().toString().trim();
                String et=tv_ed_time.getText().toString().trim();
                content = ed_content.getText().toString();//获取内容
                String create_datetime=getCreateDateTime();//创建时间：获取当前年月日和时间
                start_datetime=TimeFormatUtils.datetimeToDB(st);//将开始时间转换为存进数据库的格式
                end_datetime=TimeFormatUtils.datetimeToDB(et);//将结束时间转换为存进数据库的格式
                Log.i(TAG,start_datetime);
                if(!title.equals("")) {//如果标题不为空，完成添加操作
                    Intent intent = new Intent();
                    intent.putExtra("title", title);
                    intent.putExtra("create_datetime",create_datetime);
                    //取出显示日期的时间部分 xxHH:mm
                    intent.putExtra("start_datetime",st.substring(st.length()-7,st.length()));
                    intent.putExtra("end_datetime",et.substring(st.length()-7,st.length()));
                    setResult(RESULT_OK, intent);
                    db_insert_memo(create_datetime,title,start_datetime,end_datetime,content);//插入到数据库
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
        setContentView(R.layout.activity_add__memo);
        if(Build.VERSION.SDK_INT>=21&&getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }

        if(NavUtils.getParentActivityName(Add_MemoActivity.this)!=null&&getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }

        Intent intent=getIntent();
        //获取上一个activity传来的年月日
        this_year=intent.getStringExtra("this_year");
        this_month=intent.getStringExtra("this_month");
        this_day=intent.getStringExtra("this_day");
        ed_title=findViewById(R.id.ed_title);//标题
        tv_st_time=findViewById(R.id.st_time);//TextView开始时间
        tv_ed_time =findViewById(R.id.ed_time);//TextView结束时间
        ed_content=findViewById(R.id.ed_content);//内容
        /******************开始时间监听****************************/
        String a = this_year+"-"+this_month+"-"+this_day+" "+
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(calendar.get(Calendar.MINUTE))+":00";
        tv_st_time.setText(TimeFormatUtils.DBToDatetime(a));//调用自定义函数，显示时间

        tv_st_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyTimePiker timePiker=new MyTimePiker(Add_MemoActivity.this,calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE));//传入当前时间，初始化时间选择器
                final AlertDialog dialog=new AlertDialog.Builder(Add_MemoActivity.this).create();
                view=timePiker.myTimerPickerView();
                view.findViewById(R.id.time_piker_enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String st=this_year+"年"+this_month+"月"+this_day+"日 "+timePiker.getTimeStr();
                        tv_st_time.setText(st);
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
        /******************结束时间监听****************************/
        String b = this_year+"-"+this_month+"-"+this_day+" "+
                String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)+1)+":"+String.valueOf(calendar.get(Calendar.MINUTE))+":00";
        tv_ed_time.setText(TimeFormatUtils.DBToDatetime(b));//调用自定义函数，显示时间
        tv_ed_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] dt_strings=new String[6];//用于存放分解字符串的日期时间
                final int[] a=TimeFormatUtils.showDateTimeDivide24(tv_ed_time.getText().toString().trim());
                //分解显示的时间用于传参int[]：0.yyyy, 1.MM 2.dd 3.HH 4.mm
                final MyDatePiker myDatePiker=new MyDatePiker(Add_MemoActivity.this,a[0],a[1],a[2],"y-m-d");
                //选择日期
                final AlertDialog alertDialog=new AlertDialog.Builder(Add_MemoActivity.this).create();
                View v=myDatePiker.myDatePickerView();
                v.findViewById(R.id.datepiker_enter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dt_strings[0]=String.valueOf(myDatePiker.getYear());//选中的年
                        dt_strings[1]=String.valueOf(myDatePiker.getMonth());//选中的月
                        dt_strings[2]=String.valueOf(myDatePiker.getDay());//选中的日
                        alertDialog.dismiss();
                        //选择日期结束，开始选择时间

                        final MyTimePiker myTimePiker=new MyTimePiker(Add_MemoActivity.this,a[3],a[4]);
                        final AlertDialog alertDialog1=new AlertDialog.Builder(Add_MemoActivity.this).create();
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
    }

    /**
     * 将备忘录信息插入数据库
     * @param create_datetime (主键) 创建时间
     * @param title 标题
     * @param start_datetime 开始时间
     * @param end_datetime 结束时间
     * @param content 内容
     */
    private void db_insert_memo(String create_datetime,String title,String start_datetime,String end_datetime,String content){
        DBOpenHelper dbOpenHelper=new DBOpenHelper(Add_MemoActivity.this,"memo.db",null,1);
        SQLiteDatabase sqLiteDatabase=dbOpenHelper.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("create_datetime",create_datetime);//创建日期，即当前时间精确到秒
        values.put("memo_title",title);//标题
        values.put("start_datetime",start_datetime);//开始日期
        values.put("end_datetime",end_datetime);//结束日期
        //Log.i("24Hour:",String.valueOf(to24Hour(start_time)));
        values.put("memo_content",content);//内容
        sqLiteDatabase.insert("memo_table",null,values);
        dbOpenHelper.close();
    }

    /**
     * 获取事务创建时间(主键)，即为当前日期
     * @return 当前时间，格式：yyyy-MM-dd HH:mm:ss
     */
    private String getCreateDateTime(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());//设置格式:年-月-日 时:分:秒
        Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        //Log.i("当前时间", cal.getTime().toString());
        return dateFormat.format(cal.getTime());//返回字符串
    }

    /**
     * 将时间转为x 年 x 月 x 日 x H : M格式
     * @param year 年
     * @param month 月
     * @param day 日
     * @param hour 时(24小时制)
     * @param minute 分
     * @param form 秒
     * @return 字符串数组
     */
    private String[] getTimeString(int year, int month, int day, int hour, int minute, String form) {
        //x 年 x 月 x 日 x H : M
        //0 1  2  3 4  5 6 7 8 9
        String[] str=new String[10];
        str[0]=year+"";str[2]=month+"";str[4]=day+"";
        str[1]="年";str[3]="月";str[5]="日";str[8]=":";
        str[9]=minute>=10?(minute+""):("0"+minute);
        if (hour < 12) {
            str[7]=hour>=10?(hour+""):("0"+hour);
            str[6]="上午";
        } else if (hour == 12) {
            str[7]=hour+"";
            str[6]="下午";
        } else {
            str[7]=(hour-12)>=10?(hour-12+""):("0"+(hour-12));
            str[6]="下午";
        }
        switch (form) {
            case "D-T":
                return str;
            case "T":
                return Arrays.copyOfRange(str,6,9);
            default:
                return null;
        }
    }
}
