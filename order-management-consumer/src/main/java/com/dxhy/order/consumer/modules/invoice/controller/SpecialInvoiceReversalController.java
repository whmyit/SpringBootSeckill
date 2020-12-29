package com.dxhy.order.consumer.modules.invoice.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.modules.invoice.service.SpecialInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.*;
import com.dxhy.order.model.entity.CommonSpecialInvoice;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.utils.*;
import com.itextpdf.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;

import static com.dxhy.order.constant.OrderInfoEnum.*;
/**
 * 红字申请单控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 19:39
 */
@RestController
@Api(value = "红字申请单", tags = {"发票模块"})
@Slf4j
@RequestMapping("/pinvoice")
public class SpecialInvoiceReversalController {
    
    private final String[] notAllowedEditSubmitStatus = {"TZD0000", "TZD0071", "TZD0072", "TZD0073", "TZD0074", "TZD0076", "TZD0077", "TZD0078", "TZD0079", "TZD0080", "TZD0100"};
    
    private static final String LOGGER_MSG = "(红字申请单申请单业务处理)";
    private static final NumberFormat NF = NumberFormat.getPercentInstance();
    
    @Reference
    private ApiSpecialInvoiceReversalService apiSpecialInvoiceReversalService;
    
    @Resource
    private SpecialInvoiceService specialInvoiceService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Reference
    private ApiInvoiceService invoiceService;
    
    @Reference
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Reference
    private ValidateOrderInfo validateOrderInfo;
    
    @Reference
    private ApiTaxClassCodeService apiTaxClassCodeService;
    
    @Resource
    private UserInfoService userInfoService;


    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;

    @Reference
    private ApiOrderItemInfoService apiOrderItemInfoService;

    
    /**
     * 查询红字申请单列表
     *
     * @param limit        每页条数
     * @param page         页号
     * @param buyerName    购方名称
     * @param sellerName   销方名称
     * @param submitStatus 提交状态，可选值：TZD0500：未上传、TZD0000：审核通过、other：其他；
     * @param invoiceCode  发票代码
     * @param invoiceNo    发票号码
     * @return R
     */
    @PostMapping(value = "/specialInvoiceReversals/list")
    @ApiOperation(value = "红字申请单列表", notes = "红字申请单管理-红字申请单列表")
    @SysLog(operation = "红字申请单列表", operationDesc = "红字申请单列表", key = "红字申请单管理")
    public R getSpecialInvoiceReversals(@RequestParam("limit") Integer limit, @RequestParam("page") Integer page,
                                        String xhfNsrsbh, String buyerName, String sellerName, String submitStatus,
                                        String invoiceCode, String invoiceNo, String startTime, String endTime,
                                        String status) {
        Map<String, Object> params = new HashMap<>(10);
        params.put("buyerName", (StringUtils.isNotBlank(buyerName) ? buyerName.trim() : null));
        params.put("sellerName", (StringUtils.isNotBlank(sellerName) ? sellerName.trim() : null));
        params.put("submitStatus", (StringUtils.isNotBlank(submitStatus) ? submitStatus : null));
        params.put("invoiceCode", (StringUtils.isNotBlank(invoiceCode) ? invoiceCode.trim() : null));
        params.put("invoiceNo", (StringUtils.isNotBlank(invoiceNo) ? invoiceNo.trim() : null));
        params.put("startTime", (StringUtils.isNotBlank(startTime) ? startTime.trim() : null));
    
        if (StringUtils.isNotBlank(endTime)) {
            DateTime parse = DateUtil.parse(endTime, "yyyy-MM-dd");
            Date endOfDay = DateUtil.endOfDay(parse);
            String format = DateUtil.format(endOfDay, "yyyy-MM-dd HH:mm:ss");
            params.put("endTime", format);
        }
    
        params.put("status", (StringUtils.isNotBlank(status) ? status.trim() : null));
        params.put("limit", limit.toString());
        params.put("page", page.toString());
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        } else {
            String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
            if (xfshs.length > 1) {
                params.put("xhfNsrsbh", xfshs);
            } else {
                params.put("xfsh", xfshs[0]);
            }
        }
        PageUtils querySpecialInvoiceReversals = apiSpecialInvoiceReversalService.querySpecialInvoiceReversals(params);

        querySpecialInvoiceReversals.getList();
        //处理税率

        return R.ok().put("page", querySpecialInvoiceReversals);
    }
    
    /**
     * @param xhfNsrsbh
     * @param buyerName
     * @param sellerName
     * @param submitStatus
     * @param invoiceCode
     * @param invoiceNo
     * @param startTime
     * @param endTime
     * @param status
     * @return R
     * @throws
     * @Title : getSpecialInvoiceReversalsCount
     * @Description ：红字申请单列表金额统计
     */
    @PostMapping(value = "/specialInvoiceReversals/count")
    @ApiOperation(value = "红字申请单统计", notes = "红字申请单管理-红字申请单统计")
    @SysLog(operation = "红字申请单统计", operationDesc = "红字申请单统计", key = "红字申请单管理")
    public R getSpecialInvoiceReversalsCount(String xhfNsrsbh, String buyerName, String sellerName, String submitStatus,
                                             String invoiceCode, String invoiceNo, String startTime, String endTime,
                                             String status) {
        Map<String, Object> params = new HashMap<>(10);
        params.put("buyerName", (StringUtils.isNotBlank(buyerName) ? buyerName.trim() : null));
        params.put("sellerName", (StringUtils.isNotBlank(sellerName) ? sellerName.trim() : null));
        params.put("submitStatus", (StringUtils.isNotBlank(submitStatus) ? submitStatus : null));
        params.put("invoiceCode", (StringUtils.isNotBlank(invoiceCode) ? invoiceCode.trim() : null));
        params.put("invoiceNo", (StringUtils.isNotBlank(invoiceNo) ? invoiceNo.trim() : null));
        params.put("startTime", (StringUtils.isNotBlank(startTime) ? startTime.trim() : null));
        
        if (StringUtils.isNotBlank(endTime)) {
            DateTime parse = DateUtil.parse(endTime, "yyyy-MM-dd");
            Date endOfDay = DateUtil.endOfDay(parse);
            String format = DateUtil.format(endOfDay, "yyyy-MM-dd HH:mm:ss");
            params.put("endTime", format);
        }
        
        params.put("status", (StringUtils.isNotBlank(status) ? status.trim() : null));
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        } else {
            String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
            if (xfshs.length > 1) {
                params.put("xhfNsrsbh", xfshs);
            } else {
                params.put("xfsh", xfshs[0]);
            }
        }
        Map<String, Object> querySpecialInvoiceReversalsCount = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalsCount(params);
        return R.ok().put("data", querySpecialInvoiceReversalsCount);
    }
    
    
    /**
     * 查询红字申请单详情 TODO 修改pathvalue的方式
     *
     * @param id 红字申请单ID
     * @return R
     */
    @RequestMapping(value = "/specialInvoiceReversals/detail", method = RequestMethod.POST)
    @ApiOperation(value = "红字申请单详情", notes = "红字申请单管理-红字申请单详情")
    @SysLog(operation = "红字申请单详情", operationDesc = "红字申请单详情", key = "红字申请单管理")
    public R specialInvoiceReversalDetail(@ApiParam(name = "id", value = "红字申请单id", required = false) @RequestParam String id,
                                        @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = false) @RequestParam String xhfNsrsbh){

        Map<String, Object> result = new HashMap<>(5);
        SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.querySpecialInvoiceReversal(id);
        result.put("specialInvoiceReversal",specialInvoiceReversal);

        if (null != specialInvoiceReversal) {
            List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(specialInvoiceReversal.getId());
            specialInvoiceReversalItems.forEach(specialInvoiceReversalItem -> {
        
                /**
                 * 处理折扣行单价和数量,不显示
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getHsbz()) && OrderInfoEnum.FPHXZ_CODE_1.getKey().equals(specialInvoiceReversalItem.getHsbz())) {
                    specialInvoiceReversalItem.setXmdj("");
                    specialInvoiceReversalItem.setXmsl("");
                }
        
                /**
                 * 处理金额,格式化金额
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmje())) {
                    specialInvoiceReversalItem.setXmje(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmje(), ConfigureConstant.INT_2));
                }
        
                /**
                 * 处理税额,格式化税额
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getSe())) {
                    specialInvoiceReversalItem.setSe(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getSe(), ConfigureConstant.INT_2));
                }
        
                /**
                 * 处理单价,保留非零位
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmdj())) {
                    specialInvoiceReversalItem.setXmdj(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmdj(), ConfigureConstant.INT_8)));
                }
    
                /**
                 * 处理数量,保留非零位
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getXmsl())) {
                    specialInvoiceReversalItem.setXmsl(StringUtil.slFormat(DecimalCalculateUtil.decimalFormatToString(specialInvoiceReversalItem.getXmsl(), ConfigureConstant.INT_8)));
                }
    
                /**
                 * 处理税率,按照百分比显示
                 */
                if (StringUtils.isNotBlank(specialInvoiceReversalItem.getSl())) {
                    NF.setMaximumFractionDigits(3);
                    specialInvoiceReversalItem.setSl(specialInvoiceReversalItem.getSl().contains("%") ? specialInvoiceReversalItem.getSl() : NF.format(Double.valueOf(specialInvoiceReversalItem.getSl())));
                }
    
                if (specialInvoiceReversalItem.getLslbs() == null) {
                    specialInvoiceReversalItem.setLslbs("");
                }
            });
            result.put("specialInvoiceReversalItems", specialInvoiceReversalItems);
        }
        return R.ok().put(OrderManagementConstant.DATA, result);
    }

    /**
     *   TODO id需要放到编辑中
     * @param specialInvoiceReversal 红字申请单JSON字符串，其中申请单明细项名称为items
     * @return R
     */
    @PostMapping("/specialInvoiceReversals/add")
    @ApiOperation(value = "红字申请单新增", notes = "红字申请单管理-红字申请单新增")
    @SysLog(operation = "红字申请单新增", operationDesc = "红字申请单新增", key = "红字申请单管理")
    public R addSpecialInvoiceReversal(@RequestBody String specialInvoiceReversal) {
    
        boolean isEdit = false;
        try {
            /**
             * json数据解析和校验
             */
            JSONObject specialInvoiceReversalData = JSON.parseObject(specialInvoiceReversal);
            SpecialInvoiceReversalEntity specialInvoiceReversalEntity = JSON.toJavaObject(specialInvoiceReversalData,
                    SpecialInvoiceReversalEntity.class);

            if (StringUtils.isNotBlank(specialInvoiceReversalEntity.getId())) {
                isEdit = true;
            }

            List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntityList = new ArrayList<>();
            if (StringUtils.isNotBlank(specialInvoiceReversalData.getString("items"))) {
                specialInvoiceReversalItemEntityList = JSON.parseArray(specialInvoiceReversalData.getString("items"),
                        SpecialInvoiceReversalItem.class);
            }

            R validateResult = checkSpecialInvoiceReversal(specialInvoiceReversalEntity, specialInvoiceReversalItemEntityList);
            if(!OrderInfoContentEnum.SUCCESS.getKey().equals(validateResult.get(OrderManagementConstant.CODE))){
                return validateResult;
            }
    
            /**
             * 数据组装
             */
            UserEntity user = userInfoService.getUser();
            specialInvoiceReversalEntity.setInvoiceType(ConfigureConstant.STRING_1);
            specialInvoiceReversalEntity.setGhfqylx(GHF_QYLX_01.getKey());
            specialInvoiceReversalEntity.setNsrsbh(specialInvoiceReversalEntity.getXhfNsrsbh());
            specialInvoiceReversalEntity.setEditorId(user.getUserId().toString());
            specialInvoiceReversalEntity.setEditorName(user.getUsername());

            if(!isEdit){
                specialInvoiceReversalEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
                specialInvoiceReversalEntity.setTksj(new Date());
                specialInvoiceReversalEntity.setCreatorId(user.getUserId().toString());
                specialInvoiceReversalEntity.setCreatorName(user.getUsername());
                specialInvoiceReversalEntity.setYysbz(OrderInfoEnum.SPECIAL_YYSBZ_0000000000.getKey());
                //判断是否是扣除额的发票
                if(StringUtils.isNotBlank(specialInvoiceReversalEntity.getYfpHm()) && StringUtils.isNotBlank(specialInvoiceReversalEntity.getYfpDm())){
                    List<String> shList = new ArrayList<>();
                    shList.add(specialInvoiceReversalEntity.getXhfNsrsbh());
                    OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpdmAndFphm(specialInvoiceReversalEntity.getYfpDm(),
                            specialInvoiceReversalEntity.getYfpHm(), shList);
                    if(orderInvoiceInfo != null){
                        List<OrderItemInfo> orderItemInfos = apiOrderItemInfoService.selectOrderItemInfoByOrderId(orderInvoiceInfo.getOrderInfoId(),shList);
                        if(CollectionUtils.isNotEmpty(orderItemInfos) && orderItemInfos.size() == 1 && StringUtils.isNotBlank(orderItemInfos.get(0).getKce())){
                            specialInvoiceReversalEntity.setYysbz(OrderInfoEnum.SPECIAL_YYSBZ_0000000020.getKey());
                        }
                    }

                }
            }
            // 明细数据处理
            BigDecimal totalAmount = BigDecimal.ZERO, taxAmount = BigDecimal.ZERO;
            String taxRate = "";
            for (int i = 0; i < specialInvoiceReversalItemEntityList.size(); i++) {
                SpecialInvoiceReversalItem specialInvoiceReversalItem = specialInvoiceReversalItemEntityList.get(i);
                // 税率处理
    
                specialInvoiceReversalItem.setSl(StringUtil.formatSl(specialInvoiceReversalItem.getSl()));
    
                BigDecimal itemTaxAmount = StringUtils.isNotBlank(specialInvoiceReversalItem.getSe())
                        ? new BigDecimal(specialInvoiceReversalItem.getSe()) : BigDecimal.ZERO;
                specialInvoiceReversalItem.setSe(DigitUtils.formatDoublePrecision(itemTaxAmount).toPlainString());
    
                BigDecimal itemAmount = new BigDecimal(specialInvoiceReversalItem.getXmje());
                specialInvoiceReversalItem.setXmje(DigitUtils.formatDoublePrecision(itemAmount).toPlainString());
                specialInvoiceReversalItem.setSpecialInvoiceReversalId(specialInvoiceReversalEntity.getId());
                specialInvoiceReversalItem.setSphxh(String.valueOf(i + 1));
                if (i == 0) {
                    taxRate = specialInvoiceReversalItem.getSl();
                } else if (!taxRate.equals(specialInvoiceReversalItem.getSl()) && !"多税率".equals(taxRate)) {
                    taxRate = "多税率";
                }
                totalAmount = totalAmount.add(itemAmount);
                taxAmount = taxAmount.add(itemTaxAmount);
                if (StringUtils.isBlank(specialInvoiceReversalItem.getYhzcbs())) {
                    specialInvoiceReversalItem.setYhzcbs(YHZCBS_0.getKey());
                }
                specialInvoiceReversalItem.setId(apiInvoiceCommonService.getGenerateShotKey());
                specialInvoiceReversalItem.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
            }
    
            specialInvoiceReversalEntity.setHjbhsje(DigitUtils.formatDoublePrecision(totalAmount).toPlainString());
            specialInvoiceReversalEntity.setDslbz(taxRate);
            specialInvoiceReversalEntity.setHjse(DigitUtils.formatDoublePrecision(taxAmount).toPlainString());
            specialInvoiceReversalEntity.setKphjje(DigitUtils.formatDoublePrecision(totalAmount.add(taxAmount)).toPlainString());

            if (StringUtils.isBlank(specialInvoiceReversalEntity.getStatusCode())) {
                specialInvoiceReversalEntity.setStatusCode(SPECIAL_INVOICE_STATUS_TZD0500.getKey());
            }
            specialInvoiceReversalEntity.setKpzt(SPECIAL_INVOICE_STATUS_0.getKey());
            specialInvoiceReversalEntity.setCreateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));
            specialInvoiceReversalEntity.setUpdateTime(DateUtils.getDate(new Date(), DateUtils.DATE_TIME_PATTERN));


            specialInvoiceReversalEntity.setSqdscqqpch(specialInvoiceReversalEntity.getSqdscqqlsh());
            specialInvoiceReversalEntity.setSqdh(specialInvoiceReversalEntity.getSqdscqqlsh());


            CommonSpecialInvoice commonSpecialInvoice = new CommonSpecialInvoice();
            commonSpecialInvoice.setSpecialInvoiceReversalEntity(specialInvoiceReversalEntity);
            commonSpecialInvoice.setSpecialInvoiceReversalItemEntities(specialInvoiceReversalItemEntityList);
            /**
             * 数据库插入 添加事务处理
             */
            log.debug("更新后的红字申请单数据:{}", JsonUtils.getInstance().toJsonString(commonSpecialInvoice));
            boolean result = apiSpecialInvoiceReversalService.processCommonSpecialInvoice(isEdit, commonSpecialInvoice);
            if (result) {
                return R.ok().put("specialInvoiceReversal", specialInvoiceReversalEntity);
            } else {
                return R.error(ResponseStatusCodes.ERROR, "红字申请单保存异常");
            }
    
        } catch (Exception e) {
            log.error("程序处理异常：", e);
            return R.error(ResponseStatusCodes.ERROR, "保存异常");
        }
    }


    /**
     * 数据校验
     */
    private R checkSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversalEntity, List<SpecialInvoiceReversalItem> itemList) {

        //申请单请求流水号合法性校验

        if (StringUtils.isBlank(specialInvoiceReversalEntity.getSqdscqqlsh())) {
            return R.error(ResponseStatusCodes.IS_NULL, "专票冲红-申请单请求流水号不能为空");
        } else if (specialInvoiceReversalEntity.getSqdscqqlsh().contains("null") && specialInvoiceReversalEntity.getSqdscqqlsh().length() != 24) {
            return R.error(ResponseStatusCodes.IS_ILLEGAL, "专票冲红-申请单请求流水号非法");
        }

        //申请说明
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getSqsm())) {
            return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_REASON_IS_BLANK, "申请说明不能为空");
        }
        //申请类型
        if (!OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialInvoiceReversalEntity.getSqsm())) {
            if (StringUtils.isBlank(specialInvoiceReversalEntity.getYfpDm()) || StringUtils.isBlank(specialInvoiceReversalEntity.getYfpHm())) {
                return R.error(ResponseStatusCodes.INVOICE_CODE_AND_NO_IS_BLANK, "发票代码、号码不能为空");
            }
        }

        //销方信息校验
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfMc())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方名称不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfNsrsbh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方税号不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfDz())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方地址不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfDh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方电话不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfYh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方银行不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getXhfZh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "销方账号不能为空");
        }

        //购方信息校验
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfMc())) {
            return R.error(ResponseStatusCodes.BUYER_INFO_IS_BLANK, "购方名称不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfNsrsbh())) {
            return R.error(ResponseStatusCodes.BUYER_INFO_IS_BLANK, "购方税号不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfDz())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "购方地址不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfDh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "购方电话不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfYh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "购方银行不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getGhfZh())) {
            return R.error(ResponseStatusCodes.SELLER_INFO_IS_BLANK, "购方账号不能为空");
        }

        //开票人 经办人
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getKpr())) {
            return R.error(ResponseStatusCodes.IS_ILLEGAL, "开票人不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getAgentName())) {
            return R.error(ResponseStatusCodes.IS_ILLEGAL, "经办人不能为空");
        }

        //开票点信息
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getSld())) {
            return R.error(ResponseStatusCodes.ACCESS_POINT_ID_IS_BLANK, "开票点不能为空");
        }
        if (StringUtils.isBlank(specialInvoiceReversalEntity.getSldMc())) {
            return R.error(ResponseStatusCodes.ACCESS_POINT_ID_IS_BLANK, "开票点名称不能为空");
        }

        //审核状态
        if (StringUtils.isNotBlank(specialInvoiceReversalEntity.getStatusCode()) && ArrayUtils.contains(notAllowedEditSubmitStatus, specialInvoiceReversalEntity.getStatusCode())) {
            return R.error(ResponseStatusCodes.CURRENT_SUBMIT_STATUS_NOT_ALLOW_EDIT, "当前审核状态不允许修改");
        }
        if (StringUtils.isNotBlank(specialInvoiceReversalEntity.getStatusCode()) && ArrayUtils.contains(notAllowedEditSubmitStatus, specialInvoiceReversalEntity.getStatusCode())) {
            return R.error(ResponseStatusCodes.CURRENT_SUBMIT_STATUS_NOT_ALLOW_EDIT, "当前审核状态不允许修改");
        }

        //明细信息校验
        if (CollectionUtils.isEmpty(itemList)) {
            return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_ITEM_IS_NULL, "红字申请单明细信息不能为空");
        }

        //合计不含税金额
        BigDecimal hjbhsje = BigDecimal.ZERO;

        for (SpecialInvoiceReversalItem specialInvoiceReversalItemEntity : itemList) {

            //税编
            if (StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getSpbm()) && specialInvoiceReversalItemEntity.getSpbm().length() != 19) {
                return R.error(ResponseStatusCodes.IS_ILLEGAL, "税收分类编码必须为19位数字");
            }
            //成品油校验
            boolean isOil = false;
            if (StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getSpbm())) {
                OilEntity oilEntity = apiTaxClassCodeService.queryOilBySpbm(specialInvoiceReversalItemEntity.getSpbm());
                if (oilEntity != null) {
                    isOil = true;
                }
            }
            boolean result = (SPECIAL_INVOICE_TYPE_1.getKey().equals(specialInvoiceReversalEntity.getType()) || SPECIAL_INVOICE_TYPE_2.getKey().equals(specialInvoiceReversalEntity.getType()))
                    && !isOil;
            if (result) {
                return R.error(ResponseStatusCodes.IS_ILLEGAL, "成品油红字申请单，必须都为成品油类商品");
            }
            if (specialInvoiceReversalItemEntity.getXmmc().equals(ConfigureConstant.XJZSXHQD) && !SPECIAL_INVOICE_TYPE_0.getKey().equals(specialInvoiceReversalEntity.getType())) {
                return R.error(ResponseStatusCodes.FAIL, "成品油类型发票，商品明细不允许为\"" + ConfigureConstant.XJZSXHQD + "\"");
            }
            boolean result1 = SPECIAL_INVOICE_TYPE_2.getKey().equals(specialInvoiceReversalEntity.getType())
                    && (StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getXmsl()) || StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getXmdj()));
            if (result1) {
                return R.error(ResponseStatusCodes.FAIL, "成品油销售金额变更情况，数量、单价必须为空");
            } else {
                boolean result3 = SPECIAL_INVOICE_TYPE_1.getKey().equals(specialInvoiceReversalEntity.getType()) && (StringUtils.isBlank(specialInvoiceReversalItemEntity.getXmsl())
                        || (!ConfigureConstant.STRING_DUN.equals(specialInvoiceReversalItemEntity.getXmdw()) && !"升".equals(specialInvoiceReversalItemEntity.getXmdw())));
                if (result3) {
                    return R.error(ResponseStatusCodes.FAIL, "成品油销售数量变更情况，数量不能为空，单位必须为吨或升");
                }
            }
    
            //含税标志
            if (!HSBZ_0.getKey().equals(specialInvoiceReversalItemEntity.getHsbz())) {
                return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_ITEM_NOT_ALLOWED_INCLUDE_TAX, "红字申请单不允许包含税");
            } else if (StringUtils.isBlank(specialInvoiceReversalItemEntity.getSl()) && itemList.size() > 1) {
                return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_ITEM_TAX_RATE_IS_NULL_NOT_ALLOWED_MULTIPLE_ITEM, "红字申请单商品税率为空，不允许多条明细");
            } else {
                boolean result4 = StringUtils.isBlank(specialInvoiceReversalItemEntity.getSl()) && (
                        StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getXmdj()) || StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getXmsl()));
                if (result4) {
                    return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_ITEM_TAX_RATE_IS_NULL_UNIT_PRICE_AND_QUANTITY_MUST_IS_NULL, "红字申请单商品税率为空，单价及数量必须为空");
                }
            }

            String xmsl = specialInvoiceReversalItemEntity.getXmsl();
            String xmdj = specialInvoiceReversalItemEntity.getXmdj();
            if (StringUtils.isNotBlank(xmsl) && StringUtils.isNotBlank(xmdj)) {
                hjbhsje = hjbhsje.add(DigitUtils.formatDoublePrecision(new BigDecimal(xmsl).multiply(new BigDecimal(xmdj))));
            } else {
                if (StringUtils.isNotBlank(specialInvoiceReversalItemEntity.getXmje())) {
                    hjbhsje = hjbhsje.add(new BigDecimal(specialInvoiceReversalItemEntity.getXmje()));
                }
            }
        }

        if(StringUtils.isNotBlank(specialInvoiceReversalEntity.getHjbhsje())){

            //校验总金额是否相等
            if(hjbhsje.compareTo(new BigDecimal(specialInvoiceReversalEntity.getHjbhsje())) != 0){

                return R.error(ResponseStatusCodes.SPECIAL_INVOICE_REVERSAL_AMOUNT_ERROR, "红字申请单商品税率为空，单价及数量必须为空");
            }
        }

        return R.ok();
    }

    

    /**
     * 删除红字申请单信息
     * @param id 红字申请单ID
     * @return R
     */
    @PostMapping("/specialInvoiceReversals/delete")
    @ApiOperation(value = "红字申请单删除", notes = "红字申请单管理-红字申请单删除")
    @SysLog(operation = "红字申请单删除", operationDesc = "红字申请单删除", key = "红字申请单管理")
    public R deleteSpecialInvoiceReversals(@ApiParam(name = "id", value = "红字申请单id", required = false) @RequestParam String id) {

        try {
            SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService
                    .querySpecialInvoiceReversal(id);
            if (specialInvoiceReversal == null) {
                return R.error(ResponseStatusCodes.IS_NULL, "未找到对应记录");
            }
            
            if (!ArrayUtils.contains(notAllowedEditSubmitStatus, specialInvoiceReversal.getStatusCode())) {
                //数据库事务统一处理删除
                boolean isRemoveSuccess = apiSpecialInvoiceReversalService.deleteSpecialInvoice(specialInvoiceReversal.getId());
                if (!isRemoveSuccess) {
                    return R.error(ResponseStatusCodes.FAIL, "删除失败");
                }
            } else {
                return R.error(ResponseStatusCodes.DATA_NOT_ALLOW_DELETE, "审核中或已审核通过，不允许删除");
            }
            
        } catch (Exception e) {
            log.error("程序处理异常：", e);
            return R.error(ResponseStatusCodes.ERROR, "删除异常");
        }

        log.warn("红字申请单删除成功,id:{}", id);
        return R.ok();
    }
    
    /**
     * 上传红字申请单至税局
     *
     * @param ids 红字申请单ID数组
     * @return R
     */
    @PostMapping("/specialInvoiceReversals/submit")
    @ApiOperation(value = "红字申请单上传税局", notes = "红字申请单管理-红字申请单上传税局")
    @SysLog(operation = "红字申请单上传税局", operationDesc = "红字申请单上传税局", key = "红字申请单管理")
    public R submitSpecialInvoiceReversals(@RequestParam("ids") String[] ids) {

        log.info("红字申请单ID数组 ids:{}", JsonUtils.getInstance().toJsonString(ids));

        try {
            //参数非空校验
            if(ids == null || ids.length <= 0){
                return R.error(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), "红字申请单信息未找到");
            }

            R r = specialInvoiceService.submitSpecialInvoiceReversal(ids);

            return r;

        } catch (Exception e) {
            log.error("程序处理异常：", e);
            return R.error(ResponseStatusCodes.ERROR, "处理异常");
        }
    }
    
    /**
     * 同步税局红字申请单
     *
     * @return R
     */
    @PostMapping("/specialInvoiceReversals/sync")
    @ApiOperation(value = "红字申请单同步", notes = "红字申请单管理-红字申请单同步")
    @SysLog(operation = "红字申请单同步申请单", operationDesc = "红字申请单同步申请单", key = "红字申请单管理")
    public R syncSpecialInvoiceReversals(
            @ApiParam(name = "xhfNsrsbh", value = "销方税号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "startTime", value = "开始时间", required = true) @RequestParam(value = "startTime", required = true) String startTime,
            @ApiParam(name = "endTime", value = "开始时间", required = true) @RequestParam(value = "endTime", required = true) String endTime) {

        try {
            try {
                startTime = DateUtil.format(DateUtil.parse(startTime, "yyyy-MM-dd"), "yyyyMMdd");
                endTime = DateUtil.format(DateUtil.parse(endTime, "yyyy-MM-dd"), "yyyyMMdd");
            } catch (Exception e) {
                log.error("同步审核结果，日期格式错误!");
                return R.error().put(OrderManagementConstant.MESSAGE, "日期格式错误！");
            }

            Map<String, String> resultMap = specialInvoiceService.syncSpecialInvoiceReversal("000000000000", xhfNsrsbh,
                    ConfigureConstant.STRING_1, ORDER_INVOICE_TYPE_0.getKey(), userInfoService.getUser().getUserId().toString(),
                    userInfoService.getUser().getUsername(), startTime, endTime);
            log.info("红字申请单信息同步返回信息:{}",JsonUtils.getInstance().toJsonString(resultMap));
            return R.ok().put(OrderManagementConstant.CODE, resultMap.get(OrderManagementConstant.CODE))
                    .put(OrderManagementConstant.MESSAGE, resultMap.get(OrderManagementConstant.MESSAGE));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("程序处理异常：", e);
            return R.error(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), "同步申请单异常!");
        }
    }



    @PostMapping("/specialInvoiceReversals/revoke")
    @ApiOperation(value = "红字申请单撤销", notes = "红字申请单管理-红字申请单撤销")
    @SysLog(operation = "红字申请单撤销", operationDesc = "红字申请单撤销", key = "红字申请单管理")
    public R revoke(@ApiParam(name = "红字申请单id,红字信息表编号和税号组合 格式 [{\"id\":\"1234231421\",\"xxbbh\":\"22343\",\"xhfNsrsbh\":\"5263256369633658\"}]" ) @RequestBody String param) {

        try {

            List<R> resultList = new ArrayList<>();

            List<Map> maps = JSON.parseArray(param, Map.class);
            //参数校验
            for(Map map : maps){
                String id = (map.get("id") == null ? "" : String.valueOf(map.get("id")));
                String xhfNsrsbh = (map.get("xhfNsrsbh") == null ? "" : String.valueOf(map.get("xhfNsrsbh")));
                String xxbbh = map.get("xxbbh") == null ? "" : String.valueOf(map.get("xxbbh"));
                if(StringUtils.isBlank(id) || StringUtils.isBlank(xhfNsrsbh)){
                    log.error("{}红字信息表编号：{},参数错误",xxbbh);

                    R r = R.error().put(OrderManagementConstant.MESSAGE,"红字信息表编号" +  xxbbh + "参数错误!");
                    resultList.add(r);
                }
            }
            if(!CollectionUtils.isEmpty(resultList)){
                return R.error().put(OrderManagementConstant.DATA,resultList);
            }
            //调用接口
            int successCount = 0;
            for(Map map : maps){

                String id = (map.get("id") == null ? "" : String.valueOf(map.get("id")));
                String xhfNsrsbh = (map.get("xhfNsrsbh") == null ? "" : String.valueOf(map.get("xhfNsrsbh")));
                R revoke = specialInvoiceService.revoke(id, xhfNsrsbh);
                if(OrderInfoContentEnum.SUCCESS.getKey().equals(revoke.get(OrderManagementConstant.CODE))){
                    successCount++;
                }else{
                    resultList.add(revoke);
                }
            }
            if(successCount == maps.size()){
                return R.ok().put(OrderManagementConstant.MESSAGE,"撤销成功!");
            }else{

                int faildCount = maps.size() - successCount;
                return R.ok().put("successCount",successCount).put("faildCount",faildCount)
                        .put(OrderManagementConstant.DATA,resultList);
            }
        } catch (Exception e) {
            log.error("程序处理异常：{}", e);
            return R.error(OrderInfoContentEnum.RECEIVE_FAILD.getKey(), "同步申请单异常!");
        }
    }
    
    /**
     * 在使用
     * 蓝票转换为红票信息
     *
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @return R
     */
    @RequestMapping(value = "/specialInvoiceReversals/invocie", method = RequestMethod.GET)
    @ApiOperation(value = "红字申请单查询蓝票", notes = "红字申请单管理-红字申请单对应蓝票查询")
    public R getSpecialInvoices(@RequestParam("invoiceCode") String invoiceCode, @RequestParam("invoiceNo") String invoiceNo,
                                @RequestParam("reason") String reason) {

        R r = specialInvoiceService.mergeSpecialInvoice(invoiceCode, invoiceNo);
        if (ResponseStatusCodes.SUCCESS.equals(r.get(OrderManagementConstant.CODE))) {
            if (r.get(OrderManagementConstant.DATA) != null) {
                CommonSpecialInvoice commonSpecialInvoice = JsonUtils.getInstance().parseObject(JsonUtils.getInstance().toJsonString(r.get(OrderManagementConstant.DATA)), CommonSpecialInvoice.class);

                if (StringUtils.isNotBlank(reason) && commonSpecialInvoice != null) {
                    /**
                     * 传递申请单类型,并且是购方未抵扣的数据进行校验,
                     * 判断购方税号是否在当前登录用户信息税号数组中
                     */
                    if (SPECIAL_INVOICE_REASON_1010000000.getKey().equals(reason)) {
                        DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityByTaxplayercode(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getGhfNsrsbh());
                        if (sysDeptEntity == null) {
                            r.put(OrderManagementConstant.CODE, ResponseStatusCodes.ERROR);
                            r.put(OrderManagementConstant.ALL_MESSAGE, "购方未抵扣申请,对应发票数据与购方信息不符");
                            return r;
                        }
                    } else if (SPECIAL_INVOICE_REASON_0000000100.getKey().equals(reason)) {
                        DeptEntity sysDeptEntity = userInfoService.querySysDeptEntityByTaxplayercode(commonSpecialInvoice.getSpecialInvoiceReversalEntity().getXhfNsrsbh());
                        if (sysDeptEntity == null) {
                            r.put(OrderManagementConstant.CODE, ResponseStatusCodes.ERROR);
                            r.put(OrderManagementConstant.ALL_MESSAGE, "销方申请,对应发票数据与销方信息不符");
                            return r;
                        }
                    }
                }
            }
        }
        return r;
    }
    
    /**
     * 获取红字申请单号
     *
     * @param accessPointId 开票点ID
     * @param mechainCode   税盘编号
     * @return R
     */
    @RequestMapping(value = "/specialInvoiceReversals/code", method = RequestMethod.GET)
    @ApiOperation(value = "红字申请单获取编码code", notes = "红字申请单管理-红字申请单获取编码code")
    public R getSpecialInvoiceReversalCode(@ApiParam(value =  "受理点",name = "受理点")@RequestParam("accessPointId") String accessPointId,
                                           @ApiParam(value =  "机器编码",name = "机器编码")@RequestParam("mechainCode")String mechainCode,
                                           @ApiParam(value =  "纳税人识别号",name = "纳税人识别号")@RequestParam("xhfNsrsbh")String xhfNsrsbh) {
        
        try {
            String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
            if (xfshs.length > 1) {
                log.error("当前操作不支持多税号进行操作.请求参数:{}", JsonUtils.getInstance().toJsonString(xfshs));
                return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
            }
            String nsrsbh = xfshs[0];
            //获取税控设备
            String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
            
            Map<String, String> resultMap = specialInvoiceService.querySpecialInvoiceReversalCode(accessPointId,
                    mechainCode, ORDER_INVOICE_TYPE_0.getKey(), nsrsbh, terminalCode);
            
            return R.ok().put("specialInvoiceReversalCode",
                    StringUtils.isNotBlank(resultMap.get(OrderManagementConstant.CODE)) ? resultMap.get(OrderManagementConstant.CODE) : "");
        } catch (OrderReceiveException e) {
            log.error(e.getMessage());
            return R.error().put(OrderManagementConstant.MESSAGE, e.getMessage());
        }
    }
    
    /**
     * 预览红字申请单
     * @param response
     * @param id          红字申请单ID
     * @param suffixName  文件后缀名，支持：pdf、html
     * @param operateType 操作类型，支持：preview、download
     */
    @RequestMapping(value = "/specialInvoiceReversals/preview", method = RequestMethod.GET)
    @ApiOperation(value = "红字申请单预览", notes = "红字申请单管理-红字申请单预览")
    @SysLog(operation = "红字申请单预览", operationDesc = "红字申请单预览", key = "红字申请单管理")
    public void previewOrDownloadSpecialInvoiceReversal(HttpServletResponse response, @ApiParam(value =  "红字申请单id",name = "红字申请单id")@RequestParam("id") String id,
                                                        @ApiParam(value =  "文件后缀名",name = "文件后缀名")@RequestParam("suffixName") String suffixName,
                                                        @ApiParam(value =  "操作类型",name = "操作类型")@RequestParam("operateType")String operateType) throws IOException {

        Writer writer = null;

        if(StringUtils.isBlank(id)){
            log.error("红字申请单预览，id不能为空!");
            Map<String, String> resultMap = new HashMap<String, String>(2);
            resultMap.put(OrderManagementConstant.CODE,OrderInfoContentEnum.RECEIVE_FAILD.getKey());
            resultMap.put(OrderManagementConstant.MESSAGE,"参数错误!");
            PrintWriter writer1 = response.getWriter();
            writer1.write(JsonUtils.getInstance().toJsonString(resultMap));
            writer1.flush();
            writer1.close();
            return;
        }
        try {
            String contentDisposition = getContentDisposition(suffixName, operateType);
            if (StringUtils.isNotBlank(contentDisposition)) {
                response.addHeader("Content-Disposition", contentDisposition);
                if ("html".equals(suffixName)) {
                    response.setContentType("text/html; charset=UTF-8");
                    writer = response.getWriter();
                    
                    getSpecialInvoiceReversalListWriter(id, writer);
                } else if ("pdf".equals(suffixName)) {
                    response.setContentType("application/pdf; charset=UTF-8");
                    
                    String realPath = SpecialInvoiceReversalController.class.getResource("/").getPath();
                    String fileDir = "/redinvoice/sir";
                    File rootPath = new File(realPath + fileDir);
                    if (!rootPath.exists()) {
                        if (!rootPath.mkdirs()) {
                            throw new Exception("创建文件夹失败");
                        }
                    }
                    String fileName = id + ".html";
                    File file = new File(realPath + fileDir + File.separator + fileName);
                    writer = new FileWriter(file);
                    getSpecialInvoiceReversalListWriter(id, writer);
                    
                    OutputStream out = response.getOutputStream();
                    
                    ITextRenderer renderer = new ITextRenderer();
                    renderer.setDocument(file);
                    
                    ITextFontResolver font = renderer.getFontResolver();
                    font.addFont(realPath + "redinvoice/SIMSUN.TTC", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                    
                    renderer.layout();
                    renderer.createPDF(out);
                }
            }
        } catch (Exception e) {
            log.error("程序处理异常：", e);
        } finally {
            if (null != writer) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    log.error("程序处理异常：", e);
                }
            }
        }
    }
    
    private String getContentDisposition(String suffixName, String operateType) throws UnsupportedEncodingException {
        String contentDisposition = null;
        
        String fileName = getSepcialInvoiceReversalFileName(suffixName);
        fileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
        if (StringUtils.isNotBlank(fileName)) {
            switch (operateType) {
                case "preview":
                    contentDisposition = "inline;filename=" + fileName;
                    break;
                case "download":
                    contentDisposition = "attachment;filename=" + fileName;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + operateType);
            }
        }
        
        return contentDisposition;
    }
    
    private String getSepcialInvoiceReversalFileName(String suffixName) throws UnsupportedEncodingException {
        String fileName = null;
        
        switch (suffixName) {
            case "html":
                fileName = "红字发票信息表.html";
                break;
            case "pdf":
                fileName = "红字发票信息表.pdf";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + suffixName);
        }
        
        return fileName;
    }
    
    /**
     * 根据模板生成html
     *
     * @param id
     * @param writer
     */
    private void getSpecialInvoiceReversalListWriter(String id, Writer writer) {
        Configuration conf = new Configuration(Configuration.VERSION_2_3_0);
        try {
            conf.setClassLoaderForTemplateLoading(SpecialInvoiceReversalController.class.getClassLoader(), "/redinvoice/");
            
            SpecialInvoiceReversalEntity specialInvoiceReversal = apiSpecialInvoiceReversalService.querySpecialInvoiceReversal(id);
            if (null != specialInvoiceReversal) {
                Map<String, Object> params = new HashMap<>(15);
                params.put("tksj", DateUtil.format(specialInvoiceReversal.getTksj(),"yyyy-MM-dd HH:mm:ss"));
                params.put("xhfMc", specialInvoiceReversal.getXhfMc());
                params.put("xhfNsrsbh", specialInvoiceReversal.getXhfNsrsbh());
                params.put("ghfMc", specialInvoiceReversal.getGhfMc());
                params.put("ghfNsrsbh", specialInvoiceReversal.getGhfNsrsbh());
                params.put("hjbhsje", specialInvoiceReversal.getHjbhsje());
                params.put("hjse", specialInvoiceReversal.getHjse());
                params.put("sqsm", specialInvoiceReversal.getSqsm());
                params.put("yfpDm", specialInvoiceReversal.getYfpDm());
                params.put("yfpHm", specialInvoiceReversal.getYfpHm());
                params.put("xxbbh", StringUtils.isNotBlank(specialInvoiceReversal.getXxbbh()) ? specialInvoiceReversal.getXxbbh() : "");
    
                List<SpecialInvoiceReversalItem> specialInvoiceReversalItems = apiSpecialInvoiceReversalService.querySpecialInvoiceReversalItems(specialInvoiceReversal.getId());
                params.put("items", specialInvoiceReversalItems);
    
                Template temp = conf.getTemplate("specialInvoiceReversalList.ftl", StandardCharsets.UTF_8.name());
                temp.process(params, writer);
            }
        } catch (IOException | TemplateException e) {
            log.error("程序处理异常：", e);
        }
    }
    
    /**
     * 开具红字发票
     *
     * @param sld   开票点ID
     * @param sldMc 开票点名称
     * @param fjh   分机号
     * @return
     * @throws OrderSeparationException
     */
    @PostMapping("/specialInvoiceReversals/addInvoice")
    @ApiOperation(value = "红字申请单开具", notes = "红字申请单管理-红字申请单开具")
    @SysLog(operation = "红字申请单开具", operationDesc = "红字申请单开具", key = "红字申请单管理")
    public R addInvoice(@ApiParam(value =  "申请单id数组",name = "申请单id数组")@RequestParam("ids") String[] ids,
                        @ApiParam(value =  "受理点id",name = "受理点id")@RequestParam("sld") String sld,
                        @ApiParam(value =  "受理点名称",name = "受理点名称")@RequestParam("sldMc") String sldMc,
                        @ApiParam(value =  "分机号",name = "分机号")@RequestParam("fjh") String fjh,
                        @ApiParam(value =  "销方纳税人识别号",name = "销方纳税人识别号")@RequestParam("xhfNsrsbh") String xhfNsrsbh) throws OrderSeparationException {
    
        String[] xfshs = JsonUtils.getInstance().fromJson(xhfNsrsbh, String[].class);
        if (xfshs.length > 1) {
            log.error("当前操作不支持多税号进行操作.请求参数:{}", JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
        Map<String, Object> addInvoice = specialInvoiceService.addInvoice(ids, sld, sldMc, fjh, userInfoService.getUser().getUserId().toString(),
                userInfoService.getUser().getUsername(), userInfoService.getDepartment().getDeptId(), nsrsbh);
        R r = new R();
        r.putAll(addInvoice);
        return r;
    }
    
    /**
     * 下载红字申请单导入模板
     *
     * @param response
     */
    @RequestMapping(value = "/specialInvoiceReversals/downloadTemplate", method = RequestMethod.GET)
    @ApiOperation(value = "红字申请单模板下载", notes = "红字申请单管理-红字申请单模板下载")
    @SysLog(operation = "红字申请单模板下载", operationDesc = "红字申请单详模板下载", key = "红字申请单管理")
    public void downloadTemplate(HttpServletResponse response) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + new String("批量导入红字信息表模板.xlsx".getBytes("GB2312"), "ISO8859-1"));
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            OutputStream os = response.getOutputStream();
            
            String realPath = SpecialInvoiceReversalController.class.getResource("/").getPath();
            File templateFile = new File(realPath + "redinvoice/importSpecialInvoiceReversal.xlsx");
            
            fis = new FileInputStream(templateFile);
            
            byte[] buff = new byte[1024];
            bis = new BufferedInputStream(fis);
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
            }
        } catch (Exception e) {
            log.error("红字申请单下载异常{}", e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * axtivex专用
     * 上传成功之后更新红票信息表
     */
    @PostMapping("/updateSpecialInvoiceReversal")
    @ApiOperation(value = "红字申请单上传-ActiveX", notes = "红字申请单管理-红字申请单上传")
    @SysLog(operation = "红字申请单上传ActiveX", operationDesc = "红字申请单上传", key = "红字申请单管理")
    public R updateSpecialInvoiceReversal(@RequestParam("specialId") String specialId,
                                          @RequestParam("submitCode") String submitCode,
                                          @RequestParam("submitStatus") String submitStatus,
                                          @RequestParam("submitStatusDesc") String submitStatusDesc) {
        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.querySpecialInvoiceReversal(specialId);
        if (!ObjectUtils.isEmpty(specialInvoiceReversalEntity)) {
            specialInvoiceReversalEntity.setId(specialId);
            specialInvoiceReversalEntity.setXxbbh(submitCode);
            //未处理和已提交
            if (ConfigureConstant.STRING_0.equals(submitStatus) || ConfigureConstant.STRING_1.equals(submitStatus)) {
                specialInvoiceReversalEntity.setStatusCode("TZD0071");
                specialInvoiceReversalEntity.setStatusMessage("待查证");
                apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversalEntity);
            } else if (ConfigureConstant.STRING_2.equals(submitStatus)) {
                //审核通过
                specialInvoiceReversalEntity.setStatusCode("TZD0000");
                specialInvoiceReversalEntity.setStatusMessage("审核通过");
                apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversalEntity);
            }
        } else {
            return R.error("红字信息表查询失败");
        }
        return R.ok("红字信息表更新成功");
    }
    
    /**
     * 下载成功之后更新红票信息表
     * activex专用
     */
    @PostMapping("/updateDownloadSpecialInvoiceReversal")
    @ApiOperation(value = "红字申请单下载-ActiveX", notes = "红字申请单管理-红字申请单下载")
    @SysLog(operation = "红字申请单下载ActiveX", operationDesc = "红字申请单下载", key = "红字申请单管理")
    public R updateDownloadSpecialInvoiceReversal(@RequestParam("specialId") String specialId,
                                                  @RequestParam("submitStatus") String submitStatus,
                                                  @RequestParam("submitStatusDesc") String submitStatusDesc) {
        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = apiSpecialInvoiceReversalService.querySpecialInvoiceReversal(specialId);
        if (!ObjectUtils.isEmpty(specialInvoiceReversalEntity)) {
            specialInvoiceReversalEntity.setId(specialId);
            //未处理和已提交
            if (ConfigureConstant.STRING_0.equals(submitStatus) || ConfigureConstant.STRING_1.equals(submitStatus)) {
                specialInvoiceReversalEntity.setStatusCode("TZD0071");
                specialInvoiceReversalEntity.setStatusMessage("待查证");
                apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversalEntity);
            } else if (ConfigureConstant.STRING_2.equals(submitStatus)) {
                //审核通过
                specialInvoiceReversalEntity.setStatusCode("TZD0000");
                specialInvoiceReversalEntity.setStatusMessage("审核通过");
                apiSpecialInvoiceReversalService.updateSpecialInvoiceReversal(specialInvoiceReversalEntity);
            }
        } else {
            return R.error("红字信息表查询失败");
        }
        return R.ok("红字信息表下载更新成功");
    }
    
}
