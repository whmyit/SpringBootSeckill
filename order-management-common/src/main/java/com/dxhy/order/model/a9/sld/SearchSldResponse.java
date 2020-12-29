package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Author xueanna
 * @Date 2019/8/1 16:07
 */
@Setter
@Getter
public class SearchSldResponse extends ResponseBaseBeanExtend implements Serializable {
    
    private List<SearchSld> slds;
    
    private String fpzlDm;
    
    private String Nsrsbh;
    
    private String cpyzt;
}
