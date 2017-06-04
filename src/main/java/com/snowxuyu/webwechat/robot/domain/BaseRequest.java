package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-03
 * Time: 19:32
 */
public class BaseRequest implements Serializable {
    private String Uin;
    private String Sid;
    private String Skey;
    private String DeviceID;

    @JSONField(name = "Uin")
    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }

    @JSONField(name = "Sid")
    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    @JSONField(name = "Skey")
    public String getSkey() {
        return Skey;
    }

    public void setSkey(String skey) {
        Skey = skey;
    }

    @JSONField(name = "DeviceID")
    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }
}
