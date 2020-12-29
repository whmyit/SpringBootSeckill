package com.dxhy.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 开票冲红作废统计 Bean
 *
 * @author thinkpad fankunfeng
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRedInvalidStatisticsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 统计个数
     */
    private int count;
    /**
     * 作废备注
     * 注：0 正常 1 作废成功 （其中2作废中3作废失败不予统计）
     */
    private int zfbz;
    /**
     * 开票类型
     * 注：0蓝,1红
     */
    private int kplx;
    /**
     * 统计月份
     * 格式：yyyyMM
     */
    private String month;
}
