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
 * 门店表
 * </p>
 *
 * @author 韦宇翔
 * @since 2026-02-27
 */
@Getter
@Setter
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @TableId(value = "store_id", type = IdType.AUTO)
    private Long storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 营业时间（如：10:00-22:00）
     */
    private String businessHours;

    /**
     * 状态（0停用 1启用）
     */
    private Byte status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
