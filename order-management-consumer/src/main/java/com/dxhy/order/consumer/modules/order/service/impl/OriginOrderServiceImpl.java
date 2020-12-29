package com.dxhy.order.consumer.modules.order.service.impl;

import com.dxhy.order.api.*;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.modules.order.service.OriginOrderService;
import com.dxhy.order.consumer.utils.PageDataDealUtil;
import com.dxhy.order.model.*;
import com.ibm.icu.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原始订单业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 18:18
 */
@Service
@Slf4j
public class OriginOrderServiceImpl implements OriginOrderService{
	
	
	@Reference
	private ApiOriginOrderExtendService apiOriginOrderExtendService;
	
	@Reference
	private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
	
	@Reference
    private ApiOrderProcessService apiOrderProcessService;
	
	@Reference
	private ApiOrderInfoService apiOrderInfoService;
	
	@Reference
	private ApiOrderItemInfoService apiOrderItemInfoService;
	
	private static final String[] COMPARE_HEADERS = {"订单号", "订单金额", "已开票金额", "票单差异金额", "剩余可开票金额", "发票代码/号码", "票单金额差异原因"};

	
	@Override
	public PageUtils queryOriginList(Map<String, Object> paramMap, List<String> shList) {
		
		return apiOriginOrderExtendService.queryOriginList(paramMap, shList);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public PageUtils executeCompareOriginOrderAndInvoice(Map<String, Object> paramMap, List<String> shList) {
		
		// 返回结果集
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		// 分页查询
		PageUtils queryOriginOrderCompare = apiOriginOrderExtendService.queryOriginOrderCompare(paramMap, shList);
		List<Map<String, Object>> originList = (List<Map<String, Object>>) queryOriginOrderCompare.getList();
		for (Map<String, Object> map : originList) {
			Map<String, Object> returnMap = new HashMap<>(5);
			String ddh = map.get("ddh") == null ? "" : String.valueOf(map.get("ddh"));
			String ddje = map.get("ddje") == null ? "0.00" : String.valueOf(map.get("ddje"));
			String kpje = map.get("kpje") == null ? "0.00" : String.valueOf(map.get("kpje"));
			String ddcfje = String.valueOf(map.get("ddcfje"));
			String fpdmhm = map.get("fpdmhm") == null ? "" : String.valueOf(map.get("fpdmhm"));
			String originOrderId = String.valueOf(map.get("orderId"));
			String orderId = String.valueOf(map.get("orderInfoId"));
			int count = Integer.parseInt(map.get("countorder") == null ? "0" : String.valueOf(map.get("countorder")));
			
			// 计算 票单差异金额和剩余可开票金额
			BigDecimal cyje = new BigDecimal(kpje).subtract(new BigDecimal(ddje));
			returnMap.put("cyje", cyje.toString());
			returnMap.put("leaveje", cyje.negate().toString());
			returnMap.put("id", originOrderId);
			returnMap.put("kpje", kpje);
			returnMap.put("ddje", ddje);
			returnMap.put("ddh", ddh);
			returnMap.put("ddcfje", ddcfje);
			returnMap.put("xhfNsrsbh", map.get("xhfNsrsbh"));
			returnMap.put("xhfMc", map.get("xhfMc"));
			List<Map<String, String>> list = new ArrayList<>();
			
			boolean isAllRushRed = false;
			boolean isPartRushRed = false;
			boolean isValid = false;
			
			// 处理已开发票信息
			if (StringUtils.isNotBlank(fpdmhm)) {
				// 查询已经开具的发票数据
				OrderOriginExtendInfo originExtend = new OrderOriginExtendInfo();
				originExtend.setOriginOrderId(originOrderId);
				List<OrderOriginExtendInfo> queryOriginOrderByOrder = apiOriginOrderExtendService
						.queryOriginOrderByOrder(originExtend, shList);
				for (OrderOriginExtendInfo orderOriginExtend : queryOriginOrderByOrder) {
					OrderInvoiceInfo selectOrderInvoiceInfoByFpqqlsh = apiOrderInvoiceInfoService
							.selectOrderInvoiceInfoByFpqqlsh(orderOriginExtend.getFpqqlsh(), shList);
					if (selectOrderInvoiceInfoByFpqqlsh != null) {

						if (StringUtils.isBlank(selectOrderInvoiceInfoByFpqqlsh.getFpdm())
								|| StringUtils.isBlank(selectOrderInvoiceInfoByFpqqlsh.getFphm())) {
							break;
						}
						// 发票被冲红过
						isAllRushRed = ConfigureConstant.STRING_1.equals(selectOrderInvoiceInfoByFpqqlsh.getChBz());
						isPartRushRed = "4".equals(selectOrderInvoiceInfoByFpqqlsh.getChBz());

						if (ConfigureConstant.STRING_1.equals(selectOrderInvoiceInfoByFpqqlsh.getZfBz())) {
							isValid = true;
						}

						// 查询红字发票
						List<OrderInfo> queryOrderInfoByYfpdmYfphm = apiOrderInfoService.queryOrderInfoByYfpdmYfphm(
								selectOrderInvoiceInfoByFpqqlsh.getFpdm(), selectOrderInvoiceInfoByFpqqlsh.getFphm(), shList);
						if (CollectionUtils.isNotEmpty(queryOrderInfoByYfpdmYfphm)) {
							for (OrderInfo orderInfo : queryOrderInfoByYfpdmYfphm) {
								OrderInvoiceInfo redInvoice = apiOrderInvoiceInfoService
										.selectOrderInvoiceInfoByFpqqlsh(orderInfo.getFpqqlsh(), shList);
								if (redInvoice != null && "2".equals(redInvoice.getKpzt())
										&& StringUtils.isNotBlank(redInvoice.getFpdm())
										&& StringUtils.isNotBlank(redInvoice.getFphm())) {
									Map<String, String> invoiceMap = new HashMap<>(5);
									invoiceMap.put("fpdm", redInvoice.getFpdm());
									invoiceMap.put("fphm", redInvoice.getFphm());
									list.add(invoiceMap);
								}
							}
						}

						Map<String, String> invoiceMap = new HashMap<>(5);
						invoiceMap.put("fpdm", selectOrderInvoiceInfoByFpqqlsh.getFpdm());
						invoiceMap.put("fphm", selectOrderInvoiceInfoByFpqqlsh.getFphm());
						list.add(invoiceMap);

					}

				}

			}
			// 处理差异原因
			if (cyje.doubleValue() != 0.00) {

				if (isValid) {
					returnMap.put("reason", "发票作废");
				} else if (isAllRushRed || isPartRushRed) {
					returnMap.put("reason", "订单冲红");
				} else {
					if (cyje.doubleValue() == new BigDecimal(ddje).negate().doubleValue()) {
						returnMap.put("reason", "订单暂未开票");
					} else {
						if (count > 1) {
							if (cyje.doubleValue() > 0.00) {
								returnMap.put("reason", "订单拆分");
							} else {
								returnMap.put("reason", "订单拆分,部分未开票");
							}
						} else if (count == 1) {
							if (cyje.doubleValue() > 0.00) {
								if (originOrderId.equals(orderId)) {
									returnMap.put("reason", "订单金额编辑修改");
								} else {
									// 订单合并
									OrderOriginExtendInfo orderOriginExtendInfo = new OrderOriginExtendInfo();
									orderOriginExtendInfo.setOrderId(orderId);
									List<OrderOriginExtendInfo> queryOriginOrderByOrder = apiOriginOrderExtendService
											.queryOriginOrderByOrder(orderOriginExtendInfo, shList);
									StringBuilder hbddhStringBuilder = new StringBuilder();
									String hbddh;
									for (OrderOriginExtendInfo originOrder : queryOriginOrderByOrder) {
										if (!originOrderId.equals(originOrder.getOriginOrderId())) {
											hbddhStringBuilder.append("/").append(originOrder.getOriginDdh());
										}
									}
									hbddh = hbddhStringBuilder.toString();
									if (hbddh.startsWith("/")) {
										hbddh = hbddh.substring(1);
									}
									returnMap.put("reason", "此订单与" + hbddh + "合并");
								}
							} else {
								returnMap.put("reason", "订单暂未开票");
							}

						} else {
							log.error("原始订单查询异常");
						}
					}
				}
			} else {
				returnMap.put("reason", "无");
			}
			returnMap.put("invoiceList", list);
			resultList.add(returnMap);
		}
		// 单条原始订单数据处理
		queryOriginOrderCompare.setList(resultList);
		return queryOriginOrderCompare;
	}
	
	@Override
	public Map<String, Object> queryOriginOrderDetail(String originOrderId, List<String> shList) {
		
		Map<String, Object> resultMap = new HashMap<>(5);
		// 查询原始订单的订单和明细数据
		OrderInfo selectOrderInfoByOrderId = apiOrderInfoService.selectOrderInfoByOrderId(originOrderId, shList);
		List<OrderItemInfo> selectOrderItemInfoByOrderId = apiOrderItemInfoService
				.selectOrderItemInfoByOrderId(originOrderId, shList);
		
		Map<String, Object> queryMap = new HashMap<>(5);
		queryMap.put("orderId", originOrderId);
		List<Map<String, Object>> queryOriginOrderAndInvoiceInfo = apiOriginOrderExtendService
				.queryOriginOrderAndInvoiceInfo(queryMap, shList);
		
		OrderProcessInfo selectByOrderId = apiOrderProcessService.selectByOrderId(selectOrderInfoByOrderId.getId(), shList);
		
		boolean isAllInvoice = true;
		boolean isAllNotInvoice = true;
		List<Map<String, Object>> invoiceList = new ArrayList<>();
		for (Map<String, Object> map : queryOriginOrderAndInvoiceInfo) {
			
			String ddzt = map.get("ddzt") == null ? "" : String.valueOf(map.get("ddzt"));
			// 判断订单的状态是否是开票成功的
			if (OrderInfoEnum.ORDER_STATUS_7.getKey().equals(ddzt)
					|| OrderInfoEnum.ORDER_STATUS_5.getKey().equals(ddzt)) {
				String kpzt = map.get("kpzt") == null ? "" : String.valueOf(map.get("kpzt"));
				if (OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
					isAllNotInvoice = false;
					invoiceList.add(map);
					String chbz = String.valueOf(map.get("chbz"));
					String fpdm = String.valueOf(map.get("fpdm"));
					String fphm = String.valueOf(map.get("fphm"));
					// 查询红票信息
					List<OrderInfo> queryOrderInfoByYfpdmYfphm = apiOrderInfoService.queryOrderInfoByYfpdmYfphm(fpdm, fphm, shList);
					if (CollectionUtils.isNotEmpty(queryOrderInfoByYfpdmYfphm)) {
						for (OrderInfo orderInfo : queryOrderInfoByYfpdmYfphm) {
							OrderInvoiceInfo selectOrderInvoiceInfoByFpqqlsh = apiOrderInvoiceInfoService
									.selectOrderInvoiceInfoByFpqqlsh(orderInfo.getFpqqlsh(), shList);
							if (StringUtils.isNotBlank(selectOrderInvoiceInfoByFpqqlsh.getFpdm())
									&& StringUtils.isNotBlank(selectOrderInvoiceInfoByFpqqlsh.getFphm())) {
								Map<String, Object> invoiceMap = new HashMap<>(5);
								invoiceMap.put("fpdm", selectOrderInvoiceInfoByFpqqlsh.getFpdm());
								invoiceMap.put("fphm", selectOrderInvoiceInfoByFpqqlsh.getFphm());
								invoiceMap.put("kpzt", selectOrderInvoiceInfoByFpqqlsh.getKpzt());
								invoiceMap.put("chbz", selectOrderInvoiceInfoByFpqqlsh.getChBz());
								invoiceMap.put("ddh", selectOrderInvoiceInfoByFpqqlsh.getDdh());
								invoiceMap.put("kphjje", selectOrderInvoiceInfoByFpqqlsh.getKphjje());
								invoiceMap.put("hjbhsje", selectOrderInvoiceInfoByFpqqlsh.getHjbhsje());
								invoiceMap.put("kpse", selectOrderInvoiceInfoByFpqqlsh.getKpse());
								invoiceMap.put("orderInfoId", selectOrderInvoiceInfoByFpqqlsh.getOrderInfoId());
								invoiceMap.put("ghfMc", selectOrderInvoiceInfoByFpqqlsh.getGhfMc());
								invoiceMap.put("fpqqlsh", selectOrderInvoiceInfoByFpqqlsh.getFpqqlsh());
								invoiceList.add(invoiceMap);
							}
						}
					} else {
						log.error("已冲红的发票没有找到红票的信息");
					}

				} else {
					isAllInvoice = false;
				}
			} else {
				isAllInvoice = false;
			}
		}
		// 开票状态 0 未开票 1 部分开票 2 全部开票
		if (isAllInvoice) {
			resultMap.put("invoiceStatus", "2");
			
		} else if (isAllNotInvoice) {
			resultMap.put("invoiceStatus", "0");
			
		} else {
			resultMap.put("invoiceStatus", ConfigureConstant.STRING_1);
			
		}
		
		PageDataDealUtil.dealOrderItemInfo(selectOrderItemInfoByOrderId);
		resultMap.put("orderInfo", selectOrderInfoByOrderId);
		resultMap.put("orderItemList", selectOrderItemInfoByOrderId);
		resultMap.put("invoiceList", invoiceList);
		resultMap.put("ddly", selectByOrderId.getDdly());
		return resultMap;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void exportCompareOriginOrderAndInvoice(Map<String, Object> paramMap, ServletOutputStream outputStream, List<String> shList) {
		
		// 返回结果集
		List<Map<String, Object>> resultList = new ArrayList<>();
		PageUtils executeCompareOriginOrderAndInvoice = executeCompareOriginOrderAndInvoice(paramMap, shList);
		resultList = (List<Map<String, Object>>) executeCompareOriginOrderAndInvoice.getList();
		// 分页查询
		
		// 创建excel工作台
		SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(1000);
		sxssfWorkbook.setCompressTempFiles(true);
		// 创建一个表格
		Sheet sheet = sxssfWorkbook.createSheet();
		// 冻结首行
		sheet.createFreezePane(0, 1, 0, 1);
		// 创建一行
		Row row = sheet.createRow(0);
		// 生成一个样式
		CellStyle style = buildHeadStyle(sxssfWorkbook);
		// 生成字体
		Font font = sxssfWorkbook.createFont();
		font.setFontHeightInPoints((short) 16);
		// 生成表头
		for (int i = 0; i < COMPARE_HEADERS.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			XSSFRichTextString text = new XSSFRichTextString(COMPARE_HEADERS[i]);
			cell.setCellValue(text);
		}

		Map<Integer, Integer> colWidthMap = new HashMap<>(5);
		delColWidth(row, colWidthMap);
		int index = 1;

		for (Map<String, Object> param : resultList) {
			List<Map<String, Object>> orginInvoiceList = (List<Map<String, Object>>) param.get("invoiceList");
			// 判断原始订单是否有已经开票的数据
			if (!CollectionUtils.isEmpty(orginInvoiceList)) {
				for (Map<String, Object> map : orginInvoiceList) {

					Row tableRow = sheet.createRow(index);
					tableRow.createCell(0).setCellValue(String.valueOf(param.get("ddh")));
					tableRow.createCell(1).setCellValue(String.valueOf(param.get("ddje")));
					tableRow.createCell(2).setCellValue(String.valueOf(param.get("kpje")));
					tableRow.createCell(3).setCellValue(String.valueOf(param.get("cyje")));
					tableRow.createCell(4).setCellValue(String.valueOf(param.get("leaveje")));
					tableRow.createCell(6).setCellValue(String.valueOf(param.get("reason")));
					tableRow.createCell(5)
							.setCellValue(map.get("fpdm") + "/" + map.get("fphm"));

					delColWidth(tableRow, colWidthMap);
					index++;
				}
			} else {
				Row tableRow = sheet.createRow(index);
				tableRow.createCell(0).setCellValue(String.valueOf(param.get("ddh")));
				tableRow.createCell(1).setCellValue(String.valueOf(param.get("ddje")));
				//tableRow.createCell(1).setCellValue(String.valueOf(param.get("kpje")));
				tableRow.createCell(3).setCellValue(String.valueOf(param.get("cyje")));
				tableRow.createCell(4).setCellValue(String.valueOf(param.get("leaveje")));
				tableRow.createCell(6).setCellValue(String.valueOf(param.get("reason")));
				tableRow.createCell(2).setCellValue("0.00");
				delColWidth(tableRow, colWidthMap);
				index++;
			}
		}

		for (Map.Entry<Integer, Integer> entry : colWidthMap.entrySet()) {
			Integer key = entry.getKey();
			Integer value = entry.getValue();
			sheet.setColumnWidth(key, value);
		}
		try {
			sxssfWorkbook.write(outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
				sxssfWorkbook.close();
			} catch (IOException e) {
				log.error("输出流关闭异常:{}", e);
			}
		}
		
	}
	
	/**
	 * 存放最大列宽
	 *
	 * @param tableRow
	 * @param colWidthMap
	 */
	private void delColWidth(Row tableRow, Map<Integer, Integer> colWidthMap) {
		for (Cell cell : tableRow) {
			int columnIndex = cell.getColumnIndex();
			Integer maxColumIndex = colWidthMap.get(columnIndex);
			if (maxColumIndex == null) {
				if (StringUtils.isNotBlank(cell.getStringCellValue())) {
					int length = cell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
					colWidthMap.put(columnIndex, length * 256);
				} else {
					colWidthMap.put(columnIndex, 0);
				}
			} else {
				if (StringUtils.isNotBlank(cell.getStringCellValue())) {
					int length = cell.getStringCellValue().getBytes(StandardCharsets.UTF_8).length;
					length = length * 256;
					if (length > maxColumIndex) {
						colWidthMap.put(columnIndex, length);
					}
				}
			}
		}
	}
	
	/**
	 * 创建样式
	 *
	 * @param workBook
	 * @return
	 */
	private CellStyle buildHeadStyle(SXSSFWorkbook workBook) {
		CellStyle style = workBook.createCellStyle();
		style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.PALE_BLUE.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		return style;
	}
	
	/**
	 * 查询总计已开票和待开票金额
	 */
	@Override
	public Map<String, String> queryCompareOriginOrderAndInvoiceCounter(Map<String, Object> paramMap, List<String> shList) {
		
		Map<String, String> resultMap = new HashMap<>(5);
		BigDecimal kpzje;
		BigDecimal zje;
		BigDecimal cyje;
		BigDecimal syje;
		
		//查询数据库符合条件的数据
		Map<String, Object> queryCompareOriginOrderAndInvoiceCounter = apiOriginOrderExtendService
				.queryCompareOriginOrderAndInvoiceCounter(paramMap, shList);
		String ddje = queryCompareOriginOrderAndInvoiceCounter.get("ddje") == null ? "0.00" : String.valueOf(queryCompareOriginOrderAndInvoiceCounter.get("ddje"));
        String kpje = queryCompareOriginOrderAndInvoiceCounter.get("kpje") == null ? "0.00" : String.valueOf(queryCompareOriginOrderAndInvoiceCounter.get("kpje"));
        kpzje = new BigDecimal(kpje);
        zje = new BigDecimal(ddje);
        cyje = kpzje.subtract(zje);
        syje = zje.subtract(kpzje);
		
		resultMap.put("kpje", kpzje.toString());
		resultMap.put("zje", zje.toString());
		resultMap.put("cyje", cyje.toString());
		resultMap.put("syje", syje.toString());
		return resultMap;
	}
	
	public static void main(String[] args) {
		List<String> strList = new ArrayList<>();
		strList.add("0");
		strList.add("1");
		strList.add("2");
		List<String> subList = strList.subList(0, 2);
		System.out.println(subList);
		
	}
	
}
