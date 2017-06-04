package com.snowxuyu.webwechat.robot.utils;

import org.apache.http.cookie.Cookie;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-04
 * Time: 03:42
 */
public class CookieUtils {

    public static String getCookie(List<Cookie> cookieList) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<cookieList.size(); i++) {
            if (i == cookieList.size()-1) {
                sb.append(cookieList.get(i).getName() + "=" + cookieList.get(i).getValue());
            } else  {
                sb.append(cookieList.get(i).getName() + "=" + cookieList.get(i).getValue() + "; ");
            }
        }
        return sb.toString();
    }
}
