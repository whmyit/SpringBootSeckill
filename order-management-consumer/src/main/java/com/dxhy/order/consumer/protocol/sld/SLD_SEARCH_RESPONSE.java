package com.dxhy.order.consumer.protocol.sld;

import com.dxhy.order.protocol.RESPONSE;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 受理点列表查询返回协议bean
 *
 * @author ZSC-DXHY-PC
 * @date 创建时间: 2018/11/29 20:41
 */
@Setter
@Getter
public class SLD_SEARCH_RESPONSE extends RESPONSE implements Serializable {
    private List<SLD_SEARCH> SLDS;
}
