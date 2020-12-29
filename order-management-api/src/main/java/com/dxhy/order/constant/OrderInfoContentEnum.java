package com.dxhy.order.constant;
/**
 * 订单接口枚举类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:18
 */
public enum OrderInfoContentEnum {

    /**
     * 所有业务统一返回参数
     * 规范:
     * 格式:key使用6位字符串表示,"000000"
     * CHECK_ISS7PRI_107006("107006", "批次信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
     * 前两位字符串代表的业务场景,
     */


    /**
     * 统配适应
     */

    SUCCESS("0000", "请求成功"),
    SUCCESS_1000("1000", "订单数据获取成功"),

    SUCCESS_000000("000000", "数据成功"),
    
    INTERNAL_SERVER_ERROR("9999", "系统异常"),

    RECEIVE_FAILD("9999", "订单处理失败"),
    
    MORE_NSRSBH_ERROR("9999", "请选择明确的销方信息进行操作"),

    PARAM_NULL("9001", "订单信息为空"),

    INVOICE_KPLX_ERROR("9004", "发票类型错误"),

    INVOICE_QYLX_ERROR("9005", "购货方企业类型错误"),

    INVOICE_DKBZ_ERROR("9006", "代开模式错误开(0)代开(1)"),

    INVOICE_CZDM_ERROR("9007", "开具操作代码类型错误"),

    INVOICE_TSCHBZ_ERROR("9008", "特殊冲红标志类型错误"),
    
    INVOICE_JSHJ_ERROR("109009", "合计税额+合计不含税金额不等于开票合计金额"),

    INVOICE_LSLBS_ERROR("9010", "零税率标识错误"),

    INVOICE_YHZCBS_ERROR("9011", "优惠政策标识"),

    INVOICE_HJJE_KPHXZ_ERROR("9013", "开票行性质错误"),

    INVOICE_XMJE_ZERO_ERROR("9014", "项目金额不能为零"),

    INVOICE_XMMX_SE_ERROR("009015", "订单明细数据中项目明细税额不能大于0.06"),

    INVOICE_XMMX_JE_ERROR("9016", "项目明细金额误差不能大于0.01"),

    INVOICE_XMMX_THAN_ZERO_ERROR("9017", "订单明细数据中蓝票折扣金额不能大于或者等于零"),

    INVOICE_XMMX_LESS_ZERO_ERROR("9018", "订单明细数据中红票折扣金额不能小于或者等于零"),

    INVOICE_XMMX_ZKH_ERROR("9019", "订单明细数据中折扣行不能为第一行或不能连续两个折扣行"),

    INVOICE_XMMX_ZKHANDBZKH_ERROR("9020", "订单明细数据中折扣行与被折扣行商品编码不相同"),

    INVOICE_XMMX_ZKHFIRST_ERROR("9021", "第一行不能为折扣行"),

    INVOICE_XMMX_XLZK_ERROR("9022", "相邻两行不能为折扣行"),

    INVOICE_XMMX_ZKEANDBZKE_ERROR("9023", "订单明细数据中折扣额不能大于被折扣额"),

    INVOICE_XMMX_ZKSL_ERROR("9024", "订单明细数据中折扣税率和被折扣税率不相同"),

    INVOICE_XMMX_ONE_FPHXZ_ERROR("9025", "订单明细数据中只有一行明细 发票性质必须为1"),

    INVOICE_XMMX_LAST_FPHXZ_ERROR("9026", "订单明细数据中明细行最后一行发票性质不能为2"),

    INVOICE_XMMX_MXCOUNT_ERROR("9027", "明细行数量需要大于零"),

    INVOICE_XMMX_HJJEANDMXJE_ERROR("9028", "订单明细数据中合计金额和明细金额不相等"),
    
    INVOICE_XMMX_HJSE_ERROR("9029", "订单明细数据中合计税额有误,误差大于127分钱,建议拆分订单开票"),

    INVOICE_FPKJ_JE_ERROR("9030", "金额范围错误"),

    INVOICE_BLUEHJJE_KPHJJE_ERROR("9031", "蓝票合计金额和红票合计金额不相等"),

    ORDER_TIME_ERROR("9032", "开始时间不能大于结束时间"),

    ORDER_PAGE_ERROR("9033", "页数不能为空"),

    PARAM_TIME_NULL("9034", "开始时间或结束时间不能为空"),

    INVOICE_XMMX_ZZSTSGL_ERROR("9035", "增值税特殊管理不能为空"),

    INVOICE_XMMX_LSLBS_ERROR("9036", "零税率标识错误"),

    ORDER_MERGE_KPLX_ERROR("9037", "订单合并开票类型不同"),

    ORDER_MERGE_BZ_OVERLENGTH("9038", "备注内容超过150字符"),

    PARAM_DEPTID_ISNULL("9039", "组织机构id不能为空"),

    ORDER_MERGE_ITEM_COUNT_ERROR("9040", "冲红发票各商品行的项目金额都小于0，无法冲红！"),
    
    ORDER_MERGE_DDLY_ERROR("9041", "订单合并订单来源不同"),
    
    GENERATE_READY_ORDER_DATA_ERROR("9601", "接收到的数据为空"),

    GENERATE_READY_ORDER_ORDER_NULL("9602", "没有待处理订单"),

    GENERATE_READY_ORDER_GFXX_ERROR("9603", "购方信息不全"),

    READY_ORDER_CHECK_DATA_ERROR("9604", "生成待开数据校验接口接收到的数据为空"),

    READY_ORDER_GFXX_COMPLETION_ERROR("9605", "所选订单购方信息不全，返回前端补全信息"),

    ORDER_MERGE_DATA_NULL_ERROR("9606", "订单合并接收到的数据为空"),

    ORDER_MERGE_QUOTA_ERROR("9607", "订单金额加和超过发票限额，不允许合并"),

    ORDER_MERGE_FPLX_DIFFERENT_ERROR("9608", "所选订单发票类型不一致，不允许合并"),

    ORDER_MERGE_GFXX_DIFFERENT_ERROR("9609", "所选订单购方信息不一致，不允许合并"),

    ORDER_MERGE_GFXX_NOTNULL_ERROR("9610", "购方信息不全部为空且不为空的部分购方信息相同，返回前端提示是否合并"),
    
    ORDER_MERGE_XFXX_NOTNULL_ERROR("9611", "购方信息不全部为空且不为空的部分购方信息相同，返回前端提示是否合并"),

    ORDER_MERGE_EXCEPTION_ERROR("9612", "订单合并异常"),

    READY_ORDER_SPBM_NULL_ERROR("9613", "所选订单包含商品编码有误的明细行"),

    READY_ORDER_CZLX_NULL_ERROR("9614", "生成待开数据校验接口操作类型为空"),

    READY_ORDER_UID_NULL_ERROR("9615", "用户uId为空"),

    READY_MERGE_QUOTA_NULL_ERROR("9616", "没查到企业开票限额"),

    READY_OPEN_QUOTA_NULL_ERROR("9617", "开票限额未设置"),

    READY_OPEN_DEPTID_NULL_ERROR("9618", "组织唯一标识ID为空"),

    READY_OPEN_FPZLDM_ERROR("9619", "未知的发票种类代码"),

    ERRORORDER_BACK_ORDERID_NULL_ERROR("9620", "异常订单回退成待开订单请求接收到的订单id为空"),

    READY_OPEN_XFXX_ERROR("9621", "请补充完善销方信息！"),

    READY_OPEN_ORDER_STATUS_ERROR("9622", "订单状态有误"),

    READY_OPEN_KPR_ERROR("9623", "订单开票人不能为空"),

    MERG_ORDER_NULL_ERROR("9624", "没有查到需要处理的订单"),

    MERG_ORDER_MDBH_ERROR("9625", "门店编号不同，返回前端提示是否合并"),

    MERG_ORDER_YWLX_ERROR("9626", "业务类型不同，返回前端提示是否合并"),

    MERG_ORDER_TLMXXTS_ERROR("9627", "存在同类明细项返回前端提示"),

    MERG_ORDER_ITEMCOUNT_ERROR_51("9628", "电票明细行合并后数量超过100条"),

    MERG_ORDER_ITEMCOUNT_ERROR_0_2("9629", "纸票明细行合并后数量超过1000条"),

    STATISTICS_DATA_COUNT_ERROR("9630", "数据统计类型有误"),

    INVOICE_XMXX_XMDW_ERROR("9631", "成品油的项目单位只能为升或吨"),

    ORDER_CPY_QDBZ_ERROR("9632", "成品油的清单标志只能为4"),
    
    ORDER_SPBM_CPY_ERROR("9633", "发票明细中的商品编码只能为成品油或者非成品油中的一种"),
    
    ORDER_MERGE_CPY_ERROR("9634", "成品油订单和非成品油订单不允许合并"),
    
    ORDER_MERGE_CPY_MX_OVER_8_ERROR("9635","成品油合并后明细不能超过8行"),
    
    ORDER__CPY_XMSL_NOTNULL("9636","成品油项目数量不能为空"),
    
    ORDER__CPY_XMMX_SL_OVER_8_ERROR("9637","成品油明细不能超过8行"),
    
    ORDER__SLD_NOT_CPY_ERROR("9638","开具批次中包含成品油发票，但是受理点是非成品油受理点"),
    
    READY_OPEN_ITEM_DJ_ERROR("9639", "请点击“编辑”按钮，去掉单价和数量，系统将自动按照限额拆分单据开票。"),
    
    INVOICE_BILLING_ORDERITEM_XMMC_NULL_ERROR("2000", "发票明细中的商品名称为空!"),

    INVOICE_HEAD_ERROR_009631("009631", "订单主体信息中红票合计金额不能大于或者等于零"),

    GENERATE_READY_ORDER_GFXX_NULL_ERROR("96040", "购方税号为空"),


    // 订单号开票时 20
    STRING_ORDER_DDH("9101", "订单号", 50, true, true),

    STRING_ORDER_KPLX("9102", "开票类型", 2, true, true),

    STRING_ORDER_MDH("9103", "门店号", 20, false, true),
    // 开票时是200
    STRING_ORDER_GHF_MC("9104", "购买方名称", 100, false, true),
    // 10-20之间
    STRING_ORDER_GHF_NSRSBH("9105", "购货方纳税人识别号", 20, false, true),
    // 开票200
    STRING_ORDER_GHF_DZ("9106", "购货方地址", 80, false, true),

    STRING_ORDER_GHF_DH("9107", "购货方电话", 20, false, true),

    STRING_ORDER_GHF_YHZH("9108", "购货方银行账号", 50, false, true),

    STRING_ORDER_SPMC("9109", "商品名称", 90, true, true),

    STRING_ORDER_GGXH("9110", "规格型号", 40, false, true),

    STRING_ORDER_XMDW("9111", "项目单位", 22, false, true),
    
    STRING_ORDER_XMSL("9112", "项目数量", 20, false, false),
    
    STRING_ORDER_XMDJ("9113", "项目单价", 20, false, false),

    STRING_ORDER_XMJE("9114", "项目金额", 20, true, true),

    STRING_ORDER_SPBM("9115", "商品编码", 19, false, false),

    STRING_ORDER_SL("9116", "税率", 10, false, true),

    STRING_ORDER_BZ("9117", "备注", 200, false, true),

    STRING_ORDER_KHH("9118", "购买方开户行", 200, false, true),

    STRING_ORDER_SPBM_CHINESE("9119", "商品编码非纯数字"),
    
    
    //------------------------------以下为对外开票接口错误  start-----------------------------------------------
    /**
     * 发票开具校验项目
     */

    STRING_FPKJ_FPQQLSH("9201", "发票请求流水号", 43, true, true),

    STRING_FPKJ_DSPTBM("9202", "电商平台编码", 200, false, false),

    STRING_FPKJ_NSRSBH("9203", "纳税人识别号", 10, 20, true, true),


//    STRING_FPKJ_NSRMC("9204", "纳税人名称", 100, true, true),

    STRING_FPKJ_DKBZ("9212", "代开标志", 1, false, true),

    STRING_FPKJ_KPXM("9205", "开票项目", 1, false, true),

//    STRING_FPKJ_XHF_MC("9206", "销货方名称", 100, true, true),

//    STRING_FPKJ_XHF_NSRSBH("9239", "销货方纳税人识别号", 10, 20, true, true),

//    STRING_FPKJ_XHF_DZ("9207", "销货方地址", 100, true, true),

//    STRING_FPKJ_XHF_DH("9208", "销货方电话", 20, true, true),

    STRING_FPKJ_XHF_YHZH("9208", "销货方银行账号", 100, true, false),

    STRING_FPKJ_GHF_MC("9209", "购货方名称", 100, true, true),

    STRING_FPKJ_GHF_NSRSBH("9209", "购货方纳税人识别号", 10, 20, true, false),

    STRING_FPKJ_GHF_DZ("9210", "购货方地址", 100, true, false),

    STRING_FPKJ_GHF_DH("9210", "购货方电话", 20, true, false),

    STRING_FPKJ_GHF_SJ("9210", "购货方手机", 20, true, false),

    STRING_FPKJ_GHF_WX("9210", "购货方微信", 20, true, false),

    STRING_FPKJ_GHF_QYLX("9211", "购货方企业类型", 2, true, true),
    // 开票是100  长度10修改长度20   再修改为16
//    STRING_FPKJ_KPY("9213", "开票员", 16, true, true),

//    STRING_FPKJ_KPLX("9214", "开票类型", 2, true, true),

    STRING_FPKJ_KPHJJE("9215", "开票合计金额", 24, true, true),

    STRING_FPKJ_DDH("9216", "订单号", 50, false, true),

//    STRING_FPKJ_BMB_BBH("9217", "编码号版本表", 20, true, true),

    STRING_FPKJ_YFP_DM("9218", "原发票代码", 12, true, true),

    STRING_FPKJ_YFP_HM("9219", "原发票号码", 8, true, true),

    STRING_FPKJ_TSCHBZ("9220", "特殊冲红标志", 1, true, true),

    STRING_FPKJ_CHYY("9221", "冲红原因", 200, true, true),

    STRING_FPKJ_NSRDZDAH("9222", "纳税人电子档案号", 20, true, false),

    STRING_FPKJ_SWJGDM("9223", "税务机构代码", 11, true, false),

    STRING_FPKJ_PYDM("9224", "票样代码", 6, true, false),

//    STRING_FPKJ_GHF_SF("9225", "购货方省份", 20, true, false),

    STRING_FPKJ_GHF_YX("9226", "购货方邮箱", 50, true, false),
    //长度10修改为20 再16
    STRING_FPKJ_SKY("9227", "收款员", 16, true, false),
    //长度10修改为20 再16
    STRING_FPKJ_FHY("9227", "复核员", 16, true, false),

    STRING_FPKJ_QDBZ("9227", "清单标志", 1, true, true),

    STRING_FPKJ_QDXMMC("9227", "清单项目名称", 200, true, true),

    STRING_FPKJ_BZ("9228", "发票备注", 200, true, false),

    STRING_FPKJ_HY_MC("9229", "行业名称", 100, true, false),

    STRING_FPKJ_HY_DM("9230", "行业代码", 100, true, false),

    STRING_FPKJ_HJBHSJE("9231", "合计不含税金额", 24, true, true),

    STRING_FPKJ_HJSE("9231", "合计税额", 24, true, true),

    STRING_FPKJ_SLD("9232", "受理点", 10, true, true),

    STRING_FPKJ_HZXXBBH("9234", "红字信息表编号", 50, false, true),

    STRING_FPKJ_VALIDATE_FAILED("9233", "数据校验失败"),
    
    STRING_FPKJ_THDH("9234", "退货单号", 50, true, true),
    
    STRING_FPKJ_NSRSBH_JG("9235", "机关纳税人识别号", 10, 20, false, true),
    
    
    /**
     * 专票表格导入校验
     */
    SPECIAL_INVOICE_IMPORT_SQDWYBH("9309", "专票表格导入申请单唯一编号", 24, true, true),
    
    SPECIAL_INVOICE_IMPORT_SQLX("9309", "专票表格导入申请单类型", 1, true, true),
    
    SPECIAL_INVOICE_IMPORT_SQLX_ERROR("9309", "专票表格导入申请单类型(申请单类型0:正常;1:成品油-销售数量变更2:成品油-销售金额变更;3成品油-其他)"),
    
    SPECIAL_INVOICE_IMPORT_SQYY("9309", "专票表格导入申请单原因", 10, true, true),
    
    SPECIAL_INVOICE_IMPORT_SQYY_ERROR("9309", "专票表格导入申请单原因不合法,只能为:1100000000,1010000000,0000000100"),
    
    SPECIAL_INVOICE_IMPORT_XMMC("9309", "商品名称", 200, true, true),
    
    SPECIAL_INVOICE_IMPORT_XMMC1("9309", "商品名称", 200, true, false),
    
    SPECIAL_INVOICE_IMPORT_XMMC_ERROR("9309", "成品油专用发票不允许有“详见对应正数发票及清单”明细"),
    
    SPECIAL_INVOICE_IMPORT_XMMC_ERROR1("9309", "商品名称为“详见对应正数发票及清单”，只能存在一条商品"),
    
    SPECIAL_INVOICE_IMPORT_XMMC_ERROR2("9309", "蓝票为清单票,商品只能存在一条,并且商品名称为“详见对应正数发票及清单”"),
    
    SPECIAL_INVOICE_IMPORT_XMDJ("9309", "项目单价", 24, true, false),
    
    SPECIAL_INVOICE_IMPORT_XMDJ_ERROR("9309", "成品油销售金额变更，单价必须为空"),
    
    SPECIAL_INVOICE_IMPORT_XMDJ_ERROR1("9309", "单价必须为正数且至多保留8位小数"),
    
    SPECIAL_INVOICE_IMPORT_XMDJ_ERROR2("9309", "单价不能为空"),
    
    SPECIAL_INVOICE_IMPORT_XMDJ_ERROR3("9309", "单价必须为空"),
    
    SPECIAL_INVOICE_IMPORT_XMSL("9309", "项目数量", 24, true, false),
    
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR("9309", "成品油销售金额变更，数量必须为空"),
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR1("9309", "数量必须为负数且至多保留8位小数"),
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR2("9309", "成品油销售数量变更，数量不能为空"),
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR3("9309", "数量不能为空"),
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR4("9309", "数量必须为空"),
    SPECIAL_INVOICE_IMPORT_XMSL_ERROR5("9309", "单价和数量必须同时存在"),
    
    SPECIAL_INVOICE_IMPORT_XMJE("9309", "项目金额", 24, true, true),
    
    SPECIAL_INVOICE_IMPORT_XMJE1("9309", "项目金额", 24, true, false),
    
    SPECIAL_INVOICE_IMPORT_XMJE_ERROR1("9309", "金额必须为负数且至多保留两位小数"),
    
    SPECIAL_INVOICE_IMPORT_SL("9309", "税率", 8, true, false),
    
    SPECIAL_INVOICE_IMPORT_SL_ERROR("9309", "税率必须为百分数或空"),
    
    SPECIAL_INVOICE_IMPORT_SL_ERROR1("9309", "税率必须小于1"),
    
    SPECIAL_INVOICE_IMPORT_SPBM("9309", "商品编码", 19, true, false),
    
    SPECIAL_INVOICE_IMPORT_YHZCBS("9309", "优惠政策标识", 2, true, false),
    
    SPECIAL_INVOICE_IMPORT_YHZCBS_ERROR("9309", "享受优惠政策必须为“是”或“否”"),
    
    SPECIAL_INVOICE_IMPORT_ZZSTSGL("9309", "增值税特殊管理", 50, true, false),
    SPECIAL_INVOICE_IMPORT_ZZSTSGL_ERROR("9309", "优惠政策类型必须为“免税”或“不征税”"),
    SPECIAL_INVOICE_IMPORT_ZZSTSGL_ERROR1("9309", "享受优惠政策选择“是”，优惠政策类型必选"),
    
    SPECIAL_INVOICE_IMPORT_GGXH("9309", "规格型号", 200, true, false),
    
    SPECIAL_INVOICE_IMPORT_DW("9309", "项目单位", 100, true, false),
    
    SPECIAL_INVOICE_IMPORT_DW_ERROR("9309", "成品油销售金额变更，单位必须为空"),
    
    SPECIAL_INVOICE_IMPORT_DW_ERROR1("9309", "成品油销售数量变更，单位必须为吨或升"),
    
    SPECIAL_INVOICE_IMPORT_SE("9309", "税额", 24, true, true),
    
    SPECIAL_INVOICE_IMPORT_SE1("9309", "税额", 24, true, false),
    
    SPECIAL_INVOICE_IMPORT_SE_ERROR("9309", "零税率，税额必须为0"),
    SPECIAL_INVOICE_IMPORT_SE_ERROR1("9309", "税额必须为负数且至多保留两位小数"),
    
    SPECIAL_INVOICE_IMPORT_XHFMC("9309", "销方名称", 100, true, true),
    
    SPECIAL_INVOICE_IMPORT_GHFMC("9309", "购方名称", 100, true, false),
    
    SPECIAL_INVOICE_IMPORT_XHFSH("9309", "销方税号", 20, true, true),

    SPECIAL_INVOICE_IMPORT_YLPRQ("9309", "原蓝票日期", 20, true, true),


    SPECIAL_INVOICE_IMPORT_GHFSH("9309", "购方税号", 20, true, false),
    
    SPECIAL_INVOICE_IMPORT_YFPDM("9309", "原发票代码", 10, 12, true, true),
    SPECIAL_INVOICE_IMPORT_YFPDM_ERROR("9309", "发票代码必须为数字"),
    
    SPECIAL_INVOICE_IMPORT_YFPHM("9309", "原发票号码", 8, 8, true, true),
    SPECIAL_INVOICE_IMPORT_YFPHM_ERROR("9309", "发票号码必须为数字"),
    
    
    SPECIAL_INVOICE_IMPORT_VALIDATE_CPYZYFPLX_ERROR("9309", "申请单号下成品油专用发票类型不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_SQYY_ERROR("9309", "申请单号下申请类型(购方已抵扣,购方未抵扣,销方申请)不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_XFMC_ERROR("9309", "申请单号下销方名称不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_XFSH_ERROR("9309", "申请单号下销方税号不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_GFMC_ERROR("9309", "申请单号下购方名称不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_GFSH_ERROR("9309", "申请单号下购方税号不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_YFPDM_ERROR("9309", "申请单号下原发票代码不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_YFPHM_ERROR("9309", "申请单号下原发票号码不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ITEM_ERROR("9309", "商品明细大于8行，请以“详见对应正数发票及清单”方式冲红"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_BH_ERROR("9309", "生成原发票申请单编号为空，请稍后重试"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR("9309", "原发票可冲红余额不足"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR1("9309", "原发票信息获取失败，请稍后重试"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR2("9309", "原发票信息不存在，若按商品冲红请补全后续信息"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR3("9309", "原发票信息与导入红字申请单信息合并失败，请稍后重试"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR4("9309", "表格数据为空,请填写表格数据"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR5("9309", "销方申请,填写原发票代码号码后,需要保证后续明细数据都为空"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR6("9309", "销方申请,填写购方名称和原蓝票购方名称不一致"),
    SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR7("9309", "购方申请,填写购方税号和原蓝票购方税号不一致"),
    
    
    STRING_FPKJMX_LSLBS("9309", "零税率标识", 1, true, false),
    
    STRING_FPKJMX_XMXH("9301", "项目序号", 5, true, false),
    
    
    STRING_FPKJMX_FPHXZ("9302", "发票行性质", 1, true, true),
    STRING_FPKJMX_HSBZ("9309", "含税标志", 1, true, true),
    STRING_FPKJMX_ZXBM("9307", "自行编码", 20, true, false),
    
    
    /**
     * 对外接口错误代码
     */
    CHECK_ISS7PRI_107003("107003", "批次信息中订单请求批次号", 1, 40, true, true),
    
    CHECK_ISS7PRI_107005("107005", "批次信息中纳税人识别号", 15, 20, true, true),
    
    CHECK_ISS7PRI_107006("107006", "批次信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    
    CHECK_ISS7PRI_107007("107007", "批次信息中受理点ID", 0, 8, true, false),
    
    CHECK_ISS7PRI_107009("107009", "批次信息中受理点或开票终端", 0, 20, true, false),
    
    CHECK_ISS7PRI_107010("107010", "批次信息中发票类型", 1, 3, true, true),
    
    CHECK_ISS7PRI_107011("107011", "批次信息中发票类型暂时只支持1:纸质发票,2:电子发票"),
    
    CHECK_ISS7PRI_107012("107012", "批次信息中发票类别", 1, 2, true, true),
    
    CHECK_ISS7PRI_107013("107013", "批次信息中发票类型代码错误"),
    
    CHECK_ISS7PRI_107002("107002", "批次信息中受理点ID和开票机号必须同时存在"),
    
    CHECK_ISS7PRI_107015("107015", "批次信息中开票方式", 0, 2, true, false),
    
    CHECK_ISS7PRI_107008("107008", "批次信息中是否成品油", 0, 1, true, false),
    
    CHECK_ISS7PRI_107163("107163", "纳税人识别号需要全部大写"),
    
    CHECK_ISS7PRI_107164("107164", "纳税人识别号不能包含空格"),
    
    CHECK_ISS7PRI_107165("107165", "批次信息中开票方式只能为0,1,2,3(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票)"),
    
    CHECK_ISS7PRI_107166("107166", "批次信息中是否成品油(0:非成品油;1:成品油),默认为0"),
    
    /**
     * 订单主体信息校验
     */
    CHECK_ISS7PRI_107014("107014", "订单主体信息中订单请求唯一流水号", 1, 40, true, true),
    CHECK_ISS7PRI_107014CC("107014", "订单主体信息中订单请求唯一流水号", 1, 40, true, false),
    
    CHECK_ISS7PRI_107016("107016", "订单主体信息中纳税人识别号", 15, 20, true, true),
    
    CHECK_ISS7PRI_107017("107017", "订单主体信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    
    CHECK_ISS7PRI_107018("107018", "订单主体信息中纳税人名称", 1, 100, 80, true, true),
    
    CHECK_ISS7PRI_107020("107020", "订单主体信息中开票类型", 1, 1, true, true),
    
    CHECK_ISS7PRI_107021("107021", "订单主体信息中开票类型只能为0和1：0蓝字发票；1红字发票"),
    
    CHECK_ISS7PRI_107022("107022", "订单主体信息中销货方纳税人识别号", 15, 20, true, true),
    
    CHECK_ISS7PRI_107019("107019", "订单主体信息中发票种类代码", 1, 2, true, true),
    
    CHECK_ISS7PRI_107004("107004", "订单主体信息中发票种类代码只能为0:专票;2:普票;41:卷票;51:电子票"),
    
    CHECK_ISS7PRI_107024("107024", "订单主体信息中销货方名称", 1, 100, 80, true, true),
    
    CHECK_ISS7PRI_107026("107026", "订单主体信息中销货方地址", 1, 100, true, true),
    
    CHECK_ISS7PRI_107028("107028", "订单主体信息中销货方电话", 1, 20, true, true),
    
    CHECK_ISS7PRI_107029("107029", "订单主体信息中销货方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107023("107023", "订单主体信息中购货方纳税人识别号需要全部大写"),
    
    CHECK_ISS7PRI_107025("107025", "订单主体信息中购货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),

    CHECK_ISS7PRI_107027("107027", "订单主体信息中购货方识别号", 15, 20, true, true),

    CHECK_ISS7PRI_107031("107031", "订单主体信息中购货方识别号", 15, 20, true, false),

    CHECK_ISS7PRI_107032("107032", "订单主体信息中购货方名称", 1, 100, true, true),

//    CHECK_ISS7PRI_107033("107033", "购货方名称长度有误,最大长度100位"),

    CHECK_ISS7PRI_107034("107034", "订单主体信息中购货方地址", 0, 100, true, false),

    CHECK_ISS7PRI_107035("107035", "订单主体信息中购买方企业类型", 2, 2, true, true),
    
    CHECK_ISS7PRI_107036("107036", "订单主体信息中购买方企业类型只能为:01企业，02机关事业单位，03个人，04其他"),

    CHECK_ISS7PRI_107037("107037", "订单主体信息中购买方省份", 0, 20, true, false),

    CHECK_ISS7PRI_107038("107038", "订单主体信息中购买方固定电话", 0, 20, true, false),
    
    CHECK_ISS7PRI_107040("107040", "订单主体信息中购买方手机", 0, 20, true, false),
    
    CHECK_ISS7PRI_107042("107042", "订单主体信息中购买方邮箱", 0, 50, true, false),
    
    CHECK_ISS7PRI_107043("107043", "订单主体信息中购买方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107039("107039", "订单主体信息中购买方银行帐号", 0, 30, true, false),
    
    CHECK_ISS7PRI_107044("107044", "订单主体信息中开票人", 1, 16, true, true),
    
    CHECK_ISS7PRI_107044CC("107044", "订单主体信息中开票人", 1, 16, 20, true, false),
    
    CHECK_ISS7PRI_107046("107046", "订单主体信息中收款人", 0, 16, 16, true, false),
    
    CHECK_ISS7PRI_107048("107048", "订单主体信息中复核人", 0, 16, 16, true, false),
    
    CHECK_ISS7PRI_107049("107049", "订单主体信息中红票的原发票代码", 10, 12, true, true),
    
    CHECK_ISS7PRI_107050("107050", "订单主体信息中红票的原发票号码", 8, 8, true, true),
    
    CHECK_ISS7PRI_107052("107052", "订单主体信息中冲红原因", 0, 200, true, false),
    
    CHECK_ISS7PRI_107053("107053", "订单主体信息中红票特殊冲红标志", 1, 1, true, true),
    
    CHECK_ISS7PRI_107047("107047", "订单主体信息中开票类型为蓝票时,不能有原发票代码、号码"),
    
    CHECK_ISS7PRI_107054("107054", "订单主体信息中红票特殊冲红标志只能为0和1：0为正常冲红,1为特殊冲红"),
    
    CHECK_ISS7PRI_107055("107055", "订单明细信息中发票行性质", 1, 1, true, true),
    
    CHECK_ISS7PRI_107056("107056", "订单明细信息中发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票"),
    
    CHECK_ISS7PRI_107057("107057", "订单明细信息中项目名称", 1, 90, 92, true, true),
    
    CHECK_ISS7PRI_107058("107058", "订单明细信息中项目序号", 0, 5, true, false),
    
    CHECK_ISS7PRI_107059("107059", "订单明细信息中规格型号", 0, 40, true, false),
    
    CHECK_ISS7PRI_107060("107060", "订单明细信息中项目单位", 0, 20, 14, true, false),
    
    CHECK_ISS7PRI_107051("107051", "订单明细信息中项目数量", 0, 20, 16, true, false),
    
    CHECK_ISS7PRI_107061("107061", "订单明细信息中扣除额", 0, 20, true, false),
    
    CHECK_ISS7PRI_107062("107062", "订单明细信息中项目金额须为2位小数"),
    
    CHECK_ISS7PRI_107063("107063", "订单明细信息中自行编码", 0, 16, 20, true, false),
    
    CHECK_ISS7PRI_107064("107064", "订单明细信息中含税标志", 1, 1, true, true),
    
    CHECK_ISS7PRI_107065("107065", "订单明细信息中含税标志只能为0和1：0表示都不含税,1表示都含税"),
    
    CHECK_ISS7PRI_107066("107066", "订单主体信息中价税合计不能为0且保证小数点后两位小数"),
    
    CHECK_ISS7PRI_107067("107067", "订单主体信息中订单号", 0, 50, true, false),
    
    CHECK_ISS7PRI_107068("107068", "订单主体信息中订单时间", 0, 30, true, false),
    
    CHECK_ISS7PRI_107069("107069", "订单主体信息中退货单号", 0, 50, true, false),
    
    CHECK_ISS7PRI_107070("107070", "订单主体信息中退货单号", 0, 50, true, true),
    
    CHECK_ISS7PRI_107071("107071", "订单明细信息中税率", 0, 8, 12, true, false),
    
    CHECK_ISS7PRI_107080("107080", "订单主体信息中蓝票价税合计必须大于0且保证小数点后两位小数"),
    
    CHECK_ISS7PRI_107081("107081", "订单明细信息中项目金额不能为0"),
    
    CHECK_ISS7PRI_107082("107082", "发票批次与订单主体信息中的纳税人识别号不一致"),
    
    CHECK_ISS7PRI_107083("107083", "订单主体信息中红票价税合计必须小于0"),
    
    CHECK_ISS7PRI_107084("107084", "订单明细信息中折扣行项目数量必须为空"),
    
    CHECK_ISS7PRI_107085("107085", "订单明细信息中折扣行项目规格型号必须为空"),
    
    CHECK_ISS7PRI_107086("107086", "订单明细信息中折扣行项目单位必须为空"),

//    CHECK_ISS7PRI_107087("107087", "正常冲红，红字发票纳税人识别号需与对应蓝票相一致"),
//    CHECK_ISS7PRI_107088("107088", "红票不允许重复冲红"),
//    CHECK_ISS7PRI_107089("107089", "已经作废的发票不允许冲红"),
//    CHECK_ISS7PRI_107090("107090", "原发票代码长度有误,最大长度为12位"),
//    CHECK_ISS7PRI_107091("107091", "原发票号码长度有误,最大长度为8位"),
//    CHECK_ISS7PRI_107093("107093", "发票请求流水号不满足发票请求批次号加00X"),
//    CHECK_ISS7PRI_107095("107095", "红票开具冲红原因长度不能超过200"),
//    CHECK_ISS7PRI_107096("107096", "红票开具时退货单号不能为空"),
    
    CHECK_ISS7PRI_107097("107097", "订单主体信息中编码表版本号", 1, 10, 20, true, false),
    
    CHECK_ISS7PRI_107098("107098", "订单主体信息中清单标志", 1, 1, true, true),
    
    CHECK_ISS7PRI_107099("107099", "订单主体信息中清单标志为1或3时,清单发票项目名称不能为空"),
    
    CHECK_ISS7PRI_107100("107100", "订单明细信息中商品编码", 1, 19, true, false),
    
    CHECK_ISS7PRI_107101("107101", "订单明细信息中商品编码必须为19位数字"),
    
    CHECK_ISS7PRI_107102("107102", "订单明细信息中优惠政策标识只能为0或1,0:不使用,1:使用"),

    CHECK_ISS7PRI_107103("107103", "订单明细信息中优惠政策标识", 1, 1, true, true),

    CHECK_ISS7PRI_107104("107104", "订单明细信息中优惠政策标识为1时,增值税特殊管理不能为空"),

    CHECK_ISS7PRI_107105("107105", "订单明细信息中增值税特殊管理", 0, 50, true, false),

//    CHECK_ISS7PRI_107094("107094", "订单明细信息中增值税特殊管理", 0, 50, true, true),

    CHECK_ISS7PRI_107106("107106", "订单明细信息中优惠政策标识为0时,增值税特殊管理须为空"),

//    CHECK_ISS7PRI_107107("107107", "增值税特殊管理内容为'不征税/免税/出口零税',与商品行税率不一致"),

    CHECK_ISS7PRI_107108("107108", "订单明细信息中增值税特殊管理内容为'按5%简易征收',与商品行税率不一致"),

    CHECK_ISS7PRI_107109("107109", "订单明细信息中增值税特殊管理内容为'按3%简易征收',与商品行税率不一致"),

    CHECK_ISS7PRI_107110("107110", "订单明细信息中增值税特殊管理内容为'简易征收',与商品行税率不一致"),

    CHECK_ISS7PRI_107111("107111", "订单明细信息中增值税特殊管理内容为'按5%简易征收减按1.5%计征',与商品行税率不一致"),
    
    CHECK_ISS7PRI_107112("107112", "订单明细信息中零税率标识非空, 但商品税率不为零;请保持零税率标识与商品税率逻辑一致!"),
    
    CHECK_ISS7PRI_107113("107113", "订单明细信息中零税率标识为空, 但商品税率为零;请保持零税率标识与商品税率逻辑一致!"),
    
    CHECK_ISS7PRI_107114("107114", "订单明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';请保持零税率标识与增值税特殊管理逻辑一致!"),

//    CHECK_ISS7PRI_107115("107115", "购货方企业类型为'01:企业'时,购货方识别号不得为空!"),
//    CHECK_ISS7PRI_107116("107116", "购货方识别号不符合税务局规则, 请填写真实准确的识别号!"),
    
    CHECK_ISS7PRI_107117("107117", "订单明细信息中税率不能为13%!"),
    
    CHECK_ISS7PRI_107118("107118", "订单明细信息中专票税率不能为0%!"),
    
    CHECK_ISS7PRI_107119("107119", "订单明细信息中税率为0%时,不允许填写扣除额!"),

//    CHECK_ISS7PRI_107121("107121", "发票请求流水号:{} 第{}行 根据商品编码查询商品简称出错 "),
    
    CHECK_ISS7PRI_107122("107122", "订单明细信息中成品油项目单位必填且必须为'升'或'吨'!"),
    
    CHECK_ISS7PRI_107123("107123", "商品编码不合法,成品油只能使用成品油商品编码!"),
    
    CHECK_ISS7PRI_107124("107124", "订单主体信息中清单发票项目名称", 0, 180, 92, true, false),
    
    CHECK_ISS7PRI_107125("107125", "订单主体信息中清单标志只能为0:普通发票,1:普通发票（清单）,2:收购发票,3:收购发票（清单）,4:成品油发票"),
    
    CHECK_ISS7PRI_107126("107126", "专票冲红发票代码号码与信息表数据不一致"),
    
    CHECK_ISS7PRI_107127("107127", "专票冲红时备注必填"),
    
    CHECK_ISS7PRI_107128("107128", "订单主体信息中备注", 0, 200, true, false),
    
    CHECK_ISS7PRI_107129("107129", "专票冲红备注不满足格式要求，请核对"),
    
    CHECK_ISS7PRI_107130("107130", "单个批次最多支持999张发票开具"),
//    CHECK_ISS7PRI_107131("107131", "优惠政策标识不合法，只能为0或1"),
    
    CHECK_ISS7PRI_107132("107132", "订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择\"0或1或2\"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的\"出口零税/免税/不征税"),
    
    CHECK_ISS7PRI_107131("107131", "订单明细信息中含税标志为0时,税额不能为空"),
    
    CHECK_ISS7PRI_107133("107133", "订单明细信息中税额须为2位小数"),
    
    CHECK_ISS7PRI_107154("107154", "订单明细信息中扣除额须为2位小数"),
    
    CHECK_ISS7PRI_107137("107137", "订单明细信息中发票行性质为清单红票时,税额不能为空"),
    
    CHECK_ISS7PRI_107134("107134", "订单明细信息中税额", 0, 25, 12, true, false),
    
    CHECK_ISS7PRI_107135("107135", "订单主体信息中合计不含税金额不为0时，小数点位数须为2位小数"),
    
    CHECK_ISS7PRI_107136("107136", "订单主体信息中合计税额不为0时，小数点位数须为2位小数"),
    
    CHECK_ISS7PRI_107138("107138", "订单明细信息中零税率标识非空时，只允许传0、1、2、3"),
    
    CHECK_ISS7PRI_107139("107139", "订单明细信息中含税标志为0，税额必填"),
    
    CHECK_ISS7PRI_107140("107140", "订单明细信息中零税率标识为3（普通零税）, 则:YHZCBS填0,ZZSTSGL填空"),
    
    CHECK_ISS7PRI_107141("107141", "订单主体信息中价税合计", 1, 20, 14, true, true),

//    CHECK_ISS7PRI_107047("107047", "订单主体信息中价税合计不能为0或者0.00"),
    
    CHECK_ISS7PRI_107142("107142", "订单主体信息中合计不含税金额", 1, 20, 13, true, true),
    
    CHECK_ISS7PRI_107143("107143", "订单主体信息中合计税额", 1, 20, 12, true, true),
    
    CHECK_ISS7PRI_107144("107144", "订单主体信息中项目单价不能为空且不能为0"),
    
    CHECK_ISS7PRI_107145("107145", "订单明细信息中项目金额", 1, 20, 12, true, true),
    
    CHECK_ISS7PRI_107146("107146", "订单明细信息中税率不能为空"),
    
    CHECK_ISS7PRI_107147("107147", "订单明细信息中清单红字发票明细行数限制为1行"),
    
    CHECK_ISS7PRI_107148("107148", "订单明细信息中发票行性质为6，发票开票类型需为红字发票"),
    
    CHECK_ISS7PRI_107149("107149", "订单明细信息中项目单价", 0, 20, 16, true, false),
    
    CHECK_ISS7PRI_107150("107150", "订单明细信息中清单红字发票规格型号、计量单位、项目数量、项目单价填充为空"),
    
    CHECK_ISS7PRI_107151("107151", "订单明细信息中项目数量不能为空且不能为0"),
    
    CHECK_ISS7PRI_107152("107152", "订单明细信息中项目单价必须为正数且小数点后数值不能大于8位"),
    
    CHECK_ISS7PRI_107153("107153", "订单明细信息中项目数量小数点后数值不能大于8位"),

//    CHECK_ISS7PRI_107152("107152", "红字发票项目数量为负数"),
//    CHECK_ISS7PRI_107153("107153", "清单红字发票对应的蓝字发票为多种税率，税率填充为空，单一税率填写蓝票对应税率"),
//    CHECK_ISS7PRI_107154("107154", "专票冲红备注对应的信息表编号不存在"),
//    CHECK_ISS7PRI_107155("107155", "专票冲红已抵扣传入原发票代码号码应为空"),
//    CHECK_ISS7PRI_107156("107156", "清单红字发票对应的蓝字发票为多种商品编码，商品编码填充为空，单一商品编码填写蓝票对应商品编码"),
    
    CHECK_ISS7PRI_107157("107157", "订单明细信息中收购发票商品的税率和税额不允许为空，且都必须为0"),
    
    CHECK_ISS7PRI_107158("107158", "订单明细信息中可开具农产品收购发票的票种：增普票，电子票"),

//    CHECK_ISS7PRI_107159("107159", "开具农产品收购发票红票时，对应蓝字发票必须为农产品收购发票"),
//    CHECK_ISS7PRI_107160("107160", "发票备注1必填，0表示单价数量为空，1表示单价数量不为空"),

//    CHECK_ISS7PRI_107161("107161", "订单明细信息中备用字段1只允许传0或1"),
    
    CHECK_ISS7PRI_107162("107162", "开收购发票时，购货方纳税人识别号必须为空"),

    CHECK_ISS7PRI_107263("107263", "专票订单主体信息中销货方地址", 1, 100, true, true),

//    CHECK_ISS7PRI_107264("107264", "专票订单主体信息中销货方银行名称", 0, 100, true, true),

    CHECK_ISS7PRI_107265("107265", "专票订单主体信息中购货方地址", 1, 100, true, true),

    CHECK_ISS7PRI_107266("107266", "订单主体信息中购买方银行名称", 1, 100, true, true),

    CHECK_ISS7PRI_107267("107267", "订单主体信息中销货方地址和电话", 0, 100, true, false),

    CHECK_ISS7PRI_107268("107268", "订单主体信息中销货方银行名称和帐号", 0, 100, true, false),

    CHECK_ISS7PRI_107269("107269", "订单主体信息中购货方地址和电话", 0, 100, true, false),

    CHECK_ISS7PRI_107270("107270", "订单主体信息中购买方银行名称和帐号", 0, 100, true, false),

    CHECK_ISS7PRI_107271("107271", "专票订单主体信息中销货方地址", 1, 100, true, true),

    CHECK_ISS7PRI_107272("107272", "专票订单主体信息中销货方银行名称", 1, 70, true, true),
    
    CHECK_ISS7PRI_107273("107273", "专票订单主体信息中购货方地址", 1, 100, true, true),
    
    CHECK_ISS7PRI_107274("107274", "订单主体信息中购买方银行名称", 1, 100, true, true),
    
    CHECK_ISS7PRI_107275("107275", "订单主体信息中销货方地址", 0, 100, true, false),
    
    CHECK_ISS7PRI_107277("107277", "订单主体信息中购货方地址", 0, 100, true, false),
    
    CHECK_ISS7PRI_107278("107278", "订单主体信息中购买方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107279("107279", "订单主体信息中销货方编码", 0, 50, true, false),
    
    CHECK_ISS7PRI_107281("107281", "订单主体信息中销货方名称", 1, 100, true, false),
    
    CHECK_ISS7PRI_107282("107282", "订单主体信息中销货方地址", 1, 100, true, false),
    
    CHECK_ISS7PRI_107283("107283", "订单主体信息中销货方电话", 1, 20, true, false),
    
    CHECK_ISS7PRI_107284("107284", "订单主体信息中销货方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107285("107285", "订单主体信息中销货方银行账户", 0, 30, true, false),
    
    CHECK_ISS7PRI_107286("107286", "订单主体信息中购买方编码", 0, 50, true, false),
    
    CHECK_ISS7PRI_107287("107287", "订单主体信息中购买方纳税人识别号", 15, 20, true, false),
    
    CHECK_ISS7PRI_107288("107288", "订单主体信息中购买方名称", 0, 100, 80, true, false),
    
    CHECK_ISS7PRI_107289("107289", "订单主体信息中购买方地址", 0, 100, true, false),
    
    CHECK_ISS7PRI_107290("107290", "订单主体信息中购买方电话", 0, 20, true, false),
    
    CHECK_ISS7PRI_107291("107291", "订单主体信息中购买方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107292("107292", "订单主体信息中购买方银行银行账户", 0, 30, true, false),
    
    CHECK_ISS7PRI_107293("107293", "订单主体信息中门店号", 0, 50, true, false),
    
    CHECK_ISS7PRI_107294("107294", "订单主体信息中业务类型", 0, 50, true, false),
    
    CHECK_ISS7PRI_107295("107295", "订单主体信息中提取码", 0, 50, true, false),
    
    CHECK_ISS7PRI_107296("107296", "订单主体信息中开票方式为扫码开票时,提取码不能为空"),
    
    CHECK_ISS7PRI_107297("107297", "订单主体信息中差额征税发票数据差额不合法,必须保留小数点后两位"),
    
    CHECK_ISS7PRI_107298("107298", "订单主体信息中差额征税发票数据差额不能为空"),
    
    CHECK_ISS7PRI_107299("107299", "订单主体信息中负数发票差额征税备注需要以'差额征税。'开头"),
    
    CHECK_ISS7PRI_107300("107300", "订单主体信息中正数发票备注中差额于明细中数据不符合"),
    
    
    /**
     * 作废接口
     */
    CHECK_ISS7PRI_108001("108001", "作废请求不能为空"),
    CHECK_ISS7PRI_108002("108002", "作废批次号不能为空"),
    CHECK_ISS7PRI_108003("108003", "发票代码不能为空"),
    CHECK_ISS7PRI_108004("108004", "发票起号不能为空"),
    CHECK_ISS7PRI_108005("108005", "发票止号不能为空"),
    CHECK_ISS7PRI_108006("108006", "作废类型不能为空"),
    CHECK_ISS7PRI_108007("108007", "作废原因不能为空"),
    CHECK_ISS7PRI_108008("108008", "作废批次号长度不能超过40位"),
    CHECK_ISS7PRI_108009("108009", "发票代码长度不能超过12位"),
    CHECK_ISS7PRI_108010("108010", "发票起号长度必须为8位"),
    CHECK_ISS7PRI_108011("108011", "发票止号长度必须为8位"),
    CHECK_ISS7PRI_108012("108012", "作废类型长度必须为1位"),
    CHECK_ISS7PRI_108013("108013", "作废原因长度不能超过200位"),
    CHECK_ISS7PRI_108014("108014", "作废类型不合法，作废类型只能为0,1,2"),
    CHECK_ISS7PRI_108015("108015", "空白作废目前暂不支持"),
    CHECK_ISS7PRI_108016("108016", "不允许重复作废"),
    CHECK_ISS7PRI_108017("108017", "不能跨月作废"),
    CHECK_ISS7PRI_108018("108018", "已经冲红的蓝票不允许作废"),
    CHECK_ISS7PRI_108019("108019", "未找到要作废的发票"),
    CHECK_ISS7PRI_108028("108028", "要作废的发票代码不是数字"),
    CHECK_ISS7PRI_108029("108029", "要作废的发票起号不是数字"),
    CHECK_ISS7PRI_108030("108030", "要作废的发票止号不是数字"),
    CHECK_ISS7PRI_108031("108031", "要作废的发票起号不能大于止号"),
    CHECK_ISS7PRI_108032("108032", "目前只支持单张发票作废"),
    CHECK_ISS7PRI_108033("108033", "发票代码号码不存在"),
    CHECK_ISS7PRI_108034("108034", "纳税人识别号", 15, 20, true, true),
    CHECK_ISS7PRI_108035("108035", "纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    CHECK_ISS7PRI_108036("108036", "作废数据保存异常"),
    CHECK_ISS7PRI_108037("108037", "作废批次号不能重复"),
    CHECK_ISS7PRI_108038("108038", "当前发票已被冲红无法作废"),
    CHECK_ISS7PRI_208002("208002", "作废失败，底层返回为空"),
    CHECK_ISS7PRI_208003("208003", "作废失败，开票点url为空，或已被暂停"),
    
    
    /**********2.12.红字发票申请表上传申请***************/
    CHECK_ISS7PRI_115001("115001", "申请表上传请求数据格式有误"),
    CHECK_ISS7PRI_115002("115002", "申请表上传请求批次号", 1, 40, true, true),
    //    CHECK_ISS7PRI_115003("115003", "申请表上传请求批次号长度不合法,长度最大为40位"),
    CHECK_ISS7PRI_115004("115004", "申请表上传请求流水号", 1, 43, true, true),
    CHECK_ISS7PRI_115005("115005", "申请表上传请求流水号长度不合法,长度最大为43位"),
    CHECK_ISS7PRI_115006("115006", "申请方纳税人识别号", 15, 20, true, true),
    CHECK_ISS7PRI_115007("115007", "申请方纳税人识别号长度有误,最大长度为20位"),
    //    KPJH_FAIL_NULL("115008", "开票机号为空"),
    CHECK_ISS7PRI_115009("115009", "开票机号", 0, 20, true, false),
    CHECK_ISS7PRI_115010("115010", "发票类型", 1, 3, true, true),
    CHECK_ISS7PRI_115011("115011", "发票类型不合法"),
    CHECK_ISS7PRI_115012("115012", "发票类别", 1, 2, true, true),
    CHECK_ISS7PRI_115013("115013", "发票类别不合法,红字发票申请表上传申请发票类别只能为0,不能为1"),
    CHECK_ISS7PRI_115014("115014", "申请类别", 1, 1, true, true),
    CHECK_ISS7PRI_115015("115015", "申请类别只能为0或1"),
    CHECK_ISS7PRI_115016("115016", "信息表类型", 1, 1, true, true),
    CHECK_ISS7PRI_115017("115017", "信息表类型只能为0或1"),
    CHECK_ISS7PRI_115018("115018", "原发票代码为空"),
    CHECK_ISS7PRI_115019("115019", "原发票代码长度有误,最大长度为12位"),
    CHECK_ISS7PRI_115020("115020", "原发票号码为空"),
    CHECK_ISS7PRI_115021("115021", "原发票号码长度有误,最大长度为8位"),
    YFPKPRQ_FAIL("115022", "原发票开票日期不合法,申请类别为1时,日期必填"),
    CHECK_ISS7PRI_115023("115023", "填开时间为空"),
    CHECK_ISS7PRI_115024("115024", "填开时间不合法,格式必须是yyyyMMddHHmmss"),
    CHECK_ISS7PRI_115025("115025", "销售方纳税人识别号不合法,申请类别为1时,此项是必填"),
    CHECK_ISS7PRI_115026("115026", "销售方纳税人识别号不符合税务局规则"),
    CHECK_ISS7PRI_115027("115027", "销售方纳税人名称不合法,申请类别为1时,此项是必填"),
    CHECK_ISS7PRI_115028("115028", "销售方纳税人名称", 0, 100, true, false),
    CHECK_ISS7PRI_115029("115029", "购买方纳税人识别号不合法,申请类别为0时,此项是必填"),
    GMFNSRSBH_SW_FAIL("115030", "购买方纳税人识别号不符合税务局规则"),
    CHECK_ISS7PRI_115031("115031", "购买方纳税人名称不合法,申请类别为0时,此项是必填"),
    CHECK_ISS7PRI_115032("115032", "购买方纳税人名称", 0, 100, true, false),
    CHECK_ISS7PRI_115033("115033", "合计金额(不含税)不合法,申请类别为1时,此项是必填"),
    MXHJJE_NO_EQ_HJJE("115034", "数据关联校验不合法, 明细合计金额不等于合计金额"),
    CHECK_ISS7PRI_115035("115035", "合计税额不合法,申请类别为1时,此项是必填"),
    MXHJSE_NO_EQ_HJSE("115036", "数据关联校验不合法, 明细合计税额不等于合计税额"),
    CHECK_ISS7PRI_115037("115037", "申请说明为空"),
    CHECK_ISS7PRI_115038("115038", "申请说明填写有误, 请参考规则说明"),
    CHECK_ISS7PRI_115039("115039", "编码表版本号为空"),
    CHECK_ISS7PRI_115040("115040", "编码表版本号长度有误,最大长度为20位"),
    CHECK_ISS7PRI_115041("115041", "项目序号有误,请确认"),
    CHECK_ISS7PRI_115042("115042", "发票行性质为空"),
    CHECK_ISS7PRI_115043("115043", "商品编码为空"),
    CHECK_ISS7PRI_115044("115044", "商品编码长度有误,最大长度为19位"),
    CHECK_ISS7PRI_115045("115045", "自行编码长度有误,最大长度为16位"),
    CHECK_ISS7PRI_115046("115046", "优惠政策标识为空"),
    CHECK_ISS7PRI_115047("115047", "零税率标识不合法,空：非零税率,0:出口零税,1：免税,2：不征税 3:普通零税率"),
    CHECK_ISS7PRI_115048("115048", "增值税特殊管理", 0, 50, true, false),
    CHECK_ISS7PRI_115049("115049", "项目名称", 1, 90, true, true),
    //    CHECK_ISS7PRI_115050("115050", "项目名称长度有误,最大长度为90位"),
    CHECK_ISS7PRI_115051("115051", "规格型号", 0, 40, true, false),
    CHECK_ISS7PRI_115052("115052", "单位长度", 0, 20, true, false),
    CHECK_ISS7PRI_115053("115053", "项目数量长度有误,最大长度为20位"),
    CHECK_ISS7PRI_115054("115054", "项目单价长度有误,最大长度为20位"),
    XMJE_FAIL("115055", "项目金额不合法,需等于单价乘以数量的值"),
    CHECK_ISS7PRI_115056("115056", "项目金额不合法,且不能为0"),
    CHECK_ISS7PRI_115057("115057", "含税标志为空"),
    CHECK_ISS7PRI_115058("115058", "含税标志不合法,红字发票申请表上传申请时此项不能为1"),
    CHECK_ISS7PRI_115059("115059", "税率为空"),
    CHECK_ISS7PRI_115060("115060", "税率不合法,需精确到小数点后2位或4位"),
    CHECK_ISS7PRI_115061("115061", "税额不合法,需精确到小数点后2位"),
    CHECK_ISS7PRI_115062("115062", "合计金额HJJE不合法,必须为负数"),
    CHECK_ISS7PRI_115063("115063", "合计税额HJSE不合法,必须为负数"),
    CHECK_ISS7PRI_115064("115064", "YHZCBS为1, 且税率为0, 则LSLBS只能根据实际情况选择0或1或2中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的出口零税/免税/不征税"),
    CHECK_ISS7PRI_115065("115065", "如果税率为0,但并不属于优惠政策(即普通的零税率),则YHZCBS填0,LSLBS填3,ZZSTSGL为空"),
    CHECK_ISS7PRI_115066("115066", "如果税率不为0, 但属于优惠政策,则YHZCBS填1,LSLBS填空或不填,ZZSTSGL根据实际情况填写"),
    CHECK_ISS7PRI_115067("115067", "如果税率不为0, 且不属于优惠政策, 则YHZCBS填0,LSLBS填空或不填,ZZSTSGL不填或空"),
    CHECK_ISS7PRI_115068("115068", "如果YHZCBS为0,ZZSTSGL不填"),
    //    CHECK_ISS7PRI_115069("115069", "开票点为空"),
    CHECK_ISS7PRI_115070("115070", "开票点", 0, 8, true, false),
    CHECK_ISS7PRI_115071("115071", "申请单明细最多支持8行，超过8行需按照清单红字格式申请"),
    CHECK_ISS7PRI_115072("115072", "发票行性质有误"),
    CHECK_ISS7PRI_115073("115073", "清单红字申请单明细行数限制为1行"),
    CHECK_ISS7PRI_115074("115074", "优惠政策标识有误"),
    CHECK_ISS7PRI_115075("115075", "清单红字申请单规格型号、计量单位、项目数量、项目单价 填充为空"),
    CHECK_ISS7PRI_115076("115076", "正常冲红项目单价不能为空，且不能为0"),
    CHECK_ISS7PRI_115077("115077", "红字专票网络上传最多支持10张"),
    CHECK_ISS7PRI_115078("115078", "红字专票蓝票开票日期为格式为yyyy-MM-dd HH:mm:ss"),
    CHECK_ISS7PRI_115079("115079", "项目名称应为:详见对应正数发票及清单"),
    CHECK_ISS7PRI_115080("115080", "销货方纳税人识别号", 15, 20, true, false),
    CHECK_ISS7PRI_115081("115081", "购货方纳税人识别号", 15, 20, true, false),
    CHECK_ISS7PRI_115082("115082", "申请单商品编码非法"),
    CHECK_ISS7PRI_115083("115083", "含税标志只能为0和1：0表示都不含税,1表示都含税"),
    CHECK_ISS7PRI_115084("115084", "含税标志为0，税额必填"),
    //方格新增状态
    CHECK_ISS7PRI_060111("060111", "红字发票申请表正在处理中!"),
    CHECK_ISS7PRI_TZD0500("TZD0500", "红字发票正在上传!"),
    //方格新加状态
    CHECK_ISS7PRI_060000("060000", "申请表审核结果下载下载成功!"),
    CHECK_ISS7PRI_060112("060112", "申请表审核结果下载正在下载中!"),
    
    RED_INVOICE_CHECK_SUCCESS("010000", "校验通过"),
    
    
    /**
     * 校验红字发票下载参数
     */
    RED_INVOICEDOWNLOAD_SUCCESS("010000", "请求接收成功!"),
    RED_DOW_PCH_CF("117001", "申请表审核结果下载请求批次号重复"),
    CHECK_ISS7PRI_117002("117002", "申请表审核结果下载请求批次号为空"),
    CHECK_ISS7PRI_117003("117003", "申请表审核结果下载请求批次号长度不合法，长度最大为40位"),
    CHECK_ISS7PRI_117004("117004", "纳税人识别号为空"),
    RED_DOW_KPJH_NULL("117005", "开票机号为空"),
    CHECK_ISS7PRI_117006("117006", "发票类型为空"),
    CHECK_ISS7PRI_117007("117007", "发票类别为空"),
    CHECK_ISS7PRI_117008("117008", "红字发票申请表审核结果下载申请,发票类别不能为1"),
    RED_DOW_YQZT_NULL("117009", "逾期状态为空"),
    CHECK_ISS7PRI_117010("1170010", "填开日期起可以为空，若填写，格式须为yyyyMMdd"),
    CHECK_ISS7PRI_117011("1170011", "填开日期止可以为空，若填写，格式须为yyyyMMdd"),
    CHECK_ISS7PRI_117017("1170017", "填开日期起可以为空，若填写，格式须为yyyyMMdd"),
    CHECK_ISS7PRI_117018("1170018", "填开日期止可以为空，若填写，格式须为yyyyMMdd"),
    RED_DOW_GMFSH_FAIL("1170012", "购买方税号不合法"),
    RED_DOW_XSFSH_FAIL("1170013", "销售方税号不合法"),
    RED_DOW_XXBBH_LENGTH_FAIL("1170014", "信息表编号长度不合法, 最大为16位"),
    CHECK_ISS7PRI_117015("1170015", "信息表下载范围为空"),
    CHECK_ISS7PRI_117016("1170016", "信息表下载范围数值不合法, 只能为0或1或2"),
    CHECK_ISS7PRI_117019("1170019", "冲红申请单同步填开日期起填开日期止区间不能大于5天"),
    CHECK_ISS7PRI_117020("1170020", "冲红申请单同步填开日期起不能大于填开日期止"),
    RED_DOW_SJKCX_FAIL("502001", "数据库查询异常"),
    CHECK_ISS7PRI_502002("502002", "红字申请单信息表分页页号", 1, 5, true, true),
    CHECK_ISS7PRI_502003("502003", "红字申请单信息表分页个数", 1, 5, true, true),
    CHECK_ISS7PRI_502004("502004", "红字申请单信息表分页页号必须为数字,且不能为0"),
    CHECK_ISS7PRI_502005("502005", "红字申请单信息表分页个数必须为数字,且不能小于等于0,不能大于10"),
    CHECK_ISS7PRI_502006("502006", "红字申请单信息表编号", 16, 16, true, false),
    CHECK_ISS7PRI_502007("502007", "红字申请单下载销方税号", 15, 20, true, false),
    CHECK_ISS7PRI_502008("502008", "红字申请单下载购方税号", 15, 20, true, false),
    CHECK_ISS7PRI_502009("502009", "红字申请单下载税号", 15, 20, true, true),
    CHECK_ISS7PRI_502010("502010", "申请表审核结果下载请求批次号", 1, 40, true, true),
    
    /**
     * 合法性校验
     */
    INVOICE_HJJE_ZERO_ERROR("009012", "订单主体信息中合计金额小于等于零"),

    /**
     * 价税分离
     */
    PRICE_TAX_SEPARATION_SUCCESS("9350", "价税分离后的订单信息"),
    
    PRICE_TAX_SEPARATION_NE_KPHJJE("109351", "价税分离后开票合计金额与订单开票合计金额不相等"),

    PRICE_TAX_SEPARATION_NE_HJBHSJE("9352", "价税分离后合计不含税金额与订单合计不含税金额不相等"),

    PRICE_TAX_SEPARATION_NE_HJSE("9353", "价税分离后合计税额与订单合计税额不相等"),
    
    PRICE_TAX_SEPARATION_NE_ITEMS("9354", "价税分离明细行数据为空"),
    
    PRICE_TAX_SEPARATION_NE_ITEMS_SE("9355", "价税分离明细行数据税额误差大于6分钱"),
    
    PRICE_TAX_SEPARATION_NE_ITEMS_SE_WC("9356", "价税分离明细行数据税额误差调整值大于6分钱"),
    
    PRICE_TAX_SEPARATION_NE_ITEMS_ZSE_WC("9999", "价税分离明细行数据税额总误差大于1.27元,建议拆分开票"),

    /**
     * 发票拆分错误
     */

    UNKONW_SPLIT_TYPE("9400", "未知拆分类型"),

    ORDER_NOT_EXIST("9401", "订单不存在"),

    ORDER_ITEM_ILLEGALITY("9403", "订单项目非法"),

    ORDER_CASH_DISCOUNT_ERROR("9402", "订单和折扣行分离"),
    
    ORDER_SPLIT_SIZE_OVERLIMIT("9404", "订单拆分超过限制拆分条数:100"),

    ORDER_SPLIT_DJ_ERROR("9405", "折扣行单价大于商品行单价"),

    ORDER_SPLIT_SL_ERROR("9406", "折扣行数量大于商品行数量"),

    ORDER_SPLIT_OVERLIMIT_ERROR("9407", "订单超限额拆分异常"),
    
    ORDER_SPLIT_ILLEGAL_ERROR("9408", "订单数量小于等于1，无法按数量拆分"),
    
    ORDER_SPLIT_JE_ERROR("9408", "拆分金额大于单据金额"),
    
    ORDER_SPLIT_MXSL_ERROR("9409","订单数量拆分，明细行数量不为1，不允许拆分"),
    
    ORDER_SPLIT_JE_ILLEGALITY("9410","订单数量拆分，明细行数量不为1，不允许拆分"),
    
    ORDER_SPLIT_DJ_ILLEGALITY("9411","为保证单价不变，数量为整数，无法执行此次拆分，请加大输入的金额"),
    
    ORDER_IMPORT_BZ_DEAL_ERROR("9412", "订单导入异常,备注处理失败"),
    
    ORDER_IMPORT_CHECK_VALID("9413", "excel导入订单具体行数校验失败"),
    
    ORDER_SPLIT_JE_SL_ERROR("9414", "拆分金额只有一个，并且拆分金额等于总金额"),
    
    ORDER_SPLIT_ZSL_ERROR("9415", "拆分数量大于单据总数量"),
    
    ORDER_SPLIT_MODE_ERROR("9416", "拆分模式错误"),
    
    ORDER_SPLIT_ERROR1("9417", "当前数据已被拆分,请勿重复拆分"),
    
    /**
     * 发票冲红
     */
    INVOICE_RUSH_RED_NULL("9500", "接收冲红发票信息为空"),

//    INVOICE_RUSH_RED_FAILED("9501", "冲红发票失败"),

//    INVOICE_RUSH_RED_ORDER_FAILED("9502", "冲红订单insert数据库失败"),

//    INVOICE_RUSH_RED_ORDER_ITEM_FAILED("9503", "冲红订单明细insert数据库失败"),

//    INVOICE_RUSH_RED_PROCESS_FAILED("9504", "冲红处理表信息insert数据库失败"),

//    INVOICE_RUSH_RED_PROCESS_EXT_FAILED("9505", "冲红处理表扩展表insert数据库失败"),

    INVOICE_RUSH_RED_INVOICE_NULL("9506", "发票信息不存在"),

    INVOICE_RUSH_RED_INVOICE_YCH("9507", "发票已冲红"),

    INVOICE_RUSH_RED_INVOICE_YZF("9508", "发票已作废"),

//    INVOICE_RUSH_RED_INVOICE_DY("9509", "纸票只能跨月冲红"),

//    INVOICE_RUSH_RED_INVOICE_DATE("9510", "获取要冲红发票时间失败"),

//    INVOICE_RUSH_RED_INVOICE_UNKNOWN_CH("9511", "未知的冲红标志"),

//    INVOICE_RUSH_RED_INVOICE_CHZ("9512", "发票冲红中,请稍后操作"),

    INVOICE_RUSH_RED_INVOICE_ZFZ("9513", "发票作废中,不允许冲红"),

//    INVOICE_RUSH_RED_INVOICE_UNKNOWN_ZF("9514", "未知作废类型"),

//    INVOICE_RUSH_RED_INVOICE_SYKCHJE_NULL("9515", "剩余可冲红金额为空"),

    INVOICE_RUSH_RED_INVOICE_SYKCHJE_LESS("9516", "冲红金额大于剩余可冲红金额"),

    INVOICE_RUSH_RED_INVOICE_SLD("9517", "纸票必须要传受理点"),

//    ORDER_ROLLBACK_EXCEPTION("9520", "回退异常"),

//    ORDER_ROLLBACK_YMLR("9521", "页面录入没有原始订单，无法回退"),

//    ORDER_ROLLBACK_DATA_ERROR("9522", "数据有误，待开订单回退失败"),

    ORDER_ROLLBACK_DATA_EXCEPTION_ERROR("9524", "数据有误，异常订单回退失败"),

    ORDER_ROLLBACK_DATA_CF_ERROR("9525", "数据有误，拆分后订单回退失败"),
    
    ORDER_ROLLBACK_StATUS_ERROR("9523", "原始状态的订单，不支持回退"),

//    ORDER_ROLLBACK_DATA_NO_SUPERIOR("9526", "数据有误，拆分后订单回退失败"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR("9527", "订单部分开票中，无法回退"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK("9528", "订单有部分开票，无法回退"),
    
    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE("9530", "页面直接开票，不支持回退"),
    
    ORDER_ROLLBACK_DATA_ORDER_STATUS("9529", "此拆分订单的同级或同级的下级订单有处于开票状态，无法回退"),
    
    INVOICE_MAKE_OUT_ERROR("9531", "此开票类型不存在"),
    
    INVOICE_DATA_BATCH_NULL("9532", "此批次不存在"),
    
    INVOICE_TIME_PARSEEXCEPTION("9533", "时间格式输入错误！"),
    
    
    /**
     * 整体数据返回
     */
    
    HANDLE_ISSUE_202004("202004", "批量订单请求数据为空"),
    
    HANDLE_ISSUE_202008("202008", "批量订单请求批次数据为空"),
    
    HANDLE_ISSUE_202009("202009", "批量订单请求明细数据为空"),
    
    HANDLE_ISSUE_202010("202010", "校验对象必须包含税控设备类型"),
    
    
    /**
     * 自动开票
     */
    INVOICE_AUTO_NUMBER("109540", "开具发票订单数量超出限额,限额为2000"),
    
    INVOICE_AUTO_NUMBER_51("9541", "电子发票发票明细行数超过限制2000行"),
    
    INVOICE_AUTO_SEPARATION("9543", "价税分离未通过"),
    
    INVOICE_AUTO_PARAM_NULL("9544", "接收前台参数为空"),
    
    INVOICE_AUTO_PARAM_BLUE_DMHM("9545", "开票类型为蓝票时,不能有原发票代码、号码"),
    
    INVOICE_AUTO_PARAM_RED_DMHM("9546", "开票类型为红票时,必须有原发票代码、号码"),

//    INVOICE_AUTO_ACCEPT_NULL("9547", "接收研二发票数据，数据为空"),

//    INVOICE_AUTO_ACCEPT_INVOICE_SUCCESS("9548", "接收研二发票数据，接收成功"),

    INVOICE_AUTO_INVALID_KB_NULL("9549", "空白发票作废数据为空"),

    INVOICE_AUTO_INVALID_YK_NULL("9550", "已开发票作废数据为空"),

    INVOICE_AUTO_DATA_NULL("9551", "接收数据为空"),
    
    INVOICE_AUTO_DATA_INTERFACE_FAILED("9552", "内部错误，调用接口失败"),

    INVOICE_AUTO_DATA_UNKNOWN("9553", "发生未知异常,请联系管理员"),

    INVOICE_STATUS_EROR("9554", "发票处于开具中或开具完成状态"),

//    INVOICE_RESTART_FAILED("9555", "删除发票失败,请重试"),

//    INVOICE_HZXXBBH_NULL("9556", "红字增值税专用发票信息表编号为空"),

    INVOICE_AUTO_PARAM_NSRSBH("9557", "批次开票数据销方税号不一致"),

//    INVOICE_AUTO_PARAM_SLD("9558", "批次开票数据受理点不一致"),

//    INVOICE_AUTO_PARAM_KPJH("9559", "批次开票数据开票机号不一致"),
    
    INVOICE_AUTO_PARAM_KPLX("9560", "批次开票数据发票类型不一致"),
    
    INVOICE_AUTO_PARAM_FPZLDM("9561", "批次开票数据发票种类代码不一致"),
    
    INVOICE_QUERY_ERROR("9562", "批次对应的发票数据不存在"),
    
    INVOICE_SPBM_QUERY_NULL("9563", "查询税收分类编码信息错误！"),
    INVOICE_SPBM_SPMC_QUERY_NULL("9563", "查询税收分类编码商品名称不能为空！"),
    
    INVOICE_FG_SLD_NULL("9563", "方格查询受理点失败！"),
    
    //------------------------end-----------------------------
    
    /**
     * 发票作废
     */
    INVOICE_VALID_REPEAT("9601", "发票重复作废"),
    INVOICE_VALID_ERROR("9999", "发票作废数据插入作废表失败"),
    INVOICE_VALID_ERROR1("9999", "更新发票表作废标志失败"),
    INVOICE_VALID_ERROR2("9999", "发票作废代码号码不能为空"),
    INVOICE_VALID_ERROR3("9999", "发票作废代码号码与下一张发票不一致"),
    INVOICE_VALID_ERROR4("9999", "发票作废只作废当月发票"),
    INVOICE_VALID_ERROR5("9999", "发票作废只作废普票和专票"),
    
    /**
     * 订单导入
     */
    IMPORT_ORDERS_PARAM_NULL("9651", "订单传递数据为空"),
    IMPORT_ORDERS_ERROR("9652", "订单导入接口异常"),
    
    /**
     * 订单数据查询接口
     */
    GET_ORDERS_INVOICE_ERROR("9999", "订单数据查询接口异常"),
    GET_ORDERS_INVOICE_PARAM_NULL("9661", "订单数据查询传递数据为空"),
    GET_ORDERS_INVOICE_PARAM_NSRSBH("9662", "订单数据查询传递数据销方税号为空"),
    GET_ORDERS_INVOICE_PARAM_DDH("9663", "订单数据查询传递数据订单号为空"),
    GET_ORDERS_INVOICE_DATA_NULL("9664", "订单数据查询结果为空"),
    GET_ORDERS_INVOICE_DATA_ITEM_NULL("9665", "订单数据查询明细结果为空"),
    
    GET_ORDERS_INVOICE_ERROR_V3_009999("009999", "订单数据查询接口异常"),
    GET_ORDERS_INVOICE_PARAM_NULL_V3_009661("009661", "订单数据查询传递数据为空"),
    GET_ORDERS_INVOICE_PARAM_NSRSBH_V3_009662("009662", "订单数据查询传递数据销方税号为空"),
    GET_ORDERS_INVOICE_PARAM_DDH_V3_009663("009663", "订单数据查询传递数据订单号为空"),
    GET_ORDERS_INVOICE_DATA_NULL_V3_009664("009664", "订单数据查询结果为空"),
    GET_ORDERS_INVOICE_DATA_ITEM_NULL_V3_009665("009665", "订单数据查询明细结果为空"),
    GET_ORDERS_INVOICE_PARAM_DDH_OR_DDQQLSH_V3_009666("009666", "订单数据查询传递数据订单号,提取码,订单请求流水号不能同时为空"),
    INVOICE_QUERY_ERROR_V3_009562("009562", "批次对应的发票数据不存在"),
    
    INTERFACE_GETORDERANDINVOICE_STATUS_000000("000000", "开票成功"),
    INTERFACE_GETORDERANDINVOICE_STATUS_001000("001000", "订单处理成功"),
    INTERFACE_GETORDERANDINVOICE_STATUS_002000("002000", "订单开票中"),
    INTERFACE_GETORDERANDINVOICE_STATUS_003000("003000", "订单开票成功,已作废"),
    INTERFACE_GETORDERANDINVOICE_STATUS_004000("004000", "订单开票成功,已全部冲红"),
    INTERFACE_GETORDERANDINVOICE_STATUS_005000("005000", "订单开票成功,已部分冲红"),
    INTERFACE_GETORDERANDINVOICE_STATUS_001999("001999", "开票异常"),
    INTERFACE_GETORDERANDINVOICE_STATUS_002999("002999", "订单已删除"),
    
    /**
     * 合并前端提示标识
     */
    ORDER_MERGE_TS_REPEAT("9700", "合并前端提示标识"),
    ORDER_MERGE_ORDER_STATUS_ERROR("9701", "订单合并，订单状态有误"),
    ORDER_MERGE_YWLX_ERROR("9701", "订单合并:业务类型不一致,无法合并"),
    ORDER_MERGE_MDH_ERROR("9701", "订单合并:门店号不一致,无法合并"),


    /**
     * 发票接口相关状态
     */
    INVOICE_ERROR_CODE_010000("010000", "发票请求接收成功"),
    INVOICE_ERROR_CODE_020000("020000", "发票全部开具成功"),
    INVOICE_ERROR_CODE_020001("020001", "发票部分开具成功"),
    INVOICE_ERROR_CODE_020002("020002", "发票全部开具失败"),
    INVOICE_ERROR_CODE_020003("020003", "开票失败，无可用票源"),
    INVOICE_ERROR_CODE_021002("021002", "电子发票赋码失败"),
    INVOICE_ERROR_CODE_020111("020111", "发票正在开具"),
    INVOICE_ERROR_CODE_031002("031002", "生成pdf失败"),
    INVOICE_ERROR_CODE_040000("040000", "发票全部作废成功"),
    INVOICE_ERROR_CODE_040001("040001", "发票部分作废成功"),
    INVOICE_ERROR_CODE_040002("040002", "发票全部作废失败"),
    INVOICE_ERROR_CODE_040003("040003", "发票全部作废作废中"),
    INVOICE_ERROR_CODE_114004("114004", "获取电子发票版式文件,发票号码代码长度不合规"),
    INVOICE_ERROR_CODE_702001("702001", "暂不支持请求,请求批次号已存在"),
    INVOICE_ERROR_CODE_709999("709999", "多异常信息返回"),
    
    /**
     * 发票接口相关状态 V3
     */
    INVOICE_ERROR_CODE_010000_V3("010000", "发票请求接收成功"),
    INVOICE_ERROR_CODE_010001_V3("010001", "发票开具请求数据错误，订单请求批次号已存在"),
    INVOICE_ERROR_CODE_010002_V3("010002", "发票开具请求数据错误，订单请求流水号已存在"),
    INVOICE_ERROR_CODE_010003_V3("010003", "发票开具请求数据错误，订单批次号和流水号已存在"),
    INVOICE_ERROR_CODE_010004_V3("010004", "发票开具请求税号和对应secretId不一致!"),
    INVOICE_ERROR_CODE_010005_V3("010005", "发票开具请求税号对应的套餐未设置"),
    INVOICE_ERROR_CODE_010006_V3("010006", "发票开具请求税号对应的套餐余量不足"),
    INVOICE_ERROR_CODE_010007_V3("010007", "发票开具请求税号对应的套餐异常"),
    INVOICE_ERROR_CODE_010008_V3("010008", "发票开具请求税号对应的企业信息未设置"),
    INVOICE_ERROR_CODE_010009_V3("010009", "发票开具请求受理点ID不能为空"),
    INVOICE_ERROR_CODE_010010_V3("010010", "发票开具请求数据错误，订单请求流水号正在处理中,请勿重复请求"),
    INVOICE_ERROR_CODE_010011_V3("010011", "发票开具请求数据错误，动态码信息不存在"),
    INVOICE_ERROR_CODE_010012_V3("010012", "发票开具请求数据错误，二维码已使用"),
    INVOICE_ERROR_CODE_010013_V3("010013", "发票开具请求数据错误，二维码已失效"),
    INVOICE_ERROR_CODE_010014_V3("010014", "发票开具请求异常"),
    INVOICE_ERROR_CODE_020000_V3("020000", "发票全部开具成功"),
    INVOICE_ERROR_CODE_020001_V3("020001", "发票部分开具成功"),
    INVOICE_ERROR_CODE_020002_V3("020002", "发票全部开具失败"),
    INVOICE_ERROR_CODE_020111_V3("020111", "发票正在开具"),
    INVOICE_ERROR_CODE_021000_V3("021000", "发票开具成功"),
    INVOICE_ERROR_CODE_021001_V3("021001", "发票未开具"),
    INVOICE_ERROR_CODE_021002_V3("021002", "发票开具中"),
    INVOICE_ERROR_CODE_021003_V3("021003", "发票对应订单已失效"),
    INVOICE_ERROR_CODE_021999_V3("021999", "发票开具失败"),
    INVOICE_ERROR_CODE_104001_V3("104001", "发票开具结果数据获取，请求对象不能为空"),
    INVOICE_ERROR_CODE_104002_V3("104002", "发票开具结果数据获取，请求批次号不能为空"),
    INVOICE_ERROR_CODE_104003_V3("104003", "发票开具结果数据获取，请求批次号长度不匹配"),
    INVOICE_ERROR_CODE_104004_V3("104004", "发票开具结果数据获取，发票类型不能为空"),
    INVOICE_ERROR_CODE_104005_V3("104005", "发票开具结果数据获取，发票类型长度不匹配"),
    INVOICE_ERROR_CODE_104006_V3("104006", "发票开具结果数据获取, 发票类型不合法"),
    INVOICE_ERROR_CODE_104007_V3("104007", "发票开具结果数据获取, 是否返回失败数据参数只能为0或1"),
    INVOICE_ERROR_CODE_104008_V3("104008", "发票开具结果数据获取, 发票类型只能为1：纸票，暂不支持电子票"),
    INVOICE_ERROR_CODE_104009_V3("104009", "发票开具结果数据获取, 税号不合法"),
    INVOICE_ERROR_CODE_104010_V3("104010", "发票开具结果数据获取, 税号不能为空"),
    INVOICE_ERROR_CODE_204001_V3("204001", "发票开具结果数据获取，请求批次号不存在"),
    INVOICE_ERROR_CODE_204002_V3("204002", "发票开具结果获取查询成功:查询到?条信息"),
    INVOICE_ERROR_CODE_202005_V3("202005", "受理点处于停止状态,不可用"),
    INVOICE_ERROR_CODE_502001_V3("502001", "数据库查询异常"),
    INVOICE_ERROR_CODE_204003_V3("204003", "发票开具结果获取:未查询该批次号的发票信息"),
    
    
    /**
     * 二维码路由结果
     */
    
    EWM_ERROR_CODE_205998("205998", "二维码不存在"),
    EWM_ERROR_CODE_205999("205999", "短码转长码异常"),
    
    /**
     * 待开订单删除
     */
    UPDATE_ORDER_STATUS_IDS_NULL("90001", "未传输订单信息标识ID"),
    UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE("90002", "已开具或者开具中的订单不允许删除"),
    UPDATE_ORDER_STATUS_QUERY_NULL("90003", "未查到选中的订单信息"),
    
    /**
     * 发票统计
     */
    INVOICE_COUNT_NULL("10001","查询结果数据为空！"),
    INVOICE_SLD_NULL("10002","未查询到税盘！"),
    INVOICE_NSRMC_NULL("10004","未查询到纳税人识别号对应纳税人识别号名称！"),
    INVOICE_PARAM_ERROR("10005","请求参数为空！"),
    INVOICE_NSRSBH_ERROR("10006","纳税人识别号为空！"),
    INVOICE_TIMEFLAG_ERROR("10007","时间标志只能为0或者1！"),
    INVOICE_SP_NULL("10003","未找到统计的开票点对应税盘名称！"),
    /**
     * 发票冲红
     */
    RDEINVOICE_OVER("10008","冲红金额大于剩余可冲红金额！"),

    /**
     * manager 迁移错误码
     * 五位 20001开始
     */
    TAXCODE_NOTONE("20001","当前用户的税号多于一个！"),
    TAXCODE_ISNULL("20001","当前请求的税号为空！"),
    FJH_ISNULL("20001", "当前请求的分机号为空！"),
    FS_ISNULL("20001", "当前请求的份数为空！"),
    
    PUSH_ENTERPRISE_SUCCESS("0000", "发票开具成功！"),
    PUSH_ENTERPRISE_FAILURE("9999", "发票开具失败！"),
    
    
    /**
     * 全税/方格接口对接状态码
     */
    INVOICE_STAT_SUCCESS("0000", "接口请求成功"),
    INVOICE_STAT_ERROR("0001", "接口请求失败"),
    INVOICE_STAT_ERROR1("0002", "开票结果推送ofd文件流不能为空"),
    
    FG_INVOICE_VALID_ENPTY("0001", "没有作废发票数据"),
    FG_INVOICE_PRINT_EMPTY("0001", "没有打印发票数据"),
    /**
     * 方格打印完成返回结果状态
     */
    INVOICE_PRINT_SUCCESS("0000", "成功打印"),
    INVOICE_PRINT_FAIL("0005", "打印取消"),
    
    PRINT_INVOICE_0000("0000", "打印数据已提交"),
    
    /**
     * 发票打印
     */
    PRINT_INVOICE_9999("9999", "历史数据不能进行打印"),
    PRINT_INVOICE_9998("9999", "已作废发票暂不支持打印,所选发票中包含已作废发票,请重新选择,分段打印"),
    
    
    /**
     * 方格接口 注册信息获取
     */
    INVOICE_ZCM_SUCCESS("0000", "获取注册码成功"),
    INVOICE_ZCM_FAIL("9999", "获取注册码失败"),
    
    /**
     * 方格接口  红票申请单上传
     */
    GET_ORDERS_INVOICE_SPECIAL_NULL("009666", "红票申请单待上传数据为空"),
    
    GET_ORDERS_INVOICE_SPECIAL_NULL1("009667", "红票申请单对应税号未配置税控设备"),
    
    GET_ORDERS_INVOICE_SPECIAL_NULL2("009668", "红票申请单对应税号未配置税局地址"),
    
    GET_ORDERS_INVOICE_SPECIAL_NULL3("009669", "红票申请单对应税号未配置字典表地址"),
    
    GET_ORDERS_INVOICE_SPECIAL_NULL4("009670", "红票申请单对应税号配置域名找不到对应ip地址"),
    /**
     * 方格接口  税盘注册参数为空
     */
    GET_REGIST_TAXDISK_NULL("009667", "税盘注册参数为空"),
    /**
     * 方格接口  税盘信息同步参数为空
     */
    GET_UPDATE_TAXDISK_NULL("009668", "税盘同步接口参数为空"),
    /**
     * 方格接口  红票申请单待下载数据为空
     */
    GET_ORDERS_INVOICE_SPECIAL_DOWNLOAD_NULL("009669", "红票申请单待下载数据为空"),
    
    ERROR("0001", "请求失败"),
    
    /**
     * 历史数据导入接口对接状态码
     */
    INVOICE_INFO_IMPORT_SUCCESS("144000", "数据导入成功!"),
    INVOICE_INFO_IMPORT_ERROR("144999", "数据导入失败!"),
    INVOICE_INFO_IMPORT_ERROR_EXIST("144998", "历史数据已存在!"),
    INVOICE_INFO_FPDM_FPHM_ERROR_EXIST("144997", "发票代码和发票号码已存在!"),
    INVOICE_INFO_IMPORT_ERROR_NULL("144001", "订单发票信息为空!"),
    
    INVOICE_HEAD_INFO_IMPORT_ERROR_NULL("144002", "订单发票头信息为空!"),
    INVOICE_HEAD_INFO_DDQQLSH_ERROR_144004("144004", "订单请求唯一流水号", 1, 40, true, true),
    INVOICE_HEAD_INFO_NSRSBH_ERROR_144005("144005", "纳税人识别号", 15, 20, true, true),
    INVOICE_HEAD_INFO_NSRSBH_ERROR_144006("144006", "纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    INVOICE_HEAD_INFO_NSRSBH_ERROR_144007("144007", "纳税人识别号需要全部大写"),
    INVOICE_HEAD_INFO_NSRMC_ERROR_144008("144008", "纳税人名称", 1, 100, true, true),
    INVOICE_HEAD_INFO_KPLX_ERROR_144009("144009", "开票类型", 1, 1, true, true),
    INVOICE_HEAD_INFO_KPLX_ERROR_144010("144010", "开票类型只能为0和1：0蓝字发票；1红字发票"),
    INVOICE_HEAD_INFO_BMBBBH_ERROR_144011("144011", "编码表版本号", 1, 10, true, true),
    INVOICE_HEAD_INFO_XHFMC_ERROR_144012("144012", "销货方名称", 1, 100, true, true),
    INVOICE_HEAD_INFO_XHFDZ_ERROR_144013("144013", "销货方地址", 1, 85, true, true),
    INVOICE_HEAD_INFO_XHFDH_ERROR_144014("144014", "销货方电话", 1, 15, true, true),
    INVOICE_HEAD_INFO_XHFYH_ERROR_144015("144015", "销货方银行名称", 0, 100, true, true),
    INVOICE_HEAD_INFO_XHFZH_ERROR_144016("144016", "销货方银行账户", 0, 30, true, true),
    INVOICE_HEAD_INFO_GMFLX_ERROR_144017("144017", "购买方类型", 2, 2, true, true),
    INVOICE_HEAD_INFO_GMFLX_ERROR_144018("144018", "购买方类型只能为:01企业，02机关事业单位，03个人，04其他！"),
    INVOICE_HEAD_INFO_GMFBM_ERROR_144019("144019", "购买方编码", 0, 50, true, false),
    INVOICE_HEAD_INFO_GMFSBH_ERROR_144020("144020", "购买方纳税人识别号", 15, 20, true, true),
    INVOICE_HEAD_INFO_GMFSBH_ERROR_144021("144021", "购买方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法！"),
    INVOICE_HEAD_INFO_GMFSBH_ERROR_144022("144022", "购买方纳税人识别号需要全部大写！"),
    INVOICE_HEAD_INFO_GMFMC_ERROR_144023("144023", "购货方名称", 1, 100, true, true),
    INVOICE_HEAD_INFO_KPR_ERROR_144024("144024", "开票人", 1, 8, true, true),
    INVOICE_HEAD_INFO_YFPDM_ERROR_144025("144025", "红票的原发票代码", 10, 12, true, true),
    INVOICE_HEAD_INFO_YFPHM_ERROR_144026("144026", "红票的原发票号码", 8, 8, true, true),
    INVOICE_HEAD_INFO_QDBZ_ERROR_144027("144027", "清单标志", 1, 1, true, true),
    INVOICE_HEAD_INFO_QDBZ_ERROR_144028("144028", "清单标志只能为0:普通发票,1:普通发票（清单）,2:收购发票,3:收购发票（清单）,4:成品油发票！"),
    INVOICE_HEAD_INFO_QDXMMC_ERROR_144029("144029", "清单标志为1或3时,清单发票项目名称不能为空！"),
    INVOICE_HEAD_INFO_JSHJ_ERROR_144030("144030", "价税合计", 1, 20, true, true),
    INVOICE_HEAD_INFO_JSHJ_ERROR_144031("144031", "价税合计不能为0且保证小数点后两位小数！"),
    INVOICE_HEAD_INFO_JSHJ_ERROR_144032("144032", "蓝票价税合计必须大于0且保证小数点后两位小数！"),
    INVOICE_HEAD_INFO_JSHJ_ERROR_144033("144033", "红票价税合计必须小于0！"),
    INVOICE_HEAD_INFO_HJJE_ERROR_144034("144034", "合计不含税金额", 1, 20, true, true),
    INVOICE_HEAD_INFO_HJJE_ERROR_144035("144035", "合计不含税金额不为0时，小数点位数须为2位小数！"),
    INVOICE_HEAD_INFO_HJSE_ERROR_144036("144036", "合计税额", 1, 20, true, true),
    INVOICE_HEAD_INFO_HJSE_ERROR_144037("144037", "订单主体信息中合计税额不为0时，小数点位数须为2位小数！"),
    INVOICE_HEAD_INFO_FPLXDM_ERROR_144038("144038", "发票类型代码", 3, 3, true, true),
    INVOICE_HEAD_INFO_FPLXDM_ERROR_144039("144039", "发票类型代码错误！"),
    INVOICE_HEAD_INFO_BZ_ERROR_144040("144040", "专票冲红时备注必填！"),
    INVOICE_HEAD_INFO_BZ_ERROR_144041("144041", "专票冲红备注不满足格式要求，请核对！"),
    INVOICE_HEAD_INFO_CHYY_ERROR_144042("144042", "开票类型为红字发票时,描述冲红具体原因！"),
    INVOICE_HEAD_INFO_TSCHBZ_ERROR_144043("144043", "特殊冲红标志",1,1,true,true),
    INVOICE_HEAD_INFO_TSCHBZ_ERROR_144044("144044", "开票类型为蓝票时,不能有原发票代码、号码！"),
    INVOICE_HEAD_INFO_JQBH_ERROR_144045("144045", "机器编号",1,20,true,true),
    INVOICE_HEAD_INFO_FPDM_ERROR_144046("144046", "发票代码不能为空！"),
    INVOICE_HEAD_INFO_FPDM_ERROR_144047("144047", "发票代码长度只能是10位或者12位！"),
    INVOICE_HEAD_INFO_FPDM_ERROR_144048("144048", "发票代码不是数字！"),
    INVOICE_HEAD_INFO_FPHM_ERROR_144049("144049", "发票号码不能为空！"),
    INVOICE_HEAD_INFO_FPHM_ERROR_144050("144050", "发票号码长度只能是8位！"),
    INVOICE_HEAD_INFO_FPHM_ERROR_144051("144051", "发票号码不是数字！"),
    INVOICE_HEAD_INFO_KPRQ_ERROR_144052("144052", "开票日期",1,30,true,true),
    INVOICE_HEAD_INFO_KPRQ_ERROR_144053("144053", "开票日期格式有误！"),
    INVOICE_HEAD_INFO_JYM_ERROR_144054("144054", "校验码",1,20,true,true),
    INVOICE_HEAD_INFO_FWM_ERROR_144055("144055", "防伪码",1,200,true,true),
    INVOICE_HEAD_INFO_EWM_ERROR_144056("144056", "二维码",1,5000,true,true),
    INVOICE_MX_INFO_IMPORT_ERROR_NULL("144003","发票明细信息为空!"),
    INVOICE_MX_INFO_FPHXZ_NULL_144057("144057","明细信息中发票行性质为空！"),
    INVOICE_MX_INFO_FPHXZ_ERROR_144058("144058","明细信息中发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票"),
    INVOICE_MX_INFO_SPBM_ERROR_144059("144059","明细信息中商品税收分类编码",19,true,true),
    INVOICE_MX_INFO_XMMC_ERROR_144060("115060", "明细信息中项目名称", 1, 90, true, true),
    INVOICE_MX_INFO_GGXH_ERROR_144061("115061", "当发票行性质为红字清单或折扣行时，规格型号必须为空"),
    INVOICE_MX_INFO_DW_ERROR_144062("115062", "明细信息中单位", 1, 20, true, true),
    INVOICE_MX_INFO_DW_ERROR_144063("115063", "明细信息中成品油的项目单位只能为升或吨"),
    INVOICE_MX_INFO_SPSL_ERROR_144064("115064", "明细信息中商品数量", 1, 20, true, true),
    INVOICE_MX_INFO_SPSL_ERROR_144065("115065", "明细信息中项目数量小数点后数值不能大于8位"),
    INVOICE_MX_INFO_DJ_ERROR_144066("115066", "明细信息中单价", 1, 20, true, true),
    INVOICE_MX_INFO_DJ_ERROR_144067("115067", "明细信息中项目单价必须为正数且小数点后数值不能大于8位"),
    INVOICE_MX_INFO_ZZSTSGL_ERROR_144068("144068", "明细信息中增值税特殊管理", 0, 50, true, false),
    INVOICE_MX_INFO_YHZCBS_ERROR_144069("144069", "明细信息中优惠政策标识", 1, 1, true, true),
    INVOICE_MX_INFO_YHZCBS_ERROR_144070("144070", "明细信息中优惠政策标识只能为0或1,0:不使用,1:使用"),
    INVOICE_MX_INFO_YHZCBS_ERROR_144071("144071", "明细信息中优惠政策标识为1时,增值税特殊管理不能为空"),
    INVOICE_MX_INFO_YHZCBS_ERROR_144072("144072", "明细信息中当YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择\"0或1或2\"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的\"出口零税/免税/不征税"),
    INVOICE_MX_INFO_YHZCBS_ERROR_144073("144073", "明细信息中当优惠政策标识为0时,增值税特殊管理须为空"),
    INVOICE_MX_INFO_LSLBS_ERROR_144074("144074", "明细信息中零税率标识非空时，只允许传0、1、2、3"),
    INVOICE_MX_INFO_JE_ERROR_144075("144075", "明细信息中项目金额", 1, 16, true, true),
    INVOICE_MX_INFO_JE_ERROR_144076("144076", "明细信息中项目金额不能为0"),
    INVOICE_MX_INFO_JE_ERROR_144077("144077", "明细信息中项目金额须为2位小数"),
    INVOICE_MX_INFO_JE_ERROR_144078("144078", "明细信息中项目明细金额=单价*数量误差不能大于0.01"),
    INVOICE_MX_INFO_HSBZ_ERROR_144079("144079", "明细信息中含税标志", 1, 1, true, true),
    INVOICE_MX_INFO_HSBZ_ERROR_144080("144080", "明细信息中含税标志只能为0：0表示都不含税"),
    INVOICE_MX_INFO_HSBZ_ERROR_144081("144081", "明细信息中含税标志为0，税额必填"),
    INVOICE_MX_INFO_SL_ERROR_144082("144082", "明细信息中税率不能为空"),
    INVOICE_MX_INFO_SL_ERROR_144083("144083", "明细信息中增值税特殊管理内容为'按5%简易征收',与商品行税率不一致"),
    INVOICE_MX_INFO_SL_ERROR_144084("144084", "明细信息中增值税特殊管理内容为'按3%简易征收',与商品行税率不一致"),
    INVOICE_MX_INFO_SL_ERROR_144085("144085", "明细信息中增值税特殊管理内容为'简易征收',与商品行税率不一致"),
    INVOICE_MX_INFO_SL_ERROR_144086("144086", "明细信息中增值税特殊管理内容为'按5%简易征收减按1.5%计征',与商品行税率不一致"),
    INVOICE_MX_INFO_SL_ERROR_144087("144087", "明细信息中零税率标识非空, 但商品税率不为零;请保持零税率标识与商品税率逻辑一致!"),
    INVOICE_MX_INFO_SL_ERROR_144088("144088", "明细信息中零税率标识为空, 但商品税率为零;请保持零税率标识与商品税率逻辑一致!"),
    INVOICE_MX_INFO_SL_ERROR_144089("144089", "明细信息中零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';请保持零税率标识与增值税特殊管理逻辑一致!"),
    INVOICE_MX_INFO_SL_ERROR_144090("144090", "明细信息中零税率标识为3（普通零税）, 则:YHZCBS填0,ZZSTSGL填空"),
    INVOICE_MX_INFO_SE_ERROR_144091("144091", "明细信息中税额", 0, 25, true, false),
    INVOICE_MX_INFO_SE_ERROR_144092("144092", "明细信息中税额须为2位小数"),
    INVOICE_MX_INFO_SE_ERROR_144093("144093", "明细信息中含税标志为0时,税额不能为空"),
    INVOICE_MX_INFO_SE_ERROR_144094("144094", "明细信息中发票行性质为清单红票时,税额不能为空"),
    INVOICE_HEAD_INFO_NSRSBH_ERROR_144095("144095","销货方纳税人识别号必须和纳税人识别号一致"),
    INVOICE_HEAD_INFO_CALCULATE_ERROR_144096("144096", "合计税额+合计不含税金额不等于开票合计金额"),
    INVOICE_MX_INFO_IMPORT_ERROR_LENGTH_144097("144097", "开具发票订单数量超出限额,限额为2000"),
    INVOICE_MX_INFO_SE_ERROR_144098("144098", "单条明细税额误差大于0.06"),
    INVOICE_MX_INFO_JE_ERROR_144099("144099", "项目明细金额误差不能大于0.01"),
    INVOICE_MX_INFO_SPSL_ERROR_144100("144100","成品油项目数量不能为空"),
    INVOICE_MX_INFO_SPBM_ERROR_144101("144101", "发票明细中的商品编码只能为成品油或者非成品油中的一种"),
    INVOICE_MX_INFO_ZKJE_ERROR_144102("144102", "订单明细数据中蓝票折扣金额不能大于或者等于零"),
    INVOICE_MX_INFO_ZKJE_ERROR_144103("144103", "订单明细数据中红票折扣金额不能小于或者等于零"),
    INVOICE_MX_INFO_KPLX_ERROR_144104("144104", "发票类型错误"),
    INVOICE_MX_INFO_ZKH_ERROR_144105("144105", "订单明细数据中折扣行不能为第一行或不能连续两个折扣行"),
    INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144106("144106", "订单明细数据中折扣行与被折扣行商品编码不相同"),
    INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144107("144107", "订单明细数据中折扣额不能大于被折扣额"),
    INVOICE_MX_INFO_ZKSL_ERROR_144108("144108", "订单明细数据中折扣税率和被折扣税率不相同"),
    INVOICE_MX_INFO_FPHXZ_ERROR_144109("144109", "订单明细数据中只有一行明细 发票性质必须为0"),
    INVOICE_MX_INFO_FPHXZ_ERROR_144110("144110", "订单明细数据中明细行最后一行发票性质不能为2"),
    INVOICE_MX_INFO_THAN_ZERO_ERROR_144111("144111", "订单明细数据中蓝票折扣金额不能大于或者等于零"),
    INVOICE_MX_INFO_LESS_ZERO_ERROR_144112("144112", "订单明细数据中红票折扣金额不能小于或者等于零"),
    INVOICE_MX_SPSL_OVER_8_ERROR_144113("144113","成品油明细不能超过8行"),
    INVOICE_MX_HJJEANDMXJE_ERROR_144114("144114", "订单明细数据中合计金额和明细金额不相等"),
    INVOICE_MX_HJSE_ERROR_144115("144115", "订单明细数据中合计税额有误,误差大于127分钱,建议拆分订单开票"),
    INVOICE_MX_SPBM_ERROR_144116("144116", "商品编码填写有误"),
    INVOICE_HEAD_INFO_GMFMC_ERROR_144117("144117", "购货名称不可以存在全角字符"),
    INVOICE_MX_SPBM_ERROR_144118("144118", "商品编码非纯数字"),
    INVOICE_MX_INFO_ZKHANDBZKH_ERROR_144119("144119", "订单明细数据中折扣行与被折扣行商品名称不相同"),
    INVOICE_MX_INFO_KCE_ERROR_144120("144120", "差额征税发票，扣除额不能为空!"),
    INVOICE_MX_INFO_KCE_ERROR_144121("144121", "差额征税发票，扣除额不是合法数字!"),
    INVOICE_MX_INFO_KCE_ERROR_144122("144122", "差额征税发票，无折扣行时订单明细只能有一行!"),
    INVOICE_MX_INFO_KCE_ERROR_144123("144123", "差额征税发票，有折扣行时订单明细只能有两行!"),
    INVOICE_MX_INFO_KCE_ERROR_144124("144124", "差额征税发票，备注信息不能为空!"),
    INVOICE_MX_INFO_KCE_ERROR_144125("144125", "差额征税蓝字发票，扣除额不能为负数!"),
    INVOICE_MX_INFO_KCE_ERROR_144126("144126", "差额征税蓝字发票，第二行商品行只能为折扣行!"),
    INVOICE_MX_INFO_KCE_ERROR_144127("144127", "差额征税红字发票，扣除额不能为正数!"),
    INVOICE_MX_INFO_KCE_ERROR_144128("144128", "差额征税发票，备注需注明差额征税!"),
    INVOICE_MX_INFO_KCE_ERROR_144129("144129", "差额征税发票，扣除额不能大于项目金额!"),
    INVOICE_MX_INFO_DW_ERROR_144130("144130", "发票行性质为红字清单或折扣行时单位必须为空"),
    INVOICE_MX_INFO_SPSL_ERROR_144131("144131", "发票行性质为红字清单或折扣行时商品数量必须为空"),
    INVOICE_MX_INFO_SPSL_ERROR_144132("144132", "发票行性质为非折扣行时商品数量不能为0"),
    INVOICE_MX_INFO_DJ_ERROR_144133("144133", "发票行性质为红字清单或折扣行时单价必须为空"),
    INVOICE_MX_INFO_DJ_ERROR_144134("144134", "发票行性质为非红字清单时单价不能为0或者0.00"),
    INVOICE_MX_INFO_BZ_ERROR_144135("144135", "订单主体信息中备注", 0, 200, true, false),
    INVOICE_HEAD_INFO_GMFMC_ERROR_144136("144136", "系统未找到购买方编码对应的购买方信息"),
    
    
    /**
     * 商品信息查询接口对接状态码
     */
    COMMODITY_MESSAGE_QUERY_SUCCESS("174000", "商品信息查询成功"),
    COMMODITY_MESSAGE_QUERY_ERROR("174999", "商品信息查询失败"),
    COMMODITY_MESSAGE_QUERY_RESULT_NULL("174998", "商品信息查询结果为空"),
    COMMODITY_MESSAGE_QUERY_NULL("174997", "商品信息查询入参不能为空"),
    COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174001("174001", "纳税人识别号", 15, 20, true, true),
    COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174002("174002", "纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    COMMODITY_MESSAGE_QUERY_XHFSBH_ERROR_174003("174003", "纳税人识别号需要全部大写"),
    COMMODITY_MESSAGE_QUERY_XHFMC_ERROR_174004("174004", "销货方名称", 0, 100, true, false),
    COMMODITY_MESSAGE_QUERY_YS_ERROR_174005("174005", "页号不能为空且从1开始"),
    COMMODITY_MESSAGE_QUERY_GS_ERROR_174006("174006", "个数不能为空且从1开始,最大支持100个/页"),
    COMMODITY_MESSAGE_QUERY_ERROR_174007("174007", "商品对应ID", 0, 50, true, false),
    COMMODITY_MESSAGE_QUERY_ERROR_174008("174008", "商品页数", 1, 5, true, true),
    COMMODITY_MESSAGE_QUERY_ERROR_174009("174009", "商品个数", 1, 5, true, true),
    COMMODITY_MESSAGE_QUERY_ERROR_174010("174010", "项目名称", 0, 90, true, false),
    
    /**
     * 商品信息同步接口对接状态码
     */
    COMMODITY_MESSAGE_SYNC_SUCCESS("173000", "商品信息同步成功"),
    COMMODITY_MESSAGE_SYNC_ERROR("173999", "商品信息同步失败"),
    COMMODITY_MESSAGE_SYNC_NULL("173998", "商品信息同步入参不能为空"),
    COMMODITY_MESSAGE_SYNC_INSERT("173997", "商品信息已存在无法新增"),
    COMMODITY_MESSAGE_SYNC_UPDATE("173996", "商品信息不存在无法更新"),
    COMMODITY_MESSAGE_SYNC_DELETE("173995", "商品信息不存在无法删除"),
    COMMODITY_MESSAGE_SYNC_SPID_ERROR_173001("173001", "商品对应的ID", 1, 50, true, true),
    COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173002("173002", "销货方纳税人识别号", 15, 20, true, true),
    COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173003("173003", "销货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    COMMODITY_MESSAGE_SYNC_XHFSBH_ERROR_173004("173004", "销货方纳税人识别号需要全部大写"),
    COMMODITY_MESSAGE_SYNC_XHFMC_ERROR_173005("173005", "销货方名称", 1, 100, true, true),
    COMMODITY_MESSAGE_SYNC_SPBM_ERROR_173006("173006", "税收分类编码", 19, 19, true, true),
    COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173007("173007", "优惠政策标识", 1, 1, true, true),
    COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173008("173008", "优惠政策标识只能为0或1,0:不使用,1:使用"),
    COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173009("173009", "优惠政策标识为1时,增值税特殊管理不能为空"),
    COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173010("173010", "当YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择\"0或1或2\"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的\"出口零税/免税/不征税"),
    COMMODITY_MESSAGE_SYNC_YHZCBS_ERROR_173011("173011", "当优惠政策标识为0时,增值税特殊管理须为空"),
    COMMODITY_MESSAGE_SYNC_LSLBS_ERROR_173012("173012", "零税率标识非空时，只允许传0、1、2、3"),
    COMMODITY_MESSAGE_SYNC_XMMC_ERROR_173013("173013", "项目名称", 1, 90, true, true),
    COMMODITY_MESSAGE_SYNC_HSBZ_ERROR_173014("173014", "含税标志", 1, 1, true, true),
    COMMODITY_MESSAGE_SYNC_HSBZ_ERROR_173015("173015", "明细信息中含税标志只能为0和1：0表示都不含税,1表示都含税"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173016("173016", "税率不能为空"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173017("173017", "增值税特殊管理内容为'按5%简易征收',与商品行税率不一致"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173018("173018", "增值税特殊管理内容为'按3%简易征收',与商品行税率不一致"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173019("173019", "增值税特殊管理内容为'简易征收',与商品行税率不一致"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173020("173020", "增值税特殊管理内容为'按5%简易征收减按1.5%计征',与商品行税率不一致"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173021("173021", "零税率标识非空, 但商品税率不为零;请保持零税率标识与商品税率逻辑一致!"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173022("173022", "零税率标识为空, 但商品税率为零;请保持零税率标识与商品税率逻辑一致!"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173023("173023", "零税率标识为0/1/2, 但增值税特殊管理内容不为'出口零税/免税/不征税';请保持零税率标识与增值税特殊管理逻辑一致!"),
    COMMODITY_MESSAGE_SYNC_SL_ERROR_173024("173024", "零税率标识为3（普通零税）, 则:YHZCBS填0,ZZSTSGL填空"),
    COMMODITY_MESSAGE_SYNC_CZLX_ERROR_173025("173025", "操作类型不能为空且只能等于0,1,2"),
    COMMODITY_MESSAGE_SYNC_ERROR_173026("173026", "自行编码", 0, 16, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173027("173027", "规格型号", 0, 40, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173028("173028", "单位", 0, 20, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173029("173029", "单价", 0, 20, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173030("173030", "税率", 1, 8, true, true),
    COMMODITY_MESSAGE_SYNC_ERROR_173031("173031", "增值税特殊管理", 0, 500, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173032("173032", "税率不合法", 0, 500, true, false),
    COMMODITY_MESSAGE_SYNC_ERROR_173033("173033", "增值税特殊管理内容百分号不合法"),
    
    /**
     * 购买方信息查询接口对接状态码
     */
    BUYER_MESSAGE_QUERY_SUCCESS("184000", "购买方信息查询成功"),
    BUYER_MESSAGE_QUERY_ERROR("184999", "购买方信息查询失败"),
    BUYER_MESSAGE_QUERY_RESULT_NULL("184998", "购买方信息查询结果为空"),
    BUYER_MESSAGE_QUERY_NULL("184997", "购买方信息查询入参不能为空"),
    BUYER_MESSAGE_QUERY_GMFBM_ERROR_184001("184001", "购买方编码", 0, 50, true, false),
    BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184002("184002", "销货方纳税人识别号", 15, 20, true, true),
    BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184003("184003", "销货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    BUYER_MESSAGE_QUERY_XHFSBH_ERROR_184004("184004", "销货方纳税人识别号需要全部大写"),
    BUYER_MESSAGE_QUERY_XHFMC_ERROR_184005("184005", "销货方名称", 1, 100, true, false),
    BUYER_MESSAGE_QUERY_GMFMC_ERROR_184006("184006", "购货方名称", 1, 100, true, false),
    BUYER_MESSAGE_QUERY_GMFMC_ERROR_184007("184007", "购货名称不可以存在全角字符"),
    BUYER_MESSAGE_QUERY_YS_ERROR_184008("184008", "页号不能为空且从1开始"),
    BUYER_MESSAGE_QUERY_GS_ERROR_184009("184009", "个数不能为空且从1开始,最大支持100个/页"),
    BUYER_MESSAGE_QUERY_ERROR_184010("184010", "购买方纳税人识别号", 15, 20, true, false),
    BUYER_MESSAGE_QUERY_ERROR_184011("184011", "购买方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    BUYER_MESSAGE_QUERY_ERROR_184012("184012", "购买方纳税人识别号需要全部大写"),
    BUYER_MESSAGE_QUERY_ERROR_184013("184013", "购方页数", 1, 5, true, true),
    BUYER_MESSAGE_QUERY_ERROR_184014("184014", "购方个数", 1, 5, true, true),
    
    /**
     * 购买方信息同步接口对接状态码
     */
    BUYER_MESSAGE_SYNC_SUCCESS("183000", "购买方信息同步成功"),
    BUYER_MESSAGE_SYNC_ERROR("183999", "购买方信息同步失败"),
    BUYER_MESSAGE_SYNC_NULL("183998", "购买方信息同步入参不能为空"),
    BUYER_MESSAGE_SYNC_INSERT("183997", "购买方信息已存在无法新增"),
    BUYER_MESSAGE_SYNC_UPDATE("183996", "购买方信息不存在无法更新"),
    BUYER_MESSAGE_SYNC_DELETE("183995", "购买方信息不存在无法删除"),
    BUYER_MESSAGE_SYNC_GMFBM_ERROR_183001("183001","购买方编码",1,50,true,true),
    BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183002("183002", "销货方纳税人识别号", 15, 20, true, true),
    BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183003("183003", "销货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    BUYER_MESSAGE_SYNC_XHFSBH_ERROR_183004("183004", "销货方纳税人识别号需要全部大写"),
    BUYER_MESSAGE_SYNC_XHFMC_ERROR_183005("183005", "销货方名称", 1, 100, true, true),
    BUYER_MESSAGE_SYNC_GMFLX_ERROR_183006("183006", "购买方类型", 2, 2, true, true),
    BUYER_MESSAGE_SYNC_GMFLX_ERROR_183007("183007", "购买方类型只能为:01企业，02机关事业单位，03个人，04其他！"),
    BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183008("183008", "购买方纳税人识别号", 15, 20, true, true),
    BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183009("183009", "购买方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183010("183010", "购买方纳税人识别号需要全部大写"),
    BUYER_MESSAGE_SYNC_GMFMC_ERROR_183011("183011", "购货方名称", 1, 100, true, true),
    BUYER_MESSAGE_SYNC_GMFMC_ERROR_183012("183012", "购货名称不可以存在全角字符"),
    BUYER_MESSAGE_SYNC_GMFDZ_ERROR_183013("183013", "购货方地址", 1, 80, true, false),
    BUYER_MESSAGE_SYNC_GMFDH_ERROR_183014("183014", "购货方电话", 1, 20, true, false),
    BUYER_MESSAGE_SYNC_GMFDZ_GMFZH_ERROR_183015("183015", "购货地址和电话总长度超过100位"),
    BUYER_MESSAGE_SYNC_GMFDZ_GMFDH_ERROR_183016("183016", "购买方银行名称", 1, 70, true, false),
    BUYER_MESSAGE_SYNC_GMFDZ_GMFYH_ERROR_183017("183017", "购买方银行账号", 1, 30, true, false),
    BUYER_MESSAGE_SYNC_GMFDZ_GMFYH_GMFDH_ERROR_183018("183018", "购买方银行名称和账号总长度超过100位"),
    BUYER_MESSAGE_SYNC_CZLX_ERROR_183019("183019", "操作类型不能为空且只能等于0,1,2"),
    BUYER_MESSAGE_SYNC_GMFSBH_ERROR_183020("183020", "购买方纳税人识别号", 15, 20, true, false),
    BUYER_MESSAGE_SYNC_ERROR_183021("183021", "备注", 0, 200, true, false),
    
    /**
     * 税控设备信息同步接口对接状态码
     */
    TAX_EQUIPMENT_INFO_193000("193000", "税控设备信息同步成功"),
    TAX_EQUIPMENT_INFO_193001("193001", "税控设备信息同步请求参数为空"),
    TAX_EQUIPMENT_INFO_193002("193002", "销货方纳税人识别号", 15, 20, true, true),
    TAX_EQUIPMENT_INFO_193003("193003", "销货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    TAX_EQUIPMENT_INFO_193004("193004", "销货方纳税人识别号需要全部大写"),
    TAX_EQUIPMENT_INFO_193005("193005", "销货方名称", 1, 100, true, true),
    TAX_EQUIPMENT_INFO_193006("193006", "税控设备代码", 1, 5, true, true),
    TAX_EQUIPMENT_INFO_193007("193007", "税控设备代码只能为:000;001,002,004,005,006,007,008,009,010,011"),
    TAX_EQUIPMENT_INFO_193008("193008", "税控设备型号", 1, 100, true, false),
    TAX_EQUIPMENT_INFO_193009("193009", "关联时间", 1, 30, true, true),
    TAX_EQUIPMENT_INFO_193010("193010", "操作类型", 1, 1, true, true),
    TAX_EQUIPMENT_INFO_193011("193011", "备注", 1, 200, true, false),
    TAX_EQUIPMENT_INFO_193012("193012", "操作类型不能为空且只能等于0,1,2"),
    TAX_EQUIPMENT_INFO_193013("193013", "税控设备信息新增失败"),
    TAX_EQUIPMENT_INFO_193014("193014", "税控设备信息更新失败"),
    TAX_EQUIPMENT_INFO_193015("193015", "税控设备信息操作失败,未维护企业信息"),
    TAX_EQUIPMENT_INFO_193016("193016", "税控设备信息不存在"),
    TAX_EQUIPMENT_INFO_193999("193999", "税控设备信息更新失败"),
    
    
    /**
     * 发票余量接口对接状态码
     */
    QUERY_INVOICE_STORE_194000("194000", "发票余量查询成功"),
    QUERY_INVOICE_STORE_194001("194001", "销货方纳税人识别号", 15, 20, true, true),
    QUERY_INVOICE_STORE_194002("194002", "销货方纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),
    QUERY_INVOICE_STORE_194003("194003", "销货方纳税人识别号需要全部大写"),
    QUERY_INVOICE_STORE_194999("194999", "发票余量查询失败"),
    
    
    FG_PRINTER_CONFIGURATION_000000("000000", "发票打印成功"),
    FG_PRINTER_CONFIGURATION_009999("009999", "发票打印失败"),
    FG_PRINTER_CONFIGURATION_009000("009000", "发票打印中"),
    FG_PRINTER_CONFIGURATION_009001("009001", "发票待打印"),
    
    PRINTER_CONFIGURATION_110013("110013", "发票种类代码错误,只能为0:专票;2:普票;"),


    /**
     * 订单删除
     */
    ORDER_DELETE_120001("120001", "销方纳税人识别号不能为空"),
    ORDER_DELETE_120002("120002", "订单请求流水号不能为空"),
    ORDER_DELETE_120003("120003", "订单不存在"),
    ORDER_DELETE_120004("120004", "订单处于开票中、开票成功、开票失败状态，不允许删除"),


    ;
    
    /**
     * key值
     */
    private final String key;
    
    /**
     * 对应Message
     */
    private final String message;

    /**
     * 长度
     */
    private int length;

    /**
     * 非空判断
     */
    private boolean checkNull = false;

    /**
     * 长度判断
     */
    private boolean checkLength = false;
    
    /**
     * 最小长度
     */
    private int minLength;
    
    /**
     * 最大长度
     */
    private int maxLength;
    
    /**
     * 新税控最大长度
     */
    private int maxLengthNewTax;
    
    public String getKey() {
        return this.key;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public boolean getCheckNull() {
        return this.checkNull;
    }
    
    public boolean getCheckLength() {
        return this.checkLength;
    }
    
    /**
     * 长度判断
     */
    public int getMinLength() {
        return this.minLength;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public int getMaxLengthNewTax() {
        return this.maxLengthNewTax;
    }
    
    public static OrderInfoContentEnum getCodeValue(String key) {
        for (OrderInfoContentEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }


    OrderInfoContentEnum(String key, String message) {
        this.key = key;
        this.message = message;
    }
    
    
    OrderInfoContentEnum(String key, String message, int maxLength, boolean checkLength, boolean checkNull) {
        this.key = key;
        this.message = message;
        this.maxLength = maxLength;
        this.checkNull = checkNull;
        this.checkLength = checkLength;
    }
    
    OrderInfoContentEnum(String key, String message, int minLength, int maxLength, boolean checkLength, boolean checkNull) {
        this.key = key;
        this.message = message;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkLength = checkLength;
        this.checkNull = checkNull;
    }
    
    OrderInfoContentEnum(String key, String message, int minLength, int maxLength, int maxLengthNewTax, boolean checkLength, boolean checkNull) {
        this.key = key;
        this.message = message;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.maxLengthNewTax = maxLengthNewTax;
        this.checkLength = checkLength;
        this.checkNull = checkNull;
    }
    
    
}
