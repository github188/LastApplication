package com.iermu.client.model;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table "ACCOUNT".
 */
public class Account {

    private Long id;
    private int     login;
    private String  uid;
    private String  accessToken;
    private String  refreshToken;
    //userInfo
    private String  uname;
    private String  avatar;
    private String  email;           //邮箱
    private String  mobile;          //手机
    private int     emailstatus;    //邮箱状态
    private int     mobilestatus;   //手机状态
    private int     avatarstatus;   //头像是否上传
    //connect
    private String baiduUid;
    private String baiduAK;
    private String baiduSK;
    private String baiduUName;
    private String lyyUToken;
    private String lyyUConfig;
    private String qdltUid;         //青岛联通Uid(手机号)

    public Account() {
    }

    public Account(Long id) {
        this.id = id;
    }

    public Account(Long id, Integer login, String uid, String uname, String avatar, String accessToken, String refreshToken
            , String baiduUid, String baiduAK, String baiduSK, String lyyUToken, String lyyUConfig, String qdltUid) {
        this.id = id;
        this.login = login;
        this.uid = uid;
        this.uname = uname;
        this.avatar = avatar;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.baiduUid = baiduUid;
        this.baiduAK = baiduAK;
        this.baiduSK = baiduSK;
        this.lyyUToken = lyyUToken;
        this.lyyUConfig = lyyUConfig;
        this.qdltUid = qdltUid;
    }

    public boolean isLogin() {
        return login == 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLogin() {
        return login;
    }

    public void setLogin(Integer login) {
        this.login = login;
    }

    /**
     * Not-null value.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Not-null value.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Not-null value.
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Not-null value; ensure this value is available before it is saved to the database.
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getBaiduUid() {
        return baiduUid;
    }

    public void setBaiduUid(String baiduUid) {
        this.baiduUid = baiduUid;
    }

    public String getBaiduAK() {
        return baiduAK;
    }

    public void setBaiduAK(String baiduAK) {
        this.baiduAK = baiduAK;
    }

    public String getBaiduSK() {
        return baiduSK;
    }

    public void setBaiduSK(String baiduSK) {
        this.baiduSK = baiduSK;
    }

    public String getLyyUToken() {
        return lyyUToken;
    }

    public void setLyyUToken(String lyyUToken) {
        this.lyyUToken = lyyUToken;
    }

    public String getLyyUConfig() {
        return lyyUConfig;
    }

    public void setLyyUConfig(String lyyUConfig) {
        this.lyyUConfig = lyyUConfig;
    }

    public String getQdltUid() {
        return qdltUid;
    }

    public void setQdltUid(String qdltUid) {
        this.qdltUid = qdltUid;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean hasId() {
        return false;
    }

    public boolean hasLogin() {
        return true;
    }

    public boolean hasUid() {
        return true;
    }

    public boolean hasUname() {
        return uname != null;
    }

    public boolean hasAvatar() {
        return avatar != null;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }

    public boolean hasRefreshToken() {
        return refreshToken != null;
    }

    public boolean hasBaiduUid() {
        return baiduUid != null;
    }

    public boolean hasBaiduAK() {
        return baiduAK != null;
    }

    public boolean hasBaiduSK() {
        return baiduSK != null;
    }

    public boolean hasLyyUToken() {
        return lyyUToken != null;
    }

    public boolean hasLyyUConfig() {
        return lyyUConfig != null;
    }

    public boolean hasQdltUid() {
        return qdltUid != null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setEmailStatus(int emailStatus) {
        this.emailstatus = emailStatus;
    }

    public void setMobileStatus(int mobileStatus) {
        this.mobilestatus = mobileStatus;
    }

    public void setAvatarStatus(int avatarStatus) {
        this.avatarstatus = avatarStatus;
    }

    public boolean hasEmail() {
        return email != null;
    }

    public boolean hasMobile() {
        return mobile != null;
    }

    public boolean hasEmailstatus() {
        return true;
    }

    public boolean hasMobilestatus() {
        return true;
    }

    public boolean hasAvatarstatus() {
        return true;
    }

    public int getEmailstatus() {
        return emailstatus;
    }

    public int getMobilestatus() {
        return mobilestatus;
    }

    public int getAvatarstatus() {
        return avatarstatus;
    }

    public String getBaiduUName() {
        return baiduUName;
    }

    public void setBaiduUName(String baiduUName) {
        this.baiduUName = baiduUName;
    }

    public boolean hasBaiduUName() {
        return baiduUName != null;
    }

    public static class Builder {

        private long id;
        private int login;
        private String uid;
        private String uname;
        private String avatar;
        private String accessToken;
        private String refreshToken;
        private String baiduUid;
        private String baiduAK;
        private String baiduSK;
        private String baiduUName;
        private String lyyUToken;
        private String lyyUConfig;
        private String qdltUid;
        private String email;
        private String mobile;
        private int emailstatus;
        private int mobilestatus;
        private int avatarstatus;

        public void setId(long id) {
            this.id = id;
        }

        public void setLogin(int login) {
            this.login = login;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUid() {
            return uid;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setBaiduUid(String baiduUid) {
            this.baiduUid = baiduUid;
        }

        public void setBaiduAK(String baiduAK) {
            this.baiduAK = baiduAK;
        }

        public void setBaiduSK(String baiduSK) {
            this.baiduSK = baiduSK;
        }

        public void setLyyUToken(String lyyUToken) {
            this.lyyUToken = lyyUToken;
        }

        public void setLyyUConfig(String lyyUConfig) {
            this.lyyUConfig = lyyUConfig;
        }

        public Account build() {
            Account account = new Account();
            account.setId(id);
            account.setLogin(login);
            account.setUid(uid);
            account.setUname(uname);
            account.setAvatar(avatar);
            account.setAccessToken(accessToken);
            account.setRefreshToken(refreshToken);
            account.setBaiduUid(baiduUid);
            account.setBaiduAK(baiduAK);
            account.setBaiduSK(baiduSK);
            account.setLyyUToken(lyyUToken);
            account.setLyyUConfig(lyyUConfig);
            account.setQdltUid(qdltUid);
            account.setEmail(email);
            account.setMobile(mobile);
            account.setEmailStatus(emailstatus);
            account.setMobileStatus(mobilestatus);
            account.setAvatarStatus(avatarstatus);
            account.setBaiduUName(baiduUName);
            return account;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public int getEmailStatus() {
            return emailstatus;
        }

        public int getMobileStatus() {
            return mobilestatus;
        }

        public int getAvatarStatus() {
            return avatarstatus;
        }

        public void setEmailstatus(int emailstatus) {
            this.emailstatus = emailstatus;
        }

        public int getEmailstatus() {
            return emailstatus;
        }

        public void setMobilestatus(int mobilestatus) {
            this.mobilestatus = mobilestatus;
        }

        public int getMobilestatus() {
            return mobilestatus;
        }

        public void setAvatarstatus(int avatarstatus) {
            this.avatarstatus = avatarstatus;
        }

        public int getAvatarstatus() {
            return avatarstatus;
        }

        public String getBaiduUName() {
            return baiduUName;
        }

        public void setBaiduUName(String baiduUName) {
            this.baiduUName = baiduUName;
        }

        public void setQdltUid(String qdltUid) {
            this.qdltUid = qdltUid;
        }

        public String getQdltUid() {
            return qdltUid;
        }
    }

}