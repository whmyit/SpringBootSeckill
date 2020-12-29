package com.dxhy.order.consumer.modules.invoice.controller;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.invoice.service.SpecialInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.R;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.SpecialInvoiceReversalItem;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.entity.CommonSpecialInvoice;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import com.dxhy.order.model.entity.SpecialExcelImport;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static com.dxhy.order.constant.OrderInfoEnum.*;
/**
 * 红字申请单导入控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:40
 */
@RestController
@Slf4j
@Api(value = "红字申请单导入", tags = {"发票模块"})
@RequestMapping("/pinvoice")
public class SpecialInvoiceImportController {
    
    
    private static final String LOGGER_MSG = "(红字申请单申请单表格导入业务处理)";
    
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
    
    @Resource
    private UserInfoService userInfoService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    /**
     * 红字申请单导入
     *
     * @param file 导入excel文件 update by ysy 红字申请单导入流程优化
     *             流程: 1.根据sheet区分 购方申请，销方申请 sheet1 购方申请已抵扣 sheet2 购买申请未抵扣 sheet3 销方申请
     *             2.申请单编号相同的行为同一红字申请单的明细
     *             3.excel数据转换后校验数据 失败后返回错误数据
     * @return
     */
    @PostMapping("/special-invoice-reversals/import")
    @ApiOperation(value = "红字申请单导入", notes = "红字申请单管理-红字申请单导入")
    @SysLog(operation = "红字申请单导入", operationDesc = "红字申请单导入", key = "红字申请单管理")
    public R importSpecialInvoiceReversalsCopy(
            @ApiParam(name = "file", value = "导入的excel文件", required = true) @RequestParam(value = "file", required = true) MultipartFile file,
            @ApiParam(name = "size", value = "文件大小", required = true) @RequestParam(value = "size", required = true) String size,
            @ApiParam(name = "lastModifiedDate", value = "上次传输时间", required = true) @RequestParam(value = "lastModifiedDate", required = true) String lastModifiedDate,
            @ApiParam(name = "xhfYh", value = "销货方银行", required = true) @RequestParam(value = "xhfYh", required = true) String xhfYh,
            @ApiParam(name = "xhfDz", value = "销货方地址", required = true) @RequestParam(value = "xhfDz", required = true) String xhfDz,
            @ApiParam(name = "xhfDh", value = "销货方电话", required = true) @RequestParam(value = "xhfDh", required = true) String xhfDh,
            @ApiParam(name = "xhfZh", value = "销货方账号", required = true) @RequestParam(value = "xhfZh", required = true) String xhfZh,
            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
            @ApiParam(name = "xhfMc", value = "销货方纳税人名称", required = true) @RequestParam(value = "xhfMc", required = true) String xhfMc) {
        
        List<String> errorList = new ArrayList<>();
        
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        
        int succesCount = 0;
        try {

            //查询当前登录用户信息
            UserEntity user = userInfoService.getUser();

            // 查询税控设备
            String terminalCode = apiTaxEquipmentService.getTerminalCode(xhfNsrsbh);
            // 查询开票人
            DrawerInfoEntity drawerInfoEntity = invoiceService.queryDrawerInfo(xhfNsrsbh,user.getUserId().toString());
            if (drawerInfoEntity == null || StringUtils.isBlank(drawerInfoEntity.getDrawerName())) {
                errorList.add("未找到开票人信息");
                return R.error(ResponseStatusCodes.DRAWER_NOT_FOUND, "未找到开票人信息")
                        .put(OrderManagementConstant.ERROR_MESSAGE_LIST, errorList);
            }
    
            // 查询受理点信息
            List<String> list = new ArrayList<>();
            list.add(xhfNsrsbh);
            String accessPointId = "";
            String accessPointName = "";
            String machineCode = "";
            /**
             * 支持方格开票
             */
            if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)
                    || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(xhfNsrsbh, null);
                //方格税盘单独处理
                RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
                accessPointId = registrationCode.getJqbh();
                accessPointName = registrationCode.getJqbh();
                machineCode = registrationCode.getJqbh();
    
            } else {
                String invoiceType = ORDER_INVOICE_TYPE_0.getKey();
                String url = OpenApiConfig.querySldList;
                if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                    url = OpenApiConfig.queryKpdXxBw;
                } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                    url = OpenApiConfig.queryNsrXnsbxx;
                    /**
                     * 如果是新税控转换发票种类代码
                     */
                    invoiceType = CommonUtils.transFplxdm(invoiceType);
                }
                Set<SearchSld> searchSldSet = new HashSet<>();
                HttpInvoiceRequestUtil.getSldList(searchSldSet, url, invoiceType, "", xhfNsrsbh, null, null, terminalCode);
                if (CollectionUtils.isEmpty(searchSldSet)) {
                    errorList.add("未找到开票点");
                    return R.error(ResponseStatusCodes.ACCESS_POINT_NOT_FOUND, "未找到开票点")
                            .put(OrderManagementConstant.ERROR_MESSAGE_LIST, errorList);
                }
    
                // 根据受理点后去code值
                SearchSld sldJspxx = new ArrayList<>(searchSldSet).get(0);
                accessPointId = sldJspxx.getSldId() + "";
                accessPointName = sldJspxx.getSldMc() + "";
                machineCode = sldJspxx.getJqbh() + "";
            }
    
    
            // excel数据读取
            List<SpecialExcelImport> specialExcelImportList = specialInvoiceService
                    .readSpecialInvoiceFromExcel(file);
            log.debug("从excel表格中读取到的红字申请单数据:{}", JsonUtils.getInstance().toJsonString(specialExcelImportList));
    
            // 数据校验
            Map<String, Object> resultMap = validateOrderInfo.checkSpecialExcelImport(specialExcelImportList);
            if (!ConfigureConstant.STRING_0000.equals(resultMap.get(OrderManagementConstant.ERRORCODE))) {
                log.error("数据非空校验未通过，未通过数据:{}", resultMap);
                return R.error().put(OrderManagementConstant.CODE, resultMap.get(OrderManagementConstant.ERRORCODE))
                        .put(OrderManagementConstant.MESSAGE, resultMap.get(OrderManagementConstant.ERRORMESSAGE))
                        .put(OrderManagementConstant.ERROR_MESSAGE_LIST,
                                resultMap.get(OrderManagementConstant.ERROR_MESSAGE_LIST));
            }
            
            // excel数据转换
            List<CommonSpecialInvoice> commonSpecialInvoiceList = transSpecialInvoice(specialExcelImportList,user);
            
            // 发票数据补全
            commonSpecialInvoiceList = specialInvoiceService.completeOrderInvoiceInfo(commonSpecialInvoiceList,
                    accessPointId, accessPointName, machineCode, terminalCode, drawerInfoEntity,
                    user.getUserId().toString(), user.getUsername(), xhfMc, xhfNsrsbh, xhfDz, xhfDh, xhfYh, xhfZh);
            
            log.debug("{}处理后数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(commonSpecialInvoiceList));
            // 数据保存
            boolean isSaveSuccess = specialInvoiceService.saveSpecialInvoiceInfo(commonSpecialInvoiceList);
            
            if (!isSaveSuccess) {
                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR4);
            } else {
                succesCount = commonSpecialInvoiceList.size();
            }
            
            
        } catch (OrderReceiveException e) {
            log.error("{}红字申请单导入异常:{}", LOGGER_MSG, e);
            errorList.add(e.getMessage());
            return R.error(e.getCode(), e.getMessage()).put(OrderManagementConstant.ERROR_MESSAGE_LIST, errorList);
        } catch (IllegalArgumentException e) {
            log.error("{}红字申请单导入异常:{}", LOGGER_MSG, e);
            errorList.add("模板格式错误");
            return R.error(ResponseStatusCodes.ERROR, "模板格式错误").put(OrderManagementConstant.ERROR_MESSAGE_LIST,
                    errorList);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{}红字申请单导入异常:{}", LOGGER_MSG, e);
            errorList.add(e.getMessage());
            return R.error(ResponseStatusCodes.ERROR, "模板格式错误").put(OrderManagementConstant.ERROR_MESSAGE_LIST,
                    errorList);
        }
        
        return R.ok().put("succesCount", succesCount);
    }
    
    /**
     * @param specialExcelImportList
     * @return
     */
    private List<CommonSpecialInvoice> transSpecialInvoice(List<SpecialExcelImport> specialExcelImportList,UserEntity user) throws OrderReceiveException {
        /**
         * 循环遍历数据,获取申请编号一样的数据,放在一个对象中.
         */
        List<CommonSpecialInvoice> commonSpecialInvoiceList = new ArrayList<>();
        Map<String, CommonSpecialInvoice> sqdMap = new HashMap<>(10);

        for (SpecialExcelImport specialExcelImport : specialExcelImportList) {
            //获取当前处理数据对应流水号一样的数据,如果是第一条则为空；不为第一条则为对应流水号一致的数组
            CommonSpecialInvoice commonSpecialInvoice = sqdMap.get(specialExcelImport.getSqdwybh());

            if (commonSpecialInvoice != null) {
                //校验主体数据合法性
                SpecialInvoiceReversalEntity specialInvoiceReversalEntity = commonSpecialInvoice.getSpecialInvoiceReversalEntity();
                
                if (specialInvoiceReversalEntity.getSqdh().equals(specialExcelImport.getSqdwybh())) {
                    //如果成品油发票类型不一致返回错误
                    if (!specialExcelImport.getCypzyfplx().equals(specialInvoiceReversalEntity.getType())) {
                        log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_CPYZYFPLX_ERROR.getMessage());
                        throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_CPYZYFPLX_ERROR);
                        //如果申请原因不一致返回错误
                    } else if (!specialExcelImport.getSqyy().equals(specialInvoiceReversalEntity.getSqsm())) {
                        log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_SQYY_ERROR.getMessage());
                        throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_SQYY_ERROR);
                    }
                    //如果申请原因一致
                    if (specialExcelImport.getSqyy().equals(specialInvoiceReversalEntity.getSqsm())) {
                        //购方已抵扣,销方名称和销方税号必须一致
                        if (SPECIAL_INVOICE_REASON_1100000000.getKey().equals(specialExcelImport.getSqyy())) {
                            
                            if (!specialExcelImport.getXhfMc().equals(specialInvoiceReversalEntity.getXhfMc())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFMC_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFMC_ERROR);
                            } else if (!specialExcelImport.getXhfSh().equals(specialInvoiceReversalEntity.getXhfNsrsbh())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFSH_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFSH_ERROR);
                            }
                            //购方未抵扣,原发票代码,原发票号码,销方名称,销方税号必须一致
                        } else if (SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialExcelImport.getSqyy())) {
                            
                            if (!specialExcelImport.getXhfMc().equals(specialInvoiceReversalEntity.getXhfMc())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFMC_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFMC_ERROR);
                            } else if (!specialExcelImport.getXhfSh().equals(specialInvoiceReversalEntity.getXhfNsrsbh())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFSH_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_XFSH_ERROR);
                            } else if (!specialExcelImport.getYfpDm().equals(specialInvoiceReversalEntity.getYfpDm())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPDM_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPDM_ERROR);
                            } else if (!specialExcelImport.getYfpHm().equals(specialInvoiceReversalEntity.getYfpHm())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPHM_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPHM_ERROR);
                            }
                            //销方申请,原发票代码,原发票号码,购方名称,购方税号必须一致
                        } else if (SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy())) {
                            
                            if (!specialExcelImport.getGhfMc().equals(specialInvoiceReversalEntity.getGhfMc())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_GFMC_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_GFMC_ERROR);
                            } else if (!specialExcelImport.getGhfSh().equals(specialInvoiceReversalEntity.getGhfNsrsbh())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_GFSH_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_GFSH_ERROR);
                            } else if (!specialExcelImport.getYfpDm().equals(specialInvoiceReversalEntity.getYfpDm())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPDM_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPDM_ERROR);
                            } else if (!specialExcelImport.getYfpHm().equals(specialInvoiceReversalEntity.getYfpHm())) {
                                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPHM_ERROR.getMessage());
                                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_YFPHM_ERROR);
                            }
                            
                            
                        }
                        
                    }
                }
            }
            //合并明细数据
            commonSpecialInvoice = transSpecialInvoice(commonSpecialInvoice, specialExcelImport,user);
            
            //校验明细数据
            if (commonSpecialInvoice.getSpecialInvoiceReversalItemEntities().size() > 8) {
                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ITEM_ERROR.getMessage());
                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ITEM_ERROR);
            }
            
            if (commonSpecialInvoice.getSpecialInvoiceReversalItemEntities().size() > 1 && specialExcelImport.getSpMc().equals(ConfigureConstant.XJZSXHQD)) {
                log.error("{}数据转换异常:{}", LOGGER_MSG, OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMMC_ERROR1.getMessage());
                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_XMMC_ERROR1);
            }
            
            
            sqdMap.put(specialExcelImport.getSqdwybh(), commonSpecialInvoice);
            commonSpecialInvoiceList = new ArrayList<>(sqdMap.values());
            
            
        }
        return commonSpecialInvoiceList;
    }


    private CommonSpecialInvoice transSpecialInvoice(CommonSpecialInvoice commonSpecialInvoice, SpecialExcelImport specialExcelImport,UserEntity user) throws OrderReceiveException {




        if (commonSpecialInvoice == null) {
            commonSpecialInvoice = new CommonSpecialInvoice();
            SpecialInvoiceReversalEntity specialInvoiceReversalEntity = new SpecialInvoiceReversalEntity();
            specialInvoiceReversalEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
            specialInvoiceReversalEntity.setHjbhsje(ConfigureConstant.STRING_000);
            specialInvoiceReversalEntity.setHjse(ConfigureConstant.STRING_000);
            specialInvoiceReversalEntity.setKphjje(ConfigureConstant.STRING_000);
            specialInvoiceReversalEntity.setFpzlDm(ORDER_INVOICE_TYPE_0.getKey());
            specialInvoiceReversalEntity.setInvoiceType(ConfigureConstant.STRING_1);
            specialInvoiceReversalEntity.setGhfqylx(GHF_QYLX_01.getKey());
            specialInvoiceReversalEntity.setYfpKprq(StringUtils.isBlank(specialExcelImport.getYlprq()) ?
                    null : DateUtil.parse(specialExcelImport.getYlprq(),"yyyy-MM-dd HH:mm:ss"));
            //如果表格数据为销方申请,并且发票代码号码不为空,明细行名称,金额,税额为空,则设置数据为使用原蓝票数据
            if (SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy()) && StringUtils.isNotBlank(specialExcelImport.getYfpDm()) && StringUtils.isNotBlank(specialExcelImport.getYfpHm()) && StringUtils.isBlank(specialExcelImport.getSpMc()) && StringUtils.isBlank(specialExcelImport.getXmje()) && StringUtils.isBlank(specialExcelImport.getXmSe())) {
                specialInvoiceReversalEntity.setUseOldInvoiceData(ConfigureConstant.STRING_Y);
            } else if (SPECIAL_INVOICE_REASON_1010000000.getKey().equals(specialExcelImport.getSqyy()) && StringUtils.isNotBlank(specialExcelImport.getYfpDm()) && StringUtils.isNotBlank(specialExcelImport.getYfpHm()) && StringUtils.isBlank(specialExcelImport.getSpMc()) && StringUtils.isBlank(specialExcelImport.getXmje()) && StringUtils.isBlank(specialExcelImport.getXmSe())) {
                specialInvoiceReversalEntity.setUseOldInvoiceData(ConfigureConstant.STRING_Y);
            } else {
                specialInvoiceReversalEntity.setUseOldInvoiceData(ConfigureConstant.STRING_N);
            }
            
            
            List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntityList = new ArrayList<>();
            commonSpecialInvoice.setSpecialInvoiceReversalEntity(specialInvoiceReversalEntity);
            commonSpecialInvoice.setSpecialInvoiceReversalItemEntities(specialInvoiceReversalItemEntityList);
            
        }
        SpecialInvoiceReversalEntity specialInvoiceReversalEntity = commonSpecialInvoice.getSpecialInvoiceReversalEntity();
        List<SpecialInvoiceReversalItem> specialInvoiceReversalItemEntityList = commonSpecialInvoice.getSpecialInvoiceReversalItemEntities();
        specialInvoiceReversalEntity.setSqdh(specialExcelImport.getSqdwybh());
        specialInvoiceReversalEntity.setType(specialExcelImport.getCypzyfplx());
        specialInvoiceReversalEntity.setYfpDm(specialExcelImport.getYfpDm());
        specialInvoiceReversalEntity.setYfpHm(specialExcelImport.getYfpHm());
        specialInvoiceReversalEntity.setSqsm(specialExcelImport.getSqyy());
        //如果是购方申请,则购方信息存储excel对象的销方信息
        specialInvoiceReversalEntity.setXhfMc(specialExcelImport.getXhfMc());
        specialInvoiceReversalEntity.setXhfNsrsbh(specialExcelImport.getXhfSh());
        specialInvoiceReversalEntity.setGhfMc(specialExcelImport.getGhfMc());
        specialInvoiceReversalEntity.setGhfNsrsbh(specialExcelImport.getGhfSh());
        specialInvoiceReversalEntity.setCreatorId(user.getUserId().toString());
        specialInvoiceReversalEntity.setCreatorName(user.getUsername());
        specialInvoiceReversalEntity.setEditorId(user.getUserId().toString());
        specialInvoiceReversalEntity.setEditorName(user.getUsername());
        String taxRate = specialInvoiceReversalEntity.getDslbz();
        if (StringUtils.isBlank(taxRate)) {
            taxRate = StringUtil.formatSl(specialExcelImport.getSLv());
        } else if (!taxRate.equals(specialExcelImport.getSLv()) && !"多税率".equals(taxRate)) {
            taxRate = "多税率";
        }
        specialInvoiceReversalEntity.setDslbz(taxRate);
        specialInvoiceReversalEntity.setCreateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(new Date())));
        specialInvoiceReversalEntity.setUpdateTime(specialInvoiceReversalEntity.getCreateTime());
        specialInvoiceReversalEntity.setStatusCode(SPECIAL_INVOICE_STATUS_TZD0500.getKey());
        specialInvoiceReversalEntity.setStatusMessage(SPECIAL_INVOICE_STATUS_0.getKey());
        
        String xmje;
        String xmse;
        if (StringUtils.isNotBlank(specialExcelImport.getXmje())) {
            xmje = DecimalCalculateUtil.decimalFormatToString(specialExcelImport.getXmje(), ConfigureConstant.INT_2);
        } else {
            xmje = ConfigureConstant.STRING_000;
        }
        
        if (StringUtils.isNotBlank(specialExcelImport.getXmSe())) {
            xmse = DecimalCalculateUtil.decimalFormatToString(specialExcelImport.getXmSe(), ConfigureConstant.INT_2);
        } else {
            xmse = ConfigureConstant.STRING_000;
        }
        
        //如果为销方申请,并且为使用原蓝票数据,判断当前处理明细数据是否存在填写商品名称或者是金额或者是税额的数据,如果有返回异常.
        if (SPECIAL_INVOICE_REASON_0000000100.getKey().equals(specialExcelImport.getSqyy()) && ConfigureConstant.STRING_Y.equals(specialInvoiceReversalEntity.getUseOldInvoiceData())) {
            if (StringUtils.isNotBlank(specialExcelImport.getSpMc()) || StringUtils.isNotBlank(specialExcelImport.getXmje()) || StringUtils.isNotBlank(specialExcelImport.getXmSe())) {
                throw new OrderReceiveException(OrderInfoContentEnum.SPECIAL_INVOICE_IMPORT_VALIDATE_ERROR5);
            }
        }
        SpecialInvoiceReversalItem specialInvoiceReversalItemEntity = new SpecialInvoiceReversalItem();
        specialInvoiceReversalItemEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
        specialInvoiceReversalItemEntity.setSpecialInvoiceReversalId(specialInvoiceReversalEntity.getId());
        specialInvoiceReversalItemEntity.setSpbm(specialExcelImport.getSpBm());
        specialInvoiceReversalItemEntity.setXmmc(specialExcelImport.getSpMc());
        specialInvoiceReversalItemEntity.setGgxh(specialExcelImport.getGgXh());
        specialInvoiceReversalItemEntity.setXmdw(specialExcelImport.getXmDw());
        specialInvoiceReversalItemEntity.setXmsl(specialExcelImport.getXmSl());
        specialInvoiceReversalItemEntity.setXmdj(specialExcelImport.getXmDj());
        specialInvoiceReversalItemEntity.setXmje(xmje);
        specialInvoiceReversalItemEntity.setSl(StringUtil.formatSl(specialExcelImport.getSLv()));
        specialInvoiceReversalItemEntity.setSe(xmse);
        specialInvoiceReversalItemEntity.setHsbz(specialExcelImport.getHsbz());
        specialInvoiceReversalItemEntity.setSphxh(String.valueOf(specialInvoiceReversalItemEntityList.size() + 1));
        specialInvoiceReversalItemEntity.setCreateTime(DateUtil.parseDateTime(DateUtil.formatDateTime(new Date())));

        String yhzcbs = specialExcelImport.getYhzcbs();
        if (StringUtils.isNotBlank(yhzcbs) && ConfigureConstant.STRING_SHI.equals(yhzcbs)) {
            yhzcbs = YHZCBS_1.getKey();
        } else {
            yhzcbs = YHZCBS_0.getKey();
        }
        specialInvoiceReversalItemEntity.setYhzcbs(yhzcbs);
    
        String zzstsgl = specialExcelImport.getZzstsgl();
        if (LSLBS_1.getValue().equals(zzstsgl)) {
            zzstsgl = LSLBS_1.getKey();
        } else if (LSLBS_2.getValue().equals(zzstsgl)) {
            zzstsgl = LSLBS_2.getKey();
        }
        specialInvoiceReversalItemEntity.setZzstsgl(zzstsgl);

        specialInvoiceReversalItemEntityList.add(specialInvoiceReversalItemEntity);
    
        specialInvoiceReversalEntity.setHjbhsje(new BigDecimal(specialInvoiceReversalEntity.getHjbhsje()).add(new BigDecimal(xmje))
                .setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
        specialInvoiceReversalEntity.setHjse(new BigDecimal(specialInvoiceReversalEntity.getHjse())
                .add(new BigDecimal(xmse)).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
        specialInvoiceReversalEntity.setKphjje(new BigDecimal(specialInvoiceReversalEntity.getHjse())
                .add(new BigDecimal(specialInvoiceReversalEntity.getHjbhsje())).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString());
        return commonSpecialInvoice;
    }
    
    
}
