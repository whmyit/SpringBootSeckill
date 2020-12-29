package com.dxhy.common.generatepdf.util;

/**
 * 发票常量定义
 *
 * @author yaoxj
 * @time 2017年4月15日上午11:05:35
 */
public class InvoiceGenUtil {
	/**
     * GBK编码
     */
    public static final String CHARSET = "GBK";
    /**
     * 商品行 ——商品名称长度
     */
    public static final int SPH_SPMC_LENGTH = 32;
    /**
     * 商品行——规格型号长度
     */
    public static final int SPH_GGXH_LENGTH = 20;
    /**
     * 商品行——单位长度
     */
    public static final int SPH_DW_LENGTH = 14;
    /**
     * 默认字体大小
     */
    public static final int DEFUALT_FONTSIZE = 9;
    /**
     * 数字默认大小
     */
    public static final int NUMBER_FONTSIZE = 11;
    /**
     * 纳税人识别号-字体
     */
    public static final int NSRSBH_FONTSIZE = 12;
    /**
     * 密码区-字体
     */
    public static final int MMQ_FONTSIZE = 12;
    /**
     * 发票首页模板路径
     */
    public static final String MB_A5_URL = "template/";
    /**
     * 发票附录清单页模板路径
     */
    public static final String MB_QD_URL = "template/";
    /**
     * simsun：字体
     */
    public static final String SIMSUN = "STSong-Light";
    public static final String FONTS_COUR_TTF = "fonts/cour.ttf";
    public static final String ENCODE = "UniGB-UCS2-H";
    /**
     * 差额征税蓝字发票
     */
    public static final String KPLX_LZFP = "0";
    /**
     * 开票类型：红字发票
     */
    public static final String KPLX_HZFP = "1";
    /**
     * 清单标志：自动清单
     */
    public static final String QDBZ_ZDQD = "0";
    /**
     * 清单标志：强制清单</br>
     */
    public static final String QDBZ_QZQD = "1";
    /**
     * 代开标志——直开发票
     */
    public static final String DKBZ_ZKFP = "0";
    /**
     * 代开标志——代开发票：1
     */
    public static final String DKBZ_DKFP = "1";
    /**
     * 收购标志：1
     */
    public static final String SGBZ_SGFP = "Y";
    
    public static final String YHZCBS_FALSE = "0";
    public static final String YHZCBS_TRUE = "1";
    
    public static final String BB_FSPBMBB = "0";
    public static final String BB_SPBMBB = "1";
    /**
     * 签章位置
     */
    public static final int QZLEFT = 473;
    public static final int QZTOP = 10;
    public static final int QZRIGHT = 121;
    public static final int QZBOTTOM = 83;
    public static final int QZPAGEINDEX = 1;
    
    
    public static final int INT_110 = 110;
    
    public static final int INT_112 = 112;
    
    public static final String PRINTPDFWATERMARK = "Y";
    
    public static final String ORDER_INVOICE_TYPE_2 = "2";
    
    public static final String ORDER_INVOICE_TYPE_51 = "51";
}
