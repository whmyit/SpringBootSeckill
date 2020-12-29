package com.dxhy.order.consumer;

import com.dxhy.order.consumer.model.PageOrderExt;
import com.dxhy.order.consumer.modules.order.service.IOrderInfoService;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.OrderProcessInfoExt;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试详情页面原始订单展示
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2018/10/20 10:40
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public class OrderProcessInfoExtTest {
    
    @Resource
    IOrderInfoService orderInfoService;

//    @Test
//    public void test() {
//        String orderProcessId = "201810201151481053493963203411969";
//        String fpqqlsh = "201810201151481053493962788175872";
//        OderDetailInfo oderDetailInfo = orderInfoService.selectOrderDetailByOrderProcessIdAndFpqqlsh(orderProcessId, fpqqlsh);
//        log.info(JsonUtils.getInstance().toJsonString(oderDetailInfo));
//        log.error(JsonUtils.getInstance().toJsonString(oderDetailInfo.getPageOrderExts()));
//
//    }
//
//    @Test
//    public void test1() {
//        String orderProcessId = "201810201628311053563599672442880";
//        String fpqqlsh = "f118ccd8924e453093ac1b87ebd76be9";
//        OderDetailInfo oderDetailInfo = orderInfoService.selectOrderDetailByOrderProcessIdAndFpqqlsh(orderProcessId, fpqqlsh);
//        log.info(JsonUtils.getInstance().toJsonString(oderDetailInfo));
//        log.error(JsonUtils.getInstance().toJsonString(oderDetailInfo.getPageOrderExts()));
//
//    }
    
    public static void main(String[] args) {
        OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
        orderProcessInfo.setDdh("2");
        List<PageOrderExt> pageOrderExts = new ArrayList<>();
        getOrderProcessInfoExtInfo(orderProcessInfo, pageOrderExts, false);
        log.info(JsonUtils.getInstance().toJsonString(pageOrderExts));
        
    }

    public static void getOrderProcessInfoExtInfo(OrderProcessInfo orderProcessInfo, List<PageOrderExt> pageOrderExts, boolean deep) {
        while (true) {
            List<OrderProcessInfoExt> orderProcessInfoExt = getorderext();
            if (orderProcessInfoExt != null && orderProcessInfoExt.size() > 0) {
                for (int i = 0; i < orderProcessInfoExt.size(); i++) {

                    PageOrderExt pageOrderExt = new PageOrderExt();
                    OrderProcessInfoExt ext = orderProcessInfoExt.get(i);
                    if (ext != null) {
                        OrderProcessInfo parentOrderProcessInfo = getorderinfo();
                        if (parentOrderProcessInfo != null) {
                            /**
                             * 编辑后订单需要继续查询父订单,直到父订单出现结果为多条数据(主要用于展示拆分订单,订单编辑后重新生成订单,需要一直遍历获取数据,直到出现多条数据或者查询结果为空结束.)
                             * 如果OrderProcessInfoId和ParentOrderProcessId 相同,则说明该订单是编辑后订单或者是原始订单,
                             * 如果父订单为一条,判断父订单订单号和当前订单号是否一致,如果一致则继续循环,获取
                             *
                             */
                            if (parentOrderProcessInfo.getDdh().equals(orderProcessInfo.getDdh())) {

                                getOrderProcessInfoExtInfo(parentOrderProcessInfo, pageOrderExts, true);


                            } else {
                                if (deep && orderProcessInfoExt.size() > 1) {
                                    break;
                                }
                                pageOrderExt.setDdh(parentOrderProcessInfo.getDdh());
                                pageOrderExt.setDdzt(parentOrderProcessInfo.getDdzt());
                                pageOrderExt.setFpqqlsh(parentOrderProcessInfo.getFpqqlsh());
                                pageOrderExt.setOrderId(parentOrderProcessInfo.getId());
                                pageOrderExts.add(pageOrderExt);
                            }
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (!deep) {
                    break;
                }

            } else {
                break;
            }
        }

    }

    public static List<OrderProcessInfoExt> getorderext() {
        List<OrderProcessInfoExt> orderProcessInfoExts = new ArrayList<>();
        OrderProcessInfoExt orderProcessInfoExt = new OrderProcessInfoExt();
        orderProcessInfoExt.setOrderProcessInfoId("123123");
        orderProcessInfoExts.add(orderProcessInfoExt);

        return orderProcessInfoExts;
    }

    public static OrderProcessInfo getorderinfo() {
        OrderProcessInfo parentOrderProcessInfo = new OrderProcessInfo();
        parentOrderProcessInfo.setId("1");
        parentOrderProcessInfo.setDdh("1");
        parentOrderProcessInfo.setFpqqlsh("1");
        parentOrderProcessInfo.setDdzt("1");

        return parentOrderProcessInfo;
    }

    public static OrderProcessInfo getorderinfo2() {
        OrderProcessInfo parentOrderProcessInfo = new OrderProcessInfo();
        parentOrderProcessInfo.setId("2");
        parentOrderProcessInfo.setDdh("2");
        parentOrderProcessInfo.setFpqqlsh("2");
        parentOrderProcessInfo.setDdzt("2");

        return parentOrderProcessInfo;
    }

    public static OrderProcessInfo getorderinfo3() {
        OrderProcessInfo parentOrderProcessInfo = new OrderProcessInfo();
        parentOrderProcessInfo.setId("3");
        parentOrderProcessInfo.setDdh("3");
        parentOrderProcessInfo.setFpqqlsh("3");
        parentOrderProcessInfo.setDdzt("3");

        return parentOrderProcessInfo;
    }

    public static OrderProcessInfo getorderinfo4() {
        OrderProcessInfo parentOrderProcessInfo = new OrderProcessInfo();
        parentOrderProcessInfo.setId("4");
        parentOrderProcessInfo.setDdh("4");
        parentOrderProcessInfo.setFpqqlsh("4");
        parentOrderProcessInfo.setDdzt("4");

        return parentOrderProcessInfo;
    }
}
