package com.example.test;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * zxing方式生成二维码
 * jar包地址：https://github.com/zxing/
 * Created by Peng
 * Time: 2017/3/29 16:13
 */
public class CreateQRCode {
    public static void main(String[] args) {
        int width = 300;
        int height = 300;
        String format = "png";
        //String content = "https://www.baidu.com";
        //String content = "https://css-itg.ext.hp.com/pps_wechat/oauth.jsp?target_url=https://css-itg.ext.hp.com/pps_wechat/servicestation.jsp";
        String content = "https://w.url.cn/s/ASYTOp1";
        //定义二维码的参数
        HashMap hints = new HashMap();
        //编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        //边距
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            //Path file = new File("C:\\Users\\yuang\\Downloads\\line-bot-sdk-java-master\\test-boot2-compatibility\\out\\servicestation.png").toPath();
            //Path file = new File("C:\\Users\\yuang\\Downloads\\line-bot-sdk-java-master\\test-boot2-compatibility\\out\\baiduQRCode.png").toPath();
            Path file = new File("C:\\Users\\yuang\\Downloads\\line-bot-sdk-java-master\\test-boot2-compatibility\\out\\shortUrl.png").toPath();
            //注意：D:/files 目录必须存在，不会自动创建
            MatrixToImageWriter.writeToPath(bitMatrix, format, file);
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
