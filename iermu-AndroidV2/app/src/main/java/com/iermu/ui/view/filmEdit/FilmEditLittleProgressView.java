package com.iermu.ui.view.filmEdit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.iermu.ui.util.Util;

/**
 * Created by zhangxq on 15/12/28.
 */
public class FilmEditLittleProgressView extends View {
    private Paint paint;
    private Context context;
    private int lineWidth = 3;
    private int progress = 0;
    private int totalProgress = 100;
    private int width = 0;

    public FilmEditLittleProgressView(Context context) {
        super(context);
        this.context = context;
    }

    public FilmEditLittleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 初始化数据
     *
     * @param width
     */
    public void initDate(int width) {
        this.width = Util.DensityUtil.dip2px(context, width);
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
        paint.setColor(0x88888888);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.FILL);
        // 位置
        RectF mRectF = new RectF();
        mRectF.left = lineWidth + 1; // 左上角x
        mRectF.top = lineWidth + 1; // 左上角y
        mRectF.right = width - lineWidth - 1; // 左下角x
        mRectF.bottom = width - lineWidth - 1; // 右下角y

        // 绘制圆圈，进度条背景
        canvas.drawArc(mRectF, -90, ((float) progress / totalProgress) * 360, true, paint);

        paint.setColor(0x88888888);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);

        mRectF.left = 2; // 左上角x
        mRectF.top = 2; // 左上角y
        mRectF.right = width - 2; // 左下角x
        mRectF.bottom = width - 2; // 右下角y

        canvas.drawArc(mRectF, 0, 360, false, paint);
    }
}
