package top.rectorlee.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.rectorlee.constant.SystemConstant;

import javax.annotation.PostConstruct;

/**
 * @author Lee
 * @description
 * @date 2023-03-09  22:20:16
 */
@Slf4j
@Configuration
public class RabbitMQConfig {
    @Autowired
    private ConnectionFactory connectionFactory;

    // 项目启动初始化交换机和队列
    @PostConstruct
    public void init() {
        // rabbitAdmin 用于创建交换机、队列,并将其进行绑定
        RabbitAdmin rabbitAdminInsert = new RabbitAdmin(connectionFactory);
        RabbitAdmin rabbitAdminUpdate = new RabbitAdmin(connectionFactory);
        RabbitAdmin rabbitAdminDelete = new RabbitAdmin(connectionFactory);

        // 创建队列(java对象)
        Queue queueInsert = QueueBuilder.durable(SystemConstant.QUEUE_NAME_INSERT).build();
        Queue queueUpdate = QueueBuilder.durable(SystemConstant.QUEUE_NAME_UPDATE).build();
        Queue queueDelete = QueueBuilder.durable(SystemConstant.QUEUE_NAME_DELETE).build();
        // 将java对象的队列放入到rabbitMQ中并创建出队列
        rabbitAdminInsert.declareQueue(queueInsert);
        rabbitAdminUpdate.declareQueue(queueUpdate);
        rabbitAdminDelete.declareQueue(queueDelete);

        // 创建交换机
        Exchange exchangeInsert = ExchangeBuilder.directExchange(SystemConstant.EXCHANGE_NAME_INSERT).durable(true).build();
        Exchange exchangeUpdate = ExchangeBuilder.directExchange(SystemConstant.EXCHANGE_NAME_UPDATE).durable(true).build();
        Exchange exchangeDelete = ExchangeBuilder.directExchange(SystemConstant.EXCHANGE_NAME_DELETE).durable(true).build();
        rabbitAdminInsert.declareExchange(exchangeInsert);
        rabbitAdminUpdate.declareExchange(exchangeUpdate);
        rabbitAdminDelete.declareExchange(exchangeDelete);

        // 将队列绑定到交换机上
        Binding bindingInsert = BindingBuilder.bind(queueInsert).to(exchangeInsert).with(SystemConstant.ROUTING_KEY_INSERT).noargs();
        rabbitAdminInsert.declareBinding(bindingInsert);
        Binding bindingUpdate = BindingBuilder.bind(queueUpdate).to(exchangeUpdate).with(SystemConstant.ROUTING_KEY_UPDATE).noargs();
        rabbitAdminUpdate.declareBinding(bindingUpdate);
        Binding bindingDelete = BindingBuilder.bind(queueDelete).to(exchangeDelete).with(SystemConstant.ROUTING_KEY_DELETE).noargs();
        rabbitAdminDelete.declareBinding(bindingDelete);
    }

    @Bean
    public RabbitTemplate getRabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);

        // 开启消息未到达队列的回调机制
        rabbitTemplate.setMandatory(true);

        // 消息未到达队列的回调(先执行)
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                // 获取消息唯一id
                String msgId = message.getMessageProperties().getCorrelationId();
                log.info(String.format("消息{%s}未到达队列", msgId));
            }
        });

        // 消息未到达交换机时回调机制(需要在application.yml中添加spring.rabbitmq.publisher-confirm-type:correlated)(后执行)
        rabbitTemplate.setConfirmCallback((correlationData, b, s) -> {
            // 获取消息唯一id
            String msgId = correlationData.getId();

            if (b) {
                log.info(String.format("消息{%s}到达交换机", msgId));
            } else {
                log.info(String.format("消息{%s}未到达交换机", msgId));
            }
        });

        return rabbitTemplate;
    }
}
