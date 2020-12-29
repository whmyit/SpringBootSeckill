package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 开票人信息
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:45
 */
@Setter
@Getter
public class DrawerInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private  String  id;
    
    /**
     * 税号
     */
    private String xhfNsrsbh;
    /**
     *开票人名称
     */
    private  String  drawerName;
    /**
     *复核人
     */
    private  String  reCheckName;
    /**
     *收款人
     */
    private  String  nameOfPayee;
    /**
     *创建时间
     */
    private Date createTime;
    /**
     *创建人
     */
    private  String  credateUserId;
    /**
     *更新时间
     */
    private  Date  modifyTime;
    /**
     *更新人
     */
    private  String  modifyUserId;

}
