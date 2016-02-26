package com.iermu.ui.fragment.setupdev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iermu.R;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;

/**
 * Created by xjy on 15/9/24.
 */
public class ChangeWiFiFragment extends BaseFragment {
    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.change_wifi);
    }

    public static Fragment actionInstance(Context context) {
        return new ChangeWiFiFragment();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_wifi,container,false);
        ViewHelper.inject(this,view);
        return view;
    }
}
