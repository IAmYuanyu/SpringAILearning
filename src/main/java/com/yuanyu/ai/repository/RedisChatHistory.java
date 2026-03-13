package com.yuanyu.ai.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class RedisChatHistory implements ChatHistoryRepository{

    private final StringRedisTemplate redisTemplate;

    private final static String CHAT_HISTORY_KEY_PREFIX = "chat:history:";
    
    private final static String CHAT_ID_TYPE_MAPPING_PREFIX = "chat:id:type:";

    @Override
    public void save(String type, String chatId) {
        redisTemplate.opsForSet().add(CHAT_HISTORY_KEY_PREFIX + type, chatId);
        redisTemplate.opsForValue().set(CHAT_ID_TYPE_MAPPING_PREFIX + chatId, type);
    }

    @Override
    public List<String> getChatIds(String type) {
        // 从 Redis Set 中获取指定类型下的所有会话 ID
        Set<String> chatIds = redisTemplate.opsForSet().members(CHAT_HISTORY_KEY_PREFIX + type);
        if(chatIds == null || chatIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 将会话 ID 按字母顺序排序后转换为列表返回
        return chatIds.stream().sorted(String::compareTo).toList();
    }
    
    @Override
    public String getTypeByChatId(String chatId) {
        return redisTemplate.opsForValue().get(CHAT_ID_TYPE_MAPPING_PREFIX + chatId);
    }
}