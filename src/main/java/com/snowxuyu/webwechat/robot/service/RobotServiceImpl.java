package com.snowxuyu.webwechat.robot.service;

import com.alibaba.druid.util.StringUtils;
import com.snowxuyu.webwechat.robot.constants.Constant;
import org.framework.basic.system.PropertyPlaceholderConfigurer;
import org.framework.common.util.HttpClientUtils;
import org.framework.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-06
 * Time: 12:22
 */
@Service
public class RobotServiceImpl implements RobotService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String talk(String content) throws BaseException {

        String robotReturnMsg = "";
        String apiKey = (String) PropertyPlaceholderConfigurer.getContextProperty("api_key");
        String apiSecret = (String) PropertyPlaceholderConfigurer.getContextProperty("api_secret");

        if (StringUtils.isEmpty(apiKey) || StringUtils.isEmpty(apiSecret)) {
            robotReturnMsg = "请先配置机器人";
        }

        HashMap<String, String> robotParams = new HashMap<>();
        robotParams.put("question", content);
        robotParams.put("api_key", apiKey);
        robotParams.put("api_secret", apiSecret);
        try {
            robotReturnMsg = HttpClientUtils.doGet(Constant.WECHAT_URL_CONFIG.CLAA_ROBOT, robotParams);
        } catch (IOException e) {
            logger.debug("调用机器人接口异常：{}", e);
        }
        return robotReturnMsg;
    }
}
