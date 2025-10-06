package com.blog.blog.service.RedisService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public <T> T get(String key,TypeReference<T> typeReference){
        try{
            String cachedData = redisTemplate.opsForValue().get(key);
            if(cachedData != null){
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                return mapper.readValue(cachedData,typeReference);
            }
        }catch (Exception e){
            log.error("Exception occurred " + e.getMessage());
        }
        return null;
    }

    //we cannot directly store list<post> as redis store data as string so before storing we
    //need to serialize the list<post> and while retrieving we need to deserialize it
    public void set(String key,Object o,Long ttl){
        try{
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String json = mapper.writeValueAsString(o);
            redisTemplate.opsForValue().set(key,json,ttl, TimeUnit.SECONDS);
        }catch (JsonParseException e){
            log.error("Error while parsing the data" + e.getMessage());
        }
        catch (Exception e){
            log.error("Exception occurred : " + e.getMessage());
        }
    }

    public void resetCacheOfKeys(List<String> keys){
        for(String key : keys){
            //reset only if the key exists
            if(redisTemplate.opsForValue().get(key) != null){
                redisTemplate.delete(key);
            }
        }
    }
}
