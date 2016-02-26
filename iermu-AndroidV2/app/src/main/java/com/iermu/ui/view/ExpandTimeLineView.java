package com.iermu.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.lan.model.CamRecord;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.List;

/**
 * Created by zhangxq on 15/11/11.
 */
public class ExpandTimeLineView extends RelativeLayout implements View.OnTouchListener {
    public static int SECOND_PER_PIX = 6;

    @ViewInject(R.id.viewTimeLine)
    TimeLineView viewTimeLine;
    @ViewInject(R.id.horizontalScrollView)
    HorizontalScrollView horizontalScrollView;
    private long beginTime;
    private long endTime;

    private Context context;
    private TimeLineScrollListener listener;
    private boolean isOntouching;

    public ExpandTimeLineView(Context context) {
        super(context);
        initView();
    }

    public ExpandTimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_expand_time_line, this);
        ViewHelper.inject(this, view);

        horizontalScrollView.setOnTouchListener(this);
    }

    /**
     * 初始化开始时间秒，结束时间秒，显示宽度
     *
     * @param beginTime
     * @param endTime
     * @param windowWidth
     */
    public void draw(long beginTime, long endTime, int windowWidth, List<CamRecord> camRecords) {
        if (beginTime == 0) {
            int startTimehour = (int) (endTime / 3600);
            endTime = startTimehour * 3600;
            beginTime = endTime;
        }
        this.beginTime = beginTime;
        this.endTime = endTime;
        int viewWidth = (int) ((endTime - beginTime) / SECOND_PER_PIX);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewTimeLine.getLayoutParams();
        layoutParams.width = viewWidth + windowWidth;
        viewTimeLine.setLayoutParams(layoutParams);
        viewTimeLine.draw(windowWidth, beginTime, camRecords);
    }

    /**
     * 话录像
     *
     * @param camRecords
     */
    public void drawRecords(List<CamRecord> camRecords) {
        viewTimeLine.drawRecord(camRecords);
    }

    /**
     * 设置横向滚动的位置
     *
     * @param scrollToX
     */
    public void scrollTo(int scrollToX) {
        if (!isOntouching) {
            int timeOffet = (int) ((scrollToX - beginTime) / SECOND_PER_PIX);
            horizontalScrollView.smoothScrollTo(timeOffet, 0);
        }
    }

    /**
     * 设置滚动到末尾
     */
    public void scrollToEnd() {
        int timeOffet = (int) ((endTime - beginTime) / SECOND_PER_PIX);
        horizontalScrollView.smoothScrollTo(timeOffet, 0);
    }

    /**
     * 设置回调监听器
     *
     * @param listener
     */
    public void setListener(TimeLineScrollListener listener) {
        this.listener = listener;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (listener != null) {
            listener.onTimeLineTouch(this, event);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isOntouching = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int scrollX = horizontalScrollView.getScrollX();
            long currentTime = (beginTime + scrollX * SECOND_PER_PIX) * 1000;
            if (listener != null) {
                listener.onScroll(currentTime);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            isOntouching = false;
            if (listener != null) {
                listener.onTouchUp();
            }
        }
        return false;
    }

    public interface TimeLineScrollListener {
        /**
         * 滚动过程中调用
         */
        void onScroll(long scrollToTime);

        /**
         * 滚动结束，手指抬起时触发
         */
        void onTouchUp();

        /**
         * 触摸时触发
         */
        void onTimeLineTouch(View v, MotionEvent event);
    }
}
