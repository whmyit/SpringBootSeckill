package com.dxhy.order.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.api.ApiEmailService;
import com.dxhy.order.api.ApiHistoryDataPdfService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.OrderInfoMapper;
import com.dxhy.order.dao.OrderInvoiceInfoMapper;
import com.dxhy.order.model.HistoryDataPdfEntity;
import com.dxhy.order.model.OrderInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.email.Attachments;
import com.dxhy.order.model.email.EmailContent;
import com.dxhy.order.model.entity.InvoiceWarningInfo;
import com.dxhy.order.model.message.GlobalInfo;
import com.dxhy.order.model.message.OpenApiResponse;
import com.dxhy.order.model.ofd.OfdToPngRequest;
import com.dxhy.order.model.ofd.OfdToPngResponse;
import com.dxhy.order.service.OpenApiService;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ：杨士勇
 * @ClassName ：EmailServiceImpl
 * @Description ：发票版式文件邮箱交付
 * @date ：2019年3月7日 下午3:04:22
 */
@Service
@Slf4j
public class EmailServiceImpl implements ApiEmailService {
    
    private static final String LOGGER_MSG = "(发送邮件业务)";
    
    @Resource
    private OrderInfoMapper orderInfoMapper;
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    
    @Resource
    private ApiTaxEquipmentService apiTaxEquipmentService;
    
    @Resource
    private OpenApiService openApiService;
    
    @Resource
    private ApiHistoryDataPdfService apiHistoryDataPdfService;
    
    
    @Override
    public R sendPdfEmail(List<Map> invoiceIds, String emailAddress) {
        try {
            log.debug("发票版式文件邮箱发送，入参：{}，{}", JsonUtils.getInstance().toJsonString(invoiceIds), emailAddress);
            for (Map map : invoiceIds) {
                Map<String, String> paramMap = new HashMap<>(10);
                String id = (String) map.get("id");
                String nsrsbh = (String) map.get("xhfNsrsbh");
                List<String> shList = new ArrayList<>();
                shList.add(nsrsbh);
                if (StringUtils.isNotBlank(id)) {
                    paramMap.put(ConfigureConstant.INVOICE_ID, id);
                    paramMap.put(ConfigureConstant.EMAIL_ADDRESS, emailAddress);
                    paramMap.put(ConfigureConstant.SHLIST, JsonUtils.getInstance().toJsonString(shList));
                    String json = JsonUtils.getInstance().toJsonString(paramMap);
                    this.pushWithEmail(json);
                }
            }
			return R.ok();
		} catch (Exception e) {
            log.error("发票交付异常，异常信息为:{}",e);
			return R.error();
		}
		
	}
    
    /**
     * 发送邮箱
     *
     * @param content
     * @return
     */
    @Override
    public Map<String, Object> sendEmail(String content) {
        
        //定义回调接收实体
        Map<String, Object> returnMap = new HashMap<>(10);
        try {
            //设置邮件体
            GlobalInfo globalInfo = setEmailBody(content);

            //使用openAPi发送邮件 产品使用
//            OpenApiResponse response = openApiService.sendRequest(globalInfo, OpenApiConfig.OPENAPI_EMAIL_NOTE + ConfigureConstant.METHOD);

            //使用客户邮箱发送邮件 项目使用
            OpenApiResponse response = openApiService.sendRequest(globalInfo, OpenApiConfig.emailSendUrl);
            returnMap.put(OrderManagementConstant.CODE, response.getReturnStateInfo().getReturnCode());
            returnMap.put(OrderManagementConstant.ALL_MESSAGE, response.getReturnStateInfo().getReturnMessage());
        } catch (Exception e) {
            e.printStackTrace();
            returnMap.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            returnMap.put(OrderManagementConstant.ALL_MESSAGE, e.getMessage());
        }
        return returnMap;
    }
    
    /**
     * 设置邮件体
     *
     * @param content
     * @return
     * @throws Exception
     */
    private GlobalInfo setEmailBody(String content) throws Exception {
        //设置邮件体参数
        
        GlobalInfo globalInfo = new GlobalInfo();
        globalInfo.setZipCode("0");
        globalInfo.setEncryptCode("0");
        globalInfo.setDataExchangeId(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
        globalInfo.setContent(Base64Encoding.encode(content));
        return globalInfo;
    }
    
    @Override
    public void pushWithEmail(String message) {
        Map<String, String> resultMap = JsonUtils.getInstance().parseObject(message, Map.class);
    
        List<String> shList = JsonUtils.getInstance().parseObject(resultMap.get(ConfigureConstant.SHLIST), List.class);
    
        //邮箱交付自动推送
        String orderInvoiceId = resultMap.get(ConfigureConstant.INVOICE_ID);
        /**
         * 获取发票表数据
         */
        OrderInvoiceInfo orderInvoiceInfoReq = new OrderInvoiceInfo();
        orderInvoiceInfoReq.setId(orderInvoiceId);
        OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfoReq, shList);
    
        //组装请求邮件服务的bean 请求邮件服务
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByOrderId(orderInvoiceInfo.getOrderInfoId(), shList);
        String emailAddress = orderInfo.getGhfEmail();
    
        /**
         * 如果页面传递邮箱不为空,则使用页面传递邮箱进行发送
         */
        if (StringUtils.isNotBlank(resultMap.get(ConfigureConstant.EMAIL_ADDRESS))) {
            emailAddress = resultMap.get(ConfigureConstant.EMAIL_ADDRESS);
        }
    
        //存在邮箱的话用邮箱进行推送
        if (StringUtils.isNotBlank(emailAddress)) {
    
            //根据销方税号查询发票终端类型
            String terminalCode = apiTaxEquipmentService.getTerminalCode(orderInfo.getXhfNsrsbh());
    
            log.info("发票代码:{},号码:{},邮箱不为空开始发送pdf到邮箱：{}", orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), emailAddress);
            List<String> emailList = Lists.newArrayList();
            emailList.add(emailAddress);
            //组装邮箱发送bean
            EmailContent emailContent = buildEmailContent(orderInvoiceInfo, emailList, terminalCode);
            //调用邮件服务
            Map<String, Object> sendEmail = this.sendEmail(JsonUtils.getInstance().toJsonString(emailContent));
        
            OrderInvoiceInfo orderInvoiceInfoUpdate = new OrderInvoiceInfo();
            orderInvoiceInfoUpdate.setId(orderInvoiceInfo.getId());
            
            //根据调用邮件服务的结果更新数据库
            if (!ConfigureConstant.STRING_0000.equals(String.valueOf(sendEmail.get(OrderManagementConstant.CODE)))) {
                log.error("发票代码:{},发票号码:{},邮箱:{},发送失败", orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), emailAddress);
                orderInvoiceInfoUpdate.setEmailPushStatus("3");
                orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfoUpdate, shList);
            } else {
                orderInvoiceInfoUpdate.setEmailPushStatus("2");
                orderInvoiceInfoMapper.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfoUpdate, shList);
                log.info("发票代码:{},发票号码:{},邮箱:{},发送成功", orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), emailAddress);
            }
            
        } else {
            log.info("邮箱地址为空，不发送邮件");
        }
        
    }
    
    private EmailContent buildEmailContent(OrderInvoiceInfo orderInvoiceInfo, List<String> emails, String terminalCode) {
        //获取Calendar的方法
        Calendar c = Calendar.getInstance();
        //将Calendar的时间改成日期的时间
        c.setTime(orderInvoiceInfo.getKprq());
        //获取当前年份
        int mYear = c.get(Calendar.YEAR);
        //获取当前月份
        int mMonth = c.get(Calendar.MONTH) + 1;
        //获取当前月份的日期号码
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        //主题
        EmailContent emailContent = new EmailContent();
        emailContent.setTemplateId(OpenApiConfig.invoicePdfId);
        emailContent.setSerialNum(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
        emailContent.setSubjects(new String[]{orderInvoiceInfo.getFphm()});
        emailContent.setContents(new String[]{String.valueOf(mYear), String.valueOf(mMonth), String.valueOf(mDay),
                orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getXhfMc(),
                orderInvoiceInfo.getGhfMc(), orderInvoiceInfo.getKphjje()});
        //添加邮件地址
        emailContent.setTo(Convert.toStrArray(emails));
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
            GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean(ConfigureConstant.STRING_0000, orderInvoiceInfo.getXhfNsrsbh(), terminalCode, orderInvoiceInfo.getFpdm(), orderInvoiceInfo.getFphm(), orderInvoiceInfo.getPdfUrl());
            GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, terminalCode);
            pdfFile = Optional.ofNullable(pdf).map(pdfres -> pdfres.getResponse_EINVOICE_PDF()).map(einPdf -> einPdf.get(0)).map(pdf_File -> pdf_File.getPDF_FILE()).orElse(null);
            //pdfFile = pdf.getResponse_EINVOICE_PDF().get(0).getPDF_FILE();
        }

        if (StringUtils.isNotBlank(pdfFile)) {
            log.info("附件不为空，添加附件");
            //添加邮件附件,支持多个附件
            List<Attachments> attachmentsList = Lists.newArrayList();
            String suffix = ConfigureConstant.STRING_SUFFIX_PDF;
            String applicationType = ConfigureConstant.STRING_APPLICATION_PDF;
            if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(terminalCode)) {
                suffix = ConfigureConstant.STRING_SUFFIX_OFD;
                applicationType = ConfigureConstant.STRING_APPLICATION_OFD;
            }
            Attachments attachments = new Attachments();
            attachments.setContent(pdfFile);

            String attachFileName = orderInvoiceInfo.getFpdm() + "_" + orderInvoiceInfo.getFphm() + suffix;
            attachments.setName(attachFileName);
            attachments.setType(applicationType);
            attachmentsList.add(attachments);
            /**
             * 如果是ofd格式,需要转成png,然后放在附件中
             */
            if (ConfigureConstant.STRING_SUFFIX_OFD.equals(suffix)) {
                OfdToPngRequest ofdToPngRequest = new OfdToPngRequest();
                ofdToPngRequest.setOFDWJL(pdfFile);
                OfdToPngResponse ofdToPngResponse = HttpInvoiceRequestUtil.getOfdPng(OpenApiConfig.OfdToPngUrl, ofdToPngRequest);
                if (ofdToPngResponse != null && ConfigureConstant.STRING_000000.equals(ofdToPngResponse.getZTDM()) && StringUtils.isNotBlank(ofdToPngResponse.getPNGWJL())) {
                    String[] pngBase64List = ofdToPngResponse.getPNGWJL().split(ConfigureConstant.STRING_POINT2);
                    for (String pngBase64 : pngBase64List) {
                        Attachments attachmentsPng = new Attachments();
                        attachmentsPng.setContent(pngBase64);

                        String attachFileNamePng = orderInvoiceInfo.getFpdm() + "_" + orderInvoiceInfo.getFphm() + ConfigureConstant.STRING_SUFFIX_PNG;
                        attachmentsPng.setName(attachFileNamePng);
                        attachmentsPng.setType(ConfigureConstant.STRING_APPLICATION_PNG);
                        attachmentsList.add(attachmentsPng);
                    }
                }
            }
            emailContent.setAttachments(attachmentsList.toArray(new Attachments[0]));
        }else
            log.info("没查询到附件");
        return emailContent;
    }
    
    /**
     * 余票预警定时任务发送邮件服务
     *
     * @param invoiceWarningInfo
     * @param dqfpfs
     * @param mc
     */
    @Override
    public void sendInvoiceWarningInfoEmail(InvoiceWarningInfo invoiceWarningInfo, String dqfpfs, String mc) {
        if (OrderInfoEnum.ORDER_WARNING_OPEN.getKey().equals(invoiceWarningInfo.getSfyj())) {
            // 构造邮件实体
            EmailContent content = new EmailContent();
            content.setTemplateId(OpenApiConfig.invoiceWarning);
            content.setSerialNum(RandomUtil.randomNumbers(ConfigureConstant.INT_25));
            content.setSubjects(new String[]{});
            content.setContents(new String[]{invoiceWarningInfo.getXhfNsrsbh(), invoiceWarningInfo.getSbbh(), mc, dqfpfs});
            content.setTo(invoiceWarningInfo.getEMail().split(";"));
            // 发送邮件
            log.info("发送邮件 参数{}", JSONObject.toJSONString(content));
            Map map = this.sendEmail(JsonUtils.getInstance().toJsonString(content));
            log.info("发送邮件 结果{}", JSONObject.toJSONString(map));
            
        }
    }

/*    public static void main(String[] args) {

        Three t = new Three(null,"18");
        Secd s = new Secd(t, "2");
        First f = new First(s, "1");
        Man m = new Man(f);
        Optional<Man> o = Optional.ofNullable(m);
        String aNull = o.map(man -> man.getFirst()).map(first -> first.getSecd()).map(secd -> secd.getThree()).map(three -> three.getName()).orElse("null");
        System.out.println(aNull);

    }

    @Data
    @AllArgsConstructor
    static class Man{
        First first;
    }
    @Data
    @AllArgsConstructor
    static class First{
        Secd secd;
        String fir;
    }
    @Data
    @AllArgsConstructor
    static class Secd{
        Three three;
        String sec;
    }
    @Data
    @AllArgsConstructor
    static class Three{
        String name;
        String age;
    }*/
}
