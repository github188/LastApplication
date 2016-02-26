package com.iermu.client.business.impl;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IUserCenterBusiness;
import com.iermu.client.business.api.UserCenterApi;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.config.PathConfig;
import com.iermu.client.listener.OnDeletePhotoChangeListener;
import com.iermu.client.listener.OnScreenerPictureListener;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhoushaopei on 16/1/7.
 *
 * 个人中心关联截图、剪辑
 */
public class UserCenterBusImpl extends BaseBusiness implements IUserCenterBusiness, OnLogoutEvent {

    private Context mContext;
    private String accessToken;
    private String uid;
    private String baiduAK;

    private Map<Integer, List<ScreenClip>> mScreenClipMap = new HashMap<Integer, List<ScreenClip>>();

    public UserCenterBusImpl() {
        super();
        this.mContext = ErmuApplication.getContext();
        this.accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        this.baiduAK = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
    }

    @Override
    public void getScreenClip() {
        List<ScreenClip> screens = getScreens();
        List<ScreenClip> clips = getScreenClip(PhotoType.FILM_EDIT);
        baiduAK = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
        getUserClip();
        screens.addAll(clips);
        getScreenClipMap().put(PhotoType.ALL, screens);
        Collections.sort(screens, new Comparator<ScreenClip>() {
            @Override
            public int compare(ScreenClip s1, ScreenClip s2) {
                String time = String.valueOf(s1.getTime());
                String time1 = String.valueOf(s2.getTime());
                return time1.compareTo(time);
            }
        });
        sendListener(OnScreenerPictureListener.class, screens, PhotoType.ALL);
    }

    @Override
    public void getUserScreen() {
        List<ScreenClip> screens = getScreens();
        Collections.sort(screens, new Comparator<ScreenClip>() {
            @Override
            public int compare(ScreenClip s1, ScreenClip s2) {
                String time = String.valueOf(s1.getTime());
                String time1 = String.valueOf(s2.getTime());
                return time1.compareTo(time);
            }
        });
        getScreenClipMap().put(PhotoType.PHOTO, screens);
        sendListener(OnScreenerPictureListener.class, screens, PhotoType.PHOTO);
    }

    @Override
    public void getUserClip() {
        baiduAK = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
        String str = UserCenterApi.apiGetBaiduClip(baiduAK);
        List<ScreenClip> films = getFilms(str);
        Collections.sort(films, new Comparator<ScreenClip>() {
            @Override
            public int compare(ScreenClip s1, ScreenClip s2) {
                String time = String.valueOf(s1.getTime());
                String time1 = String.valueOf(s2.getTime());
                return time1.compareTo(time);
            }
        });
        getScreenClipMap().put(PhotoType.FILM_EDIT, films);
        sendListener(OnScreenerPictureListener.class, films, PhotoType.FILM_EDIT);
    }

    @Override
    public void deleteScreenClip(Map<String, ScreenClip> map) {
        if (map == null) return;
        List<ScreenClip> all = getScreenClip(PhotoType.ALL);
        List<ScreenClip> screens = getScreenClip(PhotoType.PHOTO);
        List<ScreenClip> clips = getScreenClip(PhotoType.FILM_EDIT);
        List<String> films = new ArrayList<String>();
        for (String key : map.keySet()){
            ScreenClip screenClip = map.get(key);
            String path = screenClip.getPath();
            int type = screenClip.getType();
            for (ScreenClip screenC : all) {
                if (screenC.getPath().equals(path)) {
                    all.remove(screenClip);
                    break;
                }
            }
            if (type == PhotoType.FILM_EDIT) {
                films.add(path);
                for (ScreenClip screenC : clips) {
                    if (screenC.getPath().equals(path)) {
                        clips.remove(screenClip);
                        break;
                    }
                }
            } else {
                for (ScreenClip screenC : screens) {
                    if (screenC.getPath().equals(path)) {
                        screens.remove(screenClip);
                        break;
                    }
                }
                FileUtil.deleteFile(path);
            }
        }
        deleteClip(films);
        sendListener(OnDeletePhotoChangeListener.class);
    }

    public void deleteClip(List<String> list) {
        if (list == null) return;
        try {
            List<String> files = new ArrayList<String>();
            for(int i=0; i<list.size(); i++) {
                String path = list.get(i);
                if(!TextUtils.isEmpty(path)) {
                    files.add(path);
                }
            }
            BaiduPCSClient client = new BaiduPCSClient();
            client.setAccessToken(baiduAK);
            BaiduPCSActionInfo.PCSSimplefiedResponse response = client.deleteFiles(files);
            Logger.i("errodCode:"+response.errorCode);
            Logger.i("message:"+response.message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ScreenClip> getScreenClip(int type) {
        List<ScreenClip> lists = mScreenClipMap.get(type);
        if (lists != null) {
//            List<ScreenClip> list = new ArrayList<ScreenClip>(Arrays.asList(new ScreenClip[lists.size()]));
//            Collections.copy(list, lists);
            return lists;
        } else {
            return new ArrayList<ScreenClip>();
        }
    }

    private List<ScreenClip> getScreens() {
        List<ScreenClip> screens = new ArrayList<ScreenClip>();
        File file = new File(PathConfig.CACHE_SHARE);
        File[] files = file.listFiles();
        if (files != null) {
            if (files.length > 0) {
                List<ScreenClip> list = new ArrayList<ScreenClip>();
                for(int i=0;i<files.length;i++) {
                    File file1 = files[i];
                    String path = file1.getPath();
                    String name = file1.getName();
                    long time = file1.lastModified();
                    long length = file1.length();
                    int index = name.lastIndexOf(".");
                    String fileName = name.substring(0, index);

                    ScreenClip screenClip = new ScreenClip();
                    screenClip.setType(PhotoType.PHOTO);
                    screenClip.setPath(path);
                    screenClip.setName(fileName);
                    screenClip.setTime(time);
                    screenClip.setLength(length);

                    list.add(screenClip);
                }
                screens = list;
            } else {
                screens =new ArrayList<ScreenClip>();
            }
        }
        return screens;
    }

    private List<ScreenClip> getFilms(String string) {
        try {
            List<ScreenClip> list = new ArrayList<ScreenClip>();
            JSONObject json = new JSONObject(string);
            JSONArray array = json.optJSONArray("list");
            if (array != null) {
                for(int i=0;i<array.length();i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String fs_id    = jsonObject.optString("fs_id");
                    String path     = jsonObject.optString("path");
                    long ctime      = jsonObject.optLong("ctime");
                    long mtime      = jsonObject.optLong("mtime");
                    String md5      = jsonObject.optString("md5");
                    long size       = jsonObject.optLong("size");
                    int isdir       = jsonObject.optInt("isdir");
                    ScreenClip screenClip = new ScreenClip();
                    screenClip.setType(PhotoType.FILM_EDIT);
                    screenClip.setFsId(fs_id);
                    screenClip.setPath(path);
                    screenClip.setTime(ctime*1000l);
                    screenClip.setMtime(mtime);
                    screenClip.setMd5(md5);
                    screenClip.setSize(size);
                    screenClip.setIsDir(isdir);
                    list.add(screenClip);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<ScreenClip>();
        }
    }

    private Map<Integer, List<ScreenClip>> getScreenClipMap() {
        if (mScreenClipMap == null) {
            mScreenClipMap = new HashMap<Integer, List<ScreenClip>>();
        }
        return mScreenClipMap;
    }

    @Override
    public void OnLogoutEvent() {
        getScreenClipMap().clear();
    }
}
