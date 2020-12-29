package com.dxhy.order.consumer.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF处理工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:27
 */
public class PdfUtil {

	/**
     * 合并PDF
     *
     * @param files 需要合并的PDF byte[]集合
     * @return 合并后的PDF byte[]
     * @throws Exception
     */
	public static byte[] mergePdfFiles(List<byte[]> files) throws Exception {
        byte[] result = null;
        Document document = new Document();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfCopy copy = new PdfCopy(document, output);
        document.open();
        for (byte[] file : files) {
            PdfReader reader = new PdfReader(file);
            int n = reader.getNumberOfPages();
            for (int page = 0; page < n; ) {
                document.newPage();
                page++;
                copy.addPage(copy.getImportedPage(reader, page));
            }
        }
        document.close();
        result = output.toByteArray();
        if (output != null) {
            output.close();
        }
        return result;
	}

	public static void main(String[] args) {
		try {
			List<byte[]> files = new ArrayList<>();
			files.add(FileUtils.readFileToByteArray(new File("D:\\\\3-line-bj.pdf")));
			files.add(FileUtils.readFileToByteArray(new File("D:\\\\3-line-bj.pdf")));
			files.add(FileUtils.readFileToByteArray(new File("D:\\\\3-line-bj.pdf")));
			byte[] result = mergePdfFiles(files);
			FileUtils.writeByteArrayToFile(new File("D:/12345.pdf"), result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
