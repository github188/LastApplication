package com.iermu.ui.fragment.personalinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IUserCenterBusiness;
import com.iermu.client.listener.OnDeletePhotoChangeListener;
import com.iermu.client.listener.OnScreenerPictureListener;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.PictureAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonDialog;
import com.iermu.ui.view.PullableStickyListHeadersListView;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 
 * Created by zhoushaopei on 16/1/5.
 */
public class PictureFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, PullToRefreshLayout.OnRefreshListener, PictureAdapter.SelectDataChangeListener, OnScreenerPictureListener, OnDeletePhotoChangeListener {

    private static String TYPE = "photo_type";

    @ViewInject(R.id.picture_list)  PullableStickyListHeadersListView mListView;
    @ViewInject(R.id.lvPullLayout)  PullToRefreshLayout mPullableLayout;
    @ViewInject(R.id.delete_layout) LinearLayout mDeleteLay;
    @ViewInject(R.id.delete_btn)    Button deleteBtn;
    @ViewInject(R.id.select_all)    TextView mTextSelectAll;
    @ViewInject(R.id.no_photo)      RelativeLayout mNoPhoto;

    private PictureAdapter adapter;
    private int currentType = -1;
    private boolean isDelete = false;
    private OnListScrollChangeListener listener;
    private List<ScreenClip> mList = new ArrayList<ScreenClip>();

    public static Fragment actionInstance(FragmentActivity activity, int type){
        PictureFragment fragment = new PictureFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }

    public void setListScrollListener(OnListScrollChangeListener listener){
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.picture_fragment, container, false);
        ViewHelper.inject(this, view);

        initView();
        return view;
    }

    private void initView() {
        Bundle bundle = getArguments();
        IUserCenterBusiness business = ErmuBusiness.getUserCenterBusiness();
        business.registerListener(OnScreenerPictureListener.class, this);
        business.registerListener(OnDeletePhotoChangeListener.class, this);
        if (currentType == PhotoType.ALL) {
            business.getScreenClip();
        } else if (currentType == PhotoType.PHOTO) {
            business.getUserScreen();
        } else if (currentType == PhotoType.FILM_EDIT) {
            business.getUserClip();
        }
        adapter = new PictureAdapter(getActivity());
        adapter.setChangeListener(this);
        mListView.setOnScrollListener(this);
        mListView.setAdapter(adapter);
        mListView.canPullUp();
        mListView.canPullDown();
        mListView.setOnItemClickListener(this);
        mPullableLayout.setOnRefreshListener(this);
        if (mPullableLayout != null && listener != null) {
            listener.onSetCurrentFragment();
        }
    }

    public void updateData(int type) {
        Logger.i("type:" + type);
        currentType = type;
        Logger.i("currentType:updateData"+currentType);
        if (mPullableLayout != null) {
            if (type == PhotoType.ALL) {
                ErmuBusiness.getUserCenterBusiness().getScreenClip();
            } else if (type == PhotoType.PHOTO) {
                ErmuBusiness.getUserCenterBusiness().getUserScreen();
            } else if (type == PhotoType.FILM_EDIT) {
                ErmuBusiness.getUserCenterBusiness().getUserClip();
            }
        }
    }

    @OnClick(value = {R.id.select_all, R.id.delete_btn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_all:
                adapter.selectAllOrUnSelectAll();
                break;
            case R.id.delete_btn:
                final CommonDialog commonDialog = new CommonDialog(getActivity());
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog
                        .setTitle(getString(R.string.delete_photo_sure))
                        .setContent(getString(R.string.delete_photo_content))
                        .setCancelText(getString(R.string.cancle))
                        .setOkText(getString(R.string.btn_cam_ok))
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Map<String, ScreenClip> deleteList = adapter.getDeleteList();
                                IUserCenterBusiness business = ErmuBusiness.getUserCenterBusiness();
                                business.deleteScreenClip(deleteList);
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
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isDelete) {
            ScreenClip item = adapter.getItem(position);
            int type = item.getType();
            if (type == PhotoType.PHOTO) {
                ScreenFilmFragment fragment = ScreenFilmFragment.actionInstance(getResources().getString(R.string.my_live), mList, position);
                if(fragment != null) {
                    addToBackStack(fragment);
                }
            } else {
                Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("com.baidu.netdisk");
                if (intent != null) {
                    startActivity(intent);
                } else {
                    ErmuApplication.toast(getString(R.string.down_bdy_txt));
                }
            }
        } else {
            adapter.selectOrUnSelectOne(position);
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        if (currentType == PhotoType.ALL) {
            ErmuBusiness.getUserCenterBusiness().getScreenClip();
        } else if (currentType == PhotoType.PHOTO) {
            ErmuBusiness.getUserCenterBusiness().getUserScreen();
        } else if (currentType == PhotoType.FILM_EDIT) {
            ErmuBusiness.getUserCenterBusiness().getUserClip();
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        if (Util.isNetworkConn(getActivity())) {
            mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else {
            mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IUserCenterBusiness business = ErmuBusiness.getUserCenterBusiness();
        business.unRegisterListener(OnScreenerPictureListener.class, this);
        business.unRegisterListener(OnDeletePhotoChangeListener.class, this);
    }

    public void showDeleteLay() {
        isDelete = true;
        mDeleteLay.setVisibility(View.VISIBLE);
        adapter.setTimeHide();
    }

    public void hideDeleteLay() {
        mTextSelectAll.setSelected(false);
        isDelete = false;
        mDeleteLay.setVisibility(View.GONE);
        deleteBtn.setText(R.string.delete);
        deleteBtn.setEnabled(false);
        adapter.setTimeShow();
    }

    @Override
    public void onSelectDataChange(Map<String, ScreenClip> map) {
        String str = getString(R.string.delete_pic_btn);
        String deleteSize = String.format(str, map.size());
        deleteBtn.setText(deleteSize);
        if (map.size() > 0 && map.size() != mList.size()) {
            mTextSelectAll.setSelected(false);
            deleteBtn.setEnabled(true);
        } else if (map.size() == mList.size()) {
            mTextSelectAll.setSelected(true);
            deleteBtn.setEnabled(true);
        } else {
            mTextSelectAll.setSelected(false);
            deleteBtn.setEnabled(false);
        }
    }

    @Override
    public void onClickImage(int position) {

    }

    private void refreshViewAdapter() {
        Logger.i("currentType:refreshViewAdapter"+currentType);
        List<ScreenClip> pictureList = ErmuBusiness.getUserCenterBusiness().getScreenClip(currentType);
        emptyLayout(pictureList);
        adapter.notifyDataSetChanged(pictureList);
        if (Util.isNetworkConn(getActivity())) {
            mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
        } else {
            mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
        }
    }

    @Override
    public void onScreenPicture(List<ScreenClip> pictureList, int type) {
        if (type != currentType) return;
        if (pictureList == null) {
            pictureList = new ArrayList<ScreenClip>();
            mList.clear();
        }
        this.mList = pictureList;
        Logger.i("currentType:onScreenPicture"+currentType);
        refreshViewAdapter();
    }

    private void emptyLayout(List<ScreenClip> mList) {
        if (mList != null && mList.size() > 0) {
            mNoPhoto.setVisibility(View.GONE);
            mPullableLayout.setVisibility(View.VISIBLE);
            if (listener != null && !isDelete) {
                listener.onDeleteVisible(true);
            } else {
                listener.onDeleteVisible(false);
            }
        } else {
            if (listener != null && !isDelete) {
                listener.onDeleteVisible(false);
            }
            mNoPhoto.setVisibility(View.VISIBLE);
            mPullableLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDeletePhoto() {
        adapter.clearDeleteList();
        if (listener != null) {
            listener.onDataChange();
        }
        if (!Util.isNetworkConn(getActivity())) return;
        if (currentType == PhotoType.ALL) {
            ErmuBusiness.getUserCenterBusiness().getScreenClip();
        } else if (currentType == PhotoType.PHOTO) {
            ErmuBusiness.getUserCenterBusiness().getUserScreen();
        } else if (currentType == PhotoType.FILM_EDIT) {
            ErmuBusiness.getUserCenterBusiness().getUserClip();
        }

    }

    public interface OnListScrollChangeListener {
        // 页面切换时调用
        void onListScrollChange(int offset, boolean isAnimation);
        //删除图片
        void onDataChange();
        //删除按钮禁用
        void onDeleteVisible(boolean visible);
        //显示当前的item
        void onSetCurrentFragment();
    }
}
