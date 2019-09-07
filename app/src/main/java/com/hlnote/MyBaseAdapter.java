package com.hlnote;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Utils.LunarUtil;

public class MyBaseAdapter extends BaseAdapter {
    private Calendar cal =Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    private List dateList;
    private Context mContext;
    DBOpenHelper dbOpenHelper;
    private int year;
    private int month;
    private int day;

    public MyBaseAdapter(Context mContext, List dateList, int year, int month, int day){//用于数据的桥梁可以传入
        super();
        this.mContext=mContext;
        this.dateList=dateList;
        this.year=year;
        this.month=month;
        this.day=day;
        dbOpenHelper=new DBOpenHelper(mContext,"memo.db",null,1);
    }
    @Override
    public int getCount() {//该方法如果返回0的话就不调用下面getView()方法如果大于0调用该方法
        return dateList.size();
    }

    @Override
    public Object getItem(int position) {
        return dateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final ViewHolder holder;
        if (view == null) {
            view= inflater.inflate(R.layout.date_item, null);//加载item的布局文件
            holder = new ViewHolder();
            holder.textView1 =view.findViewById(R.id.date_num);
            holder.textView2=view.findViewById(R.id.lunar_num);
            holder.v=view.findViewById(R.id.memo_sign);
            view.setTag(holder);//将viewHolder设置到Tag
        }else{
            holder = (ViewHolder)view.getTag();//从View中getTag取出来就可以
        }
        if((i+7)%7==0||(i+7)%7==6){//如果是周末则设置颜色为浅红色
            holder.textView1.setTextColor(mContext.getResources().getColor(R.color.colorWeekend));
            holder.textView2.setTextColor(mContext.getResources().getColor(R.color.colorWeekend));
        }
        if(dateList.get(i)==(Integer)0){
            view.setEnabled(false);
        }else {
            holder.textView1.setText(String.valueOf(dateList.get(i)));
            LunarUtil lunarCalender = new LunarUtil(year,month, Integer.parseInt(holder.textView1.getText().toString()));
            String lunarStr=lunarCalender.getChineseDay(); // 获取日，如初一
            if(lunarStr.equals("初一")){
                lunarStr=lunarCalender.getChineseMonth();
            }
            holder.textView2.setText(lunarStr);
        }
        //设置今天显示为红色
        if(dateList.get(i)==(Integer)day&&year==cal.get(Calendar.YEAR)&&month==cal.get(Calendar.MONTH)+1){
            holder.textView1.setTextColor(mContext.getResources().getColor(R.color.colorYANHONG));
            holder.textView1.setTypeface(Typeface.DEFAULT_BOLD);
            holder.textView2.setTextColor(mContext.getResources().getColor(R.color.colorYANHONG));
            holder.textView2.setTypeface(Typeface.DEFAULT_BOLD);
        }
        if (countMemo((Integer) dateList.get(i))>0){
            holder.v.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        view.setBackground(mContext.getResources().getDrawable(R.drawable.text_card));//设置selector
        /*****************设置View的点击事件********************/
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                Intent intent = new Intent(mContext, TodayActivity.class);
                Bundle bundle =new Bundle();
                if(holder.textView1.getText().equals("")){
                    //Toast.makeText(mContext,"不可用",Toast.LENGTH_LONG).show();
                } else {//如果点击的item内容不为空，则显示点击的item内容
                    //Toast.makeText(mContext,holder.textView1.getText(), Toast.LENGTH_SHORT).show();
                    bundle.putCharSequence("day",holder.textView1.getText());
                    bundle.putCharSequence("month",String.valueOf(month));
                    bundle.putCharSequence("year",String.valueOf(year));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtras(bundle);//将bundle对象添加到intent当中
                    mContext.startActivity(intent);//初始化有关intent，用于打开当天的备忘录//
                }
            }
        });
        return view;
    }
    private int countMemo(int i){
        String date=String.format(Locale.CHINA,"%1$s-%2$02d-%3$02d",year,month,i);
        Cursor cursor=dbOpenHelper.getReadableDatabase().query("memo_table",null,
                "date(start_datetime)=?",new String[]{date},null,null,null);
        int count=cursor.getCount();
        cursor.close();
        return count;
    }

    static class ViewHolder{
        TextView textView1;
        TextView textView2;
        View v;
    }
}
