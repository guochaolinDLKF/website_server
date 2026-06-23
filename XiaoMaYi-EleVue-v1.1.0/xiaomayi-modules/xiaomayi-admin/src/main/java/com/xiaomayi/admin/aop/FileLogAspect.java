// +----------------------------------------------------------------------
// | 小蚂蚁云企业级开发框架 [ 赋能开发者，助力企业发展 ]
// +----------------------------------------------------------------------
// | 版权所有 2020~2025 小蚂蚁云团队
// +----------------------------------------------------------------------
// | Licensed Apache-2.0 【小蚂蚁云】并不是自由软件，未经许可禁止去掉相关版权
// +----------------------------------------------------------------------
// | 官方网站: https://www.xiaomayicloud.com
// +----------------------------------------------------------------------
// | 软件作者: @小蚂蚁云团队 团队荣誉出品
// +----------------------------------------------------------------------
// | 版权和免责声明:
// | 本团队对该软件框架产品拥有知识产权（包括但不限于商标权、专利权、著作权、商业秘密等）
// | 均受到相关法律法规的保护，任何个人、组织和单位不得在未经本团队书面授权的情况下对所授权
// | 软件框架产品本身申请相关的知识产权，被授权主体务必妥善保管官方所授权的软件产品源码，禁
// | 止以任何形式对外泄露(包括但不限于分享、开源、网络平台),禁止用于任何违法、侵害他人合法
// | 权益等恶意的行为，禁止用于任何违反我国法律法规的一切项目研发，任何个人、组织和单位用于
// | 项目研发而产生的任何意外、疏忽、合约毁坏、诽谤、版权或知识产权侵犯及其造成的损失 (包括
// | 但不限于直接、间接、附带或衍生的损失等)，本团队不承担任何法律责任，本软件框架禁止任何
// | 单位、组织、个人用于任何违法、侵害他人合法利益等恶意的行为，如有发现违规、违法的犯罪行
// | 为，本团队将无条件配合公安机关调查取证同时保留一切以法律手段起诉的权利，本软件框架只能
// | 用于公司和个人内部的法律所允许的合法合规的软件产品研发，详细声明内容请阅读《框架免责声
// | 明》附件；
// +----------------------------------------------------------------------

package com.xiaomayi.admin.aop;

import com.xiaomayi.core.utils.StringUtils;
import com.xiaomayi.file.aspect.FileAspect;
import com.xiaomayi.file.vo.UploadFileLogVO;
import com.xiaomayi.file.vo.UploadFileVO;
import com.xiaomayi.security.utils.SecurityUtils;
import com.xiaomayi.system.entity.FileLog;
import com.xiaomayi.system.service.FileLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 文件上传日志切面
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-05-21
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class FileLogAspect extends FileAspect {

    private final FileLogService fileService;


    /**
     * 保存文件上传日志
     *
     * @param uploadFileLogVO 文件日志VO
     * @return 返回结果
     */
    @Override
    public boolean saveFileLog(UploadFileLogVO uploadFileLogVO) {
        log.info("文件上传AOP日志处理中...");

        try {
            // 上传文件列表
            List<UploadFileVO> uploadFileVOList = uploadFileLogVO.getUploadFileVOList();
            // 上传文件日志集判空
            if (StringUtils.isNotEmpty(uploadFileVOList)) {
                // 实例化文件日志列表
                List<FileLog> fileList = new ArrayList<>();
                // 遍历文件数据
                for (UploadFileVO uploadFileVO : uploadFileVOList) {
                    // 实例化操作日志对象
                    FileLog file = new FileLog();
                    // 拷贝文件日志信息
                    BeanUtils.copyProperties(uploadFileLogVO, file);
                    // 拷贝文件上传信息
                    BeanUtils.copyProperties(uploadFileVO, file);
                    // 操作人
                    file.setCreateUser(SecurityUtils.getUsername());
                    // 操作时间
                    file.setCreateTime(LocalDateTime.now());
                    // 加入列表
                    fileList.add(file);
                }
                // 批量插入数据
                if (StringUtils.isEmpty(fileList)) {
                    return false;
                }
                // 批量写入数据
                return fileService.saveBatch(fileList);
            }
        } catch (Exception e) {
            log.error("数据处理异常：{}", e.getMessage());
        }
        return false;
    }
}
