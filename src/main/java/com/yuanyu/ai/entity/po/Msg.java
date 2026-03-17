package com.yuanyu.ai.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Msg {
    MessageType messageType;
    String text;
    Map<String, Object> metadata;

    // 将 Spring AI 的 Message 对象转换为 Msg 对象，提取关键信息用于持久化。
    public Msg(Message message) {
        this.messageType = message.getMessageType();
        this.text = message.getText();
        this.metadata = message.getMetadata();
    }

    // 将 Msg 对象还原为 Spring AI 的 Message 对象，根据不同的消息类型创建相应的具体消息实例。
    public Message toMessage() {
        return switch (messageType) {
            case SYSTEM -> new SystemMessage(text);
            // case USER -> new UserMessage(text, List.of(), metadata);
            case USER -> UserMessage.builder().text(text).metadata(metadata).build();
            // case ASSISTANT -> new AssistantMessage(text, metadata, List.of(), List.of());
            case ASSISTANT -> AssistantMessage.builder().content(text).properties(metadata).build();
            default -> throw new IllegalArgumentException("Unsupported message type: " + messageType);
        };
    }
}