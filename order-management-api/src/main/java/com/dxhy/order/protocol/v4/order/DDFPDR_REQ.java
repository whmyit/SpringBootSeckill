package com.dxhy.order.protocol.v4.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 历史订单发票数据导入请求协议bean
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/17
 */
@Getter
@Setter
@ToString
public class DDFPDR_REQ implements Serializable {
    /**
     * 订单发票全数据协议bean集合
     */
    private List<DDFPZXX> DDFPZXX;
}
