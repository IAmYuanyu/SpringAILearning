package com.yuanyu.ai.controller;

import com.yuanyu.ai.config.ChatClientFactory;
import com.yuanyu.ai.constant.TypeConstants;
import com.yuanyu.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat", produces = "text/html;charset=UTF-8")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam String chatId,
                             @RequestParam(required = false) List<MultipartFile> files) {
        // 保存会话id
        chatHistoryRepository.save(TypeConstants.CHAT, chatId);

        // 请求模型
        if (files != null && !files.isEmpty()) {
            // 上传了文件，多模态聊天
            return multiModelChat(prompt, chatId, files);
        } else {
            // 没有上传文件，则普通聊天
            return normalChat(prompt, chatId);
        }
    }

    private Flux<String> multiModelChat(String prompt, String chatId, List<MultipartFile> files) {
        // 解析文件
        List<Media> medias = files.stream()
                .map(file -> new Media(
                        MimeType.valueOf(file.getContentType()), file.getResource()
                        )
                ).toList();
        // 发送请求
        return chatClient.prompt()
                .user(p -> p.text(prompt).media(medias.toArray(Media[]::new))) // 添加多模态内容
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    private Flux<String> normalChat(String prompt, String chatId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}
