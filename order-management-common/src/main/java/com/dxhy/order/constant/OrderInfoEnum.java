package com.dxhy.order.constant;

/**
 * 订单信息枚举类
 *
 * @author ZSC-DXHY
 */
public enum OrderInfoEnum {
    
    /**
     * 清单标志0-普通发票;1-普通发票(清单);2-收购发票;3-收购发票(清单);4-成品油发票
     */
    QDBZ_CODE_0("0", "普通发票"),
    QDBZ_CODE_1("1", "普通发票(清单)"),
    QDBZ_CODE_2("2", "收购发票"),
    QDBZ_CODE_3("3", "收购发票(清单)"),
    QDBZ_CODE_4("4", "成品油发票"),
    
    /**
     * 发票行性质 0正常行，1折扣行，2被折扣行，6 清单红字发票
     */
    FPHXZ_CODE_0("0", "正常行"),
    FPHXZ_CODE_1("1", "折扣行"),
    FPHXZ_CODE_2("2", "被折扣行"),
    FPHXZ_CODE_6("6", "清单红字发票"),
    
    /**
     * 发票推送企业成功失败状态(0:初始化;1:推送成功;2:推送失败)
     */
    PUSH_STATUS_0("0", "初始化"),
    PUSH_STATUS_1("1", "推送成功"),
    PUSH_STATUS_2("2", "推送失败"),
    
    /**
     * 发票推送企业状态(0:可用;1:不可用)
     */
    PUSH_TO_ENTERPRISE_0("0", "可用"),
    PUSH_TO_ENTERPRISE_1("1", "不可用"),
    
    /**
     * 数据推送
     */
    PUSH_TO_STATUS_0("0", "全部数据回退"),
    PUSH_TO_STATUS_1("1", "部分数据回退"),
    /**
     * 加急状态
     */
    FPKJ_SFJJ_0("0", "不加急开票"),
    FPKJ_SFJJ_1("1", "加急开票"),
    /**
     * 打印状态(0:未打印;1:已打印)
     */
    PRINT_STATUS_0("0", "未打印"),
    PRINT_STATUS_1("1", "已打印"),
    
    /**
     * 打印类型(0:发票;1:清单)
     */
    PRINT_TYPE_0("0", "发票"),
    PRINT_TYPE_1("1", "清单"),
    
    /**
     * todo 后期和上面合并
     * 发票打印状态(0:待打印,1:打印中,2:打印成功,3:打印失败)
     */
    INVOICE_PRINT_STATUS_0("0", "待打印"),
    INVOICE_PRINT_STATUS_1("1", "打印中"),
    INVOICE_PRINT_STATUS_2("2", "打印成功"),
    INVOICE_PRINT_STATUS_3("3", "打印失败"),
    
    /**
     * 购货方企业类型(01:企业 02：机关事业单位 03：个人 04：其它)
     */
    GHF_QYLX_01("01", "企业"),
    GHF_QYLX_02("02", "机关事业单位"),
    GHF_QYLX_03("03", "个人"),
    GHF_QYLX_04("04", "其它"),
    
    /**
     * 优惠政策标识限定为0：不使用，1：使用
     */
    YHZCBS_0("0", "不使用"),
    YHZCBS_1("1", "使用"),
    /**
     * 含税标志
     */
    HSBZ_0("0", "不含税"),
    HSBZ_1("1", "含税"),
    /**
     * 特殊冲红标志(0:正常冲红(电子发票);1:特殊冲红(冲红纸质等))
     */
    TSCHBZ_0("0", "正常冲红(电子发票)"),
    TSCHBZ_1("1", "特殊冲红(冲红纸质等)"),
    
    /**
     * 代开标志(0:自开;1:代开)
     */
    DKBZ_0("0", "自开"),
    DKBZ_1("1", "代开"),
    
    /**
     * 零税率标识错误（空：非零税率，0：出口零税率， 1：免税，2：不征收，3:普通零税率）
     */
    LSLBS_0("0", "出口零税"),
    LSLBS_1("1", "免税"),
    LSLBS_2("2", "不征税"),
    LSLBS_3("3", "普通零税率"),
    
    
    /**
     * 发票行性质(0:正常商品行;1:折扣行;2:被折扣行)
     */
    ORDER_LINE_TYPE_0("0", "正常商品行"),
    ORDER_LINE_TYPE_1("1", "折扣行"),
    ORDER_LINE_TYPE_2("2", "被折扣行"),
    
    /**
     * 发票推送状态1000待开，1001待调税控，2100赋码成功，2101赋码失败，2000签章成功，2001签章失败
     */
    PUSH_INVOICE_STATUS_1000("1000", "待开数据"),
    PUSH_INVOICE_STATUS_1001("1001", "待调税控"),
    PUSH_INVOICE_STATUS_2100("2100", "赋码成功"),
    PUSH_INVOICE_STATUS_2101("2101", "赋码失败"),
    PUSH_INVOICE_STATUS_2000("2000", "签章成功"),
    PUSH_INVOICE_STATUS_2001("2001", "签章失败"),
    
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
    INVOICE_ERROR_CODE_7503("7503", "对应税盘正在开具，暂不支持其他请求"),
    INVOICE_ERROR_CODE_113019("113019", "本开票点发票库存不足"),
    INVOICE_ERROR_CODE_113020("113020", "本开票点成品油库存不足"),
    INVOICE_ERROR_CODE_114004("114004", "获取电子发票版式文件,发票号码代码长度不合规"),
    INVOICE_ERROR_CODE_108016("108016", "发票重复作废重复作废"),
    
    /**
     * 空白发票作废状态
     */
    BLANK_INVOICES_CODE_050000("050000", "空白发票作废成功"),
    BLANK_INVOICES_CODE_050001("050001", "空白发票作废失败"),
    
    /**
     * 订单对外接口,查询状态  0:未开具;1:开具成功;2:开具失败;
     */
    INTERFACE_GETORDERANDINVOICE_STATUS_0("0", "未开具"),
    INTERFACE_GETORDERANDINVOICE_STATUS_1("1", "开具成功"),
    INTERFACE_GETORDERANDINVOICE_STATUS_2("2", "开具失败"),
    /**
     * 纳税人识别号长度(15,17,18,20)
     */
    TAXPAYER_ID_LENGTH_15("15", "15位长度税号"),
    TAXPAYER_ID_LENGTH_17("17", "17位长度税号"),
    TAXPAYER_ID_LENGTH_18("18", "18位长度税号"),
    TAXPAYER_ID_LENGTH_20("20", "20位长度税号"),
    
    
    /**
     * Excel导入后缀
     */
    EXCEL_SUFFIX_XLSX(".xlsx", "格式化excel后缀名:xlsx"),
    EXCEL_SUFFIX_XLS(".xls", "格式化excel后缀名:xls"),
    
    /**
     * 作废标志(0:正常;1:已作废;2:作废中;3:作废失败;4:作废删除)
     */
    INVALID_INVOICE_0("0", "正常"),
    INVALID_INVOICE_1("1", "已作废"),
    INVALID_INVOICE_2("2", "作废中"),
    INVALID_INVOICE_3("3", "作废失败"),
    INVALID_INVOICE_4("4", "作废删除"),
    
    /**
     * 冲红标志(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败;(特殊说明:部分冲红只记录当前最后一次操作的记录))
     */
    RED_INVOICE_0("0", "正常"),
    RED_INVOICE_1("1", "全部冲红成功"),
    RED_INVOICE_2("2", "全部冲红中"),
    RED_INVOICE_3("3", "全部冲红失败"),
    RED_INVOICE_4("4", "部分冲红成功"),
    RED_INVOICE_5("5", "部分冲红中"),
    RED_INVOICE_6("6", "部分冲红失败"),
    
    /**
     * 批次状态(0:未开票;1:开票中;2:开票成功;3:开票异常)
     */
    ORDER_BATCH_STATUS_0("0", "未开票"),
    ORDER_BATCH_STATUS_1("1", "开票中"),
    ORDER_BATCH_STATUS_2("2", "开票成功"),
    ORDER_BATCH_STATUS_3("3", "开票异常"),
    /**
     * 开票状态,(0:初始化;1:开票中;2:开票成功;3:开票失败;)
     */
    INVOICE_STATUS_0("0", "初始化"),
    INVOICE_STATUS_1("1", "开票中"),
    INVOICE_STATUS_2("2", "开票成功"),
    INVOICE_STATUS_3("3", "开票失败"),
    
    /**
     * 推送状态,(0:未推送;1:推送中;2:推送成功;3:推送失败;)
     */
    EMAIL_PUSH_STATUS_0("0", "未推送"),
    EMAIL_PUSH_STATUS_1("1", "推送中"),
    EMAIL_PUSH_STATUS_2("2", "推送成功"),
    EMAIL_PUSH_STATUS_3("3", "推送失败"),
    
    /**
     * 发票类型(1:纸票,2:电票)
     */
    INVOICE_TYPE_1("1", "纸票"),
    INVOICE_TYPE_2("2", "电票"),
    
    /**
     * 发票种类代码（0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票）
     */
    ORDER_INVOICE_TYPE_0("0", "增值税专用发票"),
    ORDER_INVOICE_TYPE_2("2", "增值税普通发票"),
    ORDER_INVOICE_TYPE_41("41", "卷票"),
    ORDER_INVOICE_TYPE_51("51", "增值税电子普通发票"),
    
    /**
     * 统一发票类型代码
     * 增值税专用发票： 004
     * 增值税普通发票： 007
     * 增值税普通发票（电子）： 026
     * 增值税专用发票（电子）：028
     */
    ORDER_INVOICE_TYPE_004("004", "增值税专用发票"),
    ORDER_INVOICE_TYPE_007("007", "增值税普通发票"),
    ORDER_INVOICE_TYPE_026("026", "增值税普通发票（电子）"),
    ORDER_INVOICE_TYPE_028("028", "增值税专用发票（电子）"),
    
    /**
     * 开票类型（0：蓝票；1：红票）
     */
    ORDER_BILLING_INVOICE_TYPE_0("0", "蓝票"),
    ORDER_BILLING_INVOICE_TYPE_1("1", "红票"),
    
    /**
     * 二维码类型
     */
    QR_TYPE_0("0", "静态码领票"),
    QR_TYPE_1("1", "动态码领票"),
    
    /**
     * 订单来源(0:Excel导入;1:手工录入;2:ape原始订单接口;3:接口自动开票录入数据4:其他;5:静态码扫码开票 6:动态码扫码开票 7:历史数据)
     */
    ORDER_SOURCE_0("0", "Excel导入"),
    ORDER_SOURCE_1("1", "手工录入"),
    ORDER_SOURCE_2("2", "api原始订单接口"),
    ORDER_SOURCE_3("3", "接口自动开票录入数据"),
    ORDER_SOURCE_4("4", "其他"),
    ORDER_SOURCE_5("5", "静态码扫码开票"),
    ORDER_SOURCE_6("6", "动态码扫码开票"),
    ORDER_SOURCE_7("7", "历史数据"),
    ORDER_SOURCE_8("8", "供应链"),


    /**
     * 订单类型（0:原始订单,1:拆分后订单,2:合并后订单,3:系统冲红订单,4:自动开票订单,5:作废重开订单）
     */
    ORDER_TYPE_0("0", "原始订单"),
    ORDER_TYPE_1("1", "拆分后订单"),
    ORDER_TYPE_2("2", "合并后订单"),
    ORDER_TYPE_3("3", "系统冲红订单"),
    ORDER_TYPE_4("4", "自动开票订单"),
    ORDER_TYPE_5("5", "作废重开订单"),
    ORDER_TYPE_6("6","历史数据订单"),
    
    /**
     * 订单是否可见状态(0:有效;1:无效)
     */
    ORDER_VALID_STATUS_0("0", "订单有效"),
    ORDER_VALID_STATUS_1("1", "订单无效"),
    
    YHZCBS_TYPE_1("1", "优惠政策标识"),
    
    ZZSTSGL_TYPE_0("0", "增值税特殊管理"),
    
    ZZSTSGL_TYPE_1("1", "增值税特殊管理"),
    
    ZZSTSGL_TYPE_2("2", "增值税特殊管理"),
    
    LSLBS_TYPE_0("0", "零税率标识"),
    
    LSLBS_TYPE_1("1", "零税率标识"),
    
    LSLBS_TYPE_2("2", "零税率标识"),
    
    LSLBS_TYPE_3("3", "零税率标识"),
    
    SL_TYPE_0("2", "税率"),
    
    /**
     * 订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中;11.删除状态）
     */
    
    ORDER_STATUS_0("0", "初始化"),
    ORDER_STATUS_1("1", "拆分后"),
    ORDER_STATUS_2("2", "合并后"),
    ORDER_STATUS_3("3", "待开具"),
    ORDER_STATUS_4("4", "开票中"),
    ORDER_STATUS_5("5", "开票成功"),
    ORDER_STATUS_6("6", "开票失败"),
    ORDER_STATUS_7("7", "冲红成功"),
    ORDER_STATUS_8("8", "冲红失败"),
    ORDER_STATUS_9("9", "冲红中"),
    ORDER_STATUS_10("10", "自动开票中"),
    ORDER_STATUS_11("11", "删除状态"),
    
    /**
     * 不作为入库使用,只是用于数据接收校验
     */
    ORDER_STATUS_99("99", "订单接收处理中"),
    
    /**
     * 订单拆分类型
     */
    ORDER_SPLIT_ITEM("3", "根据明细拆分"),
    ORDER_SPLIT_OVERLIMIT_DJ("2", "超限额保单价拆分"),
    ORDER_SPLIT_OVERLIMIT_JE("0", "超限额保金额拆分"),
    ORDER_SPLIT_OVERLIMIT_SL("1", "超限额保数量拆分"),
    ORDER_SPLIT_JE_ARRAY("4", "按照多个金额拆分"),
    ORDER_SPLIT_SL_ARRAY("5", "按照多个数量拆分"),
    /**
     * 拆分模式
     */
    ORDER_SPLIT_MODE_1("1", "按照金额超限额拆分"),
    ORDER_SPLIT_MODE_2("2", "按金额拆分成两个订单"),
    
    /**
     * 预警说明
     */
    ORDER_WARNING_CLOSE("0", "关闭"),
    ORDER_WARNING_OPEN("1", "开启"),
    
    /**
     * 税控设备说明(000:未配置;001:金税盘托管;002:金税盘A9托管;004:税控盘托管;005:百望服务器;006:本地税控盘;007:本地金税盘;008:百望服务器ActiveX;009:税控服务器;010:税控服务器UKey;011:本地UKey)
     */
    TAX_EQUIPMENT_UNKNOW("000", "未配置"),
    TAX_EQUIPMENT_C48("001", "金税盘托管"),
    TAX_EQUIPMENT_A9("002", "金税盘A9托管"),
    TAX_EQUIPMENT_BWPZ("004", "税控盘托管"),
    TAX_EQUIPMENT_BWFWQ("005", "百望服务器"),
    TAX_EQUIPMENT_FGBW("006", "本地税控盘"),
    TAX_EQUIPMENT_FGHX("007", "本地金税盘"),
    TAX_EQUIPMENT_BW_ACTIVEX("008", "百望服务器active-x"),
    TAX_EQUIPMENT_NEWTAX("009", "税控服务器"),
    TAX_EQUIPMENT_UKEY("010", "税控服务器UKey"),
    TAX_EQUIPMENT_FGUKEY("011", "本地UKey"),
    
    /**
     * 接口对象，通知汇总类型
     */
    INVOICE_TAXRATE_SUMMARY("1", "发票税率汇总"),
    INVOICE_ITEM_SUMMARY("2", "发票项目汇总"),
    
    /**
     * 生成待开数据业务来源
     */
    /**
     * 生成待开数据业务来源
     */
    READY_ORDER_SJLY_0("0", "拆分后订单"),
    READY_ORDER_SJLY_1("1", "合并后订单"),
    READY_ORDER_SJLY_2("2", "页面开票来源订单"),
    READY_ORDER_SJLY_3("3", "红票订单"),
    READY_ORDER_SJLY_4("4", "订单编辑后开票"),
    READY_ORDER_SJLY_5("5", "复制开票"),
    READY_ORDER_SJLY_6("6", "异常订单重新开票"),
    
    /**
     * 订单开票请求批次
     * 企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0
     */
    ORDER_REQUEST_TYPE_0("0", "自动开票"),
    ORDER_REQUEST_TYPE_1("1", "手动开票"),
    ORDER_REQUEST_TYPE_2("2", "静态码开票"),
    ORDER_REQUEST_TYPE_3("3", "动态码开票"),
    /**
     * 不做业务逻辑判断,只是页面开票的一个标识
     */
    ORDER_REQUEST_TYPE_9("9", "页面开票"),
    
    /**
     * 订单开票请求批次
     * 是否是成品油(0:非成品油;1:成品油),默认为0
     */
    ORDER_REQUEST_OIL_0("0", "非成品油"),
    ORDER_REQUEST_OIL_1("1", "成品油"),
    
    /**
     * 成品油标识,0:非成品油,1:经销成品油,2:生产成品油
     */
    OIL_TYPE_0("0", "非成品油"),
    OIL_TYPE_1("1", "经销成品油"),
    OIL_TYPE_2("2", "生产成品油"),
    
    /**
     * 终端类型请求头枚举类
     */
    TAX_EQUIPMENT_HEAD_BWFWQ("bwfwq", "百望服务器"),
    TAX_EQUIPMENT_HEAD_BWPZ("bwpz", "税控盘托管"),
    TAX_EQUIPMENT_HEAD_A9("A9", "金税盘A9托管"),
    TAX_EQUIPMENT_HEAD_ACTIVEX("active-x", "active-x"),
    TAX_EQUIPMENT_HEAD_C48("C48", "金税盘托管"),
    TAX_EQUIPMENT_HEAD_NEWTAX("newtax", "税控服务器"),
    TAX_EQUIPMENT_HEAD_UKEY("ukey", "税控服务器Ukey"),
    TAX_EQUIPMENT_HEAD_FG("fgkp", "方格"),

    /**
     * 异常订单开具类型
     */
    EXCEPTION_ORDER_REINVOICE_TYPE_1("1", "异常订单原数据重新开票"),
    EXCEPTION_ORDER_REINVOICE_TYPE_2("2", "异常订单编辑后开票"),
    
    /**
     * 接口推送类型
     */
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_1("1", "发票开具推送"),
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_2("2", "作废状态推送"),
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_3("3", "扫码开票推送"),
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_5("5", "供应链待审核订单推送"),
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_6("6", "红字申请单撤销状态回推"),

    INTERFACE_TYPE_INVOICE_PUSH_STATUS_51("51", "上传下载推送"),
    INTERFACE_TYPE_INVOICE_PUSH_STATUS_52("52", "打印推送"),

    /**
     * 接口协议类型
     */
    INTERFACE_PROTOCAL_TYPE_HTTP("0", "http"),
    INTERFACE_PROTOCAL_TYPE_WEBSERVICE("1", "webservice"),

    /**
     * 发票作废类型
     */
    ZFLX_0("0", "空白发票作废类型"),
    ZFLX_1("1", "正数发票作废"),
    ZFLX_2("2", "负数发票作废"),
    
    /**
     * 开票点启用状态
     */
    KPD_QYZT_1("1", "启用"),
    KPD_QYZT_2("2", "删除"),
    
    /**
     * 是否合并同类项标识
     */
    HB_MX_0("0", "合并"),
    HB_MX_1("1", "不合并"),
    
    /**
     * 成品油标志
     */
    PUSH_INVOICE_SFCPY_0("0", "非成品油"),
    PUSH_INVOICE_SFCPY_1("1", "成品油"),
    
    
    /**
     * 作废类型
     */
    
    INVOICE_VALID_ZFLX_0("0", "空白发票作废"),
    INVOICE_VALID_ZFLX_1("1", "已开发票作废"),
    
    /**
     * 订单导入中间状态
     */
    IMPORT_EXCEL_5000("5000", "订单导入中"),
    IMPORT_EXCEL_9999("9999", "订单导入失败"),
    IMPORT_EXCEL_9991("9991", "excel文件为空"),
    IMPORT_EXCEL_9992("9992", "订单购方信息校验失败"),
    IMPORT_EXCEL_0000("0000", "订单导入成功"),
    IMPORT_EXCEL_9998("9998", "订单校验成功，订单拆入数据库失败"),
    
    /**
     * 红字申请单申请原因
     */
    SPECIAL_INVOICE_REASON_1100000000("1100000000", "购买方申请:已抵扣"),
    SPECIAL_INVOICE_REASON_1010000000("1010000000", "购买方申请:未抵扣"),
    SPECIAL_INVOICE_REASON_0000000100("0000000100", "销售方申请:因发票有误购买方拒收的,因开票有误等原因尚未交付的"),
    
    /**
     * 红字申请单申请类型
     * 申请单类型0:正常;1:成品油-销售数量变更2:成品油-销售金额变更;3成品油-其他
     */
    SPECIAL_INVOICE_TYPE_0("0", "正常"),
    SPECIAL_INVOICE_TYPE_1("1", "成品油-销售数量变更"),
    SPECIAL_INVOICE_TYPE_2("2", "成品油-销售金额变更"),
    SPECIAL_INVOICE_TYPE_3("3", "成品油-其他"),
    
    /**
     * 红字信息表下载状态(0:初始化;1:下载中;2:下载完成:)
     */
    SPECIAL_INVOICE_DOWNLOAD_TYPE_0("0", "初始化"),
    SPECIAL_INVOICE_DOWNLOAD_TYPE_1("1", "下载中"),
    SPECIAL_INVOICE_DOWNLOAD_TYPE_2("2", "下载完成"),
    
    
    /**
     * 红字申请单营业税标志
     * 0000000010：1.5%税率
     * 0000000020：差额税
     * 0000000060：农产品收购
     * 0000000090：成品油
     * 0000000000：其他
     */
    SPECIAL_YYSBZ_0000000010("0000000010", "1.5%税率"),
    SPECIAL_YYSBZ_0000000020("0000000020", "差额税"),
    SPECIAL_YYSBZ_0000000060("0000000060", "农产品收购"),
    SPECIAL_YYSBZ_0000000090("0000000090", "成品油"),
    SPECIAL_YYSBZ_0000000000("0000000000", "其他"),
    
    
    /**
     * 红字申请单状态(上报状态 TZD0000:审核通过;TZD0500:未上传;TZD0061:重复上传;TZD0071:待查证;TZD0072:已核销，待查证;TZD0073:已核销,查证未通过,待处理;TZD0074:已核销;
     * TZD0075:核销后激活;TZD0076:已核销,查证未通过,处理中;TZD0077:已核销,查证未通过,已处理;TZD0078:核销未通过，待处理;TZD0079:核销未通过，处理中;
     * TZD0080:核销未通过，已处理;TZD0082:已撤销;TZD0083:已作废)
     */
    SPECIAL_INVOICE_STATUS_TZD0000("TZD0000", "审核通过"),
    SPECIAL_INVOICE_STATUS_TZD1000("TZD1000", "审核通过"),
    SPECIAL_INVOICE_STATUS_TZD0500("TZD0500", "未上传"),
    SPECIAL_INVOICE_STATUS_TZD0061("TZD0061", "重复上传"),
    SPECIAL_INVOICE_STATUS_TZD0071("TZD0071", "待查证"),
    SPECIAL_INVOICE_STATUS_TZD0072("TZD0072", "已核销，待查证"),
    SPECIAL_INVOICE_STATUS_TZD0073("TZD0073", "已核销,查证未通过,待处理"),
    SPECIAL_INVOICE_STATUS_TZD0074("TZD0074", "已核销"),
    SPECIAL_INVOICE_STATUS_TZD0075("TZD0075", "核销后激活"),
    SPECIAL_INVOICE_STATUS_TZD0076("TZD0076", "已核销,查证未通过,处理中"),
    SPECIAL_INVOICE_STATUS_TZD0077("TZD0077", "已核销,查证未通过,已处理"),
    SPECIAL_INVOICE_STATUS_TZD0078("TZD0078", "核销未通过，待处理"),
    SPECIAL_INVOICE_STATUS_TZD0079("TZD0079", "核销未通过，处理中"),
    SPECIAL_INVOICE_STATUS_TZD0080("TZD0080", "核销未通过，已处理"),
    SPECIAL_INVOICE_STATUS_TZD0082("TZD0082", "已撤销"),
    SPECIAL_INVOICE_STATUS_TZD0083("TZD0083", "已作废"),
    /**
     * 方格上传税局失败
     */
    SPECIAL_INVOICE_STATUS_TZD9998("TZD9998", "上传税局失败"),
    SPECIAL_INVOICE_STATUS_B900076("B900076", "没有满足条件的待下载数据"),
    
    
    /**
     * 专票开票状态(状态 0:已提交开票申请;1:已开票;2:未开票)
     */
    SPECIAL_INVOICE_STATUS_0("0", "未开票"),
    SPECIAL_INVOICE_STATUS_1("1", "开票中"),
    SPECIAL_INVOICE_STATUS_2("2", "开票成功"),
    SPECIAL_INVOICE_STATUS_3("3", "开票失败"),
    
    /**
     * 手工开票类型
     */
    INVOICE_BILLING_CZLX_0("0", "开票"),
    INVOICE_BILLING_CZLX_1("1", "暂存"),
    
    /**
     * 动态码状态码(0:二维码状态正常;1:二维码已作废;2:二维码已过期;3:二维码已开票;4:二维码开票异常;5:二维码开票中;)
     */
    QUICK_RESPONSE_CODE_STATUS_0("0", "二维码状态正常"),
    QUICK_RESPONSE_CODE_STATUS_1("1", "二维码已作废"),
    QUICK_RESPONSE_CODE_STATUS_2("2", "二维码已过期"),
    QUICK_RESPONSE_CODE_STATUS_3("3", "二维码已开票"),
    QUICK_RESPONSE_CODE_STATUS_4("4", "二维码开票异常"),
    QUICK_RESPONSE_CODE_STATUS_5("5", "二维码开票中"),
    
    /**
     * 开票类型
     */
    INVOICE_BILLING_TYPE_0("0", "正数发票"),
    INVOICE_BILLING_TYPE_1("1", "负数发票"),

    /**
     * 供应链推送类型
     */
    SUPPLY_CHINA_PUSH_TYPE_0("0", "推送待审核订单"),
    SUPPLY_CHINA_PUSH_TYPE_1("1", "推送发票数据"),
    
    /**
     * 供应链发票审核状态
     */
    CHECK_STATUS_0("0", "初始化状态"),
    CHECK_STATUS_1("1", "待审核状态"),
    CHECK_STATUS_2("2", "审核通过"),
    CHECK_STATUS_3("3", "审核驳回"),
    
    /**
     * 方格开票特殊票种
     * “00”不是
     * “01”农产品销售
     * “02”农产品收购
     */
    FANGGE_TSPZ_00("00", "不是特殊票种"),
    FANGGE_TSPZ_01("01", "农产品销售"),
    FANGGE_TSPZ_02("02", "农产品收购"),
    
    /**
     * 方格税率标识
     * 0是普通征收，1是减按计增，2是差额征收
     */
    FANGGE_HSSLBS_0("0", "普通征收"),
    FANGGE_HSSLBS_1("1", "减按计增"),
    FANGGE_HSSLBS_2("2", "差额征收"),
    
    /**
     * 异常订单编辑状态
     */
    EDIT_STATUS_0("0", "初始化状态"),
    EDIT_STATUS_1("1", "已编辑状态"),
    
    /**
     * 接口操作类型（0:新增,1:更新,2:删除）
     */
    INTERFACE_CZLX_0("0", "新增"),
    INTERFACE_CZLX_1("1", "更新"),
    INTERFACE_CZLX_2("2", "删除"),
    
    /**
     * 数据库删除状态(0:未删除;1:删除)
     */
    DATE_DELETE_STATUS_0("0", "未删除"),
    DATE_DELETE_STATUS_1("1", "已删除"),
    
    /**
     * 发票开具结果状态
     */
    INVOICE_QUERY_STATUS_2101("2101", "赋码失败"),
    INVOICE_QUERY_STATUS_2100("2100", "赋码成功"),
    INVOICE_QUERY_STATUS_2001("2001", "签章失败"),
    INVOICE_QUERY_STATUS_2000("2000", "签章成功"),
    INVOICE_QUERY_STATUS_4001("4001", "未知异常"),
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
    
    OrderInfoEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public static OrderInfoEnum getCodeValue(String key) {
        
        for (OrderInfoEnum item : values()) {
            if (item.getKey().equals(key)) {
                return item;
            }
        }
        return null;
    }
}
