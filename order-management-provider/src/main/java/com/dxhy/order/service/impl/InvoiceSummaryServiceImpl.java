package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiInvoiceSummaryStatisticsService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.ItemTypeEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.TaxRateTypeEnum;
import com.dxhy.order.dao.InvoiceItemRequestInfoMapper;
import com.dxhy.order.dao.InvoiceTaxRateRequestInfoMapper;
import com.dxhy.order.model.InvoiceItemPO;
import com.dxhy.order.model.InvoiceItemRequestInfo;
import com.dxhy.order.model.InvoiceTaxRatePO;
import com.dxhy.order.model.InvoiceTaxRateRequestInfo;
import com.dxhy.order.model.vo.QsBusinessVo;
import com.dxhy.order.model.vo.QsRequestVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Description:发票汇总统计接口
 * @Author:xueanna
 * @Date:2019/5/29
 */
@Slf4j
@Service
public class InvoiceSummaryServiceImpl implements ApiInvoiceSummaryStatisticsService {
    
    @Resource
    private InvoiceItemRequestInfoMapper invoiceItemRequestInfoMapper;
    @Resource
    private InvoiceTaxRateRequestInfoMapper invoiceTaxRateRequestInfoMapper;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 汇总统计数据
     */
    @Override
    public String getInvoiceSummaryStatistics(QsRequestVo vo) {
        if (ConfigureConstant.STRING_1.equals(vo.getInformType())) {
            //税率汇总
            this.getTaxRateStatistics(vo.getParam());
        } else {
            //项目汇总
            this.getItemStatistics(vo.getParam());
        }
        return null;
    }
    
    /**
     * 全税接口 获取汇总数据状态
     */
    @Override
    public List<Map> getSummaryState(QsRequestVo vo) {
        List<Map> returnlist = new ArrayList<>();
        List<QsBusinessVo> param = vo.getParam();
        if (ConfigureConstant.STRING_1.equals(vo.getInformType())) {
            //税率汇总
            for (QsBusinessVo v : param) {
                List<Map> list = invoiceTaxRateRequestInfoMapper.getSummaryTaxRateState(v.getTaxNumber(), v.getBillingDate());
                if (ObjectUtils.isEmpty(list) || list.size() == 0) {
                    //获取数据为空
                    Map map = new HashMap(10);
                    map.put("billingDate", v.getBillingDate());
                    map.put("taxNumber", v.getTaxNumber());
                    map.put("completeFlag", ConfigureConstant.STRING_2);
                    list.add(map);
                }
                returnlist.addAll(list);
            }
        } else {
            //项目汇总
            for (QsBusinessVo v : param) {
                List<Map> list = invoiceItemRequestInfoMapper.getSummaryItemState(v.getTaxNumber(), v.getBillingDate());
                if (ObjectUtils.isEmpty(list) || list.size() == 0) {
                    Map map = new HashMap(10);
                    map.put("billingDate", v.getBillingDate());
                    map.put("taxNumber", v.getTaxNumber());
                    map.put("completeFlag", ConfigureConstant.STRING_2);
                    list.add(map);
                }
                returnlist.addAll(list);
            }
        }
        return returnlist;
    }
    
    /**
     * 获取汇总数据
     *
     * @param vo
     * @return
     */
    @Override
    public List<Map> getSummaryData(QsRequestVo vo) {
        List<Map> returnlist = new ArrayList<>();
        List<QsBusinessVo> param = vo.getParam();
        if (ConfigureConstant.STRING_1.equals(vo.getInformType())) {
            //获取税率汇总
            for (QsBusinessVo v : param) {
                Map map = new HashMap(10);
                List<Map> list = invoiceTaxRateRequestInfoMapper.selectSummaryTaxRateData(v.getTaxNumber(), v.getBillingDate());
                map.put("taxNumber", v.getTaxNumber());
                map.put("billingDate", v.getBillingDate());
                map.put("summaryData", list);
                returnlist.add(map);
            }
        } else {
            //获取项目汇总
            for (QsBusinessVo v : param) {
                Map map = new HashMap(10);
                List<Map> list = invoiceItemRequestInfoMapper.selectSummaryItemData(v.getTaxNumber(), v.getBillingDate());
                map.put("taxNumber", v.getTaxNumber());
                map.put("billingDate", v.getBillingDate());
                map.put("summaryData", list);
                returnlist.add(map);
            }
        }
        return returnlist;
    }
    
    /**
     * 税率汇总
     */
    public String getTaxRateStatistics(List<QsBusinessVo> businessVo) {
        long startTime = System.currentTimeMillis();
        List<Map> sllist = new ArrayList<>();
        String key = "";
        Date d = new Date();
        for (QsBusinessVo v : businessVo) {
            //遍历参数
            //获取日期
            List<String> selectDateList = getDateList(v.getBillingDate());
        
            //逻辑处理
            List<InvoiceTaxRatePO> list = invoiceTaxRateRequestInfoMapper.selectTaxRateStatistics(v.getTaxNumber(), selectDateList);
            Map slmap = getSlList(v.getTaxNumber(), v.getBillingDate());
            for (InvoiceTaxRatePO po : list) {
                key = po.getFpzlDm() + "-" + po.getKplx() + "-" + po.getSl();
                InvoiceTaxRateRequestInfo bean = (InvoiceTaxRateRequestInfo) slmap.get(key);
                if (bean == null) {
                    break;
                }
                if (OrderInfoEnum.INVOICE_BILLING_TYPE_0.getKey().equals(po.getKplx())) {
                    //蓝票(正数)
                    //判断是否有效
                    if (ConfigureConstant.STRING_1.equals(po.getZfBz())) {
                        //作废
                        //蓝票作废金额
                        bean.setInvoiceAmountPtVoid(po.getXmje());
                        //蓝票作废税额
                        bean.setTaxAmountPtVoid(po.getSe());
                        //蓝票作废价税合计
                        bean.setTotalAmountPtVoid(po.getKphjje());
                    } else {
                        //有效
                        //蓝票有效金额
                        bean.setInvoiceAmountPt(po.getXmje());
                        //蓝票有效税额
                        bean.setTaxAmountPt(po.getSe());
                        //蓝票有效价税合计
                        bean.setTotalAmountPt(po.getKphjje());
                    }
                } else if (ConfigureConstant.STRING_1.equals(po.getKplx())) {
                    //红票(负数)
                    if (ConfigureConstant.STRING_1.equals(po.getZfBz())) {
                        //作废
                        //红票作废金额
                        bean.setInvoiceAmountNtVoid(po.getXmje());
                        //红票作废税额
                        bean.setTaxAmountNtVoid(po.getSe());
                        //红票作废价税合计
                        bean.setTotalAmountNtVoid(po.getKphjje());
                    } else {//有效
                        //红票有效金额
                        bean.setInvoiceAmountNt(po.getXmje());
                        //红票有效税额
                        bean.setTaxAmountNt(po.getSe());
                        //红票有效价税合计
                        bean.setTotalAmountNt(po.getKphjje());
                    }
                }
                slmap.put(key, bean);
            }
            sllist.add(slmap);
        }
        //税率汇总数据保存到数据库  taxTateList
        for (Map map : sllist) {
            for (Object k : map.keySet()) {
                InvoiceTaxRateRequestInfo info = (InvoiceTaxRateRequestInfo) map.get(k.toString());
                //查询数据库是否存在
                InvoiceTaxRateRequestInfo infodatabase = invoiceTaxRateRequestInfoMapper.selectTaxRateInfo(info.getXhfNsrsbh(), info.getHzrq(), info.getSl(), info.getKplx(), info.getFpzlDm());
                if (infodatabase == null) {
                    info.setId(apiInvoiceCommonService.getGenerateShotKey());
                    info.setCompleteFlag("2");
                    invoiceTaxRateRequestInfoMapper.insertTaxRateRequestInfo(info);
                } else {
                    infodatabase.setInvoiceAmountPt(info.getInvoiceAmountPt());
                    infodatabase.setTaxAmountPt(info.getTaxAmountPt());
                    infodatabase.setTotalAmountPt(info.getTotalAmountPt());
                    
                    infodatabase.setInvoiceAmountNt(info.getInvoiceAmountNt());
                    infodatabase.setTaxAmountNt(info.getTaxAmountNt());
                    infodatabase.setTotalAmountNt(info.getTotalAmountNt());
                    
                    infodatabase.setInvoiceAmountPtVoid(info.getInvoiceAmountPtVoid());
                    infodatabase.setTaxAmountPtVoid(info.getTaxAmountPtVoid());
                    infodatabase.setTotalAmountPtVoid(info.getTotalAmountPtVoid());
                    
                    infodatabase.setInvoiceAmountNtVoid(info.getInvoiceAmountNtVoid());
                    infodatabase.setTaxAmountNtVoid(info.getTaxAmountNtVoid());
                    infodatabase.setTotalAmountNtVoid(info.getTotalAmountNtVoid());
    
                    invoiceTaxRateRequestInfoMapper.updateInvoiceTaxRateById(infodatabase);
                }
            }
        }
        return null;
    }
    
    public List<String> getDateList(String hzrq) {
        List<String> dateList = new ArrayList();
        if (hzrq.contains("Q")) {
            //季度查询
            String[] q = hzrq.split("Q");
            switch (q[1]) {
                case "1":
                    dateList.add(q[0] + "01");
                    dateList.add(q[0] + "02");
                    dateList.add(q[0] + "03");
                    break;
                //可选
                case "2":
                    dateList.add(q[0] + "04");
                    dateList.add(q[0] + "05");
                    dateList.add(q[0] + "06");
                    break;
                //可选
                case "3":
                    dateList.add(q[0] + "07");
                    dateList.add(q[0] + "08");
                    dateList.add(q[0] + "09");
                    break;
                //可选
                case "4":
                    dateList.add(q[0] + "10");
                    dateList.add(q[0] + "11");
                    dateList.add(q[0] + "12");
                    break;
                //可选
                default:
                    throw new IllegalStateException("Unexpected value: " + q[1]);
            }
            
        } else {
            dateList.add(hzrq);
        }
        return dateList;
    }
    
    
    /**
     * 项目汇总
     */
    public String getItemStatistics(List<QsBusinessVo> businessVoList) {
        log.info("开始汇总项目数据");
        List<Map> datalist = new ArrayList();
        for (QsBusinessVo vo : businessVoList) {
            //遍历参数
            //获取所有项目类型
            Map itemMap = getItemList(vo.getTaxNumber(), vo.getBillingDate());
            //获取日期
            List<String> selectDateList = getDateList(vo.getBillingDate());
            List<InvoiceItemPO> list = invoiceItemRequestInfoMapper.selectSummaryItemStatistics(vo.getTaxNumber(), selectDateList);
        
            for (InvoiceItemPO po : list) {
                String xmlxkey = "";
                //判断项目类型
                if ("即征即退".equals(po.getXmlx())) {
                    if ("0.16".equals(po.getSl()) || "0.17".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_53.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_64.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_75.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_97.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_101.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_102.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_104.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_54.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_55.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.13".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_57.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_58.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_59.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_125.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_126.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_127.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_133.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_134.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_135.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.09".equals(po.getSl()) || "0.10".equals(po.getSl()) || "0.11".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_61.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_62.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_63.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_66.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_67.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_68.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_70.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_71.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_72.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.06".equals(po.getSl())) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_74.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_76.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_77.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if ("0.05".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_79.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_80.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_81.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_83.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_84.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_85.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.04".equals(po.getSl())) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_88.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_89.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_90.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if ("0.03".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_92.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_93.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_94.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_96.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_98.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_99.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    }
                } else if ("免税".equals(po.getXmlx())) {
                    if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_105.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_107.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_109.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_106.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_108.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_110.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    }
                } else if ("不征税".equals(po.getXmlx())) {
                    if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_113.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_115.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_117.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_114.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_116.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_118.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    }
                } else if ("非即征即退".equals(po.getXmlx())) {
                    //非即征即退
                    if ("0.16".equals(po.getSl()) || "0.17".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_1.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_2.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_3.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_5.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_6.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_7.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_9.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_10.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_11.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.13".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_13.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_14.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_15.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_121.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_122.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_123.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_129.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_130.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_131.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.09".equals(po.getSl()) || "0.10".equals(po.getSl()) || "0.1".equals(po.getSl()) || "0.11".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_17.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_18.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_19.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_21.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_22.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_23.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_25.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_26.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_27.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.06".equals(po.getSl())) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_29.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_30.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_31.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if ("0.05".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_33.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_34.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_35.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_37.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_38.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_39.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    } else if ("0.04".equals(po.getSl())) {
                        switch (po.getFpzlDm()) {
                            case "0":
                                //专票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_41.getKey();
                                break;
                            case "2":
                                //纸票
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_42.getKey();
                                break;
                            case "51":
                                //电
                                xmlxkey = ItemTypeEnum.ITEM_TYPE_43.getKey();
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                        }
                    } else if ("0.03".equals(po.getSl())) {
                        if (po.getSpbm().startsWith("1") || po.getSpbm().startsWith("2")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_45.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_46.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_47.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        } else if (po.getSpbm().startsWith("3") || po.getSpbm().startsWith("4") || po.getSpbm().startsWith("5")) {
                            switch (po.getFpzlDm()) {
                                case "0":
                                    //专票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_49.getKey();
                                    break;
                                case "2":
                                    //纸票
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_50.getKey();
                                    break;
                                case "51":
                                    //电
                                    xmlxkey = ItemTypeEnum.ITEM_TYPE_51.getKey();
                                    break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + po.getFpzlDm());
                            }
                        }
                    }
                }
    
                if (StringUtils.isEmpty(xmlxkey)) {
                    break;
                }
                InvoiceItemRequestInfo info = (InvoiceItemRequestInfo) itemMap.get(xmlxkey);
                info.setFpzlDm(po.getFpzlDm());
                //发票份数
                int fpfs = 0;
                if (Integer.parseInt(info.getInvoiceNum()) != 0) {
                    fpfs = Integer.parseInt(info.getInvoiceNum()) + 1;
                    info.setInvoiceNum(fpfs + "");
                } else {
                    info.setInvoiceNum("1");
                }
    
                //发票金额
                BigDecimal xmje = BigDecimal.ZERO;
                if (!ConfigureConstant.STRING_000.equals(new BigDecimal(info.getXmje()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString())) {
                    xmje = new BigDecimal(info.getXmje()).add(new BigDecimal(po.getXmje())).setScale(2, RoundingMode.HALF_UP);
                    info.setXmje(xmje.toPlainString());
                } else {
                    info.setXmje(po.getXmje());
                }
    
                //发票税额
                BigDecimal fpse = BigDecimal.ZERO;
                if (!ConfigureConstant.STRING_000.equals(new BigDecimal(info.getSe()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString())) {
                    fpse = new BigDecimal(info.getSe()).add(new BigDecimal(po.getSe())).setScale(2, RoundingMode.HALF_UP);
                    info.setSe(fpse.toPlainString());
                } else {
                    info.setSe(po.getSe());
                }
                //价税合计
                BigDecimal jshj = BigDecimal.ZERO;
                if (!ConfigureConstant.STRING_000.equals(new BigDecimal(info.getKphjje()).setScale(ConfigureConstant.INT_2, RoundingMode.HALF_UP).toPlainString())) {
                    jshj = new BigDecimal(info.getKphjje()).add(new BigDecimal(po.getKphjje())).setScale(2, RoundingMode.HALF_UP);
                    info.setKphjje(jshj.toPlainString());
                } else {
                    info.setKphjje(po.getKphjje());
                }
                itemMap.put(xmlxkey, info);
            }
            datalist.add(itemMap);
            
        }
        //保存项目汇总数据到数据库
        for (Map m : datalist) {
            for (Object k : m.keySet()) {
                InvoiceItemRequestInfo info = (InvoiceItemRequestInfo) m.get(k.toString());
                //查询数据库是否存在，存在修改，不存在添加
                InvoiceItemRequestInfo item = invoiceItemRequestInfoMapper.selectItemItem(info.getXhfNsrsbh(), info.getHzrq(), info.getSpbm());
                if (item == null) {
                    //不存在  添加
                    info.setCompleteFlag("2");
                    info.setId(apiInvoiceCommonService.getGenerateShotKey());
                    invoiceItemRequestInfoMapper.insertInvoiceItemRequest(info);
                } else {
                    item.setInvoiceNum(info.getInvoiceNum());
                    item.setXhfNsrsbh(item.getXhfNsrsbh());
                    item.setXmje(info.getXmje());
                    item.setSe(info.getSe());
                    item.setKphjje(info.getKphjje());
                    invoiceItemRequestInfoMapper.updateInvoiceItemRequestById(item);
                }
            }
        }
        return null;
    }
    
    public Map getItemList(String xhfNsrsbh, String hzrq) {
        Map map = new HashMap(10);
        InvoiceItemRequestInfo info;
        Date d = new Date();
        ItemTypeEnum[] values = ItemTypeEnum.values();
        for (ItemTypeEnum s : values) {
            info = new InvoiceItemRequestInfo();
            info.setXmmc(s.getValue());
            info.setXhfNsrsbh(xhfNsrsbh);
            info.setFpzlDm(s.getDm());
            info.setHzrq(hzrq);
            info.setSpbm(s.getKey());
            info.setInvoiceNum("0");
            info.setXmje("0");
            info.setSe("0");
            info.setKphjje("0");
            info.setCreateTime(d);
            map.put(s.getKey(), info);
        }
        return map;
    }
    
    public Map getSlList(String nsrsbh, String hzrq) {
        Map map = new HashMap(10);
        InvoiceTaxRateRequestInfo info;
        String[] fpzldm = {OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey(), OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey(), OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey()};
        String[] kplx = {OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey(), OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey()};
        Date d = new Date();
        TaxRateTypeEnum[] values = TaxRateTypeEnum.values();
        for (TaxRateTypeEnum s : values) {
            for (String dm : fpzldm) {
                for (String lx : kplx) {
                    info = new InvoiceTaxRateRequestInfo();
                    info.setFpzlDm(dm);
                    info.setSl(s.getKey());
                    info.setHzrq(hzrq);
                    info.setXhfNsrsbh(nsrsbh);
                    info.setCreateTime(d);
                    info.setKplx(lx);
                    //税率key组成部分   发票种类代码-开票类型-税率
                    String key = dm + "-" + lx + "-" + s.getKey();
                    map.put(key, info);
                }
            }
        }
        return map;
    }
    
    public static void main(String[] args) {
    
        ItemTypeEnum[] v = ItemTypeEnum.values();
        for (ItemTypeEnum s : v) {
            System.out.println(s.getDm() + "-" + s.getValue() + "-" + s.getDm());
        }
        System.out.println("dddddd");
        
    }
}
