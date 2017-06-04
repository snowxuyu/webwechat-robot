package com.snowxuyu.webwechat.robot.controller;

import com.snowxuyu.webwechat.robot.constants.Constant;
import com.snowxuyu.webwechat.robot.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:32
 */
@Controller
public class WxController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private WechatService wechatService;

    /**
     * 初始化 获取uuid
     *
     * @param model
     * @return
     */
    @RequestMapping("/init")
    public String init(Model model) {
        logger.debug("init webwechat robot");
        System.setProperty("jsse.enableSNIExtension", "false");
        //1、获取uuid
        String uuid = wechatService.getUUid();
        //2、获取二维码
        String qrCode = Constant.WECHAT_URL_CONFIG.SHOW_QRCODE + uuid;
        model.addAttribute("qrCode", qrCode);
        return "init";
    }

    @RequestMapping(value = "/wxLogin")
    public void wxLogin() {
        logger.debug("begin call wxLogin");
        //等待登陆
        logger.debug("等待登陆");
        while (!Constant.System.OK_CODE.equals(wechatService.wxLogin())) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.debug("获取登陆参数");
        wechatService.loginPage();


        logger.debug("初始化");
        wechatService.webWxInit();

        logger.debug("开启微信状态通知");
        wechatService.statusNotify();

        logger.debug("获取微信好友列表");
        wechatService.getContact();

        logger.debug("获取微信群组");
        wechatService.getGroupContact();

        logger.debug("开启消息监听");
        wechatService.syncListener();
    }
}
