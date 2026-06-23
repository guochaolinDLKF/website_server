package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.WatermarkUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 图片水印 案例
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/watermark")
@AllArgsConstructor
public class WatermarkController {

    /**
     * 添加单个水印
     *
     * @return 返回结果
     */
    @PostMapping("/watermark")
    public R watermark() {
        try {
            String srcImagePath = "D:\\upload\\1.png";
            String targetImagePath = "D:\\upload\\3.png";
            String watermarkText = "小蚂蚁云";

            // 设置水印字体、颜色和位置
            Font font = new Font("YouYuan", Font.BOLD, 100);
            Color color = Color.WHITE;
            // x坐标
            int x = 200;
            // y坐标
            int y = 200;
            // 透明度
            float alpha = 0.8f;

            // 调用添加水印方法
            WatermarkUtil.addTextWatermark(
                    srcImagePath, targetImagePath,
                    watermarkText, font, color, x, y, alpha);
            log.info("添加水印成功");
            return R.ok();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return R.failed();
    }

    /**
     * 添加平铺水印
     *
     * @return 返回结果
     */
    @PostMapping("/watermark2")
    public R watermark2() {
        try {
            String srcImagePath = "D:\\upload\\1.png";
            String targetImagePath = "D:\\upload\\2.png";
            String watermarkText = "小蚂蚁云";

            // 读取原始图片
            BufferedImage image = ImageIO.read(new File(srcImagePath));

            // 设置水印参数
            Font font = new Font("YouYuan", Font.BOLD, 100);
            // 带透明度,最后一个参数是透明度(0.0f-1.0f)
            Color color = new Color(1.0f, 1.0f, 1.0f, 0.8f);
            float alpha = 0.8f;
            // 旋转45度
            Integer degree = -45;
            // 水印间隔
            int interval = 150;

            // 添加平铺水印
            BufferedImage watermarkedImage = WatermarkUtil.addTiledTextWatermark(
                    image, watermarkText, font, color, alpha, degree, interval);

            // 保存图片
            ImageIO.write(watermarkedImage, "png", new File(targetImagePath));
            log.info("添加水印成功");
            return R.ok();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return R.failed();
    }
}
