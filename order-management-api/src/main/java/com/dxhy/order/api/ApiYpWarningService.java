package com.dxhy.order.api;

import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.InvoiceWarningInfo;
import com.dxhy.order.model.ypyj.YpYjFront;

import java.util.List;

/**
 * 余票预警service
 *
 * @author yuchenguang
 * @ClassName: YpWarningService
 * @Description: 余票预警
 * @date 2018年9月12日 下午12:10:00
 */
public interface ApiYpWarningService {
    /**
     * 余票预警查询
     *
     * @param invoiceWarningInfo
     * @param shList
     * @return
     */
    List<InvoiceWarningInfo> selectYpWarning(InvoiceWarningInfo invoiceWarningInfo, List<String> shList);
    
    /**
     * 余票预警列表分页
     *
     * @param invoiceWarningInfo
     * @param pageSize
     * @param currPage
     * @param shList
     * @return
     */
    PageUtils selectPageYpWarning(InvoiceWarningInfo invoiceWarningInfo, int pageSize, int currPage, List<String> shList);
    
    /**
     * 保存
     *
     * @param ypWarningEntity
     * @return
     */
    R saveYpWarnInfo(YpYjFront ypWarningEntity);
    
    /**
     * 更新余票预警
     *
     * @param invoiceWarningInfo
     * @param shList
     * @return
     */
    int updateYpWarnInfo(InvoiceWarningInfo invoiceWarningInfo, List<String> shList);
    
}
