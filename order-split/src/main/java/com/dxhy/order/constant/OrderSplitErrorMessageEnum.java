package com.dxhy.order.constant;

/**
 * @author ：杨士勇
 * @ClassName ：TaxSeparationErrorMessageEnum
 * @Description ：价税分离错误信息枚举类
 * @date ：2019年9月3日 上午10:00:58
 */

public enum OrderSplitErrorMessageEnum {
    
    /**
     * 订单拆分异常枚举值
     */
    ORDER_SPLIT_UNKNOWN_ERROR("1000", "订单拆分未知异常!"),
    ORDER_SPLIT_ORDERINFO_NULL_ERROR("1001", "订单信息为空!"),
    ORDER_SPLIT_ORDERITEMINFO_NULL_ERROR("1002", "订单明细信息为空!"),
    ORDER_SPLIT_ORDERITEMINFO_JE_NULL_ERROR("1003", "订单号为：%s的订单，第%s行明细金额为空!"),
    ORDER_SPLIT_TYPE_ERROR("1004", "订单拆分方式只能是 1：超限额拆分 2：按金额拆分 3：按数量拆分 4：超明细行拆分 5：明细行拆分"),
    ORDER_SPLIT_ORDERINFO_LIMITJE_ERROR("1005", "超限额拆分 limitJe参数不能为空，并且不能小于等于0!"),
    ORDER_SPLIT_ORDERINFO_JE_ERROR("1006", "订单按金额拆分 jeList参数不能为空，并且金额不能小于等于0!"),
    ORDER_SPLIT_ORDERINFO_JE_LESS_ZERO_ERROR("1007", "订单总金额小于0,红票不支持拆分!"),
    ORDER_SPLIT_ORDERINFO_SL_ERROR("1008", "订单按数量拆分 slList参数不能为空，并且拆分数量不能小于等于0!"),
    ORDER_SPLIT_ORDERINFO_LIMIT_RANG_ERROR("1009", "订单按明细行拆分 limitRang参数不能为空，并且要拆分的明细行数不能小于等于0!"),
    ORDER_SPLIT_ORDERINFO_RANG_ERROR("1010", "订单按明细行拆分 lineList参数不能为空，并且要拆分的明细行数不能小于等于0!"),
    ORDER_SPLIT_ORDERINFO_OVER_LINE_ERROR("1011", "传入的拆分明细行数大于总明细行数!"),
    ORDER_SPLIT_ORDERINFO_MXSL_ERROR("1012", "按数量拆分明细行数只能为1!"),
    ORDER_SPLIT_ORDERINFO_MXSL_NULL_ERROR("1013", "按数量拆分,明细行数量不能为空!"),
    ORDER_SPLIT_ORDERINFO_OVER_MXSL_ERROR("1014", "数量拆分，拆分的数量不能大于明细数量!"),
    ORDER_SPLIT_ORDERINFO_HSBZ_ERROR("1015", "订单明细只能为全部含税或者全部不含税，请查看含税标志是否一致!"),
    ORDER_SPLIT_ORDERINFO_SE_ERROR("1016", "不含税拆分，明细中的税额只能全部为空后者全部都不为空!"),
    ORDER_SPLIT_ORDERINFO_DJ_OVER_SPLITJE__ERROR("1017", "单价大于要拆分的金额，无法拆分"),
    ORDER_SPLIT_ORDERINFO_DJ_OVER_JE("1018", "单价大于限额，无法保证数量是整数!"),
    ORDER_SPLIT_ORDERINFO_KPJE_DIFF_ERROR("1019", "拆分后的订单开票合计金额与拆分前的订单开票合计金额不一致!"),
    
    ORDER_SPLIT_ERROR("9999", "请勿重复超限额拆分"),
    ORDER_SPLIT_ERROR1("9999", "当前数据正在进行拆分操作,请勿重复拆分"),
    ORDER_SPLIT_KCE_ERROR("1020","扣除额大于限额，暂不支持拆分!"),
    ORDER_SPLIT_KCE_ILLEGAL("1021","暂不支持扣除额发票拆分"),

    ;
    /**
     * key
     */
    private final String key;
    
    /**
     * 值
     */
    private final String value;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    OrderSplitErrorMessageEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static OrderSplitErrorMessageEnum getCodeValue(String key) {

        for (OrderSplitErrorMessageEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

}
