package com.wxshare.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: demo
 * @description:
 * @author: Cloud.
 * @create: 2019-04-24 11:32
 */
@Controller
@RequestMapping("/index")
public class WebPageController {

    //宣传视频播放页面
    @RequestMapping("/videoPlay")
    public String videoPlay() {
        return "video_play";
    }

}
