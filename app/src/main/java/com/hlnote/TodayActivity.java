package com.hlnote;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import Utils.TimeFormatUtils;

public class TodayActivity extends AppCompatActivity {

    private List<Map<String,Object>> memo_List =new ArrayList<>();
    private SimpleAdapter adapter;
    private ListView listView;
    private DBOpenHelper dbOpenHelper;
    private List<String> PK_create_date;//获取主键
    private String today;//“日”
    private String this_month;//“月”
    private String this_year;//年

    /**************解析并实现菜单*************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();//实例化一个MenuInflater对象
        inflater.inflate(R.menu.menu_today_activity,menu);//解析菜单资源文件
        return super.onCreateOptionsMenu(menu);
    }
    //新建备忘录
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//实现菜单
        switch (item.getItemId()){
            case R.id.add_memo://保存数据，上传到数据库
                Intent intent1=new Intent(TodayActivity.this,Add_MemoActivity.class);
                intent1.putExtra("this_year",this_year);
                intent1.putExtra("this_month",this_month);
                intent1.putExtra("this_day",today);
                startActivityForResult(intent1,0x111);//请求码为0x111
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        menu.add(0,1,0,"删除");
        menu.add(0,2,0,"取消");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        if(Build.VERSION.SDK_INT>=21&&getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);//设置actionbar无阴影
        }

        if(NavUtils.getParentActivityName(TodayActivity.this)!=null&&getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//如果父activity不为空则显示向左的图标
        }

        Intent intent=getIntent();//获取Intent对象
        Bundle bundle=intent.getExtras();//获取Bundle传递的信息
        /***********获取并显示当前日期*******************/
        if(bundle!=null) {
            today = bundle.getString("day");//传递“day”值
            this_month = bundle.getString("month");//传递“month”值
            this_year = bundle.getString("year");
        }
        TextView textView=findViewById(R.id.today_num);//获取日子组件
        textView.setText(today);//传递日子数值
        TextView textView1=findViewById(R.id.month_num);//获取月份组件
        String y_m=this_year+"年"+this_month+"月";
        textView1.setText(y_m);//传递月份数值

        listView=findViewById(R.id.today_list);
        listView.setEmptyView(findViewById(R.id.empty_memo));
        String[] from ={"title","start_datetime","end_datetime"};
        int[] to = {R.id.list_title,R.id.start_time,R.id.end_time};
        adapter = new SimpleAdapter(this, memo_List,R.layout.memo_list_item,from,to);
        listView.setAdapter(adapter);

        /**************ListItem点击事件*****************************/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent1=new Intent(TodayActivity.this,Edit_MemoActivity.class);
                intent1.putExtra("key",PK_create_date.get(position));//传递被点击item的主键
                intent1.putExtra("item_position",String.valueOf(position));
                startActivityForResult(intent1,0x112);//请求码为0x112
            }
        });

        db_load_item();//从数据库加载备忘
        listView.setOnCreateContextMenuListener(this);
    }

    /****************菜单响应事件******************/
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1://删除
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int pos = (int) listView.getAdapter().getItemId(menuInfo.position);
                memo_List.remove(pos);//将选中的item从视图中删除
                adapter.notifyDataSetChanged();//刷新适配器显示的数据
                db_delete_item(pos);//从数据库删除选中信息
                refreshWidget();//刷新桌面小程序
                Toast.makeText(getBaseContext(), "已删除", Toast.LENGTH_SHORT).show();
                break;
            case 2://取消
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**************重写的onActivityResult接受数据***************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode){
            case 0x111://将用户添加的备忘信息的标题展示到lsitview
                if(resultCode==RESULT_OK) {
                    String create_datetime=data.getStringExtra("create_datetime");//获取主键，存进主键字List<String>数组
                    PK_create_date.add(create_datetime);//将主键添加进数组，如果需要进行排序，先释放主键数组所有元素
                    String title_show=data.getStringExtra("title");
                    String start_time=data.getStringExtra("start_datetime");
                    String end_time=data.getStringExtra("end_datetime");
                    Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    Map<String,Object> item = new HashMap<>();
                    item.put("title",title_show);
                    item.put("start_datetime",start_time);
                    item.put("end_datetime",end_time);
                    memo_List.add(item);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(listView.getBottom());//让listview焦点移到最新添加的事务上
                    this.refreshWidget();//刷新桌面小插件
                }
                break;
            case 0x112://将修改后的备忘信息在listview中更新
                if(resultCode==RESULT_OK) {
                    String title_show = data.getStringExtra("title");
                    String start_time = data.getStringExtra("start_datetime");
                    String end_time = data.getStringExtra("end_datetime");
                    String p= data.getStringExtra("position");
                    int position=Integer.parseInt(p);
                    Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
                    Map<String, Object>item=new HashMap<>();
                    item.put("title", title_show);
                    item.put("start_datetime", start_time);
                    item.put("end_datetime", end_time);
                    memo_List.set(position,item);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(listView.getBottom());//让listview焦点移到最新添加的事务上
                    this.refreshWidget();//刷新桌面小插件
                }
                break;
        }
    }

    /**
     * 从数据库加载备忘录信息到UI
     */
    private void db_load_item(){
        dbOpenHelper=new DBOpenHelper(TodayActivity.this,"memo.db",null,1);
        String start_date=String.format(Locale.CHINA,"%1$s-%2$02d-%3$02d",this_year,Integer.parseInt(this_month),Integer.parseInt(today));
        //将年月日转为数据库可以识别的格式：yyyy-MM-dd
        PK_create_date=new ArrayList<>();
        Cursor cursor =dbOpenHelper.getReadableDatabase().query(
                "memo_table",new String[]{"create_datetime","memo_title","start_datetime","end_datetime"},
                "date(start_datetime) like date(?)",
                new String[]{start_date},null,null,null);
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<>();
            map.put("title",cursor.getString(1));
            map.put("start_datetime",TimeFormatUtils.DBToTime12(cursor.getString(2)));//从数据库读取时间，格式为12小时制
            map.put("end_datetime",TimeFormatUtils.DBToTime12(cursor.getString(3)));//从数据库读取时间，格式为12小时制
            PK_create_date.add(cursor.getString(0));
            memo_List.add(map);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbOpenHelper.close();
    }

    /**
     * 删除数据库中与指定主键的信息
     * @param position 带主键字符串的字符串数组的脚标
     */
    private void db_delete_item(int position){
        dbOpenHelper=new DBOpenHelper(TodayActivity.this,"memo.db",null,1);
        SQLiteDatabase sqLiteDatabase=dbOpenHelper.getReadableDatabase();
        String PK=PK_create_date.get(position);//获取删除对象的主键(创建日期)
        sqLiteDatabase.delete("memo_table","create_datetime=?",new String[]{PK});
        dbOpenHelper.close();
        PK_create_date.remove(position);
    }

    /**
     * 发送消息用来更新桌面小程序
     */
    private void refreshWidget(){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        if(Integer.parseInt(this_year)==calendar.get(Calendar.YEAR)&&
                Integer.parseInt(this_month)==calendar.get(Calendar.MONTH)+1&&
                Integer.parseInt(today)==calendar.get(Calendar.DAY_OF_MONTH)) {
            Intent intent = new Intent();
            intent.setAction("com.hlnote.MemoWidget");
            sendBroadcast(intent);
        }
    }
}
