package Utils;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hlnote.R;

import java.util.ArrayList;
import java.util.List;

public class MyTimePiker {
    private Context context;
    private ListView lvmn, lvh, lvmin;
    private int HOUR, MINUTE;
    final String[] str_mn= new String[]{"","上午","下午","","全天"};
    private int M_N;
    private ArrayList mnList,hourList, minuteList;

    public MyTimePiker(Context context,int hour,int minute){
        this.context = context;
        if(hour>12){
            this.M_N=2;//下午
            this.HOUR=hour-12;
        }else if(hour==12){
            this.M_N=2;//下午
        }else {
            this.M_N=1;//上午
            this.HOUR=hour;
        }
        this.MINUTE=minute;
    }

    public View myTimerPickerView() {
        View view = LayoutInflater.from(context).inflate(R.layout.mytimepiker_layout, null);
        lvmn=view.findViewById(R.id.lv_mn);
        lvh=view.findViewById(R.id.lv_h);
        lvmin =view.findViewById(R.id.lv_m);
        initDateView();
        return view;
    }

    private void initDateView() {
        mnList = new ArrayList();
        hourList = new ArrayList();
        minuteList = new ArrayList();
        getContent(mnList, hourList, minuteList);

        lvmn.setAdapter(new MyAdapter(mnList,"1x"));
        lvmn.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        int a = 0 - lvmn.getChildAt(0).getTop();
                        int b = lvmn.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        if (f <=0.55) {
                            lvmn.setSelection(lvmn.getFirstVisiblePosition());
                        }
                        if (f > 0.55 && f < 1) {
                            lvmn.setSelection(lvmn.getFirstVisiblePosition() + 1);
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        lvmn.setSelection(M_N-1);//设置当前选中为List的中数，即当前日期

        lvh.setAdapter(new MyAdapter(hourList,"3x"));
        lvh.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        //获得当滑动结束后listview可见的第0个item的滚动距离
                        int a = 0 - lvh.getChildAt(0).getTop();
                        //获得listview的每个item的高度
                        int b = lvh.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        //如果滑动出屏幕的item的大小占item大小的比重在0到0.6之间的话
                        // 显示第一个可见的，就是说如果移动范围小的话显示的日期是不变的
                        if (f <=0.55) {
                            lvh.setSelection(lvh.getFirstVisiblePosition());
                        }
                        //如果滑动出屏幕的item的大小占item大小的比重在0.6到1之间的话
                        //显示下一个item
                        if (f > 0.55&& f < 1) {
                            lvh.setSelection(lvh.getFirstVisiblePosition() + 1);
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 3) {
                    lvh.setSelection(hourList.size()+firstVisibleItem+1);
                } else if ( lvh.getLastVisiblePosition()> hourList.size()*3 - 3) {//到底部添加数据
                    lvh.setSelection(firstVisibleItem- hourList.size());
                }
            }
        });
        lvh.setSelection(hourList.size()+HOUR-2);//设置显示选中位置

        lvmin.setAdapter(new MyAdapter(minuteList,"3x"));
        lvmin.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        int a;
                        a= 0 - lvmin.getChildAt(0).getTop();
                        int b = lvmin.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        if (f <= 0.55) {
                            lvmin.setSelection(lvmin.getFirstVisiblePosition());
                        }
                        if (f > 0.55 && f < 1) {
                            lvmin.setSelection(lvmin.getFirstVisiblePosition() + 1);
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 3) {
                    lvmin.setSelection(minuteList.size()+firstVisibleItem+1);
                } else if ( lvmin.getLastVisiblePosition()> minuteList.size()*3 - 3) {//到底部添加数据
                    lvmin.setSelection(firstVisibleItem- minuteList.size());
                }
            }
        });
        lvmin.setSelection(minuteList.size()+MINUTE-1);//设置当前选中为List的中数，即当前日期
    }

    private void getContent(List mn_list, List hour_list, List minute_list) {
        //上午下午
        for (int i=0;i<4;i++) {
            mn_list.add(str_mn[i]);
        }
        String h;//小时
        for (int i = 0; i < 12; i++) {
            int mh=i+1;
            if (mh<10){
                h = "0"+mh;
            }else {
                h=mh+"";
            }
            hour_list.add(i,h);
        }
        String m;//分钟
        for (int i = 0; i < 60; i++) {
            int mm=i;
            if(mm<10){
                m="0"+i;
            }else {
               m=i+"";
            }
            minute_list.add(i,m);
        }
    }

    //输出选中的上下午
    public String getM_N(){
        int i = (lvmn.getFirstVisiblePosition()+1);
        return str_mn[i] ;
    }
    //输出选中的小时(十二小时制)
    public int getHour(){
        int hour = (lvh.getFirstVisiblePosition()+1)%hourList.size()+1;
        return hour;
    }
    //输出选中的分钟
    public int getMinute(){
        int munite=(lvmin.getFirstVisiblePosition()+1)%minuteList.size();
        return munite;
    }
    //将选中的时间输出字符串
    public String getTimeStr() {
        String Hour=getHour()<10?"0"+getHour():""+getHour();
        String Minute=getMinute()<10?"0"+getMinute():""+getMinute();
        return getM_N()+Hour+":"+Minute;
    }


    private class MyAdapter extends BaseAdapter {
        private ArrayList list;
        private String form;

        public MyAdapter(ArrayList list,String form) {
            this.list = list;
            this.form = form;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            if (list != null){
               switch (form){
                   case "1x": return list.size();
                   case "3x": return list.size()*3;
               }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return list.get(position % list.size());
        }

        @Override
        public long getItemId(int position) {
            return position % list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.piker_list_item, null);
                viewHolder.tvText = convertView.findViewById(R.id.piker_list_num);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvText.setText(String.valueOf(list.get(position % list.size())));//取余展示数据
            return convertView;
        }

    }
    static class ViewHolder{
        TextView tvText;
    }
}

