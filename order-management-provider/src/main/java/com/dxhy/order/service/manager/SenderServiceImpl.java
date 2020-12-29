package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiSenderService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.SenderDao;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.FpExpress;
import com.dxhy.order.model.entity.SenderEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Liu_Yang
 * @date 2018/09/18
 */
@Slf4j
@Service
public class SenderServiceImpl implements ApiSenderService {
    private static final String LOGGER_MSG = "发票邮寄实现类";
    @Resource
    private SenderDao senderDao;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 寄件人列表
     *
     * @param params
     * @return
     */
    @Override
    public List<SenderEntity> senderList(Map<String, Object> params) {
        log.info("{} 查询寄件人列表", LOGGER_MSG);
        return senderDao.senderList(params);
    }
    
    /**
     * 寄件人数量
     *
     * @param params
     * @return
     */
    @Override
    public int sendersTotal(Map<String, Object> params) {
        return senderDao.sendersTotal(params);
    }
    
    /**
     * 通过Id查询寄件人信息
     *
     * @param params
     * @return
     */
    @Override
    public SenderEntity querySenderById(Map<String, Object> params) {
        return senderDao.querySenderById(params);
    }
    
    /**
     * 保存或更新寄件人
     *
     * @param senderEntity
     * @return
     */
    @Override
    public R updateSender(SenderEntity senderEntity) {
        R r = new R();
        String id = senderEntity.getId();
        if (StringUtils.isNotBlank(id)) {
            //如果id存在  走更新
            log.info("{} 更新寄件人信息", LOGGER_MSG);
            int i = senderDao.updateSender(senderEntity);
            if (i > 0) {
                r.put("msg", "修改成功").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            }else{
                r.put("msg", "修改失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }else{
    
            String senderId = apiInvoiceCommonService.getGenerateShotKey();
            senderEntity.setSenderId(senderId);
            senderEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
            log.info("{} 保存寄件人信息", LOGGER_MSG);
            int i = senderDao.insertSender(senderEntity);
            if (i > 0) {
                r.put("msg", "添加成功").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            } else {
                r.put("msg", "添加失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }
        return r;
    }
    
    /**
     * 收件人列表
     *
     * @param params
     * @return
     */
    @Override
    public List<SenderEntity> recipientsList(Map<String, Object> params) {
        log.info("{} 查询收件人列表", LOGGER_MSG);
        return senderDao.recipientsList(params);
    }
    
    /**
     * 收件人总数
     *
     * @param params
     * @return
     */
    @Override
    public int recipientsTotal(Map<String, Object> params) {
        return senderDao.recipientsTotal(params);
    }
    
    /**
     * 通过Id查询收件人信息
     *
     * @param params
     * @return
     */
    @Override
    public SenderEntity queryRecipientsById(Map<String, Object> params) {
        return senderDao.queryRecipientsById(params);
    }
    
    /**
     * 保存或更新收件人
     *
     * @param senderEntity
     * @return
     */
    @Override
    public R updateRecipients(SenderEntity senderEntity) {
        R r = new R();
        String id = senderEntity.getId();
        if (StringUtils.isNotBlank(id)) {
            //如果id存在  走更新
            log.info("更新操作,参数{}", senderEntity);
            int i = senderDao.updateRecipients(senderEntity);
            if (i > 0) {
                log.info("更新成功");
                r.put("msg", "修改成功").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            }else{
                log.info("更新失败");
                r.put("msg", "修改失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }else{
            String recipientsId = apiInvoiceCommonService.getGenerateShotKey();
            senderEntity.setRecipientsId(recipientsId);
            senderEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
            log.info("新增操作,参数{}", senderEntity);
            int i = senderDao.insertRecipients(senderEntity);
            if (i > 0) {
                log.info("新增成功");
                r.put("msg", "添加成功").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
            } else {
                r.put("msg", "添加失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }
        return r;
    }
    
    /**
     * 删除收件人
     *
     * @param params
     * @return
     */
    @Override
    public int delete(Map<String, Object> params) {
        return senderDao.delete(params);
    }
    
    @Override
    public List<SenderEntity> nameList(Map<String, Object> params) {
        String type = params.get("type") == null ? "" : params.get("type").toString();
        //类型：0 寄件 1 收件
        log.info("传入参数:type={}", type);
        if (ConfigureConstant.STRING_1.equals(type)) {
            log.info("{},查询收件人姓名列表", LOGGER_MSG);
            List<SenderEntity> list = senderDao.nameReceiveList(params);
            return list;
        } else {
            log.info("{},查询寄件人姓名列表", LOGGER_MSG);
            List<SenderEntity> list = senderDao.nameSenderList(params);
            return list;
        }
    }
	
	@Override
	public R dealSenderInfo(FpExpress record) {
		SenderEntity senderEntity = new SenderEntity();
		senderEntity.setName(record.getSenderName());
		senderEntity.setPhone(record.getSenderPhone());
		senderEntity.setType("0");
		SenderEntity queryBySenderEntity2 = senderDao.queryBySenderEntity(senderEntity);
		if(queryBySenderEntity2 == null){
            String recipientsId = apiInvoiceCommonService.getGenerateShotKey();
            senderEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
            senderEntity.setAddress(record.getSenderAddress());
            senderEntity.setPostCode(record.getSenderPostCode());
            senderEntity.setBuyerName(record.getBuyerName());
            senderEntity.setMail(record.getSenderMail());
            senderEntity.setSenderId(recipientsId);
            senderEntity.setUserId(record.getUserId());
            senderDao.insertSender(senderEntity);
        }
        senderEntity.setName(record.getRecipientsName());
        senderEntity.setPhone(record.getRecipientsPhone());
        senderEntity.setType(ConfigureConstant.STRING_1);
        SenderEntity queryBySenderEntity = senderDao.queryBySenderEntity(senderEntity);
        if (queryBySenderEntity == null) {
            String recipientsId = apiInvoiceCommonService.getGenerateShotKey();
            senderEntity.setAddress(record.getRecipientsAddress());
            senderEntity.setBuyerName(record.getBuyerName());
            senderEntity.setRecipientsId(recipientsId);
            senderEntity.setMail(record.getRecipientsMail());
            senderEntity.setUserId(record.getUserId());
            senderEntity.setPostCode(record.getRecipientsPostCode());
            senderEntity.setId(apiInvoiceCommonService.getGenerateShotKey());
            senderDao.insertRecipients(senderEntity);
        }
        return R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
    }
}
