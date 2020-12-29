package com.dxhy.order.protocol.v4.invoice;

import com.dxhy.order.protocol.v4.RESPONSEV4;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 红字信息表上传返回协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/12/19 10:27
 */
@Setter
@Getter
public class HZSQDSC_RSP extends RESPONSEV4 {
    
    /**
     * 申请单上传请求批次号
     */
    private String SQBSCQQPCH;
    
    /**
     * 申请单上传返回对象
     */
    private List<HZSQDSCJG> HZSQDSCJG;
}
