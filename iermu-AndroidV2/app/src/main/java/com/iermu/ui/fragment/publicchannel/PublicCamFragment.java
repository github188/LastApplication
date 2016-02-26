package com.iermu.ui.fragment.publicchannel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IPubCamBusiness;
import com.iermu.client.listener.OnPublicCamChangedListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.ui.adapter.PublicChannelAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.LoadingView;
import com.lib.pulltorefreshview.PullToRefreshLayout;
import com.lib.pulltorefreshview.pullableview.PullableListView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.List;

/**
 * Created by zhoushaopei on 15/6/26.
 */
public class PublicCamFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener
        , AdapterView.OnItemClickListener, OnPublicCamChangedListener
        , AbsListView.OnScrollListener {

    @ViewInject(R.id.cam_list)
    PullableListView mListView;
    @ViewInject(R.id.lvPullLayout)
    PullToRefreshLayout mPullableLayout;
    @ViewInject(R.id.load_view)
    LoadingView loadView;
    @ViewInject(R.id.emptyView)
    View emptyView;
    @ViewInject(R.id.text_publick)
    TextView mPublickText;
    @ViewInject(R.id.net_error_img)
    ImageView netErrorImg;
    @ViewInject(R.id.text3)
    TextView text3;
    @ViewInject(R.id.error_btn)
    TextView mRefresh;
    @ViewInject(R.id.viewError)
    View viewError;
    @ViewInject(R.id.textViewError)
    TextView textViewError;
    @ViewInject(R.id.viewLoadBottom)
    View viewLoadBottom;
    @ViewInject(R.id.imageViewRefreshBottom)
    ImageView imageViewRefreshBottom;

    private String orderby;
    private int camType;
    private PublicChannelAdapter adapter;
    private IPubCamBusiness business;

    public static PublicCamFragment actionInstance(FragmentActivity activity, String orderby) {
        PublicCamFragment fragment = new PublicCamFragment();
        fragment.setPubCategory(orderby);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_cam, null);
        ViewHelper.inject(this, view);
        loadView.startAnimation();
        adapter = new PublicChannelAdapter(getActivity(), null);
        mListView.setOnScrollListener(this);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        mListView.setPullUp(true);
        RotateAnimation refreshingAnimation =
                (RotateAnimation) AnimationUtils.loadAnimation(getActivity(), com.lib.pulltorefreshview.R.anim.rotating);
        imageViewRefreshBottom.startAnimation(refreshingAnimation);
        mPullableLayout.setOnRefreshListener(this);
        mListView.setEmptyView(emptyView);
        emptyView.setVisibility(View.INVISIBLE);
        business = ErmuBusiness.getPubCamBusiness();
        business.registerListener(OnPublicCamChangedListener.class, this);
        business.syncNewCamList(orderby);
        isLoading = true;
        loadView.setVisibility((adapter.getCount() <= 0) ? View.VISIBLE : View.GONE);
        emptyView.setVisibility((loadView.getVisibility() == View.VISIBLE || adapter.getCount() > 0) ? View.INVISIBLE : View.VISIBLE);
        mListView.setVisibility(View.VISIBLE);
        if (!Util.isNetworkConn(getActivity())) {
            emptyView.setVisibility(View.VISIBLE);
            mPublickText.setText(getString(R.string.play_no_network));
            netErrorImg.setImageResource(R.drawable.error_img);
            text3.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
        }
//        refreshAdapter();
        return view;
    }

    @OnClick(value = {R.id.error_btn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.error_btn:
                loadView.setVisibility(View.VISIBLE);
                loadView.startAnimation();
                ErmuBusiness.getPubCamBusiness().syncNewCamList(orderby);
                isLoading = true;
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IPubCamBusiness business = ErmuBusiness.getPubCamBusiness();
        business.unRegisterListener(OnPublicCamChangedListener.class, this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                //List<String> deviceIds = adapter.getItemIds(firstVisibleItem, visibleItemCount, totalItemCount);
                //ErmuBusiness.getStreamMediaBusiness().preOpenLiveMedia(deviceIds);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (adapter == null) return;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                || scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            //List<String> deviceIds = adapter.getItemIds(firstVisibleItem, visibleItemCount, totalItemCount);
            //ErmuBusiness.getStreamMediaBusiness().preOpenLiveMedia(deviceIds);
        }
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleItemCount + firstVisibleItem == totalItemCount && !isLoading && business.getNextPageNum(orderby) != -1) {
            viewLoadBottom.setVisibility(View.VISIBLE);
            business.syncOldCamList(orderby);
            isLoading = true;
        }
    }

    private int scrollState = 0;
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;
    private boolean isLoading;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    /**
     * 设置排序分类
     *
     * @param orderby
     */
    public void setPubCategory(String orderby) {
        this.orderby = orderby;
    }

    /**
     * 切换tab时刷新数据
     */
    public void updateData() {
        if (adapter != null && adapter.getCount() == 0) {
            business.syncNewCamList(orderby);
            isLoading = true;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        business.syncNewCamList(orderby);
        isLoading = true;
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }

    private void refreshAdapter() {
        if (Util.isNetworkConn(getActivity())) {
            List<CamLive> list = business.getCamList(orderby);
            adapter.notifyDataSetChanged(list);
        } else {
            mPublickText.setText(getString(R.string.play_no_network));
            netErrorImg.setImageResource(R.drawable.error_img);
            text3.setVisibility(View.GONE);
            mRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CamLive camLive = adapter.getItem(position);
        String deviceId = camLive.getDeviceId();
        String shareId = camLive.getShareId();
        String uk = camLive.getUk();
        Fragment fragment = PublicLiveFragment.actionInstance(deviceId, shareId, uk, true);
        addToBackStack(getActivity(), fragment, true);
    }

    @Override
    public void onPublicCamChanged(Business business) {
        loadView.setVisibility(View.GONE);
        viewLoadBottom.setVisibility(View.GONE);
        isLoading = false;
        if (business.isSuccess()) {
            if (Util.isNetworkConn(getActivity())) {
                mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                viewError.setVisibility(View.GONE);
            } else {
                mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
                textViewError.setText(getString(R.string.connect_server_fail));
                viewError.setVisibility(View.VISIBLE);
            }
        } else {
            if (Util.isNetworkConn(getActivity())) {
                mPullableLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                textViewError.setText(getString(R.string.connect_server_fail)+"[" + business.getCode() + "]");
                viewError.setVisibility(View.VISIBLE);
            } else {
                mPullableLayout.refreshFinish(PullToRefreshLayout.FAIL);
                textViewError.setText(getString(R.string.connect_server_fail));
                viewError.setVisibility(View.VISIBLE);
            }
        }

        refreshAdapter();
    }

}
