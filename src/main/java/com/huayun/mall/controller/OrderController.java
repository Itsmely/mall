package com.huayun.mall.controller;


import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.response.CommonReturnType;
import com.huayun.mall.service.OrderService;
import com.huayun.mall.service.model.OrderModel;
import com.huayun.mall.service.model.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/order")
@CrossOrigin(origins = {"*"},allowCredentials = "true") //允许跨域请求
public class OrderController extends BaseController{

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private OrderService orderService;

    //购买商品
    @PostMapping(value = "/creatorder", consumes = {CONSUMES_TYPE})
    @ResponseBody
    public CommonReturnType createOrder (@RequestParam(name = "itemId") Integer itemId,
                                         @RequestParam(name = "amount") Integer amount) throws BusinessException {

        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null || isLogin.booleanValue() == false){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN);
        }

        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        OrderModel orderModel = orderService.createOrder(userModel.getId(),itemId,amount);

        return CommonReturnType.create(null);
    }
}
