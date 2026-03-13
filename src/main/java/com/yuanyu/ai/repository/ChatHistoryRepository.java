package com.yuanyu.ai.repository;

import java.util.List;

public interface ChatHistoryRepository {
    /**
     * 保存会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话 ID
     */
    void save(String type, String chatId);

    /**
     * 获取会话 ID 列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话 ID 列表
     */
    List<String> getChatIds(String type);
    
    /**
     * 根据会话 ID 获取业务类型
     * @param chatId 会话 ID
     * @return 业务类型
     */
    String getTypeByChatId(String chatId);
}
