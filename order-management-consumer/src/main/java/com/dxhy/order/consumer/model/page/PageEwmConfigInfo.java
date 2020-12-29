
package com.dxhy.order.consumer.model.page;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName ：PageEwmConfigInfo
 * @Description ：
 * @author ：杨士勇
 * @date ：2020年2月20日 下午4:42:00
 * 
 * 
 */


@Setter
@Getter
public class PageEwmConfigInfo {
	
	    private String id;
	    private String xhfMc;

	    private String xhfNsrsbh;

	    private List<PageEwmItem> itemList;

	    private String invalidTime;

}
