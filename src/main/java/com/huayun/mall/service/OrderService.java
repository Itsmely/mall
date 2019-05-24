package com.huayun.mall.service;

import com.huayun.mall.error.BusinessException;
import com.huayun.mall.service.model.OrderModel;

public interface OrderService {

    //创建订单
    //需要用户的id，商品的id，商品的数量
    OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException;
}
