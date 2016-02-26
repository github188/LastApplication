package com.iermu.ui.fragment.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.config.PathConfig;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.adapter.RecordDateGridAdapter;
import com.iermu.ui.adapter.RecordListAdapter;
import com.iermu.ui.util.BitmapUtil;
import com.iermu.ui.view.PublicCamStatusView;
import com.iermu.ui.view.timeline.HorizontalListView;
import com.lingyang.sdk.api.IPlayer;
import com.lingyang.sdk.api.VideoPlayerView;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;

import java.util.Date;

/**
 * Created by zhangxq on 15/7/21.
 */
public class LyyRecordLiveControllerView extends RelativeLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private Context context;
    @ViewInject(R.id.textViewControllerTitle)
    TextView textViewControllerTitle;
    @ViewInject(R.id.viewLanscapeControl)
    View viewLanscapeControl;
    @ViewInject(R.id.buttonOutFullScreen)
    ImageButton buttonOutFullScreen;
    @ViewInject(R.id.buttonVoice)
    ImageButton buttonVoice;
    @ViewInject(R.id.buttonPlayCtrl)
    ImageButton buttonPlayCtrl;
    @ViewInject(R.id.buttonPrintScreenCtrl)
    ImageButton buttonPrintScreenCtrl;
    //    @ViewInject(R.id.listViewTimeCtrl)
    HorizontalListView listViewTimeCtrl;
    @ViewInject(R.id.buttonSelectDateCtrl)
    View buttonSelectDateCtrl;
    @ViewInject(R.id.textViewSelectDate)
    TextView textViewSelectDate;
    @ViewInject(R.id.gridViewDatesCtrl)
    GridView gridViewDatesCtrl;
    @ViewInject(R.id.viewGridViewDate)
    View viewGridViewDate;

    @ViewInject(R.id.viewStatus)
    PublicCamStatusView viewStatus;

    private boolean isShowGridViewDate = false;
    private boolean isPlaying = true;
    private boolean isLandscape = false;
    private boolean isShowController = false;
    private boolean isVoiceOn = true;

    private VideoPlayerView videoView;
    private int videoMargin = 0;
    private View viewTop; // 视频区域view，包含控制器
    private int height; // 计算出来的控制器高度
    private int cutNum = 0;

    @ViewInject(R.id.viewCut)
    View viewCut;
    @ViewInject(R.id.imageViewCut)
    ImageView imageViewCut;

    public void setViewTop(View viewTop) {
        this.viewTop = viewTop;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setVideoMargin(int videoMargin) {
        this.videoMargin = videoMargin;
    }

    public void setVideoView(VideoPlayerView videoView) {
        this.videoView = videoView;
    }

    public void setHorizontalAdapter(RecordListAdapter adapter) {
        listViewTimeCtrl.setAdapter(adapter);
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

    public LyyRecordLiveControllerView(Context context) {
        super(context);
    }

    public LyyRecordLiveControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_record_live_controller, this);
        ViewHelper.inject(this, view);

        buttonOutFullScreen.setOnClickListener(this);
        buttonVoice.setOnClickListener(this);
        buttonPlayCtrl.setOnClickListener(this);
        listViewTimeCtrl.setOnItemClickListener(this);
        gridViewDatesCtrl.setOnItemClickListener(this);
        buttonSelectDateCtrl.setOnClickListener(this);
//        buttonGotoLiveCtrl.setOnClickListener(this);
        buttonPrintScreenCtrl.setOnClickListener(this);
        viewCut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCloudCtrl:
                if (listener != null) {
                    listener.onDeleteClick(this);
                }
                break;
            case R.id.buttonVoice:
                if (listener != null) {
                    listener.onSoundClick(this);
                }
                changeVoiceState();
                break;
            case R.id.buttonOutFullScreen:
                if (listener != null) {
                    listener.onOutFullScreenClick(this);
                }
                isLandscape = false;
                isShowController = false;
                break;
            case R.id.buttonPlayCtrl:
                if (listener != null) {
                    listener.onPlayClick(this);
                }
                break;
            case R.id.buttonPrintScreenCtrl:
                if (listener != null) {
                    listener.onPrintScreenClick(this);
                }
                break;
            case R.id.buttonSelectDateCtrl:
                viewGridViewDate.setVisibility(View.VISIBLE);
                viewLanscapeControl.setVisibility(View.GONE);
                isShowGridViewDate = true;
                isShowController = false;
                break;
            case R.id.viewCut:
                if (listener != null) {
                    listener.onViewCutClick(this);
                }
            default:
                break;
        }
    }

    /**
     * 设置控制器标题
     *
     * @param title
     */
    public void setTitle(String title) {
        textViewControllerTitle.setText(title);
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

    public void changeLanscapeControlHideState() {
        if (isLandscape()) {
            if (isShowGridViewDate()) {
                hideViewGridViewDate();
                setIsShowGridViewDate(false);
            } else {
                if (isShowController) {
                    viewLanscapeControl.setVisibility(View.GONE);
                } else {
                    viewLanscapeControl.setVisibility(View.VISIBLE);
                }
                isShowController = !isShowController;
            }
        }
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
        IPlayer iplayer = videoView.getPlayer();
        String imageName = new Date().getTime() + "";// + ".png"
        FileUtil.createDipPath(PathConfig.CACHE_SHARE + "/");
        iplayer.snapshot(PathConfig.CACHE_SHARE, imageName, new IPlayer.OnSnapshotListener() {
            @Override
            public void onSnapshotSuccess(String saveFullPath) {
                Logger.e("截图成功  " + saveFullPath);
                if (listener != null) {
                    Bitmap bitmap = BitmapUtil.getSmallBitmap(saveFullPath);
                    listener.onPrintScreenComplete(LyyRecordLiveControllerView.this, saveFullPath, bitmap);
                }
            }

            @Override
            public void onSnapshotFail(int i) {
                Logger.e("截图失败 " + i);
            }
        });
//        final Bitmap bitmap = videoView.takePicture();
//        if (bitmap == null) {
//            return;
//        }
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//
//                String imagePath = ShareUtil.saveBitmap(bitmap);
//                Bitmap smallBitMap = ShareUtil.compressImage(bitmap, 20);
//                bitmap.recycle();
//                if (listener != null) {
//                    listener.onPrintScreenComplete(LyyRecordLiveControllerView.this, imagePath, smallBitMap);
//                }
//            }
//        });
    }

    public void setImage(Bitmap bitmap) {
        cutNum++;
        Logger.d("cutNum:" + cutNum);
        viewCut.setVisibility(View.VISIBLE);
        imageViewCut.setImageBitmap(bitmap);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                cutNum--;
                if (cutNum == 0) {
                    viewCut.setVisibility(View.GONE);
                }
            }
        }, 4000);
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
                    listener.onCutImageHide(LyyRecordLiveControllerView.this);
                }
            }
        }, 4000);
    }

    /**
     * 处理转横屏时的操作
     */
    public void changeToLandscape() {
        viewLanscapeControl.setVisibility(View.GONE);
        isLandscape = true;
        isShowController = false;

        // 设置播放器的布局为全屏
        LayoutParams layoutParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        viewTop.setLayoutParams(layoutParams);
        this.setLayoutParams(layoutParams);

        LayoutParams paramsVideoView = (LayoutParams) videoView.getLayoutParams();
        paramsVideoView.setMargins(0, 0, 0, 0);
    }

    /**
     * 处理转竖屏时的操作
     */
    public void changeToPortrait() {
        viewLanscapeControl.setVisibility(View.INVISIBLE);
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

        LayoutParams paramsVideoView1 = (LayoutParams) videoView.getLayoutParams();
        paramsVideoView1.setMargins(-videoMargin, 0, -videoMargin, 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (parent.getId() == R.id.listViewTimeCtrl) {
//            if (listener != null) {
//                listener.onRecordItemClick(position, this);
//            }
//        } else
        if (parent.getId() == R.id.gridViewDatesCtrl) {
            if (listener != null) {
                listener.onDateItemClick(position, this);
            }
        }
    }

    /**
     * 设置滚动的位置
     *
     * @param position
     */
    public void scrollToPosition(int position) {
        listViewTimeCtrl.setSelection(position);
    }

    /**
     * 设置选择日期按钮文字
     *
     * @param date
     */
    public void setDate(String date) {
        textViewSelectDate.setText(date);
    }

    /**
     * 隐藏日期选择面板
     */
    public void hideViewGridViewDate() {
        viewGridViewDate.setVisibility(View.GONE);
        isShowGridViewDate = false;
    }

    /**
     * 控制器事件回调接口
     */
    public interface OnControlListener {

        // 声音开关
        void onSoundClick(LyyRecordLiveControllerView view);

        // 退出全屏
        void onOutFullScreenClick(LyyRecordLiveControllerView view);

        // 点击云台
        void onDeleteClick(LyyRecordLiveControllerView view);

        // 点击播放
        void onPlayClick(LyyRecordLiveControllerView view);

        // 点击一个列表上的录像
        void onRecordItemClick(int position, LyyRecordLiveControllerView view);

        // 点击gridView上的一个日期
        void onDateItemClick(int position, LyyRecordLiveControllerView view);

        // 点击看直播
        void onGotoLiveClick(LyyRecordLiveControllerView view);

        // 点击截屏
        void onPrintScreenClick(LyyRecordLiveControllerView view);

        // 截图隐藏时调用
        void onCutImageHide(LyyRecordLiveControllerView view);

        // 点击截图图片
        void onViewCutClick(LyyRecordLiveControllerView view);

        // 完成截图时调用
        void onPrintScreenComplete(LyyRecordLiveControllerView view, String imagePath, Bitmap bitmap);
    }
}
