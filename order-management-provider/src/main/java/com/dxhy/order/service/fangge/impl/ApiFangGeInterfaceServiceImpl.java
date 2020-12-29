package com.dxhy.order.service.fangge.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.dxhy.order.api.ApiFangGeInterfaceService;
import com.dxhy.order.api.ApiRegistrationCodeService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.config.MqttPushClient;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.dao.InvoicePrintInfoMapper;
import com.dxhy.order.dao.SpecialInvoiceReversalDao;
import com.dxhy.order.model.InvoicePrintInfo;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.model.RegistrationCode;
import com.dxhy.order.model.dto.FgInvoicePrintDto;
import com.dxhy.order.model.dto.FgSendMsgFlag;
import com.dxhy.order.model.dto.PushPayload;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.protocol.fangge.FG_COMMON_INVOICE_INFO;
import com.dxhy.order.service.impl.InvoiceDataServiceImpl;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 方格业务实现类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-04 10:33
 */
@Slf4j
@Service
public class ApiFangGeInterfaceServiceImpl implements ApiFangGeInterfaceService {
    @Resource
    private SpecialInvoiceReversalDao specialInvoiceReversalDao;
    @Resource
    InvoicePrintInfoMapper invoicePrintInfoMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private MqttPushClient mqttPushClient;
    @Resource
    private InvoiceDataServiceImpl invoiceDataServiceImpl;
    
    @Reference
    private ApiRegistrationCodeService apiRegistrationCodeService;
    
    @Override
    public void updateUploadRedInvoice(SpecialInvoiceReversalEntity entity) {
        specialInvoiceReversalDao.updateByPrimaryKeySelective(entity);
    }
    
    /**
     * 查询待打印的数据
     */
    @Override
    public FgInvoicePrintDto getPrintInvoices(String dypch, String nsrsbh) {
        return invoicePrintInfoMapper.getPrintInvoices(dypch, nsrsbh);
    }
    
    /**
     * 待打印修改状态
     */
    @Override
    public void updatePrintInvoicesStatus(String dypch, String nsrsbh, String sjzt) {
        invoicePrintInfoMapper.updatePrintInvoicesStatus(dypch, nsrsbh, sjzt);
    }
    
    /**
     * 打印完成修改数据库状态
     */
    @Override
    public void updateFgPrintInvoice(InvoicePrintInfo invoicePrintInfo, List<String> shList) {
        invoicePrintInfoMapper.updateFgPrintInvoice(invoicePrintInfo, shList);
    }
    
    @Override
    public String getRegistCodeByRedis(String nsrsbh, String jqbh) {
        /**
         * 需要新增逻辑,如果查询redis缓存为空,需要调用数据库更新到redis缓存
         */
        String registCodeStr = "";
        if (StringUtils.isNotBlank(jqbh)) {
            registCodeStr = redisService.get(String.format(Constant.REDIS_FG_TAX_DISK_INFO, nsrsbh + "_" + jqbh));
        } else {
            Set<String> keys = redisService.keys(String.format(Constant.REDIS_FG_TAX_DISK_INFO, nsrsbh + "*"));
            List<String> registerList = new ArrayList<>(keys);
            if (ObjectUtil.isNotEmpty(registerList)) {
                int i = (int) (RandomUtil.randomDouble() * registerList.size());
                registCodeStr = redisService.get(registerList.get(i));
            }
        
        }
        if (StringUtils.isBlank(registCodeStr)) {
        
            RegistrationCode registrationCodeByNsrsbhAndJqbh = apiRegistrationCodeService.getRegistrationCodeByNsrsbhAndJqbh(nsrsbh, jqbh);
            registrationCodeByNsrsbhAndJqbh.setSpzt(ConfigureConstant.STRING_0);
            saveCodeToRedis(registrationCodeByNsrsbhAndJqbh);
            registCodeStr = JsonUtils.getInstance().toJsonString(registrationCodeByNsrsbhAndJqbh);
        }
    
        return registCodeStr;
    }
    
    @Override
    public List<String> getRegistCodeListByRedis(String nsrsbh) {
        List<String> resultList = new ArrayList<>();
        Set<String> keys = redisService.keys(String.format(Constant.REDIS_FG_TAX_DISK_INFO, nsrsbh + "*"));
        if (ObjectUtil.isNotEmpty(keys)) {
            for (String key : keys) {
                resultList.add(redisService.get(key));
            }
        }
        return resultList;
    }
    
    @Override
    public void saveCodeToRedis(RegistrationCode registrationCode) {
        redisService.set(String.format(Constant.REDIS_FG_TAX_DISK_INFO, registrationCode.getXhfNsrsbh() + "_" + registrationCode.getJqbh()), registrationCode, Constant.REDIS_EXPIRE_TIME_DEFAULT);
    }
    
    @Override
    public void saveMqttToRedis(PushPayload pushPayload) {
        log.info("========>redis存放消息开始");
        PushPayload payload = new PushPayload();
        payload.setINTERFACETYPE(pushPayload.getINTERFACETYPE() == null ? "" : pushPayload.getINTERFACETYPE());
        payload.setNSRSBH(pushPayload.getNSRSBH() == null ? "" : pushPayload.getNSRSBH());
        payload.setZCM(pushPayload.getZCM() == null ? "" : pushPayload.getZCM());
        payload.setJQBH(pushPayload.getJQBH() == null ? "" : pushPayload.getJQBH());
        payload.setDDQQLSH(pushPayload.getDDQQLSH() == null ? "" : pushPayload.getDDQQLSH());
        payload.setSQBSCQQPCH(pushPayload.getSQBSCQQPCH() == null ? "" : pushPayload.getSQBSCQQPCH());
        payload.setSQBXZQQPCH(pushPayload.getSQBXZQQPCH() == null ? "" : pushPayload.getSQBXZQQPCH());
        payload.setZFPCH(pushPayload.getZFPCH() == null ? "" : pushPayload.getZFPCH());
        payload.setDYPCH(pushPayload.getDYPCH() == null ? "" : pushPayload.getDYPCH());
        String key = String.format(Constant.REDIS_FG_MQTT_MSG, pushPayload.getNSRSBH(), pushPayload.getJQBH());
        String value = JsonUtils.getInstance().toJsonString(payload);
        //判断缓存中当前存放的流水号是否存在
        if (ConfigureConstant.STRING_1.equals(payload.getINTERFACETYPE())) {
            List<String> list = redisService.lrange(key);
            log.info("========>获取redis里面所有的消息，key:{},allValue:{}", key, JsonUtils.getInstance().toJsonString(list));
            if (list != null && !CollectionUtils.isEmpty(list)) {
                boolean flag = true;
                for (String v : list) {
                    PushPayload redisPushPayload = JsonUtils.getInstance().parseObject(v, PushPayload.class);
                    //存在
                    if (ConfigureConstant.STRING_1.equals(redisPushPayload.getINTERFACETYPE()) && pushPayload.getDDQQLSH().equals(redisPushPayload.getDDQQLSH())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    redisService.lPush(key, value);
                    log.info("========>redis存放消息成功，key:[{}],value:[{}]", key, value);
                } else {
                    log.info("========>redis存放消息失败，发票请求流水号已经存在，key:[{}],value:[{}]", key, value);
                }
            } else {
                redisService.lPush(key, value);
                log.info("========>redis存放消息成功，key:[{}],value:[{}]", key, value);
            }
        } else {
            redisService.lPush(key, value);
            log.info("========>redis存放消息成功，key:[{}],value:[{}]", key, value);
        }
        
    }
    
    /**
     * 消费redis队列消息
     * 并且发送到mqtt
     */
    @Override
    public boolean pushMqttMsg(String nsrsbh, String jqbh) {
        log.info("========>开始消费redis消息,税号为：{},机器编号为：{}", nsrsbh, jqbh);
    
        //发布消息topic
        String mqttTopic = String.format(Constant.FG_MQTT_TOPIC_PUB_FANGGE, nsrsbh, jqbh);
    
        FgSendMsgFlag fgSendMsgFlag = new FgSendMsgFlag();
        boolean flag = mqttPushClient.getSubscriptions(mqttTopic);
        //在线
        if (flag) {
            //获取是否可消费消息标识
            String flagRedisKey = String.format(Constant.REDIS_FG_MQTT_MSG_FLAG, nsrsbh, jqbh);
            String msgFlag = redisService.get(flagRedisKey);
        
            //消息队列rediskey
            String msgRedisKey = String.format(Constant.REDIS_FG_MQTT_MSG, nsrsbh, jqbh);
            //标识为空,第一次,可以消费数据
            if (StringUtils.isEmpty(msgFlag)) {
                String msg = redisService.rPop(msgRedisKey);
                if (StringUtils.isEmpty(msg)) {
                    //置状态为可取（0为可取）
                    fgSendMsgFlag.setIsSendFlag(ConfigureConstant.STRING_0);
                    fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
                    redisService.set(flagRedisKey, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                } else {
                    //发送消息
                    int reasonCode = mqttPushClient.publish(mqttTopic, msg);
                    if (Integer.valueOf(ConfigureConstant.STRING_0) == reasonCode) {
                        log.info("测试开票时间-发送指令：{},指令内容：{}", DateUtil.now(), msg);
                        log.info("========>发送mqtt消息完成，topic为：{},消息内容：{}", mqttTopic, msg);
                        //置状态为不可取（1为不可取）
                        fgSendMsgFlag.setIsSendFlag(ConfigureConstant.STRING_1);
                        fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
                        redisService.set(flagRedisKey, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                    } else {
                        log.error("========>发送mqtt消息失败,请检查客户机是否连接或者clientid是否重复");
                        log.info("========>消息重新放入缓存中,key:[{}],msg:[{}]", msgRedisKey, msg);
                        redisService.rPush(msgRedisKey, msg);
                    }
                }
            } else {
                fgSendMsgFlag = JsonUtils.getInstance().parseObject(msgFlag, FgSendMsgFlag.class);
                log.info("========>mqtt是否可发送税号为[{}]的消息：{}", nsrsbh, "0".equals(fgSendMsgFlag.getIsSendFlag()) ? "是" : "否");
                //可取
                if (ConfigureConstant.STRING_0.equals(fgSendMsgFlag.getIsSendFlag())) {
                    //消费数据
                    String msg = redisService.rPop(msgRedisKey);
                    if (!StringUtils.isEmpty(msg)) {
                        int reasonCode = mqttPushClient.publish(mqttTopic, msg);
                        if (ConfigureConstant.INT_0 == reasonCode) {
                            log.info("测试开票时间-发送指令：{},指令内容：{}", DateUtil.now(), msg);
                            log.info("========>发送mqtt消息完成,topic为:[{}],消息内容:[{}]", mqttTopic, msg);
                            //置状态为不可取
                            fgSendMsgFlag.setIsSendFlag(ConfigureConstant.STRING_1);
                            fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
                            redisService.set(flagRedisKey, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        } else {
                            log.error("========>发送mqtt消息失败,请检查客户机是否连接或者clientid是否重复");
                            log.info("========>消息重新放入缓存中，key:[{}],msg[{}]", msgRedisKey, msg);
                            redisService.rPush(msgRedisKey, msg);
                        }
                    }
                }
            }
        } else {//离线
            log.info("没有客户端订阅该topic:{}", mqttTopic);
            /**
             * 如果未订阅调用同步接口,更新金税盘状态为离线
             */
            return false;
        }
        return true;
    }
    
    
    /**
     * 更新redis里的状态为可发送
     * 逻辑描述：加可发送状态标识为了应对redis队列假如没有数据，不知道何时消费数据，通过标识和线程定时调用来实现
     */
    @Override
    public void updateMsgFlag(String nsrsbh, String jqbh) {
        //获取是否可消费消息标识
        String flagRedisKey = String.format(Constant.REDIS_FG_MQTT_MSG_FLAG, nsrsbh, jqbh);
        log.info("更新mqtt消息为可发送状态，redis key为[{}]", flagRedisKey);
        String s = redisService.get(flagRedisKey);
        if (StringUtils.isNotEmpty(s)) {
            FgSendMsgFlag fgSendMsgFlag = new FgSendMsgFlag();
            fgSendMsgFlag.setIsSendFlag(ConfigureConstant.STRING_0);
            fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
            //置状态为可取（0为可发送）
            redisService.set(flagRedisKey, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
        }
    }
    
    @Override
    public void saveInvoicePrintInfo(InvoicePrintInfo printInfo) {
        int insert = invoicePrintInfoMapper.insertSelective(printInfo);
    }
    
    @Override
    public List<InvoicePrintInfo> getPrintInvoicesList(String dypch, String nsrsbh) {
        return invoicePrintInfoMapper.getPrintInvoicesList(dypch, nsrsbh);
    }
    
    /**
     * 发票冲红修改冲红金额
     *
     * @param info
     * @param orderInvoiceInfo
     */
    @Override
    public void dealRedInvoice(FG_COMMON_INVOICE_INFO info, OrderInvoiceInfo orderInvoiceInfo) {
        try {
            orderInvoiceInfo.setFpdm(info.getFP_DM());
            orderInvoiceInfo.setFphm(info.getFP_HM());
            orderInvoiceInfo.setFwm(info.getFWM());
            orderInvoiceInfo.setEwm(info.getEWM());
            orderInvoiceInfo.setJqbh(info.getJQBH());
            orderInvoiceInfo.setJym(info.getJYM());
            orderInvoiceInfo.setKprq(DateUtilsLocal.getMissDataFormat(info.getKPRQ()));
            info.setHJBHSJE(orderInvoiceInfo.getHjbhsje());
            //如果是红票的话 更新原蓝票的冲红标志和剩余可冲红金额
            invoiceDataServiceImpl.dealRedInvoice(orderInvoiceInfo, orderInvoiceInfo.getKpzt(), NsrsbhUtils.transShListByNsrsbh(orderInvoiceInfo.getXhfNsrsbh()));
        } catch (Exception e) {
            log.info("修改金额错误");
        }
    }
    
    @Override
    public void handlerIsSendFlag() {
        //模糊查询状态所有的key
        Set<String> set = redisService.keys("*" + Constant.FG_MQTT_MSG_FLAG + "*");
        if (CollectionUtils.isEmpty(set)) {
            log.info("========》没有要更新的发送状态");
        } else {
            FgSendMsgFlag fgSendMsgFlag;
            for (String key : set) {
                String flagMsg = redisService.get(key);
                if ("0".equals(flagMsg) || "1".equals(flagMsg)) {
                    //重新放入，以前是状态值是0或者1，现在修改为json格式
                    fgSendMsgFlag = new FgSendMsgFlag();
                    fgSendMsgFlag.setIsSendFlag(flagMsg);
                    fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
                    redisService.set(key, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                } else {
                    fgSendMsgFlag = JsonUtils.getInstance().parseObject(flagMsg, FgSendMsgFlag.class);
                    Date lastUpdateDate = DateUtil.parse(fgSendMsgFlag.getLastUpdateTime(), "yyyy-MM-dd HH:mm:ss");
                    //更新时间与当前时间间隔超过设定值
                    if (lastUpdateDate.getTime() < DateTime.now().getTime()) {
                        //更新状态
                        if (ConfigureConstant.STRING_1.equals(fgSendMsgFlag.getIsSendFlag())) {
                            //置状态为可取（0为可发送）
                            fgSendMsgFlag.setIsSendFlag(ConfigureConstant.STRING_0);
                            fgSendMsgFlag.setLastUpdateTime(DateUtil.now());
                        }
                        redisService.set(key, JsonUtils.getInstance().toJsonString(fgSendMsgFlag), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                    }
                }
            }
        }
    }
    
    
}
