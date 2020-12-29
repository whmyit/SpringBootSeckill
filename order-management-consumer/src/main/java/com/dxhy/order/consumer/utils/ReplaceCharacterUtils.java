package com.dxhy.order.consumer.utils;

import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.model.c48.zf.DEPRECATE_FAILED_INVOICE;
import com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP;
import com.dxhy.order.protocol.v4.invalid.ZFFPXX;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 替换特殊字符工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020/4/15 11:29
 */
public class ReplaceCharacterUtils {
    
    /**
     * 替换特殊字符 fankunfeng   public 是为了方便测试 规范应该用 private
     *
     * @param ddpcxxReq
     * @return
     */
    public static DDPCXX_REQ replaceCharacter(DDPCXX_REQ ddpcxxReq) {
        //批次信息
        DDPCXX commonOrderBatch = ddpcxxReq.getDDPCXX();
        commonOrderBatch.setFPLXDM(StringUtil.replaceStr(commonOrderBatch.getFPLXDM()));
        commonOrderBatch.setNSRSBH(StringUtil.replaceStr(commonOrderBatch.getNSRSBH()));
        commonOrderBatch.setKPZD(StringUtil.replaceStr(commonOrderBatch.getKPZD()));
        commonOrderBatch.setDDQQPCH(StringUtil.replaceStr(commonOrderBatch.getDDQQPCH()));
        commonOrderBatch.setKPFS(StringUtil.replaceStr(commonOrderBatch.getKPFS()));
        commonOrderBatch.setKZZD(StringUtil.replaceStr(commonOrderBatch.getKZZD()));
        //订单信息
        List<DDZXX> commonOrders = ddpcxxReq.getDDZXX();
        for (DDZXX commonOrder : commonOrders) {
            //head
            DDTXX commonOrderHead = commonOrder.getDDTXX();
            commonOrderHead.setYFPDM(StringUtil.replaceStr(commonOrderHead.getYFPDM()));
            commonOrderHead.setYFPHM(StringUtil.replaceStr(commonOrderHead.getYFPHM()));
            commonOrderHead.setXHFZH(StringUtil.replaceStr(commonOrderHead.getXHFZH()));
            commonOrderHead.setXHFYH(StringUtil.replaceStr(commonOrderHead.getXHFYH()));
            commonOrderHead.setXHFSBH(StringUtil.replaceStr(commonOrderHead.getXHFSBH()));
            commonOrderHead.setXHFMC(StringUtil.replaceStr(commonOrderHead.getXHFMC()));
            commonOrderHead.setXHFDZ(StringUtil.replaceStr(commonOrderHead.getXHFDZ()));
            commonOrderHead.setXHFDH(StringUtil.replaceStr(commonOrderHead.getXHFDH()));
            commonOrderHead.setTSCHBZ(StringUtil.replaceStr(commonOrderHead.getTSCHBZ()));
            commonOrderHead.setTHDH(StringUtil.replaceStr(commonOrderHead.getTHDH()));
            commonOrderHead.setSKR(StringUtil.replaceStr(commonOrderHead.getSKR()));
            commonOrderHead.setQDXMMC(StringUtil.replaceStr(commonOrderHead.getQDXMMC()));
            commonOrderHead.setQDBZ(StringUtil.replaceStr(commonOrderHead.getQDBZ()));
            commonOrderHead.setNSRSBH(StringUtil.replaceStr(commonOrderHead.getNSRSBH()));
            commonOrderHead.setNSRMC(StringUtil.replaceStr(commonOrderHead.getNSRMC()));
            commonOrderHead.setKPR(StringUtil.replaceStr(commonOrderHead.getKPR()));
            commonOrderHead.setKPLX(StringUtil.replaceStr(commonOrderHead.getKPLX()));
            commonOrderHead.setJSHJ(StringUtil.replaceStr(commonOrderHead.getJSHJ()));
            commonOrderHead.setHJSE(StringUtil.replaceStr(commonOrderHead.getHJSE()));
            commonOrderHead.setHJJE(StringUtil.replaceStr(commonOrderHead.getHJJE()));
            commonOrderHead.setGMFZH(StringUtil.replaceStr(commonOrderHead.getGMFZH()));
            commonOrderHead.setGMFYH(StringUtil.replaceStr(commonOrderHead.getGMFYH()));
            commonOrderHead.setGMFSJH(StringUtil.replaceStr(commonOrderHead.getGMFSJH()));
            commonOrderHead.setGMFSF(StringUtil.replaceStr(commonOrderHead.getGMFSF()));
            commonOrderHead.setGMFLX(StringUtil.replaceStr(commonOrderHead.getGMFLX()));
            commonOrderHead.setGMFSBH(StringUtil.replaceStr(commonOrderHead.getGMFSBH()));
            commonOrderHead.setGMFMC(StringUtil.replaceStr(commonOrderHead.getGMFMC()));
            commonOrderHead.setGMFDH(StringUtil.replaceStr(commonOrderHead.getGMFDH()));
            commonOrderHead.setGMFDZYX(StringUtil.replaceStr(commonOrderHead.getGMFDZYX()));
            commonOrderHead.setGMFDZ(StringUtil.replaceStr(commonOrderHead.getGMFDZ()));
            commonOrderHead.setFHR(StringUtil.replaceStr(commonOrderHead.getFHR()));
            commonOrderHead.setDDQQLSH(StringUtil.replaceStr(commonOrderHead.getDDQQLSH()));
            commonOrderHead.setDDH(StringUtil.replaceStr(commonOrderHead.getDDH()));
            commonOrderHead.setDDSJ(StringUtil.replaceStr(commonOrderHead.getDDSJ()));
            commonOrderHead.setYWLX(StringUtil.replaceStr(commonOrderHead.getYWLX()));
            commonOrderHead.setTQM(StringUtil.replaceStr(commonOrderHead.getTQM()));
            commonOrderHead.setGMFBM(StringUtil.replaceStr(commonOrderHead.getGMFBM()));
            commonOrderHead.setCHYY(StringUtil.replaceStr(commonOrderHead.getCHYY()));
            //备注特殊处理
            commonOrderHead.setBZ(StringUtil.replaceStr(commonOrderHead.getBZ(), false));
            commonOrderHead.setBYZD5(StringUtil.replaceStr(commonOrderHead.getBYZD5()));
            commonOrderHead.setBYZD4(StringUtil.replaceStr(commonOrderHead.getBYZD4()));
            commonOrderHead.setBYZD3(StringUtil.replaceStr(commonOrderHead.getBYZD3()));
            commonOrderHead.setBYZD2(StringUtil.replaceStr(commonOrderHead.getBYZD4()));
            commonOrderHead.setBYZD1(StringUtil.replaceStr(commonOrderHead.getBYZD1()));
            commonOrderHead.setBMBBBH(StringUtil.replaceStr(commonOrderHead.getBMBBBH()));
            
            //明细
            List<DDMXXX> orderInvoiceItems = commonOrder.getDDMXXX();
            for (DDMXXX orderInvoiceItem : orderInvoiceItems) {
                orderInvoiceItem.setZZSTSGL(StringUtil.replaceStr(orderInvoiceItem.getZZSTSGL()));
                orderInvoiceItem.setZXBM(StringUtil.replaceStr(orderInvoiceItem.getZXBM()));
                orderInvoiceItem.setYHZCBS(StringUtil.replaceStr(orderInvoiceItem.getYHZCBS()));
                orderInvoiceItem.setXH(StringUtil.replaceStr(orderInvoiceItem.getXH()));
                orderInvoiceItem.setSPSL(StringUtil.replaceStr(orderInvoiceItem.getSPSL()));
                orderInvoiceItem.setXMMC(StringUtil.replaceStr(orderInvoiceItem.getXMMC()));
                orderInvoiceItem.setJE(StringUtil.replaceStr(orderInvoiceItem.getJE()));
                orderInvoiceItem.setDJ(StringUtil.replaceStr(orderInvoiceItem.getDJ()));
                orderInvoiceItem.setSPBM(StringUtil.replaceStr(orderInvoiceItem.getSPBM()));
                orderInvoiceItem.setSL(StringUtil.replaceStr(orderInvoiceItem.getSL()));
                orderInvoiceItem.setSE(StringUtil.replaceStr(orderInvoiceItem.getSE()));
                orderInvoiceItem.setLSLBS(StringUtil.replaceStr(orderInvoiceItem.getLSLBS()));
                orderInvoiceItem.setHSBZ(StringUtil.replaceStr(orderInvoiceItem.getHSBZ()));
                orderInvoiceItem.setGGXH(StringUtil.replaceStr(orderInvoiceItem.getGGXH()));
                orderInvoiceItem.setFPHXZ(StringUtil.replaceStr(orderInvoiceItem.getFPHXZ()));
                orderInvoiceItem.setDW(StringUtil.replaceStr(orderInvoiceItem.getDW()));
                orderInvoiceItem.setKCE(StringUtil.replaceStr(orderInvoiceItem.getKCE()));
                orderInvoiceItem.setBYZD3(StringUtil.replaceStr(orderInvoiceItem.getBYZD3()));
                orderInvoiceItem.setBYZD2(StringUtil.replaceStr(orderInvoiceItem.getBYZD2()));
                orderInvoiceItem.setBYZD1(StringUtil.replaceStr(orderInvoiceItem.getBYZD1()));
            }
            //放入
        }
        return ddpcxxReq;
    }
    
    
    /**
     * bean
     */
    public static ZFXX_RSP transzfxxRsp(DEPRECATE_INVOICES_RSP deprecateInvoicesRsp, String fpdm, String fphm) {
        ZFXX_RSP zfxxRsp = new ZFXX_RSP();
        zfxxRsp.setZFPCH(deprecateInvoicesRsp.getZFPCH());
        
        zfxxRsp.setZTDM(deprecateInvoicesRsp.getSTATUS_CODE());
        zfxxRsp.setZTXX(deprecateInvoicesRsp.getSTATUS_MESSAGE());
        List<ZFFPXX> zffpxxList = new ArrayList<>();
        /**
         * 判断作废成功不返回失败数据数组
         */
        if (OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey().equals(deprecateInvoicesRsp.getSTATUS_CODE()) && deprecateInvoicesRsp.getDeprecate_failed_invoice() == null) {
            ZFFPXX zffpxx = new ZFFPXX();
            zffpxx.setFPDM(fpdm);
            zffpxx.setFPHM(fphm);
            zffpxx.setZTDM(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey());
            zffpxx.setZTXX(OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getMessage());
            zffpxxList.add(zffpxx);
        }
        if (deprecateInvoicesRsp.getDeprecate_failed_invoice() != null && deprecateInvoicesRsp.getDeprecate_failed_invoice().length > 0) {
            
            for (DEPRECATE_FAILED_INVOICE deprecateFailedInvoice : deprecateInvoicesRsp.getDeprecate_failed_invoice()) {
                String code = OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey();
                if (ConfigureConstant.STRING_0000.equals(deprecateFailedInvoice.getSTATUS_CODE())) {
                    code = OrderInfoContentEnum.INVOICE_ERROR_CODE_040000.getKey();
                } else {
                    code = deprecateFailedInvoice.getSTATUS_CODE();
                }
                ZFFPXX zffpxx = new ZFFPXX();
                zffpxx.setFPDM(deprecateFailedInvoice.getFP_DM());
                zffpxx.setFPHM(deprecateFailedInvoice.getFP_HM());
                zffpxx.setZTDM(code);
                zffpxx.setZTXX(deprecateFailedInvoice.getSTATUS_MESSAGE());
                zffpxxList.add(zffpxx);
                zfxxRsp.setZTDM(code);
                zfxxRsp.setZTXX(deprecateFailedInvoice.getSTATUS_MESSAGE());
            }
        }
        
        
        zfxxRsp.setZFFPXX(zffpxxList);
        return zfxxRsp;
    }
    
    
}
