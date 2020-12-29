package com.dxhy.order.consumer;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.invoice.protocol.sl.*;
import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderItemInfoService;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.api.ValidateOrderInfo;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceRushRedService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.openapi.service.IAllocateInvoiceInterfaceServiceV3;
import com.dxhy.order.consumer.openapi.service.IInterfaceServiceV3;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWNLOAD_REQ;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWNLOAD_RSP;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_REQ;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_RSP;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_RSP;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.utils.InterfaceBeanTransUtils;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class InterfaceTestV3 {
    
    @Autowired
    IInterfaceServiceV3 interfaceServiceV3;
    
    @Autowired
    IAllocateInvoiceInterfaceServiceV3 allocateInvoiceInterfaceServiceV3;
    
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Resource
    private MakeOutAnInvoiceService makeOutAnInovice;
    
    @Resource
    private InvoiceRushRedService invoiceRushRedService;
    
    @Reference
    ApiOrderItemInfoService apiOrderItemInfoService;
    
    
    @Reference
    ApiOrderInfoService apiOrderInfoService;
    
    @Reference
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    /**
     * 开票接口测试
     */
    @Test
    public void testKp() {
        String request = "{\n" +
                "    \"COMMON_ORDER_BATCH\": {\n" +
                "        \"DDQQPCH\": \"BT_8135434e8f8144eb8c04f36ef86b6364\", \n" +
                "        \"NSRSBH\": \"1403016L1NN5336\", \n" +
                "        \"SLDID\": \"\", \n" +
                "        \"KPJH\": \"\", \n" +
                "        \"FPLX\": \"2\", \n" +
                "        \"FPLB\": \"51\", \n" +
                "        \"KZZD\": \"扩展字段\"\n" +
                "    }, \n" +
                "    \"COMMON_ORDERS\": [\n" +
                "        {\n" +
                "            \"COMMON_ORDER_HEAD\": {\n" +
                "                \"DDQQLSH\": \"BT_8135434e8f8144eb8c04f36ef86b6364\", \n" +
                "                \"NSRSBH\": \"1403016L1NN5336\", \n" +
                "                \"NSRMC\": \"纳税人名称\", \n" +
                "                \"KPLX\": \"0\", \n" +
                "                \"BMB_BBH\": \"31.0\", \n" +
                "                \"XSF_NSRSBH\": \"1403016L1NN5336\", \n" +
                "                \"XSF_MC\": \"销售方名称\", \n" +
                "                \"XSF_DZ\": \"销售方地址\", \n" +
                "                \"XSF_DH\": \"销售方电话\", \n" +
                "                \"XSF_YH\": \"销售方银行名称\", \n" +
                "                \"XSF_ZH\": \"销售方银行账号\", \n" +
                "                \"GMF_NSRSBH\": \"1403016L1NN5336\", \n" +
                "                \"GMF_MC\": \"购买方名称\", \n" +
                "                \"GMF_DZ\": \"重庆市渝北区空港工业园100号地块标准厂房6号楼、7号楼 \", \n" +
                "                \"GMF_QYLX\": \"01\", \n" +
                "                \"GMF_SF\": \"1\", \n" +
                "                \"GMF_GDDH\": \"123\", \n" +
                "                \"GMF_SJ\": \"456\", \n" +
                "                \"GMF_EMAIL\": \"购买方邮箱\", \n" +
                "                \"GMF_YH\": \"购买方银行名称\", \n" +
                "                \"GMF_ZH\": \"购买方银行账号\", \n" +
                "                \"KPR\": \"开票人\", \n" +
                "                \"SKR\": \"收款人\", \n" +
                "                \"FHR\": \"复核人\", \n" +
                "                \"YFP_DM\": \"\", \n" +
                "                \"YFP_HM\": \"\", \n" +
                "                \"QD_BZ\": \"0\", \n" +
                "                \"QDXMMC\": \"清单发票项目名称\", \n" +
                "                \"JSHJ\": \"1.00\", \n" +
                "                \"HJJE\": \"0\", \n" +
                "                \"HJSE\": \"0\", \n" +
                "                \"BZ\": \"备注\", \n" +
                "                \"CHYY\": \"冲红原因\", \n" +
                "                \"TSCHBZ\": \"0\",\n" +
                "                \"DDH\": \"321654\", \n" +
                "                \"THDH\": \"退货单号\", \n" +
                "                \"DDDATE\": \"2018-10-22 12:00:00\",\n" +
                "                \"BYZD1\": \"备用字段1\", \n" +
                "                \"BYZD2\": \"备用字段2\", \n" +
                "                \"BYZD3\": \"备用字段3\", \n" +
                "                \"BYZD4\": \"备用字段4\", \n" +
                "                \"BYZD5\": \"备用字段5\"\n" +
                "            }, \n" +
                "            \"ORDER_INVOICE_ITEMS\": [\n" +
                "                {\n" +
                "                    \"XMXH\": \"1\", \n" +
                "                    \"FPHXZ\": \"0\", \n" +
                "                    \"SPBM\": \"1010115010100000000\", \n" +
                "                    \"ZXBM\": \"332\", \n" +
                "                    \"YHZCBS\": \"0\", \n" +
                "                    \"LSLBS\": \"\", \n" +
                "                    \"ZZSTSGL\": \"\", \n" +
                "                    \"XMMC\": \"项目11名称\", \n" +
                "                    \"GGXH\": \"ge\", \n" +
                "                    \"DW\": \"1\", \n" +
                "                    \"XMSL\": \"1\", \n" +
                "                    \"XMDJ\": \"1.00\", \n" +
                "                    \"XMJE\": \"1.00\", \n" +
                "                    \"HSBZ\": \"1\", \n" +
                "                    \"SL\": \"0.06\", \n" +
                "                    \"SE\": \"\", \n" +
                "                    \"BYZD1\": \"\", \n" +
                "                    \"BYZD2\": \"备用字段2\", \n" +
                "                    \"BYZD3\": \"备用字段3\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";
    
        /**
         * 自动开票
         */
    
        COMMON_ORDER_REQ parseObject = JsonUtils.getInstance().parseObject(request, COMMON_ORDER_REQ.class);
    
        DDPCXX_REQ ddpcxx_req = InterfaceBeanTransUtils.transDdpcxxReq(parseObject);
    
        DDPCXX_RSP ddpcxx_rsp = allocateInvoiceInterfaceServiceV3.allocateInvoicesV3(ddpcxx_req, "289efb7512e54146273b982456b03f42ea93", parseObject.getCOMMON_ORDER_BATCH().getKPJH(),null);
    
        COMMON_ORDER_RSP commonOrderRsp = InterfaceBeanTransUtils.transDdpcxxRsp(ddpcxx_rsp);
    
    
        String returnJsonString = JsonUtils.getInstance().toJsonString(commonOrderRsp);
        System.out.println("1341341234" + returnJsonString);
    
    }
    
    /**
     * 开票接口字段整理
     */
    @Test
    public void testKpZL() {
        String data = "{\"COMMON_ORDER_BATCH\":{\"DDQQPCH\":\"d369a23511c54eafb9c83f65a37f0b5f\",\"NSRSBH\":\"150001196104213403\",\"SLDID\":\"\",\"KPJH\":\"\",\"FPLX\":\"2\",\"FPLB\":\"51\",\"KZZD\":\"\"},\"COMMON_ORDERS\":[{\"COMMON_ORDER_HEAD\":{\"DDQQLSH\":\"0bff4402b4354dd49fbfa6cf04afb48e\",\"NSRSBH\":\"150001196104213403\",\"NSRMC\":\"北京中原房地产经纪有限公司\",\"KPLX\":\"0\",\"BMB_BBH\":\"31.0\",\"XSF_NSRSBH\":\"150001196104213403\",\"XSF_MC\":\"北京中原房地产经纪有限公司\",\"XSF_DZ\":\"北京市朝阳区朝外大街20号联合大厦10层\",\"XSF_DH\":\"65951688\",\"XSF_YH\":\"\",\"XSF_ZH\":\"\",\"GMF_NSRSBH\":\"150001196104213403\",\"GMF_MC\":\"个人(大象)\",\"GMF_DZ\":\"\",\"GMF_QYLX\":\"03\",\"GMF_SF\":\"\",\"GMF_GDDH\":\"\",\"GMF_SJ\":\"\",\"GMF_EMAIL\":\"\",\"GMF_YH\":\"\",\"GMF_ZH\":\"\",\"KPR\":\"CH2\",\"SKR\":\"\",\"FHR\":\"\",\"YFP_DM\":\"\",\"YFP_HM\":\"\",\"QD_BZ\":\"0\",\"QDXMMC\":\"代理费\",\"JSHJ\":\"11.00\",\"HJJE\":\"0\",\"HJSE\":\"0\",\"BZ\":\"213123\",\"CHYY\":\"\",\"TSCHBZ\":\"\",\"DDH\":\"20190103062021220679\",\"THDH\":\"\",\"DDDATE\":\"2019-01-03 06:20:21\",\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"BYZD4\":\"\",\"BYZD5\":\"\"},\"ORDER_INVOICE_ITEMS\":[{\"XMXH\":\"\",\"FPHXZ\":\"0\",\"SPBM\":\"3040502020000000000\",\"ZXBM\":\"\",\"YHZCBS\":\"0\",\"LSLBS\":\"\",\"ZZSTSGL\":\"\",\"XMMC\":\"代理费\",\"GGXH\":\"\",\"DW\":\"\",\"XMSL\":\"1\",\"XMDJ\":\"100\",\"XMJE\":\"100\",\"HSBZ\":\"0\",\"SL\":\"0\",\"SE\":\"1\",\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\"}]}]}";
    
        //   String url = "{\"COMMON_INVOICE\":[{\"COMMON_INVOICE_DETAIL\":[{\"XMMC\":\"热力\",\"HSBZ\":\"0\",\"ZXBM\":\"\",\"LSLBS\":\"\",\"XMSL\":2,\"XMXH\":1,\"SPBM\":\"1100000000000000000\",\"GGXH\":\"YSM25M-0715-S-L\",\"XMDJ\":11666.665,\"SE\":4666.67,\"DW\":\"台\",\"YHZCBS\":\"0\",\"XMJE\":23333.33,\"SL\":0.2,\"FPHXZ\":\"0\"}],\"COMMON_INVOICE_ORDER\":{\"DDH\":\"Req201812170000012\",\"DDDATE\":\"\",\"THDH\":null},\"COMMON_INVOICE_HEAD\":{\"QDXMMC\":\"\",\"GMF_NSRSBH\":\"91440300192449004F\",\"XSF_DH\":\"59281888\",\"KPR\":\"李汀兰\",\"GMF_SJ\":\"\",\"FHR\":\"李汀兰\",\"QD_BZ\":\"0\",\"KPLX\":0,\"GMF_MC\":\"深圳市怡岛环境空调工程有限公司\",\"GMF_DZ\":\"深圳市南山区蛇口半岛花园A区6栋201电话:0755-26883751\",\"GMF_SF\":\"\",\"BZ\":\"FZHI1800097FS  中山红棉电镀厂YSM风柜FZHI1800097FS  中山红棉电镀厂YSM风柜\",\"BYZD4\":\"\",\"BYZD3\":\"\",\"BYZD2\":\"\",\"BYZD1\":\"\",\"HJJE\":\"23333.33\",\"NSRSBH\":\"911101082018050516\",\"BYZD5\":\"\",\"GMF_GDDH\":\"\",\"XSF_NSRSBH\":\"911101082018050516\",\"FPQQLSH\":\"94918039-8d5d-4bc8-91bf-a65ad88f0f96001\",\"GMF_WX\":\"\",\"JSHJ\":28000,\"GMF_EMAIL\":\"\",\"BMB_BBH\":\"1\",\"NSRMC\":\"北京江森自控有限公司\",\"GMF_QYLX\":\"01\",\"TSCHBZ\":0,\"XSF_YHZH\":\"中国工商银行北京大都市支行 0200080319020158224\",\"HJSE\":4666.67,\"GMF_YHZH\":\"招商银行深圳蛇口支行811281793710001?\",\"XSF_MC\":\"北京江森自控有限公司\",\"XSF_DZ\":\"北京市海淀区知春路51号慎昌大厦511室 59281888\",\"PYDM\":\"\",\"SKR\":\"李汀兰\"}}],\"COMMON_INVOICES_BATCH\":{\"KPJH\":\"0\",\"FPLB\":\"2\",\"SLDID\":\"95\",\"FPLX\":\"1\",\"FPQQPCH\":\"94918039-8d5d-4bc8-91bf-a65ad88f0f96\",\"KZZD\":\"\",\"NSRSBH\":\"911101082018050516\"}}";

//        System.out.println("base64加密开票数据示例："+Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8)));
    }
    
    
    /**
     * 开票结果测试
     */
    @Test
    public void testKPJG() {
        String requestStr = "{\n" +
                "    \"FPLX\": \"1\", \n" +
                "    \"NSRSBH\": \"140301206111099566\",\n" +
                "    \"RETURNFAIL\": \"0\",\n" +
                "    \"DDQQPCH\": \"55448844\"\n" +
                "}\n";
    
        /**
         * 开具发票结果获取
         */
    
        GET_INVOICE_REQ getInvoiceReq = JsonUtils.getInstance().parseObject(requestStr, GET_INVOICE_REQ.class);
    
        DDKJXX_REQ ddkjxx_req = InterfaceBeanTransUtils.transDdkjxxReq(getInvoiceReq);
    
        DDKJXX_RSP ddkjxx_rsp = interfaceServiceV3.getAllocatedInvoicesV3(ddkjxx_req);
    
        GET_INVOICE_RSP getInvoiceRsp = InterfaceBeanTransUtils.transDdkjxxRsp(ddkjxx_rsp);
    
    
        String returnJsonString = JsonUtils.getInstance().toJsonString(getInvoiceRsp);
        System.out.println("1341341234" + returnJsonString);
    
    }
    
    /**
     * 订单和发票数据查询接口
     */
    @Test
    public void testDDANDFP() {
        String commonDecrypt2 = "{\n" +
                "    \"NSRSBH\": \"150001196104213403\", \n" +
                "    \"DDQQLSH\": \"2d1f20a2e49647b4bd2ca7246\", \n" +
                "    \"DDH\": \"394f834b7f\"\n" +
                "}\n";
    
        ORDER_REQUEST order_request = JsonUtils.getInstance().parseObject(commonDecrypt2, ORDER_REQUEST.class);
        DDFPCX_REQ ddfpcx_req = InterfaceBeanTransUtils.transDdfpcxReq(order_request);
        DDFPCX_RSP ddfpcx_rsp = interfaceServiceV3.getOrderInfoAndInvoiceInfoV3(ddfpcx_req);
    
        ORDER_INVOICE_RESPONSE response1 = InterfaceBeanTransUtils.transDdfpcxRsp(ddfpcx_rsp);
    
    
        String returnJsonString = JsonUtils.getInstance().toJsonString(response1);
        System.out.println("jieguo :" + returnJsonString);
    }
    
    /**
     * 发票作废接口
     */
    //@Test
    /*public void testZf() {
        String commonDecrypt2 = "{\n" +
                "    \"ZFPCH\": \"作废批次号\", \n" +
                "    \"FP_DM\": \"发票代码\", \n" +
                "    \"FPQH\": \"发票起号\", \n" +
                "    \"FPZH\": \"发票止号\", \n" +
                "    \"ZFLX\": \"作废类型\", \n" +
                "    \"ZFYY\": \"作废原因\"\n" +
                "}\n";

        INVALID_INVOICE_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, INVALID_INVOICE_REQ.class);

//        DEPRECATE_INVOICES_REQ parseObject1 = BeanTransitionUtils.transitionInvoiceInvalidRequestV3(parseObject);
        System.out.println(JsonUtils.getInstance().toJsonString(parseObject));

        DEPRECATE_INVOICES_RSP invoiceInvalid = interfaceService.invoiceInvalid(parseObject);

        String a = "{\n" +
                "    \"ZFPCH\": \"作废批次号\", \n" +
                "    \"STATUS_CODE\": \"状态代码\", \n" +
                "    \"STATUS_MESSAGE\": \"状态信息\", \n" +
                "    \"DEPRECATE_FAILED_INVOICE\": [\n" +
                "        {\n" +
                "            \"FP_DM\": \"发票代码\", \n" +
                "            \"FP_HM\": \"发票号码\", \n" +
                "            \"STATUS_CODE\": \"状态代码\", \n" +
                "            \"STATUS_MESSAGE\": \"状态信息\"\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";

        invoiceInvalid = JsonUtils.getInstance().parseObject(a, DEPRECATE_INVOICES_RSP.class);
        com.dxhy.order.consumer.protocol.invoice.DEPRECATE_INVOICES_RSP deprecate_invoices_rsp = BeanTransitionUtils.transition_deprecate_invoices_rsp(invoiceInvalid);

        INVALID_INVOICE_RSP invalid_invoice_rsp = BeanTransitionUtils.transitionInvoiceInvalidResponseV3(deprecate_invoices_rsp);
        String returnJsonString = JsonUtils.getInstance().toJsonString(invalid_invoice_rsp);

        System.out.println(JsonUtils.getInstance().toJsonString(returnJsonString));
    }
    */
    /**
     * 专票申请单上传接口
     */
    @Test
    public void testZPsqdsc() {
    
        String commonDecrypt2 = "{\n" +
                "  \"RED_INVOICE_FORM_BATCH\": {\n" +
                "    \"FPLB\": \"0\",\n" +
                "    \"FPLX\": \"1\",\n" +
                "    \"KPJH\": \"0\",\n" +
                "    \"KZZD\": \"\",\n" +
                "    \"NSRSBH\": \"15000120561127953X\",\n" +
                "    \"SLDID\": \"221\",\n" +
                "    \"SQBSCQQPCH\": \"661616315914200317163419\",\n" +
                "    \"SQLB\": \"0\"\n" +
                "  },\n" +
                "  \"RED_INVOICE_FORM_UPLOADS\": [\n" +
                "    {\n" +
                "      \"ORDER_INVOICE_ITEMS\": [\n" +
                "        {\n" +
                "          \"DW\": \"\",\n" +
                "          \"FPHXZ\": \"1\",\n" +
                "          \"GGXH\": \"君不见黄河之水天上来\",\n" +
                "          \"HSBZ\": \"0\",\n" +
                "          \"LSLBS\": \"\",\n" +
                "          \"SE\": \"-6.00\",\n" +
                "          \"SL\": \"0.06\",\n" +
                "          \"SPBM\": \"1010115010200000000\",\n" +
                "          \"XMDJ\": \"10.00\",\n" +
                "          \"XMJE\": \"-100.00\",\n" +
                "          \"XMMC\": \"商品\",\n" +
                "          \"XMSL\": \"-10\",\n" +
                "          \"XMXH\": \"1\",\n" +
                "          \"YHZCBS\": \"0\",\n" +
                "          \"ZXBM\": \"\",\n" +
                "          \"ZZSTSGL\": \"\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"RED_INVOICE_FORM_HEAD\": {\n" +
                "        \"BMB_BBH\": \"34\",\n" +
                "        \"GMF_MC\": \"140301206111099566\",\n" +
                "        \"GMF_NSRSBH\": \"140301206111099566\",\n" +
                "        \"HJJE\": \"-100.00\",\n" +
                "        \"HJSE\": \"-6.00\",\n" +
                "        \"KZZD1\": \"\",\n" +
                "        \"KZZD2\": \"\",\n" +
                "        \"SQBSCQQLSH\": \"661616315914200317163419\",\n" +
                "        \"SQSM\": \"0000000100\",\n" +
                "        \"TKSJ\": \"20200317163520\",\n" +
                "        \"XSF_MC\": \"\",\n" +
                "        \"XSF_NSRSBH\": \"\",\n" +
                "        \"XXBLX\": \"0\",\n" +
                "        \"YFP_DM\": \"1100111561\",\n" +
                "        \"YFP_HM\": \"36954824\",\n" +
                "        \"YFP_KPRQ\": \"2020-03-17 12:34:56\",\n" +
                "        \"YYSBZ\": \"0000000000\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    
        RED_INVOICE_FORM_REQ redInvoiceFormReq = JsonUtils.getInstance().parseObject(commonDecrypt2, RED_INVOICE_FORM_REQ.class);
    
        HZSQDSC_REQ hzsqdsc_req = InterfaceBeanTransUtils.transHzsqdscReq(redInvoiceFormReq);
    
        HZSQDSC_RSP hzsqdsc_rsp = interfaceServiceV3.specialInvoiceRushRedV3(hzsqdsc_req, redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getKPJH());
    
        RED_INVOICE_FORM_RSP redInvoiceFormRsp = InterfaceBeanTransUtils.transHzsqdscRsp(hzsqdsc_rsp);
    
        String returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceFormRsp);
    
        System.out.println(returnJsonString);
    
    }
    
    @Test
    public void testchsqdxz() {
        
        String commonDecrypt2 = "{\n" +
                "    \"SQBXZQQPCH\": \"申请表审核结果下载请求批次号\", \n" +
                "    \"NSRSBH\": \"申请方纳税人识别号\", \n" +
                "    \"SLDID\": \"受理点ID\", \n" +
                "    \"KPJH\": \"开票机号\", \n" +
                "    \"FPLX\": \"发票类型\", \n" +
                "    \"FPLB\": \"发票类别\", \n" +
                "    \"TKRQ_Q\": \"填开日期起，可空\", \n" +
                "    \"TKRQ_Z\": \"填开日期止，可空\", \n" +
                "    \"GMF_NSRSBH\": \"购买方税号，可空\", \n" +
                "    \"XSF_NSRSBH\": \"销售方税号，可空\", \n" +
                "    \"XXBBH\": \"信息表编号，可空\", \n" +
                "    \"XXBFW\": \"信息表下载范围：0全部；1本企业申请；2其它企业申请\", \n" +
                "    \"PAGENO\": \"页数\", \n" +
                "    \"PAGESIZE\": \"个数\"\n" +
                "}\n";
        RED_INVOICE_FORM_DOWNLOAD_REQ redInvoiceFormDownloadReq = JsonUtils.getInstance().parseObject(commonDecrypt2, RED_INVOICE_FORM_DOWNLOAD_REQ.class);
    
        HZSQDXZ_REQ hzsqdxz_req = InterfaceBeanTransUtils.transHzsqdxzReq(redInvoiceFormDownloadReq);
    
        HZSQDXZ_RSP hzsqdxz_rsp = interfaceServiceV3.downSpecialInvoiceV3(hzsqdxz_req, redInvoiceFormDownloadReq.getSLDID(), redInvoiceFormDownloadReq.getKPJH());
    
        RED_INVOICE_FORM_DOWNLOAD_RSP redInvoiceFormDownloadRsp = InterfaceBeanTransUtils.transHzsqdxzRsp(hzsqdxz_rsp);
    
        String returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceFormDownloadRsp);
        System.out.println(returnJsonString);
    }
//
//    @Test
//    public void test1() {
//        String str = "201811301436181068393263037284353";
//        CommonOrderInfo orderInfo = new CommonOrderInfo();
//        // String str =
//        // "{\"cOMMON_INVOICE\":[{\"cOMMON_INVOICE_DETAIL\":[{\"fPHXZ\":\"0\",\"hSBZ\":\"0\",\"lSLBS\":\"1\",\"sE\":0,\"sL\":0,\"sPBM\":\"3070401000000000000\",\"xMDJ\":20,\"xMJE\":40,\"xMMC\":\"*餐饮服务*餐饮服务3\",\"xMSL\":2,\"xMXH\":1,\"yHZCBS\":\"1\",\"zZSTSGL\":\"免税\"}],\"cOMMON_INVOICE_HEAD\":{\"bMB_BBH\":\"1.0\",\"fHR\":\"ysy\",\"fPQQLSH\":\"803d1f6205514fbea4b1baa958fdf72b001\",\"gMF_EMAIL\":\"750733747@qq.com\",\"gMF_MC\":\"大象信息\",\"gMF_QYLX\":\"03\",\"hJJE\":40,\"hJSE\":0,\"jSHJ\":40,\"kPLX\":\"0\",\"kPR\":\"ysy\",\"nSRMC\":\"大象慧云\",\"nSRSBH\":\"911101082018050516\",\"qD_BZ\":\"0\",\"sKR\":\"ysy\",\"tSCHBZ\":\"0\",\"xSF_DH\":\"13688889999\",\"xSF_DZ\":\"北京\",\"xSF_MC\":\"大象慧云\",\"xSF_NSRSBH\":\"911101082018050516\",\"xSF_YHZH\":\"6222888899996666交通银行\"},\"cOMMON_INVOICE_ORDER\":{\"dDDATE\":\"2018-09-18
//        // 17:22:10\",\"dDH\":\"wz201809171104\"}}],\"cOMMON_INVOICES_BATCH\":{\"fPLB\":\"51\",\"fPLX\":\"2\",\"fPQQPCH\":\"803d1f6205514fbea4b1baa958fdf72b\",\"nSRSBH\":\"911101082018050516\"}}";
//        OrderInfo selectByPrimaryKey = apiOrderInfoService.selectOrderInfoByOrderId(str);
//        List<OrderItemInfo> selectByOrderId = apiOrderItemInfoService.selectOrderItemInfoByOrderId(str);
//        orderInfo.setOrderItemInfo(selectByOrderId);
//        orderInfo.setOrderInfo(selectByPrimaryKey);
//        List<CommonOrderInfo> list = new ArrayList<CommonOrderInfo>();
//        list.add(orderInfo);
//        makeOutAnInovice.makeOutAnInovice(list, "11", "aaaaa");
//
//    }
    
    /**
     * 专票冲红申请接口测试
     */
    @Test
    public void testZPCH() {
        
        String reqStr = "{\"HZFPSQBSCS_BATCH\":{\"KPJH\":\"0\",\"FPLB\":\"0\",\"SQLB\":\"0\",\"SLDID\":\"88\",\"FPLX\":\"1\",\"SQBSCQQPCH\":\"4d0abffc-f984-4a90-a96b-7974e052ad43\",\"KZZD\":\"\",\"NSRSBH\":\"911101082018050516\"},\"HZFPSQBSC\":[{\"HZFPSQBSC_HEAD\":{\"KZZD2\":\"\",\"GMF_NSRSBH\":\"91110108600040399G\",\"GMF_MC\":\"微软（中国）有限公司\",\"KZZD1\":\"\",\"SQBSCQQLSH\":\"71c9cdc1-2a3c-4af6-abbc-851873cd026d\",\"YYSBZ\":\"0000000000\",\"TKSJ\":\"20181101085426\",\"YFP_DM\":\"5000153560\",\"YFP_HM\":\"29436572\",\"SQSM\":\"0000000100\",\"BMB_BBH\":\"1.0\",\"XXBLX\":\"0\"},\"HZFPSQBSC_DETAIL\":[{\"XMMC\":\"热力\",\"HSBZ\":\"0\",\"ZXBM\":\"\",\"LSLBS\":\"\",\"XMSL\":10,\"ZZSTSGL\":\"\",\"XMXH\":1,\"SPBM\":\"1100000000000000000\",\"GGXH\":null,\"XMDJ\":1497.6,\"SE\":2396.16,\"DW\":\"个\",\"YHZCBS\":\"0\",\"XMJE\":14976,\"SL\":0.16,\"FPHXZ\":\"0\"}]}]}";
//        R r = interfaceService.specialInvoiceRushRed(reqStr);
//        System.out.println(r);
        HZFPSQBSCS_REQ req = JsonUtils.getInstance().parseObject(reqStr, HZFPSQBSCS_REQ.class);
        System.out.println(JsonUtils.getInstance().toJsonString(req));
    
    }
    
    
    @Test
    public void hz() {
    
        String ss = "{\r\n" +
                "    \"HZFPSQBSCS_BATCH\": {\r\n" +
                "        \"KPJH\": \"0\", \r\n" +
                "        \"FPLB\": \"0\", \r\n" +
                "        \"SQLB\": \"1\", \r\n" +
                "        \"SLDID\": \"88\", \r\n" +
                "        \"FPLX\": \"1\", \r\n" +
                "        \"SQBSCQQPCH\": \"a9240d33-a0d3-42e5-8248-ccc4504f31bd\", \r\n" +
                "        \"KZZD\": \"\", \r\n" +
                "        \"NSRSBH\": \"911101082018050516\"\r\n" +
                "    }, \r\n" +
                "    \"HZFPSQBSC\": [\r\n" +
                "        {\r\n" +
                "            \"HZFPSQBSC_HEAD\": {\r\n" +
                "                \"KZZD2\": \"\", \r\n" +
                "                \"KZZD1\": \"\", \r\n" +
                "                \"SQBSCQQLSH\": \"d09af2a5-4ee3-4933-9f64-6c970ca0ab1d\", \r\n" +
                "                \"YYSBZ\": \"0000000000\", \r\n" +
                "                \"TKSJ\": \"20181030\", \r\n" +
                "                \"YFP_DM\": \"5000153560\", \r\n" +
                "                \"YFP_HM\": \"29436572\", \r\n" +
                "                \"SQSM\": \"0000000100\", \r\n" +
                "                \"BMB_BBH\": \"1.0\", \r\n" +
                "                \"XXBLX\": \"0\"\r\n" +
                "            },\"HZFPSQBSC_DETAIL\": [\r\n" +
                "                {\r\n" +
                "                    \"XMXH\": \"1\", \r\n" +
                "                    \"FPHXZ\": \"1\", \r\n" +
                "                    \"SPBM\": \"312\", \r\n" +
                "                    \"ZXBM\": \"e434\", \r\n" +
                "                    \"YHZCBS\": \"1\", \r\n" +
                "                    \"LSLBS\": \"1\", \r\n" +
                "                    \"ZZSTSGL\": \"1\",\r\n" +
                "                    \"XMMC\": \"项目名称\", \r\n" +
                "                    \"GGXH\": \"规格型号\", \r\n" +
                "                    \"DW\": \"单位\", \r\n" +
                "                    \"XMSL\": \"23\", \r\n" +
                "                    \"XMDJ\": \"24\", \r\n" +
                "                    \"XMJE\": \"43\", \r\n" +
                "                    \"HSBZ\": \"0\", \r\n" +
                "                    \"SL\": \"0.5\", \r\n" +
                "                    \"SE\": \"34\"\r\n" +
                "                }\r\n" +
                "            ]\r\n" +
                "        }\r\n" +
                "    ]\r\n" +
                "}";
        HZFPSQBSCS_REQ req = new HZFPSQBSCS_REQ();
        JSONObject po = JSON.parseObject(ss);
        
        JSONObject jsonObject = po.getJSONObject("HZFPSQBSCS_BATCH");
        String jsonString = JSONObject.toJSONString(jsonObject);
        HZFPSQBSCS_BATCH parseObject = JSONObject.parseObject(jsonString, HZFPSQBSCS_BATCH.class);
        req.setHZFPSQBSCS_BATCH(parseObject);
        
        JSONArray jsonArray = po.getJSONArray("HZFPSQBSC");
        String jsonString1 = JSONObject.toJSONString(jsonArray);
        List<HZFPSQBSC> parseArray = JSONObject.parseArray(jsonString1, HZFPSQBSC.class);
        HZFPSQBSC[] hh = new HZFPSQBSC[parseArray.size()];
        HZFPSQBSC hhhh = new HZFPSQBSC();
        for (int i = 0; i < parseArray.size(); i++) {
            HZFPSQBSC_HEAD hzfpsqbsc_HEAD = parseArray.get(i).getHZFPSQBSC_HEAD();
            hhhh.setHZFPSQBSC_HEAD(hzfpsqbsc_HEAD);
            hh[i] = hhhh;
            if (ConfigureConstant.STRING_1.equals(parseObject.getSQLB())) {
                String jsonString2 = JSONObject.toJSONString(parseArray.get(i).getHZFPSQBSC_DETAIL());
                List<HZFPSQBSC_DETAIL> parseArray2 = JSONObject.parseArray(jsonString2, HZFPSQBSC_DETAIL.class);
                if (parseArray2 != null) {
                    HZFPSQBSC_DETAIL[] ddd = new HZFPSQBSC_DETAIL[parseArray2.size()];
                    for (int j = 0; j < parseArray2.size(); j++) {
                        ddd[i] = parseArray2.get(j);
                    }
                    hh[i].setHZFPSQBSC_DETAIL(ddd);
                }
                req.setHZFPSQBSCS(hh);
            }
        }
        System.out.println(req);
    }
    
    @Test
    public void Test() {
        String str = "{\"DDPCXX\":{\"CPYBS\":\"0\",\"DDQQPCH\":\"wz200417143302197\",\"FPLXDM\":\"0\",\"KPFS\":\"1\",\"KPZD\":\"-1\",\"KZZD\":\"\",\"NSRSBH\":\"15000120561127953X\"},\"DDZXX\":[{\"DDMXXX\":[{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"67.16314912\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"673.91\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"10.03392020\",\"XH\":\"1\",\"XMMC\":\"物业服务67.16\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"30.79698150\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"313.33\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"10.17395803\",\"XH\":\"2\",\"XMMC\":\"物业服务30.80\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"26.91901613\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"2609.75\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"96.94825921\",\"XH\":\"3\",\"XMMC\":\"物业服务26.92\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"81.32662919\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"3004.42\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"36.94267983\",\"XH\":\"4\",\"XMMC\":\"物业服务81.33\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"98.40276965\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型>号\",\"HSBZ\":\"1\",\"JE\":\"8985.54\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"91.31390275\",\"XH\":\"5\",\"XMMC\":\"物业服务98.40\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"87.39204020\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"3873.40\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"44.32213541\",\"XH\":\"6\",\"XMMC\":\"物业服务87.39\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"95.74007524\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"8641.87\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"90.26386757\",\"XH\":\"7\",\"XMMC\":\"物业服务95.74\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"24.71593818\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"2321.37\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"93.92194935\",\"XH\":\"8\",\"XMMC\":\"物业服务24.72\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"},{\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"DJ\":\"38.11026788\",\"DW\":\"份\",\"FPHXZ\":\"0\",\"GGXH\":\"规格型号\",\"HSBZ\":\"1\",\"JE\":\"222.73\",\"LSLBS\":\"\",\"SE\":\"0.00\",\"SL\":\"0.13\",\"SPBM\":\"1010101000000000000\",\"SPSL\":\"5.84434279\",\"XH\":\"9\",\"XMMC\":\"物业服务38.11\",\"YHZCBS\":\"0\",\"ZXBM\":\"\",\"ZZSTSGL\":\"\"}],\"DDTXX\":{\"BMBBBH\":\"33.0\",\"BYZD1\":\"\",\"BYZD2\":\"\",\"BYZD3\":\"\",\"BYZD4\":\"\",\"BYZD5\":\"\",\"BZ\":\"\",\"CHYY\":\"\",\"DDH\":\"0417143302_iUT\",\"DDQQLSH\":\"0417143302_iUT\",\"DDSJ\":\"\",\"FHR\":\"fhzz\",\"GMFBM\":\"\",\"GMFDH\":\"010-84567891\",\"GMFDZ\":\"wz购方地址\",\"GMFDZYX\":\"wuzhen0605@sina.com\",\"GMFLX\":\"01\",\"GMFMC\":\"北京棱聚影视文化传媒有限公司           \",\"GMFSBH\":\"911101086JKKQ12539\",\"GMFSF\":\"\",\"GMFSJH\":\"13123456789\",\"GMFYH\":\"wzxxbank\",\"GMFZH\":\"645877777888999\",\"HJJE\":\"30646.32\",\"HJSE\":\"0.00\",\"JSHJ\":\"30646.32\",\"KPLX\":\"0\",\"KPR\":\"wzxx\",\"NSRMC\":\"北京京东智新贸易有限公司\",\"NSRSBH\":\"15000120561127953X\",\"QDBZ\":\"0\",\"QDXMMC\":\"\",\"SKR\":\"skxx\",\"THDH\":\"\",\"TSCHBZ\":\"\",\"XHFDH\":\"010-8123478\",\"XHFDZ\":\"xx销方地址\",\"XHFMC\":\"北京京东智新贸易有限公司\",\"XHFSBH\":\"15000120561127953X\",\"XHFYH\":\"xx销方bank\",\"XHFZH\":\"6211478964777\",\"YFPDM\":\"\",\"YFPHM\":\"\",\"YWLX\":\"\"}}]}";
        
        DDPCXX_REQ ddpcxx_req = JsonUtils.getInstance().parseObject(str, DDPCXX_REQ.class);
        /**
         * 接口数据整体校验
         */
        Map<String, String> checkInvParam1 = validateInterfaceOrder.checkInterfaceParamV3(ddpcxx_req, "289efb7512e54146273b982456b03f42ea93", OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
        if (!ConfigureConstant.STRING_0000.equals(checkInvParam1.get(OrderManagementConstant.ERRORCODE))) {
            log.error("{}数据非空和长度校验未通过，未通过数据:{}", null, checkInvParam1);
            
        }
    }
}
