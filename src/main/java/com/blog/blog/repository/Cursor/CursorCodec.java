package com.blog.blog.repository.Cursor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CursorCodec {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String encodeCursor(Cursor cursor) throws JsonProcessingException {
        Map<String,Object> map = new HashMap<>();
        map.put("createdAt",cursor.getCreatedAt().getTime());
        map.put("id",cursor.getId());
        String json = MAPPER.writeValueAsString(map);
        return Base64.getUrlEncoder().encodeToString(json.getBytes());
    }

    public static Cursor decodeCursor(String cursor) throws JsonProcessingException {
        String json = new String(Base64.getDecoder().decode(cursor));
        Map<String,Object> map = MAPPER.readValue(json,Map.class);
        Long createdAtValue = ((Number)map.get("createdAt")).longValue();
        Long id = ((Number)map.get("id")).longValue();
        return new Cursor(
                new Date(createdAtValue),
                Long.valueOf(id));
    }

}
