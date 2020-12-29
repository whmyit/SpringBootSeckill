package com.dxhy.order.consumer.openapi.api;

import cn.hutool.core.date.DateUtil;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.consumer.openapi.service.FangGeInterfaceService;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.model.protocol.ResponseData;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.protocol.fangge.*;
import com.dxhy.order.protocol.invoice.RED_INVOICE_FORM_RSP;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Description 开票
 * @Author xueanna
 * @Date 2019/6/25 10:46
 */
@RestController
@RequestMapping(value = "/invoice/fangge/")
@Api(value = "/invoice/fangge/")
@Slf4j
public class FangGeInvoicesController {
    private static final String LOGGER_INVOICES = "（方格对外接口）";
    @Autowired
    private FangGeInterfaceService fangGeInterfaceService;
    @Resource
    private ICommonInterfaceService commonInterfaceService;
    @Reference
    private ICommonDisposeService commonDisposeService;

    @ApiOperation(value = "方格对外接口", notes = "方格对外接口", httpMethod = "POST", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @RequestMapping(path = "/{interfaceVersion}/{interfaceName}", method = RequestMethod.POST)
    public Result invoice(HttpServletRequest request, HttpServletResponse response,
                          @PathVariable("interfaceVersion") String interfaceVersion,
                          @PathVariable("interfaceName") String interfaceName,
                          @ApiParam(name = "timeStamp", value = "当前时间戳", required = true) @RequestParam(value = "Timestamp", required = true) String timeStamp,
                          @ApiParam(name = "nonce", value = "随机正整数", required = true) @RequestParam(value = "Nonce", required = true) String nonce,
                          @ApiParam(name = "secretId", value = "标识用户身份的SecretId", required = true) @RequestParam(value = "SecretId", required = true) String secretId,
                          @ApiParam(name = "signature", value = "请求签名", required = true) @RequestParam(value = "Signature", required = true) String signature,
                          @ApiParam(name = "encryptCode", value = "加密标识 0:base64加密,1:3DES加密", required = true) @RequestParam(required = true) String encryptCode,
                          @ApiParam(name = "zipCode", value = "压缩标识 0:不压缩,1:压缩", required = true) @RequestParam(required = true) String zipCode,
                          @ApiParam(name = "content", value = "业务请求参数", required = true) @RequestParam(required = true) String content) {
        log.info("{}请求通知", LOGGER_INVOICES);
        Result result = new Result();
        ResponseStatus responseStatus = new ResponseStatus();
        ResponseData responseData = new ResponseData();
        try {


            request.setCharacterEncoding("UTF-8");

            response.setCharacterEncoding("UTF-8");


            /**
             * 校验参数是否为空
             */
            result = commonInterfaceService.checkInterfaceParam(interfaceVersion, interfaceName, timeStamp, nonce, secretId, signature, encryptCode, zipCode, content);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},数据格式校验未通过.", LOGGER_INVOICES);
                return result;
            }

            /* *//**
             * 鉴权  TODO  测试先去掉鉴权
             */
            final DateTime begin1 = now();

            result = commonInterfaceService.auth(timeStamp, nonce, secretId, signature, encryptCode, zipCode, content);
            final long millSeconds1 = new Duration(begin1, now()).getMillis();

            log.debug("{}鉴权耗时{}毫秒", LOGGER_INVOICES, millSeconds1);

            responseStatus = (ResponseStatus) result.get(ConfigurerInfo.RESPONSESTATUS);

            if (!ConfigurerInfo.SUCCSSCODE.equals(responseStatus.getCode())) {
                log.error("{},鉴权未通过", LOGGER_INVOICES);
                return result;
            }
            //解密
            CommonRequestParam commonRequestParam = new CommonRequestParam();
            commonRequestParam.setZipCode(zipCode);
            commonRequestParam.setEncryptCode(encryptCode);
            commonRequestParam.setContent(content);
            commonRequestParam.setSecretId(secretId);

            String commonDecrypt2 = commonDisposeService.commonDecrypt(commonRequestParam);
            log.debug("{}解密日志：{}", LOGGER_INVOICES, commonDecrypt2);
            FG_ORDER_RESPONSE invoices = new FG_ORDER_RESPONSE();
            String returnJsonString = "";
            //根据接口名称判断是什么接口
            if (ConfigurerInfo.FANG_GE_GETINVOICES.equals(interfaceName)) {
                log.info("开票获取数据接口，参数：{}", commonDecrypt2);
                /**
                 * 获取待开具数据
                 */
                FG_GET_INVOICE_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_GET_INVOICE_REQ.class);
                log.info("测试开票时间-方格查询：{},订单请求流水号：{}", DateUtil.now(), paramContent.getDDQQLSH());
                invoices = fangGeInterfaceService.getInvoices(paramContent);
                List<FG_COMMON_ORDER> l = (List<FG_COMMON_ORDER>) invoices.getData();
                returnJsonString = JsonUtils.getInstance().toJsonString(l);
            } else if (ConfigurerInfo.FANG_GE_GETINVOICESTATUS.equals(interfaceName)) {
                log.info("开票更新数据状态接口，参数：{}", commonDecrypt2);
                /**
                 * 接收待开订单数据状态
                 */
                List<FG_COMMON_ORDER_STATUS> paramContent = (List<FG_COMMON_ORDER_STATUS>) JsonUtils.getInstance().jsonToList(commonDecrypt2, FG_COMMON_ORDER_STATUS.class);
                log.info("测试开票时间-方格通知接收成功：{},订单请求流水号：{}", DateUtil.now(), paramContent.get(0).getDDQQLSH());
                invoices = fangGeInterfaceService.getInvoiceStatus(paramContent);

            } else if (ConfigurerInfo.FANG_GE_UPDATEINVOICES.equals(interfaceName)) {
                log.info("开票完成接口，参数：{}", commonDecrypt2);
                /**
                 * 接收开票完成数据
                 */
                List<FG_COMMON_INVOICE_INFO> paramContent = (List<FG_COMMON_INVOICE_INFO>) JsonUtils.getInstance().jsonToList(commonDecrypt2, FG_COMMON_INVOICE_INFO.class);
                log.info("测试开票时间-方格开票结果返回：{},订单请求流水号：{}", DateUtil.now(), paramContent.get(0).getDDQQLSH());
                invoices = fangGeInterfaceService.updateInvoices(paramContent);

            } else if (ConfigurerInfo.FANG_GE_GETUPLOADREDINVOICE.equals(interfaceName)) {
                log.info("红字信息表上传接口，参数：{}", commonDecrypt2);
                /**
                 * 获取红字申请单待上传数据接口
                 */
                FG_GET_INVOICE_UPLOAD_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_GET_INVOICE_UPLOAD_REQ.class);
                invoices = fangGeInterfaceService.getUploadRedInvoice(paramContent);
                returnJsonString = JsonUtils.getInstance().toJsonString(invoices.getData());

            } else if (ConfigurerInfo.FANG_GE_GETUPLOADREDINVOICESTATUS.equals(interfaceName)) {
                log.info("红字信息表上传更新状态接口，参数：{}", commonDecrypt2);
                /**
                 * 接收红字申请单待上传数据状态接口
                 */
                FG_COMMON_RED_INVOICE_UPLOAD_STATUS paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_COMMON_RED_INVOICE_UPLOAD_STATUS.class);
                invoices = fangGeInterfaceService.getUploadRedInvoiceStatus(paramContent);
            } else if (ConfigurerInfo.FANG_GE_UPDATEUPLOADREDINVOICE.equals(interfaceName)) {
                /**
                 * 接收红字申请单上传数据接口
                 */
                RED_INVOICE_FORM_RSP paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, RED_INVOICE_FORM_RSP.class);
                invoices = fangGeInterfaceService.updateUploadRedInvoice(paramContent);
            } else if (ConfigurerInfo.FANG_GE_GETDOWNLOADREDINVOICE.equals(interfaceName)) {
                log.info("红字信息表下载获取数据接口，参数：{}", commonDecrypt2);
                /**
                 * 获取红字申请单待下载数据接口
                 */
                FG_GET_INVOICE_DOWNLOAD_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_GET_INVOICE_DOWNLOAD_REQ.class);
                invoices = fangGeInterfaceService.getDownloadRedInvoice(paramContent);
                returnJsonString = JsonUtils.getInstance().toJsonString(invoices.getData());

            } else if (ConfigurerInfo.FANG_GE_GETDOWNLOADREDINVOICESTATUS.equals(interfaceName)) {
                log.info("红字信息表下载更新状态接口，参数：{}", commonDecrypt2);
                /**
                 * 接收红字申请单待下载数据状态接口
                 */
                FG_RED_INVOICE_DOWNLOAD_STATUS_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_RED_INVOICE_DOWNLOAD_STATUS_REQ.class);
                invoices = fangGeInterfaceService.updateDownloadRedInvoiceStatus(paramContent);
            } else if (ConfigurerInfo.FANG_GE_UPDATEDOWNLOADREDINVOICE.equals(interfaceName)) {
                log.info("红字信息表下载完成接口，参数：{}", commonDecrypt2);
                /**
                 * 接收红字申请单下载数据接口
                 */
                FG_RED_INVOICE_DOWNLOAD_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_RED_INVOICE_DOWNLOAD_REQ.class);
                invoices = fangGeInterfaceService.updateDownloadRedInvoice(paramContent, "system", "system");
            } else if (ConfigurerInfo.FANG_GE_GETDEPRECATEINVOICES.equals(interfaceName)) {
                log.info("作废获取数据接口，参数：{}", commonDecrypt2);
                /**
                 * 获取待作废数据接口
                 */
                FG_GET_INVOICE_ZF_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_GET_INVOICE_ZF_REQ.class);
                invoices = fangGeInterfaceService.getDeprecateInvoices(paramContent);
                returnJsonString = JsonUtils.getInstance().toJsonString(invoices.getData());

            } else if (ConfigurerInfo.FANG_GE_GETDEPRECATEINVOICESSTATUS.equals(interfaceName)) {
                log.info("作废修改数据状态接口，参数：{}", commonDecrypt2);
                /**
                 * 接收作废数据状态接口
                 */
                FG_GET_INVOICE_INVALID_STATUS_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_GET_INVOICE_INVALID_STATUS_REQ.class);
                invoices = fangGeInterfaceService.getDeprecateInvoicesStatus(paramContent);
            } else if (ConfigurerInfo.FANG_GE_UPDATEDEPRECATEINVOICES.equals(interfaceName)) {
                log.info("作废完成接口，参数：{}", commonDecrypt2);
                /**
                 * 接收作废结果数据接口
                 */
                FG_INVALID_INVOICE_FINISH_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_INVALID_INVOICE_FINISH_REQ.class);
                invoices = fangGeInterfaceService.updateDeprecateInvoices(paramContent);
            } else if (ConfigurerInfo.FANG_GE_GETPRINTINVOICES.equals(interfaceName)) {
                log.info("打印获取数据接口，参数：{}", commonDecrypt2);
                /**
                 * 获取待打印数据接口
                 */
                FG_INVOICE_PRING_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_INVOICE_PRING_REQ.class);
                invoices = fangGeInterfaceService.getPrintInvoices(paramContent);
                returnJsonString = JsonUtils.getInstance().toJsonString(invoices.getData());

            } else if (ConfigurerInfo.FANG_GE_GETPRINTINVOICESSTATUS.equals(interfaceName)) {
                log.info("打印修改数据状态接口，参数：{}", commonDecrypt2);
                /**
                 * 接收待打印数据状态接口
                 */
                FG_INVOICE_PRING_STATUS_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_INVOICE_PRING_STATUS_REQ.class);
                invoices = fangGeInterfaceService.getPrintInvoicesStatus(paramContent);
            } else if (ConfigurerInfo.FANG_GE_UPDATEPRINTINVOICES.equals(interfaceName)) {
                log.info("打印完成接口，参数：{}", commonDecrypt2);
                /**
                 * 接收打印完成数据接口
                 */
                FG_INVOICE_PRING_FINISH_REQ paramContent = JsonUtils.getInstance().parseObject(commonDecrypt2, FG_INVOICE_PRING_FINISH_REQ.class);
                invoices = fangGeInterfaceService.updatePrintInvoices(paramContent);
            } else if (ConfigurerInfo.FANG_GE_UPDATETAXDISKINFO.equals(interfaceName)) {
                log.info("税盘数据同步接口，参数：{}", commonDecrypt2);
                /**
                 * 税盘数据同步
                 */
                invoices = fangGeInterfaceService.updateTaxDiskInfo(commonDecrypt2);
            } else if (ConfigurerInfo.FANG_GE_REGISTTAXDISK.equals(interfaceName)) {
                log.info("税盘注册接口，参数：{}", commonDecrypt2);
                /**
                 * 税盘注册
                 */
                invoices = fangGeInterfaceService.registTaxDisk(commonDecrypt2);
                returnJsonString = JsonUtils.getInstance().toJsonString(invoices.getData());
            }
            String data = null;

            log.debug("{},接口返回数据:{}", LOGGER_INVOICES, returnJsonString);

            if (!StringUtils.isBlank(returnJsonString)) {
                commonRequestParam.setContent(returnJsonString);
                /**
                 * 加密
                 */
                data = commonDisposeService.commonEncrypt(commonRequestParam);
                log.debug("{},加密后返回数据:{}", LOGGER_INVOICES, data);
            }

            responseStatus.setCode(invoices.getSTATUS_CODE());
            responseStatus.setMessage(invoices.getSTATUS_MESSAGE());
            result.put(ConfigurerInfo.RESPONSESTATUS, responseStatus);
            if (data != null) {
                responseData.setContent(data);
                responseData.setEncryptCode(commonRequestParam.getEncryptCode());
                responseData.setZipCode(commonRequestParam.getZipCode());
                result.put(ConfigurerInfo.RESPONSEDATA, responseData);
            }
            log.info("返回报文{}", JsonUtils.getInstance().toJsonString(result));
            return result;

        } catch (Exception e) {
            log.error("{}异常:{}", LOGGER_INVOICES, e);
            return Result.error(new ResponseStatus(OrderInfoContentEnum.INVOICE_STAT_ERROR.getKey()
                    , OrderInfoContentEnum.INVOICE_STAT_ERROR.getMessage()));
        }

    }

    private DateTime now() {
        return new DateTime();
    }


}
