package com.dxhy.order.service.manager;

import com.dxhy.invoice.protocol.sl.*;
import com.dxhy.order.BaseTest;
import org.junit.Test;


public class ISSServiceImplTest extends BaseTest {

    
    @Test
    public void redInvoiceUpload() {
        HZFPSQBSCS_REQ req = new HZFPSQBSCS_REQ();
        HZFPSQBSCS_BATCH batch = new HZFPSQBSCS_BATCH();
        HZFPSQBSC_HEAD head = new HZFPSQBSC_HEAD();
        HZFPSQBSC_DETAIL[] details = new HZFPSQBSC_DETAIL[1];
        HZFPSQBSC_DETAIL detail = new HZFPSQBSC_DETAIL();
        HZFPSQBSC hzfpsqbsc = new HZFPSQBSC();

        batch.setFPLB("0");
        batch.setFPLX("1");
        batch.setKPJH("");
        batch.setNSRSBH("150001194112132161");
        batch.setSLDID("111");
        batch.setSQBSCQQPCH("661545615484190704093735");
        batch.setSQLB("0");

        head.setSQBSCQQLSH("661545615484190704093735");
        head.setYFP_DM("1400111555");
        head.setYFP_HM("1400111555");
        head.setYFP_KPRQ("");
        head.setXSF_NSRSBH("150001194112132161");
        head.setXSF_MC("销项测试子公司C48");
        head.setGMF_NSRSBH("911101082018050516");
        head.setGMF_MC("购方名称法第三方第三方的");
        head.setHJJE("-108.85");
        head.setHJSE("-14.15");
        head.setSQSM("0000000100");
        head.setBMB_BBH("1.0");
        head.setXXBLX("0");
        head.setTKSJ("20190704093840");
        head.setYYSBZ("0000000000");

        detail.setZZSTSGL("");
        detail.setXMMC("*汽油*撒旦法");
        detail.setGGXH("");
        detail.setDW("升");
        detail.setXMSL("-123.0");
        detail.setXMDJ("0.88495575");
        detail.setXMJE("-108.85");
        detail.setHSBZ("0");
        detail.setSL("0.13");
        detail.setSE("-14.15");
        detail.setXMXH("1");
        detail.setFPHXZ("0");
        detail.setSPBM("1070101010100000000");
        detail.setZXBM("");
        detail.setYHZCBS("0");
        detail.setLSLBS("");
        details[0] = detail;

        hzfpsqbsc.setHZFPSQBSC_HEAD(head);
        hzfpsqbsc.setHZFPSQBSC_DETAIL(details);
        HZFPSQBSC[] hzfpsqbscs = {hzfpsqbsc};

        req.setHZFPSQBSCS(hzfpsqbscs);
        req.setHZFPSQBSCS_BATCH(batch);
        printJSON(req);
//        HZFPSQBSCS_RSP rsp = apiISSService.redInvoiceUpload(req);
//        printJSON(rsp);
    }
    
}
