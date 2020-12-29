package com.dxhy.order.consumer.protocol.fiscal;

import com.dxhy.invoice.protocol.sl.cpy.DownloadCpyKcResMxs;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-06-19 18:19:17
 * @Describe
 */
@Setter
@Getter
public class DOWNLOAD_CPYKC_REQ {
    private String fjh;
    private String xhfNsrsbh;
    private List<DownloadCpyKcResMxs> mxs;
}
