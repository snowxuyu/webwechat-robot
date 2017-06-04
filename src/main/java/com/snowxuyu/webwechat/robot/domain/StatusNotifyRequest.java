package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 00:59
 */
public class StatusNotifyRequest implements Serializable {

    private BaseRequest BaseRequest;

    private Integer Code = 3;

    private String FromUserName;

    private String ToUserName;

    private Integer ClientMsgId;

    @JSONField(name = "BaseRequest")
    public com.snowxuyu.webwechat.robot.domain.BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(com.snowxuyu.webwechat.robot.domain.BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    @JSONField(name = "Code")
    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    @JSONField(name = "FromUserName")
    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    @JSONField(name = "ToUserName")
    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    @JSONField(name = "ClientMsgId")
    public Integer getClientMsgId() {
        return ClientMsgId;
    }

    public void setClientMsgId(Integer clientMsgId) {
        ClientMsgId = clientMsgId;
    }
}
