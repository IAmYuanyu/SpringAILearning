package com.yuanyu.ai.controller;

import com.yuanyu.ai.config.ChatClientFactory;
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
public class GameController {
    // private final ChatClient gameChatClient;
    private final ChatClientFactory chatClientFactory;

    @RequestMapping(value = "/game", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam String chatId) {
        ChatClient gameChatClient = chatClientFactory.getGameChatClient();
        return gameChatClient.prompt()
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
