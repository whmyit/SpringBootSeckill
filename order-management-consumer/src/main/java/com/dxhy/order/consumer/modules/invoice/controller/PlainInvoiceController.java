package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.api.ApiInvoiceService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.invoice.service.C48RedInvoiceService;
import com.dxhy.order.consumer.modules.invoice.service.PlainInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInvoiceAndOrderMxInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.dy.DyResponse;
import com.dxhy.order.model.entity.PrintEntity;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.dxhy.order.utils.RMBUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述信息：纸质发票控制层
 *
 * @author 谢元强
 * @date Created on 2018-07-21
 */
@Slf4j
@RestController
@Api(value = "纸质发票", tags = {"发票模块"})
@RequestMapping("/plainInvoice")
public class PlainInvoiceController {
    private static final String LOGGER_MSG = "纸质发票控制层";
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Resource
    private PlainInvoiceService plainInvoiceService;
    
    @Resource
    private C48RedInvoiceService redInvoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * todo 需要优化
     *
     * @param invoiceCode 发票代码 invoiceNo 发票号码
     * @Description 根据发票代码发票号码查看发票版式文件
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/query")
    @ApiOperation(value = "纸票预览", notes = "纸票管理-纸票预览")
    @SysLog(operation = "纸票预览发票", operationDesc = "纸票预览发票", key = "纸票管理")
    public R query(@RequestParam String invoiceCode, @RequestParam String invoiceNo) {
        log.info("{}根据发票代码发票号码查看发票版式文件 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
        Map<String, Object> map = new HashMap<>(5);
        map.put("invoiceCode", invoiceCode);
        map.put("invoiceNo", invoiceNo);
        if (StringUtils.isEmpty(invoiceCode) || StringUtils.isEmpty(invoiceNo)) {
            log.info("{}参数错误", LOGGER_MSG);
            return R.error(ConfigureConstant.STRING_9999, "参数错误");
        }
        log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
        //纳税人识别号
        List<String> shList = userInfoService.getTaxpayerCodeList();
        shList = shList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, shList);
        if (commonOrderInvoiceAndOrderMxInfo == null) {
            log.info("{}发票信息信息不存在 ");
            return R.error("发票信息不存在");
        }
        // 开票状态,(0:初始化;1:开票中;2:开票成功;3:开票失败;)',
        String kpzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKpzt();
        if (!OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
            log.info("{} 发票代码{} 发票号码{} 开票状态不为开票成功 状态为{} ", LOGGER_MSG, invoiceCode, invoiceNo, kpzt);
            return R.error("该发票开票状态异常");
        }
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getFpzlDm())) {
            log.info("{} 发票代码{} 发票号码{} 发票类型不符 ", LOGGER_MSG, invoiceCode, invoiceNo);
            return R.error("该发票发票类型异常");
        }
    
        //判断发票是否可以冲红 开票类型（0：蓝票；1：红票）
        String kplx = commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getKplx();
        if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(kplx)) {
            //红票增加原因
            String redBz = "对应正数发票代码：" + commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getYfpDm() + "号码："
                    + commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getYfpHm() + commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getBz();
            commonOrderInvoiceAndOrderMxInfo.getOrderInfo().setBz(redBz);
        }
        //冲红标志(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败
        String chbz = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getChBz();
        //作废标志(0:正常;1:已作废;
        String zfzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getZfBz();
    
        String sykchje = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getSykchje();
        if (StringUtils.isBlank(sykchje)) {
            sykchje = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKphjje();
        }
        //按钮显示 0 显示冲红按钮  1 不可以显示冲红按钮
        String show = "0";
        //状态 显示 0 正常  1 已冲红 2 已作废
        String flag = "0";
        if (ConfigureConstant.STRING_1.equals(zfzt)) {
            //已作废 不可已冲红
            log.info("{} 发票代码{} 发票号码{} 发票已作废 ", LOGGER_MSG, invoiceCode, invoiceNo);
            show = "1";
            flag = "2";
        } else if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(kplx)) {
            //红票
            show = "1";
            flag = "0";
            //(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败
        } else if (!ConfigureConstant.STRING_0.equals(chbz)) {
            //已冲红且冲红
            if (Double.parseDouble(sykchje) <= 0) {
                log.info("{} 发票代码{} 发票号码{} 发票已冲红,且剩余可冲红金额为空 ", LOGGER_MSG, invoiceCode, invoiceNo);
                show = "1";
                flag = "1";
            } else {
                log.info("{} 发票代码{} 发票号码{} 发票已冲红,且剩余可冲红金额{} ", LOGGER_MSG, invoiceCode, invoiceNo, sykchje);
                show = "0";
                flag = "1";
            }
        } else {
            show = "0";
            flag = "0";
        }
        map.put("show", show);
        map.put("flag", flag);
        BigDecimal kphjje = new BigDecimal(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getKphjje());
        String rmbString = RMBUtils.number2CnMontrayUnit(kphjje);
        String kprq = DateUtils.getDateStr(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKprq(), "yyyy年MM月dd日");
        map.put("rmbString", rmbString);
        map.put("kprq", kprq);
    
        return R.ok().put("data", commonOrderInvoiceAndOrderMxInfo).put("info", map);
    }
    
    /**
     * @param printEntity
     * @Description 打印纸质发票
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21 update by ysy 兼容多税控设备
     */
    @PostMapping("/printInvoice")
    @ApiOperation(value = "纸票打印", notes = "纸票管理-纸票打印")
    @SysLog(operation = "纸票打印发票", operationDesc = "纸票打印发票", key = "纸票管理")
    public R printInvoice(@RequestBody PrintEntity printEntity) {
        /**
         * ids中数据格式为:
         * [{
         *     "id":"123123123",
         *     "xhfNsrsbh":"2323"
         * }]
         */
        if (printEntity == null) {
            return R.error(ConfigureConstant.STRING_9999, "参数错误");
        }
        log.info("{}打印发票或清单 参数{}", LOGGER_MSG, printEntity.toString());
        //发票id
        List<Map> ids = printEntity.getIds();
        //打印类型
        String printType = printEntity.getPrintType();
        //打印点
        String printId = printEntity.getPrintId();
        if (StringUtils.isEmpty(printType)) {
            log.warn("{}打印类型为空", LOGGER_MSG);
            return R.error(ConfigureConstant.STRING_9999, "打印类型不可为空");
        }
        if (StringUtils.isEmpty(printId)) {
            log.warn("{}打印点为空", LOGGER_MSG);
            return R.error(ConfigureConstant.STRING_9999, "打印点不可为空");
        }
        if (ids == null || ids.size() <= 0) {
            log.warn("{}参数错误", LOGGER_MSG);
            return R.error(ConfigureConstant.STRING_9999, "参数错误");
        }
        try {
            DyResponse result = plainInvoiceService.printInvoice(printEntity);
            if (ConfigureConstant.STRING_0000.equals(result.getCode())) {
                log.info("{}纸票打印成功", LOGGER_MSG);
                log.info("{}更新发票打印状态", LOGGER_MSG);
                plainInvoiceService.updateInvoiceDyztById(ids);
                return R.ok(result.getMsg());
            } else {
                return R.error(result.getCode(), result.getMsg());
            }
        } catch (OrderReceiveException e) {
            log.error("发票打印业务异常:{}", e);
            return R.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("发票打印业务异常:{}", e);
            return R.error("业务处理异常");
        
        }
    }

    
    /**
     * @Description 发票打印列表
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryInvoiceList")
    @ApiOperation(value = "纸票列表", notes = "纸票管理-纸票列表")
    @SysLog(operation = "纸票列表", operationDesc = "纸票列表", key = "纸票管理")
    public R queryInvoiceList(HttpServletRequest request,
                              @ApiParam(name = "fpdm", value = "发票号码", required = false) @RequestParam("fpdm") String fpdm,
                              @ApiParam(name = "fphmStart", value = "发票起号", required = false) @RequestParam("fphmStart") String fphmStart,
                              @ApiParam(name = "fphmEnd", value = "发票止号", required = false) @RequestParam("fphmEnd") String fphmEnd,
                              @ApiParam(name = "kplx", value = "开票类型", required = false) @RequestParam("kplx") String kplx,
                              @ApiParam(name = "ddh", value = "订单号", required = false) @RequestParam("ddh") String ddh,
                              @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam("startTime") String startTime,
                              @ApiParam(name = "endTime", value = "结束时间", required = true) @RequestParam("endTime") String endTime,
                              @ApiParam(name = "ghfMc", value = "购买方名称", required = false) @RequestParam("ghfMc") String ghfMc,
                              @ApiParam(name = "fpzlDm", value = "发票种类代码", required = false) @RequestParam("fpzlDm") String fpzlDm,
                              @ApiParam(name = "fpzlDms", value = "发票种类代码多个", required = false) @RequestParam(value = "fpzlDms", required = false) String fpzlDms,
                              @ApiParam(name = "xhfNsrsbh", value = "销售方纳税人识别号", required = true) @RequestParam("xhfNsrsbh") String xhfNsrsbh,
                              @ApiParam(name = "pageSize", value = "每页显示个数", required = true) @RequestParam("pageSize") String pageSize,
                              @ApiParam(name = "currPage", value = "当前页数", required = true) @RequestParam("currPage") String currPage,
                              @ApiParam(name = "dyzt", value = "打印状态", required = true) @RequestParam("dyzt") String dyzt,
                              @ApiParam(name = "fjh", value = "分机号", required = false) @RequestParam(value = "fjh", required = false) String fjh,
                              @ApiParam(name = "sld", value = "受理点", required = false) @RequestParam(value = "sld", required = false) String sld,
                              @ApiParam(name = "qdbz", value = "清单标志", required = false) @RequestParam(value = "qdbz", required = false) String qdbz) {
        log.debug("{},发票查询列表入参 --ghfMc:{},fpzlDm:{},startTime:{},endTime:{},fpdm{},kplx{},ddh{},fphmStart:{} fphmEnd:{} xhfNsrsbh:{}qdbz:{}", LOGGER_MSG, ghfMc, fpzlDm, startTime, endTime, fpdm, kplx, ddh, fphmStart, fphmEnd, xhfNsrsbh, qdbz);
        Map<String, Object> paramMap = new HashMap<>(5);
        SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        try {
            if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
                return R.error(OrderInfoContentEnum.PARAM_NULL.getKey(), OrderInfoContentEnum.PARAM_NULL.getMessage());
            } else {
                Date starttime = sf2.parse(startTime);
                Date endtime = sf2.parse(endTime);
                if (starttime.after(endtime)) {
                    log.error("{}开始时间不能大于结束时间", LOGGER_MSG);
                    return R.error("开始时间不能大于结束时间");
                }
                paramMap.put("startTime", startTime);
                paramMap.put("endTime", endTime);
            
            }
        } catch (ParseException e) {
            log.error("{}时间转换异常", LOGGER_MSG);
            return R.error("时间转换异常");
        }
    
        if (StringUtils.isNotBlank(ghfMc)) {
            paramMap.put("ghfMc", ghfMc);
        }
        if (StringUtils.isNotBlank(fpzlDm)) {
            paramMap.put("fpzlDm", fpzlDm);
        }
        if (StringUtils.isNotBlank(fpdm)) {
            paramMap.put("fpdm", fpdm);
        }
    
        if (StringUtils.isNotBlank(kplx)) {
            paramMap.put("kplx", kplx);
        }
        if (StringUtils.isNotBlank(ddh)) {
            paramMap.put("ddh", ddh);
        }
    
        /**
         * 发票起号或者是发票止号为空,则把起号或者是止号给发票号码赋值.起号和止号赋值为空
         * 2.发票起号或者是止号同时有值或者是同时无值,则不作处理,
         */
        if (StringUtils.isNotBlank(fphmStart)) {
            paramMap.put("fphmStart", fphmStart);
        }
        if (StringUtils.isNotBlank(fphmEnd)) {
            paramMap.put("fphmEnd", fphmEnd);
        }
        if (StringUtils.isNotBlank(fphmStart) && StringUtils.isNotBlank(fphmEnd)) {
            if (fphmStart.compareTo(fphmEnd) > 0) {
                return R.error("发票起号不能大于发票止号");
            }
        }
    
        if (StringUtils.isNotBlank(pageSize)) {
            paramMap.put("pageSize", Integer.parseInt(pageSize));
        }
        if (StringUtils.isNotBlank(currPage)) {
            paramMap.put("currPage", Integer.parseInt(currPage));
        }
        paramMap.put("zfbz", ConfigureConstant.STRING_0);
        if (StringUtils.isNotBlank(dyzt)) {
            paramMap.put("dyzt", dyzt);
        }
        /**
         * 清单标志,前端传0或1
         * 0:不带清单
         * 1:带清单
         * 如果为空不进行筛选
         * 对应数据库操作,
         * 如果为0,查询清单标志为0,2的
         * 如果为1,查询清单标志为1,3的
         */
        if (StringUtils.isNotBlank(qdbz)) {
            String[] qdbzs = JsonUtils.getInstance().fromJson(qdbz, String[].class);
            paramMap.put("qdbz", qdbzs);
        }
        /**
         * 分机号
         */
        if (StringUtils.isNotBlank(fjh)) {
            paramMap.put("fjh", fjh);
        }
    
        /**
         * 受理点id即可
         */
        if (StringUtils.isNotBlank(sld)) {
            paramMap.put("sld", sld);
        }
        if (StringUtils.isNotBlank(fpzlDms)) {
            List<String> fpzlList = JsonUtils.getInstance().parseObject(fpzlDms, List.class);
            paramMap.put("fpzlList", fpzlList);
        }
    
        log.info("{}调用订单系统的发票列表查询接口  参数{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(paramMap));
        PageUtils pageUtils = apiOrderInvoiceInfoService.selectInvoiceByOrder(paramMap, shList);
        log.info("{}调用订单系统的发票列表查询接口   返回数据 {}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(pageUtils));
        return R.ok().put("data", pageUtils);
    }
    
    /**
     * @param invoiceCode 发票代码 invoiceNo 发票号码
     * @Description 根据发票代码发票号码查看发票版式文件
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryRedInvoice")
    @ApiOperation(value = "纸票查询红票", notes = "纸票管理-纸票查询红票数据")
    @SysLog(operation = "查询红字发票数据", operationDesc = "查询红字发票数据", key = "发票管理")
    public R queryRedInvoice(@RequestParam String invoiceCode, @RequestParam String invoiceNo,
                             @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh) {
        log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
        if (StringUtils.isEmpty(invoiceCode) || StringUtils.isEmpty(invoiceNo)) {
            log.info("{}参数错误", LOGGER_MSG);
            return R.error(ConfigureConstant.STRING_9999, "参数错误");
        }
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
        Map<String, Object> map = redInvoiceService.mergeSpecialInvoiceAndReversal(invoiceCode, invoiceNo, shList);
        log.info("{}根据发票代码发票号码查看发票版式文件 ", LOGGER_MSG);
        if (ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.CODE))) {
            return R.ok().put("data", map.get("data"));
        } else {
            return R.error(map.get(OrderManagementConstant.CODE).toString(), map.get("msg").toString());
        }
    }
    
    /**
     * 打印测试
     *
     * @param dydbs  打印点标识
     * @param dyfpzl 打印发票种类 0 专票 2普票
     * @param dylx   清单类型  1 发票  2 清单
     * @return
     */
    @PostMapping("/printTest")
    @ApiOperation(value = "纸票打印测试", notes = "纸票管理-纸票打印测试")
    @SysLog(operation = "纸票打印测试", operationDesc = "纸票打印测试", key = "纸票管理")
    public R printTest(@RequestParam String dydbs, @RequestParam String dyfpzl, @RequestParam String dylx) {
        log.info("打印测试接口开始执行 参数  dydbs= {} ,  dyfpzl= {} , dylx = {} ); ", dydbs, dyfpzl, dylx);
        
        if (!StringUtils.isNotBlank(dydbs)) {
            return R.error().put("msg", "请求参数打印点标识为空,请选择打印点");
        }
        if (!StringUtils.isNotBlank(dyfpzl)) {
            return R.error().put("msg", "请求参数发票类型为空,请选择发票类型");
        }
        if (!StringUtils.isNotBlank(dylx)) {
            return R.error().put("msg", "请求参数打印类型为空,请选择打印类型");
        }
        return plainInvoiceService.printTest(dydbs, dyfpzl, dylx);
    }
    
    
}
