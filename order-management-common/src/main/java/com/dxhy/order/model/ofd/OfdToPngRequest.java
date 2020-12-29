package com.dxhy.order.model.ofd;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * ofd转png请求bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/6/5 17:44
 */
@Getter
@Setter
public class OfdToPngRequest implements Serializable {
    private String OFDWJL;
}
