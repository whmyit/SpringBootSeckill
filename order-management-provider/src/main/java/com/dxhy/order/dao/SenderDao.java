package com.dxhy.order.dao;

import com.dxhy.order.model.entity.SenderEntity;

import java.util.List;
import java.util.Map;

/**
 * 寄件人列表数据层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:35
 */
public interface SenderDao {
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
     * 通过id查询寄件人(邮寄选择用回显)
     *
     * @param params
     * @return
     */
    SenderEntity querySenderById(Map<String, Object> params);
    
    /**
     * 新增寄件人
     *
     * @param senderEntity
     * @return
     */
    int insertSender(SenderEntity senderEntity);
    
    /**
     * 更新寄件人
     *
     * @param senderEntity
     * @return
     */
    int updateSender(SenderEntity senderEntity);
    
    /**
     * 删除寄/收件人
     *
     * @param params
     * @return
     */
    int delete(Map<String, Object> params);
    
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
     * 通过ID查询收件人信息（邮寄选择回显）
     *
     * @param params
     * @return
     */
    SenderEntity queryRecipientsById(Map<String, Object> params);
    
    /**
     * 新增收件人
     *
     * @param senderEntity
     * @return
     */
    int insertRecipients(SenderEntity senderEntity);
    
    /**
     * 更新收件人
     *
     * @param senderEntity
     * @return
     */
    int updateRecipients(SenderEntity senderEntity);
    
    /**
     * 查询寄件人姓名列表
     *
     * @param params
     * @return
     */
    List<SenderEntity> nameSenderList(Map<String, Object> params);
    
    /**
     * 查询收件人姓名列表
     *
     * @param params
     * @return
     */
    List<SenderEntity> nameReceiveList(Map<String, Object> params);
    
    /**
     * 根据条件查询收件人，寄件人
     *
     * @param senderEntity
     * @return
     */
    SenderEntity queryBySenderEntity(SenderEntity senderEntity);
    
}
