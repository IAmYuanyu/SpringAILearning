package com.yuanyu.ai.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuanyu.ai.entity.po.Msg;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
public class RedisChatMemory implements ChatMemory {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private final static String PREFIX = "chat:memory:";

    /**
     * 添加单条消息
     */
    @Override
    public void add(String conversationId, Message message) {
        // 将 Message 转换为 JSON 字符串
        Msg msg = new Msg(message);
        String jsonMsg = null;
        try {
            jsonMsg = objectMapper.writeValueAsString(msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("消息序列化失败", e);
        }
        // 存入 Redis 列表
        redisTemplate.opsForList().rightPush(PREFIX + conversationId, jsonMsg);
    }

    /**
     * 核心方法：批量添加会话消息到 Redis
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 空值校验
        if (messages == null || messages.isEmpty()) {
            return;
        }
        // 将 Message 列表转换为 JSON 字符串列表
        List<String> list = messages.stream().map(Msg::new).map(msg -> {
            try {
                return objectMapper.writeValueAsString(msg);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("消息序列化失败", e);
            }
        }).toList();
        // 存入 Redis 列表
        redisTemplate.opsForList().rightPushAll(PREFIX + conversationId, list);
    }

    /**
     * 适配接口：获取指定会话的全部历史消息
     */
    @Override
    public List<Message> get(String conversationId) {
        // Redis list range 操作中，end=-1 表示读取从 0 到最后一个元素（全部消息）
        List<String> list = redisTemplate.opsForList().range(PREFIX + conversationId, 0, -1);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        // 将 JSON 字符串反序列化为 Message 列表
        return list.stream().map(s -> {
            try {
                return objectMapper.readValue(s, Msg.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("消息反序列化失败", e);
            }
        }).map(Msg::toMessage).toList();
    }

    /**
     * 清空指定会话的所有消息
     */
    @Override
    public void clear(String conversationId) {
        redisTemplate.delete(PREFIX + conversationId);
    }

    /**
     * 读取指定会话的最后 N 条消息
     * @param conversationId
     * @param lastN
     * @return
     */
    public List<Message> get(String conversationId, int lastN) {
        List<String> list = redisTemplate.opsForList().range(PREFIX + conversationId, 0, lastN);
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        return list.stream().map(s -> {
            try {
                return objectMapper.readValue(s, Msg.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("消息反序列化失败", e);
            }
        }).map(Msg::toMessage).toList();
    }
}