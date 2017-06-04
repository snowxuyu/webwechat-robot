package com.snowxuyu.webwechat.robot.constants;

import org.framework.basic.constant.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:37
 */
public interface Constant extends Constants {

    interface WECHAT_URL_CONFIG {
        String JS_LOGN = "https://login.wx.qq.com/jslogin"; //获取uuid的接口
        String SHOW_QRCODE = "https://login.weixin.qq.com/qrcode/"; //显示二维码
        String WAIT_LOGIN = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login"; //等待登陆接口
        String WEBWX_INIT = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxinit"; //微信初始化
        String STATUS_NOTIFY = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxstatusnotify"; //开启微信状态通知
        String GET_CONTACT = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxgetcontact"; //获取好友列表
        String GET_GROUP_CONTACT = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxbatchgetcontact"; //获取微信群组
        String SYNC_CHECK = "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin/synccheck"; //消息检查
        String WEBWX_SYNC = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsync"; //获取最新消息
        String SEND_MSG = "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxsendmsg"; //发送消息

        String CLAA_ROBOT = "http://i.itpk.cn/api.php"; //调用机器人接口
    }


    // 特殊用户 须过滤
    List<String> FILTER_USERS = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail",
            "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend",
            "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin",
            "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts",
            "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm",
            "notification_messages");
}
