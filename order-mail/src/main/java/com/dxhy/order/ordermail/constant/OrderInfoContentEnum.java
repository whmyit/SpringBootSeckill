package com.dxhy.order.ordermail.constant;

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
    
    PRINT_SUCCESS("0000","打印成功"),

    INVOICE_NULL("9002", "发票开具信息为空"),

    INVOICE_MX_NULL("9003", "发票开具明细信息为空"),

    INVOICE_KPLX_ERROR("9004", "发票类型错误"),

    INVOICE_QYLX_ERROR("9005", "购货方企业类型错误"),

    INVOICE_DKBZ_ERROR("9006", "代开模式错误开(0)代开(1)"),

    INVOICE_CZDM_ERROR("9007", "开具操作代码类型错误"),

    INVOICE_TSCHBZ_ERROR("9008", "特殊冲红标志类型错误"),

    INVOICE_JSHJ_ERROR("9009", "合计税额+合计不含税金额不等于开票合计金额"),

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

    ORDER_MERGE_MDH_ERROR("9611", "所选订单门店号不一致，不允许合并"),

    ORDER_MERGE_EXCEPTION_ERROR("9612", "订单合并异常"),

    READY_ORDER_SPBM_NULL_ERROR("9613", "所选订单包含商品编码有误的明细行"),

    READY_ORDER_CZLX_NULL_ERROR("9614", "生成待开数据校验接口操作类型为空"),

    READY_ORDER_UID_NULL_ERROR("9615", "用户uId为空"),

    READY_MERGE_QUOTA_NULL_ERROR("9616", "没查到企业开票限额"),

    READY_OPEN_QUOTA_NULL_ERROR("9617", "开票限额未设置"),

    READY_OPEN_DEPTID_NULL_ERROR("9618", "组织唯一标识ID为空"),

    READY_OPEN_FPZLDM_ERROR("9619", "未知的发票种类代码"),

    ERRORORDER_BACK_ORDERID_NULL_ERROR("9620", "异常订单回退成待开订单请求接收到的订单id为空"),

    READY_OPEN_XFXX_ERROR("9621", "查询销货方信息失败"),

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

    INVOICE_HEAD_ERROR_009631("009631", "订单主体信息中红票合计金额不能大于或者等于零"),

    GENERATE_READY_ORDER_GFXX_NULL_ERROR("96040", "购方税号为空"),
    
    ORDER_MERGE_YWSX_ERROR("9640", "业务属性不同的不能合并"),

    ORDER_MERGE_YWLY_ERROR("9641", "业务来源不同的不能合并"),
    
    ORDER_MERGE_LIMIT_ERROR("9642", "限额为0，不允许开票"),

    // 订单号开票时 20
    STRING_ORDER_DDH("9101", "订单号", 20, true, true),

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
    


    STRING_FPKJ_NSRMC("9204", "纳税人名称", 100, true, true),

    STRING_FPKJ_DKBZ("9212", "代开标志", 1, false, true),

    STRING_FPKJ_KPXM("9205", "开票项目", 1, false, true),

    STRING_FPKJ_XHF_MC("9206", "销货方名称", 100, true, true),

    STRING_FPKJ_XHF_NSRSBH("9239", "销货方纳税人识别号", 10, 20, true, true),

    STRING_FPKJ_XHF_DZ("9207", "销货方地址", 100, true, true),

    STRING_FPKJ_XHF_DH("9208", "销货方电话", 20, true, true),

    STRING_FPKJ_XHF_YHZH("9208", "销货方银行账号", 100, true, false),

    STRING_FPKJ_GHF_MC("9209", "购货方名称", 100, true, true),

    STRING_FPKJ_GHF_NSRSBH("9209", "购货方纳税人识别号", 10, 20, true, false),

    STRING_FPKJ_GHF_DZ("9210", "购货方地址", 100, true, false),

    STRING_FPKJ_GHF_DH("9210", "购货方电话", 20, true, false),

    STRING_FPKJ_GHF_SJ("9210", "购货方手机", 20, true, false),

    STRING_FPKJ_GHF_WX("9210", "购货方微信", 20, true, false),

    STRING_FPKJ_GHF_QYLX("9211", "购货方企业类型", 2, true, true),
    // 开票是100  长度10修改长度20   再修改为16
    STRING_FPKJ_KPY("9213", "开票员", 16, true, true),

    STRING_FPKJ_KPLX("9214", "开票类型", 2, true, true),

    STRING_FPKJ_KPHJJE("9215", "开票合计金额", 24, true, true),

    STRING_FPKJ_DDH("9216", "订单号", 20, false, true),

    STRING_FPKJ_BMB_BBH("9217", "编码号版本表", 20, true, true),

    STRING_FPKJ_YFP_DM("9218", "原发票代码", 12, true, true),

    STRING_FPKJ_YFP_HM("9219", "原发票号码", 8, true, true),

    STRING_FPKJ_TSCHBZ("9220", "特殊冲红标志", 1, true, true),

    STRING_FPKJ_CHYY("9221", "冲红原因", 200, true, true),

    STRING_FPKJ_NSRDZDAH("9222", "纳税人电子档案号", 20, true, false),

    STRING_FPKJ_SWJGDM("9223", "税务机构代码", 11, true, false),

    STRING_FPKJ_PYDM("9224", "票样代码", 6, true, false),

    STRING_FPKJ_GHF_SF("9225", "购货方省份", 20, true, false),
    
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

    STRING_FPKJ_THDH("9234", "退货单号", 20, true, true),
    
    STRING_FPKJ_NSRSBH_JG("9235", "机关纳税人识别号", 10, 20, false, true),

    CHECK_ISS7PRI_107123("107123", "商品编码不合法,成品油只能使用成品油商品编码!"),
    

    /**
     * 发票开具项目明细
     */

    STRING_FPKJMX_XMXH("9301", "发票开具明细项目序号", 5, true, false),
    STRING_FPKJMX_XMMC("9301", "发票开具明细项目名称", 200, true, true),

    STRING_FPKJMX_FPHXZ("9302", "发票开具明细发票行性质", 1, true, true),
    
    STRING_FPKJMX_XMDJ("9303", "发票开具明细项目单价", 24, true, false),

    STRING_FPKJMX_XMSL("9304", "发票开具明细项目数量", 24, true, false),

    STRING_FPKJMX_XMJE("9305", "发票开具明细项目金额", 24, true, true),

    STRING_FPKJMX_SL("9306", "发票开具明细税率", 8, true, true),

    STRING_FPKJMX_SPBM("9307", "发票开具明细商品编码", 19, true, true),

    STRING_FPKJMX_ZXBM("9307", "发票开具明细自行编码", 20, true, false),

    STRING_FPKJMX_YHZCBS("9308", "优惠政策标识", 1, true, true),

    STRING_FPKJMX_ZZSTSGL("9309", "增值税特殊管理", 50, true, false),
    STRING_FPKJMX_LSLBS("9309", "零税率标识", 1, true, false),
    STRING_FPKJMX_GGXH("9309", "规格型号", 200, true, false),
    STRING_FPKJMX_DW("9309", "项目单位", 100, true, false),
    STRING_FPKJMX_HSBZ("9309", "含税标志", 1, true, true),

    STRING_FPKJMX_SE("9310", "发票开具明细税额", 24, true, false),
    
    
    /**
     * 二维码路由结果
     */
    
    EWM_ERROR_CODE_205998("205998", "二维码不存在"),
    EWM_ERROR_CODE_205999("205999", "短码转长码异常"),

    /**
     * 对外接口错误代码
     */
    CHECK_ISS7PRI_107003("107003", "批次信息中订单请求批次号", 1, 40, true, true),

    CHECK_ISS7PRI_107005("107005", "批次信息中纳税人识别号", 15, 20, true, true),

    CHECK_ISS7PRI_107006("107006", "批次信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),

    CHECK_ISS7PRI_107007("107007", "批次信息中受理点ID", 0, 8, true, false),

    CHECK_ISS7PRI_107009("107009", "批次信息中开票机号", 0, 20, true, false),

    CHECK_ISS7PRI_107010("107010", "批次信息中发票类型", 1, 1, true, true),

    CHECK_ISS7PRI_107011("107011", "批次信息中发票类型暂时只支持1:纸质发票,2:电子发票"),

    CHECK_ISS7PRI_107012("107012", "批次信息中发票类别", 1, 2, true, true),

    CHECK_ISS7PRI_107013("107013", "批次信息中发票类型为1时:发票类别只能为0:专票 2:普票41:卷票,发票类型为2时:发票类别只能为51:电子票"),
    
    CHECK_ISS7PRI_107015("107015", "批次信息中开票方式", 0, 2, true, false),
    
    CHECK_ISS7PRI_107163("107163", "纳税人识别号需要全部大写"),

    CHECK_ISS7PRI_107164("107164", "纳税人识别号不能包含空格"),
    
    CHECK_ISS7PRI_107165("107165", "批次信息中开票方式只能为0和1(0:自动开票;1:手动开票)"),

    /**
     * 订单主体信息校验
     */
    CHECK_ISS7PRI_107014("107014", "订单主体信息中订单请求唯一流水号", 1, 40, true, true),

    CHECK_ISS7PRI_107016("107016", "订单主体信息中纳税人识别号", 15, 20, true, true),

    CHECK_ISS7PRI_107017("107017", "订单主体信息中纳税人识别号长度只有15位，17位，18位，20位，其他长度不合法"),

    CHECK_ISS7PRI_107018("107018", "订单主体信息中纳税人名称", 1, 100, true, true),

    CHECK_ISS7PRI_107020("107020", "订单主体信息中开票类型", 1, 1, true, true),

    CHECK_ISS7PRI_107021("107021", "订单主体信息中开票类型只能为0和1：0蓝字发票；1红字发票"),

    CHECK_ISS7PRI_107022("107022", "订单主体信息中销货方纳税人识别号", 15, 20, true, true),

    CHECK_ISS7PRI_107024("107024", "订单主体信息中销货方名称", 1, 100, true, true),

    CHECK_ISS7PRI_107026("107026", "订单主体信息中销货方地址", 1, 100, true, true),

    CHECK_ISS7PRI_107028("107028", "订单主体信息中销货方电话", 1, 20, true, true),

    CHECK_ISS7PRI_107029("107029", "订单主体信息中销货方银行名称", 0, 100, true, false),

    CHECK_ISS7PRI_107030("107030", "订单主体信息中销货方银行银行账户", 0, 30, true, false),

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

    CHECK_ISS7PRI_107046("107046", "订单主体信息中收款人", 0, 16, true, false),

    CHECK_ISS7PRI_107048("107048", "订单主体信息中复核人", 0, 16, true, false),

    CHECK_ISS7PRI_107049("107049", "订单主体信息中红票的原发票代码", 10, 12, true, true),

    CHECK_ISS7PRI_107050("107050", "订单主体信息中红票的原发票号码", 8, 8, true, true),

    CHECK_ISS7PRI_107052("107052", "订单主体信息中冲红原因", 0, 200, true, false),

    CHECK_ISS7PRI_107053("107053", "订单主体信息中红票特殊冲红标志", 1, 1, true, true),

    CHECK_ISS7PRI_107054("107054", "订单主体信息中红票特殊冲红标志只能为0和1：0为正常冲红,1为特殊冲红"),

    CHECK_ISS7PRI_107055("107055", "订单明细信息中发票行性质", 1, 1, true, true),

    CHECK_ISS7PRI_107056("107056", "订单明细信息中发票行性质只能为:0正常行、1折扣行、2被折扣行、6清单红字发票"),

    CHECK_ISS7PRI_107057("107057", "订单明细信息中项目名称", 1, 90, true, true),

    CHECK_ISS7PRI_107058("107058", "订单明细信息中项目序号", 0, 5, true, false),

    CHECK_ISS7PRI_107059("107059", "订单明细信息中规格型号", 0, 40, true, false),

    CHECK_ISS7PRI_107060("107060", "订单明细信息中项目单位", 0, 20, true, false),

    CHECK_ISS7PRI_107062("107062", "订单明细信息中项目金额须为2位小数"),

    CHECK_ISS7PRI_107063("107063", "订单明细信息中自行编码", 0, 16, true, false),

    CHECK_ISS7PRI_107064("107064", "订单明细信息中含税标志", 1, 1, true, true),

    CHECK_ISS7PRI_107065("107065", "订单明细信息中含税标志只能为0和1：0表示都不含税,1表示都含税"),

    CHECK_ISS7PRI_107066("107066", "订单主体信息中价税合计不能为0且保证小数点后两位小数"),

    CHECK_ISS7PRI_107067("107067", "订单主体信息中订单号", 1, 20, true, true),

    CHECK_ISS7PRI_107068("107068", "订单主体信息中订单时间", 0, 30, true, false),

    CHECK_ISS7PRI_107069("107069", "订单主体信息中退货单号", 0, 20, true, false),
    CHECK_ISS7PRI_107070("107070", "订单主体信息中退货单号", 0, 20, true, true),

    CHECK_ISS7PRI_107080("107080", "订单主体信息中蓝票价税合计必须大于0且保证小数点后两位小数"),

    CHECK_ISS7PRI_107081("107081", "订单明细信息中项目金额不能为0"),

    CHECK_ISS7PRI_107082("107082", "发票批次与订单主体信息中的纳税人识别号不一致"),

    CHECK_ISS7PRI_107083("107083", "订单主体信息中红票价税合计必须小于0"),
    
    CHECK_ISS7PRI_107084("107084", "订单主体信息中购方邮件地址格式不正确"),

//    CHECK_ISS7PRI_107084("107084", "商品行行数最大为8行"),
//    CHECK_ISS7PRI_107085("107085", "蓝票的剩余可冲红金额等于0,不允许冲红"),
//    CHECK_ISS7PRI_107086("107086", "冲红金额大于蓝票的剩余可冲红金额,不允许冲红"),
//    CHECK_ISS7PRI_107087("107087", "正常冲红，红字发票纳税人识别号需与对应蓝票相一致"),
//    CHECK_ISS7PRI_107088("107088", "红票不允许重复冲红"),
//    CHECK_ISS7PRI_107089("107089", "已经作废的发票不允许冲红"),
//    CHECK_ISS7PRI_107090("107090", "原发票代码长度有误,最大长度为12位"),
//    CHECK_ISS7PRI_107091("107091", "原发票号码长度有误,最大长度为8位"),
//    CHECK_ISS7PRI_107093("107093", "发票请求流水号不满足发票请求批次号加00X"),
//    CHECK_ISS7PRI_107095("107095", "红票开具冲红原因长度不能超过200"),
//    CHECK_ISS7PRI_107096("107096", "红票开具时退货单号不能为空"),

    CHECK_ISS7PRI_107097("107097", "订单主体信息中编码表版本号", 1, 10, true, true),

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

//    CHECK_ISS7PRI_107121("107121", "发票请求流水号:{} 第{}行 根据商品编码查询商品简称出错 "),

    CHECK_ISS7PRI_107122("107122", "订单明细信息中成品油项目单位必填且必须为'升'或'吨'!"),

//    CHECK_ISS7PRI_107123("107123", "购买方企业类型只能为:01.企业,02.机关事业单位,03.个人,04.其他"),

    CHECK_ISS7PRI_107124("107124", "订单主体信息中清单发票项目名称", 0, 180, true, false),

    CHECK_ISS7PRI_107125("107125", "订单主体信息中清单标志只能为0:普通发票,1:普通发票（清单）,2:收购发票,3:收购发票（清单）,4:成品油发票"),

//    CHECK_ISS7PRI_107126("107126", "清单发票项目名称长度不能大于180"),

    CHECK_ISS7PRI_107127("107127", "专票冲红时备注必填"),

    CHECK_ISS7PRI_107128("107128", "订单主体信息中备注", 0, 200, true, false),

    CHECK_ISS7PRI_107129("107129", "专票冲红备注不满足格式要求，请核对"),

    CHECK_ISS7PRI_107130("107130", "``单个批次最多支持9999张发票开具"),
//    CHECK_ISS7PRI_107131("107131", "优惠政策标识不合法，只能为0或1"),

    CHECK_ISS7PRI_107132("107132", "订单明细信息中YHZCBS(优惠政策标识)为1, 且税率为0, 则LSLBS只能根据实际情况选择\"0或1或2\"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的\"出口零税/免税/不征税"),

    CHECK_ISS7PRI_107133("107133", "订单明细信息中税额须为2位小数"),

    CHECK_ISS7PRI_107134("107134", "订单明细信息中税额", 0, 25, true, false),

    CHECK_ISS7PRI_107135("107135", "订单主体信息中合计不含税金额不为0时，小数点位数须为2位小数"),

    CHECK_ISS7PRI_107136("107136", "订单主体信息中合计税额不为0时，小数点位数须为2位小数"),

    CHECK_ISS7PRI_107138("107138", "订单明细信息中零税率标识非空时，只允许传0、1、2、3"),

    CHECK_ISS7PRI_107139("107139", "订单明细信息中含税标志为0，税额必填"),

    CHECK_ISS7PRI_107140("107140", "订单明细信息中零税率标识为3（普通零税）, 则:YHZCBS填0,ZZSTSGL填空"),

    CHECK_ISS7PRI_107141("107141", "订单主体信息中价税合计", 1, 25, true, true),

//    CHECK_ISS7PRI_107047("107047", "订单主体信息中价税合计不能为0或者0.00"),

    CHECK_ISS7PRI_107142("107142", "订单主体信息中合计不含税金额", 1, 25, true, true),

    CHECK_ISS7PRI_107143("107143", "订单主体信息中合计税额", 1, 25, true, true),

    CHECK_ISS7PRI_107144("107144", "订单主体信息中项目单价不能为空且不能为0"),

    CHECK_ISS7PRI_107145("107145", "订单明细信息中项目金额", 1, 25, true, true),

    CHECK_ISS7PRI_107146("107146", "订单明细信息中税率不能为空"),

    CHECK_ISS7PRI_107147("107147", "订单明细信息中清单红字发票明细行数限制为1行"),

    CHECK_ISS7PRI_107148("107148", "订单明细信息中发票行性质为6，发票开票类型需为红字发票"),

//    CHECK_ISS7PRI_107149("107149", "清单红字发票项目名称不合法"),

    CHECK_ISS7PRI_107150("107150", "订单明细信息中清单红字发票规格型号、计量单位、项目数量、项目单价填充为空"),

    CHECK_ISS7PRI_107151("107151", "订单明细信息中项目数量不能为空且不能为0"),

//    CHECK_ISS7PRI_107152("107152", "红字发票项目数量为负数"),
//    CHECK_ISS7PRI_107153("107153", "清单红字发票对应的蓝字发票为多种税率，税率填充为空，单一税率填写蓝票对应税率"),
//    CHECK_ISS7PRI_107154("107154", "专票冲红备注对应的信息表编号不存在"),
//    CHECK_ISS7PRI_107155("107155", "专票冲红已抵扣传入原发票代码号码应为空"),
//    CHECK_ISS7PRI_107156("107156", "清单红字发票对应的蓝字发票为多种商品编码，商品编码填充为空，单一商品编码填写蓝票对应商品编码"),

    CHECK_ISS7PRI_107157("107157", "订单明细信息中收购发票商品的税率和税额不允许为空，且都必须为0"),

    CHECK_ISS7PRI_107158("107158", "可开具农产品收购发票的票种：增普票，电子票"),

//    CHECK_ISS7PRI_107159("107159", "开具农产品收购发票红票时，对应蓝字发票必须为农产品收购发票"),
//    CHECK_ISS7PRI_107160("107160", "发票备注1必填，0表示单价数量为空，1表示单价数量不为空"),

//    CHECK_ISS7PRI_107161("107161", "订单明细信息中备用字段1只允许传0或1"),

//    CHECK_ISS7PRI_107162("107162", "发票备注只允许传0或1"),

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
    
    CHECK_ISS7PRI_107279("107279", "订单主体信息中销货方编码ID", 0, 50, true, false),
    
    CHECK_ISS7PRI_107280("107280", "订单主体信息中销货方纳税人识别号", 15, 20, true, false),
    
    CHECK_ISS7PRI_107281("107281", "订单主体信息中销货方名称", 1, 100, true, false),
    
    CHECK_ISS7PRI_107282("107282", "订单主体信息中销货方地址", 1, 100, true, false),
    
    CHECK_ISS7PRI_107283("107283", "订单主体信息中销货方电话", 1, 20, true, false),
    
    CHECK_ISS7PRI_107284("107284", "订单主体信息中销货方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107285("107285", "订单主体信息中销货方银行银行账户", 0, 30, true, false),
    
    CHECK_ISS7PRI_107286("107286", "订单主体信息中购买方编码ID", 0, 50, true, false),
    
    CHECK_ISS7PRI_107287("107287", "订单主体信息中购买方纳税人识别号", 15, 20, true, false),
    
    CHECK_ISS7PRI_107288("107288", "订单主体信息中购买方名称", 1, 100, true, true),
    
    CHECK_ISS7PRI_107289("107289", "订单主体信息中购买方地址", 1, 100, true, false),
    
    CHECK_ISS7PRI_107290("107290", "订单主体信息中购买方电话", 1, 20, true, false),
    
    CHECK_ISS7PRI_107291("107291", "订单主体信息中购买方银行名称", 0, 100, true, false),
    
    CHECK_ISS7PRI_107292("107292", "订单主体信息中购买方银行银行账户", 0, 30, true, false),
    
    CHECK_ISS7PRI_107293("107293", "订单主体信息中门店号", 0, 50, true, false),
    
    CHECK_ISS7PRI_107294("107294", "订单主体信息中业务类型", 0, 5, true, false),
    
    CHECK_ISS7PRI_107295("107295", "订单批次业务来源必传", 1, 1, true, true),
    
    CHECK_ISS7PRI_107296("107296", "订单批次业务来源必传,且值必须是0|1|2"),
    
    CHECK_ISS7PRI_107297("107297", "订单主体业务属性必传", 1, 80, true, true),
    
    CHECK_ISS7PRI_107298("107298", "订单主体业务属性ID必传", 1, 50, true, true),
    
    CHECK_ISS7PRI_107299("107299", "订单主体购买方邮件地址格式不正确", 1, 50, true, true),
    
    
    /**
     * 打印点；发票打印；接口
     */
    INVOICE_PRINT_1000("1000", "打印失败"),
    
    INVOICE_PRINT_1001("1001", "打印类型有误，只能为fp、qd"),
    
    INVOICE_PRINT_1002("1002", "订单请求流水号不正确"),

    INVOICE_PRINT_1003("1003", "打印点不可为空"),
    
    INVOICE_PRINT_9999("9999", "系统异常"),
    
    
    INVOICE_SEARCH_0000("0000","查询成功"),
    
    INVOICE_SEARCH_1000("1000","查询失败"),
    
    INVOICE_SEARCH_1001("1001","查询失败,参数不正确"),
    
    INVOICE_SEARCH_9999("9999","系统异常"),
    
    /**
     * 成功
     */
    PAPER_INVOICE_NOPRINT_0000("0000","成功"),
    
    /**
     * 邮件发送成功，
     */
    SEND_EMAIL_0000("0000","成功"),
    
    /**
     * 开票点查询成功
     */
    KPDINFO_0000("0000","查询成功"),
    
    KPDINFO_1000("1000","查询失败"),
    
    KPDINFO_1001("1001","纳税人识别号不合法，长度必须为15、17、18、20"),
    
    KPDINFO_1002("1002","发票种类代码不合法，只能为0、2、41、51"),
    
    KPDINFO_1003("1003","成品油标识不合法，只能为0、1"),
    
    KPDINFO_9999("9999","系统异常"),
    
    
    
    /**
     * 邮件发送失败
     */
    SEND_EMAIL_9999("9999","失败"),
    
    /**
     * 
     */
    PAPER_INVOICE_NOPRINT_9999("9999","异常"),
    
    /**
     * 整体数据返回
     */

    HANDLE_ISSUE_202004("202004", "批量订单请求数据为空"),

    HANDLE_ISSUE_202008("202008", "``批量订单请求批次数据为空"),

    /**
     * 合法性校验
     */
    INVOICE_HJJE_ZERO_ERROR("009012", "订单主体信息中合计金额小于等于零"),

    /**
     * 价税分离
     */
    PRICE_TAX_SEPARATION_SUCCESS("9350", "价税分离后的订单信息"),

    PRICE_TAX_SEPARATION_NE_KPHJJE("9351", "价税分离后开票合计金额与订单开票合计金额不相等"),

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
    
    ORDER_IMPORT_BZ_DEAL_ERROR("9412","订单导入异常,备注处理失败"),
    
    ORDER_IMPORT_CHECK_VALID("9413","excel导入订单具体行数校验失败"),
    
    ORDER_SPLIT_JE_SL_ERROR("9414","拆分金额只有一个，并且拆分金额等于总金额"),
    
    ORDER_SPLIT_ZSL_ERROR("9415", "拆分数量大于单据总数量"),
    
    ORDER_SPLIT_MODE_ERROR("9416", "拆分模式错误"),

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

//    ORDER_ROLLBACK_StATUS_ERROR("9523", "存在非待开状态的订单，不支持回退"),

//    ORDER_ROLLBACK_DATA_NO_SUPERIOR("9526", "数据有误，拆分后订单回退失败"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR("9527", "订单部分开票中，无法回退"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK("9528", "订单有部分开票，无法回退"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE("9530", "页面直接开票，不支持回退"),

    ORDER_ROLLBACK_DATA_ORDER_STATUS("9529", "此拆分订单的同级或同级的下级订单有处于开票状态，无法回退"),

    INVOICE_MAKE_OUT_ERROR("9531", "此开票类型不存在"),

    INVOICE_DATA_BATCH_NULL("9532", "此批次不存在"),

    INVOICE_TIME_PARSEEXCEPTION("9533", "时间格式输入错误！"),

    /**
     * 自动开票
     */
    INVOICE_AUTO_NUMBER("9540", "开具发票订单数量超出限额,限额为2000"),

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
    
    INVOICE_ERROR_CODE_9564("9564", "只能作废普票和专票"),
    
    INVOICE_ERROR_CODE_9565("9565", "作废发票只能为本月发票"),
    
    INVOICE_ERROR_CODE_9566("9566", "没有查到开票限额"),
    
    INVOICE_ERROR_CODE_9567("9567", "开票限额为0，不支持开票"),
    
    INVOICE_ERROR_CODE_9568("9568", "接口方式开票，金额超出当前税盘开票限额，不允许开票"),
    
    INVOICE_ERROR_CODE_9569("9569", "开具蓝票时，原发票代码号码必须为空"),
    
    INVOICE_ERROR_CODE_9570("9570", "订单主体信息中红票的原发票代码只能是数字"),
    
    INVOICE_ERROR_CODE_9571("9571", "订单主体信息中红票的原发票号码只能是数字"),

    //------------------------end-----------------------------

    /**
     * 发票作废
     */
    INVOICE_VALID_REPEAT("9601", "发票重复作废"),

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
    INVOICE_QUERY_ERROR_V3_009562("009562", "批次对应的发票数据不存在"),
    
    INTERFACE_GETORDERANDINVOICE_STATUS_000000("000000", "开票成功"),
    INTERFACE_GETORDERANDINVOICE_STATUS_001000("001000", "订单处理成功"),
    INTERFACE_GETORDERANDINVOICE_STATUS_001999("001999", "开票异常"),

    /**
     * 合并前端提示标识
     */
    ORDER_MERGE_TS_REPEAT("9700", "合并前端提示标识"),
    ORDER_MERGE_ORDER_STATUS_ERROR("9701", "订单合并，订单状态有误"),

    
    FJH_ISNULL("20001", "当前请求的分机号为空！"),    
    FS_ISNULL("20001", "当前请求的份数为空！"),

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
    INVOICE_ERROR_CODE_114004("114004", "获取电子发票PDF,发票号码代码长度不合规"),
    INVOICE_ERROR_CODE_702001("702001", "暂不支持请求,请求批次号已存在"),

    /**
     * 发票接口相关状态 V3
     */
    INVOICE_ERROR_CODE_010000_V3("010000", "发票请求接收成功"),
    INVOICE_ERROR_CODE_010001_V3("010001", "发票开具请求数据错误，订单请求批次号已存在"),
    INVOICE_ERROR_CODE_010002_V3("010002", "发票开具请求数据错误，订单请求流水号已存在"),
    INVOICE_ERROR_CODE_010003_V3("010003", "发票开具请求数据错误，订单批次号和流水号已存在"),
    INVOICE_ERROR_CODE_010004_V3("010004", "发票开具请求税号和对应secretId不一致!"),
    INVOICE_ERROR_CODE_010005_V3("010005", "发票开具请求数据错误,业务属性ID不匹配"),
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
    INVOICE_ERROR_CODE_104006_V3("104006", "发票开具结果数据获取, 发票类型只能为1：纸票，暂不支持电子票"),
    INVOICE_ERROR_CODE_104007_V3("104007", "发票开具结果数据获取, 是否返回失败数据参数只能为0或1"),
    INVOICE_ERROR_CODE_104008_V3("104008", "发票开具结果数据获取, 发票类型只能为1：纸票，暂不支持电子票"),
    INVOICE_ERROR_CODE_204001_V3("204001", "发票开具结果数据获取，请求批次号不存在"),
    INVOICE_ERROR_CODE_204002_V3("204002", "发票开具结果获取查询成功:查询到?条信息"),
    INVOICE_ERROR_CODE_202005_V3("202005", "受理点处于停止状态,不可用"),
    INVOICE_ERROR_CODE_502001_V3("502001", "数据库查询异常"),
    INVOICE_ERROR_CODE_204003_V3("204003", "发票开具结果获取:未查询该批次号的发票信息"),
    INVOICE_ERROR_CODE_204004_V3("204004", "当前税号没有可用受理点"),
    INVOICE_ERROR_CODE_204005_V3("204005", "沒有匹配上当前受理点"),
    INVOICE_ERROR_CODE_204006_V3("204006", "当前受理点票量不足"),

    /**
     * 待开订单删除
     */
    UPDATE_ORDER_STATUS_IDS_NULL("90001","未传输订单信息标识ID"),
    UPDATE_ORDER_STATUS_ORDER_DDZT_IS_NOTTHREE("90002","已开具或者开具中的订单不允许删除"),
    UPDATE_ORDER_STATUS_QUERY_NULL("90003","未查到选中的订单信息"),

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

    /**
     * 全税接口对接状态码
     */
    INVOICE_STAT_SUCCESS("0000","接口请求成功"),
    INVOICE_STAT_ERROR("0001","接口请求失败");

    /**
     * key值
     */
    private String key;

    /**
     * 对应Message
     */
    private String message;

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
     * 长度判断
     */

    public int getminLength() {
        return this.minLength;
    }

    public int getmaxLength() {
        return this.maxLength;
    }

    public static OrderInfoContentEnum getCodeValue(String key) {
        for (OrderInfoContentEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }

    OrderInfoContentEnum(String key, String message, int minLength, int maxLength, boolean checkLength, boolean checkNull) {
        this.key = key;
        this.message = message;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkLength = checkLength;
        this.checkNull = checkNull;
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

    public boolean getCheckLenth() {
        return this.checkLength;
    }
}
