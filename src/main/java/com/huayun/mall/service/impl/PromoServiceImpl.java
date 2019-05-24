package com.huayun.mall.service.impl;

import com.huayun.mall.dao.PromoDOMapper;
import com.huayun.mall.dataobject.PromoDO;
import com.huayun.mall.service.PromoService;
import com.huayun.mall.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class PromoServiceImpl implements PromoService {
    @Resource
    private PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoById(Integer id) {
        PromoDO promoDO = promoDOMapper.getPromoByid(id);
        PromoModel promoModel = convertFromPromoDO(promoDO);
        if(promoModel == null){
            return null;
        }

        //判断当前时间是否在活动时间之内， 活动状态：1 还未开始， 2 进行中， 3 已经结束
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        } else{
            promoModel.setStatus(2);
        }
        return promoModel;
    }

    private PromoModel convertFromPromoDO(PromoDO promoDO){
        if(promoDO == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));

        return promoModel;
    }
}
