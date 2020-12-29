package com.dxhy.order.consumer.utils;

import com.dxhy.order.constant.ConfigureConstant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ：杨士勇
 * @ClassName ：QrCodeUtil
 * @Description ：生成带logo的二维码
 * @date ：2019年11月12日 上午10:23:07
 */
public class QrCodeUtil {
    /**
     * 默认是黑色
     */
    private static final int QRCOLOR = 0xFF000000;
    /**
     * 背景颜色
     */
    private static final int BGWHITE = 0xFFFFFFFF;
    /**
     * 二维码宽
     */
    private static final int WIDTH = 400;
    /**
     * 二维码高
     */
    private static final int HEIGHT = 400;
    
    /**
     * 设置背景色
     */
    private static final Map<String, Integer> BACK_GROUND_MAP = new HashMap<String, Integer>() {
        {
            put("gray", 0xF5F5F9);
            put("white", 0xFFFFFFFF);
        }
        
        
    };
    
    /**
     * 用于设置QR二维码参数
     */
    private static final Map<EncodeHintType, Object> HASH_MAP = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        
        {
            // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8);
        }
    };
    
    /**
     * 生成带logo的二维码图片
     *
     * @param logoFile
     * @param qrUrl
     * @param note
     * @param backGround
     * @return
     */
    public static String drawLogoQrCode(File logoFile, String qrUrl, String note, String backGround) {
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            BitMatrix bm = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, HASH_MAP);
            BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        
            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            int back = BGWHITE;
            if (StringUtils.isNotBlank(backGround)) {
                if (BACK_GROUND_MAP.get(backGround) != null) {
                    back = BACK_GROUND_MAP.get(backGround);
                }
    
            }
            
            for (int x = 0; x < WIDTH; x++) {
                for (int y = 0; y < HEIGHT; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? QRCOLOR : back);
                }
            }
            
            int width = image.getWidth();
            int height = image.getHeight();
            if (logoFile != null && logoFile.exists()) {
                // 构建绘图对象
                Graphics2D g = image.createGraphics();
                // 读取Logo图片
                BufferedImage logo = ImageIO.read(logoFile);
                // 开始绘制logo图片
                g.drawImage(logo, width * 2 / 5, height * 2 / 5, width * 2 / 10, height * 2 / 10, null);
                g.dispose();
                logo.flush();
            }
            // 自定义文本描述
            if (StringUtils.isNotEmpty(note)) {
                // 新的图片，把带logo的二维码下面加上文字
                BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D outg = outImage.createGraphics();
                // 画二维码到新的面板
                outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                // 画文字到新的面板
                outg.setColor(Color.BLACK);
                // 字体、字型、字号
                outg.setFont(new Font("楷体", Font.BOLD, 30));
                int strWidth = outg.getFontMetrics().stringWidth(note);
                if (strWidth > ConfigureConstant.INT_399) {
                    // //长度过长就截取前面部分
                    // 长度过长就换行
                    String note1 = note.substring(0, note.length() / 2);
                    String note2 = note.substring(note.length() / 2);
                    int strWidth1 = outg.getFontMetrics().stringWidth(note1);
                    int strWidth2 = outg.getFontMetrics().stringWidth(note2);
                    outg.drawString(note1, 200 - strWidth1 / 2, height + (outImage.getHeight() - height) / 32);
                    BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D outg2 = outImage2.createGraphics();
                    outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
                    outg2.setColor(Color.BLACK);
                    // 字体、字型、字号
                    outg2.setFont(new Font("宋体", Font.BOLD, 30));
                    outg2.drawString(note2, 200 - strWidth2 / 2, outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight()) / 32);
                    outg2.dispose();
                    outImage2.flush();
                    outImage = outImage2;
                } else {
                    // 画文字
                    outg.drawString(note, 200 - strWidth / 2, height + (outImage.getHeight() - height) / 32);
                }
                outg.dispose();
                outImage.flush();
                image = outImage;
            }
            
            image.flush();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            byte[] byteArray = out.toByteArray();
            
            String encodeToString = Base64Encoding.encodeToString(byteArray);
            return encodeToString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        String s = QrCodeUtil.drawLogoQrCode(null, "www.baidu.com", null, "gray");
        File file = new File("F://aaa.png");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.writeByteArrayToFile(file, Base64Encoding.decode(s));
    }
    
    
}

