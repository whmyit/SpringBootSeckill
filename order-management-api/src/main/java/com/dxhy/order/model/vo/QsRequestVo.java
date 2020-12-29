package com.dxhy.order.model.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 全税接口对接参数实体
 * @Author xueanna
 * @Date 2019/5/31 16:23
 */
@Setter
@Getter
public class QsRequestVo implements Serializable {

    /**
     * 通知类型  1、税率统计     2、项目统计
     */
    private String informType;
    
    /**
     * 业务参数
     */
    private List<QsBusinessVo> param;
    
}

