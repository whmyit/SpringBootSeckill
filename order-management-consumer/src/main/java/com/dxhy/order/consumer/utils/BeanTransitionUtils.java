package com.dxhy.order.consumer.utils;

import cn.hutool.core.util.ObjectUtil;
import com.dxhy.invoice.protocol.sk.doto.response.Mx;
import com.dxhy.invoice.protocol.sl.Response;
import com.dxhy.invoice.protocol.sl.cpy.*;
import com.dxhy.invoice.protocol.sl.sld.SldDownRequest;
import com.dxhy.invoice.protocol.sl.sld.SldInvoiceRollPloRequest;
import com.dxhy.invoice.protocol.sl.sld.SldKcmxResponse;
import com.dxhy.invoice.protocol.sl.sld.SldUpRequest;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.protocol.cpy.*;
import com.dxhy.order.consumer.protocol.invoice.DEPRECATE_INVOICES_RSP;
import com.dxhy.order.consumer.protocol.order.GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP;
import com.dxhy.order.consumer.protocol.order.HZFPSQBSCS_RSP;
import com.dxhy.order.consumer.protocol.order.REDINVREQBILLXX;
import com.dxhy.order.consumer.protocol.order.RESPONSE_HZFPSQBSC;
import com.dxhy.order.consumer.protocol.sld.*;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.hp.*;
import com.dxhy.order.model.a9.kp.*;
import com.dxhy.order.model.a9.query.ResponseCommonInvoice;
import com.dxhy.order.model.a9.sld.SearchSld;
import com.dxhy.order.model.c48.zf.DEPRECATE_FAILED_INVOICE;
import com.dxhy.order.model.entity.BuyerEntity;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.protocol.RESPONSE;
import com.dxhy.order.protocol.fangge.FG_COMMON_ORDER_HEAD;
import com.dxhy.order.protocol.fangge.FG_ORDER_INVOICE_ITEM;
import com.dxhy.order.protocol.order.INVALID_INVOICE_INFO;
import com.dxhy.order.protocol.order.INVALID_INVOICE_RSP;
import com.dxhy.order.protocol.v4.buyermanage.GMFXXTB_REQ;
import com.dxhy.order.protocol.v4.buyermanage.GMFXX_COMMON;
import com.dxhy.order.protocol.v4.commodity.SPXX;
import com.dxhy.order.protocol.v4.commodity.SPXXTB_REQ;
import com.dxhy.order.protocol.v4.invalid.ZFFPXX;
import com.dxhy.order.protocol.v4.invalid.ZFXX_RSP;
import com.dxhy.order.protocol.v4.invoice.*;
import com.dxhy.order.protocol.v4.order.*;
import com.dxhy.order.protocol.v4.taxequipment.SKSBXXTB_REQ;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author ：杨士勇
 * @ClassName ：BeanTransition
 * @Description ：bean转换工具类
 * @date ：2018年10月15日 下午2:19:37
 */
@Slf4j
public class BeanTransitionUtils {
    private static final String LOGGER_MSG = "(对象转换工具类)";
    
    /**
     * 成品油局端可下载库存--请求协议bean转换
     *
     * @param cpyJdkcRequest
     * @return
     */
    public static CpyJdKcRequest transitionJdkcRequest(CPY_JDKC_REQUEST cpyJdkcRequest) {
        CpyJdKcRequest cpyJdKcRequest = new CpyJdKcRequest();
        cpyJdKcRequest.setFjh(cpyJdkcRequest.getFJH());
        cpyJdKcRequest.setNsrsbh(cpyJdkcRequest.getNSRSBH());
        cpyJdKcRequest.setSjfw(cpyJdkcRequest.getSJFW());
        return cpyJdKcRequest;
    }
    
    /**
     * 成品油已下载库存--请求协议bean转换
     *
     * @param cpyYxzkcRequest
     * @return com.dxhy.invoice.protocol.sl.cpy.CpyYxzKcRequest
     */
    
    public static CpyYxzKcRequest transitionYxzkcRequest(CPY_YXZKC_REQUEST cpyYxzkcRequest) {
        CpyYxzKcRequest cpyYxzKcRequest = new CpyYxzKcRequest();
        cpyYxzKcRequest.setFjh(cpyYxzkcRequest.getFJH());
        cpyYxzKcRequest.setNsrsbh(cpyYxzkcRequest.getNSRSBH());
        cpyYxzKcRequest.setSpbm(cpyYxzkcRequest.getSPBM());
        return cpyYxzKcRequest;
    }
    
    /**
     * 成品油库存下载--请求协议bean 转换
     *
     * @param downloadCpykcRequest
     * @return DownloadCpyKcRequest
     */
    
    public static DownloadCpyKcRequest transitionCpykcRequest(DOWNLOAD_CPYKC_REQUEST downloadCpykcRequest) {
        DownloadCpyKcRequest downloadCpyKcRequest = new DownloadCpyKcRequest();
        downloadCpyKcRequest.setFjh(downloadCpykcRequest.getFJH());
        downloadCpyKcRequest.setNsrsbh(downloadCpykcRequest.getNSRSBH());
        
        List<DOWNLOAD_CPYKC_RESMXS> listMxs = downloadCpykcRequest.getMXS();
        List<DownloadCpyKcResMxs> resultList = new ArrayList<>();
        
        for (DOWNLOAD_CPYKC_RESMXS mxs : listMxs) {
            DownloadCpyKcResMxs downloadCpyKcResMxs = new DownloadCpyKcResMxs();
            DOWNLOAD_CPYKC_MX mx2 = mxs.getMX();
            DownloadCpyKcMx mx = new DownloadCpyKcMx();
            mx.setSl(mx2.getSL());
            mx.setSpbm(mx2.getSPBM());
            downloadCpyKcResMxs.setMx(mx);
            resultList.add(downloadCpyKcResMxs);
        }
        downloadCpyKcRequest.setMxs(resultList);
        return downloadCpyKcRequest;
    }
    
    /**
     * 成品油库存退回--请求协议bean转换
     *
     * @param backCpykcRequest
     * @return BackCpyKcRequest
     */
    
    public static BackCpyKcRequest transitionCpykchtRequest(BACK_CPYKC_REQUEST backCpykcRequest) {
        
        BackCpyKcRequest backCpyKcRequest = new BackCpyKcRequest();
        backCpyKcRequest.setFjh(backCpykcRequest.getFJH());
        backCpyKcRequest.setNsrsbh(backCpykcRequest.getNSRSBH());
        
        BackCpyKcMx backCpyKcMx = new BackCpyKcMx();
        BACK_CPYKC_MX mx = backCpykcRequest.getMX();
        backCpyKcMx.setSl(mx.getSL());
        backCpyKcMx.setSpbm(mx.getSPBM());
        backCpyKcRequest.setMx(backCpyKcMx);
        return backCpyKcRequest;
    }
    
    /**
     * 成品油库存同步--请求协议bean转换
     *
     * @param syncCpykcRequest
     * @return SyncCpyKcRequest
     */
    
    public static SyncCpyKcRequest transitionCpykctbRequest(SYNC_CPYKC_REQUEST syncCpykcRequest) {
        SyncCpyKcRequest syncCpyKcRequest = new SyncCpyKcRequest();
        syncCpyKcRequest.setFjh(syncCpykcRequest.getFJH());
        syncCpyKcRequest.setNsrsbh(syncCpykcRequest.getNSRSBH());
        return syncCpyKcRequest;
    }
    
    
    /**
     * 作废接口--响应协议bean转换
     *
     * @param deprecateInvoicesRsp
     * @return
     */
    public static DEPRECATE_INVOICES_RSP transitionDeprecateInvoicesRsp(com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP deprecateInvoicesRsp) {
        DEPRECATE_INVOICES_RSP deprecateInvoicesRsp1 = new DEPRECATE_INVOICES_RSP();
        deprecateInvoicesRsp1.setZFPCH(deprecateInvoicesRsp.getZFPCH());
        deprecateInvoicesRsp1.setSTATUS_CODE(deprecateInvoicesRsp.getSTATUS_CODE());
        deprecateInvoicesRsp1.setSTATUS_MESSAGE(deprecateInvoicesRsp.getSTATUS_MESSAGE());
        
        deprecateInvoicesRsp1.setDEPRECATE_FAILED_INVOICE(deprecateInvoicesRsp.getDeprecate_failed_invoice());
        
        return deprecateInvoicesRsp1;
    }
    
    
    /**
     * 作废接口返回数据,V4数据转换为V3数据
     */
    public static com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP transDeprecateInvoicesRsp(ZFXX_RSP zfxxRsp) {
        com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP deprecateInvoicesRsp = new com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP();
        deprecateInvoicesRsp.setZFPCH(zfxxRsp.getZFPCH());
        
        deprecateInvoicesRsp.setSTATUS_CODE(zfxxRsp.getZTDM());
        deprecateInvoicesRsp.setSTATUS_MESSAGE(zfxxRsp.getZTXX());
        DEPRECATE_FAILED_INVOICE[] deprecate_failed_invoices = new DEPRECATE_FAILED_INVOICE[0];
        if (zfxxRsp.getZFFPXX() != null && zfxxRsp.getZFFPXX().size() > 0) {
            deprecate_failed_invoices = new DEPRECATE_FAILED_INVOICE[zfxxRsp.getZFFPXX().size()];
            for (int i = 0; i < zfxxRsp.getZFFPXX().size(); i++) {
                DEPRECATE_FAILED_INVOICE deprecate_failed_invoice = new DEPRECATE_FAILED_INVOICE();
                ZFFPXX zffpxx = zfxxRsp.getZFFPXX().get(i);
                deprecate_failed_invoice.setFP_DM(zffpxx.getFPDM());
                deprecate_failed_invoice.setFP_HM(zffpxx.getFPHM());
                deprecate_failed_invoice.setSTATUS_CODE(zffpxx.getZTDM());
                deprecate_failed_invoice.setSTATUS_MESSAGE(zffpxx.getZTXX());
                deprecate_failed_invoices[i] = deprecate_failed_invoice;
                
            }
            
        }
        
        
        deprecateInvoicesRsp.setDeprecate_failed_invoice(deprecate_failed_invoices);
        return deprecateInvoicesRsp;
    }
    
    
    /**
     * 成品油局端可下载库存--响应协议bean转换
     *
     * @param queryCpyJdKc
     * @return CPY_JDKC_RESPONSE
     */
    
    public static CPY_JDKC_RESPONSE transtionCpyJdKcResponse(CpyJdKcResponse queryCpyJdKc) {
        CPY_JDKC_RESPONSE cpyJdkcResponse = new CPY_JDKC_RESPONSE();
        cpyJdkcResponse.setSTATUS_CODE(queryCpyJdKc.getStatusCode());
        cpyJdkcResponse.setSTATUS_MESSAGE(queryCpyJdKc.getStatusMessage());
        CpyJdKcXzz xzz = queryCpyJdKc.getXzz();
        CpyjdKcMxs mxs = xzz.getMxs();
        CpyJdKcMx mx = mxs.getMx();
        List<CpyJdKcXzzmxs> xzzmxs = mx.getXzzmxs();

        List<CpyJdKcKxzMx> kxzmxs = queryCpyJdKc.getKxzmxs();

        CPY_JDKC_XZZ cpyJdkcXzz = new CPY_JDKC_XZZ();
        CPY_JDKC_MXS cpyJdkcMxs = new CPY_JDKC_MXS();
        CPY_JDKC_MX cpyJdkcMx = new CPY_JDKC_MX();
        cpyJdkcMx.setSBBM(mx.getSbbm());
        cpyJdkcMx.setSLLSH(mx.getSllsh());
        cpyJdkcMx.setSLZT(mx.getSlzt());
        List<CPY_JDKC_XZZ_MXS> xZZMXS = new ArrayList<>();
        for (CpyJdKcXzzmxs kcXzzmx : xzzmxs) {
            CPY_JDKC_XZZ_MXS cpyJdkcXzzMxs = new CPY_JDKC_XZZ_MXS();
            cpyJdkcXzzMxs.setSL(kcXzzmx.getSl());
            cpyJdkcXzzMxs.setSPBM(kcXzzmx.getSpbm());
            xZZMXS.add(cpyJdkcXzzMxs);
        }
        cpyJdkcMx.setXZZMXS(xZZMXS);
        cpyJdkcMxs.setMX(cpyJdkcMx);
        cpyJdkcXzz.setMXS(cpyJdkcMxs);
        cpyJdkcResponse.setXZZ(cpyJdkcXzz);
    
    
        List<CPY_JDKC_KXZ_MX> cpyJdkcKxzMxes = new ArrayList<>();
        for (CpyJdKcKxzMx cpyJdKcKxzMx : kxzmxs) {
            CPY_JDKC_KXZ_MX cpyJdkcKxzMx = new CPY_JDKC_KXZ_MX();
            cpyJdkcKxzMx.setSL(cpyJdKcKxzMx.getSl());
            cpyJdkcKxzMx.setSPBM(cpyJdKcKxzMx.getSpbm());
            cpyJdkcKxzMxes.add(cpyJdkcKxzMx);
        }
        cpyJdkcResponse.setKXZMXS(cpyJdkcKxzMxes);
        return cpyJdkcResponse;
    }

    /**
     * 成品油已下载库存--响应协议bean转换
     *
     * @param queryCpyYxzKc
     * @return CPY_YXZKC_RESPONSE
     */

    public static CPY_YXZKC_RESPONSE transitionCpyYxzKcResponse(CpyYxzKcResponse queryCpyYxzKc) {
        CPY_YXZKC_RESPONSE cpyYxzkcResponse = new CPY_YXZKC_RESPONSE();
        cpyYxzkcResponse.setSTATUS_CODE(queryCpyYxzKc.getStatusCode());
        cpyYxzkcResponse.setSTATUS_MESSAGE(queryCpyYxzKc.getStatusMessage());
        List<CpyYxzKcMxs> mxs = queryCpyYxzKc.getMxs();
        List<CPY_YXZKC_MXS> mXS = new ArrayList<>();
        for (CpyYxzKcMxs cpyYxzKcMxs : mxs) {
            CPY_YXZKC_MXS cpyYxzkcMxs = new CPY_YXZKC_MXS();
            cpyYxzkcMxs.setSL(cpyYxzKcMxs.getSl());
            cpyYxzkcMxs.setSPBM(cpyYxzKcMxs.getSpbm());
            mXS.add(cpyYxzkcMxs);
        }
        cpyYxzkcResponse.setMXS(mXS);
        return cpyYxzkcResponse;
    }

    /**
     * 成品油库存下载--响应协议bean转换
     *
     * @param downloadCpyKc
     * @return DOWNLOAD_CPYKC_RESPONSE
     */

    public static DOWNLOAD_CPYKC_RESPONSE transitionDownloadCpyKcResponse(DownloadCpyKcResponse downloadCpyKc) {
        DOWNLOAD_CPYKC_RESPONSE downloadCpykcResponse = new DOWNLOAD_CPYKC_RESPONSE();
        downloadCpykcResponse.setSTATUS_CODE(downloadCpyKc.getStatusCode());
        downloadCpykcResponse.setSTATUS_MESSAGE(downloadCpyKc.getStatusMessage());
        List<DownloadCpyKcMxs> mxs = downloadCpyKc.getMxs();
        List<DOWNLOAD_CPYKC_MXS> mXS = new ArrayList<>();
        for (DownloadCpyKcMxs downloadCpyKcMxs : mxs) {
            DOWNLOAD_CPYKC_MXS downloadCpykcMxs = new DOWNLOAD_CPYKC_MXS();
            downloadCpykcMxs.setSL(downloadCpyKcMxs.getSl());
            downloadCpykcMxs.setSPBM(downloadCpyKcMxs.getSl());
            mXS.add(downloadCpykcMxs);
        }
        downloadCpykcResponse.setMXS(mXS);
        return downloadCpykcResponse;
    }

    /**
     * 成品油库存退回--响应协议bean转换
     *
     * @param backCpyKc
     * @return BACK_CPYKC_RESPONSE
     */

    public static BACK_CPYKC_RESPONSE transitionBackCpyKcResponse(BackCpyKcResponse backCpyKc) {
        BACK_CPYKC_RESPONSE backCpykcResponse = new BACK_CPYKC_RESPONSE();
        backCpykcResponse.setSTATUS_CODE(backCpyKc.getStatusCode());
        backCpykcResponse.setSTATUS_MESSAGE(backCpyKc.getStatusMessage());
        List<BackCpyKcMx> mxs2 = backCpyKc.getMxs();
        List<BACK_CPYKC_MX> mXS = new ArrayList<>();
        for (BackCpyKcMx backCpyKcMx : mxs2) {
            BACK_CPYKC_MX backCpykcMx = new BACK_CPYKC_MX();
            backCpykcMx.setSL(backCpyKcMx.getSl());
            backCpykcMx.setSPBM(backCpyKcMx.getSpbm());
            mXS.add(backCpykcMx);
        }
        backCpykcResponse.setMXS(mXS);
        return backCpykcResponse;
    }

    /**
     * 成品油库存同步--响应协议bean转换
     *
     * @param syncCpyKc
     * @return SYNC_CPYKC_RESPONSE
     */

    public static SYNC_CPYKC_RESPONSE transitionSyncCpyKcResponse(SyncCpyKcResponse syncCpyKc) {
        SYNC_CPYKC_RESPONSE syncCpykcResponse = new SYNC_CPYKC_RESPONSE();
        syncCpykcResponse.setSTATUS_CODE(syncCpyKc.getStatusCode());
        syncCpykcResponse.setSTATUS_MESSAGE(syncCpyKc.getStatusMessage());
        syncCpykcResponse.setTBBS(syncCpykcResponse.getTBBS());
        List<Mx> mxs = syncCpyKc.getMxs();
        List<MX> mXS = new ArrayList<>();
        for (Mx mxIndex : mxs) {
            MX mx = new MX();
            mx.setSL(mxIndex.getSl());
            mx.setSPBM(mx.getSPBM());
            mXS.add(mx);
        }
        syncCpykcResponse.setMXS(mXS);
        return syncCpykcResponse;
    }
    
    /**
     * 开具发票结果内层数据获取--响应协议bean转换
     *
     * @param orderInvoiceInfo
     * @return
     */
    public static FPZXX transitionCommonInvoiceInfoV3(OrderInvoiceInfo orderInvoiceInfo) {
        
        FPZXX fpzxx = new FPZXX();
        fpzxx.setDDQQLSH(orderInvoiceInfo.getFpqqlsh());
        fpzxx.setFPDM(orderInvoiceInfo.getFpdm());
        fpzxx.setFPHM(orderInvoiceInfo.getFphm());
        fpzxx.setFWM(orderInvoiceInfo.getFwm());
        fpzxx.setJQBH(orderInvoiceInfo.getJqbh());
        fpzxx.setJYM(orderInvoiceInfo.getJym());
        fpzxx.setKPRQ(orderInvoiceInfo.getKprq() != null ? DateUtilsLocal.getYMDHMIS(orderInvoiceInfo.getKprq()) : "");
    
        return fpzxx;
    
    }
    
    
    /**
     * 开具发票结果内层数据获取--响应协议bean转换
     *
     * @param orderInvoiceInfo
     * @return
     */
    public static ResponseCommonInvoice transitionCommonInvoiceInfoV2(OrderInvoiceInfo orderInvoiceInfo) {
        
        ResponseCommonInvoice fpzxx = new ResponseCommonInvoice();
        fpzxx.setFPQQLSH(orderInvoiceInfo.getFpqqlsh());
        fpzxx.setFP_DM(orderInvoiceInfo.getFpdm());
        fpzxx.setFP_HM(orderInvoiceInfo.getFphm());
        fpzxx.setFWM(orderInvoiceInfo.getFwm());
        fpzxx.setJQBH(orderInvoiceInfo.getJqbh());
        fpzxx.setJYM(orderInvoiceInfo.getJym());
        fpzxx.setKPRQ(orderInvoiceInfo.getKprq() != null ? DateUtilsLocal.getYMDHMIS(orderInvoiceInfo.getKprq()) : "");
        
        return fpzxx;
        
    }
    
    /**
     * 受理点列表查询请求协议bean转换
     *
     * @param sld_invoicerollplo_request
     * @return
     */
    public static SldInvoiceRollPloRequest transSldInvoiceReq(SLD_INVOICEROLLPLO_REQUEST sld_invoicerollplo_request) {
        SldInvoiceRollPloRequest sldInvoiceRollPloRequest = new SldInvoiceRollPloRequest();
        if (sld_invoicerollplo_request != null) {
            sldInvoiceRollPloRequest.setFpzlDM(sld_invoicerollplo_request.getFPZLDM());
            sldInvoiceRollPloRequest.setKpdId(sld_invoicerollplo_request.getKPDID());
            sldInvoiceRollPloRequest.setNsrsbh(sld_invoicerollplo_request.getNSRSBH());
        } else {
            log.warn("{}受理点列表查询请求协议bean转换,请求参数为空!", LOGGER_MSG);
        }

        return sldInvoiceRollPloRequest;

    }

    /**
     * 受理点列表查询返回协议bean转换
     *
     * @param sldKcmxResponse
     * @return
     */
    public static SLDKCMX_RESPONSE transSldInvoiceRsp(SldKcmxResponse sldKcmxResponse) {
        SLDKCMX_RESPONSE sldkcmx_response = new SLDKCMX_RESPONSE();
        List<SLDKCMX> sldkcmxlist = new ArrayList<>();


        /**
         * 返回对象不为空,并且数组中数据大于0
         */
        if (sldKcmxResponse != null && sldKcmxResponse.getKclb().size() > 0) {
            for (int i = 0; i < sldKcmxResponse.getKclb().size(); i++) {
                SLDKCMX sldkcmx = new SLDKCMX();
                sldkcmx.setDDYFPHM(sldKcmxResponse.getKclb().get(i).getDdyFphm());
                sldkcmx.setDYDMC(sldKcmxResponse.getKclb().get(i).getDydMc());
                sldkcmx.setFJH(sldKcmxResponse.getKclb().get(i).getFjh());
                sldkcmx.setFP_DM(sldKcmxResponse.getKclb().get(i).getFpdm());
                sldkcmx.setFPQSHM(sldKcmxResponse.getKclb().get(i).getFpqshm());
                sldkcmx.setFPDQHM(sldKcmxResponse.getKclb().get(i).getFpdqhm());
                sldkcmx.setFPZZHM(sldKcmxResponse.getKclb().get(i).getFpzzhm());
                sldkcmx.setJQBH(sldKcmxResponse.getKclb().get(i).getJqbh());
                sldkcmx.setNSRSBH(sldKcmxResponse.getKclb().get(i).getNsrsbh());
                sldkcmx.setSLDID(sldKcmxResponse.getKclb().get(i).getSldId());
                sldkcmx.setSLDMC(sldKcmxResponse.getKclb().get(i).getSldMc());
                sldkcmx.setSPR(sldKcmxResponse.getKclb().get(i).getSpr());
                sldkcmx.setSPSJ(sldKcmxResponse.getKclb().get(i).getSpsj());
                sldkcmx.setSYFS(sldKcmxResponse.getKclb().get(i).getSyfs());
                sldkcmx.setSYZT(sldKcmxResponse.getKclb().get(i).getSyzt());
                sldkcmx.setXPR(sldKcmxResponse.getKclb().get(i).getXpr());
                sldkcmx.setXPSJ(sldKcmxResponse.getKclb().get(i).getXpsj());
                sldkcmxlist.add(sldkcmx);
            }
            sldkcmx_response.setSLDKCMXES(sldkcmxlist);
            sldkcmx_response.setSTATUS_CODE(sldKcmxResponse.getStatusCode());
            sldkcmx_response.setSTATUS_MESSAGE(sldKcmxResponse.getStatusMessage());
        } else {
            sldkcmx_response.setSLDKCMXES(sldkcmxlist);
            sldkcmx_response.setSTATUS_CODE(ConfigureConstant.STRING_9999);
            sldkcmx_response.setSTATUS_MESSAGE("");
        }
    
    
        return sldkcmx_response;
    
    }

    /**
     * 受理点上票请求协议bean转换
     *
     * @param slduprequest
     * @return
     */
    public static SldUpRequest transSldUpInvoiceReq(SLDUP_REQUEST slduprequest) {
        SldUpRequest sldUpRequest = new SldUpRequest();
        if (slduprequest != null) {
            sldUpRequest.setFpzlDm(slduprequest.getFPZLDM());
            sldUpRequest.setFpdm(slduprequest.getFP_DM());
            sldUpRequest.setFpqshm(slduprequest.getFPQSHM());
            sldUpRequest.setFpzzhm(slduprequest.getFPZZHM());
            sldUpRequest.setNsrsbh(slduprequest.getNSRSBH());
            sldUpRequest.setSpr(slduprequest.getSPR());
            sldUpRequest.setSldId(slduprequest.getSLDID());
        } else {
            log.warn("{}受理点上票请求协议bean转换,请求参数为空!", LOGGER_MSG);
        }

        return sldUpRequest;

    }


    /**
     * 受理点下票请求协议bean转换
     *
     * @param SLDDOWNREQUEST
     * @return
     */
    public static SldDownRequest transSldDownInvoiceReq(SLDDOWN_REQUEST SLDDOWNREQUEST) {
        SldDownRequest sldDownRequest = new SldDownRequest();
        if (SLDDOWNREQUEST != null) {
            sldDownRequest.setNsrsbh(SLDDOWNREQUEST.getNSRSBH());
            sldDownRequest.setSldId(SLDDOWNREQUEST.getSLDID());
            sldDownRequest.setXpr(SLDDOWNREQUEST.getXPR());
        } else {
            log.warn("{}受理点上票请求协议bean转换,请求参数为空!", LOGGER_MSG);
        }

        return sldDownRequest;

    }
    
    /**
     * 受理点列表查询返回协议bean转换
     *
     * @param searchSldSet
     * @return
     */
    public static SLD_SEARCH_RESPONSE transSldSearchRsp(Set<SearchSld> searchSldSet) {
        SLD_SEARCH_RESPONSE sld_search_response = new SLD_SEARCH_RESPONSE();
        List<SLD_SEARCH> sld_searches = new ArrayList<>();
        
        
        /**
         * 返回对象不为空,并且数组中数据大于0
         */
        if (ObjectUtil.isNotEmpty(searchSldSet) && searchSldSet.size() > 0) {
            ArrayList<SearchSld> searchSlds = new ArrayList<>(searchSldSet);
            for (SearchSld searchSld : searchSlds) {
                SLD_SEARCH sld_search = new SLD_SEARCH();
                sld_search.setSLDID(searchSld.getSldId());
                sld_search.setSLDMC(searchSld.getSldMc());
                sld_search.setJQBH(searchSld.getJqbh());
                sld_searches.add(sld_search);
            }
            
            sld_search_response.setSLDS(sld_searches);
            sld_search_response.setSTATUS_CODE(OrderInfoContentEnum.SUCCESS.getKey());
            sld_search_response.setSTATUS_MESSAGE("成功");
        } else {
            sld_search_response.setSLDS(sld_searches);
            sld_search_response.setSTATUS_CODE(ConfigureConstant.STRING_9999);
            sld_search_response.setSTATUS_MESSAGE("");
        }
    
    
        return sld_search_response;
    
    }

    /**
     * 受理点通用返回协议bean转换
     *
     * @param response
     * @return
     */
    public static RESPONSE transSldInvoiceCommonRsp(Response response) {
        RESPONSE response1 = new RESPONSE();
        if (response != null) {
            response1.setSTATUS_CODE(response.getStatusCode());
            response1.setSTATUS_MESSAGE(response.getStatusMessage());
        } else {
            log.warn("{}受理点通用返回协议bean转换,请求参数为空!", LOGGER_MSG);
        }
        return response1;

    }
    
    
    /**
     * 发票自动开票接口请求头对象转换
     *
     * @param common_invoices_batch
     * @return
     */
    public static InvoiceBatchRequest transitionAutoBatchRequest(CommonInvoicesBatch common_invoices_batch) {
        InvoiceBatchRequest invoiceBatchRequest = new InvoiceBatchRequest();
        invoiceBatchRequest.setFpqqpch(common_invoices_batch.getFPQQPCH());
        invoiceBatchRequest.setXhfNsrsbh(common_invoices_batch.getNSRSBH());
        invoiceBatchRequest.setSldid(common_invoices_batch.getSLDID());
        invoiceBatchRequest.setKpjh(common_invoices_batch.getKPJH());
        invoiceBatchRequest.setKplx(common_invoices_batch.getFPLX());
        invoiceBatchRequest.setFplb(common_invoices_batch.getFPLB());
        invoiceBatchRequest.setKzzd(common_invoices_batch.getKZZD());
        invoiceBatchRequest.setCreateTime(new Date());
        invoiceBatchRequest.setUpdateTime(new Date());
        return invoiceBatchRequest;
    }
    
    
    /**
     * 发票自动开票接口请求头对象转换V3
     *
     * @param ddpcxx
     * @return
     */
    public static InvoiceBatchRequest transitionAutoBatchRequestV3(DDPCXX ddpcxx) {
        InvoiceBatchRequest invoiceBatchRequest = new InvoiceBatchRequest();
        invoiceBatchRequest.setFpqqpch(ddpcxx.getDDQQPCH());
        invoiceBatchRequest.setXhfNsrsbh(ddpcxx.getNSRSBH());
        invoiceBatchRequest.setKplx(ddpcxx.getFPLXDM());
        invoiceBatchRequest.setKzzd(ddpcxx.getKZZD());
        invoiceBatchRequest.setCreateTime(new Date());
        invoiceBatchRequest.setUpdateTime(new Date());
        return invoiceBatchRequest;
    }
    
    
    /**
     * 底层开票数据转换为订单业务数据
     *
     * @param common_invoice
     * @return
     */
    public static CommonOrderInfo transitionCommonOrderInfo(CommonInvoice common_invoice) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        
        OrderInfo orderInfo = transitionOrderInfo(common_invoice.getCOMMON_INVOICE_HEAD(), common_invoice.getCOMMON_INVOICE_ORDER());
        
        List<OrderItemInfo> orderItemInfos = transitionOrderItemInfo(common_invoice.getCOMMON_INVOICE_DETAIL(), orderInfo.getXhfNsrsbh());
        
        /**
         * 默认开票项目为明细行首行数据
         */
        orderInfo.setKpxm(orderItemInfos.get(0).getXmmc());

        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);

        return commonOrderInfo;
    }

    /**
     * 底层开票数据转换为订单导入业务数据
     *
     * @param common_invoice
     * @return
     */
    public static CommonOrderInfo transitionCommonOrderInfoForImportOrders(com.dxhy.order.consumer.protocol.order.COMMON_INVOICE common_invoice) {
        CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
        CommonInvoice common_invoice1 = new CommonInvoice();
        CommonInvoiceHead common_invoice_head1 = new CommonInvoiceHead();
        CommonInvoiceDetail[] common_invoice_details1 = new CommonInvoiceDetail[common_invoice.getCOMMON_INVOICE_DETAIL().length];
        CommonInvoiceOrder common_invoice_order1 = new CommonInvoiceOrder();
    
        BeanUtils.copyProperties(common_invoice.getCOMMON_INVOICE_HEAD(), common_invoice_head1);
        BeanUtils.copyProperties(common_invoice.getCOMMON_INVOICE_ORDER(), common_invoice_order1);
        for (int i = 0; i < common_invoice.getCOMMON_INVOICE_DETAIL().length; i++) {
            CommonInvoiceDetail common_invoice_detail = new CommonInvoiceDetail();
            BeanUtils.copyProperties(common_invoice.getCOMMON_INVOICE_DETAIL()[i], common_invoice_detail);
        
            common_invoice_details1[i] = common_invoice_detail;
        }
        common_invoice1.setCOMMON_INVOICE_HEAD(common_invoice_head1);
        common_invoice1.setCOMMON_INVOICE_ORDER(common_invoice_order1);
        common_invoice1.setCOMMON_INVOICE_DETAIL(common_invoice_details1);

        OrderInfo orderInfo = transitionOrderInfo(common_invoice1.getCOMMON_INVOICE_HEAD(), common_invoice1.getCOMMON_INVOICE_ORDER());
    
        List<OrderItemInfo> orderItemInfos = transitionOrderItemInfo(common_invoice1.getCOMMON_INVOICE_DETAIL(), orderInfo.getXhfNsrsbh());

        orderInfo.setKpxm(orderItemInfos.get(0).getXmmc());
        /**
         * 补全发票种类代码,开票机号,受理点,受理点名称
         */
        orderInfo.setFpzlDm(common_invoice.getCOMMON_INVOICE_HEAD().getFPZLDM());
        orderInfo.setKpjh(common_invoice.getCOMMON_INVOICE_HEAD().getKPJH());
        orderInfo.setSld(common_invoice.getCOMMON_INVOICE_HEAD().getSLD());
    
        commonOrderInfo.setOrderInfo(orderInfo);
        commonOrderInfo.setOrderItemInfo(orderItemInfos);
    
        return commonOrderInfo;
    }
    
    /**
     * 调用底层红字申请单上传接口数据转换bean转换
     */
    public static HZFPSQBSCS_RSP convertHzfpQbs(HpUploadResponse uploadHpSqd) {
        HZFPSQBSCS_RSP response = new HZFPSQBSCS_RSP();
        
        if (Constant.SUCCSSCODE.equals(uploadHpSqd.getCode())) {
            
            if (uploadHpSqd.getResult() != null) {
                response.setSTATUS_CODE(uploadHpSqd.getResult().getStatusCode());
                response.setSTATUS_MESSAGE(uploadHpSqd.getResult().getStatusMessage());
                response.setSQBSCQQPCH(uploadHpSqd.getResult().getSqbscqqpch());
                if (!CollectionUtils.isEmpty(uploadHpSqd.getResult().getResponse_HZFPSQBSC())) {
                    RESPONSE_HZFPSQBSC[] response_HZFPSQBSC = new RESPONSE_HZFPSQBSC[uploadHpSqd.getResult().getResponse_HZFPSQBSC().size()];
                    int i = 0;
                    for (HpUploadResponseHzfpsqbsc resp : uploadHpSqd.getResult().getResponse_HZFPSQBSC()) {
                        RESPONSE_HZFPSQBSC res = new RESPONSE_HZFPSQBSC();
                        res.setSQBSCQQLSH(resp.getSQBSCQQLSH());
                        res.setSQDH(resp.getSQDH());
                        res.setXXBBH(resp.getXXBBH());
                        res.setSTATUS_CODE(resp.getSTATUS_CODE());
                        res.setSTATUS_MESSAGE(resp.getSTATUS_MESSAGE());
                        response_HZFPSQBSC[i] = res;
                        i++;
                    }
                    response.setRESPONSE_HZFPSQBSC(response_HZFPSQBSC);
                }
            } else {
                response.setSTATUS_CODE(uploadHpSqd.getCode());
                response.setSTATUS_MESSAGE(uploadHpSqd.getMsg());
            }
            
        } else {
            response.setSTATUS_CODE(uploadHpSqd.getCode());
            response.setSTATUS_MESSAGE(uploadHpSqd.getMsg());
        }
        return response;
    }
    
    /**
     * 自动开票接口数据转换--订单主体信息
     *
     * @param common_invoice_head
     * @return
     */
    public static OrderInfo transitionOrderInfo(CommonInvoiceHead common_invoice_head, CommonInvoiceOrder common_invoice_order) {
        
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setFpqqlsh(common_invoice_head.getFPQQLSH());
        orderInfo.setDdh(common_invoice_order.getDDH());
        orderInfo.setThdh(common_invoice_order.getTHDH());
        orderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_0.getKey());
        orderInfo.setDdrq(StringUtils.isBlank(common_invoice_order.getDDDATE()) ? new Date() : DateUtilsLocal.getDefaultDate_yyyy_MM_dd_HH_mm_ss(common_invoice_order.getDDDATE()));
        orderInfo.setDsptbm("");
        orderInfo.setNsrsbh(common_invoice_head.getNSRSBH());
        orderInfo.setNsrmc(common_invoice_head.getNSRMC());
        orderInfo.setNsrdzdah(common_invoice_head.getNSRSBH());
        orderInfo.setSwjgDm("");
        // TODO: 2018/10/25 代开标志默认为0
        orderInfo.setDkbz(ConfigureConstant.STRING_0);
        orderInfo.setPydm(common_invoice_head.getPYDM());
        /**
         * 外层进行补全,使用明细行第一行商品名称作为开票项目
         */
        orderInfo.setKpxm("");
        orderInfo.setBbmBbh(common_invoice_head.getBMB_BBH());
        orderInfo.setXhfMc(common_invoice_head.getXSF_MC());
        orderInfo.setXhfNsrsbh(common_invoice_head.getXSF_NSRSBH());
        orderInfo.setXhfDz(common_invoice_head.getXSF_DZ());
        orderInfo.setXhfDh(common_invoice_head.getXSF_DH());
        // TODO: 2018/10/25 前期使用银行字段存储银行帐号
        if (StringUtils.isNotBlank(common_invoice_head.getXSF_YHZH())) {
            orderInfo.setXhfYh(common_invoice_head.getXSF_YHZH());
        }
        orderInfo.setGhfQylx(common_invoice_head.getGMF_QYLX());
        orderInfo.setGhfSf(common_invoice_head.getGMF_SF());
        orderInfo.setGhfMc(common_invoice_head.getGMF_MC());
        orderInfo.setGhfNsrsbh(common_invoice_head.getGMF_NSRSBH());
        orderInfo.setGhfDz(common_invoice_head.getGMF_DZ());
        orderInfo.setGhfDh(common_invoice_head.getGMF_GDDH());
        // TODO: 2018/10/25 前期使用银行字段存储银行帐号
        if (StringUtils.isNotBlank(common_invoice_head.getGMF_YHZH())) {
            orderInfo.setGhfYh(common_invoice_head.getGMF_YHZH());
        }
        orderInfo.setGhfSj(common_invoice_head.getGMF_SJ());
        orderInfo.setGhfEmail(common_invoice_head.getGMF_EMAIL());
        orderInfo.setHyDm("");
        orderInfo.setHyMc("");
        orderInfo.setKpr(common_invoice_head.getKPR());
        orderInfo.setSkr(common_invoice_head.getSKR());
        orderInfo.setFhr(common_invoice_head.getFHR());
        orderInfo.setKplx(common_invoice_head.getKPLX());
        /**
         * 外层进行补全,使用最外层的发票类型作为种类代码
         */
        orderInfo.setFpzlDm("");
        orderInfo.setYfpDm(common_invoice_head.getYFP_DM());
        orderInfo.setYfpHm(common_invoice_head.getYFP_HM());
        orderInfo.setChyy(common_invoice_head.getCHYY());
        orderInfo.setTschbz(common_invoice_head.getTSCHBZ());
        // TODO: 2018/10/25 操作代码默认为10
        orderInfo.setCzdm("10");
        orderInfo.setQdBz(common_invoice_head.getQD_BZ());
        orderInfo.setQdXmmc(common_invoice_head.getQDXMMC());
        orderInfo.setKphjje(common_invoice_head.getJSHJ());
        orderInfo.setHjbhsje(common_invoice_head.getHJJE());
        orderInfo.setHjse(common_invoice_head.getHJSE());
        // TODO: 2018/10/25 后期考虑添加以下字段:门店号,业务类型,推送地址
        orderInfo.setMdh("");
        orderInfo.setYwlx("");
        /**
         * 外层进行补全,使用最外层的数据进行填充
         */
    
        orderInfo.setKpjh("");
        orderInfo.setSld("");
        orderInfo.setSldMc("");

        orderInfo.setBz(common_invoice_head.getBZ());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderInfo.setByzd1(common_invoice_head.getBYZD1());
        orderInfo.setByzd2(common_invoice_head.getBYZD2());
        orderInfo.setByzd3(common_invoice_head.getBYZD3());
        orderInfo.setByzd4(common_invoice_head.getBYZD4());
        orderInfo.setByzd5(common_invoice_head.getBYZD5());
        return orderInfo;
    }
    
    /**
     * 自动开票接口数据转换--订单明细信息
     *
     * @param common_invoice_details
     * @return
     */
    public static List<OrderItemInfo> transitionOrderItemInfo(CommonInvoiceDetail[] common_invoice_details, String xhfNsrsbh) {
        List<OrderItemInfo> orderItemInfos = new ArrayList<>();
        for (int i = 0; i < common_invoice_details.length; i++) {
            OrderItemInfo orderItemInfo = new OrderItemInfo();
            String sphxh = String.valueOf(i + 1);
            if (0 != common_invoice_details[i].getXMXH()) {
                sphxh = String.valueOf(common_invoice_details[i].getXMXH());
            }
            orderItemInfo.setSphxh(sphxh);
            orderItemInfo.setXmmc(common_invoice_details[i].getXMMC());
            orderItemInfo.setXmdw(common_invoice_details[i].getDW());
            orderItemInfo.setGgxh(common_invoice_details[i].getGGXH());
    
            orderItemInfo.setXmdj(common_invoice_details[i].getXMDJ());
            orderItemInfo.setXmsl(common_invoice_details[i].getXMSL());
            orderItemInfo.setHsbz(common_invoice_details[i].getHSBZ());
            orderItemInfo.setFphxz(common_invoice_details[i].getFPHXZ());
            orderItemInfo.setSpbm(StringUtil.fillZero(common_invoice_details[i].getSPBM(),19));
            orderItemInfo.setZxbm(common_invoice_details[i].getZXBM());
            orderItemInfo.setYhzcbs(common_invoice_details[i].getYHZCBS());
            orderItemInfo.setLslbs(common_invoice_details[i].getLSLBS());
            orderItemInfo.setZzstsgl(common_invoice_details[i].getZZSTSGL());
            orderItemInfo.setKce("");
            orderItemInfo.setXmje(common_invoice_details[i].getXMJE());
            orderItemInfo.setSl(common_invoice_details[i].getSL());
            orderItemInfo.setSe(common_invoice_details[i].getSE());
            orderItemInfo.setXhfNsrsbh(xhfNsrsbh);
            orderItemInfo.setWcje("");
            orderItemInfo.setByzd1(common_invoice_details[i].getBYZD1());
            orderItemInfo.setByzd2(common_invoice_details[i].getBYZD2());
            orderItemInfo.setByzd3(common_invoice_details[i].getBYZD3());
            orderItemInfo.setCreateTime(new Date());
            orderItemInfos.add(orderItemInfo);
        }
        return orderItemInfos;
    }
    
    
    /**
     * 订单processinfo数据补全
     *
     * @param orderInfo
     * @return
     */
    public static void transitionAutoProcessInfo(OrderProcessInfo orderProcessInfo, OrderInfo orderInfo) {

        Date createTime = new Date();
        Date updateTime = createTime;
        orderProcessInfo.setOrderInfoId(orderInfo.getId());
        orderProcessInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        orderProcessInfo.setDdh(orderInfo.getDdh());
        orderProcessInfo.setTqm(orderInfo.getTqm());
        orderProcessInfo.setKphjje(orderInfo.getKphjje());
        orderProcessInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderProcessInfo.setKpse(orderInfo.getHjse());
        orderProcessInfo.setFpzlDm(orderInfo.getFpzlDm());
        orderProcessInfo.setGhfMc(orderInfo.getGhfMc());
        orderProcessInfo.setGhfNsrsbh(orderInfo.getGhfNsrsbh());
        orderProcessInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        orderProcessInfo.setXhfMc(orderInfo.getXhfMc());
        orderProcessInfo.setKpxm(orderInfo.getKpxm());
        orderProcessInfo.setDdcjsj(orderInfo.getDdrq());
        orderProcessInfo.setDdlx(orderInfo.getDdlx());
        orderProcessInfo.setDdly(OrderInfoEnum.ORDER_SOURCE_3.getKey());
        orderProcessInfo.setYwlx(orderInfo.getYwlx());
        orderProcessInfo.setYwlxId(orderInfo.getYwlxId());
        orderProcessInfo.setSbyy("");
        orderProcessInfo.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
        orderProcessInfo.setCreateTime(orderInfo.getCreateTime() == null ? createTime : orderInfo.getCreateTime());
        orderProcessInfo.setUpdateTime(orderInfo.getUpdateTime() == null ? updateTime : orderInfo.getUpdateTime());
        orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_0.getKey());
    }
    
    /**
     * 订单发票表数据入库补全
     *
     * @param orderInfo
     * @return
     */
    public static void transitionOrderInvoiceInfo(OrderInvoiceInfo orderInvoiceInfo, OrderInfo orderInfo) {
        orderInvoiceInfo.setOrderInfoId(orderInfo.getId());
        orderInvoiceInfo.setOrderProcessInfoId(orderInfo.getProcessId());
        orderInvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        // TODO: 2018/10/25 开票流水号对应底层的发票请求流水号,外层进行赋值
        orderInvoiceInfo.setKplsh("");
        orderInvoiceInfo.setDdh(orderInfo.getDdh());
        orderInvoiceInfo.setKphjje(orderInfo.getKphjje());
        orderInvoiceInfo.setKplx(orderInfo.getKplx());
        orderInvoiceInfo.setKprq(null);
        orderInvoiceInfo.setFpdm("");
        orderInvoiceInfo.setFphm("");
        orderInvoiceInfo.setJym("");
        orderInvoiceInfo.setSbyy("");
        orderInvoiceInfo.setCreateTime(new Date());
        orderInvoiceInfo.setUpdateTime(new Date());
        orderInvoiceInfo.setMdh(orderInfo.getMdh());
        orderInvoiceInfo.setGhfMc(orderInfo.getGhfMc());
        orderInvoiceInfo.setGhfSj(orderInfo.getGhfSj());
        orderInvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderInvoiceInfo.setKpse(orderInfo.getHjse());
        orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_0.getKey());
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            orderInvoiceInfo.setChyy(orderInfo.getChyy());
            orderInvoiceInfo.setChsj(new Date());
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
            String[] split = orderInfo.getBz().split(ConfigureConstant.STRING_HZBZ);
            if (split.length > 1) {
                orderInvoiceInfo.setHzxxbbh(split[1]);
            }
        
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
            orderInvoiceInfo.setSykchje("0");
        } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
            orderInvoiceInfo.setSykchje(orderInfo.getKphjje());
        }
        orderInvoiceInfo.setFpzlDm(orderInfo.getFpzlDm());
        orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_1.getKey());
        orderInvoiceInfo.setKpr(orderInfo.getKpr());
        orderInvoiceInfo.setFwm("");
        orderInvoiceInfo.setEwm("");
        orderInvoiceInfo.setJqbh("");
        orderInvoiceInfo.setPdfUrl("");
        orderInvoiceInfo.setRzZt("");
        orderInvoiceInfo.setSld(orderInfo.getSld());
        orderInvoiceInfo.setSldMc(orderInfo.getSldMc());
        orderInvoiceInfo.setFjh(orderInfo.getKpjh());
        orderInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_0.getKey());
        orderInvoiceInfo.setZfyy("");
        orderInvoiceInfo.setXhfMc(orderInfo.getXhfMc());
        orderInvoiceInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        orderInvoiceInfo.setPushStatus(OrderInfoEnum.PUSH_STATUS_0.getKey());
        orderInvoiceInfo.setDyzt(OrderInfoEnum.PRINT_STATUS_0.getKey());
        orderInvoiceInfo.setQdbz(orderInfo.getQdBz());
    }

    /**
     * 根据明细行判断发票是否为清单发票
     *
     * @param orderItemInfos
     * @return
     */
    public static void getOrderInvoiceInfoQdBz(String terminalCode, OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos) {
    
    
        /**
         * 清单标志获取
         * 如果清单标志为空,使用默认值0,
         * 如果清单标志不为空,根据明细行长度判断是否为清单票,然后赋值默认清单名称
         *清单标志0-普通发票;1-普通发票(清单);2-收购发票;3-收购发票(清单);4-成品油发票
         * 如果为红票清单项目名称不一样
         */
    
        String qdxmmc = ConfigureConstant.XJXHQD;
        if (OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            qdxmmc = ConfigureConstant.XJZSXHQD;
        }
        if (StringUtils.isBlank(orderInfo.getQdBz())) {
            orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_0.getKey());
        }
        if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(orderInfo.getQdBz()) && StringUtils.isBlank(orderInfo.getQdXmmc())) {
            orderInfo.setQdXmmc(qdxmmc);
        }
        if (OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz()) && StringUtils.isBlank(orderInfo.getQdXmmc())) {
            orderInfo.setQdXmmc(qdxmmc);
        }
        if (orderItemInfos.size() > ConfigureConstant.INT_8) {
    
            if (OrderInfoEnum.QDBZ_CODE_0.getKey().equals(orderInfo.getQdBz())) {
                orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_1.getKey());
                orderInfo.setQdXmmc(qdxmmc);
            } else if (OrderInfoEnum.QDBZ_CODE_2.getKey().equals(orderInfo.getQdBz())) {
                orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_3.getKey());
                orderInfo.setQdXmmc(qdxmmc);
            }
        }

        //红字发票没有清单
        if(OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())){
            if(orderItemInfos.size() < ConfigureConstant.INT_8){
                if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(orderInfo.getQdBz())) {
                    orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_0.getKey());
                    orderInfo.setQdXmmc(qdxmmc);
                } else if (OrderInfoEnum.QDBZ_CODE_3.getKey().equals(orderInfo.getQdBz())) {
                    orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_2.getKey());
                    orderInfo.setQdXmmc(qdxmmc);
                }
            }
        }



    }


    /**
     * 获取订单批次开票数据
     *
     * @param orderInfo
     * @return
     */
    public static InvoiceBatchRequest getInvoiceBatchRequest(OrderInfo orderInfo) {
        InvoiceBatchRequest invoiceBatchRequest = new InvoiceBatchRequest();
        /**
         * 外层补全数据
         */
        invoiceBatchRequest.setId("");
        invoiceBatchRequest.setFpqqpch("");
        invoiceBatchRequest.setXhfNsrsbh(orderInfo.getNsrsbh());
        invoiceBatchRequest.setSldid(orderInfo.getSld());
        if (StringUtils.isBlank(orderInfo.getKpjh())) {
            invoiceBatchRequest.setKpjh("");
        } else {
            invoiceBatchRequest.setKpjh(orderInfo.getKpjh());
        }
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
            invoiceBatchRequest.setKplx(ConfigureConstant.STRING_2);
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())
                || OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.getFpzlDm())) {
            invoiceBatchRequest.setKplx(ConfigureConstant.STRING_1);
        }
        invoiceBatchRequest.setFplb(orderInfo.getFpzlDm());
        invoiceBatchRequest.setKzzd("");
        invoiceBatchRequest.setCreateTime(new Date());
        invoiceBatchRequest.setUpdateTime(new Date());
        return invoiceBatchRequest;
    }
    
    
    /**
     * 发票作废重构V3--请求协议bean转换
     *
     * @param deprecateInvoicesRsp
     * @return BackCpyKcRequest
     */
    
    public static INVALID_INVOICE_RSP transitionInvoiceInvalidResponseV3(com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP deprecateInvoicesRsp) {
        
        INVALID_INVOICE_RSP invalidInvoiceRsp = new INVALID_INVOICE_RSP();
        invalidInvoiceRsp.setZFPCH(deprecateInvoicesRsp.getZFPCH());
        invalidInvoiceRsp.setSTATUS_CODE(deprecateInvoicesRsp.getSTATUS_CODE());
        invalidInvoiceRsp.setSTATUS_MESSAGE(deprecateInvoicesRsp.getSTATUS_MESSAGE());
        if (deprecateInvoicesRsp.getDeprecate_failed_invoice() != null && deprecateInvoicesRsp.getDeprecate_failed_invoice().length > 0) {
            List<INVALID_INVOICE_INFO> invalid_invoice_infos = new ArrayList<>();
            for (int i = 0; i < deprecateInvoicesRsp.getDeprecate_failed_invoice().length; i++) {
                DEPRECATE_FAILED_INVOICE deprecate_failed_invoice = deprecateInvoicesRsp.getDeprecate_failed_invoice()[i];
                INVALID_INVOICE_INFO invalid_invoice_info = new INVALID_INVOICE_INFO();
                invalid_invoice_info.setFP_DM(deprecate_failed_invoice.getFP_DM());
                invalid_invoice_info.setFP_HM(deprecate_failed_invoice.getFP_HM());
                invalid_invoice_info.setSTATUS_CODE(deprecate_failed_invoice.getSTATUS_CODE());
                invalid_invoice_info.setSTATUS_MESSAGE(deprecate_failed_invoice.getSTATUS_MESSAGE());
                invalid_invoice_infos.add(invalid_invoice_info);
            }
            invalidInvoiceRsp.setINVALID_INVOICE_INFOS(invalid_invoice_infos);
        }

        return invalidInvoiceRsp;
    }
    
    
    /**
     * 红字发票申请表上传重构V3,对外接口转换底层接口--请求协议bean转换
     *
     * @param hzsqdsc_req
     * @return hzfpsqbscsReq
     */
    
    public static HzfpsqbsReq transitionSpecialInvoiceRushRedV3(HZSQDSC_REQ hzsqdsc_req, String sldid, String kpjh, String fplx, String fplb) {
        
        HzfpsqbsReq hzfpsqbsReq = new HzfpsqbsReq();
        HzfpsqbscBatch hzfpsqbscBatch = new HzfpsqbscBatch();
        hzfpsqbscBatch.setSQBSCQQPCH(hzsqdsc_req.getHZSQDSCPC().getSQBSCQQPCH());
        hzfpsqbscBatch.setNSRSBH(hzsqdsc_req.getHZSQDSCPC().getNSRSBH());
        hzfpsqbscBatch.setSLDID(sldid);
        hzfpsqbscBatch.setKPJH(kpjh);
        hzfpsqbscBatch.setFPLX(fplx);
        hzfpsqbscBatch.setFPLB(fplb);
        hzfpsqbscBatch.setSQLB(hzsqdsc_req.getHZSQDSCPC().getSQLB());
        hzfpsqbscBatch.setKZZD(hzsqdsc_req.getHZSQDSCPC().getKZZD());
        hzfpsqbsReq.setHZFPSQBSCSBATCH(hzfpsqbscBatch);
        if (hzsqdsc_req.getHZSQDSCZXX() != null && hzsqdsc_req.getHZSQDSCZXX().size() > 0) {
            Hzfpsqbsc[] hzfpsqbscs = new Hzfpsqbsc[hzsqdsc_req.getHZSQDSCZXX().size()];
            
            for (int i = 0; i < hzsqdsc_req.getHZSQDSCZXX().size(); i++) {
                HZSQDTXX hzsqdtxx = hzsqdsc_req.getHZSQDSCZXX().get(i).getHZSQDTXX();
                
                List<DDMXXX> ddmxxx = hzsqdsc_req.getHZSQDSCZXX().get(i).getDDMXXX();
                
                
                Hzfpsqbsc hzfpsqbsc = new Hzfpsqbsc();
                HzfpsqbsHead hzfpsqbsHead = new HzfpsqbsHead();
                hzfpsqbsHead.setSQBSCQQLSH(hzsqdtxx.getSQBSCQQLSH());
                hzfpsqbsHead.setXXBLX(hzsqdtxx.getXXBLX());
                hzfpsqbsHead.setYFP_DM(hzsqdtxx.getYFPDM());
                hzfpsqbsHead.setYFP_HM(hzsqdtxx.getYFPHM());
                hzfpsqbsHead.setYYSBZ(hzsqdtxx.getYYSBZ());
                hzfpsqbsHead.setYFP_KPRQ(hzsqdtxx.getYFPKPRQ());
                hzfpsqbsHead.setTKSJ(hzsqdtxx.getTKSJ());
                hzfpsqbsHead.setXSF_NSRSBH(hzsqdtxx.getXHFSBH());
                hzfpsqbsHead.setXSF_MC(hzsqdtxx.getXHFMC());
                hzfpsqbsHead.setGMF_NSRSBH(hzsqdtxx.getGMFSBH());
                hzfpsqbsHead.setGMF_MC(hzsqdtxx.getGMFMC());
                hzfpsqbsHead.setHJJE(hzsqdtxx.getHJJE());
                hzfpsqbsHead.setHJSE(hzsqdtxx.getHJSE());
                hzfpsqbsHead.setSQSM(hzsqdtxx.getSQSM());
                hzfpsqbsHead.setBMB_BBH(hzsqdtxx.getBMBBBH());
                hzfpsqbsHead.setKZZD1(hzsqdtxx.getKZZD1());
                hzfpsqbsHead.setKZZD2(hzsqdtxx.getKZZD2());
                
                hzfpsqbsc.setHZFPSQBSCHEAD(hzfpsqbsHead);
                if (ddmxxx != null && ddmxxx.size() > 0) {
                    HzfpsqbsDetail[] hzfpsqbsDetails = new HzfpsqbsDetail[ddmxxx.size()];
                    for (int j = 0; j < ddmxxx.size(); j++) {
                        HzfpsqbsDetail hzfpsqbsDetail = new HzfpsqbsDetail();
                        hzfpsqbsDetail.setXMXH(StringUtils.isNotBlank(ddmxxx.get(j).getXH()) ? ddmxxx.get(j).getXH() : String.valueOf(j + 1));
                        hzfpsqbsDetail.setFPHXZ(ddmxxx.get(j).getFPHXZ());
                        hzfpsqbsDetail.setSPBM(ddmxxx.get(j).getSPBM());
                        hzfpsqbsDetail.setZXBM(ddmxxx.get(j).getZXBM());
                        hzfpsqbsDetail.setYHZCBS(ddmxxx.get(j).getYHZCBS());
                        hzfpsqbsDetail.setLSLBS(ddmxxx.get(j).getLSLBS());
                        hzfpsqbsDetail.setZZSTSGL(ddmxxx.get(j).getZZSTSGL());
                        hzfpsqbsDetail.setXMMC(ddmxxx.get(j).getXMMC());
                        hzfpsqbsDetail.setGGXH(ddmxxx.get(j).getGGXH());
                        hzfpsqbsDetail.setDW(ddmxxx.get(j).getDW());
                        hzfpsqbsDetail.setXMSL(ddmxxx.get(j).getSPSL());
                        hzfpsqbsDetail.setXMDJ(ddmxxx.get(j).getDJ());
                        hzfpsqbsDetail.setXMJE(ddmxxx.get(j).getJE());
                        hzfpsqbsDetail.setHSBZ(ddmxxx.get(j).getHSBZ());
                        hzfpsqbsDetail.setSL(ddmxxx.get(j).getSL());
                        hzfpsqbsDetail.setSE(ddmxxx.get(j).getSE());
                        
                        if (StringUtils.isBlank(ddmxxx.get(j).getGGXH())) {
                            hzfpsqbsDetail.setGGXH("");
                        }
                        hzfpsqbsDetails[j] = hzfpsqbsDetail;
                    }
                    hzfpsqbsc.setHZFPSQBSCDETAILIST(hzfpsqbsDetails);
                }
                hzfpsqbscs[i] = hzfpsqbsc;
            }
            hzfpsqbsReq.setHZFPSQBSCLIST(hzfpsqbscs);
        }
        return hzfpsqbsReq;
    }
    
    /**
     * 红字发票申请表上传重构V3,底层接口转换对外接口--响应协议bean转换
     *
     * @param hzfpsqbscsRsp
     * @return hzfpsqbscsReq
     */
    
    public static HZSQDSC_RSP transitionSpecialInvoiceRushRedRspV3(HpUploadResponse hzfpsqbscsRsp) {
        
        HZSQDSC_RSP hzsqdsc_rsp = new HZSQDSC_RSP();
        hzsqdsc_rsp.setSQBSCQQPCH(hzfpsqbscsRsp.getResult().getSqbscqqpch());
        hzsqdsc_rsp.setZTDM(hzfpsqbscsRsp.getResult().getStatusCode());
        hzsqdsc_rsp.setZTXX(hzfpsqbscsRsp.getResult().getStatusMessage());
        
        if (hzfpsqbscsRsp.getResult().getResponse_HZFPSQBSC() != null && hzfpsqbscsRsp.getResult().getResponse_HZFPSQBSC().size() > 0) {
            List<HZSQDSCJG> hzsqdscjgs = new ArrayList<>();
            
            for (int i = 0; i < hzfpsqbscsRsp.getResult().getResponse_HZFPSQBSC().size(); i++) {
                HpUploadResponseHzfpsqbsc responseHzfpsqbsc = hzfpsqbscsRsp.getResult().getResponse_HZFPSQBSC().get(i);
                HZSQDSCJG redInvoiceFormUploadResponse = new HZSQDSCJG();
                redInvoiceFormUploadResponse.setSQBSCQQLSH(responseHzfpsqbsc.getSQBSCQQLSH());
                redInvoiceFormUploadResponse.setSQDH(responseHzfpsqbsc.getSQDH());
                redInvoiceFormUploadResponse.setZTDM(responseHzfpsqbsc.getSTATUS_CODE());
                redInvoiceFormUploadResponse.setZTXX(responseHzfpsqbsc.getSTATUS_MESSAGE());
                redInvoiceFormUploadResponse.setXXBBH(responseHzfpsqbsc.getXXBBH());
                hzsqdscjgs.add(redInvoiceFormUploadResponse);
            }
            hzsqdsc_rsp.setHZSQDSCJG(hzsqdscjgs);
        }
        return hzsqdsc_rsp;
    }
    
    /**
     * 红字发票申请表下载重构V3,对外接口转换底层接口--响应协议bean转换
     *
     * @param hzsqdxz_req
     * @return hzfpsqbscsReq
     */
    
    public static HpInvocieRequest transitionDownSpecialInvoiceV3(HZSQDXZ_REQ hzsqdxz_req, String sldid, String kpjh, String fplx, String fplb) {
        
        HpInvocieRequest hpInvocieRequest = new HpInvocieRequest();
        hpInvocieRequest.setSQBXZQQPCH(hzsqdxz_req.getSQBXZQQPCH());
        hpInvocieRequest.setNSRSBH(hzsqdxz_req.getNSRSBH());
        hpInvocieRequest.setSLDID(sldid);
        hpInvocieRequest.setKPJH(kpjh);
        hpInvocieRequest.setFPLX(fplx);
        hpInvocieRequest.setFPLB(fplb);
        hpInvocieRequest.setTKRQ_Q(hzsqdxz_req.getTKRQQ());
        hpInvocieRequest.setTKRQ_Z(hzsqdxz_req.getTKRQZ());
        hpInvocieRequest.setGMF_NSRSBH(hzsqdxz_req.getGMFSBH());
        hpInvocieRequest.setXSF_NSRSBH(hzsqdxz_req.getXHFSBH());
        hpInvocieRequest.setXXBBH(hzsqdxz_req.getXXBBH());
        hpInvocieRequest.setXXBFW(hzsqdxz_req.getXXBFW());
        
        hpInvocieRequest.setPageNo(hzsqdxz_req.getYS());
        hpInvocieRequest.setPageSize(hzsqdxz_req.getGS());
        
        return hpInvocieRequest;
    }
    
    /**
     * 红字发票申请表下载重构V3,底层接口转换对外接口--响应协议bean转换
     *
     * @param hpResponseBean
     * @return hzfpsqbscsReq
     */
    
    public static HZSQDXZ_RSP transitionDownSpecialInvoiceRspV3(HpResponseBean hpResponseBean) {
        
        HZSQDXZ_RSP hzsqdxz_rsp = new HZSQDXZ_RSP();
        
        hzsqdxz_rsp.setSQBXZQQPCH(hpResponseBean.getResult().getSqbscqqpch());
        hzsqdxz_rsp.setCGGS(hpResponseBean.getResult().getSuccess_COUNT());
        hzsqdxz_rsp.setZTDM(hpResponseBean.getResult().getStatusCode());
        hzsqdxz_rsp.setZTXX(hpResponseBean.getResult().getStatusMessage());
        
        
        if (hpResponseBean.getResult().getRedinvreqbillxx() != null && hpResponseBean.getResult().getRedinvreqbillxx().size() > 0) {
            List<HZSQDXZZXX> hzsqdxzzxxes = new ArrayList<>();
            List<ResponseHzfpsqbsc> redinvreqbillxxes = hpResponseBean.getResult().getRedinvreqbillxx();
    
            for (ResponseHzfpsqbsc redinvreqbillxx : redinvreqbillxxes) {
                HZSQDXZZXX hzsqdxzzxx = new HZSQDXZZXX();
                HZSQDXZTXX hzsqdxztxx = new HZSQDXZTXX();
                hzsqdxztxx.setSQDH(redinvreqbillxx.getSqdh());
                hzsqdxztxx.setXXBBH(redinvreqbillxx.getXxbbh());
                hzsqdxztxx.setYFPDM(redinvreqbillxx.getYfp_DM());
                hzsqdxztxx.setYFPHM(redinvreqbillxx.getYfp_HM());
                hzsqdxztxx.setFPLXDM(redinvreqbillxx.getFplb());
                hzsqdxztxx.setDSLBZ(redinvreqbillxx.getDslbz());
                hzsqdxztxx.setTKSJ(redinvreqbillxx.getTksj());
                hzsqdxztxx.setXHFSBH(redinvreqbillxx.getXsf_NSRSBH());
                hzsqdxztxx.setXHFMC(redinvreqbillxx.getXsf_MC());
                hzsqdxztxx.setGMFSBH(redinvreqbillxx.getGmf_NSRSBH());
                hzsqdxztxx.setGMFMC(redinvreqbillxx.getGmf_MC());
                hzsqdxztxx.setHJJE(redinvreqbillxx.getHjje());
                hzsqdxztxx.setHJSE(redinvreqbillxx.getHjse());
                hzsqdxztxx.setSQSM(redinvreqbillxx.getSqsm());
                hzsqdxztxx.setBMBBBH(redinvreqbillxx.getBmb_BBH());
                hzsqdxztxx.setYYSBZ(redinvreqbillxx.getYysbz());
                hzsqdxztxx.setZTDM(redinvreqbillxx.getStatus_CODE());
                hzsqdxztxx.setZTXX(redinvreqbillxx.getStatus_MESSAGE());
        
        
                hzsqdxzzxx.setHZSQDXZTXX(hzsqdxztxx);
        
                List<Commoninvdetail> commoninvdetailList = redinvreqbillxx.getCommoninvdetails();
                if (commoninvdetailList != null && commoninvdetailList.size() > 0) {
    
                    List<DDMXXX> orderInvoiceItems = new ArrayList<>();
                    for (Commoninvdetail commoninvdetail : commoninvdetailList) {
                        DDMXXX ddmxxx = new DDMXXX();
                        ddmxxx.setXH(String.valueOf(commoninvdetail.getXmxh()));
                        ddmxxx.setFPHXZ(commoninvdetail.getFphxz());
                        ddmxxx.setSPBM(commoninvdetail.getSpbm());
                        ddmxxx.setZXBM(commoninvdetail.getZxbm());
                        ddmxxx.setYHZCBS(commoninvdetail.getYhzcbs());
                        ddmxxx.setLSLBS(commoninvdetail.getLslbs());
                        ddmxxx.setZZSTSGL(commoninvdetail.getZzstsgl());
                        ddmxxx.setXMMC(commoninvdetail.getXmmc());
                        ddmxxx.setGGXH(commoninvdetail.getGgxh());
                        ddmxxx.setDW(commoninvdetail.getDw());
                        ddmxxx.setSPSL(commoninvdetail.getXmsl());
                        ddmxxx.setDJ(commoninvdetail.getXmdj());
                        ddmxxx.setJE(commoninvdetail.getXmje());
                        ddmxxx.setHSBZ(commoninvdetail.getHsbz());
                        ddmxxx.setSL(commoninvdetail.getSl());
                        ddmxxx.setSE(commoninvdetail.getSe());
                        ddmxxx.setKCE("");
        
                        orderInvoiceItems.add(ddmxxx);
                    }
                    hzsqdxzzxx.setDDMXXX(orderInvoiceItems);
                }
                hzsqdxzzxxes.add(hzsqdxzzxx);
        
        
            }
            hzsqdxz_rsp.setHZSQDXZZXX(hzsqdxzzxxes);
            
        }
        
        return hzsqdxz_rsp;
    }
    
    /**
     * 红字信息表下载协议bean转换
     *
     * @param uploadHpSqd
     * @return
     */
    @SuppressWarnings("AliDeprecation")
    public static GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP convertHzResponse(HpResponseBean uploadHpSqd) {
        GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP response = new GETREDEINVOICEAPPLICATIONRESUlT_STATUS_RSP();
        if (Constant.SUCCSSCODE.equals(uploadHpSqd.getCode())) {
            
            if (uploadHpSqd.getResult() != null) {
                if (Constant.SUCCSSCODE.equals(uploadHpSqd.getResult().getStatusCode())) {
                    if (CollectionUtils.isNotEmpty(uploadHpSqd.getResult().getRedinvreqbillxx())) {
                        REDINVREQBILLXX[] redinvreqbillxx = new REDINVREQBILLXX[uploadHpSqd.getResult().getRedinvreqbillxx().size()];
                        int j = 0;
                        for (ResponseHzfpsqbsc repsonse : uploadHpSqd.getResult().getRedinvreqbillxx()) {
                            REDINVREQBILLXX rep = new REDINVREQBILLXX();
                            rep.setBMB_BBH(repsonse.getBmb_BBH());
                            rep.setDSLBZ(repsonse.getDslbz());
                            rep.setFPLB(repsonse.getFplb());
                            rep.setFPLX(repsonse.getFplx());
                            rep.setGMF_MC(repsonse.getGmf_MC());
                            rep.setGMF_NSRSBH(repsonse.getGmf_NSRSBH());
                            rep.setHJJE(repsonse.getHjje());
                            rep.setHJSE(repsonse.getHjse());
                            rep.setSQDH(repsonse.getSqdh());
                            rep.setSQSM(repsonse.getSqsm());
                            rep.setSTATUS_CODE(repsonse.getStatus_CODE());
                            rep.setSTATUS_MESSAGE(repsonse.getStatus_MESSAGE());
                            rep.setTKSJ(repsonse.getTksj());
                            rep.setXSF_MC(repsonse.getXsf_MC());
                            rep.setXSF_NSRSBH(repsonse.getXsf_NSRSBH());
                            rep.setXXBBH(repsonse.getXxbbh());
                            rep.setYFP_DM(repsonse.getYfp_DM());
                            rep.setYFP_HM(repsonse.getYfp_HM());
                            rep.setYYSBZ(repsonse.getYysbz());
                            if (CollectionUtils.isNotEmpty(repsonse.getCommoninvdetails())) {
                                com.dxhy.order.consumer.protocol.order.COMMON_INVOICE_DETAIL[] common_INVOICE_DETAIL = new com.dxhy.order.consumer.protocol.order.COMMON_INVOICE_DETAIL[repsonse.getCommoninvdetails().size()];
                                int i = 0;
                                for (Commoninvdetail det : repsonse.getCommoninvdetails()) {
                                    com.dxhy.order.consumer.protocol.order.COMMON_INVOICE_DETAIL detail = new com.dxhy.order.consumer.protocol.order.COMMON_INVOICE_DETAIL();
                                    detail.setXMXH(det.getXmxh());
                                    detail.setFPHXZ(det.getFphxz());
                                    detail.setSPBM(det.getSpbm());
                                    detail.setZXBM(det.getZxbm());
                                    detail.setYHZCBS(det.getYhzcbs());
                                    detail.setLSLBS(det.getLslbs());
                                    detail.setZZSTSGL(det.getZzstsgl());
                                    detail.setXMMC(det.getXmmc());
                                    detail.setGGXH(det.getGgxh());
                                    detail.setDW(det.getDw());
                                    detail.setXMSL(det.getXmsl());
                                    detail.setXMDJ(det.getXmdj());
                                    detail.setXMJE(det.getXmje());
                                    detail.setHSBZ(det.getHsbz());
                                    detail.setSL(det.getSl());
                                    detail.setSE(det.getSe());
                                    common_INVOICE_DETAIL[i] = detail;
                                    i++;
                                    
                                }
                                rep.setCOMMONINVDETAILS(common_INVOICE_DETAIL);
                                
                            }
                            redinvreqbillxx[j] = rep;
                            j++;
                        }
                        response.setREDINVREQBILLXX(redinvreqbillxx);
                    }
                }
                response.setSTATUS_CODE(uploadHpSqd.getResult().getStatusCode());
                response.setSTATUS_MESSAGE(uploadHpSqd.getResult().getStatusMessage());
                response.setSQBXZQQPCH(uploadHpSqd.getResult().getSqbscqqpch());
                response.setSUCCESS_COUNT(uploadHpSqd.getResult().getSuccess_COUNT());
            } else {
                response.setSTATUS_CODE(uploadHpSqd.getCode());
                response.setSTATUS_MESSAGE(uploadHpSqd.getMsg());
            }
        } else {
            response.setSTATUS_CODE(uploadHpSqd.getCode());
            response.setSTATUS_MESSAGE(uploadHpSqd.getMsg());
        }
        return response;
    }
    
    /**
     * 补全销方信息
     *
     * @param commonOrderInfo
     * @param sysDeptEntity
     * @return
     */
    public static CommonOrderInfo transitionOrderSellerInfo(CommonOrderInfo commonOrderInfo, DeptEntity sysDeptEntity) {
        
        /**
         * 如果销方地址为空则进行补全
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfDz())) {
            commonOrderInfo.getOrderInfo().setXhfDz(sysDeptEntity.getTaxpayerAddress());
            /**
             * 如果销方电话为空则进行补全
             */
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfDh())) {
                commonOrderInfo.getOrderInfo().setXhfDh(sysDeptEntity.getTaxpayerPhone());
            }
        }
    
    
        /**
         * 如果销方银行为空则进行补全
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfYh())) {
            commonOrderInfo.getOrderInfo().setXhfYh(sysDeptEntity.getTaxpayerBank());
    
            /**
             * 如果销方帐号为空则进行补全
             */
            if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getXhfZh())) {
                commonOrderInfo.getOrderInfo().setXhfZh(sysDeptEntity.getTaxpayerAccount());
            }
        }
        
        return commonOrderInfo;
    }
    
    /**
     * 补全购方信息
     *
     * @param commonOrderInfo
     * @param buyerEntity
     * @return
     */
    public static CommonOrderInfo transitionOrderBuyerInfo(CommonOrderInfo commonOrderInfo, BuyerEntity buyerEntity) {
    
        /**
         * 如果购方税号为空,补全税号
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfQylx())) {
            commonOrderInfo.getOrderInfo().setGhfQylx(buyerEntity.getGhfQylx());
        }
    
        /**
         * 如果购方税号为空,补全税号
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfNsrsbh())) {
            commonOrderInfo.getOrderInfo().setGhfNsrsbh(buyerEntity.getTaxpayerCode());
        }
    
        /**
         * 如果购方名称为空,补全名称
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfMc())) {
            commonOrderInfo.getOrderInfo().setGhfMc(buyerEntity.getPurchaseName());
        }
        
        /**
         * 如果购方地址为空,补全地址
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfDz())) {
            commonOrderInfo.getOrderInfo().setGhfDz(buyerEntity.getAddress());
        }
        
        /**
         * 如果购方电话为空,补全电话
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfDh())) {
            commonOrderInfo.getOrderInfo().setGhfDh(buyerEntity.getPhone());
        }
        
        /**
         * 如果购方银行为空,补全银行
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfYh())) {
            commonOrderInfo.getOrderInfo().setGhfYh(buyerEntity.getBankOfDeposit());
        }
        
        /**
         * 如果购方帐号为空,补全帐号
         */
        if (StringUtils.isBlank(commonOrderInfo.getOrderInfo().getGhfZh())) {
            commonOrderInfo.getOrderInfo().setGhfZh(buyerEntity.getBankNumber());
        }
        
        
        return commonOrderInfo;
    }
    
    /**
     * 订单批次号转换
     *
     * @param common_ORDER_BATCH
     * @return OrderBatchRequest
     * @author: 陈玉航
     * @date: Created on 2019年7月23日 上午10:39:37
     */
    public static OrderBatchRequest transitionOrderBatchRequest(CommonInvoicesBatch common_ORDER_BATCH) {
        OrderBatchRequest orderBatchRequest = new OrderBatchRequest();
        orderBatchRequest.setCreateTime(new Date());
        orderBatchRequest.setUpdateTime(new Date());
        orderBatchRequest.setDdqqpch(common_ORDER_BATCH.getFPQQPCH());
        orderBatchRequest.setFpzldm(common_ORDER_BATCH.getFPLB());
        orderBatchRequest.setKpfs(ConfigureConstant.STRING_0);
        orderBatchRequest.setKpjh(common_ORDER_BATCH.getKPJH());
        orderBatchRequest.setStatus(OrderInfoEnum.ORDER_BATCH_STATUS_0.getKey());
        orderBatchRequest.setSldid(common_ORDER_BATCH.getSLDID());
        orderBatchRequest.setXhfNsrsbh(common_ORDER_BATCH.getNSRSBH());
        return orderBatchRequest;
    }
    
    /**
     * 订单批次号转换
     *
     * @param ddpcxx
     * @return OrderBatchRequest
     * @author: 陈玉航
     * @date: Created on 2019年7月23日 上午10:39:37
     */
    public static OrderBatchRequest transitionOrderBatchRequestV3(DDPCXX ddpcxx) {
        OrderBatchRequest orderBatchRequest = new OrderBatchRequest();
        orderBatchRequest.setCreateTime(new Date());
        orderBatchRequest.setUpdateTime(new Date());
        orderBatchRequest.setDdqqpch(ddpcxx.getDDQQPCH());
        orderBatchRequest.setFpzldm(ddpcxx.getFPLXDM());
        orderBatchRequest.setKpfs(ddpcxx.getKPFS());
        orderBatchRequest.setStatus(OrderInfoEnum.ORDER_BATCH_STATUS_0.getKey());
        orderBatchRequest.setXhfNsrsbh(ddpcxx.getNSRSBH());
        return orderBatchRequest;
    }
    
    
    /**
     * 订单业务bean替换特殊字符
     *
     * @param commonOrderInfo
     * @return
     */
    public static CommonOrderInfo replaceCharacter(CommonOrderInfo commonOrderInfo) {
        //先处理其他 4 个字段
        commonOrderInfo.setFlagbs(StringUtil.replaceStr(commonOrderInfo.getFlagbs()));
        commonOrderInfo.setHzfpxxbbh(StringUtil.replaceStr(commonOrderInfo.getHzfpxxbbh()));
        commonOrderInfo.setSjywly(StringUtil.replaceStr(commonOrderInfo.getSjywly()));
        commonOrderInfo.setKpfs(StringUtil.replaceStr(commonOrderInfo.getKpfs()));
        //orderInfo
        OrderInfo orderInfo = commonOrderInfo.getOrderInfo();
        orderInfo.setYwlx(StringUtil.replaceStr(orderInfo.getYwlx()));
        orderInfo.setYfpHm(StringUtil.replaceStr(orderInfo.getYfpHm()));
        orderInfo.setYfpDm(StringUtil.replaceStr(orderInfo.getYfpDm()));
        orderInfo.setXhfZh(StringUtil.replaceStr(orderInfo.getXhfZh()));
        orderInfo.setXhfYh(StringUtil.replaceStr(orderInfo.getXhfYh()));
        orderInfo.setXhfNsrsbh(StringUtil.replaceStr(orderInfo.getXhfNsrsbh()));
        //销货方名称和购货方名称 不替换
        orderInfo.setXhfDh(StringUtil.replaceStr(orderInfo.getXhfDh()));
        orderInfo.setXhfDz(StringUtil.replaceStr(orderInfo.getXhfDz()));
        //Date格式不处理
        orderInfo.setUpdateTime(orderInfo.getUpdateTime());
        orderInfo.setTschbz(StringUtil.replaceStr(orderInfo.getTschbz()));
        orderInfo.setThdh(StringUtil.replaceStr(orderInfo.getThdh()));
        orderInfo.setSwjgDm(StringUtil.replaceStr(orderInfo.getSwjgDm()));
        orderInfo.setStatus(StringUtil.replaceStr(orderInfo.getStatus()));
        orderInfo.setSldMc(StringUtil.replaceStr(orderInfo.getSldMc()));
        orderInfo.setSkr(StringUtil.replaceStr(orderInfo.getSkr()));
        orderInfo.setSld(StringUtil.replaceStr(orderInfo.getSld()));
        orderInfo.setQdXmmc(StringUtil.replaceStr(orderInfo.getQdXmmc()));
        orderInfo.setQdBz(StringUtil.replaceStr(orderInfo.getQdBz()));
        orderInfo.setPydm(StringUtil.replaceStr(orderInfo.getPydm()));
        orderInfo.setProcessId(StringUtil.replaceStr(orderInfo.getProcessId()));
        orderInfo.setNsrsbh(StringUtil.replaceStr(orderInfo.getNsrsbh()));
        orderInfo.setNsrmc(StringUtil.replaceStr(orderInfo.getNsrmc()));
        orderInfo.setNsrdzdah(StringUtil.replaceStr(orderInfo.getNsrdzdah()));
        orderInfo.setMdh(StringUtil.replaceStr(orderInfo.getMdh()));
        orderInfo.setKpxm(StringUtil.replaceStr(orderInfo.getKpxm()));
        orderInfo.setKpr(StringUtil.replaceStr(orderInfo.getKpr()));
        orderInfo.setKplx(StringUtil.replaceStr(orderInfo.getKplx()));
        orderInfo.setKpjh(StringUtil.replaceStr(orderInfo.getKpjh()));
        orderInfo.setKphjje(StringUtil.replaceStr(orderInfo.getKphjje()));
        orderInfo.setId(StringUtil.replaceStr(orderInfo.getId()));
        orderInfo.setHyMc(StringUtil.replaceStr(orderInfo.getHyMc()));
        orderInfo.setHyDm(StringUtil.replaceStr(orderInfo.getHyDm()));
        orderInfo.setHjse(StringUtil.replaceStr(orderInfo.getHjse()));
        orderInfo.setHjbhsje(StringUtil.replaceStr(orderInfo.getHjbhsje()));
        orderInfo.setGhfZh(StringUtil.replaceStr(orderInfo.getGhfZh()));
        orderInfo.setGhfYh(StringUtil.replaceStr(orderInfo.getGhfYh()));
        orderInfo.setGhfSj(StringUtil.replaceStr(orderInfo.getGhfSj()));
        orderInfo.setGhfSf(StringUtil.replaceStr(orderInfo.getGhfSf()));
        orderInfo.setGhfQylx(StringUtil.replaceStr(orderInfo.getGhfQylx()));
        orderInfo.setGhfNsrsbh(StringUtil.replaceStr(orderInfo.getGhfNsrsbh()));
        //销货方名称和购货方名称 不替换
        orderInfo.setGhfEmail(StringUtil.replaceStr(orderInfo.getGhfEmail()));
        orderInfo.setGhfDz(StringUtil.replaceStr(orderInfo.getGhfDz()));
        orderInfo.setGhfDh(StringUtil.replaceStr(orderInfo.getGhfDh()));
        orderInfo.setFpzlDm(StringUtil.replaceStr(orderInfo.getFpzlDm()));
        orderInfo.setFpqqlsh(StringUtil.replaceStr(orderInfo.getFpqqlsh()));
        orderInfo.setFhr(StringUtil.replaceStr(orderInfo.getFhr()));
        orderInfo.setDsptbm(StringUtil.replaceStr(orderInfo.getDsptbm()));
        orderInfo.setDkbz(StringUtil.replaceStr(orderInfo.getDkbz()));
        orderInfo.setDdlx(StringUtil.replaceStr(orderInfo.getDdlx()));
        orderInfo.setDdh(StringUtil.replaceStr(orderInfo.getDdh()));
        orderInfo.setCzdm(StringUtil.replaceStr(orderInfo.getCzdm()));
        //date格式不处理
        orderInfo.setCreateTime(orderInfo.getCreateTime());
        orderInfo.setChyy(StringUtil.replaceStr(orderInfo.getChyy()));
        //备注特殊处理
        orderInfo.setBz(StringUtil.replaceStr(orderInfo.getBz(), false));
        orderInfo.setByzd5(StringUtil.replaceStr(orderInfo.getByzd5()));
        orderInfo.setByzd4(StringUtil.replaceStr(orderInfo.getByzd4()));
        orderInfo.setByzd3(StringUtil.replaceStr(orderInfo.getByzd3()));
        orderInfo.setByzd2(StringUtil.replaceStr(orderInfo.getByzd2()));
        orderInfo.setByzd1(StringUtil.replaceStr(orderInfo.getByzd1()));
        orderInfo.setBbmBbh(StringUtil.replaceStr(orderInfo.getBbmBbh()));
        //orderItemInfo
        List<OrderItemInfo> orderItemInfos = commonOrderInfo.getOrderItemInfo();
        for (OrderItemInfo orderItemInfo : orderItemInfos) {
            orderItemInfo.setZxbm(StringUtil.replaceStr(orderItemInfo.getZxbm()));
            orderItemInfo.setZzstsgl(StringUtil.replaceStr(orderItemInfo.getZzstsgl()));
            orderItemInfo.setYhzcbs(StringUtil.replaceStr(orderItemInfo.getYhzcbs()));
            orderItemInfo.setXmsl(StringUtil.replaceStr(orderItemInfo.getXmsl()));
            orderItemInfo.setXmmc(StringUtil.replaceStr(orderItemInfo.getXmmc()));
            orderItemInfo.setXmje(StringUtil.replaceStr(orderItemInfo.getXmje()));
            orderItemInfo.setXmdw(StringUtil.replaceStr(orderItemInfo.getXmdw()));
            orderItemInfo.setXmdj(StringUtil.replaceStr(orderItemInfo.getXmdj()));
            orderItemInfo.setWcje(StringUtil.replaceStr(orderItemInfo.getWcje()));
            orderItemInfo.setSphxh(StringUtil.replaceStr(orderItemInfo.getSphxh()));
            orderItemInfo.setSpbm(StringUtil.replaceStr(orderItemInfo.getSpbm()));
            orderItemInfo.setSl(StringUtil.replaceStr(orderItemInfo.getSl()));
            orderItemInfo.setSe(StringUtil.replaceStr(orderItemInfo.getSe()));
            orderItemInfo.setOrderInfoId(StringUtil.replaceStr(orderItemInfo.getOrderInfoId()));
            orderItemInfo.setLslbs(StringUtil.replaceStr(orderItemInfo.getLslbs()));
            orderItemInfo.setKce(StringUtil.replaceStr(orderItemInfo.getKce()));
            orderItemInfo.setXhfNsrsbh(StringUtil.replaceStr(orderItemInfo.getXhfNsrsbh()));
            orderItemInfo.setId(StringUtil.replaceStr(orderItemInfo.getId()));
            orderItemInfo.setHsbz(StringUtil.replaceStr(orderItemInfo.getHsbz()));
            orderItemInfo.setGgxh(StringUtil.replaceStr(orderItemInfo.getGgxh()));
            orderItemInfo.setFphxz(StringUtil.replaceStr(orderItemInfo.getFphxz()));
            //date格式不处理
            orderItemInfo.setCreateTime(orderItemInfo.getCreateTime());
            orderItemInfo.setByzd5(StringUtil.replaceStr(orderItemInfo.getByzd5()));
            orderItemInfo.setByzd4(StringUtil.replaceStr(orderItemInfo.getByzd4()));
            orderItemInfo.setByzd3(StringUtil.replaceStr(orderItemInfo.getByzd3()));
            orderItemInfo.setByzd2(StringUtil.replaceStr(orderItemInfo.getByzd2()));
            orderItemInfo.setByzd1(StringUtil.replaceStr(orderItemInfo.getByzd1()));
    
        }
        return commonOrderInfo;
    }
    
    public static DDPCXX_RSP convertMapToCommonRsp(R r) {
        DDPCXX_RSP orderRsp = new DDPCXX_RSP();
        orderRsp.setDDQQPCH(r.get("fpqqpch") == null ? "" : String.valueOf(r.get("fpqqpch")));
        orderRsp.setZTDM(r.get(OrderManagementConstant.CODE) == null ? ""
                : String.valueOf(r.get(OrderManagementConstant.CODE)));
        orderRsp.setZTXX(r.get(OrderManagementConstant.MESSAGE) == null ? ""
                : String.valueOf(r.get(OrderManagementConstant.MESSAGE)));
        return orderRsp;
    }
    
    
    /**
     * bean转换
     */
    public static BuyerEntity createBuyerEntity(OrderInfo orderInfo) {
        BuyerEntity buyerEntity = new BuyerEntity();
        /**
         * 纳税人识别号
         */
        buyerEntity.setTaxpayerCode(orderInfo.getGhfNsrsbh());
        /**
         * 购货方名称
         */
        buyerEntity.setPurchaseName(orderInfo.getGhfMc());
        /**
         * 购货方地址
         */
        buyerEntity.setAddress(orderInfo.getGhfDz());
        /**
         * 购货方电话
         */
        buyerEntity.setPhone(orderInfo.getGhfDh());
        /**
         * 开户银行
         */
        buyerEntity.setBankOfDeposit(orderInfo.getGhfYh());
        /**
         * 购货方账号
         */
        buyerEntity.setBankNumber(orderInfo.getGhfZh());
        /**
         * 购货方邮箱
         */
        buyerEntity.setEmail(orderInfo.getGhfEmail());
        /**
         * 购货方企业类型
         */
        buyerEntity.setGhfQylx(orderInfo.getGhfQylx());
        /**
         * 销货方纳税人识别号
         */
        buyerEntity.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        /**
         * 销货方纳税人名称
         */
        buyerEntity.setXhfMc(orderInfo.getXhfMc());
        return buyerEntity;
    }

    /**
     * 导入已开发票信息：将订单发票协议bean转换为订单业务bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/15
     * @param orderInvoiceInfo 订单发票全数据协议beanV3
     * @return com.dxhy.order.model.OrderInfo
     */
    public static OrderInfo transitionInsertOrderInfo(DDFPXX orderInvoiceInfo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setFpqqlsh(orderInvoiceInfo.getDDQQLSH());
        orderInfo.setDdh(orderInvoiceInfo.getDDH());
        orderInfo.setThdh(orderInvoiceInfo.getTHDH());
        //订单类型
        orderInfo.setDdlx(OrderInfoEnum.ORDER_TYPE_6.getKey());
        orderInfo.setDsptbm("");
        orderInfo.setNsrsbh(orderInvoiceInfo.getNSRSBH());
        orderInfo.setNsrmc(orderInvoiceInfo.getNSRMC());
        orderInfo.setNsrdzdah(orderInvoiceInfo.getNSRSBH());
        orderInfo.setSwjgDm("");
        //代开标志
        orderInfo.setDkbz(ConfigureConstant.STRING_0);
        orderInfo.setPydm("");
        //开票项目
        orderInfo.setKpxm("");
        orderInfo.setBbmBbh(orderInvoiceInfo.getBMBBBH());

        orderInfo.setXhfMc(orderInvoiceInfo.getXHFMC());
        orderInfo.setXhfNsrsbh(orderInvoiceInfo.getXHFSBH());
        orderInfo.setXhfDz(orderInvoiceInfo.getXHFDZ());
        orderInfo.setXhfDh(orderInvoiceInfo.getXHFDH());
        orderInfo.setXhfYh(orderInvoiceInfo.getXHFYH());
        orderInfo.setXhfZh(orderInvoiceInfo.getXHFZH());

        orderInfo.setGhfQylx(orderInvoiceInfo.getGMFLX());
        orderInfo.setGhfSf(orderInvoiceInfo.getGMFSF());
        orderInfo.setGhfId(orderInvoiceInfo.getGMFBM());
        orderInfo.setGhfMc(orderInvoiceInfo.getGMFMC());
        orderInfo.setGhfNsrsbh(orderInvoiceInfo.getGMFSBH());
        orderInfo.setGhfDz(orderInvoiceInfo.getGMFDZ());
        orderInfo.setGhfDh(orderInvoiceInfo.getGMFDH());
        orderInfo.setGhfYh(orderInvoiceInfo.getGMFYH());
        orderInfo.setGhfZh(orderInvoiceInfo.getGMFZH());
        orderInfo.setGhfSj(orderInvoiceInfo.getGMFSJH());
        orderInfo.setGhfEmail(orderInvoiceInfo.getGMFDZYX());

        orderInfo.setHyDm("");
        orderInfo.setHyMc("");
        orderInfo.setKpr(orderInvoiceInfo.getKPR());
        orderInfo.setSkr(orderInvoiceInfo.getSKR());
        orderInfo.setFhr(orderInvoiceInfo.getFHR());
        //订单日期
        Date ddrq = StringUtils.isBlank(orderInvoiceInfo.getDDSJ()) ? new Date() : DateUtilsLocal.getDefaultDate_yyyy_MM_dd_HH_mm_ss(orderInvoiceInfo.getDDSJ());
        if (ddrq == null) {
            ddrq = new Date();
        }
        orderInfo.setDdrq(ddrq);
        orderInfo.setKplx(orderInvoiceInfo.getKPLX());
        //发票种类代码
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(orderInvoiceInfo.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getFPLXDM())) {
            orderInfo.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(orderInvoiceInfo.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInvoiceInfo.getFPLXDM())) {
            orderInfo.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(orderInvoiceInfo.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInvoiceInfo.getFPLXDM())) {
            orderInfo.setFpzlDm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        } else {
            orderInfo.setFpzlDm(orderInvoiceInfo.getFPLXDM());
        }
        orderInfo.setYfpDm(orderInvoiceInfo.getYFPDM());
        orderInfo.setYfpHm(orderInvoiceInfo.getYFPHM());
        orderInfo.setChyy(orderInvoiceInfo.getCHYY());
        orderInfo.setTschbz(orderInvoiceInfo.getTSCHBZ());
        orderInfo.setCzdm("");
        orderInfo.setQdBz(orderInvoiceInfo.getQDBZ());
        orderInfo.setQdXmmc(orderInvoiceInfo.getQDXMMC());

        orderInfo.setKphjje(orderInvoiceInfo.getJSHJ());
        orderInfo.setHjbhsje(orderInvoiceInfo.getHJJE());
        orderInfo.setHjse(orderInvoiceInfo.getHJSE());

        orderInfo.setMdh(orderInvoiceInfo.getMDH());
        orderInfo.setYwlx(orderInvoiceInfo.getYWLX());
        orderInfo.setBz(orderInvoiceInfo.getBZ());
        orderInfo.setKpjh(orderInvoiceInfo.getKPJH());
        orderInfo.setSld(orderInvoiceInfo.getKPZD());
        orderInfo.setTqm(orderInvoiceInfo.getTQM());
        orderInfo.setStatus(orderInvoiceInfo.getDDZT());
        orderInfo.setByzd1(orderInvoiceInfo.getBYZD1());
        orderInfo.setByzd2(orderInvoiceInfo.getBYZD2());
        orderInfo.setByzd3(orderInvoiceInfo.getBYZD3());
        orderInfo.setByzd4(orderInvoiceInfo.getBYZD4());
        orderInfo.setByzd5(orderInvoiceInfo.getBYZD5());
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        return orderInfo;
    }

    /**
     * 转换批量开票数据库实体对象
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     * @param ddfpxx 订单发票全数据协议
     * @return com.dxhy.order.model.InvoiceBatchRequest
     */
    public static InvoiceBatchRequest transitionInvoiceBatchRequest(DDFPXX ddfpxx){
        InvoiceBatchRequest transitionBatchRequest = new InvoiceBatchRequest();
        transitionBatchRequest.setXhfNsrsbh(ddfpxx.getXHFSBH());
        transitionBatchRequest.setSldid(ddfpxx.getKPZD());
        transitionBatchRequest.setKpjh(ddfpxx.getKPJH());

        //发票类别
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(ddfpxx.getFPLXDM())) {
            transitionBatchRequest.setFplb(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(ddfpxx.getFPLXDM())) {
            transitionBatchRequest.setFplb(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddfpxx.getFPLXDM())) {
            transitionBatchRequest.setFplb(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        } else {
            transitionBatchRequest.setFplb(ddfpxx.getFPLXDM());
        }

        //开票类型
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(transitionBatchRequest.getFplb())) {
            transitionBatchRequest.setKplx(OrderInfoEnum.INVOICE_TYPE_2.getKey());
        } else {
            transitionBatchRequest.setKplx(OrderInfoEnum.INVOICE_TYPE_1.getKey());
        }

        if(StringUtils.equals(OrderInfoEnum.ORDER_STATUS_5.getKey(),ddfpxx.getDDZT())){
            transitionBatchRequest.setStatus("2000");
            transitionBatchRequest.setMessage("发票生成PDF成功");
        }
        transitionBatchRequest.setKzzd("");
        transitionBatchRequest.setCreateTime(new Date());
        transitionBatchRequest.setUpdateTime(new Date());
        return transitionBatchRequest;
    }

    /**
     * 构建订单请求批次对象
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     * @param ddfpxx 订单发票全数据协议
     * @return com.dxhy.order.model.OrderBatchRequest
     */
    public static OrderBatchRequest transitionOrderBatchRequest(DDFPXX ddfpxx){
        OrderBatchRequest obr = new OrderBatchRequest();
        obr.setXhfNsrsbh(ddfpxx.getXHFSBH());
        obr.setKpfs(OrderInfoEnum.ORDER_REQUEST_TYPE_0.getKey());
        obr.setSldid(ddfpxx.getKPZD());
        obr.setKpjh(ddfpxx.getKPJH());
        //发票种类代码
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(ddfpxx.getFPLXDM())) {
            obr.setFpzldm(OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(ddfpxx.getFPLXDM())) {
            obr.setFpzldm(OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey());
        } else if (OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(ddfpxx.getFPLXDM()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(ddfpxx.getFPLXDM())) {
            obr.setFpzldm(OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey());
        } else {
            obr.setFpzldm(ddfpxx.getFPLXDM());
        }

        //成品油判断
        if(StringUtils.equals(OrderInfoEnum.QDBZ_CODE_4.getKey(),ddfpxx.getQDBZ())){
            obr.setSfcpy(OrderInfoEnum.ORDER_REQUEST_OIL_1.getKey());
        }else{
            obr.setSfcpy(OrderInfoEnum.ORDER_REQUEST_OIL_0.getKey());
        }
        //批次状态：开票成功
        obr.setStatus(OrderInfoEnum.ORDER_BATCH_STATUS_2.getKey());
        obr.setMessage("开票成功");
        obr.setKzzd("");
        obr.setCreateTime(new Date());
        obr.setUpdateTime(new Date());
        return obr;
    }

    /**
     * 构建原始订单到最终订单的关系bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     * @param orderInfo 订单业务bean
     * @return com.dxhy.order.model.OrderOriginExtendInfo
     */
    public static OrderOriginExtendInfo buildOrderOriginExtendInfo(OrderInfo orderInfo){
        OrderOriginExtendInfo orderOriginExtendInfo = new OrderOriginExtendInfo();
        orderOriginExtendInfo.setOrderId(orderInfo.getId());
        orderOriginExtendInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        orderOriginExtendInfo.setOriginOrderId(orderInfo.getId());
        orderOriginExtendInfo.setOriginFpqqlsh(orderInfo.getFpqqlsh());
        orderOriginExtendInfo.setOriginDdh(orderInfo.getDdh());
        orderOriginExtendInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        orderOriginExtendInfo.setStatus(ConfigureConstant.STRING_0);
        orderOriginExtendInfo.setCreateTime(new Date());
        orderOriginExtendInfo.setUpdateTime(new Date());
        return orderOriginExtendInfo;
    }

    /**
     * 构建发票批量开票明细bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     * @param transitionBatchRequest 批量开票bean
     * @param orderInfo 订单业务bean
     * @param i 已开发票信息集合游标
     * @return com.dxhy.order.model.InvoiceBatchRequestItem
     */
    public static InvoiceBatchRequestItem transitionInvoiceBatchRequestItem(InvoiceBatchRequest transitionBatchRequest,
                                                                 OrderInfo orderInfo,int i){
        InvoiceBatchRequestItem invoiceBatchRequestItem = new InvoiceBatchRequestItem();
        invoiceBatchRequestItem.setInvoiceBatchId(transitionBatchRequest.getId());
        invoiceBatchRequestItem.setFpqqpch(transitionBatchRequest.getFpqqpch());
        invoiceBatchRequestItem.setFpqqlsh(orderInfo.getFpqqlsh());

        DecimalFormat df = new DecimalFormat("000");
        String format = df.format(1);
        String kplsh = transitionBatchRequest.getFpqqpch() + format;
        invoiceBatchRequestItem.setKplsh(kplsh);
        invoiceBatchRequestItem.setXhfNsrsbh(orderInfo.getXhfNsrsbh());

        invoiceBatchRequestItem.setStatus(transitionBatchRequest.getStatus());
        invoiceBatchRequestItem.setMessage(transitionBatchRequest.getMessage());
        invoiceBatchRequestItem.setCreateTime(new Date());
        invoiceBatchRequestItem.setUpdateTime(new Date());
        return invoiceBatchRequestItem;
    }

    /**
     * 转换订单与发票对应关系业务bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/16
     * @param ddfpxx_i 订单发票全数据协议bean
     * @param orderInfo 订单业务bean
     * @param invoiceBatchRequestItem 发票批量开票明细bean
     * @return com.dxhy.order.model.OrderInvoiceInfo
     */
    public static OrderInvoiceInfo transitionOrderInvoiceInfo(DDFPXX ddfpxx_i,OrderInfo orderInfo,InvoiceBatchRequestItem invoiceBatchRequestItem){
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setOrderInfoId(orderInfo.getId());
        orderInvoiceInfo.setOrderProcessInfoId(orderInfo.getProcessId());
        orderInvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
        //开票流水号
        orderInvoiceInfo.setKplsh(invoiceBatchRequestItem.getKplsh());
        orderInvoiceInfo.setDdh(orderInfo.getDdh());
        orderInvoiceInfo.setMdh(orderInfo.getMdh());
        orderInvoiceInfo.setGhfMc(orderInfo.getGhfMc());
        orderInvoiceInfo.setGhfSj(orderInfo.getGhfSj());
        orderInvoiceInfo.setKphjje(orderInfo.getKphjje());
        orderInvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
        orderInvoiceInfo.setKpse(orderInfo.getHjse());
        orderInvoiceInfo.setKplx(orderInfo.getKplx());
        orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_2.getKey());

        Date kprq = StringUtils.isBlank(ddfpxx_i.getKPRQ()) ? new Date() :
                DateUtilsLocal.getDefaultDate_yyyy_MM_dd_HH_mm_ss(ddfpxx_i.getKPRQ());
        if (kprq == null) {
            kprq = new Date();
        }
        orderInvoiceInfo.setKprq(kprq);

        orderInvoiceInfo.setFpdm(ddfpxx_i.getFPDM());
        orderInvoiceInfo.setFphm(ddfpxx_i.getFPHM());
        orderInvoiceInfo.setFpzlDm(orderInfo.getFpzlDm());
        orderInvoiceInfo.setJym(ddfpxx_i.getJYM());
        orderInvoiceInfo.setKpr(ddfpxx_i.getKPR());
        orderInvoiceInfo.setFwm(ddfpxx_i.getFWM());
        orderInvoiceInfo.setEwm("");
        orderInvoiceInfo.setJqbh(ddfpxx_i.getJQBH());
        //pdf流信息
        orderInvoiceInfo.setPdfUrl("");
        orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_0.getKey());

        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
            orderInvoiceInfo.setSykchje("0");
        } else if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(orderInvoiceInfo.getKplx())) {
            orderInvoiceInfo.setSykchje(orderInfo.getKphjje());
        }

        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            orderInvoiceInfo.setChyy(orderInfo.getChyy());
            orderInvoiceInfo.setChsj(new Date());
        }
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) &&
                OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm())) {
            String[] split = orderInfo.getBz().split(ConfigureConstant.STRING_HZBZ);
            if (split.length > 1) {
                orderInvoiceInfo.setHzxxbbh(split[1]);
            }
        }
        orderInvoiceInfo.setZfBz(OrderInfoEnum.INVALID_INVOICE_0.getKey());
        orderInvoiceInfo.setZfsj(null);
        orderInvoiceInfo.setSbyy("");
        orderInvoiceInfo.setRzZt("");
        orderInvoiceInfo.setSld(orderInfo.getSld());
        orderInvoiceInfo.setSldMc(orderInfo.getSldMc());
        orderInvoiceInfo.setFjh(orderInfo.getKpjh());
        orderInvoiceInfo.setXhfMc(orderInfo.getXhfMc());
        orderInvoiceInfo.setXhfNsrsbh(orderInfo.getXhfNsrsbh());
        orderInvoiceInfo.setQdbz(orderInfo.getQdBz());
        orderInvoiceInfo.setDyzt(OrderInfoEnum.PRINT_STATUS_0.getKey());
        orderInvoiceInfo.setPushStatus(OrderInfoEnum.PUSH_STATUS_0.getKey());
        orderInvoiceInfo.setEmailPushStatus(OrderInfoEnum.PUSH_STATUS_0.getKey());
        orderInvoiceInfo.setCreateTime(new Date());
        orderInvoiceInfo.setUpdateTime(new Date());
        return orderInvoiceInfo;
    }

    /**
     * 查询商品信息：转换并获取商品信息公共协议bean
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param commodityCodeEntity 商品编码实体类
     * @return com.dxhy.order.protocol.v4.commodity.SPXX
     */
    public static SPXX transitionSpxx(CommodityCodeEntity commodityCodeEntity){
        SPXX spxx = new SPXX();
        spxx.setSPID(commodityCodeEntity.getId());
        spxx.setSPBM(commodityCodeEntity.getTaxClassCode());
        spxx.setZXBM(commodityCodeEntity.getEncoding());
        spxx.setYHZCBS(commodityCodeEntity.getEnjoyPreferentialPolicies());
        spxx.setLSLBS(commodityCodeEntity.getPreferentialPoliciesType());

        //优惠政策标识
        String yhzcbs = spxx.getYHZCBS();
        //零税率标识
        String lslbs = spxx.getLSLBS();
        //如果YHZCBS为1, 则ZZSTSGL必须填; 如果YHZCBS为0,ZZSTSGL不填
        if(StringUtils.equals(OrderInfoEnum.YHZCBS_1.getKey(),yhzcbs)){
            if(StringUtils.isNotBlank(lslbs)) {
                switch (lslbs) {
                    case "0":
                        spxx.setZZSTSGL(OrderInfoEnum.LSLBS_0.getValue());
                        break;
                    case "1":
                        spxx.setZZSTSGL(OrderInfoEnum.LSLBS_1.getValue());
                        break;
                    case "2":
                        spxx.setZZSTSGL(OrderInfoEnum.LSLBS_2.getValue());
                        break;
                    default:
                        spxx.setZZSTSGL("");
                }
            }
        }else {
            spxx.setZZSTSGL("");
        }
        spxx.setXMMC(commodityCodeEntity.getMerchandiseName());
        spxx.setGGXH(commodityCodeEntity.getSpecificationModel());
        spxx.setDW(commodityCodeEntity.getMeteringUnit());
        spxx.setDJ(commodityCodeEntity.getUnitPrice());
        spxx.setHSBZ(commodityCodeEntity.getTaxLogo());
        spxx.setSL(commodityCodeEntity.getTaxRate());
        spxx.setSPJC(commodityCodeEntity.getTaxClassAbbreviation());
        return spxx;
    }

    /**
     * 同步商品信息：商品信息协议bean转换为商品编码实体类
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/20
     * @param spxxtbReq 同步商品信息请求协议bean
     * @return com.dxhy.order.model.entity.CommodityCodeEntity
     */
    public static CommodityCodeEntity transitionCommodityCodeEntity(SPXXTB_REQ spxxtbReq){
        CommodityCodeEntity commodityCodeEntity = new CommodityCodeEntity();
        //商品对应的ID
        commodityCodeEntity.setId(spxxtbReq.getSPID());
        //销货方纳税人识别号
        commodityCodeEntity.setXhfNsrsbh(spxxtbReq.getXHFSBH());
        //销货方纳税人名称
        commodityCodeEntity.setEnterpriseName(spxxtbReq.getXHFMC());
        //商品税收分类编码
        commodityCodeEntity.setTaxClassCode(spxxtbReq.getSPBM());
        //自行编码
        commodityCodeEntity.setEncoding(spxxtbReq.getZXBM());
        //优惠政策标识
        commodityCodeEntity.setEnjoyPreferentialPolicies(spxxtbReq.getYHZCBS());
        //零税率标识
        commodityCodeEntity.setPreferentialPoliciesType(spxxtbReq.getLSLBS());
        //项目名称
        commodityCodeEntity.setMerchandiseName(spxxtbReq.getXMMC());
        //规格型号
        commodityCodeEntity.setSpecificationModel(spxxtbReq.getGGXH());
        //单位
        commodityCodeEntity.setMeteringUnit(spxxtbReq.getDW());
        //单价
        commodityCodeEntity.setUnitPrice(spxxtbReq.getDJ());
        //含税标志
        commodityCodeEntity.setTaxLogo(spxxtbReq.getHSBZ());
        //税率
        commodityCodeEntity.setTaxRate(spxxtbReq.getSL());
        return commodityCodeEntity;
    }

    /**
     * 将购买方公共协议bean转换为购方信息实体类
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param buyerEntity 购方信息实体类
     * @return com.dxhy.order.protocol.v4.buyermanage.GMFXX_COMMON
     */
    public static GMFXX_COMMON transitionGmfxxCommon(BuyerEntity buyerEntity){
        GMFXX_COMMON gmfxxCommon = new GMFXX_COMMON();
        //购买方编码
        gmfxxCommon.setGMFBM(buyerEntity.getBuyerCode());
        //购买方类型
        gmfxxCommon.setGMFLX(buyerEntity.getGhfQylx());
        //购买方纳税人识别号
        gmfxxCommon.setGMFSBH(buyerEntity.getTaxpayerCode());
        //购买方名称
        gmfxxCommon.setGMFMC(buyerEntity.getPurchaseName());
        //购买方地址
        gmfxxCommon.setGMFDZ(buyerEntity.getAddress());
        //购买方电话
        gmfxxCommon.setGMFDH(buyerEntity.getPhone());
        //购买方银行名称
        gmfxxCommon.setGMFYH(buyerEntity.getBankOfDeposit());
        //购买方银行账号
        gmfxxCommon.setGMFZH(buyerEntity.getBankNumber());
        //备注
        gmfxxCommon.setBZ(buyerEntity.getRemarks());
        return gmfxxCommon;
    }

    /**
     * 将同步购买方信息请求协议bean转换为购方信息实体类
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/21
     * @param gmfxxtbReq 同步购买方信息请求协议bean
     * @return com.dxhy.order.model.entity.BuyerEntity
     */
    public static BuyerEntity transitionBuyerEntity(GMFXXTB_REQ gmfxxtbReq){
        BuyerEntity buyerEntity = new BuyerEntity();
        //购买方编码
        buyerEntity.setBuyerCode(gmfxxtbReq.getGMFBM());
        //销货方纳税人识别号
        buyerEntity.setXhfNsrsbh(gmfxxtbReq.getXHFSBH());
        //销货方纳税人名称
        buyerEntity.setXhfMc(gmfxxtbReq.getXHFMC());
        //购买方类型
        buyerEntity.setGhfQylx(gmfxxtbReq.getGMFLX());
        //购买方纳税人识别号
        buyerEntity.setTaxpayerCode(gmfxxtbReq.getGMFSBH());
        //购买方名称
        buyerEntity.setPurchaseName(gmfxxtbReq.getGMFMC());
        //购买方地址
        buyerEntity.setAddress(gmfxxtbReq.getGMFDZ());
        //购买方电话
        buyerEntity.setPhone(gmfxxtbReq.getGMFDH());
        //购买方银行名称
        buyerEntity.setBankOfDeposit(gmfxxtbReq.getGMFYH());
        //购买方银行账号
        buyerEntity.setBankNumber(gmfxxtbReq.getGMFZH());
        //备注
        buyerEntity.setRemarks(gmfxxtbReq.getBZ());
        return buyerEntity;
    }
    
    /**
     * 税控设备信息转换
     *
     * @param sksbxxtbReq
     * @return
     */
    public static TaxEquipmentInfo transitionTaxEquipment(SKSBXXTB_REQ sksbxxtbReq) {
        TaxEquipmentInfo taxEquipmentInfo = new TaxEquipmentInfo();
        taxEquipmentInfo.setId("");
        taxEquipmentInfo.setXhfNsrsbh(sksbxxtbReq.getXHFSBH());
        taxEquipmentInfo.setXhfMc(sksbxxtbReq.getXHFMC());
        taxEquipmentInfo.setGroupId("");
        taxEquipmentInfo.setGroupName("");
        taxEquipmentInfo.setSksbCode(sksbxxtbReq.getSKSBDM());
        String sksbMc = "";
        if (OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_UNKNOW.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_C48.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_A9.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_FGBW.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_FGHX.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_FGHX.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_UKEY.getValue();
        } else if (OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(sksbxxtbReq.getSKSBDM())) {
            sksbMc = OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getValue();
        }
        taxEquipmentInfo.setSksbName(sksbMc);
        taxEquipmentInfo.setSksbType(sksbxxtbReq.getSKSBXH());
        taxEquipmentInfo.setLinkTime(DateUtilsLocal.getDefaultDate_yyyy_MM_dd_HH_mm_ss(sksbxxtbReq.getGLSJ()));
        taxEquipmentInfo.setSfdm(sksbxxtbReq.getSFDM());
        taxEquipmentInfo.setSfmc(sksbxxtbReq.getSFMC());
        taxEquipmentInfo.setBz(sksbxxtbReq.getBZ());
        taxEquipmentInfo.setCreateUserId("");
        taxEquipmentInfo.setUpdateUserId("");
        taxEquipmentInfo.setCreateTime(new Date());
        taxEquipmentInfo.setUpdateTime(new Date());
    
    
        return taxEquipmentInfo;
    }
    
    /**
     * 订单数据和发票数据对外明细协议bean转换
     *
     * @param orderItemInfos
     * @return
     */
    public static List<FG_ORDER_INVOICE_ITEM> transitionFGORDER_INVOICE_ITEM(List<OrderItemInfo> orderItemInfos, OrderInfo orderInfo, FG_COMMON_ORDER_HEAD fg_order_invoice_head) {
        List<FG_ORDER_INVOICE_ITEM> order_invoice_items = new ArrayList<>();
    
        boolean result = orderItemInfos.size() > 8 && OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())
                && (OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(orderInfo.getFpzlDm()) ||
                OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(orderInfo.getFpzlDm()));
        if (result) {
            FG_ORDER_INVOICE_ITEM order_invoice_item = new FG_ORDER_INVOICE_ITEM();
            Double xmje = 0D;
            Double se = 0D;
            boolean flagSl = false;
            String sl = orderItemInfos.get(0).getSl();
            fg_order_invoice_head.setZHSL(sl);
            boolean flagSpbm = false;
            String spbm = orderItemInfos.get(0).getSpbm();
            for (OrderItemInfo orderItemInfo : orderItemInfos) {
                if (!sl.equals(orderItemInfo.getSl())) {
                    flagSl = true;
                    fg_order_invoice_head.setSFDSL(ConfigureConstant.STRING_1);
                    fg_order_invoice_head.setZHSL("");
                }
                if (!spbm.equals(orderItemInfo.getSpbm())) {
                    flagSpbm = true;
                }
                Double valueOf = Double.valueOf(orderItemInfo.getXmje());
                xmje = DecimalCalculateUtil.add(xmje, valueOf);
                Double valueOf2 = Double.valueOf(orderItemInfo.getSe());
                se = DecimalCalculateUtil.add(se, valueOf2);
            }
            order_invoice_item.setFPHXZ(OrderInfoEnum.FPHXZ_CODE_0.getKey());
            orderInfo.setQdBz(OrderInfoEnum.QDBZ_CODE_1.getKey());
            order_invoice_item.setXMXH("1");
            order_invoice_item.setSL(orderItemInfos.get(0).getSl());
            if (flagSl) {
                order_invoice_item.setSL("");
            }
            order_invoice_item.setSPBM(orderItemInfos.get(0).getSpbm());
            if (flagSpbm) {
                order_invoice_item.setSPBM("");
            }
            order_invoice_item.setGGXH("");
            order_invoice_item.setDW("");
            order_invoice_item.setXMSL("");
            order_invoice_item.setXMDJ("");
            order_invoice_item.setZXBM("");
            // TODO 规则未定，底层先让传0
            order_invoice_item.setYHZCBS("0");
            order_invoice_item.setLSLBS("");
            order_invoice_item.setZZSTSGL("");
            order_invoice_item.setXMMC("详见对应正数发票及清单");
            order_invoice_item.setXMJE(String.valueOf(xmje).contains("-") ? String.valueOf(xmje) : ("-" + xmje));
            // TODO 传来的item金额都是查询蓝票的金额，都是价税分离后的，含税标志为0不含税
            order_invoice_item.setHSBZ("0");
            order_invoice_item.setSE(String.valueOf(se).contains("-") ? String.valueOf(se) : ("-" + se));
            order_invoice_items.add(order_invoice_item);
            return order_invoice_items;
        }

        //判断是红票还是蓝票
        for (int i = 0; i < orderItemInfos.size(); i++) {
            FG_ORDER_INVOICE_ITEM order_invoice_item = new FG_ORDER_INVOICE_ITEM();
            OrderItemInfo orderItemInfo = orderItemInfos.get(i);
            order_invoice_item.setXMXH(orderItemInfo.getSphxh());
            order_invoice_item.setFPHXZ(orderItemInfo.getFphxz());
            order_invoice_item.setSPBM(orderItemInfo.getSpbm());
            order_invoice_item.setZXBM(orderItemInfo.getZxbm());
            order_invoice_item.setYHZCBS(orderItemInfo.getYhzcbs());
            order_invoice_item.setLSLBS(orderItemInfo.getLslbs());
            order_invoice_item.setZZSTSGL(orderItemInfo.getZzstsgl());
            /**
             * 项目明细信息,商品名称去除补全
             * 项目名称一定是补全后的
             * 只需要找到第二个星号,截取就行
             */
            String xmmc = orderItemInfo.getXmmc();
            if (xmmc.contains("*") && xmmc.startsWith("*")) {
                String[] strings = xmmc.split("\\*");
                if (strings.length > 2 && StringUtil.checkName(strings[1])) {
                    order_invoice_item.setXMMC(xmmc.substring(xmmc.indexOf("*", 2) + 1));
                } else {
                    order_invoice_item.setXMMC(orderItemInfo.getXmmc());
                }
        
            } else {
                order_invoice_item.setXMMC(orderItemInfo.getXmmc());
            }
            order_invoice_item.setGGXH(orderItemInfo.getGgxh());
            order_invoice_item.setDW(orderItemInfo.getXmdw());
            order_invoice_item.setXMSL(orderItemInfo.getXmsl());
            order_invoice_item.setXMDJ(orderItemInfo.getXmdj());
            order_invoice_item.setXMJE(orderItemInfo.getXmje());
            order_invoice_item.setHSBZ(orderItemInfo.getHsbz());
            order_invoice_item.setSL(orderItemInfo.getSl());
            order_invoice_item.setSE(orderItemInfo.getSe());
            order_invoice_item.setBYZD1(orderItemInfo.getByzd1());
            order_invoice_item.setBYZD2(orderItemInfo.getByzd2());
            order_invoice_item.setBYZD3(orderItemInfo.getByzd3());
            order_invoice_item.setKCJE("");
            order_invoice_item.setSPSM("");
    
            if (orderItemInfos.size() == 1 && OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx()) && ConfigureConstant.XJZSXHQD.equals(orderItemInfo.getXmmc())) {
                order_invoice_item.setFPHXZ(OrderInfoEnum.FPHXZ_CODE_0.getKey());
                if (StringUtils.isBlank(orderItemInfo.getSl())) {
                    fg_order_invoice_head.setSFDSL(ConfigureConstant.STRING_1);
                    fg_order_invoice_head.setZHSL("");
                } else {
                    fg_order_invoice_head.setZHSL(orderItemInfo.getSl());
                }
            }
            order_invoice_items.add(order_invoice_item);
        }
        return order_invoice_items;
    }

    /**
     * 订单数据和发票数据对外协议bean转换
     *
     * @param orderInfo
     * @param orderInvoiceInfo
     * @return
     */
    public static FG_COMMON_ORDER_HEAD transitionFG_ORDER_INVOICE_HEAD(OrderInfo orderInfo, OrderInvoiceInfo orderInvoiceInfo, List<OrderItemInfo> orderItemInfos) {
        FG_COMMON_ORDER_HEAD order_invoice_head = new FG_COMMON_ORDER_HEAD();
        order_invoice_head.setDDQQLSH(orderInfo.getFpqqlsh());
        order_invoice_head.setNSRSBH(orderInfo.getNsrsbh());
        order_invoice_head.setNSRMC(orderInfo.getNsrmc());
        order_invoice_head.setKPLX(orderInfo.getKplx());
        order_invoice_head.setBMB_BBH(orderInfo.getBbmBbh());
        order_invoice_head.setXSF_NSRSBH(orderInfo.getNsrsbh());
        order_invoice_head.setXSF_MC(orderInfo.getXhfMc());
        order_invoice_head.setXSF_DZ(orderInfo.getXhfDz());
        order_invoice_head.setXSF_DH(orderInfo.getXhfDh());
        order_invoice_head.setXSF_YH(orderInfo.getXhfYh());
        order_invoice_head.setXSF_ZH(orderInfo.getXhfZh());
        order_invoice_head.setGMF_NSRSBH(orderInfo.getGhfNsrsbh());
        order_invoice_head.setGMF_MC(orderInfo.getGhfMc());
        order_invoice_head.setGMF_DZ(orderInfo.getGhfDz());
        order_invoice_head.setGMF_QYLX(orderInfo.getGhfQylx());
        order_invoice_head.setGMF_SF(orderInfo.getGhfSf());
        order_invoice_head.setGMF_GDDH(orderInfo.getGhfDh());
        order_invoice_head.setGMF_SJ(orderInfo.getGhfSj());
        order_invoice_head.setGMF_WX("");
        order_invoice_head.setGMF_EMAIL(orderInfo.getGhfEmail());
        order_invoice_head.setGMF_YH(orderInfo.getGhfYh());
        order_invoice_head.setGMF_ZH(orderInfo.getGhfZh());
        order_invoice_head.setKPR(orderInfo.getKpr());
        order_invoice_head.setSKR(orderInfo.getSkr());
        order_invoice_head.setFHR(orderInfo.getFhr());
        order_invoice_head.setYFP_DM(orderInfo.getYfpDm());
        order_invoice_head.setYFP_HM(orderInfo.getYfpHm());
        order_invoice_head.setQD_BZ(orderInfo.getQdBz());
        order_invoice_head.setQDXMMC(orderInfo.getQdXmmc());
    
        order_invoice_head.setJSHJ(orderInfo.getKphjje());
        order_invoice_head.setHJJE(orderInfo.getHjbhsje());
        order_invoice_head.setHJSE(orderInfo.getHjse());
    
        /**
         * 备注处理,红票去除原发票代码等数据
         */
        String bz = orderInfo.getBz();
        if (bz.contains("对应正数发票代码")) {
            int bzIndex = bz.indexOf("对应正数发票代码");
            bz = bz.substring(0, bzIndex);
            order_invoice_head.setBZ(bz);
        } else {
            order_invoice_head.setBZ(orderInfo.getBz());
        }
    
        order_invoice_head.setPYDM(orderInfo.getPydm());
        order_invoice_head.setCHYY(orderInfo.getChyy());
        order_invoice_head.setTSCHBZ(orderInfo.getTschbz());
        order_invoice_head.setFPZLDM(orderInfo.getFpzlDm());
        order_invoice_head.setDDH(orderInfo.getDdh());
        order_invoice_head.setTHDH(orderInfo.getThdh());
        order_invoice_head.setDDDATE(DateUtilsLocal.getYMDHMIS(orderInfo.getDdrq()));
        // TODO: 2018/9/21 后期考虑添加订单类型和订单状态等数据.
        if (orderInvoiceInfo != null) {
            order_invoice_head.setJQBH(orderInvoiceInfo.getSld());
            /**
             * 上传完成信息表编号
             */
            if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInvoiceInfo.getKplx())) {
                order_invoice_head.setHZXXBBH(orderInvoiceInfo.getHzxxbbh());
            }
        }
        order_invoice_head.setBYZD1("");
        order_invoice_head.setBYZD2("");
        order_invoice_head.setBYZD3("");
        order_invoice_head.setBYZD4("");
        order_invoice_head.setBYZD5("");
        //特殊票种
        order_invoice_head.setTSPZ("00");
        //含税税率标识
        order_invoice_head.setHSSLBS(ConfigureConstant.STRING_0);
        order_invoice_head.setHZFW("");
    
    
        boolean isSl = false;
        //处理综合税率标识
        for (int i = 0; i < orderItemInfos.size(); i++) {
            if (orderItemInfos.size() > 1) {
                if (i > 0) {
                    OrderItemInfo previousInfo = orderItemInfos.get(i - 1);
                    OrderItemInfo currentInfo = orderItemInfos.get(i);
                    //多税率
                    if (!previousInfo.getSl().equals(currentInfo.getSl())) {
                        isSl = true;
                    }
                }
            }

        }
        if(isSl){
            order_invoice_head.setSFDSL("1");
            order_invoice_head.setZHSL("");
        }else{ //单税率
            order_invoice_head.setSFDSL("");
            order_invoice_head.setZHSL(orderItemInfos.get(0).getSl());
        }
        order_invoice_head.setSSYF("");
        order_invoice_head.setJMBBH("");
        order_invoice_head.setQMCS(ConfigurerInfo.FG_QMCS);
        return order_invoice_head;
    }
    
}
