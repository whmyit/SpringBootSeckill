package com.dxhy.order.model.ypyj;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 余票预警前端交互bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/12 17:28
 */
@Getter
@Setter
public class YpYjFront implements Serializable {
    
    
    /**
     * 纳税人识别号
     */
    private String xhfNsrsbh;
    /**
     * 纳税人名称
     */
    private String xhfMc;
    
    private List<Fpzldm> fpzldms;
    
    /**
     * 邮箱
     */
    private String userEmail;
    
    /**
     * 是否预警,0不预警 1预警
     */
    private String sfyj;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 更新人
     */
    private String userId;
    
    /**
     * 邮寄发送次数
     */
    private int yjcs;
    
    /**
     * 更新人所属组织 ID
     */
    private String deptId;
}
