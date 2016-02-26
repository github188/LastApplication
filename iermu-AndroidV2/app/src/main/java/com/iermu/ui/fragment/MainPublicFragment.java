package com.iermu.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.listener.OnPublicCamChangedListener;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.PubCamCategory;
import com.iermu.client.test.PubCamType;
import com.iermu.ui.adapter.ViewPagerAdapter;
import com.iermu.ui.fragment.publicchannel.PublicCamFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wcy on 15/6/18.
 */
public class MainPublicFragment extends BaseFragment {

    private static final String KEY_PUBCAMLIST = "pubcamlist";

    @ViewInject(R.id.viewpager) ViewPager viewPager;
    @ViewInject(R.id.imgview) LinearLayout imgView;
    @ViewInject(R.id.recommend_cam) TextView recommendCam;
    @ViewInject(R.id.hot_cam) TextView hotCam;
    @ViewInject(R.id.new_cam) TextView newCam;

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private ViewPagerAdapter adapter;
    private int offSet;
    private int currentItem;
    private Matrix matrix = new Matrix();
    private int bmWidth;
    private Animation animation;
    private Bitmap cursor;

    /**
     * 首页Tab启动公共频道
     * @param activity
     * @return
     */
    public static Fragment actionInstance(FragmentActivity activity){
        return new MainPublicFragment();
    }

    /**
     * 从体验入口启动公共频道
     * @param ctx
     * @return
     */
    public static Fragment actionPuCamList(FragmentActivity ctx) {
        MainPublicFragment fragment = new MainPublicFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_PUBCAMLIST, true);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.actionbar_title_pubcam)
        .setCommonBackClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                //公共频道体验状态, 会有返回按钮.
                getActivity().finish();
            }
        });
        Bundle bundle = getArguments();
        boolean b = (bundle!=null)&&bundle.containsKey(KEY_PUBCAMLIST);
        if(!b) setDisableCommonBack();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_channel, container, false);
        ViewHelper.inject(this, view);
        recommendCam.setTextColor(getResources().getColor(R.color.devices_help_btn_bg));
        hotCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
        newCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
        initViewPager(inflater);

        return view;
    }

    private void initViewPager(LayoutInflater inflater) {
        Fragment fragment0 = PublicCamFragment.actionInstance(getActivity(), PubCamCategory.RECOMMEND);
        Fragment fragment1 = PublicCamFragment.actionInstance(getActivity(), PubCamCategory.VIEW);
        Fragment fragment2 = PublicCamFragment.actionInstance(getActivity(), PubCamCategory.NEW);
        fragments.add(fragment0);
        fragments.add(fragment1);
        fragments.add(fragment2);

        initeCursor();
        if (fragments.size() > 0) {
        adapter = new ViewPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(PubCamType.RECOMED_CAM);
        viewPager.setOffscreenPageLimit(fragments.size() - 1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        recommendCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                        hotCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        newCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        recommendCam.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                        hotCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        newCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        if (currentItem == 1) {
                            animation = new TranslateAnimation(
                                    offSet * 2 + bmWidth, 0, 0, 0);
                        } else if (currentItem == 2) {
                            animation = new TranslateAnimation(offSet * 4 + 2
                                    * bmWidth, 0, 0, 0);
                        }
                        ((PublicCamFragment)(fragments.get(0))).updateData();
                        break;
                    case 1:
                        recommendCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        hotCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                        newCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        recommendCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        hotCam.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                        newCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        if (currentItem == 0) {
                            animation = new TranslateAnimation(0, offSet * 2
                                    + bmWidth, 0, 0);
                        } else if (currentItem == 2) {
                            animation = new TranslateAnimation(4 * offSet + 2
                                    * bmWidth, offSet * 2 + bmWidth, 0, 0);
                        }
                        ((PublicCamFragment)(fragments.get(1))).updateData();
                        break;
                    case 2:
                        recommendCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        hotCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                        newCam.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                        recommendCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        hotCam.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                        newCam.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                        if (currentItem == 0) {
                            animation = new TranslateAnimation(0, 4 * offSet + 2
                                    * bmWidth, 0, 0);
                        } else if (currentItem == 1) {
                            animation = new TranslateAnimation(
                                    offSet * 2 + bmWidth, 4 * offSet + 2 * bmWidth,
                                    0, 0);
                        }
                        ((PublicCamFragment)(fragments.get(2))).updateData();
                }
                currentItem = arg0;
                animation.setDuration(150);
                animation.setFillAfter(true);
                imgView.startAnimation(animation);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        }
    }

    @OnClick(value = {R.id.recommend_cam, R.id.hot_cam, R.id.new_cam})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.recommend_cam:

                viewPager.setCurrentItem(PubCamType.RECOMED_CAM);
                ((PublicCamFragment)(fragments.get(0))).updateData();
                break;
            case R.id.hot_cam:

                viewPager.setCurrentItem(PubCamType.HOT_CAM);
                ((PublicCamFragment)(fragments.get(1))).updateData();
                break;
            case R.id.new_cam:

                viewPager.setCurrentItem(PubCamType.NEW_CAM);
                ((PublicCamFragment)(fragments.get(2))).updateData();
                break;
        }
    }

    private void initeCursor() {
        cursor = BitmapFactory.decodeResource(getResources(), R.drawable.public_blue_line);
        bmWidth = cursor.getWidth();
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        offSet = (dm.widthPixels - 3 * bmWidth) / 6;
        matrix.setTranslate(offSet, 0);
//        imgView.setImageMatrix(matrix);
        currentItem = 0;
    }

}
