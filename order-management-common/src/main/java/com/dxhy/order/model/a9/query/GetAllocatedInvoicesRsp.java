package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Description: 发票开具结果数据返回bean
 * @Author: zgj
 * @CreateDate: 2018-07-23 17:42
 * @UpdateUser: zgj
 * @UpdateDate: 2018-07-23 17:42
 * @UpdateRemark:
 * @Version: 1.0
 */
@Getter
@Setter
public class GetAllocatedInvoicesRsp extends ResponseBaseBeanExtend {
    /**
     * 发票请求批次号
     */
    private String FPQQPCH;
    
    private String NSRSBH;
    /**
     * 发票响应信息实体
     */
    private List<ResponseCommonInvoice> RESPONSE_COMMON_INVOICE;
}
