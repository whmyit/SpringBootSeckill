package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiInvoiceService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.InvoiceDao;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.DrawerInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liangyuhuan
 * @date 2018/8/1
 */
@Slf4j
@Service
public class InvoiceServiceImpl implements ApiInvoiceService {

    @Resource
    private InvoiceDao invoiceDao;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;

    @Override
    public R saveDrawer(DrawerInfoEntity drawerInfoEntity, List<String> shList) {
        R r = new R();
        String id = drawerInfoEntity.getId();
        if (StringUtils.isNotBlank(id)) {
            //如果id存在  走更新
            int i = invoiceDao.updateDrawer(drawerInfoEntity, shList);
            if (i > 0) {
                r.put("msg", "更新成功").put(OrderManagementConstant.CODE, "0");
            } else {
                r.put("msg", "更新失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }else{
            String uuid = apiInvoiceCommonService.getGenerateShotKey();
            drawerInfoEntity.setId(uuid);
            int i =  invoiceDao.insertDrawer(drawerInfoEntity);
            if(i > 0){
                r.put("msg", "保存成功").put(OrderManagementConstant.CODE, "0");
            }else{
                r.put("msg", "保存失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }
        return r;
    }

    /**
     * @Description  开票人信息查询
     * @Author xieyuanqiang
     * @param userId 当前登录人  taxpayerCode 纳税人识别号
     * @Date 11:02 2018-08-02
     */
    @Override
    public DrawerInfoEntity queryDrawerInfo (String taxpayerCode , String userId){
        log.info("开票人信息查询，参数为当前登录人：{} 纳税人识别号：{}", userId, taxpayerCode);
        List<String> shList = new ArrayList<>();
        shList.add(taxpayerCode);
        DrawerInfoEntity drawerInfoEntity = invoiceDao.queryDrawerInfo(shList, userId);
        log.info("开票人信息查询 结果为{}", drawerInfoEntity == null ? "null" : drawerInfoEntity.toString());
        return drawerInfoEntity;
    }
    
}
