package com.iermu.client.business.api.response;

/**
 * 响应错误码
 *
 * Created by wcy on 15/6/23.
 */
public class ErrorCode {

    public static final int UNDEFINED               = 0;   //未定义
    //客户端自定义
    public static final int SUCCESS                 = 1;   //表示成功

    //iermu 服务器定义状态码
    public static final int PARAM_ERROR             = 31023;//访问的参数错误
    public static final int NETWORK_ERROR           = 31021;//网络错误
    public static final int DEVICE_ALREADY_REGISTED = 31350;//设备已经注册
    public static final int ADD_DEVICE_FAILED       = 31352;//添加设备失败
    public static final int NO_PERMISSION           = 31354;//没有权限

    public static final int GRANT_INVALID           = 400513;//授权码失效
    public static final int GRANT_USED              = 400514;//授权码已使用
    public static final int NOT_GRANT_SELF          = 400515;//不能授权给自己
    public static final int DEVICE_GRANT_FAILED     = 400516;//授权失败

    public static final int DEVICE_NOT_EXIST        = 31353;//被删除设备不存在
    public static final int DELETE_DEVICE_GRANT_FAILE= 31383;//删除授权设备失败

    public static final int CONNECT_API_FAILED      = 400300;//连接百度Api失败(设备不在线｜百度服务器出错)
    public static final int SEND_COMMAND_FAILED     = 31388; //发送命令到设备失败(设备不在线｜百度服务器出错)
    public static final int UPDATE_DEVSETTING_FAILED=400003; //更新设备设置信息失败(设备不在线｜百度服务器出错)

    public static final int CONNECT_USERTOKEN_INVALID=400303;//连接UserToken无效(羚羊云)
    public static final int CONNECT_TYPE_FAIL       = 300100;//连接平台类型不能变更 device connect type cannot change


    public static final int FORBIDENT_CREATE_SHARE  = 31373;//该设备不能创建分享 forbident to create share
    public static final int API_CLOSED              = 50201;//服务器更新导致错误 api closed
    public static final int ACCESS_TOKEN_INVALID    = 110;  //AccessToken过期

    //register
    public static final int OAUTH2_INVALID_CLIENT   = 40013;//无效的应用请求
    public static final int USER_CHECK_USERNAME_FAILED  = 40031;//用户名无效
    public static final int USER_USERNAME_BADWORD   = 40032;//用户名禁用
    public static final int USER_USERNAME_EXISTS    = 40033;//用户名已存在
    public static final int USER_EMAIL_FORMAT_ILLEGAL   = 40034;//邮箱格式非法
    public static final int USER_EMAIL_ACCESS_ILLEGAL   = 40035;//邮箱禁用
    public static final int USER_EMAIL_EXSITS       = 40036;//邮箱已存在
    public static final int USER_ADD_FAILED         = 40038;//添加用户失败
    public static final int API_ERROR_INVALID_CLIENT= 40101;
    public static final int API_ERROR_NO_AUTH       = 40102;

    //login
    public static final int OAUTH2_INVALID_REQUEST        = 40014;
    public static final int OAUTH2_UNAUTHORIZED_CLIENT    = 40018;
    public static final int OAUTH2_REDIRECT_URI_MISMATCH  = 40020;
    public static final int OAUTH2_ACCESS_DENIED          = 40302;
    public static final int OAUTH2_UNSUPPORTED_RESPONSE_TYPE    = 40021;
    public static final int OAUTH2_INVALID_SCOPE          = 40022;
    public static final int OAUTH2_INVALID_GRANT          = 40019;
    public static final int OAUTH2_UNSUPPORTED_GRANT_TYPE = 40017;
    public static final int OAUTH2_INSUFFICIENT_SCOPE     = 40303;
    public static final int REFRESH_TOKEN_INVALID         = 40119;
    public static final int OAUTH2_ERROR_USER_NOT_EXIST   = 40120;//用户不存在
    public static final int OAUTH2_ERROR_USER_PASSWORD    = 40121;//密码错误

    //update username
    public static final int NO_AUTH = 40102;//无权限
    public static final int UPDATE_USERNAME_FAILED = 400411;//修改用户名失败

    //完善资料
    public static final int USER_PROFILE_ALREADY_COMPLETED = 40050;//用户资料完整，不需要完善资料
    public static final int USER_PROFILE_COMPLETE_FAILED = 40051;//修改密码失败
    public static final int USER_NOT_EXIST                = 40040;//用户不存在
    public static final int PASSWORD_ERROR                = 40041;//密码错误
    public static final int USER_GET_CONNECT_FAILED = 40061;//获取第三方信息失败



}
