//package com.iermu.ui.fragment.test;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.TextView;
//
//import me.allenz.androidapplog.Logger;
//import me.allenz.androidapplog.LoggerFactory;
//
///**
// * Created by wcy on 15/11/13.
// */
//public class TestShowLogActivity extends Activity {
//
//    TextView logTextView;
//    private static final Logger logger = LoggerFactory.getLogger();
//    Logger loggerBaidu = LoggerFactory.getLogger("Test BaiduLogin");
//
//    public static void actionShowLog(Activity ctx) {
//        Intent intent = new Intent(ctx, TestShowLogActivity.class);
//        ctx.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        logTextView = LoggerFactory.createLogScrollTextView(this);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LoggerFactory.bindTextView(logTextView);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int count = 100;
//                while (count > 0) {
//                    logger.verbose("verbose"+count);
//                    logger.debug("debug");
//                    logger.info("info"+count);
//                    logger.warn("warn");
//
//                    loggerBaidu.verbose("verbose"+count);
//                    loggerBaidu.debug("debug");
//                    loggerBaidu.info("info"+count);
//                    loggerBaidu.warn("warn");
//                    count--;
//                }
//            }
//        }).start();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LoggerFactory.unbindTextView();
//    }
//
//}
