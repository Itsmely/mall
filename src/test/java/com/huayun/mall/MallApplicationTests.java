package com.huayun.mall;

import com.huayun.mall.dao.UserDOMapper;
import com.huayun.mall.dataobject.UserDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallApplicationTests {

    @Test
    public void contextLoads() {
    }

}
