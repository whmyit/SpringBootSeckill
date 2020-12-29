package com.dxhy.order.consumer.protocol.order;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 红字申请单上传返回信息
 * todo V1或者是V2版本,后续不再更新迭代.
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/5/15 16:08
 */
@Getter
@Setter
@Deprecated
public class RESPONSE_HZFPSQBSC implements Serializable {
    
    private String SQBSCQQLSH;
    private String SQDH;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
    private String XXBBH;
}
