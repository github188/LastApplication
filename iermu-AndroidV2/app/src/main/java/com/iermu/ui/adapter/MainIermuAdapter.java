package com.iermu.ui.adapter;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.CamOnlineStatus;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveDataType;
import com.iermu.client.model.constant.ShareType;
import com.iermu.client.model.viewmodel.CollectCamItem;
import com.iermu.client.model.viewmodel.MimeCamItem;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.MainIermuFragment;
import com.iermu.ui.fragment.MineIermu.MainRecordFragment;
import com.iermu.ui.fragment.MineIermu.ShareControlFragment;
import com.iermu.ui.fragment.authshare.AuthUserFragment;
import com.iermu.ui.fragment.camseting.SettingFragment;
import com.iermu.ui.fragment.publicchannel.PublicLiveFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.UpgradeViewloading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * Created by zhoushaopei on 15/6/19.
 */
public class MainIermuAdapter extends BaseAdapter implements View.OnClickListener, AuthUserFragment.SetAuthUser {

    private final static int ACTION_HOME = 1;    //授权家人使用
    private final static int ACTION_PUBLIC = 2;    //申请公开直播
    private final static int ACTION_SETTING = 3;    //设置界面
    private final static int ACTION_AUTH = 4;    //设置界面

    private FragmentActivity ctx;
    private List<MimeCamItem> mList;
    private SweetAlertDialog dialog;
    CamLive cam;
    private boolean isGrantUserLoading = false;//加载授权用户进度
    private int imageWidth;
    private OnDropCallBack mCallBack;

    private Map<String, String> thumbnailMap;
    private long lastUpdateTime = 0;

    private ICamSettingBusiness camSettingBusiness;

    private Map mMap;

    private Map countMap = new HashMap();

    private Map viewMap = new HashMap();

    @Override
    public void setUser(String num) {
    }


    public interface OnDropCallBack {
        public void dropAuth(String deviceId, String camName);
    }

    public MainIermuAdapter(FragmentActivity context, OnDropCallBack callBack) {
        this.ctx = context;
        this.mList = new ArrayList<MimeCamItem>();
        this.mMap = new HashMap();
        this.thumbnailMap = new HashMap<String, String>();
        initImageWidth();
        mCallBack = callBack;
    }

    private void initImageWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        ctx.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels - Util.DensityUtil.dip2px(ctx, 16);
        imageWidth = width;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getItemType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logger.i("itemView: position " + position);
        ViewHolderMy holder_my = null;
        ViewHolderAuth holder_auth = null;
        ViewHolderCollOne holder_collect_one = null;
        ViewHolderCollTwo holder_coll_two = null;
        int type = getItemViewType(position);
        MimeCamItem item = (MimeCamItem) getItem(position);
        switch (type) {
            case MimeCamItem.TYPE_MIME:
                holder_my = new ViewHolderMy();
                convertView = View.inflate(ctx, R.layout.item_mycam, null);
                initMyCamView(convertView, holder_my);
                viewMap.put(item.getItem().getDeviceId(),convertView);
                initMyCamData(holder_my, item);
                break;
            case MimeCamItem.TYPE_AUTHORIZE:
                holder_auth = new ViewHolderAuth();
                convertView = View.inflate(ctx, R.layout.item_authorize_cam, null);
                initAuthCamView(convertView, holder_auth);
                initAuthCamData(holder_auth, item);
                break;
            case MimeCamItem.TYPE_COLLECT:
                holder_collect_one = new ViewHolderCollOne();
                convertView = View.inflate(ctx, R.layout.item_collectcam_one, null);
                initCollectView(convertView, holder_collect_one);
                initCollectData(holder_collect_one, item);
                break;
            case MimeCamItem.TYPE_COLLECTITEM:
                holder_coll_two = new ViewHolderCollTwo();
                convertView = View.inflate(ctx, R.layout.item_collectcam_two, null);
                initCollectTwoView(convertView, holder_coll_two);
                initCollectTwoData(holder_coll_two, item, position);
                break;
        }
        return convertView;
    }

    //我的设备
    private void initMyCamView(View convertView, ViewHolderMy holder_my) {
        holder_my.auth_cam_name = (TextView) convertView.findViewById(R.id.auth_cam_name);
        holder_my.auth_cam_img = (ImageView) convertView.findViewById(R.id.auth_cam_img);
        holder_my.auth_home = (LinearLayout) convertView.findViewById(R.id.auth_home);
        holder_my.auth_text = (TextView) convertView.findViewById(R.id.auth_text);
        holder_my.apply_public = (LinearLayout) convertView.findViewById(R.id.apply_public);
        holder_my.look_video = (LinearLayout) convertView.findViewById(R.id.look_video);
        holder_my.auth_set = (ImageView) convertView.findViewById(R.id.my_cam_setting);
        holder_my.cam_status = (TextView) convertView.findViewById(R.id.cam_status);
        holder_my.share_txt = (TextView) convertView.findViewById(R.id.share_txt);
        holder_my.lock_img = (ImageView) convertView.findViewById(R.id.lock_img);
        holder_my.lyy_logo = (ImageView) convertView.findViewById(R.id.lyy_logo);
        holder_my.upgrade_lodding_img = (ImageView) convertView.findViewById(R.id.upgrade_lodding_img);
        holder_my.upgrade_progress = (TextView) convertView.findViewById(R.id.upgrade_progress);
        holder_my.upgrade_text = (TextView) convertView.findViewById(R.id.upgrade_text);
        holder_my.upgrade_view_loading = (UpgradeViewloading) convertView.findViewById(R.id.upgrade_view_loading);

        convertView.setTag(holder_my);
    }

    private void initMyCamData(final ViewHolderMy holder_my, MimeCamItem item) {
        cam = item.getItem();
        CamLive cam = item.getItem();
        Integer status = (Integer)mMap.get(cam.getDeviceId());
        int count = cam.getGrantNum();
        if (count > 0) {
            holder_my.auth_text.setText(String.format(ctx.getString(R.string.over_auth_family), count));
//            holder_my.auth_home.setBackgroundResource(R.drawable.auth_press);
            holder_my.auth_text.setTextColor(ctx.getResources().getColor(R.color.cancle_empower_home));
            setAuthDrawable(holder_my.auth_text);
        }
        int type = cam.getConnectType();
        if (type == ConnectType.LINYANG) {
            holder_my.lyy_logo.setVisibility(View.VISIBLE);
        } else {
            holder_my.lyy_logo.setVisibility(View.INVISIBLE);
        }
        if (cam.isOffline()) {
            holder_my.cam_status.setText(R.string.off_line);
            holder_my.cam_status.setBackgroundResource(R.drawable.state_off);
            loadNetImage(cam, holder_my.auth_cam_img, R.drawable.iermu_thumb);
        } else if (cam.isPowerOn()) {
            holder_my.cam_status.setText(R.string.on);
            holder_my.cam_status.setBackgroundResource(R.drawable.state_on);
            loadNetImage(cam, holder_my.auth_cam_img, R.drawable.iermu_thumb);
        } else {
            holder_my.cam_status.setText(R.string.close);
            holder_my.cam_status.setBackgroundResource(R.drawable.state_off);
            ViewGroup.LayoutParams params = holder_my.auth_cam_img.getLayoutParams();
            params.width = imageWidth;
            params.height = imageWidth * 9 / 16;
            Picasso.with(ctx).load("default")//cacheUrl
                    .placeholder(R.drawable.iermu_turn_off_)
                    .resize(imageWidth, imageWidth * 9 / 16)
                    .into(holder_my.auth_cam_img);

        }
        holder_my.auth_cam_name.setText(cam.getDescription());
        holder_my.look_video.setTag(cam);
        int shareType = cam.getShareType();
        RelativeLayout.LayoutParams paramUp = (RelativeLayout.LayoutParams) holder_my.upgrade_view_loading.getLayoutParams();
        paramUp.height = imageWidth * 9 / 16;

        if (shareType == ShareType.PUB_NOTCLOUD) {
//            holder_my.apply_public.setBackgroundResource(R.drawable.share_selector);
            holder_my.share_txt.setText(R.string.now_play);
            holder_my.share_txt.setTextColor(ctx.getResources().getColor(R.color.cancle_empower_home));
            setShareDrawable(holder_my.share_txt);
        } else if (shareType == ShareType.PRI_NOTCLOUD) {
//            holder_my.apply_public.setBackgroundResource(R.drawable.share_selector);
            holder_my.share_txt.setText(R.string.secret_share);
            holder_my.share_txt.setTextColor(ctx.getResources().getColor(R.color.cancle_empower_home));
            setShareDrawable(holder_my.share_txt);
        }
        holder_my.auth_set.setTag(item);
        holder_my.auth_home.setTag(item);
        holder_my.apply_public.setTag(item);
        holder_my.auth_home.setOnClickListener(this);
        holder_my.apply_public.setOnClickListener(this);
        holder_my.look_video.setOnClickListener(this);
        holder_my.auth_set.setOnClickListener(this);

        review_upgrade_loading(holder_my);


    }

    // 授权使用
    private void initAuthCamView(View convertView, ViewHolderAuth holder_auth) {
        holder_auth.my_cam_name = (TextView) convertView.findViewById(R.id.my_cam_name);
        holder_auth.my_cam_img = (ImageView) convertView.findViewById(R.id.my_cam_img);
        holder_auth.camstatus1 = (TextView) convertView.findViewById(R.id.cam_status1);
        holder_auth.drop_auth_dev = (TextView) convertView.findViewById(R.id.drop_auth_dev);
        holder_auth.lyylogo = (ImageView) convertView.findViewById(R.id.lyy_logo);
        convertView.setTag(holder_auth);
    }

    private void initAuthCamData(final ViewHolderAuth holder, MimeCamItem item) {
        CamLive cam = item.getItem();
        int type = cam.getConnectType();
        if (type == ConnectType.LINYANG) {
            holder.lyylogo.setVisibility(View.VISIBLE);
        } else {
            holder.lyylogo.setVisibility(View.INVISIBLE);
        }
        if (cam.isOffline()) {
            holder.camstatus1.setText(R.string.off_line);
            holder.camstatus1.setBackgroundResource(R.drawable.state_off);
            loadNetImage(cam, holder.my_cam_img, R.drawable.iermu_thumb);
        } else if (cam.isPowerOn()) {
            holder.camstatus1.setBackgroundResource(R.drawable.state_on);
            holder.camstatus1.setText(R.string.on);
            loadNetImage(cam, holder.my_cam_img, R.drawable.iermu_thumb);
        } else {
            holder.camstatus1.setText(R.string.close);
            holder.camstatus1.setBackgroundResource(R.drawable.state_off);
            String thumbnail = cam.getThumbnail();
            ViewGroup.LayoutParams params = holder.my_cam_img.getLayoutParams();
            params.width = imageWidth;
            params.height = imageWidth * 9 / 16;
            Picasso.with(ctx).load("default")
                    .placeholder(R.drawable.iermu_turn_off_)
                    .resize(imageWidth, imageWidth * 9 / 16)
                    .into(holder.my_cam_img);
        }
        holder.my_cam_name.setText(cam.getDescription());
        holder.drop_auth_dev.setTag(item);
        holder.drop_auth_dev.setOnClickListener(this);
    }

    private void loadNetImage(CamLive cam, final ImageView imageView, int placeholderResid) {
        final String thumbnail = TextUtils.isEmpty(cam.getThumbnail()) ? "default" : cam.getThumbnail();
        String mapThumbnail = thumbnailMap.get(cam.getDeviceId());
        final String deviceId = cam.getDeviceId();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = imageWidth;
        params.height = imageWidth * 9 / 16;
        imageView.setLayoutParams(params);
        final String cacheThumbnail = TextUtils.isEmpty(mapThumbnail) ? thumbnail : mapThumbnail;
        Picasso.with(ctx).load(cacheThumbnail)
                .placeholder(placeholderResid)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (thumbnailMap.containsKey(deviceId)) {
                            Picasso.with(ctx).load(thumbnail)
                                    .noPlaceholder()
                                            //.noFade()
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            thumbnailMap.put(deviceId, thumbnail);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                        thumbnailMap.put(deviceId, cacheThumbnail);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void loadSmallNetImage(CamLive cam, final ImageView imageView, int placeholderResid) {
        final String thumbnail = TextUtils.isEmpty(cam.getThumbnail()) ? "default" : cam.getThumbnail();
        String mapThumbnail = thumbnailMap.get(cam.getDeviceId());
        final String deviceId = cam.getDeviceId();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = imageWidth;
        params.height = imageWidth / 4;
        imageView.setLayoutParams(params);
        final String cacheThumbnail = TextUtils.isEmpty(mapThumbnail) ? thumbnail : mapThumbnail;
        Picasso.with(ctx).load(cacheThumbnail)
                .placeholder(placeholderResid)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (thumbnailMap.containsKey(deviceId)) {
                            Picasso.with(ctx).load(thumbnail)
                                    .noPlaceholder()
                                            //.noFade()
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            thumbnailMap.put(deviceId, thumbnail);
                                        }

                                        @Override
                                        public void onError() {
                                        }
                                    });
                        }
                        thumbnailMap.put(deviceId, cacheThumbnail);
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    //收藏第一种形式
    private void initCollectView(View convertView, ViewHolderCollOne holder_collect_one) {
        View view = convertView.findViewById(R.id.collect_cam);
        holder_collect_one.itemView = view;
        holder_collect_one.save_name = (TextView) view.findViewById(R.id.cam_name);
        holder_collect_one.save_img = (ImageView) view.findViewById(R.id.cam_img);
        holder_collect_one.cam_state = (TextView) view.findViewById(R.id.cam_status2);
        holder_collect_one.lyy_logo_collect = (ImageView) view.findViewById(R.id.lyy_logo_);
        convertView.setTag(holder_collect_one);
    }

    private void initCollectData(ViewHolderCollOne holder_collect_one, MimeCamItem item) {
        CamLive cam = item.getItem();
        if (cam.getConnectType() == ConnectType.LINYANG) {
            holder_collect_one.lyy_logo_collect.setVisibility(View.VISIBLE);
        } else {
            holder_collect_one.lyy_logo_collect.setVisibility(View.INVISIBLE);
        }
        if (cam.isOffline()) {
            holder_collect_one.cam_state.setText(R.string.off_line);
            holder_collect_one.cam_state.setBackgroundResource(R.drawable.state_off);
        } else if (cam.isPowerOn()) {
            holder_collect_one.cam_state.setText(R.string.on);
            holder_collect_one.cam_state.setBackgroundResource(R.drawable.state_on);
        } else {
            holder_collect_one.cam_state.setText(R.string.close);
            holder_collect_one.cam_state.setBackgroundResource(R.drawable.state_off);
        }
        holder_collect_one.save_name.setText(cam.getDescription());
        holder_collect_one.itemView.setTag(cam);
        holder_collect_one.itemView.setOnClickListener(this);
        loadNetImage(cam, holder_collect_one.save_img, R.drawable.iermu_thumb);
    }

    //收藏第二种形式
    private void initCollectTwoView(View convertView, ViewHolderCollTwo holder_save_two) {
        View leftView = convertView.findViewById(R.id.collect_left);
        View rightView = convertView.findViewById(R.id.collect_right);

        holder_save_two.leftView = leftView;
        holder_save_two.coll_name_left = (TextView) leftView.findViewById(R.id.cam_name);
        holder_save_two.coll_img_left = (ImageView) leftView.findViewById(R.id.cam_img);
        holder_save_two.cam_status2_left = (TextView) leftView.findViewById(R.id.cam_status2);
        holder_save_two.lyy_logo_collect_left = (ImageView) leftView.findViewById(R.id.lyy_logo);

        holder_save_two.rightView = rightView;
        holder_save_two.coll_name_right = (TextView) rightView.findViewById(R.id.cam_name);
        holder_save_two.coll_img_right = (ImageView) rightView.findViewById(R.id.cam_img);
        holder_save_two.cam_status2_right = (TextView) rightView.findViewById(R.id.cam_status2);
        holder_save_two.lyy_logo_collect_right = (ImageView) rightView.findViewById(R.id.lyy_logo);
        convertView.setTag(holder_save_two);
    }

    private void initCollectTwoData(final ViewHolderCollTwo holder, MimeCamItem item, final int position) {
        CollectCamItem collectItem = (CollectCamItem) item;
        CamLive leftCam = collectItem.getLeftCam();
        if (leftCam.getConnectType() == ConnectType.LINYANG) {
            holder.lyy_logo_collect_left.setVisibility(View.VISIBLE);
        } else {
            holder.lyy_logo_collect_left.setVisibility(View.GONE);
        }
        if (leftCam.isOffline()) {
            holder.cam_status2_left.setText(R.string.off_line);
            holder.cam_status2_left.setBackgroundResource(R.drawable.state_off);
            loadSmallNetImage(leftCam, holder.coll_img_left, R.drawable.iermu_turn_off_);
        } else if (leftCam.isPowerOn()) {
            holder.cam_status2_left.setText(R.string.on);
            holder.cam_status2_left.setBackgroundResource(R.drawable.state_on);
        } else {
            holder.cam_status2_left.setText(R.string.close);
            holder.cam_status2_left.setBackgroundResource(R.drawable.state_off);
        }
        holder.leftView.setTag(leftCam);
        holder.leftView.setOnClickListener(this);
        holder.coll_name_left.setText(leftCam.getDescription());
        loadSmallNetImage(leftCam, holder.coll_img_left, R.drawable.iermu_thumb);

        CamLive rightCam = collectItem.getRightCam();
        holder.rightView.setVisibility((rightCam == null) ? View.INVISIBLE : View.VISIBLE);
        if (rightCam == null) {
            //TODO 解决右边无数据，下方显示空白
            ViewGroup.LayoutParams layoutParams = holder.rightView.getLayoutParams();
            layoutParams.height = 50;
            holder.rightView.setLayoutParams(layoutParams);
            return;
        }
        if (rightCam.getConnectType() == ConnectType.LINYANG) {
            holder.lyy_logo_collect_right.setVisibility(View.VISIBLE);
        } else {
            holder.lyy_logo_collect_right.setVisibility(View.INVISIBLE);
        }
        if (rightCam.isOffline()) {
            holder.cam_status2_right.setText(R.string.off_line);
            holder.cam_status2_right.setBackgroundResource(R.drawable.state_off);
        } else if (rightCam.isPowerOn()) {
            holder.cam_status2_right.setText(R.string.on);
            holder.cam_status2_right.setBackgroundResource(R.drawable.state_on);
        } else {
            holder.cam_status2_right.setText(R.string.close);
            holder.cam_status2_right.setBackgroundResource(R.drawable.state_off);
        }
        holder.rightView.setTag(rightCam);
        holder.rightView.setOnClickListener(this);
        holder.coll_name_right.setText(rightCam.getDescription());
        loadSmallNetImage(rightCam, holder.coll_img_right, R.drawable.iermu_thumb);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auth_home:
                if (Util.isNetworkConn(ctx)) {
                    isGrantUserLoading = true;
                } else {
                    ErmuApplication.toast(ctx.getResources().getString(R.string.play_no_network));
                }
                actionFragment(1, v);
                break;
            case R.id.apply_public:
                actionFragment(2, v);
                break;
            case R.id.look_video:
                CamLive videoLive = (CamLive) v.getTag();
                BaseFragment.addToBackStack(ctx, MainRecordFragment.actionInstance(videoLive.getDeviceId()), true);
                break;
            case R.id.my_cam_setting:
                actionFragment(3, v);
                break;
            case R.id.collect_left:
                CamLive leftLive = (CamLive) v.getTag();
                if (leftLive.getDataType() == LiveDataType.COLLECT) {
                    openPublicFragment(leftLive);
                }
                break;
            case R.id.collect_right:
                CamLive rightLive = (CamLive) v.getTag();
                if (rightLive.getDataType() == LiveDataType.COLLECT) {
                    openPublicFragment(rightLive);
                }
                break;
            case R.id.collect_cam:
                CamLive itemLive = (CamLive) v.getTag();
                if (itemLive.getDataType() == LiveDataType.COLLECT) {
                    openPublicFragment(itemLive);
                }
                break;
            case R.id.drop_auth_dev:
                actionFragment(4, v);
                break;
        }
    }

    private void setShareDrawable(TextView view) {
        Drawable drawable = ctx.getResources().getDrawable(R.drawable.iermu_share_over);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        view.setCompoundDrawables(drawable, null, null, null);
    }

    private void setAuthDrawable(TextView view) {
        Drawable drawable = ctx.getResources().getDrawable(R.drawable.auth_have_homer);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        view.setCompoundDrawables(drawable, null, null, null);
    }

    private void actionFragment(int i, View v) {
        MimeCamItem item = (MimeCamItem) v.getTag();
        CamLive camLive = item.getItem();
        String deviceId = camLive.getDeviceId();
        String camName = camLive.getDescription();
        //int status = camLive.getStatus();
        String description = camLive.getDescription();
        String uk = camLive.getUk();

        if (i == ACTION_HOME) {
            Fragment fragment = AuthUserFragment.actionInstance(ctx, deviceId, description, uk);
            ((AuthUserFragment) fragment).setOnControlListener(this);
            BaseFragment.addToBackStack(ctx, fragment);
        } else if (i == ACTION_PUBLIC) {
            Fragment fragmentLive = ShareControlFragment.actionInstance(ctx, deviceId);
            BaseFragment.addToBackStack(ctx, fragmentLive);
        } else if (i == ACTION_SETTING) {
            Fragment fragmentSet = SettingFragment.actionInstance(deviceId);//ctx,, status,camName
            BaseFragment.addToBackStack(ctx, fragmentSet);
        } else if (i == ACTION_AUTH) {
            mCallBack.dropAuth(deviceId, camName);
        }
    }

    /**
     * 获取当前屏幕显示的DeviceIds
     *
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     * @return
     */
    public List<String> getItemIds(int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        List<String> deviceIds = new ArrayList<String>();
        if (getCount() > 0) {
            for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount - 1; i++) {
                MimeCamItem item = (MimeCamItem) getItem(i);
                int itemType = item.getItemType();
                if (itemType == MimeCamItem.TYPE_MIME
                        || itemType == MimeCamItem.TYPE_AUTHORIZE
                        || itemType == MimeCamItem.TYPE_COLLECT) {
                    CamLive live = item.getItem();
                    deviceIds.add(live.getDeviceId());
                } else if (itemType == MimeCamItem.TYPE_COLLECTITEM) {
                    CollectCamItem collectItem = (CollectCamItem) item;
                    CamLive leftCam = collectItem.getLeftCam();
                    CamLive rightCam = collectItem.getRightCam();
                    if (leftCam != null) {
                        deviceIds.add(leftCam.getDeviceId());
                    }
                    if (rightCam != null) {
                        deviceIds.add(rightCam.getDeviceId());
                    }
                }
            }
        }
        Logger.i(" first:" + firstVisibleItem + " visible:" + visibleItemCount + " total:" + totalItemCount + " deviceIds:" + deviceIds.toString());
        return deviceIds;
    }

    public void notifyDataSetChanged(List<MimeCamItem> list) {
        if (list == null) {
            list = new ArrayList<MimeCamItem>();
        }
        this.mList = list;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(Map map) {
        if (map == null) {
            map = new HashMap();
        }
        this.mMap = map;
        notifyDataSetChanged();
    }

    class ViewHolderMy {
        TextView auth_cam_name;
        ImageView auth_cam_img;
        LinearLayout auth_home;
        LinearLayout apply_public;
        LinearLayout look_video;
        ImageView auth_set;
        TextView cam_status;
        TextView share_txt;
        TextView auth_text;
        ImageView lock_img;
        ImageView lyy_logo;
        ImageView upgrade_lodding_img;
        TextView upgrade_progress;
        TextView upgrade_text;
        UpgradeViewloading upgrade_view_loading;
    }

    class ViewHolderAuth {
        TextView my_cam_name;
        ImageView my_cam_img;
        TextView camstatus1;
        ImageView cam_img;
        TextView drop_auth_dev;
        ImageView lyylogo;
        View cover_view;
    }

    class ViewHolderCollOne {
        TextView save_name;
        ImageView save_img;
        TextView cam_state;
        View itemView;
        ImageView lyy_logo_collect;
    }

    class ViewHolderCollTwo {
        TextView coll_name_left;
        TextView coll_name_right;
        ImageView coll_img_left;
        ImageView coll_img_right;
        TextView cam_status2_left;
        TextView cam_status2_right;
        View leftView;
        View rightView;
        ImageView lyy_logo_collect_left;
        ImageView lyy_logo_collect_right;
    }

    private void openPublicFragment(CamLive camLive) {
        String shareId = camLive.getShareId();
        String uk1 = camLive.getUk();
        String deviceId1 = camLive.getDeviceId();
        Fragment fragment = PublicLiveFragment.actionInstance(deviceId1, shareId, uk1, false);
        if (fragment == null) {
            Logger.d("opened");
            return;
        }
        BaseFragment.addToBackStack(ctx, fragment, true);
    }

    public View getUpgradeView(String deviced){
        return (View)viewMap.get(deviced);
    }

    private void review_upgrade_loading(ViewHolderMy holder_my){
        Integer upgradeCount = (Integer)MainIermuFragment.upgradeCountMap.get(cam.getDeviceId());
        Integer upgradeStatus = (Integer)MainIermuFragment.upgradeStatusMap.get(cam.getDeviceId());
        if(upgradeCount!=null) {
            UpgradeViewloading loadingView = holder_my.upgrade_view_loading;
            loadingView.setVisibility(View.VISIBLE);
            loadingView.setTextProgress(upgradeCount);
            switch (upgradeStatus) {
                case 1:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_start_upgrade));
                    break;
                case 2:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_start_download));
                    break;
                case 3:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_download_success));
                    break;
                case 4:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_update_now));
                    break;
                case 5:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_update_success_refresh));
                    loadingView.setLoadingImg(5);
                    break;
                case -1:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_down_fail_refresh));
                    loadingView.setLoadingImg(-1);
                    break;
                case -2:
                    loadingView.setTextTip(ctx.getString(R.string.firmware_update_fail_refresh));
                    loadingView.setLoadingImg(-2);
                    break;
            }
            loadingView.startAnimation();
        }
    }
}
