package com.dxhy.order.consumer;

import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.consumer.handle.FangGeInvoiceResendMsgTask;
import com.dxhy.order.consumer.modules.fiscal.controller.FiscalStateController;
import com.dxhy.order.consumer.modules.invoice.controller.InvoiceDetailsController;
import com.dxhy.order.consumer.modules.invoice.controller.InvoiceInvalidController;
import com.dxhy.order.consumer.modules.invoice.controller.PlainInvoiceController;
import com.dxhy.order.consumer.openapi.api.FangGeInvoicesController;
import com.dxhy.order.consumer.openapi.api.InvoiceRestApi;
import com.dxhy.order.model.R;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 方格接口对接-测试
 * @Author:xueanna
 * @Date:2019/5/29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Slf4j
public class FangGeInvoiceTest {
    
    @Autowired
    private FangGeInvoicesController fangGeInvoicesController;
    @Reference
    private ICommonDisposeService commonDisposeService;
    @Autowired
    private FiscalStateController fiscalStateController;
    @Autowired
    private PlainInvoiceController plainInvoiceController;
    @Autowired
    private InvoiceInvalidController invoiceInvalidController;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    private InvoiceDetailsController invoiceDetailsController;
    @Autowired
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    @Autowired
    private FangGeInvoiceResendMsgTask fangGeInvoiceResendMsgTask;
    
    @Autowired
    private InvoiceRestApi invoiceRestAPI;
    
    @Before
    public void setup() {
    
    }
    
    @Test
    public void name() {
    }
    
    /**
     * 发票填开选择机器编号
     */
    @Test
    public void queryJqbh() {
        R number = invoiceInvalidController.getCodeAndNumber(req, "499000138494", "2", "[\"50001000666654321\"]");
        System.out.printf(JsonUtils.getInstance().toJsonString(number));
    }
    
    /**
     * 获取待开票接口测试
     */
    @Test
    public void invoiceTest() throws Exception {
        log.info("----------方格获取开票数据接口开始测试-----------------------");
        
        /*String str = "{\"NSRSBH\":\"50001000666654321\",\"DDQQLSH\":\"201908011345161156803060694122496\",\"KSSJ\":\"\",\"JSSJ\":\"\"}";*/
        String str = "{\"NSRSBH\":\"11010120910703001\",\"DDQQLSH\":\"201908091502171159721544692400128\",\"KSSJ\":\"\",\"JSSJ\":\"\"}";
        String s = formatParam(str);
     /*Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 待开票修改状态
     */
    @Test
    public void getInvoiceStatus() throws Exception {
        log.info("----------方格待开票修改状态测试-----------------------");
        String str = "[{\"NSRSBH\":\"150001205110278555\",\"DDQQLSH\":\"201811011621021057910372582948864\",\"SJZT\":\"0\"}]";
        String s = formatParam(str);
      /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETINVOICESTATUS, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 开票完成
     */
    @Test
    public void updateInvoices() throws Exception {
        log.info("------开票完成---------------------------");
       /* String str = "[\n" +
                "  {\n" +
                "    \"NSRSBH\": \"150001205110278555\",\n" +
                "    \"DDQQLSH\": \"201811011621021057910372582948864\",\n" +
                "    \"JQBH\": \"499000101350\",\n" +
                "    \"DDH\": \"3432\",\n" +
                "    \"JYM\": \"test\",\n" +
                "    \"FWM\": \"test\",\n" +
                "    \"EWM\": \"test\",\n" +
                "    \"FP_DM\": \"test\",\n" +
                "    \"FP_HM\": \"test\",\n" +
                "    \"KPRQ\": \"20190202121212\",\n" +
                "    \"FPZLDM\": \"2\",\n" +
                "    \"HJBHSJE\": \"22\",\n" +
                "    \"KPHJSE\": \"22\",\n" +
                "    \"STATUSCODE\": \"2\",\n" +
                "    \"STATUSMSG\": \"2\"\n" +
                "  }\n" +
                "]";*/
        String str = "[\n" +
                "  {\n" +
                "    \"NSRSBH\": \"15000120561127953X\",\n" +
                "    \"DDQQLSH\": \"201908082012071159437127767949312\",\n" +
                "    \"JQBH\": \"661616315914\",\n" +
                "    \"DDH\": \"91203366049694341192\",\n" +
                "    \"JYM\": \"\",\n" +
                "    \"FWM\": \"\",\n" +
                "    \"EWM\": \"\",\n" +
                "    \"FP_DM\": \"\",\n" +
                "    \"FP_HM\": \"\",\n" +
                "    \"KPRQ\": \"20190803181201\",\n" +
                "    \"FPZLDM\": \"51\",\n" +
                "    \"HJBHSJE\": \"800.00\",\n" +
                "    \"KPHJSE\": \"\",\n" +
                "    \"STATUSCODE\": \"0\",\n" +
                "    \"STATUSMSG\": \"处理成功\"\n" +
                "  }\n" +
                "]";
       /* String s = formatParam(str);
        Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATEINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    @Test
    public void getpdf() {
        R r = invoiceDetailsController.queryInvoiceDateilsPdf("111000020026", "20000022", "51", "123123123");
        System.out.println(r);
    }
    
    /**
     * 红票申请单待上传
     */
    @Test
    public void getUploadRedInvoice() throws Exception {
        log.info("------红票申请单待上传------");
        String str = "{\n" +
                "  \"NSRSBH\": \"422010201709012004\",\n" +
                "  \"SQBSCQQPCH\": \"661616316992190809093434\"\n" +
                "}";
        String s = formatParam(str);
         /*Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETUPLOADREDINVOICE, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "eyJOU1JTQkgiOiI0MjIwMTAyMDE3MDkwMTIwMDQiLCJTUUJTQ1FRTFNIIjoiNjYxNjE2MzE2OTkyMTkwODA5MDkzNDM0In0=",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单下载数据状态
     */
    @Test
    public void getUploadRedInvoiceStatus() throws Exception {
        log.info("------红票申请单更新数据状态------");
        String str = "{\n" +
                "  \"NSRSBH\": \"150301199811285326\",\n" +
                "  \"SQBSCQQPCH\": \"661616316327190117175929\",\n" +
                "  \"SQDQQSJ\": [\n" +
                "    {\n" +
                "      \"SQBSCQQLSH\": \"661616316327190117175929\",\n" +
                "      \"SJZT\": \"0\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String s = formatParam(str);
      /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETUPLOADREDINVOICESTATUS, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单上传完成
     */
    @Test
    public void updateUploadRedInvoice() throws Exception {
        log.info("------红票申请单上传完成------");
        String str = "{\n" +
                "    \"SQBSCQQPCH\": \"661616316327190117175929\", \n" +
                "    \"STATUS_CODE\": \"状态代码\", \n" +
                "    \"STATUS_MESSAGE\": \"状态信息\", \n" +
                "    \"RED_INVOICE_FORM_UPLOAD_RESPONSES\": [\n" +
                "        {\n" +
                "            \"SQBSCQQLSH\": \"661616316327190117175929\", \n" +
                "            \"SQDH\": \"ss\", \n" +
                "            \"STATUS_CODE\": \"TZD0000\", \n" +
                "            \"STATUS_MESSAGE\": \"审核通过\", \n" +
                "            \"XXBBH\": \"1403011904000362\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        String s = formatParam(str);
     /*   Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATEUPLOADREDINVOICE, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单待上传
     */
    @Test
    public void getDownloadRedInvoice() throws Exception {
        log.info("------红票申请单待下载------");
        String str = "{\n" +
                "  \"NSRSBH\": \"150301199811285326\",\n" +
                "  \"SQBXZQQPCH\": \"661616316327190117175929\"\n" +
                "}";
        String s = formatParam(str);
       /* Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETDOWNLOADREDINVOICE, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单下载数据状态
     */
    @Test
    public void getDownloadRedInvoiceStatus() throws Exception {
        log.info("------红票申请单下载更新数据状态------");
        String str = "{\n" +
                "  \"NSRSBH\": \"150301199811285326\",\n" +
                "  \"SQBXZQQPCH\": \"661616316327190117175929\",\n" +
                "  \"SJZT\":\"0\"\n" +
                "}";
        String s = formatParam(str);
       /* Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETDOWNLOADREDINVOICESTATUS, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单下载完成
     */
    @Test
    public void updateDownloadRedInvoice() throws Exception {
        log.info("------红票申请单下载完成------");
        String str = "{\n" +
                "  \"SQBXZQQPCH\": \"申请表下载请求批次号\",\n" +
                "  \"STATUS_CODE\": \"状态代码\",\n" +
                "  \"STATUS_MESSAGE\": \"状态信息\",\n" +
                "  \"SUCCESS_COUNT\": \"成功获取的个数\",\n" +
                "  \"RED_INVOICE_FORM_DOWNLOADS\": [\n" +
                "    {\n" +
                "      \"RED_INVOICE_FORM_DOWN_HEAD\": {\n" +
                "        \"SQDH\": \"申请单号\",\n" +
                "        \"XXBBH\": \"信息表编号\",\n" +
                "        \"STATUS_CODE\": \"状态代码\",\n" +
                "        \"STATUS_MESSAGE\": \"状态信息\",\n" +
                "        \"YFP_DM\": \"原蓝字发票代码\",\n" +
                "        \"YFP_HM\": \"原蓝字发票号码\",\n" +
                "        \"FPZLDM\": \"发票种类代码\",\n" +
                "        \"DSLBZ\": \"多税率标志:0一票一税率，1一票多税率\",\n" +
                "        \"TKSJ\": \"填开时间\",\n" +
                "        \"XSF_NSRSBH\": \"销售方纳税人识别号\",\n" +
                "        \"XSF_MC\": \"销售方纳税人名称\",\n" +
                "        \"GMF_NSRSBH\": \"购买方纳税人识别号\",\n" +
                "        \"GMF_MC\": \"购买方纳税人名称\",\n" +
                "        \"HJJE\": \"合计金额(带负号,不含税)\",\n" +
                "        \"HJSE\": \"合计税额(带负号)\",\n" +
                "        \"SQSM\": \"十位数字表示的申请说明\",\n" +
                "        \"BMB_BBH\": \"商品编码版本号\",\n" +
                "        \"YYSBZ\": \"营业税标志\"\n" +
                "      },\n" +
                "      \"ORDER_INVOICE_ITEMS\": [\n" +
                "        {\n" +
                "          \"XMXH\": \"项目序号\",\n" +
                "          \"FPHXZ\": \"发票行性质\",\n" +
                "          \"SPBM\": \"商品编码\",\n" +
                "          \"ZXBM\": \"自行编码\",\n" +
                "          \"YHZCBS\": \"优惠政策标识\",\n" +
                "          \"LSLBS\": \"零税率标识\",\n" +
                "          \"ZZSTSGL\": \"增值税特殊管理\",\n" +
                "          \"XMMC\": \"项目名称\",\n" +
                "          \"GGXH\": \"规格型号\",\n" +
                "          \"DW\": \"单位\",\n" +
                "          \"XMSL\": \"项目数量\",\n" +
                "          \"XMDJ\": \"项目单价\",\n" +
                "          \"XMJE\": \"项目金额\",\n" +
                "          \"HSBZ\": \"含税标志,固定为0不含税\",\n" +
                "          \"SL\": \"税率\",\n" +
                "          \"SE\": \"税额\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String s = formatParam(str);
       /* Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATEDOWNLOADREDINVOICE, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 红票申请单待作废
     */
    @Test
    public void getDeprecateInvoices() throws Exception {
        log.info("------待作废------");
        String str = "{\n" +
                "  \"NSRSBH\": \"911101082018050516\",\n" +
                "  \"ZFPCH\": \"3\"\n" +
                "}";
        String s = formatParam(str);
      /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETDEPRECATEINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 待作废状态
     */
    @Test
    public void getDeprecateInvoicesStatus() throws Exception {
        log.info("------待作废------");
        String str = "{\n" +
                "  \"NSRSBH\": \"911101082018050516\",\n" +
                "  \"ZFPCH\": \"3\",\n" +
                "  \"SJZT\":\"0\"\n" +
                "}";
        String s = formatParam(str);
       /* Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETDEPRECATEINVOICESSTATUS, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 作废完成
     */
    @Test
    public void updateDeprecateInvoices() throws Exception {
        log.info("------作废完成------");
        String str = "{\n" +
                "  \"ZFPCH\": \"ae327c72-c1c7-4ed9-a50e-387052caab24\",\n" +
                "  \"NSRSBH\": \"50001000666654321\",\n" +
                "  \"STATUS_CODE\": \"2\",\n" +
                "  \"STATUS_MESSAGE\": \"\",\n" +
                "  \"INVALID_INVOICE_INFOS\": [\n" +
                "    {\n" +
                "      \"FP_DM\": \"010000000001\",\n" +
                "      \"FP_HM\": \"00005031\",\n" +
                "      \"ZFZT\": \"1\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
       /* String s = formatParam(str);
        Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATEDEPRECATEINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 待打印
     */
    @Test
    public void getPrintInvoices() throws Exception {
        log.info("------待打印------");
        String str = "{\n" +
                "\"NSRSBH\": \"911101082018050516\",\n" +
                "\"DYPCH\": \"7979677372331622420180910161249\"\n" +
                "}";
        String s = formatParam(str);
       /* Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETPRINTINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 待打印状态
     */
    @Test
    public void getPrintInvoicesStatus() throws Exception {
        log.info("------待打印状态------");
        String str = "{\n" +
                "  \"NSRSBH\": \"911101082018050516\",\n" +
                "  \"DYPCH\": \"7979677372331622420180910161249\",\n" +
                "  \"SJZT\": \"0\"\n" +
                "}";
        String s = formatParam(str);
       /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_GETPRINTINVOICESSTATUS, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "0", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 打印完成
     */
    @Test
    public void updatePrintInvoices() throws Exception {
        log.info("------打印完成------");
        String str = "{\n" +
                "  \"NSRSBH\": \"11010120910703001\",\n" +
                "  \"DYPCH\": \"76a990e133eb477d898fadb56216e4be\",\n" +
                "  \"DYJG\": \"0\"\n" +
                "}";
      /*  String s = formatParam(str);
        Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATEPRINTINVOICES, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "0", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 注册税盘接口
     */
    @Test
    public void registTaxDisk() throws Exception {
        log.info("----------方格注册税盘开始测试-----------------------");
//        String str = "{\"NSRSBH\":\"50001000666654321\",\"NSRMC\":\"北京白云有限公司\",\"ZCLX\":\"1\",\"JQBH\":\"499000138494\"}";
        String str = "{\"NSRSBH\":\"6666659FRN9PGU5\",\"NSRMC\":\"北京白云有限公司\",\"ZCLX\":\"0\",\"JQBH\":\"661616317848\"}";
        String s = this.formatParam(str);
     /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_REGISTTAXDISK, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    /**
     * 税盘信息同步
     */
    @Test
    public void updateTaxDiskInfo() throws Exception {
        log.info("----------方格注册税盘开始测试-----------------------");
        String str = "{\n" +
                "  \"NSRSBH\": \"\",\n" +
                "  \"NSRMC\": \"测试31\",\n" +
                "  \"CPYQYLX\": \"0\",\n" +
                "  \"JQBM\": \"\",\n" +
                "  \"FPZLDM\": \"41\",\n" +
                "  \"LXKPSJ\": \"24h\",\n" +
                "  \"JSPSZ\": \"20190705112330\",\n" +
                "  \"JSPLX\": \"0\",\n" +
                "  \"JSPXX\": [\n" +
                "    {\n" +
                "      \"QYDM\": \"\",\n" +
                "      \"FJH\": \"\",\n" +
                "      \"HXJSPZT\": \"\",\n" +
                "      \"SFJYZS\": \"\",\n" +
                "      \"SCBSRQ\": \"\",\n" +
                "      \"CSQSRQ\": \"\",\n" +
                "      \"SFDCSQ\": \"\",\n" +
                "      \"SFDSSQ\": \"\",\n" +
                "      \"KPXE\": \"\",\n" +
                "      \"LXKPXE\": \"\",\n" +
                "      \"QYSJ\": \"2018-09-09\",\n" +
                "      \"KPJZSJ\": \"2018-09-09\",\n" +
                "      \"BWJSPZT\": \"0\",\n" +
                "      \"SJBSQSRQ\": \"2018-09-09\",\n" +
                "      \"SJBSZZRQ\": \"2019-09-09\",\n" +
                "      \"DZKPXE\": \"88453\",\n" +
                "      \"ZSLJXE\": \"8854\",\n" +
                "      \"FSLJXE\": \"8834\",\n" +
                "      \"FSFPTS\": \"8832\",\n" +
                "      \"ZXBSRQ\": \"2019-06-29\",\n" +
                "      \"SYRL\": \"34\",\n" +
                "      \"SCJZRQ\": \"2019-09-09\",\n" +
                "      \"LXKPZS\": \"8823\",\n" +
                "      \"LXZSLJJE\": \"8823\",\n" +
                "      \"LXFSLJJE\": \"882343\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
      /*  String str = "{\n" +
                "  \"NSRSBH\": \"11010120181016031\",\n" +
                "  \"NSRMC\": \"李四\",\n" +
                "  \"CPYQYLX\": \"1\",\n" +
                "  \"JQBM\": \" 2434987687\",\n" +
                "  \"FPZLDM\": \"41\",\n" +
                "  \"LXKPSJ\": \"72h\",\n" +
                "  \"JSPSZ\": \"20190203121230\",\n" +
                "  \"JSPLX\": \"0\",\n" +
                "  \"JSPXX\": [\n" +
                "    {\n" +
                "      \"QYDM\": \"120000\",\n" +
                "      \"FJH\": \"23134\",\n" +
                "      \"HXJSPZT\": \"1\",\n" +
                "      \"SFJYZS\": \"1\",\n" +
                "      \"SCBSRQ\": \"2016-09-09\",\n" +
                "      \"CSQSRQ\": \"2017-09-09\",\n" +
                "      \"SFDCSQ\": \"1\",\n" +
                "      \"SFDSSQ\": \"1\",\n" +
                "      \"KPXE\": \"9999\",\n" +
                "      \"LXKPXE\": \"9999\",\n" +
                "      \"QYSJ\": \"\",\n" +
                "      \"KPJZSJ\": \"\",\n" +
                "      \"BWJSPZT\": \"\",\n" +
                "      \"SJBSQSRQ\": \"\",\n" +
                "      \"SJBSZZRQ\": \"\",\n" +
                "      \"DZKPXE\": \"\",\n" +
                "      \"ZSLJXE\": \"\",\n" +
                "      \"FSLJXE\": \"\",\n" +
                "      \"FSFPTS\": \"\",\n" +
                "      \"ZXBSRQ\": \"\",\n" +
                "      \"SYRL\": \"\",\n" +
                "      \"SCJZRQ\": \"\",\n" +
                "      \"LXKPZS\": \"\",\n" +
                "      \"LXZSLJJE\": \"\",\n" +
                "      \"LXFSLJJE\": \"\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";*/
        String s = formatParam(str);
      /*  Result result = fangGeInvoicesController.invoice("v2", ConfigurerInfo.FANG_GE_UPDATETAXDISKINFO, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(result));*/
    }
    
    
    public String formatParam(String str) {
        CommonRequestParam param = new CommonRequestParam();
        param.setContent(str);
        param.setEncryptCode(ConfigurerInfo.ENCRYPTCODE_0);
        param.setZipCode(ConfigurerInfo.ZIPCODE_1);
        String s = commonDisposeService.commonEncrypt(param);
        return s;
    }
    
    @Test
    public void test() throws Exception {
        String str = "{\n" +
                "  \"NSRSBH\": \"11010120181016031\",\n" +
                "  \"NSRMC\": \"测试31\",\n" +
                "  \"ZCLX\": \"1\",\n" +
                "  \"JQBM\": \"499000101350\"\n" +
                "}";
        String s = formatParam(str);
        System.out.println(s);
    }
    
    @Test
    public void test1() {
        /*String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis("50001000666654321", "499000138494");
        RegistCode registCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistCode.class);
        *//**
         * 存放开票信息到redis队列
         *//*
        PushPayload pushPayload = new PushPayload();
        pushPayload.setINTERFACETYPE(ConfigureConstant.STRING_1);//接口发票开具
        pushPayload.setNSRSBH(registCode.getNsrsbh());
        pushPayload.setJQBH(registCode.getJqbh());
        pushPayload.setZCM(registCode.getRegistCode());
        pushPayload.setDDQQLSH("0505105543_fP6");
        apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
        apiFangGeInterfaceService.saveMqttToRedis(pushPayload);
        apiFangGeInterfaceService.saveMqttToRedis(pushPayload);*/
        
        fangGeInvoiceResendMsgTask.execute("5");
        fangGeInvoiceResendMsgTask.execute("5");
    }
    
    @Test
    public void printerConfiguration() {
        String str = "{\"OPERATEFLAG\":\"0\",\"NSRSBH\":\"test50001000666654321\",\"DYJMC\":\"Microsoft Print to PDF\",\"SBJ\":\"0\",\"ZBJ\":\"0\",\"CJR\":\"xan\",\"BJR\":\"xan\",\"NSRMC\":\"50001000666654321\"}";
        String s = this.formatParam(str);
      /*  String v3 = invoiceRestAPI.orderApiV3("v3", ConfigurerInfo.PRINTERCONFIG, "2342", "324",
                "289efb7512e54146273b982456b03f42ea93", "7n0qwmm3F9iltQRC6eVxwS9wuzk",
                "0", "1", s);

        log.info("执行结果测试成功{}", JsonUtils.getInstance().toJsonString(v3));*/
    }
}
