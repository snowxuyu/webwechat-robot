package com.snowxuyu.webwechat.robot.service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.snowxuyu.webwechat.robot.constants.Constant;
import com.snowxuyu.webwechat.robot.domain.BaseRequest;
import com.snowxuyu.webwechat.robot.domain.ChatSetRequest;
import com.snowxuyu.webwechat.robot.domain.GroupContactRequest;
import com.snowxuyu.webwechat.robot.domain.StatusNotifyRequest;
import com.snowxuyu.webwechat.robot.domain.SynccheckResult;
import com.snowxuyu.webwechat.robot.domain.WebwxSyncRequest;
import com.snowxuyu.webwechat.robot.domain.WxEntity;
import com.snowxuyu.webwechat.robot.utils.MatcherUtils;
import org.framework.basic.system.PropertyPlaceholderConfigurer;
import org.framework.common.crypto.Tools;
import org.framework.common.util.HttpClientUtils;
import org.framework.common.util.RandomUtils;
import org.framework.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-06-02
 * Time: 22:52
 */
@Service
public class WechatServiceImpl implements WechatService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ThreadPoolTaskExecutor threadPool;


    private WxEntity wxEntity;

    @Override
    public String getUUid() throws BaseException {
        String result = "";
        wxEntity = new WxEntity();
        Map<String, String> map = new HashMap<>();
        map.put("appid", "wx782c26e4c19acffb");
        map.put("redirect_uri", "https://wx.qq.com/cgi-bin/mmwebwx-bin/webwxnewloginpage");
        map.put("fun", "new");
        map.put("lang", "zh_CN");
        map.put("_", String.valueOf(System.currentTimeMillis()));
        try {
            result = HttpClientUtils.doGet(Constant.WECHAT_URL_CONFIG.JS_LOGN, map);
        } catch (IOException e) {
            logger.error("====>>调用微信getUUid接口出错{}", e);
        }

        if (StringUtils.isEmpty(result)) {
            logger.debug("获取uuid接口返回空");
            return null;
        }

        String code = MatcherUtils.matcher(result, "window.QRLogin.code = (\\d+);");
        if (!StringUtils.isEmpty(code) && Constant.System.OK_CODE.equals(code)) {
            String uuid = MatcherUtils.matcher(result, "window.QRLogin.uuid = \"(.*)\"");
            wxEntity.setUuid(uuid);
            logger.debug("====>>微信返回uuid为：{}", uuid);
            return uuid;
        }
        return null;
    }

    @Override
    public String wxLogin() throws BaseException {
        String uuid = wxEntity.getUuid();
        String result = "";
        String codeStatus = "";
        long timestamp = System.currentTimeMillis();
        HashMap<String, String> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("loginicon", "true");
        map.put("tip", "0");
        map.put("r", Long.toString(~timestamp));
        map.put("_", Long.toString(timestamp));
        String link = Tools.createLinkString(map, false);
        try {
            result = HttpClientUtils.doGet(Constant.WECHAT_URL_CONFIG.WAIT_LOGIN + "?" + link, null);
        } catch (IOException e) {
            logger.debug("调用登陆接口异常: {}", e);
        }

        //logger.debug("等待微信登陆返回结果: {}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("微信登陆接口返回数据为空");
            return null;
        }

        codeStatus = MatcherUtils.matcher(result, "indow.code=(\\d+);");

        if ("408".equals(codeStatus)) {
            logger.debug("登陆超时...");
        } else if ("201".equals(codeStatus)) {
            logger.debug("扫码成功...");
        } else if (Constant.System.OK_CODE.equals(codeStatus)) {
            logger.debug("登陆成功...");
            String redirectUrl = MatcherUtils.matcher(result, "window.redirect_uri=\"(\\S+?)\";");
            wxEntity.setRedirectUrl(redirectUrl);
        }
        return codeStatus;
    }

    @Override
    public void loginPage() throws BaseException {
        String result = "";
        String redirectUrl = wxEntity.getRedirectUrl() + "&fun=new&version=v2";
        try {
            result = HttpClientUtils.doGet(redirectUrl, null);
            String cookie = HttpClientUtils.getCookie();
            wxEntity.setCookie(cookie);
        } catch (IOException e) {
            logger.error("调用微信loginpageg出错");
        }

        //logger.debug("loginPage返回结果: {}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("loginPage返回结果为空，获取登陆参数失败");
            return;
        }

        logger.debug("获取登陆参数成功");
        String skey = MatcherUtils.matcher(result, "<skey>(\\S+)</skey>");
        String wxsid = MatcherUtils.matcher(result, "<wxsid>(\\S+)</wxsid>");
        String wxuin = MatcherUtils.matcher(result, "<wxuin>(\\S+)</wxuin>");
        String passTicket = MatcherUtils.matcher(result, "<pass_ticket>(\\S+)</pass_ticket>");
        wxEntity.setSkey(skey);
        wxEntity.setWxsid(wxsid);
        wxEntity.setWxuin(wxuin);
        wxEntity.setPassTicket(passTicket);
    }

    @Override
    public void webWxInit() throws BaseException {
        String result = "";
        Map<String, String> rulMap = new HashMap<>();
        rulMap.put("r", Long.toString(System.currentTimeMillis()));
        rulMap.put("lang", "zh_CN");
        rulMap.put("pass_ticket", wxEntity.getPassTicket());

        String url = Constant.WECHAT_URL_CONFIG.WEBWX_INIT + "?" + Tools.createLinkString(rulMap, false);

        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setDeviceID(wxEntity.getDeviceID());
        baseRequest.setSid(wxEntity.getWxsid());
        baseRequest.setSkey(wxEntity.getSkey());
        baseRequest.setUin(wxEntity.getWxuin());
        wxEntity.setBaseRequest(baseRequest);
        String jsonString = JSONObject.toJSONString(baseRequest);
        logger.debug(jsonString);

        String requestJson = "{\"BaseRequest\":" + jsonString + "}";

        try {
            result = HttpClientUtils.doPost(url, requestJson);
        } catch (IOException e) {
            logger.error("调用webwxinit接口出错");
        }

        //logger.debug("微信初始化返回结果: {}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("微信初始化返回结果为空");
            return;
        }

        String baseResponseString = JSONObject.parseObject(result).getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseString);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("微信初始化失败");
        } else {
            logger.debug("微信初始化成功");
            String user = JSONObject.parseObject(result).getString("User");
            String syncKey = JSONObject.parseObject(result).getString("SyncKey");
            String chatSet = JSONObject.parseObject(result).getString("ChatSet");
            JSONObject jsonSyncKey = JSONObject.parseObject(syncKey);
            JSONArray list = jsonSyncKey.getJSONArray("List");
            JSONArray jsonArray = new JSONArray();
            String[] setStr = chatSet.split("\\,");
            for (String str : setStr) {
                if (str.startsWith("@@")) {
                    ChatSetRequest chatSetRequest = new ChatSetRequest();
                    chatSetRequest.setUserName(str);
                    chatSetRequest.setEncryChatRoomId("");
                    jsonArray.add(chatSetRequest);
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String jsonStr = JSONObject.toJSONString(list.get(i));
                JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                if (i == list.size() - 1) {
                    builder.append(jsonObj.getInteger("Key")).append("_").append(jsonObj.getInteger("Val"));
                } else {
                    builder.append(jsonObj.getInteger("Key")).append("_").append(jsonObj.getInteger("Val")).append("|");
                }
            }

            wxEntity.setUser(JSONObject.parseObject(user));
            wxEntity.setSyncKey(jsonSyncKey);
            wxEntity.setSyncDetailKey(builder.toString());
            wxEntity.setChatSet(jsonArray);
        }
    }

    @Override
    public void statusNotify() throws BaseException {
        String result = "";
        String url = Constant.WECHAT_URL_CONFIG.STATUS_NOTIFY + "?lang=zh_CN&pass_ticket=" + wxEntity.getPassTicket();
        String userName = wxEntity.getUser().getString("UserName");

        StatusNotifyRequest notifyRequest = new StatusNotifyRequest();
        notifyRequest.setBaseRequest(wxEntity.getBaseRequest());
        notifyRequest.setFromUserName(userName);
        notifyRequest.setToUserName(userName);
        notifyRequest.setClientMsgId(new Long(System.currentTimeMillis()).intValue());

        String requestJson = JSONObject.toJSONString(notifyRequest);

        try {
            result = HttpClientUtils.doPost(url, requestJson);
        } catch (IOException e) {
            logger.error("调用开启微信通知接口异常: {}", e);
        }

        //logger.debug("调用开启微信通知返回结果：{}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("调用开启微信通知返回结果为空");
            return;
        }

        String baseResponseString = JSONObject.parseObject(result).getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseString);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("开启微信通知失败");
        } else {
            logger.debug("开启微信通知成功");
        }

    }

    @Override
    public void getContact() throws BaseException {

        String result = "";

        String url = Constant.WECHAT_URL_CONFIG.GET_CONTACT + "?lang=zh_CN&pass_ticket=" + wxEntity.getPassTicket() + "&r=" + System.currentTimeMillis() + "&seq=0&skey=" + wxEntity.getSkey();

        try {
            Map<String, String> cookieMap = new HashMap<>();
            cookieMap.put("Cookie", wxEntity.getCookie());
            HttpClientUtils.setHeader(cookieMap);
            result = HttpClientUtils.doGet(url, null);
        } catch (IOException e) {
            logger.error("获取微信联系人接口异常: {}", e);
        }

        //logger.debug("获取微信联系人接口返回结果:{}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("获取联系人返回结果为空");
            return;
        }

        JSONObject jsonResult = JSONObject.parseObject(result);
        String baseResponseStr = jsonResult.getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseStr);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("获取联系人失败");
        } else {
            logger.debug("获取联系人成功");
            logger.debug("共{}位联系人", jsonResult.getInteger("MemberCount"));
            JSONArray memberList = jsonResult.getJSONArray("MemberList");
            wxEntity.setMemberList(memberList);
        }
    }

    @Override
    public void getGroupContact() throws BaseException {

        String result = "";

        String url = Constant.WECHAT_URL_CONFIG.GET_GROUP_CONTACT + "?type=ex&r=" + System.currentTimeMillis() + "&lang=zh_CN&pass_ticket=" + wxEntity.getPassTicket();

        GroupContactRequest groupContactRequest = new GroupContactRequest();
        groupContactRequest.setBaseRequest(wxEntity.getBaseRequest());
        groupContactRequest.setCount(wxEntity.getChatSet().size());
        groupContactRequest.setList(wxEntity.getChatSet());

        String jsonRequest = JSONObject.toJSONString(groupContactRequest);

        try {
            Map<String, String> cookieMap = new HashMap<>();
            cookieMap.put("Cookie", wxEntity.getCookie());
            HttpClientUtils.setHeader(cookieMap);
            result = HttpClientUtils.doPost(url, jsonRequest);
        } catch (IOException e) {
            logger.error("调用获取微信群组接口异常:{}", e);
        }

        //logger.debug("调用获取微信群组接口返回结果：{}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("调用获取微信群组接口返回结果为空");
            return;
        }

        JSONObject jsonResult = JSONObject.parseObject(result);
        String baseResponseStr = jsonResult.getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseStr);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("获取微信群组失败");
        } else {
            logger.debug("获取微信群组成功");
        }


    }

    @Override
    public void syncListener() throws BaseException {

        final WxEntity finalWxEntity = wxEntity;

        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    SynccheckResult syncCheck = syncCheck(finalWxEntity);
                    if ("1100".equals(syncCheck.getRetcode())) {
                        logger.debug("微信已退出");
                    } else if ("0".equals(syncCheck.getRetcode())) {
                        if ("7".equals(syncCheck.getSelector())) {
                            logger.debug("离开聊天界面");
                        } else if ("2".equals(syncCheck.getSelector())) {
                            logger.debug("有新的消息");
                            JSONObject jsonMsg = webwxSync(finalWxEntity);
                            webwxSendMsg(finalWxEntity, jsonMsg);
                        }
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 发送消息
     */
    private void webwxSendMsg(WxEntity entity, JSONObject jsonMsg) {

        //解析jsonMsg  只有@和私聊的才回复
        JSONArray jsonMsgArr = jsonMsg.getJSONArray("AddMsgList");

        for (int i = 0; i < jsonMsgArr.size(); i++) {
            JSONObject msg = jsonMsgArr.getObject(i, JSONObject.class);
            String remarkName = getUserRemarkName(msg.getString("FromUserName"), wxEntity);
            String content = msg.getString("Content");

            Integer msgType = msg.getInteger("MsgType");

            if (1 == msgType) {
                //文字消息

                if (Constant.FILTER_USERS.contains(msg.getString("ToUserName"))) {
                    //过滤特殊用户
                    continue;
                }
                if (msg.getString("FromUserName").equals(entity.getUser().getString("UserName"))) {
                    //过滤掉自己
                    continue;
                }

                logger.debug("{}：{}", remarkName, content);

                //调用机器人
                String robotReturnMsg = "";
                String apiKey = (String)PropertyPlaceholderConfigurer.getContextProperty("api_key");
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
                sengMsg(entity, robotReturnMsg, msg.getString("FromUserName"));
            } else if (3 == msgType) {
                //图片消息
                sengMsg(entity, "暂不支持图片", msg.getString("FromUserName"));
            } else  if (34 == msgType) {
                sengMsg(entity, "暂不支持语音", msg.getString("FromUserName"));
            }

        }


    }


    //发送消息
    private void sengMsg(WxEntity entity, String content, String toUser) {
        logger.debug("开始发送消息");

        logger.debug("{}: {}", entity.getUser().getString("UserName"), content);
        String result = "";
        String url = Constant.WECHAT_URL_CONFIG.SEND_MSG + "?lang=zh_CN&pass_ticket=" + entity.getPassTicket();

        String clientMsgId = System.currentTimeMillis() + RandomUtils.getRandomString(4);
        JSONObject msg = new JSONObject();
        msg.put("Type", 1);
        msg.put("Content", content);
        msg.put("FromUserName", entity.getUser().getString("UserName"));
        msg.put("ToUserName", toUser);
        msg.put("LocalID", clientMsgId);
        msg.put("ClientMsgId", clientMsgId);

        JSONObject sengMsg = new JSONObject();

        sengMsg.put("BaseRequest", entity.getBaseRequest());
        sengMsg.put("Msg", msg);

        String retMsg = JSONObject.toJSONString(sengMsg);

        try {
            Map<String, String> cookieMap = new HashMap<>();
            cookieMap.put("Cookie", wxEntity.getCookie());
            HttpClientUtils.setHeader(cookieMap);
            result = HttpClientUtils.doPost(url, retMsg);
        } catch (Exception e) {
            logger.error("调用发送信息接口异常:{}", e);
        }

        //logger.debug("发送信息返回结果：{}", result);

        JSONObject jsonResult = JSONObject.parseObject(result);
        String baseResponseStr = jsonResult.getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseStr);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("消息发送失败");
        } else {
            logger.debug("消息发送成功");
        }
    }

    /**
     * 获取好友名称
     *
     * @param fromUserName
     * @return
     */
    private String getUserRemarkName(String fromUserName, WxEntity entity) {
        String name = "未知的名字";
        JSONArray memberList = entity.getMemberList();
        for (int i = 0; i < memberList.size(); i++) {
            JSONObject member = memberList.getObject(i, JSONObject.class);
            if (member.getString("UserName").equals(fromUserName)) {
                if (!StringUtils.isEmpty(member.getString("RemarkName"))) {
                    name = member.getString("RemarkName");
                } else {
                    name = member.getString("NickName");
                }
                return name;
            }
        }
        return name;
    }

    /**
     * 获取最新消息
     *
     * @param entity
     */
    private JSONObject webwxSync(WxEntity entity) {
        String result = "";
        String url = Constant.WECHAT_URL_CONFIG.WEBWX_SYNC + "?sid=" + entity.getWxsid() + "&skey=" + entity.getSkey() + "&lang=zh_CN";

        WebwxSyncRequest webwxSyncRequest = new WebwxSyncRequest();
        webwxSyncRequest.setBaseRequest(entity.getBaseRequest());
        webwxSyncRequest.setSyncKey(entity.getSyncKey());
        webwxSyncRequest.setRr(new Long(~System.currentTimeMillis()).intValue());
        String jsonRequest = JSONObject.toJSONString(webwxSyncRequest);

        try {
            result = HttpClientUtils.doPost(url, jsonRequest);
        } catch (IOException e) {
            logger.error("调用微信获取最新消息接口异常:{}", e);
        }

        //logger.debug("调用微信获取最新消息接口返回结果：{}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("调用微信获取最新消息接口返回空");
            return null;
        }

        String baseResponseString = JSONObject.parseObject(result).getString("BaseResponse");
        JSONObject baseResponse = JSONObject.parseObject(baseResponseString);
        if (0 != baseResponse.getInteger("Ret")) {
            logger.debug("获取最新消息失败");
        } else {
            logger.debug("获取最新消息成功");
            String syncKey = JSONObject.parseObject(result).getString("SyncKey");
            JSONObject jsonSyncKey = JSONObject.parseObject(syncKey);
            JSONArray list = jsonSyncKey.getJSONArray("List");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                String jsonStr = JSONObject.toJSONString(list.get(i));
                JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                if (i == list.size() - 1) {
                    builder.append(jsonObj.getInteger("Key")).append("_").append(jsonObj.getInteger("Val"));
                } else {
                    builder.append(jsonObj.getInteger("Key")).append("_").append(jsonObj.getInteger("Val")).append("|");
                }
            }
            wxEntity.setSyncDetailKey(builder.toString());
            wxEntity.setSyncKey(jsonSyncKey);
        }

        return JSONObject.parseObject(result);
    }


    /**
     * 消息检查
     *
     * @param entity
     * @return
     */
    private SynccheckResult syncCheck(WxEntity entity) {

        String result = "";
        String url = Constant.WECHAT_URL_CONFIG.SYNC_CHECK;

        HashMap<String, String> params = new HashMap<>();
        params.put("r", String.valueOf(System.currentTimeMillis()));
        params.put("skey", entity.getSkey());
        params.put("sid", entity.getWxsid());
        params.put("uin", entity.getWxuin());
        params.put("deviceid", entity.getDeviceID());
        params.put("synckey", entity.getSyncDetailKey());
        params.put("_", String.valueOf(System.currentTimeMillis()));

        try {
            Map<String, String> cookieMap = new HashMap<>();
            cookieMap.put("Cookie", wxEntity.getCookie());
            HttpClientUtils.setHeader(cookieMap);
            result = HttpClientUtils.doGet(url, params);
        } catch (IOException e) {
            logger.debug("调用微信消息检查接口出错");

            //接口挂了 终止当前线程 继续开启新的线程
            Thread.currentThread().interrupt();
            while (Thread.interrupted()) {
                syncListener();
            }
        }

        //logger.debug("调用微信消息检查接口返回结果:{}", result);

        if (StringUtils.isEmpty(result)) {
            logger.debug("调用微信消息检查接口返回空");
            return null;
        }

        String retcode = MatcherUtils.matcher(result, "retcode:\"(\\d+)\",");
        String selector = MatcherUtils.matcher(result, "selector:\"(\\d+)\"}");

        SynccheckResult synccheckResult = new SynccheckResult();
        synccheckResult.setRetcode(retcode);
        synccheckResult.setSelector(selector);
        return synccheckResult;
    }

}
