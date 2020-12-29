package com.dxhy.order.consumer.openapi.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.dxhy.invoice.protocol.sl.sld.SldJspxx;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;
import com.dxhy.order.api.*;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.constant.RespStatusEnum;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.fiscal.service.a9.SldManagerServiceA9;
import com.dxhy.order.consumer.openapi.service.ICommonInterfaceService;
import com.dxhy.order.consumer.utils.BeanTransitionUtils;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.dxhy.order.model.protocol.ResponseStatus;
import com.dxhy.order.model.protocol.Result;
import com.dxhy.order.protocol.v4.order.DDPCXX_REQ;
import com.dxhy.order.protocol.v4.order.DDZXX;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 订单对外接口业务实现类
 *
 * @author: chengyafu
 * @date: 2018年8月9日 下午4:15:27
 */
@Service
@Slf4j
public class CommonInterfaceServiceImpl implements ICommonInterfaceService {
    
    
    private static final String LOGGER_MSG = "(订单对外接口通用业务类)";
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;

    @Reference
    private ApiCommodityService apiCommodityService;
    
    @Resource
    private SldManagerServiceA9 sldManagerServiceA;
    
    @Reference
    private ApiBusinessTypeService apiBusinessTypeService;
    
    @Reference
    private ApiOrderBatchRequestService apiOrderBatchRequestService;
    
    @Reference
    private ApiTaxClassCodeService taxClassCodeService;
    
    @Reference
    private ApiTaxEquipmentService taxEquipmentService;
    
    @Reference
    private ICommonDisposeService commonDisposeService;
    
    @Reference
    private ApiFangGeInterfaceService apiFangGeInterfaceService;
    
    @Reference
    private RedisService redisService;
    
    @Resource
    private UnifyService unifyService;
    
    
    /**
     * 校验接口入参数据,非空和数据校验
     *
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
    @Override
    public Result checkInterfaceParam(String interfaceVersion, String interfaceName, String timestamp, String nonce, String secretId, String signature, String encryptCode, String zipCode, String content) {
        
        log.info("{},数据校验,请求的interfaceVersion:{},interfaceName:{},timestamp:{},nonce:{},secretId:{},signature:{},encryptCode:{},zipCode:{},content:{}", LOGGER_MSG, interfaceVersion, interfaceName, timestamp, nonce, secretId, signature, encryptCode, zipCode, content);
        
        if (StringUtils.isBlank(interfaceVersion)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getCode(), RespStatusEnum.CHECK_INTERFACEVERSION_NULL.getDescribe()));
        } else if (StringUtils.isBlank(interfaceName)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACENAME_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACENAME_NULL.getCode(), RespStatusEnum.CHECK_INTERFACENAME_NULL.getDescribe()));
        } else if (StringUtils.isBlank(timestamp)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_TIMESTAMP_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_TIMESTAMP_NULL.getCode(), RespStatusEnum.CHECK_TIMESTAMP_NULL.getDescribe()));
        } else if (StringUtils.isBlank(nonce)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_NONCE_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_NONCE_NULL.getCode(), RespStatusEnum.CHECK_NONCE_NULL.getDescribe()));
        } else if (StringUtils.isBlank(secretId)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_SECRETID_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_SECRETID_NULL.getCode(), RespStatusEnum.CHECK_SECRETID_NULL.getDescribe()));
        } else if (StringUtils.isBlank(signature)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_SIGNATURE_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_SIGNATURE_NULL.getCode(), RespStatusEnum.CHECK_SIGNATURE_NULL.getDescribe()));
        } else if (StringUtils.isBlank(encryptCode)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_ENCRYPTCODE_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_ENCRYPTCODE_NULL.getCode(), RespStatusEnum.CHECK_ENCRYPTCODE_NULL.getDescribe()));
        } else if (StringUtils.isBlank(zipCode)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_ZIPCODE_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_ZIPCODE_NULL.getCode(), RespStatusEnum.CHECK_ZIPCODE_NULL.getDescribe()));
        } else if (StringUtils.isBlank(content)) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_CONTENT_NULL.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_CONTENT_NULL.getCode(), RespStatusEnum.CHECK_CONTENT_NULL.getDescribe()));
        }
        
        /**
         * 接口版本只支持v1和v2,v3,v4
         * 接口名称暂时不作校验
         */
        if (!(ConfigurerInfo.INTERFACE_VERSION_V1.equals(interfaceVersion) || ConfigurerInfo.INTERFACE_VERSION_V2.equals(interfaceVersion) || ConfigurerInfo.INTERFACE_VERSION_V3.equals(interfaceVersion) || ConfigurerInfo.INTERFACE_VERSION_V4.equals(interfaceVersion))) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getCode(), RespStatusEnum.CHECK_INTERFACEVERSION_DATA_ERROR.getDescribe()));
        } else if (!(ConfigureConstant.STRING_0.equals(encryptCode) || ConfigureConstant.STRING_1.equals(encryptCode))) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_ENCRYPTCODE_DATA_ERROR.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_ENCRYPTCODE_DATA_ERROR.getCode(), RespStatusEnum.CHECK_ENCRYPTCODE_DATA_ERROR.getDescribe()));
        } else if (!(ConfigureConstant.STRING_0.equals(zipCode) || ConfigureConstant.STRING_1.equals(zipCode))) {
            log.error("{}数据校验失败,错误信息为:{}", LOGGER_MSG, RespStatusEnum.CHECK_ZIPCODE_NULL_DATA_ERROR.getDescribe());
            return Result.error(new ResponseStatus(RespStatusEnum.CHECK_ZIPCODE_NULL_DATA_ERROR.getCode(), RespStatusEnum.CHECK_ZIPCODE_NULL_DATA_ERROR.getDescribe()));
        }
        
        return Result.ok(new ResponseStatus(RespStatusEnum.SUCCESS.getCode(), RespStatusEnum.SUCCESS.getDescribe()));
        
    }

    @Override
    public Result auth(String timeStamp
            ,String nonce
            ,String secretId
            ,String reqSign
            ,String encryptCode
            ,String zipCode
            ,String content) {
/*        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/json;charset=UTF-8");*/
/*        String nonce = request.getParameter(ConfigurerInfo.NONCE);
        String secretId = request.getParameter(ConfigurerInfo.SECRETID);
        String timeStamp = request.getParameter(ConfigurerInfo.TIMESTAMP);
        String zipCode = request.getParameter(ConfigurerInfo.ZIPCODE);
        String encryptCode = request.getParameter(ConfigurerInfo.ENCRYPTCODE);
        String content = request.getParameter(ConfigurerInfo.CONTENT);
        String reqSign = request.getParameter(ConfigurerInfo.SIGNATURE);*/
        log.info("{},请求的secretId:{}，nonce:{},timestamp:{},zipCode:{},encryptCode:{},签名值sign:{}，内容content:{}", LOGGER_MSG, secretId, nonce, timeStamp, zipCode, encryptCode, reqSign, content);
/*        StringBuilder url = new StringBuilder();
        url.append(request.getMethod()).append(request.getServerName());
        if (ConfigureConstant.INT_80 != request.getServerPort() && ConfigureConstant.INT_443 != request.getServerPort()) {
            url.append(":").append(request.getServerPort());
        }
        url.append(request.getRequestURI()).append("?");
        log.info("{}生成签名的URL:{}", LOGGER_MSG, url);*/
        //特定排序
        TreeMap<String, String> sortMap = new TreeMap<>();
        sortMap.put(ConfigurerInfo.NONCE, nonce);
        sortMap.put(ConfigurerInfo.SECRETID, secretId);
        sortMap.put(ConfigurerInfo.TIMESTAMP, timeStamp);
        sortMap.put(ConfigurerInfo.CONTENT, content);
        sortMap.put(ConfigurerInfo.ENCRYPTCODE, encryptCode);
        sortMap.put(ConfigurerInfo.ZIPCODE, zipCode);
        //获取id对应的key
        String value = commonDisposeService.getAuthMap(secretId);
        //获取key为空
        if (!StringUtils.isNotBlank(value)) {
            log.error("{}根据secretId:{},获取对应的secretKey为空!", LOGGER_MSG, secretId);
            return Result.error(new ResponseStatus(RespStatusEnum.NOTAUTH.getCode(), RespStatusEnum.NOTAUTH.getDescribe()));
        }
        
        log.info("{}通过secretId:{},获取的对应的secretKey:{}", LOGGER_MSG, secretId, value);
        String localSign = "";
        try {
            localSign = HmacSHA1Util.genSign("", sortMap, value);
        } catch (Exception e) {
            log.error("{}鉴权异常,secretId为:{},secretKey为:{},错误原因为:{}", LOGGER_MSG, secretId, value, e);
            return Result.error(new ResponseStatus(RespStatusEnum.AUTHFAIL.getCode(), RespStatusEnum.AUTHFAIL.getDescribe()));
        }
        log.info("{}生成的本地签名值为local:{}，请求签名值:{}", LOGGER_MSG, localSign, reqSign);
        
        if (StringUtils.isNotBlank(localSign) && StringUtils.isNotBlank(reqSign)) {
            if (localSign.equals(reqSign)) {
                log.info("secretId:{},鉴权成功", secretId);
                return Result.ok(new ResponseStatus(RespStatusEnum.SUCCESS.getCode(), RespStatusEnum.SUCCESS.getDescribe()));
            } else {
                log.error("{}鉴权失败.请求鉴权值为:{},计算后鉴权值为:{}", LOGGER_MSG, reqSign, localSign);
                return Result.error(new ResponseStatus(RespStatusEnum.AUTHFAIL.getCode(), RespStatusEnum.AUTHFAIL.getDescribe()));
            }
        } else {
            log.error("{}鉴权失败.请求鉴权值和计算后鉴权值为空", LOGGER_MSG);
            return Result.error(new ResponseStatus(RespStatusEnum.AUTHFAIL.getCode(), RespStatusEnum.AUTHFAIL.getDescribe()));
        }
        
    }
    
    /**
     * 业务类型信息采集
     *
     * @param ywlx   业务类型名称
     * @param nsrsbh 销售方纳税人识别号
     * @return String 业务类型ID
     * @author: 陈玉航
     * @date: Created on 2019年6月29日 下午4:30:40
     */
    @Override
    public String yesxInfoCollect(String ywlx, String nsrsbh, String xhfmc) {
        //查询业务类型信息
        List<String> shList = new ArrayList<>();
        shList.add(nsrsbh);
        BusinessTypeInfo bti = apiBusinessTypeService.queryYwlxInfoByNameAndNsrsbh(ywlx, shList);
        if (bti == null) {
            String generateMixString = RandomUtil.randomString(20);
            log.info("{} 业务类型信息不存在，采集入库并返回业务类型Id:{}", LOGGER_MSG, generateMixString);
            bti = new BusinessTypeInfo();
            bti.setBusinessId(generateMixString);
            bti.setBusinessName(ywlx);
            bti.setDescription("");
            bti.setStatus(ConfigureConstant.STRING_0);
            bti.setId(apiInvoiceCommonService.getGenerateShotKey());
            bti.setXhfNsrsbh(nsrsbh);
            bti.setXhfMc(xhfmc);
            bti.setCreateTime(new Date());
            //保存业务类型信息
            apiBusinessTypeService.saveBusinessTypeInfo(bti);
            return generateMixString;
        } else {
            log.info("{} 业务类型信息已存在，返回业务类型Id:{}", LOGGER_MSG, bti.getBusinessId());
            return bti.getBusinessId();
        }
    }
    
    @Override
    public R checkSldInfoA9(DDPCXX_REQ ddpcxxReq, String sldid, String terminalCode) {
        boolean isContainCpy = false;
        //循环所有的订单 判断开票是否包含成品油
        for (DDZXX ddzxx : ddpcxxReq.getDDZXX()) {
            if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(ddzxx.getDDTXX().getQDBZ())) {
                isContainCpy = true;
            }
        
        }
        //如果包含成品油 判断受力点是否有成品油的资质
        if (isContainCpy) {
            /**
             * 种类代码转换
             */
            String invoiceType = CommonUtils.transFpzldm(ddpcxxReq.getDDPCXX().getFPLXDM());
    
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
    
            Set<SearchSld> resultList = new HashSet<>();
            HttpInvoiceRequestUtil.getSldList(resultList, url, invoiceType, OrderInfoEnum.OIL_TYPE_1.getKey(), ddpcxxReq.getDDZXX().get(0).getDDTXX().getXHFSBH(), null, sldid, terminalCode);
            HttpInvoiceRequestUtil.getSldList(resultList, url, invoiceType, OrderInfoEnum.OIL_TYPE_2.getKey(), ddpcxxReq.getDDZXX().get(0).getDDTXX().getXHFSBH(), null, sldid, terminalCode);
            //如果根据成品油标识 id查询不到受理点查询失败
            if (ObjectUtil.isEmpty(resultList) || resultList.size() <= 0) {
                log.error("{}受理点为非成品油的受理点，但是批次中包含成品油发票", LOGGER_MSG);
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.ORDER__SLD_NOT_CPY_ERROR.getKey())
                        .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.ORDER__SLD_NOT_CPY_ERROR.getMessage());
            }
        }
        return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
    }
    
    
    /**
     * @Description ：校验数据是否重复
     */
    @Override
    public R checkOrderInfoIsRepeat(DDPCXX_REQ ddpcxxReq) {
        
        List<String> shList = new ArrayList<>();
        shList.add(ddpcxxReq.getDDPCXX().getNSRSBH());
        int pcCount = apiOrderBatchRequestService.selectOrderBatchRequestByDdqqpch(ddpcxxReq.getDDPCXX().getDDQQPCH(), shList);
        
        //数据库校验请求批次号是否存在
        if (pcCount > 0) {
            log.error("{}订单批次在数据库中已存在!批次号为:{}", LOGGER_MSG, ddpcxxReq.getDDPCXX().getDDQQPCH());
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010001_V3.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010001_V3.getMessage())
                    .put("fpqqpch", ddpcxxReq.getDDPCXX().getDDQQPCH());
        }
        
        //TODO redis中校验请求批次号是否存在 开票队列中放入数据的时候会以请求批次号为key添加一条数据
        return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
    }
    
    /**
     * 处理受理点
     * 获取sldid,当sldid为-1时,先调用querySld接口,获取所有受理点列表
     * 再根据sldid 调用selectSldJspxx接口 获取对应的分机号
     * 最后分别赋值sldid（sldid）,kpjh（fjh）
     *
     * @param
     * @return
     */
    @Override
    public R dealWithSldStartV3(String sldid, String fpzldm, String nsrsbh, String qdbz, String terminalCode) {
        //方格税盘单独处理
        if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
            String registCodeStr = apiFangGeInterfaceService.getRegistCodeByRedis(nsrsbh, sldid);
            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(registCodeStr, RegistrationCode.class);
            if (ObjectUtil.isNotEmpty(registCodeStr)) {
                return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("sldid", registrationCode.getJqbh())
                        .put("kpjh", registrationCode.getJqbh()).put("sldmc", registrationCode.getJqbh());
            } else {
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getKey())
                        .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getMessage());
            }
        
        } else {
            /**
             * 种类代码转换
             */
            fpzldm = CommonUtils.transFpzldm(fpzldm);
    
            String url = OpenApiConfig.querySldList;
            if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                url = OpenApiConfig.queryKpdXxBw;
            } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                url = OpenApiConfig.queryNsrXnsbxx;
                /**
                 * 如果是新税控转换发票种类代码
                 */
                fpzldm = CommonUtils.transFplxdm(fpzldm);
            }
    
            Set<SearchSld> searchSldSet = new HashSet<>();
            if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(qdbz)) {
                // 成品油的获取成品油的受理点
        
                HttpInvoiceRequestUtil.getSldList(searchSldSet, url, fpzldm, OrderInfoEnum.OIL_TYPE_1.getKey(), nsrsbh, null, sldid, terminalCode);
                HttpInvoiceRequestUtil.getSldList(searchSldSet, url, fpzldm, OrderInfoEnum.OIL_TYPE_2.getKey(), nsrsbh, null, sldid, terminalCode);
        
            } else {
        
                HttpInvoiceRequestUtil.getSldList(searchSldSet, url, fpzldm, "", nsrsbh, null, sldid, terminalCode);
            
            }
        
            //轮询所有受理点查询剩余票量
            if (CollectionUtils.isNotEmpty(searchSldSet)) {
                for (SearchSld sldXx : searchSldSet) {
                    /**
                     * 如果受理点不为空,需要匹配正确受理点进行开票
                     */
                    if (StringUtils.isNotBlank(sldid) && !ConfigureConstant.STRING_1_.equals(sldid) && !sldid.equals(sldXx.getSldId())) {
                        continue;
                    }
                    SldJspxxRequest sldKcRequest = new SldJspxxRequest();
                    sldKcRequest.setSldid(sldXx.getSldId());
                    sldKcRequest.setFpzldm(fpzldm);
                    sldKcRequest.setNsrsbh(nsrsbh);
                    SldJspxxResponse sldJspxxResponse = unifyService.querSldFpfs(sldKcRequest, terminalCode);
    
    
                    //查询受理点剩余份数 找到一条剩余份数不为0的数据
                    if (ObjectUtil.isNotEmpty(sldJspxxResponse) && ConfigureConstant.STRING_0000.equals(sldJspxxResponse.getStatusCode()) && ObjectUtil.isNotEmpty(sldJspxxResponse.getSldJspxxList())) {
                        List<SldJspxx> sldKykcList = sldJspxxResponse.getSldJspxxList();
                        for (SldJspxx sldKcmx : sldKykcList) {
                            String fpfs = String.valueOf(sldKcmx.getFpfs());
    
                            if (Integer.parseInt(fpfs) > 0) {
                                String fjh = sldXx.getFjh();
                                if (StringUtils.isBlank(fjh)) {
                                    fjh = sldKcmx.getFjh();
                                }
                                if (StringUtils.isNotBlank(sldid) && sldid.equals(sldXx.getSldId())) {
                                    return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("sldid", sldXx.getSldId())
                                            .put("kpjh", fjh).put("sldmc", sldXx.getSldMc());
                                } else {
                                    return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("sldid", sldXx.getSldId())
                                            .put("kpjh", fjh).put("sldmc", sldXx.getSldMc());
                                }
                            }
    
                        }
                    }
                }
            } else {
                return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getKey())
                        .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getMessage());
            }
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_ERROR_CODE_202005_V3.getMessage());
        
        }
    }
    
    /**
     * 商品明细数据补全商品名称简码
     *
     * @param orderItemInfo
     * @param terminal
     * @return
     */
    @Override
    public void dealOrderItem(List<OrderItemInfo> orderItemInfo, String xhfNsrsbh, String qdbz, String terminal) throws OrderReceiveException {
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        for (OrderItemInfo item : orderItemInfo) {
        
            /**
             * 如果是清单红字发票的话,不进行明细行补全
             */
            if (OrderInfoEnum.FPHXZ_CODE_6.getKey().equals(item.getFphxz())) {
                continue;
            }
            /**
             * 根据商品编码判断如何进行补全数据
             */
            if (StringUtils.isBlank(item.getSpbm())) {
                /**
                 * 商品编码为空,调用接口根据商品名称进行匹配数据
                 */
    
                if (StringUtils.isBlank(item.getXmmc())) {
                    log.error("{}商品名称为空", LOGGER_MSG);
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_SPBM_SPMC_QUERY_NULL, item.getSphxh());
                }
                Map<String, String> map = new HashedMap<>();
                map.put("productName", item.getXmmc());
    
                //自行编码如果不为空的话，根据商品名称和自行编码查询商品信息
                if (StringUtils.isNotBlank(item.getZxbm())) {
                    map.put("zxbm", item.getZxbm());
                }
    
                log.info("{}根据纳税人识别号和商品名称查询商品信息的接口,入参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
                List<CommodityCodeEntity> queryProductList = apiCommodityService.queryProductList(map, shList);
                log.info("{}根据纳税人识别号和商品名称查询商品信息的接口,出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(queryProductList));
                
                if (CollectionUtils.isEmpty(queryProductList)) {
                    log.error("{}根据商品名称查询到的商品为空,商品名称：{}", LOGGER_MSG, item.getXmmc());
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_SPBM_QUERY_NULL, item.getSphxh());
                }
                if(queryProductList.size()>1){
                    log.error("{}根据商品名称查询到多个商品信息,商品名称：{}", LOGGER_MSG, item.getXmmc());
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_SPBM_QUERY_NULL, item.getSphxh());
                }
                CommodityCodeEntity queryProduct = queryProductList.get(0);
                item.setSpbm(queryProduct.getTaxClassCode());
                /**
                 * 如果项目名称为空,使用底层返回数据进行补全
                 * 如果不为空,并且需要补全,则进行补全
                 */
                String spmc = item.getXmmc();
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtils.isBlank(spmc)) {
                    if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminal)) {
                        spmc = stringBuilder.append("*").append(queryProduct.getTaxClassAbbreviation()).append("*").append(queryProduct.getMerchandiseName()).toString();
        
                    } else {
                        spmc = queryProduct.getMerchandiseName();
                    }
                } else if (StringUtil.checkStr(spmc, queryProduct.getTaxClassAbbreviation())) {
                    if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminal)) {
                        spmc = stringBuilder.append("*").append(queryProduct.getTaxClassAbbreviation()).append("*").append(spmc).toString();
                    }
                }
                item.setXmmc(spmc);
                //如果税率为空使用查询到的税率否则使用原税率
                if (StringUtils.isBlank(item.getSl()) && StringUtils.isNotBlank(queryProduct.getTaxRate())) {
                    item.setSl(StringUtil.formatSl(queryProduct.getTaxRate()));
                }
    
            } else {
                /**
                 * 商品编码不为空,需要调用底层商品编码获取简码接口获取数据
                 */
                log.debug("spbm:{}", item.getSpbm());
                TaxClassCodeEntity qtc = taxClassCodeService.queryTaxClassCodeEntity(item.getSpbm());
                log.info("{} 商品分类信息查询完毕：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(qtc));
                if (qtc == null) {
                    log.error("{}商品名称为{}，税收分类编码为{}的商品行，税收分类编码错误。。"
                            , LOGGER_MSG, item.getXmmc(), item.getSpbm());
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_SPBM_QUERY_NULL, item.getSphxh());
                }
    
                /**
                 * 成品油校验
                 * 如果返回的数据是成品油的,然后清单标志不为4,
                 * todo 后期需要修改商品编码查询数据补全明细,数据库添加字段,支持商品显示是否为成品油数据
                 */
                if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(qdbz) && !ConfigureConstant.STRING_Y.equals(qtc.getCpy())) {
                    log.error("{}商品名称为{}，税收分类编码为{}的商品行，成品油数据只能传递成品油编码"
                            , LOGGER_MSG, item.getXmmc(), item.getSpbm());
                    throw new OrderReceiveException(OrderInfoContentEnum.CHECK_ISS7PRI_107123);
                } else if (!OrderInfoEnum.QDBZ_CODE_4.getKey().equals(qdbz) && ConfigureConstant.STRING_Y.equals(qtc.getCpy())) {
                    log.error("{}商品名称为{}，税收分类编码为{}的商品行，成品油数据只能传递成品油编码"
                            , LOGGER_MSG, item.getXmmc(), item.getSpbm());
                    throw new OrderReceiveException(OrderInfoContentEnum.CHECK_ISS7PRI_107123);
                }
    
                /**
                 * 如果项目名称为空,使用底层返回数据进行补全
                 * 如果不为空,并且需要补全,则进行补全
                 */
                String spmc = item.getXmmc();
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtils.isBlank(spmc)) {
                    if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminal)) {
                        spmc = stringBuilder.append("*").append(qtc.getSpjc()).append("*").append(qtc.getSpmc()).toString();
                    } else {
                        spmc = qtc.getSpmc();
                    }
        
                } else if (StringUtil.checkStr(spmc, qtc.getSpjc())) {
                    if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminal)) {
                        spmc = stringBuilder.append("*").append(qtc.getSpjc()).append("*").append(spmc).toString();
                    }
                }
                
                /**
                 * 判断补全后数据商品编码长度是否超过接口定义的90字节,如果超过自动进行截取,只取前面90字节.
                 */
                int strLength = 0;
                try {
                    strLength = spmc.getBytes(ConfigureConstant.STRING_CHARSET_GBK).length;
                    
                    if (strLength > ConfigureConstant.INT_90) {
                        log.debug("对明细行商品名称进行截取,截取前数据为:{}", spmc);
                        spmc = StringUtil.subStringByByte(spmc, ConfigureConstant.INT_90);
                        log.debug("对明细行商品名称进行截取,截取后数据为:{}", spmc);
                    }
    
                } catch (Exception e) {
                    log.error("获取商品名称字节码长度出现问题，spmc:{}", spmc);
                    throw new OrderReceiveException(OrderInfoContentEnum.INVOICE_SPBM_QUERY_NULL, item.getSphxh());
                }
    
                item.setXmmc(spmc);
    
                //如果税率为空使用查询到的税率否则使用原税率
                if (StringUtils.isBlank(item.getSl()) && StringUtils.isNotBlank(qtc.getZzssl())) {
                    String[] zzssl = qtc.getZzssl().split(ConfigureConstant.STRING_POINT1);
                    if (zzssl.length > 0) {
                        item.setSl(zzssl[0]);
                    }
                }
    
            }
            
        }
    }
    
    
    @Override
    public com.dxhy.order.model.R mealAllowance(String taxpayerCode) {
        /**
         *  该接口只有查询套餐余量为0时才会返回失败,其他默认返回成功(查询异常,查询失败等)
         */
        
        //设置请求参数
//        Map<String, Object> paramMap = new HashMap<>(3);
//        paramMap.put("taxpayerCode", taxpayerCode);
//        paramMap.put("menuId", OpenApiConfig.systemProductId);
//        String paramJson = JsonUtils.getInstance().toJsonString(paramMap);
//        try {
//            log.info("套餐余量请求url:{},入参:{}", OpenApiConfig.mealAllowanceUrl, paramJson);
//            String result = HttpUtils.doPost(OpenApiConfig.mealAllowanceUrl, paramJson);
//            log.info("套餐余量请求url:{},出参:{}", OpenApiConfig.mealAllowanceUrl, result);
//            if (StringUtils.isEmpty(result)) {
//                log.error("税号对应套餐余量为空");
//                return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
////                return R.error().put(ConfigureConstant.CODE,OrderInfoContentEnum.INVOICE_ERROR_CODE_010005_V3.getKey()).put(ConfigureConstant.MSG,OrderInfoContentEnum.INVOICE_ERROR_CODE_010005_V3.getMessage());
//            }
//            MealAllowanceDto mealAllowanceDto = JsonUtils.getInstance().parseObject(result, MealAllowanceDto.class);
//            if (ConfigurerInfo.SUCCSSCODE.equals(mealAllowanceDto.getCode())) {
//                if (CollectionUtils.isNotEmpty(mealAllowanceDto.getData())) {
//                    List<MealAllowanceDataDto> data = mealAllowanceDto.getData();
//                    if (data.size() > 1) {
//                        log.error("套餐余量为多个");
//                        return R.error().put(ConfigureConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010007_V3.getKey()).put(ConfigureConstant.MSG, OrderInfoContentEnum.INVOICE_ERROR_CODE_010007_V3.getMessage());
//                    }
//                    if (Integer.parseInt(data.get(0).getChargeNumber()) <= 0) {
//                        log.error("套餐余量不足");
//                        return R.error().put(ConfigureConstant.CODE, OrderInfoContentEnum.INVOICE_ERROR_CODE_010006_V3.getKey()).put(ConfigureConstant.MSG, OrderInfoContentEnum.INVOICE_ERROR_CODE_010006_V3.getMessage());
//                    }
//                }
//            } else {
//                return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
////                return R.error().put(ConfigureConstant.CODE,OrderInfoContentEnum.INVOICE_ERROR_CODE_010005_V3.getKey()).put(ConfigureConstant.MSG,mealAllowanceDto.getMessage());
//            }
//        } catch (Exception e) {
//            log.error("请求套餐余量失败,{}", e);
//            return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
////            return R.error().put(ConfigureConstant.CODE,OrderInfoContentEnum.INVOICE_ERROR_CODE_010007_V3.getKey()).put(ConfigureConstant.MSG,OrderInfoContentEnum.INVOICE_ERROR_CODE_010007_V3.getMessage());
//        }
        return com.dxhy.order.model.R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
    }

    /**
     * 构建基础订单数据,主要处理的数据为:订单处理表,订单处理扩展表,订单发票表,
     * 订单表和订单明细表数据需要预先维护
     * 注意:订单类型,订单来源,订单状态等需要外层自动补全
     * 根据枚举类型判断对应业务,然后判断生成对应表数据.
     *
     * @param orderInfo
     * @param orderItemInfoList
     * @param orderProcessInfo
     * @param orderInvoiceInfo
     */
    @Override
    public void buildInsertOrderData(OrderInfo orderInfo, List<OrderItemInfo> orderItemInfoList, OrderProcessInfo orderProcessInfo, OrderInvoiceInfo orderInvoiceInfo) {
        /**
         * 订单入库前数据补全
         */
        String orderId = apiInvoiceCommonService.getGenerateShotKey();
        String processId = apiInvoiceCommonService.getGenerateShotKey();
        String fpqqlsh = apiInvoiceCommonService.getGenerateShotKey();
    
    
        orderInfo.setId(orderId);

        Date createTime = new Date();
        Date updateTime = createTime;
        if (StringUtils.isBlank(orderInfo.getFpqqlsh())) {
            orderInfo.setFpqqlsh(fpqqlsh);
        }
        if (orderInfo.getCreateTime() == null) {
            orderInfo.setCreateTime(createTime);
        }
        if (orderInfo.getUpdateTime() == null) {
            orderInfo.setUpdateTime(updateTime);
        }
        
        orderInfo.setProcessId(processId);
        
        /**
         * 订单明细入库前数据补全
         */
        for (int j = 0; j < orderItemInfoList.size(); j++) {
            OrderItemInfo orderItemInfo = orderItemInfoList.get(j);
            orderItemInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
            orderItemInfo.setOrderInfoId(orderInfo.getId());
            if (StringUtils.isNotBlank(orderItemInfo.getSl())) {
                orderItemInfo.setSl(StringUtil.formatSl(orderItemInfo.getSl()));
            }
            orderItemInfo.setSphxh(String.valueOf(j + 1));
    
            orderItemInfo.setCreateTime(orderItemInfo.getCreateTime() == null ? new Date() : orderItemInfo.getCreateTime());
        }
    
        String terminalCode = taxEquipmentService.getTerminalCode(orderInfo.getXhfNsrsbh());
        /**
         * 清单标志赋值
         */
        BeanTransitionUtils.getOrderInvoiceInfoQdBz(terminalCode, orderInfo, orderItemInfoList);
        
        /**
         * 订单处理表入库前数据补全
         */
        BeanTransitionUtils.transitionAutoProcessInfo(orderProcessInfo, orderInfo);
        orderProcessInfo.setId(processId);
        orderProcessInfo.setOrderInfoId(orderId);
    
    
        /**
         * 订单发票表入库前数据补全
         * todo 2019-04-10添加订单可冲红金额  蓝票为开票价税合计，红票为0
         */
        String invoiceId = apiInvoiceCommonService.getGenerateShotKey();
        String kplsh = apiInvoiceCommonService.getGenerateShotKey();
        BeanTransitionUtils.transitionOrderInvoiceInfo(orderInvoiceInfo, orderInfo);
        orderInvoiceInfo.setId(invoiceId);
        orderInvoiceInfo.setOrderInfoId(orderId);
        orderInvoiceInfo.setOrderProcessInfoId(processId);
        orderInvoiceInfo.setKplsh(kplsh);
        if (ConfigureConstant.STRING_0.equals(orderInfo.getKplx())) {
            orderInvoiceInfo.setSykchje(orderInfo.getKphjje());
        } else if (ConfigureConstant.STRING_1.equals(orderInfo.getKplx())) {
            orderInvoiceInfo.setSykchje(ConfigureConstant.STRING_0);
        }
    
    
    }
    
    @Override
    public String getDdqqlshRedisStatus(String ddqqlsh) {
        /**
         * 根据订单请求流水号查询redis中数据状态,
         * 如果查到数据为空则返回空
         */
        return redisService.get(ddqqlsh);
        
    }
    
    @Override
    public void setDdqqlshRedisStatus(String ddqqlsh, String status) {
        
        if (StringUtils.isNotBlank(status)) {
            redisService.set(ddqqlsh, status, Constant.REDIS_EXPIRE_TIME_DEFAULT);
        } else {
            redisService.del(ddqqlsh);
        }
        
    }
    
    
}
