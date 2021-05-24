package top.chao58.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String object2Json(Object object) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
            log.debug("object2Json：{} ===>>> {}", object, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static <T> T json2Object(String json, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(json, clazz);
            log.debug("json2Object：{} ===>>> {}", json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T json2Object(Map<?, ?> json, Class<T> clazz) {
        T t = null;
        try {
            t = objectMapper.readValue(object2Json(json), clazz);
            log.debug("json2Object：{} ===>>> {}", json, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }


}
