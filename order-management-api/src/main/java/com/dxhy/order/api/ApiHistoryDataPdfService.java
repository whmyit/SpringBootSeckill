package com.dxhy.order.api;

import com.dxhy.order.model.HistoryDataPdfEntity;
import com.dxhy.order.model.OrderInvoiceInfo;

import java.util.List;

/**
 * 历史数据导入-发票pdf文件读取和存储服务
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/5/12
 */
public interface ApiHistoryDataPdfService {
    
    /**
     * 根据id查询发票pdf信息
     *
     * @param fpdm   发票代码
     * @param fphm   发票号码
     * @param shList 税号数组
     * @return HistoryDataPdfEntity
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/12
     */
    HistoryDataPdfEntity find(String fpdm, String fphm, List<String> shList);

    /**
     * 保存发票pdf到mongodb服务
     *
     * @param orderInvoiceInfo 订单与发票对应关系业务bean
     * @param pdfFile          发票PDF文件Base64加密字符串
     * @param suffix           后缀名
     * @author <a href="tivenninesongs@163.com">yaoxuguang</a>
     * @date 2020/5/12
     */
    void save(OrderInvoiceInfo orderInvoiceInfo, String pdfFile, String suffix);
}
