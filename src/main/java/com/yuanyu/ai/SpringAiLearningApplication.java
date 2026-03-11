package com.yuanyu.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.yuanyu.ai.mapper")
@SpringBootApplication
public class SpringAiLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAiLearningApplication.class, args);
	}

}
