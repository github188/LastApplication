package com.iermu.ui.view.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.iermu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TimeSpanViewLinearLayout extends LinearLayout {

    private int m_iW = 0;
    private int m_iMovePoint = 0;
    //Bitmap m_timePointer;
    private int m_iTimeSpan = 0;//2*60*60;  // 当前屏幕代表的时间跨度，单位：秒
    private int m_factor = 0;   // 时间刻度与像素变换比例系数
    private int m_iCursorImgPos = 0; // 游标图像点
    private int m_iCursorPos = 0;  // 计算点
    private long m_iCurrTime;
    private int m_iEdgeW;  // 上下边框
    private int m_iEdgeH;   // 左右边框
    private int m_iNewEdgeH;
    private int m_iTop;
    private int m_iBottom;
    private int m_iTimelineImgW; //时间轴宽度
    private int m_iTimelineImgH;   //时间轴高度
    private boolean m_bResetW = false;  // 重新设置指针位置

    private ArrayList<cmsRecStruct> m_playlist = null;
    private int m_iPlaylistLen = 0;

    private Rect m_rectTimeP;

    public static final int MOD_TYPE_HALF = 5;
    public static final int MOD_TYPE_ONE = 30;
    private static final int ITEM_HALF_DIVIDER = 3;
    private static final int ITEM_ONE_DIVIDER = 10;

    private static final int ITEM_MAX_HEIGHT = 40;
    private static final int ITEM_MID_HEIGHT = 40;
    private static final int ITEM_MIN_HEIGHT = 40;
    private int mValue = 50, mMaxValue = 100, mModType = MOD_TYPE_ONE, mLineDivider = ITEM_HALF_DIVIDER;
    private int mLastX = 0;

    public TimeSpanViewLinearLayout(Context context) {
        super(context);
    }

    public TimeSpanViewLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        LayoutInflater.from(context).inflate(R.layout.time_span_view, this, true);
        init();
    }

    void init() {
        m_iEdgeW = getDevDip(0, TimeSpanViewLinearLayout.this.getContext());
        m_iEdgeH = 0;   // 左右边框
        m_iTimelineImgW = 1280; //时间轴宽度, 与实际背景图片尺寸匹配
        m_iTimelineImgH = 50;   //时间轴高度
        m_iTimeSpan = 60 * 60;    //
    }


    // 启动前，要先设置playlist
    public void setPlaylist(ArrayList<cmsRecStruct> reclist, long cursorT) {
        m_iCurrTime = cursorT;
        if (reclist == null) return;
        m_playlist = reclist;
        m_iPlaylistLen = reclist.size();
    }

    // 运行时更新playlist
    public void setPlaylist(ArrayList<cmsRecStruct> reclist) {
        if (reclist == null) return;
        m_playlist = reclist;
        m_iPlaylistLen = reclist.size();
    }

    // 设置当前指针
    public void setTimecursorT(long cursorT) {
        m_iCurrTime = cursorT;
        this.invalidate();
    }

    // 设置滑动屏幕距离
    public void setMovePoint(int iPoint) {
        m_iMovePoint = iPoint;
    }

    // 设置时间跨度系数
    public void setTimeSpan(int timespan) {
        if (timespan >= m_iW) {
            m_iTimeSpan = timespan;
            m_factor = m_iTimeSpan / m_iW;
            this.invalidate();
        }
    }

    public int getTimeSpan() {
        return m_iTimeSpan;
    }

    public int getFactor() {
        return m_factor;
    }

    public int getEdgeW() {
        return m_iEdgeW;
    }

    public int getEdgeH() {
        return m_iTimelineImgH;
    }

    public void resetW() {
        m_bResetW = true;
    }

    /**
     * 计算没有数字显示位置的辅助方法
     *
     * @param value
     * @param xPosition
     * @param textWidth
     * @return
     */
    private float countLeftStart(int value, float xPosition, float textWidth) {
        float xp = 0f;
        if (value < 20) {
            xp = xPosition - (textWidth * 1 / 2);
        } else {
            xp = xPosition - (textWidth * 2 / 2);
        }
        return xp;
    }

    /**
     * 从中间往两边开始画刻度线
     *
     * @param canvas
     */
    private void drawScaleLine(Canvas canvas) {
        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.BLACK);

        int iTextSize = DimenUtils.sp2px(getContext(), 12);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(iTextSize);

        int width = m_iW, drawCount = 0;
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

        int iTop = 20;
        int iBottom = m_iBottom - 20;
        int iH_max = DimenUtils.dip2px(getContext(), ITEM_MAX_HEIGHT);
        int iH_mid = DimenUtils.dip2px(getContext(), ITEM_MID_HEIGHT);
        int iH_min = DimenUtils.dip2px(getContext(), ITEM_MIN_HEIGHT);
        for (int i = 0; drawCount <= 4 * width; i++) {
            int numSize = String.valueOf(mValue + i).length();

            xPosition = (width / 2 - m_iMovePoint) + i * DimenUtils.dip2px(getContext(), mLineDivider);
            if (xPosition < m_iW) {
                if ((mValue + i) % mModType == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_max, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_max + iTop, linePaint);
                    if (mValue + i <= mMaxValue) {
                        long lT = (long) (m_iCurrTime + (xPosition - m_iCursorPos) * m_factor);
                        //Log.d("tanhx", "curr time="+m_iCurrTime + ", xpos=" + xPosition + ", cursorPos=" + m_iCursorPos);
                        canvas.drawText(timeStamp2Time(lT, false), xPosition - (textWidth * numSize / 2), getHeight() / 2 + iTextSize / 2, textPaint);
                    }
                } else if ((mValue + i) % MOD_TYPE_HALF == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_mid, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_mid + iTop, linePaint);
                } else {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_min, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_min + iTop, linePaint);

                }
            }

            xPosition = (width / 2 - m_iMovePoint) - i * DimenUtils.dip2px(getContext(), mLineDivider);
            if (xPosition > 0) {
                if ((mValue - i) % mModType == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_max, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_max + iTop, linePaint);

                    if (mValue - i >= 0) {
                        long lT = (long) (m_iCurrTime - (m_iCursorPos - xPosition) * m_factor);
                        canvas.drawText(timeStamp2Time(lT, false), xPosition - (textWidth * numSize / 2), getHeight() / 2 + iTextSize / 2, textPaint);
                    }
                } else if ((mValue + i) % MOD_TYPE_HALF == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_mid, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_mid + iTop, linePaint);

                } else {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_min, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_min + iTop, linePaint);

                }
            }

            drawCount += 2 * DimenUtils.dip2px(getContext(), mLineDivider);
        }

        //画指针   #f5f4f0
        /*linePaint.setColor(Color.rgb(0xf5, 0xf4, 0xf0));
        linePaint.setStrokeWidth(4);
		int iPosX = m_iW/2;
		canvas.drawLine(iPosX, iTop, iPosX, iBottom, linePaint);
		*/
        canvas.restore();
    }

    private void drawScaleLineT(Canvas canvas) {
        canvas.save();

        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.BLACK);

        int iTextSize = DimenUtils.sp2px(getContext(), 12);
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(iTextSize);

        int width = m_iW, drawCount = 0;
        float xPosition = 0, textWidth = Layout.getDesiredWidth("0", textPaint);

        int iTop = 20;
        int iBottom = m_iBottom - 20;
        int iH_max = DimenUtils.dip2px(getContext(), ITEM_MAX_HEIGHT);
        int iH_mid = DimenUtils.dip2px(getContext(), ITEM_MID_HEIGHT);
        int iH_min = DimenUtils.dip2px(getContext(), ITEM_MIN_HEIGHT);
        for (int i = 0; drawCount <= 4 * width; i++) {
            int numSize = String.valueOf(mValue + i).length();

            int iPxDivider = DimenUtils.dip2px(getContext(), mLineDivider);
            xPosition = (width / 2) + i * iPxDivider;
            if (xPosition < m_iW) {
                if ((mValue + i) % mModType == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_max, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_max + iTop, linePaint);
                    //if (mValue + i <= mMaxValue) {
                    long lT = (long) (m_iCurrTime + (iPxDivider * i) * m_factor);
                    //Log.d("tanhx", "lt = " + lT);
                    canvas.drawText(timeStamp2Time(lT, false), xPosition - (textWidth * numSize / 2), getHeight() / 2 + iTextSize / 2, textPaint);
                    //}
                } else if ((mValue + i) % MOD_TYPE_HALF == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_mid, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_mid + iTop, linePaint);
                } else {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_min, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_min + iTop, linePaint);

                }
            }

            xPosition = (width / 2) - i * DimenUtils.dip2px(getContext(), mLineDivider);
            if (xPosition > 0) {
                if ((mValue - i) % mModType == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_max, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_max + iTop, linePaint);

                    //if (mValue - i >= 0) {
                    long lT = (long) (m_iCurrTime - (i * iPxDivider) * m_factor);
                    canvas.drawText(timeStamp2Time(lT, false), xPosition - (textWidth * numSize / 2), getHeight() / 2 + iTextSize / 2, textPaint);
                    //}
                } else if ((mValue + i) % MOD_TYPE_HALF == 0) {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_mid, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_mid + iTop, linePaint);

                } else {
                    canvas.drawLine(xPosition, iTop, xPosition, iH_min, linePaint);
                    canvas.drawLine(xPosition, iBottom, xPosition, iBottom - iH_min + iTop, linePaint);

                }
            }

            drawCount += 2 * DimenUtils.dip2px(getContext(), mLineDivider);
        }

        //画指针   #f5f4f0
        /*linePaint.setColor(Color.rgb(0xf5, 0xf4, 0xf0));
        linePaint.setStrokeWidth(4);
		int iPosX = m_iW/2;
		canvas.drawLine(iPosX, iTop, iPosX, iBottom, linePaint);
		*/
        canvas.restore();
    }

    /**
     * 需要传入——m_iCurrTime  m_factor 就可以绘制录像段了
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (m_iW == 0 || m_bResetW) {  // 初始化
            m_bResetW = false;
            m_iW = getWidth();
            m_iCursorPos = m_iW / 2;
            m_iW = m_iW - 2 * m_iEdgeW;
            if (m_factor == 0) {
                if (m_iTimeSpan == 0) {
                    m_iTimeSpan = m_iW;
                }
                m_factor = m_iTimeSpan / m_iW;
            }
            m_iNewEdgeH = (int) (m_iEdgeH * (float) getHeight() / (float) m_iTimelineImgH);
            m_iBottom = this.getHeight() - m_iNewEdgeH;
            m_iTop = m_iNewEdgeH + 1;
			/*int iTimepointW = m_timePointer.getWidth();
			int iTimepointH = getHeight();
			m_iCursorImgPos = m_iCursorPos-iTimepointW/2;
			m_rectTimeP = new Rect(m_iCursorImgPos, m_iTop+15, m_iCursorImgPos+iTimepointW, m_iBottom-16);*/
        }
        Paint p = new Paint();
        int iLast_l = -1; // 上一次的位置
        int iLast_r = -1;
        int iLeft, iRight;
        for (int i = 0; i < m_iPlaylistLen; i++) {
            cmsRecStruct playlist = m_playlist.get(i);
            //m_iCurrTime = playlist.rec_start_time;
            iLeft = m_iMovePoint + m_iCursorPos + (int) (playlist.rec_start_time - m_iCurrTime) / m_factor;
            iRight = m_iMovePoint + m_iCursorPos + (int) (playlist.rec_end_time - m_iCurrTime) / m_factor;
            if (iRight < m_iEdgeW || iLeft > m_iW + m_iEdgeW) continue;
            if (iLeft < m_iEdgeW) iLeft = m_iEdgeW;
            if (iRight > m_iW + m_iEdgeW) iRight = m_iW + m_iEdgeW;
            p.setColor(0x8017afeb);
            if (iRight - iLeft < 1) iRight = iLeft + 1;
            if (iLeft <= iLast_r) iLeft = iLast_r + 1;
            if (iRight - iLeft >= 1) {
                canvas.drawRect(new Rect(iLeft, m_iTop + 20, iRight, m_iBottom - 20), p);
            }
            iLast_l = iLeft;
            iLast_r = iRight;
        }

        // 画刻度
        drawScaleLineT(canvas);

    }

    public int getDevDip(int idx, Context c) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                idx, c.getResources().getDisplayMetrics());
        return px;
    }

    public String timeStamp2Time(long timestampString, boolean bS) {
        SimpleDateFormat dspFmt = new SimpleDateFormat(bS ? "HH:mm:ss" : "HH:mm");
        String date = dspFmt.format(new java.util.Date(timestampString * 1000));
        return date;
    }

    public String timeStamp2Time(long timestampString) {
        SimpleDateFormat dspFmt = new SimpleDateFormat("HH:mm:ss");
        String date = dspFmt.format(new java.util.Date(timestampString * 1000));
        return date;
    }

}
