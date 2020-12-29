package com.dxhy.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.*;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.NsrQueueEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.OrderInfoMapper;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.dao.OrderItemInfoMapper;
import com.dxhy.order.dao.OrderQrcodeExtendInfoMapper;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.card.Fpkj;
import com.dxhy.order.model.card.Fpkjxx;
import com.dxhy.order.model.card.InsertCardRequest;
import com.dxhy.order.model.message.GlobalInfo;
import com.dxhy.order.model.message.OpenApiResponse;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.service.OpenApiService;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author ：杨士勇
 * @ClassName ：InsertCardServiceImpl
 * @Description ：
 * @date ：2020年4月8日 下午4:22:25
 */
@Service
@Slf4j
public class InsertCardServiceImpl implements ApiInsertCardService {
	
	private static final String LOGGER_MSG = "(插卡业务)";
	
	@Resource
	OrderInvoiceInfoMapper orderInvoiceInfoMapper;
	
	@Resource
	OrderInfoMapper orderInfoMapper;
	
	@Resource
	OrderItemInfoMapper orderItemInfoMapper;
	
	@Resource
	ApiQuickCodeInfoService quickCodeInfoService;
	
	@Resource
	ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Resource
	OrderQrcodeExtendInfoMapper orderQrcodeExtendInfoMapper;
	
	@Resource
	IRabbitMqSendMessage iRabbitMqSendMessage;
	
	@Resource
	private OpenApiService openApiService;
	
	@Resource
	private ApiInvoiceCommonService apiInvoiceCommonService;
	
	@Resource
	private ApiHistoryDataPdfService apiHistoryDataPdfService;
	
	@Override
	public void insertCard(String message) {
		
		log.info("插卡接口入参:{}", message);
		
		InvoicePush invoicePush = JsonUtils.getInstance().parseObject(message, InvoicePush.class);
		
		List<String> shList = new ArrayList<>();
		shList.add(invoicePush.getNSRSBH());
		
		OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
		/**
		 * 入参前赋值为发票请求流水号,不是开票流水号
		 */
		orderInvoiceInfo1.setFpqqlsh(invoicePush.getFPQQLSH());
		OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
		
		OrderInfo orderInfo = orderInfoMapper
                .selectOrderInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
        
        List<OrderItemInfo> selectOrderItemInfoByOrderId = orderItemInfoMapper
                .selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
        
        OrderQrcodeExtendInfo orderQrcodeExtendInfo = new OrderQrcodeExtendInfo();
        orderQrcodeExtendInfo.setOrderInfoId(orderInfo.getId());
        OrderQrcodeExtendInfo selectByOrderQrcodeExtendInfo = orderQrcodeExtendInfoMapper
                .selectByOrderQrcodeExtendInfo(orderQrcodeExtendInfo, shList);
		
		InsertCardRequest insertCardRequest = buildInsertCardRequest(invoicePush, orderInvoiceInfo,
				orderInfo, selectOrderItemInfoByOrderId, selectByOrderQrcodeExtendInfo);
		
		//获取配置的appid
		EwmGzhConfig ewmGzhConfig = quickCodeInfoService.queryGzhEwmConfig(orderInfo.getXhfNsrsbh());
		if (ewmGzhConfig != null) {
			insertCardRequest.getFpxx_base().setAppid(ewmGzhConfig.getAppid());
		}
		
		log.info("插卡接口content内容:{}", JsonUtils.getInstance().toJsonString(insertCardRequest));
		
		GlobalInfo globalInfo = new GlobalInfo();
		globalInfo.setEncryptCode("0");
		globalInfo.setZipCode("0");
		globalInfo.setDataExchangeId(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
		
		globalInfo.setContent(Base64Encoding.encodeToString(JsonUtils.getInstance().toJsonString(insertCardRequest).getBytes(StandardCharsets.UTF_8)));
		log.info("插卡接口入参:{}", JsonUtils.getInstance().toJsonString(insertCardRequest));
		
		OpenApiResponse openApiResponse = openApiService.sendRequest(globalInfo, OpenApiConfig.insertCardUrl);
		
		if (ConfigureConstant.STRING_0000.equals(openApiResponse.getReturnStateInfo().getReturnCode())) {
			log.info("插卡成功,授权订单号:{}", selectByOrderQrcodeExtendInfo.getAuthOrderId());
			//更新插卡状态
			OrderQrcodeExtendInfo updateOrderQrcodeInfo = new OrderQrcodeExtendInfo();
			updateOrderQrcodeInfo.setId(selectByOrderQrcodeExtendInfo.getId());
			updateOrderQrcodeInfo.setCardStatus("1");
			updateOrderQrcodeInfo.setXhfNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
			orderQrcodeExtendInfoMapper.updateByPrimaryKeySelective(updateOrderQrcodeInfo, shList);
		} else {
			String msg = Base64Encoding.decodeToString(String.valueOf(openApiResponse.getReturnStateInfo().getReturnMessage()));
			log.info("插卡失败,授权订单号:{},错误信息:{}", selectByOrderQrcodeExtendInfo.getAuthOrderId(), msg);
            OrderQrcodeExtendInfo updateOrderQrcodeInfo = new OrderQrcodeExtendInfo();
            updateOrderQrcodeInfo.setId(selectByOrderQrcodeExtendInfo.getId());
            updateOrderQrcodeInfo.setXhfNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
            updateOrderQrcodeInfo.setCardStatus("2");
            orderQrcodeExtendInfoMapper.updateByPrimaryKeySelective(updateOrderQrcodeInfo, shList);
        }
    }
	
	private InsertCardRequest buildInsertCardRequest(InvoicePush invoicePush, OrderInvoiceInfo orderInvoiceInfo,
	                                                 OrderInfo selectOrderInfoByOrderId, List<OrderItemInfo> selectOrderItemInfoByOrderId,
	                                                 OrderQrcodeExtendInfo selectByOrderQrcodeExtendInfo) {
		InsertCardRequest insertCardRequest = new InsertCardRequest();
		Fpkj fpkj = new Fpkj();
		// 生成20位的请求流水号
		fpkj.setFpqqlsh(apiInvoiceCommonService.getGenerateShotKey());
		fpkj.setDdh(orderInvoiceInfo.getDdh());
		fpkj.setFp_dm(orderInvoiceInfo.getFpdm());
		fpkj.setFp_hm(orderInvoiceInfo.getFphm());
		fpkj.setJym(orderInvoiceInfo.getJym());
		
		String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInvoiceInfo.getXhfNsrsbh());
		String pdfFile = "";
		/**
		 * 方格UKey的电票调用monggodb获取数据
		 */
		if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
			HistoryDataPdfEntity historyDataPdfEntity = apiHistoryDataPdfService.find(orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), NsrsbhUtils.transShListByNsrsbh(orderInvoiceInfo.getXhfNsrsbh()));
			if (Objects.nonNull(historyDataPdfEntity)) {
				pdfFile = historyDataPdfEntity.getPdfFileData();
			}
		} else {
			GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(invoicePush.getFPQQPCH(), invoicePush.getNSRSBH(), terminalCode, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getPdfUrl());
			
			GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
			pdfFile = pdf.getResponse_EINVOICE_PDF().get(0).getPDF_FILE();
		}
		
		
		// 获取pdf
		fpkj.setPdf(pdfFile);
		fpkj.setNsrsbh(orderInvoiceInfo.getXhfNsrsbh());
		fpkj.setNsrmc(selectOrderInfoByOrderId.getXhfMc());
		fpkj.setNsrdzdah(selectOrderInfoByOrderId.getNsrdzdah());
		fpkj.setKpxm(selectOrderInfoByOrderId.getKpxm());
		fpkj.setBmb_bbh(selectOrderInfoByOrderId.getBbmBbh());
		fpkj.setXhf_dz(selectOrderInfoByOrderId.getXhfDz());
		fpkj.setXhf_dh(selectOrderInfoByOrderId.getXhfDh());
        fpkj.setXhf_yhzh(selectOrderInfoByOrderId.getXhfYh());
        fpkj.setGhfmc(selectOrderInfoByOrderId.getGhfMc());
        fpkj.setGhf_nsrsbh(selectOrderInfoByOrderId.getGhfNsrsbh());
		fpkj.setGhf_dz(selectOrderInfoByOrderId.getGhfDz());
		fpkj.setGhf_dh(selectOrderInfoByOrderId.getGhfDh());
		fpkj.setGhf_email(selectOrderInfoByOrderId.getGhfEmail());
		fpkj.setGhf_yhzh(
				(StringUtils.isBlank(selectOrderInfoByOrderId.getGhfYh()) ? "" : selectOrderInfoByOrderId.getGhfYh())
						+ (StringUtils.isBlank(selectOrderInfoByOrderId.getGhfZh()) ? ""
								: selectOrderInfoByOrderId.getGhfZh()));
		fpkj.setGmf_sf(selectOrderInfoByOrderId.getGhfSj());
		fpkj.setJqbh(orderInvoiceInfo.getJqbh());
		fpkj.setFjh(orderInvoiceInfo.getFjh());
		fpkj.setVersion("");
	    fpkj.setSwjg_dm(selectOrderInfoByOrderId.getSwjgDm());
	    fpkj.setDkbz(selectOrderInfoByOrderId.getDkbz());
	    fpkj.setSgbz("");
	    fpkj.setKphjje(selectOrderInfoByOrderId.getKphjje());
	    fpkj.setHjbhsje(selectOrderInfoByOrderId.getHjbhsje());
	    fpkj.setKphjse(selectOrderInfoByOrderId.getHjse());
	    fpkj.setKpy(selectOrderInfoByOrderId.getKpr());
	    fpkj.setSky(selectOrderInfoByOrderId.getSkr());
	    fpkj.setFhr(selectOrderInfoByOrderId.getFhr());
	    fpkj.setKprq(DateUtil.format(orderInvoiceInfo.getKprq(), "yyyy-MM-dd HH:mm:ss"));
	    fpkj.setKplx(OrderInfoEnum.INVOICE_BILLING_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx()) ? ConfigureConstant.STRING_1 : ConfigureConstant.STRING_2);
	
	    if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
		    fpkj.setYfp_dm(selectOrderInfoByOrderId.getYfpDm());
		    fpkj.setYfp_hm(selectOrderInfoByOrderId.getYfpHm());
		
	    }
	    fpkj.setQd_bz(OrderInfoEnum.QDBZ_CODE_0.getKey());
	    fpkj.setQdxmmc(selectOrderInfoByOrderId.getQdXmmc());
	    fpkj.setCh_bz("");
	    fpkj.setChyy("");
	    fpkj.setBz(selectOrderInfoByOrderId.getBz());
	
	    // TODO 从配置中读取
	    fpkj.setAppid(OpenApiConfig.appid);
	    fpkj.setSqddh(selectByOrderQrcodeExtendInfo.getAuthOrderId());
	    // 默认插卡到微信
	    fpkj.setApplx("0");
		fpkj.setSqlx("0");
		List<Fpkjxx> itemList = new ArrayList<>();
		for (OrderItemInfo orderItemInfo : selectOrderItemInfoByOrderId) {
			Fpkjxx fpkjxx = new Fpkjxx();
			fpkjxx.setSphxh(orderItemInfo.getSphxh());
			fpkjxx.setXmmc(orderItemInfo.getXmmc());
			fpkjxx.setXmdw(orderItemInfo.getXmdw());
			fpkjxx.setGgxh(orderItemInfo.getGgxh());
			fpkjxx.setXmsl(orderItemInfo.getXmsl());
			fpkjxx.setHsbz(orderItemInfo.getHsbz());
			fpkjxx.setFphxz(orderItemInfo.getFphxz());
			fpkjxx.setXmdj(orderItemInfo.getXmdj());
			fpkjxx.setSpbm(orderItemInfo.getSpbm());
			fpkjxx.setZxbm(orderItemInfo.getZxbm());
			fpkjxx.setYhzcbs(orderItemInfo.getYhzcbs());
			fpkjxx.setLslbs(orderItemInfo.getLslbs());
			fpkjxx.setZzstsgl(orderItemInfo.getZzstsgl());
			fpkjxx.setXmje(orderItemInfo.getXmje());
			fpkjxx.setSl(orderItemInfo.getSl());
			fpkjxx.setSe(orderItemInfo.getSe());
			itemList.add(fpkjxx);
		}
		insertCardRequest.setFpxx_base(fpkj);
		insertCardRequest.setFpkjxx_xmxxs(itemList);
		return insertCardRequest;
	}
	
	@Override
	public void sendToInsertCardQueue(InvoicePush invoicePush) {
		iRabbitMqSendMessage.autoSendRabbitMqMessage(invoicePush.getNSRSBH(), NsrQueueEnum.INSERT_CARD_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(invoicePush));
	}
	
	public static void main(String[] args) {
		String decodeToString = Base64Encoding.decodeToString("5Lia5Yqh5aSE55CG5byC5bi4IQ==");
		System.out.print(decodeToString);
	}

}
