package com.iermu.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.IUserCenterBusiness;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.config.PathConfig;
import com.iermu.client.listener.OnMyCamCountListener;
import com.iermu.client.listener.OnScreenerPictureListener;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.LoginActivity;
import com.iermu.ui.activity.MainActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.person.CompletePersonalFragment;
import com.iermu.ui.fragment.person.FeedBackFragment;
import com.iermu.ui.fragment.personalinfo.PhotoFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonDialog;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by wcy on 15/6/18.
 */
public class MainPersonalFragment extends BaseFragment implements OnUserInfoListener ,OnMyCamCountListener, OnScreenerPictureListener {

    @ViewInject(R.id.tv_user)       TextView userName;
    @ViewInject(R.id.img_photo)     CircleImageView photo;
    @ViewInject(R.id.all_count)     TextView mAllCount;
    @ViewInject(R.id.film_edit_num) TextView mFilmEditNum;
    @ViewInject(R.id.have_num_cam)  TextView tvCam;
    @ViewInject(R.id.cam_num)       TextView mCamNum;
    @ViewInject(R.id.mail_auth)     TextView mMailAuth;

    private IMimeCamBusiness business;
    private String emailTxt;
    private static MainActivity activity;

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        hideActionBar();
    }

    public static Fragment actionInstance(FragmentActivity acx) {
        activity = (MainActivity)acx;
        return new MainPersonalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_current, container, false);
        ViewHelper.inject(this, view);
        ErmuBusiness.getMimeCamBusiness().registerListener(OnMyCamCountListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().addUserInfoListener(this);
        ErmuBusiness.getAccountAuthBusiness().getUserInfo();
        IUserCenterBusiness centerBusiness = ErmuBusiness.getUserCenterBusiness();
        centerBusiness.registerListener(OnScreenerPictureListener.class, this);
        centerBusiness.getUserClip();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Account account = AccountWrapper.queryAccount();
                if (account != null && !TextUtils.isEmpty(account.getUname())) {
                    setUserInfo(account.getUname(), account.getAvatar(), account.getEmail(), account.getEmailstatus());
                }
            }
        }, 1000);
        ErmuBusiness.getMimeCamBusiness().getMineCamCount();
        initPhoto();
        refreshView();
        return view;
    }

    private void initPhoto() {
        File file = new File(PathConfig.CACHE_SHARE);
        File[] files = file.listFiles();
        List<ScreenClip> screenClip = ErmuBusiness.getUserCenterBusiness().getScreenClip(PhotoType.FILM_EDIT);
        mAllCount.setText(files != null ? files.length+"" : 0+"");
        mFilmEditNum.setText(screenClip.size()>0 ? screenClip.size()+"" : 0+"");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initPhoto();
            refreshView();
            ErmuBusiness.getUserCenterBusiness().getUserClip();
            Account account = AccountWrapper.queryAccount();
            if (account != null) {
                Logger.i("account:"+account.getUname()+"---"+account.getEmail());
                setUserInfo(account.getUname(), account.getAvatar(), account.getEmail(), account.getEmailstatus());
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ErmuBusiness.getMimeCamBusiness().unRegisterListener(OnMyCamCountListener.class, this);
        ErmuBusiness.getAccountAuthBusiness().removeUserInfoListener();
        ErmuBusiness.getUserCenterBusiness().unRegisterListener(OnScreenerPictureListener.class, this);
    }

    @OnClick(value = {R.id.menu_iermu_buy,R.id.agreement_request, R.id.menu_cvr_buy, R.id.menu_set, R.id.menu_feedback, R.id.btn_logout, R.id.menu_about
            , R.id.photo_view, R.id.film_edit, R.id.cam_view, R.id.collection_photo, R.id.my_account,R.id.img_photo})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_iermu_buy:
                WebActivity.actionStartWeb(getActivity(), WebActivity.BUG_CAM);
                break;
            case R.id.menu_cvr_buy:
                WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_CVRBUY);
                break;
            case R.id.menu_feedback:
                WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_QUESTION);
                break;
            case R.id.menu_about:
                Fragment aboutFragment = AboutFragment.actionInstance(getActivity());
                addToBackStack(getActivity(), aboutFragment);
                break;
            case R.id.my_account:
                //TODO 暂时注释掉登录注册分支代码
                //if (TextUtils.isEmpty(emailTxt)) {
                    String name = userName.getText().toString();
                    Fragment fragment = CompletePersonalFragment.actionInstance(getActivity(), name);
                    super.addToBackStack(getActivity(), fragment, true);
                //} else {
                //    Fragment fragment = UserInfoFragment.actionInstance(getActivity());
                //    super.addToBackStack(getActivity(), fragment);
                //}
                break;
            //case R.id.img_photo:
            //    Fragment fragmentData = UserInfoFragment.actionInstance(getActivity());
            //    super.addToBackStack(getActivity(), fragmentData);
            //    break;
            case R.id.agreement_request:
                Fragment fragmentFeed = FeedBackFragment.acitonInstance(getActivity());
                super.addToBackStack(getActivity(),fragmentFeed);
                break;
            case R.id.btn_logout:
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog
                        .setTitle(getResources().getString(R.string.logout_account))
                        .setContent(getString(R.string.tip_iermu_logout))
                        .setCancelText(getResources().getString(R.string.cancle_txt))
                        .setOkText(getResources().getString(R.string.sure))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 清除WebView页面缓存Cookie
                                CookieSyncManager.createInstance(getActivity());
                                CookieManager.getInstance().removeAllCookie();
                                CookieSyncManager.getInstance().sync();
                                LoginActivity.actionStartLogin(getActivity());
                                ErmuBusiness.getAccountAuthBusiness().logout();
                                commonDialog.dismiss();
                                getActivity().finish();
                            }
                        })
                        .setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                commonDialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.photo_view:
                Fragment photoFragment = PhotoFragment.actionInstance(getActivity(), PhotoType.PHOTO);
                addToBackStack(photoFragment);
                break;
            case R.id.film_edit:
                if(Util.isNetworkConn(getActivity())) {
                    Fragment filmFragment = PhotoFragment.actionInstance(getActivity(), PhotoType.FILM_EDIT);
                    addToBackStack(filmFragment);
                } else {
                    ErmuApplication.toast(getString(R.string.network_error_wait));
                }
                break;
            case R.id.collection_photo:
                if(Util.isNetworkConn(getActivity())) {
                    Fragment allFragment = PhotoFragment.actionInstance(getActivity(), PhotoType.ALL);
                    addToBackStack(allFragment);
                } else {
                    ErmuApplication.toast(getString(R.string.network_error_wait));
                }
                break;
            case R.id.cam_view:
                if (activity != null) activity.switchMainFragment();
                break;
        }
    }

    private void refreshView() {
        if (getActivity() != null) {
            int count = ErmuBusiness.getPreferenceBusiness().getMyCamCount();
            String numCam = getResources().getString(R.string.have_num_cam);
            tvCam.setText(String.format(numCam, count));
            mCamNum.setText(String.valueOf(count));
        }
    }

    @Override
    public void onUserInfo(UserInfo info) {
        if (info == null) return;
        int emailStatus = info.getEmailStatus();
        String email = info.getEmail();
        setUserInfo(info.getUserName(), info.getAvatar(), email, emailStatus);
    }

    private void setUserInfo(String userName, String avatar, String email, int emailState) {
        avatar = TextUtils.isEmpty(avatar) ? "default" : avatar;
        Picasso.with(getActivity())
                .load(avatar)
                .resize(photo.getWidth(), photo.getHeight())
                .centerCrop()
                .placeholder(R.drawable.avator_img)
                .into(photo);
        this.emailTxt = email;
        this.userName.setText(userName);
        if (TextUtils.isEmpty(emailTxt)) {
            mMailAuth.setVisibility(View.GONE);
        } else {
            mMailAuth.setVisibility(View.VISIBLE);
            mMailAuth.setVisibility(emailState == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private String isDigital(String name) {
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher((CharSequence) name);
        boolean result = matcher.matches();
        if (result) {
            String start = name.substring(0, 3);
            String end = name.substring(name.length() - 3, name.length());
            return start + "*****" + end;
        }
        return name;
    }

    @Override
    public void onMyCamCount(int count) {
        refreshView();
    }

    @Override
    public void onScreenPicture(List<ScreenClip> pictures, int type) {
        if (type == PhotoType.FILM_EDIT) {
            if (pictures == null) {
                pictures = new ArrayList<ScreenClip>();
            }
            mFilmEditNum.setText(pictures.size()+"");
        }
    }
}
