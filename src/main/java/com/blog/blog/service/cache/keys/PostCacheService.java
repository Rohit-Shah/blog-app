package com.blog.blog.service.cache.keys;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.Response.CursorResponse;

import java.util.Optional;

public interface PostCacheService {

    void cachePostResponse(int size,PostSearchCriteria criteria,CursorResponse<PostDTO> posts);

    Optional<CursorResponse<PostDTO>> getCachedPostResponse(int size, PostSearchCriteria criteria);

    void evictPostCache(int size,PostSearchCriteria criteria);

    void evictAllPostsCache();

}
