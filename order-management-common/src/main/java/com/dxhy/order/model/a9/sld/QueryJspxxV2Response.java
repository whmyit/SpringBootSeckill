package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 查询金税盘响应
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:29
 */
@Getter
@Setter
public class QueryJspxxV2Response extends ResponseBaseBeanExtend {
    
    private List<JspxxcxA9> jspxxs;
    
    
}
