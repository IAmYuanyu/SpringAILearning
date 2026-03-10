package com.yuanyu.ai.controller;

import com.yuanyu.ai.entity.vo.MessageVO;
import com.yuanyu.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ai/history")
@RequiredArgsConstructor
public class ChatHistoryController {
    private final ChatHistoryRepository chatHistoryRepository;

    private final ChatMemory chatMemory;

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    @GetMapping("/{type}")
    public List<String> chat(@PathVariable("type") String type) {
        return chatHistoryRepository.getChatIds(type);
    }

    /**
     * 获取会话历史消息详情
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     * @return 会话消息列表
     */
    @GetMapping("/{type}/{chatId}")
    public List<MessageVO> chat(@PathVariable("type") String type, @PathVariable("chatId") String chatId) {
        List<Message> messages = chatMemory.get(chatId);
        if (messages == null) {
            return new ArrayList<>();
        }
        return messages.stream().map(MessageVO::new).toList();
    }
}
