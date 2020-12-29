package com.dxhy.order.consumer.protocol.fiscal;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 11:00:13
 * @Describe
 */
@Setter
@Getter
public class SLD_DEL_REQ {
    private String sldId;
    private List<String> xhfNsrsbh;
}
