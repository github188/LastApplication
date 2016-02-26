package com.iermu.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.iermu.client.util.DateUtil;
import com.iermu.lan.model.CamRecord;
import com.iermu.ui.util.Util;

import java.util.List;

/**
 * Created by zhangxq on 15/11/12.
 */
public class TimeLineView extends View {
    private Paint mPaint;
    private Canvas canvas;

    private Context context;
    private boolean isVisible;

    private int heightBig = 7; // 大刻度的高度dp值
    private int heightSmall = 2; // 小刻度的高度dp值
    private int scaleWidth = 60; // 刻度的宽度像素值
    private int windowWidth; // 屏幕宽度像素值
    private long beginTime; // 开始时间
    private List<CamRecord> records; // 录像列表

    public TimeLineView(Context context) {
        super(context);
    }

    public TimeLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mPaint = new Paint();
    }

    /**
     * 设置显示区域宽度
     *
     * @param windowWidth
     */
    public void draw(int windowWidth, long beginTime, List<CamRecord> camRecords) {
        this.records = camRecords;
        this.windowWidth = windowWidth;
        this.beginTime = beginTime;
        this.invalidate();
    }

    /**
     * 画录像
     *
     * @param camRecords
     */
    public void drawRecord(List<CamRecord> camRecords) {
        mPaint.setColor(0x88888888);
        int canvasHeight = getHeight();
        if (camRecords != null) {
            for (int j = 0; j < camRecords.size(); j++) {
                CamRecord camRecord = camRecords.get(j);
                long startTime = camRecord.getStartTime();
                long endTime = camRecord.getEndTime();
                int beginX = (int) ((startTime - beginTime) / ExpandTimeLineView.SECOND_PER_PIX + windowWidth / 2);
                int endX = (int) ((endTime - beginTime) / ExpandTimeLineView.SECOND_PER_PIX + windowWidth / 2);
                canvas.drawRect(beginX, 0, endX, canvasHeight, mPaint);
            }
        }
    }

    @Override
    public void invalidate() {
        isVisible = true;
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        if (isVisible) {
            int canvasHeight = getHeight();
            int canvasWidth = getWidth();
            int bigBenginY = canvasHeight / 2 - Util.DensityUtil.dip2px(context, heightBig);
            int bigEndY = canvasHeight / 2 + Util.DensityUtil.dip2px(context, heightBig);
            int smallBenginY = canvasHeight / 2 - Util.DensityUtil.dip2px(context, heightSmall);
            int smallEndY = canvasHeight / 2 + Util.DensityUtil.dip2px(context, heightSmall);

            mPaint.setColor(0x88888888);
            if (records != null) {
                for (int j = 0; j < records.size(); j++) {
                    CamRecord camRecord = records.get(j);
                    long startTime = camRecord.getStartTime();
                    long endTime = camRecord.getEndTime();
                    int beginX = (int) ((startTime - beginTime) / ExpandTimeLineView.SECOND_PER_PIX + windowWidth / 2);
                    int endX = (int) ((endTime - beginTime) / ExpandTimeLineView.SECOND_PER_PIX + windowWidth / 2);
                    canvas.drawRect(beginX, 0, endX, canvasHeight, mPaint);
                }
            }

            // 设置文字
            mPaint.setTextSize(40);
            String text = "00:00";
            Rect mBounds = new Rect();
            mPaint.getTextBounds(text, 0, text.length(), mBounds);
            float textWidth = mBounds.width();
            float textHeight = mBounds.height();
            mPaint.setColor(Color.WHITE);
            for (int i = 0; i < canvasWidth / scaleWidth; i++) {
                int x = i * scaleWidth + windowWidth / 2;
                if (i % 5 == 0) {
                    mPaint.setStrokeWidth(5);
                    canvas.drawLine(x, bigBenginY, x, bigEndY, mPaint);
                    long currentTime = (beginTime + i * scaleWidth * ExpandTimeLineView.SECOND_PER_PIX) * 1000;
                    String time = DateUtil.getHourAndMin(currentTime);
                    canvas.drawText(time, x - textWidth / 2, bigEndY + textHeight + 20, mPaint);
                } else {
                    mPaint.setStrokeWidth(1);
                    canvas.drawLine(x, smallBenginY, x, smallEndY, mPaint);
                }
            }

            // 把前面的空间补全
            for (int i = 0; i < 20; i++) {
                int x = windowWidth / 2 - i * scaleWidth;
                if (i % 5 == 0) {
                    mPaint.setStrokeWidth(5);
                    canvas.drawLine(x, bigBenginY, x, bigEndY, mPaint);
                    long currentTime = (beginTime - i * scaleWidth * ExpandTimeLineView.SECOND_PER_PIX) * 1000;
                    String time = DateUtil.getHourAndMin(currentTime);
                    canvas.drawText(time, x - textWidth / 2, bigEndY + textHeight + 20, mPaint);
                } else {
                    mPaint.setStrokeWidth(1);
                    canvas.drawLine(x, smallBenginY, x, smallEndY, mPaint);
                }
            }
        }
    }
}
