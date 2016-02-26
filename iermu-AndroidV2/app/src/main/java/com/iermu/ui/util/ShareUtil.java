package com.iermu.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.iermu.client.ErmuApplication;
import com.iermu.client.config.PathConfig;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by zhangxq on 15/8/11.
 */
public class ShareUtil {

    private static Bitmap bitmapShare;

    public static Bitmap getBitmapShare() {
        return bitmapShare;
    }

    /**
     * shareSdk初始化
     *
     * @param context
     */
    public static void initShareSdk(Context context) {
        ShareSDK.initSDK(context);//"2e9286f63262"
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("Id", "1");
        hashMap.put("SortId", "1");
        hashMap.put("AppId", "wx885bb48c38bf16f4");
        hashMap.put("AppSecret", "7ae2a8552d93788f59fc6dfe923b2c0e");
        hashMap.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(Wechat.NAME, hashMap);
        hashMap = new HashMap<String, Object>();
        hashMap.put("Id", "2");
        hashMap.put("SortId", "2");
        hashMap.put("AppId", "wx885bb48c38bf16f4");
        hashMap.put("AppSecret", "7ae2a8552d93788f59fc6dfe923b2c0e");
        hashMap.put("Enable", "true");
        ShareSDK.setPlatformDevInfo(WechatMoments.NAME, hashMap);
        Logger.d("ShareSDK init ok");
    }

    /**
     * 通用分享方法
     *
     * @param context
     * @param title
     * @param url
     * @param imageUrl
     * @param content
     */
    public static void share(Context context, String title, String url, String imageUrl, String content) {
        final OnekeyShare oks = new OnekeyShare();

        oks.setTitle(title);
//        oks.setTitleUrl("www.baidu.com");
        oks.setText(content);
        oks.setImageUrl(imageUrl);
        oks.setUrl(url);
//        oks.setComment("我是测试评论文本");
//        oks.setSite("分享");
//        oks.setSiteUrl("www.baidu.com");
        oks.show(context);
    }

    /**
     * 分享截屏
     *
     * @param context
     * @param imagePath
     */
    public static void share(Context context, String imagePath) {
        final OnekeyShare oks = new OnekeyShare();
        oks.setImagePath(imagePath);
        oks.show(context);
    }

    public static void capsuleShare(Context context, String imagePath) {
        final OnekeyShare oks = new OnekeyShare();
        oks.setTitle(" ");
        oks.setText(" ");
        oks.setImagePath(imagePath);
        oks.show(context);
    }

    /**
     * 保存方法
     */
    public static void saveBitmap(Bitmap bitmap, long currentTime, String deviceId) {
        if (bitmap == null) {
            return;
        }
        bitmapShare = bitmap;
        String fileName = DateUtil.formatDate(new Date(currentTime), DateUtil.FORMAT9);
        String imageName = fileName + ".jpeg";
        // 创建文件夹
        File fdir = new File(PathConfig.CACHE_SHARE);//+"/"+deviceId
        if (!fdir.exists()) {
            fdir.mkdirs();
        }

        // 创建文件
        File f = new File(fdir, imageName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Logger.d("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d("filePath:" + f.getPath());
    }

    /**
     * 保存预置位图片
     */
    public static void savePresetBitmap(Bitmap bitmap, long currentTime) {
        if (bitmap == null) {
            return;
        }
        bitmapShare = bitmap;
        String fileName = DateUtil.formatDate(new Date(currentTime), DateUtil.FORMAT9);
        String imageName = fileName + ".jpeg";
        // 创建文件夹
        File fdir = new File(PathConfig.CACHE_PRESET);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }

        // 创建文件
        File f = new File(fdir, imageName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Logger.d("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d("filePath:" + f.getPath());
    }

    /**
     * view 转换为pic
     */
    public static void saveCapsuleBitmap(Bitmap bitmap, long currentTime) {
        if (bitmap == null) {
            return;
        }
        String imageName = currentTime + ".jpeg";
        // 创建文件夹
        File fdir = new File(PathConfig.CACHE_CAPSULE);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        initBitmap(bitmap, fdir, imageName);
    }

    /**
     * 截屏
     */
    public static void saveVideoBitmap(Bitmap bitmap, long currentTime) {
        if (bitmap == null) {
            return;
        }
        String imageName = currentTime + ".jpeg";
        // 创建文件夹
        File fdir = new File(PathConfig.CACHE_VIDEO);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        initBitmap(bitmap, fdir, imageName);

    }

    private static void initBitmap(Bitmap bitmap, File fdir, String imageName) {
// 创建文件
        File f = new File(fdir, imageName);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 保存
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Logger.d("保存成功");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.d("filePath:" + f.getPath());
    }

    public static String getImagePath(long cutTime) {
        String fileName = DateUtil.formatDate(new Date(cutTime), DateUtil.FORMAT9);
        String imagePath = PathConfig.CACHE_SHARE + "/" + fileName + ".jpeg";
        return imagePath;
    }

    public static String getCapsuleImgPath(long cutTime) {
        String imagePath = PathConfig.CACHE_CAPSULE + "/" + cutTime + ".jpeg";
        return imagePath;
    }

    public static String getPresetImgPath(long cutTime) {
        String fileName = DateUtil.formatDate(new Date(cutTime), DateUtil.FORMAT9);
        String imagePath = PathConfig.CACHE_PRESET + "/" + fileName + ".jpeg";
        return imagePath;
    }

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int percent) {
        if (image == null) {
            return null;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, percent, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
