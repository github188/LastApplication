package com.iermu.ui.fragment.setupdev;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.ui.activity.MainActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Random;

/**
 * Created by zhoushaopei on 15/10/12.
 */
public class CardItemFragment extends Fragment {

    @ViewInject(R.id.item_bg)       RelativeLayout mIemtBg;
    @ViewInject(R.id.devices_id)    TextView mDeviceId;
    @ViewInject(R.id.select_dev_img)ImageView mSelectDevBg;

    private final static String DEVICE_ID = "deviceid";
    private int[] imgIdArray;
    Random random = new Random();

    public static Fragment actionInstance(Context context, String deviceid) {
        CardItemFragment cardItemFragment = new CardItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_ID, deviceid);
        cardItemFragment.setArguments(bundle);
        return cardItemFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.card_item, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮

        imgIdArray = new int[]{R.drawable.find_dev_one, R.drawable.find_dev_two, R.drawable.find_dev_three};
        Bundle bundle = getArguments();
        String devId = bundle.getString(DEVICE_ID);
        mIemtBg.setBackgroundResource(imgIdArray[random.nextInt(3)]);
        mDeviceId.setText(getResources().getString(R.string.device_id_text)+" "+devId);
        return view;
    }

    public void setSelectDevBg() {
        if (mSelectDevBg != null) mSelectDevBg.setBackgroundResource(R.drawable.dev_select_click);
    }

    public void setSelectDevNormal() {
        if (mSelectDevBg != null) mSelectDevBg.setBackgroundResource(R.drawable.dev_select_normal);
    }

    public String getDeviceId() {
        Bundle bundle = getArguments();
        String devId = (bundle!=null) ? bundle.getString(DEVICE_ID) : "";
        return devId;
    }
}
