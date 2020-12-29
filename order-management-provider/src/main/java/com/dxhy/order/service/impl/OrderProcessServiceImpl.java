package com.dxhy.order.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiOrderProcessService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.dao.OrderOriginExtendInfoMapper;
import com.dxhy.order.dao.OrderProcessInfoExtMapper;
import com.dxhy.order.dao.OrderProcessInfoMapper;
import com.dxhy.order.model.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 订单处理业务实现
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 20:44
 */
@Slf4j
@Service
public class OrderProcessServiceImpl implements ApiOrderProcessService {

    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Resource
    private OrderProcessInfoExtMapper orderProcessInfoExtMapper;

    @Resource
    private OrderOriginExtendInfoMapper orderOriginExtendInfoMapper;

    @Resource
    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
    
    @Override
    public OrderProcessInfo selectOrderProcessInfoByProcessId(String id, List<String> shList) {
        return orderProcessInfoMapper.selectOrderProcessInfoByProcessId(id, shList);
    }
    
    @Override
    public OrderProcessInfo queryOrderProcessInfoByFpqqlsh(String fpqqlsh, List<String> shList) {
        return orderProcessInfoMapper.queryOrderProcessInfoByFpqqlsh(fpqqlsh, shList);
    }
    
    @Override
    public List<OrderProcessInfo> selectOrderProcessInfoByDdqqpch(String ddqqpch, List<String> shList) {
        return orderProcessInfoMapper.selectOrderProcessInfoByDdqqpch(ddqqpch, shList);
    }
    
    @Override
    public int updateOrderProcessInfo(OrderInfo orderInfo, List<String> shList) {
        return orderProcessInfoMapper.updateOrderProcessInfo(orderInfo, shList);
    }
    
    @Override
    public PageUtils selectOrderInfo(Map map, List<String> shList) {
        int pageSize = (Integer) map.get("pageSize");
        int currPage = (Integer) map.get("currPage");
        log.info("订单查询，当前页：{},页面条数:{}", currPage, pageSize);
        PageHelper.startPage(currPage, pageSize);
        List<Map> list = orderProcessInfoMapper.queryOrderInfo(map, shList);
        PageInfo<Map> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateKpzt(String fpqqlsh, String ddzt, String kpzt, String sbyy, List<String> shList) {
        boolean isSuccess = false;
        OrderProcessInfo newOrderProcessInfo = new OrderProcessInfo();
        newOrderProcessInfo.setFpqqlsh(fpqqlsh);
        newOrderProcessInfo.setDdzt(ddzt);
        newOrderProcessInfo.setSbyy(sbyy);
        int i = orderProcessInfoMapper.updateOrderProcessInfoByFpqqlsh(newOrderProcessInfo, shList);
        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
        orderInvoiceInfo.setFpqqlsh(fpqqlsh);
        orderInvoiceInfo.setKpzt(kpzt);
        int j = apiOrderInvoiceInfoService.updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
        if (i > 0 && j > 0) {
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public int updateOrderProcessInfoByProcessId(OrderProcessInfo orderProcessInfo, List<String> shList) {
        return orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
    }

    @Override
    public int insert(OrderProcessInfo record) {
        return orderProcessInfoMapper.insertOrderProcessInfo(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateOrderDdztByList(List<OrderInfo> list, String key, List<String> shList) {
        int i = 0;
        for (OrderInfo orderInfo : list) {
            OrderProcessInfo orderProcessInfo2 = new OrderProcessInfo();
            orderProcessInfo2.setOrderInfoId(orderInfo.getId());
            orderProcessInfo2.setDdzt(key);
            int updateByPrimaryKeySelective = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo2, shList);
            if (updateByPrimaryKeySelective <= 0) {
                return updateByPrimaryKeySelective;
            }
            i++;
        }
        return i;
        //return orderProcessInfoMapper.updateDdztByList(list, key);
    }
    
    /**
     * 根据销方税号,订单号,发票请求流水号进行查询orderprocess信息
     *
     * @param map
     * @param shList
     * @return
     */
    @Override
    public List<OrderProcessInfo> selectOrderProcessByFpqqlshDdhNsrsbh(Map map, List<String> shList) {
        return orderProcessInfoMapper.selectOrderProcessByFpqqlshDdhNsrsbh(map, shList);
    }
    
    /**
     * 根据processId获取订单所有的最终子订单
     */
    @Override
    public List<OrderProcessInfo> findChildList(String processId, List<String> shList) {
        List<OrderProcessInfo> finalChildList = new ArrayList<>();
        // 通过处理表id查出所有关联的处理扩展表数据
        List<OrderProcessInfoExt> selectExtByParentProcessId = orderProcessInfoExtMapper.selectExtByParentProcessId(processId, shList);
        
        
        finalChildList = findFianlChildList(selectExtByParentProcessId, finalChildList, shList);
        return finalChildList;
    }
    
    @Override
    public List<OrderProcessInfo> findTopParentList(OrderProcessInfo orderProcessInfo, List<String> shList) {
        //声明原始订单的集合
        List<OrderProcessInfo> topParentList = CollectionUtil.newArrayList(orderProcessInfo);
        //如果当前订单不是原始订单，则查找与之相关的原始订单
        List<OrderProcessInfoExt> selectExtByParentProcessId = orderProcessInfoExtMapper.selectOrderProcessInfoExtByOrderProcessId(orderProcessInfo.getId(), shList);
        if (CollectionUtils.isNotEmpty(selectExtByParentProcessId)) {
            //递归查找原始订单
            topParentList = findTopParentList(selectExtByParentProcessId, topParentList, shList);
        }
        return topParentList;
    }
    
    private List<OrderProcessInfo> findFianlChildList(List<OrderProcessInfoExt> selectExtByParentProcessId,
                                                      List<OrderProcessInfo> finalChildList, List<String> shList) {
    
    
        for (OrderProcessInfoExt ext : selectExtByParentProcessId) {
            // 先查合并再查查分
            List<OrderProcessInfoExt> selectByOrderProcessId = orderProcessInfoExtMapper
                    .selectExtByParentProcessId(ext.getOrderProcessInfoId(), shList);
            OrderProcessInfo selectByPrimaryKey = orderProcessInfoMapper
                    .selectOrderProcessInfoByProcessId(ext.getOrderProcessInfoId(), shList);
            // 有子订单 并且子订单的parentprocessId 和 orderProcessId不相同的为再次拆分合并后的订单
            boolean result = selectByOrderProcessId != null && selectByOrderProcessId.size() > 0
                    && !(selectByOrderProcessId.size() == 1 && selectByOrderProcessId.get(0).getParentOrderProcessId()
                    .equals(selectByOrderProcessId.get(0).getOrderProcessInfoId()))
                    && selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
            if (result) {
                findFianlChildList(selectByOrderProcessId, finalChildList, shList);
        
            } else {
        
                if (!selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey())) {
                    finalChildList.add(selectByPrimaryKey);
                }
                continue;
            }
        }
        return finalChildList;
    }
    
    /**
     * 递归查询原始订单
     *
     * @param selectExtByParentProcessId 订单处理扩展信息集合
     * @param topParentList              原始订单集合（包含当前订单）
     * @return java.util.List<java.util.Map < java.lang.String, com.dxhy.order.model.OrderProcessInfo>>
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/4/10
     */
    private List<OrderProcessInfo> findTopParentList(List<OrderProcessInfoExt> selectExtByParentProcessId,
                                                     List<OrderProcessInfo> topParentList, List<String> shList) {
        for (OrderProcessInfoExt ext : selectExtByParentProcessId) {
            //根据当前订单的父订单id查询订单处理扩展表
            List<OrderProcessInfoExt> selectByOrderProcessId = orderProcessInfoExtMapper
                    .selectOrderProcessInfoExtByOrderProcessId(ext.getParentOrderProcessId(), shList);
            //根据当前订单的父订单id查询订单处理表
            OrderProcessInfo selectByPrimaryKey = orderProcessInfoMapper
                    .selectOrderProcessInfoByProcessId(ext.getParentOrderProcessId(), shList);
            if (CollectionUtils.isNotEmpty(selectByOrderProcessId)) {
                findTopParentList(selectByOrderProcessId, topParentList, shList);
            } else {
                //判断订单类型是否为原始订单
                if (selectByPrimaryKey.getDdlx().equals(OrderInfoEnum.ORDER_TYPE_0.getKey())) {
                    topParentList.add(selectByPrimaryKey);
                }
            }
        }
        return topParentList;
    }
    
    @Override
    public PageUtils selectYwlxCount(Map<String, Object> map, List<String> shList) {
        int pageSize = (Integer) map.get("pageSize");
        int currPage = (Integer) map.get("currPage");
        //这里前端从1开始需要进行-1操作
        //		currPage=currPage-1;
        PageHelper.startPage(currPage, pageSize);
        List<Map<String, Object>> list = orderProcessInfoMapper.selectYwlxCount(map, shList);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }
    
    @Override
    public Map<String, String> selectYwlxCountTotal(Map<String, Object> paramMap, List<String> shList) {
        Map<String, String> map = orderProcessInfoMapper.selectYwlxCountTotal(paramMap, shList);
        return map;
    }
    
    @Override
    public OrderProcessInfo selectByOrderId(String orderId, List<String> shList) {
        return orderProcessInfoMapper.selectByOrderId(orderId,shList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateListOrderProcessInfoByProcessId(List<OrderProcessInfo> updateList) {
        int i = 0;
        for(OrderProcessInfo process : updateList){
            List<String> shList = new ArrayList<>();
            shList.add(process.getXhfNsrsbh());
            i = i + orderProcessInfoMapper.updateOrderProcessInfoByProcessId(process, shList);
        }
        return i;
    }

    @Override
    public boolean isExistNoAuditOrder(Map<String,Object> paramMap,List<String> shList) {

        String existNoAuditOrder = orderProcessInfoMapper.isExistNoAuditOrder(paramMap, shList);
        return existNoAuditOrder != null;
    }

    @Override
    public int updateOrderProcessInfoByFpqqlsh(OrderProcessInfo updateProcessInfo, List<String> shList) {

        return orderProcessInfoMapper.updateOrderProcessInfoByFpqqlsh(updateProcessInfo,shList);
    }

}
