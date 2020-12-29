package com.dxhy.order.model.a9.sld;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author xueanna
 * @Date 2019/8/1 16:09
 */
@Setter
@Getter
@EqualsAndHashCode
public class SearchSld implements Serializable {
    private String sldId;
    private String sldMc;
    private String jqbh;
    private String nsrsbh;
    private String zdbs;
    private String fjh;
    /**
     * 税控设备标志
     */
    private String terminalCode;
}
