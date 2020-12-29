package com.dxhy.order.model.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author fankunfeng
 * @Date 2019-06-13 11:06:27
 * @Describe
 */
@Setter
@Getter
public abstract class AbstractMessage {
    /**
     * 主键id
     */
    private String id;
    /**
     * 消息主题
     */
    private String messageTitle;
    /**
     * 消息类型
     */
    private String messageType;
    /**
     * 消息内容
     */
    private String messageContent;
    /**
     * 消息创建时间
     */
    private Date createTime;
    /**
     * 消息创建人
     */
    private String createUserId;
    /**
     * 消息是否已读
     */
    private String status;
    /**
     * 当前页
     */
    private int currentPage;
    /**
     * 页面张数
     */
    private int  pageSize;
}
