package com.example.milkteasystem.dto;

import lombok.Data;

/**
 * 分页请求DTO
 */
@Data
public class PageRequestDTO {
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
}
