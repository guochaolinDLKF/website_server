package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.core.utils.R;
import com.xiaomayi.qrcode.dto.QrCodeParam;
import com.xiaomayi.qrcode.utils.QRCodeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * <p>
 * 案例测试 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/qrcode")
@AllArgsConstructor
public class QrCodeController {

    /**
     * 生成普通二维码
     *
     * @throws Exception 异常处理
     */
    @GetMapping(value = "/encodeQrCode", produces = MediaType.IMAGE_PNG_VALUE)
    public R encodeQrCode() throws Exception {
        String result = QRCodeUtil.encodeQrCode(
                "小蚂蚁云",
                500,
                500,
                "E:\\qrcode\\普通二维码.jpg");
        return R.ok(result);
    }

    /**
     * 生成带LOGO二维码
     *
     * @throws Exception 异常处理
     */
    @GetMapping(value = "/encodeLogoQrCode", produces = MediaType.IMAGE_PNG_VALUE)
    public R encodeLogoQrCode() throws Exception {
        String result = QRCodeUtil.encodeQrCodeWithEmbeddedImg(
                "小蚂蚁云",
                500,
                500,
                "E:\\qrcode\\微信.png",
                "E:\\qrcode\\生成带LOGO二维码.jpg");
        return R.ok(result);
    }

    /**
     * 生成带LOGO和文字的二维码
     *
     * @throws Exception 异常处理
     */
    @GetMapping(value = "/encodeLogoTextQrCode", produces = MediaType.IMAGE_PNG_VALUE)
    public R encodeLogoTextQrCode() throws Exception {
        // 生成带LOGO、文字的二维码
        QrCodeParam para = QrCodeParam.builder()
                .qrCodeFileOutputPath("E:\\qrcode\\带文字带图片的二维码.jpg")
                .qrCodeContent("https://www.xiaomayicloud.com")
                .qrCodeWidth(500)
                .qrCodeHeight(500)
                .embeddedImgFilePath("E:\\qrcode\\支付宝.png")
                .wordContent("小蚂蚁云官网")
                .wordSize(20)
                .build();
        String result = QRCodeUtil.encodeQrCodeWithEmbeddedImgAndFonts(para);
        return R.ok(result);
    }

    /**
     * 识别二维码
     *
     * @throws Exception 异常处理
     */
    @GetMapping(value = "/decodeQrCode")
    public R decodeQrCode() throws Exception {
        String result = QRCodeUtil.decodeQrCode(new File("E:\\qrcode\\普通二维码.jpg"));
        return R.ok(result);
    }

}
