package com.iermu.ui.fragment.setupdev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.iermu.R;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/9/24.
 */
public class  FindDevNumFragment extends BaseFragment {
    @ViewInject(R.id.buttonOk)
    Button mButtonOk;
    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.help_me_find_dev);
    }
    public static Fragment actionInstance(Context context) {
        return new FindDevNumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_devnum,container,false);
        ViewHelper.inject(this, view);
        return view;
    }
    @OnClick(value = {R.id.buttonOk})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.buttonOk :
                popBackStack();
                break;
        }
    }
}
