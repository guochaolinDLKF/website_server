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

package com.xiaomayi.system.service.impl;

import com.xiaomayi.core.utils.FileUtils;
import com.xiaomayi.core.utils.IpUtils;
import com.xiaomayi.core.utils.R;
import com.xiaomayi.system.service.ServerService;
import com.xiaomayi.system.vo.server.*;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * <p>
 * 服务监控 服务实现类
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-11
 */
@Service
public class ServerServiceImpl implements ServerService {

    private static final int OSHI_WAIT_SECOND = 1000;

    /**
     * 获取服务信息
     *
     * @return 返回结果
     */
    @Override
    public R getServerInfo() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();

        // 实例化服务监控对象
        ServerInfoVO serverInfoVO = new ServerInfoVO();

        // CPU信息
        CpuInfoVO cpuInfoVO = getCpuInfo(hal.getProcessor());
        serverInfoVO.setCpuInfo(cpuInfoVO);

        // 系统信息
        SystemInfoVO systemInfoVO = getSystemInfo();
        serverInfoVO.setSystemInfo(systemInfoVO);

        // 内存信息
        MemoryInfoVO memoryInfoVO = getMemoryInfo(hal.getMemory());
        serverInfoVO.setMemoryInfo(memoryInfoVO);

        // JVM信息
        JvmInfoVO jvmInfoVO = getJvmInfo();
        serverInfoVO.setJvmInfo(jvmInfoVO);

        // 磁盘信息
        List<DiskInfoVO> diskInfoVOList = getDiskInfo(si.getOperatingSystem());
        serverInfoVO.setDiskInfo(diskInfoVOList);

        // 返回结果
        return R.ok(serverInfoVO);
    }

    /**
     * 获取CPU信息
     *
     * @param processor 参数
     * @return 返回结果
     */
    private CpuInfoVO getCpuInfo(CentralProcessor processor) {
        // 实例化CPU信息VO
        CpuInfoVO cpuInfo = new CpuInfoVO();

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        // CPU核心数
        cpuInfo.setCpuNum(processor.getLogicalProcessorCount());
        // CPU总的使用率
        cpuInfo.setTotal(totalCpu);
        // CPU系统使用率
        cpuInfo.setSys(cSys);
        // CPU用户使用率
        cpuInfo.setUsed(user);
        // CPU当前等待率
        cpuInfo.setWait(iowait);
        // CPU当前空闲率
        cpuInfo.setFree(idle);
        // 返回结果
        return cpuInfo;
    }

    /**
     * 获取系统信息
     *
     * @return 返回结果
     */
    private SystemInfoVO getSystemInfo() {
        // 实例化系统信息VO
        SystemInfoVO systemInfoVO = new SystemInfoVO();

        // 获取系统属性信息
        Properties props = System.getProperties();
        systemInfoVO.setComputerName(IpUtils.getHostName());
        systemInfoVO.setComputerIp(IpUtils.getHostIp());
        systemInfoVO.setOsName(props.getProperty("os.name"));
        systemInfoVO.setOsArch(props.getProperty("os.arch"));
        systemInfoVO.setUserDir(props.getProperty("user.dir"));
        // 返回结果
        return systemInfoVO;
    }

    /**
     * 获取内存信息
     *
     * @param memory 内存
     * @return 返回结果
     */
    private MemoryInfoVO getMemoryInfo(GlobalMemory memory) {
        // 实例化内存对象VO
        MemoryInfoVO memoryInfoVO = new MemoryInfoVO();
        memoryInfoVO.setTotal(memory.getTotal());
        memoryInfoVO.setUsed(memory.getTotal() - memory.getAvailable());
        memoryInfoVO.setFree(memory.getAvailable());
        return memoryInfoVO;
    }

    /**
     * 获取JVM信息
     *
     * @return 返回结果
     */
    private JvmInfoVO getJvmInfo() {
        // 实例化JVM信息VO
        JvmInfoVO jvmInfoVO = new JvmInfoVO();

        Properties props = System.getProperties();
        jvmInfoVO.setTotal(Runtime.getRuntime().totalMemory());
        jvmInfoVO.setMax(Runtime.getRuntime().maxMemory());
        jvmInfoVO.setFree(Runtime.getRuntime().freeMemory());
        jvmInfoVO.setVersion(props.getProperty("java.version"));
        jvmInfoVO.setHome(props.getProperty("java.home"));
        return jvmInfoVO;
    }

    /**
     * 获取磁盘信息
     *
     * @param os 磁盘
     * @return 返回结果
     */
    private List<DiskInfoVO> getDiskInfo(OperatingSystem os) {
        // 实例化磁盘信息列表
        List<DiskInfoVO> diskInfoVOList = new LinkedList<DiskInfoVO>();

        // 系统磁盘
        FileSystem fileSystem = os.getFileSystem();
        // 获取文件存储信息
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        // 遍历数据源
        for (OSFileStore fs : fileStores) {
            // 磁盘总量
            long total = fs.getTotalSpace();
            // 空闲磁盘
            long free = fs.getUsableSpace();
            // 已使用
            long used = total - free;

            // 实例化磁盘信息VO
            DiskInfoVO sysFile = new DiskInfoVO();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(FileUtils.formatFileSize(total));
            sysFile.setFree(FileUtils.formatFileSize(free));
            sysFile.setUsed(FileUtils.formatFileSize(used));
            // 数值转换
            double usage = BigDecimal.valueOf(used)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
            sysFile.setUsage(usage);
            diskInfoVOList.add(sysFile);
        }
        return diskInfoVOList;
    }

}
