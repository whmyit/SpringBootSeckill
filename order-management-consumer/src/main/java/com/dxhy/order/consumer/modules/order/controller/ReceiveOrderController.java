package com.dxhy.order.consumer.modules.order.controller;

import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiOrderInfoService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.consumer.model.PageOrderInfo;
import com.dxhy.order.consumer.model.PageOrderItemInfo;
import com.dxhy.order.consumer.model.page.PageSld;
import com.dxhy.order.consumer.modules.order.service.ExcelReadService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.consumer.utils.InterfaceResponseUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderItemInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：ReceiveOrderController
 * @Description ：订单接收controller
 * @date ：2018年7月20日 下午2:31:24
 */
@Api(value = "订单接收", tags = {"订单模块"})
@RestController
@RequestMapping("/receiveOrder")
@Slf4j
public class ReceiveOrderController {

    private static final String LOGGER_MSG = "(订单接收接口)";

    @Resource
    private MakeOutAnInvoiceService makeOutAnInvoiceService;

    @Resource
    private IGenerateReadyOpenOrderService generateReadyOpenOrderService;

    @Resource
    private ExcelReadService excelReadService;

    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;

    @Reference
    private ApiOrderInfoService orderInfoService;

    @Reference
    private ApiOrderInvoiceInfoService orderInvoiceInfoService;

    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonMapperService;


    /**
     * 用于页面录入订单信息的接口
     *
     * @param orderInfoList
     * @return
     */
    @PostMapping("/page")
    @ApiOperation(value = "手工填开", notes = "订单接收-根据页面录入的信息创建订单信息并开具开具发票")
    @SysLog(operation = "手工填开rest接口", operationDesc = "根据页面录入的信息创建订单信息并开具开具发票", key = "订单接收")
    @SuppressWarnings("unchecked")
    public R acceptByPage(
            @ApiParam(name = "orderList", value = "orderList", required = true) @RequestBody PageOrderInfo orderInfoList) {

        log.info("{}页面订单信息录入的接口，前端传入的参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(orderInfoList));
        R vo = new R();
        if (StringUtils.isNotBlank(orderInfoList.getXhfNsrsbh())) {
            List<String> list = JsonUtils.getInstance().parseObject(orderInfoList.getXhfNsrsbh(), List.class);
            orderInfoList.setXhfNsrsbh(list.get(0));
        }

        /**
         * 前端对象转换
         */
        CommonOrderInfo commonOrderInfo = pageToFpkjInfo(orderInfoList);

        /**
         * 根据税号查询税控设备
         */
        String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInfoList.getXhfNsrsbh());
        if (terminalCode == null) {
            return R.error().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put(OrderManagementConstant.MESSAGE, "税控设未配置");
        }

        /**
         * 特殊字符处理
         */
        BeanTransitionUtils.replaceCharacter(commonOrderInfo);

        /**
         * 数据拆分与补全
         */
        commonOrderInfo.setTerminalCode(terminalCode);
        String sld = orderInfoList.getSld();
        String sldmc = orderInfoList.getSldmc();
        commonOrderInfo.setSjywly(OrderInfoEnum.READY_ORDER_SJLY_2.getKey());


        R excuSingle = generateReadyOpenOrderService.excuSingle(commonOrderInfo, orderInfoList.getUid());
        if (!excuSingle.get(OrderManagementConstant.CODE).equals(OrderInfoContentEnum.SUCCESS.getKey())) {
            return R.error().put(OrderManagementConstant.CODE, excuSingle.get(OrderManagementConstant.CODE))
                    .put(OrderManagementConstant.MESSAGE, excuSingle.get(OrderManagementConstant.MESSAGE));
        }
        List<CommonOrderInfo> commonOrderInfos = (List<CommonOrderInfo>) excuSingle.get(OrderManagementConstant.DATA);
        log.debug("页面填写开票，拆分补全后的数据:{}", JsonUtils.getInstance().toJsonString(commonOrderInfos));
    
        /**
         * 保存数据
         */
    
        if (OrderInfoEnum.INVOICE_BILLING_CZLX_1.getKey().equals(orderInfoList.getCzlx())) {
            // 如果是暂存 直接补齐后的数据入库
            return R.ok().put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getKey());
        
        }
    
        Map<String, PageSld> sldMap = new HashMap<>(2);
        PageSld pageSld = new PageSld();
        pageSld.setSldid(sld);
        pageSld.setSldmc(sldmc);
        sldMap.put("_" + commonOrderInfo.getOrderInfo().getFpzlDm(), pageSld);
        // 调用开票接口
        R r = makeOutAnInvoiceService
                .makeOutAnInovice(commonOrderInfos, sldMap);
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
            r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
            vo.putAll(r);
            return vo;
        } else {
            vo.putAll(r);
            return vo;
        }

    }

    /**
     * 用于页面excel导入的接口
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "excel订单录入接口", notes = "订单接收-根据excel中的数据创建订单信息")
    @PostMapping("/excel")
    @SysLog(operation = "表格导入rest接口", operationDesc = "根据excel中的数据创建订单信息", key = "订单接收")
    public R acceptByExcel(
            @ApiParam(name = "file", value = "导入的excel文件", required = true) @RequestParam(value = "file", required = true) MultipartFile file,
            @ApiParam(name = "size", value = "文件大小", required = false) @RequestParam(value = "size", required = false) String size,
            @ApiParam(name = "lastModifiedDate", value = "上次传输时间", required = false) @RequestParam(value = "lastModifiedDate", required = false) String lastModifiedDate,
            @ApiParam(name = "xhfYh", value = "销货方银行", required = true) @RequestParam(value = "xhfYh", required = true) String xhfYh,
            @ApiParam(name = "xhfDz", value = "销货方地址", required = true) @RequestParam(value = "xhfDz", required = true) String xhfDz,
            @ApiParam(name = "xhfDh", value = "销货方电话", required = true) @RequestParam(value = "xhfDh", required = true) String xhfDh,
            @ApiParam(name = "xhfZh", value = "销货方账号", required = true) @RequestParam(value = "xhfZh", required = true) String xhfZh,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "xhfMc", value = "销货方纳税人名称", required = true) @RequestParam(value = "xhfMc", required = true) String xhfMc) {
        Map<String, List<NewOrderExcel>> readOrderInfoFromExcelxls;
        try {
            //销方信息校验
            if (StringUtils.isBlank(xhfYh) || StringUtils.isBlank(xhfDz) || StringUtils.isBlank(xhfDh)
                    || StringUtils.isBlank(xhfZh) || StringUtils.isBlank(xhfNsrsbh) || StringUtils.isBlank(xhfMc)) {
                log.error("销方信息不全");
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.RECEIVE_FAILD, null);
            }

            List<String> shList = new ArrayList<>();
            shList.add(xhfNsrsbh);
            Map<String, String> paramMap = buildSellerMap(xhfYh, xhfDz, xhfDh, xhfZh, xhfNsrsbh, xhfMc);

            //从excel中读取数据
            List<NewOrderExcel> newOrderExcels = excelReadService.readOrderInfoFromExcelxls(file);
            if(newOrderExcels.size() <= 0){
                return R.error().put(OrderManagementConstant.CODE,OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                        .put(OrderManagementConstant.MESSAGE,"excel模板为空!");

            }
            //导入的条数
            int count = newOrderExcels.size();
            log.debug("{}从excel中读取到的数据条数:{}", LOGGER_MSG, count);
            //替换特殊字符串
            excelReplaceCharacter(newOrderExcels);

            //具有相同购方信息和订单号的数据作为同一订单
            Map<String, List<NewOrderExcel>> orderExcelMap = groupByBuyerInfo(newOrderExcels);

            //订单校验
            Map<String, Object> examinByMap = excelReadService.examinByMap(orderExcelMap, xhfNsrsbh);
            if (!(boolean) examinByMap.get(ConfigureConstant.STRING_IS_PASS)) {
                log.error("{}订单信息校验失败", LOGGER_MSG);
                return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.RECEIVE_FAILD,
                        examinByMap.get(OrderManagementConstant.DATA)).put("count", count);
    
            }
            //组装订单信息
            List<CommonOrderInfo> orderExcel2OrderInfo = excelReadService.excelToOrderInfo(orderExcelMap, paramMap);
            log.info("{}excel转换成订单数据完成", LOGGER_MSG);
    
            //将list按照excel中的位置正序排序
            Collections.reverse(orderExcel2OrderInfo);
            // 倒序排列
            // 将数据插入数据库
            generateReadyOpenOrderService.saveOrderInfo(orderExcel2OrderInfo);
    
            return InterfaceResponseUtils.buildReturnInfo(OrderInfoContentEnum.SUCCESS, null).put("count", count);
    
        } catch (OrderReceiveException e) {
            log.error("{}excel导入异常:{}", LOGGER_MSG, e);
            return R.error(e.getCode(), e.getMessage());
        } catch (OrderSeparationException e) {
            log.error("{}订单价税分离异常:{}", e.getMessage());
            return R.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("{}excel导入处理异常:{}", LOGGER_MSG, e);
            return R.error();
        }

    }


    /**
     * 具有相同购方信息和订单号的数据作为同一订单
     * @param newOrderExcels
     * @return
     */
    private Map<String,List<NewOrderExcel>> groupByBuyerInfo(List<NewOrderExcel> newOrderExcels) {

        Map<String, List<NewOrderExcel>> orderExcelMap = new LinkedHashMap<>();

        for (NewOrderExcel orderExcel : newOrderExcels) {

            String key = (orderExcel.getFpzlDm() == null ? "" : orderExcel.getFpzlDm())
                    + (orderExcel.getDdh() == null ? "" : orderExcel.getDdh())
                    + (orderExcel.getGhf_mc() == null ? "" : orderExcel.getGhf_mc())
                    + (orderExcel.getGhf_nsrsbh() == null ? "" : orderExcel.getGhf_nsrsbh())
                    + (orderExcel.getGhf_dz() == null ? "" : orderExcel.getGhf_dz())
                    + (orderExcel.getGhf_dh() == null ? "" : orderExcel.getGhf_dh())
                    + (orderExcel.getGhf_zh() == null ? "" : orderExcel.getGhf_zh())
                    + (orderExcel.getGhf_yh() == null ? "" : orderExcel.getGhf_yh());
            String hashCode = String.valueOf(key.hashCode());
            // 利用字符串的hascode做key过滤相同条件的数据
            if (orderExcelMap.get(hashCode) == null) {
                List<NewOrderExcel> orderList = new ArrayList<>();
                orderList.add(orderExcel);
                orderExcelMap.put(hashCode, orderList);
            } else {
                List<NewOrderExcel> list = orderExcelMap.get(hashCode);
                list.add(orderExcel);
                orderExcelMap.put(hashCode, list);
            }
        }
        return orderExcelMap;
    }

    /**
     * 封装销方
     * @param xhfYh
     * @param xhfDz
     * @param xhfDh
     * @param xhfZh
     * @param xhfNsrsbh
     * @param xhfMc
     * @return
     */
    private Map<String, String> buildSellerMap(String xhfYh, String xhfDz, String xhfDh,
                                               String xhfZh, String xhfNsrsbh, String xhfMc) {

        Map<String, String> paramMap = new HashMap<>(5);
        paramMap.put("xhfYh", xhfYh);
        paramMap.put("xhfDz", xhfDz);
        paramMap.put("xhfDh", xhfDh);
        paramMap.put("xhfZh", xhfZh);
        paramMap.put("xhfNsrsbh", xhfNsrsbh);
        paramMap.put("xhfMc", xhfMc);
        return paramMap;
    }

    /**
     * public 是方便测试用的，测完改成 private
     * fankunfeng
     */
    public void excelReplaceCharacter(List<NewOrderExcel> orderExcelList) {

        Date startTime = new Date();
        log.info("Excel特殊字符替换开始时间：{}", startTime);

        for (NewOrderExcel oldOrderExcel : orderExcelList) {
            oldOrderExcel.setZzstsgl(StringUtil.replaceStr(oldOrderExcel.getZzstsgl()));
            oldOrderExcel.setZxbm(StringUtil.replaceStr(oldOrderExcel.getZxbm()));
            oldOrderExcel.setYhzcbs(StringUtil.replaceStr(oldOrderExcel.getYhzcbs()));
            oldOrderExcel.setXmsl(StringUtil.replaceStr(oldOrderExcel.getXmsl()));
            oldOrderExcel.setXmmc(StringUtil.replaceStr(oldOrderExcel.getXmmc()));
            oldOrderExcel.setXmje(StringUtil.replaceStr(oldOrderExcel.getXmje()));
            oldOrderExcel.setXmdw(StringUtil.replaceStr(oldOrderExcel.getXmdw()));
            oldOrderExcel.setXmdj(StringUtil.replaceStr(oldOrderExcel.getXmdj()));
            oldOrderExcel.setSpbm(StringUtil.replaceStr(oldOrderExcel.getSpbm()));
            oldOrderExcel.setSl(StringUtil.replaceStr(oldOrderExcel.getSl()));
            oldOrderExcel.setSe(StringUtil.replaceStr(oldOrderExcel.getSe()));
            oldOrderExcel.setRowIndex(StringUtil.replaceStr(oldOrderExcel.getRowIndex()));
            oldOrderExcel.setLslbs(StringUtil.replaceStr(oldOrderExcel.getLslbs()));
            oldOrderExcel.setHsbz(StringUtil.replaceStr(oldOrderExcel.getHsbz()));
            oldOrderExcel.setGhf_qylx(StringUtil.replaceStr(oldOrderExcel.getGhf_qylx()));
            oldOrderExcel.setGhf_zh(StringUtil.replaceStr(oldOrderExcel.getGhf_zh()));
            oldOrderExcel.setGhf_yh(StringUtil.replaceStr(oldOrderExcel.getGhf_yh()));
            oldOrderExcel.setGhf_nsrsbh(StringUtil.replaceStr(oldOrderExcel.getGhf_nsrsbh()));
            //销货方名称和购货方名称 不替换
            oldOrderExcel.setGhf_dz(StringUtil.replaceStr(oldOrderExcel.getGhf_dz()));
            oldOrderExcel.setGhf_dh(StringUtil.replaceStr(oldOrderExcel.getGhf_dh()));
            oldOrderExcel.setGgxh(StringUtil.replaceStr(oldOrderExcel.getGgxh()));
            oldOrderExcel.setFpzlDm(StringUtil.replaceStr(oldOrderExcel.getFpzlDm()));
            oldOrderExcel.setDdh(StringUtil.replaceStr(oldOrderExcel.getDdh()));
            oldOrderExcel.setColumnIndex(StringUtil.replaceStr(oldOrderExcel.getColumnIndex()));
            //备注特殊处理
            oldOrderExcel.setBz(StringUtil.replaceStr(oldOrderExcel.getBz(), false));
            oldOrderExcel.setBmbbbh(StringUtil.replaceStr(oldOrderExcel.getBmbbbh()));
        }

        Date endTime = new Date();
        log.info("Excel特殊字符替换结束时间：{}，共花费{}ms", startTime, endTime.getTime() - startTime.getTime());
    }

    /**
     * 前端页面填写数据转换成订单可处理通用数据
     *
     * @param pageOrderInfo
     * @return
     */
    private CommonOrderInfo pageToFpkjInfo(PageOrderInfo pageOrderInfo) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setBz(StringUtils.isBlank(pageOrderInfo.getBz()) ? "" : pageOrderInfo.getBz());
        orderInfo.setFhr(pageOrderInfo.getFhr());
        orderInfo.setGhfDh(pageOrderInfo.getGhfDh());
        orderInfo.setGhfDz(pageOrderInfo.getGhfDz());
        orderInfo.setDdlx(OrderInfoEnum.ORDER_SOURCE_0.getKey());
        orderInfo.setKpjh(pageOrderInfo.getKpjh());
        // 购货方邮箱
        orderInfo.setGhfEmail(pageOrderInfo.getGhfEmail());
        orderInfo.setGhfMc(pageOrderInfo.getGhfMc());
        orderInfo.setGhfNsrsbh(pageOrderInfo.getGhfNsrsbh());
        orderInfo.setGhfSj(pageOrderInfo.getGhfSj());
        //  银行账号是否需要拆分开
        orderInfo.setGhfYh(pageOrderInfo.getGhfYh());
        orderInfo.setGhfZh(pageOrderInfo.getGhfZh());
        orderInfo.setDdh(StringUtils.isBlank(pageOrderInfo.getDdh()) ? RandomUtil.randomNumbers(12) : pageOrderInfo.getDdh());
        orderInfo.setDdrq(new Date());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setBbmBbh(SystemConfig.bmbbbh);
        orderInfo.setSld(pageOrderInfo.getSld());
        orderInfo.setKpjh(pageOrderInfo.getKpjh());
        orderInfo.setSldMc(pageOrderInfo.getSldmc());
        orderInfo.setQdBz(StringUtils.isNotBlank(pageOrderInfo.getQdbz()) ? pageOrderInfo.getQdbz() : OrderInfoEnum.QDBZ_CODE_0.getKey());
        if (pageOrderInfo.getPageOrderItemInfo().length > 0) {
            orderInfo.setKpxm(pageOrderInfo.getPageOrderItemInfo()[0].getXmmc());
        }
        orderInfo.setFpzlDm(pageOrderInfo.getFplx());
        orderInfo.setKpr(pageOrderInfo.getKpy());
        orderInfo.setSkr((pageOrderInfo.getSky()));
        orderInfo.setKplx(pageOrderInfo.getKplx());
        orderInfo.setYfpDm(pageOrderInfo.getYfpdm());
        orderInfo.setYfpHm(pageOrderInfo.getYfphm());
        //orderInfo.setThdh(pageOrderInfo.getDdh());

        orderInfo.setGhfQylx(pageOrderInfo.getGhfqylx());
        orderInfo.setDkbz(OrderInfoEnum.DKBZ_0.getKey());

        orderInfo.setXhfNsrsbh(pageOrderInfo.getXhfNsrsbh());
        orderInfo.setNsrsbh(pageOrderInfo.getXhfNsrsbh());
        orderInfo.setNsrmc(pageOrderInfo.getXhfmc());
        orderInfo.setXhfDz(pageOrderInfo.getXhfdz());
        orderInfo.setXhfDh(pageOrderInfo.getXhfdh());
        orderInfo.setXhfYh(pageOrderInfo.getXhfyh());
        orderInfo.setXhfZh(pageOrderInfo.getXhfzh());
        orderInfo.setXhfMc(pageOrderInfo.getXhfmc());
        orderInfo.setYwlx(pageOrderInfo.getYwlx());
        String orderId = apiInvoiceCommonMapperService.getGenerateShotKey();
        orderInfo.setId(orderId);
        orderInfo.setFpqqlsh(apiInvoiceCommonMapperService.getGenerateShotKey());

        //处理开票合计金额

        double kphjje = 0.00;
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        PageOrderItemInfo[] pageOrderItemInfoArray = pageOrderInfo.getPageOrderItemInfo();
        int i = 1;
        for (PageOrderItemInfo pageOrderItemInfo : pageOrderItemInfoArray) {
            OrderItemInfo orderItemInfo = new OrderItemInfo();
            orderItemInfo.setId(apiInvoiceCommonMapperService.getGenerateShotKey());
            orderItemInfo.setOrderInfoId(orderId);

            orderItemInfo.setXmdw(pageOrderItemInfo.getXmdw());
            orderItemInfo.setGgxh(pageOrderItemInfo.getGgxh());

            orderItemInfo.setXmsl("");
            orderItemInfo.setXmdj("");
            if (!StringUtils.isBlank(pageOrderItemInfo.getSl())) {
                orderItemInfo.setSl(StringUtil.formatSl(pageOrderItemInfo.getSl()));

            }
            orderItemInfo.setSpbm(pageOrderItemInfo.getSpbm());
            if (!StringUtils.isBlank(pageOrderItemInfo.getXmdj())) {
                orderItemInfo.setXmdj(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmdj(), ConfigureConstant.INT_8));
            }


            if (!StringUtils.isBlank(pageOrderItemInfo.getXmje())) {
                orderItemInfo.setXmje(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmje(), ConfigureConstant.INT_2));
            }
            orderItemInfo.setXmmc(pageOrderItemInfo.getXmmc());
            if (!StringUtils.isBlank(pageOrderItemInfo.getXmsl())) {
                orderItemInfo.setXmsl(DecimalCalculateUtil.decimalFormatToString(pageOrderItemInfo.getXmsl(), ConfigureConstant.INT_8));
            }
    
            orderItemInfo.setHsbz(pageOrderItemInfo.getHsbz());
            orderItemInfo.setFphxz(pageOrderItemInfo.getFphxz());
            orderItemInfo.setYhzcbs(pageOrderItemInfo.getYhzcbs());
            orderItemInfo.setZzstsgl(pageOrderItemInfo.getZzstsgl());
            orderItemInfo.setLslbs(pageOrderItemInfo.getLslbs());
            orderItemInfo.setSphxh(String.valueOf(i));
            orderItemInfo.setSe(pageOrderItemInfo.getXmse());
            orderItemInfo.setKce(pageOrderItemInfo.getKce());
            orderItemInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
            orderItemInfos.add(orderItemInfo);
            if (!StringUtils.isBlank(orderItemInfo.getXmje())) {
                kphjje += Double.parseDouble(orderItemInfo.getXmje());
            }
            i++;
        }
        if (pageOrderItemInfoArray.length <= ConfigureConstant.INT_2 && StringUtils.isNotBlank(pageOrderItemInfoArray[0].getKce())) {
            if (pageOrderInfo.getKplx().equals(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey())) {
                if (StringUtils.isNotBlank(orderInfo.getBz())) {
                    if (!orderInfo.getBz().startsWith(ConfigureConstant.STRING_CEZS)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(ConfigureConstant.STRING_CEZS_).append(pageOrderItemInfoArray[0].getKce()).append("。").append(orderInfo.getBz());
                        orderInfo.setBz(sb.toString());
                    }
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ConfigureConstant.STRING_CEZS_).append(pageOrderItemInfoArray[0].getKce()).append("。");
                    orderInfo.setBz(sb.toString());
                }
            }
        }
        orderInfo.setKphjje(DecimalCalculateUtil.decimalFormatToString(String.valueOf(kphjje), ConfigureConstant.INT_2));
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);

        return commonOrderInfo;

    }


    /**
     * 专票冲红进行开票
     *
     * @param commonOrderInfo
     * @param uId
     * @param deptId
     * @return
     */
    @ApiOperation(value = "专票冲红全部、部分冲红", notes = "订单接收-专票冲红")
    @PostMapping("/specialty")
    @SysLog(operation = "专票冲红rest接口", operationDesc = "专票冲红接口", key = "专票冲红")
    public R specialty(
            @ApiParam(name = "commonOrderInfo", value = "commonOrderInfo", required = true) @RequestBody CommonOrderInfo commonOrderInfo,
            @ApiParam(name = "uId", value = "uId", required = true) @RequestParam("uId") String uId,
            @ApiParam(name = "deptId", value = "deptId", required = true) @RequestParam("deptId") String deptId) {
        log.info("{}页面订单信息录入的接口，前端传入的参数:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonOrderInfo));
        R vo = new R();
        try {
            String sld = commonOrderInfo.getOrderInfo().getSld();
            String sldMc = commonOrderInfo.getOrderInfo().getSldMc();
            /**
             * 根据税号查询税控设备
             */
            String terminalCode = apiTaxEquipmentService.getTerminalCode(commonOrderInfo.getOrderInfo().getXhfNsrsbh());
    
            commonOrderInfo.getOrderInfo().setId("");
            commonOrderInfo.getOrderInfo().setProcessId("");
            commonOrderInfo.setSjywly(OrderInfoEnum.READY_ORDER_SJLY_3.getKey());
            //冲红生成待开单据
            R excuSingle = generateReadyOpenOrderService.reshRed(commonOrderInfo, uId, deptId);

            if (!excuSingle.get(OrderManagementConstant.CODE).equals(OrderInfoContentEnum.SUCCESS.getKey())) {
                return R.error().put(OrderManagementConstant.CODE, excuSingle.get(OrderManagementConstant.CODE)).put(OrderManagementConstant.MESSAGE, excuSingle.get(OrderManagementConstant.MESSAGE));
            }
            Map<String, PageSld> sldMap = new HashMap<>(2);
            PageSld pageSld = new PageSld();
            pageSld.setSldid(sld);
            pageSld.setSldmc(sldMc);
            sldMap.put("_" + commonOrderInfo.getOrderInfo().getFpzlDm(), pageSld);
            //调用开票接口


            R r = makeOutAnInvoiceService.makeOutAnInovice((List<CommonOrderInfo>) excuSingle.get(OrderManagementConstant.DATA), sldMap);
            if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
                return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("msg", OrderInfoContentEnum.SUCCESS.getMessage());
            } else {
                return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey()).put("msg", r.get("msg"));
            }

        } catch (Exception e) {
            log.debug("{},发生异常:{}", LOGGER_MSG, e);
            return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey()).put("msg", OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
        }
    }




}
