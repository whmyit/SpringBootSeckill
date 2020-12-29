package com.dxhy.order.consumer.modules.itaxlog.service.impl;

import com.dxhy.itax.dto.SysLogDto;
import com.dxhy.itax.enums.OptResultEnum;
import com.dxhy.itax.enums.SystemSignEnum;
import com.dxhy.itax.logutil.LogProcessUtil;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.consumer.model.SysLogEntity;
import com.dxhy.order.consumer.modules.itaxlog.service.IItaxlogService;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 大B日志集成服务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/7/19 17:52
 */
@Service
@Slf4j
public class ItaxlogServiceImpl implements IItaxlogService {
    private final static String LOGGER_MSG = "(日志推送大B)";
    @Resource
    private LogProcessUtil logProcessUtil;
    
    @Override
    public void saveItaxLog(SysLogEntity sysLogEntity) {
        log.debug("{}请求数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(sysLogEntity));
        logProcessUtil.pushMsg(transitionSysLog(sysLogEntity));
        
    }
    
    private static SysLogDto transitionSysLog(SysLogEntity sysLogEntity) {
        SysLogDto sysLogDto = new SysLogDto();
        /**
         * 请求 IP
         */
        sysLogDto.setIp(sysLogEntity.getIp());
        /**
         * 操作结果说明，例如：
         * 销项-冲红了一张电子
         * 发票，发票代码为 123，
         * 发票号码为 456；进项- 手工认证了一张发票，
         * 发票代码为 123，发票号
         * 码为 456；计缴申报管理
         * -新增了一条取数规则，
         * 名称为 123
         */
        if (StringUtils.isNotBlank(sysLogEntity.getParams()) && "null".equals(sysLogEntity.getParams())) {
            sysLogEntity.setParams("");
        } else if (StringUtils.isBlank(sysLogEntity.getParams())) {
            sysLogEntity.setParams("");
        }
        String content = new StringBuffer(sysLogEntity.getOperation()).append(ConfigureConstant.STRING_LINE).append(sysLogEntity.getOperationDesc()).append(ConfigureConstant.STRING_LINE).append(sysLogEntity.getParams()).toString();
        if (ConfigureConstant.INT_150 <= content.length()) {
            content = content.substring(ConfigureConstant.INT_0, ConfigureConstant.INT_150);
        }
        sysLogDto.setContent(content);
        /**
         * 关键字
         */
        sysLogDto.setKeywords(sysLogEntity.getKey());
        /**
         * 系统名称可使用依赖包里
         * SystemSignEnum 枚举类
         * 获取模块标识,根据自
         * 身模块定义
         */
        sysLogDto.setModule(SystemSignEnum.XXGL.code());
        
        /**
         * 操作主体公司 ID
         * todo 暂时使用用户id
         */
        sysLogDto.setOptDept(Long.parseLong(sysLogEntity.getUserId()));
        /**
         * 操作结果
         */
        sysLogDto.setOptResult(sysLogEntity.isResult() ? OptResultEnum.CG.code() : OptResultEnum.SB.code());
        /**
         * 操作时间 YYYY-mm-dd hh:mm:ss
         */
        sysLogDto.setOptTime(sysLogEntity.getCreateDate());
        /**
         * 功能名称
         */
        sysLogDto.setOptType(sysLogEntity.getOperation());
        /**
         * 操作人ID
         */
        sysLogDto.setOptUserId(sysLogEntity.getUserId());
        
        return sysLogDto;
    }
}
