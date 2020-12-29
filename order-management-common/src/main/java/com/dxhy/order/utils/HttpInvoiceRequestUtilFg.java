package com.dxhy.order.utils;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.ExceptionContentEnum;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.a9.sld.SearchSldResponse;
import com.dxhy.order.model.fg.*;
import com.dxhy.order.model.protocol.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dxhy.order.utils.HttpInvoiceRequestUtil.getRequestHead;

/**
 * 方格税控设备以http方式请求底层获取数据工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/9/24 15:35
 */
@Slf4j
public class HttpInvoiceRequestUtilFg {

    private static final String LOGGER_MSG = "(方格请求底层接口)";
    
    
    /**
     * 调用底层生成PDF
     *
     * @param url
     * @param invoiceQueueEntity
     * @return
     */
    public static Result genPdf(String url, InvoiceQueueEntity invoiceQueueEntity, String terminalCode) {
    
        Map<String, String> header = getRequestHead(terminalCode);
        String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(invoiceQueueEntity);
        log.debug("{}调用生成pdf的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
        String result = HttpUtils.doGetWithHeader(url, requestParam, header);
        log.debug("{}调用生成pdf的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
        return JsonUtils.getInstance().parseObject(result, Result.class);
    }
    
    /**
     * 调用同步税盘信息接口
     *
     * @param url
     * @param tbSpxxParam
     * @return
     */
    public static ResponseBaseBean tbSpxx(String url, TbSpxxParam tbSpxxParam,String terminalCode) {
        ResponseBaseBean responseBaseBean = new ResponseBaseBean();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            Map<String,String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(tbSpxxParam);
            log.debug("{}调用同步税盘信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}调用同步税盘信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, ResponseBaseBean.class);
            return responseBaseBean;

        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用同步税盘信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
    }
    
    /**
     * 调用获取打印机信息接口
     *
     * @param url
     * @param id
     * @return
     */
    public static FgkpSkDyjEntity getDyjxx(String url, String id, String terminalCode) {

        FgkpSkDyjEntity fgkpSkDyjEntity = new FgkpSkDyjEntity();
        fgkpSkDyjEntity.setCode(Constant.SUCCSSCODE);
        try {
            Map<String, String> paraMap = new HashMap<>(1);
            paraMap.put("id", id);
            Map<String, String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(paraMap);
            log.debug("{}调用获取打印机信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}调用获取打印机信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            FgkpSkDyjEntityInfo fgkpSkDyjEntityInfo = JsonUtils.getInstance().parseObject(result, FgkpSkDyjEntityInfo.class);
            if (ObjectUtil.isNotNull(fgkpSkDyjEntityInfo) && Constant.SUCCSSCODE.equals(fgkpSkDyjEntityInfo.getCode())) {
                fgkpSkDyjEntity = fgkpSkDyjEntityInfo.getData();
                fgkpSkDyjEntity.setCode(fgkpSkDyjEntityInfo.getCode());
                fgkpSkDyjEntity.setMsg(fgkpSkDyjEntityInfo.getMsg());
                return fgkpSkDyjEntity;
            }

        } catch (Exception e) {
            fgkpSkDyjEntity.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            fgkpSkDyjEntity.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用获取打印机信息异常:{}", LOGGER_MSG, e);
            return fgkpSkDyjEntity;
        }

        return fgkpSkDyjEntity;
    }




    /**
     * 调用添加打印机信息接口
     *
     * @param url
     * @param fgkpSkDyjParam
     * @return
     */
    public static ResponseBaseBean addDyjxx(String url, FgkpSkDyjParam fgkpSkDyjParam) {
        
        ResponseBaseBean responseBaseBean = new ResponseBaseBean();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(fgkpSkDyjParam);
            log.debug("{}调用添加打印机信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPost(url, requestParam);
            log.debug("{}调用添加打印机信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, FgkpSkDyjEntity.class);
            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用添加打印机信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    
    /**
     * 调用添加打印机信息接口
     * todo 添加和更新是否可以合并?
     *
     * @param url
     * @param fgkpSkDyjParam
     * @return
     */
    public static ResponseBaseBean updateDyjxx(String url, FgkpSkDyjParam fgkpSkDyjParam) {
        
        ResponseBaseBean responseBaseBean = new ResponseBaseBean();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(fgkpSkDyjParam);
            log.debug("{}调用更新打印机信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPost(url, requestParam);
            log.debug("{}调用更新打印机信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, FgkpSkDyjEntity.class);
            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用更新打印机信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    
    /**
     * 调用删除打印机信息接口
     *
     * @param url
     * @param id
     * @return
     */
    public static ResponseBaseBean deleteDyjxx(String url, String id) {
        
        ResponseBaseBean responseBaseBean = new ResponseBaseBean();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            Map<String, String> paraMap = new HashMap<>(1);
            paraMap.put("id", id);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(paraMap);
            log.debug("{}调用删除打印机信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPost(url, requestParam);
            log.debug("{}调用删除打印机信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, FgkpSkDyjEntity.class);
            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用删除打印机信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    
    /**
     * 调用查询打印机列表信息接口
     *
     * @param url
     * @param fgkpSkDyjmcCxParam
     * @return
     */
    public static FgkpSkDyjEntityList findSkDyjByShZl(String url, FgkpSkDyjmcCxParam fgkpSkDyjmcCxParam, String terminalCode) {
        
        FgkpSkDyjEntityList responseBaseBean = new FgkpSkDyjEntityList();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            Map<String, String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(fgkpSkDyjmcCxParam);
            log.debug("{}调用查询打印机列表信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}调用查询打印机列表信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, FgkpSkDyjEntityList.class);
            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用查询打印机列表信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    
    /**
     * 调用查询税盘列表信息接口
     *
     * @param url
     * @param fgJspxxReqEntity
     * @return
     */
    public static SearchSldResponse querySpByNsrsbh(String url, FgJspxxReqEntity fgJspxxReqEntity,String terminalCode) {
        
        SearchSldResponse responseBaseBean = new SearchSldResponse();
        responseBaseBean.setStatusCode(Constant.SUCCSSCODE);
        try {
            Map<String,String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(fgJspxxReqEntity);
            log.debug("{}调用查询税盘列表信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam,header);
            log.debug("{}调用查询税盘列表信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            Result result1 = JsonUtils.getInstance().parseObject(result, Result.class);
            
            if (ObjectUtil.isNotNull(result1) && Constant.SUCCSSCODE.equals(result1.get("code"))) {
                List<SearchSld> searchSlds = new ArrayList<>();
    
                List<SpBwXxCxDto> data = JSON.parseArray(result1.get("data").toString(), SpBwXxCxDto.class);
                for (SpBwXxCxDto dto : data) {
                    SearchSld sldSearch = new SearchSld();
                    sldSearch.setNsrsbh(dto.getNsrsbh());
                    sldSearch.setSldId(dto.getJqbh());
                    sldSearch.setJqbh(dto.getJqbh());
                    sldSearch.setTerminalCode(terminalCode);
                    sldSearch.setZdbs("");
                    sldSearch.setSldMc(dto.getJqbh());
                    sldSearch.setFjh(dto.getJqbh());
                    searchSlds.add(sldSearch);
                }
    
                responseBaseBean.setStatusCode(String.valueOf(result1.get("code")));
                responseBaseBean.setStatusMessage(String.valueOf(result1.get("msg")));
                responseBaseBean.setSlds(searchSlds);
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setStatusCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setStatusMessage(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用查询打印机列表信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    /**
     * 调用税盘限额信息接口
     *
     * @param url
     * @param fgJspxxReqEntity
     * @return
     */
    public static SpFpXeDto querySpZlXeByNsrsbh(String url, FgJspxxReqEntity fgJspxxReqEntity,String terminalCode) {
        
        SpFpXeDto responseBaseBean = new SpFpXeDto();
        responseBaseBean.setCode(Constant.SUCCSSCODE);
        try {
            Map<String, String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(fgJspxxReqEntity);
            log.debug("{}调用税盘限额信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}调用税盘限额信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            responseBaseBean = JsonUtils.getInstance().parseObject(result, SpFpXeDto.class);
            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
                return responseBaseBean;
            }
            
        } catch (Exception e) {
            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
            log.error("{}调用税盘限额信息异常:{}", LOGGER_MSG, e);
            return responseBaseBean;
        }
        
        return responseBaseBean;
    }
    
    
    /**
     * 调用获取注册码信息接口
     *
     * @param url
     * @param sqZcxxParam
     * @return
     */
    public static String sqZcxx(String url, SqZcxxParam sqZcxxParam,String terminalCode) {
        
        try {
            Map<String, String> header = getRequestHead(terminalCode);
            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(sqZcxxParam);
            log.debug("{}调用获取注册码的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
            String result = HttpUtils.doPostWithHeader(url, requestParam, header);
            log.debug("{}调用获取注册码的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
            JSONObject result1 = JsonUtils.getInstance().parseObject(result, JSONObject.class);
            if (ObjectUtil.isNotNull(result1) && Constant.SUCCSSCODE.equals(result1.getString("code"))) {
                JSONObject data = result1.getJSONObject("data");
                return data.getString("ZCM");
            }
            
        } catch (Exception e) {
            
            log.error("{}调用获取注册码异常:{}", LOGGER_MSG, e);
            return "";
        }
        
        return "";
    }

//
//    /**
//     * 调用税盘限额信息接口
//     *
//     * @param url
//     * @param nsrsbh
//     * @return
//     */
//    public static FG_TAX_INFOS_RES querySpZlXeByNsrsbh(String url, String nsrsbh) {
//
//        FG_TAX_INFOS_RES responseBaseBean = new FG_TAX_INFOS_RES();
//        responseBaseBean.setCode(Constant.SUCCSSCODE);
//        try {
//            Map<String,String> paraMap = new HashMap<>(1);
//            paraMap.put("nsrsbh",nsrsbh);
//            String requestParam = JsonUtils.getInstance().toJsonStringNullToEmpty(paraMap);
//            log.debug("{}调用税盘限额信息的接口,url:{},入参:{}", LOGGER_MSG, url, requestParam);
//            String result = HttpUtils.doPost(url, requestParam);
//            log.debug("{}调用税盘限额信息的接口,出参:{}", LOGGER_MSG, StringUtils.isNotBlank(result) && result.length() > 500 ? result.substring(0, 500) : result);
//            responseBaseBean = JsonUtils.getInstance().parseObject(result, FG_TAX_INFOS_RES.class);
//            if (ObjectUtil.isNotNull(responseBaseBean) && Constant.SUCCSSCODE.equals(responseBaseBean.getCode())) {
//                return responseBaseBean;
//            }
//
//        } catch (Exception e) {
//            responseBaseBean.setCode(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getKey());
//            responseBaseBean.setMsg(ExceptionContentEnum.FG_INVOICE_ERROR_9990.getMessage());
//            log.error("{}调用税盘限额信息异常:{}", LOGGER_MSG, e);
//            return responseBaseBean;
//        }
//
//        return responseBaseBean;
//    }
//

}
