package com.huayun.mall.controller;

import com.alibaba.druid.util.StringUtils;
import com.huayun.mall.controller.viewObject.UserVO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.response.CommonReturnType;
import com.huayun.mall.service.UserService;
import com.huayun.mall.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
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
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;

    //用户注册接口
    @PostMapping(value = "/register",consumes = CONSUMES_TYPE)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                    @RequestParam(name = "name") String name,
                                    @RequestParam(name = "gender") Integer gender,
                                    @RequestParam(name = "age") Integer age,
                                    @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name="password") String password) throws BusinessException {
        //验证手机号和对应的optCode相符合
        String otp = (String) httpServletRequest.getSession().getAttribute("telphone");
        if(!StringUtils.equals(otpCode,otp)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(gender);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(MD5Encoder.encode(password.getBytes()));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //使用短信验证码获取OTP注册
    @PostMapping(value = "/getotp",consumes = CONSUMES_TYPE)
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
