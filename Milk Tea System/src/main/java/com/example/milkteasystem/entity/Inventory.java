package com.example.milkteasystem.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 库存表
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Getter
@Setter
public class Inventory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 库存ID
     */
    @TableId(value = "inventory_id", type = IdType.AUTO)
    private Long inventoryId;

    /**
     * 商品ID（一对一关联）
     */
    private Long productId;

    /**
     * 当前库存数量
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer sold;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
