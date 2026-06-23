package com.xiaomayi.admin.controller.demo;

import com.xiaomayi.core.utils.R;
import com.xiaomayi.core.utils.ZipUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 压缩、解压缩文件 前端控制器
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-26
 */
@Slf4j
@RestController
@RequestMapping("/zip")
@AllArgsConstructor
public class UnZipController {

    /**
     * 文件压缩
     *
     * @return 返回结果
     * @throws IOException 异常处理
     */
    @GetMapping("/compress")
    public R compress() throws IOException {
        // 设置源文件路径
        String sourceFolderPath = "E:\\compress";
        // 设置压缩文件路径
        String zipFilePath = "E:\\compress.zip";
        // 实现文件压缩
        ZipUtils.zip(sourceFolderPath, zipFilePath, true);
        return R.ok();
    }

    /**
     * 指定文件列表压缩
     *
     * @return 返回结果
     * @throws IOException 异常处理
     */
    @GetMapping("/compress2")
    public R compress2() throws IOException {
        // 设置文件列表
        List<File> srcFiles = new ArrayList<>();
        srcFiles.add(new File("E:\\compress\\文件1.xlsx"));
        srcFiles.add(new File("E:\\compress\\文件2.xlsx"));
        srcFiles.add(new File("E:\\compress\\文件1.docx"));
        // 压缩文件路径
        String zipFilePath = "E:\\compress.zip";
        // 实现文件压缩
        ZipUtils.zip(srcFiles, zipFilePath);
        return R.ok();
    }

    /**
     * 文件解压缩
     *
     * @return 返回结果
     * @throws IOException 异常处理
     */
    @GetMapping("/decompress")
    public R decompress() throws IOException {
        // 设置压缩文件路径
        String zipFilePath = "E:\\compress.zip";
        // 设置解压文件路径
        String destDirectory = "E:\\compress2";
        // 实现文件解压缩
        ZipUtils.unzip(zipFilePath, destDirectory);
        return R.ok();
    }

}
