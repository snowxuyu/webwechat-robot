package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-03
 * Time: 18:04
 */
@Data
public class WxEntity implements Serializable {
    private String DeviceID = "e" + System.currentTimeMillis() / 1000;
    private String uuid;
    private String redirectUrl;
    private String skey;
    private String wxsid;
    private String wxuin;
    private String passTicket;

    private String cookie;

    private BaseRequest baseRequest;

    private JSONObject syncKey; //json对象

    private String syncDetailKey; // 1_xxxxxxx|2_xxxxxxx|3_xxxxxx

    private JSONObject user;

    private JSONArray chatSet;

    private JSONArray memberList; //微信联系人
}
