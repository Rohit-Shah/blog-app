package com.blog.blog.service.serviceBean.cachebean.postcachebean;

import com.blog.blog.DTO.PostRequest.PostDTO;
import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.Response.CursorResponse;
import com.blog.blog.service.cache.keys.PostCacheService;
import com.blog.blog.service.cache.keys.PostKeyGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheServiceBean implements PostCacheService {

    private final RedisTemplate<String,Object> redisTemplate;
    private final PostKeyGenerator keyGenerator;
    @Value("${cache.post.ttl-mins}")
    private int CACHE_TTL;

    @Override
    public void cachePostResponse(int size,PostSearchCriteria criteria,CursorResponse<PostDTO> posts) {
        String key = keyGenerator.generateCacheKey(size,criteria);
        try{
            redisTemplate.opsForValue().set(key,posts,CACHE_TTL, TimeUnit.MINUTES);
            log.debug("Post response cached for key {} ",key);
        }catch (Exception e){
            log.error("Error while saving post cache {} ",e.getMessage());
        }
    }

    @Override
    public Optional<CursorResponse<PostDTO>> getCachedPostResponse(int size,PostSearchCriteria criteria) {
        String key = keyGenerator.generateCacheKey(size,criteria);
        try{
            Object response = redisTemplate.opsForValue().get(key);
            if(response != null){
                log.debug("Found post cached data for key {} ",key);
                return Optional.of((CursorResponse<PostDTO>)response);
            }
        }catch (Exception e){
            log.error("Error while fetching post cache value {} ",e.getMessage());
        }
        log.debug("No cached data found for post for key {} ",key);
        return Optional.empty();
    }

    @Override
    public void evictPostCache(int size, PostSearchCriteria criteria) {
        String key = keyGenerator.generateCacheKey(size,criteria);
        try{
            redisTemplate.delete(key);
            log.debug("Evicted cache for key {} ",key);
        }catch (Exception e){
            log.error("Error while evicting cache for key {} ",key);
        }
    }

    @Override
    public void evictAllPostsCache(){
        String pattern = keyGenerator.getPostCachePrefix() + "*";
        try{
            ScanOptions options = ScanOptions.scanOptions()
                    .match(pattern)
                    .count(100)
                    .build();
            List<String> keysToDelete = new ArrayList<>();

            try(Cursor<String> cursor = redisTemplate.scan(options)){
                cursor.forEachRemaining(keysToDelete::add);
            }

            if(!keysToDelete.isEmpty()){
                redisTemplate.delete(keysToDelete);
                log.debug("Evicted {} post cache entries ",keysToDelete.size());
            }
        }catch (Exception e){
            log.error("Error while evicting post cache");
        }
    }
}
