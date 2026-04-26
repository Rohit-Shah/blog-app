package com.blog.blog.Response;

import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorResponse<T> {
    private Collection<T> response;
    private String nextCursor;
    private boolean hasNext;
    private int size;

    public static <T> CursorResponse<T> from(Collection<T> content,String nextCursor,boolean hasNext,int size){
        return CursorResponse.<T>builder().response(content)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .size(size).build();
    }

}
