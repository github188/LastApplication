package com.iermu.ui.fragment.person;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.config.PathConfig;
import com.iermu.client.listener.OnGetThirdConnectListener;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.Business;
import com.iermu.client.model.UserInfo;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.activity.LoginiActivity;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommitDialog;
import com.iermu.ui.view.DimenUtils;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.sweetalert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp.zhy.http.okhttp.callback.ResultCallback;


/**
 * Created by xjy on 15/11/26.
 */
public class UserInfoFragment extends BaseFragment implements OnUserInfoListener, ChangeUserNameFragment.OnUserNameChanged, OnGetThirdConnectListener {

    @ViewInject(R.id.head_img)
    CircleImageView headImg;
    @ViewInject(R.id.nick_name)
    TextView mNickName;
    @ViewInject(R.id.mail_txt)
    TextView mMailTxt;
    @ViewInject(R.id.bind_bd)
    TextView mBindBd;
    @ViewInject(R.id.baidu_account_lay)
    RelativeLayout mBaiduLay;
    @ViewInject(R.id.choose_pictures)
    LinearLayout mChoosePicture;
    @ViewInject(R.id.choose_img)
    LinearLayout mChooseImg;
    @ViewInject(R.id.cancel)
    TextView mCancel;
    @ViewInject(R.id.dialog_bg)
    View mDialogBg;
    @ViewInject(R.id.person_nickname)
    RelativeLayout mPersonNick;
    @ViewInject(R.id.person_head)
    RelativeLayout mPersonHead;
    @ViewInject(R.id.person_login_password)
    RelativeLayout mPersonLoginPas;
    @ViewInject(R.id.actionbar_title)
    TextView mActionbarTitle;
    @ViewInject(R.id.actionbar_back)
    ImageView mActionBack;
    @ViewInject(R.id.baidu_back_img)
    ImageView mBaiduImg;

    static final int REQUESTCODE_PICK = 0; //从相册选取图片
    static final int REQUESTCODE_CUTTING = 2;// 裁剪图片
    private long currentTime;
    private CommitDialog dialog;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.person_edit_data);
    }

    @Override
    public void onActionBarCreated(View view) {
        super.onActionBarCreated(view);
        ViewHelper.inject(this, view);
    }

    public static Fragment actionInstance(FragmentActivity Activity) {
        return new UserInfoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_data, container, false);
        ViewHelper.inject(this, view);

        Account account = AccountWrapper.queryAccount();
        if (account != null) {
            String avatar = account.getAvatar();
            String userName = account.getUname();
            String email = account.getEmail();
            int emailStatus = account.getEmailstatus();
            String bdUid = account.getBaiduUid();
            String bdAccount = account.getBaiduUName();
            refreshView(avatar, userName, email, emailStatus, bdUid);
            updateBdState(bdUid, bdAccount);
        }

        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.registerListener(OnGetThirdConnectListener.class, this);
        business.getUserInfo();
        business.getThirdConnect();
        business.addUserInfoListener(this);
        return view;
    }

    @OnClick(value = {R.id.choose_img, R.id.cancel, R.id.person_head, R.id.person_nickname, R.id.baidu_account_lay, R.id.person_login_password, R.id.peroson_login_email, R.id.btn_logout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_head:
                mPersonNick.setEnabled(false);
                mPersonHead.setEnabled(false);
                mPersonLoginPas.setEnabled(false);
                mChoosePicture.setVisibility(View.VISIBLE);
                mDialogBg.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(400).playOn(mChoosePicture);
//                chooseHead = new PersonChooseImgDialog(getActivity(), R.style.load_dialog);
//                Window window = chooseHead.getWindow();
//                window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
//                window.setWindowAnimations(R.style.dialogAnim_style);  //添加动画
//                window.setBackgroundDrawableResource(R.drawable.custom_bg);
//                chooseHead.show();
//                chooseHead.setOnChooseListener(this);
                break;
            case R.id.person_nickname:
                String niceName = mNickName.getText().toString();
                Fragment fragmentName = ChangeUserNameFragment.actionInstance(getActivity(), niceName);
                ((ChangeUserNameFragment) fragmentName).setOnChangeListener(this);
                super.addToBackStack(getActivity(), fragmentName);
                break;
            case R.id.baidu_account_lay:
                if (Util.isNetworkConn(getActivity())) {
                    WebActivity.actionStartWeb(getActivity(), WebActivity.BIND_BAI_DU_URL);
                } else {
                    ErmuApplication.toast(getString(R.string.no_net));
                }
                break;
            case R.id.person_login_password:
                Fragment fragmentPas = ChangePasswordFragment.actionInstance(getActivity());
                super.addToBackStack(getActivity(), fragmentPas);
                break;
            case R.id.peroson_login_email:
                break;
            case R.id.btn_logout:
                SweetAlertDialog dialog = new SweetAlertDialog(getActivity());
                dialog.changeAlertType(SweetAlertDialog.NORMAL_TYPE);
                dialog.setTitleText(getResources().getString(R.string.logout_account));
                dialog.setContentText(getString(R.string.tip_iermu_logout));
                dialog.setCancelText(getResources().getString(R.string.sure));
                dialog.setConfirmText(getResources().getString(R.string.cancel));
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        // 清除WebView页面缓存Cookie
                        CookieSyncManager.createInstance(getActivity());
                        CookieManager.getInstance().removeAllCookie();
                        CookieSyncManager.getInstance().sync();
                        LoginiActivity.actionStartLogin(getActivity(), false);
                        ErmuBusiness.getAccountAuthBusiness().logout();
                        sweetAlertDialog.dismiss();
                        getActivity().finish();
                    }
                });
                dialog.show();
                break;
            case R.id.cancel:
                YoYo.with(Techniques.FadeOutDown).duration(400).playOn(mChoosePicture);
                mChoosePicture.setVisibility(View.INVISIBLE);
                mDialogBg.setVisibility(View.INVISIBLE);
                mPersonNick.setEnabled(true);
                mPersonHead.setEnabled(true);
                mPersonLoginPas.setEnabled(true);
                break;
            case R.id.choose_img:
                mChoosePicture.setVisibility(View.INVISIBLE);
                mDialogBg.setVisibility(View.INVISIBLE);
                mPersonNick.setEnabled(true);
                mPersonHead.setEnabled(true);
                mPersonLoginPas.setEnabled(true);
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IAccountAuthBusiness business = ErmuBusiness.getAccountAuthBusiness();
        business.unRegisterListener(OnGetThirdConnectListener.class, this);
    }

    /**
     * 更换头像
     */
//    @Override
//    public void callBack() {
//        chooseHead.dismiss();
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
//        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(pickIntent, REQUESTCODE_PICK);
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        switch (requestCode) {
            case REQUESTCODE_PICK:
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                break;
            case REQUESTCODE_CUTTING:
                setPicToView(data);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        int px = DimenUtils.dip2px(getActivity(), 90);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);//宽高的比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", px);//裁剪的图片的宽高
        intent.putExtra("outputY", px);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    /**
     * 设置修剪好的图片
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            final Drawable drawable = new BitmapDrawable(null, photo);
            headImg.setImageDrawable(drawable);
            final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            final Bitmap bitmapSmall = FileUtil.compressImage(bitmap, 20, 700);//kb
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    currentTime = new Date().getTime();
                    FileUtil.saveAvatarBitmap(bitmapSmall, currentTime);
                    uploadAvatar();
                }
            }, 200);
        }
    }

    private void uploadAvatar() {
        showDialog();
        String imagePath = PathConfig.CACHE_AVATAR + "/" + currentTime + ".jpg";
        File file = new File(imagePath);
//        int size = (int) (file.length() / 1024);
//        ErmuApplication.toast("size大小:"+size);
        String accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        String url = ApiConfig.UPLOAD_AVATAR;
        Map<String, String> params = new HashMap<String, String>();
        params.put("access_token",accessToken);
        Pair<String, File> pair = new Pair<String, File>("image", file);
        new okhttp.zhy.http.okhttp.request.OkHttpRequest.Builder()
                .url(url)
                .params(params)
                .headers(null)
                .tag(getActivity())
                .files(pair)
                .upload(new ResultCallback<String>() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (dialog != null) dialog.dismiss();
                        Logger.i("uploadAvatar"+e.toString());
                        ErmuApplication.toast(getString(R.string.upload_avatar_failed));
                    }

                    @Override
                    public void onResponse(String response) {

                        if (response != null) {
                            try {
                                JSONObject json = new JSONObject(response);
                                String avatar = json.optString("avatar");
                                String uid = json.optString("uid");
                                AccountWrapper.updateUserAvatar(uid, avatar);
                                Picasso.with(getActivity())
                                        .load(avatar)
                                        .placeholder(headImg.getDrawable())
                                        .into(headImg, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                if (dialog != null) dialog.dismiss();
                                                ErmuApplication.toast(getString(R.string.upload_avatar_success));
                                            }

                                            @Override
                                            public void onError() {}
                                        });
                                Logger.i("uploadAvatar"+response.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void showDialog() {
        dialog = new CommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(getString(R.string.upload_avatar_loading));
    }

    private void updateBdState(String uid, String bdAccount) {
        if (TextUtils.isEmpty(uid)) {
            mBindBd.setText(getString(R.string.no_binding));
            mBaiduImg.setVisibility(View.VISIBLE);
            mBaiduLay.setEnabled(true);
            mBaiduLay.setClickable(true);
        } else {
            mBindBd.setText(bdAccount);
            mBaiduImg.setVisibility(View.INVISIBLE);
            mBaiduLay.setEnabled(false);
            mBaiduLay.setClickable(false);
        }
    }

    @Override
    public void changeUserName(String uName) {
        mNickName.setText(uName);
    }

    @Override
    public void onUserInfo(UserInfo info) {
        if (info == null) return;
        String avatar = info.getAvatar();
        String userName = info.getUserName();
        String email = info.getEmail();
        int emailStatus = info.getEmailStatus();
        refreshView(avatar, userName, email, emailStatus, "");
    }

    @Override
    public void onGetThirdConnect(Business business, String uid, String bdAccount) {
        updateBdState(uid, bdAccount);
    }

    private void refreshView(final String avatar, String userName, String email, int emailStatus, String bdUid) {
        Picasso.with(getActivity())
                .load(avatar)
                .placeholder(R.drawable.avator_img)
                .into(headImg);
        mNickName.setText(userName);
        if (TextUtils.isEmpty(email)) {
            mMailTxt.setVisibility(View.GONE);
        } else {
            mMailTxt.setVisibility(View.VISIBLE);
            if (emailStatus == 0) {
                if (getActivity() != null) {
                    String auth = "(" + getActivity().getResources().getString(R.string.baidu_auth) + ")";
                    mMailTxt.setText(email + auth);
                }
            } else {
                mMailTxt.setText(email);
                mMailTxt.setCompoundDrawables(null, null, null, null);
            }
        }
    }
}
