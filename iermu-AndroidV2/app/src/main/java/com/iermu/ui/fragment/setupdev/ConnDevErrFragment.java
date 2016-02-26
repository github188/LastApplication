package com.iermu.ui.fragment.setupdev;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MainIermuFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/9/24.
 */
public class ConnDevErrFragment extends BaseFragment {
    @ViewInject(R.id.buttonLoad)
    Button mButtonLoad;
    @ViewInject(R.id.actionbar_back)
    ImageView mBack;
    @ViewInject(R.id.inform_fail)
    TextView mInformfail;

    private static final String ERROR_TXT = "errortxt";
    private String faileText;
    private String otherRegister;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_about);
    }

    public static Fragment actionInstance(FragmentActivity activity, String error) {
        Fragment fragment = new ConnDevErrFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ERROR_TXT, error);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
        mBack.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_condev_err, container, false);
        ViewHelper.inject(this, view);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕长亮
        ErmuBusiness.getSetupDevBusiness().quitSetupDev();

        faileText = getArguments().getString(ERROR_TXT);
        otherRegister = getActivity().getResources().getString(R.string.other_regiter);
        if (faileText.contains(otherRegister)) {
            mButtonLoad.setText(R.string.back_iermu);
        }
        mInformfail.setText(faileText);
        return view;
    }

    @OnClick(value = {R.id.buttonLoad})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonLoad:
                if (faileText.contains(otherRegister)) {
                    popBackAllStack();
                } else {
                    popBackStack(getActivity(), SearchGuideDevFragment.class);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            popBackAllStack();
        }
        return super.onKeyDown(keyCode, event);

    }
}
