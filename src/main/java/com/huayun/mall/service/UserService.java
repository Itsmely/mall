package com.huayun.mall.service;

import com.huayun.mall.error.BusinessException;
import com.huayun.mall.service.model.UserModel;

public interface UserService {
    //通过用户ID获取用户对象的方法
    UserModel getUserById(Integer id);
    //注册用户
    void register(UserModel userModel) throws BusinessException;

}

