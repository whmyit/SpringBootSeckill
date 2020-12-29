package com.dxhy.order.model.c48.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 受理点C48获取
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/4 22:29
 */
@Setter
@Getter
public class SldglResponse extends ResponseBaseBeanExtend implements Serializable {
    
    private List<Fpsldmx> fpslds;
    private String totalCount;
    private String pageSize;
    private String totalPage;
    private String currPage;
}
