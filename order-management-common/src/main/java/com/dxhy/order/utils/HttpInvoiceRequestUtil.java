package com.dxhy.order.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.ExceptionContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.a9.dy.DyRequest;
import com.dxhy.order.model.a9.dy.DyRequestExtend;
import com.dxhy.order.model.a9.dy.DyResponse;
import com.dxhy.order.model.a9.hp.*;
import com.dxhy.order.model.a9.kp.*;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfRequestExtend;
import com.dxhy.order.model.a9.pdf.GetPdfResponse;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.a9.query.GetAllocateInvoicesStatusRsp;
import com.dxhy.order.model.a9.query.GetAllocatedInvoicesRsp;
import com.dxhy.order.model.a9.sld.*;
import com.dxhy.order.model.a9.zf.*;
import com.dxhy.order.model.bwactivexs.kpd.KpdRequset;
import com.dxhy.order.model.bwactivexs.kpd.KpdResponse;
import com.dxhy.order.model.bwactivexs.kpd.KpdXx;
import com.dxhy.order.model.bwactivexs.sp.SpRequest;
import com.dxhy.order.model.bwactivexs.sp.SpResponse;
import com.dxhy.order.model.bwactivexs.sp.SpXx;
import com.dxhy.order.model.c48.dy.PrintInvoicesReqVt;
import com.dxhy.order.model.c48.dy.PrintReq;
import com.dxhy.order.model.c48.sld.FpKpd;
import com.dxhy.order.model.c48.sld.KpdSpglRequest;
import com.dxhy.order.model.c48.sld.KpdSpglResponse;
import com.dxhy.order.model.c48.sld.SldSearchRequest;
import com.dxhy.order.model.c48.zf.DEPRECATE_FAILED_INVOICE;
import com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP;
import com.dxhy.order.model.newtax.Kpzdxx;
import com.dxhy.order.model.newtax.NsrXnsbxx;
import com.dxhy.order.model.newtax.NsrXnsbxxs;
import com.dxhy.order.model.ofd.OfdToPngRequest;
import com.dxhy.order.model.ofd.OfdToPngResponse;
import com.dxhy.order.model.ukey.QueryJqbhList;
import com.dxhy.order.model.ukey.QueryJqbhResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 以http方式请求底层获取数据工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/9/24 15:35
 */
@Slf4j
public class HttpInvoiceRequestUtil {
    private static final String LOGGER_MSG = "(请求底层接口)";
    
    /**
     * 获取http请求头
     *
     * @param terminalCode
     * @return
     */
    public static Map<String, String> getRequestHead(String terminalCode) {
        
        Map<String, String> header = new HashMap<>(10);
        
        // 设置请求头参数
        if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(terminalCode)) {
            log.debug("百望服务器");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_BWFWQ.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode)) {
            log.debug("税控盘托管");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_BWPZ.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {
            log.debug("金税盘A9托管");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_A9.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            log.debug("active-x");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_ACTIVEX.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
            log.debug("C48,Http方式请求");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_C48.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
            log.debug("税控服务器,Http方式请求");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_NEWTAX.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
            log.debug("税控服务器UKey,Http方式请求");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_UKEY.getKey());
        } else if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            log.debug("方格,Http方式请求");
            header.put("X-Request-Id", OrderInfoEnum.TAX_EQUIPMENT_HEAD_FG.getKey());
        }
        return header;
    }
    
    /**
     * 调用底层获取PDF
     *
     * @param url
     * @param getPdfRequest
     * @param terminalCode
     * @return
     */
    public static GetPdfResponseExtend getPdf(String fgUrl, String url, GetPdfRequest getPdfRequest, String terminalCode) {
    
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(getPdfRequest);
        GetPdfResponseExtend getPdfResponseExtend = new GetPdfResponseExtend();
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)) {
            log.debug("{}方格获取pdf的接口,url:{},入参:{}", LOGGER_MSG, fgUrl, requestParam);
            String result = HttpUtils.doPostWithHeader(fgUrl, requestParam, header);
            log.debug("{}方格获取pdf的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            getPdfResponseExtend = JsonUtils.getInstance().parseObject(result, GetPdfResponseExtend.class);
    
        } else {
            log.debug("{}获取pdf的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}获取pdf的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            GetPdfResponse getPdfResponse = JsonUtils.getInstance().parseObject(result, GetPdfResponse.class);
            if (getPdfResponse != null) {
                if (getPdfResponse.getResult() != null) {
                    getPdfResponseExtend = getPdfResponse.getResult();
                } else {
                    getPdfResponseExtend.setSTATUS_CODE(getPdfResponse.getCode());
                    getPdfResponseExtend.setSTATUS_MESSAGE(getPdfResponse.getMsg());
                }
            }
        }
    
        return getPdfResponseExtend;
    
    
    }
    
    /**
     * 调用底层进行打印
     *
     * @param url
     * @param request
     * @param terminalCode
     * @return
     */
    public static DyResponse batchPrint(String url, DyRequest request, String terminalCode) {
    
        DyResponse response;
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
        if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
            request.setTerminalCode(terminalCode);
            log.debug("{}打印接口,url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(request));
            String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
            log.debug("{}打印接口，出参:{}", LOGGER_MSG, result);
            // 返回参数解析成对象
            response = JsonUtils.getInstance().parseObject(result, DyResponse.class);
        } else {
            PrintInvoicesReqVt printInvoicesReqVt = convertToPrintInvoicesReqVt(request);
            log.debug("{}发票打印的接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(printInvoicesReqVt));
            String printResult = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(printInvoicesReqVt), header);
            log.debug("{}发票打印的接口，出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(printResult));
            JSONObject parseObject = JSONObject.parseObject(printResult);
            response = new DyResponse();
            response.setDypch(parseObject.getJSONObject("result").getString("dypch"));
            response.setCode(parseObject.getJSONObject("result").getString("status_CODE"));
            response.setMsg(parseObject.getJSONObject("result").getString("status_MESSAGE"));
        }
        return response;
    }

    /**
     * 发票作废接口调用
     *
     * @param url
     * @param zfRequest
     * @param terminalCode
     * @return
     */
    public static DEPRECATE_INVOICES_RSP zfInvoice(String url, ZfRequest zfRequest, String terminalCode) {
        Map<String, String> header = getRequestHead(terminalCode);
        // 区分开票终端类型
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(zfRequest);
    
        log.debug("{}作废接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(requestParam));
        String result = HttpUtils.doPostWithHeader(url, requestParam, header);
        log.debug("{}作废接口，出参:{}", LOGGER_MSG, result);
        return convertZfResponseBean(convertStringToZfResponseBean(result));
    
    }
    
    public static KbZfResponseExtend kbZfInvoice(String url, KbZfRequest kbZfRequest, String terminalCode) {
        
        Map<String, String> header = getRequestHead(terminalCode);
        kbZfRequest.setTerminalCode(terminalCode);
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(kbZfRequest);
        
        log.debug("{}空白发票作废的接口，url:{},入参：{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(requestParam));
        String result = HttpUtils.doPostWithHeader(url, requestParam, header);
        log.debug("{}空白发票作废的接口，出参：{}", LOGGER_MSG, result);
        KbZfResponseBean kbZfResponseBean = JsonUtils.getInstance().parseObject(result, KbZfResponseBean.class);
        KbZfResponseExtend kbZfResponseExtend = new KbZfResponseExtend();
        if (kbZfResponseBean != null ) {
            if (kbZfResponseBean.getResult() != null && StringUtils.isNotBlank(kbZfResponseBean.getResult().getStatusCode())) {
                kbZfResponseExtend = kbZfResponseBean.getResult();
            } else {
                kbZfResponseExtend.setStatusCode(kbZfResponseBean.getCode());
                kbZfResponseExtend.setStatusMessage(kbZfResponseBean.getMsg());
            }
        }
        
        return kbZfResponseExtend;
    }
    
    /**
     * 调用底层红字申请单上传接口
     *
     * @param url
     * @param hzfpsqbsReq
     * @param terminalCode
     * @return
     */
    public static HpUploadResponse redInvoiceUpload(String url, HzfpsqbsReq hzfpsqbsReq, String terminalCode) {
        HpUploadResponse hpUploadResponse = new HpUploadResponse();
        Map<String, String> header = getRequestHead(terminalCode);
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(hzfpsqbsReq);

        log.debug("{}红字申请单上传的接口，url:{},入参:{}", LOGGER_MSG, url, requestParam);
        String result = HttpUtils.doPostWithHeader(url, requestParam, header);
        log.debug("{}红字申请单上传的接口，出参:{}", LOGGER_MSG, result);
        
        try {
            hpUploadResponse = JsonUtils.getInstance().parseObject(result, HpUploadResponse.class);
            
            
            if (!Constant.SUCCSSCODE.equals(hpUploadResponse.getCode()) || hpUploadResponse.getResult() == null) {
                HpUploadResponseExtend hpUploadResponseExtend = new HpUploadResponseExtend();
                String statusCode = hpUploadResponse.getCode();
                String statusMsg = hpUploadResponse.getMsg();
                if (ObjectUtil.isNotEmpty(hpUploadResponse.getResult()) && ObjectUtil.isNotEmpty(hpUploadResponse.getResult().getResponse_HZFPSQBSC()) && hpUploadResponse.getResult().getResponse_HZFPSQBSC().size() > 0 && StringUtils.isNotBlank(hpUploadResponse.getResult().getResponse_HZFPSQBSC().get(0).getSTATUS_CODE())) {
                    statusCode = hpUploadResponse.getResult().getResponse_HZFPSQBSC().get(0).getSTATUS_CODE();
                    statusMsg = hpUploadResponse.getResult().getResponse_HZFPSQBSC().get(0).getSTATUS_MESSAGE();
                }
                hpUploadResponseExtend.setStatusCode(statusCode);
                hpUploadResponseExtend.setStatusMessage(statusMsg);
                hpUploadResponseExtend.setSqbscqqpch(hzfpsqbsReq.getHZFPSQBSCSBATCH().getSQBSCQQPCH());
                hpUploadResponse.setResult(hpUploadResponseExtend);
            }
        } catch (Exception e) {
            HpUploadResponseExtend hpUploadResponseExtend = new HpUploadResponseExtend();
            hpUploadResponseExtend.setStatusCode(ExceptionContentEnum.HZSQD_UPLOAD_ERROR_999999.getKey());
            hpUploadResponseExtend.setStatusMessage(ExceptionContentEnum.HZSQD_UPLOAD_ERROR_999999.getMessage());
            hpUploadResponseExtend.setSqbscqqpch(hzfpsqbsReq.getHZFPSQBSCSBATCH().getSQBSCQQPCH());
            hpUploadResponse.setResult(hpUploadResponseExtend);
        }
        
        
        return hpUploadResponse;
    }
    
    public static HpResponseBean redInvoiceDown(String url, HpInvocieRequest reqData, String terminalCode) {
        HpResponseBean hpResponseBean = new HpResponseBean();
        Map<String, String> header = getRequestHead(terminalCode);
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(reqData);
        
        log.debug("{}红字申请单下载接口，url:{},入参:{}", LOGGER_MSG, url, requestParam);
        String result = HttpUtils.doPostWithHeader(url, requestParam, header);
        log.debug("{}红字申请单下载接口，出参:{}", LOGGER_MSG, result);
        // 返回参数解析成对象
    
        try {
            hpResponseBean = JsonUtils.getInstance().parseObject(result, HpResponseBean.class);
            
            if (!Constant.SUCCSSCODE.equals(hpResponseBean.getCode()) || hpResponseBean.getResult() == null) {
                HpResponseExtend hpResponseExtend = new HpResponseExtend();
                hpResponseExtend.setStatusCode(hpResponseBean.getCode());
                hpResponseExtend.setStatusMessage(hpResponseBean.getMsg());
                hpResponseExtend.setSqbscqqpch(reqData.getSQBXZQQPCH());
                hpResponseBean.setResult(hpResponseExtend);
            }
        } catch (Exception e) {
            HpResponseExtend hpResponseExtend = new HpResponseExtend();
            hpResponseExtend.setStatusCode(ExceptionContentEnum.HZSQD_UPLOAD_ERROR_999999.getKey());
            hpResponseExtend.setStatusMessage(ExceptionContentEnum.HZSQD_UPLOAD_ERROR_999999.getMessage());
            hpResponseBean.setResult(hpResponseExtend);
        }
        return hpResponseBean;
    }
    
    /**
     * 单张获取发票版式文件请求数据组装
     *
     * @param fpqqpch
     * @param nsrsbh
     * @param terminalCode
     * @param fpdm
     * @param fphm
     * @param pdfUrl
     * @return
     */
    public static GetPdfRequest getPdfRequestBean(String fpqqpch, String nsrsbh, String terminalCode, String fpdm, String fphm, String pdfUrl) {
        GetPdfRequest getPdfRequest = new GetPdfRequest();
        getPdfRequest.setFPQQPCH(fpqqpch);
        getPdfRequest.setNSRSBH(nsrsbh);
        getPdfRequest.setTerminalCode(terminalCode);
        List<GetPdfRequestExtend> getPdfRequestExtendList = new ArrayList<>();
        GetPdfRequestExtend getPdfRequestExtend = new GetPdfRequestExtend();
        getPdfRequestExtend.setID(pdfUrl);
        getPdfRequestExtend.setFP_DM(fpdm);
        getPdfRequestExtend.setFP_HM(fphm);
        getPdfRequestExtendList.add(getPdfRequestExtend);
        getPdfRequest.setREQUEST_EINVOICE_PDF(getPdfRequestExtendList.toArray(new GetPdfRequestExtend[0]));
        return getPdfRequest;
        
    }
    
    
    /**
     * 打印对接C48接口bean转换
     */
    private static PrintInvoicesReqVt convertToPrintInvoicesReqVt(DyRequest request) {
        PrintInvoicesReqVt printInvoicesReqVt = new PrintInvoicesReqVt();
        //A9和百望的字段名称为发票标识
        printInvoicesReqVt.setDYLX(request.getFpbs());
        printInvoicesReqVt.setDYPCH(request.getDypch());
        printInvoicesReqVt.setDYDBS(request.getSpotKey());
        
        if (CollectionUtils.isNotEmpty(request.getInvoicePrintPackageDetailList())) {
            List<PrintReq> list = new ArrayList<>();
            for (DyRequestExtend dyRequestExtend : request.getInvoicePrintPackageDetailList()) {
                PrintReq req = new PrintReq();
                req.setFPQQLSH(dyRequestExtend.getFpqqlsh());
                list.add(req);
            }
            printInvoicesReqVt.setL(list);
        }
        return printInvoicesReqVt;
    }
    
    /**
     * bean转换
     */
    private static ZfResponseBean convertStringToZfResponseBean(String response) {
        ZfResponseBean zpResponse = new ZfResponseBean();
        JSONObject parseObject = JSONObject.parseObject(response);
        zpResponse.setCode(parseObject.getString("code"));
        zpResponse.setMsg(parseObject.getString("msg"));
        ZfResponseExtend result;
        if (parseObject.getString("result") != null) {
            result = JSON.parseObject(parseObject.getString("result"), ZfResponseExtend.class);
            zpResponse.setResult(result);
        }
        return zpResponse;
        
    }
    
    /**
     * 协议bean转换 TODO 协议bean转换不完整
     */
    private static DEPRECATE_INVOICES_RSP convertZfResponseBean(ZfResponseBean zfInvoice) {
        
        DEPRECATE_INVOICES_RSP response = new DEPRECATE_INVOICES_RSP();
        
        if (zfInvoice.getResult() != null) {
            response.setSTATUS_CODE(StringUtils.isBlank(zfInvoice.getResult().getStatusCode()) ? zfInvoice.getCode() :
                    zfInvoice.getResult().getStatusCode());
            response.setSTATUS_MESSAGE(StringUtils.isBlank(zfInvoice.getResult().getStatusMessage()) ? zfInvoice.getMsg() :
                    zfInvoice.getResult().getStatusMessage());
            response.setZFPCH(zfInvoice.getResult().getZfpch());
            
            if (zfInvoice.getResult().getDeprecate_failed_invoice() != null) {
                List<DEPRECATE_FAILED_INVOICE> parseArray = JSON.parseArray(zfInvoice.getResult().getDeprecate_failed_invoice(), DEPRECATE_FAILED_INVOICE.class);
                
                DEPRECATE_FAILED_INVOICE[] deprecate_failed_invoice = new DEPRECATE_FAILED_INVOICE[parseArray.size()];
                int i = 0;
                for(DEPRECATE_FAILED_INVOICE deprecate : parseArray){
                	deprecate_failed_invoice[i] = deprecate;
                	i++;
                }
                response.setDeprecate_failed_invoice(deprecate_failed_invoice);
            }
        } else {
            response.setSTATUS_CODE(zfInvoice.getCode());
            response.setSTATUS_MESSAGE(zfInvoice.getMsg());
        }
    
    
        return response;
    }
    
    /**
     * 受理点获取发票份数接口
     *
     * @param url
     * @param kccxRequest
     * @param terminalCode
     * @return
     */
    public static SldKcResponse querySldFpfs(String url, SldKcRequest kccxRequest, String terminalCode) {
        
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
        // 设置请求头参数
        log.debug("受理点发票份数信息查询，url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(kccxRequest));
        String result = HttpUtils.doPostWithHeader(url,
                JsonUtils.getInstance().toJsonStringNullToEmpty(kccxRequest), header);
        log.debug("受理点发票份数信息查询，url:{},出参:{}", url, result);
        SldKcResponse response = new SldKcResponse();
        
        if (StringUtils.isBlank(result)) {
            return response;
        }
        response = JsonUtils.getInstance().parseObject(result, SldKcResponse.class);
        return response;
    }
    
    /**
     * 查询受理点,使用getSldList接口,
     *
     * @param url   A9和C48url
     */
    @Deprecated
    public static SearchSldResponse querySld(String url, SldRequest sldRequest, String terminalCode) {
        SearchSldResponse response = new SearchSldResponse();
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
        
        if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
            if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                KpdRequset kpdRequset = new KpdRequset();
                List<String> shList = new ArrayList<>();
                shList.add(sldRequest.getNsrsbh());
                kpdRequset.setNsrsbhs(shList);
                kpdRequset.setTerminalCode(terminalCode);
                kpdRequset.setSkpId(sldRequest.getFjh());
                
                log.debug("获取开票点信息接口，url:{} 入参:{}", url, JsonUtils.getInstance().toJsonString(sldRequest));
                String result = HttpUtils.doPostWithHeader(url,
                        JsonUtils.getInstance().toJsonStringNullToEmpty(kpdRequset), header);
                log.debug("获取开票点信息接口，出参:{}", result);
                // 返回参数解析成对象
                KpdResponse queryKpdXxList = JsonUtils.getInstance().parseObject(result, KpdResponse.class);
                response = convertToSldSearchResponse(queryKpdXxList, terminalCode);
            } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                /**
                 * 新税控虚拟设备信息查询
                 */
                String fpzldm = CommonUtils.transFplxdm(sldRequest.getFplxDm());
                sldRequest.setFplxDm(fpzldm);
                NsrXnsbxxs nsrXnsbxxs = queryNsrXnsbxx(url, sldRequest, terminalCode);
                if (ObjectUtil.isNotEmpty(nsrXnsbxxs)) {
        
                    response = convertToNewTaxSld(nsrXnsbxxs, sldRequest.getNsrsbh(), terminalCode);
                }
    
            } else {
                //除active-x c48以外的接口全部走A9的接口
                sldRequest.setQyzt("1");
                sldRequest.setTerminalCode(terminalCode);
                sldRequest.setNsrsbh(sldRequest.getNsrsbh());
                log.debug("查询受理点的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest));
                String result = HttpUtils.doPostWithHeader(url,
                        JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest), header);
                log.debug("查询受理点的接口,出参:{}", result);
                // 返回参数解析成对象
                SldResponseBean querySlds = JsonUtils.getInstance().parseObject(result, SldResponseBean.class);
                response = convertSldResponseBean(querySlds, sldRequest.getNsrsbh(), terminalCode);
            }
        } else {
            SldSearchRequest request = new SldSearchRequest();
            request.setCpyzt(sldRequest.getCpybs());
            request.setFpzlDm(sldRequest.getFpzldm());
            request.setNsrsbh(sldRequest.getNsrsbh());
            log.debug("C48接口，查询受理点的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(request));
            String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
            log.debug("C48接口，查询受理点的接口,出参:{}", result);
            SldResponseBean querySlds = JsonUtils.getInstance().parseObject(result, SldResponseBean.class);
            response = convertSldResponseBean(querySlds, sldRequest.getNsrsbh(), terminalCode);
        }
        return response;
    }
    
    public static void getSldList(Set<SearchSld> searchSlds, String querySldListUrl, String fpzldm, String cpybs, String nsrsbh, String fjh, String sldId, String terminalCode) {
        List<String> fpzls = new ArrayList<>();
    
        /**
         * 发票种类为空时,为获取全部数据,使用多个种类进行轮训获取
         * todo 后期需要添加多种票种
         */
        if (StringUtils.isNotBlank(fpzldm)) {
            fpzls.add(fpzldm);
        } else {
            fpzls.add(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
            fpzls.add(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
            fpzls.add(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        }
        
        for (String fpzl : fpzls) {
    
            //成品油
            SldRequest sldSearchRequest = new SldRequest();
            sldSearchRequest.setNsrsbh(nsrsbh);
            sldSearchRequest.setFpzldm(fpzl);
            sldSearchRequest.setFplxDm(fpzl);
            sldSearchRequest.setCpybs(cpybs);
            if (StringUtils.isNotBlank(fjh)) {
                sldSearchRequest.setFjh(fjh);
            }
            if (StringUtils.isNotBlank(sldId)) {
                sldSearchRequest.setId(sldId);
            }
    
            SearchSldResponse querySld = HttpInvoiceRequestUtil.querySld(querySldListUrl, sldSearchRequest, terminalCode);
    
            if (querySld != null && querySld.getStatusCode().equals(ConfigurerInfo.SUCCSSCODE) && querySld.getSlds() != null && querySld.getSlds().size() > 0) {
                searchSlds.addAll(querySld.getSlds());
            }
    
        }
    }
    
    /**
     * 新税控UKey和百旺服务器查询分机号接口
     *
     * @param url
     * @param sldRequest
     * @param terminalCode
     * @return
     */
    public static QueryJqbhList queryFjh(String url, SldRequest sldRequest, String terminalCode) {
        QueryJqbhList queryJqbhList = new QueryJqbhList();
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
        sldRequest.setTerminalCode(terminalCode);
        sldRequest.setNsrsbh(sldRequest.getNsrsbh());
        log.debug("查询分机号的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest));
        String result = HttpUtils.doPostWithHeader(url,
                JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest), header);
        log.debug("查询分机号的接口,出参:{}", result);
        // 返回参数解析成对象
        QueryJqbhResponse queryJqbhResponse = JsonUtils.getInstance().parseObject(result, QueryJqbhResponse.class);
        if (ObjectUtil.isNotEmpty(queryJqbhResponse) && ConfigurerInfo.SUCCSSCODE.equals(queryJqbhResponse.getCode())) {
            queryJqbhList = queryJqbhResponse.getResult();
        }
        
        return queryJqbhList;
    }
    
    /**
     * bean转换
     */
    private static SearchSldResponse convertSldResponseBean(SldResponseBean querySlds, String nsrsbh, String terminalCode) {
        SearchSldResponse response = new SearchSldResponse();
        
        if (querySlds.getResult() != null) {
            response.setStatusCode(StringUtils.isBlank(querySlds.getResult().getStatusCode()) ? querySlds.getCode() :
                    querySlds.getResult().getStatusCode());
            response.setStatusMessage(StringUtils.isBlank(querySlds.getResult().getStatusMessage()) ? querySlds.getMsg() :
                    querySlds.getResult().getStatusMessage());
            if (querySlds.getResult().getSlds() != null) {
                List<SearchSld> list = new ArrayList<>();
                for (SldXx sldxx : querySlds.getResult().getSlds()) {
                    SearchSld search = new SearchSld();
                    search.setJqbh(sldxx.getJqbh());
                    search.setSldId(sldxx.getSldid());
                    search.setSldMc(sldxx.getSldmc());
                    search.setFjh(sldxx.getFjh());
                    search.setNsrsbh(nsrsbh);
                    search.setTerminalCode(terminalCode);
                    list.add(search);
                }
                response.setSlds(list);
            }
            
        } else {
            response.setStatusCode(querySlds.getCode());
            response.setStatusMessage(querySlds.getMsg());
        }
        
        return response;
    }
    
    /**
     * 百望服务器返回json转换
     */
    private static SearchSldResponse convertToSldSearchResponse(KpdResponse response, String terminalCode) {
        SearchSldResponse sldResponse = new SearchSldResponse();
        sldResponse.setStatusCode(response.getCode());
        sldResponse.setStatusMessage(response.getMsg());
        if (Constant.SUCCSSCODE.equals(response.getCode())) {
            sldResponse.setStatusCode(Constant.SUCCSSCODE);
            if (CollectionUtils.isNotEmpty(response.getContent())) {
                List<SearchSld> slds = new ArrayList<>();
                for (KpdXx kpdXx : response.getContent()) {
                    SearchSld sldSearch = new SearchSld();
                    sldSearch.setSldId(kpdXx.getKpdId());
                    sldSearch.setSldMc(kpdXx.getKpdMc());
                    sldSearch.setJqbh(kpdXx.getZdh());
                    sldSearch.setZdbs(kpdXx.getZdh());
                    sldSearch.setNsrsbh(kpdXx.getNsrsbh());
                    sldSearch.setTerminalCode(terminalCode);
                    slds.add(sldSearch);
                }
                sldResponse.setSlds(slds);
            }
        }
        return sldResponse;
    }
    
    /**
     * 百望服务器返回json转换
     */
    private static SearchSldResponse convertToNewTaxSld(NsrXnsbxxs response, String nsrsbh, String terminalCode) {
        SearchSldResponse sldResponse = new SearchSldResponse();
        sldResponse.setStatusCode(response.getCode());
        sldResponse.setStatusMessage(response.getMsg());
        if (Constant.SUCCSSCODE.equals(response.getCode())) {
            sldResponse.setStatusCode(Constant.SUCCSSCODE);
            if (CollectionUtils.isNotEmpty(response.getContent())) {
                List<SearchSld> slds = new ArrayList<>();
                /**
                 * 处理外层税控设备信息
                 */
                
                for (NsrXnsbxx nsrXnsbxx : response.getContent()) {
                    if (ObjectUtil.isNotEmpty(nsrXnsbxx.getKpzdxxs())) {
                        for (Kpzdxx kpzdxx : nsrXnsbxx.getKpzdxxs()) {
                            SearchSld sldSearch = new SearchSld();
                            sldSearch.setSldId(kpzdxx.getKpdid());
                            sldSearch.setSldMc(kpzdxx.getKpzdmc());
                            sldSearch.setJqbh(nsrXnsbxx.getXnsbh());
                            sldSearch.setFjh(nsrXnsbxx.getXnsbh());
                            sldSearch.setNsrsbh(nsrsbh);
                            sldSearch.setTerminalCode(terminalCode);
                            slds.add(sldSearch);
                        }
                    }
                    
                }
                sldResponse.setSlds(slds);
            }
        }
        return sldResponse;
    }


//    /**
//     * 查询受理点
//     *
//     * @param bw_url  百望服务器url
//     * @param a9_url  A9url
//     * @param c48_url C48url
//     */
//    public static SldglResponse querySldList(String bw_url, String a9_url, String c48_url, SldglRequest request, String terminalCode) {
//
//        SldglResponse sldglResponse;
//        // 设置请求头参数
//        Map<String, String> header = getRequestHead(terminalCode);
//        if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
//            if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
//                KpdRequset kpdRequset = new KpdRequset();
//                List<String> list = new ArrayList<>();
//                list.add(request.getNsrsbhs()[0].getNsrsbh());
//                kpdRequset.setNsrsbhs(list);
//
//                kpdRequset.setTerminalCode(terminalCode);
//                log.debug("获取开票点信息接口，url:{} 入参:{}", bw_url, JsonUtils.getInstance().toJsonString(request));
//                String result = HttpUtils.doPostWithHeader(bw_url,
//                        JsonUtils.getInstance().toJsonStringNullToEmpty(kpdRequset), header);
//                log.debug("获取开票点信息接口，出参:{}", result);
//                // 返回参数解析成对象
//                KpdResponse queryKpdXxList = JsonUtils.getInstance().parseObject(result, KpdResponse.class);
//                sldglResponse = convertToSldglResponse(queryKpdXxList);
//            } else {
//                //除active-x c48以外的接口全部走A9的接口
//                SldRequest sldRequest = new SldRequest();
//                sldRequest.setNsrsbh(request.getNsrsbhs()[0].getNsrsbh());
//                sldRequest.setTerminalCode(terminalCode);
//                log.debug("查询受理点的接口,url:{},入参:{}", a9_url, JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest));
//                String result = HttpUtils.doPostWithHeader(a9_url,
//                        JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest), header);
//                log.debug("查询受理点的接口,出参:{}", result);
//                // 返回参数解析成对象
//                SldResponseBean querySlds = JsonUtils.getInstance().parseObject(result, SldResponseBean.class);
//                sldglResponse = convertToSldResponseBean(querySlds);
//            }
//        } else {
//            log.debug("C48接口，查询受理点的接口,url:{},入参:{}", c48_url, JsonUtils.getInstance().toJsonStringNullToEmpty(request));
//            String result = HttpUtils.doPostWithHeader(c48_url, JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
//            log.debug("C48接口，查询受理点的接口,出参:{}", result);
//            C48SldResponseBean responseBean = JsonUtils.getInstance().parseObject(result, C48SldResponseBean.class);
//            sldglResponse = responseBean.getResult();
//        }
//        return sldglResponse;
//
//    }
//    /**
//     * bean转换
//     */
//    private static SldglResponse convertToSldglResponse(KpdResponse queryKpdXxList) {
//        SldglResponse response = new SldglResponse();
//        response.setStatusCode(queryKpdXxList.getCode());
//        response.setStatusMessage(queryKpdXxList.getMsg());
//        if(Constant.SUCCSSCODE.equals(queryKpdXxList.getCode())){
//            List<Fpsldmx> fpslds = new ArrayList<>();
//            if(CollectionUtils.isNotEmpty(queryKpdXxList.getContent())){
//                for(KpdXx kpdXx : queryKpdXxList.getContent()){
//                    Fpsldmx sldmx = new Fpsldmx();
//                    sldmx.setSldid(Integer.valueOf(kpdXx.getKpdId()));
//                    sldmx.setSldmc(kpdXx.getKpdMc());
//                    sldmx.setKpdmc(kpdXx.getZdh());
//                    sldmx.setSldzt(kpdXx.getSfqy());
//                    fpslds.add(sldmx);
//                }
//                response.setFpslds(fpslds);
//            }
//
//        }
//        return response;
//    }
    
    /**
     * 协议bean的转换 为null的对象转换为
     */
    private static void beanConvert(AllocateInvoicesReq req_allocate_invoices_req) {
        
        // 判断是否是成品油
        req_allocate_invoices_req.getCOMMON_INVOICES_BATCH().setCPYFP(OrderInfoEnum.QDBZ_CODE_4.getKey()
                .equals(req_allocate_invoices_req.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getQD_BZ()));
    
        for (CommonInvoice commonInvoice : req_allocate_invoices_req.getCOMMON_INVOICE()) {
            
            //百望服务器的清单标志只能为 0 不带清单 1 带清单
            /**
             * 税控盘托管支持农产品开票 ；去除税控盘托管清单标志转换。2020-04-13
             * 0-非收购发票；
             * 1-非收购发票（清单）
             * 2-收购发票；
             * 3-收购发票（清单）
             * 4-成品油发票
             * 6-可抵扣通行费发票
             * 8-不可抵扣通行费发票
             */
            if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(req_allocate_invoices_req.getTerminalCode())
                    || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(req_allocate_invoices_req.getTerminalCode())
                    || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(req_allocate_invoices_req.getTerminalCode())) {
                if (commonInvoice.getCOMMON_INVOICE_DETAIL().length > 8) {
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_1.getKey());
                } else {
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());
                }
            }
        
            int i = 0;
            
            
            for (CommonInvoiceDetail commonInvoiceDetail : commonInvoice.getCOMMON_INVOICE_DETAIL()) {
                /**
                 *  空代表无
                 * 1 出口免税和其他免税优惠政策
                 * 2 不征增值税
                 * 3 普通零税率
                 * 新税控零税率标识字段属性无0，转为1
                 */
                if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(req_allocate_invoices_req.getTerminalCode())) {
                    if (StringUtils.isNotBlank(commonInvoiceDetail.getLSLBS()) && "0".equals(commonInvoiceDetail.getLSLBS())) {
                        commonInvoiceDetail.setLSLBS(OrderInfoEnum.LSLBS_1.getKey());
                        commonInvoiceDetail.setZZSTSGL(OrderInfoEnum.LSLBS_1.getValue());
                    }
                }
                i++;
            }

        
        }
    }
    
    public static ResponseBaseBean invoiceIssuing(String url, String terminalCode, AllocateInvoicesReq requestData) {
        
        Map<String, String> header = getRequestHead(terminalCode);
        if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
            beanConvert(requestData);
            requestData.setTerminalCode(terminalCode);
            
        }
        
        // 设置请求头参数
        log.debug("{}开票接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonStringNullToEmpty(requestData));
        String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(requestData), header);
        log.debug("{}开票接口，出参:{}", LOGGER_MSG, result);
        
        return convertKpResponseBean(result);
    }
    
    /**
     * bean转换方法
     */
    
    private static ResponseBaseBean convertKpResponseBean(String result) {
        ResponseBaseBean response = new ResponseBaseBean();
        JSONObject parseObject = JSON.parseObject(result);
        response.setCode(parseObject.getString("code"));
        response.setMsg(parseObject.getString("msg"));

        return response;
    }
    
    /**
     * 发票状态查询接口
     *
     * @param url
     * @param invoiceQueryRequest
     * @param terminalCode
     * @return
     */
    public static GetAllocatedInvoicesRsp invoiceResult(String url, String invoiceQueryRequest, String terminalCode) {
        GetAllocatedInvoicesRsp getAllocatedInvoicesRsp = new GetAllocatedInvoicesRsp();
        try {
            // 设置请求头参数
            Map<String, String> header = getRequestHead(terminalCode);
        
        
            log.debug("{}获取发票开具结果的接口,url:{},入参:{}", LOGGER_MSG, url, invoiceQueryRequest);
            String result = HttpUtils.doPostWithHeader(url, invoiceQueryRequest, header);
            log.debug("{}获取发票开具结果的接口,出参:{}", LOGGER_MSG, result);

//            //bean转换
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (StringUtils.isNotBlank(jsonObject.getString("result"))) {
                if (StringUtils.isNotBlank(jsonObject.getJSONObject("result").getString("fpkjResult"))) {
                    GetAllocatedInvoicesRsp getallocatedInvoicesRsp = JsonUtils.getInstance().parseObject(jsonObject.getJSONObject("result").getString("fpkjResult"), GetAllocatedInvoicesRsp.class);
                
                    return getallocatedInvoicesRsp;
                }
            
            
            } else {
                getAllocatedInvoicesRsp.setStatusCode(jsonObject.getString("code"));
                getAllocatedInvoicesRsp.setStatusMessage(jsonObject.getString("msg"));
            }
        
        } catch (Exception e) {
            getAllocatedInvoicesRsp.setStatusCode(ExceptionContentEnum.QUERY_INVOICE_ERROR_999999.getKey());
            getAllocatedInvoicesRsp.setStatusMessage(ExceptionContentEnum.QUERY_INVOICE_ERROR_999999.getMessage());
        
        }
        return getAllocatedInvoicesRsp;
    
    }
    
    /**
     * 发票状态查询接口
     *
     * @param url
     * @param invoiceQueryRequest
     * @param terminalCode
     * @return
     */
    public static GetAllocateInvoicesStatusRsp queryInvoiceStatus(String url, String invoiceQueryRequest, String terminalCode) {
        GetAllocateInvoicesStatusRsp getAllocateInvoicesStatusRsp = new GetAllocateInvoicesStatusRsp();
        try {
            // 设置请求头参数
            Map<String, String> header = getRequestHead(terminalCode);
        
        
            log.debug("{}获取发票状态查询的接口,url:{},入参:{}", LOGGER_MSG, url, invoiceQueryRequest);
            String result = HttpUtils.doPostWithHeader(url, invoiceQueryRequest, header);
            log.debug("{}获取发票状态查询的接口,出参:{}", LOGGER_MSG, result);
        
            //bean转换
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (StringUtils.isNotBlank(jsonObject.getString("result"))) {
                if (StringUtils.isNotBlank(jsonObject.getJSONObject("result").getString("fpkjzt"))) {
                    getAllocateInvoicesStatusRsp = JsonUtils.getInstance().parseObject(jsonObject.getJSONObject("result").getString("fpkjzt"), GetAllocateInvoicesStatusRsp.class);
    
                    return getAllocateInvoicesStatusRsp;
                }
    
    
            } else {
                getAllocateInvoicesStatusRsp.setSTATUS_CODE(jsonObject.getString("code"));
                getAllocateInvoicesStatusRsp.setSTATUS_MESSAGE(jsonObject.getString("msg"));
            }

        } catch (Exception e) {
            getAllocateInvoicesStatusRsp.setSTATUS_CODE(ExceptionContentEnum.QUERY_INVOICE_ERROR1_999999.getKey());
            getAllocateInvoicesStatusRsp.setSTATUS_MESSAGE(ExceptionContentEnum.QUERY_INVOICE_ERROR1_999999.getMessage());
        }
        return getAllocateInvoicesStatusRsp;
    
    }
    /**
    * 查询开票限额
    */
    public static JspxxResponse queryNsrpzKpxe(String url,String nsrsbh, String fpzlDm, String terminalCode) {
        JspxxResponse queryJspxxV2 = new JspxxResponse();
        if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
        
        
            return queryJspxxV2;
        }
        // 数据校验
        Map<String, String> header = getRequestHead(terminalCode);
        QueryJspxxV2Request request = new QueryJspxxV2Request();
        request.setFpzlDm(fpzlDm);
        request.setNsrsbh(nsrsbh);
        request.setQysj("");
        request.setTerminalCode(terminalCode);
        // 设置请求头参数
        log.debug("调用接口:{},金税盘信息查询，url:{},入参:{}", header.get("X-Request-Id"), url, JsonUtils.getInstance().toJsonStringNullToEmpty(request));
        String result = HttpUtils.doPostWithHeader(url,
                JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
        log.debug("调用接口:{},金税盘信息查询，url:{},出参:{}", header.get("X-Request-Id"), url, result);
    
        if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
        
            QueryNsrpzKpxeResponse response = JsonUtils.getInstance().parseObject(result, QueryNsrpzKpxeResponse.class);
            queryJspxxV2 = convertToQueryNsrpzKpxeResponse(response);
        } else {
        
            JspxxResponseResult response = JsonUtils.getInstance().parseObject(result, JspxxResponseResult.class);
            queryJspxxV2 = response.getResult();
        }
        return queryJspxxV2;
    }
    /**
     * bean转换
     */
    private static JspxxResponse convertToQueryNsrpzKpxeResponse(QueryNsrpzKpxeResponse queryNsrpzKpxe) {
        JspxxResponse jspResopse = new JspxxResponse();
        if("0000".equals(queryNsrpzKpxe.getCode())){
            List<Jspxxcx> jspxxs = new ArrayList<>();
            if(queryNsrpzKpxe.getResult() != null){

                Jspxxcx jspxxcx =  new Jspxxcx();
                jspResopse.setStatusCode(StringUtils.isBlank(queryNsrpzKpxe.getResult().getStatusCode()) ? "0000" : queryNsrpzKpxe.getResult().getStatusCode());
                jspResopse.setStatusMessage(queryNsrpzKpxe.getResult().getStatusMessage());
                jspxxcx.setDzkpxe(queryNsrpzKpxe.getResult().getKpxe());
                jspxxcx.setFpzlDm(queryNsrpzKpxe.getResult().getFpzlDm());
                jspxxcx.setNsrsbh(queryNsrpzKpxe.getResult().getNsrsbh());
                jspxxs.add(jspxxcx);
    
            }
            jspResopse.setJspxxs(jspxxs);
    
        } else {
            jspResopse.setStatusCode(queryNsrpzKpxe.getCode());
            jspResopse.setStatusMessage(queryNsrpzKpxe.getMsg());
        }
        return jspResopse;
    }
    
    /**
     * 获取受理点下一张发票接口
     *
     * @param url
     * @param request
     * @param terminalCode
     * @return
     */
    public static QueryNextInvoiceResponseExtend queryNextInvoice(String url, QueryNextInvoiceRequest request, String terminalCode) {
        QueryNextInvoiceResponseExtend queryNextInvoiceResponseExtend = new QueryNextInvoiceResponseExtend();
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            queryNextInvoiceResponseExtend.setStatusCode(ConfigurerInfo.SUCCSSCODE);
        } else {
            // 设置请求头参数
            Map<String, String> header = getRequestHead(terminalCode);
            // 设置请求头参数
            log.debug("查询下一张发票的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(request));
            String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(request), header);
            log.debug("查询下一张发票的接口,出参:{}", result);
            // 返回参数解析成对象
            QueryNextInvoiceResponse queryNextInvoiceResponse = JsonUtils.getInstance().parseObject(result, QueryNextInvoiceResponse.class);
            if (ObjectUtil.isNotEmpty(queryNextInvoiceResponse) && ConfigurerInfo.SUCCSSCODE.equals(queryNextInvoiceResponse.getCode()) && ObjectUtil.isNotEmpty(queryNextInvoiceResponse.getResult())) {
                queryNextInvoiceResponseExtend = queryNextInvoiceResponse.getResult();
                queryNextInvoiceResponseExtend.setStatusCode(queryNextInvoiceResponse.getCode());
            }
        }
    
        return queryNextInvoiceResponseExtend;
    }
    
    /**
     * 获取库存信息
     *
     * @param url
     * @param kccxRequest
     * @param terminalCode
     * @return
     */
    public static SldKcByFjhResponseExtend queryKcxxByFjh(String url, SldKcRequest kccxRequest, String terminalCode) {
        SldKcByFjhResponseExtend sldKcByFjhResponseExtend = new SldKcByFjhResponseExtend();
        Map<String, String> header = getRequestHead(terminalCode);
        // 设置请求头参数
        
        if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            sldKcByFjhResponseExtend.setKcmxes(new ArrayList<>());
            return sldKcByFjhResponseExtend;
        }
        log.debug("根据分机号查询库存信息的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(kccxRequest));
        String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(kccxRequest), header);
        log.debug("根据分机号查询库存信息的接口,出参:{}", result);
        
        if (StringUtils.isBlank(result)) {
            sldKcByFjhResponseExtend.setKcmxes(new ArrayList<>());
            return sldKcByFjhResponseExtend;
        }
        SldKcByFjhResponse response = JsonUtils.getInstance().parseObject(result, SldKcByFjhResponse.class);
        sldKcByFjhResponseExtend = response.getResult();
        return sldKcByFjhResponseExtend;
    }
    
    /**
     * 获取新税控虚拟设备信息
     *
     * @param url
     * @param sldRequest
     * @param terminalCode
     * @return
     */
    public static NsrXnsbxxs queryNsrXnsbxx(String url, SldRequest sldRequest, String terminalCode) {
        Map<String, String> header = getRequestHead(terminalCode);
        
        // 设置请求头参数
        
        log.debug("根据税号查询虚拟设备信息的接口,url:{},入参:{}", url, JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest));
        String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(sldRequest), header);
        log.debug("根据税号查询虚拟设备信息的接口,出参:{}", result);
        
        return JsonUtils.getInstance().parseObject(result, NsrXnsbxxs.class);
    }
    
    /**
     * 根据税号获取分机
     *
     * @param url
     * @param kpdSpglRequest
     * @param terminalCode
     * @return
     */
    public static KpdSpglResponse getFjh(String url, KpdSpglRequest kpdSpglRequest, String terminalCode) {
    
        KpdSpglResponse response = new KpdSpglResponse();
        // 设置请求头参数
        Map<String, String> header = getRequestHead(terminalCode);
    
        String requestParam;
        SearchFjhRequest request = new SearchFjhRequest();
        request.setNsrsbh(kpdSpglRequest.getNsrsbhs()[0].getNsrsbh());
        request.setFjh(kpdSpglRequest.getFjh());
        request.setTerminalCode(terminalCode);
        if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
        
            requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(kpdSpglRequest);
        
            log.debug("{}获取分机号的接口,url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(requestParam));
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}获取分机号的接口,出参:{}", LOGGER_MSG, result);
        
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (StringUtils.isNotBlank(jsonObject.getString("result"))) {
            
                response = JsonUtils.getInstance().parseObject(jsonObject.getString("result"), KpdSpglResponse.class);
            
            }
        
        
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            SpRequest spRequst = new SpRequest();
            List<String> nsrsbh = new ArrayList<>();
            nsrsbh.add(request.getNsrsbh());
            spRequst.setNsrsbhs(nsrsbh);
            spRequst.setTerminalCode(terminalCode);
            log.debug("获取分机号的接口，url:{} 入参:{}", url, JsonUtils.getInstance().toJsonString(spRequst));
            String result = HttpUtils.doPostWithHeader(url,
                    JsonUtils.getInstance().toJsonStringNullToEmpty(spRequst), header);
            log.debug("获取分机号的接口，出参:{}", result);
            // 返回参数解析成对象
            SpResponse querySpXxList = JsonUtils.getInstance().parseObject(result, SpResponse.class);
            response = convertToResponse(convetToSearchFjhResponse(querySpXxList));
        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
        
            /**
             * todo 暂时调用新税控库存信息获取分机号
             */
            SldKcRequest kccxRequest = new SldKcRequest();
            kccxRequest.setSldid("");
            kccxRequest.setFpzldm("");
            kccxRequest.setNsrsbh(request.getNsrsbh());
            kccxRequest.setFjh(request.getFjh());
            kccxRequest.setJqbh("");
            kccxRequest.setTerminalCode(terminalCode);
        
            log.debug("获取分机号的接口，url:{} 入参:{}", url, JsonUtils.getInstance().toJsonString(kccxRequest));
            String result = HttpUtils.doPostWithHeader(url,
                    JsonUtils.getInstance().toJsonStringNullToEmpty(kccxRequest), header);
            log.debug("获取分机号的接口，出参:{}", result);
            // 返回参数解析成对象
            SldKcByFjhResponse sldKcByFjhResponse = JsonUtils.getInstance().parseObject(result, SldKcByFjhResponse.class);
            response.setStatusCode(sldKcByFjhResponse.getCode());
            response.setStatusMessage(sldKcByFjhResponse.getMsg());
            if (sldKcByFjhResponse.getResult() != null && sldKcByFjhResponse.getResult().getKcmxes() != null && sldKcByFjhResponse.getResult().getKcmxes().size() > 0) {
                List<FpKpd> fpkpds = new ArrayList<>();
                for (SldKcmxByFjh search : sldKcByFjhResponse.getResult().getKcmxes()) {
                    FpKpd fpKpd = new FpKpd();
                    fpKpd.setKpdid(Integer.valueOf(search.getFjh()));
                    fpKpd.setKpdmc(search.getFjh());
                    fpKpd.setFjh(search.getFjh());
                    fpKpd.setNsrsbh(search.getNsrsbh());
                    fpkpds.add(fpKpd);
                }
                response.setFpkpds(fpkpds);
            }
        } else {
        
            requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(request);
            log.debug("{}获取分机号的接口,url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonString(requestParam));
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}获取分机号的接口,出参:{}", LOGGER_MSG, result);
        
            JSONObject json = JSON.parseObject(result);
            if ("0000".equals(json.getString("code")) && json.getString("result") != null) {
                SearchFjhResponse searchFjhFpzlDm = JsonUtils.getInstance().parseObject(json.getString("result"), SearchFjhResponse.class);
                response = convertToResponse(searchFjhFpzlDm);
            }
        }
    
    
        return response;
    }
    
    /**
     * bean转换
     */
    private static SearchFjhResponse convetToSearchFjhResponse(SpResponse querySpXxList) {
        SearchFjhResponse response = new SearchFjhResponse();
        response.setStatusCode(querySpXxList.getCode());
        response.setStatusMessage(querySpXxList.getMsg());
        if ("0000".equals(querySpXxList.getCode())) {
            if (CollectionUtils.isNotEmpty(querySpXxList.getContent())) {
                List<SearchFjh> searchFjhs = new ArrayList<>();
                for (SpXx xx : querySpXxList.getContent()) {
                    SearchFjh fjh = new SearchFjh();
                    fjh.setKpdId(xx.getSkpId());
                    fjh.setKpdMc(xx.getSkpName());
                    searchFjhs.add(fjh);
                }
                response.setSearchFjhs(searchFjhs);
            }
        }
        return response;
    }
    
    
    /**
     * bean转换
     */
    private static KpdSpglResponse convertToResponse(SearchFjhResponse searchFjhFpzlDm) {
        KpdSpglResponse response = new KpdSpglResponse();
        response.setStatusCode(searchFjhFpzlDm.getStatusCode());
        response.setStatusMessage(searchFjhFpzlDm.getStatusMessage());
        if (searchFjhFpzlDm.getSearchFjhs() != null && searchFjhFpzlDm.getSearchFjhs().size() > 0) {
            List<FpKpd> fpkpds = new ArrayList<>();
            for (SearchFjh search : searchFjhFpzlDm.getSearchFjhs()) {
                FpKpd fpKpd = new FpKpd();
                fpKpd.setKpdid(Integer.valueOf(search.getKpdId()));
                fpKpd.setKpdmc(search.getKpdMc());
                fpKpd.setFjh(search.getFjh());
                fpKpd.setNsrsbh(search.getNsrsbh());
                fpkpds.add(fpKpd);
            }
            response.setFpkpds(fpkpds);
        }
        return response;
    }
    
    public static OfdToPngResponse getOfdPng(String url, OfdToPngRequest ofdToPngRequest) {
        
        
        // 设置请求头参数
        log.debug("{}ofd转png接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonStringNullToEmpty(ofdToPngRequest));
        String result = HttpUtils.doPost(url, JsonUtils.getInstance().toJsonStringNullToEmpty(ofdToPngRequest));
        log.debug("{}ofd转png接口，出参:{}", LOGGER_MSG, result);
        
        return JsonUtils.getInstance().parseObject(result, OfdToPngResponse.class);
        
    }

    /**
     * 查询发票开具的最终状态
     *
     * @param url
     * @param invoiceQueryRequset
     * @return
     */
    public static CommonInvoiceStatus queryInvoiceFinalSatusFromSk(String url, InvoiceQuery invoiceQueryRequset) {
    
        CommonInvoiceStatus commonInvoiceStatus = new CommonInvoiceStatus();
        commonInvoiceStatus.setStatusCode(ConfigurerInfo.SUCCSSCODE);
        Map<String, String> header = getRequestHead(invoiceQueryRequset.getTerminalCode());
        // 设置请求头参数

        log.debug("{}发票开具状态的接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonStringNullToEmpty(invoiceQueryRequset));
        String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(invoiceQueryRequset), header);
        log.debug("{}发票开具状态的接口，出参:{}", LOGGER_MSG, result);

        InvoiceQueryResponse invoiceQueryResponse = new InvoiceQueryResponse();
        if(OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(invoiceQueryRequset.getTerminalCode())){
            InvoiceQueryResponseNewTax response =  JsonUtils.getInstance().parseObject(result, InvoiceQueryResponseNewTax.class);
            invoiceQueryResponse = convertToInvoiceQueryResponse(response);

        }else{
            invoiceQueryResponse = JsonUtils.getInstance().parseObject(result, InvoiceQueryResponse.class);
        }

        if (ObjectUtil.isNotNull(invoiceQueryResponse)) {
            commonInvoiceStatus = invoiceQueryResponse.getCommonInvoicestatus();
            commonInvoiceStatus.setStatusCode(invoiceQueryResponse.getStatusCode());
            commonInvoiceStatus.setStatusMessage(invoiceQueryResponse.getStatusMessage());
        }
        return commonInvoiceStatus;
    
    }

    private static InvoiceQueryResponse convertToInvoiceQueryResponse(InvoiceQueryResponseNewTax response) {
        InvoiceQueryResponse invoiceQueryResponse = new InvoiceQueryResponse();
        if(response != null){
            invoiceQueryResponse.setStatusCode(response.getZTDM());
            invoiceQueryResponse.setStatusMessage(response.getZTXX());
            if(response.getCOMMON_INVOICESTATUS() != null){
                CommonInvoiceStatus commonInvoicestatus = new CommonInvoiceStatus();
                commonInvoicestatus.setFpztms(response.getCOMMON_INVOICESTATUS().getFPZTMS());
                commonInvoicestatus.setFpzt(response.getCOMMON_INVOICESTATUS().getFPZT());
                commonInvoicestatus.setFpqqlsh(response.getCOMMON_INVOICESTATUS().getFPQQLSH());
                invoiceQueryResponse.setCommonInvoicestatus(commonInvoicestatus);
            }
        }
        return invoiceQueryResponse;
    }


    public static ResponseBaseBean redInvoiceRevoke(String url, RedInvoiceRevokeRequest invoiceQueryRequest) {

        Map<String, String> header = getRequestHead(invoiceQueryRequest.getTerminalCode());
        // 设置请求头参数
        log.debug("{}红字申请单撤销的接口，url:{},入参:{}", LOGGER_MSG, url, JsonUtils.getInstance().toJsonStringNullToEmpty(invoiceQueryRequest));
        String result = HttpUtils.doPostWithHeader(url, JsonUtils.getInstance().toJsonStringNullToEmpty(invoiceQueryRequest),header);
        log.debug("{}红字申请单撤销的接口，出参:{}", LOGGER_MSG, result);

        return JsonUtils.getInstance().parseObject(result, ResponseBaseBean.class);

    }
    
    
}
