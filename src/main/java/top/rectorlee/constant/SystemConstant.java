package top.rectorlee.constant;

/**
 * @author Lee
 * @description
 * @date 2023-03-10  0:31:38
 */
public class SystemConstant {
    // 交换机名称
    public static final String EXCHANGE_NAME_INSERT = "rabbitMQ-exchange-insert";
    public static final String EXCHANGE_NAME_UPDATE = "rabbitMQ-exchange-update";
    public static final String EXCHANGE_NAME_DELETE = "rabbitMQ-exchange-delete";

    // 队列名
    public static final String QUEUE_NAME_INSERT = "rabbitMQ-queue-insert";
    public static final String QUEUE_NAME_UPDATE = "rabbitMQ-queue-update";
    public static final String QUEUE_NAME_DELETE = "rabbitMQ-queue-delete";

    // 路由key
    public static final String ROUTING_KEY_INSERT = "rabbitMQ-key-insert";
    public static final String ROUTING_KEY_UPDATE = "rabbitMQ-key-update";
    public static final String ROUTING_KEY_DELETE = "rabbitMQ-key-delete";

    // redis key的前缀
    public static final String REDIS_KEY_PREFIX = "userNo-";

    // 消息类型
    public static final int MESSAGE_TYPE_INSERT = 1;
    public static final int MESSAGE_TYPE_UPDATE = 2;
    public static final int MESSAGE_TYPE_DELETE = 3;
}
