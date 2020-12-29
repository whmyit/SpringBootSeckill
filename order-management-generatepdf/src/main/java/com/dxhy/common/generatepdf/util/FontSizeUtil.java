package com.dxhy.common.generatepdf.util;

import com.dxhy.common.generatepdf.exception.CustomException;
/**
 * 字体工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:51
 */
public class FontSizeUtil {
    public static int getFontSize(String org)
            throws CustomException {
        FontSize font = null;
        double orgLength = 0.0D;
        try {
            orgLength = StringUtil.getLength(org);
        } catch (Exception e) {
            throw new CustomException(1005, "获取字体大小失败(" + org + ")", e);
        }
        font = compare(orgLength, org);
        return font.size();
    }

    public static int getFontSizeDK(String org) throws CustomException {
        FontSize font = null;
        double orgLength = 0.0D;
        try {
            orgLength = StringUtil.getLength(org);
        } catch (Exception e) {
            throw new CustomException(1005, "获取字体大小失败(" + org + ")", e);
        }
        font = dkcompare(orgLength, org);
        return font.size();
    }

    public static FontSize dkcompare(double length, String org) throws CustomException {
        if (length <= FontSize.DKnine.length()) {
            return FontSize.DKnine;
        }
        if ((length > FontSize.DKnine.length()) && (length <= FontSize.DKeight.length())) {
            return FontSize.DKeight;
        }
        if ((length > FontSize.DKeight.length()) && (length <= FontSize.DKseven.length())) {
            return FontSize.DKseven;
        }
        if ((length > FontSize.DKseven.length()) && (length <= FontSize.DKsiX.length())) {
            return FontSize.DKsiX;
        }
        if ((length > FontSize.DKsiX.length()) && (length <= FontSize.DKfive.length())) {
            return FontSize.DKfive;
        }
        if ((length > FontSize.DKfive.length()) && (length <= FontSize.DKfour.length())) {
            return FontSize.DKfour;
        }
        if ((length > FontSize.DKfour.length()) && (length <= FontSize.DKthree.length())) {
            return FontSize.DKthree;
        }
        if ((length > FontSize.DKthree.length()) && (length <= FontSize.DKtwo.length())) {
            return FontSize.DKtwo;
        }
        throw new CustomException(1006, "文字长度过长，获取字体大小失败(" + length + "：" + org + ")");
    }

    public static FontSize compare(double length, String org) throws CustomException {
        if (length <= FontSize.NOnine.length()) {
            return FontSize.NOnine;
        }
        if ((length > FontSize.NOnine.length()) && (length <= FontSize.NOeight.length())) {
            return FontSize.NOeight;
        }
        if ((length > FontSize.NOeight.length()) && (length <= FontSize.NOseven.length())) {
            return FontSize.NOseven;
        }
        if ((length > FontSize.NOseven.length()) && (length <= FontSize.NOsiX.length())) {
            return FontSize.NOsiX;
        }
        if ((length > FontSize.NOsiX.length()) && (length <= FontSize.NOfive.length())) {
            return FontSize.NOfive;
        }
        if ((length > FontSize.NOfive.length()) && (length <= FontSize.NOfour.length())) {
            return FontSize.NOfour;
        }
        if ((length > FontSize.NOfour.length()) && (length <= FontSize.NOthree.length())) {
            return FontSize.NOthree;
        }
        if ((length > FontSize.NOthree.length()) && (length <= FontSize.NOtwo.length())) {
            return FontSize.NOtwo;
        }
        throw new CustomException(1006, "文字长度过长，获取字体大小失败(" + length + "：" + org + ")");
    }

    public static FontSize getFontSizeForDZDH(double length, String org) throws CustomException {
        if (length <= FontSize.DZDHnine.length()) {
            return FontSize.DZDHnine;
        }
        if ((length > FontSize.DZDHnine.length()) && (length <= FontSize.DZDHeight.length())) {
            return FontSize.DZDHeight;
        }
        if ((length > FontSize.DZDHeight.length()) && (length <= FontSize.DZDHseven.length())) {
            return FontSize.DZDHseven;
        }
        if ((length > FontSize.DZDHseven.length()) && (length <= FontSize.DZDHsix.length())) {
            return FontSize.DZDHsix;
        }
        throw new CustomException(1006, "文字长度过长，获取字体大小失败(" + length + "：" + org + ")");
    }

    public static FontSize getFontSizeForXMMC(double length, String org) throws CustomException {
        if (length <= FontSize.XMMCnine.length()) {
            return FontSize.XMMCnine;
        }
        if ((length > FontSize.XMMCnine.length()) && (length <= FontSize.XMMCeight.length())) {
            return FontSize.XMMCeight;
        }
        if ((length > FontSize.XMMCeight.length()) && (length <= FontSize.XMMCseven.length())) {
            return FontSize.XMMCseven;
        }
        if ((length > FontSize.XMMCseven.length()) && (length <= FontSize.XMMCsix.length())) {
            return FontSize.XMMCsix;
        }
        throw new CustomException(1006, "文字长度过长，获取字体大小失败(" + length + "：" + org + ")");
    }

    public static int getFontSizeForQd(String org) throws CustomException {
        FontSize font = null;
        double orgLength = 0.0D;
        try {
            orgLength = StringUtil.getLength(org);
        } catch (Exception e) {
            throw new CustomException(1005, "获取字体大小失败(" + org + ")", e);
        }
        font = qdcompare(orgLength, org);
        return font.size();
    }

    public static FontSize qdcompare(double length, String org) throws CustomException {
        if (length <= FontSize.QDnine.length()) {
            return FontSize.QDnine;
        }
        if ((length > FontSize.QDnine.length()) && (length <= FontSize.QDeight.length())) {
            return FontSize.QDeight;
        }
        throw new CustomException(1006, "文字长度过长，获取字体大小失败(" + length + "：" + org + ")");
    }

    public static int getFontSize(String[] src, int srcFontSzie, int length) {
        int size = src.length / length;
        int ys = src.length % length;
        if ((size != 0) &&
                (ys >= 1)) {
            size++;
        }

        if ((size == 1) && (ys == 0)) {
            size = 0;
        }
        return srcFontSzie - size;
    }

    public static FontSize getFontSizeForGgxh(String src) throws CustomException {
        try {
            double srcLength = StringUtil.getLength(src);
            if (srcLength <= FontSize.GGXHnine.length()) {
                return FontSize.GGXHnine;
            }
            if ((srcLength > FontSize.GGXHnine.length()) && (srcLength <= FontSize.GGXHeight.length())) {
                return FontSize.GGXHeight;
            }
            if ((srcLength > FontSize.GGXHeight.length()) && (srcLength <= FontSize.GGXHseven.length())) {
                return FontSize.GGXHseven;
            }
            if ((srcLength > FontSize.GGXHseven.length()) && (srcLength <= FontSize.GGXHsix.length())) {
                return FontSize.GGXHsix;
            }
            if ((srcLength > FontSize.GGXHsix.length()) && (srcLength <= FontSize.GGXHfive.length())) {
                return FontSize.GGXHfive;
            }
            if ((srcLength > FontSize.GGXHfive.length()) && (srcLength <= FontSize.GGXHfour.length())) {
                return FontSize.GGXHfour;
            }
            return FontSize.GGXHfour;
        } catch (Exception e) {
            throw new CustomException(1007, "规格型号字体设置失败(" + src + ")", e);
        }
    }

    public static FontSize getFontSizeForDw(String src) throws CustomException {
        try {
            double srcLength = StringUtil.getLength(src);
            if (srcLength <= FontSize.DWnine.length()) {
                return FontSize.DWnine;
            }
            if ((srcLength > FontSize.DWnine.length()) && (srcLength <= FontSize.DWeight.length())) {
                return FontSize.DWeight;
            }
            if ((srcLength > FontSize.DWeight.length()) && (srcLength <= FontSize.DWseven.length())) {
                return FontSize.DWseven;
            }
            if ((srcLength > FontSize.DWseven.length()) && (srcLength <= FontSize.DWsix.length())) {
                return FontSize.DWsix;
            }
            if ((srcLength > FontSize.DWsix.length()) && (srcLength <= FontSize.DWfive.length())) {
                return FontSize.DWfive;
            }
            if ((srcLength > FontSize.DWfive.length()) && (srcLength <= FontSize.DWfour.length())) {
                return FontSize.DWfour;
            }
            return FontSize.DWfour;
        } catch (Exception e) {
            throw new CustomException(1007, "单位字体设置失败(" + src + ")", e);
        }
    }

    public static FontSize getFontSizeForQDBZ(String src) throws CustomException {
        try {
            double srcLength = StringUtil.getLength(src);
            if (srcLength <= FontSize.QDBZnine.length()) {
                return FontSize.QDBZnine;
            }
            if ((srcLength > FontSize.QDBZnine.length()) && (srcLength <= FontSize.QDBZeight.length())) {
                return FontSize.QDBZeight;
            }
            if ((srcLength > FontSize.QDBZeight.length()) && (srcLength <= FontSize.QDBZseven.length())) {
                return FontSize.QDBZseven;
            }
            if ((srcLength > FontSize.QDBZseven.length()) && (srcLength <= FontSize.QDBZsix.length())) {
                return FontSize.QDBZsix;
            }
            return FontSize.QDBZsix;
        } catch (Exception e) {
            throw new CustomException(1007, "清单备注字体设置失败(" + src + ")", e);
        }
    }

    public static FontSize getFontSizeForBZ(String src) throws CustomException {
        try {
            double srcLength = StringUtil.getLength(src);
            if (srcLength <= FontSize.BZnine.length()) {
                return FontSize.BZnine;
            }
            if ((srcLength > FontSize.BZnine.length()) && (srcLength <= FontSize.BZeight.length())) {
                return FontSize.BZeight;
            }
            if ((srcLength > FontSize.BZeight.length()) && (srcLength <= FontSize.BZseven.length())) {
                return FontSize.BZseven;
            }
            if ((srcLength > FontSize.BZseven.length()) && (srcLength <= FontSize.BZsix.length())) {
                return FontSize.BZsix;
            }
            return FontSize.BZsix;
        } catch (Exception e) {
            throw new CustomException(1007, "备注字体设置失败(" + src + ")", e);
        }
    }

    public static FontSize getFontSizeForSl(String src) throws CustomException {
        try {
            double srcLength = StringUtil.getLength(src);
            if (srcLength <= FontSize.SLnine.length()) {
                return FontSize.SLnine;
            }
            if ((srcLength > FontSize.SLnine.length()) && (srcLength <= FontSize.SLseven.length())) {
                return FontSize.SLseven;
            }
            return FontSize.SLseven;
        } catch (Exception e) {
            throw new CustomException(1007, "备注字体设置失败(" + src + ")", e);
        }
    }
}
