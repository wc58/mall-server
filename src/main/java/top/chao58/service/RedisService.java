package top.chao58.service;

public interface RedisService {

    void set(String key, String value);

    String get(String key);

    Boolean delete(String key);

    Boolean expire(String key, long expire);

    Long increment(String key, Long stepSize);

}
