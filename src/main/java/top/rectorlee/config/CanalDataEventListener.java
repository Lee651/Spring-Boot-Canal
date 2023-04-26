package top.rectorlee.config;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import top.rectorlee.entity.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee
 * @description Canal 监听类
 * @date 2023-04-26  10:33:19
 */
@Slf4j
@CanalEventListener
public class CanalDataEventListener {
    @Autowired
    private RedisTemplate redisTemplate;

    // 监听增加数据
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();

        int id =Integer.valueOf(columnsList.get(0).getValue());
        String name = columnsList.get(1).getValue();
        String nickName = columnsList.get(2).getValue();
        User user = User.builder().id(id).name(name).nickName(nickName).build();
        log.info("新增数据为: {}", user);

        String redisKey = "userNo:" + id;
        String redisValue = JSONObject.toJSONString(user);
        redisTemplate.opsForValue().set(redisKey, redisValue);
        log.info("redis add key: {}, value: {}",  redisKey, redisValue);
    }

    // 监听修改数据
    @UpdateListenPoint
    public void onEventUpdate(CanalEntry.RowData rowData) {
        // 修改前数据
        List<CanalEntry.Column> oldColumnsList = rowData.getBeforeColumnsList();
        int oldId = Integer.valueOf(oldColumnsList.get(0).getValue());
        String oldName = oldColumnsList.get(1).getValue();
        String oldNickName = oldColumnsList.get(2).getValue();
        User oldUser = User.builder().id(oldId).name(oldName).nickName(oldNickName).build();
        log.info("修改前数据为: {}", oldUser);

        // 修改后数据
        List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();
        int id = Integer.valueOf(columnsList.get(0).getValue());
        String newName = columnsList.get(1).getValue();
        String newNickName = columnsList.get(2).getValue();
        User newUser = User.builder().id(id).name(newName).nickName(newNickName).build();
        log.info("修改后的数据为: {}", newUser);

        String redisKey = "userNo:" + id;
        String redisValue = JSONObject.toJSONString(newUser);
        redisTemplate.opsForValue().set(redisKey, redisValue);
        log.info("redis update key: {}, value: {}", redisKey, redisValue);
    }

    // 监听删除数据
    @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> columnsList = rowData.getBeforeColumnsList();
        int id = Integer.valueOf(columnsList.get(0).getValue());
        String name = columnsList.get(1).getValue();
        String nickName = columnsList.get(2).getValue();
        User user = User.builder().id(id).name(name).nickName(nickName).build();
        log.info("删除前的数据为: {}", user);

        String redisKey = "userNo:" + id;
        String redisValue = JSONObject.toJSONString(user);
        redisTemplate.opsForValue().set(redisKey, redisValue, RandomUtil.randomInt(1, 5), TimeUnit.SECONDS);
        log.info("redis delete key: {}, value: {}",  redisKey, redisValue);
    }
}
