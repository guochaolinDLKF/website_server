package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.core.utils.R;
import com.xiaomayi.oss.utils.AliyunOSSUtil;
import com.xiaomayi.oss.vo.AliyunOSSVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/oss")
@AllArgsConstructor
public class OSSController {

    /**
     * 上传文件至OSS云存储
     *
     * @param file 文件对象
     * @return 返回结果
     */
    @PostMapping("/uploadFile")
    public R uploadFile(@RequestParam("file") MultipartFile file) {
        // 定义存储目录
        String folder = "test";
        // 定义新文件名
        String newFileName = file.getOriginalFilename();
        // 上传文件
        AliyunOSSVO aliyunOSSVO = AliyunOSSUtil.upload(file, folder, newFileName);
        // 返回结果
        return R.ok(aliyunOSSVO);
    }

    /**
     * 文件下载
     *
     * @return 返回结果
     */
    @GetMapping("/downloadFile")
    public R downloadFile() {
        // OSS文件地址
        String ossFilePath = "test/logo.jpg";
        // 本地文件地址
        String filePath = "E:/logo.jpg";
        // 读取OSS文件并写入本地文件
        AliyunOSSUtil.writeLocalFile(ossFilePath, filePath);
        return R.ok();
    }

    /**
     * 文件删除
     *
     * @return 返回结果
     */
    @DeleteMapping("/deleteFile")
    public R deleteFile() {
        AliyunOSSUtil.delete("test/logo.jpg");
        return R.ok();
    }

}
