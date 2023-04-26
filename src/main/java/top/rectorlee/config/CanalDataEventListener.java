package top.rectorlee.config;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xpand.starter.canal.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import top.rectorlee.constant.SystemConstant;
import top.rectorlee.entity.User;

import java.util.List;

/**
 * @author Lee
 * @description Canal 监听类
 * @date 2023-04-26  10:33:19
 */
@Slf4j
@CanalEventListener
public class CanalDataEventListener {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 监听增加数据
    @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        List<CanalEntry.Column> columnsList = rowData.getAfterColumnsList();

        int id =Integer.valueOf(columnsList.get(0).getValue());
        String name = columnsList.get(1).getValue();
        String nickName = columnsList.get(2).getValue();
        User user = User.builder().id(id).name(name).nickName(nickName).build();
        log.info("新增数据为: {}", user);

        // rabbitMQ 发送消息
        sendMessage(id, user, SystemConstant.MESSAGE_TYPE_INSERT);
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

        // rabbitMQ 发送消息
        sendMessage(id, newUser, SystemConstant.MESSAGE_TYPE_UPDATE);
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

        // rabbitMQ 发送消息
        sendMessage(id, user,  SystemConstant.MESSAGE_TYPE_DELETE);
    }

    private void sendMessage(int id, User user, int type) {
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(SystemConstant.REDIS_KEY_PREFIX + id);
        String msg = JSONObject.toJSONString(user);
        // 消息持久化
        MessageProperties messageProperties = MessagePropertiesBuilder.newInstance().setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        org.springframework.amqp.core.Message mqMessage = new org.springframework.amqp.core.Message(msg.getBytes(), messageProperties);
        if (SystemConstant.MESSAGE_TYPE_INSERT == type) {
            rabbitTemplate.send(SystemConstant.EXCHANGE_NAME_INSERT, SystemConstant.ROUTING_KEY_INSERT, mqMessage, correlationData);
        } else if (SystemConstant.MESSAGE_TYPE_UPDATE == type) {
            rabbitTemplate.send(SystemConstant.EXCHANGE_NAME_UPDATE, SystemConstant.ROUTING_KEY_UPDATE, mqMessage, correlationData);
        } else {
            rabbitTemplate.send(SystemConstant.EXCHANGE_NAME_DELETE, SystemConstant.ROUTING_KEY_DELETE, mqMessage, correlationData);
        }

        log.info("发送消息 {} 到MQ中", msg);
    }
}
