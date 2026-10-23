package com.chiaya.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果结构：{ "records": [], "total": 0, "page": 1, "pageSize": 10 }
 *
 * @param <T> 记录类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 当前页数据 */
    private List<T> records;

    /** 记录总数 */
    private Long total;

    /** 当前页码，从 1 开始 */
    private Integer page;

    /** 每页条数 */
    private Integer pageSize;

    public PageResult(List<T> records , Long total) {
        this.records = records;
        this.total = total;
    }
}
