package com.iermu.client.business.impl;

import android.content.Context;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IAccountAuthBusiness;
import com.iermu.client.listener.OnUserInfoListener;

/**
 * Created by wcy on 15/7/23.
 */
public class AccountAuthStrategy extends BaseBusinessStrategy implements IAccountAuthBusiness {

    private IAccountAuthBusiness mBusiness;

    public AccountAuthStrategy(IAccountAuthBusiness business) {
        super(business);
        this.mBusiness = business;
    }

    @Override
    public void addUserInfoListener(OnUserInfoListener listener) {
        mBusiness.addUserInfoListener(listener);
    }

    @Override
    public void removeUserInfoListener() {
        mBusiness.removeUserInfoListener();
    }

    @Override
    public void emailRegister(final String username, final String password, final String email) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.emailRegister(username, password, email);
            }
        });
    }

    @Override
    public void registerAccount(final String username, final String password) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.registerAccount(username, password);
            }
        });
    }

    @Override
    public void getAccessTokenAsQDLT(final String redirectUrl, final String connectUid) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getAccessTokenAsQDLT(redirectUrl, connectUid);
            }
        });
    }

    @Override
    public void getAccessToken(final String redirectUrl) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getAccessToken(redirectUrl);
            }
        });
    }

    @Override
    public void logout() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.logout();
            }
        });
    }

    @Override
    public void getNewPoster(final Context context) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getNewPoster(context);
            }
        });
    }

    @Override
    public boolean isLogin() {
        return  mBusiness.isLogin();
    }

    @Override
    public boolean isQDLTLogin(String connectUid) {
        return mBusiness.isQDLTLogin(connectUid);
    }

    @Override
    public String getAccessToken() {
        return mBusiness.getAccessToken();
    }

    @Override
    public String getUid() {
        return mBusiness.getUid();
    }

    @Override
    public void getUserInfo() {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getUserInfo();
            }
        });
    }

    @Override
    public void feedBack(final String opinion, final String contact) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.feedBack(opinion, contact);
            }
        });
    }

    @Override
    public void updateUserName(final String userName) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.updateUserName(userName);
            }
        });
    }

    @Override
    public void updatePassword(final String oldPassword, final String password) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.updatePassword(oldPassword, password);
            }
        });
    }

    @Override
    public void completeUserInfo(final String username, final String email, final String password) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.completeUserInfo(username, email, password);
            }
        });
    }

    @Override
    public void getThirdConnect() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getThirdConnect();
            }
        });
    }

    @Override
    public String getBaiduUid() {
        return mBusiness.getBaiduUid();
    }

    @Override
    public String getBaiduAccessToken() {
        return mBusiness.getBaiduAccessToken();
    }


}
