package com.dxhy.order.model.a9.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @ClassName ：QueryNsrpzKpxeResponseExtend
 * @Description ：查询限额返回对象
 * @author ：杨士勇
 * @date ：2019年6月27日 下午7:23:06
 *
 *
 */
@Getter
@Setter
public class QueryNsrpzKpxeResponseExtend extends ResponseBaseBeanExtend{
	private String nsrsbh;
    private String nsrmc;
    private String fpzlDm;
    private String kpxe;
}
