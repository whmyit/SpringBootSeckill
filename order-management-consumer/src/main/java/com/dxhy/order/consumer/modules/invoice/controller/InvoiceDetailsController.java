package com.dxhy.order.consumer.modules.invoice.controller;

import com.dxhy.order.api.ApiHistoryDataPdfService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.generateinvoice.PDFProducer;
import com.dxhy.order.consumer.utils.FileConversion;
import com.dxhy.order.model.CommonOrderInvoiceAndOrderMxInfo;
import com.dxhy.order.model.HistoryDataPdfEntity;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.ofd.OfdToPngRequest;
import com.dxhy.order.model.ofd.OfdToPngResponse;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 发票明细 controller
 *
 * @author Dear
 */
@Slf4j
@RestController
@Api(value = "发票详情", tags = {"发票模块"})
@RequestMapping("/invoiceDetails")
public class InvoiceDetailsController {

	private static final String CONTENT_DISPOSITION2 = "Content-Disposition";
	
	private static final String ATTACHMENT_FILENAME_DETAIL = "inline;filename=";
	
	private static final String ISO8859_1 = "ISO8859-1";
	
	private static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
	
	private static final String CONTENT_TYPE = "Content-Type";
	
	private final static String HEADER_USER_AGENT_FIREFOX = "Firefox";
	
	private final static int FILE_NAME_UTF8_LENGTH_LIMIT = 150;
	
	private final static String LOGGER_MSG = "(发票明细 controller)";
	
	private static final String SHORT_MESSAE_TQM_PREFIX = "sims_notes_tqm_";
	
	@Reference
	private ApiTaxEquipmentService apiTaxEquipmentService;
	
	@Reference
	private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
	
	@Reference
	private RedisService redisService;
	
	@Reference
	private ApiHistoryDataPdfService historyDataPdfService;
	
	/**
	 * 查询发票信息详情
	 *
	 * @param invoiceNo   发票号码
	 * @param invoiceType 发票类型
	 * @param invoiceCode 发票代码
	 * @return
	 */
	@PostMapping("/queryInvoiceDetailInfoPdf")
	@ApiOperation(value = "发票详情查询", notes = "发票详情-发票详情查询")
	@SysLog(operation = "发票详情查询", operationDesc = "发票详情查询", key = "发票详情查询")
	public R queryInvoiceDateilsPdf(@RequestParam String invoiceCode, @RequestParam String invoiceNo,
	                                @RequestParam String invoiceType, @RequestParam String xhfNsrsbh) {
		log.info("查询发票信息详情 参数 发票号码{} 发票代码{}发票类型{}销方税号{}", invoiceNo, invoiceCode, invoiceType, xhfNsrsbh);
		if (StringUtil.isEmpty(invoiceNo) || StringUtil.isEmpty(invoiceCode) || StringUtil.isEmpty(invoiceType) || StringUtil.isEmpty(xhfNsrsbh)) {
			return R.error("发票号码,发票代码,发票类型,销方税号未填写");
		}
		// Map<String, Object> map = new HashMap<String, Object>(5);
		try {
			List<String> shList = new ArrayList<>();
			shList.add(xhfNsrsbh);
			String ofdUrl = "";
			// 0 已开具 1 已冲红 2已作废
			String fpzt = ConfigureConstant.STRING_0;
			// 0 返回 1 返回打印下载 2 返回打印 下载
			String show = ConfigureConstant.STRING_0;
			// 冲红
			log.info("根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", invoiceCode, invoiceNo);
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService
					.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, shList);
			if (commonOrderInvoiceAndOrderMxInfo == null) {
				log.info("{}发票信息信息不存在 ");
				return R.error("发票信息不存在");
			}
			// 开票状态,(0:初始化;1:开票中;2:开票成功;3:开票失败;)',
			String kpzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKpzt();
			if (!OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
				log.info("{} 发票代码{} 发票号码{} 开票状态不为开票成功 ", LOGGER_MSG, invoiceCode, invoiceNo);
				return R.error("该发票开票状态异常");
			}
			
			String chbz = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getChBz();
			// 判断发票是否可以冲红 开票类型（0：蓝票；1：红票）
			String kplx = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplx();
			
			// 作废标志(0:正常;1:已作废;
			String zfzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getZfBz();
			
			String terminalCode = apiTaxEquipmentService
					.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh());
			boolean result = (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(invoiceType);
			if (result) {
				ofdUrl = String
						.format(OpenApiConfig.ofdUrl, commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(),
								commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(),
								commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpzlDm(), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh())
						.replaceAll("&", "%26");
			}
			if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(invoiceType)) {
				if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
					// （0：蓝票；1：红票）
					boolean result2 = OrderInfoEnum.INVALID_INVOICE_0.getKey().equals(zfzt)
							&& !(OrderInfoEnum.RED_INVOICE_1.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_4.getKey().equals(chbz));
					if (result2) {
						show = ConfigureConstant.STRING_2;
						fpzt = ConfigureConstant.STRING_0;
					} else {
						show = ConfigureConstant.STRING_1;
						fpzt = ConfigureConstant.STRING_1.equals(zfzt) ? ConfigureConstant.STRING_2
								: (!ConfigureConstant.STRING_0.equals(chbz) ? ConfigureConstant.STRING_1
								: ConfigureConstant.STRING_0);
					}
				} else {// 红票
					fpzt = ConfigureConstant.STRING_0;
					show = ConfigureConstant.STRING_1;
					// 0 返回 1 返回打印下载 2 返回打印 下载
					// 冲红
				}
				
			} else {
				
				/**
				 * 冲红标志(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败
				 * ;(特殊说明:部分冲红只记录当前最后一次操作的记录))
				 *
				 * 作废标志(0:正常;1:已作废;2:作废中;3:作废失败)
				 *
				 * 返回前端数据 1:已冲红;0:正常;2:已作废;3:部分冲红
				 *
				 *
				 * 1.作废标志为1时,则发票状态为2 2.作废标志为0,2或者是3时,判断冲红标志进行取值,
				 * 如果冲红标志为0,2,3,5,6则发票状态为0 如果冲红标志为1,则发票状态为1 如果冲红标志为4,则发票状态为3
				 */
				if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(zfzt)) {
					fpzt = ConfigureConstant.STRING_2;
				} else if (OrderInfoEnum.INVALID_INVOICE_0.getKey().equals(zfzt)
						|| OrderInfoEnum.INVALID_INVOICE_2.getKey().equals(zfzt)
						|| OrderInfoEnum.INVALID_INVOICE_3.getKey().equals(zfzt)) {
					if (OrderInfoEnum.RED_INVOICE_0.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_2.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_3.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_5.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_6.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_0;
					} else if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_1;
					} else if (OrderInfoEnum.RED_INVOICE_4.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_3;
					}
				}
				
			}
			return R.ok().put("kplx", kplx).put("fpzt", fpzt).put("show", show).put("ofdUrl", ofdUrl);
		} catch (Exception e) {
			log.debug("{}出错了。。{}", LOGGER_MSG, e);
			return R.error("业务处理异常");
		}
	}
	
	/**
	 * 预览发票信息
	 *
	 * @param invoiceNo   发票号码
	 * @param invoiceType 发票类型
	 * @param invoiceCode 发票代码
	 * @return
	 */
	@GetMapping(value = "/previewInvoicePdf")
	@ApiOperation(value = "发票详情预览", notes = "发票详情-发票详情预览")
	@SysLog(operation = "发票详情预览", operationDesc = "发票详情预览", key = "发票详情预览")
	public void previewInvoicePdf(@RequestParam String invoiceCode, @RequestParam String invoiceNo,
	                              @RequestParam String invoiceType, @RequestParam String xhfNsrsbh, HttpServletResponse response, HttpServletRequest request) {
		log.info("预览发票信息详情 参数 发票号码{} 发票代码{}发票类型{}销方税号{}", invoiceNo, invoiceCode, invoiceType, xhfNsrsbh);
		if (StringUtil.isEmpty(invoiceNo) || StringUtil.isEmpty(invoiceCode) || StringUtil.isEmpty(invoiceType) || StringUtil.isEmpty(xhfNsrsbh)) {
			return;
		}
		List<String> shList = new ArrayList<>();
		shList.add(xhfNsrsbh);
		try {
			log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService
					.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, shList);
			if (commonOrderInvoiceAndOrderMxInfo == null || commonOrderInvoiceAndOrderMxInfo.getOrderInfo() == null) {
				log.info("{}发票信息信息不存在 ", LOGGER_MSG);
				return;
			}
			/**
			 * 获取税控设备信息
			 */
			String terminalCode = apiTaxEquipmentService
					.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getXhfNsrsbh());
			
			byte[] pdfFile = getFile(commonOrderInvoiceAndOrderMxInfo, terminalCode);
			
			response.setHeader(CONTENT_TYPE, TEXT_HTML_CHARSET_UTF_8);
			
			OutputStream toClient = null;
			// 根据uuid查找pdf并写流
			try {
				String filename;
				// 设置返回的文件类型
				boolean result3 = (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(invoiceType);
				if (result3) {
					response.setContentType(ConfigureConstant.STRING_APPLICATION_OFD);
					filename = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm()
							+ ConfigureConstant.STRING_LINE
							+ commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm()
							+ ConfigureConstant.STRING_LINE
							+ DateUtilsLocal.getDefaultFormatToString(new Date())
							+ ConfigureConstant.STRING_SUFFIX_OFD;
				} else {
					response.setContentType(ConfigureConstant.STRING_APPLICATION_PDF);
					filename = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm()
							+ ConfigureConstant.STRING_LINE
							+ commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm()
							+ ConfigureConstant.STRING_LINE
							+ DateUtilsLocal.getDefaultFormatToString(new Date())
							+ ConfigureConstant.STRING_SUFFIX_PDF;
				}
				
				String fileName = null;
				
				// 获取客户端信息,判断客户端是否为火狐
				if (request.getHeader(HttpHeaders.USER_AGENT).contains(HEADER_USER_AGENT_FIREFOX)) {
					fileName = new String(filename.getBytes(StandardCharsets.UTF_8), ISO8859_1);
				} else {
					// 否则使用UTF-8
					fileName = URLEncoder.encode(filename, StandardCharsets.UTF_8.name());
					// 解决:一个汉字编码成UTF-8是9个字节，那么17个字便是153个字节，所以便会报错
					if (fileName.length() > FILE_NAME_UTF8_LENGTH_LIMIT) {
						String guessCharset = request.getCharacterEncoding(); /*
						 * 根据request的locale
						 * 得出可能的编码，
						 * 中文操作系统通常是gb2312
						 */
						fileName = new String(filename.getBytes(guessCharset), ISO8859_1);
					}
				}
				response.setHeader(CONTENT_DISPOSITION2, ATTACHMENT_FILENAME_DETAIL + fileName);
				
				toClient = response.getOutputStream();
				toClient.write(pdfFile);
				toClient.flush();
				
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			} finally {
				try {
					if (toClient != null) {
						toClient.close();
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			
		} catch (Exception e) {
			log.debug("{}出错了。。{}", LOGGER_MSG, e);
		}
	}
	

	/**
	 * 预览发票图片信息
	 * @param invoiceDate
	 * @return
	 */
	@PostMapping(value = "/previewInvoicePng")
	@ApiOperation(value = "预览发票图片", notes = "发票详情-预览发票图片")
	@SysLog(operation = "预览发票图片", operationDesc = "预览发票图片", key = "发票详情")
	public R previewInvoicePng(@ApiParam(name = "invoiceDate", value = "发票数据", required = true) @RequestBody String invoiceDate) {
		
		if (StringUtils.isBlank(invoiceDate)) {
			log.error("{},请求参数为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}
		Map<String, String> map = JsonUtils.getInstance().parseObject(invoiceDate, Map.class);
		
		String invoiceCode = map.get("invoiceCode");
		String invoiceNo = map.get("invoiceNo");
		String invoiceType = map.get("invoiceType");
		String xhfNsrsbh = map.get("xhfNsrsbh");
		log.info("预览发票图片 参数 发票号码{} 发票代码{}发票类型{}销方税号{}", invoiceNo, invoiceCode, invoiceType, xhfNsrsbh);
		if (StringUtil.isEmpty(invoiceNo) || StringUtil.isEmpty(invoiceCode) || StringUtil.isEmpty(invoiceType) || StringUtil.isEmpty(xhfNsrsbh)) {
			log.error("{},请求参数为空!", LOGGER_MSG);
			return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
		}
		List<String> pngBase64List = new ArrayList<>();
		try {
			
			// 根据uuid查找pdf并写流
			
			List<String> shList = new ArrayList<>();
			shList.add(xhfNsrsbh);
			log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", LOGGER_MSG, invoiceCode, invoiceNo);
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService
					.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(invoiceCode, invoiceNo, shList);
			if (commonOrderInvoiceAndOrderMxInfo == null || commonOrderInvoiceAndOrderMxInfo.getOrderInfo() == null) {
				log.error("{}发票信息信息不存在 ", LOGGER_MSG);
				return R.error(OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_NULL);
			}
			/**
			 * 获取税控设备信息
			 */
			String terminalCode = apiTaxEquipmentService
					.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getXhfNsrsbh());
			
			byte[] pdfFile = getFile(commonOrderInvoiceAndOrderMxInfo, terminalCode);
			
			//根据税控设备类型判断调用,进行转换图片
			boolean result3 = (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) && OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(invoiceType);
			if (result3) {
				//新税控调用底层接口进行转换
				OfdToPngRequest ofdToPngRequest = new OfdToPngRequest();
				ofdToPngRequest.setOFDWJL(Base64Encoding.encodeToString(pdfFile));
				OfdToPngResponse ofdToPngResponse = HttpInvoiceRequestUtil.getOfdPng(OpenApiConfig.ofdToPngUrl, ofdToPngRequest);
				if (ofdToPngResponse != null && ConfigureConstant.STRING_000000.equals(ofdToPngResponse.getZTDM()) && StringUtils.isNotBlank(ofdToPngResponse.getPNGWJL())) {
					String[] pngBase64 = ofdToPngResponse.getPNGWJL().split(ConfigureConstant.STRING_POINT2);
					pngBase64List.addAll(Lists.newArrayList(pngBase64));
				}
			} else {
				//本地pdf转换
				byte[][] pngs = FileConversion.pdfByteToPngByte(pdfFile, "png");
				if (pngs != null && pngs.length > 0) {
					for (byte[] png : pngs) {
						pngBase64List.add(Base64Encoding.encodeToString(png));
					}
				}
			}
			
			
		} catch (Exception e) {
			log.error("{}出错了。。{}", LOGGER_MSG, e);
		}
		return R.ok().put(OrderManagementConstant.DATA, pngBase64List);
	}
	
	public byte[] getFile(CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo, String terminalCode) {
		byte[] pdfFile = null;
		
		if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getFpzlDm())) {
			//判断是否为导入的历史数据(订单类型)
			if (StringUtils.equals(OrderInfoEnum.ORDER_TYPE_6.getKey(),
					commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getDdlx())) {
				pdfFile = findPdfFile(commonOrderInvoiceAndOrderMxInfo);
			} else if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode) && StringUtils.isNotEmpty(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getMongodbId())) {
				//方格UKey数据,电票存储在mongo中,
				pdfFile = findPdfFile(commonOrderInvoiceAndOrderMxInfo);
			} else {
				String kpls = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplsh();
				
				// 请求bean组装
				GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(
						kpls.substring(0, kpls.length() - 3),
						commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh(), terminalCode,
						commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getPdfUrl());
				GetPdfResponseExtend pdf2 = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean,
						terminalCode);
				pdfFile = Base64Encoding.decode(pdf2.getResponse_EINVOICE_PDF().get(0).getPDF_FILE());
				
				
			}
		} else {
			//判断是否为导入的历史数据
			if (StringUtils.equals(OrderInfoEnum.ORDER_TYPE_6.getKey(),
					commonOrderInvoiceAndOrderMxInfo.getOrderInfo().getDdlx())) {
				pdfFile = findPdfFile(commonOrderInvoiceAndOrderMxInfo);
			} else {
				R createPdf = PDFProducer.createPdf(commonOrderInvoiceAndOrderMxInfo);
				if (OrderInfoContentEnum.SUCCESS.getKey().equals(createPdf.get(OrderManagementConstant.CODE))) {
					pdfFile = (byte[]) createPdf.get(OrderManagementConstant.DATA);
				}
			}
		}
		return pdfFile;
	}
	
	@GetMapping(value = "/previewInvoicePdfByTqm")
	@ApiOperation(value = "发票详情根据提取码预览", notes = "发票详情-发票详情根据提取码预览")
	@SysLog(operation = "发票详情根据提取码预览", operationDesc = "发票详情根据提取码预览", key = "发票详情预览")
	public void previewInvoicePdfByTqm(
			@ApiParam(name = "tqm", value = "提取码", required = false) @RequestParam("tqm") String tqm,
			HttpServletResponse response, HttpServletRequest request) {
		log.info("根据提取码预览发票的接口，提取码:{}", tqm);
		
		try {
			
			if (StringUtil.isEmpty(tqm)) {
				log.error("参数为空!");
				PrintWriter out = response.getWriter();
				out.append("提取码不存在!");
				out.close();
				return;
			}
			String result = redisService.get(SHORT_MESSAE_TQM_PREFIX + tqm);
			if (StringUtils.isBlank(result)) {
				PrintWriter out = response.getWriter();
				out.append("提取码不存在!");
				out.close();
				return;
			}
			OrderInvoiceInfo parseObject = JsonUtils.getInstance().parseObject(result, OrderInvoiceInfo.class);
			//发票详情预览
			this.previewInvoicePdf(parseObject.getFpdm(), parseObject.getFphm(), parseObject.getFpzlDm(), parseObject.getXhfNsrsbh(), response, request);
		} catch (Exception e) {
			log.debug("{}出错了。。{}", LOGGER_MSG, e);
		} finally {
		}
		
	}
	
	@PostMapping("/queryInvoiceDateilInfoPdfByTqm")
	@ApiOperation(value = "发票详情查询根据提取码", notes = "发票详情-发票详情查询根据提取码")
	@SysLog(operation = "发票详情查询根据提取码", operationDesc = "发票详情查询根据提取码", key = "发票详情查询")
	public R queryInvoiceDateilsPdf(
			@ApiParam(name = "tqm", value = "提取码", required = false) @RequestParam("tqm") String tqm) {
		log.info("查询发票信息详情 参数 提取码:{}", tqm);
		if (StringUtil.isEmpty(tqm)) {
			log.error("参数为空!");
			return R.error().put("message", "提取码不存在!");
		}
		String result = redisService.get(SHORT_MESSAE_TQM_PREFIX + tqm);
		if (StringUtils.isBlank(result)) {
			log.error("根据提取码获取redis中的数据为空!");
			return R.error().put("message", "提取码不存在!");
		}
		OrderInvoiceInfo parseObject = JsonUtils.getInstance().parseObject(result, OrderInvoiceInfo.class);
		
		try {
			String ofdUrl = "";
			// 0 已开具 1 已冲红 2已作废
			String fpzt = ConfigureConstant.STRING_0;
			// 0 返回 1 返回打印下载 2 返回打印 下载
			String show = ConfigureConstant.STRING_0;
			// 冲红
			log.info("{}根据发票代码发票号码查看发票信息 参数 发票代码 {} 发票号码{}", parseObject.getFpdm(), parseObject.getFphm());
			CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo = apiOrderInvoiceInfoService
					.selectOrderInvoiceInfoByFpdmFphmAndNsrsbh(parseObject.getFpdm(), parseObject.getFphm(), null);
			if (commonOrderInvoiceAndOrderMxInfo == null) {
				log.info("{}发票信息信息不存在 ");
				return R.error("发票信息不存在");
			}
			// 开票状态,(0:初始化;1:开票中;2:开票成功;3:开票失败;)',
			String kpzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKpzt();
			if (!OrderInfoEnum.INVOICE_STATUS_2.getKey().equals(kpzt)) {
				log.info("{} 发票代码{} 发票号码{} 开票状态不为开票成功 ", LOGGER_MSG, parseObject.getFpdm(), parseObject.getFphm());
				return R.error("该发票开票状态异常");
			}
			
			String chbz = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getChBz();
			// 判断发票是否可以冲红 开票类型（0：蓝票；1：红票）
			String kplx = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getKplx();
			
			// 作废标志(0:正常;1:已作废;
			String zfzt = commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getZfBz();
			
			String terminalCode = apiTaxEquipmentService
					.getTerminalCode(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh());
			if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
				ofdUrl = String
						.format(OpenApiConfig.ofdUrl, commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(),
								commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(),
								commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpzlDm(), commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh())
						.replaceAll("&", "%26");
			}
			if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(parseObject.getFpzlDm())) {
				if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(kplx)) {
					// （0：蓝票；1：红票）
					boolean result4 = OrderInfoEnum.INVALID_INVOICE_0.getKey().equals(zfzt)
							&& !(OrderInfoEnum.RED_INVOICE_1.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_4.getKey().equals(chbz));
					if (result4) {
						show = ConfigureConstant.STRING_2;
						fpzt = ConfigureConstant.STRING_0;
					} else {
						show = ConfigureConstant.STRING_1;
						fpzt = ConfigureConstant.STRING_1.equals(zfzt) ? ConfigureConstant.STRING_2
								: (!ConfigureConstant.STRING_0.equals(chbz) ? ConfigureConstant.STRING_1
								: ConfigureConstant.STRING_0);
					}
				} else {// 红票
					fpzt = ConfigureConstant.STRING_0;
					// 0 返回 1 返回打印下载 2 返回打印 下载
					show = ConfigureConstant.STRING_1;
					// 冲红
				}
				
			} else {
				
				/**
				 * 冲红标志(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败
				 * ;(特殊说明:部分冲红只记录当前最后一次操作的记录))
				 *
				 * 作废标志(0:正常;1:已作废;2:作废中;3:作废失败)
				 *
				 * 返回前端数据 1:已冲红;0:正常;2:已作废;3:部分冲红
				 *
				 *
				 * 1.作废标志为1时,则发票状态为2 2.作废标志为0,2或者是3时,判断冲红标志进行取值,
				 * 如果冲红标志为0,2,3,5,6则发票状态为0 如果冲红标志为1,则发票状态为1 如果冲红标志为4,则发票状态为3
				 */
				if (OrderInfoEnum.INVALID_INVOICE_1.getKey().equals(zfzt)) {
					fpzt = ConfigureConstant.STRING_2;
				} else if (OrderInfoEnum.INVALID_INVOICE_0.getKey().equals(zfzt)
						|| OrderInfoEnum.INVALID_INVOICE_2.getKey().equals(zfzt)
						|| OrderInfoEnum.INVALID_INVOICE_3.getKey().equals(zfzt)) {
					if (OrderInfoEnum.RED_INVOICE_0.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_2.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_3.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_5.getKey().equals(chbz)
							|| OrderInfoEnum.RED_INVOICE_6.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_0;
					} else if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_1;
					} else if (OrderInfoEnum.RED_INVOICE_4.getKey().equals(chbz)) {
						fpzt = ConfigureConstant.STRING_3;
					}
				}
				
			}
			return R.ok().put("kplx", kplx).put("fpzt", fpzt).put("show", show).put("ofdUrl", ofdUrl);
		} catch (Exception e) {
			log.debug("{}出错了。。{}", LOGGER_MSG, e);
			return R.error("业务处理异常");
		}
	}
	
	private byte[] findPdfFile(CommonOrderInvoiceAndOrderMxInfo commonOrderInvoiceAndOrderMxInfo) {
		List<String> shList = new ArrayList<>();
		shList.add(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getXhfNsrsbh());
		byte[] pdfFile = null;
		HistoryDataPdfEntity historyDataPdfEntity = historyDataPdfService.find(commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFpdm(),
				commonOrderInvoiceAndOrderMxInfo.getOrderInvoiceInfo().getFphm(), shList);
		if (Objects.isNull(historyDataPdfEntity)) {
			R createPdf = PDFProducer.createPdf(commonOrderInvoiceAndOrderMxInfo);
			if (OrderInfoContentEnum.SUCCESS.getKey().equals(createPdf.get(OrderManagementConstant.CODE))) {
				pdfFile = (byte[]) createPdf.get(OrderManagementConstant.DATA);
			}
		} else {
			pdfFile = Base64Encoding.decode(historyDataPdfEntity.getPdfFileData());
		}
		return pdfFile;
	}
}
