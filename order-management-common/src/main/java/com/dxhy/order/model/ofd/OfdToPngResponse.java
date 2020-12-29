package com.dxhy.order.model.ofd;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * ofd转png返回bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/5 17:46
 */
@Getter
@Setter
public class OfdToPngResponse implements Serializable {
    private String ZTDM;
    private String ZTXX;
    private String PNGWJL;
}
