package com.huayun.mall.dao;

import com.huayun.mall.dataobject.PromoDO;

public interface PromoDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PromoDO record);

    int insertSelective(PromoDO record);

    PromoDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PromoDO record);

    int updateByPrimaryKey(PromoDO record);

    PromoDO getPromoByid(Integer id);
}