package com.iermu.ui.activity;

import android.app.Activity;
import android.os.SystemClock;

import java.util.Stack;

/**
 * Activity堆栈管理(用于清除MainActivity之前的Activity)
 *
 * Created by wcy on 15/10/18.
 */
public class ActivityStackHelper {

    static Stack<Activity> activityStack = new Stack<Activity>();

    /**
     * 新增Activity
     * @param ctx
     */
    public static void addActivityStack(Activity ctx) {
        activityStack.push(ctx);
    }

    /**
     * 清空Activity堆栈
     */
    public static void clearActivityStack() {
        SystemClock.sleep(300);
        while(!activityStack.isEmpty()) {
            Activity pop = activityStack.pop();
            pop.finish();
        }
    }

    /**
     * 清除栈顶Activity
     */
    public static void popActivityStack() {
        if(activityStack.isEmpty()) return;
        Activity pop = activityStack.pop();
        if(pop!=null) pop.finish();
    }
}
