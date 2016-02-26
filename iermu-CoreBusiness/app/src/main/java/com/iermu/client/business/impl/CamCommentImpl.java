package com.iermu.client.business.impl;

import android.graphics.Color;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.IPubCamCommentBusiness;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.business.api.response.CommentListResponse;
import com.iermu.client.business.api.response.CommentSendResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.listener.OnCamCommentChangedListener;
import com.iermu.client.listener.OnCamLiveFindListener;
import com.iermu.client.listener.OnStoreSuccessListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamComment;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.util.LanguageUtil;
import com.iermu.eventobj.BusObject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.DanmakuFactory;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.danmaku.parser.android.BiliDanmukuParser;

/**
 * Created by zhangxq on 15/8/3.
 */
public class CamCommentImpl extends BaseBusiness implements IPubCamCommentBusiness, OnAccountTokenEvent {
    private Map<String, List<CamComment>> commentsMap;
    private Map<String, Integer> countMap;
    private int page = 0;

    private List<CamComment> commentsTemp;

    private String accessToken;
    private String uid;

    private int localId = 1;
    private boolean isStop = false;
    private IMimeCamBusiness mimeCamBusiness;

    public CamCommentImpl() {
        mimeCamBusiness = ErmuBusiness.getMimeCamBusiness();
        commentsMap = new HashMap<String, List<CamComment>>();
        countMap = new HashMap<String, Integer>();
        commentsTemp = new ArrayList<CamComment>();
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        BusObject.subscribeEvent(OnAccountTokenEvent.class, this);
    }

    @Override
    public void refreshCommentList(String shareId, String uk) {
        CommentListResponse response = PubCamApi.getCommentList(shareId, uk, 1, 3);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                // 刷新前先删除发送的临时数据，避免数据重复
                List<CamComment> comments = commentsMap.get(shareId);
                if (comments == null) {
                    commentsMap.put(shareId, new ArrayList<CamComment>());
                }
                for (CamComment comment : commentsTemp) {
                    comments.remove(comment);
                }

                List<CamComment> list = response.getList();
                compareAddToTop(list, shareId);
                countMap.put(shareId, response.getCount());
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnCamCommentChangedListener.class, false);
    }

    @Override
    public void syncNewCommentList(String shareId, String uk) {
        page = 1;
        CommentListResponse response = PubCamApi.getCommentList(shareId, uk, page, 40);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                List<CamComment> list = response.getList();
                commentsMap.put(shareId, list);
                countMap.put(shareId, response.getCount());
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnCamCommentChangedListener.class, false);
    }

    @Override
    public void syncOldCommentList(String shareId, String uk) {
        page++;
        CommentListResponse response = PubCamApi.getCommentList(shareId, uk, page, 40);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                // 刷新前先删除发送的临时数据，避免数据重复
                List<CamComment> comments = commentsMap.get(shareId);
                if (comments == null) {
                    commentsMap.put(shareId, new ArrayList<CamComment>());
                }
                for (CamComment comment : commentsTemp) {
                    comments.remove(comment);
                }

                List<CamComment> list = response.getList();
                compareAddToBottom(list, shareId);
                countMap.put(shareId, response.getCount());
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnCamCommentChangedListener.class, true);
    }

    @Override
    public void sendComment(String shareId, String deviceId, String commentStr, int parentId) {
        String avator = AccountWrapper.getAccountByUid(uid).getAvatar();
        CamComment comment = new CamComment();
        comment.setcId("");
        if (avator != null && avator.length() > 0) {
            comment.setAvator(avator);
        } else {
            comment.setAvator("http://tb.himg.baidu.com/sys/portrait/item/4ada77756169687561656c6c61377957");
        }
        comment.setContent(commentStr);
        comment.setUid(uid);
        comment.setDate(String.valueOf(new Date().getTime() / 1000));
        localId++;
        comment.setLocalId(localId);
        if (commentsMap.get(shareId) == null) {
            commentsMap.put(shareId, new ArrayList<CamComment>());
        }
        commentsMap.get(shareId).add(0, comment);
        commentsTemp.add(comment);
        sendListener(OnCamCommentChangedListener.class, false);

//        PubCamApi.sendComment(accessToken, deviceId, commentStr, parentId);
        CommentSendResponse response = PubCamApi.sendComment(accessToken, deviceId, commentStr, parentId);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                for (CamComment com : commentsMap.get(shareId)) {
                    if (com.getLocalId() == localId) {
                        com = response.getComment();
                    }
                }
                countMap.put(shareId, response.getCount());
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnCamCommentChangedListener.class, false);
    }

    @Override
    public List<CamComment> getCommentList(String shareId) {
        return commentsMap.get(shareId);
    }

    public BaseDanmakuParser getDanmuInputStream(String shareId) {
        if (commentsMap.get(shareId) == null) {
            commentsMap.put(shareId, new ArrayList<CamComment>());
        }
        InputStream is = commentsToInputstream(commentsMap.get(shareId));
        return createParser(is);
    }

    public String getUid() {
        return uid;
    }

    public int getCount(String shareId) {
        return (countMap.containsKey(shareId)) ? countMap.get(shareId) : 0;
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.uid = uid;
        this.accessToken = accessToken;
    }

    // 添加弹幕
    public void addDanmaku(IDanmakuView viewDanmu, String content) {
        BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        if (danmaku == null || viewDanmu == null) {
            return;
        }
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.priority = 1;
        danmaku.time = viewDanmu.getCurrentTime();
        danmaku.textSize = 50;
        danmaku.textColor = Color.YELLOW;
        viewDanmu.addDanmaku(danmaku);
    }

    public void beginDanmu(final IDanmakuView viewDanmu, final String shareId) {
        if (viewDanmu == null) {
            return;
        }

        List<CamComment> comments = commentsMap.get(shareId);
        if (comments == null) {
            comments = new ArrayList<CamComment>();
            commentsMap.put(shareId, comments);
        }
        synchronized (shareId) {
            this.isStop = false;
        }
        List<CamComment> finalComments = new ArrayList<CamComment>(comments);
        String language = LanguageUtil.getLanguage();
        while (!isStopDanmu(shareId)) {
            for (CamComment comment : finalComments) {
                int length = language.equals("zh") ? 40 : 140;
                if (comment.getContent().length() > length) {
                    continue;
                }
                BaseDanmaku danmaku = DanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
                if (danmaku == null) {
                    continue;
                }
                danmaku.text = comment.getContent();
                danmaku.padding = 5;
                danmaku.priority = 1;
                danmaku.time = viewDanmu.getCurrentTime();
                danmaku.textSize = 50;
                danmaku.textColor = Color.WHITE;
                viewDanmu.addDanmaku(danmaku);

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 数据变更之后更新弹幕数据
            finalComments.clear();
            finalComments.addAll(commentsMap.get(shareId));
        }
    }

    private boolean isStopDanmu(String shareId) {
        synchronized (shareId) {
            return isStop;
        }
    }

    public void setDanmuStudus(String shareId) {
        synchronized (shareId) {
            this.isStop = true;
        }
    }

    @Override
    public void favour(String deviceId) {
        PubCamApi.favour(accessToken, deviceId);
    }

    @Override
    public void store(String shareId, String uk) {
        Response response = PubCamApi.store(accessToken, shareId, uk);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                sendListener(OnStoreSuccessListener.class, bus);
                break;
            default:
                //TODO 通知界面错误
                break;
        }
    }

    @Override
    public void unStore(String shareId, String uk, String deviceId) {
        Response response = PubCamApi.unStore(accessToken, shareId, uk);
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                mimeCamBusiness.deleteCamLive(deviceId);
                break;
            default:
                //TODO 通知界面错误
                break;
        }
    }

    @Override
    public void findCamLive(String shareId, String uk) {
        CamMetaResponse res = PubCamApi.apiCamMeta(null, accessToken, shareId, uk);
        Business bus = res.getBusiness();
        CamLive live = null;
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                live = res.getCamLive();
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnCamLiveFindListener.class, live);
    }

    // 对比数据不重复，则插入到顶部
    private void compareAddToTop(List<CamComment> list, String shareId) {
        for (int i = list.size() - 1; i >= 0; i--) {
            CamComment comment1 = list.get(i);
            boolean isContain = false;
            for (CamComment comment : commentsMap.get(shareId)) {
                if (comment.getcId().equals(comment1.getcId())) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                commentsMap.get(shareId).add(0, comment1);
            }
        }
    }

    // 对比数据不重复，则插入到底部
    private void compareAddToBottom(List<CamComment> list, String shareId) {
        for (CamComment comment1 : list) {
            boolean isContain = false;
            for (int i = commentsMap.get(shareId).size() - 1; i >= 0; i--) {
                CamComment comment = commentsMap.get(shareId).get(i);
                if (comment.getcId().equals(comment1.getcId())) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                commentsMap.get(shareId).add(comment1);
            }
        }
    }

    // 将数据组装成xml，并且加载进流，并将流返回
    private InputStream commentsToInputstream(List<CamComment> list) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (Exception e) {
        }
        Document doc = builder.newDocument();

        Element root = doc.createElement("i");
        doc.appendChild(root); // 将根元素添加到文档上

//        float time = 0;
//        if (list == null) {
//            list = new ArrayList<CamComment>();
//        }
//        for (int i = 0; i < list.size(); i++) {
//            CamComment comment = list.get(i);
//            String content = comment.getContent();
//            Element d = doc.createElement("d");
//            time = time + 0.3f;
//            String strP = String.format("%f,1,20,16777215", time);
//            Logger.d("docdocdoc::" + strP);
//            d.setAttribute("p", strP);  // bili弹幕格式
//            Text tname = doc.createTextNode(content);
//            d.appendChild(tname);
//            root.appendChild(d);// 添加属性
//        }

        try {
            Source source = new DOMSource(doc);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            OutputStreamWriter write = new OutputStreamWriter(outStream);

            Result result = new StreamResult(write);

            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            xformer.transform(source, result);
            InputStream is = new ByteArrayInputStream(outStream.toByteArray());
            return is;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }
}
