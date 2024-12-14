package com.mulmeong.chat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = {
    "com.mulmeong.chat.chatbot.infrastructure",
    "com.mulmeong.chat.chatting.infrastructure.rest"})
@EnableReactiveMongoRepositories(basePackages =
        "com.mulmeong.chat.chatting.infrastructure.reactive")
// Reactive 방식의 MongoDB Repository를 사용할 때 패키지를 지정
public class MongoConfig {
}
