package com.dxhy.order.model.fg;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 生成发票队列Bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:14
 */
@Getter
@Setter
public final class InvoiceQueueEntity implements Serializable {
    
    
    /**
     * 发票ID
     */
    private Long invoiceId;
    
    /**
     * 发票请求批次号
     */
    private String invoicePch;
    /**
     * 受理点ID
     */
    private String sldId;
    /**
     * 发票类型:1纸质发票，2电子发票
     */
    private String invoiceType;
    /**
     * 扩展字段
     */
    private String kzzd;
    
    /**
     * 税纳人识别号(NSRSBH)
     */
    private String taxpayerIdentifyNo;
    
    /**
     * 纳税人所在税务机关代码(SZ_SWJG_DM)
     */
    private String taxAuthorityCode;
    /**
     * 税务机构代码(swjg_dm)
     */
    private String taxOfficeRegCode;
    /**
     * PDF文件路径
     */
    private String pdfFilePath;
    /**
     * 电商平台编码(DSPTBM)
     */
    private String eshopCode;
    
    /**
     * 开票合计金额(KPHJJE)
     */
    private Double billingAmount;
    
    /**
     * 开票类型 (KPLX,0正票 1红票)
     */
    private Long billingType;
    
    /**
     * 签章ID(QZID)
     */
    private String signatureId;
    
    /**
     * 订单号码(DDH)
     */
    private String orderNo;
    
    /**
     * 发票请求唯一流水号(FPQQLSH)
     */
    private String invoiceRequestSerialNo;
    
    /**
     * 原发票代码(YFP_DM)
     */
    private String oldInvoiceCode;
    
    /**
     * 原发票号码(YFP_HM)
     */
    private String oldInvoiceNo;
    
    /**
     * 备注(BZ)
     */
    private String memo;
    
    /**
     * 购货方手机(GHF_SJ)
     */
    private String buyerMobile;
    
    /**
     * 购货方固定电话(GHF_GDDH)
     */
    private String buyerFixedPhone;
    
    /**
     * 购货方邮箱(GHF_EMAIL)
     */
    private String buyerEmail;
    
    /**
     * 购货方名称(GHFMC)
     */
    private String buyerName;
    
    /**
     * 购货方识别号(GHF_NSRSBH)
     */
    private String buyerTaxpayerIdentifyNo;
    
    /**
     * 购货方地址(GHF_DZ)
     */
    private String buyerAddress;
    /**
     * 购货方企业类型(GHFQYLX)
     */
    private String buyerEnterpriseTypeCode;
    
    /**
     * 开票员(KPY)
     */
    private String billingStaff;
    
    /**
     * 销货方识别号(XHF_NSRSBH)
     */
    private String sellerTaxpayerIdentifyNo;
    
    /**
     * 销货方名称(XHFMC)
     */
    private String sellerName;
    
    /**
     * 行业名称(HY_MC)
     */
    private String industryName;
    
    /**
     * 开票方名称(NSRMC)
     */
    private String taxpayer;
    
    /**
     * 操作代码(CZDM)
     */
    private String operatorNo;
    
    /**
     * 购方开户行及账号(FKFKHYH_FKFYHZH)
     */
    private String infoClientBankAccount;
    
    /**
     * 购方地址电话(FKFDZ_FKFDH)
     */
    private String infoClientAddressPhone;
    
    /**
     * 销方开户行及账号(XHFKHYH_SKFYHZH)
     */
    private String infoSellerBankAccount;
    
    /**
     * 销方地址电话(XHFDZ_XHFDH)
     */
    private String infoSellerAddressPhone;
    
    /**
     * 复核人(FHR)
     */
    private String infoChecker;
    
    /**
     * 销货清单(XHQD)
     */
    private String infoListName;
    
    /**
     * 合计税额（KPHJSE）
     */
    private Double infoTaxAmount;
    /**
     * 合计不含税金额（HJBHSJE）
     */
    private Double infoAmount;
    /**
     * 所属月份（SSYF）
     */
    private String InfoMonth;
    /**
     * 销货清单标志（XHQDBZ）
     */
    private String GoodsListFlag;
    /**
     * 返回编码（RETCODE）
     */
    private String RetCode;
    /**
     * 防伪密文（FWMW）
     */
    private String Ciphertext;
    /**
     * 校验码（JYM）
     */
    private String checkCode;
    /**
     * 数字签名（SZQM）
     */
    private String infoInvoicer;
    /**
     * 收款员（SKY）
     */
    private String cashier;
    /**
     * 编码表版本号，目前为1.0 (BMB_BBH)
     */
    private String codeTableVersion;
    
    /**
     * 发票种类代码
     */
    private String invoiceKindCode;
    /**
     * 清单标志(qd_bz)
     */
    private String listFlag;
    /**
     * 清单项目名称(qdxmmc)
     */
    private String listItemname;
    
    /**
     * 发票代码(FP_DM)
     */
    private String invoiceCode;
    /**
     * 发票号码(FP_HM)
     */
    private String invoiceNo;
    /**
     * 开票日期(KPRQ)
     */
    private Date billingDate;
    /**
     * 二维码(EWM)
     */
    private String twoDimensionCode;
    
    /**
     * 机器编号(jqbh)
     */
    private String machineNumber;
    
    /**
     * 分机号(fjh)
     */
    private String extensionNumber;
    
    /**
     * 开票流水号(KPLSH,发票代码+发票号码)
     */
    private String invoiceSerialNo;
    /**
     * 处理业务错误代码
     */
    private String returnCode;
    
    /**
     * 收购标志
     **/
    private String takeoversMark;
    /**
     * 代开标志
     */
    private String agentInvoiceFlag;
    /**
     * 处理业务错误描述
     */
    private String returnMessage;
    /**
     * 队列错误处理次数
     */
    private int queueErrorCount;
    
    
    /**
     * 特殊冲红标志
     */
    private String tschbz;
    
    private String byzd5;
    
    private String byzd4;
    
    private String byzd3;
    
    private String byzd2;
    
    private String byzd1;
    
    private String pdfFile;
    /**
     * 明细
     */
    private List<InvoiceDetailQueueEntity> detailQueueEntityList;
}
