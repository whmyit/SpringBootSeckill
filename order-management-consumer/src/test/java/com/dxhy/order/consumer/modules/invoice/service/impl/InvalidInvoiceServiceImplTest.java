package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.consumer.BaseTest;
import com.dxhy.order.consumer.modules.invoice.service.InvalidInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;

public class InvalidInvoiceServiceImplTest extends BaseTest {
    
    @Autowired
    private InvalidInvoiceService invalidInvoiceService;

//    @Test
//    public void validInvoice() throws Exception{
////        receviePoint, invoiceType, invoiceCode, invoiceNum, kpjh
//        R r = invalidInvoiceService.validInvoice("120", "2",
//                "014001800205", "25035896", "0", "150001194112132161","大象慧云");
//        printJSON(r);
//    }
////
//    @Test
//    public void batchValidInvoice() throws OrderReceiveException {
//        String[] str = new String[]{"201907051507301147039281781538817"};
//        R r = invalidInvoiceService.batchValidInvoice(str);
//        printJSON(r);
//    }
}
