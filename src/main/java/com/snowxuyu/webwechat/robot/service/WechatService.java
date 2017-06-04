package com.snowxuyu.webwechat.robot.service;

import org.framework.exception.BaseException;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:52
 */
public interface WechatService {
    String getUUid() throws BaseException;

    String wxLogin() throws BaseException;

    void loginPage() throws BaseException;

    void webWxInit() throws BaseException;

    void statusNotify() throws BaseException;

    void getContact() throws BaseException;

    void getGroupContact() throws BaseException;

    void syncListener() throws BaseException;
}
