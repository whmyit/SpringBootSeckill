package com.dxhy.order.service.manager;

import com.dxhy.order.dao.CommodityDao;
import com.dxhy.order.dao.SenderDao;
import com.dxhy.order.model.entity.SenderEntity;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class SenderServiceImplTest {

    @Autowired
    private SenderDao senderDao;
    
    @Autowired
    private CommodityDao commodityDao;
    
    @Test
    public void senderList() {
        Map<String, Object> params = new HashMap<>(5);
        params.put("userId",116);
        params.put("type",0);
        params.put("offset",1);
        params.put("limit",10);
        List<SenderEntity> senderEntities = senderDao.senderList(params);
        log.info(JsonUtils.getInstance().toJsonString(senderEntities));
    }

    @Test
    public void sendersTotal() {
        Map<String, Object> params = new HashMap<>(5);
        int i = senderDao.sendersTotal(params);
        log.info(JsonUtils.getInstance().toJsonString(i));
    }

    @Test
    public void nameList() {
        Map<String, Object> params = new HashMap<>(5);
        String type = "1";
        params.put("userId", 100727);
        if("1".equals(type)){
            List<SenderEntity> list = senderDao.nameReceiveList(params);
            log.info(JsonUtils.getInstance().toJsonString(list));
        }else{
            List<SenderEntity> list = senderDao.nameSenderList(params);
            log.info(JsonUtils.getInstance().toJsonString(list));
        }
    }
    
    
}
