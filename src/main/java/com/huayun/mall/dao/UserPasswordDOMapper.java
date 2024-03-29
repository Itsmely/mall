package com.huayun.mall.dao;

import com.huayun.mall.dataobject.UserPasswordDO;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserPasswordDOMapper {
    //long countByExample(UserPasswordDOExample example);

    //int deleteByExample(UserPasswordDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordDO record);

    int insertSelective(UserPasswordDO record);

    //List<UserPasswordDO> selectByExample(UserPasswordDOExample example);

    UserPasswordDO selectByUserId(Integer userId);

    UserPasswordDO selectByPrimaryKey(Integer id);

    //int updateByExampleSelective(@Param("record") UserPasswordDO record, @Param("example") UserPasswordDOExample example);

    //int updateByExample(@Param("record") UserPasswordDO record, @Param("example") UserPasswordDOExample example);

    int updateByPrimaryKeySelective(UserPasswordDO record);

    int updateByPrimaryKey(UserPasswordDO record);
}