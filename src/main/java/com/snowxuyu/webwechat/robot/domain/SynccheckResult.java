package com.snowxuyu.webwechat.robot.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 12:47
 */
@Data
public class SynccheckResult implements Serializable {

    private String retcode;
    private String selector;
}
