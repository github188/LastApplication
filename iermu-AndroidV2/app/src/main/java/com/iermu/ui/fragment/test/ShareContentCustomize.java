package com.iermu.ui.fragment.test;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * Created by xjy on 15/7/29.
 */
public class ShareContentCustomize implements ShareContentCustomizeCallback {
    @Override
    public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
        if ("WeWechatMoments".equals(platform.getName())) {
            paramsToShare.setMusicUrl("http://220.181.110.66/cdn/baidump3/20110829/music/5974988.mp3");
        }else if("Wechat".equals(platform.getName())){
            paramsToShare.setMusicUrl("http://220.181.110.66/cdn/baidump3/20110829/music/5974988.mp3");
        }

    }
}
