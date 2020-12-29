package com.dxhy.order.constant;

/**
 * 常量值
 *
 * @author jerome
 * @date 2016/5/26
 */
public class ConfigurerInfo {
    /**
     * 常量定值
     */


    public static final int PASSWORD_SIZE = 24;

    /**
     * 常量变值配置
     */

    public static final String ENCRYPTCODE = "encryptCode";
    public static final String ZIPCODE = "zipCode";
    public static final String CONTENT = "content";

    public static final String TIMESTAMP = "Timestamp";

    public static final String NONCE = "Nonce";

    public static final String SECRETID = "SecretId";
    public static final String SIGNATURE = "Signature";

    public static final String RESPONSESTATUS = "responseStatus";

    public static final String RESPONSEDATA = "responseData";

    public static final String SUCCSSCODE = "0000";
    
    /**
     * 接口相关
     */

    /**
     * 对外接口版本号--v1
     */
    public static final String INTERFACE_VERSION_V1 = "v1";
    
    /**
     * 对外接口版本号--v2
     */
    public static final String INTERFACE_VERSION_V2 = "v2";
    
    /**
     * 最新的对外接口文档
     * 对外接口版本号--v3
     */
    public static final String INTERFACE_VERSION_V3 = "v3";
    
    /**
     * 统一的对外接口文档
     * 对外接口版本号--v4
     */
    public static final String INTERFACE_VERSION_V4 = "v4";
    
    
    /**
     * 自动开票接口
     */
    public static final String ALLOCATEINVOICES = "AllocateInvoices";
    /**
     * 请求执行状态查询接口
     */
    public static final String GETALLOCATEINVOICESSTATUS = "GetAllocateInvoicesStatus";
    /**
     * 开具发票结果获取接口
     */
    public static final String GETALLOCATEDINVOICES = "GetAllocatedInvoices";
    /**
     * 发票作废接口
     */
    public static final String DEPRECATEINVOICES = "DeprecateInvoices";
    /**
     * 获取电子发票接口
     */
    public static final String GETINVOICEPDFFILES = "GetInvoicePdfFiles";
    /**
     * 发票打印状态接口
     */
    public static final String PRINTINVOICES = "PrintInvoices";
    /**
     * 发票自定义打印接口
     */
    public static final String GETPRINTINVOICESSTATUS = "GetPrintInvoicesStatus";
    /**
     * 企业数据自动导入接口
     */
    public static final String IMPORTORDERS = "ImportOrders";
    /**
     * 开票点上下票列表管理接口
     */
    public static final String QUERYINVOICEROLLPLOLIST = "Queryinvoicerollplolist";
    /**
     * 开票点上票接口接口
     */
    public static final String ACCESSPOINTUPINVOICE = "AccessPointUpInvoice";
    /**
     * 开票点下票接口接口
     */
    public static final String ACCESSPOINTDOWNINVOICE = "AccessPointDownInvoice";
    /**
     * 开票点列表接口接口
     */
    public static final String QUERYSLD = "QuerySld";
    /**
     * 根据订单号获取订单数据以及发票数据接口
     */
    public static final String GETORDERINFOANDINVOICEINFO = "GetOrderInfoAndInvoiceInfo";
    /**
     * 红字发票申请单上传接口
     */
    public static final String ALLOCATEREDINVOICEAPPLICATION = "AllocateRedInvoiceApplication";
    /**
     * 红字发票申请单审核结果下载接口
     */
    public static final String DOWNLOADREDINVOICEAPPLICATIONRESULT = "DownloadRedInvoiceApplicationResult";
    /**
     * 根据提取码获取订单
     */
    public static final String GETORDERINFOBYTQM = "GetOrderInfoByTqm";
    /**
     * 成品油库存局端可下载库存查询接口
     */
    public static final String QUERYCPYJDKC = "QueryCpyJdKc";
    /**
     * 成品油已下载库存查询接口
     */
    public static final String QUERYCPYYXZKC = "QueryCpyYxzKc";
    /**
     * 成品油库存下载接口
     */
    public static final String DOWNLOADCPYKC = "DownloadCpyKc";
    /**
     * 成品油库存退回接口
     */
    public static final String BACKCPYKC = "BackCpyKc";
    /**
     * 成品油库存同步接口
     */
    public static final String SYNCCPYKC = "SyncCpyKc";
    /**
     * 获取动态码的接口
     */
    public static final String  GENERATDYNAMICCODE  = "GenerateDynamicCode";

    /**
     * 历史数据导入接口
     */
    public static final String IMPORTINVOICEINFO = "importInvoiceInfo";

    /**
     * 商品信息查询接口
     */
    public static final String QUERYCOMMODITYINFO = "QueryCommodityInfo";

    /**
     * 商品信息同步接口
     */
    public static final String SYNCCOMMODITYINFO = "SyncCommodityInfo";
    
    /**
     * 购买方信息查询接口
     */
    public static final String QUERYBUYERINFO = "queryBuyerInfo";
    
    /**
     * 购买方信息同步接口
     */
    public static final String SYNCBUYERINFO = "SyncBuyerInfo";
    
    /**
     * 税控设备信息同步接口
     */
    public static final String SYNCTAXEQUIPMENTINFO = "SyncTaxEquipmentInfo";
    
    /**
     * 发票余量同步接口
     */
    public static final String QUERYINVOICESTORE = "QueryInvoiceStore";
    /**
     * 订单删除
     */
    public static final String ORDERDELETE = "orderDelete";
    
    
    /**
     * 开票
     */
    /**
     * 待开发票数据接口
     */
    public static final String FANG_GE_GETINVOICES = "getInvoices";
    /**
     * 接收待开订单数据状态接口
     */
    public static final String FANG_GE_GETINVOICESTATUS = "getInvoiceStatus";
    /**
     * 接收开票完成订单数据接口
     */
    public static final String FANG_GE_UPDATEINVOICES = "updateInvoices";
    
    
    /**
     * 红字申请单上传
     */
    /**
     * 获取红字申请单待上传数据接口
     */
    public static final String FANG_GE_GETUPLOADREDINVOICE = "getUploadRedInvoice";
    /**
     * 接收红字申请单待上传数据状态接口
     */
    public static final String FANG_GE_GETUPLOADREDINVOICESTATUS = "getUploadRedInvoiceStatus";
    /**
     * 接收红字申请单上传数据接口
     */
    public static final String FANG_GE_UPDATEUPLOADREDINVOICE = "updateUploadRedInvoice";
    
    /**
     * 红字申请单下载
     */
    /**
     * 获取红字申请单待下载数据接口
     */
    public static final String FANG_GE_GETDOWNLOADREDINVOICE = "getDownloadRedInvoice";
    /**
     * 接收红字申请单待下载数据状态接口
     */
    public static final String FANG_GE_GETDOWNLOADREDINVOICESTATUS = "getDownloadRedInvoiceStatus";
    /**
     * 接收红字申请单下载数据接口
     */
    public static final String FANG_GE_UPDATEDOWNLOADREDINVOICE = "updateDownloadRedInvoice";
    
    /**
     * 发票作废
     */
    /**
     * 获取待作废数据
     */
    public static final String FANG_GE_GETDEPRECATEINVOICES = "getDeprecateInvoices";
    /**
     * 接收待作废发票状态
     */
    public static final String FANG_GE_GETDEPRECATEINVOICESSTATUS = "getDeprecateInvoicesStatus";
    /**
     * 接收作废完成订单数据接口
     */
    public static final String FANG_GE_UPDATEDEPRECATEINVOICES = "updateDeprecateInvoices";
    
    /**
     * 发票打印
     */
    /**
     * 获取待打印的接口数据
     */
    public static final String FANG_GE_GETPRINTINVOICES = "getPrintInvoices";
    /**
     * 接收待打印订单状态接口
     */
    public static final String FANG_GE_GETPRINTINVOICESSTATUS = "getPrintInvoicesStatus";
    /**
     * 接收打印结果接口
     */
    public static final String FANG_GE_UPDATEPRINTINVOICES = "updatePrintInvoices";
    /**
     * 税盘数据同步
     */
    public static final String FANG_GE_UPDATETAXDISKINFO = "updateTaxDiskInfo";
    /**
     * 税盘注册
     */
    public static final String FANG_GE_REGISTTAXDISK = "registTaxDisk";
    
    /**
     * 启用zip压缩
     */
    public static final String ZIPCODE_1 = "1";
    
    /**
     * 启用base64加密
     */
    public static final String ENCRYPTCODE_0 = "0";

    /**
     * 启用3DES加密
     */
    public static final String ENCRYPTCODE_1 = "1";

    /**
     * 方格接口 签名参数 固定
     */
    public static final String FG_QMCS="0000004282000000";
    

    
    

}

