package com.snowxuyu.webwechat.robot.domain;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 05:07
 */
public class ChatSetRequest implements Serializable {

    private String UserName;

    private String EncryChatRoomId;

    @JSONField(name = "UserName")
    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @JSONField(name = "EncryChatRoomId")
    public String getEncryChatRoomId() {
        return EncryChatRoomId;
    }

    public void setEncryChatRoomId(String encryChatRoomId) {
        EncryChatRoomId = encryChatRoomId;
    }
}
