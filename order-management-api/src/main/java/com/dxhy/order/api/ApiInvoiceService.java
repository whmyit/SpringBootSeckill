package com.dxhy.order.api;

import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.DrawerInfoEntity;

import java.util.List;

/**
 * @author liangyuhuan
 * @date 2018/8/1
 */
public interface ApiInvoiceService {
    
    /**
     * 保存开票信息
     *
     * @param drawerInfoEntity
     * @param shList
     * @return
     */
    R saveDrawer(DrawerInfoEntity drawerInfoEntity, List<String> shList);
    
    /**
     * 开票人信息查询
     *
     * @param taxpayerCode
     * @param userId
     * @return
     */
    DrawerInfoEntity queryDrawerInfo(String taxpayerCode, String userId);
    
}
