package com.dxhy.order.model.protocol;

/**
 * 数据业务bean
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:41
 */
public class CommonReponse {

    private ResponseStatus responseStatus;

    private ResponseData responseData;

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseData responseData) {
        this.responseData = responseData;
    }
}
