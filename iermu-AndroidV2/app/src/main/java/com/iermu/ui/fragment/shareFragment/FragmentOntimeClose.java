package com.iermu.ui.fragment.shareFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iermu.R;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;

/**
 * Created by xjy on 15/6/28.
 */
public class FragmentOntimeClose extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ontime_destroy,container,false);
        ViewHelper.inject(this,view);
        return view;
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new FragmentOntimeClose();
    }
    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }
}
