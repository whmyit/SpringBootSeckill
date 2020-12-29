package com.dxhy.order.model.itaxbaseapi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * 发票余量返回实体
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:21
 */
@Getter
@Setter
public class MealAllowanceDto extends ResponseBaseDto{

     private List<MealAllowanceDataDto> data;

}
