package com.iermu.ui.fragment.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMessageCamBusiness;
import com.iermu.client.business.dao.AlarmImageDataWrapper;
import com.iermu.client.listener.OnMessageChangeListener;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.AlarmMessageAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;
import com.viewinject.annotation.event.OnScroll;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by zhoushaopei on 15/7/7.
 */
public class AlarmMessageFragment extends BaseFragment implements AlarmMessageAdapter.SelectDataChangeListener,
        OnMessageChangeListener, AbsListView.OnScrollListener {
    public static final String KEY_DEVICE_ITEM = "keyDeviceItem";

    @ViewInject(R.id.delete_layout)
    LinearLayout mDelLayout;
    @ViewInject(R.id.del_btn)
    ImageButton mDelButton;
    @ViewInject(R.id.undel_btn)
    ImageButton mUnDelButton;
    @ViewInject(R.id.title)
    TextView title;
    @ViewInject(R.id.listView)
    StickyListHeadersListView listView;
    @ViewInject(R.id.textViewSelectNum)
    TextView mAlarmNum;
    @ViewInject(R.id.img_num)
    TextView mImgtNum;
    @ViewInject(R.id.select_all)
    TextView textViewSelectAll;
    @ViewInject(R.id.empty_alarm_photo)
    LinearLayout emptyAlarmPhoto;

    private AlarmMessageAdapter adapter;

    private List<AlarmImageData> imageDatas = new ArrayList<AlarmImageData>();
    private List<List<AlarmImageData>> imageDatasArr = new ArrayList<List<AlarmImageData>>();
    private AlarmDeviceItem deviceItem;
    IMessageCamBusiness business;
    private String deviceId;
    private int totalNum;
    private int lastImageSize; // 记录上一次加载的图片个数

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int deleteIndex = intent.getIntExtra("deleteIndex", -1);
            Logger.d("deleteIndex:" + deleteIndex);
            if (deleteIndex >= 0) {
                imageDatas.remove(deleteIndex);
                refreshAdapter();
                totalNum--;
                String alarms = (totalNum > 1) ? getString(R.string.alarm_nums) : getString(R.string.alarm_num);
                mImgtNum.setText(String.format(alarms, totalNum));
            }
        }
    };

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_alarm_message);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        deviceItem = (AlarmDeviceItem) getArguments().getSerializable(KEY_DEVICE_ITEM);
        title.setText((deviceItem != null) ? deviceItem.getDeviceName() : "");
    }

    public static Fragment actionInstance(Context context, AlarmDeviceItem deviceItem) {
        AlarmMessageFragment messageFragment = new AlarmMessageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DEVICE_ITEM, deviceItem);
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_alert_message, null);
        ViewHelper.inject(this, view);

        String format = String.format(getResources().getString(R.string.select_alarm_pic), 0);
        selectNumPics(0, format);

        deviceId = deviceItem.getDeviceId();
        business = ErmuBusiness.getMessageCamBusiness();
        business.registerListener(OnMessageChangeListener.class, this);
        deviceItem = (AlarmDeviceItem) getArguments().getSerializable(KEY_DEVICE_ITEM);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setEmptyView(emptyAlarmPhoto);
            }
        }, 100);

        business.syncNewImageDatas(deviceItem.getDeviceId());

        adapter = new AlarmMessageAdapter(getActivity());
        adapter.setDatas(imageDatas, imageDatasArr);
        adapter.setListener(this);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(this);

        totalNum = (int) AlarmImageDataWrapper.getCountByDeviceId(deviceId);

        getActivity().registerReceiver(mBroadcastReceiver, new IntentFilter(AlarmBigImageFragment.INTENT_DELETE_IMAGEDATA));

        return view;
    }

    @OnClick(value = {R.id.del_btn, R.id.select_all, R.id.select_delete, R.id.undel_btn, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.del_btn:
                if (adapter.getCount() > 0) {
                    mDelLayout.setVisibility(View.VISIBLE);
                    mUnDelButton.setVisibility(View.VISIBLE);
                    mDelButton.setVisibility(View.GONE);
                    adapter.setIsDeleting(true);
                }
                break;
            case R.id.undel_btn:
                setUnDelete();
                break;
            case R.id.select_all:
                adapter.selectOrUnSelectAll();
                break;
            case R.id.select_delete:
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog
                        .setTitle(getString(R.string.alarm_delete_sure))
                        .setContent(getString(R.string.alarm_delete))
                        .setCancelText(getString(R.string.cancle_txt))
                        .setOkText(getString(R.string.sure))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Long> deleteList = adapter.getDeleteList();
                                totalNum -= deleteList.size();
                                business.deleteImageDatas(deleteList);
                                setUnDelete();
                                if (imageDatas.size() < 30) {
                                    business.syncNewImageDatas(deviceId);
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
                break;
            case R.id.back:
                popBackStack();
                break;
        }
    }

    @Override
    public void onSelectDataChange(List<Long> selectList) {
        int size = selectList.size();
        String selectPics = size > 1 ? getResources().getString(R.string.select_alarm_pics) : getResources().getString(R.string.select_alarm_pic);
        String format = String.format(selectPics, size);
        selectNumPics(size, format);
        if (selectList.size() > 0 && selectList.size() == imageDatas.size()) {
            textViewSelectAll.setSelected(true);
        } else {
            textViewSelectAll.setSelected(false);
        }
    }

    @Override
    public void onImageClick(int position) {
        Fragment fragment = AlarmBigImageFragment.actionInstance(deviceItem.getDeviceName(), imageDatas, position);
        if (fragment != null) {
            addToBackStack(fragment);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        business.unRegisterListener(OnMessageChangeListener.class, this);
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void selectNumPics(int size, String numPics) {
        int start = numPics.indexOf(String.valueOf(size));
        String numLength = String.valueOf(size);
        int sizeLength = Integer.valueOf(numLength.length());
        SpannableString spannableString = new SpannableString(numPics);
        spannableString.setSpan(new AbsoluteSizeSpan(18, true), start, start + sizeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff00acef")), start, start + sizeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAlarmNum.setText(spannableString);
    }

    public void setUnDelete() {
        mDelLayout.setVisibility(View.GONE);
        mUnDelButton.setVisibility(View.GONE);
        mDelButton.setVisibility(View.VISIBLE);
        adapter.setIsDeleting(false);
        adapter.clearDeleteList();
    }

    @Override
    public void onMessageChange() {
        imageDatas = business.getImageDatas();
        refreshAdapter();
        if (imageDatas.size() >= totalNum) {
            totalNum = imageDatas.size();
            mImgtNum.setVisibility(View.VISIBLE);
            String alarms = (totalNum > 1) ? getString(R.string.alarm_nums) : getString(R.string.alarm_num);
            mImgtNum.setText(String.format(alarms, imageDatas.size()));
        } else {
            mImgtNum.setVisibility(View.GONE);
        }

        if (adapter.getDeleteList().size() != adapter.getCount()) {
            textViewSelectAll.setSelected(false);
        }
    }

    /**
     * 刷新adapter
     */
    private void refreshAdapter() {
        imageDatasArr.clear();
        if (imageDatas != null && imageDatas.size() > 0) {
            String date = "";
            for (int i = 0; i < imageDatas.size(); i++) {
                AlarmImageData imageData = imageDatas.get(i);
                String dateCurrent = imageData.getAlarmtime().substring(0, 10);
                if (!dateCurrent.equals(date)) {
                    date = dateCurrent;
                    imageDatasArr.add(new ArrayList<AlarmImageData>());
                }
                imageDatasArr.get(imageDatasArr.size() - 1).add(imageData);
            }
        }

        adapter.setDatas(imageDatas, imageDatasArr);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ListView listViewSub = (ListView) this.listView.getChildAt(0);
        if (listViewSub != null
                && listViewSub.getChildCount() > 0
                && listViewSub.getLastVisiblePosition() == imageDatasArr.size() - 1
                && listViewSub.getChildAt(listViewSub.getChildCount() - 1) != null
                && listViewSub.getChildAt(listViewSub.getChildCount() - 1).getBottom() <= listViewSub.getMeasuredHeight()) {
            if (imageDatas.size() < totalNum && lastImageSize != imageDatas.size()) {
                lastImageSize = imageDatas.size();
                Logger.d("load more...  currentNum:" + imageDatas.size() + "_totalNum:" + totalNum);
                business.syncOldImageDatas(deviceId);
            }
        }
    }
}
