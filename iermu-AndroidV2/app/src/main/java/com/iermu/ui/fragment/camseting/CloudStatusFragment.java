package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnDevCloudListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.adapter.CloudServiceAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.List;

/**
 * Created by xjy on 15/7/3.
 */

public class CloudStatusFragment extends BaseFragment implements OnDevCloudListener {


    private CloudServiceAdapter adapter;

    @ViewInject(R.id.listview)  ListView mListView;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.cloud_server_state);
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new CloudStatusFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_cloud_services_state, container, false);
        ViewHelper.inject(this, view);
        initView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnDevCloudListener.class, this);
        business.syncCamCloud();
        refreshView();
        return view;
    }

    private void initView() {
        adapter = new CloudServiceAdapter(getActivity());
        mListView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnDevCloudListener.class, this);
        super.onDestroy();
    }

    private void refreshView() {
        List<CamLive> camCloud = ErmuBusiness.getCamSettingBusiness().getCamCloud();
       adapter.notifyData(camCloud);
    }

    @Override
    public void onDevCloud(Business bus) {
        int businessCode = bus.getCode();
        switch (businessCode) {
            case BusinessCode.SUCCESS:

                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
        }
        refreshView();
    }
}
