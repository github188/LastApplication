package com.iermu.client.util;

import android.app.Application;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 对象缓存
 *
 * Created by wcy on 15/7/10.
 */
public class ObjectCacheUtil {

    /**
     * 生成缓存Key
     * @param unique 唯一标识
     * @param obj	 缓存Key规则
     * @return
     */
    public static String generateCacheKey(String unique, Object... obj) {
        String key = unique;
        if(obj != null) {
            for(int i=0; i<obj.length; i++) {
                key += "_" + obj[i];
            }
        }
        return key;
    }

    /**
     * 序列化对象
     * @param ser
     * @param key
     * @return
     */
    public static boolean saveObject(Context context, Serializable ser, String key) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(key, Application.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (IOException e) {}
            try {
                fos.close();
            } catch (IOException e) {}
        }
    }

    /**
     * 反序列化对象
     * @param file
     * @return
     */
    public static Serializable readObject(Context context, String file) {
        if(!isExistDataCache(context, file)) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof InvalidClassException) {
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (IOException e) {}
            try {
                fis.close();
            } catch (IOException e) {}
        }
        return null;
    }

    //判断缓存是否存在
    public static boolean isExistDataCache(Context context, String cacheKey) {
        boolean exist = false;
        File data = context.getFileStreamPath(cacheKey);
        if(data.exists()) {
            exist = true;
        }
        return exist;
    }


}
