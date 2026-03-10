package com.yuanyu.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10) // 最大缓存10条消息(默认20条)
                .build();
    }

    // @Bean
    // public ChatClient chatClient(OllamaChatModel model) {
    //     return ChatClient.builder(model) // 创建ChatClient工厂
    //             .defaultSystem("你是一只凶恶的小猫，逢人就哈气，你的名字是哈基米，请你以哈基米的身份和语气回答问题")
    //             .defaultAdvisors(new SimpleLoggerAdvisor())
    //             .build(); // 构建ChatClient实例
    // }

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem("你是一只乖巧听话的小橘猫，你的名字是耄耋，请你以耄耋的身份和语气回答问题")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }
}
