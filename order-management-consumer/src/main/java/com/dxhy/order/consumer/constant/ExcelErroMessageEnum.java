package com.dxhy.order.consumer.constant;

/**
 * excel导入错误提示类
 *
 * @author ZSC-DXHY
 */

public enum ExcelErroMessageEnum {
    /**
     * 校验成功
     */
    SUCCESSCODE("0000", "处理成功"),
    /**
     * 备注
     */
    RECEIVE_DATA_FAILD("9999", "数据校验失败"),
    /**
     * 订单号为空
     */
    ORDERINFO_NULL("9601", "存在未填写订单号的数据"),
    /**
     * 税号填写错误
     */
    TAX_ERROR("9603", "购方税号填写有误"),
    /**
     * 税号填写有误
     */
    TAX_LENGTH_ERROR("9604", "购方税号为15、17、18、20位字母或数字组成"),
    /**
     * 数量填写有误
     */
    ORDERINFO_SL_ERROR("9605", "税率必须带%且不可超过100%"),
    /**
     * 单价填写有误
     */
    ORDERINFO_DJ_ERROR("9606", "单价填写有误"),
    /**
     * 金额为空
     */
    ORDERINFO_JE_NULL("9607", "未填写金额"),
    /**
     * 金额错误
     */
    ORDERINFO_JE_ERROR("9608", "金额填写有误"),
    /**
     * 税收分类编码非数字
     */
    ORDERINFO_SSFLBM_ERROR("9609", "税收编码填写有误"),
    /**
     * 税收分类编码位数有误
     */
    ORDERINFO_SSFLBM_LENGTH_EROR("9610", "税收编码位数有误"),
    /**
     * 订单折扣行的上一行不是折扣行
     */
    ORDERINFO_ZKH_ERROR("9611", "折扣行未与商品行紧邻"),
    /**
     * 商品名称未填写
     */
    ORDERINFO_XMMC_NULL("9613", "未填写商品名称"),
    /**
     * 文件类型错误
     */
    ORDERINFO_FILEPREFIX_ERROR("9612", "文件类型错误"),
    /**
     * 税率格式错误
     */
    ORDERINFO_SL_FORMAT_ERROR("9614", "税率必须带%且不可超过100%"),
    /**
     * 订单导入Excel读取错误
     */
    ORDERINFO_EXCEL_READERROR("9615", "excel读取错误"),
    /**
     * 企业类型为空
     */
    ORDERINFO_ENTERPRISE_TYPE_NULL("9616", "购货方抬头类型为空"),
    /**
     * 企业类型错误
     */
    ORDERINFO_ENTERPRISE_TYPE_ERROR("9617", "购货方抬头类型错误"),
    /**
     * 购买方名称超过100个字符
     */
    ORDERINFO_GHFMC_OVERLENGTH("9618", "购方名称超过最大长度100位"),
    /**
     * 门店号长度有误
     */
    ORDERINFO_MDH_OVERLENGTH("9619", "门店号长度超过40位"),
    /**
     * 购货方电话 20位
     */
    ORDERINFO_GHFDH_OVERLENGTH("9620", "购方电话超过最大长度20位"),
    /**
     * 购货方银行 70位
     */
    ORDERINFO_GHFYH_OVERLENGTH("9621", "购方开户行超过最大长度100位"),
    /**
     * 购货方账号 30位.
     */
    ORDERINFO_GHFZH_OVERLENGTH("9622", "购方银行账号超过最大长度30位"),
    /**
     * 商品名称 90位
     */
    ORDERINFO_SPMC_OVERLENGTH("9623", "商品名称最长为90字符"),
    /**
     * 规格型号 200位
     */
    ORDERINFO_GGXH_OVERLENGTH("9624", "规则型号超最大长度40位"),
    /**
     * 单位 100位
     */
    ORDERINFO_DW_OVERLENGTH("9625", "单位超出最大长度20位"),
    /**
     * 单价24位
     */
    ORDERINFO_DJ_OVERLENGTH("9626", "单价超出最大长度20位，小数点后8位"),
    /**
     * 金额 16
     */
    ORDERINFO_JE_OVERLENGTH("9627", "金额为16位，小数点后2位"),
    /**
     * 税率 8位
     */
    ORDERINFO_SL_OVERLENGTH("9628", "税率超过8位"),
    /**
     * 数量 24
     */
    ORDERINFO_XMSL_OVERLENGTH("9629", "数量超出最大位数20位，小数点后8位"),
    /**
     * 订单号长度有误
     */
    ORDERINFO_DDH_LENGTH_ERROR("9630", "订单号为字母或数字，下划线组成，最大长度为50位"),
    /**
     * 购货方地址+电话 100位
     */
    ORDERINFO_GHFDZ_OVERLENGTH("9631", "购货方地址和电话总长度超过100位"),
    /**
     * 发票类型为空
     */
    ORDERINFO_KPLX_NULL("9632", "发票类型为空"),
    /**
     * 项目单价为0
     */
    ORDERINFO_XMDJ_ZERO("9633", "单价金额为0"),
    /**
     * 项目金额为0
     */
    ORDERINFO_XMJE_ZERO("9634", "项目金额为0"),
    /**
     * 项目数量为0
     */
    ORDERINFO_XMSL_ZERO("9635", "数量填写有误"),
    /**
     * 项目数量小数点后超过8位
     */
    ORDERINFO_XMSL_OVERLENGTHEIGHT("9636", "数量超出最大位数20位，小数点后8位"),
    /**
     * 项目单价小数点后超过8位
     */
    ORDERINFO_XMDJ_OVERLENGTHEIGHT("9637", "单价超出最大长度20位，小数点后8位"),
    /**
     * 备注超过200位
     */
    ORDERINFO_BZ_OVERLENGTHEIGHT("9638", "备注超过150位"),
    /**
     * 购货方电话格式错误
     */
    ORDERINFO_GHFDH_ERROR("9639", "购方电话格式错误"),
    /**
     * 订单号格式错误
     */
    ORDERINFO_DDHFORMAT_ERROR("9640", "订单号为字母或数字，下划线组成，最大长度为50位"),
    /**
     * 开票类型长度错误
     */
    ORDERINFO_UNKNOW_KPLX("9602", "未知的发票类型"),
    /**
     * 项目数量不符合格式
     */
    ORDERINFO_XMSL_ERROR("9641", "数量填写有误"),
    /**
     * 单价 数量 金额 不一致
     */
    ORDERINFO_JE_DJ_INCONFORMITY("9643", "单价，数量金额不一致"),
    /**
     *
     */
    ORDERINFO_ELE_ITEM_OVERLIMIT("9644", "电票明细超过1000行"),
    /**
     * 专票的抬头类型不能为个人
     */
    ORDERINFO_SPECIAL_QYLX_ERROR("9645", "专票的抬头类型不能为个人"),
    /**
     * 专票的抬头类型不能为个人
     */
    ORDERINFO_RANGE_OVERLIMIT_ERROR("9646", "数据超过一万行"),
    /**
     * excel模板错误
     */
    ORDERINFO_EXCEL_TEMPLATE_ERROR("9647", "excel模板错误"),
    /**
     * 含税标志空
     */
    ORDERINFO_EXCEL_HSBZ_NULL("9649", "含税标志为空"),
    /**
     * 含税标志错误
     */
    ORDERINFO_EXCEL_HSBZ_UNKNOWN("9650", "未知含税标志"),
    /**
     * 项目单价数量金额不一致
     */
    ORDERINFO_XMDJ_ERROR("9642", "数量、单价与金额不匹配"),
    
    /**
     * 税收分类编码有误
     * todo 后期修改为9699,前端自适应显示错误信息
     */
    ORDERINFO_SSFLBM_SL_ERROR("9610", "税收编码为大类,税率必填"),
    
    ORDERINFO_SSFLBM_SL_NULL("9611","该商品税率没有维护，不能为空"),

    ORDERINFO_9701("9701", "企业自编码长度超过16位"),

    ORDERINFO_9702("9702", "编码表版本号长度超过10位"),

    ORDERINFO_9703("9703", "税额长度超过30位"),

    ORDERINFO_9704("9704", "输入税额有误"),

    ORDERINFO_9705("9705", "零税率标识输入有误"),

    ORDERINFO_9706("9706", "零税率标识、税率与享受税收优惠政策内容不一致"),

    ORDERINFO_9707("9707", "优惠政策标识只能为是和否"),

    ORDERINFO_9708("9708", "优惠政策标识和享受税收优惠政策内容不一致"),

    ORDERINFO_9709("9709", "零税率标识与税率不一致"),

    ORDERINFO_9710("9710", "零税率标识与享受税收优惠政策内容不一致"),

    ORDERINFO_9711("9711","享受税收优惠政策内容与税率不一致" ),

    ORDERINFO_9712("9712", "零税率标识与享受税收优惠标识不一致"),

    ORDERINFO_9713("9713", "增值税特殊管理长度超过50位！"),

    ORDERINFO_9714("9714","成品油单位只能为吨或者升"),
    
    ORDERINFO_9715("9715", "成品油和非成品油明细不能混开"),
    
    ORDERINFO_9716("9716", "成品油的项目数量不能为空"),
    
    ORDERINFO_9717("9717", "成品油的项目明细不能超过8行"),
    
    ORDER_JE_9718("9718", "订单的金额不能为0"),
    
    ORDERINFO_ZKH_XMJE_ERROR("9719", "折扣行金额错误"),
    ORDERINFO_9720("9720", "增值税特殊管理百分号不正确"),
    
    /**
     * 购货方地址 80位
     */
    ORDERINFO_GHFDZ_LENGTH_ERROR("9720", "购货方地址长度超过100位"),
    
    /**
     * 购货方银行+账户 100位
     */
    ORDERINFO_GHFYH_GHFZH_OVERLENGTH("9721", "购货方银行和账户总长度超过100位"),
    /**
     *
     */
    ORDERINFO_YWLX_OVERLENGTH("9722", "业务类型长度最大100位"),
    
    ORDERINFO_ZKH_SPBM_ERROR("9723","折扣行税收分类编码与被折扣行不一致"),

    ORDERINFO_ZKH_SL_ERROR("9723","折扣行商品税率与被折扣行不一致"),


    ORDERINFO_GFBM_ERROR("9724","购方编码超过长度限制"),
    ORDERINFO_GFYX_ERROR("9725","购方邮箱超过长度限制"),
    ORDERINFO_GFYX_FORMAT_ERROR("9725","购方邮箱格式错误"),
    ORDERINFO_DDH_EXCEL_REPEAT_ERROR("9726","excel表格中存在重复的订单号数据!"),

    ORDERINFO_DDH_REPEAT_ERROR("9727","订单号已存在!"),




    ;


    private final String key;

    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    ExcelErroMessageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static ExcelErroMessageEnum getCodeValue(String key) {

        for (ExcelErroMessageEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
