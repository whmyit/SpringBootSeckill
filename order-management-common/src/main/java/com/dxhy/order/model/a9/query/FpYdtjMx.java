package com.dxhy.order.model.a9.query;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：FpYdtjMx
 * @Description ：
 * @date ：2019年8月20日 下午4:07:47
 */
@Getter
@Setter
public class FpYdtjMx {
    
    private String sl;
    /**
     * 明细信息
     */
    private List<FpYdtjMxItem> mxxxs;
    
}
