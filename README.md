MySQL 与 Redis 如何保证数据一致性？
可以使用 canal 监听 MySQL 的 binlog 日志，当 binlog 日志发生变化时，调用对应的 API 拿到变化后的数据，再使用 MQ 异步发送变化后的消息，消息监听到后，最后去同步 Redis 中的数据，从而达到数据一致的目的。 
