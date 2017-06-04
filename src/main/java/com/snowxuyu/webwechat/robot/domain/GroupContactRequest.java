package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 04:26
 */
public class GroupContactRequest implements Serializable {

    private BaseRequest BaseRequest;

    private Integer Count;

    private JSONArray List;

    @JSONField(name = "BaseRequest")
    public com.snowxuyu.webwechat.robot.domain.BaseRequest getBaseRequest() {
        return BaseRequest;
    }

    public void setBaseRequest(com.snowxuyu.webwechat.robot.domain.BaseRequest baseRequest) {
        BaseRequest = baseRequest;
    }

    @JSONField(name = "Count")
    public Integer getCount() {
        return Count;
    }

    public void setCount(Integer count) {
        Count = count;
    }

    @JSONField(name = "List")
    public JSONArray getList() {
        return List;
    }

    public void setList(JSONArray list) {
        List = list;
    }
}
