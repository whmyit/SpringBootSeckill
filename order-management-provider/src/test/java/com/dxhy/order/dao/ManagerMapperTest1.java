package com.dxhy.order.dao;

import com.dxhy.order.ServiceStarter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

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
public class ManagerMapperTest1 {
    
    @Autowired
    CommodityDao commodityDao;
    @Autowired
    BuyerDao buyerDao;
    @Autowired
    InvoiceDao invoiceDao;
    @Autowired
    RuleSplitDao ruleSplitDao;
    
    @Autowired
    OrderItemInfoMapper orderItemInfoMapper;
    
    @Resource
    TaxClassCodeDao taxClassCodeDao;
    @Resource
    OrderInfoMapper orderInfoMapper;
    
    @Autowired
    OrderInvoiceInfoMapper orderProcessMapper;
    
    @Autowired
    OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Autowired
    OrderInvoiceInfoMapper invocieInfoMapper;
    
    
    @Test
    public void test() {
    
		
		/*OrderItemInfo orderItemInfo = new OrderItemInfo();
		orderItemInfo.setId(DistributedKeyMaker.generateShotKey());
		orderItemInfo.setOrderInfoId(orderId);
		orderItemInfo.setHsbz("0");
		orderItemInfo.setFphxz("1");
		orderItemInfo.setSphxh("1");
		orderItemInfo.setXmje("10.00");
		orderItemInfo.setCreateTime(new Date());
		List<OrderItemInfo> list = new ArrayList<OrderItemInfo>();
		list.add(orderItemInfo);
		//list.add(orderInfo);
		int insertByList = orderItemInfoMapper.insertOrderItemByList(list);
		
		OrderInvoiceInfo invoice = new OrderInvoiceInfo();
		invoice.setId(invoiceId);
		invoice.setOrderInfoId(orderId);
		invoice.setOrderProcessInfoId(processId);
		invoice.setFpqqlsh(fpqqlsh);
		invoice.setDdh(ddh);
		List<OrderInvoiceInfo> list1 = new ArrayList<OrderInvoiceInfo>();*/
        //invocieInfoMapper.ins
        
        
    }
    
}
