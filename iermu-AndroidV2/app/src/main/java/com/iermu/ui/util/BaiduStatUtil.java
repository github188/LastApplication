package com.iermu.ui.util;

import android.content.Context;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

/**
 * Created by zhoushaopei on 15/11/2.
 */
public class BaiduStatUtil {

    public static void initBaiduStat(Context context) {
        // 设置AppKey
        // StatService.setAppKey("cb0b0665e5"); // appkey必须在mtj网站上注册生成，该设置建议在AndroidManifest.xml中填写，代码设置容易丢失

        /*
         * 设置渠道的推荐方法。该方法同setAppChannel（String）， 如果第三个参数设置为true（防止渠道代码设置会丢失的情况），将会保存该渠道，每次设置都会更新保存的渠道，
         * 如果之前的版本使用了该函数设置渠道，而后来的版本需要AndroidManifest.xml设置渠道，那么需要将第二个参数设置为空字符串,并且第三个参数设置为false即可。
         * appChannel是应用的发布渠道，不需要在mtj网站上注册，直接填写就可以 该参数也可以设置在AndroidManifest.xml中
         */
        StatService.setAppChannel(context, "txyyb", true);
        // 测试时，可以使用1秒钟session过期，这样不断的间隔1S启动退出会产生大量日志。
        StatService.setSessionTimeOut(30);
        // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
        StatService.setOn(context, StatService.EXCEPTION_LOG);
        /*
         * 设置启动时日志发送延时的秒数<br/> 单位为秒，大小为0s到30s之间<br/> 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
         *
         * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。<br/> 这个参数的设置暂时只支持代码加入，
         * 在您的首个启动的Activity中的onCreate函数中使用就可以。<br/>
         */
        StatService.setLogSenderDelayed(1);
        /*
         * 用于设置日志发送策略<br /> 嵌入位置：Activity的onCreate()函数中 <br />
         *
         * 调用方式：StatService.setSendLogStrategy(this,SendStrategyEnum. SET_TIME_INTERVAL, 1, false); 第二个参数可选：
         * SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
         * 这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
         * 表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
         */
        StatService.setSendLogStrategy(context, SendStrategyEnum.SET_TIME_INTERVAL, 1, false);
        // 调试百度统计SDK的Log开关，可以在Eclipse中看到sdk打印的日志，发布时去除调用，或者设置为false
        StatService.setDebugOn(true);

        String sdkVersion = StatService.getSdkVersion();
    }

}
