package com.yuanyu.ai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChatClientFactory {

    private final Map<String, ChatClient> chatClientMap;

    // 提供静态常量，避免硬编码Bean名称
    public static final String NORMAL_CHAT_CLIENT = "chatClient";
    public static final String GAME_CHAT_CLIENT = "gameChatClient";

    // 获取指定类型的ChatClient
    public ChatClient getChatClient(String clientType) {
        ChatClient client = chatClientMap.get(clientType);
        if (client == null) {
            throw new IllegalArgumentException("不支持的客户端类型：" + clientType);
        }
        return client;
    }

    // 提供语义化方法，更友好
    public ChatClient getNormalChatClient() {
        return getChatClient(NORMAL_CHAT_CLIENT);
    }

    public ChatClient getGameChatClient() {
        return getChatClient(GAME_CHAT_CLIENT);
    }

}
