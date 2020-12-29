package com.dxhy.order.model.ukey;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * 百望盘阵和UKey统一机器编号数据返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-22 9:29
 */
@Getter
@Setter
public class QueryJqbhResponse extends ResponseBaseBean {
    
    private QueryJqbhList result;
}
