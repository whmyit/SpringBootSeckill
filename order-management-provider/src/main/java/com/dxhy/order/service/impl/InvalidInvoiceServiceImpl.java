package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiInvalidInvoiceService;
import com.dxhy.order.constant.NsrQueueEnum;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.InvalidInvoiceInfoMapper;
import com.dxhy.order.model.InvalidInvoiceInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 作废接口相关实现类
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class InvalidInvoiceServiceImpl implements ApiInvalidInvoiceService {
    
    @Resource
    InvalidInvoiceInfoMapper invalidInvoiceInfoMapper;
    
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;
    
    @Override
    public boolean validInvoice(InvalidInvoiceInfo invalidInvoiceInfo) {
        //插入数据库
        int insert = invalidInvoiceInfoMapper.insertValidInvoice(invalidInvoiceInfo);
        return insert > 0;
    }

    @Override
    public PageUtils queryByInvalidInvoice(Map paramMap, List<String> shList) {
        int pageSize = (Integer) paramMap.get("pageSize");
        int currPage = (Integer) paramMap.get("currPage");
        PageHelper.startPage(currPage, pageSize);
        List<Map> list = invalidInvoiceInfoMapper.selectByMap(paramMap, shList);
        PageInfo<Map> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }

    @Override
    public int updateFgInvalidInvoice(InvalidInvoiceInfo invalidInvoiceInfo, List<String> shList) {
        return invalidInvoiceInfoMapper.updateFgInvalidInvoice(invalidInvoiceInfo,shList);
    }

    @Override
    public InvalidInvoiceInfo selectByInvalidInvoiceInfo(InvalidInvoiceInfo record, List<String> shList) {
        return invalidInvoiceInfoMapper.selectByInvalidInvoiceInfo(record, shList);
    }

    @Override
    public R invalidInvoice(String content, String nsrsbh) {
        log.info("发票作废推送数据放入rabbitMq中的数据:content:{}", content);
        try {
            iRabbitMqSendMessage.autoSendRabbitMqMessage(nsrsbh, NsrQueueEnum.INVALID_MESSAGE.getValue(), content);
            return R.ok().put(OrderManagementConstant.CODE, OrderInfoContentEnum.SUCCESS.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.SUCCESS.getMessage());
        } catch (Exception e) {
            log.error("发票接收请求放入对列失败,异常信息，e:{}", e);
            return R.error().put(OrderManagementConstant.CODE, OrderInfoContentEnum.RECEIVE_FAILD.getKey())
                    .put(OrderManagementConstant.MESSAGE, OrderInfoContentEnum.RECEIVE_FAILD.getMessage());
        }
    }

	@Override
	public PageUtils queryKbInvoiceList(Map<String, Object> paramMap,List<String> xhfNsrsbh) {
        int pageSize = (Integer) paramMap.get("pageSize");
        int currPage = (Integer) paramMap.get("currPage");
        PageHelper.startPage(currPage, pageSize);
        List<InvalidInvoiceInfo> list = invalidInvoiceInfoMapper.queryKbInvoiceList(paramMap, xhfNsrsbh);
        PageInfo<InvalidInvoiceInfo> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
        
        return page;
    }
    
    /**
     * 查询待作废的数据信息
     */
    @Override
    public List<InvalidInvoiceInfo> selectInvalidInvoiceInfo(String zfpch, List<String> nsrsbh) {
        return invalidInvoiceInfoMapper.selectInvalidInvoiceInfo(zfpch, nsrsbh);
    }

}
