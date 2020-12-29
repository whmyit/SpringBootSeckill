package com.dxhy.order.rabbitplugin.monitor;

/**
 * 队列状态
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:27
 */
public class PoolStatus {
    private String host;
    private String queueName;
    private boolean lisStatus;
    private int activeConsumerCount;
    private boolean isRunning;
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getQueueName() {
        return queueName;
    }
    
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
    
    public boolean isLisStatus() {
        return lisStatus;
    }
    
    public void setLisStatus(boolean lisStatus) {
        this.lisStatus = lisStatus;
    }
    
    public int getActiveConsumerCount() {
        return activeConsumerCount;
    }
    
    public void setActiveConsumerCount(int activeConsumerCount) {
        this.activeConsumerCount = activeConsumerCount;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
