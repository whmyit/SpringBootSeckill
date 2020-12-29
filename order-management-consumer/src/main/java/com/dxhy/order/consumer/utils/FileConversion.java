package com.dxhy.order.consumer.utils;

import com.dxhy.order.constant.OrderInfoEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 描述信息： 文档转换  pdf转png  png 转base64
 *
 * @author 谢元强
 * @date Created on 2018-08-13
 */
@Slf4j
public class FileConversion {
    
    private final static String LOGGER_MSG = "(PDF转图片)";
    
    private static final String SUFFIX_PDF = ".pdf";
    private static final String SUFFIX_OFD = ".ofd";
    
    /**
     * @Description pdf 转png图片
     * @Author xieyuanqiang
     * @Date 15:12 2018-08-13
     */
    public static File[] pdfToPngFile(String pdfPath) {
        {
            File[] files = null;

            PDDocument document = null;
            try {
                document = PDDocument.load(new File(pdfPath));
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                int pageCounter = 0;
                files = new File[document.getNumberOfPages()];
                String imgPath = null;
                for (PDPage page : document.getPages()) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(pageCounter,
                            150, ImageType.RGB);
                    imgPath = pdfPath.substring(0, pdfPath.lastIndexOf(".")) +"-"+ (pageCounter) + ".png";
                    ImageIOUtil.writeImage(bim, imgPath,150);
                    files[pageCounter] = new File(imgPath);
                    pageCounter++;
                }
            }
            catch (IOException e)  {
                files = null;
                log.error(e.getMessage(), e);
            }finally{
                try {
                    if (document != null) {
                        document.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    document = null;
                }
            }
            return files;
        }
    }
    /**
     * 本地图片转换成base64字符串
     * @param file
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:40:46
     */
    public static List<String> pngToBase64(File[] file) {
        List<String> dataList = new ArrayList<>();
        InputStream in = null;
        byte[] data = null;
        if(file!=null){
           for(File f :file){
               // 读取图片字节数组
               try {
                   in = new FileInputStream(f);
                   data = new byte[in.available()];
                   in.read(data);
                   in.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
               // 对字节数组Base64编码
               String base64 = new String(Base64.encodeBase64(data), StandardCharsets.UTF_8);
               // 返回Base64编码过的字节数组字符串
               dataList.add("data:pdf/png;base64," + base64);
           }
        }
        return dataList;
    }

    /**
     * Description: 将pdf的base64转换为png的文件
     * @param  base64Content base64编码内容，文件的存储路径（含文件名）
     * @Author fuyuwei
     * Create Date: 2015年7月30日 上午9:40:23
     */
    public static File[] base64StringToPng(String base64Content,String pdfName,String pdfPath) {
        // PDF本地临时文件
        File tepmPDFFile = null;
        // PDF转换后的PNG文件
        File[] pngFiles = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        new File(pdfPath).mkdirs();
        try {
            //base64编码内容转换为字节数组
            byte[] streams = Base64Encoding.decode(base64Content);
            tepmPDFFile = new File(pdfPath + "/" + pdfName + SUFFIX_PDF);
            if (tepmPDFFile.exists()) {
                tepmPDFFile.createNewFile();
            }
            log.info("临时生成的pdf文件 tepmPDFFile {}", tepmPDFFile);
            FileUtils.writeByteArrayToFile(tepmPDFFile, streams);
            pngFiles = pdfToPngFile(tepmPDFFile.getAbsolutePath());
            log.info("临时生成的png文件 pngFiles ");
            return pngFiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Description: 将pdf的base64转换为png的文件
     *
     * @param base64Content base64编码内容，文件的存储路径（含文件名）
     * @Author fuyuwei
     * Create Date: 2015年7月30日 上午9:40:23
     */
    public static File base64StringToPdf(String base64Content, String pdfName, String pdfPath, String terminalCode) {
        // PDF本地临时文件
        File tepmPDFFile = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        new File(pdfPath).mkdirs();
        try {
            //base64编码内容转换为字节数组
            byte[] streams = Base64Encoding.decode(base64Content);
            String suffix = SUFFIX_PDF;
            if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_UKEY.getKey().equals(terminalCode)) {
                suffix = SUFFIX_OFD;
            }
            tepmPDFFile = new File(pdfPath + "/" + pdfName + suffix);
            if (tepmPDFFile.exists()) {
                tepmPDFFile.createNewFile();
            }
            log.info("临时生成的pdf文件 tepmPDFFile {}", tepmPDFFile);
            FileUtils.writeByteArrayToFile(tepmPDFFile, streams);
            return tepmPDFFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static byte[][] pdfByteToPngByte(byte[] pdfByte, String formatName) {
        byte[][] pngBytes = null;
        
        try {
            PDDocument document = PDDocument.load(pdfByte);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            pngBytes = new byte[document.getNumberOfPages()][];
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(i, 150, ImageType.RGB);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bim, formatName, out);
                byte[] pngByte = out.toByteArray();
                pngBytes[i] = pngByte;
                out.close();
            }
            document.close();
        } catch (IOException e) {
            log.error("{}pdf流转换为图片流出现问题:{}", LOGGER_MSG, e);
        }
        return pngBytes;
    }
    
    public static List pdfToPngFileForPc(byte[] streams, String pdfName, String pdfPath) {
        // PDF本地临时文件
        File tepmPDFFile = null;
        File[] pngFiles = null;
        List<String> data = new ArrayList<>();
        new File(pdfPath).mkdirs();
        try {
            tepmPDFFile = new File(pdfPath + "/" + pdfName + SUFFIX_PDF);
            if (tepmPDFFile.exists()) {
                tepmPDFFile.createNewFile();
            }
            log.info("临时生成的pdf文件 tepmPDFFile {}");
            FileUtils.writeByteArrayToFile(tepmPDFFile, streams);
            log.info("-----pdfTopng start----------{},{}", new Date(), tepmPDFFile);
            pngFiles = pdfToPngFile(tepmPDFFile.getAbsolutePath());
            log.info("-----pdfTopng end----------" + new Date());
            for (File pngFile : pngFiles) {
                if (pngFile != null) {
                    data.add(Base64.encodeBase64String(FileUtils.readFileToByteArray(pngFile)));
                }
            }
        } catch (IOException e) {
            log.error("save png File error" + e.getMessage(), e);
        }
        return data;
    }
    
    /**
     * @Description
     * @Author xieyuanqiang
     * @Date 17:33 2018-09-12
     */
    public static File[] getTypeFile(String pdfPath, String suffx) {
        Collection<File> listFiles = FileUtils.listFiles(new File(pdfPath), FileFilterUtils.suffixFileFilter(suffx), DirectoryFileFilter.INSTANCE);
        File[] file = new File[]{};
        return listFiles.toArray(file);
    }
    
    public static void main(String[] args) throws IOException {
        
        String pdfPath = "d:/ofd/011001600111_06199731.pdf";
        pdfPath = "d:/ofd/qd.pdf";
        byte[] bytes = FileUtils.readFileToByteArray(new File(pdfPath));
        
        byte[][] pngBytes = pdfByteToPngByte(bytes, "png");
        
        for (int i = 0; i < Objects.requireNonNull(pngBytes).length; i++) {
            byte[] pngByte1 = pngBytes[i];
            FileUtils.writeByteArrayToFile(new File("d:/ofd/1" + i + ".png"), pngByte1);
        }
    }
}
