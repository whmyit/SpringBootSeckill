package com.dxhy.order.consumer.modules.fiscal.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.invoice.protocol.sk.doto.request.SkReqYhzxxcx;
import com.dxhy.invoice.protocol.sk.doto.response.Kcmx;
import com.dxhy.invoice.protocol.sl.fp.FpKpd;
import com.dxhy.invoice.protocol.sl.fp.KccxResponse;
import com.dxhy.invoice.protocol.sl.fp.YhzxxcxResponse;
import com.dxhy.invoice.protocol.sl.sld.*;
import com.dxhy.invoice.service.sl.FpQueryService;
import com.dxhy.invoice.service.sl.SldManagerService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.fiscal.service.a9.SldManagerServiceA9;
import com.dxhy.order.consumer.modules.fiscal.service.bwactivexs.SldManagerServiceBw;
import com.dxhy.order.consumer.openapi.service.FangGeInterfaceService;
import com.dxhy.order.model.a9.dy.DydListRequst;
import com.dxhy.order.model.a9.dy.DydResponse;
import com.dxhy.order.model.a9.dy.DydResponseDetail;
import com.dxhy.order.model.a9.dy.DydResponseExtend;
import com.dxhy.order.model.a9.query.YdhzxxRequest;
import com.dxhy.order.model.a9.query.YhzxxResponse;
import com.dxhy.order.model.a9.sld.JspxxResponse;
import com.dxhy.order.model.a9.sld.SearchFjh;
import com.dxhy.order.model.a9.sld.SldKcmx;
import com.dxhy.order.model.a9.sld.*;
import com.dxhy.order.model.bwactivexs.dy.DydListRequest;
import com.dxhy.order.model.bwactivexs.dy.DydListResponse;
import com.dxhy.order.model.bwactivexs.dy.DydXx;
import com.dxhy.order.model.bwactivexs.server.SkServerRequest;
import com.dxhy.order.model.bwactivexs.server.SkServerResponse;
import com.dxhy.order.model.entity.InvoiceQuotaEntity;
import com.dxhy.order.model.fg.FgJspxxReqEntity;
import com.dxhy.order.model.fg.SpFpXeDto;
import com.dxhy.order.model.fg.SpFpXeMxDto;
import com.dxhy.order.model.newtax.NsrXnsbxx;
import com.dxhy.order.model.newtax.NsrXnsbxxs;
import com.dxhy.order.model.ukey.QueryJqbh;
import com.dxhy.order.model.ukey.QueryJqbhList;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.HttpInvoiceRequestUtilFg;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 *
 * @ClassName ：UnifyServiceImpl
 * @Description ：税控相关接口统一调用
 * @author ：杨士勇
 * @date ：2019年6月25日 上午10:23:51
 *
 *
 */

@Service
@Slf4j
public class UnifyServiceImpl implements UnifyService {
	private final static String LOGGER_MSG = "(税控统一调用业务层)";
	
	@Reference
	private SldManagerService sldManagerService;
	
	@Resource
	private SldManagerServiceA9 sldManagerServiceA9;
	
	@Resource
	private SldManagerServiceBw sldManagerServiceBw;
	
	@Reference
	private FpQueryService fpQueryService;
	
	@Reference
	private RedisService redisService;
	
	@Resource
	private FangGeInterfaceService fangGeInterfaceService;
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	
	@Override
	public InvoiceQuotaEntity queryInvoiceQuotaInfoFromRedis(String nsrsbh, String fpzlDm, String terminalCode) {
		
		if (!OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
			log.info("查询发票限额的接口，入参:{},{},{}", nsrsbh, fpzlDm, terminalCode);
			String cacheKey = String.format(Constant.REDIS_INVOICE_QUO_PREFIX, nsrsbh, fpzlDm, terminalCode);
			log.debug("redis中缓存的限额的key:{}", cacheKey);
			InvoiceQuotaEntity invoiceQuotaEntity = redisService.get(cacheKey, InvoiceQuotaEntity.class);
			log.debug("redis中缓存的限额信息:{}", JsonUtils.getInstance().toJsonString(invoiceQuotaEntity));
			if (invoiceQuotaEntity == null || StringUtils.isBlank(invoiceQuotaEntity.getInvoiceAmount())) {
				// 如果缓存中的限额信息为空 重新查询 缓存数据到redis
				/**
				 * 调用金税盘信息查询接口,获取限额,然后缓存税号对应票种的限额,返回当前票种对应的限额,
				 */
				JspxxResponse queryJspxxV2 = new JspxxResponse();
				if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
					//todo fangge
					/**
					 * 方格百望和航信
					 */
					String jspxx = ConfigureConstant.STRING_0;
					if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode)) {
						log.info("查询税号为{}的本地{}税盘信息限额", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGBW.getValue());
						jspxx = ConfigureConstant.STRING_1;
					} else if (OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)) {
						log.info("查询税号为{}的本地{}税盘信息限额", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGHX.getValue());
						jspxx = ConfigureConstant.STRING_0;
					} else {
						log.info("查询税号为{}的本地{}税盘信息限额", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getValue());
						jspxx = ConfigureConstant.STRING_2;
						
					}
					FgJspxxReqEntity fgJspxxReqEntity = new FgJspxxReqEntity();
					fgJspxxReqEntity.setNsrsbh(nsrsbh);
					fgJspxxReqEntity.setJsplx(jspxx);
					fgJspxxReqEntity.setFpzlDm(fpzlDm);
					SpFpXeDto spFpXeDto = HttpInvoiceRequestUtilFg.querySpZlXeByNsrsbh(OpenApiConfig.getXeFg, fgJspxxReqEntity,terminalCode);
					queryJspxxV2.setStatusCode(spFpXeDto.getCode());
					queryJspxxV2.setStatusMessage(spFpXeDto.getMsg());
					List<Jspxxcx> list = new ArrayList<>();
					if (!ObjectUtils.isEmpty(spFpXeDto)) {
						SpFpXeMxDto spFpXeMxDto = spFpXeDto.getData();
						Jspxxcx jspxxcx = new Jspxxcx();
						jspxxcx.setNsrsbh(nsrsbh);
						jspxxcx.setDzkpxe(spFpXeMxDto.getDzkpxe());
						jspxxcx.setFpzlDm(fpzlDm);
						list.add(jspxxcx);
					}
					queryJspxxV2.setJspxxs(list);
				} else {
					queryJspxxV2 = HttpInvoiceRequestUtil.queryNsrpzKpxe(OpenApiConfig.queryNsrpzKpxe, nsrsbh, fpzlDm, terminalCode);
				}
				if (queryJspxxV2 != null && !CollectionUtils.isEmpty(queryJspxxV2.getJspxxs()) && Constant.SUCCSSCODE.equals(queryJspxxV2.getStatusCode())) {
					for (Jspxxcx jspxxcx : queryJspxxV2.getJspxxs()) {
						if (StringUtils.isBlank(jspxxcx.getDzkpxe())) {
							log.error("限额查询失败,税号为:{},种类为:{},设备类型为:{}", nsrsbh, jspxxcx.getFpzlDm(), terminalCode);
							return null;
						}
						
						InvoiceQuotaEntity invoiceQuotaEntity1 = new InvoiceQuotaEntity();
						invoiceQuotaEntity1.setInvoiceAmount(jspxxcx.getDzkpxe());
						invoiceQuotaEntity1.setInvoiceType(jspxxcx.getFpzlDm());
						invoiceQuotaEntity1.setTaxpayerCode(nsrsbh);
						
						boolean cacheToRedis = redisService.set(String.format(Constant.REDIS_INVOICE_QUO_PREFIX, nsrsbh, jspxxcx.getFpzlDm(), terminalCode), invoiceQuotaEntity1, 60 * 60);
						if (!cacheToRedis) {
							log.warn("单张开票限额到redis失败,税号:{},票种为:{}", nsrsbh, jspxxcx.getFpzlDm());
						}

						/**
						 * 返回当前票种的限额数据
						 */
						if (fpzlDm.equals(jspxxcx.getFpzlDm())) {
							invoiceQuotaEntity = invoiceQuotaEntity1;
							if (log.isDebugEnabled()) {
								log.debug("当前数据查询的限额信息为:{}", JsonUtils.getInstance().toJsonString(invoiceQuotaEntity));
							}
						}
					}
				}
				
			}
			log.info("查询发票限额的接口，出参：{}", JsonUtils.getInstance().toJsonString(invoiceQuotaEntity));
			return invoiceQuotaEntity;
		} else {
			InvoiceQuotaEntity entity = new InvoiceQuotaEntity();
			entity.setInvoiceAmount("1000000000");
			return entity;
		}
	}
	
	/**
	 * 获取分机号信息
	 *
	 * @param shList
	 * @param fjh
	 * @return
	 */
	@Override
	public Set<SearchFjh> getFjh(List<String> shList, String fjh) {
		
		//根据纳税人识别号和分机号查询发票种类代码
		
		Set<SearchFjh> resultList = new HashSet<>();
		for (String nsrsbh : shList) {
			try {
				
				
				com.dxhy.order.model.c48.sld.KpdSpglResponse kpdSpglResponse = new com.dxhy.order.model.c48.sld.KpdSpglResponse();
				KpdSpglRequest kpdSpglRequest = new KpdSpglRequest();
				Nsrsbh[] nsrsbhs = new Nsrsbh[1];
				Nsrsbh nsrsbh1 = new Nsrsbh();
				nsrsbh1.setNsrsbh(nsrsbh);
				nsrsbhs[0] = nsrsbh1;
				
				kpdSpglRequest.setNsrsbhs(nsrsbhs);
				kpdSpglRequest.setFjh(StringUtils.isBlank(fjh) ? "" : fjh);
				// TODO: 2019/7/24 后期需要处理,目前默认传递分页为第一页,并且设置页数为100
				kpdSpglRequest.setPageNo(ConfigureConstant.STRING_1);
				kpdSpglRequest.setPageSize(ConfigureConstant.STRING_100);
				//启动状态
				kpdSpglRequest.setQyzt(ConfigureConstant.STRING_1);
				
				
				//查询税控设备
				String terminalCode = apiTaxEquipmentService.getTerminalCode(nsrsbh);
				String url = "";
				if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
					url = OpenApiConfig.querySpBw;
				} else if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
					url = "";
				} else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
					url = OpenApiConfig.queryJqbh;
				} else {
					url = OpenApiConfig.getSearchFjhFpzlDm;
				}
				if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
					com.dxhy.invoice.protocol.sl.sld.KpdSpglResponse kpdSpglResponse1 = sldManagerService.queryKpdList(kpdSpglRequest);
					if (kpdSpglResponse1 != null && OrderInfoContentEnum.SUCCESS.getKey().equals(kpdSpglResponse1.getStatusCode())) {
						kpdSpglResponse.setStatusCode(kpdSpglResponse1.getStatusCode());
						List<com.dxhy.order.model.c48.sld.FpKpd> fpKpdList = new ArrayList<>();
						for (FpKpd fpkpd : kpdSpglResponse1.getFpkpds()) {
							com.dxhy.order.model.c48.sld.FpKpd fpKpd = new com.dxhy.order.model.c48.sld.FpKpd();
							fpKpd.setKpdid(fpkpd.getKpdid());
							fpKpd.setNsrsbh(fpkpd.getNsrsbh());
							fpKpd.setFjh(fpkpd.getFjh());
							fpKpd.setJqbh(fpkpd.getJqbh());
							fpKpd.setKpdmc(fpkpd.getKpdmc());
							fpKpd.setCjr(fpkpd.getCjr());
							fpKpd.setBz(fpkpd.getBz());
							fpKpd.setQyzt(fpkpd.getQyzt());
							fpKpd.setCjsj(fpkpd.getCjsj());
							fpKpd.setGxsj(fpkpd.getGxsj());
							fpKpd.setCpyzt(fpkpd.getCpyzt());
							fpKpdList.add(fpKpd);
							
						}
						kpdSpglResponse.setFpkpds(fpKpdList);
						if (OrderInfoContentEnum.SUCCESS.getKey().equals(kpdSpglResponse.getStatusCode())) {
							if (org.apache.commons.collections.CollectionUtils.isNotEmpty(kpdSpglResponse.getFpkpds())) {
								convertToSearchFjh(resultList, kpdSpglResponse.getFpkpds(), terminalCode);
							}
						}
					}
				} else if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
					/**
					 * 根据税号查询税盘信息
					 */
					Map<String, Object> map = new HashMap<>(2);
					map.put("nsrsbh", nsrsbh);
					String jsplx = ConfigureConstant.STRING_0;
					if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(terminalCode)) {
						log.info("查询税号为{}的本地{}税盘信息", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGBW.getValue());
						jsplx = ConfigureConstant.STRING_1;
					} else if (OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(terminalCode)) {
						log.info("查询税号为{}的本地{}税盘信息", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGHX.getValue());
						jsplx = ConfigureConstant.STRING_0;
					} else {
						log.info("查询税号为{}的本地{}税盘信息", nsrsbh, OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getValue());
						jsplx = ConfigureConstant.STRING_2;
					}
					FgJspxxReqEntity fgJspxxReqEntity = new FgJspxxReqEntity();
					fgJspxxReqEntity.setNsrsbh(nsrsbh);
					fgJspxxReqEntity.setJsplx(jsplx);
					SearchSldResponse searchSldResponse = HttpInvoiceRequestUtilFg.querySpByNsrsbh(OpenApiConfig.getSpxxFg, fgJspxxReqEntity, terminalCode);
					if (OrderInfoContentEnum.SUCCESS.getKey().equals(searchSldResponse.getStatusCode())) {
						List<SearchSld> searchSlds = searchSldResponse.getSlds();
						convertToFgFjh(resultList, searchSlds, searchSldResponse.getFpzlDm(), terminalCode);
					}
					
				} else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
					
					SldRequest sldRequest = new SldRequest();
					sldRequest.setNsrsbh(nsrsbh);
					sldRequest.setFjh(fjh);
					sldRequest.setFpzldm("");
					QueryJqbhList queryJqbhList = HttpInvoiceRequestUtil.queryFjh(url, sldRequest, terminalCode);
					if (ObjectUtil.isNotEmpty(queryJqbhList)) {
						convertToUkeyFjh(resultList, queryJqbhList.getJqbhs(), null, terminalCode);
					}
					
					
				} else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
					
					/**
					 * 新税控虚拟设备信息查询
					 */
					SldRequest sldRequest = new SldRequest();
					sldRequest.setNsrsbh(nsrsbh);
					NsrXnsbxxs nsrXnsbxxs = HttpInvoiceRequestUtil.queryNsrXnsbxx(OpenApiConfig.queryNsrXnsbxx, sldRequest, terminalCode);
					if (ObjectUtil.isNotEmpty(nsrXnsbxxs)) {
						convertToNewTaxFjh(resultList, nsrXnsbxxs.getContent(), nsrsbh, terminalCode);
					}
					
					
				} else {
					com.dxhy.order.model.c48.sld.KpdSpglRequest kpdSpglRequest1 = new com.dxhy.order.model.c48.sld.KpdSpglRequest();
					kpdSpglRequest1.setFjh(kpdSpglRequest.getFjh());
					kpdSpglRequest1.setPageNo(kpdSpglRequest.getPageNo());
					kpdSpglRequest1.setPageSize(kpdSpglRequest.getPageSize());
					List<String> newShList = NsrsbhUtils.transShListByNsrsbh(nsrsbh);
					kpdSpglRequest1.setNsrsbhs(NsrsbhUtils.getC48Nsrsbhs(newShList));
					kpdSpglResponse = HttpInvoiceRequestUtil.getFjh(url, kpdSpglRequest1, terminalCode);
					if (kpdSpglResponse != null && OrderInfoContentEnum.SUCCESS.getKey().equals(kpdSpglResponse.getStatusCode())) {
						if (org.apache.commons.collections.CollectionUtils.isNotEmpty(kpdSpglResponse.getFpkpds())) {
							convertToSearchFjh(resultList, kpdSpglResponse.getFpkpds(), terminalCode);
						}
					}
				}
			} catch (Exception e) {
				log.error("{}获取开票点失败:{}", LOGGER_MSG, e);
			}
		}
		
		
		return resultList;
		
	}
	
	/**
	 * 查询受理点金税盘信息 c48部分库存信息是根据金税盘信息查询
	 */
	@Override
	public SldJspxxResponse querSldFpfs(SldJspxxRequest sldJspxxRequest, String terminalCode) {
		
		// 根据缓存的key去redis中获取限额信息
		SldJspxxResponse sldJspxxResponse;
		// 判断redis中是否缓存发票限额信息
		
		if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
			
			SldKcRequest kccxRequest = new SldKcRequest();
			kccxRequest.setFpzldm(sldJspxxRequest.getFpzldm());
			kccxRequest.setNsrsbh(sldJspxxRequest.getNsrsbh());
			kccxRequest.setSldid(sldJspxxRequest.getSldid());
			kccxRequest.setTerminalCode(terminalCode);
			SldKcResponse sldKcResponse = HttpInvoiceRequestUtil.querySldFpfs(OpenApiConfig.querySldFpfs, kccxRequest, terminalCode);
			sldJspxxResponse = convertToSldJspxxResponse(sldKcResponse, sldJspxxRequest.getSldid());
		} else {
	        log.debug("C48 查询受理点金税盘信息的接口,入参:{}", JsonUtils.getInstance().toJsonString(sldJspxxRequest));
	        sldJspxxResponse = sldManagerService.selectSldJspxx(sldJspxxRequest);
	        log.debug("C48 查询受理点金税盘信息的接口,出参:{}", JsonUtils.getInstance().toJsonString(sldJspxxResponse));
	
        }

		return sldJspxxResponse;
	}

    /**
     * bean转换
     */
    private SldJspxxResponse convertToSldJspxxResponse(SldKcResponse queryKcxx, String sldid) {
        SldJspxxResponse response = new SldJspxxResponse();
    
        if (OrderInfoContentEnum.SUCCESS.getKey().equals(queryKcxx.getCode())) {
            if (queryKcxx.getResult() != null) {
                response.setStatusCode(StringUtils.isBlank(queryKcxx.getResult().getStatusCode()) ? queryKcxx.getCode() : queryKcxx.getResult().getStatusCode());
                response.setStatusMessage(StringUtils.isBlank(queryKcxx.getResult().getStatusMessage()) ? queryKcxx.getMsg() : queryKcxx.getResult().getStatusMessage());
            
                if (CollectionUtils.isNotEmpty(queryKcxx.getResult().getKcmxes())) {
	                List<SldJspxx> sldJspxxList = new ArrayList<>();
                    for (SldKcmx kcmx : queryKcxx.getResult().getKcmxes()) {
                        SldJspxx sldJspxx = new SldJspxx();
                        sldJspxx.setFpfs(Integer.valueOf(kcmx.getFpfs()));
                        sldJspxx.setSldid(Integer.valueOf(sldid));
                        sldJspxxList.add(sldJspxx);
                    }
                    response.setSldJspxxList(sldJspxxList);
                }
                
            } else {
                response.setStatusCode(queryKcxx.getCode());
                response.setStatusMessage(queryKcxx.getMsg());
            }
        } else {
            response.setStatusCode(queryKcxx.getCode());
            response.setStatusMessage(queryKcxx.getMsg());
        }
        return response;
	}
	
	/**
	 * bean转换
	 */
	
	private KccxResponse convertToKccxResponse(SldKcByFjhResponse queryKcxxByFjh) {
		KccxResponse response = new KccxResponse();
		if (OrderInfoContentEnum.SUCCESS.getKey().equals(queryKcxxByFjh.getCode())) {
			if (queryKcxxByFjh.getResult() != null) {
				response.setStatusCode(queryKcxxByFjh.getCode());
				response.setStatusMessage(queryKcxxByFjh.getMsg());
				if(CollectionUtils.isNotEmpty(queryKcxxByFjh.getResult().getKcmxes())) {
					List<Kcmx> kcmxes = new ArrayList<>();
					for (SldKcmxByFjh sldKcmxByFjh : queryKcxxByFjh.getResult().getKcmxes()) {
						Kcmx kcmx = new Kcmx();
						kcmx.setFjh(sldKcmxByFjh.getFjh());
						kcmx.setFpdm(sldKcmxByFjh.getFpdm());
						kcmx.setFpfs(sldKcmxByFjh.getFpfs());
						kcmx.setFpzlDm(sldKcmxByFjh.getFpzlDm());
						kcmx.setNsrsbh(sldKcmxByFjh.getNsrsbh());
						kcmx.setQshm(sldKcmxByFjh.getQshm());
						kcmx.setZdh(sldKcmxByFjh.getZdh());
						kcmxes.add(kcmx);
					}
					response.setKcmxes(kcmxes);
				}
			}else{
				response.setStatusCode(queryKcxxByFjh.getCode());
				response.setStatusMessage(queryKcxxByFjh.getMsg());
			}
		}else{
			response.setStatusCode(queryKcxxByFjh.getCode());
			response.setStatusMessage(queryKcxxByFjh.getMsg());
		}
		return response;
	}


	/**
     * 查询月度汇总信息
     */
	@Override
    public YhzxxResponse queryYhzxx(SkReqYhzxxcx paramSkReqYhzxxcx, String terminalCode) {
        YhzxxResponse response = new YhzxxResponse();
		if(!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)){
			YdhzxxRequest request = new YdhzxxRequest();
			if(!OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)){
                request.setFjh(paramSkReqYhzxxcx.getFjh());
			}else {
                request.setXnsbh(paramSkReqYhzxxcx.getFjh());
            }
			request.setXhfNsrsbh(paramSkReqYhzxxcx.getNsrsbh());
			request.setMonth(paramSkReqYhzxxcx.getSsyf().substring(4));
			request.setYear(paramSkReqYhzxxcx.getSsyf().substring(0, 4));
			request.setTerminalCode(terminalCode);
			request.setFpzlDm(paramSkReqYhzxxcx.getFpzlDm());
			response = sldManagerServiceA9.queryYhzxx(request);
		}else{
			log.debug("C48 发票汇总表统计的接口,入参:{}", JsonUtils.getInstance().toJsonString(paramSkReqYhzxxcx));
			YhzxxcxResponse queryYhzxx = fpQueryService.queryYhzxx(paramSkReqYhzxxcx);
			log.debug("C48 发票汇总表统计的接口,出参:{}", JsonUtils.getInstance().toJsonString(queryYhzxx));
		}
		return response;
	}
	
	/**
	 * 查询打印点
	 */
	@Override
    public DydResponseExtend queryDydxxcxList(DydxxcxRequest dydxxcxRequest, String terminalCode) {
		DydResponseExtend response = new DydResponseExtend();
		if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
			if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
				DydListRequest request = new DydListRequest();
				List<String> shList = new ArrayList<>();
				shList.add(dydxxcxRequest.getNsrsbhs()[0].getNsrsbh());
				request.setNsrsbhs(shList);
				request.setTerminalCode(terminalCode);
				DydListResponse queryDydXxList = sldManagerServiceBw.queryDydXxList(request);
				response = convetToDyResponseExtend(queryDydXxList);
			} else {
				DydListRequst dyRequest = new DydListRequst();
				dyRequest.setNsrsbh(dydxxcxRequest.getNsrsbhs()[0].getNsrsbh());
				dyRequest.setDyjzt(dydxxcxRequest.getDydzt());
				dyRequest.setTerminalCode(terminalCode);
				DydResponse dyResponse = sldManagerServiceA9.queryDydxxcxList(dyRequest);
				response = dyResponse.getResult();
			}
			
		} else {
			log.debug("c48  查询打印点的接口，入参:{}", JsonUtils.getInstance().toJsonString(dydxxcxRequest));
			DydxxcxResponse dydxxcxResponse = sldManagerService.queryDydxxcxList(dydxxcxRequest);
			log.debug("c48  查询打印点的接口，出参:{}", JsonUtils.getInstance().toJsonString(dydxxcxResponse));
			response = convertToDyResponseExtend(dydxxcxResponse);
		}
		return response;
	}
	
	
	/**
	 * bean转换
	 */
	private DydResponseExtend convetToDyResponseExtend(DydListResponse queryDydXxList) {
		DydResponseExtend response = new DydResponseExtend();
		
		response.setStatusCode(queryDydXxList.getCode());
		response.setStatusMessage(queryDydXxList.getMsg());
		if(OrderInfoContentEnum.SUCCESS.getKey().equals(queryDydXxList.getCode())){
			if(queryDydXxList.getContent() != null){
				
				if(CollectionUtils.isNotEmpty(queryDydXxList.getContent().getRecords())) {
					List<DydResponseDetail> fpdyjs = new ArrayList<>();
					for (DydXx dyDxx : queryDydXxList.getContent().getRecords()) {
						DydResponseDetail detail = new DydResponseDetail();
						detail.setServerId(dyDxx.getId());
						detail.setDyjmc(dyDxx.getMc());
						detail.setDisUp(dyDxx.getSbj());
						detail.setDyjid(dyDxx.getId());
						detail.setDisRight(dyDxx.getZbj());
						detail.setDyjzt(dyDxx.getQybz());
						detail.setNsrmc(dyDxx.getNsrmc());
						detail.setNsrsbh(dyDxx.getNsrsbh());
						fpdyjs.add(detail);
					}
					response.setFpdyjs(fpdyjs);
				}
			}
			
		}
		return response;
	}


	/**
	 * bean转换
	 */
    private DydResponseExtend convertToDyResponseExtend(DydxxcxResponse dyResponse) {
        DydResponseExtend response = new DydResponseExtend();
        response.setStatusCode(dyResponse.getStatusCode());
        response.setStatusMessage(dyResponse.getStatusMessage());
        if (dyResponse.getFpdydxxList() == null) {
    
        } else {
            
            if (!CollectionUtils.isEmpty(dyResponse.getFpdydxxList())) {
                List<DydResponseDetail> fpdydxxList = new ArrayList<>();
                for (Fpdydxx dyDetail : dyResponse.getFpdydxxList()) {
                    DydResponseDetail dyd = new DydResponseDetail();
                    dyd.setDyjid(String.valueOf(dyDetail.getDydid()));
                    dyd.setDyjmc(dyDetail.getDydmc());
                    dyd.setSpotKey(dyDetail.getSpotKey());
					dyd.setNsrsbh(dyDetail.getNsrsbh());
                    dyd.setDyjzt(dyDetail.getDydzt());
                    dyd.setZxzt(dyDetail.getZxzt());
                    dyd.setServerId(String.valueOf(dyDetail.getServerId()));
                    dyd.setServerName(dyDetail.getServerName());
					fpdydxxList.add(dyd);
				}
                response.setFpdyjs(fpdydxxList);
			}
			
		}
		return response;
	}


    /**
     * 根据受理点id和纳税人识别号查询服务器ip端口信息
     */
    @Override
    public SkServerResponse queryServerInfo(SkServerRequest request, String terminalCode) {
	    SkServerResponse response = new SkServerResponse();
	    if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
		    response = sldManagerServiceBw.queryServerInfo(request);
	    } else {
		    response.setCode(OrderInfoContentEnum.RECEIVE_FAILD.getKey());
		    response.setMsg("税控设备类型错误!");
	    }
	    return response;
    }
	
	
	/**
	 * bean转换
	 */
	private void convertToSearchFjh(Set<SearchFjh> searchFjhSet, List<com.dxhy.order.model.c48.sld.FpKpd> fpkpds, String terminalCode) {
		for (com.dxhy.order.model.c48.sld.FpKpd fpKpd : fpkpds) {
			SearchFjh search = new SearchFjh();
			search.setFjh(fpKpd.getFjh());
			search.setKpdId(String.valueOf(fpKpd.getKpdid()));
			search.setKpdMc(fpKpd.getKpdmc());
			search.setNsrsbh(fpKpd.getNsrsbh());
			search.setTerminalCode(terminalCode);
			searchFjhSet.add(search);
		}
	}
	
	
	/**
	 * bean转换
	 */
	
	public static void convertToFgFjh(Set<com.dxhy.order.model.a9.sld.SearchFjh> searchFjhSet, List<SearchSld> searchSlds, String fpzldm, String terminalCode) {
		for (SearchSld fpKpd : searchSlds) {
			com.dxhy.order.model.a9.sld.SearchFjh search = new com.dxhy.order.model.a9.sld.SearchFjh();
			search.setKpdMc(fpKpd.getSldMc());
			search.setKpdId(fpKpd.getJqbh());
			search.setNsrsbh(fpKpd.getNsrsbh());
			search.setFjh(fpKpd.getFjh());
			search.setFpzlDms(fpzldm);
			search.setTerminalCode(terminalCode);
			searchFjhSet.add(search);
		}
	}
	
	/**
	 * bean转换
	 */
	
	public static void convertToUkeyFjh(Set<com.dxhy.order.model.a9.sld.SearchFjh> searchFjhSet, List<QueryJqbh> queryJqbhs, String fpzldm, String terminalCode) {
		for (QueryJqbh queryJqbh : queryJqbhs) {
			com.dxhy.order.model.a9.sld.SearchFjh search = new com.dxhy.order.model.a9.sld.SearchFjh();
			search.setKpdMc(queryJqbh.getMc());
			search.setKpdId(queryJqbh.getJqbh());
			search.setNsrsbh(queryJqbh.getNsrsbh());
			search.setFjh(queryJqbh.getJqbh());
			search.setFpzlDms(fpzldm);
			search.setTerminalCode(terminalCode);
			searchFjhSet.add(search);
		}
	}
	
	
	public static void convertToNewTaxFjh(Set<com.dxhy.order.model.a9.sld.SearchFjh> searchFjhSet, List<NsrXnsbxx> nsrXnsbxxes, String nsrsbh, String terminalCode) {
		for (NsrXnsbxx nsrXnsbxx : nsrXnsbxxes) {
			com.dxhy.order.model.a9.sld.SearchFjh search = new com.dxhy.order.model.a9.sld.SearchFjh();
			search.setKpdMc(nsrXnsbxx.getXnsbh());
			search.setKpdId(nsrXnsbxx.getXnsbh());
			search.setNsrsbh(nsrsbh);
			search.setFjh(nsrXnsbxx.getXnsbh());
			search.setTerminalCode(terminalCode);
			searchFjhSet.add(search);
		}
	}
	
}
