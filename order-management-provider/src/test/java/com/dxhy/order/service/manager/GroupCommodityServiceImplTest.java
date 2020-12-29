package com.dxhy.order.service.manager;

import com.dxhy.order.BaseTest;
import com.dxhy.order.api.ApiGroupCommodityService;
import com.dxhy.order.dao.GroupCommodityDao;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupCommodity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class GroupCommodityServiceImplTest extends BaseTest {

    @Autowired
    private GroupCommodityDao groupCommodityDao;

    @Autowired
    ApiGroupCommodityService groupCommodityService;


    @Test
    public void queryGroupList() {
        List<String> xfsh = new ArrayList<>();
        xfsh.add("150001194112132161");
//        List<GroupCommodity> list = groupCommodityDao.selectGroupList(xfsh);
//        printJSON(list);
    }

    @Test
    public void saveGroup() {
//        GroupCommodity groupCommodity = new GroupCommodity();
//        groupCommodity.setUserId("100727");
//        groupCommodity.setTaxpayerCode("150001194112132161");
//        R r = groupCommodityService.saveGroup(groupCommodity);
//        printJSON(r);
//        groupCommodity.setId(apiInvoiceCommonService.getGenerateShotKey());
//        int i = groupCommodityDao.insertGroup(groupCommodity);
    }
}
