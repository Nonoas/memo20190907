package Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.hlnote.R;

import java.util.ArrayList;
import java.util.List;

public class MyDatePiker {
    private Context context;
    private ListView lvy, lvm, lvd;
    private int YEAR, MONTH, DAY;
    private String form;
    private DateUtils dateUtils=new DateUtils();
    final private int REF_YEAR_S=dateUtils.REF_YEAR_S;
    final private int REF_YEAR_E=dateUtils.REF_YEAR_E;
    private ArrayList yearList=new ArrayList<>(), monthList=new ArrayList(), dayList=new ArrayList();

    public MyDatePiker(Context context, int y, int m, int d, String form) {
        this.context = context;
        this.YEAR = y;
        this.MONTH = m;
        this.DAY = d;
        this.form=form;
        if (!form.equals("y-m")&&!form.equals("y-m-d")){
            Log.e("MyDatePiker.class", "form的参数错误");
        }
    }

    public View myDatePickerView() {
        View view = LayoutInflater.from(context).inflate(R.layout.mydatepiker_layout, null);
        LinearLayout l=view.findViewById(R.id.piker_linear_layout);
        lvy=view.findViewById(R.id.lvy);
        lvm=view.findViewById(R.id.lvm);
        lvd=view.findViewById(R.id.lvd);
        initDateView();
        if(form.equals("y-m")) {
            l.removeViewAt(5);
            l.removeViewAt(4);
        }
        return view;
    }

    private void initDateView() {
        getContent(yearList, monthList, dayList);

        final MyAdapter adapter=new MyAdapter(dayList);
        final Handler handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what){
                    case 0x222:
                        String gy=getYear();//获取当前显示的年
                        String gm=getMonth();//获取当前显示的月
                        String gd=getDay();//获取当前显示的日
                        resetDayContent(dayList,Integer.parseInt(gy),Integer.parseInt(gm));
                        adapter.notifyDataSetChanged();
                        if(Integer.parseInt(gd)>dayList.size()){
                            lvd.setSelection(dayList.size()-2);
                        }else {
                            lvd.setSelection(Integer.parseInt(gd)-2);
                        }
                }
                return false;
            }
        });

        lvd.setAdapter(adapter);
        lvd.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        int a = 0 - lvd.getChildAt(0).getTop();
                        int b = lvd.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        if (f <=0.55) {
                            lvd.setSelection(lvd.getFirstVisiblePosition());
                        }
                        if (f > 0.55 && f < 1) {
                            lvd.setSelection(lvd.getFirstVisiblePosition() + 1);
                        }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 3) {
                    lvd.setSelection(dayList.size()+firstVisibleItem+1);
                } else if ( lvd.getLastVisiblePosition()> dayList.size()*3 - 3) {//到底部添加数据
                    lvd.setSelection(firstVisibleItem- dayList.size());
                }
            }
        });
        lvd.setSelection(dayList.size()+DAY-2);//设置当前选中为List的中数，即当前日期

        lvy.setAdapter(new MyAdapter(yearList));
        lvy.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        //获得当滑动结束后listview可见的第0个item的滚动距离
                        int a = 0 - lvy.getChildAt(0).getTop();
                        //获得listview的每个item的高度
                        int b = lvy.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        //如果滑动出屏幕的item的大小占item大小的比重在0到0.6之间的话
                        // 显示第一个可见的，就是说如果移动范围小的话显示的日期是不变的
                        if (f <=0.55) {
                            lvy.setSelection(lvy.getFirstVisiblePosition());
                        }
                        //如果滑动出屏幕的item的大小占item大小的比重在0.6到1之间的话
                        //显示下一个item
                        if (f > 0.55&& f < 1) {
                            lvy.setSelection(lvy.getFirstVisiblePosition() + 1);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x222);
                            }
                        }).start();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 3) {
                    lvy.setSelection(yearList.size()+firstVisibleItem+1);
                } else if ( lvy.getLastVisiblePosition()> yearList.size()*3 - 3) {//到底部添加数据
                    lvy.setSelection(firstVisibleItem- yearList.size());
                }
            }
        });
        lvy.setSelection(YEAR-REF_YEAR_S-1);//设置显示选中为当前日期

        lvm.setAdapter(new MyAdapter(monthList));
        lvm.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态
                        int a;
                        a= 0 - lvm.getChildAt(0).getTop();
                        int b = lvm.getMeasuredHeight() / 3;
                        float f = (float) a / b;
                        if (f <= 0.55) {
                            lvm.setSelection(lvm.getFirstVisiblePosition());
                        }
                        if (f > 0.55 && f < 1) {
                            lvm.setSelection(lvm.getFirstVisiblePosition() + 1);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0x222);
                            }
                        }).start();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < 3) {
                    lvm.setSelection(monthList.size()+firstVisibleItem+1);
                } else if ( lvm.getLastVisiblePosition()> monthList.size()*3 - 3) {//到底部添加数据
                    lvm.setSelection(firstVisibleItem- monthList.size());
                }
            }
        });
        lvm.setSelection(monthList.size()+MONTH-2);//设置当前选中为List的中数，即当前日期
    }

    private void getContent(List<String> year_list, List<String> mouth_list, List<String> day_list) {
        //因为选择器是分3格，中间显示的数字才算是时间，拉到最上方和最下方的时候都要留白，所以要赋值的话list前后要填加“空”，
        String my;
        for (int i = 0;i<REF_YEAR_E-REF_YEAR_S+1; i++) {
            int m = REF_YEAR_S+i;
            my = m + "";
            year_list.add(i,my);
        }
        String mm;
        for (int i = 0; i < 12; i++) {
            int m=i+1;
                mm = m+ "";
            mouth_list.add(i, mm);
        }
        String md;
        for (int i = 0; i < dateUtils.daysOfmonth(YEAR,MONTH); i++) {
            int m=i+1;
            md = m + "";
            day_list.add(i, md);
        }
    }

    private void resetDayContent(List<String> day_list,int _year,int _month){
        if(day_list.size()!=dateUtils.daysOfmonth(_year,_month)){
            day_list.clear();
            String md;
            for (int i = 0; i < dateUtils.daysOfmonth(_year,_month); i++) {
                int m=i+1;
                md = m + "";
                day_list.add(i, md);
            }
        }
    }

    public String getYear(){
        int position=(lvy.getFirstVisiblePosition()+1)%yearList.size();
        return yearList.get(position).toString().trim();
    }
    public String getMonth(){
        int position=(lvm.getFirstVisiblePosition()+1)%monthList.size();
        Log.i("AAAA",lvm.getFirstVisiblePosition()+" "+position);
        return monthList.get(position).toString().trim();
    }
    public String getDay(){
        int position=(lvd.getFirstVisiblePosition()+1)%dayList.size();
        return dayList.get(position).toString().trim();
    }
    public String getDateStr() {
        String year = getYear();
        String month = getMonth();
        switch (form) {
            case "y-m":
                return year + "年" + month + "月";
            case "y-m-d":
                String day = getDay();
                return year + "年" + month + "月" + day + "日";
        }
        return "传进的form的值有误";
    }

    private class MyAdapter extends BaseAdapter {
        private ArrayList list;

        public MyAdapter(ArrayList list) {
            this.list = list;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            if (list != null)
                return list.size() * 3;
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

