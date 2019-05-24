package com.huayun.mall.service.impl;

import com.huayun.mall.dao.ItemDOMapper;
import com.huayun.mall.dao.ItemStockDOMapper;
import com.huayun.mall.dataobject.ItemDO;
import com.huayun.mall.dataobject.ItemStockDO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.service.ItemService;
import com.huayun.mall.service.PromoService;
import com.huayun.mall.service.model.ItemModel;
import com.huayun.mall.service.model.PromoModel;
import com.huayun.mall.validator.ValidationResult;
import com.huayun.mall.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.xml.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private ValidatorImpl validator;
    @Resource
    private ItemDOMapper itemDOMapper;
    @Resource
    private ItemStockDOMapper itemStockDOMapper;

    @Resource
    private PromoService promoService;

    private ItemDO converItemDOFromItemModel(ItemModel itemModel){
    if(itemModel == null){
        return null;
    }
    ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        return itemDO;
    }

    private ItemStockDO converItemStockModelFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());

        return itemStockDO;
    }
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转换itemModel -》 dataObject
        ItemDO itemDO = converItemDOFromItemModel(itemModel);

        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = converItemStockModelFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();

        List<ItemModel> itemModelList = itemDOList.stream().map(x -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(x.getId());
            ItemModel itemModel = convertItemModelFromDataObject(x,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }

        //操作获取库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(id);
        ItemModel itemModel = convertItemModelFromDataObject(itemDO,itemStockDO);

        //获取商品秒杀活动信息
        PromoModel promoModel = promoService.getPromoById(itemModel.getId());
        if(promoModel != null && promoModel.getStatus().intValue() != 3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    @Override
    @Transactional
    public Boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {

        //如果库存大于下单数，返回1，否则返回0
        Integer flag = itemStockDOMapper.decreaseStock(itemId,amount);
        if(flag > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }


    private ItemModel convertItemModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }

}
