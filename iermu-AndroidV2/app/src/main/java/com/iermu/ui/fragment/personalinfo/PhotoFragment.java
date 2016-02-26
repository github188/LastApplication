package com.iermu.ui.fragment.personalinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.config.PathConfig;
import com.iermu.client.listener.OnUserInfoListener;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.ui.adapter.ViewPagerAdapter;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.ViewPagerScroll;
import com.squareup.picasso.Picasso;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhoushaopei on 16/1/5.
 */
public class PhotoFragment extends BaseFragment implements OnUserInfoListener, PictureFragment.OnListScrollChangeListener {

    private static String TYPE = "photo_type";

    @ViewInject(R.id.del_btn)           ImageButton mDelButton;
    @ViewInject(R.id.undel_btn)         ImageButton mUnDelButton;
    @ViewInject(R.id.title)             TextView mTitle;
    @ViewInject(R.id.view_pager)        ViewPagerScroll viewPager;
    @ViewInject(R.id.imgview)           LinearLayout imgView;
    @ViewInject(R.id.avator_img)        CircleImageView mAvatar;
    @ViewInject(R.id.nick_name)         TextView mNickName;
    @ViewInject(R.id.have_photo)        TextView mHavePhoto;
    @ViewInject(R.id.all_txt)           TextView mAllTxt;
    @ViewInject(R.id.print_screen_txt)  TextView mPhotoTxt;
    @ViewInject(R.id.film_edit_txt)     TextView mFilmEditTxt;
    @ViewInject(R.id.linearLayout)      RelativeLayout linearLayout;
    @ViewInject(R.id.photo_info)        LinearLayout mPhotoInfo;
    @ViewInject(R.id.all_txt)           TextView mAllText;

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private ViewPagerAdapter adapter;
    private int offSet;
    private int currentItem;
    private Matrix matrix = new Matrix();
    private int bmWidth;
    private Animation animation;
    private Bitmap cursor;
    private static Context context;
    private int currentType = -1;


    public static Fragment actionInstance(FragmentActivity activity, int type){
        PhotoFragment fragment = new PhotoFragment();
        context = activity;
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCustomActionBar(R.layout.actionbar_alarm_message);
    }

    @Override
    public void onActionBarCreated(View view) {
        ViewHelper.inject(this, view);
        mTitle.setText(getString(R.string.my_phone));
        mTitle.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_fragment, container, false);
        ViewHelper.inject(this, view);

        initView();
        initViewPager(inflater);
        return view;
    }

    private void initView() {
        mAllText.setTextColor(getResources().getColor(R.color.devices_help_btn_bg));
        getPhotoNum();
        ErmuBusiness.getAccountAuthBusiness().getUserInfo();
        ErmuBusiness.getAccountAuthBusiness().addUserInfoListener(this);
        mTitle.setTag(-1);
    }

    private void initViewPager(LayoutInflater inflater) {
        Bundle arguments = getArguments();
        currentType = arguments.getInt(TYPE);
        PictureFragment fragment0 = (PictureFragment) PictureFragment.actionInstance(getActivity(), PhotoType.ALL);
        PictureFragment fragment1 = (PictureFragment) PictureFragment.actionInstance(getActivity(), PhotoType.PHOTO);
        PictureFragment fragment2 = (PictureFragment) PictureFragment.actionInstance(getActivity(), PhotoType.FILM_EDIT);
        fragment0.setListScrollListener(this);
        fragment1.setListScrollListener(this);
        fragment2.setListScrollListener(this);
        fragments.add(fragment0);
        fragments.add(fragment1);
        fragments.add(fragment2);

        initeCursor();
        adapter = new ViewPagerAdapter(getChildFragmentManager(),fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size() - 1);
        viewPager.setOnPageChangeListener(new PageChangeListener());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            getPhotoNum();
            int item = viewPager.getCurrentItem();
            if (item == PhotoType.ALL) {
                ((PictureFragment)(fragments.get(0))).updateData(PhotoType.ALL);
            } else if (item == PhotoType.PHOTO) {
                ((PictureFragment)(fragments.get(1))).updateData(PhotoType.PHOTO);
            } else if (item == PhotoType.FILM_EDIT) {
                ((PictureFragment)(fragments.get(2))).updateData(PhotoType.FILM_EDIT);
            }
        }
        super.onHiddenChanged(hidden);
    }

    @OnClick(value = {R.id.back, R.id.all_txt, R.id.print_screen_txt, R.id.film_edit_txt, R.id.del_btn, R.id.undel_btn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                popBackStack();
                break;
            case R.id.all_txt:
                viewPager.setCurrentItem(PhotoType.ALL);
                ((PictureFragment)(fragments.get(0))).updateData(PhotoType.ALL);
                break;
            case R.id.print_screen_txt:
                viewPager.setCurrentItem(PhotoType.PHOTO);
                ((PictureFragment)(fragments.get(1))).updateData(PhotoType.PHOTO);
                break;
            case R.id.film_edit_txt:
                viewPager.setCurrentItem(PhotoType.FILM_EDIT);
                ((PictureFragment)(fragments.get(2))).updateData(PhotoType.FILM_EDIT);
                break;
            case R.id.del_btn:
                showDeleteLay();
                break;
            case R.id.undel_btn:
                hideDeleteLay();
                break;
        }
    }

    private void showDeleteLay() {
        int count = adapter.getCount();
        if (count > 0) {
            mUnDelButton.setVisibility(View.VISIBLE);
            mDelButton.setVisibility(View.GONE);
            int currentItem1 = viewPager.getCurrentItem();
            ((PictureFragment)(fragments.get(currentItem1))).showDeleteLay();
            linearLayout.setVisibility(View.GONE);
            viewPager.setScrollble(false);
        }
    }

    private void hideDeleteLay() {
        mUnDelButton.setVisibility(View.GONE);
        mDelButton.setVisibility(View.VISIBLE);
        int currentItem1 = viewPager.getCurrentItem();
        ((PictureFragment)(fragments.get(currentItem1))).hideDeleteLay();
        linearLayout.setVisibility(View.VISIBLE);
        viewPager.setScrollble(true);
    }

    @Override
    public void onListScrollChange(int offSet, boolean isAnimation) {
        if (mTitle.getTag() == null) return;
        int tag = (Integer)mTitle.getTag();
        if (offSet > 0 && isAnimation) {//1:title消失     info 显示
            if (tag == 1) return;
            mTitle.setTag(1);
            fadeIn(offSet, mPhotoInfo);
            fadeOut(offSet, mTitle);
            mPhotoInfo.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.INVISIBLE);
        } else if (offSet < 0 && isAnimation) {//2:title 显示   info 消失
            if (tag == 0) return;
            mTitle.setTag(0);
            fadeIn(Math.abs(offSet), mTitle);
            fadeOut(Math.abs(offSet), mPhotoInfo);
            mPhotoInfo.setVisibility(View.GONE);
            mTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDataChange() {
        getPhotoNum();
        hideDeleteLay();
    }

    @Override
    public void onDeleteVisible(boolean disable) {
        mDelButton.setVisibility(disable == true ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onSetCurrentFragment() {
        viewPager.setCurrentItem(currentType);
        if (currentType == PhotoType.ALL) {
            ((PictureFragment)(fragments.get(0))).updateData(PhotoType.ALL);
        }
    }

    class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    mAllTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                    mPhotoTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mFilmEditTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mAllTxt.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                    mPhotoTxt.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                    mFilmEditTxt.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                    if (currentItem == 1) {
                        animation = new TranslateAnimation(
                                offSet * 2 + bmWidth, 0, 0, 0);
                    } else if (currentItem == 2) {
                        animation = new TranslateAnimation(offSet * 4 + 2
                                * bmWidth, 0, 0, 0);
                    }
                    ((PictureFragment)(fragments.get(0))).updateData(PhotoType.ALL);
                    break;
                case 1:
                    mAllTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mPhotoTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                    mFilmEditTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mAllTxt.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                    mPhotoTxt.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                    mFilmEditTxt.setTextColor(getResources().getColor(R.color.devices_help_btn));
                    if (currentItem == 0) {
                        animation = new TranslateAnimation(0, offSet * 2
                                + bmWidth, 0, 0);
                    } else if (currentItem == 2) {
                        animation = new TranslateAnimation(4 * offSet + 2
                                * bmWidth, offSet * 2 + bmWidth, 0, 0);
                    }
                    ((PictureFragment)(fragments.get(1))).updateData(PhotoType.PHOTO);
                    break;
                case 2:
                    mAllTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mPhotoTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_three));
                    mFilmEditTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimensionPixelSize(R.dimen.activity_text_size_one));
                    mAllTxt.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                    mPhotoTxt.setTextColor(getResources().getColor(R.color.devices_help_btn) );
                    mFilmEditTxt.setTextColor(getResources().getColor(R.color.devices_help_btn_bg) );
                    if (currentItem == 0) {
                        animation = new TranslateAnimation(0, 4 * offSet + 2
                                * bmWidth, 0, 0);
                    } else if (currentItem == 1) {
                        animation = new TranslateAnimation(
                                offSet * 2 + bmWidth, 4 * offSet + 2 * bmWidth,
                                0, 0);
                    }
                    ((PictureFragment)(fragments.get(2))).updateData(PhotoType.FILM_EDIT);
            }
            currentItem = arg0;
//            animation.setDuration(150);
            animation.setFillAfter(true);
            imgView.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    private void initeCursor() {
        cursor = BitmapFactory.decodeResource(getResources(), R.drawable.public_blue_line);
        bmWidth = cursor.getWidth();
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        offSet = (dm.widthPixels - 3 * bmWidth) / 6;
        matrix.setTranslate(offSet, 0);
        currentItem = 0;
    }

    private void getPhotoNum() {
        List<ScreenClip> screens = ErmuBusiness.getUserCenterBusiness().getScreenClip(PhotoType.FILM_EDIT);
        File file = new File(PathConfig.CACHE_SHARE);
        File[] files = file.listFiles();
        String collection = "";
        int size = 0;
        if (files != null) {
            size = screens.size() + files.length;
            collection = String.format(getActivity().getString(R.string.have_phone), size);
        } else {
            size = 0 + screens.size();
            collection = String.format(getActivity().getString(R.string.have_phone), size);
        }
        int start = collection.indexOf(String.valueOf(size));
        String numLength = String.valueOf(size);
        int sizeLength = Integer.valueOf(numLength.length());
        SpannableString spannableString = new SpannableString(collection);
        spannableString.setSpan(new AbsoluteSizeSpan(24, true), start, start + sizeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mHavePhoto.setText(spannableString);
    }

    private void fadeOut(int i, View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(i);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(false);
        view.setAnimation(alphaAnimation);
    }

    private void fadeIn(int i, View view) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(i);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setFillBefore(false);
        view.setAnimation(alphaAnimation);
    }

    @Override
    public void onUserInfo(UserInfo info) {
        if (info == null) return;
        mNickName.setText(info.getUserName());
        Picasso.with(getActivity())
                .load(info.getAvatar())
                .resize(mAvatar.getWidth(), mAvatar.getHeight())
                .centerCrop()
                .placeholder(R.drawable.avator_img)
                .into(mAvatar);
    }
}
