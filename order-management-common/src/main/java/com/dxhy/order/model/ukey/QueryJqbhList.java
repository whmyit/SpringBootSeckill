package com.dxhy.order.model.ukey;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 百望盘阵和UKey统一机器编号数据返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-22 9:31
 */
@Getter
@Setter
public class QueryJqbhList implements Serializable {
    
    
    private List<QueryJqbh> jqbhs;
}
