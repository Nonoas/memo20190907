package mView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.hlnote.R;

public class SlidingMemoView extends ScrollView {
    private int scroll_height;//滚动距离
    private int h;
    private ListView lv;
    private Boolean isFirst=false;//标记第一次进入获取控件
    private LinearLayout l1,l2,l3;

    public SlidingMemoView(Context context) {
        super(context,null);
    }

    public SlidingMemoView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
        this.setOverScrollMode(OVER_SCROLL_NEVER);//设置没有滑动到边缘时的泛光
    }

    public SlidingMemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 第一次进入时获取删除按钮控件
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        l1=findViewById(R.id.memo_linear_1);
        l2=findViewById(R.id.memo_linear_2);
        l3=findViewById(R.id.memo_linear_3);
        h=l1.getHeight()+l2.getHeight()+l3.getHeight();//获取日历高度
        scroll_height=l1.getHeight()+l2.getHeight();//获取滑动判断高度
        isFirst=true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //默认隐藏删除按钮
        if(changed){
            this.scrollTo(0,0);
            //h=getChildAt(0).getHeight()+getChildAt(1).getHeight()+getChildAt(2).getHeight();
            //scroll_height =300;//获取课滚动的高度
        }
    }

    /**
     *手势判断
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                changeScolly();
                break;
            case MotionEvent.ACTION_DOWN:
                performClick();
        }
        return super.onTouchEvent(ev);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
    //根据滑动距离判断是否收起日历
    private void changeScolly() {
        if(getScrollY()>= scroll_height){
            this.post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(0, h+10);
                }
            });
        }else{
            this.post(new Runnable() {
                @Override
                public void run() {
                    smoothScrollTo(0, 0);
                }
            });
        }
    }
}
