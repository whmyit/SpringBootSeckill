package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
* @Description: 商品同步集团税编信息
* @Author:xueanna
* @Date:2019/9/23
*/
@Setter
@Getter
public class CommodityTaxClassCodeReq implements Serializable {
    private List<CommodityTaxClassCodeParam> taxClassCodeIdArray;
    private List<String> xhfNsrsbh;
}
