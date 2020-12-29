package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author fankunfeng
 * @Date 2019-06-13 11:07:35
 * @Describe
 */
@Setter
@Getter
public class MessageInfoTaxCodeList extends AbstractMessage implements Serializable {
    /**
     * 纳税人识别号
     */
    private String[] taxpayerCode;
}
