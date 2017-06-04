package com.snowxuyu.webwechat.robot.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:47
 */
public abstract class MatcherUtils {

    public static String matcher(String context, String regex) {
        Pattern pattern = Pattern.compile(regex); //编译正则表达式模板
        Matcher matcher = pattern.matcher(context); //利用正则表达式去匹配内容
        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
