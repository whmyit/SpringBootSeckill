package com.dxhy.order.consumer;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.consumer.modules.order.controller.ReceiveOrderController;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.JsonUtils;

import java.util.*;

/**
 * @Author fankunfeng
 * @Date 2019-05-14 14:51:16
 * @Describe
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ConsumerStarter.class)
//@WebAppConfiguration
public class ReplaceTest {
    public static void main(String[] args) {
//        testReplace();
//        testReplaceExcel();
//        insertReplaceTest();
    
        Date tkrqqDate = DateUtils.stringToDate("20200221", "yyyyMMdd");
        Date tkrqzDate = DateUtils.stringToDate("20200220", "yyyyMMdd");
    
        if (DateUtil.compare(tkrqqDate, tkrqzDate) > ConfigureConstant.INT_0) {
            System.out.println(1);
        }
    }

    public static void insertReplaceTest() {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        commonOrderInfo.setKpfs("");
        commonOrderInfo.setSjywly("");
        commonOrderInfo.setHzfpxxbbh("");
        commonOrderInfo.setFlagbs("");
        OrderInfo newOrderInfo = new OrderInfo();
//        newOrderInfo.setYwlx("");
//        newOrderInfo.setYfpHm("");
//        newOrderInfo.setYfpDm("");
//        newOrderInfo.setXhfZh("");
//        newOrderInfo.setXhfYh("");
//        newOrderInfo.setXhfNsrsbh("");
//        newOrderInfo.setXhfMc("");
//        newOrderInfo.setXhfDh("");
//        newOrderInfo.setXhfDz("");
//        newOrderInfo.setUpdateTime(new Date());
//        newOrderInfo.setTschbz("");
//        newOrderInfo.setThdh("");
//        newOrderInfo.setSwjgDm("");
//        newOrderInfo.setStatus("");
//        newOrderInfo.setSykchje("");
//        newOrderInfo.setSldMc("");
//        newOrderInfo.setSkr("");
//        newOrderInfo.setSld("");
//        newOrderInfo.setQdXmmc("");
//        newOrderInfo.setQdBz("");
//        newOrderInfo.setPydm("");
//        newOrderInfo.setProcessId("");
//        newOrderInfo.setNsrsbh("");
//        newOrderInfo.setNsrmc("");
//        newOrderInfo.setNsrdzdah("");
//        newOrderInfo.setMdh("");
//        newOrderInfo.setKpxm("");
//        newOrderInfo.setKpr("");
//        newOrderInfo.setKplx("");
//        newOrderInfo.setKpjh("");
//        newOrderInfo.setKphjje("");
//        newOrderInfo.setId("");
//        newOrderInfo.setHyMc("");
//        newOrderInfo.setHyDm("");
//        newOrderInfo.setHjse("");
//        newOrderInfo.setHjbhsje("");
//        newOrderInfo.setGhfZh("");
//        newOrderInfo.setGhfYh("");
//        newOrderInfo.setGhfSj("");
//        newOrderInfo.setGhfSf("");
//        newOrderInfo.setGhfQylx("");
//        newOrderInfo.setGhfNsrsbh("");
        newOrderInfo.setGhfMc("dsad🐟");
//        newOrderInfo.setGhfEmail("");
//        newOrderInfo.setGhfDz("");
//        newOrderInfo.setGhfDh("");
//        newOrderInfo.setFpzlDm("");
//        newOrderInfo.setFpqqlsh("");
//        newOrderInfo.setFhr("");
//
//        newOrderInfo.setDsptbm("");
//        newOrderInfo.setDkbz("");
//        newOrderInfo.setDdrq("");
//        newOrderInfo.setDdlx("");
//        newOrderInfo.setDdh("");
//        newOrderInfo.setCzdm("");
        newOrderInfo.setCreateTime(new Date());
        newOrderInfo.setChyy("撒大苏打《dasd《（dasfa\r\ndasgas");
        //备注特殊处理
        newOrderInfo.setBz("实打实、r\r\t\nsad ");
        newOrderInfo.setByzd5("");
        newOrderInfo.setByzd4("");
        newOrderInfo.setByzd3("");
        newOrderInfo.setByzd2("");
        newOrderInfo.setByzd1("");
        newOrderInfo.setBbmBbh("");
        commonOrderInfo.setOrderInfo(newOrderInfo);
        List<OrderItemInfo> list = new ArrayList<>();
        OrderItemInfo newOrderItenInfo = new OrderItemInfo();
        newOrderItenInfo.setZxbm("");
        newOrderItenInfo.setZzstsgl("");
        newOrderItenInfo.setYhzcbs("");
        newOrderItenInfo.setXmsl("");
        newOrderItenInfo.setXmmc("大白菜🐟六斤岁月无恙");
        newOrderItenInfo.setXmje("");
        newOrderItenInfo.setXmdw("");
        newOrderItenInfo.setXmdj("");
        newOrderItenInfo.setWcje("");
        newOrderItenInfo.setSphxh("");
        newOrderItenInfo.setSpbm("");
        newOrderItenInfo.setSl("");
        newOrderItenInfo.setSe("");
        newOrderItenInfo.setOrderInfoId("");
        newOrderItenInfo.setLslbs("");
        newOrderItenInfo.setKce("");
        newOrderItenInfo.setId("");
        newOrderItenInfo.setHsbz("");
        newOrderItenInfo.setGgxh("");
        newOrderItenInfo.setFphxz("");
        //date格式不处理
        newOrderItenInfo.setCreateTime(new Date());
        newOrderItenInfo.setByzd5("");
        newOrderItenInfo.setByzd4("");
        newOrderItenInfo.setByzd3("");
        newOrderItenInfo.setByzd2("");
        newOrderItenInfo.setByzd1("");
        list.add(newOrderItenInfo);
        commonOrderInfo.setOrderItemInfo(list);
    
        System.out.println(JsonUtils.getInstance().toJsonString(commonOrderInfo));
//        CommonOrderInfo commonOrderInfo1 = BeanTransitionUtils.replaceCharacter(commonOrderInfo);
//        System.out.println(JsonUtils.getInstance().toJsonString(commonOrderInfo1));
    }

    //    @Test
    public static void testReplaceExcel() {
        Map<String, List<NewOrderExcel>> oldMap = new HashMap<>(5);
        List<NewOrderExcel> list = new ArrayList<>();
        NewOrderExcel newOrderExcel = new NewOrderExcel();
        newOrderExcel.setZzstsgl("");
        newOrderExcel.setZxbm("");
        newOrderExcel.setYhzcbs("");
        newOrderExcel.setXmsl("");
//        newOrderExcel.setXmmc("");
//        newOrderExcel.setXmje("");
//        newOrderExcel.setXmdw("");
//        newOrderExcel.setXmdj("");
//        newOrderExcel.setSpbm("");
//        newOrderExcel.setSl("");
//        newOrderExcel.setSe("");
//        newOrderExcel.setRowIndex("");
//        newOrderExcel.setLslbs("");
//        newOrderExcel.setHsbz("");
//        newOrderExcel.setGhfqylx("");
//        newOrderExcel.setGhf_zh("");
//        newOrderExcel.setGhf_yh("");
//        newOrderExcel.setGhf_nsrsbh("");
        newOrderExcel.setGhf_mc("dasfggsd🐟");
        newOrderExcel.setGhf_dz("asda🈷");
        newOrderExcel.setGhf_dh("asda🈷");
        newOrderExcel.setGgxh("dasfggsd（是、\r\t\n啊");
        newOrderExcel.setFpzlDm("");
        newOrderExcel.setDdh("");
        newOrderExcel.setColumnIndex("");
        //备注特殊处理
        newOrderExcel.setBz("撒的撒发射点发、r、\r\t\\\ndsad ");
        newOrderExcel.setBmbbbh("");
        list.add(newOrderExcel);
        oldMap.put(String.valueOf(list.hashCode()), list);
        //替换
        ReceiveOrderController req = new ReceiveOrderController();
        /*Map<String, List<NewOrderExcel>> stringListMap = req.excelReplaceCharacter(oldMap);
        System.out.println(JsonUtils.getInstance().toJsonString(oldMap));
        System.out.println(JsonUtils.getInstance().toJsonString(stringListMap));*/
    }

    public static void testReplace() {
    /*
    *{"COMMON_ORDERS":[{"COMMON_ORDER_HEAD":
    * {"BMB_BBH":"1.0","BYZD1":"","BYZD2":"","BYZD3":"","BYZD4":"","BYZD5":"","BZ":"测试备注","CHYY":"",
    * "DDDATE":"2019-06-10 15:55:41","DDH":"WZBlbhv2XznZk2001","DDQQLSH":"WZBlbhv2XznZk2001",
    *
    * "FHR":"wz复核","GMF_DZ":"№北京中关㊣村大街℃℅℉","GMF_EMAIL":"429630580@qq.com","GMF_GDDH":"010-84567891","GMF_ID":"${GMF_ID}","GMF_MC":"大Σ-Ω象慧Ё云","GMF_NSRSBH":"111122223333QA3","GMF_QYLX":"01","GMF_SF":"","GMF_SJ":"13123456789","HJJE":"9.43","HJSE":"0.57","JSHJ":"10.00","KPLX":"0","KPR":"wz开票","MDH":"","NSRMC":"150001194112132161","NSRSBH":"150001194112132161","QDXMMC":"","QD_BZ":"0","SKR":"wz收款","THDH":"","TSCHBZ":"","XSF_DH":"010-81234567","XSF_DZ":"北京à海淀区蘒","XSF_ID":"${XSF_ID}","XSF_MC":"苹§果","XSF_NSRSBH":"150001194112132161","YFP_DM":"","YFP_HM":"","YWLX":"1"},"ORDER_INVOICE_ITEMS":[{"BYZD1":"","BYZD2":"","BYZD3":"","DW":"24㏄片/板ぁ-ん","FPHXZ":"0","GGXH":"24㏄片/板ぁ-ん","HSBZ":"1","LSLBS":"","SE":"","SL":"0.06","SPBM":"1010115000000000000","XMDJ":"","XMJE":"10.00","XMMC":" 123▓-▕456","XMSL":"","XMXH":"","YHZCBS":"0","ZXBM":"","ZZSTSGL":""}]}],"COMMON_ORDER_BATCH":{"DDQQPCH":"WZBlbhv2XznZk2","FPLB":"2","FPLX":"1","KPJH":"","KZZD":"","NSRSBH":"150001194112132161","SLDID":"-1"}}

     */
        ORDER_INVOICE_ITEM order_invoice_items = new ORDER_INVOICE_ITEM();
        order_invoice_items.setXMXH("");//项目序号
        order_invoice_items.setFPHXZ("0");//发票行性质
        order_invoice_items.setZZSTSGL("免税");//增值税特殊管理
        order_invoice_items.setSPBM("3040502020000000000");//商品编码
        order_invoice_items.setZXBM("");//自行编码
        order_invoice_items.setGGXH("");//规格型号
        //税务相关
        order_invoice_items.setYHZCBS("1");//优惠政策标识
        order_invoice_items.setHSBZ("1");//含税标志
        order_invoice_items.setLSLBS("1");//零税率标识
        order_invoice_items.setSL("0");//税率
        order_invoice_items.setSE("");//税额
        //项目相关
        order_invoice_items.setXMMC("大蒜\r\t\n6斤");//项目名称
        order_invoice_items.setXMJE("100.00");//项目金额
        order_invoice_items.setXMDJ("100.00");//项目单价
        order_invoice_items.setXMSL("1");//项目数量
        order_invoice_items.setDW("");//单位
//        order_invoice_items.setBYZD1("");
//        order_invoice_items.setBYZD2("");
//        order_invoice_items.setBYZD3("");

        COMMON_ORDER_HEAD common_order_head = new COMMON_ORDER_HEAD();
        common_order_head.setBMB_BBH("30.0");//编码表版本号
        common_order_head.setDDDATE("2019-01-03 05:46:37");//订单时间
        common_order_head.setDDH("");//订单号
        common_order_head.setDDQQLSH("");//订单请求流水号
        //销售方信息
        common_order_head.setXSF_DH("12345678");//销售方电话
        common_order_head.setXSF_DZ("北京市海淀区中关村南路6号");//销售方地址
        common_order_head.setXSF_MC("北京大树有限公司🐟");//销售方名称
        common_order_head.setXSF_NSRSBH("911101082018050516");//销售方纳税人识别号   150001196104213403   911101082018050516
        common_order_head.setXSF_YH("工商银行海淀支行");//销售方银行名称
        common_order_head.setXSF_ZH("987654321");//销售方银行账号
        //购买方信息
        common_order_head.setGMF_DZ("№北京中关㊣村大街℃℅℉");//购买方地址
        common_order_head.setGMF_EMAIL("1🐟🐟3@163.com");//购买方邮箱
        common_order_head.setGMF_GDDH("3333🐟🐟333");//购买方固定电话
        common_order_head.setGMF_MC("小明");//购买方名称
        common_order_head.setGMF_NSRSBH("91110108MA004CPN95");//购买方纳税人识别号
        common_order_head.setGMF_QYLX("03");//购买方企业类型
        common_order_head.setGMF_SF("");//购买方省份
        common_order_head.setGMF_SJ("15737150801");//购买方手机
        common_order_head.setGMF_YH("工商银行朝🐟🐟阳支行");//购买方银行名称
        common_order_head.setGMF_ZH("323232🐟🐟3232");//购买方银行账号

        common_order_head.setKPR("凡");//开票人
        common_order_head.setSKR("凡");//收款人
        common_order_head.setFHR("凡");//复核人
        common_order_head.setYFP_DM("");//原发票代码
        common_order_head.setYFP_HM("");//原发票号码
        //金额相关
        common_order_head.setHJJE("100.00");//合计金额
        common_order_head.setHJSE("0.00");//合计税额
        common_order_head.setJSHJ("100.00");//价税合计
        common_order_head.setKPLX("0");//开票类型 0 蓝票 1红票
        common_order_head.setNSRMC("150001196104213403纳税人名称");//纳税人名称
        common_order_head.setNSRSBH("911101082018050516");//纳税人识别号
        common_order_head.setQD_BZ("0");//清单标志
        common_order_head.setQDXMMC("代理费");//清单发票项目名称
        common_order_head.setTHDH("");//退货单号
        common_order_head.setCHYY("");//冲红原因
        common_order_head.setTSCHBZ("");//特殊冲红标志
        common_order_head.setBZ("备注\r\nzzzzz");//备注
//        common_order_head.setBYZD1("");
//        common_order_head.setBYZD2("");
//        common_order_head.setBYZD3("");
//        common_order_head.setBYZD4("");
//        common_order_head.setBYZD5("");

        //组装order
        COMMON_ORDER common_order = new COMMON_ORDER();
        common_order.setCOMMON_ORDER_HEAD(common_order_head);
        List<ORDER_INVOICE_ITEM> list = new ArrayList<>();
        list.add(order_invoice_items);
        common_order.setORDER_INVOICE_ITEMS(list);

        COMMON_ORDER_BATCH common_order_batch = new COMMON_ORDER_BATCH();
        common_order_batch.setDDQQPCH("");//订单请求批次号
        common_order_batch.setFPLX("2");//发票类型 1纸质发票，2电子发票
        common_order_batch.setFPLB("51");//发票类别 发票类型为1时, 0:专票 2:普票41:卷票   发票类型为2时, 51:电子发票
        common_order_batch.setNSRSBH("911101082018050516");//纳税人识别号
        common_order_batch.setSLDID("\t\r\n");//受理点id  电票传""  纸票传 -1
        common_order_batch.setKPJH("");//开票机号 电票"" 纸票受理点为""的时候也是""
        common_order_batch.setKZZD("");//扩展字段
    
        //组装all
        COMMON_ORDER_REQ info = new COMMON_ORDER_REQ();
        info.setCOMMON_ORDER_BATCH(common_order_batch);
        List<COMMON_ORDER> list2 = new ArrayList<>();
        list2.add(common_order);
        info.setCOMMON_ORDERS(list2);
        System.out.println(JsonUtils.getInstance().toJsonString(info));
//        COMMON_ORDER_REQ req = ReplaceCharacterUtils.replaceCharacter(info);
//        System.out.println(JsonUtils.getInstance().toJsonString(req));
    }
}
