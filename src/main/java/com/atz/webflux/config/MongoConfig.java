package com.atz.webflux.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
@RequiredArgsConstructor
public class MongoConfig implements InitializingBean {

    @Lazy
    private final MappingMongoConverter mongoConverter;

    @Override
    public void afterPropertiesSet() {
        mongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null)); //Remove _class como campo en MongoDB
    }
}








