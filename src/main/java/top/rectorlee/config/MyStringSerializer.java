package top.rectorlee.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

/**
 * @author Lee
 * @description 自定义序列化类
 * @date 2023-04-26  15:39:16
 */
@Slf4j
@Component
public class MyStringSerializer implements RedisSerializer<String> {
    private final Charset charset;

    public MyStringSerializer() {
        this(Charset.forName("UTF8"));
    }

    public MyStringSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        String keyPrefix = "";
        String saveKey = new String(bytes, charset);
        int indexOf = saveKey.indexOf(keyPrefix);
        if (indexOf > 0) {
            log.info("key缺少前缀");
        } else {
            saveKey = saveKey.substring(indexOf);
        }
        log.info ("saveKey: {}",saveKey);
        return (saveKey.getBytes () == null ? null : saveKey);
    }

    @Override
    public byte[] serialize(String string) {
        String keyPrefix = "";
        String key = keyPrefix + string;
        log.info ("key: {}, getBytes: {}",key, key.getBytes(charset));
        return (key == null ? null : key.getBytes (charset));
    }
}
