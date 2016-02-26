package com.iermu.client.model.constant;

import com.iermu.client.business.api.response.ErrorCode;
import com.iermu.client.business.api.response.Response;

/**
 * 业务层状态码定义
 *
 * Created by wcy on 15/6/25.
 */
public class BusinessCode {

    public static final int UNDEFINED       = 0x0000;   //未定义


    //Http响应状态 (HTTP网络层)
    public static final int SUCCESS         = 0x0001;   //成功
    public static final int RESPONSE_ERROR  = 0x0002;   //网络响应错误
    public static final int HTTP_ERROR      = 0x0003;   //HttpExecption错误
    public static final int IO_ERROR        = 0x0004;   //IOExecption错误
    public static final int JSON_ERROR      = 0x0005;   //JsonExecption错误
    public static final int LAN_ERROR       = 0x0006;   //局域网错误


    //业务状态码对应(客户端)
    public static final int WIFI_CLOSE      = 0x0007;   //Wifi连接已关闭
    public static final int DEV_NOTACTIVE   = 0x0008;   //设备未激活
    public static final int NOTFIND_SCANDEV = 0x0009;   //扫描设备失败(没有扫描到摄像机设备)


    //Api接口ErrorCode对应状态码(服务器)
    //SetupDev
    public static final int DEV_REGISTED    = 0x0010;   //设备已注册
    public static final int ADD_DEVICE_FAILED= 0x0011;  //添加设备失败
    public static final int NO_PERMISSION   = 0x0012;   //没有权限
    public static final int CONNECT_TYPE_FAIL= 0x0013;
    //ShareAuth TODO
    public static final int GRANT_INVALID           = 0x0014;//授权码失效
    public static final int GRANT_USED              = 0x0015;//授权码已使用
    public static final int NOT_GRANT_SELF          = 0x0016;//不能授权给自己
    public static final int DEVICE_GRANT_FAILED     = 0x0017;//授权失败
    //Setting
    public static final int CONNECT_API_FAILED      = 0x0018;//连接百度Api失败(设备不在线｜百度服务器出错)
    public static final int SEND_COMMAND_FAILED     = 0x0019;//发送命令到设备失败(设备不在线｜百度服务器出错)
    public static final int UPDATE_DEVSETTING_FAILED= 0x0020;//更新设备设置信息失败(设备不在线｜百度服务器出错)
    //DropAuthDev
    public static final int DEVICE_NOT_EXIST        = 0x0021;//被删除设备不存在
    public static final int DELETE_DEVICE_GRANT_FAILE= 0x0022;//删除授权设备失败
    public static final int FORBIDENT_CREATE_SHARE  = 0x0023;//该设备不能创建分享
    //Other
    public static final int API_CLOSED              = 0x0024;//服务器更新导致错误 api closed
    public static final int ACCESS_TOKEN_INVALID    = 0x0025;//AccessToken过期
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
    public static final int LYY_AUTHFAIL                  = 0x0044;   //羚羊云登录失败

    public static final int HIWIFI_CONNECT_FAIL           = 0x0026;//AccessToken过期
    //update username
    public static final int NO_AUTH                       = 40102;//无权限
    public static final int UPDATE_USERNAME_FAILED        = 400411;//修改用户名失败

    //完善资料
    public static final int USER_PROFILE_ALREADY_COMPLETED= 40050;//用户资料完整，不需要完善资料
    public static final int USER_PROFILE_COMPLETE_FAILED  = 40051;//完善资料失败
    public static final int USER_NOT_EXIST                = 40040;//用户不存在
    public static final int PASSWORD_ERROR                = 40041;//密码错误
    public static final int USER_GET_CONNECT_FAILED       = 40061;//获取第三方信息失败


    public static final int PUSH_FAILED             = 0x0027;// 绑定百度推送失败
    public static final int REGISTE_FILED           = 0x0028;// 打开推送通道失败
    //{"error_code":31373,"error_msg":"forbident to create share"


    /**
     * 匹配服务器响应错误状态码对应业务状态码
     * @param response
     * @return
     */
    public static int matchErrorCode(Response response) {
        if(response == null) {
            return RESPONSE_ERROR;
        }
        return matchErrorCode(response.getErrorCode());
    }

    /**
     * 匹配服务器响应错误状态码对应业务状态码
     * @param errorCode
     * @return
     */
    public static int matchErrorCode(int errorCode) {
        int code = UNDEFINED;
        switch(errorCode) {
        case ErrorCode.SUCCESS:
            code = SUCCESS;
            break;
        case ErrorCode.PARAM_ERROR:
            //
            break;
        case ErrorCode.NETWORK_ERROR:
            code = HTTP_ERROR;
            break;
        case ErrorCode.API_CLOSED:
            code = API_CLOSED;
            break;
        case ErrorCode.ACCESS_TOKEN_INVALID:
            code = ACCESS_TOKEN_INVALID;
            break;
        case ErrorCode.DEVICE_ALREADY_REGISTED:
            code = DEV_REGISTED;
            break;
        case ErrorCode.ADD_DEVICE_FAILED:
            code = ADD_DEVICE_FAILED;
            break;
        case ErrorCode.NO_PERMISSION:
            code= NO_PERMISSION;
            break;
        case ErrorCode.GRANT_INVALID:
            code= GRANT_INVALID;
            break;
        case ErrorCode.GRANT_USED:
            code= GRANT_USED;
            break;
        case ErrorCode.NOT_GRANT_SELF:
            code= NOT_GRANT_SELF;
            break;
        case ErrorCode.DEVICE_GRANT_FAILED:
            code= DEVICE_GRANT_FAILED;
            break;
        case ErrorCode.CONNECT_API_FAILED:
            code = CONNECT_API_FAILED;
            break;
        case ErrorCode.SEND_COMMAND_FAILED:
            code = SEND_COMMAND_FAILED;
            break;
        case ErrorCode.UPDATE_DEVSETTING_FAILED:
            code = UPDATE_DEVSETTING_FAILED;
            break;
        case ErrorCode.DEVICE_NOT_EXIST:
            code = DEVICE_NOT_EXIST;
            break;
        case ErrorCode.DELETE_DEVICE_GRANT_FAILE:
            code = DELETE_DEVICE_GRANT_FAILE;
            break;
        case ErrorCode.CONNECT_TYPE_FAIL:
            code = CONNECT_TYPE_FAIL;
            break;
        case ErrorCode.FORBIDENT_CREATE_SHARE:
            code = FORBIDENT_CREATE_SHARE;
            break;
        case ErrorCode.OAUTH2_INVALID_CLIENT:
            code = OAUTH2_INVALID_CLIENT;
            break;
        case ErrorCode.USER_CHECK_USERNAME_FAILED:
            code = USER_CHECK_USERNAME_FAILED;
            break;
        case ErrorCode.USER_USERNAME_BADWORD:
            code = USER_USERNAME_BADWORD;
            break;
        case ErrorCode.USER_USERNAME_EXISTS:
            code = USER_USERNAME_EXISTS;
            break;
        case ErrorCode.USER_EMAIL_FORMAT_ILLEGAL:
            code = USER_EMAIL_FORMAT_ILLEGAL;
            break;
        case ErrorCode.USER_EMAIL_ACCESS_ILLEGAL:
            code = USER_EMAIL_ACCESS_ILLEGAL;
            break;
        case ErrorCode.USER_EMAIL_EXSITS:
            code = USER_EMAIL_EXSITS;
            break;
        case ErrorCode.USER_ADD_FAILED:
            code = USER_ADD_FAILED;
            break;
        case ErrorCode.API_ERROR_INVALID_CLIENT:
            code = API_ERROR_INVALID_CLIENT;
            break;
        case ErrorCode.API_ERROR_NO_AUTH:
            code = API_ERROR_NO_AUTH;
        break;
        case ErrorCode.OAUTH2_INVALID_REQUEST:
            code = OAUTH2_INVALID_REQUEST;
            break;
        case ErrorCode.OAUTH2_UNAUTHORIZED_CLIENT:
            code = OAUTH2_UNAUTHORIZED_CLIENT;
            break;
        case ErrorCode.OAUTH2_REDIRECT_URI_MISMATCH:
            code = OAUTH2_REDIRECT_URI_MISMATCH;
            break;
        case ErrorCode.OAUTH2_ACCESS_DENIED:
            code = OAUTH2_ACCESS_DENIED;
            break;
        case ErrorCode.OAUTH2_UNSUPPORTED_RESPONSE_TYPE:
            code = OAUTH2_UNSUPPORTED_RESPONSE_TYPE;
            break;
        case ErrorCode.OAUTH2_INVALID_SCOPE:
            code = OAUTH2_INVALID_SCOPE;
            break;
        case ErrorCode.OAUTH2_INVALID_GRANT:
            code = OAUTH2_INVALID_GRANT;
            break;
        case ErrorCode.OAUTH2_UNSUPPORTED_GRANT_TYPE:
            code = OAUTH2_UNSUPPORTED_GRANT_TYPE;
            break;
        case ErrorCode.OAUTH2_INSUFFICIENT_SCOPE:
            code = OAUTH2_INSUFFICIENT_SCOPE;
            break;
        case ErrorCode.REFRESH_TOKEN_INVALID:
            code = REFRESH_TOKEN_INVALID;
            break;
        case ErrorCode.OAUTH2_ERROR_USER_NOT_EXIST:
            code = OAUTH2_ERROR_USER_NOT_EXIST;
            break;
        case ErrorCode.OAUTH2_ERROR_USER_PASSWORD:
            code = OAUTH2_ERROR_USER_PASSWORD;
            break;

        case ErrorCode.UPDATE_USERNAME_FAILED:
            code = UPDATE_USERNAME_FAILED;
            break;
        case ErrorCode.USER_PROFILE_ALREADY_COMPLETED:
            code = USER_PROFILE_ALREADY_COMPLETED;
            break;
        case ErrorCode.USER_PROFILE_COMPLETE_FAILED:
            code = USER_PROFILE_COMPLETE_FAILED;
            break;
        case ErrorCode.USER_GET_CONNECT_FAILED:
            code = USER_GET_CONNECT_FAILED;
            break;
        case ErrorCode.USER_NOT_EXIST:
            code = USER_NOT_EXIST;
            break;
        case ErrorCode.PASSWORD_ERROR:
            code = PASSWORD_ERROR;
            break;
        }
        return code;
    }
}
