package com.huayun.mall.controller;

import com.huayun.mall.controller.viewObject.ItemVO;
import com.huayun.mall.controller.viewObject.UserVO;
import com.huayun.mall.error.BusinessException;
import com.huayun.mall.error.EmBusinessError;
import com.huayun.mall.response.CommonReturnType;
import com.huayun.mall.service.ItemService;
import com.huayun.mall.service.OrderService;
import com.huayun.mall.service.model.ItemModel;
import com.huayun.mall.service.model.OrderModel;
import com.huayun.mall.service.model.UserModel;
import org.apache.ibatis.builder.BuilderException;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.awt.EmbeddedFrame;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Email;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/item")
@CrossOrigin(origins = {"*"},allowCredentials = "true") //允许跨域请求
public class ItemController extends BaseController{

    @Resource
    private ItemService itemService;

    @Resource
    private OrderService orderService;

    @Resource
    private HttpServletRequest httpServletRequest;

    //创建商品信息
    @PostMapping(value = "/create",consumes = {CONSUMES_TYPE})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {

        //封装service请求创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromItemModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);

    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);

        if(itemModel.getPromoModel() != null){
            //有正在进行或即将开始的秒杀活动
            //设置秒杀活动状态
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            //设置秒杀活动id
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            //设置秒杀活动开始时间
            itemVO.setStartTime(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            //设置秒杀活动结束时间
            itemVO.setEndTime(itemModel.getPromoModel().getEndDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            //秒杀价格
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else {
            //没有秒杀活动
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }

    //浏览商品详情页
    @GetMapping("/get")
    @ResponseBody
    public CommonReturnType getItem(Integer id){
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    //浏览商品列表
    @GetMapping("/list")
    @ResponseBody
    public CommonReturnType listItem(){
        List<ItemModel> ListItemModel = itemService.listItem();
        List<ItemVO> listItemVO = new ArrayList<>();
        for(ItemModel itemModel : ListItemModel){
            ItemVO itemVO = convertItemVOFromItemModel(itemModel);
            listItemVO.add(itemVO);
        }
        return CommonReturnType.create(listItemVO);

    }

}
