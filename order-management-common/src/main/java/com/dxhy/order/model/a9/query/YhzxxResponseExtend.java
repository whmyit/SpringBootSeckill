package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：YhzxxResponseExtend
 * @Description ：发票汇总表汇总扩展bean
 * @date ：2019年8月20日 下午3:33:41
 */
@Getter
@Setter
public class YhzxxResponseExtend extends ResponseBaseBeanExtend {
    
    private List<FpYdtj> fpYdtj;
    
}
