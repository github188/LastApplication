package com.iermu.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.ui.util.Util;

/**
 * 通用弹框提示组件
 * <p/>
 * Created by zhangxq on 15/12/10.
 */
public class CommonDialog extends Dialog {
    private Context context;

    private TextView textViewTitle;
    private TextView textViewContent;
    private View viewTwoButton;

    private TextView buttonCancel;
    private TextView buttonOk;
    private TextView buttonOne;

    private View.OnClickListener cancelListener;
    private View.OnClickListener okListener;

    public CommonDialog(Context context) {
        super(context, R.style.load_dialog);
        this.context = context;
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_common_dialog);
        textViewTitle = (TextView) findViewById(R.id.textViewCommonTitle);
        textViewContent = (TextView) findViewById(R.id.textViewCommonContent);
        viewTwoButton = findViewById(R.id.viewTwoButton);
        buttonCancel = (TextView) findViewById(R.id.buttonLeft);
        buttonOk = (TextView) findViewById(R.id.buttonRight);
        buttonOne = (TextView) findViewById(R.id.buttonOne);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public CommonDialog setTitle(String title) {
        textViewTitle.setVisibility(View.VISIBLE);
        textViewTitle.setText(title);
        return this;
    }

    /**
     * 设置内容
     *
     * @param content
     */
    public CommonDialog setContent(String content) {
        textViewContent.setVisibility(View.VISIBLE);
        textViewContent.setText(content);
        return this;
    }

    /**
     * 设置取消按钮文字
     *
     * @param cancelText
     * @return
     */
    public CommonDialog setCancelText(String cancelText) {
        buttonCancel.setText(cancelText);
        return this;
    }

    /**
     * 设置确定按钮文字
     *
     * @param okText
     * @return
     */
    public CommonDialog setOkText(String okText) {
        buttonOk.setText(okText);
        return this;
    }

    /**
     * 设置取消监听事件
     *
     * @param cancelListener
     * @return
     */
    public CommonDialog setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
        return this;
    }

    /**
     * 设置确定监听事件
     *
     * @param okListener
     * @return
     */
    public CommonDialog setOkListener(View.OnClickListener okListener) {
        this.okListener = okListener;
        return this;
    }

    @Override
    public void show() {
        if (cancelListener != null && okListener != null) {
            buttonCancel.setOnClickListener(cancelListener);
            buttonOk.setOnClickListener(okListener);
        } else if (cancelListener == null && okListener == null) {
            ErmuApplication.toast("必须设置至少一个监听");
        } else if (cancelListener == null) {
            buttonOne.setVisibility(View.VISIBLE);
            viewTwoButton.setVisibility(View.GONE);
            buttonOne.setOnClickListener(okListener);
        } else if (okListener == null) {
            buttonOne.setVisibility(View.VISIBLE);
            viewTwoButton.setVisibility(View.GONE);
            buttonOne.setOnClickListener(cancelListener);
        }
        super.show();
    }
}
