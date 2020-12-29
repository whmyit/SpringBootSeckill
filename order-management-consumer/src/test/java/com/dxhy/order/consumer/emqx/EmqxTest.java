package com.dxhy.order.consumer.emqx;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.fusesource.hawtbuf.Buffer.utf8;

/**
 * @author ZSC-DXHY
 * @date 创建时间: 2019/6/27 8:54
 */
public class EmqxTest {
    
    public static void main(String[] args) throws Exception {
        MQTT mqtt = new MQTT();
        mqtt.setConnectAttemptsMax(0);
        mqtt.setReconnectAttemptsMax(0);
        mqtt.setHost("10.1.2.46", 1883);
        
        final BlockingConnection publishConnection = mqtt.blockingConnection();
        publishConnection.connect();
        
        final BlockingConnection subscribeConnection = mqtt.blockingConnection();
        subscribeConnection.connect();
        
        Topic[] topic = {new Topic(utf8("foo"), QoS.EXACTLY_ONCE)};
        byte[] qoses = subscribeConnection.subscribe(topic);
        
        final long start = System.currentTimeMillis();
        final AtomicLong sendCounter = new AtomicLong();
        final AtomicLong receiveCounter = new AtomicLong();
        
        Thread receiver = new Thread("receiver") {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (System.currentTimeMillis() > start + TimeUnit.SECONDS.toMillis(120)) {
                            break;
                        }
                        Thread.sleep(1000);
                        subscribeConnection.receive().ack();
                        receiveCounter.incrementAndGet();
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        receiver.start();
        
        Thread sender = new Thread("sender") {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (System.currentTimeMillis() > start + TimeUnit.SECONDS.toMillis(10)) {
                            break;
                        }
                        Thread.sleep(1000);
                        publishConnection.publish("foo", new byte[1024], QoS.EXACTLY_ONCE, false);
                        sendCounter.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        sender.start();
        
        while (true) {
            if (System.currentTimeMillis() > start + TimeUnit.SECONDS.toMillis(10)) {
                break;
            }
            Thread.sleep(1000);
            System.out.println("Sent: " + sendCounter.get() + ", Received: " + receiveCounter.get());
        }
        
        publishConnection.disconnect();
        subscribeConnection.disconnect();
        System.out.println("111");
        receiver.join();
        sender.join();
    }
    
}
