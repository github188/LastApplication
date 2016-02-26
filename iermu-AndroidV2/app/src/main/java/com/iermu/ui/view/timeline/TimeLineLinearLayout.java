package com.iermu.ui.view.timeline;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iermu.R;

import java.util.ArrayList;

public class TimeLineLinearLayout extends LinearLayout {

	public TimeSpanViewLinearLayout mTSV;
	public TextView m_timeTip;

	private RelativeLayout mTimePointer; // 时间信息提示
	private int m_iScrW = 0;
	private int m_iPortH;
	
	public TimeLineLinearLayout(Context context) {
		super(context);
	}

	public TimeLineLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		// 导入布局
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.time_line, this, true);
//		LayoutInflater.from(context).inflate(getRes(context, "time_line", "layout"), this, true);
		mTSV = (TimeSpanViewLinearLayout) findViewById(R.id.TimeSpanView_layout );
		// 背景默认为白色，可自行设定
		//mTSV.setBackgroundColor(Color.TRANSPARENT);    #0f3055
		// 时间指针
		mTimePointer = (RelativeLayout) findViewById(R.id.time_pointer);
		m_timeTip = (TextView) findViewById(R.id.time_line_tip);
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		boolean bLand = newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE;
		//int iH = DimenUtils.dip2px(getContext(), 100);
		//setViewH(iH);
	}
	
	public void setViewH(int iH){
		int iMaxH = DimenUtils.dip2px(getContext(), 100);
		if(iH>iMaxH) iH = iMaxH;
		Log.d("tanhx", "max=" + iMaxH + ", h=" + iH);
		if(iH>iMaxH) iH = iMaxH;
		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) mTSV.getLayoutParams();
		p.height = iH;
		mTSV.requestLayout();
		View v = findViewById(R.id.time_point_bg);
		p = (RelativeLayout.LayoutParams) v.getLayoutParams();
		p.height = iH;
		v.requestLayout();
		v = findViewById(R.id.time_point_line);
		p = (RelativeLayout.LayoutParams) v.getLayoutParams();
		p.height = iH-10;// bLand? iH-10 : iH;
		v.requestLayout();
		mTSV.resetW();
	}
	
	public void setTimePointer(boolean bVisible){
		mTimePointer.setVisibility(bVisible? View.VISIBLE : View.GONE);
	}
	
	public void setPlaylist(ArrayList<cmsRecStruct> playlist, long cursorT) {
		mTSV.setPlaylist(playlist, cursorT);
		if(m_iScrW==0){
			m_iScrW = getWidth();
			// 时间指针初始化
			timePointerInit(false);
		}
	}

	public void setTipPos(int iW){
		m_iScrW = iW;
		// 时间指针初始化
		timePointerInit(true);
	}
	
	public void setPlaylist(ArrayList<cmsRecStruct> playlist){
		mTSV.setPlaylist(playlist);
	}
	
	public void setTimecusorT(int cursorT){
		mTSV.setTimecursorT(cursorT);
	}
	
	private void timePointerInit(boolean m_bVisible) {
		/*if(mTimePointer==null) return;
		// 移到最左边
		Paint paint = new Paint();        
		paint.setTextSize(m_timeTip.getTextSize());     
		String strTemp = m_timeTip.getText().toString();
		if(strTemp==null || strTemp.equals("")) strTemp = "";

		if(m_bVisible) return;
		// 设置时间指针不可见
		//mTimePointer.setVisibility(View.VISIBLE);
		//m_timeTip.setVisibility(View.GONE);
*/	}

	public void setBackgroundColor(int color) {
		// 将TimeSpanView的背景色改变
		mTSV.setBackgroundColor(color);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	public  int getRes(Context c, String strName, String strDefType) {
		return c.getResources().getIdentifier(strName, strDefType,
				c.getPackageName());
	}

	// 随机0-255整数
	private int RandomNum() {
		return (int) (Math.random() * 255);
	}
}
