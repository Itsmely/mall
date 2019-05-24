package com.huayun.mall.service;

import com.huayun.mall.error.BusinessException;
import com.huayun.mall.service.model.ItemModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ItemService {

    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;
    //商品列表浏览
    List<ItemModel> listItem();
    //商品详情浏览
    ItemModel getItemById(Integer id);

    Boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException;

    void increaseSales(@Param("itemId") Integer itemId, @Param("amount") Integer amount) throws BusinessException;
}
