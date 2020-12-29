package com.dxhy.common.generatepdf.util;

/**
 * pdf字体设置
 *
 * @author ZSC-DXHY
 */

public enum FontSize {
    /**
     * 字体信息
     */
    NOtwelve(12, 0), NOnine(9, 52), NOeight(8, 60), NOseven(7, 68), NOsiX(6, 80), NOfive(5, 100), NOfour(4, 120), NOthree(3, 160), NOtwo(2, 250),
    DKtwelve(12, 0), DKnine(9, 41), DKeight(8, 49), DKseven(7, 53), DKsiX(6, 61), DKfive(5, 75), DKfour(4, 93), DKthree(3, 123), DKtwo(2, 200),
    GGXHnine(9, 8), GGXHeight(8, 8), GGXHseven(7, 10), GGXHsix(6, 12), GGXHfive(5, 15), GGXHfour(4, 20),
    DWnine(9, 6), DWeight(8, 6), DWseven(7, 8), DWsix(6, 11), DWfive(5, 13), DWfour(4, 14),
    QDnine(9, 110), QDeight(8, 120),
    QDBZnine(9, 354, 118), QDBZeight(8, 396, 132), QDBZseven(7, 456, 152), QDBZsix(6, 460, 176),
    BZnine(9, 240, 48), BZeight(8, 324, 54), BZseven(7, 372, 62), BZsix(6, 460, 72),
    XMMCnine(9, 32), XMMCeight(8, 37), XMMCseven(7, 42), XMMCsix(6, 90),
    DZDHnine(9, 50), DZDHeight(8, 59), DZDHseven(7, 68), DZDHsix(6, 100),
    SLnine(9, 5), SLseven(7, 10);
    
    private final int fontSize;
    private final int length;
    private int fontWidth;
    
    FontSize(int fontSize, int length) {
        this.fontSize = fontSize;
        this.length = length;
    }
    
    FontSize(int fontSize, int length, int fontWidth) {
        this.fontSize = fontSize;
        this.length = length;
        this.fontWidth = fontWidth;
    }
    
    public int size() {
        return this.fontSize;
    }
    
    public int length() {
        return this.length;
    }
    
    public int width() {
        return this.fontWidth;
    }
}
