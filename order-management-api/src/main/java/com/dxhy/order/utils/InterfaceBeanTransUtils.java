package com.dxhy.order.utils;

import com.dxhy.order.protocol.invoice.*;
import com.dxhy.order.protocol.order.*;
import com.dxhy.order.protocol.v4.invalid.ZFFPXX;
import com.dxhy.order.protocol.v4.invalid.ZFTSXX_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFXX_REQ;
import com.dxhy.order.protocol.v4.invoice.*;
import com.dxhy.order.protocol.v4.order.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口对象转换工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/4/16 14:09
 */
public class InterfaceBeanTransUtils {
    
    /**
     * V3订单开票接口转换为V4接口协议bean
     *
     * @param commonOrderReq
     * @return
     */
    public static DDPCXX_REQ transDdpcxxReq(COMMON_ORDER_REQ commonOrderReq) {
    
    
        DDPCXX_REQ ddpcxxReq = new DDPCXX_REQ();
    
        DDPCXX ddpcxx = new DDPCXX();
        ddpcxx.setDDQQPCH(commonOrderReq.getCOMMON_ORDER_BATCH().getDDQQPCH());
        ddpcxx.setNSRSBH(commonOrderReq.getCOMMON_ORDER_BATCH().getNSRSBH());
        ddpcxx.setKPZD(commonOrderReq.getCOMMON_ORDER_BATCH().getSLDID());
        ddpcxx.setFPLXDM(commonOrderReq.getCOMMON_ORDER_BATCH().getFPLB());
        ddpcxx.setKPFS(commonOrderReq.getCOMMON_ORDER_BATCH().getKPFS());
        ddpcxx.setCPYBS(commonOrderReq.getCOMMON_ORDER_BATCH().getSFCPY());
        ddpcxx.setKZZD(commonOrderReq.getCOMMON_ORDER_BATCH().getKZZD());
    
        List<DDZXX> ddzxxes = new ArrayList<>();
        if (commonOrderReq.getCOMMON_ORDERS() != null && commonOrderReq.getCOMMON_ORDERS().size() > 0) {
            for (COMMON_ORDER commonOrder : commonOrderReq.getCOMMON_ORDERS()) {
                DDZXX ddzxx = new DDZXX();
                DDTXX ddtxx = new DDTXX();
                ddtxx.setDDQQLSH(commonOrder.getCOMMON_ORDER_HEAD().getDDQQLSH());
                ddtxx.setNSRSBH(commonOrder.getCOMMON_ORDER_HEAD().getNSRSBH());
                ddtxx.setNSRMC(commonOrder.getCOMMON_ORDER_HEAD().getNSRMC());
                ddtxx.setKPLX(commonOrder.getCOMMON_ORDER_HEAD().getKPLX());
                ddtxx.setBMBBBH(commonOrder.getCOMMON_ORDER_HEAD().getBMB_BBH());
                ddtxx.setXHFSBH(commonOrder.getCOMMON_ORDER_HEAD().getXSF_NSRSBH());
                ddtxx.setXHFMC(commonOrder.getCOMMON_ORDER_HEAD().getXSF_MC());
                ddtxx.setXHFDZ(commonOrder.getCOMMON_ORDER_HEAD().getXSF_DZ());
                ddtxx.setXHFDH(commonOrder.getCOMMON_ORDER_HEAD().getXSF_DH());
                ddtxx.setXHFYH(commonOrder.getCOMMON_ORDER_HEAD().getXSF_YH());
                ddtxx.setXHFZH(commonOrder.getCOMMON_ORDER_HEAD().getXSF_ZH());
                ddtxx.setGMFBM(commonOrder.getCOMMON_ORDER_HEAD().getGMF_ID());
                ddtxx.setGMFSBH(commonOrder.getCOMMON_ORDER_HEAD().getGMF_NSRSBH());
                ddtxx.setGMFMC(commonOrder.getCOMMON_ORDER_HEAD().getGMF_MC());
                ddtxx.setGMFDZ(commonOrder.getCOMMON_ORDER_HEAD().getGMF_DZ());
                ddtxx.setGMFLX(commonOrder.getCOMMON_ORDER_HEAD().getGMF_QYLX());
                ddtxx.setGMFSF(commonOrder.getCOMMON_ORDER_HEAD().getGMF_SF());
                ddtxx.setGMFDH(commonOrder.getCOMMON_ORDER_HEAD().getGMF_GDDH());
                ddtxx.setGMFSJH(commonOrder.getCOMMON_ORDER_HEAD().getGMF_SJ());
                ddtxx.setGMFDZYX(commonOrder.getCOMMON_ORDER_HEAD().getGMF_EMAIL());
                ddtxx.setGMFYH(commonOrder.getCOMMON_ORDER_HEAD().getGMF_YH());
                ddtxx.setGMFZH(commonOrder.getCOMMON_ORDER_HEAD().getGMF_ZH());
                ddtxx.setKPR(commonOrder.getCOMMON_ORDER_HEAD().getKPR());
                ddtxx.setSKR(commonOrder.getCOMMON_ORDER_HEAD().getSKR());
                ddtxx.setFHR(commonOrder.getCOMMON_ORDER_HEAD().getFHR());
                ddtxx.setYFPDM(commonOrder.getCOMMON_ORDER_HEAD().getYFP_DM());
                ddtxx.setYFPHM(commonOrder.getCOMMON_ORDER_HEAD().getYFP_HM());
                ddtxx.setQDBZ(commonOrder.getCOMMON_ORDER_HEAD().getQD_BZ());
                ddtxx.setQDXMMC(commonOrder.getCOMMON_ORDER_HEAD().getQDXMMC());
                ddtxx.setJSHJ(commonOrder.getCOMMON_ORDER_HEAD().getJSHJ());
                ddtxx.setHJJE(commonOrder.getCOMMON_ORDER_HEAD().getHJJE());
                ddtxx.setHJSE(commonOrder.getCOMMON_ORDER_HEAD().getHJSE());
                ddtxx.setBZ(commonOrder.getCOMMON_ORDER_HEAD().getBZ());
                ddtxx.setCHYY(commonOrder.getCOMMON_ORDER_HEAD().getCHYY());
                ddtxx.setTSCHBZ(commonOrder.getCOMMON_ORDER_HEAD().getTSCHBZ());
                ddtxx.setDDH(commonOrder.getCOMMON_ORDER_HEAD().getDDH());
                ddtxx.setTHDH(commonOrder.getCOMMON_ORDER_HEAD().getTHDH());
                ddtxx.setDDSJ(commonOrder.getCOMMON_ORDER_HEAD().getDDDATE());
                ddtxx.setMDH(commonOrder.getCOMMON_ORDER_HEAD().getMDH());
                ddtxx.setYWLX(commonOrder.getCOMMON_ORDER_HEAD().getYWLX());
                ddtxx.setKPFS(commonOrder.getCOMMON_ORDER_HEAD().getKPFS());
                ddtxx.setTQM(commonOrder.getCOMMON_ORDER_HEAD().getTQM());
                ddtxx.setDTM(commonOrder.getCOMMON_ORDER_HEAD().getDYNAMIC_CODE_URL());
                ddtxx.setDDZT(commonOrder.getCOMMON_ORDER_HEAD().getORDER_STATUS());
                ddtxx.setDDZTXX(commonOrder.getCOMMON_ORDER_HEAD().getORDER_MESSAGE());
                ddtxx.setBYZD1(commonOrder.getCOMMON_ORDER_HEAD().getBYZD1());
                ddtxx.setBYZD2(commonOrder.getCOMMON_ORDER_HEAD().getBYZD2());
                ddtxx.setBYZD3(commonOrder.getCOMMON_ORDER_HEAD().getBYZD3());
                ddtxx.setBYZD4(commonOrder.getCOMMON_ORDER_HEAD().getBYZD4());
                ddtxx.setBYZD5(commonOrder.getCOMMON_ORDER_HEAD().getBYZD5());
            
                List<DDMXXX> ddmxxxList = new ArrayList<>();
                if (commonOrder.getORDER_INVOICE_ITEMS() != null && commonOrder.getORDER_INVOICE_ITEMS().size() > 0) {
                    for (ORDER_INVOICE_ITEM orderInvoiceItem : commonOrder.getORDER_INVOICE_ITEMS()) {
                        DDMXXX ddmxxx = new DDMXXX();
                        ddmxxx.setXH(orderInvoiceItem.getXMXH());
                        ddmxxx.setFPHXZ(orderInvoiceItem.getFPHXZ());
                        ddmxxx.setSPBM(orderInvoiceItem.getSPBM());
                        ddmxxx.setZXBM(orderInvoiceItem.getZXBM());
                        ddmxxx.setYHZCBS(orderInvoiceItem.getYHZCBS());
                        ddmxxx.setLSLBS(orderInvoiceItem.getLSLBS());
                        ddmxxx.setZZSTSGL(orderInvoiceItem.getZZSTSGL());
                        ddmxxx.setXMMC(orderInvoiceItem.getXMMC());
                        ddmxxx.setGGXH(orderInvoiceItem.getGGXH());
                        ddmxxx.setDW(orderInvoiceItem.getDW());
                        ddmxxx.setSPSL(orderInvoiceItem.getXMSL());
                        ddmxxx.setDJ(orderInvoiceItem.getXMDJ());
                        ddmxxx.setJE(orderInvoiceItem.getXMJE());
                        ddmxxx.setHSBZ(orderInvoiceItem.getHSBZ());
                        ddmxxx.setSL(orderInvoiceItem.getSL());
                        ddmxxx.setSE(orderInvoiceItem.getSE());
                        ddmxxx.setKCE(orderInvoiceItem.getKCE());
                        ddmxxx.setBYZD1(orderInvoiceItem.getBYZD1());
                        ddmxxx.setBYZD2(orderInvoiceItem.getBYZD2());
                        ddmxxx.setBYZD3(orderInvoiceItem.getBYZD3());
                        ddmxxxList.add(ddmxxx);
                    
                    }
                } else {
                    DDMXXX ddmxxx = new DDMXXX();
                    ddmxxxList.add(ddmxxx);
                }
            
            
                ddzxx.setDDTXX(ddtxx);
                ddzxx.setDDMXXX(ddmxxxList);
                ddzxxes.add(ddzxx);
            }
        }
    
    
        ddpcxxReq.setDDPCXX(ddpcxx);
        ddpcxxReq.setDDZXX(ddzxxes);
    
    
        return ddpcxxReq;
    }
    
    
    /**
     * 订单开票接口,V4接口协议bean转化为V3对外接口
     *
     * @param parseObject
     * @return
     */
    public static COMMON_ORDER_RSP transDdpcxxRsp(DDPCXX_RSP parseObject) {
        COMMON_ORDER_RSP commonOrderRsp = new COMMON_ORDER_RSP();
        commonOrderRsp.setDDQQPCH(parseObject.getDDQQPCH());
        commonOrderRsp.setSTATUS_CODE(parseObject.getZTDM());
        commonOrderRsp.setSTATUS_MESSAGE(parseObject.getZTXX());
    
        return commonOrderRsp;
    }
    
    /**
     * 开票结果查询接口,V3请求数据转化为V4
     *
     * @param parseObject
     * @return
     */
    public static DDKJXX_REQ transDdkjxxReq(GET_INVOICE_REQ parseObject) {
        DDKJXX_REQ ddkjxxReq = new DDKJXX_REQ();
        ddkjxxReq.setFPLXDM("51");
        ddkjxxReq.setSFFHSBSJ(parseObject.getRETURNFAIL());
        ddkjxxReq.setNSRSBH(parseObject.getNSRSBH());
        ddkjxxReq.setDDQQPCH(parseObject.getDDQQPCH());
    
    
        return ddkjxxReq;
    }
    
    
    /**
     * 开票结果查询接口,V4结果数据转换为V3
     *
     * @param parseObject
     * @return
     */
    public static GET_INVOICE_RSP transDdkjxxRsp(DDKJXX_RSP parseObject) {
        GET_INVOICE_RSP getInvoiceRsp = new GET_INVOICE_RSP();
        getInvoiceRsp.setDDQQPCH(parseObject.getDDQQPCH());
        getInvoiceRsp.setSTATUS_CODE(parseObject.getZTDM());
        getInvoiceRsp.setSTATUS_MESSAGE(parseObject.getZTXX());
    
        List<COMMON_INVOICE_INFO> commonInvoiceInfos = new ArrayList<>();
        if (parseObject.getFPZXX() != null && parseObject.getFPZXX().size() > 0) {
            for (FPZXX fpzxx : parseObject.getFPZXX()) {
                COMMON_INVOICE_INFO commonInvoiceInfo = new COMMON_INVOICE_INFO();
                commonInvoiceInfo.setDDQQLSH(fpzxx.getDDQQLSH());
                commonInvoiceInfo.setJQBH(fpzxx.getJQBH());
                commonInvoiceInfo.setFP_DM(fpzxx.getFPDM());
                commonInvoiceInfo.setFP_HM(fpzxx.getFPHM());
                commonInvoiceInfo.setKPRQ(fpzxx.getKPRQ());
                commonInvoiceInfo.setJYM(fpzxx.getJYM());
                commonInvoiceInfo.setFWM(fpzxx.getFWM());
                commonInvoiceInfo.setSTATUS_CODE(fpzxx.getZTDM());
                commonInvoiceInfo.setSTATUS_MESSAGE(fpzxx.getZTXX());
            
                commonInvoiceInfos.add(commonInvoiceInfo);
            }
        } else {
            COMMON_INVOICE_INFO commonInvoiceInfo = new COMMON_INVOICE_INFO();
            commonInvoiceInfos.add(commonInvoiceInfo);
        
        }
        getInvoiceRsp.setCOMMON_INVOICE_INFOS(commonInvoiceInfos);
    
    
        return getInvoiceRsp;
    }
    
    
    /**
     * 作废接口,V3请求数据转换为V4数据
     */
    public static ZFXX_REQ transZfxxReq(INVALID_INVOICE_REQ invalidInvoiceReq) {
        ZFXX_REQ zfxxReq = new ZFXX_REQ();
        zfxxReq.setZFPCH(invalidInvoiceReq.getZFPCH());
        zfxxReq.setXHFSBH(invalidInvoiceReq.getNSRSBH());
        zfxxReq.setFPDM(invalidInvoiceReq.getFP_DM());
        zfxxReq.setFPQH(invalidInvoiceReq.getFPQH());
        zfxxReq.setFPZH(invalidInvoiceReq.getFPZH());
        zfxxReq.setZFLX(invalidInvoiceReq.getZFLX());
        zfxxReq.setZFYY(invalidInvoiceReq.getZFYY());
        
        return zfxxReq;
    }
    
    /**
     * 订单和发票查询接口,V3请求数据转化为V4
     */
    public static DDFPCX_REQ transDdfpcxReq(ORDER_REQUEST orderRequest) {
        DDFPCX_REQ ddfpcxReq = new DDFPCX_REQ();
        ddfpcxReq.setNSRSBH(orderRequest.getNSRSBH());
        String ddqqlsh = orderRequest.getFPQQLSH();
        if (StringUtils.isNotBlank(orderRequest.getDDQQLSH())) {
            ddqqlsh = orderRequest.getDDQQLSH();
        }
        ddfpcxReq.setDDQQLSH(ddqqlsh);
        ddfpcxReq.setTQM(orderRequest.getTQM());
        ddfpcxReq.setDDH(orderRequest.getDDH());
        
        
        return ddfpcxReq;
    }
    
    
    /**
     * 订单和发票查询,以及推送接口,V4数据转化为V3数据
     */
    public static ORDER_INVOICE_RESPONSE transDdfpcxRsp(DDFPCX_RSP ddfpcxRsp) {
        ORDER_INVOICE_RESPONSE orderInvoiceResponse = new ORDER_INVOICE_RESPONSE();
        List<COMMON_ORDER_INVOICE> commonOrderInvoices = new ArrayList<>();
        
        if (ddfpcxRsp.getDDFPZXX() != null && ddfpcxRsp.getDDFPZXX().size() > 0) {
            for (DDFPZXX ddfpzxx : ddfpcxRsp.getDDFPZXX()) {
                COMMON_ORDER_INVOICE commonOrderInvoice = new COMMON_ORDER_INVOICE();
                List<ORDER_EXTENSION_INFO> orderExtensionInfos = new ArrayList<>();
                List<ORDER_INVOICE_ITEM> orderInvoiceItems = new ArrayList<>();
                ORDER_INVOICE_INFO orderInvoiceInfo = new ORDER_INVOICE_INFO();
                orderInvoiceInfo.setKPJH(ddfpzxx.getDDFPXX().getKPJH());
                orderInvoiceInfo.setSLD(ddfpzxx.getDDFPXX().getKPZD());
                /**
                 * V3版本发票种类代码处理,由底层的004,007,026改为对应的0,2,51
                 */
                String fpzldm = ddfpzxx.getDDFPXX().getFPLXDM();
                if (StringUtils.isNotBlank(fpzldm)) {
                    if ("004".equals(fpzldm) || "0".equals(fpzldm)) {
                        orderInvoiceInfo.setFPZLDM("0");
                    } else if ("007".equals(fpzldm) || "2".equals(fpzldm)) {
                        orderInvoiceInfo.setFPZLDM("2");
                    } else if ("026".equals(fpzldm) || "51".equals(fpzldm)) {
                        orderInvoiceInfo.setFPZLDM("51");
                    }
                }
                
                orderInvoiceInfo.setJQBH(ddfpzxx.getDDFPXX().getJQBH());
                orderInvoiceInfo.setFP_DM(ddfpzxx.getDDFPXX().getFPDM());
                orderInvoiceInfo.setFP_HM(ddfpzxx.getDDFPXX().getFPHM());
                orderInvoiceInfo.setKPRQ(ddfpzxx.getDDFPXX().getKPRQ());
                orderInvoiceInfo.setJYM(ddfpzxx.getDDFPXX().getJYM());
                orderInvoiceInfo.setFWM(ddfpzxx.getDDFPXX().getFWM());
                orderInvoiceInfo.setPDF_FILE(ddfpzxx.getDDFPXX().getPDFZJL());
                orderInvoiceInfo.setPDF_URL(ddfpzxx.getDDFPXX().getPDFDZ());
                orderInvoiceInfo.setDDQQLSH(ddfpzxx.getDDFPXX().getDDQQLSH());
                orderInvoiceInfo.setNSRSBH(ddfpzxx.getDDFPXX().getNSRSBH());
                orderInvoiceInfo.setNSRMC(ddfpzxx.getDDFPXX().getNSRMC());
                orderInvoiceInfo.setKPLX(ddfpzxx.getDDFPXX().getKPLX());
                orderInvoiceInfo.setBMB_BBH(ddfpzxx.getDDFPXX().getBMBBBH());
                orderInvoiceInfo.setXSF_NSRSBH(ddfpzxx.getDDFPXX().getXHFSBH());
                orderInvoiceInfo.setXSF_MC(ddfpzxx.getDDFPXX().getXHFMC());
                orderInvoiceInfo.setXSF_DZ(ddfpzxx.getDDFPXX().getXHFDZ());
                orderInvoiceInfo.setXSF_DH(ddfpzxx.getDDFPXX().getXHFDH());
                orderInvoiceInfo.setXSF_YH(ddfpzxx.getDDFPXX().getXHFYH());
                orderInvoiceInfo.setXSF_ZH(ddfpzxx.getDDFPXX().getXHFZH());
                orderInvoiceInfo.setGMF_ID(ddfpzxx.getDDFPXX().getGMFBM());
                orderInvoiceInfo.setGMF_NSRSBH(ddfpzxx.getDDFPXX().getGMFSBH());
                orderInvoiceInfo.setGMF_MC(ddfpzxx.getDDFPXX().getGMFMC());
                orderInvoiceInfo.setGMF_DZ(ddfpzxx.getDDFPXX().getGMFDZ());
                orderInvoiceInfo.setGMF_QYLX(ddfpzxx.getDDFPXX().getGMFLX());
                orderInvoiceInfo.setGMF_SF(ddfpzxx.getDDFPXX().getGMFSF());
                orderInvoiceInfo.setGMF_GDDH(ddfpzxx.getDDFPXX().getGMFDH());
                orderInvoiceInfo.setGMF_YH(ddfpzxx.getDDFPXX().getGMFYH());
                orderInvoiceInfo.setGMF_ZH(ddfpzxx.getDDFPXX().getGMFZH());
                orderInvoiceInfo.setGMF_SJ(ddfpzxx.getDDFPXX().getGMFSJH());
                orderInvoiceInfo.setGMF_EMAIL(ddfpzxx.getDDFPXX().getGMFDZYX());
                orderInvoiceInfo.setKPR(ddfpzxx.getDDFPXX().getKPR());
                orderInvoiceInfo.setSKR(ddfpzxx.getDDFPXX().getSKR());
                orderInvoiceInfo.setFHR(ddfpzxx.getDDFPXX().getFHR());
                orderInvoiceInfo.setYFP_DM(ddfpzxx.getDDFPXX().getYFPDM());
                orderInvoiceInfo.setYFP_HM(ddfpzxx.getDDFPXX().getYFPHM());
                orderInvoiceInfo.setQD_BZ(ddfpzxx.getDDFPXX().getQDBZ());
                orderInvoiceInfo.setQDXMMC(ddfpzxx.getDDFPXX().getQDXMMC());
                orderInvoiceInfo.setJSHJ(ddfpzxx.getDDFPXX().getJSHJ());
                orderInvoiceInfo.setHJJE(ddfpzxx.getDDFPXX().getHJJE());
                orderInvoiceInfo.setHJSE(ddfpzxx.getDDFPXX().getHJSE());
                orderInvoiceInfo.setBZ(ddfpzxx.getDDFPXX().getBZ());
                orderInvoiceInfo.setCHYY(ddfpzxx.getDDFPXX().getCHYY());
                orderInvoiceInfo.setTSCHBZ(ddfpzxx.getDDFPXX().getTSCHBZ());
                orderInvoiceInfo.setDDH(ddfpzxx.getDDFPXX().getDDH());
                orderInvoiceInfo.setTHDH(ddfpzxx.getDDFPXX().getTHDH());
                orderInvoiceInfo.setDDDATE(ddfpzxx.getDDFPXX().getDDSJ());
                orderInvoiceInfo.setMDH(ddfpzxx.getDDFPXX().getMDH());
                orderInvoiceInfo.setYWLX(ddfpzxx.getDDFPXX().getYWLX());
                orderInvoiceInfo.setKPFS(ddfpzxx.getDDFPXX().getKPFS());
                orderInvoiceInfo.setTQM(ddfpzxx.getDDFPXX().getTQM());
                orderInvoiceInfo.setDYNAMIC_CODE_URL(ddfpzxx.getDDFPXX().getDTM());
                orderInvoiceInfo.setORDER_STATUS(ddfpzxx.getDDFPXX().getDDZT());
                orderInvoiceInfo.setORDER_MESSAGE(ddfpzxx.getDDFPXX().getDDZTXX());
                orderInvoiceInfo.setBYZD1(ddfpzxx.getDDFPXX().getBYZD1());
                orderInvoiceInfo.setBYZD2(ddfpzxx.getDDFPXX().getBYZD2());
                orderInvoiceInfo.setBYZD3(ddfpzxx.getDDFPXX().getBYZD3());
                orderInvoiceInfo.setBYZD4(ddfpzxx.getDDFPXX().getBYZD4());
                orderInvoiceInfo.setBYZD5(ddfpzxx.getDDFPXX().getBYZD5());
                
                if (ddfpzxx.getDDKZXX() != null && ddfpzxx.getDDKZXX().size() > 0) {
                    for (DDKZXX ddkzxx : ddfpzxx.getDDKZXX()) {
                        ORDER_EXTENSION_INFO orderExtensionInfo = new ORDER_EXTENSION_INFO();
                        orderExtensionInfo.setDDQQLSH(ddkzxx.getDDQQLSH());
                        orderExtensionInfo.setDDH(ddkzxx.getDDH());
                        orderExtensionInfo.setDDLX(ddkzxx.getDDLX());
                        orderExtensionInfo.setBYZD1(ddkzxx.getBYZD1());
                        orderExtensionInfo.setBYZD2(ddkzxx.getBYZD2());
                        orderExtensionInfo.setBYZD3(ddkzxx.getBYZD3());
                        orderExtensionInfos.add(orderExtensionInfo);
                        
                    }
                }
    
                if (ddfpzxx.getDDMXXX() != null && ddfpzxx.getDDMXXX().size() > 0) {
                    for (DDMXXX ddmxxx : ddfpzxx.getDDMXXX()) {
                        ORDER_INVOICE_ITEM orderInvoiceItem = new ORDER_INVOICE_ITEM();
                        orderInvoiceItem.setXMXH(ddmxxx.getXH());
                        orderInvoiceItem.setFPHXZ(ddmxxx.getFPHXZ());
                        orderInvoiceItem.setSPBM(ddmxxx.getSPBM());
                        orderInvoiceItem.setZXBM(ddmxxx.getZXBM());
                        orderInvoiceItem.setYHZCBS(ddmxxx.getYHZCBS());
                        orderInvoiceItem.setLSLBS(ddmxxx.getLSLBS());
                        orderInvoiceItem.setZZSTSGL(ddmxxx.getZZSTSGL());
                        orderInvoiceItem.setXMMC(ddmxxx.getXMMC());
                        orderInvoiceItem.setGGXH(ddmxxx.getGGXH());
                        orderInvoiceItem.setDW(ddmxxx.getDW());
                        orderInvoiceItem.setXMSL(ddmxxx.getSPSL());
                        orderInvoiceItem.setXMDJ(ddmxxx.getDJ());
                        orderInvoiceItem.setXMJE(ddmxxx.getJE());
                        orderInvoiceItem.setHSBZ(ddmxxx.getHSBZ());
                        orderInvoiceItem.setSL(ddmxxx.getSL());
                        orderInvoiceItem.setSE(ddmxxx.getSE());
                        orderInvoiceItem.setKCE(ddmxxx.getKCE());
                        orderInvoiceItem.setBYZD1(ddmxxx.getBYZD1());
                        orderInvoiceItem.setBYZD2(ddmxxx.getBYZD2());
                        orderInvoiceItem.setBYZD3(ddmxxx.getBYZD3());
                        orderInvoiceItems.add(orderInvoiceItem);
    
                    }
                }
                
                
                commonOrderInvoice.setORDER_INVOICE_INFO(orderInvoiceInfo);
                commonOrderInvoice.setORDER_EXTENSION_INFOS(orderExtensionInfos);
                commonOrderInvoice.setORDER_INVOICE_ITEMS(orderInvoiceItems);
                
                commonOrderInvoices.add(commonOrderInvoice);
            }
        }
        
        
        orderInvoiceResponse.setCOMMON_ORDER_INVOICES(commonOrderInvoices);
        orderInvoiceResponse.setSTATUS_CODE(ddfpcxRsp.getZTDM());
        orderInvoiceResponse.setSTATUS_MESSAGE(ddfpcxRsp.getZTXX());
        
        
        return orderInvoiceResponse;
    }
    
    /**
     * 二维码生成接口,请求参数V3转换为V4数据
     */
    public static DDZXX transDdzxxReq(DYNAMIC_COMMON_ORDER dynamicCommonOrder) {
        DDZXX ddzxx = new DDZXX();
        List<DDMXXX> ddmxxxList = new ArrayList<>();
        
        DDTXX ddtxx = new DDTXX();
        if (dynamicCommonOrder != null && dynamicCommonOrder.getCOMMON_ORDER_HEAD() != null) {
            ddtxx.setDDQQLSH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getDDQQLSH());
            ddtxx.setNSRSBH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getNSRSBH());
            ddtxx.setNSRMC(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getNSRMC());
            ddtxx.setKPLX(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getKPLX());
            ddtxx.setBMBBBH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBMB_BBH());
            ddtxx.setXHFSBH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_NSRSBH());
            ddtxx.setXHFMC(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_MC());
            ddtxx.setXHFDZ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_DZ());
            ddtxx.setXHFDH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_DH());
            ddtxx.setXHFYH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_YH());
            ddtxx.setXHFZH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getXSF_ZH());
            ddtxx.setGMFBM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_ID());
            ddtxx.setGMFSBH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_NSRSBH());
            ddtxx.setGMFMC(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_MC());
            ddtxx.setGMFDZ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_DZ());
            ddtxx.setGMFLX(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_QYLX());
            ddtxx.setGMFSF(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_SF());
            ddtxx.setGMFDH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_GDDH());
            ddtxx.setGMFSJH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_SJ());
            ddtxx.setGMFDZYX(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_EMAIL());
            ddtxx.setGMFYH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_YH());
            ddtxx.setGMFZH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getGMF_ZH());
            ddtxx.setKPR(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getKPR());
            ddtxx.setSKR(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getSKR());
            ddtxx.setFHR(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getFHR());
            ddtxx.setYFPDM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getYFP_DM());
            ddtxx.setYFPHM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getYFP_HM());
            ddtxx.setQDBZ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getQD_BZ());
            ddtxx.setQDXMMC(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getQDXMMC());
            ddtxx.setJSHJ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getJSHJ());
            ddtxx.setHJJE(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getHJJE());
            ddtxx.setHJSE(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getHJSE());
            ddtxx.setBZ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBZ());
            ddtxx.setCHYY(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getCHYY());
            ddtxx.setTSCHBZ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getTSCHBZ());
            ddtxx.setDDH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getDDH());
            ddtxx.setTHDH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getTHDH());
            ddtxx.setDDSJ(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getDDDATE());
            ddtxx.setMDH(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getMDH());
            ddtxx.setYWLX(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getYWLX());
            ddtxx.setKPFS(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getKPFS());
            ddtxx.setTQM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getTQM());
            ddtxx.setDTM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getDYNAMIC_CODE_URL());
            ddtxx.setDDZT(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getORDER_STATUS());
            ddtxx.setDDZTXX(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getORDER_MESSAGE());
            ddtxx.setFPLXDM(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getFPZLDM());
            ddtxx.setBYZD1(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBYZD1());
            ddtxx.setBYZD2(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBYZD2());
            ddtxx.setBYZD3(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBYZD3());
            ddtxx.setBYZD4(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBYZD4());
            ddtxx.setBYZD5(dynamicCommonOrder.getCOMMON_ORDER_HEAD().getBYZD5());
        }
        
        if (dynamicCommonOrder != null && dynamicCommonOrder.getORDER_INVOICE_ITEMS() != null && dynamicCommonOrder.getORDER_INVOICE_ITEMS().size() > 0) {
            for (int i = 0; i < dynamicCommonOrder.getORDER_INVOICE_ITEMS().size(); i++) {
                DDMXXX ddmxxx = new DDMXXX();
                ddmxxx.setXH(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getXMXH());
                ddmxxx.setFPHXZ(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getFPHXZ());
                ddmxxx.setSPBM(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getSPBM());
                ddmxxx.setZXBM(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getZXBM());
                ddmxxx.setYHZCBS(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getYHZCBS());
                ddmxxx.setLSLBS(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getLSLBS());
                ddmxxx.setZZSTSGL(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getZZSTSGL());
                ddmxxx.setXMMC(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getXMMC());
                ddmxxx.setGGXH(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getGGXH());
                ddmxxx.setDW(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getDW());
                ddmxxx.setSPSL(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getXMSL());
                ddmxxx.setDJ(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getXMDJ());
                ddmxxx.setJE(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getXMJE());
                ddmxxx.setHSBZ(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getHSBZ());
                ddmxxx.setSL(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getSL());
                ddmxxx.setSE(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getSE());
                ddmxxx.setKCE(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getKCE());
                ddmxxx.setBYZD1(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getBYZD1());
                ddmxxx.setBYZD2(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getBYZD2());
                ddmxxx.setBYZD3(dynamicCommonOrder.getORDER_INVOICE_ITEMS().get(i).getBYZD3());
                ddmxxxList.add(ddmxxx);
                
                
            }
        }
        
        
        ddzxx.setDDTXX(ddtxx);
        ddzxx.setDDMXXX(ddmxxxList);
        return ddzxx;
    }
    
    
    /**
     * 红字申请单上传接口,V3请求数据转化为V4
     */
    public static HZSQDSC_REQ transHzsqdscReq(RED_INVOICE_FORM_REQ redInvoiceFormReq) {
        HZSQDSC_REQ hzsqdscReq = new HZSQDSC_REQ();
        HZSQDSCPC hzsqdscpc = new HZSQDSCPC();
        if (redInvoiceFormReq != null && redInvoiceFormReq.getRED_INVOICE_FORM_BATCH() != null) {
            hzsqdscpc.setSQBSCQQPCH(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getSQBSCQQPCH());
            hzsqdscpc.setNSRSBH(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getNSRSBH());
            hzsqdscpc.setKPZD(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getSLDID());
            hzsqdscpc.setFPLXDM(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getFPLB());
            hzsqdscpc.setSQLB(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getSQLB());
            hzsqdscpc.setKZZD(redInvoiceFormReq.getRED_INVOICE_FORM_BATCH().getKZZD());
        }
        
        hzsqdscReq.setHZSQDSCPC(hzsqdscpc);
        
        List<HZSQDSCZXX> hzsqdsczxxes = new ArrayList<>();
        if (redInvoiceFormReq != null && redInvoiceFormReq.getRED_INVOICE_FORM_UPLOADS() != null && redInvoiceFormReq.getRED_INVOICE_FORM_UPLOADS().size() > 0) {
            for (RED_INVOICE_FORM_UPLOAD redInvoiceFormUpload : redInvoiceFormReq.getRED_INVOICE_FORM_UPLOADS()) {
                HZSQDSCZXX hzsqdsczxx = new HZSQDSCZXX();
                HZSQDTXX hzsqdtxx = new HZSQDTXX();
                hzsqdtxx.setSQBSCQQLSH(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getSQBSCQQLSH());
                hzsqdtxx.setXXBLX(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getXXBLX());
                hzsqdtxx.setYFPDM(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getYFP_DM());
                hzsqdtxx.setYFPHM(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getYFP_HM());
                hzsqdtxx.setYYSBZ(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getYYSBZ());
                hzsqdtxx.setYFPKPRQ(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getYFP_KPRQ());
                hzsqdtxx.setTKSJ(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getTKSJ());
                hzsqdtxx.setXHFSBH(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getXSF_NSRSBH());
                hzsqdtxx.setXHFMC(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getXSF_MC());
                hzsqdtxx.setGMFSBH(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getGMF_NSRSBH());
                hzsqdtxx.setGMFMC(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getGMF_MC());
                hzsqdtxx.setHJJE(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getHJJE());
                hzsqdtxx.setHJSE(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getHJSE());
                hzsqdtxx.setSQSM(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getSQSM());
                hzsqdtxx.setBMBBBH(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getBMB_BBH());
                hzsqdtxx.setKZZD1(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getKZZD1());
                hzsqdtxx.setKZZD2(redInvoiceFormUpload.getRED_INVOICE_FORM_HEAD().getKZZD2());
                hzsqdsczxx.setHZSQDTXX(hzsqdtxx);
                
                List<DDMXXX> ddmxxxList = new ArrayList<>();
                
                if (redInvoiceFormUpload != null && redInvoiceFormUpload.getORDER_INVOICE_ITEMS() != null && redInvoiceFormUpload.getORDER_INVOICE_ITEMS().size() > 0) {
                    for (ORDER_INVOICE_ITEM orderInvoiceItem : redInvoiceFormUpload.getORDER_INVOICE_ITEMS()) {
                        DDMXXX ddmxxx = new DDMXXX();
                        ddmxxx.setXH(orderInvoiceItem.getXMXH());
                        ddmxxx.setFPHXZ(orderInvoiceItem.getFPHXZ());
                        ddmxxx.setSPBM(orderInvoiceItem.getSPBM());
                        ddmxxx.setZXBM(orderInvoiceItem.getZXBM());
                        ddmxxx.setYHZCBS(orderInvoiceItem.getYHZCBS());
                        ddmxxx.setLSLBS(orderInvoiceItem.getLSLBS());
                        ddmxxx.setZZSTSGL(orderInvoiceItem.getZZSTSGL());
                        ddmxxx.setXMMC(orderInvoiceItem.getXMMC());
                        ddmxxx.setGGXH(orderInvoiceItem.getGGXH());
                        ddmxxx.setDW(orderInvoiceItem.getDW());
                        ddmxxx.setSPSL(orderInvoiceItem.getXMSL());
                        ddmxxx.setDJ(orderInvoiceItem.getXMDJ());
                        ddmxxx.setJE(orderInvoiceItem.getXMJE());
                        ddmxxx.setHSBZ(orderInvoiceItem.getHSBZ());
                        ddmxxx.setSL(orderInvoiceItem.getSL());
                        ddmxxx.setSE(orderInvoiceItem.getSE());
                        ddmxxx.setKCE(orderInvoiceItem.getKCE());
                        ddmxxx.setBYZD1(orderInvoiceItem.getBYZD1());
                        ddmxxx.setBYZD2(orderInvoiceItem.getBYZD2());
                        ddmxxx.setBYZD3(orderInvoiceItem.getBYZD3());
                        ddmxxxList.add(ddmxxx);
                        
                        
                    }
                }
    
                hzsqdsczxx.setDDMXXX(ddmxxxList);
    
                hzsqdsczxxes.add(hzsqdsczxx);
            }
        }
        
        hzsqdscReq.setHZSQDSCZXX(hzsqdsczxxes);
        
        
        return hzsqdscReq;
    }
    
    
    /**
     * 红字申请单上传接口,V4返回数据转化为V3
     */
    public static RED_INVOICE_FORM_RSP transHzsqdscRsp(HZSQDSC_RSP hzsqdscRsp) {
        RED_INVOICE_FORM_RSP redInvoiceFormRsp = new RED_INVOICE_FORM_RSP();
        
        List<RED_INVOICE_FORM_UPLOAD_RESPONSE> redInvoiceFormUploadResponses = new ArrayList<>();
        if (hzsqdscRsp != null) {
            redInvoiceFormRsp.setSQBSCQQPCH(hzsqdscRsp.getSQBSCQQPCH());
            redInvoiceFormRsp.setSTATUS_CODE(hzsqdscRsp.getZTDM());
            redInvoiceFormRsp.setSTATUS_MESSAGE(hzsqdscRsp.getZTXX());
            if (hzsqdscRsp.getHZSQDSCJG() != null && hzsqdscRsp.getHZSQDSCJG().size() > 0) {
                for (HZSQDSCJG hzsqdscjg : hzsqdscRsp.getHZSQDSCJG()) {
                    RED_INVOICE_FORM_UPLOAD_RESPONSE redInvoiceFormUploadResponse = new RED_INVOICE_FORM_UPLOAD_RESPONSE();
                    redInvoiceFormUploadResponse.setSQBSCQQLSH(hzsqdscjg.getSQBSCQQLSH());
                    redInvoiceFormUploadResponse.setSQDH(hzsqdscjg.getSQDH());
                    redInvoiceFormUploadResponse.setSTATUS_CODE(hzsqdscjg.getZTDM());
                    redInvoiceFormUploadResponse.setSTATUS_MESSAGE(hzsqdscjg.getZTXX());
                    redInvoiceFormUploadResponse.setXXBBH(hzsqdscjg.getXXBBH());
                    redInvoiceFormUploadResponses.add(redInvoiceFormUploadResponse);
                    
                }
                redInvoiceFormRsp.setRED_INVOICE_FORM_UPLOAD_RESPONSES(redInvoiceFormUploadResponses);
            }
            
            
        }
        
        
        return redInvoiceFormRsp;
    }
    
    /**
     * 红字申请单下载接口,V3请求数据转化为V4
     */
    public static HZSQDXZ_REQ transHzsqdxzReq(RED_INVOICE_FORM_DOWNLOAD_REQ redInvoiceFormDownloadReq) {
        HZSQDXZ_REQ hzsqdxzReq = new HZSQDXZ_REQ();
        if (redInvoiceFormDownloadReq != null) {
            hzsqdxzReq.setSQBXZQQPCH(redInvoiceFormDownloadReq.getSQBXZQQPCH());
            hzsqdxzReq.setNSRSBH(redInvoiceFormDownloadReq.getNSRSBH());
            hzsqdxzReq.setFPLXDM(redInvoiceFormDownloadReq.getFPLB());
            hzsqdxzReq.setTKRQQ(redInvoiceFormDownloadReq.getTKRQ_Q());
            hzsqdxzReq.setTKRQZ(redInvoiceFormDownloadReq.getTKRQ_Z());
            hzsqdxzReq.setGMFSBH(redInvoiceFormDownloadReq.getGMF_NSRSBH());
            hzsqdxzReq.setXHFSBH(redInvoiceFormDownloadReq.getXSF_NSRSBH());
            hzsqdxzReq.setXXBBH(redInvoiceFormDownloadReq.getXXBBH());
            hzsqdxzReq.setXXBFW(redInvoiceFormDownloadReq.getXXBFW());
            hzsqdxzReq.setYS(redInvoiceFormDownloadReq.getPAGENO());
            hzsqdxzReq.setGS(redInvoiceFormDownloadReq.getPAGESIZE());
            
        }
        
        
        return hzsqdxzReq;
    }
    
    /**
     * 红字申请单下载接口,V4返回数据转化为V3
     */
    public static RED_INVOICE_FORM_DOWNLOAD_RSP transHzsqdxzRsp(HZSQDXZ_RSP hzsqdxzRsp) {
        RED_INVOICE_FORM_DOWNLOAD_RSP redInvoiceFormDownloadRsp = new RED_INVOICE_FORM_DOWNLOAD_RSP();
        if (hzsqdxzRsp != null) {
            redInvoiceFormDownloadRsp.setSQBXZQQPCH(hzsqdxzRsp.getSQBXZQQPCH());
            redInvoiceFormDownloadRsp.setSUCCESS_COUNT(hzsqdxzRsp.getCGGS());
            
            redInvoiceFormDownloadRsp.setSTATUS_CODE(hzsqdxzRsp.getZTDM());
            redInvoiceFormDownloadRsp.setSTATUS_MESSAGE(hzsqdxzRsp.getZTXX());
            
            
            if (hzsqdxzRsp.getHZSQDXZZXX() != null && hzsqdxzRsp.getHZSQDXZZXX().size() > 0) {
                List<RED_INVOICE_FORM_DOWNLOAD> redInvoiceFormDownloads = new ArrayList<>();
                for (HZSQDXZZXX hzsqdxzzxx : hzsqdxzRsp.getHZSQDXZZXX()) {
                    String fplx = "";
                    
                    
                    if ("51".equals(hzsqdxzzxx.getHZSQDXZTXX().getFPLXDM())) {
                        fplx = "2";
                    } else {
                        fplx = "1";
                    }
                    RED_INVOICE_FORM_DOWNLOAD redInvoiceFormDownload = new RED_INVOICE_FORM_DOWNLOAD();
                    RED_INVOICE_FORM_DOWN_HEAD redInvoiceFormDownHead = new RED_INVOICE_FORM_DOWN_HEAD();
                    List<ORDER_INVOICE_ITEM> orderInvoiceItems = new ArrayList<>();
                    redInvoiceFormDownHead.setSQDH(hzsqdxzzxx.getHZSQDXZTXX().getSQDH());
                    redInvoiceFormDownHead.setXXBBH(hzsqdxzzxx.getHZSQDXZTXX().getXXBBH());
                    redInvoiceFormDownHead.setSTATUS_CODE(hzsqdxzzxx.getHZSQDXZTXX().getZTDM());
                    redInvoiceFormDownHead.setSTATUS_MESSAGE(hzsqdxzzxx.getHZSQDXZTXX().getZTXX());
                    redInvoiceFormDownHead.setYFP_DM(hzsqdxzzxx.getHZSQDXZTXX().getYFPDM());
                    redInvoiceFormDownHead.setYFP_HM(hzsqdxzzxx.getHZSQDXZTXX().getYFPHM());
                    redInvoiceFormDownHead.setFPLX(fplx);
                    redInvoiceFormDownHead.setFPLB(hzsqdxzzxx.getHZSQDXZTXX().getFPLXDM());
                    redInvoiceFormDownHead.setDSLBZ(hzsqdxzzxx.getHZSQDXZTXX().getDSLBZ());
                    redInvoiceFormDownHead.setTKSJ(hzsqdxzzxx.getHZSQDXZTXX().getTKSJ());
                    redInvoiceFormDownHead.setXSF_NSRSBH(hzsqdxzzxx.getHZSQDXZTXX().getXHFSBH());
                    redInvoiceFormDownHead.setXSF_MC(hzsqdxzzxx.getHZSQDXZTXX().getXHFMC());
                    redInvoiceFormDownHead.setGMF_NSRSBH(hzsqdxzzxx.getHZSQDXZTXX().getGMFSBH());
                    redInvoiceFormDownHead.setGMF_MC(hzsqdxzzxx.getHZSQDXZTXX().getGMFMC());
                    redInvoiceFormDownHead.setHJJE(hzsqdxzzxx.getHZSQDXZTXX().getHJJE());
                    redInvoiceFormDownHead.setHJSE(hzsqdxzzxx.getHZSQDXZTXX().getHJSE());
                    redInvoiceFormDownHead.setSQSM(hzsqdxzzxx.getHZSQDXZTXX().getSQSM());
                    redInvoiceFormDownHead.setBMB_BBH(hzsqdxzzxx.getHZSQDXZTXX().getBMBBBH());
                    redInvoiceFormDownHead.setYYSBZ(hzsqdxzzxx.getHZSQDXZTXX().getYYSBZ());
                    
                    if (hzsqdxzzxx.getDDMXXX() != null && hzsqdxzzxx.getDDMXXX().size() > 0) {
                        for (DDMXXX ddmxxx : hzsqdxzzxx.getDDMXXX()) {
                            ORDER_INVOICE_ITEM orderInvoiceItem = new ORDER_INVOICE_ITEM();
                            orderInvoiceItem.setXMXH(ddmxxx.getXH());
                            orderInvoiceItem.setFPHXZ(ddmxxx.getFPHXZ());
                            orderInvoiceItem.setSPBM(ddmxxx.getSPBM());
                            orderInvoiceItem.setZXBM(ddmxxx.getZXBM());
                            orderInvoiceItem.setYHZCBS(ddmxxx.getYHZCBS());
                            orderInvoiceItem.setLSLBS(ddmxxx.getLSLBS());
                            orderInvoiceItem.setZZSTSGL(ddmxxx.getZZSTSGL());
                            orderInvoiceItem.setXMMC(ddmxxx.getXMMC());
                            orderInvoiceItem.setGGXH(ddmxxx.getGGXH());
                            orderInvoiceItem.setDW(ddmxxx.getDW());
                            orderInvoiceItem.setXMSL(ddmxxx.getSPSL());
                            orderInvoiceItem.setXMDJ(ddmxxx.getDJ());
                            orderInvoiceItem.setXMJE(ddmxxx.getJE());
                            orderInvoiceItem.setHSBZ(ddmxxx.getHSBZ());
                            orderInvoiceItem.setSL(ddmxxx.getSL());
                            orderInvoiceItem.setSE(ddmxxx.getSE());
                            orderInvoiceItem.setKCE(ddmxxx.getKCE());
                            orderInvoiceItem.setBYZD1(ddmxxx.getBYZD1());
                            orderInvoiceItem.setBYZD2(ddmxxx.getBYZD2());
                            orderInvoiceItem.setBYZD3(ddmxxx.getBYZD3());
                            
                            orderInvoiceItems.add(orderInvoiceItem);
                        }
                    }
                    
                    
                    redInvoiceFormDownload.setRED_INVOICE_FORM_DOWN_HEAD(redInvoiceFormDownHead);
                    redInvoiceFormDownload.setORDER_INVOICE_ITEMS(orderInvoiceItems);
                    
                    
                    redInvoiceFormDownloads.add(redInvoiceFormDownload);
                    
                }
                
                redInvoiceFormDownloadRsp.setRED_INVOICE_FORM_DOWNLOADS(redInvoiceFormDownloads);
            }
    
        }
        
        
        return redInvoiceFormDownloadRsp;
    }
    
    /**
     * 红字申请单下载接口,V3请求数据转化为V4
     */
    public static ZFTSXX_REQ transZftsReq(INVALID_INVOICES_RSP invalidInvoicesRsp) {
        ZFTSXX_REQ zftsxxReq = new ZFTSXX_REQ();
        
        if (invalidInvoicesRsp != null) {
            zftsxxReq.setZFPCH(invalidInvoicesRsp.getZFPCH());
            zftsxxReq.setXHFSBH(invalidInvoicesRsp.getNSRSBH());
            
            List<ZFFPXX> zffpxxList = new ArrayList<>();
            if (invalidInvoicesRsp.getINVALID_INVOICE_INFOS() != null && invalidInvoicesRsp.getINVALID_INVOICE_INFOS().size() > 0) {
                for (INVALID_INVOICE_INFOS invalidInvoiceInfos : invalidInvoicesRsp.getINVALID_INVOICE_INFOS()) {
                    ZFFPXX zffpxx = new ZFFPXX();
                    zffpxx.setFPDM(invalidInvoiceInfos.getFP_DM());
                    zffpxx.setFPHM(invalidInvoiceInfos.getFP_HM());
                    zffpxx.setZFLX(invalidInvoiceInfos.getZFLX());
                    zffpxx.setZFYY(invalidInvoiceInfos.getZFYY());
                    zffpxx.setZTDM(invalidInvoiceInfos.getSTATUS_CODE());
                    zffpxx.setZTXX(invalidInvoiceInfos.getSTATUS_MESSAGE());
                    zffpxxList.add(zffpxx);
                    
                    
                }
                zftsxxReq.setZFFPXX(zffpxxList);
            }
        }
        
        
        return zftsxxReq;
    }
    
}
