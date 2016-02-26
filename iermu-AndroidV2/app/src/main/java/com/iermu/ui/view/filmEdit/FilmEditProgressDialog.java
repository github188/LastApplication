package com.iermu.ui.view.filmEdit;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.iermu.R;

/**
 * 剪辑进度弹框
 * <p/>
 * Created by zhangxq on 15/12/10.
 */
public class FilmEditProgressDialog extends Dialog {
    private TextView buttonIKnow;
    private FilmEditProgressView progressView;
    private TextView textViewProgress;

    public FilmEditProgressDialog(Context context) {
        super(context, R.style.load_dialog);
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_record_cut_progress_dialog);
        this.setCanceledOnTouchOutside(false);
        buttonIKnow = (TextView) findViewById(R.id.buttonIKnow);
        progressView = (FilmEditProgressView) findViewById(R.id.viewProgress);
        textViewProgress = (TextView) findViewById(R.id.textViewProgress);

        progressView.initDate(100, 3);
        progressView.invalidate();
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        progressView.setProgress(progress);
        textViewProgress.setText(getContext().getString(R.string.clip_save_video) + progress + "%");
    }

    /**
     * 设置确定按钮监听
     *
     * @param listener
     */
    public void setButtonIKnowListener(View.OnClickListener listener) {
        this.buttonIKnow.setOnClickListener(listener);
    }
}
