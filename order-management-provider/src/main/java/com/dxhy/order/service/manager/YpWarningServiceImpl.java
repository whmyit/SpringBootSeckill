package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.api.ApiYpWarningService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.dao.InvoiceWarningInfoMapper;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.InvoiceWarningInfo;
import com.dxhy.order.model.ypyj.Fpzldm;
import com.dxhy.order.model.ypyj.YpYjFront;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author yuchenguang
 * @ClassName: YpWarningServiceImpl
 * @Description: 余票预警impl
 * @date 2018年9月12日 下午12:12:46
 */
@Slf4j
@Service
public class YpWarningServiceImpl implements ApiYpWarningService {
    
    private final static String LOGGER_MSG = "余票预警实现类";
    
    @Resource
    private InvoiceWarningInfoMapper invoiceWarningInfoMapper;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    
    /**
     * 余票预警查询
     *
     * @param invoiceWarningInfo
     * @param invoiceWarningInfo
     * @return
     */
    @Override
    public List<InvoiceWarningInfo> selectYpWarning(InvoiceWarningInfo invoiceWarningInfo, List<String> shList) {
        log.info("{}余票预警列表查询 参数 {}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(invoiceWarningInfo));
        return invoiceWarningInfoMapper.selectYpWarning(invoiceWarningInfo, shList);
    }
    
    @Override
    public PageUtils selectPageYpWarning(InvoiceWarningInfo invoiceWarningInfo, int pageSize, int currPage, List<String> shList) {
        log.info("查询纸电票传入参数为{}", JsonUtils.getInstance().toJsonString(invoiceWarningInfo));
        //这里前端从1开始需要进行-1操作
        PageHelper.startPage(currPage, pageSize);
        List<InvoiceWarningInfo> list = invoiceWarningInfoMapper.selectYpWarning(invoiceWarningInfo, shList);
        PageInfo<InvoiceWarningInfo> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }
    
    /**
     * 保存
     *
     * @param ypYjFront
     * @return
     */
    @Override
    public R saveYpWarnInfo(YpYjFront ypYjFront) {
        if (ypYjFront != null && ypYjFront.getFpzldms() != null && ypYjFront.getFpzldms().size() > 0) {
            for (Fpzldm fpzldm : ypYjFront.getFpzldms()) {
                List<String> shList = NsrsbhUtils.transShListByNsrsbh(ypYjFront.getXhfNsrsbh());
                InvoiceWarningInfo invoiceWarningInfo = new InvoiceWarningInfo();
                invoiceWarningInfo.setId(fpzldm.getId());
                invoiceWarningInfo.setXhfNsrsbh(ypYjFront.getXhfNsrsbh());
                invoiceWarningInfo.setXhfMc(ypYjFront.getXhfMc());
                invoiceWarningInfo.setSbbh(fpzldm.getSbbh());
                invoiceWarningInfo.setSbMc(fpzldm.getSbMc());
                invoiceWarningInfo.setFpzlDm(fpzldm.getFpzldm());
                invoiceWarningInfo.setYjfs(fpzldm.getYjfs());
                invoiceWarningInfo.setEMail(ypYjFront.getUserEmail());
                invoiceWarningInfo.setSfyj(ypYjFront.getSfyj());
                invoiceWarningInfo.setPhone(ypYjFront.getPhone());
                invoiceWarningInfo.setCreateTime(new Date());
                invoiceWarningInfo.setUpdateTime(new Date());
                invoiceWarningInfo.setUserId(ypYjFront.getUserId());
                invoiceWarningInfo.setYjcs(ConfigureConstant.STRING_0);
                invoiceWarningInfo.setXhfNsrsbh(ypYjFront.getXhfNsrsbh());
                invoiceWarningInfo.setDeptId(ypYjFront.getDeptId());
                List<InvoiceWarningInfo> invoiceWarningInfos = invoiceWarningInfoMapper.selectYpWarning(invoiceWarningInfo, shList);
                if (invoiceWarningInfos != null && invoiceWarningInfos.size() > 0) {
                    invoiceWarningInfo.setId(invoiceWarningInfos.get(0).getId());
                    int i = invoiceWarningInfoMapper.updateYpWarnInfo(invoiceWarningInfo, shList);
                    log.info("{}保存余票预警信息 更新结果 {}", LOGGER_MSG, i);
                } else {
                    invoiceWarningInfo.setId(apiInvoiceCommonService.getGenerateShotKey());
                    invoiceWarningInfo.setDeleteStatus(ConfigureConstant.STRING_0);
                    int i = invoiceWarningInfoMapper.insertInvoiceWarning(invoiceWarningInfo);
                    log.info("{}保存余票预警信息 更新结果 {}", LOGGER_MSG, i);
                }
                
            }
        }
        
        return R.ok();
    }
    
    @Override
    public int updateYpWarnInfo(InvoiceWarningInfo invoiceWarningInfo, List<String> shList) {
        return invoiceWarningInfoMapper.updateYpWarnInfo(invoiceWarningInfo, shList);
    }
    
}
