package com.huayun.mall.controller;

import com.alibaba.druid.util.StringUtils;
import com.huayun.mall.controller.viewObject.UserVO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.response.CommonReturnType;
import com.huayun.mall.service.UserService;
import com.huayun.mall.service.model.UserModel;
import org.apache.ibatis.annotations.Param;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/user")
@CrossOrigin(allowedHeaders = "*",allowCredentials = "true") //允许跨域请求
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest httpServletRequest;

    //用户登陆
    @PostMapping(value = "/login", consumes = CONSUMES_TYPE)
    @ResponseBody
    public CommonReturnType login(@Param(value = "telphone") String telphone,
                                  @Param(value= "password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //判断前端传入的数据是否为空
        if(StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户名和密码不能为空");
        }
        //校验用户是否合法
        UserModel userModel = userService.validateLogin(telphone,getMD5String(password));

        //将用户信息写入session
        UserVO userVO = convertFromModel(userModel);
        httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER",userVO);

        httpServletRequest.getSession().setAttribute("USER_NAME",userVO.getName());

        //在控制台打印已登陆的用户名
        System.out.println(this.httpServletRequest.getSession().getAttribute("IS_LOGIN"));

        return CommonReturnType.create(null);
    }



    //用户注册接口
    @Transactional
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = CONSUMES_TYPE)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") String gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name="password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的optCode相符合
        String otp = (String) httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode,otp)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码错误");
        }
        //判断前台性别为男或女并转为int类型
        Integer genderInt;
        if(gender.equals("男")){
            genderInt = 1;
        }else if(gender.equals("女")){
            genderInt = 0;
        }else {
            genderInt = -1;
        }

        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(genderInt);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(getMD5String(password));

        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //使用MD5对密码进行加密
    public static String getMD5String(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();

        //加密后的字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));
        return newStr;
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
