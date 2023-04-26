package top.rectorlee.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.rectorlee.constant.SystemConstant;
import top.rectorlee.entity.User;
import top.rectorlee.mapper.UserMapper;
import top.rectorlee.service.UserService;

/**
 * @author Lee
 * @description
 * @date 2023-04-26  10:54:12
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addUser(User user) {
        userMapper.addUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }

    @Override
    public User findUser(Integer id) {
        String redisKey = SystemConstant.REDIS_KEY_PREFIX + id;
        String redisValue = (String) redisTemplate.opsForValue().get(redisKey);
        if (redisValue == null) {
            User user= userMapper.findUser(id);
            String value = JSONObject.toJSONString(user);

            redisTemplate.opsForValue().set(redisKey, value);
            log.info("redis add key: {}, value: {}", redisKey, value);

            return user;
        }

        User user = JSONObject.parseObject(redisValue, User.class);

        return user;
    }
}
