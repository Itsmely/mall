package com.huayun.mall.controller;

import com.huayun.mall.controller.viewObject.UserVO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.response.CommonReturnType;
import com.huayun.mall.service.UserService;
import com.huayun.mall.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;


    //使用短信验证码获取OTP注册
    @GetMapping("/getotp")
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telphone") String telphone){
        //生成随机短信验证码
        Random random = new Random();
        int code = random.nextInt(8999);
        code = code + 1000;
        String otp = String.valueOf(code);

        //将OTP验证码与手机号关联，用session的方式绑定
        httpServletRequest.getSession().setAttribute(telphone,otp);

        //将短信验证码发送给用户（省略）
        System.out.println("telphone:" + telphone + ",验证码:" + otp);

        return CommonReturnType.create(null);

    }



    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException {
        UserModel userModel  = userService.getUserById(id);
        //若获取的用户信息不存在
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }



        UserVO userVO =  convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    private UserVO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }

}
