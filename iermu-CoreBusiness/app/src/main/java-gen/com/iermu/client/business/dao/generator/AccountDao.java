package com.iermu.client.business.dao.generator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.iermu.client.model.Account;
import com.iermu.client.model.Account.Builder;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCOUNT".
*/
public class AccountDao extends AbstractDao<Account, Long> {

    public static final String TABLENAME = "ACCOUNT";

    /**
     * Properties of entity Account.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Login = new Property(1, Integer.class, "login", false, "LOGIN");
        public final static Property Uid = new Property(2, String.class, "uid", false, "UID");
        public final static Property AccessToken = new Property(3, String.class, "accessToken", false, "ACCESS_TOKEN");
        public final static Property RefreshToken = new Property(4, String.class, "refreshToken", false, "REFRESH_TOKEN");
        public final static Property Uname = new Property(5, String.class, "uname", false, "UNAME");
        public final static Property Avatar = new Property(6, String.class, "avatar", false, "AVATAR");
        public final static Property Email = new Property(7, String.class, "email", false, "EMAIL");
        public final static Property Mobile = new Property(8, String.class, "mobile", false, "MOBILE");
        public final static Property Emailstatus = new Property(9, Integer.class, "emailstatus", false, "EMAILSTATUS");
        public final static Property Mobilestatus = new Property(10, Integer.class, "mobilestatus", false, "MOBILESTATUS");
        public final static Property Avatarstatus = new Property(11, Integer.class, "avatarstatus", false, "AVATARSTATUS");
        public final static Property BaiduUid = new Property(12, String.class, "baiduUid", false, "BAIDU_UID");
        public final static Property BaiduAK = new Property(13, String.class, "baiduAK", false, "BAIDU_AK");
        public final static Property BaiduSK = new Property(14, String.class, "baiduSK", false, "BAIDU_SK");
        public final static Property BaiduUName = new Property(15, String.class, "baiduUName", false, "BAIDU_UNAME");
        public final static Property LyyUToken = new Property(16, String.class, "lyyUToken", false, "LYY_UTOKEN");
        public final static Property LyyUConfig = new Property(17, String.class, "lyyUConfig", false, "LYY_UCONFIG");
        public final static Property QdltUid = new Property(18, String.class, "qdltUid", false, "QDLT_UID");
    };


    public AccountDao(DaoConfig config) {
        super(config);
    }
    
    public AccountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCOUNT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"LOGIN\" INTEGER," + // 1: login
                "\"UID\" TEXT NOT NULL UNIQUE ," + // 2: uid
                "\"ACCESS_TOKEN\" TEXT," + // 3: accessToken
                "\"REFRESH_TOKEN\" TEXT," + // 4: refreshToken
                "\"UNAME\" TEXT," + // 5: uname
                "\"AVATAR\" TEXT," + // 6: avatar
                "\"EMAIL\" TEXT," + // 7: email
                "\"MOBILE\" TEXT," + // 8: mobile
                "\"EMAILSTATUS\" INTEGER," + // 9: emailstatus
                "\"MOBILESTATUS\" INTEGER," + // 10: mobilestatus
                "\"AVATARSTATUS\" INTEGER," + // 11: avatarstatus
                "\"BAIDU_UID\" TEXT," + // 12: baiduUid
                "\"BAIDU_AK\" TEXT," + // 13: baiduAK
                "\"BAIDU_SK\" TEXT," + // 14: baiduSK
                "\"BAIDU_UNAME\" TEXT," + // 15: baiduUName
                "\"LYY_UTOKEN\" TEXT," + // 16: lyyUToken
                "\"LYY_UCONFIG\" TEXT," + // 17: lyyUConfig
                "\"QDLT_UID\" TEXT);"); // 18: qdltUid
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCOUNT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Account entity) {
        stmt.clearBindings();
        if(entity.hasId()) {
            stmt.bindLong(1, entity.getId());
        }
        if(entity.hasLogin()) {
            stmt.bindLong(2, entity.getLogin());
        }
        if(entity.hasUid()) {
            stmt.bindString(3, entity.getUid());
        }
        if(entity.hasAccessToken()) {
            stmt.bindString(4, entity.getAccessToken());
        }
        if(entity.hasRefreshToken()) {
            stmt.bindString(5, entity.getRefreshToken());
        }
        if(entity.hasUname()) {
            stmt.bindString(6, entity.getUname());
        }
        if(entity.hasAvatar()) {
            stmt.bindString(7, entity.getAvatar());
        }
        if(entity.hasEmail()) {
            stmt.bindString(8, entity.getEmail());
        }
        if(entity.hasMobile()) {
            stmt.bindString(9, entity.getMobile());
        }
        if(entity.hasEmailstatus()) {
            stmt.bindLong(10, entity.getEmailstatus());
        }
        if(entity.hasMobilestatus()) {
            stmt.bindLong(11, entity.getMobilestatus());
        }
        if(entity.hasAvatarstatus()) {
            stmt.bindLong(12, entity.getAvatarstatus());
        }
        if(entity.hasBaiduUid()) {
            stmt.bindString(13, entity.getBaiduUid());
        }
        if(entity.hasBaiduAK()) {
            stmt.bindString(14, entity.getBaiduAK());
        }
        if(entity.hasBaiduSK()) {
            stmt.bindString(15, entity.getBaiduSK());
        }
        if(entity.hasBaiduUName()) {
            stmt.bindString(16, entity.getBaiduUName());
        }
        if(entity.hasLyyUToken()) {
            stmt.bindString(17, entity.getLyyUToken());
        }
        if(entity.hasLyyUConfig()) {
            stmt.bindString(18, entity.getLyyUConfig());
        }
        if(entity.hasQdltUid()) {
            stmt.bindString(19, entity.getQdltUid());
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Account readEntity(Cursor cursor, int offset) {
        Builder builder = Account.newBuilder();
        if (!cursor.isNull(offset + 0)) {
            builder.setId(cursor.getLong(offset + 0));
        }
        if (!cursor.isNull(offset + 1)) {
            builder.setLogin(cursor.getInt(offset + 1));
        }
        builder.setUid(cursor.getString(offset + 2));
        if (!cursor.isNull(offset + 3)) {
            builder.setAccessToken(cursor.getString(offset + 3));
        }
        if (!cursor.isNull(offset + 4)) {
            builder.setRefreshToken(cursor.getString(offset + 4));
        }
        if (!cursor.isNull(offset + 5)) {
            builder.setUname(cursor.getString(offset + 5));
        }
        if (!cursor.isNull(offset + 6)) {
            builder.setAvatar(cursor.getString(offset + 6));
        }
        if (!cursor.isNull(offset + 7)) {
            builder.setEmail(cursor.getString(offset + 7));
        }
        if (!cursor.isNull(offset + 8)) {
            builder.setMobile(cursor.getString(offset + 8));
        }
        if (!cursor.isNull(offset + 9)) {
            builder.setEmailstatus(cursor.getInt(offset + 9));
        }
        if (!cursor.isNull(offset + 10)) {
            builder.setMobilestatus(cursor.getInt(offset + 10));
        }
        if (!cursor.isNull(offset + 11)) {
            builder.setAvatarstatus(cursor.getInt(offset + 11));
        }
        if (!cursor.isNull(offset + 12)) {
            builder.setBaiduUid(cursor.getString(offset + 12));
        }
        if (!cursor.isNull(offset + 13)) {
            builder.setBaiduAK(cursor.getString(offset + 13));
        }
        if (!cursor.isNull(offset + 14)) {
            builder.setBaiduSK(cursor.getString(offset + 14));
        }
        if (!cursor.isNull(offset + 15)) {
            builder.setBaiduUName(cursor.getString(offset + 15));
        }
        if (!cursor.isNull(offset + 16)) {
            builder.setLyyUToken(cursor.getString(offset + 16));
        }
        if (!cursor.isNull(offset + 17)) {
            builder.setLyyUConfig(cursor.getString(offset + 17));
        }
        if (!cursor.isNull(offset + 18)) {
            builder.setQdltUid(cursor.getString(offset + 18));
        }
        return builder.build();
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Account entity, int offset) {
        throw new UnsupportedOperationException("Protobuf objects cannot be modified");
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Account entity, long rowId) {
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Account entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return false;
    }
    
}
