
package com.dxhy.order.model.a9.query;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ：杨士勇
 * @ClassName ：YhzxxResponse
 * @Description ：月度汇总信息
 * @date ：2019年8月20日 下午3:28:41
 */
@Getter
@Setter
public class YhzxxResponse extends ResponseBaseBean {
    
    private YhzxxResponseExtend result;
    
}
