package top.rectorlee.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.rectorlee.constant.SystemConstant;
import top.rectorlee.entity.User;

/**
 * @author Lee
 * @description RabbitMQ 监听类
 * @date 2023-04-26  18:56:36
 */
@Slf4j
@Component
public class RabbitMQListener {
    @Autowired
    private RedisTemplate redisTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "true", autoDelete = "false", exclusive = "false", name = SystemConstant.QUEUE_NAME_INSERT),
            exchange = @Exchange(name = SystemConstant.EXCHANGE_NAME_INSERT),
            key = SystemConstant.ROUTING_KEY_INSERT
    ))
    public void addRedis(String message) {
        log.info("add redis start");

        // 反序列化
        User user = JSONObject.parseObject(message, User.class);
        String redisKey = SystemConstant.REDIS_KEY_PREFIX +  user.getId();
        redisTemplate.opsForValue().set(redisKey, message);

        log.info("add redis end");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "true", autoDelete = "false", exclusive = "false", name = SystemConstant.QUEUE_NAME_UPDATE),
            exchange = @Exchange(name = SystemConstant.EXCHANGE_NAME_UPDATE),
            key = SystemConstant.ROUTING_KEY_UPDATE
    ))
    public void updateRedis(String message) {
        log.info("Update redis start");

        // 反序列化
        User user = JSONObject.parseObject(message, User.class);
        String redisKey = SystemConstant.REDIS_KEY_PREFIX +  user.getId();
        redisTemplate.opsForValue().set(redisKey, message);

        log.info("Update redis end");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(durable = "true", autoDelete = "false", exclusive = "false", name = SystemConstant.QUEUE_NAME_DELETE),
            exchange = @Exchange(name = SystemConstant.EXCHANGE_NAME_DELETE),
            key = SystemConstant.ROUTING_KEY_DELETE
    ))
    public void deleteRedis(String message) {
        log.info("delete redis start");

        // 反序列化
        User user = JSONObject.parseObject(message, User.class);
        String redisKey = SystemConstant.REDIS_KEY_PREFIX +  user.getId();
        // 直接调用delete()删除key
        redisTemplate.delete(redisKey);
        // 将key设置简短的过期时间
        // redisTemplate.opsForValue().set(redisKey, message, RandomUtil.randomInt(1, 5), TimeUnit.SECONDS);

        log.info("delete redis end");
    }
}
