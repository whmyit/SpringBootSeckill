package com.dxhy.order.consumer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ：杨士勇
 * @ClassName ：OpenApiConfig
 * @Description ：外部api接口配置
 * @date ：2019年6月25日 上午10:17:55
 */
@Component
public class OpenApiConfig {

    /**
     * A9开票相关接口
     */
    /**
     * 已开发票作废
     */
    public static String ykfpzf;
    /**
     * 空白发票作废
     */
    public static String blankInvoiceZf;
    /**
     * 发票开具状态查询
     */
    public static String invoiceStatusQuery;
    /**
     * 发票开具结果获取
     */
    public static String invoicingResult;
    /**
     * 红字申请单下载接口
     */
    public static String redInvoiceDown;
    /**
     * 红字申请单上传接口
     */
    public static String redInvoiceUpload;

    /**
     * 获取pdf的接口
     */
    public static String getPdf;
    /**
     * 获取受理点下一张发票的接口
     */
    public static String queryNextInvoice;

    /**
     * 获取开票限额的配置
     */
    public static String queryNsrpzKpxe;
    /**
     * 获取受理点列表
     */
    public static String querySldList;
    /**
     * 获取库存信息
     */
    public static String querySldFpfs;
    /**
     * 根据分机号获取库存信息
     */
    public static String querykcxxByFjh;
    
    /**
     * 根据税号分机号获取发票种类代码
     */
    public static String getSearchFjhFpzlDm;
    
    /**
     * 根据机器编号
     */
    public static String queryJqbh;
    
    /**
     * 新税控获取分机号
     */
    public static String queryNsrXnsbxx;
    
    /**
     * 打印机列表查询
     */
    public static String queryDydxxcxList;
    /**
     * 发票打印接口
     */
    public static String printInvoice;
    /**
     * 发票打印接口状态获取
     */
    public static String getPrintInvoiceStatus;
    /**
     * 月度汇总
     */
    public static String ydtj;
    /**
     * 百旺盘阵月度统计详情
     */
    public static String getBbfxDetail;
    /**
     * 发票状态查询
     */
    public static String queryInvoiceStatus;


    public static String queryInvoiceStatusNewTax;


    public static String redInvoiceRevoke;


    /**
     * 百望 active-x
     */
    /**
     * 查询打印点列表
     */
    public static String queryDydxxcxListBw;
    /**
     * 查询开票点信息
     */
    public static String queryKpdXxBw;
    /**
     * 根据纳税人识别号查询税控盘
     */
    public static String querySpBw;
    /**
     * 根据纳税人识别号和开票点id查询服务器信息
     */
    public static String queryServerByKpdIdAndNsrsbh;
    /**
     * 调用税控获取打印机信息地址
     */
    public static String queryDyjInfo;

    /**
     * 方格接口
     */
    /**
     * 调用税控获取打印机列表地址
     */
    public static String queryFgDyjInfoList;

    /**
     * 调用税控获取打印机信息地址
     */
    public static String queryFgDyjInfo;

    /**
     * 调用税控同步税盘信息地址
     */
    public static String tbSpxxFg;

    /**
     * 调用税控生成pdf地址
     */
    public static String genPdfFg;

    /**
     * 调用申请注册码地址
     */
    public static String sqZcxxFg;

    /**
     * 调用获取pdf地址
     */
    public static String getPdfFg;

    /**
     * 根据纳税人和发票种类代码查询限额
     */
    public static String getXeFg;

    /**
     * 根据纳税人识别号查询税盘信息
     */
    public static String getSpxxFg;

    /**
     * 权限系统相关接口
     */

    /**
     * 获取用户信息
     */
    public static String queryUserInfo;

    /**
     * 税控设备同步
     */
    public static String pushTaxEquipment;

    /**
     * 获取用户信息
     */
    public static String queryDBUserInfo;

    /**
     * 获取销方信息
     */
    public static String queryOrgInfoByCode;

    /**
     * 获取大B销方信息
     */
    public static String queryDBOrgInfoByCode;

    /**
     * 获取辅助运营销方信息
     */
    public static String queryfzyyTaxpayerList;

    /**
     * 获取辅助运营从Eureka获取销方信息
     */
    public static String queryFzyyTaxpayerListByEureka;
    
    /**
     * 推送系统消息
     */
    public static String systemMessagePush;


    /**
     * 打印测试c48
     */
    public static String printTest_c48;
    /**
     * c48分页查询受理点
     */
    public static String querySldList_c48;

    /**
     * 对外二维码短码
     */
    public static String qrCodeShortUrl;

    /**
     * 我的发票公众号相关接口
     */
    /**
     * 静态码领票跳转地址
     */
    public static String qrCodeScanUrl;
    
    /**
     * 获取授权url的接口
     */
    public static String getAuthUrl;
    
    /**
     * 获取授权状态的接口
     */
    public static String getAuthStatus;
    
    /**
     * 通联支付大B相关接口
     */
    /**
     * 套餐余量数据查询
     */
    public static String mealAllowanceUrl;
    
    /**
     * 获取套餐余量接口产品ID
     */
    public static String systemProductId;

    /**
     * ofd转pngURL
     */
    public static String ofdToPngUrl;

    /**
     * ofd下载路径
     */
    public static String ofdUrl;

    public static String myFrontUrl;

    @Autowired
    public void initConfig(PropertiesUtils openApiInitBean) {

        /** A9相关接口地址 */
        /**
         * 已开发票作废,支持C48
         */
        ykfpzf = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/ykfpzf";
        /**
         * 空白发票作废,支持C48
         */
        blankInvoiceZf = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/blankInvoiceZf";
        /**
         * 发票开具状态查询,支持C48
         */
        invoiceStatusQuery = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/invoiceStatusQuery";

        /**
         * 获取pdf接口,支持C48
         */
        getPdf = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/getPdf";
        /**
         * 受理点列表查询,支持C48
         */
        querySldList = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/querySld";

        querySldFpfs = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/querySldKykc";
        /**
         * 税号开票限额接口,支持C48
         */
        queryNsrpzKpxe = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryNsrpzKpxe";

        /**
         * 获取下一张发票接口,支持C48
         */
        queryNextInvoice = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryNextInvoice";

        queryDydxxcxList = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryDyjByDyjmcAndDyjzt";
        /**
         * 发票打印接口,支持C48,入参和出参与A9不统一
         */
        printInvoice = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/printInvoice";

        /**
         * 发票打印状态查询接口,仅支持C48.  A9和其他设备没有
         */
        getPrintInvoiceStatus = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/getPrintInvoicesStatus";

        ydtj = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/ydtj";
        /**
         * 红字信息表下载,支持C48
         */
        redInvoiceDown = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/redInvoiceDown";
        /**
         * 红字信息表上传,支持C48
         */
        redInvoiceUpload = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/redInvoiceUpload";
    
        /**
         * 查询分机号和种类,支持C48,C48返回数据不全
         */
        /**
         * 只有百旺盘阵和新税控UKey使用
         */
        queryJqbh = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryJqbh";
        /**
         * 只有A9使用
         */
        getSearchFjhFpzlDm = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/getSearchFjhFpzlDm";
        /**
         * 只有新税控使用
         */
        queryNsrXnsbxx = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryNsrXnsbxx";
    
        /**
         * 库存余量查询,支持C48
         */
        querykcxxByFjh = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryKcxx";
        /**
         * 查询发票最终的开具状态,支持C48
         */
        queryInvoiceStatus = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/queryInvoiceStatus";

        /**
         * 新税控最终状态查询
         */
        queryInvoiceStatusNewTax = openApiInitBean.getInterfaceNewTaxBusinessUrl() + "/api/managerment/invoice/sk/2.0/queryInvoiceStatus";
        /**
         * 红字信息表撤回接口,仅支持C48,
         */
        redInvoiceRevoke = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/redInvoiceCancel";

        /**
         * 百旺盘阵专用月度汇总详情
         */
        getBbfxDetail = openApiInitBean.getInterfaceA9BusinessUrl() + "/invoice/business/v1.0/getBbfxDetail";
        /** 百望 active-x调用*/
        /**
         * 获取打印点信息
         */
        queryDydxxcxListBw = openApiInitBean.getInterfaceA9BusinessUrl() + "/sk/invoice/BWActiveX/web/v1.0/SkDyj/selectList";
        queryKpdXxBw = openApiInitBean.getInterfaceA9BusinessUrl() + "/sk/invoice/BWActiveX/web/v1.0/SkKpd/queryAll";
        querySpBw = openApiInitBean.getInterfaceA9BusinessUrl() + "/sk/invoice/BWActiveX/web/v1.0/SkSkp/selectSkpByNsrsbh";
        queryServerByKpdIdAndNsrsbh = openApiInitBean.getInterfaceA9BusinessUrl() + "/sk/invoice/BWActiveX/web/v1.0/SkServer/queryServerByKpdIdAndNsrsbh";
        queryDyjInfo = openApiInitBean.getInterfaceA9BusinessUrl() + "/sk/invoice/BWActiveX/web/v1.0/SkDyj/selectDyjInfo";

        /**
         * 方格
         */
        queryFgDyjInfoList = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/findSkDyjByShZl";
        queryFgDyjInfo = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/getDyjxx";
        tbSpxxFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/tbSpxx";
        genPdfFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/genPdf";
        sqZcxxFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/sqZcxx";
        getPdfFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/getPdf";
        getXeFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/querySpZlXeByNsrsbh";
        getSpxxFg = openApiInitBean.getInterfaceFgBusinessUrl() + "/invoice/business/v1.0/querySpByNsrsbh";
        //todo 追加方格配置

        /**
         * 大B相关接口调用
         */
        queryUserInfo = openApiInitBean.getSsoUrl() + "/uadmin/user/queryUserInfoNew";
        pushTaxEquipment = openApiInitBean.getSsoUrl() + "/uadmin/dept/taxControlEquipment";
        queryDBUserInfo = openApiInitBean.getSsoUrl() + "/sso/getUserInfo";
        queryOrgInfoByCode = openApiInitBean.getSsoUrl() + "/uadmin/dept/queryDeptByNameAndCode";
        queryDBOrgInfoByCode = openApiInitBean.getDBUserInfoUrl() + "/api/dept/queryOrgInfoByCode";
        queryfzyyTaxpayerList = openApiInitBean.getFzyyUrl() + "/customer/customer/company-customer/getCustomerPage?productId=%s&sourceType=9&page=%s&limit=%s&location=&companyName=";
    
        queryFzyyTaxpayerListByEureka = openApiInitBean.getFzyyEurekaUrl() + "/customer/company-customer/getCustomerPage?productId=%s&sourceType=9&page=%s&limit=%s&location=&companyName=";
        /** 套餐余量查询 */
        mealAllowanceUrl = openApiInitBean.getSsoUrl() + "/api/allinpay/mealAllowance";
        /**
         * 推送大B系统消息
         */
        systemMessagePush = openApiInitBean.getSsoUrl() + "/notify/addNotify";


        /** c48打印测试接口 */
        printTest_c48 = openApiInitBean.getPrintDomain() + "/dyd/printTest";

        qrCodeShortUrl = openApiInitBean.getLocalDomain() + "/api/v3/%s";

        /** 公众号相关接口调用 */
        qrCodeScanUrl = openApiInitBean.getFrontUrl() + "/html/wxscaninvoice/transferPage.html?tqm=%s&nsrsbh=%s&type=%s";
//        synSellerInfoUrl = openApiInitBean.getMyinvoiceDomain() + "/myinvoice-data/enterprise/insertSelective";
        getAuthUrl = openApiInitBean.getMyinvoiceDomain() + "/wxservice/api/getAuthUrl";
        getAuthStatus = openApiInitBean.getMyinvoiceDomain() + "/wxservice/api/getAuthData";


        /**
         * 套餐产品查询Id
         */
        systemProductId = openApiInitBean.getSystemProductId();

        ofdUrl = openApiInitBean.getOfdUrl();
        myFrontUrl = openApiInitBean.getFrontUrl() + "/html/wxscaninvoice/invoicePreview.html";
        ofdToPngUrl = openApiInitBean.getOfdToPngUrl() + "/taxControl/invoice/business/v1.0/convertPng";

    }
}
