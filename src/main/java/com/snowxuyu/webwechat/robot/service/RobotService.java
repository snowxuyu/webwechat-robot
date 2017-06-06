package com.snowxuyu.webwechat.robot.service;

import org.framework.exception.BaseException;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-06
 * Time: 12:21
 */
public interface RobotService {

    String talk (String content) throws BaseException;
}
