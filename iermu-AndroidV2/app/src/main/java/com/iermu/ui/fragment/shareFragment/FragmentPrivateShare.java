package com.iermu.ui.fragment.shareFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iermu.R;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/6/28.
 */
public class FragmentPrivateShare extends BaseFragment {
      @ViewInject(R.id.share_ontime_close)    LinearLayout  shareOntimeClose;
      @ViewInject(R.id.share_set_password)    LinearLayout  shareSetPassword;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_share,container,false);
        ViewHelper.inject(this,view);
        return view;
    }

    @OnClick(value = {R.id.share_ontime_close,R.id.share_set_password})
    public void onClick(View v){
          switch(v.getId()){
              case R.id.share_ontime_close:
                  Fragment fragmentOntime = FragmentOntimeClose.actionInstance(getActivity());
                  addToBackStack(getActivity(),fragmentOntime);
                  break;
              case R.id.share_set_password:
                  Fragment fragmentSetPassword = FragmentSetPassword.acitonIntance(getActivity());
                  addToBackStack(getActivity(),fragmentSetPassword);
                  break;
          }
    }
    public static Fragment actionInstance(FragmentActivity activity) {
        return new FragmentPrivateShare();
    }
    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }
}
