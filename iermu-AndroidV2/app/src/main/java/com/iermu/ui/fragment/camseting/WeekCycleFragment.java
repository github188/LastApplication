package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.CronType;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjy on 15/6/25.
 */
public class WeekCycleFragment extends BaseFragment {

    @ViewInject(R.id.set_work_monday)   RelativeLayout setMonday;
    @ViewInject(R.id.set_work_tuesday)  RelativeLayout setTuesday;
    @ViewInject(R.id.set_work_wednesday)RelativeLayout setWednesday;
    @ViewInject(R.id.set_work_thursday) RelativeLayout setThursday;
    @ViewInject(R.id.set_work_friday)   RelativeLayout setFriday;
    @ViewInject(R.id.set_work_saturday) RelativeLayout setSaturday;
    @ViewInject(R.id.set_work_sunday)   RelativeLayout setSunday;

    private List<View> items = new ArrayList<View>();
    private CronRepeat cronRepeat;
    private String deviceId;
    private CronType cronType;
    private static final String INTENT_DEVICEID = "deviceid";
    private static final String INTENT_CRONTYPE = "cron_type";
    private static final String INTENT_REPEAT   = "cron_repeat";
    private static final String INTENT_REQCODE = "requestCode";
    private OnWeekCycleListener listener;
    private boolean isSave = false;


    public static Fragment actionInstance(FragmentActivity activity, String deviceId, CronType cronType, CronRepeat repeat, int requestCode) {
        Fragment fragment = new WeekCycleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        bundle.putSerializable(INTENT_CRONTYPE, cronType);
        bundle.putInt(INTENT_REQCODE, requestCode);
        bundle.putSerializable(INTENT_REPEAT, repeat);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.work_cycle))
        .setCommonBackClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
                if (listener != null && cronRepeat != null) {
                    listener.weekCycle(cronRepeat, isSave);
                }
            }
        });
    }

    public void setWeekCycleListener(OnWeekCycleListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_cycle, container, false);
        ViewHelper.inject(this, view);
        deviceId = getArguments().getString(INTENT_DEVICEID);
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        cronRepeat = (CronRepeat) getArguments().getSerializable(INTENT_REPEAT);
        initView();
        return view;
    }

    private void initView() {
        items.add(setMonday);
        items.add(setTuesday);
        items.add(setWednesday);
        items.add(setThursday);
        items.add(setFriday);
        items.add(setSaturday);
        items.add(setSunday);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOnClickListener(itemslistener);
        }
        refreshView();
    }

    View.OnClickListener itemslistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            View imagView = view.findViewById(R.id.workcycle_selected_img);
            boolean visible = !(imagView.getVisibility() == View.VISIBLE);
            if (!visible && cronRepeat.isLastOne()) return;
            imagView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            switch (view.getId()) {
                case R.id.set_work_monday:
                    isSave = true;
                    cronRepeat.setMonday(visible);
                    break;
                case R.id.set_work_tuesday:
                    isSave = true;
                    cronRepeat.setTuesday(visible);
                    break;
                case R.id.set_work_wednesday:
                    isSave = true;
                    cronRepeat.setWednesday(visible);
                    break;
                case R.id.set_work_thursday:
                    isSave = true;
                    cronRepeat.setThursday(visible);
                    break;
                case R.id.set_work_friday:
                    isSave = true;
                    cronRepeat.setFriday(visible);
                    break;
                case R.id.set_work_saturday:
                    isSave = true;
                    cronRepeat.setSaturday(visible);
                    break;
                case R.id.set_work_sunday:
                    isSave = true;
                    cronRepeat.setSunday(visible);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(listener != null && cronRepeat != null) {
                listener.weekCycle(cronRepeat, isSave);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshView() {
        if (cronRepeat == null) return;
        setImageViewVisible(setMonday, cronRepeat.isMonday());
        setImageViewVisible(setTuesday, cronRepeat.isTuesday());
        setImageViewVisible(setWednesday, cronRepeat.isWednesday());
        setImageViewVisible(setThursday, cronRepeat.isThursday());
        setImageViewVisible(setFriday, cronRepeat.isFriday());
        setImageViewVisible(setSaturday, cronRepeat.isSaturday());
        setImageViewVisible(setSunday, cronRepeat.isSunday());
    }

    //显示|隐藏ImageView
    private void setImageViewVisible(View view, boolean visible) {
        if (view == null) return;
        View imagView = view.findViewById(R.id.workcycle_selected_img);
        imagView.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public interface OnWeekCycleListener{
        void weekCycle(CronRepeat cronRepeat, boolean isSave);
    }

}
