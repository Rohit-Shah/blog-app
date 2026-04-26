package com.blog.blog.Response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.Collection;

@Getter
@Setter
@Builder
public class PageResponse<T> {
    private Collection<T> content;
    private Integer totalPages;
    private long totalElements;
    private Integer size;
    private Integer page;
    private boolean first;
    private boolean last;
    private boolean empty;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .build();
    }
}
