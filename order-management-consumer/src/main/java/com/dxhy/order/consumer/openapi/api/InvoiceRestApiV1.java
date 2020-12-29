package com.dxhy.order.consumer.openapi.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.constant.RespStatusEnum;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.openapi.service.IInterfaceService;
import com.dxhy.order.consumer.protocol.cpy.*;
import com.dxhy.order.consumer.protocol.sld.*;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.hp.HpInvocieRequest;
import com.dxhy.order.model.a9.kp.AllocateInvoicesReq;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.a9.query.GetAllocateInvoicesStatusRsp;
import com.dxhy.order.model.a9.query.GetAllocatedInvoicesRsp;
import com.dxhy.order.model.a9.query.ResponseCommonInvoice;
import com.dxhy.order.model.c48.dy.*;
import com.dxhy.order.model.protocol.ResponseData;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.protocol.RESPONSE;
import com.dxhy.order.protocol.order.INVALID_INVOICE_REQ;
import com.dxhy.order.protocol.order.ORDER_REQUEST;
import com.dxhy.order.protocol.order.ORDER_RESPONSE;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.HttpUtils;
import com.dxhy.order.utils.InterfaceBeanTransUtils;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.joda.time.DateTime.now;


/**
 * 订单对外接口入口
 *
 * @author ZSC-DXHY
 */
@SuppressWarnings("AliDeprecation")
@RestController
@Api(value = "订单开票旧版本接口", tags = {"订单接口模块"})
@Slf4j
public class InvoiceRestApiV1 {

    private static final String LOGGER_MSG = "(对外接口V1)";

    @Resource
    private IInterfaceService interfaceService;

    @Reference
    private ICommonDisposeService commonDisposeService;

    @Resource
    private ICommonInterfaceService commonInterfaceService;

    @Reference
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;


    @ApiOperation(value = "订单对外接口", notes = "订单对外接口入口", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequestMapping(path = "/invoice/{interfaceVersion}/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
    public Result receiveOrder(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("interfaceVersion") String interfaceVersion,
                               @PathVariable("interfaceName") String interfaceName,
                               @ApiParam(name = "timestamp", value = "当前时间戳", required = true) @RequestParam(value = "Timestamp", required = true) String timestamp,
                               @ApiParam(name = "nonce", value = "随机正整数", required = true) @RequestParam(value = "Nonce", required = true) String nonce,
                               @ApiParam(name = "secretId", value = "标识用户身份的SecretId", required = true) @RequestParam(value = "SecretId", required = true) String secretId,
                               @ApiParam(name = "signature", value = "请求签名", required = true) @RequestParam(value = "Signature", required = true) String signature,
                               @ApiParam(name = "encryptCode", value = "加密标识 0:不加密,1:加密", required = true) @RequestParam(required = true) String encryptCode,
                               @ApiParam(name = "zipCode", value = "压缩标识 0:不压缩,1:压缩", required = true) @RequestParam(required = true) String zipCode,
                               @ApiParam(name = "content", value = "业务请求参数", required = true) @RequestParam(required = true) String content) {


        ResponseStatus responseStatus = new ResponseStatus();
        ResponseData responseData = new ResponseData();
        Result result = new Result();
        try {

            request.setCharacterEncoding(StandardCharsets.UTF_8.name());

            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            /**
             * 校验接口入参是否为空
             */
            result = commonInterfaceService.checkInterfaceParam(interfaceVersion, interfaceName, timestamp, nonce, secretId, signature, encryptCode, zipCode, content);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},数据格式校验未通过.", LOGGER_MSG);
                return result;
            }
            CommonRequestParam commonRequestParam = new CommonRequestParam();
            commonRequestParam.setZipCode(zipCode);
            commonRequestParam.setEncryptCode(encryptCode);
            commonRequestParam.setContent(content);
            commonRequestParam.setSecretId(secretId);

            final DateTime begin1 = now();
            /**
             * 鉴权
             */
            result = commonInterfaceService.auth(timestamp, nonce, secretId, signature, encryptCode, zipCode, content);

            final long millSeconds1 = new Duration(begin1, now()).getMillis();
            log.debug("{}鉴权耗时{}毫秒", LOGGER_MSG, millSeconds1);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},鉴权未通过", LOGGER_MSG);
                return result;
            }
            //解密
            String commonDecrypt2 = commonDisposeService.commonDecrypt(commonRequestParam);
            log.debug("{}解密日志：{}", LOGGER_MSG, commonDecrypt2);

            //返回参数
            String returnJsonString = orderapiV2Process(interfaceName, interfaceVersion, commonDecrypt2);


            String data = null;

            log.debug("{},接口返回数据:{}", LOGGER_MSG, returnJsonString);
            if (!StringUtils.isBlank(returnJsonString)) {
                commonRequestParam.setContent(returnJsonString);
                /**
                 * 加密
                 */
                data = commonDisposeService.commonEncrypt(commonRequestParam);
                log.debug("{},加密后返回数据:{}", LOGGER_MSG, data);
            }

            if (data != null) {
                responseStatus.setCode(RespStatusEnum.SUCCESS.getCode());
                responseStatus.setMessage(RespStatusEnum.SUCCESS.getDescribe());
                responseData.setContent(data);
                responseData.setEncryptCode(commonRequestParam.getEncryptCode());
                responseData.setZipCode(commonRequestParam.getZipCode());
                result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
                result.put(ConfigurerInfo.RESPONSEDATA, responseData);
                log.info("{},接口:{}调用成功,返回数据:{}", LOGGER_MSG, interfaceName, JsonUtils.getInstance().toJsonString(result));
                return result;
            }

        } catch (Exception e) {
            log.error("{},接口请求数据出现异常,异常原因为:{}", LOGGER_MSG, e);
        }
        responseStatus.setCode(RespStatusEnum.FAIL.getCode());
        responseStatus.setMessage(RespStatusEnum.FAIL.getDescribe());
        result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        log.info("{},接口:{}调用失败,返回数据:{}", LOGGER_MSG, interfaceName, JsonUtils.getInstance().toJsonString(result));
        return result;
    }

    public String orderapiV2Process(String interfaceName, String interfaceVersion, String commonDecrypt2) {
        String returnJsonString = "";
        if (ConfigurerInfo.ALLOCATEINVOICES.equals(interfaceName)) {
            /**
             * 自动开票
             */
            AllocateInvoicesReq parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, AllocateInvoicesReq.class);

            R allocateInvoices = interfaceService.allocateInvoices(parseObject);
            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {

                Map<String, String> fpkj = new HashMap<>(5);
                fpkj.put("STATUS_CODE", allocateInvoices.get(OrderManagementConstant.CODE).toString());
                fpkj.put("STATUS_MESSAGE", allocateInvoices.get(OrderManagementConstant.MESSAGE).toString());
                returnJsonString = JsonUtils.getInstance().toJsonString(fpkj);
            } else {
                if (OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getKey().equals(allocateInvoices.get(OrderManagementConstant.CODE))) {
                    String fpqqpch = String.valueOf(allocateInvoices.get("fpqqpch"));
                    allocateInvoices = new R();
                    Map<String, String> fpkj = new HashMap<>(5);
                    fpkj.put("STATUS_CODE", OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getKey());
                    fpkj.put("STATUS_MESSAGE", OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getMessage());
                    fpkj.put("FPQQPCH", fpqqpch);
                    allocateInvoices.put(OrderManagementConstant.CODE, 0);
                    allocateInvoices.put(OrderManagementConstant.MESSAGE, "成功");
                    allocateInvoices.put("fpkj", fpkj);
                }
                returnJsonString = JsonUtils.getInstance().toJsonString(allocateInvoices);
            }

        } else if (ConfigurerInfo.GETALLOCATEINVOICESSTATUS.equals(interfaceName)) {
            /**
             * 请求执行状态查询
             */
            GetAllocateInvoicesStatusRsp invoiceStatus = interfaceService.invoiceStatus(commonDecrypt2);

            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {
                returnJsonString = JsonUtils.getInstance().toJsonString(invoiceStatus);

            } else {
                Map<String, Object> fpkj = new HashMap<>(5);
                fpkj.put("STATUSCODE", invoiceStatus.getSTATUS_CODE());
                fpkj.put("STATUSMESSAGE", invoiceStatus.getSTATUS_MESSAGE());
                fpkj.put("FPQQPCH", invoiceStatus.getFPQQPCH());
                fpkj.put("INVOICES_FAILED", invoiceStatus.getINVOICES_FAILED());
                Map<String, String> result = new HashMap<>(5);
                result.put("code", "0");
                result.put("msg", invoiceStatus.getSTATUS_MESSAGE());
                result.put("fpkjzt", JsonUtils.getInstance().toJsonString(fpkj));
                returnJsonString = JsonUtils.getInstance().toJsonString(result);

            }

        } else if (ConfigurerInfo.GETALLOCATEDINVOICES.equals(interfaceName)) {
            /**
             * 开具发票结果获取
             */
            GetAllocatedInvoicesRsp allocatedInvoices = interfaceService.getAllocatedInvoices(commonDecrypt2);

            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {

                Map<String, Object> fpkj = new HashMap<>(5);
                fpkj.put("STATUS_CODE", allocatedInvoices.getStatusCode());
                fpkj.put("STATUS_MESSAGE", allocatedInvoices.getStatusMessage());
                fpkj.put("FPQQPCH", allocatedInvoices.getFPQQPCH());
                fpkj.put("RESPONSE_COMMON_INVOICE", allocatedInvoices.getRESPONSE_COMMON_INVOICE());
                returnJsonString = JsonUtils.getInstance().toJsonString(fpkj);

            } else {

                Map<String, Object> fpkj = new HashMap<>(5);
                fpkj.put("STATUSCODE", allocatedInvoices.getStatusCode());
                fpkj.put("STATUSMESSAGE", allocatedInvoices.getStatusMessage());
                fpkj.put("FPQQPCH", allocatedInvoices.getFPQQPCH());
                fpkj.put("RESPONSE_COMMON_INVOICE", allocatedInvoices.getRESPONSE_COMMON_INVOICE());
                Map<String, String> result = new HashMap<>(5);
                result.put("code", "0");
                result.put("msg", allocatedInvoices.getStatusMessage());
                result.put("fpkjResult", JsonUtils.getInstance().toJsonString(fpkj));
                returnJsonString = JsonUtils.getInstance().toJsonString(result);

            }

        } else if (ConfigurerInfo.DEPRECATEINVOICES.equals(interfaceName)) {
            /**
             * 发票作废
             */
            INVALID_INVOICE_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, INVALID_INVOICE_REQ.class);

            ZFXX_REQ zfxxReq = InterfaceBeanTransUtils.transZfxxReq(parseObject);


            ZFXX_RSP zfxxRsp = interfaceService.invoiceInvalid(zfxxReq);

            com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP invoiceInvalid = BeanTransitionUtils.transDeprecateInvoicesRsp(zfxxRsp);
            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {
                com.dxhy.order.consumer.protocol.invoice.DEPRECATE_INVOICES_RSP deprecateInvoicesRsp = BeanTransitionUtils.transitionDeprecateInvoicesRsp(invoiceInvalid);
                returnJsonString = JsonUtils.getInstance().toJsonString(deprecateInvoicesRsp);
            } else {
                returnJsonString = JsonUtils.getInstance().toJsonString(invoiceInvalid);

            }

        } else if (ConfigurerInfo.GETINVOICEPDFFILES.equals(interfaceName)) {
            /**
             * 获取电子发票
             */
            GetPdfRequest pdfRequestBean = JsonUtils.getInstance().parseObject(commonDecrypt2, GetPdfRequest.class);
            GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());

            returnJsonString = JsonUtils.getInstance().toJsonString(pdf);


        } else if (ConfigurerInfo.PRINTINVOICES.equals(interfaceName)) {
            /**
             * 发票自定义打印
             */
            PrintInvoicesReqVt printInvoicesReqVt = JsonUtils.getInstance().parseObject(commonDecrypt2, PrintInvoicesReqVt.class);
            Map<String, String> requestHead = HttpInvoiceRequestUtil.getRequestHead(OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());

            /**
             * 请求流水号换成底层流水号进行处理
             */
            if (printInvoicesReqVt != null && printInvoicesReqVt.getL() != null && printInvoicesReqVt.getL().size() > 0) {
                for (int i = 0; i < printInvoicesReqVt.getL().size(); i++) {
                    PrintReq printReq = printInvoicesReqVt.getL().get(i);
                    OrderInvoiceInfo orderInvoiceInfo = apiOrderInvoiceInfoService.selectOrderInvoiceInfoByFpqqlsh(printReq.getFPQQLSH(), null);
                    printInvoicesReqVt.getL().get(i).setFPQQLSH(orderInvoiceInfo.getKplsh());
                }
            }
            log.debug("{}发票打印的接口，url:{},入参:{}", LOGGER_MSG, OpenApiConfig.printInvoice, JsonUtils.getInstance().toJsonString(printInvoicesReqVt));
            String printResult = HttpUtils.doPostWithHeader(OpenApiConfig.printInvoice, JsonUtils.getInstance().toJsonStringNullToEmpty(printInvoicesReqVt), requestHead);
            log.debug("{}发票打印的接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(printResult));
            JSONObject parseObject1 = JSONObject.parseObject(printResult);
            PrintInvoicesRes printInvoices = JsonUtils.getInstance().parseObject(parseObject1.getString("result"), PrintInvoicesRes.class);
            returnJsonString = JsonUtils.getInstance().toJsonString(printInvoices);

        } else if (ConfigurerInfo.GETPRINTINVOICESSTATUS.equals(interfaceName)) {
            /**
             * 发票打印状态
             */
            PrintQueryReq parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, PrintQueryReq.class);
            Map<String, String> requestHead = HttpInvoiceRequestUtil.getRequestHead(OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());

            log.debug("{}发票打印的接口，url:{},入参:{}", LOGGER_MSG, OpenApiConfig.getPrintInvoiceStatus, JsonUtils.getInstance().toJsonString(parseObject));
            String printResult = HttpUtils.doPostWithHeader(OpenApiConfig.getPrintInvoiceStatus, JsonUtils.getInstance().toJsonStringNullToEmpty(parseObject), requestHead);
            log.debug("{}发票打印的接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(printResult));
            JSONObject parseObject1 = JSONObject.parseObject(printResult);
            PrintQueryRes printQueryRes = JsonUtils.getInstance().parseObject(parseObject1.getString("result"), PrintQueryRes.class);
            returnJsonString = JsonUtils.getInstance().toJsonString(printQueryRes);

        } else if (ConfigurerInfo.IMPORTORDERS.equals(interfaceName)) {
            /**
             * 企业数据自动导入
             */
            com.dxhy.order.consumer.protocol.order.ALLOCATE_INVOICES_REQ allocateInvoicesReq = JsonUtils.getInstance().parseObject(commonDecrypt2, com.dxhy.order.consumer.protocol.order.ALLOCATE_INVOICES_REQ.class);
            com.dxhy.order.consumer.protocol.order.COMMON_INVOICE[] commonInvoice = allocateInvoicesReq.getCOMMON_INVOICE();

            RESPONSE importOrders = interfaceService.importOrders(asList(commonInvoice));
            returnJsonString = JsonUtils.getInstance().toJsonString(importOrders);

        } else if (ConfigurerInfo.QUERYINVOICEROLLPLOLIST.equals(interfaceName)) {
            /**
             * 受理点上下票列表管理接口
             */
            SLD_INVOICEROLLPLO_REQUEST sldInvoicerollploRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, SLD_INVOICEROLLPLO_REQUEST.class);
            SLDKCMX_RESPONSE printInvoices = interfaceService.queryinvoicerollplolist(sldInvoicerollploRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(printInvoices);

        } else if (ConfigurerInfo.ACCESSPOINTUPINVOICE.equals(interfaceName)) {

            /**
             * 受理点上票接口
             */
            SLDUP_REQUEST slduprequest = JsonUtils.getInstance().parseObject(commonDecrypt2, SLDUP_REQUEST.class);
            RESPONSE response1 = interfaceService.accessPointUpInvoice(slduprequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(response1);

        } else if (ConfigurerInfo.ACCESSPOINTDOWNINVOICE.equals(interfaceName)) {

            /**
             * 受理点下票接口
             */
            SLDDOWN_REQUEST slddownRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, SLDDOWN_REQUEST.class);
            RESPONSE response1 = interfaceService.accessPointDownInvoice(slddownRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(response1);

        } else if (ConfigurerInfo.QUERYSLD.equals(interfaceName)) {

            /**
             * 受理点列表查询接口
             */
            SLD_SEARCH_REQUEST sldSearchRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, SLD_SEARCH_REQUEST.class);
            RESPONSE response1 = interfaceService.querySld(sldSearchRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(response1);

        } else if (ConfigurerInfo.GETORDERINFOANDINVOICEINFO.equals(interfaceName)) {

            /**
             * 根据订单号获取订单数据以及发票数据接口
             */
            ORDER_REQUEST orderRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, ORDER_REQUEST.class);
            ORDER_RESPONSE response1 = interfaceService.getOrderInfoAndInvoiceInfo(orderRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(response1);

        } else if (ConfigurerInfo.ALLOCATEREDINVOICEAPPLICATION.equals(interfaceName)) {
            /**
             * 红字发票申请单上传
             */
            R specialInvoiceRushRed = interfaceService.specialInvoiceRushRed(commonDecrypt2);

            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {

                com.dxhy.order.consumer.protocol.order.HZFPSQBSCS_RSP hzfpsqbscsRsp = new com.dxhy.order.consumer.protocol.order.HZFPSQBSCS_RSP();
                if (specialInvoiceRushRed.get(OrderManagementConstant.DATA) != null) {
                    hzfpsqbscsRsp = (com.dxhy.order.consumer.protocol.order.HZFPSQBSCS_RSP) specialInvoiceRushRed.get(OrderManagementConstant.DATA);
                    returnJsonString = JsonUtils.getInstance().toJsonString(hzfpsqbscsRsp);
                } else {
                    hzfpsqbscsRsp.setSTATUS_CODE(specialInvoiceRushRed.get(OrderManagementConstant.CODE).toString());
                    hzfpsqbscsRsp.setSTATUS_MESSAGE(specialInvoiceRushRed.get(OrderManagementConstant.MESSAGE).toString());
                    returnJsonString = JsonUtils.getInstance().toJsonString(hzfpsqbscsRsp);
                }

            } else {
                returnJsonString = JsonUtils.getInstance().toJsonString(specialInvoiceRushRed);

            }

        } else if (ConfigurerInfo.DOWNLOADREDINVOICEAPPLICATIONRESULT.equals(interfaceName)) {
            /**
             * 红字发票申请单审核结果下载
             */
            com.dxhy.order.consumer.protocol.order.HZFPSQBXZ_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, com.dxhy.order.consumer.protocol.order.HZFPSQBXZ_REQ.class);

            HpInvocieRequest hpInvocieRequest = new HpInvocieRequest();
            BeanUtils.copyProperties(parseObject, hpInvocieRequest);
            R downSpecialInvoice = interfaceService.downSpecialInvoice(hpInvocieRequest);

            /**
             * v2接口进行返回参数重新封装,
             */
            if (ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion)) {

                com.dxhy.order.consumer.protocol.order.GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP redInvoiceDown = new com.dxhy.order.consumer.protocol.order.GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP();
                if (downSpecialInvoice.get(OrderManagementConstant.DATA) != null) {
                    redInvoiceDown = (com.dxhy.order.consumer.protocol.order.GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP) downSpecialInvoice.get(OrderManagementConstant.DATA);
                    returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceDown);
                } else {
                    redInvoiceDown.setSTATUS_CODE(downSpecialInvoice.get(OrderManagementConstant.CODE).toString());
                    redInvoiceDown.setSTATUS_MESSAGE(downSpecialInvoice.get(OrderManagementConstant.MESSAGE).toString());
                    returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceDown);
                }

            } else {
                returnJsonString = JsonUtils.getInstance().toJsonString(downSpecialInvoice);

            }

        } else if (ConfigurerInfo.QUERYCPYJDKC.equals(interfaceName)) {
            /**
             * 成品油库存局端可下载库存查询
             */
            CPY_JDKC_REQUEST cpyJdkcRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, CPY_JDKC_REQUEST.class);
            CPY_JDKC_RESPONSE cpyJdkcResponse = interfaceService.queryCpyJdKc(cpyJdkcRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(cpyJdkcResponse);

        } else if (ConfigurerInfo.QUERYCPYYXZKC.equals(interfaceName)) {
            /**
             * 成品油已下载库存查询
             */
            CPY_YXZKC_REQUEST cpyYxzkcRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, CPY_YXZKC_REQUEST.class);
            CPY_YXZKC_RESPONSE cpyYxzkcResponse = interfaceService.queryCpyYxzKc(cpyYxzkcRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(cpyYxzkcResponse);

        } else if (ConfigurerInfo.DOWNLOADCPYKC.equals(interfaceName)) {
            /**
             * 成品油库存下载
             */
            DOWNLOAD_CPYKC_REQUEST downloadCpykcRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, DOWNLOAD_CPYKC_REQUEST.class);
            DOWNLOAD_CPYKC_RESPONSE downloadCpykcResponse = interfaceService.downloadCpyKc(downloadCpykcRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(downloadCpykcResponse);

        } else if (ConfigurerInfo.BACKCPYKC.equals(interfaceName)) {
            /**
             * 成品油库存退回
             */
            BACK_CPYKC_REQUEST backCpykcRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, BACK_CPYKC_REQUEST.class);
            BACK_CPYKC_RESPONSE backCpykcResponse = interfaceService.backCpyKc(backCpykcRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(backCpykcResponse);

        } else if (ConfigurerInfo.SYNCCPYKC.equals(interfaceName)) {
            /**
             * 成品油库存同步
             */
            SYNC_CPYKC_REQUEST syncCpykcRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, SYNC_CPYKC_REQUEST.class);
            SYNC_CPYKC_RESPONSE syncCpykcResponse = interfaceService.syncCpyKc(syncCpykcRequest);
            returnJsonString = JsonUtils.getInstance().toJsonString(syncCpykcResponse);
        } else {

            returnJsonString = "";
        }
        return returnJsonString;
    }

    public static void main(String[] args) {
        String str = "{\n" +
                "  \"STATUSMESSAGE\": \"发票全部开具成功\",\n" +
                "  \"STATUSCODE\": \"020000\",\n" +
                "  \"FPQQPCH\": \"6041ac45-3651-4d59-8662-8e9c848917a6\",\n" +
                "}";

        String b = "[{\"FPQQLSH\":\"6041ac45-3651-4d59-8662-8e9c848917a6001\",\"FP_DM\":\"1100194130\",\"FP_HM\":\"37948174\",\"FWM\":\"2/57-96352><<1<>1/+6>9+6**14<></+3*5<>*1<8*>8/89813*8->31239>9-89<-4>3*1<91/+6-8>6**/0*/<92544>>81-8<-*><0*5\",\"JQBH\":\"661820793180\",\"JYM\":\"72494590541684343288\",\"KPRQ\":\"2020-05-22 10:40:23\",\"PDF_URL\":\"\"}]";
        GetAllocatedInvoicesRsp allocatedInvoices = JsonUtils.getInstance().parseObject(str, GetAllocatedInvoicesRsp.class);
        List<ResponseCommonInvoice> responseCommonInvoiceList = JSON.parseArray(b, ResponseCommonInvoice.class);
        allocatedInvoices.setRESPONSE_COMMON_INVOICE(responseCommonInvoiceList);
        Map<String, Object> fpkj = new HashMap<>(5);
        fpkj.put("STATUSCODE", allocatedInvoices.getStatusCode());
        fpkj.put("STATUSMESSAGE", allocatedInvoices.getStatusMessage());
        fpkj.put("FPQQPCH", allocatedInvoices.getFPQQPCH());
        fpkj.put("RESPONSE_COMMON_INVOICE", allocatedInvoices.getRESPONSE_COMMON_INVOICE());
        Map<String, String> result = new HashMap<>(5);
        result.put("code", "0");
        result.put("msg", allocatedInvoices.getStatusMessage());
        result.put("fpkjResult", JsonUtils.getInstance().toJsonString(fpkj));
        String returnJsonString = JsonUtils.getInstance().toJsonString(result);
        System.out.println(returnJsonString);
    }

}
