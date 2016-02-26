package com.iermu.ui.view.filmEdit;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iermu.R;

/**
 * 通用弹框提示组件
 * <p/>
 * Created by zhangxq on 15/12/10.
 */
public class FilmEditFailDialog extends Dialog {
    private TextView buttonTryAgain;
    private TextView buttonCancel;
    private ImageButton buttonClose;

    public FilmEditFailDialog(Context context) {
        super(context, R.style.load_dialog);
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_record_cut_fail_dialog);
        this.setCanceledOnTouchOutside(false);
        buttonTryAgain = (TextView) findViewById(R.id.buttonTryAgain);
        buttonCancel = (TextView) findViewById(R.id.buttonCancel);
        buttonClose = (ImageButton) findViewById(R.id.buttonClose);
    }

    /**
     * 设置确定按钮监听
     *
     * @param listener
     */
    public void setButtonTryAgainListener(View.OnClickListener listener) {
        this.buttonTryAgain.setOnClickListener(listener);
    }

    /**
     * 设置关闭按钮监听
     *
     * @param listener
     */
    public void setButtonCloseListener(View.OnClickListener listener) {
        this.buttonClose.setOnClickListener(listener);
        this.buttonCancel.setOnClickListener(listener);
    }
}
