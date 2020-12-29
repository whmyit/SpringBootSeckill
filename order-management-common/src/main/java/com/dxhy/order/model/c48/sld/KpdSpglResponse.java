package com.dxhy.order.model.c48.sld;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 开票点C48
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/5 1:18
 */
@Getter
@Setter
public class KpdSpglResponse extends ResponseBaseBeanExtend implements Serializable {
    private List<FpKpd> fpkpds;
    private String totalCount;
    private String pageSize;
    private String totalPage;
    private String currPage;
}
