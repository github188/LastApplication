package com.iermu.ui.fragment.message;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMessageCamBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.listener.OnGetMessageMineCamListListener;
import com.iermu.client.listener.OnSetAlarmNoticeListener;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.CronType;
import com.iermu.ui.adapter.AlarmSettingAdapter;
import com.iermu.ui.adapter.AlarmSettingAdapter.OnButtonClickListener;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.camseting.AlarmTimeFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.CommonDialog;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.List;

/**
 * Created by zhoushaopei on 15/7/10.
 */
public class AlarmSettingFragment extends BaseFragment implements OnButtonClickListener,
        OnSetAlarmNoticeListener, OnGetMessageMineCamListListener {
    @ViewInject(R.id.listView)
    PullableListView listView;

    private AlarmSettingAdapter adapter;
    private IMessageCamBusiness business;
    private ICamSettingBusiness businessSetting;
    CommonCommitDialog cusDialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_common);
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        AlarmSettingFragment settingFragment = new AlarmSettingFragment();
        return settingFragment;
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        setCommonActionBar(getString(R.string.alarm_tile));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.alarm_setting, null);
        ViewHelper.inject(this, view);
        business = ErmuBusiness.getMessageCamBusiness();
        adapter = new AlarmSettingAdapter(getActivity());
        adapter.setOnButtonClickListener(this);
        listView.setAdapter(adapter);
        listView.setPullDown(true);
        listView.setPullUp(true);
        businessSetting = ErmuBusiness.getCamSettingBusiness();
        businessSetting.registerListener(OnSetAlarmNoticeListener.class, this);
        business.registerListener(OnGetMessageMineCamListListener.class, this);

        updateData();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateData();
        }
    }

    private void updateData() {
        business.getMineCamList();
    }

    @OnClick(value = {R.id.actionbar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_back:
                popBackStack();
                break;
        }
    }

    @Override
    public void onCloseClick(int position) {
        final AlarmDeviceItem deviceItem = adapter.getItem(position);
        // 关闭报警
        final boolean conn = Util.isNetworkConn(getActivity());
        final CommonDialog commonDialog = new CommonDialog(getActivity());
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setTitle(getString(R.string.alarm_close_sure))
                .setContent(getString(R.string.alarm_close_no_message))
                .setCancelText(getString(R.string.cancle_txt))
                .setOkText(getString(R.string.sure))
                .setOkListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (conn) {
                            cusDialog = new CommonCommitDialog(getActivity());
                            cusDialog.show();
                            cusDialog.setStatusText(getString(R.string.push));
//                            businessSetting.setAlarmNotice(deviceItem.getDeviceId(), false);
                            businessSetting.stopAlarmPush(deviceItem.getDeviceId());
                        } else {
                            Toast toast = Toast.makeText(getActivity(), getString(R.string.play_no_network), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        commonDialog.dismiss();
                    }
                })
                .setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commonDialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onResetClick(int position) {
        AlarmDeviceItem deviceItem = adapter.getItem(position);
        Fragment fragment = AlarmTimeFragment.actionInstance(getActivity(), deviceItem.getDeviceId(), CronType.ALARM);
        addToBackStack(getActivity(), fragment);
    }

    @Override
    public void onGotoOpenClick(int position) {
        AlarmDeviceItem deviceItem = adapter.getItem(position);
        IMimeCamBusiness business1 = ErmuBusiness.getMimeCamBusiness();
        String deviceid = deviceItem.getDeviceId();
        CamLive camLive = business1.getCamLive(deviceid);
        if (camLive.getConnectType() == ConnectType.LINYANG) {
            toast(getString(R.string.develop_now));
        } else {
            if (camLive.isPowerOff()) {
                ErmuApplication.toast(getString(R.string.setting_cam_shutdown));
            } else if (camLive.isOffline()) {
                ErmuApplication.toast(getString(R.string.setting_cam_offline));
            } else {
                Fragment fragment = AlarmTimeFragment.actionInstance(getActivity(), deviceItem.getDeviceId(), CronType.ALARM);
                addToBackStack(getActivity(), fragment);
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        business.unRegisterListener(OnSetAlarmNoticeListener.class, this);
        business.unRegisterListener(OnGetMessageMineCamListListener.class, this);
    }

    @Override
    public void onSetAlarmNotice(Business business, boolean isNotice) {
        if (cusDialog != null) cusDialog.cancel();
        switch (business.getCode()) {
            case BusinessCode.SUCCESS:
                updateData();
                break;
            default:
                ErmuApplication.toast(getString(R.string.server_or_cam_error));
                break;
        }
    }

    @Override
    public void onGetMineCamList(List<AlarmDeviceItem> alarmDeviceItems) {
        adapter.setDeviceItems(alarmDeviceItems);
        adapter.notifyDataSetChanged();
    }

    private void toast(String str) {
        Toast toast = Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
