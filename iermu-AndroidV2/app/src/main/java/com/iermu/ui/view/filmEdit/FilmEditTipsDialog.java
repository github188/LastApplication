package com.iermu.ui.view.filmEdit;

import android.app.Dialog;
import android.content.Context;
import android.location.GpsStatus;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;

/**
 * 通用弹框提示组件
 * <p>
 * Created by zhangxq on 15/12/10.
 */
public class FilmEditTipsDialog extends Dialog {
    private TextView buttonIKnow;

    public FilmEditTipsDialog(Context context) {
        super(context, R.style.load_dialog);
        initView();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_record_cut_tips_dialog);
        this.setCanceledOnTouchOutside(false);
        buttonIKnow = (TextView) findViewById(R.id.buttonIKnow);
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
