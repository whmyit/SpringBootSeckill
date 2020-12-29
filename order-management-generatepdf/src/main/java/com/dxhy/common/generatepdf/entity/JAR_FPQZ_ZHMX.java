package com.dxhy.common.generatepdf.entity;
/**
 * 发票签章数据
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:15
 */
public class JAR_FPQZ_ZHMX extends JAR_FPQZ_KJMX {
    private String xh;
    private int zhs;

    public String getXh() {
        return this.xh;
    }

    public void setXh(String xh) {
        this.xh = xh;
    }

    public int getZhs() {
        return this.zhs;
    }

    public void setZhs(int zhs) {
        this.zhs = zhs;
    }
}
