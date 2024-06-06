package com.ruoyi.dts.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dts.core.express.ExpressService;
import com.ruoyi.dts.core.notify.NotifyService;
import com.ruoyi.dts.core.notify.NotifyType;
import com.ruoyi.dts.core.util.JacksonUtil;
import com.ruoyi.dts.db.domain.DtsComment;
import com.ruoyi.dts.db.domain.DtsOrder;
import com.ruoyi.dts.db.domain.DtsOrderGoods;
import com.ruoyi.dts.db.domain.UserVo;
import com.ruoyi.dts.db.service.*;
import com.ruoyi.dts.db.util.OrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminOrderService {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderService.class);

    @Autowired
    private DtsOrderGoodsService orderGoodsService;
    @Autowired
    private DtsOrderService orderService;
    @Autowired
    private DtsGoodsProductService productService;
    @Autowired
    private DtsUserService userService;
    @Autowired
    private DtsCommentService commentService;
    @Autowired
    private ExpressService expressService;
    /*
     * @Autowired private WxPayService wxPayService;
     */
    @Autowired
    private NotifyService notifyService;

    @Autowired
    private AdminDataAuthService adminDataAuthService;

    public Object list(Integer userId, String orderSn, List<Short> orderStatusArray, Integer page, Integer limit,
                       String sort, String order) {

        // 需要区分数据权限，如果属于品牌商管理员，则需要获取当前用户管理品牌店铺
        List<Integer> brandIds = null;
        if (adminDataAuthService.isBrandManager()) {
            brandIds = adminDataAuthService.getBrandIds();
            logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));

            if (brandIds == null || brandIds.size() == 0) {//如果尚未管理任何入驻店铺，则返回空数据
                Map<String, Object> data = new HashMap<>();
                data.put("total", 0L);
                data.put("items", null);

                logger.info("【请求结束】商场管理->订单管理->查询,响应结果:{}", JSONObject.toJSONString(data));
                return AjaxResult.success(data);
            }
        }
        List<DtsOrder> orderList = null;
        long total = 0L;
        if (brandIds == null || brandIds.size() == 0) {
            orderList = orderService.querySelective(userId, orderSn, orderStatusArray);
            total = PageInfo.of(orderList).getTotal();
        } else {
            orderList = orderService.queryBrandSelective(brandIds, userId, orderSn, orderStatusArray);
            total = PageInfo.of(orderList).getTotal();
        }

        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", orderList);

        logger.info("【请求结束】商场管理->订单管理->查询,响应结果:{}", JSONObject.toJSONString(data));
        return AjaxResult.success(data);
    }

    public Object detail(Integer id) {
        DtsOrder order = orderService.findById(id);
        List<DtsOrderGoods> orderGoods = orderGoodsService.queryByOid(id);
        UserVo user = userService.findUserVoById(order.getUserId());
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("orderGoods", orderGoods);
        data.put("user", user);

        logger.info("【请求结束】商场管理->订单管理->详情,响应结果:{}", JSONObject.toJSONString(data));
        return AjaxResult.success(data);
    }

    /**
     * 订单退款
     * <p>
     * 1. 检测当前订单是否能够退款; 2. 微信退款操作; 3. 设置订单退款确认状态； 4. 订单商品库存回库。
     * <p>
     * TODO 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤： 1.
     * 管理员登录微信官方支付平台点击退款操作进行退款 2. 管理员登录Dts管理后台点击退款操作进行订单状态修改和商品库存回库
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Transactional
    public Object refund(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String refundMoney = JacksonUtil.parseString(body, "refundMoney");
        if (orderId == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "orderId为空");
        }
        if (StringUtils.isEmpty(refundMoney)) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "refundMoney为空");
        }

        DtsOrder order = orderService.findById(orderId);
        if (order == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "order为空");
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "");
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            return AjaxResult.error("不退款");
        }

        // 微信退款
        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
        // 元转成分
        Integer totalFee = order.getActualPrice().multiply(new BigDecimal(100)).intValue();
        wxPayRefundRequest.setTotalFee(totalFee);
        wxPayRefundRequest.setRefundFee(totalFee);

        /**
         * 为了账号安全，暂时屏蔽api退款 WxPayRefundResult wxPayRefundResult = null; try {
         * wxPayRefundResult = wxPayService.refund(wxPayRefundRequest); } catch
         * (WxPayException e) { e.printStackTrace(); return
         * ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败"); } if
         * (!wxPayRefundResult.getReturnCode().equals("SUCCESS")) { logger.warn("refund
         * fail: " + wxPayRefundResult.getReturnMsg()); return
         * ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败"); } if
         * (!wxPayRefundResult.getResultCode().equals("SUCCESS")) { logger.warn("refund
         * fail: " + wxPayRefundResult.getReturnMsg()); return
         * ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败"); }
         */

        // 设置订单取消状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.info("商场管理->订单管理->订单退款失败:{}", "更新数据已失效");
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<DtsOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (DtsOrderGoods orderGoods : orderGoodsList) {
            Integer productId = orderGoods.getProductId();
            Short number = orderGoods.getNumber();
            if (productService.addStock(productId, number) == 0) {
                logger.info("商场管理->订单管理->订单退款失败:{}", "商品货品库存增加失败");
                throw new RuntimeException("商品货品库存增加失败");
            }
        }

        // TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND,
                new String[]{order.getOrderSn().substring(8, 14)});

        logger.info("【请求结束】商场管理->订单管理->订单退款,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

    /**
     * 发货 1. 检测当前订单是否能够发货 2. 设置订单发货状态
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object ship(String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        String shipSn = JacksonUtil.parseString(body, "shipSn");
        String shipChannel = JacksonUtil.parseString(body, "shipChannel");
        if (orderId == null || shipSn == null || shipChannel == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "惨呼不对");
        }

        DtsOrder order = orderService.findById(orderId);
        if (order == null) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "惨呼不对");
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "惨呼不对");
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            logger.info("商场管理->订单管理->订单发货失败:{}", "更新数据失败!");
            return AjaxResult.error(HttpStatus.ERROR, "更新数据失败");
        }

        // TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户: *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});

        logger.info("【请求结束】商场管理->订单管理->订单发货,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

    /**
     * 回复订单商品
     *
     * @param body 订单信息，{ orderId：xxx }
     * @return 订单操作结果 成功则 { errno: 0, errmsg: '成功' } 失败则 { errno: XXX, errmsg: XXX }
     */
    public Object reply(String body) {
        Integer commentId = JacksonUtil.parseInteger(body, "commentId");
        if (commentId == null || commentId == 0) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "惨呼不对");
        }
        // 目前只支持回复一次
        if (commentService.findById(commentId) != null) {
            return AjaxResult.error("当前订单状态不能退款");
        }
        String content = JacksonUtil.parseString(body, "content");
        if (StringUtils.isEmpty(content)) {
            return AjaxResult.error(HttpStatus.BAD_REQUEST, "惨呼不对");
        }
        // 创建评价回复
        DtsComment comment = new DtsComment();
        comment.setType((byte) 2);
        comment.setValueId(commentId);
        comment.setContent(content);
        comment.setUserId(0); // 评价回复没有用
        comment.setStar((short) 0); // 评价回复没有用
        comment.setHasPicture(false); // 评价回复没有用
        comment.setPicUrls(new String[]{}); // 评价回复没有用
        commentService.save(comment);

        logger.info("【请求结束】商场管理->订单管理->订单商品回复,响应结果:{}", "成功!");
        return AjaxResult.success();
    }

    /**
     * 快递公司列表
     *
     * @return
     */
    public Object listShipChannel() {
        List<Map<String, String>> vendorMaps = expressService.getAllVendor();
        List<Map<String, Object>> shipChannelList = new ArrayList<Map<String, Object>>(vendorMaps == null ? 0 : vendorMaps.size());
        for (Map<String, String> map : vendorMaps) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", map.get("code"));
            b.put("label", map.get("name"));
            shipChannelList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("shipChannelList", shipChannelList);
        logger.info("获取已配置的快递公司总数：{}", shipChannelList.size());
        return AjaxResult.success(data);
    }

}
