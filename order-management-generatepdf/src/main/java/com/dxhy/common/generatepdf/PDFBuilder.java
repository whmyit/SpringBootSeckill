package com.dxhy.common.generatepdf;

import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJ;
import com.dxhy.common.generatepdf.entity.JAR_FPQZ_KJMX;
import com.dxhy.common.generatepdf.entity.JAR_FPQZ_ZHMX;
import com.dxhy.common.generatepdf.exception.CustomException;
import com.dxhy.common.generatepdf.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

import static com.dxhy.common.generatepdf.util.InvoiceGenUtil.*;
/**
 * pdf生成
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 21:09
 */
@Slf4j
public class PDFBuilder {
    /**
     * 宋体
     */
    private static BaseFont fontSimsun;
    private static BaseFont fontCour;
    private static BaseFont fontCourNew;
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    public static void afterPropertiesSet() throws CustomException {
        try {
            fontSimsun = BaseFont.createFont(InvoiceGenUtil.SIMSUN, InvoiceGenUtil.ENCODE, false);
            fontCour = BaseFont.createFont("Courier", "", false);
            fontCourNew = BaseFont.createFont(InvoiceGenUtil.FONTS_COUR_TTF, "",
                    false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(1001, new StringBuilder()
                    .append("初始化字体错误:").append(e.getMessage()).toString(), e);
        }
    }

    /**
     * 组装PDF
     *
     * @param kj
     * @return
     * @throws Exception
     */
    public static Map<String, Object> bulidPdfA5(JAR_FPQZ_KJ kj)
            throws Exception {
        Map pdfMap = new HashMap(5);
        int qzsize = 0;
        afterPropertiesSet();
        JAR_FPQZ_KJMX[] kjmxs = kj.getJAR_FPQZ_KJMXS();
        JAR_FPQZ_ZHMX[] zhmxs = recombination(kjmxs);
        log.info("==>开票明细总个数：{}", kjmxs.length);
        log.info("==>开票明细行数：{}", zhmxs.length);
        boolean isQd;
        if ("51".equals(kj.getFPZL())) {
            isQd = (zhmxs.length > 8);
        } else {
            isQd = (kjmxs.length > 8);
        }
        int defaultFontSize = InvoiceGenUtil.DEFUALT_FONTSIZE;
        int mmqFontSize = InvoiceGenUtil.MMQ_FONTSIZE;
        int nsrsbhFontSize = InvoiceGenUtil.NSRSBH_FONTSIZE;
        int numberFontSize = InvoiceGenUtil.NUMBER_FONTSIZE;
        PdfReader reader = null;
        ByteArrayOutputStream out = null;
        PdfStamper ps = null;
        try {
            if ((null == kj.getMB_A5()) || (kj.getMB_A5().length == 0)) {
                reader = new PdfReader(new StringBuilder().append(InvoiceGenUtil.MB_A5_URL)
        
                        .append(StringUtils.isBlank(kj.getMBDM()) ? "0000" : kj.getMBDM()).append(".pdf").toString());
            } else {
                reader = new PdfReader(kj.getMB_A5());
            }
            out = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, out);
        } catch (Exception e) {
            throw new CustomException(1002, new StringBuilder()
                    .append("初始化发票模板错误:").append(e.getMessage()).toString(), e);
        }
        PdfContentByte pcb = ps.getUnderContent(1);
        //-----------------------代码提取-------------------------------
        String fpzl = kj.getFPZL();
        String jqbh = kj.getJQBH();
        String kj_ewm = kj.getEWM();
        String fp_dm = kj.getFP_DM();
        String fp_hm = kj.getFP_HM();

        //生成二维码
        byte[] imageData = null;
        try {
            String ewm = "";
            if ((StringUtils.isBlank(kj_ewm))
                    || ("09".equals(kj.getSJLY()))) {
                ewm = EWMUtil.generateTwoDimCode(fp_dm,
                        StringUtil.addZero(fp_hm, 8), kj.getHJJE(),
                        kj.getKPRQ(), kj.getJYM(), fpzl);
            } else {
                ewm = kj_ewm;
            }
            //Base64加密
            Base64.Decoder decoder = Base64.getDecoder();
            imageData = decoder.decode(ewm.getBytes(StandardCharsets.UTF_8));
            Image ewmImage = Image.getInstance(imageData);
            if (ORDER_INVOICE_TYPE_51.equals(fpzl)) {
                ewmImage.setAbsolutePosition(30.0F, 332.0F);
            } else {
                ewmImage.setAbsolutePosition(23.0F, 314.0F);
            }
            ewmImage.scaleAbsolute(50.0F, 50.0F);
            ewmImage.setAlignment(2);
            pcb.addImage(ewmImage);
        } catch (Exception e) {
            throw new CustomException(1003, "生成二维码错误:" + e.getMessage() + "(" + kj_ewm + ")", e);
        }

        pcb.beginText();

        String tcnr = new StringBuilder()
                .append(InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ()) ? "代开 "
                        : "")
                .append(InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? "收购 "
                        : "")
                .append(InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX()) ? "销项负数"
                        : "").toString();
        if (!StringUtils.isBlank(tcnr)) {
            if (ORDER_INVOICE_TYPE_51.equals(fpzl)) {
                pcb.setFontAndSize(fontSimsun, 15.0F);
                pcb.showTextAligned(3, tcnr.trim(), 105.0F, 330.0F, 0.0F);
            } else {
                pcb.setFontAndSize(fontSimsun, defaultFontSize);
                pcb.showTextAligned(3, tcnr.trim(), 105.0F, 324.0F, 0.0F);
            }
        }
        if (!ORDER_INVOICE_TYPE_51.equals(fpzl)) {
            //添加代码号码
            pcb.setFontAndSize(fontSimsun, 19.0F);
            pcb.showTextAligned(3, fp_dm, 88.0F, 348.0F, 0.0F);
            pcb.showTextAligned(3, StringUtil.addZero(fp_hm, 8), 462.0F,
                    348.0F, 0.0F);

            //再次添加机器代码号码
            pcb.setFontAndSize(fontSimsun, 8.0F);
            pcb.showTextAligned(3, fp_dm, 545.0F, 345.0F, 0.0F);
            pcb.setFontAndSize(fontSimsun, 11.0F);
            pcb.showTextAligned(3, StringUtil.addZero(fp_hm, 8), 540.0F,
                    334.0F, 0.0F);
    
            //开票日期
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            String kprq = new SimpleDateFormat("yyyy-MM-dd").format(kj.getKPRQ());
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            String date = new StringBuilder().append(kprq, 0, 4).append("年")
                    .append(kprq, 5, 7).append("月")
                    .append(kprq, 8, 10).append("日").toString();
            pcb.showTextAligned(
                    3, date, 520.0F,
                    320.0F, 0.0F);
    
        } else {
            //电子发票添加机器代码号码
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, fp_dm, 481.0F, 373.0F, 0.0F);
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, StringUtil.addZero(fp_hm, 8), 481.0F, 356.0F, 0.0F);
    
            //电子发票添加开票日期
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            String kprq = new SimpleDateFormat("yyyy-MM-dd").format(kj.getKPRQ());
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            String date = new StringBuilder().append(kprq, 0, 4).append("       ")
                    .append(kprq, 5, 7).append("       ")
                    .append(kprq, 8, 10).toString();
            pcb.showTextAligned(
                    3, date, 481.0F, 338.0F, 0.0F);
            // 电子发票添加校验码
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            int len = 0;
            String source = kj.getJYM();
            StringBuilder target = new StringBuilder();
            for (int i = 0; i < source.length(); i++) {
                target.append(source, len, len + 1);
                len++;
                if (len % 5 == 0) {
                    target.append("  ");
                }
            }
            pcb.showTextAligned(3, target.toString(), 481.0F, 320.0F, 0.0F);
        }
    
    
        pcb.setFontAndSize(fontCourNew, mmqFontSize);
        String mw = kj.getFP_MW();
        if (StringUtils.isNotBlank(mw) && mw.length() > InvoiceGenUtil.INT_112) {
        
        } else if (StringUtils.isNotBlank(mw) && mw.length() > InvoiceGenUtil.INT_110) {
            if (StringUtils.isNotBlank(mw)) {
                pcb.showTextAligned(1, mw.substring(0, mw.length() / 4), 477.0F,
                        298.0F, 0.0F);
                pcb.showTextAligned(1,
                        mw.substring(mw.length() / 4, mw.length() / 4 * 2), 477.0F,
                        283.0F, 0.0F);
                pcb.showTextAligned(1,
                        mw.substring(mw.length() / 4 * 2, mw.length() / 4 * 3), 477.0F,
                        268.0F, 0.0F);
                pcb.showTextAligned(1, mw.substring(mw.length() / 4 * 3),
                        477.0F, 253.0F, 0.0F);
            }
        } else {
            if (StringUtils.isNotBlank(mw)) {
                pcb.showTextAligned(1, mw.substring(0, mw.length() / 4), 478.0F,
                        298.0F, 0.0F);
                pcb.showTextAligned(1,
                        mw.substring(mw.length() / 4, mw.length() / 4 * 2), 478.0F,
                        283.0F, 0.0F);
                pcb.showTextAligned(1,
                        mw.substring(mw.length() / 4 * 2, mw.length() / 4 * 3), 478.0F,
                        268.0F, 0.0F);
                pcb.showTextAligned(1, mw.substring(mw.length() / 4 * 3),
                        478.0F, 253.0F, 0.0F);
            }
        }
    
    
        //专票机器编号注销  卷票没做处理
        // case "0": 专票  case "2": 普票  case "12":  卷票     电票
        if (ORDER_INVOICE_TYPE_2.equals(fpzl)) {
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, "机器编号：" + jqbh, 75.0F, 314.0F, 0.0F);
        } else if (ORDER_INVOICE_TYPE_51.equals(fpzl)) {
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, jqbh, 75.0F, 321.0F, 0.0F);
        }
    
        /**
         * todo 收购发票显示收购字样,但是购方和销方不进行颠倒
         */
        kj.setSGBZ("N");
        String gmf_mc = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getXSF_MC() : kj.getGMF_MC();
        pcb.setFontAndSize(fontSimsun, FontSizeUtil.getFontSize(gmf_mc));
        pcb.showTextAligned(3, gmf_mc, 108.0F, 300.0F, 0.0F);
    
        String gmf_nsrsbh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getXSF_NSRSBH() : kj.getGMF_NSRSBH();
        if (!StringUtils.isBlank(gmf_nsrsbh)) {
            pcb.setFontAndSize(fontCourNew, nsrsbhFontSize);
            pcb.showTextAligned(3, gmf_nsrsbh, 108.0F, 284.0F, 0.0F);
        }

        //地址电话
        String gmf_dzdh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getXSF_DZDH() : kj.getGMF_DZDH();
        if (!StringUtils.isBlank(gmf_dzdh)) {
            double length = 0;
            try {
                length = gmf_dzdh.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                log.error("获取购货方地址电话长度异常！{}",e);
                e.printStackTrace();
            }
            pcb.setFontAndSize(fontSimsun, FontSizeUtil.getFontSizeForDZDH(length, gmf_dzdh).size());
            if (length <= 76) {
                pcb.showTextAligned(3, gmf_dzdh, 108.0F, 270.0F, 0.0F);
            } else {
                String[] strings = StringUtil.substringToArry(gmf_dzdh, 76);
                pcb.showTextAligned(3, strings[0], 108.0F, 273.0F, 0.0F);
                pcb.showTextAligned(3, strings[1], 108.0F, 267.0F, 0.0F);
            }
        }
        //银行账号 100
        String gmf_yhzh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getXSF_YHZH() : kj.getGMF_YHZH();
        if (!StringUtils.isBlank(gmf_yhzh)) {
            double length = 0;
            try {
                length = gmf_yhzh.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                log.error("获取银行账号长度错误！{}",e);
                e.printStackTrace();
            }
            pcb.setFontAndSize(fontSimsun, FontSizeUtil.getFontSizeForDZDH(length, gmf_yhzh).size());
            if (length <= 76) {
                pcb.showTextAligned(3, gmf_yhzh, 108.0F, 254.0F, 0.0F);
            } else {
                String[] ghf = StringUtil.substringToArry(gmf_yhzh, 76);
                pcb.showTextAligned(3, ghf[0], 108.0F, 257.0F, 0.0F);
                pcb.showTextAligned(3, ghf[1], 108.0F, 251.0F, 0.0F);
            }
        }

        pcb.setFontAndSize(fontCourNew, numberFontSize);
        pcb.showTextAligned(
                2,
                new StringBuilder().append("¥")
                        .append(ArithUtil.fdd(kj.getHJJE(), 2, 2)).toString(),
                478.0F, 126.0F, 0.0F);

        boolean bl = false;

        BigDecimal sl = BigDecimal.ZERO;
        String slStr = "";
        for (int i = 0; i < kjmxs.length; i++) {
            if (i == 0) {
                sl = new BigDecimal(kjmxs[i].getSL());
                if (sl.compareTo(BigDecimal.ZERO) == 0) {
                    slStr = ValidateUtil.getSlStr(kj.getBMB_BBH(),
                            kjmxs[i].getZZSTSGL());
                    bl = true;
                } else {
                    slStr = new StringBuilder()
                            .append(ArithUtil.fdd(
                                    sl.multiply(new BigDecimal("100")), 0, 0))
                            .append("%").toString();
                }
            } else if (sl.compareTo(new BigDecimal(kjmxs[i].getSL())) != 0) {
                bl = false;
                slStr = "";
                break;
            }

        }

        pcb.setFontAndSize(fontCourNew, numberFontSize);
        if (bl) {
            pcb.showTextAligned(2, "***", 590.0F, 126.0F, 0.0F);
        } else {
            pcb.showTextAligned(
                    2,
                    new StringBuilder().append("¥")
                            .append(ArithUtil.fdd(kj.getHJSE(), 2, 2))
                            .toString(), 590.0F, 126.0F, 0.0F);
        }

        //价税合计金额展示
        BigDecimal jshj = new BigDecimal(kj.getHJJE()).add(new BigDecimal(kj
                .getHJSE()));
        pcb.setFontAndSize(fontCourNew, numberFontSize);
        pcb.showTextAligned(
                3,
                new StringBuilder().append("¥")
                        .append(ArithUtil.fdd(jshj, 2, 2)).toString(), 474.0F,
                110.0F, 0.0F);

        pcb.setFontAndSize(fontSimsun, defaultFontSize);
        //价税合计汉字展示
        pcb.showTextAligned(3, Money2CnUtil.money2Cn(jshj), 190.0F, 110.0F,
                0.0F);

        String xsf_mc = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getGMF_MC() : kj.getXSF_MC();
        pcb.setFontAndSize(
                fontSimsun,
                InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ()) ? FontSizeUtil
                        .getFontSizeDK(xsf_mc) : FontSizeUtil
                        .getFontSize(xsf_mc));
        pcb.showTextAligned(3, xsf_mc, 108.0F, 91.0F, 0.0F);

        String xsf_nsrsbh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getGMF_NSRSBH() : kj.getXSF_NSRSBH();
        if (!StringUtils.isBlank(xsf_nsrsbh)) {
            pcb.setFontAndSize(fontCourNew, nsrsbhFontSize);
            pcb.showTextAligned(3, xsf_nsrsbh, 108.0F, 76.0F, 0.0F);
        }
        //销货方地址电话
        String xsf_dzdh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getGMF_DZDH() : kj.getXSF_DZDH();
        if (!StringUtils.isBlank(xsf_dzdh)) {
            double length = 0;
            try {
                length = xsf_dzdh.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            log.info("销货方地址电话：{}",length);
            pcb.setFontAndSize(fontSimsun, FontSizeUtil.getFontSizeForDZDH(length, xsf_dzdh).size());
            if (length <= 76) {
                pcb.showTextAligned(3, xsf_dzdh, 108.0F, 63.0F, 0.0F);
            } else {
                String[] strings = StringUtil.substringToArry(xsf_dzdh, 76);
                pcb.showTextAligned(3, strings[0], 108.0F, 66.0F, 0.0F);
                pcb.showTextAligned(3, strings[1], 108.0F, 60.0F, 0.0F);
            }
        }

        String xsf_yhzh = InvoiceGenUtil.SGBZ_SGFP.equals(kj.getSGBZ()) ? kj
                .getGMF_YHZH() : kj.getXSF_YHZH();
        if (!StringUtils.isBlank(xsf_yhzh)) {
//            pcb.setFontAndSize(fontSimsun, InvoiceGenUtil.DKBZ_DKFP.equals(kj
//                    .getDKBZ()) ? FontSizeUtil.getFontSizeDK(xsf_yhzh)
//                    : FontSizeUtil.getFontSize(xsf_yhzh));

            double length = 0;
            try {
                length = xsf_yhzh.getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                log.error("获取销货方银行账号长度异常：{}",e);
                e.printStackTrace();
            }
            pcb.setFontAndSize(fontSimsun, FontSizeUtil.getFontSizeForDZDH(length, xsf_yhzh).size());
            if (length <= 76) {
                pcb.showTextAligned(3, xsf_yhzh, 108.0F, 49.0F, 0.0F);
            } else {
                String[] xsf_yhzhStr = StringUtil.substringToArry(xsf_yhzh, 76);
                pcb.showTextAligned(3, xsf_yhzhStr[0], 108.0F, 52.0F, 0.0F);
                pcb.showTextAligned(3, xsf_yhzhStr[1], 108.0F, 46.0F, 0.0F);
            }
        }

        if (InvoiceGenUtil.DKBZ_DKFP.equals(kj.getDKBZ())) {
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, "(代开机关)", 305.0F, 91.0F, 0.0F);
            pcb.showTextAligned(3, "(代开机关)", 305.0F, 77.0F, 0.0F);
            pcb.showTextAligned(3, "(完税凭证号)", 296.0F, 50.0F, 0.0F);
        }

        //备注
        if (!StringUtils.isBlank(kj.getBZ())) {
            String subBz = StringUtil.substring(kj.getBZ(), 230);
            FontSize font = FontSizeUtil.getFontSizeForBZ(subBz);
            pcb.setFontAndSize(fontSimsun, font.size());
            /**
             * 判断备注,
             * 1.截取字符串,判断是否有换行符号\n,如果存在,进行切分
             * 2.用切分后的数据再进行长度判断是否需要换行
             */
            List<String> sumBz = new ArrayList<>();
    
            String[] splitBz = subBz.split("\\n");
            if (splitBz.length > 0) {
                for (String s : splitBz) {
                    String[] bzs = StringUtil.substringToArry(s, font.width());
                    sumBz.addAll(Arrays.asList(bzs));
                }
            }
            
            int bzy = 92;
            int row = 0;
            int spacing = 10;
            for (int i = 0; i < defaultFontSize - font.size(); i++) {
                spacing--;
            }
            for (String bz : sumBz) {
                int bzCoordinate = bzy - row * spacing;
                pcb.showTextAligned(3, bz, 370.0F, bzCoordinate, 0.0F);
                row++;
            }
        }
        pcb.setFontAndSize(fontSimsun, defaultFontSize);
    
        if (!StringUtils.isBlank(kj.getSKR())) {
            pcb.showTextAligned(3, StringUtil.substring(kj.getSKR(), 16), 65.0F,
                    30.0F, 0.0F);
        }
        if (!StringUtils.isBlank(kj.getFHR())) {
            pcb.showTextAligned(3, StringUtil.substring(kj.getFHR(), 16),
                    215.0F, 30.0F, 0.0F);
        }
        pcb.showTextAligned(3, StringUtil.substring(kj.getKPR(), 16), 347.0F,
                30.0F, 0.0F);


        //作废标志  图片去掉
//        if ("1".equals(kj.getZFBZ())) {
//            Image zf = Image.getInstance("src/main/resources/public/zf.png");
//            zf.setAbsolutePosition(280.0F, 180.0F);
//            zf.scaleAbsolute(50.0F, 30.0F);
//            zf.setAlignment(2);
//            zf.setRotationDegrees(30.0F);
//            pcb.addImage(zf);
//        }
        if ("1".equals(kj.getZFBZ())) {
            if ("51".equals(fpzl)) {
                log.error("电票只能冲红不能作废！");
                throw new CustomException(1998, "电票只能冲红不能作废");
            } else {
                pcb.setFontAndSize(fontSimsun, 19.0F);
                pcb.showTextAligned(3, "作废", 178.0F, 318.0F, 0.0F);
            }
        }

        //如果为成品油，不适用清单模板
        if ("4".equals(kj.getQD_BZ())) {
            isQd = false;
            if ("51".equals(fpzl)) {
                pcb.setFontAndSize(fontSimsun, 15.0F);
                pcb.showTextAligned(3, "成品油", 105.0F, 345.0F, 0.0F);
            } else {
                pcb.setFontAndSize(fontSimsun, defaultFontSize);
                pcb.showTextAligned(3, "成品油", 105.0F, 335.0F, 0.0F);
            }
        }

        List qdList = new ArrayList();
        if (!isQd) {
            //创建发票主体明细
            if ("51".equals(kj.getFPZL())) {
                bulidA5Mx(pcb, kj, zhmxs);
            } else {
                bulidA5MxZP(pcb, kj, kjmxs);
            }
            //设置蒙板水印
            if (StringUtils.isNotBlank(kj.getPrintPdfWaterMark()) && PRINTPDFWATERMARK.equals(kj.getPrintPdfWaterMark())) {
        
                printWaterMark(ps, reader, StringUtils.isNotBlank(kj.getPrintPdfWaterMarkMsg()) ? kj.getPrintPdfWaterMarkMsg() : "仅作为预览使用");
            } else {
                printWaterMark(ps, reader, "仅作为预览使用");
            }
            pcb.endText();
            ps.close();
        } else {
            JAR_FPQZ_KJMX qdxmmc = new JAR_FPQZ_KJMX();
            /*if (InvoiceGenUtil.QDBZ_QZQD.equals(kj.getQD_BZ())) {
                qdxmmc.setXMMC(kj.getQDXMMC());
            } else*/if (InvoiceGenUtil.KPLX_LZFP.equals(kj.getKPLX())) {
                qdxmmc.setXMMC("(详见销货清单)");
            } else if (InvoiceGenUtil.KPLX_HZFP.equals(kj.getKPLX())) {
                qdxmmc.setXMMC("(详见对应正数发票及清单)");
            }

            qdxmmc.setXMJE(ArithUtil.fdd(kj.getHJJE(), 2, 2));
            if (bl) {
                qdxmmc.setSE("***");
            } else {
                qdxmmc.setSE(ArithUtil.fdd(kj.getHJSE(), 2, 2));
            }

            qdxmmc.setSL(slStr);

            //创建清单明细
            bulidQdMx(pcb, recombination(new JAR_FPQZ_KJMX[]{qdxmmc}));
    
            //设置蒙板水印
            if (StringUtils.isNotBlank(kj.getPrintPdfWaterMark()) && PRINTPDFWATERMARK.equals(kj.getPrintPdfWaterMark())) {
        
                printWaterMark(ps, reader, StringUtils.isNotBlank(kj.getPrintPdfWaterMarkMsg()) ? kj.getPrintPdfWaterMarkMsg() : "仅作为预览使用");
            } else {
                printWaterMark(ps, reader, "仅作为预览使用");
            }
            pcb.endText();


            ps.close();
            qdList.add(out.toByteArray());
            int size = 35;
            int mxLength = zhmxs.length;
            List genQd = new ArrayList();
            int start = 0;
            int pagelen = size;
            while (start + pagelen < mxLength) {
                while (zhmxs[(start + pagelen)].getZhs() != 0) {
                    pagelen--;
                }
                JAR_FPQZ_ZHMX[] qdmxs = null;
                qdmxs = new JAR_FPQZ_ZHMX[pagelen];
                System.arraycopy(zhmxs, start, qdmxs, 0, pagelen);
                genQd.add(qdmxs);
                start += pagelen;
                pagelen = size;
            }
            pagelen = mxLength - start;
            JAR_FPQZ_ZHMX[] qdmxs = new JAR_FPQZ_ZHMX[mxLength - start];
            System.arraycopy(zhmxs, start, qdmxs, 0, pagelen);
            genQd.add(qdmxs);
            qzsize = genQd.size();
            BigDecimal zjje = new BigDecimal("0.00");
            BigDecimal zjse = new BigDecimal("0.00");
            for (int page = 0; page < genQd.size(); page++) {
                //update by ysy 总计金额修改为 每页累加的金额
                Map<String, Object> bulidQdMx = bulidQdMx(kj, (JAR_FPQZ_ZHMX[]) genQd.get(page),
                        page + 1, genQd.size(), zjje, zjse);
                zjje = (BigDecimal) bulidQdMx.get("zjje");
                zjse = (BigDecimal) bulidQdMx.get("zjse");
                qdList.add(bulidQdMx.get("data"));
            }
        }

        if (isQd) {
            out = new ByteArrayOutputStream();
            mergePdfFiles(qdList, out);
        }
        byte[] data = null;
        data = out.toByteArray();
        if (out != null) {
            out.close();
        }
        pdfMap.put("fileByte", data);
        pdfMap.put("qdsize", qzsize);
        return pdfMap;
    }
    
    /**
     * 添加水印模版
     *
     * @param ps
     * @param reader
     * @param waterMark
     */
    public static void printWaterMark(PdfStamper ps, PdfReader reader, String waterMark) {
        PdfGState gs = new PdfGState();
        gs.setFillOpacity(0.3f);
        gs.setStrokeOpacity(0.4f);
        Rectangle pageRect = reader.getPageSizeWithRotation(1);
        PdfContentByte under = ps.getOverContent(1);
        
        under.saveState();
        under.setGState(gs);
        under.beginText();
        under.setFontAndSize(fontSimsun, 20);
        
        JLabel label = new JLabel();
        label.setText(waterMark);
        FontMetrics metrics = label.getFontMetrics(label.getFont());
        int textH = metrics.getHeight();
        int textW = metrics.stringWidth(label.getText());
        int interval = -5;
        
        // 水印文字成30度角倾斜
        //你可以随心所欲的改你自己想要的角度
        for (int height = interval + textH; height < pageRect.getHeight();
             height = height + textH * 20) {
            for (int width = interval + textW; width < pageRect.getWidth() + textW;
                 width = width + textW * 3) {
                under.showTextAligned(Element.ALIGN_LEFT
                        , waterMark, width - textW,
                        height - textH, 30);
            }
        }
        // 添加水印文字
        under.endText();
    }

    /**
     * 按照个数区分明细
     *
     * @param pcb
     * @param kj
     * @param kpmxs
     * @throws CustomException
     */
    private static void bulidA5MxZP(PdfContentByte pcb, JAR_FPQZ_KJ kj, JAR_FPQZ_KJMX[] kpmxs) throws CustomException {
    
        int fontSize = InvoiceGenUtil.DEFUALT_FONTSIZE;
        for (int i = 0; i < kpmxs.length; i++) {
            JAR_FPQZ_KJMX mx = kpmxs[i];
            /**
             * 1.根据开票名称的字数判断是否换行
             */
            double xmmcLength = 0;
            try {
                xmmcLength = mx.getXMMC().getBytes("GBK").length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                log.error("项目名称获取长度异常！{}",e);
            }
            log.info("项目名称长度:{}", xmmcLength);
            //2. 根据长度获取字号
            FontSize compare = FontSizeUtil.getFontSizeForXMMC(xmmcLength, mx.getXMMC());
            //3.放入PDF
            int y = 228 - 12 * i;
            pcb.setFontAndSize(fontSimsun, compare.size());
            if (xmmcLength <= 48) {
                pcb.showTextAligned(3, mx.getXMMC(), 27.0F, y, 0.0F);
            } else {
                String[] strings = StringUtil.substringToArry(mx.getXMMC(), 48);
                pcb.showTextAligned(3, strings[0], 27.0F, y, 0.0F);
                int z = 228 - 12 * i - 6;
                pcb.showTextAligned(3, strings[1], 27.0F, z, 0.0F);
            }
    
            if (!StringUtils.isBlank(mx.getGGXH())) {
                pcb.setFontAndSize(fontSimsun, fontSize);
                pcb.showTextAligned(3, mx.getGGXH(), 178.0F, y, 0.0F);
            }
            if (!StringUtils.isBlank(mx.getDW())) {
                pcb.setFontAndSize(fontSimsun, fontSize);
                pcb.showTextAligned(1, mx.getDW(), 238.0F, y, 0.0F);
            }
            pcb.setFontAndSize(fontSimsun, fontSize);
            pcb.showTextAligned(
                    2,
                    StringUtils.isBlank(mx.getXMSL()) ? ""
                            : ArithUtil.fdd(mx.getXMSL(), 8, 0), 317.0F, y,
                    0.0F);
            pcb.showTextAligned(
                    2,
                    StringUtils.isBlank(mx.getXMDJ()) ? ""
                            : ArithUtil.fdd(mx.getXMDJ(), 8, 2), 390.0F, y,
                    0.0F);
            if (!StringUtils.isBlank(mx.getXMJE())) {
                pcb.showTextAligned(2, ArithUtil.fdd(mx.getXMJE(), 2, 2),
                        478.0F, y, 0.0F);
            }
            if (!StringUtils.isBlank(mx.getSL())) {
                String slStr = "";
                if (StringUtils.isBlank(kpmxs[0].getKCE())) {
                    if (new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0) {
                        slStr = ValidateUtil.getSlStr(kj.getBMB_BBH(), mx.getZZSTSGL());
                    } else {
                        slStr = new StringBuilder().append(ArithUtil.fdd(new BigDecimal(mx.getSL()).multiply(new BigDecimal("100")),
                                1, 0)).append("%").toString();
                    }
                } else {
                    slStr = "***";
                }
                FontSize font = FontSizeUtil.getFontSizeForSl(slStr);
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(2, slStr, 502.0F, y, 0.0F);
            }
            if ((!StringUtils.isBlank(mx.getSE()))
                    && (!StringUtils.isBlank(mx.getSL()))) {
                pcb.setFontAndSize(fontSimsun, fontSize);
                pcb.showTextAligned(
                        2,
                        new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0 ? "***"
                                : ArithUtil.fdd(mx.getSE(), 2, 2), 590.0F, y,
                        0.0F);
            }
        }
    }

    /**
     * 电票发票主题部分，明细
     * 按照行数区分明细
     *
     * @param pcb
     * @param kj
     * @param zhmxs
     * @throws CustomException
     */
    private static void bulidA5Mx(PdfContentByte pcb, JAR_FPQZ_KJ kj,
                                  JAR_FPQZ_ZHMX[] zhmxs) throws CustomException {
        int fontSize = InvoiceGenUtil.DEFUALT_FONTSIZE;
        for (int i = 0; i < zhmxs.length; i++) {
            JAR_FPQZ_ZHMX mx = zhmxs[i];
            int y = 225 - 12 * i;
            pcb.setFontAndSize(fontSimsun, fontSize);
            pcb.showTextAligned(3, mx.getXMMC(), 27.0F, y, 0.0F);
            if ((!StringUtils.isBlank(mx.getGGXH())) && (!isZkh(kj, mx))) {
                FontSize font = mx.getZhs() > 0 ? FontSizeUtil
                        .getFontSizeForGgxh(zhmxs[(i - 1)].getGGXH())
                        : FontSizeUtil.getFontSizeForGgxh(mx.getGGXH());
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(3, mx.getGGXH(), 178.0F, y, 0.0F);
            }
            if ((!StringUtils.isBlank(mx.getDW())) && (!isZkh(kj, mx))) {
                FontSize font = mx.getZhs() > 0 ? FontSizeUtil
                        .getFontSizeForDw(zhmxs[(i - 1)].getDW())
                        : FontSizeUtil.getFontSizeForDw(mx.getDW());
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(1, mx.getDW(), 238.0F, y, 0.0F);
            }
            pcb.setFontAndSize(fontSimsun, fontSize);
            pcb.showTextAligned(
                    2,
                    (StringUtils.isBlank(mx.getXMSL())) || (isZkh(kj, mx)) ? ""
                            : ArithUtil.fdd(mx.getXMSL(), 8, 0), 317.0F, y,
                    0.0F);
            pcb.showTextAligned(
                    2,
                    (StringUtils.isBlank(mx.getXMDJ())) || (isZkh(kj, mx)) ? ""
                            : ArithUtil.fdd(mx.getXMDJ(), 8, 2), 390.0F, y,
                    0.0F);
            if (!StringUtils.isBlank(mx.getXMJE())) {
                pcb.showTextAligned(2, ArithUtil.fdd(mx.getXMJE(), 2, 2),
                        478.0F, y, 0.0F);
            }
            if (!StringUtils.isBlank(mx.getSL())) {
                String slStr = "";
                if (StringUtils.isBlank(zhmxs[0].getKCE())) {
                    if (new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0) {
                        slStr = ValidateUtil.getSlStr(kj.getBMB_BBH(), mx.getZZSTSGL());
                    } else {
                        slStr = new StringBuilder().append(ArithUtil.fdd(new BigDecimal(mx.getSL()).multiply(new BigDecimal("100")),
                                1, 0)).append("%").toString();
                    }
                } else {
                    slStr = "***";
                }
                FontSize font = FontSizeUtil.getFontSizeForSl(slStr);
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(2, slStr, 502.0F, y, 0.0F);
            }
            if ((!StringUtils.isBlank(mx.getSE()))
                    && (!StringUtils.isBlank(mx.getSL()))) {
                pcb.setFontAndSize(fontSimsun, fontSize);
                pcb.showTextAligned(
                        2,
                        new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0 ? "***"
                                : ArithUtil.fdd(mx.getSE(), 2, 2), 590.0F, y,
                        0.0F);
            }
        }
    }

    /**
     * 建立清单明细
     *
     * @param kj
     * @param zhmxs
     * @param currPage
     * @param totalPage
     * @return
     * @throws CustomException
     * @throws Exception
     */
    private static Map<String,Object> bulidQdMx(JAR_FPQZ_KJ kj, JAR_FPQZ_ZHMX[] zhmxs,
                                 int currPage, int totalPage,BigDecimal zjje,BigDecimal zjse) throws CustomException, Exception {
        Map<String, Object> map = new HashMap<>(5);
        int y = 595;
        JAR_FPQZ_KJMX[] kjmxs = kj.getJAR_FPQZ_KJMXS();
        int fontSize = InvoiceGenUtil.DEFUALT_FONTSIZE;
        PdfReader reader = null;
        ByteArrayOutputStream out = null;
        PdfStamper ps = null;
        try {
            if ((null == kj.getMB_LIST()) || (kj.getMB_LIST().length == 0)) {
                reader = new PdfReader(InvoiceGenUtil.MB_QD_URL + kj.getMBDM().split("/")[0] + "/list.pdf");
            } else {
                reader = new PdfReader(kj.getMB_LIST());
            }
            out = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, out);
        } catch (Exception e) {
            throw new CustomException(1008, "初始化清单模板错误", e);
        }
        PdfContentByte pcb = ps.getOverContent(1);
        pcb.beginText();

        pcb.setFontAndSize(fontSimsun,
                FontSizeUtil.getFontSizeForQd(kj.getGMF_MC()));
        pcb.showTextAligned(3, kj.getGMF_MC(), 88.0F, 692.0F, 0.0F);

        pcb.setFontAndSize(fontSimsun,
                FontSizeUtil.getFontSizeForQd(kj.getXSF_MC()));
        pcb.showTextAligned(3, kj.getXSF_MC(), 88.0F, 668.0F, 0.0F);

        pcb.setFontAndSize(fontSimsun, fontSize);
        pcb.showTextAligned(3, kj.getFP_DM(), 170.0F, 644.0F, 0.0F);

        pcb.showTextAligned(3, StringUtil.addZero(kj.getFP_HM(), 8), 290.0F,
                644.0F, 0.0F);

        pcb.showTextAligned(1, new StringBuilder().append(totalPage)
                .toString(), 485.0F, 644.0F, 0.0F);

        pcb.showTextAligned(1, new StringBuilder().append(currPage)
                .toString(), 547.0F, 644.0F, 0.0F);

        BigDecimal xjje = BigDecimal.ZERO;
        BigDecimal xjse = BigDecimal.ZERO;
        for (int i = 0; i < zhmxs.length; i++) {
            JAR_FPQZ_ZHMX mx = zhmxs[i];
            pcb.setFontAndSize(fontSimsun, fontSize);
            pcb.showTextAligned(1, mx.getXh(), 40.0F, y, 0.0F);
            pcb.showTextAligned(3, mx.getXMMC(), 55.0F, y, 0.0F);
            if ((!StringUtils.isBlank(mx.getGGXH())) && (!isZkh(kj, mx))) {
                FontSize font = mx.getZhs() > 0 ? FontSizeUtil
                        .getFontSizeForGgxh(zhmxs[(i - 1)].getGGXH())
                        : FontSizeUtil.getFontSizeForGgxh(mx.getGGXH());
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(3, mx.getGGXH(), 220.0F, y, 0.0F);
            }
            if ((!StringUtils.isBlank(mx.getDW())) && (!isZkh(kj, mx))) {
                FontSize font = mx.getZhs() > 0 ? FontSizeUtil
                        .getFontSizeForDw(zhmxs[(i - 1)].getDW())
                        : FontSizeUtil.getFontSizeForDw(mx.getDW());
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(1, mx.getDW(), 286.0F, y, 0.0F);
            }
            pcb.setFontAndSize(fontSimsun, fontSize);
            pcb.showTextAligned(
                    2,
                    (StringUtils.isBlank(mx.getXMSL())) || (isZkh(kj, mx)) ? ""
                            : ArithUtil.fdd(mx.getXMSL(), 8, 0), 360.0F, y,
                    0.0F);
            pcb.showTextAligned(
                    2,
                    (StringUtils.isBlank(mx.getXMDJ())) || (isZkh(kj, mx)) ? ""
                            : ArithUtil.fdd(mx.getXMDJ(), 8, 2), 420.0F, y,
                    0.0F);
            if (!StringUtils.isBlank(mx.getXMJE())) {
                pcb.showTextAligned(2, ArithUtil.fdd(mx.getXMJE(), 2, 2),
                        490.0F, y, 0.0F);
                xjje = xjje.add(new BigDecimal(mx.getXMJE()));
            }
            if (!StringUtils.isBlank(mx.getSL())) {
                String slStr = "";
                if (StringUtils.isBlank(zhmxs[0].getKCE())) {
                    if (new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0) {
                        slStr = ValidateUtil.getSlStr(kj.getBMB_BBH(), mx.getZZSTSGL());
                    } else {
                        slStr = new StringBuilder().append(ArithUtil.fdd(
                                new BigDecimal(mx.getSL()).multiply(new BigDecimal("100")),
                                1, 0)).append("%").toString();
                    }
                } else {
                    slStr = "***";
                }
                FontSize font = FontSizeUtil.getFontSizeForSl(slStr);
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(2, slStr, 512.0F, y, 0.0F);
            }
            if ((!StringUtils.isBlank(mx.getSE()))
                    && (!StringUtils.isBlank(mx.getSL()))) {
                pcb.setFontAndSize(fontSimsun, fontSize);
                pcb.showTextAligned(
                        2,
                        new BigDecimal(mx.getSL()).compareTo(BigDecimal.ZERO) == 0 ? "***"
                                : ArithUtil.fdd(mx.getSE(), 2, 2), 585.0F, y,
                        0.0F);
                xjse = xjse.add(new BigDecimal(mx.getSE()));
            }
            y -= 12;
        }
        zjje = zjje.add(xjje).setScale(2, RoundingMode.HALF_UP);
        zjse = zjse.add(xjse).setScale(2, RoundingMode.HALF_UP);
    
        pcb.setFontAndSize(fontSimsun, fontSize);
        pcb.showTextAligned(2, new StringBuilder().append("¥")
                .append(ArithUtil.fdd(xjje, 2, 2)).toString(), 490.0F, 163.0F, 0.0F);
        boolean bl = false;
        int count = 0;
        for (JAR_FPQZ_KJMX kjmx : kjmxs) {
            if (new BigDecimal(kjmx.getSL()).compareTo(BigDecimal.ZERO) == 0) {
                count++;
            }
        }
        if (count == kjmxs.length) {
            bl = true;
        }
    
        if (bl) {
            pcb.showTextAligned(2, "***", 585.0F, 163.0F, 0.0F);
        } else {
            pcb.showTextAligned(2, new StringBuilder().append("¥")
                            .append(ArithUtil.fdd(xjse, 2, 2)).toString(),
                    585.0F, 163.0F, 0.0F);
        }
        
        
        //总计金额
        pcb.showTextAligned(2, new StringBuilder().append("¥")
                        .append(ArithUtil.fdd(zjje.toString(), 2, 2)).toString(),
                490.0F, 151.0F, 0.0F);
        //总计税额
        if (bl) {
            pcb.showTextAligned(2, "***", 585.0F, 151.0F, 0.0F);
        } else {
            pcb.showTextAligned(
                    2,
                    new StringBuilder().append("¥")
                            .append(ArithUtil.fdd(zjse.toString(), 2, 2))
                            .toString(), 585.0F, 151.0F, 0.0F);
        }
        if (!StringUtils.isBlank(kj.getBZ())) {
            FontSize font = FontSizeUtil.getFontSizeForQDBZ(StringUtil
                    .substring(kj.getBZ(), 230));
            pcb.setFontAndSize(fontSimsun, font.size());
            String[] bzs = StringUtil.substringToArry(
                    StringUtil.substring(kj.getBZ(), 230), font.width());
            int bzY = 22;
            int bzyz = 137;
            if (bzs.length == 1) {
                pcb.showTextAligned(3, StringUtil.substring(kj.getBZ(), 230),
                        53.0F, bzyz, 0.0F);
            } else {
                for (int i = 0; i < bzs.length; i++) {
                    bzY--;
                }
                for (int k = 0; k < bzs.length; k++) {
                    String bz = bzs[k];
                    if (k != 0) {
                        bzyz -= bzY;
                    }
                    pcb.showTextAligned(3, bz, 53.0F, bzyz, 0.0F);
                }
            }
        }

        pcb.setFontAndSize(fontSimsun, fontSize);
        String kprqStr = new SimpleDateFormat("yyyy-MM-dd").format(kj.getKPRQ());
        pcb.showTextAligned(
                3,
                new StringBuilder().append(kprqStr, 0, 4)
                        .append("        ").append(kprqStr.subSequence(5, 7))
                        .append("     ").append(kprqStr.subSequence(8, 10))
                        .toString(), 460.0F, 88.0F, 0.0F);

        pcb.endText();
        //设置蒙板水印
        if (StringUtils.isNotBlank(kj.getPrintPdfWaterMark()) && PRINTPDFWATERMARK.equals(kj.getPrintPdfWaterMark())) {
        
            printWaterMark(ps, reader, StringUtils.isNotBlank(kj.getPrintPdfWaterMarkMsg()) ? kj.getPrintPdfWaterMarkMsg() : "仅作为预览使用");
        } else {
            printWaterMark(ps, reader, "仅作为预览使用");
        }
        ps.close();
        byte[] pdfBytes = out.toByteArray();
        if (out != null) {
            out.close();
        }
        
        map.put("zjje", zjje);
        map.put("zjse", zjse);
        map.put("data", pdfBytes);
        return map;
    }

    /**
     * 处理明细
     *
     * @param pcb
     * @param zhmxs
     * @throws CustomException
     */
    private static void bulidQdMx(PdfContentByte pcb, JAR_FPQZ_ZHMX[] zhmxs)
            throws CustomException {
        int defaultFontSize = InvoiceGenUtil.DEFUALT_FONTSIZE;
        for (int i = 0; i < zhmxs.length; i++) {
            JAR_FPQZ_ZHMX mx = zhmxs[i];
            int y = 225 - 12 * i;
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            pcb.showTextAligned(3, mx.getXMMC(), 27.0F, y, 0.0F);
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            if (!StringUtils.isBlank(mx.getXMJE())) {
                pcb.showTextAligned(2, mx.getXMJE(), 478.0F, y, 0.0F);
            }
            if (!StringUtils.isBlank(mx.getSL())) {
                FontSize font = FontSizeUtil.getFontSizeForSl(mx.getSL());
                pcb.setFontAndSize(fontSimsun, font.size());
                pcb.showTextAligned(2, mx.getSL(), 502.0F, y, 0.0F);
            }
            pcb.setFontAndSize(fontSimsun, defaultFontSize);
            if (!StringUtils.isBlank(mx.getSE())) {
                pcb.showTextAligned(2, mx.getSE(), 590.0F, y, 0.0F);
            }
        }
    }

    /**
     * 合并PDF数据
     *
     * @param files
     * @param output
     * @return
     * @throws CustomException
     */
    private static boolean mergePdfFiles(List<byte[]> files,
                                         ByteArrayOutputStream output) throws CustomException {
        try {
            Document document = new Document();
            PdfCopy copy = new PdfCopy(document, output);
            document.open();
            PdfReader reader;
            int page;
            for (byte[] file : files) {
                reader = new PdfReader(file);
                int n = reader.getNumberOfPages();
                for (page = 0; page < n; ) {
                    document.newPage();
                    copy.addPage(copy.getImportedPage(reader, ++page));
                }
            }
            document.close();
        } catch (Exception e) {
            throw new CustomException(1009, "清单合并错误", e);
        }
        return true;
    }

    /**
     * 对明细进行处理
     *
     * @param mxs
     * @return
     * @throws CustomException
     */
    private static JAR_FPQZ_ZHMX[] recombination(JAR_FPQZ_KJMX[] mxs)
            throws CustomException {
        List mxList = new ArrayList();
        int xh = 0;
        for (JAR_FPQZ_KJMX mx : mxs) {
            /**
             * 商品名称s
             */
            String[] spmcs = null;
            /**
             * 规格型号s
             */
            String[] ggxhs = null;
            /**
             * 单位s
             */
            String[] dws = null;
            int len = 0;
    
            spmcs = StringUtil.substringToArry(
                    StringUtil.substring(mx.getXMMC(), 90),
                    InvoiceGenUtil.SPH_SPMC_LENGTH);
            if (spmcs != null) {
                len = spmcs.length;
            }
    
            if (!StringUtils.isBlank(mx.getGGXH())) {
                ggxhs = StringUtil.substringToArry(
                        StringUtil.substring(mx.getGGXH(), 40),
                        InvoiceGenUtil.SPH_GGXH_LENGTH);
                len = Math.max(ggxhs.length, len);
            }
            if (!StringUtils.isBlank(mx.getDW())) {
                dws = StringUtil.substringToArry(
                        StringUtil.substring(mx.getDW(), 22),
                        InvoiceGenUtil.SPH_DW_LENGTH);
                len = Math.max(dws.length, len);
            }
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    JAR_FPQZ_ZHMX zhmx = new JAR_FPQZ_ZHMX();
                    zhmx.setXh(String.valueOf(++xh));
                    zhmx.setZhs(i);
                    zhmx.setXMMC(null == spmcs ? mx.getXMMC() : spmcs[i]);
                    zhmx.setDW(null == dws ? mx.getDW() : dws[i]);
                    zhmx.setGGXH(null == ggxhs ? mx.getGGXH() : ggxhs[i]);
                    String zhsl = "";
                    if (StringUtils.isBlank(mx.getXMSL())) {
                        if ((!StringUtils.isBlank(mx.getXMDJ()))
                                && (new BigDecimal(mx.getXMDJ())
                                .compareTo(BigDecimal.ZERO) != 0)) {
                            zhsl = ArithUtil
                                    .fdd(new BigDecimal(mx.getXMJE()).divide(
                                            new BigDecimal(mx.getXMDJ()), 8, 4),
                                            8, 0);
                        }
                    } else if (new BigDecimal(mx.getXMSL())
                            .compareTo(BigDecimal.ZERO) != 0) {
                        zhsl = mx.getXMSL();
                    }
                    zhmx.setXMSL(zhsl);
                    String zhdj = "";
                    if (StringUtils.isBlank(mx.getXMDJ())) {
                        if ((!StringUtils.isBlank(mx.getXMSL()))
                                && (new BigDecimal(mx.getXMSL())
                                .compareTo(BigDecimal.ZERO) != 0)) {
                            zhdj = ArithUtil
                                    .fdd(new BigDecimal(mx.getXMJE()).divide(
                                            new BigDecimal(mx.getXMSL()), 8, 4),
                                            8, 0);
                        }
                    } else if (new BigDecimal(mx.getXMDJ())
                            .compareTo(BigDecimal.ZERO) != 0) {
                        zhdj = mx.getXMDJ();
                    }
                    zhmx.setXMDJ(zhdj);
                    zhmx.setXMJE(mx.getXMJE());
                    zhmx.setSL(mx.getSL());
                    zhmx.setSE(mx.getSE());
                    zhmx.setZZSTSGL(mx.getZZSTSGL());
                    zhmx.setKCE(mx.getKCE());
                    mxList.add(zhmx);
                } else {
                    JAR_FPQZ_ZHMX zhmx = new JAR_FPQZ_ZHMX();
                    zhmx.setXh("");
                    zhmx.setZhs(i);
                    zhmx.setXMMC((null != spmcs) && (i < spmcs.length) ? spmcs[i]
                            : "");
                    zhmx.setGGXH((null != ggxhs) && (i < ggxhs.length) ? ggxhs[i]
                            : "");
                    zhmx.setDW((null != dws) && (i < dws.length) ? dws[i] : "");
                    mxList.add(zhmx);
                }
            }
        }
        JAR_FPQZ_ZHMX[] zhmxs = new JAR_FPQZ_ZHMX[mxList.size()];
        mxList.toArray(zhmxs);
        return zhmxs;
    }

    private static boolean isZkh(JAR_FPQZ_KJ kj, JAR_FPQZ_ZHMX mx) {
        boolean flag = false;
        if (!StringUtils.isBlank(mx.getXMJE())) {
            if ((kj.getKPLX().equals(InvoiceGenUtil.KPLX_LZFP))
                    && (!StringUtils.isBlank(mx.getXMJE()))
                    && (new BigDecimal(mx.getXMJE()).compareTo(BigDecimal.ZERO) == -1)) {
                flag = true;
            }
            if ((kj.getKPLX().equals(InvoiceGenUtil.KPLX_HZFP))
                    && (new BigDecimal(mx.getXMJE()).compareTo(BigDecimal.ZERO) == 1)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 转意发票种类
     *
     * @param fpzl
     * @return
     */
    private static String formatFPZL(String fpzl) {
        switch (fpzl) {
            case "0":
                return "z";
            case "2":
                return "p";
            case "12":
                return "j";
            case "51":
                return "d";
            default:
                return null;
        }

    }
}
