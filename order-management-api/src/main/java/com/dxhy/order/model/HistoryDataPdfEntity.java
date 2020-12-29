package com.dxhy.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 已开发票历史数据导入-发票pdf文件存储对象
 * @author: <a href="tivenninesongs@163.com">yaoxuguang</a>
 * @createDate: Created in 2020/5/12
 */
@Getter
@Setter
@ToString
public class HistoryDataPdfEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 开票日期
     */
    private String kprq;
    /**
     * 发票代码
     */
    private String fpdm;
    /**
     * 发票号码
     */
    private String fphm;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * pdf文件（BASE64加密字符串）
     */
    private String pdfFileData;
    
    /**
     * 文件后缀名
     */
    private String suffix;
}
