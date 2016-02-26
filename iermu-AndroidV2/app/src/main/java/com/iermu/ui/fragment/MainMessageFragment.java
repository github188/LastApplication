package com.iermu.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMessageCamBusiness;
import com.iermu.client.listener.OnGetMessageMineCamListListener;
import com.iermu.client.listener.OnMessageCamChangedListener;
import com.iermu.client.listener.OnMimeCamChangedListener;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.activity.MainActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.adapter.MineMessageAdapter;
import com.iermu.ui.fragment.message.AlarmMessageFragment;
import com.iermu.ui.fragment.message.AlarmSettingFragment;
import com.iermu.ui.fragment.setupdev.ConnAndBuyFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.LoadingView;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wcy on 15/6/18.
 */
public class MainMessageFragment extends BaseFragment implements OnMessageCamChangedListener,
        AdapterView.OnItemClickListener, OnMimeCamChangedListener, OnGetMessageMineCamListListener {

    @ViewInject(R.id.listview)
    PullableListView mListView;
    @ViewInject(R.id.lvPullLayout)
    PullToRefreshLayout mPullableLayout;
    @ViewInject(R.id.load_view)
    LoadingView loadView;
    @ViewInject(R.id.empty_view_img)
    RelativeLayout noCamLiveView;
    @ViewInject(R.id.empty_alarm)
    View empty_alarm;
    @ViewInject(R.id.empty_alarm_all)
    View empty_alarm_all;
    @ViewInject(R.id.live_setting)
    RelativeLayout live_setting;

    MineMessageAdapter mAdapter;
    List<AlarmDeviceItem> deviceItems;
    private boolean isUpdated;
    private String deviceId;

    private IMessageCamBusiness business;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_message);
        ViewHelper.inject(getActivity());
    }

    @Override
    public void onActionBarCreated(View view) {
        ViewHelper.inject(this, view);
    }

    public static Fragment actionInstance(MainActivity mainActivity) {
        return new MainMessageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainmessage, container, false);
        ViewHelper.inject(this, view);

        mAdapter = new MineMessageAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setPullDown(true);
        mListView.setPullUp(true);
        mListView.setOnItemClickListener(this);

        business = ErmuBusiness.getMessageCamBusiness();
        business.registerListener(OnGetMessageMineCamListListener.class, this);
        ErmuBusiness.getMimeCamBusiness().registerListener(OnMimeCamChangedListener.class, this);

        updateData("");
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            updateData("");
        }
    }

    /**
     * 更新数据和提示页面
     */
    public void updateData(String deviceId) {
        this.deviceId = deviceId;
        int camLiveCount = business.getMineCamLiveCount();
        if (camLiveCount == 0) {
            if (live_setting != null) live_setting.setVisibility(View.GONE);
            noCamLiveView.setVisibility(View.VISIBLE);
            empty_alarm.setVisibility(View.GONE);
            empty_alarm_all.setVisibility(View.GONE);
        } else {
            if (live_setting != null) live_setting.setVisibility(View.VISIBLE);
            // 同步黄蓝标志
            business.getMineCamList();//获取设备的列表
        }
    }

    @OnClick(value = {R.id.live_setting, R.id.add_cam_btn, R.id.buttonOpenAlarm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_setting:
                Fragment fragment = AlarmSettingFragment.actionInstance(getActivity());
                BaseFragment.addToBackStack(getActivity(), fragment);
                break;
            case R.id.add_cam_btn:
                String language = LanguageUtil.getLanguage();
                String baiduUid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
                if (language.equals("zh")) {
                    if (!TextUtils.isEmpty(baiduUid)) {
                        Fragment fragment1 = ConnAndBuyFragment.actionInstance(getActivity());
                        addToBackStack(fragment1);
                    } else {
                        WebActivity.actionStartWeb(getActivity(), WebActivity.BIND_BAI_DU_URL);
                    }
                } else {
                    Fragment fragment1 = ConnAndBuyFragment.actionInstance(getActivity());
                    addToBackStack(fragment1);
                }
                break;
            case R.id.buttonOpenAlarm:
                Fragment fragment2 = AlarmSettingFragment.actionInstance(getActivity());
                BaseFragment.addToBackStack(getActivity(), fragment2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessageCamChanged() {
        loadView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlarmDeviceItem item = mAdapter.getItem(position);
        item.setHasNew(false);
        mAdapter.notifyDataSetChanged();
        Fragment fragment = AlarmMessageFragment.actionInstance(getActivity(), item);
        addToBackStack(fragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ErmuBusiness.getMimeCamBusiness().unRegisterListener(OnMimeCamChangedListener.class, this);
        business.unRegisterListener(OnGetMessageMineCamListListener.class, this);
    }

    @Override
    public void onMimeCamChanged() {
        if (!isUpdated) {
            updateData("");
            isUpdated = true;
        }
    }

    @Override
    public void onGetMineCamList(List<AlarmDeviceItem> alarmDeviceItems) {
        List<AlarmDeviceItem> deviceItemsNew = new ArrayList<AlarmDeviceItem>();
        for (AlarmDeviceItem deviceItem : alarmDeviceItems) {
            if (deviceItem.getCount() > 0) {
                deviceItemsNew.add(deviceItem);
            }
        }
        int camLiveCount = business.getMineCamLiveCount();
        for (int i = 0; i < deviceItemsNew.size(); i++) {
            AlarmDeviceItem deviceItem = deviceItemsNew.get(i);
            if (deviceItems != null) {
                for (int j = 0; j < deviceItems.size(); j++) {
                    AlarmDeviceItem deviceItem1 = deviceItems.get(j);
                    if (deviceItem1.getDeviceId().equals(deviceItem.getDeviceId())) {
                        deviceItem.setHasNew(deviceItem1.isHasNew());
                        break;
                    }
                }
            }
            if (deviceItem.getDeviceId().equals(deviceId)) {
                deviceItem.setHasNew(true);
            }
        }
        deviceItems = deviceItemsNew;
        mAdapter.setDeviceItems(deviceItems);
        mAdapter.notifyDataSetChanged();
        if (deviceItems.size() == 0) {
            if (business.getOpendAlarmCamCount() == camLiveCount) {
                empty_alarm_all.setVisibility(View.VISIBLE);
                empty_alarm.setVisibility(View.GONE);
                noCamLiveView.setVisibility(View.GONE);
            } else {
                empty_alarm.setVisibility(View.VISIBLE);
                empty_alarm_all.setVisibility(View.GONE);
                noCamLiveView.setVisibility(View.GONE);
            }
        } else {
            noCamLiveView.setVisibility(View.GONE);
            empty_alarm.setVisibility(View.GONE);
            empty_alarm_all.setVisibility(View.GONE);
        }
    }
}
