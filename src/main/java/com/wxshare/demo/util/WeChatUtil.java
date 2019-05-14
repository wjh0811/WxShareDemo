package com.wxshare.demo.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @program: demo
 * @description:
 * @author: Cloud.
 * @create: 2019-04-24 11:20
 */
public class WeChatUtil {

    public static final String TOKEN = "werty";

    public static final String APPID = "wxeb42afc83892700b";

    public static final String APPSECRET = "1d4ce392dce0d778684f75c4a30bd64f";

    //创建菜单的接口
    public static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";

    //获取基本accessToken的接口
    public static final String GET_ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";

    //删除菜单的接口
    public static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

    //页面使用jssdk的凭据
    public static final String GET_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

    //获取网页授权accessToken的接口
    public static final String GET_WEB_ACCESSTOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

    //获取用户信息的接口
    public static final String GET_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    public static String accessToken;//调用基础接口的凭据

    public static long expiresTime;//凭据的失效时间


    /**
     * 获取网页授权的AccessToken凭据
     * @return
     */
    public static JSONObject getWebAccessToken(String code){
        String result = HttpUtil.get(GET_WEB_ACCESSTOKEN_URL.replace("APPID", APPID).replace("SECRET", APPSECRET).replace("CODE", code));
        JSONObject json = JSONObject.parseObject(result);
        return json;
    }

    /**
     * 获取基本的AccessToken凭据
     * @return
     */
    public static String getAccessToken(){
        //当accessToken为null或者失效才重新去获取
        if(accessToken==null||new Date().getTime()>expiresTime){
            String result = HttpUtil.get(GET_ACCESSTOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET));
            JSONObject json = JSONObject.parseObject(result);
            //凭据
            accessToken = json.getString("access_token");
            //有效期
            Long expires_in = json.getLong("expires_in");
            //设置凭据的失效时间 = 当前时间+有效期
            expiresTime = new Date().getTime()+((expires_in-60)*1000);
        }

        return accessToken;
    }


    /**
     * 创建菜单
     */
    public static void createMenu(String menuJson){
        //发起请求到指定的接口，并且带上菜单json数据
        String result = HttpUtil.post(CREATE_MENU_URL.replace("ACCESS_TOKEN",getAccessToken()), menuJson);
        System.out.println(result);
    }

    /**
     * 删除菜单
     */
    public static void deleteMenu(){
        String result = HttpUtil.get(DELETE_MENU_URL.replace("ACCESS_TOKEN", getAccessToken()));
        System.out.println(result);
    }

    //发送模板消息的接口
    public static final String SEND_TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";

    /**
     * 发送模板
     *
     */
    public static void sendTemplate(String data){
        String result = HttpUtil.post(SEND_TEMPLATE_URL.replace("ACCESS_TOKEN", getAccessToken()),data);
        System.out.println(result);
    }


    /**
     * 获取用户信息
     *
     */
    public static JSONObject getUserInfo(String accessToken, String openId){
        String result = HttpUtil.get(GET_USERINFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID",openId));
        JSONObject json = JSONObject.parseObject(result);
        return json;
    }

    /**
     * 获取JSSDK的Ticket
     */
    public static String getTicket(){
        //发起请求到指定的接口
        String result = HttpUtil.get(GET_TICKET_URL.replace("ACCESS_TOKEN",getAccessToken()));
        JSONObject json = JSONObject.parseObject(result);
        System.out.println(json);
        return json.getString("ticket");
    }


    /**
     * 计算jssdk-config的签名
     * @param jsapi_ticket
     * @param timestamp
     * @param noncestr
     * @param url
     * @return
     */
    public static String getSignature(String jsapi_ticket,Long timestamp,String noncestr,String url ){
        //对所有待签名参数按照字段名的ASCII 码从小到大排序（字典序）
        Map<String,Object> map = new TreeMap<>();
        map.put("jsapi_ticket",jsapi_ticket);
        map.put("timestamp",timestamp);
        map.put("noncestr",noncestr);
        map.put("url",url);
        //使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串string1
        StringBuilder sb = new StringBuilder();
        Set<String> set = map.keySet();
        for (String key : set) {
            sb.append(key+"="+map.get(key)).append("&");
        }
        //去掉最后一个&符号
        String temp = sb.substring(0,sb.length()-1);
        //使用sha1加密
        String signature = SecurityUtil.SHA1(temp);
        return signature;
    }



}
