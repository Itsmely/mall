package com.huayun.mall.dao;

import com.huayun.mall.dataobject.UserDO;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserDOMapper {
    //long countByExample(UserDOExample example);

    //int deleteByExample(UserDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    //List<UserDO> selectByExample(UserDOExample example);

    UserDO selectByPrimaryKey(Integer id);
    UserDO selectByTelphone(String telphone);

    //int updateByExampleSelective(@Param("record") UserDO record, @Param("example") UserDOExample example);

    //int updateByExample(@Param("record") UserDO record, @Param("example") UserDOExample example);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);
}