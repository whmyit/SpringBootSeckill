package com.dxhy.order.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.utils.Base64Encoding;
import com.dxhy.order.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author xueanna
 * @Date 2019/7/3 19:05
 */
@Slf4j
public class MqttPushClient {

    private static MqttClient client;

    public static MqttClient getClient() {
        return client;
    }

    public static void setClient(MqttClient client) {
        MqttPushClient.client = client;
    }

    public void connect(String host, String username, String password, int timeout, int keepalive) {
        try {
            //客户端id随机生成
            String clientId = Constant.CLIENT_PREFIX + System.currentTimeMillis();
            client = new MqttClient(host, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(timeout);
            options.setKeepAliveInterval(keepalive);
            options.setCleanSession(true);
            try {
                client.connect(options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发布，默认qos为0，非持久化
     *
     * @param topic
     * @param pushMessage
     */
    public int publish(String topic, String pushMessage) {
        return publish(1, false, topic, pushMessage);
    }

    /**
     * 发布
     *
     * @param qos
     * @param retained
     * @param topic
     * @param pushMessage
     */
    public int publish(int qos, boolean retained, String topic, String pushMessage) {
        int reasonCode = 0;
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
        MqttTopic mTopic = client.getTopic(topic);
        if (null == mTopic) {
            log.error("topic not exist");
        }
        MqttDeliveryToken token = null;
        try {
            token = mTopic.publish(message);
            token.waitForCompletion();
        } catch (MqttPersistenceException e) {
            reasonCode = e.getReasonCode();
            e.printStackTrace();
        } catch (MqttException e) {
            reasonCode = e.getReasonCode();
            e.printStackTrace();
        }
        return reasonCode;
    }

    /**
     * 订阅某个主题，qos默认为0
     *
     * @param topic
     */
    public void subscribe(String topic) {
        subscribe(topic, 0);
    }

    /**
     * 订阅某个主题
     *
     * @param topic
     * @param qos
     */
    public void subscribe(String topic, int qos) {
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消订阅某个主题
     *
     * @param topic
     */
    public void unsubscribe(String topic) {
        try {
            client.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public MqttTopic test(String topic) {
        MqttTopic topic1 = client.getTopic(topic);
        return topic1;
    }

    public boolean getSubscriptions(String topic) {
        boolean flag = false;

        try {
            //判断mqttTopic是否被订阅，如果没有订阅，则不发送消息
            String base64ClientCredentials = Base64Encoding.encode(OpenApiConfig.mqttUserName + ":" + OpenApiConfig.mqttPassword);
            Map<String, String> headMap = new HashMap<>(2);
            headMap.put("Content-Type", ContentType.APPLICATION_JSON.toString());
            headMap.put(ConfigureConstant.AUTHORIZATION, ConfigureConstant.BASIC + " " + base64ClientCredentials);
            String result = HttpUtils.doGetWithHeader(OpenApiConfig.mqttSubscriptions, null, headMap);
            if (!ObjectUtils.isEmpty(result)) {
                log.info("获取客户端订阅列表，返回结果为：{}", result);
                JSONObject obj = JSONObject.parseObject(result);
                if (ConfigureConstant.STRING_0.equals(obj.getString("code"))) {
                    JSONArray data = obj.getJSONArray("data");
                    for (Object o : data) {
                        JSONObject jsonObject = (JSONObject) o;
                        if (topic.equals(jsonObject.getString("topic"))) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取mqtt订阅列表异常", e);
        }

        return flag;
    }

}
