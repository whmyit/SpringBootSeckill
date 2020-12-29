package com.dxhy.order.consumer.modules.supplychain.model;

import com.dxhy.order.utils.JsonUtils;
import lombok.Data;

/**
 * 供应链状态请求
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:43
 */
@Data
public class SynCheckResultRequestBean {

    private String xfTaxno;
    private String batchNo;
    private String poNo;
    private String status;
    private String message;

    public static void main(String[] args) {

        SynCheckResultRequestBean requestBean  = new SynCheckResultRequestBean();
        System.out.println(JsonUtils.getInstance().toJsonStringNullToEmpty(requestBean));

    }

}
