package com.snowxuyu.webwechat.robot.exception;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:40
 */
public class WechatException extends RuntimeException {

    public WechatException() {
    }

    public WechatException(String message) {
        super(message);
    }

    public WechatException(String message, Throwable cause) {
        super(message, cause);
    }

    public WechatException(Throwable cause) {
        super(cause);
    }

    public WechatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
