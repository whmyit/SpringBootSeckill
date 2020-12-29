package com.dxhy.order.service.impl;

import com.dxhy.order.api.OrderRollbackService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.OrderOriginExtendInfoMapper;
import com.dxhy.order.dao.OrderProcessInfoExtMapper;
import com.dxhy.order.dao.OrderProcessInfoMapper;
import com.dxhy.order.exceptions.OrderReceiveException;
import com.dxhy.order.model.OrderOriginExtendInfo;
import com.dxhy.order.model.OrderProcessInfo;
import com.dxhy.order.model.OrderProcessInfoExt;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Description:订单回退实现类
 * @author: chengyafu
 * @date: 2018年7月31日 下午2:02:58
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class OrderRollbackServiceImpl implements OrderRollbackService {
    
    private static final String LOGGER_MSG = "(订单回退业务类)";
    
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    @Resource
    private OrderProcessInfoExtMapper orderProcessInfoExtMapper;
    @Resource
    private OrderOriginExtendInfoMapper orderOriginExtendInfoMapper;
    
    @Override
    public R orderRollback(List<Map> orderId) {
        log.debug("{},收到回退请求,数据{}", LOGGER_MSG, orderId);
        R vo = new R();
        int k = 0;
        int b = 0;
        List<String> errorMessageList = new ArrayList<>();
        for (Map map : orderId) {
            
            String id = (String) map.get("id");
            String nsrsbh = (String) map.get("xhfNsrsbh");
            List<String> shList = new ArrayList<>();
            shList.add(nsrsbh);
            // 根据订单id查询处理表 得到处理表id
            OrderProcessInfo processInfo = orderProcessInfoMapper.selectByOrderId(id, shList);
            if (processInfo != null) {
                String jsonString = JsonUtils.getInstance().toJsonString(processInfo);
                log.debug("{}，通过当前订单id得到处理表数据：{}", LOGGER_MSG, jsonString);
                R rollback = rollback(processInfo);
                if (ConfigureConstant.STRING_0000.equals(rollback.get(OrderManagementConstant.CODE))) {
                    k += 1;
                }
                if (OrderInfoContentEnum.ORDER_ROLLBACK_DATA_EXCEPTION_ERROR.getKey().equals(rollback.get(OrderManagementConstant.CODE))) {
                    k += 1;
                } else {
                    if (rollback.get(OrderManagementConstant.MESSAGE) != null && !"".equals(rollback.get(OrderManagementConstant.MESSAGE))) {
                        errorMessageList.add(String.valueOf(rollback.get(OrderManagementConstant.MESSAGE)));
                    }
                }
            } else {
                log.debug("{}此orderId:{}没有处理表信息", LOGGER_MSG, id);
                
                b += 1;
            }
        }
        if (orderId.size() == k) {
            log.debug("{},全部成功", LOGGER_MSG);
            vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey()).put("msg", OrderInfoContentEnum.SUCCESS.getMessage());
            return vo;
        }
        if (orderId.size() == b) {
            log.debug("{},全部失败", LOGGER_MSG);
            vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey()).put("msg", OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
            return vo;
        }
        log.debug("{},成功回退{}条数据", LOGGER_MSG, k);
        vo.put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey()).put("msg", "成功回退" + k + "条数据").put("errorMessage", errorMessageList);
        return vo;
    }
    /**
     * 订单回退业务 逻辑更新 回退订单添加限制条件 拆分的订单的同级订单如果再次拆分合并 不允许回退
     */
    public R rollback(OrderProcessInfo processInfo) {
        R r = new R();
        List<String> shList = new ArrayList<>();
        shList.add(processInfo.getXhfNsrsbh());
        try {
            // 异常回退
            if (OrderInfoEnum.ORDER_STATUS_6.getKey().equals(processInfo.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(processInfo.getDdzt())) {
                // 修改处理表状态为待开具ORDER_STATUS_3
                log.debug("{}要回退异常订单的orderId：{}", LOGGER_MSG, processInfo.getOrderInfoId());
                OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
                orderProcessInfo.setId(processInfo.getId());
                orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_3.getKey());
                int updateResult = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
                if (updateResult <= 0) {
                    log.error("{}，异常订单回退失败，订单id：{}", LOGGER_MSG, processInfo.getOrderInfoId());
                    return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_ROLLBACK_DATA_EXCEPTION_ERROR, null);
                }
                // 拆分后回退
            } else if (OrderInfoEnum.ORDER_TYPE_0.getKey().equals(processInfo.getDdlx())) {
                if (OrderInfoEnum.ORDER_STATUS_0.getKey().equals(processInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_1.getKey().equals(processInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_2.getKey().equals(processInfo.getDdzt()) || OrderInfoEnum.ORDER_STATUS_3.getKey().equals(processInfo.getDdzt())) {
                    log.error("{}，原始订单不需要回退，订单id：{}", LOGGER_MSG, processInfo.getOrderInfoId());
                    return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_ROLLBACK_StATUS_ERROR, null);
                }
            } else {
    
                /**
                 *
                 * 1.根据当前需要处理的数据,查询当前订单是否允许进行回退,如果不允许返回失败.
                 * 2.如果允许,查询当前订单的父级订单数据,然后获取同级的数据.
                 * 3.汇总数据归为两类,一类为需要更新为无效状态的数据,一类为需要更新为有效状态的数据
                 * 4.查询同级订单是被拆分还是被合并,被合并的为寻找有效状态数据,被拆分的为寻找无效状态数据
                 * 5.优先获取被拆分数据,然后获取被合并数据,
                 */
    
                if (OrderInfoEnum.ORDER_VALID_STATUS_1.getKey().equals(processInfo.getOrderStatus())) {
        
                    log.error("{}，当前订单状态无效不需要回退，订单id：{}", LOGGER_MSG, processInfo.getOrderInfoId());
                    return R.setCodeAndMsg(OrderInfoContentEnum.ORDER_ROLLBACK_DATA_EXCEPTION_ERROR, null);
                }
    
                Set<String> validStatusData = new HashSet<>();
                Set<String> originValidStatusData = new HashSet<>();
                Set<String> statusData = new HashSet<>();
                Set<String> originStatusData = new HashSet<>();
                List<OrderProcessInfo> childrenList = new ArrayList<>();
                List<OrderProcessInfoExt> selectExtByParentProcessId = new ArrayList<>();
    
    
                /**
                 * 前提条件,只有拆分与合并插入处理扩展表数据,其他情况不插入数据.
                 * 定位当前数据是拆分后数据还是合并后数据,
                 * 如果是拆分后数据,需要递归获取该数据同级数据向下分裂的数据
                 * 如果是合并数据,直接把当前数据放在无效状态数据中,把他父级数据放在有效状态数据中
                 *
                 */
                //判断是否为合并后数据,如果数组长度大于1,说明是合并后的数据,此时,只需要把当前数据状态改为无效状态,把当前数据对应的子集改为有效状态.结束.
                List<OrderProcessInfoExt> orderProcessInfoExts1 = orderProcessInfoExtMapper.selectOrderProcessInfoExtByOrderProcessId(processInfo.getId(), shList);
                //如果根据当前id查询数据后存在数据,说明该数据是合并后数据,把当前是数据存放在无效状态数据中,把其他数据存放在有效状态数据中
                if (orderProcessInfoExts1 != null && orderProcessInfoExts1.size() > 0) {
                    if (orderProcessInfoExts1.size() > 1) {
                        validStatusData.add(processInfo.getId());
                        originValidStatusData.add(processInfo.getOrderInfoId());
                        for (OrderProcessInfoExt orderProcessInfoExt : orderProcessInfoExts1) {
                            statusData.add(orderProcessInfoExt.getParentOrderProcessId());
                            originStatusData.add(orderProcessInfoExt.getParentOrderInfoId());
                        }
                        selectExtByParentProcessId.addAll(orderProcessInfoExts1);
                    } else {
                        //判断是否为拆分后数据,拆分后数据,需要获取父级数据,然后递归获取有效无效状态
                        List<OrderProcessInfoExt> parentOrderProcessInfoExts = orderProcessInfoExtMapper.selectExtByParentProcessId(orderProcessInfoExts1.get(0).getParentOrderProcessId(), shList);
                        //如果根据父id查询数据后存在数据,说明该数据是拆分后数据,把父级数据改为有效状态,
                        if (parentOrderProcessInfoExts != null && parentOrderProcessInfoExts.size() > 0) {
                            //不用循环单独处理,迭代时会判断
                            statusData.add(orderProcessInfoExts1.get(0).getParentOrderProcessId());
                            originStatusData.add(orderProcessInfoExts1.get(0).getParentOrderInfoId());
                            //遍历所有兄弟节点，排除父节点合并后回退产生的干扰
                            for (int i = 0 ; i < parentOrderProcessInfoExts.size() ; i++) {
                                OrderProcessInfoExt ext = parentOrderProcessInfoExts.get(i);
                                OrderProcessInfo opi = orderProcessInfoMapper
                                        .selectOrderProcessInfoByProcessId(ext.getOrderProcessInfoId(), shList);
                                if (OrderInfoEnum.ORDER_TYPE_2.getKey().equals(opi.getDdlx())) {
                                    log.info("拆分后的订单回退，存在父节点合并回退产生的子节点，排除掉，processid：{}",opi.getId());
                                    parentOrderProcessInfoExts.remove(ext);
                                }
                            }
                            selectExtByParentProcessId.addAll(parentOrderProcessInfoExts);
                            findFianlChildList(selectExtByParentProcessId, validStatusData, originValidStatusData, shList,statusData,originStatusData);
                        }
                    }
                }
                updateByTranaction(selectExtByParentProcessId,validStatusData,statusData,originValidStatusData,originStatusData,shList);

            }
        } catch (OrderReceiveException e) {
            log.error("订单回退异常，异常信息:{}", e);
            return R.error().put(OrderManagementConstant.CODE, e.getCode()).put(OrderManagementConstant.MESSAGE,
                    "订单号为" + processInfo.getDdh() + "的订单异常：" + e.getMessage());
        } catch (Exception e) {
            log.error("发生未知异常;{}", e);
            return R.error();
        }
        return r;
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateByTranaction(List<OrderProcessInfoExt> selectExtByParentProcessId, Set<String> validStatusData,
                                    Set<String> statusData, Set<String> originValidStatusData, Set<String> originStatusData,List<String> shList) {

        //更细订单process扩展表的状态为无效
        for(OrderProcessInfoExt ext : selectExtByParentProcessId){
            //更新process扩展表为无效状态
            OrderProcessInfoExt updateExt = new OrderProcessInfoExt();
            updateExt.setId(ext.getId());
            updateExt.setStatus("1");
            List<String> list = new ArrayList<>();
            list.add(ext.getXhfNsrsbh());
            int i = orderProcessInfoExtMapper.updateByPrimaryKeySelective(updateExt, list);
            if (i <= 0) {
                log.error("更新扩展表失败");
            }
        }

        // 更新子订单状态为无效
        log.debug("{}需要更新为无效状态的数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(validStatusData));
        for (String validProcessId : validStatusData) {
            OrderProcessInfo orderProcessInfo2 = new OrderProcessInfo();
            orderProcessInfo2.setId(validProcessId);
            orderProcessInfo2.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
            int updateByPrimaryKeySelective = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo2, shList);
            if (updateByPrimaryKeySelective <= 0) {
                log.error("更新扩展表失败");

            }
        }
        log.debug("{}需要更新为有效状态的数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(statusData));
        // 修改父订单状态为有效
        for (String processId : statusData) {
            OrderProcessInfo orderProcessInfo2 = new OrderProcessInfo();
            orderProcessInfo2.setId(processId);
            orderProcessInfo2.setOrderStatus(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
            int updateByPrimaryKeySelective = orderProcessInfoMapper.updateOrderProcessInfoByProcessId(orderProcessInfo2, shList);
            if (updateByPrimaryKeySelective <= 0) {
                log.error("更新扩展表失败");

            }
        }
        //更新原始订单扩展表的数据
        for (String orderId : originValidStatusData) {
            //更新子订单的原始订单表的扩展数据
            OrderOriginExtendInfo originExtendInfo = new OrderOriginExtendInfo();
            originExtendInfo.setStatus("1");
            originExtendInfo.setOrderId(orderId);
            int updateSelectiveByOrderId = orderOriginExtendInfoMapper.updateSelectiveByOrderId(originExtendInfo, shList);
            if (updateSelectiveByOrderId <= 0) {
                log.error("更新原始订单扩展表失败");


            }

        }

        for (String orderId : originStatusData) {
            //更新子订单的原始订单表的扩展数据
            OrderOriginExtendInfo originExtendInfo = new OrderOriginExtendInfo();
            originExtendInfo.setStatus("0");
            originExtendInfo.setOrderId(orderId);
            int updateSelectiveByOrderId = orderOriginExtendInfoMapper.updateSelectiveByOrderId(originExtendInfo, shList);
            if (updateSelectiveByOrderId <= 0) {
                log.error("更新原始订单扩展表失败");
            }

        }
    }

    /**
     * 父级数据进行递归判断.
     *
     * @param selectExtByParentProcessId
     * @param validStatusData
     * @param statusData
     * @throws OrderReceiveException
     */
    private void findFinalChildList(List<OrderProcessInfoExt> selectExtByParentProcessId, Set<String> validStatusData, Set<String> statusData, List<String> shList) throws OrderReceiveException {
    
        /**
         * 循环当前数据,递归深度,
         * 1.获取当前数据,拆分后的记录,查到所有拆分的数据都要归类为无效状态数据
         * 2.获取当前数据,合并的记录,查询所有拆分的数据都要归类为无效状态数据
         */
        /**
         * 根据当前数据获取拆分记录,找到所有拆分的记录,并递归获取拆分订单,遍历到最底层后,直到没有拆分数据,然后逐级判断,
         */
        /**
         * 获取当前有效状态数据的状态,如果有不可回退状态,返回错误
         */
        /**
         * 获取当前数据是否其他数据合并,如果合并,需要找到父级订单下,parent为两个不同的id,processid为一个,所以查询出来为数组,所有的数据,抛出当前数据后,修改为有效状态
         */
        for (OrderProcessInfoExt orderProcessInfoExt : selectExtByParentProcessId) {
            /**
             * 循环当前数据,递归深度,
             * 1.获取当前数据,拆分后的记录,查到所有拆分的数据都要归类为无效状态数据
             * 2.获取当前数据,合并的记录,查询所有拆分的数据都要归类为无效状态数据
             */
        
        
            /**
             * 根据当前数据获取拆分记录,找到所有拆分的记录,并递归获取拆分订单,遍历到最底层后,直到没有拆分数据,然后逐级判断,
             */
            //如果当前数据处理订单和父订单ID一致,说明是他自己,无需继续执行,并把该状态设置为无效状态数据
            if (orderProcessInfoExt.getOrderProcessInfoId().equals(orderProcessInfoExt.getParentOrderProcessId())) {
                validStatusData.add(orderProcessInfoExt.getOrderProcessInfoId());
                continue;
            }
        
            List<OrderProcessInfoExt> orderProcessInfoExts = orderProcessInfoExtMapper.selectExtByParentProcessId(orderProcessInfoExt.getOrderProcessInfoId(), shList);
        
            OrderProcessInfo selectByPrimaryKey = orderProcessInfoMapper.selectOrderProcessInfoByProcessId(orderProcessInfoExt.getOrderProcessInfoId(), shList);
        
            //如果查询处理表数据为空,说明当前数据没有在数据库中存在,直接默认是无效状态数据
            if (selectByPrimaryKey == null) {
                validStatusData.add(orderProcessInfoExt.getOrderProcessInfoId());
                continue;
            }
        
        
            /**
             * 获取当前有效状态数据的状态,如果有不可回退状态,返回错误
             */
            if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(selectByPrimaryKey.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(selectByPrimaryKey.getDdzt())) {
                throw new OrderReceiveException(
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR.getKey(),
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR.getMessage());
            } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(selectByPrimaryKey.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_6.getKey().equals(selectByPrimaryKey.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(selectByPrimaryKey.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(selectByPrimaryKey.getDdzt())
                    || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(selectByPrimaryKey.getDdzt())) {
                throw new OrderReceiveException(
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK.getKey(),
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK.getMessage());
            } else if (orderProcessInfoExts != null && orderProcessInfoExts.size() == 1 && orderProcessInfoExts.get(0).getParentOrderProcessId()
                    .equals(orderProcessInfoExts.get(0).getOrderProcessInfoId())) {
                throw new OrderReceiveException(
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE.getKey(),
                        OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE.getMessage());
            } else {
    
                if (orderProcessInfoExts == null || selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_0.getKey())) {
                    validStatusData.add(selectByPrimaryKey.getId());
                }
            }
        
            //如果扩展表查询数据为不为空,并且数组长度大于0,并且查询数据为一条数据,订单id和处理表id不相同,并且状态为无效
            boolean result = orderProcessInfoExts != null && orderProcessInfoExts.size() > 0
                    && !(orderProcessInfoExts.size() == 1 && orderProcessInfoExts.get(0).getParentOrderProcessId()
                    .equals(orderProcessInfoExts.get(0).getOrderProcessInfoId())) && selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
            if (result) {
                findFinalChildList(orderProcessInfoExts, validStatusData, statusData, shList);
        
            } else {
                //查询到扩展表数据为空时,说明这个数据为最底层状态,查看数据是否被别人合并,如果被合并,需要把被合并数据改为有效状态.
        
        
            }
        }
    }
    
    /**
     * @param originValidStatusData
     * @Title : findFianlChildList @Description
     * ：通过递归运算找到同级兄弟订单的所有最终子订单 @param @param orderId @param @return @return
     * List<OrderProcessInfo> @exception
     */
    
    private void findFianlChildList(List<OrderProcessInfoExt> selectExtByParentProcessId
            , Set<String> validStatusData
            , Set<String> originValidStatusData
            , List<String> shList
            ,Set<String> statusData
            ,Set<String> originStatusData) throws OrderReceiveException {
    
        for (OrderProcessInfoExt ext : selectExtByParentProcessId) {

            OrderProcessInfo selectByPrimaryKey = orderProcessInfoMapper
                    .selectOrderProcessInfoByProcessId(ext.getOrderProcessInfoId(), shList);
            if (OrderInfoEnum.ORDER_TYPE_2.getKey().equals(selectByPrimaryKey.getDdlx())) {
                log.info("该订单为合并后的订单需要将该订单的其他父订单状态置为有效");
                //当前订单的父订单
                String firstFather = selectExtByParentProcessId.get(0).getParentOrderProcessId();
                //查询所有父订单
                List<OrderProcessInfoExt> opiext = orderProcessInfoExtMapper.selectOrderProcessInfoExtByOrderProcessId(ext.getOrderProcessInfoId(), shList);
                //遍历 将除了当前父订单之外的其他父订单置为有效
                opiext.forEach(oe->{
                    if (!oe.getParentOrderProcessId().equals(firstFather)) {
                        statusData.add(oe.getParentOrderProcessId());
                        originStatusData.add(oe.getParentOrderInfoId());
                    }
                });
            }
            // 先查合并再查查分
            List<OrderProcessInfoExt> selectByOrderProcessId = orderProcessInfoExtMapper
                    .selectExtByParentProcessId(ext.getOrderProcessInfoId(), shList);

            if (ext.getOrderProcessInfoId().equals(ext.getParentOrderProcessId()) || selectByPrimaryKey == null) {
                continue;
            }
            // 有子订单 并且子订单的parentprocessId 和 orderProcessId不相同的为再次拆分合并后的订单
            boolean result = selectByOrderProcessId != null && selectByOrderProcessId.size() > 0
                    && !(selectByOrderProcessId.size() == 1 && selectByOrderProcessId.get(0).getParentOrderProcessId()
                    .equals(selectByOrderProcessId.get(0).getOrderProcessInfoId())) && selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey());
            if (result) {
                findFianlChildList(selectByOrderProcessId, validStatusData, originValidStatusData, shList,statusData,originStatusData);
        
            } else {
        
                if (OrderInfoEnum.ORDER_STATUS_4.getKey().equals(selectByPrimaryKey.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_9.getKey().equals(selectByPrimaryKey.getDdzt())) {
                    throw new OrderReceiveException(
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR.getKey(),
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR.getMessage());
                } else if (OrderInfoEnum.ORDER_STATUS_5.getKey().equals(selectByPrimaryKey.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_6.getKey().equals(selectByPrimaryKey.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_7.getKey().equals(selectByPrimaryKey.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_8.getKey().equals(selectByPrimaryKey.getDdzt())
                        || OrderInfoEnum.ORDER_STATUS_10.getKey().equals(selectByPrimaryKey.getDdzt())) {
                    throw new OrderReceiveException(
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK.getKey(),
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_YK.getMessage());
                } else if ((selectByOrderProcessId.size() == 1 && selectByOrderProcessId.get(0).getParentOrderProcessId()
                        .equals(selectByOrderProcessId.get(0).getOrderProcessInfoId()))) {
                    throw new OrderReceiveException(
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE.getKey(),
                            OrderInfoContentEnum.ORDER_ROLLBACK_DATA_ORDER_STATUS_ERROR_PAGE.getMessage());
                } else {
            
                    if (!selectByPrimaryKey.getOrderStatus().equals(OrderInfoEnum.ORDER_VALID_STATUS_1.getKey())) {
                        validStatusData.add(selectByPrimaryKey.getId());
                        originValidStatusData.add(selectByPrimaryKey.getOrderInfoId());
                    } else {
                        throw new OrderReceiveException(
                                OrderInfoContentEnum.RECEIVE_FAILD.getKey(),
                                "部分订单已删除不可回退!");
                    }
                    continue;
                }
    
            }
        }
    }

}
