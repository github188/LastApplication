package com.iermu.ui.view.filmEdit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.iermu.ui.util.Util;

/**
 * Created by zhangxq on 15/12/28.
 */
public class FilmEditProgressView extends View {
    private Paint paint;
    private Context context;
    private int lineWidth = 5;
    private int progress = 0;
    private int totalProgress = 100;
    private int width = 0;

    public FilmEditProgressView(Context context) {
        super(context);
        this.context = context;
    }

    public FilmEditProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 初始化数据
     *
     * @param width
     * @param lineWidth
     */
    public void initDate(int width, int lineWidth) {
        this.width = Util.DensityUtil.dip2px(context, width);
        this.lineWidth = Util.DensityUtil.dip2px(context, lineWidth);
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 设置画笔相关属性
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff3af0da);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        // 位置
        RectF mRectF = new RectF();
        mRectF.left = lineWidth / 2; // 左上角x
        mRectF.top = lineWidth / 2; // 左上角y
        mRectF.right = width - lineWidth / 2; // 左下角x
        mRectF.bottom = width - lineWidth / 2; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, ((float) progress / totalProgress) * 360, false, paint);
    }
}
