package com.iermu.lan.model;

/**
 * Created by zsj on 15/10/15.
 */
public enum ErrorCode {

        SUCCESS("发送指令成功", 0), NETEXCEPT("网络异常", 1), EXECUTEFAILED("命令执行失败", 2),
        NAS_USERNAME_PWD_ERROR("用户名或密码不正确",3),NAS_IP_ERROR("ip不正确",4);
        // 成员变量
        private String name;
        private int index;

        // 构造方法
        private ErrorCode(String name, int index) {
            this.name = name;
            this.index = index;
        }

        // 普通方法
        public static String getName(int index) {
            for (ErrorCode c : ErrorCode.values()) {
                if (c.getIndex() == index) {
                    return c.name;
                }
            }
            return null;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }


}
