package com.huayun.mall.service.impl;

import com.huayun.mall.dao.OrderDOMapper;
import com.huayun.mall.dao.SequenceDOMapper;
import com.huayun.mall.dataobject.OrderDO;
import com.huayun.mall.dataobject.SequenceDO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.service.ItemService;
import com.huayun.mall.service.OrderService;
import com.huayun.mall.service.UserService;
import com.huayun.mall.service.model.ItemModel;
import com.huayun.mall.service.model.OrderModel;
import com.huayun.mall.service.model.UserModel;
import org.apache.commons.lang3.builder.ToStringExclude;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sun.awt.EmbeddedFrame;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private ItemService itemService;

    @Resource
    private OrderDOMapper orderDOMapper;

    @Resource
    private UserService userService;

    @Resource
    private SequenceDOMapper sequenceDOMapper;

    @Override
    public OrderModel createOrder(Integer userId, Integer itemId, Integer amount) throws BusinessException {

        //根据入参判断商品信息是否存在
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.ITEM_NOT_EXIST);
        }
        //判断用户是否合法
        UserModel userModel = userService.getUserById(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //判断购买商品数量是否合法
        if(amount <=0 || amount >= 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"购买商品数量错误！");
        }

        //落单减库存
        Boolean flag = itemService.decreaseStock(itemId,amount);
        //返回flase，代表库存不够
        if(flag == false){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //如果返回true，扣减库存正常。
        //创建orderModel对象
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setUserId(userId);
        //计算订单总价
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(BigDecimal.valueOf(amount)));
        //生成订单编号，16位
        orderModel.setId(generateOrderNo());

        //orderModel转化为orderDO
        OrderDO orderDO = convertFromOrderModel(orderModel);

        //插入数据库中
        orderDOMapper.insertSelective(orderDO);

        //增加该商品销量
        itemService.increaseSales(itemId,amount);

        return orderModel;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNo(){
        StringBuffer sb = new StringBuffer();
        //前8位为时间戳
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String time = dateFormat.format(date);
        sb.append(time);

        //中间6位为自增长，每个订单唯一
        //数据库中创建一张自增序列表sequence_info
        //从sequence_info表中获取当前序列值
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");

        //获取最新的currentValue
        sequence = sequenceDO.getCurrentValue();
        //获取最新的currentValue +1
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        //更新currentValue
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);

        //凑足6位拼接序列
        String sequenceStr = String.valueOf(sequence);
        //序列值前面的几位补0
        for(int i=0;i<6-sequenceStr.length();i++){
            sb.append(0);
        }
        //将序列拼接上去
        //000001
        sb.append(sequenceStr);

        //最后2位为分库分表位，00-99，分库分表，订单水平拆分
        // 订单信息落到拆分后的100个库的100张表中，分散数据库从查询和落单压力
        // 订单号不变，这条订单记录一定会落到某一个库的某一张表上
        // Integer userId = 1000122;
        // userId % 100
        // 暂时写死
        sb.append("00");


        return sb.toString();
    }


    private OrderDO convertFromOrderModel(OrderModel orderModel) {
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        return orderDO;
    }
}
