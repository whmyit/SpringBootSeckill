package com.dxhy.order.consumer.constant;

/**
 * @author thinkpad
 * @date 2017/2/21
 */
public enum RespStatusEnum {
    /**
     *
     */
    AUTHFAIL("0001", "签名验证失败"),

    /**
     * secretId对应的secretKey为空
     */
    NOTAUTH("0002", "签名验证失败:secretId对应的secretKey为空!"),
    SUCCESS("0000", "数据接收成功"),
    FAIL("9999", "数据接收失败"),
    CONTENTNULL("9999", "数据解析失败"),
    NETERROR("9999", "网络错误"),
    PING("0000", "Connection SUCCESS"),
    TAYPAYERNULL("9999", "该纳税人无开票地址"),
    CHECKSUCCESS("0000", "对账查询成功"),
    QUERYINVOCESUCCESS("0000", "查询发票成功"),
    BUSINESSTYPEEROOR("9999", "当前税号业务类型错误"),
    NONETAXPAYER("9999", "没有初始化该纳税人信息"),
    NOCOMPLEMENT("9999", "企业信息补全失败"),
    CHECK_INTERFACEVERSION_NULL("9999", "接口参数-版本号为空"),
    CHECK_INTERFACEVERSION_DATA_ERROR("9999", "接口参数-版本号错误"),
    CHECK_INTERFACENAME_NULL("9999", "接口参数-接口名称为空"),
    CHECK_TIMESTAMP_NULL("9999", "接口参数-时间戳为空"),
    CHECK_NONCE_NULL("9999", "接口参数-随机数为空"),
    CHECK_SECRETID_NULL("9999", "接口参数-加密ID为空"),
    CHECK_SIGNATURE_NULL("9999", "接口参数-签名值为空"),
    CHECK_ENCRYPTCODE_NULL("9999", "接口参数-加密方式为空"),
    CHECK_ENCRYPTCODE_DATA_ERROR("9999", "接口参数-加密方式错误"),
    CHECK_ZIPCODE_NULL("9999", "接口参数-压缩方式为空"),
    CHECK_ZIPCODE_NULL_DATA_ERROR("9999", "接口参数-压缩方式错误"),
    CHECK_CONTENT_NULL("9999", "接口参数-数据为空"),
    OTHERPLAT("9999", "第三方平台调用失败");
    
    private final String code;
    private final String describe;
    
    RespStatusEnum(String code, String describe) {
        this.code = code;
        this.describe = describe;
    }
    
    
    public String getCode() {
        return this.code;
    }

    public String getDescribe() {
        return this.describe;
    }
}
