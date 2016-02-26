package com.iermu.ui.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.iermu.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 选择器Dialog工具类(日期 | 时间 | 日期和时间)
 * @author wcy
 *
 */
public class PickerDialogUtil implements OnDateChangedListener, OnTimeChangedListener {

	private static String POSITIVE_BUTTON = "设置";
	private static String NEGATIVE_BUTTON = "取消";
	private Context mContext;
	private boolean isCheckInvalid = true;	//是否检验无效日期|事件
	private Date mInitDate;					//初始化日期|时间
	private PickerChooseListener mListener;	//监听事件

	private PickerDialogUtil(Context context) {
		this.mContext = context;
        POSITIVE_BUTTON = mContext.getResources().getString(R.string.setting);
        NEGATIVE_BUTTON = mContext.getResources().getString(R.string.cancle_txt);
    }

	/**
	 * 初始化日期选择器Dialog对象
	 * @param context
	 * @return
	 */
	public static PickerDialogUtil initDatePicker(Context context) {
		PickerDialogUtil dialog = new PickerDialogUtil(context);
		return dialog;
	}

	/**
	 * 配置初始化日期|时间
	 * @param date	日期|时间
	 * @return
	 */
	public PickerDialogUtil configInitDate(Date date) {
		this.mInitDate = date;
		return this;
	}

	/**
	 * 配置选择器监听事件
	 * @param listener
	 * @return
	 */
	public PickerDialogUtil configListener(PickerChooseListener listener) {
		this.mListener = listener;
		return this;
	}

	/**
	 * 配置检验无效日期｜时间
	 * @param checkInvalid
	 * @return
	 */
	public PickerDialogUtil configCheckInvalid(boolean checkInvalid) {
		this.isCheckInvalid = checkInvalid;
		return this;
	}
	
	/**
	 * 弹出日期选择器
	 * @param inputTextView
	 * @return
	 */
	public AlertDialog showDatePicker(final TextView inputTextView) {
		
		return null;
	}

	/**
	 * 显示时间选择器
	 * @return
	 */
	public AlertDialog showTimePicker() {
		final TimePicker timePicker = new TimePicker(mContext);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		init(mInitDate, null, timePicker);
		AlertDialog ad = new AlertDialog.Builder(mContext)
				//.setTitle()
				.setView(timePicker)
				.setPositiveButton(POSITIVE_BUTTON, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Calendar cal = Calendar.getInstance();
						int year = cal.get(Calendar.YEAR);
						int month= cal.get(Calendar.MONTH);
						int day	 = cal.get(Calendar.DAY_OF_MONTH);
						cal.set(year, month, day, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
						if (mListener != null) {
							mListener.onTimeChoose(cal.getTime());
						}
					}
				}).setNegativeButton(NEGATIVE_BUTTON, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//
					}
				}).show();
		return ad;
	}


	/**
	 * 显示日期选择器
	 * @return
	 */
	public AlertDialog showDatePicker() {
		final DatePicker datePicker = new DatePicker(mContext);
		datePicker.setCalendarViewShown(false);
        init(mInitDate, datePicker);
        AlertDialog ad = new AlertDialog.Builder(mContext)
	        //.setTitle()
        	.setView(datePicker)
        	.setPositiveButton(POSITIVE_BUTTON, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	Calendar cal= Calendar.getInstance();
	            	int year	= datePicker.getYear();
					int month	= datePicker.getMonth();
					int day		= datePicker.getDayOfMonth();
					cal.set(year, month, day);
	            	if(mListener != null) {
	            		mListener.onDateChoose(cal.getTime());
	            	}
	            }
	        }).setNegativeButton(NEGATIVE_BUTTON, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//
	            }
	        }).show();
        return ad;
	}

	/**
	 * 显示日期和时间选择器
	 * @return
	 */
	public AlertDialog showDateTimePicker() {
		final DatePicker datePicker = new DatePicker(mContext);
		datePicker.setCalendarViewShown(false);
		final TimePicker timePicker = new TimePicker(mContext);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);
		LinearLayout layout = new LinearLayout(mContext);
		layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(datePicker);
		layout.addView(timePicker);

        init(mInitDate, datePicker, timePicker);
        AlertDialog ad = new AlertDialog.Builder(mContext)
	        //.setTitle()
        	.setView(layout)
        	.setPositiveButton(POSITIVE_BUTTON, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
	            	Calendar cal= Calendar.getInstance();
	            	int year	= datePicker.getYear();
					int month	= datePicker.getMonth();
					int day		= datePicker.getDayOfMonth();
					int hour	= timePicker.getCurrentHour();
					int minute	= timePicker.getCurrentMinute();
					cal.set(year, month, day, hour, minute);
	            	if(mListener != null) {
	            		mListener.onDateTimeChoose(cal.getTime());
	            	}
	            }
	        }).setNegativeButton(NEGATIVE_BUTTON, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	//
	            }
	        }).show();
        return ad;
	}

	/**
	 * 初始化日期、时间选择器
	 * @param initDate		初始化日期、时间
	 * @param datePicker	日期选择器
	 * @param timePicker	时间选择器
	 */
	private void init(Date initDate, DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
    	if(initDate != null) {
    		calendar.setTime(initDate);
    	}
		if(datePicker != null) {
			int year		= calendar.get(Calendar.YEAR);
			int monthOfYear	= calendar.get(Calendar.MONTH);
			int dayOfMonth	= calendar.get(Calendar.DAY_OF_MONTH);
			datePicker.init(year, monthOfYear, dayOfMonth, this);
			datePicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
		}

    	if(timePicker != null) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    	}
	}

	/**
	 * 初始化日期选择器日期
	 * @param initDate	 初始化日期
	 * @param datePicker 日期选择器
	 */
    public void init(Date initDate, DatePicker datePicker) {
    	init(initDate, datePicker, null);
    }

    //日期选择变更
	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Calendar calendar = Calendar.getInstance();
		long currentTime = calendar.getTimeInMillis();
		calendar.set(year, monthOfYear, dayOfMonth);
		long changedTime = calendar.getTimeInMillis();
		if(isCheckInvalid && changedTime < currentTime) {//校验日期有效性
			calendar.setTimeInMillis(currentTime);
			int currentYear			= calendar.get(Calendar.YEAR);
			int currentMonthOfYear	= calendar.get(Calendar.MONTH);
			int currentDayOfMonth	= calendar.get(Calendar.DAY_OF_MONTH);
			view.updateDate(currentYear, currentMonthOfYear, currentDayOfMonth);
		}
	}

	//时间选择器变更
	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		
	}

	/**
	 * 选择器有效选中事件监听
	 */
	public interface PickerChooseListener {
		/**
		 * 选择日期
		 * @param date 日期对象
		 */
		public abstract void onDateChoose(Date date);

		/**
		 * 选择时间
		 * @param time 时间单位
		 */
		public abstract void onTimeChoose(Date time);

		/**
		 * 选择日期、时间
		 * @param date 包含日期、时间的对象
		 */
		public abstract void onDateTimeChoose(Date date);
	}
}
