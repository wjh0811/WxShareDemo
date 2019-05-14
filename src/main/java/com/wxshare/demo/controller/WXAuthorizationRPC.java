package com.wxshare.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.wxshare.demo.util.SecurityUtil;
import com.wxshare.demo.util.WeChatUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @program: demo
 * @description:
 * @author: Cloud.
 * @create: 2019-04-24 11:19
 */
@Controller
@RequestMapping("/access")
public class WXAuthorizationRPC {

    @RequestMapping(value="/weChat",method= RequestMethod.GET)
    @ResponseBody
    public String validate(String signature,String timestamp,String nonce,String echostr){


        //1. 将token、timestamp、nonce三个参数进行字典序排序
        String[] arr = {timestamp,nonce, WeChatUtil.TOKEN};
        Arrays.sort(arr);
        //2. 将三个参数字符串拼接成一个字符串进行sha1加密
        StringBuilder sb = new StringBuilder();
        for (String temp : arr) {
            sb.append(temp);
        }
        //3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        if(SecurityUtil.SHA1(sb.toString()).equals(signature)){
            //接入成功
            System.out.println("接入成功");
            return echostr;
        }
        //接入失败
        return null;
    }

    @RequestMapping(value="/share",method= RequestMethod.POST)
    @ResponseBody
    public void shareValidate(HttpServletRequest request, HttpServletResponse response){
        //外部传入url获取url url需要是微信中打开的url否则会报错。
        String URL = request.getParameter("url");
        //转换url

        String url="";
        //需要转换解码url
        try {
            url = URLDecoder.decode(URL,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //通过code来换取access_token
        String aeecss_token  = WeChatUtil.getAccessToken();

        String aeecss_ticket  = WeChatUtil.getTicket();

        String nonceStr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳

        //5、将参数排序并拼接字符串
        String str = "jsapi_ticket="+aeecss_ticket+"&noncestr="+nonceStr+"&timestamp="+timestamp+"&url="+url;

        //6、将字符串进行sha1加密
        String signature = SecurityUtil.SHA1(str);
        System.out.println("参数："+str+"\n签名："+signature);
        String appId = WeChatUtil.APPID;

        Map<String,String> map=new HashMap();
        map.put("appId",appId);
        map.put("timestamp",timestamp);
        map.put("accessToken",aeecss_token);
        map.put("ticket",aeecss_ticket);
        map.put("nonceStr",nonceStr);
        map.put("signature",signature);
        JSONObject json = (JSONObject)JSONObject.toJSON(map);

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e1) {

            e1.printStackTrace();
        }
        System.out.println(json.toString());
        pw.write(json.toString());
        pw.close();


    }

}

