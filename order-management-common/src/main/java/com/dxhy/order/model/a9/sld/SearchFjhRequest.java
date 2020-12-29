package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.RequestBaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:  根据纳税人识别号查询分机号 查询发票种类代码 请求对象
 * @Author: chenyuzhen
 * @CreateDate: 2019/7/10 11:02
 */
@Setter
@Getter
public class SearchFjhRequest extends RequestBaseBean{
    private String nsrsbh;
    private String fjh;
}
