package com.dxhy.order.service.impl;

import com.dxhy.order.api.ApiHistoryDataPdfService;
import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.MongodbService;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.model.HistoryDataPdfEntity;
import com.dxhy.order.model.OrderInvoiceInfo;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.JsonUtils;
import com.mongodb.BasicDBObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 历史数据导入-发票pdf文件读取和存储服务
 *
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/5/12
 */
@Service
@Slf4j
public class HistoryDataPdfServiceImpl implements ApiHistoryDataPdfService {
    @Resource
    private MongodbService mongodbService;
    @Resource
    private ApiOrderInvoiceInfoService orderInvoiceInfoService;
    
    @Override
    public HistoryDataPdfEntity find(String fpdm, String fphm, List<String> shList) {
        //根据代码和号码从订单和发票关系表中查询mongodb_id
        OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoService.findMongoDbIdByFpdmAndFphm(fpdm, fphm, shList);
        log.info("订单和发票关系表中查询到的数据:{}", JsonUtils.getInstance().toJsonString(orderInvoiceInfo));
        //根据mongodb_id获取发票pdf文件存储信息
        if (orderInvoiceInfo != null && StringUtils.isNotBlank(orderInvoiceInfo.getMongodbId())) {
            HistoryDataPdfEntity historyDataPdfEntity = mongodbService.find(orderInvoiceInfo.getMongodbId(),
                    HistoryDataPdfEntity.class, OpenApiConfig.MONGODB_COLLECTION_NAME);
            log.info("从mongodb服务中获取的发票pdf文件存储信息:{}", historyDataPdfEntity);
            if (Objects.nonNull(historyDataPdfEntity)) {
                return historyDataPdfEntity;
            }
        }
        return null;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(OrderInvoiceInfo orderInvoiceInfo, String pdfFile, String suffix) {
        String mongodbId = "";
        
        //判断pdf流是否存在
        if (StringUtils.isNotBlank(pdfFile)) {
            BasicDBObject data = new BasicDBObject();
            data.put("kprq", DateUtilsLocal.getYMDHMIS(orderInvoiceInfo.getKprq()));
            data.put("fpdm", orderInvoiceInfo.getFpdm());
            data.put("fphm", orderInvoiceInfo.getFphm());
            data.put("suffix", suffix);
            data.put("fileName", orderInvoiceInfo.getFpdm() + orderInvoiceInfo.getFphm() + suffix);
            data.put("pdfFileData", pdfFile);
            data = mongodbService.save(data, OpenApiConfig.MONGODB_COLLECTION_NAME);
            mongodbId = String.valueOf(data.get("_id"));
            log.info("mongodb id :" + mongodbId);
            if (StringUtils.isNotBlank(mongodbId)) {
                List<String> shList = new ArrayList<>();
                shList.add(orderInvoiceInfo.getXhfNsrsbh());
                orderInvoiceInfoService.updateMongoDbIdByFpdmAndFphm(orderInvoiceInfo.getFpqqlsh(), mongodbId, shList);
            }
        }
    }
}
