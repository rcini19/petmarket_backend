package com.dev.petmarket_backend.common.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> content;
    private PageInfo pageInfo;

    public PaginatedResponse(List<T> content, PageInfo pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }
}
