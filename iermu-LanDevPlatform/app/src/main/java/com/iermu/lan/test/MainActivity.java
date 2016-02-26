//package com.iermu.lan.test;
//
//import android.os.Handler;
//import android.os.Message;
//import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
////import com.cms.media.widget.VideoView;
//import com.iermu.lan.AirCapsuleApi;
//import com.iermu.lan.CardVidioApi;
//import com.iermu.lan.LanDevPlatformApi;
//import com.iermu.lan.R;
//import com.iermu.lan.airCapsule.impl.AirCapsuleImpl;
//import com.iermu.lan.cardVidio.impl.CareVidioImpl;
//import com.iermu.lan.model.AirCapsuleResult;
//import com.iermu.lan.model.CardInforResult;
//import com.iermu.lan.model.NasParamResult;
//import com.iermu.lan.model.Result;
//import com.cms.iermu.cms.CmsConstants;
//import com.iermu.lan.NasDevApi;
//import com.cms.iermu.cms.CmsErr;
//import com.iermu.lan.model.NasPlayListResult;
//import com.iermu.lan.cloud.impl.LanDevPlatformImpl;
//import com.iermu.lan.nas.impl.NasDevImpl;
//
//
//public class MainActivity extends ActionBarActivity implements View.OnClickListener {
//    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8 ,btn9 ,btn10 ,btn11 ,btn12 ,btn13,btn14,btn15;
////    VideoView videoView;
//    EditText et1,et2,et3,et4;
//    TextView t1;
//    Handler m_handler;
//    public static String url = null;
//    public static String text = null;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btn1 = (Button) findViewById(R.id.button1);//开启设备平扫
//        btn2 = (Button) findViewById(R.id.button2);//关闭设备平扫
//        btn3 = (Button) findViewById(R.id.button3);//上传预置位
//        btn4 = (Button) findViewById(R.id.button4);//执行预置位
//        btn5 = (Button) findViewById(R.id.button5);//按坐标位移
//        btn6 = (Button) findViewById(R.id.button6);//是否平扫
//        btn7 = (Button) findViewById(R.id.button7);//是否支持位移
//        btn8 = (Button) findViewById(R.id.button8);//是否支持云台
//        btn9 = (Button) findViewById(R.id.button9);//获取nas视频列表
//        btn10 = (Button) findViewById(R.id.button10);//获取nas视频播放路径
//        btn11 = (Button) findViewById(R.id.button11);//获取nas目录
//        btn12 = (Button) findViewById(R.id.button12);//获取局域网播放路径
//        btn13 = (Button) findViewById(R.id.button13);//停止nas播放
//        btn14 = (Button) findViewById(R.id.button14);//获取nas设置参数
//        btn15 = (Button) findViewById(R.id.button15);//空气胶囊
//        et1 = (EditText) findViewById(R.id.editText1);//获取nas设置参数
//        et2 = (EditText) findViewById(R.id.editText2);//获取nas设置参数
////        et3 = (EditText) findViewById(R.id.editText3);//获取nas设置参数
////        et4 = (EditText) findViewById(R.id.editText4);//获取nas设置参数
//        t1 = (TextView) findViewById(R.id.textView);//获取nas设置参数
////        videoView = (VideoView) findViewById(R.id.videoView);
////        et1.setText("5911957302");
////        et2.setText("18159019");
//        et1.setText("137892724315");
//        et2.setText("18159019");
////          et1.setText("137896306459");
////          et2.setText("2927335352");
////        et1.setText("137896305835");
////        et2.setText("4127392870");
////        et1.setText("137896305835");
////        et2.setText("4127392870");
//
//        btn1.setOnClickListener(this);
//        btn2.setOnClickListener(this);
//        btn3.setOnClickListener(this);
//        btn4.setOnClickListener(this);
//        btn5.setOnClickListener(this);
//        btn6.setOnClickListener(this);
//        btn7.setOnClickListener(this);
//        btn8.setOnClickListener(this);
//        btn9.setOnClickListener(this);
//        btn10.setOnClickListener(this);
//        btn11.setOnClickListener(this);
//        btn12.setOnClickListener(this);
//        btn13.setOnClickListener(this);
//        btn14.setOnClickListener(this);
//        btn15.setOnClickListener(this);
//
//    }
//
//
//
//    @Override
//    public void onClick(View v) {
//        // TODO Auto-generated method stub
//        int i = v.getId();
//        if (i == R.id.button1) {
//            new test1().start();
//
//        } else if (i == R.id.button2) {
//            new test2().start();
//
//        } else if (i == R.id.button3) {
//            new test3().start();
//
//        } else if (i == R.id.button4) {
//            new test4().start();
//
//        } else if (i == R.id.button5) {
//            new test5().start();
//            m_handler = new Handler() {
//                public void handleMessage(Message msg) {
//                    t1.setText(text);
//                }
//            };
//
//        } else if (i == R.id.button6) {
//            new test6().start();
//
//        } else if (i == R.id.button7) {
//            new test7().start();
//
//        } else if (i == R.id.button8) {
//            new test8().start();
//
//        } else if (i == R.id.button9) {
//            new test9().start();
//            m_handler = new Handler() {
//                public void handleMessage(Message msg) {
//                    t1.setText(text);
//                }
//            };
//
//        } else if (i == R.id.button10) {
//            new test10().start();
//            m_handler = new Handler() {
//                public void handleMessage(Message msg) {
////                        videoView.playVideo(url, false);
//                }
//            };
//
//            new test11().start();
//
//        } else if (i == R.id.button11) {
//            new test11().start();
//
//        } else if (i == R.id.button12) {
//            new test12().start();
//            m_handler = new Handler() {
//                public void handleMessage(Message msg) {
////                        videoView.playVideo(url, false);
//                }
//            };
//
//            new test13().start();
//
//        } else if (i == R.id.button13) {
//            new test13().start();
//
//        } else if (i == R.id.button14) {
//            new test14().start();
//
//        } else if (i == R.id.button15) {
//            new test15().start();
//            m_handler = new Handler() {
//                public void handleMessage(Message msg) {
//                    t1.setText(text);
//                }
//            };
//
//        } else {
//        }
//        m_handler = new Handler() {
//            public void handleMessage(Message msg) {
//                t1.setText(text);
//            }
//        };
//    }
//
//
//
//    public class test1 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.openDevRotate(MainActivity.this, et1.getText().toString(), et2.getText().toString());
//        }
//    }
//
//    public class test2 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.closeDevRotate(MainActivity.this, et1.getText().toString(), et2.getText().toString());
//        }
//    }
//
//    public class test3 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.addDevPresetPoint(MainActivity.this, et1.getText().toString(), et2.getText().toString(), 4);
//        }
//    }
//
//    public class test4 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.toDevPresetPoint(MainActivity.this, et1.getText().toString(), et2.getText().toString(), 4);
//        }
//    }
//
//    public class test5 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            int s1 = Integer.parseInt(et1.getText().toString());
//            int s2 = Integer.parseInt(et2.getText().toString());
//            int s3 = Integer.parseInt(et3.getText().toString());
//            int s4 = Integer.parseInt(et4.getText().toString());
//            Result res = lanDevPlatform.setDevMovePoint(MainActivity.this, et1.getText().toString(), et2.getText().toString(), s1, s2, s3, s4);
//            text = ""+res.getResultInt();
//            if(res!=null) m_handler.sendMessage(new Message());
//        }
//    }
//
//
//    public class test6 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.isRotate(MainActivity.this, et1.getText().toString(), et2.getText().toString());
//            text = ""+res.toString();
//            if(res!=null) m_handler.sendMessage(new Message());
////            Log.i("isSupportRotate",res.isResultBooean()?"true":"false");
//        }
//    }
//
//    public class test7 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            LanDevPlatformApi lanDevPlatform = new LanDevPlatformImpl();
//            Result res = lanDevPlatform.isSupportXYMove(MainActivity.this, et1.getText().toString(), et2.getText().toString());
//            Log.i("isSupportXYMove", res.isResultBooean() ? "true" : "false");
//        }
//
//    }
//
//    public class test8 extends Thread {
//        @Override
//        public void run() {
//            super.run();
////            LanDevPlatformApi lanDevPlatform = new AirCapsuleImpl();
////            Result res = lanDevPlatform.isSupportPlatform(MainActivity.this, et1.getText().toString(), et2.getText().toString());
////            Log.i("isSupportXYMove", res.isResultBooean() ? "true" : "false");
//        }
//    }
//
//    public class test9 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            NasDevApi nasDev = new NasDevImpl();
//            NasPlayListResult res = nasDev.getPlayList(MainActivity.this, et1.getText().toString(), et2.getText().toString(), "2015/11/29 18:40:37", "2015-11-30 18:40:37", new CmsErr(CmsConstants.CMS_LAN_CONN_FAIL, "init"));//YY-MM-DD HH:mm:SS
////            text = "size:"+res.getList().size()+"====>"+res.toString();
//            text = "====>"+res.toString();
//            if(res!=null) m_handler.sendMessage(new Message());
//            Log.i("nasDev",res.toString());
//        }
//    }
//    public class test10 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            NasDevApi nasDev = new NasDevImpl();
//            Result res = nasDev.startNasPlay(MainActivity.this, et1.getText().toString(), et2.getText().toString(), "2014-11-20 19:48:48", "2014-12-24 19:48:48");
//            url=res.getResultString();
//            Log.i("nasDev", res.getResultString());
//            if(res.getResultString()!=null) m_handler.sendMessage(new Message());
//        }
//    }
//
//    public class test11 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            NasDevApi nasDev = new NasDevImpl();
////            Result res = nasDev.getSmbFolder("192.168.199.1", "netposas", "300367");
//            Result res = nasDev.getSmbFolder("192.168.199.1", "guest", "");
////            Result res = nasDev.getNfsPath(MainActivity.this,"137893758203", "18159019", "192.168.199.1");
//            text = ""+res.toString();
//            if(res!=null) m_handler.sendMessage(new Message());
//            Log.i("nasDev", res.toString());
//
//        }
//    }
//
//    public class test12 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            Result res = new LanDevPlatformImpl().getLanPlayUrl(MainActivity.this, et1.getText().toString(), et2.getText().toString(),"");
//            url=res.getResultString();
//            if(res!=null) m_handler.sendMessage(new Message());
//        }
//    }
//
//    public class test13 extends Thread {
//        @Override
//        public void run() {
//            super.run();
////            NasDevApi nasDev = new NasDevImpl();
////            Result res = nasDev.stopNasPlay(MainActivity.this, et1.getText().toString(), et2.getText().toString());
////            Log.i("nasDev", res.toString());
//
//        }
//    }
//
//    public class test14 extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            NasDevApi nasDev = new NasDevImpl();
//            NasParamResult res = nasDev.getNasParam(MainActivity.this,et1.getText().toString(), et2.getText().toString());
//            Log.i("nasDev", res.toString());
//
//        }
//    }
//
//    public class test15 extends Thread {
//        @Override
//        public void run() {
//            super.run();
////            AirCapsuleApi nasDev = new AirCapsuleImpl();
////            AirCapsuleResult res = nasDev.getAirCapsuleData(MainActivity.this,et1.getText().toString(), et2.getText().toString());
////            text = "温度:"+res.getTemperature()+"   湿度:"+res.getHumidity();
////            if(res!=null) m_handler.sendMessage(new Message());
////            Log.i("nasDev", res.tosDev = new AirCapsuleImpl();
//            CardVidioApi nasDev = new CareVidioImpl();
//            CardInforResult res = nasDev.getCardInfor(MainActivity.this, et1.getText().toString(), et2.getText().toString());
//            text = res.toString();
//            if(res!=null) m_handler.sendMessage(new Message());
//            Log.i("nasDev", res.toString());
//
//        }
//    }
//
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
//}
