package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 发票响应信息实体
 * @Author: zgj
 * @CreateDate: 2018-07-23 17:31
 * @UpdateUser: zgj
 * @UpdateDate: 2018-07-23 17:31
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class ResponseCommonInvoice extends ResponseBaseBeanExtend {
    
    /**
     * 发票请求唯一流水号
     */
    private String FPQQLSH;
    
    /**
     * 税控设备编号
     */
    private String JQBH;
    
    /**
     * 发票代码
     */
    private String FP_DM;
    
    /**
     * 发票号码
     */
    private String FP_HM;
    
    /**
     * 开票日期
     */
    private String KPRQ;
    
    /**
     * 校验码
     */
    private String JYM;
    
    /**
     * 防伪码
     */
    private String FWM;
    
    /**
     * pdf下载路径
     */
    private String PDF_URL;
}
