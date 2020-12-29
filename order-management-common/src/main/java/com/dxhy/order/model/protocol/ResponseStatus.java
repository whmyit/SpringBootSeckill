package com.dxhy.order.model.protocol;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 接口返回外层
 *
 * @author ZSC-DXHY
 */
@Setter
@Getter
public class ResponseStatus implements Serializable {

    private static final long serialVersionUID = -2529816874169707L;

    private String code;
    private String message;

    public ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseStatus() {
    }
}
