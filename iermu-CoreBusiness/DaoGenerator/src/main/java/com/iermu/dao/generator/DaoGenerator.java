package com.iermu.dao.generator;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * 数据库表、Dao代码自动生成
 *
 *
 * 注意事项:
 *  1.数据库版本号: 必须大于升级脚本序号(DBMigrationHelper'XX') 例如:数据库版本为: 2, Helper脚本版本号对应为: 1
 *
 */
public class DaoGenerator {

    public static final int VERSION = 25;                                                           //数据库版本号
    public static final String DEFAULT_DAOPACKAGE    = "com.iermu.client.business.dao.generator";   //Dao层包路径
    public static final String DEFAULT_SCHEMAPACKAGE = "com.iermu.client.business.dao.schema";      //数据表Schema包路径
    public static final String DEFAULT_MODELPACKAGE  = "com.iermu.client.model";                    //业务模型包路径

    /**
     * 主函数
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        de.greenrobot.daogenerator.DaoGenerator generator = new de.greenrobot.daogenerator.DaoGenerator();
        Schema schema = new Schema(VERSION, DEFAULT_MODELPACKAGE);
        schema.setDefaultJavaPackageDao(DEFAULT_DAOPACKAGE);

        generatorAccount(args, schema);         // 账号信息表
        generatorAlarmImageData(args, schema);  // 报警消息表
        generatorCamSetting(args, schema);      // 设置信息表
        generatorWiFi(args, schema);            // wifi信息表
        generatorCloudPosition(args, schema);   // 预置位信息表
        generatorCamLive(args, schema);         // 设备信息列表

        generator.generateAll(schema, args[0]);
    }

    /**
     * 生成Account表
     *  _id、login、uid、uname、avatar、accessToken、refreshToken、
     *      baiduUid, baiduAK、baiduSK、lyyUid、lyyPwd、lyyAppId、qdltUid
     *
     *  login: --:账号信息被清除 0:退出登录 1:登录状态
     *
     * @param args
     * @param schema
     * @throws Exception
     */
    private static void generatorAccount(String args[], Schema schema) throws Exception {
        Entity box = schema.addProtobufEntity("Account");
        box.addIdProperty().autoincrement();
        box.addIntProperty("login");
        box.addStringProperty("uid").notNull().unique();    //UID唯一
        box.addStringProperty("accessToken");
        box.addStringProperty("refreshToken");
        //userInfo
        box.addStringProperty("uname");
        box.addStringProperty("avatar");
        box.addStringProperty("email");
        box.addStringProperty("mobile");
        box.addIntProperty("emailstatus");
        box.addIntProperty("mobilestatus");
        box.addIntProperty("avatarstatus");
        //connect
        box.addStringProperty("baiduUid");
        box.addStringProperty("baiduAK");
        box.addStringProperty("baiduSK");
        box.addStringProperty("baiduUName");
        box.addStringProperty("lyyUToken");
        box.addStringProperty("lyyUConfig");
        box.addStringProperty("qdltUid");
    }

    /**
     * 生成 CamSetting 表
     *
     * @param args
     * @param schema
     * @throws Exception
     */
    private static void generatorCamSetting(String args[], Schema schema) throws Exception {
        Entity box = schema.addProtobufEntity("CamSettingData");
        box.addIdProperty().autoincrement();
        box.addStringProperty("uniqueId").notNull().unique();
        box.addStringProperty("uid").notNull();
        box.addStringProperty("deviceId").notNull();
        box.addIntProperty("isAlarmOpen");
        box.addStringProperty("infoJson");
    }

    /**
     * 生成报警信息表
     *
     * @param args
     * @param schema
     * @throws Exception
     */
    private static void generatorAlarmImageData(String args[], Schema schema) throws Exception {
        Entity box = schema.addProtobufEntity("AlarmImageData");
        box.addIdProperty().primaryKey().autoincrement();
        box.addStringProperty("title");
        box.addStringProperty("description");
        box.addStringProperty("deviceId");
        box.addStringProperty("recdatetime");
        box.addStringProperty("alarmtime");
    }

    private static void testGenerate(String args[], Schema schema) throws Exception {
        Entity info = schema.addProtobufEntity("CamInfo");
        info.addStringProperty("model");
//        Entity box = schema.addEntity("Test");
//        box.addIdProperty();
//        box.addStringProperty("name");
//        box.addIntProperty("slots");
//        box.addStringProperty("description");
//        box.addIntProperty("index");
//        box.addStringProperty("email");
    }

    private static void generatorWiFi(String args[], Schema schema) throws Exception{
        Entity box = schema.addProtobufEntity("WifiInfo");
        box.addIdProperty().primaryKey().autoincrement();
        box.addStringProperty("ssid").unique();
        box.addStringProperty("account");
        box.addStringProperty("pass");
    }

    /**
     * 生成 CloudPosition 表
     *
     * @param args
     * @param schema
     * @throws Exception
     */
    private static void generatorCloudPosition(String args[], Schema schema) throws Exception {
        Entity box = schema.addProtobufEntity("CloudPosition");
//        box.addIdProperty().autoincrement();
        box.addStringProperty("uniqueId").notNull().unique().primaryKey();
        box.addStringProperty("uid").notNull();
        box.addStringProperty("deviceId").notNull();
        box.addIntProperty("preset").notNull();
        box.addStringProperty("title");
        box.addStringProperty("imagePath");
        box.addLongProperty("addDate").notNull();
    }

    /**
     * 临时表: 仅存储个人摄像机数据
     * @param args
     * @param schema
     */
    private static void generatorCamLive(String args[], Schema schema) {
        Entity box = schema.addProtobufEntity("CamLive");
        box.addStringProperty("uniqueId").notNull().unique().primaryKey();  //uid_deviceid
        box.addStringProperty("uid").notNull();
        box.addStringProperty("deviceId").notNull();
        box.addStringProperty("shareId");
        box.addStringProperty("uk");
        box.addStringProperty("description");
        box.addIntProperty("shareType");
        box.addIntProperty("status");
        box.addStringProperty("thumbnail");
        box.addIntProperty("dataType");

        box.addIntProperty("connectType");
        box.addStringProperty("connectCid");
        box.addStringProperty("streamId");
        box.addStringProperty("cvrDay");
        box.addLongProperty("cvrEndTime");

        box.addStringProperty("avator");
        box.addStringProperty("ownerName");
        box.addIntProperty("personNum");
        box.addStringProperty("goodNum");
        box.addIntProperty("storeStatus");
        box.addIntProperty("grantNum");
    }
}
