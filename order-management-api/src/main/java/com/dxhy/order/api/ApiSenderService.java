package com.dxhy.order.api;

import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.FpExpress;
import com.dxhy.order.model.entity.SenderEntity;

import java.util.List;
import java.util.Map;

/**
 * 寄件人业务接口
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:37
 */
public interface ApiSenderService {
    /**
     * 查询寄件人列表
     *
     * @param params
     * @return
     */
    List<SenderEntity> senderList(Map<String, Object> params);
    
    /**
     * 查询寄件人总数
     *
     * @param params
     * @return
     */
    int sendersTotal(Map<String, Object> params);
    
    /**
     * 通过ID查询寄件人信息
     *
     * @param params
     * @return
     */
    SenderEntity querySenderById(Map<String, Object> params);
    
    /**
     * 新增/更新寄件人
     *
     * @param senderEntity
     * @return
     */
    R updateSender(SenderEntity senderEntity);
    
    /**
     * 查询收件人列表
     *
     * @param params
     * @return
     */
    List<SenderEntity> recipientsList(Map<String, Object> params);
    
    /**
     * 查询收件人总数
     *
     * @param params
     * @return
     */
    int recipientsTotal(Map<String, Object> params);
    
    /**
     * 通过ID查询收件人信息
     *
     * @param params
     * @return
     */
    SenderEntity queryRecipientsById(Map<String, Object> params);
    
    /**
     * 新增/更新收件人
     *
     * @param senderEntity
     * @return
     */
    R updateRecipients(SenderEntity senderEntity);
    
    /**
     * 删除寄/收件人
     *
     * @param params
     * @return
     */
    int delete(Map<String, Object> params);
    
    /**
     * 查询寄/收件人姓名列表
     *
     * @param params
     * @return
     */
    List<SenderEntity> nameList(Map<String, Object> params);
    
    /**
     * 处理收件人 寄件人信息 没有新增
     *
     * @param record
     * @return
     */
    R dealSenderInfo(FpExpress record);
}
