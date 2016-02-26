package com.iermu.client;

import com.iermu.client.model.GrantUser;

import java.util.List;

/**
 * 分享、授权业务模块
 *
 * Created by zhoushaopei on 15/7/13.
 */
public interface ICamShareBusiness extends IBaseBusiness {

    /**
     * 获取授权用户列表
     * @param deviceId
     */
    public void syncGrantUsers(String deviceId);

    /**
     * 创建分享链接
     *
     * @param deviceId
     * @param introduce
     * @param shareType
     */
    public void createShare(String deviceId, String introduce, int shareType);

    /**
     * 取消分享链接
     *
     * @param deviceId
     */
    public void cancleShare(String deviceId);

    /**
     * 获取设备授权码
     *
     * @param deviceId
     */
    public void getGrantCode(String deviceId);

    /**
     * 把云摄像头授权给其他用户
     *
     * @param code
     */
    public void grantShare(String code);

    /**
     * 创建分享链接
     * @param deviceId
     * @return
     */
    public String getShareLink(String deviceId);

    /**
     * 获取授权用户列表
     */
    public List<GrantUser> getGrantUser(String deviceId);

    /**
     * 删除设备授权用户信息
     *
     * @param deviceId
     * @param uk
     */
    public void dropGrantUser(String deviceId, String uk);

    /**
     * 删除某个用户名下被授权的云摄像头
     *
     * @param deviceId
     */
    public void dropGrantDevice(String deviceId);

}
