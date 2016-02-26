package com.iermu.client.business.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.iermu.client.business.dao.generator.AccountDao;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.model.Account;
import com.iermu.client.model.Connect;
import com.iermu.client.model.UserInfo;
import com.iermu.client.model.constant.ConnectType;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * 账号数据表操作
 *
 * Created by wcy on 15/9/9.
 */
public class AccountWrapper {

    @Inject static DaoSession daoSession;

    public static Account queryAccount() {
        AccountDao accountDao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = accountDao.queryBuilder();
        builder.where(AccountDao.Properties.Login.eq(1));
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        Account account = builder.unique();
        return account;
    }

    /**
     * 更新账号登录状态
     * @param uid
     */
    public static void logout(String uid) {
        SQLiteDatabase db = daoSession.getDatabase();
        QueryBuilder.LOG_SQL    = true;
        QueryBuilder.LOG_VALUES = true;
        ContentValues values = new ContentValues();
        values.put(AccountDao.Properties.Login.columnName, 0);
        db.update(AccountDao.TABLENAME, values, AccountDao.Properties.Login.columnName+"=?", new String[]{"1"});
    }

    /**
     * 插入一条账号数据: login=1 (如果数据表中存在则更新)
     * @param uid
     * @param accessToken
     * @param refreshToken
     */
    public static Account insert(String uid, String accessToken, String refreshToken) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            account = new Account();
        }
        account.setLogin(1);
        account.setUid(uid);
        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);
        dao.insertOrReplace(account);
        return account;
    }

//    /**
//     * 插入一条账号数据 (如果数据表中存在则更新)
//     * @param uid
//     * @param accessToken
//     * @param refreshToken
//     * @param baiduUid
//     * @param baiduAK
//     * @param baiduSK
//     * @param lyyUToken
//     * @param lyyUConfig
//     */
//    @Deprecated
//    public static void insert(String uid, String accessToken, String refreshToken
//            , String baiduUid, String baiduAK, String baiduSK
//            , String lyyUToken, String lyyUConfig, String bdAccount) {
//        AccountDao dao = daoSession.getAccountDao();
//        QueryBuilder<Account> builder = dao.queryBuilder();
//        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
//        if(account == null) {
//            account = new Account();
//        }
//        account.setLogin(1);
//        account.setUid(uid);
//        account.setAccessToken(accessToken);
//        account.setRefreshToken(refreshToken);
//        account.setLyyUToken(lyyUToken);
//        account.setLyyUConfig(lyyUConfig);
//        if(!TextUtils.isEmpty(baiduUid)) {
//            account.setBaiduUid(baiduUid);
//        }
//        if(!TextUtils.isEmpty(baiduAK)) {
//            account.setBaiduAK(baiduAK);
//        }
//        if(!TextUtils.isEmpty(baiduSK)) {
//            account.setBaiduSK(baiduSK);
//        }
//        if (!TextUtils.isEmpty(bdAccount)) {
//            account.setBaiduUName(bdAccount);
//        }
//        dao.insertOrReplace(account);
//    }

    /**
     * 更新第三方平台信息
     * @param uid           用户ID
     * @param connectList   平台数据
     */
    public static void updateConnect(String uid, List<Connect> connectList) {
        if(connectList == null || connectList.size()<=0) return;
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            account = new Account();
        }
        account.setUid(uid);
        for (int i=0; i<connectList.size(); i++) {
            Connect connect = connectList.get(i);
            int connectType = connect.getConnectType();
            if (connectType == ConnectType.LINYANG) {
                String lyyUToken    = connect.getUserToken();
                String lyyUConfig   = connect.getUserConfig();
                if(!TextUtils.isEmpty(lyyUToken))   account.setLyyUToken(lyyUToken);
                if(!TextUtils.isEmpty(lyyUConfig))  account.setLyyUConfig(lyyUConfig);
            } else if (connectType == ConnectType.BAIDU) {
                String baiduUid = connect.getUid();
                String baiduUName   = connect.getUserName();
                String baiduAK      = connect.getAccessToken();
                String baiduSK      = connect.getRefreshToken();
                if(!TextUtils.isEmpty(baiduUid))    account.setBaiduUid(baiduUid);
                if(!TextUtils.isEmpty(baiduAK))     account.setBaiduAK(baiduAK);
                if(!TextUtils.isEmpty(baiduSK))     account.setBaiduSK(baiduSK);
                if(!TextUtils.isEmpty(baiduUName))  account.setBaiduUName(baiduUName);
            } else if(connectType == ConnectType.QDLT) {
                String qdltUid      = connect.getUid();
                if(!TextUtils.isEmpty(qdltUid))     account.setQdltUid(qdltUid);
            }
        }
        dao.insertOrReplace(account);
    }

    /**
     * 保存用户信息
     * @param info
     */
    public static void insertUserInfo(UserInfo info) {
        if(info == null) return;
        String avatar   = info.getAvatar();
        String uid      = info.getUid();
        String userName = info.getUserName();
        String email    = info.getEmail();
        String mobile   = info.getMobile();
        int emailStatus = info.getEmailStatus();
        int mobileStatus = info.getMobileStatus();
        int avatarStatus = info.getAvatarStatus();

        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            account = new Account();
        }
        account.setUid(uid);
        account.setUname(userName);
        account.setAvatar(avatar);
        account.setEmail(email);
        account.setMobile(mobile);
        account.setEmailStatus(emailStatus);
        account.setMobileStatus(mobileStatus);
        account.setAvatarStatus(avatarStatus);
        dao.insertOrReplace(account);
    }

    public static void updateUserName(String uid, String userName) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            return;
        }
        account.setUname(userName);
        dao.insertOrReplace(account);
    }

    public static void updateUserAvatar(String uid, String avatar) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            return;
        }
        account.setAvatar(avatar);
        dao.insertOrReplace(account);
    }

    public static void updateUserInfo(String uid, String userName, String email) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            return;
        }
        account.setUname(userName);
        account.setEmail(email);
        dao.insertOrReplace(account);
    }

    public static void updateAccount(String uid, String baiduUid, String baiduAK, String baiduSK
            , String lyyUToken, String lyyUConfig, String bdAccount) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        Account account = builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
        if(account == null) {
            return;
        }
        if(!TextUtils.isEmpty(baiduUid)) {
            account.setBaiduUid(baiduUid);
        }
        if(!TextUtils.isEmpty(baiduAK)) {
            account.setBaiduAK(baiduAK);
        }
        if(!TextUtils.isEmpty(baiduSK)) {
            account.setBaiduSK(baiduSK);
        }
        if (!TextUtils.isEmpty(bdAccount)) {
            account.setBaiduUName(bdAccount);
        }
        account.setLyyUToken(lyyUToken);
        account.setLyyUConfig(lyyUConfig);
        dao.insertOrReplace(account);
    }

    /**
     * 获取账户信息
     *
     * @param uid
     * @return
     */
    public static Account getAccountByUid(String uid) {
        AccountDao dao = daoSession.getAccountDao();
        QueryBuilder<Account> builder = dao.queryBuilder();
        return builder.where(AccountDao.Properties.Uid.eq(uid)).unique();
    }

}
