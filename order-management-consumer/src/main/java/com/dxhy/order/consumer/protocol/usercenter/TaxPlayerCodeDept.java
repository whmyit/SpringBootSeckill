package com.dxhy.order.consumer.protocol.usercenter;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 用户中心认证：纳税人识别号组织实体
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/4/8 19:19
 */
@Getter
@Setter
public class TaxPlayerCodeDept extends DeptEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 角色信息集合
     */
    private List<RoleEntity> roleList;
}
