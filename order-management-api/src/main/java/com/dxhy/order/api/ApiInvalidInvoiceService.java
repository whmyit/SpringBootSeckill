package com.dxhy.order.api;

import com.dxhy.order.model.InvalidInvoiceInfo;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;

import java.util.List;
import java.util.Map;

/**
 * 作废相关接口
 *
 * @author ZSC-DXHY
 */
public interface ApiInvalidInvoiceService {
    
    /**
     * 空白发票的作废接口
     *
     * @param paramMap
     * @param shList
     * @return
     */
    PageUtils queryByInvalidInvoice(Map paramMap, List<String> shList);
    
    /**
     * 更新方格作废信息
     *
     * @param invalidInvoiceInfo
     * @param shList
     * @return
     */
    int updateFgInvalidInvoice(InvalidInvoiceInfo invalidInvoiceInfo, List<String> shList);
    
    /**
     * 作废发票
     *
     * @param invalidInvoiceInfo
     * @return
     */
    boolean validInvoice(InvalidInvoiceInfo invalidInvoiceInfo);
    
    /**
     * 根据条件查询作废表
     *
     * @param record
     * @param shList
     * @return InvalidInvoiceInfo
     */
    InvalidInvoiceInfo selectByInvalidInvoiceInfo(InvalidInvoiceInfo record, List<String> shList);
    
    /**
     * 作废数据存放队列
     *
     * @param jsonString
     * @param nsrsbh
     * @return
     */
    R invalidInvoice(String jsonString, String nsrsbh);
    
    /**
     * 查询空白发票作废列表
     *
     * @param paramMap
     * @param xhfNsrsbh
     * @return
     */
    PageUtils queryKbInvoiceList(Map<String, Object> paramMap, List<String> xhfNsrsbh);
    
    /**
     * 查询待作废的发票信息
     *
     * @param zfpch
     * @param nsrsbh
     * @return
     */
    List<InvalidInvoiceInfo> selectInvalidInvoiceInfo(String zfpch, List<String> nsrsbh);
    
}
