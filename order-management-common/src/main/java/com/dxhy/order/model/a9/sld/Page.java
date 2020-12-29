package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 受理点分页对象
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:18
 */
@Getter
@Setter
public class Page extends ResponseBaseBeanExtend {
    
    private List<KpdSldSearchResponse> list;
    private String totalCount;
    private String pageSize;
    private String totalPage;
    private String currPage;
    
}
