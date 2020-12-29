package com.dxhy.order.model.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品分组表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:58
 */
@Setter
@Getter
public class GroupCommodity  implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private String id;
    /**
     *分组编码
     */
    private String groupCode;
    /**
     *分组名称
     */
    private String groupName;
    /**
     *上级分组编码
     */
    private String superiorCoding;

    /**
     *是否为页子节点   0 是  1 不是
     */
    private String isLeaf;

    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;

    /**
     * 创建人id
     */
    private String userId;

}
