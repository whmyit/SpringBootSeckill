/**
 * @Title：ApiPushService.java
 * @Package：com.dxhy.order.api
 * @author : 杨士勇
 * @date ：2018年10月17日-下午6:01:48
 */

package com.dxhy.order.api;

import com.dxhy.order.model.InvoicePush;
import com.dxhy.order.model.PushInfo;
import com.dxhy.order.model.R;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：ApiPushService
 * @Description ：推送业务api类
 * @date ：2018年10月17日 下午6:01:48
 */

public interface ApiPushService {
    
    /**
     * 推送近一个月邮箱发送失败的数据
     *
     * @param shList
     */
    void pushInvoiceEmailMonthTask(List<String> shList);

    /**
     * 回推数据放入mq
     *
     * @param invoicePush
     * @param xhfNsrsbh
     * @param queueName   队列名称
     */
    void putDataInQueue(InvoicePush invoicePush, String xhfNsrsbh, String queueName);

    /**
     * 查询推送信息
     *
     * @param pushInfo
     * @return
     */
    PushInfo selectByPushInfo(PushInfo pushInfo);
    
    /**
     * 中移项目 推送待审核的数据到进项
     * @param invoicePush
     * @param pushType
     * @return
     */
    R pushCompleteOrder(InvoicePush invoicePush,String pushType);


    /**
     * 推送统一路由
     *
     * @param pushMsg
     * @return
     */
    R pushRouting(String pushMsg);
    
    /**
     * 作废推送统一路由
     *
     * @param message
     * @return
     */
    R pushInvoiceInvalidRouting(String message);
    
    /**
     * 推送地址配置列表
     *
     * @param pushInfo
     * @param shList
     * @return
     */
    List<PushInfo> queryPushInfoList(PushInfo pushInfo, List<String> shList);
    
    /**
     * 查询推送信息
     *
     * @param pushInfo
     * @return
     */
    PushInfo queryPushInfo(PushInfo pushInfo);
    
    /**
     * 更新推送信息
     *
     * @param pushInfo
     * @return
     */
    int updatePushInfo(PushInfo pushInfo);
    
    /**
     * 新增推送信息
     *
     * @param insertPushInfo
     * @return
     */
    int addPushInfo(PushInfo insertPushInfo);

    /**
     *
     * @param xxbbh 信息表编号
     * @param xhfNsrsbh 销方税号
     * @param ghfNsrsbh 购方税号
     * @param s 状态：0已撤销
     * @return
     */
    void pushHZXXBtatus(String xxbbh, String xhfNsrsbh, String ghfNsrsbh, String s);
}
