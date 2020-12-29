package com.dxhy.order.rabbitplugin.core;

import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.util.Assert;
/**
 * 消息队列连接池
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 14:41
 */
public class ConnectionForFactory {
    
    public static Connection getConn(ConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "rabbitmq connectionFactory is null");
        Connection conn = connectionFactory.createConnection();
        Assert.state(conn.isOpen(), "have connection is closed");
        return conn;
    }
    
}
