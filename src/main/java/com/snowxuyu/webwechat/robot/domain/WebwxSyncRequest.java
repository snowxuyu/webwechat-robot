package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 13:12
 */
public class WebwxSyncRequest implements Serializable {

    private BaseRequest BaseRequest;
    private JSONObject SyncKey;
    private Integer rr;

    @JSONField(name = "BaseRequest")
    public com.snowxuyu.webwechat.robot.domain.BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(com.snowxuyu.webwechat.robot.domain.BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    @JSONField(name = "SyncKey")
    public JSONObject getSyncKey() {
        return SyncKey;
    }

    public void setSyncKey(JSONObject syncKey) {
        SyncKey = syncKey;
    }

    @JSONField(name = "rr")
    public Integer getRr() {
        return rr;
    }

    public void setRr(Integer rr) {
        this.rr = rr;
    }
}
