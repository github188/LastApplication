package com.iermu.client.business.impl.runnable;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.iermu.client.business.api.StreamMediaApi;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.listener.OnLiveMediaListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ShareType;
import com.iermu.client.util.Logger;
import com.iermu.eventobj.BusObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 百度设备线程扫描流媒体(直播地址、直播状态) (个人设备、公共设备)
 *
 *  1.线程连接池(最大连接数控制)
 *  2.线程间数据访问同步
 *  3.单条流媒体数据, 扫描状态、次数、开始扫描时间、完成扫描时间(最大重试次数)
 *  4.断网状态监听, 中断扫描进程|恢复扫描进程
 *  5.扫描堆栈(最大数量控制)
 *
 * 羚羊云流媒体扫描
 *  1.连接摄像机(MediaSourceConnect)
 *  2.获取直播状态(扫描)
 *  3.获取直播地址(假的)
 *
 * Created by wcy on 15/8/6.
 */
public class BaiduStreamMediaScanner {

    public static final int MAX_RETRYCOUNT  = 5;        //最大重试次数
    public static final int MAX_TIMEOUT     = 12*1000;  //最大超时时间12s(流媒体数据缓存最大时间)
    public static final int MAX_VALID_QUEUECOUNT  = 8;  //队列最大数(有效流媒体数据加载队列个数)
    //
    public static final int WHAT_START_SCAN = 0x01;     //启动扫描
    public static final int WHAT_STOP_SCAN  = 0x02;     //停止扫描

    private static final ThreadFactory sThreadFactory	= new ScanThreadFactory();
    private static int threadPoolSize					= 3;
    private static Executor executor					= Executors.newFixedThreadPool(threadPoolSize, sThreadFactory);
    private static final Handler mHandler;
    private static final HandlerThread mRunner = new HandlerThread("BaiduStreamMedia-scanner");
    private static List<MediaStatusListener> mStatusListeners;
    private static BaiduStreamMediaScanner mInstance;

    //缓存当前扫描流媒体列表、扫描状态
    //流媒体数据缓存
    private static Map<String, LiveMedia> cacheMediaMap;//{deviceId, LiveMedia}
    //扫描状态缓存
    private static List<ScanStatus> mScanQueue;        //ScanStatus: {deviceId,status,count,startAt,completeAt}
    private static String accessToken;
    //private static Status status;

    static {
        Logger.i("start");
        mRunner.setDaemon(true);
        mRunner.setPriority(Thread.NORM_PRIORITY);
        mRunner.start();

        //status = Status.IDLE;
        mStatusListeners = new ArrayList<MediaStatusListener>();
        mHandler = new ScanHandler();
        mScanQueue = new ArrayList<ScanStatus>();
        cacheMediaMap = new HashMap<String, LiveMedia>();
    }

    private static BaiduStreamMediaScanner getInstance() {
        if (mInstance == null) {
            synchronized (BaiduStreamMediaScanner.class) {
                if (mInstance == null) {
                    mInstance = new BaiduStreamMediaScanner();
                }
            }
        }
        return mInstance;
    }

    /**
     * 开始扫描设备流媒体数据
     * @param live
     * @param token
     */
    public static synchronized void start(CamLive live, String token) {
        accessToken = token;
        String deviceId = live.getDeviceId();
        ScanStatus contains = containsScanQueue(deviceId);
        synchronized (BaiduStreamMediaScanner.class) {
            if(contains == null) {
                Logger.i("新增扫描队列 ");
                ScanStatus scan = new ScanStatus();
                scan.deviceId = live.getDeviceId();
                scan.camLive  = live;
                scan.count    = 0;
                scan.status   = Status.IDLE;
                scan.startAt  = 0;
                scan.completeAt=0;
                clearScanQueue();
                int size = mScanQueue.size();
                if(size > MAX_VALID_QUEUECOUNT) {
                    ScanStatus lasted = mScanQueue.get(size - 1);
                    if((lasted.getValidDiff()>MAX_TIMEOUT && lasted.status==Status.COMPLETED)
                            || lasted.status!=Status.SCANING) {
                        Logger.i("remove 扫描队列 ");
                        mScanQueue.remove(size-1);
                    }
                }
                mScanQueue.add(0, scan);
            } else if(contains.status==Status.COMPLETED && contains.getValidDiff()<=MAX_TIMEOUT){
                Logger.i("COMPLETED 扫描队列 ");
                BusObject.sendListener(OnLiveMediaListener.class, deviceId);
                return;
            } else {
                Logger.i("else 扫描队列 ");
            }
        }
        getInstance().notifyScanEngine();
    }

    /**
     * 开始扫描设备流媒体数据
     * @param lives
     * @param token
     */
    public static synchronized void start(List<CamLive> lives, String token) {
        accessToken = token;
        synchronized (BaiduStreamMediaScanner.class) {
            ListIterator<CamLive> iterator = lives.listIterator();
            while (iterator.hasNext()) {
                CamLive next    = iterator.next();
                if(next == null) {
                    Logger.e("deviceid is null.");
                    continue;
                }
                String deviceId = next.getDeviceId();
                ScanStatus contains = containsScanQueue(deviceId);

                if( contains==null) {
                    ScanStatus scan = new ScanStatus();
                    scan.deviceId = deviceId;
                    scan.camLive  = next;
                    scan.count    = 0;
                    scan.status   = Status.IDLE;
                    scan.startAt  = 0;
                    scan.completeAt=0;
                    int size = mScanQueue.size();
                    if(size > MAX_VALID_QUEUECOUNT) {
                        ScanStatus lasted = mScanQueue.get(size - 1);
                        if((lasted.getValidDiff()>MAX_TIMEOUT && lasted.status==Status.COMPLETED)
                                || lasted.status!=Status.SCANING) {
                            Logger.i("remove 扫描队列 ");
                            mScanQueue.remove(size-1);
                        }
                    }
                    mScanQueue.add(0, scan);
                } else if(contains.status==Status.COMPLETED && contains.getValidDiff()<=MAX_TIMEOUT){
                    BusObject.sendListener(OnLiveMediaListener.class, contains.deviceId);
                }
            }
        }
        getInstance().notifyScanEngine();
    }

    /**
     * 停止扫描设备流媒体数据
     * @param deviceId
     */
    public static synchronized void stop(String deviceId) {
        mScanQueue.remove(deviceId);
        getInstance().notifyScanEngine();
    }

    /**
     * 检索缓冲流媒体数据
     * @param deviceId
     */
    public static synchronized LiveMedia search(String deviceId) {
        LiveMedia media = cacheMediaMap.get(deviceId);
        if(media != null) {
            cacheMediaMap.remove(deviceId);
            removeScanQueue(deviceId);
        }
        return media;
    }

    private static void removeScanQueue(String deviceId) {
        synchronized (BaiduStreamMediaScanner.class) {
            ListIterator<ScanStatus> iterator = mScanQueue.listIterator();
            while(iterator.hasNext()) {
                ScanStatus status = iterator.next();
                if(status.deviceId.equals(deviceId)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private void notifyScanEngine() {
        if(scanQueueEmpty()) {
            return;
        }
        mHandler.sendEmptyMessage(WHAT_START_SCAN);
    }

    private static ScanStatus containsScanQueue(String deviceId) {
        ScanStatus queue = null;
        ListIterator<ScanStatus> iterator = mScanQueue.listIterator();
        while(iterator.hasNext()) {
            ScanStatus status = iterator.next();
            if(status.deviceId.equals(deviceId)) {
                queue = status;
                break;
            }
        }
        return queue;
    }

    private static synchronized boolean scanQueueEmpty() {
        synchronized (BaiduStreamMediaScanner.class) {
            return mScanQueue.isEmpty();
        }
    }

    private static synchronized void clearScanQueue() {
        synchronized (BaiduStreamMediaScanner.class) {
            mScanQueue.clear();
        }
    }

    private static synchronized void putCacheMap(String deviceId, LiveMedia media) {
        cacheMediaMap.put(deviceId, media);
    }

    private static void startScan() {
//        if(status == Status.SCANING) {
//            return;
//        }

        synchronized (BaiduStreamMediaScanner.class) {
            ListIterator<ScanStatus> iterator = mScanQueue.listIterator();
            while (iterator.hasNext()) {
                final ScanStatus next = iterator.next();
                if(next.getValidDiff() > MAX_TIMEOUT) {
                    Logger.i(Thread.currentThread().getName(), "startScan: devId:"+next.deviceId+"  timeout validDiff:"+ next.getValidDiff());
                    next.status = Status.IDLE;
                    next.count    = 0;
                    next.startAt  = 0;
                    next.completeAt=0;
                }
                if(next.status != Status.RETRY && next.status != Status.IDLE ) {
                    Logger.i(Thread.currentThread().getName(), "startScan: devId:"+next.deviceId+" status:"
                            + next.status
                            + " timeDiff:"+ next.getTimeDiff()
                            + " validDiff:"+ next.getValidDiff());
                    if(next.status == Status.COMPLETED || next.status == Status.FAIL) {
                        Logger.i("sendListener: devId"+next.deviceId);
                        BusObject.sendListener(OnLiveMediaListener.class, next.deviceId);
                    }
                    return;
                }

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        while(next.status!=Status.COMPLETED
                                && next.status!=Status.FAIL
                                && next.count <= MAX_RETRYCOUNT) {

                            CamLive live    = next.camLive;
                            String deviceId = next.deviceId;
                            next.count++;
                            next.status = Status.SCANING;
                            if(next.count == 1) {
                                next.startAt = System.currentTimeMillis();
                                next.completeAt = System.currentTimeMillis();
                            }

                            LiveMediaResponse res = null;
                            //if(live.getConnectType()== ConnectType.BAIDU) {
                            //    String baiduAccessToken = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
                            //    res = StreamMediaApi.apiBaiduLivePlay(deviceId, baiduAccessToken, live.getShareId(), live.getUk());
                            //} else
                            if(live.getShareType() == ShareType.PRIVATE) {
                                res = StreamMediaApi.apiLivePlay(deviceId, accessToken, "", "");
                            } else {
                                res = StreamMediaApi.apiLivePlay(deviceId, accessToken, live.getShareId(), live.getUk());
                            }
                            Business bus = res.getBusiness();
                            if(bus.getCode() == BusinessCode.SUCCESS) {
                                LiveMedia media = res.getLiveMedia();
                                //if(media!=null) media.setConnectType(ConnectType.BAIDU);
                                putCacheMap(deviceId, media);
                                //scanQueuePop();
                                next.status = Status.COMPLETED;
                                next.completeAt = System.currentTimeMillis();
                                BusObject.sendListener(OnLiveMediaListener.class, deviceId);
                                Logger.i(Thread.currentThread().getName(), "startScan: devId:"+next.deviceId+"  success " + deviceId);
                            } else if(bus.getCode() == BusinessCode.CONNECT_API_FAILED) {
                                //scanQueuePop();
                                next.status = Status.FAIL;
                                next.completeAt = System.currentTimeMillis();
                                BusObject.sendListener(OnLiveMediaListener.class, deviceId);
                                Logger.i(Thread.currentThread().getName(),"startScan: devId:"+next.deviceId+"  fail " + deviceId);
                            } else {
                                next.status = Status.RETRY;
                                Logger.i(Thread.currentThread().getName(),"startScan: devId:"+next.deviceId+"  fail " + deviceId);
                            }
                        }

                        if(next.status==Status.RETRY && next.count > MAX_RETRYCOUNT) {
                            next.status = Status.FAIL;
                            next.completeAt = System.currentTimeMillis();
                            BusObject.sendListener(OnLiveMediaListener.class, next.deviceId);
                            Logger.i(Thread.currentThread().getName(),"startScan: devId:"+next.deviceId+"  fail " + next.deviceId);
                        }
                    }
                });
            }
        }
//        Logger.i("startScan StreamMedia");
//        while ( !mScanQueue.empty() ) {
//            status = Status.SCANING;
//
//            ScanStatus scan = mScanQueue.peek();
//
//            BdLiveMedia media = (BdLiveMedia) cacheMediaMap.get(element);
//            if(cacheMediaMap.containsKey(element) && media.getEffectiveTime()>=10000) {
//                Logger.i("startScan: exist " + element);
//                mScanQueue.pop();
//                BusObject.sendListener(OnLiveMediaListener.class, element);
//            } else {
//                LiveMediaResponse res = StreamMediaApi.apiLivePlay(element, accessToken, "", "");
//                Business bus = res.getBusiness();
//                if(bus.getCode() == BusinessCode.SUCCESS) {
//                    Logger.i("startScan: success " + element);
//                    media = res.getLiveMedia();
//                    cacheMediaMap.put(element, media);
//                    mScanQueue.pop();
//                } else {
//                    Logger.i("startScan: fail " + element);
//                    //TODO 继续重试
//                }
//                BusObject.sendListener(OnLiveMediaListener.class, element);
//            }
//        }
//        status = Status.COMPLETED;
//        Logger.i("endScan StreamMedia");
    }

    static class ScanHandler extends Handler {
        public ScanHandler() {
            super(mRunner.getLooper());
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_START_SCAN:
                startScan();
                break;
            case WHAT_STOP_SCAN:
                break;
            }
        }
    }

    static class ScanThreadFactory implements ThreadFactory {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "BaiduStreamMedia #" + mCount.getAndIncrement());
            thread.setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        }
    }

    static class ScanStatus {
        String  deviceId;       //设备ID
        CamLive camLive;        //扫描设备
        Status  status;         //扫描状态
        int     count;          //扫描次数
        long    startAt;        //开始扫描时间
        long    completeAt;     //结束扫描时间

        public long getValidDiff() {
            return System.currentTimeMillis() - completeAt;
        }

        public long getTimeDiff() {
            return completeAt - startAt;
        }
    }

    public static enum Status {
        IDLE,       //空闲状态
        SCANING,    //正在扫描
        RETRY,      //重试扫描
        COMPLETED,  //扫描完成
        FAIL        //扫描失败
    }
}
