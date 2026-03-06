package com.example.milkteasystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Getter
@Setter
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 订单号（唯一）
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态（0待支付 1待取餐 2已完成 3已取消）
     */
    private Byte orderStatus;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 取餐人姓名
     */
    private String takeName;

    /**
     * 取餐人电话
     */
    private String takePhone;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;
}
