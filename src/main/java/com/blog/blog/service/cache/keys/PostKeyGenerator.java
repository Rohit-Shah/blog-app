package com.blog.blog.service.cache.keys;

import com.blog.blog.DTO.PostRequest.PostSearchCriteria;

public interface PostKeyGenerator{

    String getPostCachePrefix();
    String generateCacheKey(int size,PostSearchCriteria criteria);

}
