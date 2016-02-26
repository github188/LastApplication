package com.iermu.apiservice.body;

/**
 * 摄像机列表
 *
 * Created by wcy on 15/7/16.
 */
public class DeviceListBody {

    private String  method;     //函数名
    private int     device_type;//设备类型,默认为1, 预留字段
    private String  data_type;  //数据类型, 默认为'my'我的设备;'grant'授权设备,'sub'订阅设备,'all'所有以上
    private String  list_type;  //列表类型, 默认为'all'所有设备, 'page'分页返回
    private int     page;       //页数, 默认为1, listType为page时有效
    private int     count;      //每页数据条数, 默认为10, listType为page时有效
    private String  access_token; //

    /**
     * 获取摄像机列表Body
     * @param method
     * @param devType
     * @param dataType
     * @param listType
     * @param page
     * @param count
     * @param accessToken
     */
    public DeviceListBody(String method, int devType, String dataType, String listType, int page, int count, String accessToken) {
        this.method      = method;
        this.device_type = devType;
        this.data_type   = dataType;
        this.list_type   = listType;
        this.page        = (page<=0) ? 1 : page;
        this.count       = (count<=0) ? 10 : count;
        this.access_token= accessToken;
    }


}
