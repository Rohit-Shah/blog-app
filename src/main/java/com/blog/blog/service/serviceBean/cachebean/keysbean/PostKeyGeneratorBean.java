package com.blog.blog.service.serviceBean.cachebean.keysbean;

import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.service.cache.keys.PostKeyGenerator;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class PostKeyGeneratorBean implements PostKeyGenerator {

    private static final String POST_CACHE_PREFIX = "post";

    @Override
    public String getPostCachePrefix(){
        return POST_CACHE_PREFIX;
    }

    @Override
    public String generateCacheKey(int size,PostSearchCriteria criteria) {
        Map<String,String> params = new TreeMap<>();
        if(criteria.getCategory() != null) params.put("category",criteria.getCategory().name());
        if(criteria.getCategory() != null) params.put("content",criteria.getContent());
        if(criteria.getCategory() != null) params.put("cursor",criteria.getCursor());
        if(criteria.getCategory() != null) params.put("category",criteria.getCategory().name());
        String paramString = params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(":"));
        return POST_CACHE_PREFIX + ":" + paramString;
    }
}
