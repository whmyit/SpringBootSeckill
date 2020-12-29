package com.dxhy.order.protocol.order;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 订单全数据协议bean
 *
 * @author ZSC-DXHY
 */
@ToString
@Setter
@Getter
public class COMMON_ORDER_HEAD implements Serializable {
    /**
     * 订单请求唯一流水号
     */
    private String DDQQLSH;
    
    /**
     * 纳税人识别号  销货方纳税人识别号
     */
    private String NSRSBH;
    
    /**
     * 纳税人名称  销货方纳税人名称
     */
    private String NSRMC;
    
    /**
     * 开票类型
     */
    private String KPLX;
    
    /**
     * 编码表版本号
     */
    private String BMB_BBH;
    
    /**
     * 销售方纳税人识别号
     */
    private String XSF_NSRSBH;
    
    /**
     * 销售方名称
     */
    private String XSF_MC;
    
    /**
     * 销售方地址
     */
    private String XSF_DZ;
    
    /**
     * 销售方电话
     */
    private String XSF_DH;
    
    /**
     * 销售方银行账号名称
     */
    private String XSF_YH;
    
    /**
     * 销售方银行账号
     */
    private String XSF_ZH;
    
    /**
     * 购买方编码
     */
    private String GMF_ID;
    
    /**
     * 购买方纳税人识别号
     */
    private String GMF_NSRSBH;
    
    /**
     * 购买方名称
     */
    private String GMF_MC;
    
    /**
     * 购买方地址
     */
    private String GMF_DZ;
    
    /**
     * 购买方企业类型
     */
    private String GMF_QYLX;
    
    /**
     * 购买方省份
     */
    private String GMF_SF;
    
    /**
     * 购买方固定电话
     */
    private String GMF_GDDH;
    
    /**
     * 购买方银行账号名称
     */
    private String GMF_YH;
    
    /**
     * 购买方银行账号
     */
    private String GMF_ZH;
    
    /**
     * 购买方手机
     */
    private String GMF_SJ;
    
    /***
     * 购买方邮箱
     */
    private String GMF_EMAIL;
    
    /**
     * 开票人
     */
    private String KPR;
    
    /**
     * 收款人
     */
    private String SKR;
    
    /**
     * 复核人
     */
    private String FHR;
    
    /**
     * 原发票代码
     */
    private String YFP_DM;
    
    /**
     * 原发票号码红字发票必填
     */
    private String YFP_HM;
    
    /**
     * 清单标志
     */
    private String QD_BZ;
    
    /**
     * 清单发票项目名称
     */
    private String QDXMMC;
    
    /**
     * 价税合计
     */
    private String JSHJ;
    
    /**
     * 合计金额(不含税)
     */
    private String HJJE;
    
    /**
     * 合计税额
     */
    private String HJSE;
    
    /**
     * 备注  冲红时必填
     * 增值税发票红字发票（非专票）开具时，备注要求:
     * 开具负数发票，必须在备注中注明“对应正数发票代码:XXXXXXXXX号码:YYYYYYYY”字样，其中“X”为发票代码，“Y”为发票号码。如未注明，系统自动追加。
     * 增值税发票红字发票（专票）开具时，备注要求:
     * 开具负数发票，必须在备注中注明“开具红字增值税专用发票信息表编号ZZZZZZZZZZZZZZZZ”字样，其中“Z”为开具红字增值税专用发票所需要的长度为16位信息表编号。如未注明，系统报错。
     */
    private String BZ;
    
    /**
     * 冲红原因
     */
    private String CHYY;
    
    /**
     * 特殊冲红标志
     * 冲红时必填：0正常冲红(电子发票)1特殊冲红(冲红纸质等)
     */
    private String TSCHBZ;
    
    /**
     * 订单号
     */
    private String DDH;
    
    /**
     * 退货单号
     */
    private String THDH;
    
    /**
     * 订单时间
     */
    private String DDDATE;
    
    /**
     * 门店号
     */
    private String MDH;
    
    /**
     * 业务类型区分企业业务线),可以企业自定义
     */
    private String YWLX;
    
    /**
     * 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
     */
    private String KPFS;
    
    /**
     * 提取码
     */
    private String TQM;
    
    /**
     * 动态码URL
     */
    private String DYNAMIC_CODE_URL;
    
    /**
     * 订单状态
     */
    private String ORDER_STATUS;
    
    /**
     * 订单状态对应的信息
     */
    private String ORDER_MESSAGE;
    
    /**
     * 备用字段
     */
    private String BYZD1;
    private String BYZD2;
    private String BYZD3;
    private String BYZD4;
    private String BYZD5;
    
}
