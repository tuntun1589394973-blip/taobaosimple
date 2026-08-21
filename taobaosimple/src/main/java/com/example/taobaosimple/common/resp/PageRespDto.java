package com.example.taobaosimple.common.resp;

import java.util.List;

public class PageRespDto<T> {
    private final long total;
    private final List<T> records;

    public PageRespDto(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public long getTotal() {
        return total;
    }
    public List<T> getRecords() {
        return records;
    }

}
