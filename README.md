spring boot 整合 canal，使用 canal 监听 mysql 的 binlog 日志，当数据库发生增删改时，canal 监听到 binlog 日志变化后，调用对应的方法，然后去同步修改 redis 中的数据，从而实现 mysql 与 redis 数据一致。
正确的步骤应该是在 canal 监听到 mysql 的 binlog 日志发生变化时，拿到变化后的数据，使用 MQ 发送消息，在监听消息时，再去同步更新 redis 中的数据，从而实现 mysql 与 redis 数据一致。(demo 中少了使用 MQ 的步骤，可以加上)
