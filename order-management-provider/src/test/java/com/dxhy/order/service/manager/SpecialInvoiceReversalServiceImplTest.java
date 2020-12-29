package com.dxhy.order.service.manager;

import com.dxhy.order.BaseTest;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.entity.SpecialInvoiceReversalItemEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpecialInvoiceReversalServiceImplTest extends BaseTest {


    @Test
    public void submitSpecialInvoiceReversal() {
        SpecialInvoiceReversalEntity specialInvoiceReversal = new SpecialInvoiceReversalEntity();
        specialInvoiceReversal.setId("6552430909058973696");
        specialInvoiceReversal.setType("0");
        specialInvoiceReversal.setSqdh("661545615484190704142104");
        specialInvoiceReversal.setSqsm("0000000100");
        specialInvoiceReversal.setYfpDm("1400111555");
        specialInvoiceReversal.setYfpHm("51000914");
        specialInvoiceReversal.setInvoiceType("1");
        specialInvoiceReversal.setFpzlDm("0");
        specialInvoiceReversal.setTksj(new Date());
        specialInvoiceReversal.setXhfMc("销项测试子公司C48");
        specialInvoiceReversal.setNsrsbh("150001194112132161");
        specialInvoiceReversal.setGhfMc("购方名称法第三方第三方的");
        specialInvoiceReversal.setXhfNsrsbh("911101082018050516");
        specialInvoiceReversal.setGhfqylx("01");
        specialInvoiceReversal.setHjbhsje("-108.85");
        specialInvoiceReversal.setDslbz("0.13");
        specialInvoiceReversal.setHjse("-14.15");
        specialInvoiceReversal.setKphjje("-123.00");
        specialInvoiceReversal.setStatusCode("TZD0500");
        specialInvoiceReversal.setSld("111");
        specialInvoiceReversal.setSldMc("150001194112132161");
        specialInvoiceReversal.setKpr("hghg");
        specialInvoiceReversal.setKpzt("2");
        specialInvoiceReversal.setCreatorId("100783");
        specialInvoiceReversal.setCreatorName("fybc48");
        specialInvoiceReversal.setCreateTime(new Date());
        specialInvoiceReversal.setEditorId("100783");
        specialInvoiceReversal.setUpdateTime(new Date());

        List<SpecialInvoiceReversalItemEntity> specialInvoiceReversalItems = new ArrayList<>();
        SpecialInvoiceReversalItemEntity entity = new SpecialInvoiceReversalItemEntity();
        entity.setId("6552430909130276864");
        entity.setSpecialInvoiceReversalId("6552430909058973696");
        entity.setCode("1070101010100000000");
        entity.setName("*汽油*撒旦法");
        entity.setUnit("升");
        entity.setQuantity("-123.0");
        entity.setUnitPrice("0.88495575");
        entity.setAmount("-108.85");
        entity.setTaxRate("0.13");
        entity.setTaxAmount("-14.15");
        entity.setTaxFlag("0");
        entity.setSeqNum("1");
        entity.setIsSpecial("0");
        entity.setSpecialType("");
        entity.setCreatorId("100783");
        entity.setCreatorName("fybc48");
        entity.setCreateTime(new Date());
        entity.setEditorId("100783");
        entity.setEditorName("fybc48");
        entity.setEditTime(new Date());
        specialInvoiceReversalItems.add(entity);
        /*JSONObject jsonObject = apiSpecialInvoiceReversalService.submitSpecialInvoiceReversal(specialInvoiceReversal, specialInvoiceReversalItems);
        printJSON(jsonObject);*/
    }
}
