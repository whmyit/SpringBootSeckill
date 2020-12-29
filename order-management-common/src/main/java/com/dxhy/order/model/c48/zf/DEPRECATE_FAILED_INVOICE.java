package com.dxhy.order.model.c48.zf;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 作废失败数据C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 23:26
 */
@Getter
@Setter
public class DEPRECATE_FAILED_INVOICE implements Serializable {
    private String FP_DM;
    private String FP_HM;
    private String STATUS_CODE;
    private String STATUS_MESSAGE;
}
