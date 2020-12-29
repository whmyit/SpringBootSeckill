package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 金税盘响应扩展信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:17
 */
@Getter
@Setter
public class JspxxResponseExtend extends ResponseBaseBeanExtend {
    
    List<Jspxx> sldJspxxList;
    
}
