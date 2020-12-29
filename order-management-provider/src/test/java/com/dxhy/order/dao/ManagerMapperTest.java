package com.dxhy.order.dao;

import com.dxhy.order.ServiceStarter;
import com.dxhy.order.model.entity.RuleSplitEntity;
import com.dxhy.order.model.entity.SenderEntity;
import com.dxhy.order.utils.DistributedKeyMaker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：ManagerMapperTest
 * @Description ：测试mapper是否正确
 * @date ：2019年7月11日 下午10:54:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceStarter.class)
@WebAppConfiguration
@Slf4j
public class ManagerMapperTest {
    
    @Autowired
    CommodityDao commodityDao;
    @Autowired
    BuyerDao buyerDao;
    @Autowired
    InvoiceDao invoiceDao;
    @Autowired
    RuleSplitDao ruleSplitDao;
    
    @Resource
    TaxClassCodeDao taxClassCodeDao;
    
    @Resource
    SenderDao senderDao;
    
    @Resource
    SpecialInvoiceReversalDao specialInvoiceReversalDao;
    
    
    @Test
    public void testRuleSplitDao() {
        RuleSplitEntity entity = new RuleSplitEntity();
        entity.setId(DistributedKeyMaker.generateShotKey());
        entity.setTaxpayerCode("3333");
        ruleSplitDao.update(entity);
        ruleSplitDao.insert(entity);
        ruleSplitDao.selectRuleSplit("11", "111");
    }
    
    @Test
    public void testSendList() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("offset", 10);
        param.put("limit", 20);
        param.put("userId", "10000");
        List<SenderEntity> senderList = senderDao.senderList(param);
        
    }

    
}
