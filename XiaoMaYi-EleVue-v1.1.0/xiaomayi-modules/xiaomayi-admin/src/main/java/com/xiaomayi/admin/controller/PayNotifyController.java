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

package com.xiaomayi.admin.controller;

import com.alibaba.fastjson2.JSON;
import com.alipay.api.AlipayApiException;
import com.github.wxpay.sdk.WXPayUtil;
import com.xiaomayi.alipay.config.AlipayConfig;
import com.xiaomayi.alipay.dto.AlipayNotifyParamDTO;
import com.xiaomayi.alipay.service.AlipayService;
import com.xiaomayi.alipay.utils.ParamsUtils;
import com.xiaomayi.alipay.vo.NotifyParamsVO;
import com.xiaomayi.wxpay.config.WxPayConfig;
import com.xiaomayi.wxpay.utils.StreamUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付回调 前端控制器
 * 特别说明：此处仅仅写的是支付宝、微信支付回调通知，本身放在后台并不合适，实际项目使用时根据实际场景自行移动或者拷贝文件
 * </p>
 *
 * @author 小蚂蚁云团队
 * @since 2024-06-14
 */
@Slf4j
@RestController
@RequestMapping("/pay/notify")
@Tag(name = "参数管理", description = "参数管理")
@AllArgsConstructor
public class PayNotifyController {

    private final AlipayService alipayService;

    /**
     * 支付宝支付回调通知
     *
     * @param request  网络请求
     * @param response 请求响应
     * @return 返回结果
     * @throws Exception 异常处理
     */
    @Operation(summary = "支付宝回调通知", description = "支付宝回调通知")
    @PostMapping("/alipay")
    public String alipayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("支付通知回调方法成功调用");

        NotifyParamsVO notifyParams = alipayService.alipayNotify(request);
        // 获取参数
        Map<String, String> params = ParamsUtils.ParamstoMap(request);
        String paramsJson = JSON.toJSONString(params);
        log.info("支付宝回调，{}", paramsJson);

        // 检测是否已接收通知
        if (!notifyParams.isIsreceive()) {
            log.info("支付宝回调签名认证失败，signVerified=false, params:{}", paramsJson);
            // 返回失败
            return "failure";
        }

        log.info("进入成功处理流程");
        // 商品订单号
        String outTradeNo = params.get("out_trade_no");
        log.info("商品回传订单号：{}", outTradeNo);

        // 1. 根据调用订单号查询订单信息
        // TODO... 查询订单并处理业务逻辑，如果订单支付状态已处理则直接返回成功 "success"

        log.info("验证订单金额：{}", params.get("total_amount"));
        // 2. 判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额）
        // TODO... 验证支付金额是否与订单金额一致

        log.info("金额验证通过");

        // 3. 校验通知中的seller_id（或者seller_email)是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），

        // 4、验证app_id是否为该商户本身。
        if (!params.get("app_id").equals(AlipayConfig.getAlipayAppId())) {
            throw new AlipayApiException("app_id不一致");
        }
        log.info("APPID验证通过");

        // 构建支付回调参数
        AlipayNotifyParamDTO param = buildAlipayNotifyParam(params);
        // 交易状态
        String trade_status = param.getTradeStatus();

        // 支付成功
        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
            log.info("支付宝回调支付成功，trade_status: {}", trade_status);

            // TODO... 处理业务逻辑，比如更新订单支付状态成功，保存交易流水，修改订单状态
        } else {
            log.error("没有处理支付宝回调业务，支付宝交易状态：{},params: {}", trade_status, paramsJson);
        }

        // 返回结果
        return "success";
    }

    /**
     * 构建支付宝回调通知参数
     *
     * @param params 参数
     * @return 返回结果
     */
    private AlipayNotifyParamDTO buildAlipayNotifyParam(Map<String, String> params) {
        String json = JSON.toJSONString(params);
        return JSON.parseObject(json, AlipayNotifyParamDTO.class);
    }

    /**
     * 微信支付回调通知
     *
     * @param request  网络请求
     * @param response 请求响应
     * @return 返回结果
     * @throws Exception 异常处理
     */
    @Operation(summary = "微信支付回调通知", description = "微信支付回调通知")
    @PostMapping("/wxpay")
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("微信支付结果异步通知处理");
        // 获得通知结果
        ServletInputStream inputStream = request.getInputStream();
        String notifyXml = StreamUtils.inputStream2String(inputStream, "utf-8");
        System.out.println("xmlString = " + notifyXml);
        log.debug(notifyXml);
        // 定义响应对象
        HashMap<String, String> returnMap = new HashMap<>();
        // 签名验证：防止伪造回调
        if (WXPayUtil.isSignatureValid(notifyXml, WxPayConfig.getAppKey())) {
            // 解析返回结果
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);
            //判断支付是否成功
            if ("SUCCESS".equals(notifyMap.get("result_code"))) {
                // 校验订单金额是否一致
                String totalFee = notifyMap.get("total_fee");
                // 交易流水号
                String outTradeNo = notifyMap.get("out_trade_no");

                // TODO... 此处根据实际项目需求处理业务逻辑


                // 返回处理成功响应，特别说明：如果订单已处理则直接返回处理成功即可，编译订单等业务数据被重复处理
                // 如果返回SUCCESS，微信会认为支付成功，不会发起二次通知，如果返回FAIL，微信会发起二次通知，直到超过一定次数，最终通知失败。
                log.info("支付成功，通知已处理");
                returnMap.put("return_code", "SUCCESS");
                returnMap.put("return_msg", "OK");
                String returnXml = WXPayUtil.mapToXml(returnMap);
                response.setContentType("text/xml");

                // 返回结果
                return returnXml;
            }
        }

        // 校验失败，返回失败应答
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");
        String returnXml = WXPayUtil.mapToXml(returnMap);
        response.setContentType("text/xml");
        log.warn("校验失败");
        return returnXml;
    }

}
