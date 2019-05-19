package com.huayun.mall.controller;

import com.huayun.mall.dao.UserDOMapper;
import com.huayun.mall.dataobject.UserDO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class IndexController {
    @Resource
    private UserDOMapper userDOMapper;

    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "Welcome to HuayunMall!";
    }
}
