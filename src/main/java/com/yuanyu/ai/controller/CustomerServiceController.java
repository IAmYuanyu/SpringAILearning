package com.yuanyu.ai.controller;

import com.yuanyu.ai.config.ChatClientFactory;
import com.yuanyu.ai.constant.TypeConstants;
import com.yuanyu.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class CustomerServiceController {
    private final ChatClient serviceChatClient;
    // private final ChatClientFactory chatClientFactory;

    private final ChatHistoryRepository chatHistoryRepository;


    @RequestMapping(value = "/service", produces = "text/html;charset=UTF-8")
    public Flux<String> service(@RequestParam String prompt, @RequestParam String chatId) {
        // 保存会话id
        chatHistoryRepository.save(TypeConstants.SERVICE, chatId); // 记得改为TypeConstants.SERVICE
        // 请求模型
        // ChatClient serviceChatClient = chatClientFactory.getServiceChatClient();
        return serviceChatClient.prompt()
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
