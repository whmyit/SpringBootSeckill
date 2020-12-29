package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.constant.OrderSeparationException;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceRushRedService;
import com.dxhy.order.consumer.modules.order.service.IGenerateReadyOpenOrderService;
import com.dxhy.order.consumer.modules.order.service.MakeOutAnInvoiceService;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.*;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ZSC-DXHY
 */
@Service
@Slf4j
public class InvoiceRushRedServiceImpl implements InvoiceRushRedService {
    
    private static final String Order_Rush_Red_Service = "普电发票冲红业务类";
    
    @Resource
    private MakeOutAnInvoiceService makeOutAnInvoiceService;
    @Resource
    private IGenerateReadyOpenOrderService generateReadyOpenOrderService;
    
    @Reference
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    @Resource
    private UserInfoService userInfoService;
    
    @Override
    public R eleRush(String fpdm, String fphm, String chyy, List<String> shList) throws OrderSeparationException {
        log.debug("{}接收到发票冲红请求,发票代码{},发票号码{},冲红原因{},税号:{}", Order_Rush_Red_Service, fpdm, fphm, chyy, JsonUtils.getInstance().toJsonString(shList));
        R vo = new R();
        //核对去数据库查询发票信息 返回已冲红或为冲红，返回对应发票信息   这里的原发票号码、代码代发票的代码、号码
        OrderInvoiceInfo orderInvoiceInfo = apiInvoiceCommonService.selectByYfp(fpdm, fphm, shList);
        if (orderInvoiceInfo != null) {
            if (!OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoiceInfo.getChBz())
                    || !OrderInfoEnum.RED_INVOICE_2.getKey().equals(orderInvoiceInfo.getChBz())) {
                OrderInfo orderInfo = apiInvoiceCommonService.selectByOrderInvoiceId(orderInvoiceInfo.getOrderInfoId(), shList);
                List<OrderItemInfo> orderItemInfos = apiInvoiceCommonService.selectOrderItemByOrderInfoId(orderInfo.getId(), shList);
                CommonOrderInfo commonOrderInfo = new CommonOrderInfo();
                /**
                 * 清空order_id和order_process_id,后续插入数据库时做了判断,是否进行更新还是插入,清空后则进行插入
                 */
                orderInfo.setId("");
                orderInfo.setProcessId("");
                commonOrderInfo.setOrderInfo(orderInfo);
                orderInfo.setKplx(OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey());
                orderInfo.setYfpDm(fpdm);
                orderInfo.setYfpHm(fphm);
                orderInfo.setChyy(chyy);
                commonOrderInfo.setOrderItemInfo(orderItemInfos);
                com.dxhy.order.model.R excuSingle = generateReadyOpenOrderService.reshRed(commonOrderInfo, userInfoService.getUser().getUserId().toString(), userInfoService.getUser().getDeptId());
                if (!excuSingle.get(OrderManagementConstant.CODE).equals(OrderInfoContentEnum.SUCCESS.getKey())) {
                    return R.error().put(OrderManagementConstant.CODE, excuSingle.get(OrderManagementConstant.CODE)).put(OrderManagementConstant.MESSAGE, excuSingle.get(OrderManagementConstant.MESSAGE));
                }
                List<CommonOrderInfo> list = (List<CommonOrderInfo>) excuSingle.get(OrderManagementConstant.DATA);
                //调用开票接口
                R r = makeOutAnInvoiceService.makeOutAnInovice(list, null);
                if (OrderInfoContentEnum.SUCCESS.getKey().equals(r.get(OrderManagementConstant.CODE))) {
                    r.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey());
                    vo.putAll(r);
                    return vo;
                } else {
                    vo.putAll(r);
                    return vo;
                }
            } else {
                String ch = null;
                if (OrderInfoEnum.RED_INVOICE_1.getKey().equals(orderInvoiceInfo.getChBz())) {
                    ch = OrderInfoEnum.RED_INVOICE_1.getValue();
                }
                if (OrderInfoEnum.RED_INVOICE_2.getKey().equals(orderInvoiceInfo.getChBz())) {
                    ch = OrderInfoEnum.RED_INVOICE_2.getValue();
                }
                log.debug("{},发票处于:{}", Order_Rush_Red_Service, ch);
                return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_TSCHBZ_ERROR.getKey()).put("msg", ch);
            }
        }
        log.info("发票信息不存在,{}", Order_Rush_Red_Service);
        return vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_NULL.getKey()).put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.INVOICE_RUSH_RED_INVOICE_NULL.getMessage());
    }
    
}
