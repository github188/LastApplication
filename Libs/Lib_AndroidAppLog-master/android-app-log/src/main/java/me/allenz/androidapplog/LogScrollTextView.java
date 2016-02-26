package me.allenz.androidapplog;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class LogScrollTextView extends EditText {

    public LogScrollTextView(final Context context) {
		super(context);
		setDefaultAttributes();
		setAutoScrolling();
	}

	public LogScrollTextView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setAutoScrolling();
	}

	public LogScrollTextView(final Context context, final AttributeSet attrs,
                             final int defStyle) {
		super(context, attrs, defStyle);
		setAutoScrolling();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setDefaultAttributes() {
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
		setBackgroundColor(Color.parseColor("#00000000"));
        setScrollbarFadingEnabled(true);
        setNestedScrollingEnabled(true);
	}

	private void setAutoScrolling() {
		setGravity(Gravity.BOTTOM);
		setSingleLine(false);
		//setKeyListener(null);
		//setMovementMethod(ScrollingMovementMethod.getInstance());
//		addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(final CharSequence s, final int start,
//					final int before, final int count) {
//			}
//
//			@Override
//			public void beforeTextChanged(final CharSequence s,
//					final int start, final int count, final int after) {
//			}
//
//			@Override
//			public void afterTextChanged(final Editable s) {
//				final int scrollAmount = getLayout().getLineTop(getLineCount())
//						- getHeight();
//				if (scrollAmount > 0) {
//                    scrollTo(0, scrollAmount);
//				} else {
//                    scrollTo(0, 0);
//				}
//			}
//		});
	}

//	@Override
//	public boolean dispatchTouchEvent(final MotionEvent event) {
//		return false;
//	}
}
