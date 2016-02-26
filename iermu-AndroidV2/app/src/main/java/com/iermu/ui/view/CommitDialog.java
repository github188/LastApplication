package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.iermu.R;

/**
 * Created by xjy on 15/8/25.
 */
public class CommitDialog extends Dialog {

    public static final int NORMAL_TYPE = 0;
    private View contextView;
    private TextView loadText;
    private Context context;

    public CommitDialog(Context context, int theme) {
        super(context, R.style.custom_dialog);
    }

    public CommitDialog(Context context) {
        super(context, NORMAL_TYPE);
        this.context =context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCanceledOnTouchOutside(false);
//        contextView = View.inflate(context, R.layout.custom_loading_dialog, null);
        setContentView(R.layout.custom_loading_dialog);
        loadText = (TextView) findViewById(R.id.load_text);

    }

    public void cancel(long delayTime) {
        contextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, delayTime);
    }

    public void setStatusText(String text) {
        loadText.setText(text);
    }
}
