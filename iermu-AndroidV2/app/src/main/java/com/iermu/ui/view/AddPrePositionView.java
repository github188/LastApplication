package com.iermu.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.util.Logger;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;

/**
 * 添加预置位弹框
 *
 * Created by zhangxq on 15/10/20.
 */
public class AddPrePositionView extends LinearLayout implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, TextWatcher {
    private Context context;

    @ViewInject(R.id.textViewAddCancel)
    TextView textViewAddCancel;
    @ViewInject(R.id.textViewAddSubmit)
    TextView textViewAddSubmit;
    @ViewInject(R.id.imageViewAdd)
    ImageView imageViewAdd;
    @ViewInject(R.id.editTextAdd)
    EditText editTextAdd;
    @ViewInject(R.id.viewAddRadioGroup)
    RadioGroup viewAddRadioGroup;

    private String position1 = "门口";
    private String position2 = "窗户";
    private String position3 = "客厅";
    private AddPositionCallBack listener;

    private String textBefore = ""; // 记录编辑前文字

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setListener(AddPositionCallBack listener) {
        this.listener = listener;
    }

    /**
     * 设置预置位图片
     *
     * @param path
     */
    public void setImage(String path) {
        Picasso.with(context).load(new File(path)).into(imageViewAdd);
    }

    public void setImage(Bitmap bitmap) {
        imageViewAdd.setImageBitmap(bitmap);
    }

    /**
     * 隐藏键盘
     */
    public void hideKeyboard() {
        // 隐藏键盘
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextAdd.getWindowToken(), 0);
    }

    public AddPrePositionView(Context context) {
        super(context);
    }

    public AddPrePositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_add_cloud_preposition, this);
        ViewHelper.inject(this, view);
        position1 = context.getString(R.string.door);
        position2 = context.getString(R.string.window);
        position3 = context.getString(R.string.parlour);

        viewAddRadioGroup.setOnCheckedChangeListener(this);
        editTextAdd.addTextChangedListener(this);
    }

    /**
     * 初始化位置
     *
     * @param title
     */
    public void setData(String title) {
        Logger.d("title:" + title);
        editTextAdd.setText(title);
        if (title == null || title.length() == 0) {
            textViewAddSubmit.setEnabled(false);
            viewAddRadioGroup.clearCheck();
            editTextAdd.setText("");
            return;
        }
        if (title.equals(position1)) {
            viewAddRadioGroup.check(R.id.radioButton1);
        } else if (title.equals(position2)) {
            viewAddRadioGroup.check(R.id.radioButton2);
        } else if (title.equals(position3)) {
            viewAddRadioGroup.check(R.id.radioButton3);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        String text = editTextAdd.getText().toString().trim();
        switch (checkedId) {
            case R.id.radioButton1:
                if (!(!text.equals(position1) && textBefore.equals(position1))) {
                    editTextAdd.setText(position1);
                }
                break;
            case R.id.radioButton2:
                if (!(!text.equals(position2) && textBefore.equals(position2))) {
                    editTextAdd.setText(position2);
                }
                break;
            case R.id.radioButton3:
                if (!(!text.equals(position3) && textBefore.equals(position3))) {
                    editTextAdd.setText(position3);
                }
                break;
        }
    }


    @OnClick(value = {R.id.textViewAddCancel, R.id.textViewAddSubmit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewAddCancel:
                if (listener != null) {
                    listener.onCancelClick();
                }
                break;
            case R.id.textViewAddSubmit:
                String title = editTextAdd.getText().toString().trim();
                if (listener != null) {
                    listener.onSubmitClick(title);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textBefore = s.toString().trim();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().trim().length() > 0) {
            textViewAddSubmit.setEnabled(true);
        } else {
            textViewAddSubmit.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().trim();
        if ((textBefore.equals(position1) || textBefore.equals(position2) || textBefore.equals(position3))
                && (!text.equals(position1) && !text.equals(position2) && !text.equals(position3))) {
            viewAddRadioGroup.clearCheck();
        }
    }

    /**
     * editText获取焦点
     */
    public void editTextRequestFocus() {
        editTextAdd.requestFocus();
    }

    /**
     * 添加预置位弹框事件回调
     */
    public interface AddPositionCallBack {
        /**
         * 取消按钮点击
         */
        void onCancelClick();

        /**
         * 保存按钮点击
         */
        void onSubmitClick(String title);
    }
}
