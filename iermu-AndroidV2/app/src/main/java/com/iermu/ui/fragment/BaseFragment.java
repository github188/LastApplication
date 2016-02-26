package com.iermu.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.iermu.R;
import com.iermu.ui.activity.BaseActionBarActivity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

/**
 * Fragment基类
 * 1.管理Fragment堆栈
 * 2.公共ActionBar配置
 * 3.公共....
 * Created by wcy on 15/6/18.
 */
public abstract class BaseFragment extends Fragment implements /*FragmentManager.OnBackStackChangedListener, */FragmentActivityHelper {

    static Stack<Fragment> fragmentStack = new Stack<Fragment>();

    /**
     * 创建ActionBar回调函数
     *
     * @param fragment
     */
    protected abstract void onCreateActionBar(BaseFragment fragment);

    /**
     * ActionBar创建成功回调函数
     *
     * @param view ActionBar视图布局
     */
    public void onActionBarCreated(View view) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            onCreateOrBindActionBar();
        }
        //FragmentManager manager = getActivity().getSupportFragmentManager();
        //manager.addOnBackStackChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view != null) {
            view.setClickable(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            onCreateOrBindActionBar();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //FragmentManager manager = getActivity().getSupportFragmentManager();
        //manager.removeOnBackStackChangedListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //TODO 子类必须重载onHiddenChanged
        if (!hidden) {
            onCreateOrBindActionBar();
        }
    }

    //@Override
    public static void onBackStackChanged(FragmentActivity activity) {
        //FragmentActivity activity = getActivity();
        FragmentManager manager = activity.getSupportFragmentManager();
        int stackEntryCount = manager.getBackStackEntryCount();
        if (fragmentStack.empty() && stackEntryCount<=0) {
            List<Fragment> fragments = manager.getFragments();
            disableContentFragment(activity, false);
            for (Fragment fragment : fragments) {
                if (fragment != null && !fragment.isHidden()) {
                    fragment.onHiddenChanged(false);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//检测返回键事件
            boolean keyDown = emptyBackStack();//是否拦截事件
            this.popBackStack();
            return !keyDown;
        }
        return false;
    }

    /**
     * 处理返回键事件
     *
     * @param context
     * @param keyCode 事件码
     * @param event   事件
     * @return
     */
    public static boolean onKeyDown(FragmentActivity context, int keyCode, KeyEvent event) {
        if (context == null) {
            android.util.Log.e("", "Context is null.");
            return false;
        }
        FragmentManager manager = context.getSupportFragmentManager();
        if (manager == null) {
            android.util.Log.e("", "FragmentManager is null.");
            return false;
        }

        boolean keyDown = false;
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        if (lastElement != null && lastElement instanceof FragmentActivityHelper) {
            keyDown = ((FragmentActivityHelper) lastElement).onKeyDown(keyCode, event);
        }
        return keyDown;
    }

    /**
     * 判断当前Fragment是否在栈顶
     * @return
     */
    public boolean existBackStackTop() {
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        return lastElement!=null && lastElement.isAdded() && (lastElement.getTag().equals(this.getTag()));
    }

    /**
     * 将Fragment压栈
     *
     * @param context Context
     * @param target  目标Fragment
     */
    public static void addToBackStack(FragmentActivity context, Fragment target) {
        addToBackStack(context, target, false);
    }

    public static void addToBackStack(FragmentActivity context, Fragment target, boolean isAnimate) {
        String className = target.getClass().getName();
        addToBackStack(context, className, target, isAnimate);
    }

    /**
     * 清空Fragment堆栈
     *
     * @param context
     */
    public static void popBackAllStack(FragmentActivity context) {
        onPopBackAllStack(context);
    }

    /**
     * 清空Fragment堆栈
     */
    public void popBackAllStack() {
        onPopBackAllStack(getActivity());
    }

    public static void popBackStack(FragmentActivity context) {
        onPopBackStack(context);
    }

    public void popBackStack(Class sourceTarget) {
        onPopBackStack(getActivity(), sourceTarget);
    }

    /**
     * 回退到目标Fragment
     *
     * @param context
     * @param sourceTarget (该Fragment必须包含在堆栈中)
     */
    public static void popBackStack(FragmentActivity context, Class sourceTarget) {
        onPopBackStack(context, sourceTarget);
    }

    /**
     * 添加Fragment到堆栈
     *
     * @param target 目标Fragment
     */
    public void addToBackStack(Fragment target) {
        String className = target.getClass().getName();
        addToBackStack(getActivity(), className, target, false);
    }

    /**
     * 添加Fragment到堆栈
     *
     * @param target 目标Fragment
     */
    public void addToBackStack(Fragment target, boolean isAnimate) {
        String className = target.getClass().getName();
        addToBackStack(getActivity(), className, target, isAnimate);
    }

    //添加转场动画
    public void addToBackStack(Fragment target, boolean isSlideDownIn, boolean isSlideRightIn) {
        String className = target.getClass().getName();
        addToBackStack(getActivity(), className, target, isSlideDownIn, isSlideRightIn);
    }

    /**
     * 添加Fragment到堆栈 (清除当前显示的Fragment)
     *
     * @param target
     */
    public void addJumpToBackStack(Fragment target) {
        FragmentActivity context = getActivity();
        onPopBackStack(context);
        addToBackStack(target);
    }

    /**
     * 添加Fragment到堆栈 (清除当前显示的Fragment), 判断是否有入场动画
     *
     * @param target
     */
    public void addJumpToBackStack(Fragment target, boolean isAnimate) {
        FragmentActivity context = getActivity();
        onPopBackStack(context);
        addToBackStack(target, isAnimate);
    }

    /**
     * 添加Fragment到堆栈 (清除Fragment堆栈至源目标Fragment)
     *
     * @param jumpTarget
     * @param sourceTarget 源目标Fragment(该Fragment必须在堆栈中)
     */
    public void addJumpToBackStack(Fragment jumpTarget, Class sourceTarget) {
        FragmentActivity context = getActivity();
        onPopBackStack(context, sourceTarget);
        addToBackStack(jumpTarget);
    }

    /**
     * 添加Fragment到堆栈 (清除Fragment所有回退栈)
     *
     * @param target
     */
    public void addJumpToPopAllStack(Fragment target) {
        FragmentActivity context = getActivity();
        onPopBackAllStack(context);
        addToBackStack(target);
    }

    //TODO 需要重新优化
//    public void addSingleToBackStack(Fragment target) {
//        String fragmentTag = target.getClass().getName();
//        FragmentActivity context = getActivity();
//        disableFragmentView(true);
//        FragmentManager manager = context.getSupportFragmentManager();
//        Fragment tagFragment = manager.findFragmentByTag(fragmentTag);
//        if(tagFragment == null) {
//            manager.beginTransaction()
//                    .add(R.id.content_fragment, target, fragmentTag)
//                    .addToBackStack(fragmentTag).commit();
//            fragmentStack.add(target);
//        } else {
//            manager.beginTransaction()
//                    .show(tagFragment)
//                    /*.addToBackStack(fragmentTag)*/.commit();
//        }
//        disableContentFragment(context, true);
//    }

    /**
     * 添加Fragment到堆栈
     *
     * @param fragmentTag Tag标记
     * @param fragment    Fragment实例
     */
    private static void addToBackStack(FragmentActivity context, String fragmentTag, Fragment fragment, boolean isAnimate) {
        disableFragmentView(true);
        //TODO 新增代码 (处理上一个页面HiddenChanged事件)
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        if (lastElement != null && lastElement.isAdded()) {
            lastElement.onHiddenChanged(true);
        }
        FragmentManager manager = context.getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        if (fragmentStack.isEmpty() && fragments != null) { //TODO 处理默认首页Fragment onHiddenChanged事件
            for (Fragment f : fragments) {
                if (f != null) f.onHiddenChanged(true);
            }
        }

        if (isAnimate) {
            Fragment fragmentByTag = manager.findFragmentByTag(fragmentTag);

            manager.beginTransaction().setCustomAnimations(R.anim.slide_up_in, R.anim.slide_down_out)
                    .add(R.id.content_fragment, fragment, fragmentTag)
                    .addToBackStack(fragmentTag)
                    .commitAllowingStateLoss();
        } else {
            manager.beginTransaction().add(R.id.content_fragment, fragment, fragmentTag)
                    .addToBackStack(fragmentTag).commitAllowingStateLoss();
        }

        fragmentStack.add(fragment);
        disableContentFragment(context, true);
    }

    private static void addToBackStack(FragmentActivity context, String fragmentTag, Fragment fragment, boolean isAnimate, boolean isSlideRightIn) {
        disableFragmentView(true);
        //TODO 新增代码 (处理上一个页面HiddenChanged事件)
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        if (lastElement != null && lastElement.isAdded()) {
            lastElement.onHiddenChanged(true);
        }
        FragmentManager manager = context.getSupportFragmentManager();
        List<Fragment> fragments = manager.getFragments();
        if (fragmentStack.isEmpty() && fragments != null) { //TODO 处理默认首页Fragment onHiddenChanged事件
            for (Fragment f : fragments) {
                if (f != null) f.onHiddenChanged(true);
            }
        }

        if (isSlideRightIn) {
            manager.beginTransaction().setCustomAnimations(R.anim.slide_right_in, 0, 0, R.anim.slide_right_out)
                    .add(R.id.content_fragment, fragment, fragmentTag)
                    .addToBackStack(fragmentTag).commitAllowingStateLoss();
        } else {
            manager.beginTransaction().add(R.id.content_fragment, fragment, fragmentTag)
                    .addToBackStack(fragmentTag).commitAllowingStateLoss();
        }

        fragmentStack.add(fragment);
        disableContentFragment(context, true);
    }


    public void popBackStack() {
        onPopBackStack(getActivity());
    }

    /**
     * 弹出Fragment页面
     *
     * @param delayMillis
     */
    public void popBackStack(long delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPopBackStack(getActivity());
            }
        }, delayMillis);
    }

    private static void onPopBackAllStack(FragmentActivity context) {
        FragmentManager manager = context.getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (fragmentStack != null) {
            fragmentStack.clear();
        }
    }

    /**
     * 弹出Fragment堆栈
     *
     * @param context
     * @param popTarget 最终回退目标Fragment
     */
    private static void onPopBackStack(FragmentActivity context, Class popTarget) {
        if (context == null) {
            android.util.Log.e("", "Context is null.");
            return;
        }
        FragmentManager manager = context.getSupportFragmentManager();
        if (manager == null) {
            android.util.Log.e("", "FragmentManager is null.");
            return;
        }
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        while (lastElement != null && !(lastElement.getTag().equals(popTarget.getName()))) {
            if (!fragmentStack.empty()) {
                fragmentStack.pop();
            }
            manager.popBackStack();
            lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        }
        disableFragmentView(false);
        if (lastElement != null && lastElement.isAdded()) {
            lastElement.onHiddenChanged(false);
        }
    }

    /**
     * 弹出Fragment堆栈
     *
     * @param context
     */
    private static void onPopBackStack(FragmentActivity context) {
        if (context == null) {
            android.util.Log.e("", "Context is null.");
            return;
        }
        FragmentManager manager = context.getSupportFragmentManager();
        if (manager == null) {
            android.util.Log.e("", "FragmentManager is null.");
            return;
        }
        if (!fragmentStack.empty()) {
            fragmentStack.pop();
        }
        manager.popBackStack();
        disableFragmentView(false);
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        if (lastElement != null && lastElement.isAdded()) {
            lastElement.onHiddenChanged(false);
        }
    }

    private static void disableFragmentView(boolean disable) {
        Fragment lastElement = fragmentStack.size() > 0 ? fragmentStack.lastElement() : null;
        if (lastElement != null && lastElement.isAdded()) {
            View view = lastElement.getView();
            if (view != null) {
                view.setClickable(!disable);
            }
        }
    }

    /**
     * 检测Fragment回退堆栈是否为空
     *
     * @return
     */
    public boolean emptyBackStack() {
        return (fragmentStack == null) || fragmentStack.empty();
    }

    /**
     * 设置自定义ActionBar
     *
     * @param layoutRes 自定义View资源
     */
    public void setCustomActionBar(int layoutRes) {
        BaseActionBarActivity context = (BaseActionBarActivity) getActivity();
        ActionBar actionBar = context.getSupportActionBar();
        if (Build.VERSION.SDK_INT >= 21) {
            actionBar.setElevation(0);
        }
        //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(layoutRes, null, false);
        //ActionBar.LayoutParams params = new ActionBar.LayoutParams( android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT);
        //actionBar.setCustomView(view, params);
        actionBar.setCustomView(layoutRes);
        actionBar.setTitle("");
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setShowHideAnimationEnabled(false);
        actionBar.show();
    }

    /**
     * 设置公用ActionBar
     *
     * @param titleRes 标题资源ID
     * @return
     */
    public BaseFragment setCommonActionBar(int titleRes) {
        String string = getActivity().getResources().getString(titleRes);
        setCommonActionBar(string);
        return this;
    }

    /**
     * 设置公用ActionBar
     *
     * @param title 标题文本
     * @return
     */
    public BaseFragment setCommonActionBar(String title) {
        setCustomActionBar(R.layout.actionbar_common);
        TextView textView = (TextView) getActivity().findViewById(R.id.actionbar_title);
        getActivity().findViewById(R.id.actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        });
        textView.setText(title);
        return this;
    }

    /**
     * 设置隐藏公用Back按钮
     *
     * @return
     */
    public BaseFragment setDisableCommonBack() {
        getActivity().findViewById(R.id.actionbar_back).setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 设置公用Finish按钮禁用
     *
     * @return
     */
    public BaseFragment setCommonFinishDisabled() {
        getActivity().findViewById(R.id.actionbar_finish).setEnabled(false);
        return this;
    }

    /**
     * 设置公共finish按钮可用
     *
     * @return
     */
    public BaseFragment setCommonFinishEnbled() {
        getActivity().findViewById(R.id.actionbar_finish).setEnabled(true);
        return this;
    }

    /**
     * 设置隐藏公用Finish按钮
     *
     * @return
     */
    public BaseFragment setCommonFinishHided() {
        getActivity().findViewById(R.id.actionbar_finish).setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 显示actionBar Finish
     */
    public BaseFragment setCommonFinishShow() {
        getActivity().findViewById(R.id.actionbar_finish).setVisibility(View.VISIBLE);
        return this;
    }

    public BaseFragment setCommonTitle(int txtRes) {
        String string = getActivity().getResources().getString(txtRes);
        TextView textView = (TextView) getActivity().findViewById(R.id.actionbar_title);
        textView.setText(string);
        return this;
    }

    public BaseFragment setCommonTitle(String txt) {
        TextView textView = (TextView) getActivity().findViewById(R.id.actionbar_title);
        textView.setText(txt);
        return this;
    }

    /**
     * 设置公用Finish按钮文字
     *
     * @param txtRes 字符串资源ID
     * @return
     */
    public BaseFragment setCommonFinish(int txtRes) {
        String string = getActivity().getResources().getString(txtRes);
        TextView finish = (TextView) getActivity().findViewById(R.id.actionbar_finish);
        finish.setVisibility(View.VISIBLE);
        finish.setText(string);
        return this;
    }

    /**
     * 设置公用Finish按钮文字
     *
     * @param text
     * @return
     */
    public BaseFragment setCommonFinish(String text) {
        TextView finish = (TextView) getActivity().findViewById(R.id.actionbar_finish);
        finish.setVisibility(View.VISIBLE);
        finish.setText(text);
        return this;
    }

    /**
     * 设置公用Finish按钮点击事件
     *
     * @param listener
     * @return
     */
    public BaseFragment setCommonFinishClick(OnCommonClickListener listener) {
        getActivity().findViewById(R.id.actionbar_finish).setOnClickListener(listener);
        return this;
    }

    public BaseFragment setCommonBackClick(final OnCommonClickListener listener) {
        getActivity().findViewById(R.id.actionbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(v);
            }
        });
        return this;
    }

    /**
     * 设置公用ActionBar背景图片|颜色
     *
     * @param backgroudRes 资源ID
     * @return
     */
    public BaseFragment setCommonBackgroud(int backgroudRes) {
        BaseActionBarActivity context = (BaseActionBarActivity) getActivity();
        ActionBar actionBar = context.getSupportActionBar();
        View customView = actionBar.getCustomView();
        customView.setBackgroundResource(backgroudRes);
        return this;
    }

    /**
     * 隐藏ActionBar
     *
     * @return
     */
    public BaseFragment hideActionBar() {
        BaseActionBarActivity context = (BaseActionBarActivity) getActivity();
        ActionBar actionBar = context.getSupportActionBar();
        disableABCShowHideAnimation(actionBar);
        actionBar.hide();
        return this;
    }

    /**
     * 显示ActionBar
     *
     * @return
     */
    public BaseFragment showActionBar() {
        BaseActionBarActivity context = (BaseActionBarActivity) getActivity();
        ActionBar actionBar = context.getSupportActionBar();
        actionBar.show();
        return this;
    }

//    private void initSystemBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getActivity().setTranslucentStatus(true);
//            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
//            tintManager.setStatusBarTintEnabled(true);
//            tintManager.setStatusBarTintResource(R.color.actionbar_bg);
//            SystemBarConfig config = tintManager.getConfig();
//            listViewDrawer.setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
//        }
//    }

    //取消ActionBar动画
    private static void disableABCShowHideAnimation(ActionBar actionBar) {
        try {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        } catch (Exception exception) {
            try {
                Field mActionBarField = actionBar.getClass().getSuperclass().getDeclaredField("mActionBar");
                mActionBarField.setAccessible(true);
                Object icsActionBar = mActionBarField.get(actionBar);
                Field mShowHideAnimationEnabledField = icsActionBar.getClass().getDeclaredField("mShowHideAnimationEnabled");
                mShowHideAnimationEnabledField.setAccessible(true);
                mShowHideAnimationEnabledField.set(icsActionBar, false);
                Field mCurrentShowAnimField = icsActionBar.getClass().getDeclaredField("mCurrentShowAnim");
                mCurrentShowAnimField.setAccessible(true);
                mCurrentShowAnimField.set(icsActionBar, null);
                //icsActionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(icsActionBar, false);
            } catch (Exception e) {
                //....
            }
        }
    }

    private static void disableContentFragment(FragmentActivity context, boolean disable) {
        View view = context.findViewById(R.id.content_fragment);
        if (view != null) {
            view.setClickable(disable);
        }
    }

    private void onCreateOrBindActionBar() {
        onCreateActionBar(this);
        BaseActionBarActivity context = (BaseActionBarActivity) getActivity();
        ActionBar actionBar = context.getSupportActionBar();
        onActionBarCreated(actionBar.getCustomView());
    }

    /**
     * 公用ActionBar Finish按钮点击事件
     *
     * @author wcy
     */
    public interface OnCommonClickListener extends View.OnClickListener {
        @Override
        public void onClick(View v);
    }

}
