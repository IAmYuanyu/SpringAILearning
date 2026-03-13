package com.yuanyu.ai.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Component
// public class InMemoryChatHistoryRepository implements ChatHistoryRepository {
//
//     private final Map<String, List<String>> chatHistory = new HashMap<>();
//
//     @Override
//     public void save(String type, String chatId) {
//         // // 先判断是否有某种类型的会话历史
//         // if (!chatHistory.containsKey(type)) {
//         //     // 没有就创建
//         //     chatHistory.put(type, new ArrayList<>());
//         // }
//         // // 获取该类型的会话历史
//         // List<String> chatIds = chatHistory.get(type);
//         // 上方代码可以简化为下面一行代码：
//         List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());
//
//
//         // 判断该会话历史中是否已经存在该会话
//         if (!chatIds.contains(chatId)) {
//             // 没有就添加
//             chatIds.add(chatId);
//         }
//     }
//
//     @Override
//     public List<String> getChatIds(String type) {
//         // List<String> chatIds = chatHistory.get(type);
//         // return chatIds != null ? chatIds : new ArrayList<>();
//         // 上方代码可以简化为下面一行代码：
//         return chatHistory.getOrDefault(type, List.of());
//     }
// }
