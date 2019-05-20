package com.huayun.mall.service.impl;

import com.huayun.mall.dao.UserDOMapper;
import com.huayun.mall.dao.UserPasswordDOMapper;
import com.huayun.mall.dataobject.UserDO;
import com.huayun.mall.dataobject.UserPasswordDO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.service.UserService;
import com.huayun.mall.service.model.UserModel;
import com.huayun.mall.validator.ValidationResult;
import com.huayun.mall.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.EnumMap;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private ValidatorImpl validator;


    @Resource
    private UserDOMapper userDOMapper;

    @Resource
    private UserPasswordDOMapper userPasswordDOMapper;

    //验证用户登陆合法性
    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException {
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObject(userDO,userPasswordDO);
        //对比用户登陆的加密密码和数据库密码是否一致
        if(!StringUtils.equals(userModel.getEncrptPassword(),encrptPassword)){
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userModel;
    }

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null){
            return null;
        }
        //通过用户id获取对应的用户的加密密码
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO,userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BusinessException {
        if(userModel == null) throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

/*        if(StringUtils.isEmpty(userModel.getName()) || userModel.getGender() == null ||
                userModel.getTelphone() == null || userModel.getAge() == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }*/

        //使用validator校验userModel的各个属性是否符合校验规则
        ValidationResult validationResult = validator.validate(userModel);
        //验证userModel，如果有错误，会将这个boolean置为true
        if(validationResult.isHasErrors()){
            //抛异常，封装错误信息
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }

        //实现model -> dataobject方法
        UserDO userDO = convertFromModel(userModel);
        try{
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"该手机号已注册！");
        }

        //查询出user的数据库自增ID后赋值给userModel
        userModel.setId(userDO.getId());
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);

        return;
    }


    private UserPasswordDO convertPasswordFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel,userDO);
        return userDO;
    }


    //把userDO和userPasswordDO组装成userModel
    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null){
            return null;
        }
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if(userPasswordDO != null){
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());

        }
        return userModel;
    }
}
