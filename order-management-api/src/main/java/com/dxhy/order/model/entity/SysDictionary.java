package com.dxhy.order.model.entity;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 字典表
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:43
 */
@Getter
@Setter
public class SysDictionary implements Serializable {
    private String id;
    private String  name;
    private String  type;
    private String  code;
    private String  value;
    private String  orderNum;
    private String  remark;
    private String  delFlag;

}
