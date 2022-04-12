package com.qfedu.demo.model;

import java.util.List;

public class RespPageBean {
    private Long total;
    private List<?> rows;

    public RespPageBean(Long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public RespPageBean() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
