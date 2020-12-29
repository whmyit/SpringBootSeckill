package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Description 全税请求业务参数实体
 * @Author xueanna
 * @Date 2019/5/31 16:25
 */
@Setter
@Getter
public class QsBusinessVo implements Serializable {
    /**
     *   税号
     */
    private String taxNumber;

    /**
     *   开票日期
     */
    private String billingDate;
}
