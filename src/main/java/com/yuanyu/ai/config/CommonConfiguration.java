package com.yuanyu.ai.config;

import com.yuanyu.ai.constant.SystemConstants;
import com.yuanyu.ai.tool.CourseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
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

    @Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    /**
     * 普通的聊天模型，使用Ollama本地部署
     * @param model
     * @param chatMemory
     * @return
     */
    // @Bean
    // public ChatClient chatClient(OllamaChatModel model) {
    //     return ChatClient.builder(model) // 创建ChatClient工厂
    //             .defaultSystem("你是一只凶恶的小猫，逢人就哈气，你的名字是哈基米，请你以哈基米的身份和语气回答问题")
    //             .defaultAdvisors(new SimpleLoggerAdvisor())
    //             .build(); // 构建ChatClient实例
    // }

    /**
     * 普通的聊天模型，调用云模型
     * @param model
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())
                .defaultSystem("你是一只乖巧听话的小橘猫，你的名字是耄耋，请你以耄耋的身份和语气回答问题")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 哄哄模拟器
     * @param model
     * @param chatMemory
     * @return
     */
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 黑马客服模拟器
     * @param model
     * @param chatMemory
     * @param courseTools
     * @return
     */
    @Bean
    public ChatClient serviceChatClient(
            OpenAiChatModel model,
            ChatMemory chatMemory,
            CourseTools courseTools) {
        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.CUSTOMER_SERVICE_SYSTEM)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(), // 确保有对话记忆
                        new SimpleLoggerAdvisor())
                .defaultTools(courseTools) // 添加工具
                .build();
    }

    /**
     * PDF问答
     * @param model
     * @param chatMemory
     * @param vectorStore
     * @return
     */
    @Bean
    public ChatClient pdfChatClient(
            OpenAiChatModel model,
            ChatMemory chatMemory,
            VectorStore vectorStore) {
        return ChatClient.builder(model)
                .defaultSystem("请根据提供的上下文回答问题，不要自己猜测。")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(), // 确保有对话记忆
                        new SimpleLoggerAdvisor(),
                        QuestionAnswerAdvisor.builder(vectorStore) // 添加向量库问答工具
                                .searchRequest(SearchRequest.builder() // 向量检索的请求参数
                                        .similarityThreshold(0.5d) // 相似度阈值
                                        .topK(2) // 返回的文档片段数量
                                        .build())
                                .build()
                )
                .build();
    }
}
