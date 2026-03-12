package com.yuanyu.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.List;


@SpringBootTest
public class MDReaderTests {

    @Autowired
    private VectorStore vectorStore;

    @Test
    public void testMDReader() {
        Resource resource = new FileSystemResource("temp/demo.md");

        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .withAdditionalMetadata("filename", "demo.md")
                .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);

        List<Document> documents = reader.get();

        // 写入向量库
        vectorStore.add(documents);

        SearchRequest request = SearchRequest.builder()
                .query("1表示什么")
                .topK(1) // 搜索结果数量
                // .similarityThreshold(0.6) // 相似度阈值
                .build();

        List<Document> docs = vectorStore.similaritySearch(request);
        if (docs == null) {
            System.out.println("没有搜索到任何内容");
            return;
        }
        for (Document doc : docs) {
            System.out.println(doc);
            System.out.println(doc.getId());
            System.out.println(doc.getScore());
            System.out.println(doc.getText());
        }

    }
}
