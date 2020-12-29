package com.dxhy.order.consumer.openapi.api;

import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.consumer.constant.RespStatusEnum;
import com.dxhy.order.consumer.openapi.service.*;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.model.protocol.ResponseData;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWNLOAD_REQ;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_DOWNLOAD_RSP;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_REQ;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_RSP;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXCX_RSP;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_RSP;
import com.dxhy.order.protocol.v4.commodity.SPXXCX_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXCX_RSP;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_REQ;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_RSP;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_REQ;
import com.dxhy.order.protocol.v4.fpyl.FPYLCX_RSP;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_RSP;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_RSP;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.joda.time.DateTime.now;


/**
 * 订单对外接口入口
 *
 * @author ZSC-DXHY
 */
@RestController
@Api(value = "订单开票接口", tags = {"订单接口模块"})
@Slf4j
public class InvoiceRestApi {
    
    private static final String LOGGER_MSG = "(对外接口V3/V4)";
    
    @Resource
    private IInterfaceService interfaceService;
    
    @Resource
    private IInterfaceServiceV3 interfaceServiceV3;
    
    @Resource
    private IAllocateInvoiceInterfaceServiceV3 allocateInvoiceInterfaceServiceV3;
    
    @Resource
    private IDynamicCodeInterfaceServiceV3 dynamicCodeInterfaceServiceV3;
    
    @Reference
    private ICommonDisposeService commonDisposeService;
    
    @Resource
    private ICommonInterfaceService commonInterfaceService;
    
    
    /**
     * 统一对外接口版本
     *
     * @param request
     * @param response
     * @param interfaceVersion
     * @param interfaceName
     * @param timestamp
     * @param nonce
     * @param secretId
     * @param signature
     * @param encryptCode
     * @param zipCode
     * @param content
     * @return
     */
    @ApiOperation(value = "订单对外接口", notes = "订单对外接口入口", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequestMapping(path = "/invoice/api/{interfaceVersion}/{interfaceName}", method = {RequestMethod.POST, RequestMethod.GET})
    public String orderApiV3(HttpServletRequest request, HttpServletResponse response,
                             @PathVariable("interfaceVersion") String interfaceVersion,
                             @PathVariable("interfaceName") String interfaceName,
                             @ApiParam(name = "timestamp", value = "当前时间戳", required = true) @RequestParam(value = "Timestamp", required = true) String timestamp,
                             @ApiParam(name = "nonce", value = "随机正整数", required = true) @RequestParam(value = "Nonce", required = true) String nonce,
                             @ApiParam(name = "secretId", value = "标识用户身份的SecretId", required = true) @RequestParam(value = "SecretId", required = true) String secretId,
                             @ApiParam(name = "signature", value = "请求签名", required = true) @RequestParam(value = "Signature", required = true) String signature,
                             @ApiParam(name = "encryptCode", value = "加密标识 0:不加密,1:加密", required = true) @RequestParam(required = true) String encryptCode,
                             @ApiParam(name = "zipCode", value = "压缩标识 0:不压缩,1:压缩", required = true) @RequestParam(required = true) String zipCode,
                             @ApiParam(name = "content", value = "业务请求参数", required = true) @RequestParam(required = true) String content) {
        
        
        Result result = new Result();
        ResponseStatus responseStatus = new ResponseStatus();
        ResponseData responseData = new ResponseData();
        try {
    
            request.setCharacterEncoding(StandardCharsets.UTF_8.name());
    
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            /**
             * 校验接口入参是否为空
             */
            result = commonInterfaceService.checkInterfaceParam(interfaceVersion, interfaceName, timestamp, nonce, secretId, signature, encryptCode, zipCode, content);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

//            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
//                log.error("{},数据格式校验未通过.", LOGGER_MSG);
//                return JsonUtils.getInstance().toJsonString(result);
//            }

            final DateTime begin1 = now();
            /**
             * 鉴权
             */
//            result = commonInterfaceService.auth(timestamp, nonce, secretId, signature, encryptCode, zipCode, content);

            final long millSeconds1 = new Duration(begin1, now()).getMillis();
            log.debug("{}鉴权耗时{}毫秒", LOGGER_MSG, millSeconds1);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

//            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
//                log.error("{},鉴权未通过", LOGGER_MSG);
//                return JsonUtils.getInstance().toJsonString(result);
//            }
            //解密
            CommonRequestParam commonRequestParam = new CommonRequestParam();
            commonRequestParam.setZipCode(zipCode);
            commonRequestParam.setEncryptCode(encryptCode);
            commonRequestParam.setContent(content);
            commonRequestParam.setSecretId(secretId);

            String commonDecrypt2 = commonDisposeService.commonDecrypt(commonRequestParam);
            log.debug("{}解密日志：{}", LOGGER_MSG, commonDecrypt2);
            String returnJsonString = "";
            //调用业务处理逻辑方法
            if (ConfigurerInfo.INTERFACE_VERSION_V4.equals(interfaceVersion)) {
                returnJsonString = orderApiV4HandingBusiness(interfaceName, commonDecrypt2, secretId,"0");
            } else {
                returnJsonString = orderApiV3HandingBusiness(interfaceName, commonDecrypt2, secretId,"0");
            }
    
    
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
                return JsonUtils.getInstance().toJsonString(result);
            }
            
        } catch (Exception e) {
            log.error("{},接口请求数据出现异常,异常原因为:{}", LOGGER_MSG, e);
        }
        responseStatus.setCode(RespStatusEnum.FAIL.getCode());
        responseStatus.setMessage(RespStatusEnum.FAIL.getDescribe());
        result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
        log.info("{},接口:{}调用失败,返回数据:{}", LOGGER_MSG, interfaceName, JsonUtils.getInstance().toJsonString(result));
        return JsonUtils.getInstance().toJsonString(result);
    }

    /**
     * 销项管理v3版本业务处理公共方法
     *
     * @param interfaceName  接口方法
     * @param commonDecrypt2 请求数据明文
     * @param secretId       用户身份id
     * @return String 处理逻辑明文
     */
    public String orderApiV3HandingBusiness(String interfaceName, String commonDecrypt2, String secretId,String protocol_type) {
        //返回参数
        String returnJsonString = "";
        
        if (ConfigurerInfo.ALLOCATEINVOICES.equals(interfaceName)) {
            /**
             * 自动开票
             */
            COMMON_ORDER_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, COMMON_ORDER_REQ.class);
            
            DDPCXX_REQ ddpcxxReq = InterfaceBeanTransUtils.transDdpcxxReq(parseObject);
            
            DDPCXX_RSP ddpcxxRsp = allocateInvoiceInterfaceServiceV3.allocateInvoicesV3(ddpcxxReq, secretId, parseObject.getCOMMON_ORDER_BATCH().getKPJH(),protocol_type);
            
            COMMON_ORDER_RSP commonOrderRsp = InterfaceBeanTransUtils.transDdpcxxRsp(ddpcxxRsp);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(commonOrderRsp);
            
        } else if (ConfigurerInfo.GETALLOCATEDINVOICES.equals(interfaceName)) {
            /**
             * 开具发票结果获取
             */
            
            GET_INVOICE_REQ getInvoiceReq = JsonUtils.getInstance().parseObject(commonDecrypt2, GET_INVOICE_REQ.class);
            
            DDKJXX_REQ ddkjxxReq = InterfaceBeanTransUtils.transDdkjxxReq(getInvoiceReq);
            
            DDKJXX_RSP ddkjxxRsp = interfaceServiceV3.getAllocatedInvoicesV3(ddkjxxReq);
            
            GET_INVOICE_RSP getInvoiceRsp = InterfaceBeanTransUtils.transDdkjxxRsp(ddkjxxRsp);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(getInvoiceRsp);
            
        } else if (ConfigurerInfo.DEPRECATEINVOICES.equals(interfaceName)) {
            /**
             * 发票作废
             */
            
            INVALID_INVOICE_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, INVALID_INVOICE_REQ.class);
            
            ZFXX_REQ zfxxReq = InterfaceBeanTransUtils.transZfxxReq(parseObject);
            
            
            ZFXX_RSP zfxxRsp = interfaceService.invoiceInvalid(zfxxReq);
            
            com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP invoiceInvalid = BeanTransitionUtils.transDeprecateInvoicesRsp(zfxxRsp);
            
            INVALID_INVOICE_RSP invalidInvoiceRsp = BeanTransitionUtils.transitionInvoiceInvalidResponseV3(invoiceInvalid);
            returnJsonString = JsonUtils.getInstance().toJsonString(invalidInvoiceRsp);
            
            
        } else if (ConfigurerInfo.GETORDERINFOANDINVOICEINFO.equals(interfaceName)) {
            
            /**
             * 根据订单号获取订单数据以及发票数据接口
             */
            ORDER_REQUEST orderRequest = JsonUtils.getInstance().parseObject(commonDecrypt2, ORDER_REQUEST.class);
            DDFPCX_REQ ddfpcxReq = InterfaceBeanTransUtils.transDdfpcxReq(orderRequest);
            DDFPCX_RSP ddfpcxRsp = interfaceServiceV3.getOrderInfoAndInvoiceInfoV3(ddfpcxReq);
            
            ORDER_INVOICE_RESPONSE response1 = InterfaceBeanTransUtils.transDdfpcxRsp(ddfpcxRsp);
            returnJsonString = JsonUtils.getInstance().toJsonString(response1);
            
        } else if (ConfigurerInfo.ALLOCATEREDINVOICEAPPLICATION.equals(interfaceName)) {
            /**
             * 红字发票申请单上传
             */
            
            RED_INVOICE_FORM_REQ redInvoiceFormReq = JsonUtils.getInstance().parseObject(commonDecrypt2, RED_INVOICE_FORM_REQ.class);
            
            HZSQDSC_REQ hzsqdscReq = InterfaceBeanTransUtils.transHzsqdscReq(redInvoiceFormReq);
            
            HZSQDSC_RSP hzsqdscRsp = interfaceServiceV3.specialInvoiceRushRedV3(hzsqdscReq, redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getKPJH());
            
            RED_INVOICE_FORM_RSP redInvoiceFormRsp = InterfaceBeanTransUtils.transHzsqdscRsp(hzsqdscRsp);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceFormRsp);
            
            
        } else if (ConfigurerInfo.DOWNLOADREDINVOICEAPPLICATIONRESULT.equals(interfaceName)) {
            /**
             * 红字发票申请单审核结果下载
             */
            RED_INVOICE_FORM_DOWNLOAD_REQ redInvoiceFormDownloadReq = JsonUtils.getInstance().parseObject(commonDecrypt2, RED_INVOICE_FORM_DOWNLOAD_REQ.class);
    
            HZSQDXZ_REQ hzsqdxzReq = InterfaceBeanTransUtils.transHzsqdxzReq(redInvoiceFormDownloadReq);
    
            HZSQDXZ_RSP hzsqdxzRsp = interfaceServiceV3.downSpecialInvoiceV3(hzsqdxzReq, redInvoiceFormDownloadReq.getSLDID(), redInvoiceFormDownloadReq.getKPJH());
    
            RED_INVOICE_FORM_DOWNLOAD_RSP redInvoiceFormDownloadRsp = InterfaceBeanTransUtils.transHzsqdxzRsp(hzsqdxzRsp);
    
            returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceFormDownloadRsp);
    
        } else if (ConfigurerInfo.GENERATDYNAMICCODE.equals(interfaceName)) {
            /**
             * 生成动态码接口
             */
    
            DYNAMIC_COMMON_ORDER commonOrder = JsonUtils.getInstance().parseObject(commonDecrypt2, DYNAMIC_COMMON_ORDER.class);
            DDZXX ddzxx = InterfaceBeanTransUtils.transDdzxxReq(commonOrder);
            EWM_RSP dynamicCode = dynamicCodeInterfaceServiceV3.getDynamicCode(ddzxx);
    
            DYNAMIC_CODE_RSP dynamicResponse = new DYNAMIC_CODE_RSP();
            dynamicResponse.setSTATUS_MESSAGE(dynamicCode.getZTXX());
            dynamicResponse.setDYNAMIC_CODE_URL(dynamicCode.getDTM());
            dynamicResponse.setTQM(dynamicCode.getTQM());
            dynamicResponse.setDISABLED_TIME(dynamicCode.getSXSJ());
            
            dynamicResponse.setSTATUS_CODE(dynamicCode.getZTDM());
            returnJsonString = JsonUtils.getInstance().toJsonString(dynamicResponse);
            
        } else {
            returnJsonString = "";
            
        }
        return returnJsonString;
    }
    
    /**
     * 销项管理v4版本业务处理公共方法
     *
     * @param interfaceName  接口方法
     * @param commonDecrypt2 请求数据明文
     * @param secretId       用户身份id
     * @return String 处理逻辑明文
     */
    public String orderApiV4HandingBusiness(String interfaceName, String commonDecrypt2, String secretId,String protocol_type) {
        //返回参数
        String returnJsonString = "";
        
        if (ConfigurerInfo.ALLOCATEINVOICES.equals(interfaceName)) {
            /**
             * 自动开票
             */
            DDPCXX_REQ ddpcxxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, DDPCXX_REQ.class);
            
            DDPCXX_RSP ddpcxxRsp = allocateInvoiceInterfaceServiceV3.allocateInvoicesV3(ddpcxxReq, secretId, null,protocol_type);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(ddpcxxRsp);
        
        } else if (ConfigurerInfo.GETALLOCATEDINVOICES.equals(interfaceName)) {
            /**
             * 开具发票结果获取
             */
            
            DDKJXX_REQ ddkjxxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, DDKJXX_REQ.class);
            
            DDKJXX_RSP ddkjxxRsp = interfaceServiceV3.getAllocatedInvoicesV3(ddkjxxReq);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(ddkjxxRsp);
        
        } else if (ConfigurerInfo.DEPRECATEINVOICES.equals(interfaceName)) {
            /**
             * 发票作废
             */
            ZFXX_REQ parseObject = JsonUtils.getInstance().parseObject(commonDecrypt2, ZFXX_REQ.class);
        
            ZFXX_RSP invoiceInvalid = interfaceService.invoiceInvalid(parseObject);
        
            returnJsonString = JsonUtils.getInstance().toJsonString(invoiceInvalid);
        
        
        } else if (ConfigurerInfo.GETORDERINFOANDINVOICEINFO.equals(interfaceName)) {
            
            /**
             * 根据订单号获取订单数据以及发票数据接口
             */
            DDFPCX_REQ ddfpcxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, DDFPCX_REQ.class);
            DDFPCX_RSP ddfpcxRsp = interfaceServiceV3.getOrderInfoAndInvoiceInfoV3(ddfpcxReq);
            returnJsonString = JsonUtils.getInstance().toJsonString(ddfpcxRsp);
            
        } else if (ConfigurerInfo.ALLOCATEREDINVOICEAPPLICATION.equals(interfaceName)) {
            /**
             * 红字发票申请单上传
             */
            
            HZSQDSC_REQ hzsqdscReq = JsonUtils.getInstance().parseObject(commonDecrypt2, HZSQDSC_REQ.class);
            
            HZSQDSC_RSP hzsqdscRsp = interfaceServiceV3.specialInvoiceRushRedV3(hzsqdscReq, ConfigureConstant.STRING_0);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(hzsqdscRsp);
            
            
        } else if (ConfigurerInfo.DOWNLOADREDINVOICEAPPLICATIONRESULT.equals(interfaceName)) {
            /**
             * 红字发票申请单审核结果下载
             */
            HZSQDXZ_REQ redInvoiceFormDownloadReq = JsonUtils.getInstance().parseObject(commonDecrypt2, HZSQDXZ_REQ.class);
        
            HZSQDXZ_RSP redInvoiceFormDownloadRsp = interfaceServiceV3.downSpecialInvoiceV3(redInvoiceFormDownloadReq, null, null);
        
            returnJsonString = JsonUtils.getInstance().toJsonString(redInvoiceFormDownloadRsp);
        
        } else if (ConfigurerInfo.GENERATDYNAMICCODE.equals(interfaceName)) {
            /**
             * 生成动态码接口
             */
            DDZXX ddzxx = JsonUtils.getInstance().parseObject(commonDecrypt2, DDZXX.class);
            EWM_RSP dynamicCode = dynamicCodeInterfaceServiceV3.getDynamicCode(ddzxx);
            
            returnJsonString = JsonUtils.getInstance().toJsonString(dynamicCode);
            
        } else if (ConfigurerInfo.IMPORTINVOICEINFO.equals(interfaceName)) {
            /**
             * 已开发票历史数据导入接口
             */
            DDFPDR_REQ ddfpdrReq = JsonUtils.getInstance().parseObject(commonDecrypt2, DDFPDR_REQ.class);
            List<DDFPZXX> ddfpzxxList = ddfpdrReq.getDDFPZXX();
            List<DDFPDR_RSP> ddfpdrRspList = interfaceServiceV3.importIssuedInvoice(ddfpzxxList);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(ddfpdrRspList);
        } else if (ConfigurerInfo.QUERYCOMMODITYINFO.equals(interfaceName)) {
            /**
             * 商品信息查询接口
             */
            SPXXCX_REQ spxxcxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, SPXXCX_REQ.class);
            SPXXCX_RSP spxxcxRsp = interfaceServiceV3.queryCommodityMessage(spxxcxReq);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(spxxcxRsp);
        } else if (ConfigurerInfo.SYNCCOMMODITYINFO.equals(interfaceName)) {
            /**
             * 商品信息同步接口
             */
            List<SPXXTB_REQ> spxxtbReqList = JSONObject.parseArray(commonDecrypt2, SPXXTB_REQ.class);
            List<SPXXTB_RSP> spxxtbRspList = interfaceServiceV3.syncCommodityMessage(spxxtbReqList);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(spxxtbRspList);
        } else if (ConfigurerInfo.QUERYBUYERINFO.equals(interfaceName)) {
            /**
             * 购买方信息查询接口
             */
            GMFXXCX_REQ gmfxxcxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, GMFXXCX_REQ.class);
            GMFXXCX_RSP gmfxxcxRsp = interfaceServiceV3.queryBuyerMessage(gmfxxcxReq);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(gmfxxcxRsp);
        } else if (ConfigurerInfo.SYNCBUYERINFO.equals(interfaceName)) {
            /**
             * 购买方信息同步接口
             */
            List<GMFXXTB_REQ> gmfxxtbReqList = JSONObject.parseArray(commonDecrypt2, GMFXXTB_REQ.class);
            List<GMFXXTB_RSP> gmfxxtbRspList = interfaceServiceV3.syncBuyerMessage(gmfxxtbReqList);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(gmfxxtbRspList);
        } else if (ConfigurerInfo.SYNCTAXEQUIPMENTINFO.equals(interfaceName)) {
            /**
             * 税控设备信息同步接口
             */
            List<SKSBXXTB_REQ> sksbxxtbReqList = JSONObject.parseArray(commonDecrypt2, SKSBXXTB_REQ.class);
            List<SKSBXXTB_RSP> sksbxxtbRspList = interfaceServiceV3.syncTaxEquipmentInfo(sksbxxtbReqList);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(sksbxxtbRspList);
        } else if (ConfigurerInfo.QUERYINVOICESTORE.equals(interfaceName)) {
            /**
             * 发票余量接口
             */
            FPYLCX_REQ fpylcxReq = JsonUtils.getInstance().parseObject(commonDecrypt2, FPYLCX_REQ.class);
            List<FPYLCX_RSP> fpylcxRsps = interfaceServiceV3.queryInvoiceStore(fpylcxReq);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(fpylcxRsps);
        }else if (ConfigurerInfo.ORDERDELETE.equals(interfaceName)) {
            /**
             * 订单删除接口
             */
            DDSC_REQ ddsc_req = JsonUtils.getInstance().parseObject(commonDecrypt2, DDSC_REQ.class);
            DDSC_RSP ddsc_rsp = interfaceServiceV3.orderDelete(ddsc_req);
            returnJsonString = JsonUtils.getInstance().toJsonStringNullToEmpty(ddsc_rsp);
        } else {
            returnJsonString = "";
    
        }
        return returnJsonString;
    }
    

}
