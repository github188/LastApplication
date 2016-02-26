package com.iermu.ui.fragment;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.person.FeedBackFragment;
import com.iermu.ui.util.CheckVersionUtils;
import com.iermu.ui.view.PersonOfficalPhone;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;


/**
 * Created by xjy on 15/7/27.
 */
public class AboutFragment extends BaseFragment {

    @ViewInject(R.id.menu_feedback)
    RelativeLayout menuFeedBack;
    @ViewInject(R.id.custom_phone)
    RelativeLayout customPhone;
    @ViewInject(R.id.person_offical)
    RelativeLayout personOffical;
    @ViewInject(R.id.cam_version)
    TextView camName;
    @ViewInject(R.id.dev_update)
    RelativeLayout devUpdate;
    @ViewInject(R.id.version_text)
    TextView versionText;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.set_about);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_about, container, false);
        ViewHelper.inject(this, view);

        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        camName.setText(version);

        int newVersion = ErmuBusiness.getPreferenceBusiness().getAlertedUpdateVersion();
        int build = ErmuApplication.getVersionBuild();
        if(build >= newVersion) {
            versionText.setText(R.string.last_version);
            devUpdate.setEnabled(false);
        } else {
            versionText.setText("");
            devUpdate.setEnabled(true);
        }
        return view;
    }

    @OnClick(value = {R.id.menu_feedback, R.id.custom_phone, R.id.person_offical, R.id.cam_version, R.id.dev_update})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_feedback:
//                UfoSDK.init(getActivity().getApplication());
//                UfoSDK.startFeedbackActivity(v.getContext());
                Fragment fragmentFeed = FeedBackFragment.acitonInstance(getActivity());
                super.addToBackStack(getActivity(),fragmentFeed);
                break;
            case R.id.custom_phone:
                PersonOfficalPhone dialog = new PersonOfficalPhone(getActivity(), R.style.custom_dialog);
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
                window.setWindowAnimations(R.style.dialogAnim_style);  //添加动画
                window.setBackgroundDrawableResource(R.drawable.custom_bg);
                dialog.show();
                break;
            case R.id.person_offical:
                WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_IERMU);
                break;
            case R.id.cam_name:
                break;
            case R.id.dev_update:
                boolean enabled = devUpdate.isEnabled();
                if (enabled) devUpdate.setEnabled(false);
                CheckVersionUtils.toastUpdateVersion(getActivity(), devUpdate);
                break;
        }
    }
}
