package com.dxhy.order.model.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 返回外层报文
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class ResponseData implements Serializable {
    private static final long serialVersionUID = -229816874132169722L;
    private String encryptCode;
    private String zipCode;
    private String content;


    public ResponseData(String encryptCode, String zipCode, String content) {
        this.encryptCode = encryptCode;
        this.zipCode = zipCode;
        this.content = content;
    }

    public ResponseData() {
    }
}
