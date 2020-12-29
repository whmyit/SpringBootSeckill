package com.dxhy.order;

import com.dxhy.order.model.a9.kp.AllocateInvoicesReq;
import com.dxhy.order.utils.JsonUtils;

/**
 * @author ZSC-DXHY
 * @date 创建时间: 2019/9/3 15:43
 */
public class Test {
    
    public static void main(String[] args) {
        
        String message = "{\"COMMON_INVOICE\":[{\"COMMON_INVOICE_DETAIL\":[{\"DW\":\"\",\"FPHXZ\":\"0\",\"GGXH\":\"\",\"HSBZ\":\"0\",\"LSLBS\":\"\",\"SE\":\"11.18\",\"SL\":\"0.10\",\"SPBM\":\"1010101010000000000\",\"XMBM\":\"\",\"XMDJ\":\"9.09090909\",\"XMJE\":\"111.82\",\"XMMC\":\"*谷物*10\",\"XMSL\":\"12.3\",\"XMXH\":1,\"YHZCBS\":\"0\",\"ZZSTSGL\":\"\"}],\"COMMON_INVOICE_HEAD\":{\"BMB_BBH\":\"33.0\",\"BZ\":\"\",\"FHR\":\"\",\"FPQQLSH\":\"ffe9eb6a0d2245ce8a7fd613972c0418001\",\"GMF_DZ\":\"\",\"GMF_EMAIL\":\"\",\"GMF_GDDH\":\"\",\"GMF_MC\":\"发的\",\"GMF_NSRSBH\":\"FGHJKGHJGHJ434322\",\"GMF_QYLX\":\"01\",\"GMF_SF\":\"\",\"GMF_SJ\":\"\",\"GMF_WX\":\"\",\"GMF_YHZH\":\"\",\"HJJE\":\"111.82\",\"HJSE\":\"11.18\",\"JSHJ\":\"123.00\",\"KPLX\":\"0\",\"KPR\":\"撒旦法\",\"NSRMC\":\"北京贪吃蛇有限公司\",\"NSRSBH\":\"911101082018050516\",\"QD_BZ\":\"0\",\"SKR\":\"\",\"TSCHBZ\":\"0\",\"XSF_DH\":\"87945621\",\"XSF_DZ\":\"测试地址\",\"XSF_MC\":\"北京贪吃蛇有限公司\",\"XSF_NSRSBH\":\"911101082018050516\",\"XSF_YHZH\":\"测试银行654213789\",\"YFP_DM\":\"\",\"YFP_HM\":\"\"},\"COMMON_INVOICE_ORDER\":{\"DDDATE\":\"2019-09-03 14:55:59\",\"DDH\":\"75359519980957893580\",\"THDH\":\"\"}}],\"COMMON_INVOICES_BATCH\":{\"FPLB\":\"51\",\"FPLX\":\"2\",\"FPQQPCH\":\"ffe9eb6a0d2245ce8a7fd613972c0418\",\"KPJH\":\"\",\"KZZD\":\"\",\"NSRSBH\":\"911101082018050516\",\"SLDID\":\"\"},\"TERMINALCODE\":\"001\"}";
//        message = "\"{\\\"COMMON_INVOICE\\\":[{\\\"COMMON_INVOICE_DETAIL\\\":[{\\\"DW\\\":\\\"\\\",\\\"FPHXZ\\\":\\\"0\\\",\\\"GGXH\\\":\\\"\\\",\\\"HSBZ\\\":\\\"0\\\",\\\"LSLBS\\\":\\\"\\\",\\\"SE\\\":\\\"11.18\\\",\\\"SL\\\":\\\"0.10\\\",\\\"SPBM\\\":\\\"1010101010000000000\\\",\\\"XMBM\\\":\\\"\\\",\\\"XMDJ\\\":\\\"9.09090909\\\",\\\"XMJE\\\":\\\"111.82\\\",\\\"XMMC\\\":\\\"*谷物*10\\\",\\\"XMSL\\\":\\\"12.3\\\",\\\"XMXH\\\":1,\\\"YHZCBS\\\":\\\"0\\\",\\\"ZZSTSGL\\\":\\\"\\\"}],\\\"COMMON_INVOICE_HEAD\\\":{\\\"BMB_BBH\\\":\\\"33.0\\\",\\\"BZ\\\":\\\"\\\",\\\"FHR\\\":\\\"\\\",\\\"FPQQLSH\\\":\\\"ffe9eb6a0d2245ce8a7fd613972c0418001\\\",\\\"GMF_DZ\\\":\\\"\\\",\\\"GMF_EMAIL\\\":\\\"\\\",\\\"GMF_GDDH\\\":\\\"\\\",\\\"GMF_MC\\\":\\\"发的\\\",\\\"GMF_NSRSBH\\\":\\\"FGHJKGHJGHJ434322\\\",\\\"GMF_QYLX\\\":\\\"01\\\",\\\"GMF_SF\\\":\\\"\\\",\\\"GMF_SJ\\\":\\\"\\\",\\\"GMF_WX\\\":\\\"\\\",\\\"GMF_YHZH\\\":\\\"\\\",\\\"HJJE\\\":\\\"111.82\\\",\\\"HJSE\\\":\\\"11.18\\\",\\\"JSHJ\\\":\\\"123.00\\\",\\\"KPLX\\\":\\\"0\\\",\\\"KPR\\\":\\\"撒旦法\\\",\\\"NSRMC\\\":\\\"北京贪吃蛇有限公司\\\",\\\"NSRSBH\\\":\\\"911101082018050516\\\",\\\"QD_BZ\\\":\\\"0\\\",\\\"SKR\\\":\\\"\\\",\\\"TSCHBZ\\\":\\\"0\\\",\\\"XSF_DH\\\":\\\"87945621\\\",\\\"XSF_DZ\\\":\\\"测试地址\\\",\\\"XSF_MC\\\":\\\"北京贪吃蛇有限公司\\\",\\\"XSF_NSRSBH\\\":\\\"911101082018050516\\\",\\\"XSF_YHZH\\\":\\\"测试银行654213789\\\",\\\"YFP_DM\\\":\\\"\\\",\\\"YFP_HM\\\":\\\"\\\"},\\\"COMMON_INVOICE_ORDER\\\":{\\\"DDDATE\\\":\\\"2019-09-03 14:55:59\\\",\\\"DDH\\\":\\\"75359519980957893580\\\",\\\"THDH\\\":\\\"\\\"}}],\\\"COMMON_INVOICES_BATCH\\\":{\\\"FPLB\\\":\\\"51\\\",\\\"FPLX\\\":\\\"2\\\",\\\"FPQQPCH\\\":\\\"ffe9eb6a0d2245ce8a7fd613972c0418\\\",\\\"KPJH\\\":\\\"\\\",\\\"KZZD\\\":\\\"\\\",\\\"NSRSBH\\\":\\\"911101082018050516\\\",\\\"SLDID\\\":\\\"\\\"},\\\"TERMINALCODE\\\":\\\"001\\\"}\"";
        
        message = JsonUtils.getInstance().parseObject(message, String.class);
        AllocateInvoicesReq parseObject = JsonUtils.getInstance().parseObject(message, AllocateInvoicesReq.class);
        System.out.println(JsonUtils.getInstance().toJsonString(parseObject));
    
    }
}
