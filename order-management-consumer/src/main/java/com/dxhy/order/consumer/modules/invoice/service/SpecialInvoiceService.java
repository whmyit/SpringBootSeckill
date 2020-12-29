package com.dxhy.order.consumer.modules.invoice.service;

import com.alibaba.fastjson.JSONArray;
import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.R;
import com.dxhy.order.model.SpecialInvoiceReversalItem;
import com.dxhy.order.model.a9.hp.HpResponseBean;
import com.dxhy.order.model.entity.CommonSpecialInvoice;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import com.dxhy.order.model.entity.SpecialExcelImport;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_RSP;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @Author fankunfeng
 * @Date 2019-06-15 10:59:13
 * @Describe manager 迁移 生成红字专票拆分服务
 */
public interface SpecialInvoiceService {
	
	/**
	 * 开票
	 *
	 * @param submitCodes
	 * @param accessPointId
	 * @param accessPointName
	 * @param extensionNum
	 * @param operatorId
	 * @param operatorName
	 * @param departmentId
	 * @param taxpayerCode
	 * @return
	 * @throws OrderSeparationException
	 */
	Map<String, Object> addInvoice(String[] submitCodes, String accessPointId, String accessPointName, String extensionNum,
	                               String operatorId, String operatorName, String departmentId, String taxpayerCode) throws OrderSeparationException;
	
	/**
	 * 申请单提交
	 *
	 * @param specialInvoiceReversal
	 * @param specialInvoiceReversalItems
	 * @return
	 */
	Map<String, String> submitSpecialInvoiceReversal(SpecialInvoiceReversalEntity specialInvoiceReversal, List<SpecialInvoiceReversalItem> specialInvoiceReversalItems);
	
	/**
	 * 同步申请单
	 *
	 * @param code
	 * @param taxpayerCode
	 * @param invoiceType
	 * @param invoiceCategory
	 * @param operatorId
	 * @param operatorName
	 * @return
	 */
	JSONArray syncSpecialInvoiceReversal(String code, String taxpayerCode, String invoiceType, String invoiceCategory, String operatorId, String operatorName);
	
	/**
	 * 查询申请单
	 *
	 * @param accessPointId
	 * @param mechainCode
	 * @param invoiceCategory
	 * @param nsrsbh
	 * @param terminalCode
	 * @return
	 * @throws OrderReceiveException
	 */
	Map<String, String> querySpecialInvoiceReversalCode(String accessPointId, String mechainCode, String invoiceCategory, String nsrsbh, String terminalCode) throws OrderReceiveException;
	
	/**
	 * 合并申请单
	 *
	 * @param invoiceCode
	 * @param invoiceNo
	 * @return
	 */
	R mergeSpecialInvoice(String invoiceCode, String invoiceNo);
	
	/**
	 * 同步申请单
	 *
	 * @param string
	 * @param xhfNsrsbh
	 * @param string1
	 * @param key
	 * @param string2
	 * @param username
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	Map<String, String> syncSpecialInvoiceReversal(String string, String xhfNsrsbh, String string1, String key, String string2,
	                                               String username, String startTime, String endTime);
	
	/**
	 * 申请单导入
	 *
	 * @param file
	 * @return
	 * @throws OrderReceiveException
	 */
	List<SpecialExcelImport> readSpecialInvoiceFromExcel(MultipartFile file) throws OrderReceiveException;
	
	/**
	 * 补全订单信息
	 *
	 * @param commonSpecialInvoiceList
	 * @param accessPointId
	 * @param accessPointName
	 * @param machineCode
	 * @param terminal
	 * @param drawerInfoEntity
	 * @param userId
	 * @param userName
	 * @param xhfMc
	 * @param xhfNsrsbh
	 * @param xhfDz
	 * @param xhfDh
	 * @param xhfYh
	 * @param xhfZh
	 * @return
	 * @throws OrderReceiveException
	 * @throws InterruptedException
	 */
	List<CommonSpecialInvoice> completeOrderInvoiceInfo(List<CommonSpecialInvoice> commonSpecialInvoiceList,
	                                                    String accessPointId, String accessPointName, String machineCode, String terminal, DrawerInfoEntity drawerInfoEntity, String userId, String userName, String xhfMc, String xhfNsrsbh, String xhfDz, String xhfDh, String xhfYh, String xhfZh) throws OrderReceiveException, InterruptedException;
	
	/**
	 * 保存申请单
	 *
	 * @param commonSpecialInvoiceList
	 * @return
	 */
	boolean saveSpecialInvoiceInfo(List<CommonSpecialInvoice> commonSpecialInvoiceList);
	
	/**
	 * 保存申请单
	 *
	 * @param hzsqdscRsp
	 * @param hzsqdscReq
	 * @param sldid
	 * @param kpjh
	 * @param fplx
	 * @param fplb
	 */
	void saveSpecialInvoiceRequest(HZSQDSC_RSP hzsqdscRsp, HZSQDSC_REQ hzsqdscReq, String sldid, String kpjh, String fplx, String fplb);
	
	/**
	 * 红字申请单下载方格
	 *
	 * @param hzsqdxzReq
	 * @return
	 */
	HpResponseBean downloadSpecialInvoiceReversalFg(HZSQDXZ_REQ hzsqdxzReq);
	
	/**
	 * 保存申请单
	 *
	 * @param ids
	 * @return
	 */
	R submitSpecialInvoiceReversal(String[] ids);
	
	/**
	 * 申请单下载
	 *
	 * @param id
	 * @param xhfNsrsbh
	 * @return
	 */
	R revoke(String id, String xhfNsrsbh);
}
