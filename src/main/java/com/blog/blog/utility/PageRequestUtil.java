package com.blog.blog.utility;

import com.blog.blog.Request.PaginationRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageRequestUtil {

    public static Pageable getPageableRequest(PaginationRequest paginationRequest){
        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize(),paginationRequest.getDirection(),paginationRequest.getSortField());
    }

}
