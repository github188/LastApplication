package com.iermu.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cms.media.widget.VideoView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.config.PathConfig;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.RecordDateGridAdapter;
import com.iermu.ui.util.ShareUtil;
import com.iermu.ui.view.timeline.HorizontalListView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Date;

/**
 * Created by zhangxq on 15/7/21.
 */
public class CardRecordLiveControllerView extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener, HorizontalListView.OnScrollListener, View.OnTouchListener {
    private Context context;
    @ViewInject(R.id.textViewControllerTitle)
    TextView textViewControllerTitle;
    @ViewInject(R.id.linearLayoutTop)
    View linearLayoutTop;
    @ViewInject(R.id.linearLayoutBottom)
    View linearLayoutBottom;
    @ViewInject(R.id.buttonPrintScreenCtrl)
    ImageButton buttonPrintScreenCtrl;
    @ViewInject(R.id.buttonOutFullScreen)
    ImageButton buttonOutFullScreen;
    @ViewInject(R.id.buttonVoice)
    ImageButton buttonVoice;
    @ViewInject(R.id.buttonPlayCtrl)
    ImageButton buttonPlayCtrl;
    @ViewInject(R.id.viewHorizontalCtrl)
    LinearLayout viewHorizontalCtrl;
    @ViewInject(R.id.viewSelectDateCtrl)
    View viewSelectDateCtrl;
    @ViewInject(R.id.textViewSelectDateCtrl)
    TextView textViewSelectDateCtrl;
    @ViewInject(R.id.gridViewDatesCtrl)
    GridView gridViewDatesCtrl;
    @ViewInject(R.id.viewGridViewDate)
    View viewGridViewDate;
    @ViewInject(R.id.imageViewIndicatorBar)
    ImageView imageViewIndicatorBar;
    @ViewInject(R.id.imageViewIndicatorCircle)
    ImageView imageViewIndicatorCircle;
    @ViewInject(R.id.textViewToast)
    TextView textViewToast;
    @ViewInject(R.id.viewIndicator)
    View viewIndicator;
    @ViewInject(R.id.viewStatus)
    PublicCamStatusView viewStatus;
    @ViewInject(R.id.viewCut)
    View viewCut;
    @ViewInject(R.id.imageViewCut)
    ImageView imageViewCut;
    @ViewInject(R.id.textViewCut)
    TextView textViewCut;
    @ViewInject(R.id.textViewCurrentTime)
    TextView textViewCurrentTime;
    @ViewInject(R.id.imageViewTimeRedDot)
    ImageView imageViewTimeRedDot;

    private boolean isShowGridViewDate = false;
    private boolean isPlaying = true;
    private boolean isLandscape = false;
    private boolean isShowController = false;
    private boolean isVoiceOn = true;
    private boolean isChangeLanscapHidden = false;
    private boolean isIndicatorShow = false;

    private VideoView videoView;
    private View viewTop; // 视频区域view，包含控制器
    private int height; // 计算出来的控制器高度

    // 画面移动有关参数
    private int marginLeft;
    private int touchX;
    private int offsetx;

    // 广角指示器移动有关参数
    private int indicatorWidth = 0;
    private int circleOffsetx;

    // 照相声音播放
    MediaPlayer mediaPlayer;

    public void setViewTop(View viewTop) {
        this.viewTop = viewTop;
    }

    public void setVideoHeight(int height) {
        this.height = height;
        Logger.d("height:" + height);
    }

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
    }

    public void setHorizontalView(View view) {
        view.setOnTouchListener(this);
        viewHorizontalCtrl.addView(view);
    }

    public void removeHorizontalView(View view) {
        viewHorizontalCtrl.removeView(view);
    }

    public void setGridViewDateAdapter(RecordDateGridAdapter adapter) {
        gridViewDatesCtrl.setAdapter(adapter);
    }

    private OnControlListener listener;

    public void setOnControlListener(OnControlListener listener) {
        this.listener = listener;
    }

    public PublicCamStatusView getViewStatus() {
        return viewStatus;
    }

    public CardRecordLiveControllerView(Context context) {
        super(context);
    }

    public CardRecordLiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_card_record_live_controller, this);
        ViewHelper.inject(this, view);

        buttonOutFullScreen.setOnClickListener(this);
        buttonVoice.setOnClickListener(this);
        buttonPrintScreenCtrl.setOnClickListener(this);
        buttonPlayCtrl.setOnClickListener(this);
        gridViewDatesCtrl.setOnItemClickListener(this);
        viewCut.setOnClickListener(this);
        viewSelectDateCtrl.setOnClickListener(this);

        mediaPlayer = MediaPlayer.create(context, R.raw.music_image_cut_);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonVoice:
                changeVoiceState();
                if (listener != null) {
                    listener.onSoundClick();
                }
                delayHideCtrl();
                break;
            case R.id.buttonOutFullScreen:
                if (listener != null) {
                    listener.onOutFullScreenClick();
                }
                break;
            case R.id.buttonPlayCtrl:
                if (listener != null) {
                    listener.onPlayClick();
                }
                delayHideCtrl();
                break;
            case R.id.buttonPrintScreenCtrl:
                if (listener != null) {
                    listener.onPrintScreenClick();
                }
                delayHideCtrl();
                break;
            case R.id.viewSelectDateCtrl:
                viewGridViewDate.setVisibility(View.VISIBLE);
                isShowGridViewDate = true;
                changeLanscapeControlHideState();
                break;
            case R.id.viewCut:
                if (listener != null) {
                    listener.onViewCutClick();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 处理转横屏时的操作
     */
    public void changeToLandscape() {
        videoView.setTranslationX(0);
        textViewCut.setVisibility(View.GONE);
        viewIndicator.setVisibility(View.GONE);
        showLanscapeCtrl(false);
        isLandscape = true;
        isShowController = false;
        changeLanscapeControlHideState();

        // 设置播放器的布局为全屏
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);
        this.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params = (LayoutParams) textViewToast.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);

        LayoutParams paramsVideoView = (LayoutParams) videoView.getLayoutParams();
        paramsVideoView.setMargins(0, 0, 0, 0);
    }

    /**
     * 处理转竖屏时的操作
     */
    public void changeToPortrait() {
        hideViewGridViewDate();
        videoView.setTranslationX(offsetx);
        textViewCut.setVisibility(View.VISIBLE);
        if (isIndicatorShow) {
            viewIndicator.setVisibility(View.VISIBLE);
        }
        hideLanscapeCtrl(false);
        isLandscape = false;
        isShowController = false;

        // 设置播放器的布局，退出全屏
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, height);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);
        this.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams params = (LayoutParams) textViewToast.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);

        LayoutParams paramsVideoView1 = (LayoutParams) videoView.getLayoutParams();
        paramsVideoView1.setMargins(-marginLeft, 0, -marginLeft, 0);
    }

    /**
     * 设置控制器标题
     *
     * @param res
     */
    public void setTitle(int res) {
        textViewControllerTitle.setText(res);
    }

    public boolean isVoiceOn() {
        return isVoiceOn;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public boolean isShowGridViewDate() {
        return isShowGridViewDate;
    }

    public void setIsShowGridViewDate(boolean isShowGridViewDate) {
        this.isShowGridViewDate = isShowGridViewDate;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public void setButtonPlayEnable(boolean isEnable) {
        buttonPlayCtrl.setEnabled(isEnable);
    }

    private int delayNum = 0;

    public void changeLanscapeControlHideState() {
        if (isLandscape) {
            if (isShowController) {
                isChangeLanscapHidden = true;
                hideLanscapeCtrl(true);
            } else {
                isChangeLanscapHidden = false;
                showLanscapeCtrl(true);
                delayHideCtrl();
            }
            isShowController = !isShowController;
        }
    }

    private void delayHideCtrl() {
        delayNum++;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 过3秒如果没有隐藏，则自动隐藏
                delayNum--;
                if (!isChangeLanscapHidden && delayNum == 0) {
                    hideLanscapeCtrl(true);
                    isShowController = false;
                }
            }
        }, 3000);
    }

    /**
     * 修改声音状态
     */
    public void changeVoiceState() {
        if (isVoiceOn) {
            buttonVoice.setSelected(true);
        } else {
            buttonVoice.setSelected(false);
        }
        isVoiceOn = !isVoiceOn;
    }

    /**
     * 设置声音的禁用状态
     *
     * @param isEnable
     */
    public void setButtonVoiceEnable(boolean isEnable) {
        buttonVoice.setEnabled(isEnable);
    }

    public void changePlayStateButton() {
        if (isPlaying) {
            buttonPlayCtrl.setSelected(false);
        } else {
            buttonPlayCtrl.setSelected(true);
        }
    }

    /**
     * 设置截屏按钮的禁用
     *
     * @param isEnabled
     */
    public void setButtonPrintScreenEnabled(boolean isEnabled) {
        buttonPrintScreenCtrl.setEnabled(isEnabled);
    }

    /**
     * 截图
     */
    public void printScreen() {
        mediaPlayer.start();
        final Bitmap bitmap = videoView.takePicture();
        if (bitmap == null) {
            return;
        }
        Bitmap smallBitMap = ShareUtil.compressImage(bitmap, 20);
        final long imageTime = new Date().getTime();
        if (listener != null) {
            listener.onPrintScreenComplete(smallBitMap, imageTime);
        }
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSaveScreenImage(bitmap, imageTime);
                }
            }
        });
    }

    /**
     * 显示截图
     *
     * @param bitmap
     */
    private int cutImageNum = 0;

    public void setImage(Bitmap bitmap) {
        viewCut.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.BounceIn).duration(300).playOn(viewCut);
        cutImageNum++;
        imageViewCut.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutImageNum--;
                if (cutImageNum == 0) {
                    viewCut.setVisibility(View.GONE);
                    ShareUtil.getBitmapShare().recycle();
                }
            }
        }, 3000);
    }

    /**
     * 显示截屏保存toast
     */
    private int cutNum = 0;

    public void imageCutToast() {
        cutNum++;
        Logger.d("cutNum:" + cutNum);
        textViewToast.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutNum--;
                if (cutNum == 0) {
                    toast(getResources().getString(R.string.save_screen));//"截屏已保存到" + PathConfig.CACHE_SHARE
                }
            }
        }, 3000);
    }

    /**
     * 显示截图
     *
     * @param bitmap
     */
    public void setImageCut(Bitmap bitmap) {
        viewCut.setVisibility(View.VISIBLE);
        imageViewCut.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                viewCut.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onCutImageHide();
                }
            }
        }, 4000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.gridViewDatesCtrl) {
            if (listener != null) {
                listener.onDateItemClick(position);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AdapterView<?> view, int status) {

    }

    @Override
    public void onScroll(AdapterView<?> view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        delayHideCtrl();
    }

    /**
     * 设置选择日期按钮文字
     *
     * @param date
     */
    public void setDate(String date) {
        textViewSelectDateCtrl.setText(date);
    }

    /**
     * 隐藏日期选择面板
     */
    public void hideViewGridViewDate() {
        viewGridViewDate.setVisibility(View.GONE);
        isShowGridViewDate = false;
    }

    /**
     * 隐藏横向控制器
     *
     * @param isAnimate
     */
    public void hideLanscapeCtrl(boolean isAnimate) {
        if (isAnimate) {
            YoYo.with(Techniques.SlideOutUp).duration(300).playOn(linearLayoutTop);
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(linearLayoutBottom);
        } else {
            linearLayoutTop.setVisibility(View.GONE);
            linearLayoutBottom.setVisibility(View.GONE);
        }
    }

    /**
     * 显示横向控制器
     *
     * @param isAnimate
     */
    public void showLanscapeCtrl(boolean isAnimate) {
        if (isAnimate) {
            YoYo.with(Techniques.SlideInDown).duration(300).playOn(linearLayoutTop);
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(linearLayoutBottom);
        } else {
            linearLayoutTop.setVisibility(View.VISIBLE);
            linearLayoutBottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置左右移动空间
     */
    public void setLeftAndRightMergin(int marginLeft) {
        this.marginLeft = marginLeft;
        LayoutParams paramsVideoView = (LayoutParams) videoView.getLayoutParams();
        paramsVideoView.setMargins(-marginLeft, 0, -marginLeft, 0);

        // 计算广角指示器宽度;
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        imageViewIndicatorBar.measure(w, h);
        indicatorWidth = imageViewIndicatorBar.getMeasuredWidth();
    }

    /**
     * 处理触摸事件
     */
    public void onTopTouch(View view, MotionEvent event) {
        if (isLandscape) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (isShowGridViewDate) {
                    hideViewGridViewDate();
                } else {
                    if (view.getId() == R.id.listViewTime || view.getId() == R.id.timeLineView) {
                        delayHideCtrl();
                    } else {
                        changeLanscapeControlHideState();
                    }
                }
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchX = (int) event.getRawX();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                int dx = (int) event.getRawX() - touchX;

                offsetx = offsetx + dx;
                if (offsetx > marginLeft) {
                    offsetx = marginLeft;
                } else if (offsetx < -marginLeft) {
                    offsetx = -marginLeft;
                }
                videoView.setTranslationX(offsetx);
                if (indicatorWidth != 0) {
                    circleOffsetx = -offsetx * indicatorWidth / (marginLeft * 2);
                    imageViewIndicatorCircle.setTranslationX(circleOffsetx);
                }
                touchX = (int) event.getRawX();
            }
        }
    }

    /**
     * 横向滚动条触摸事件
     *
     * @param view
     * @param event
     */
    public void onScrollViewTouch(View view, MotionEvent event) {
        if (isLandscape) {
            if (isShowGridViewDate) {
                hideViewGridViewDate();
            } else {
                if (view.getId() == R.id.listViewTime || view.getId() == R.id.timeLineView) {
                    delayHideCtrl();
                } else {
                    changeLanscapeControlHideState();
                }
            }
        }
    }

    /**
     * 自定义toast和相关参数
     */
    private int toastNum;

    public void toast(String content) {
        toastNum++;
        textViewToast.setText(content);
        textViewToast.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toastNum--;
                if (toastNum == 0) {
                    textViewToast.setVisibility(View.GONE);
                }
            }
        }, 3000);
    }

    /**
     * 设置广角指示器的显示或隐藏
     *
     * @param isShow
     */
    public void setIsIndicatorShow(boolean isShow) {
        isIndicatorShow = isShow;
        if (isShow) {
            if (!isLandscape) {
                viewIndicator.setVisibility(View.VISIBLE);
            }
        } else {
            viewIndicator.setVisibility(View.GONE);
        }
    }

    /**
     * listViewTime滚动时调用
     */
    public void onListViewTimeScroll() {
        if (isLandscape) {
            delayHideCtrl();
        }
    }

    /**
     * 设置当前播放时间
     *
     * @param currentTime
     */
    public void setCurrentTime(String currentTime, boolean isOnScroll) {
        if (!isOnScroll) {
            if (imageViewTimeRedDot.getVisibility() == View.INVISIBLE) {
                imageViewTimeRedDot.setVisibility(View.VISIBLE);
            } else {
                imageViewTimeRedDot.setVisibility(View.INVISIBLE);
            }
        }
        textViewCurrentTime.setText("存储卡录像 " + currentTime);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isLandscape) {
            if (v.getId() == R.id.viewTimeLineParent) {
                delayHideCtrl();
            } else {
                changeLanscapeControlHideState();
            }
        }
        return false;
    }

    /**
     * 控制器事件回调接口
     */
    public interface OnControlListener {

        // 声音开关
        void onSoundClick();

        // 退出全屏
        void onOutFullScreenClick();

        // 点击播放
        void onPlayClick();

        // 点击gridView上的一个日期
        void onDateItemClick(int position);

        // 点击截屏
        void onPrintScreenClick();

        // 截图隐藏时调用
        void onCutImageHide();

        // 点击截图图片
        void onViewCutClick();

        // 完成截图时调用
        void onPrintScreenComplete(Bitmap bitmap, long imageTime);

        // 保存截图完成时调用
        void onSaveScreenImage(Bitmap bitmap, long imageTime);
    }
}
