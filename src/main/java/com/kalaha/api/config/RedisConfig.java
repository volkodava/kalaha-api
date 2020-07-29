package com.kalaha.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalaha.api.model.Game;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final ObjectMapper mapper;
    private final RedisConnectionFactory factory;

    public RedisConfig(ObjectMapper mapper, RedisConnectionFactory factory) {
        this.mapper = mapper;
        this.factory = factory;
    }

    @Bean
    public RedisTemplate<String, Game> redisGameTemplate() {
        RedisTemplate<String, Game> template = new RedisTemplate<>();
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Game> jsonSerializationRedisSerializer = new Jackson2JsonRedisSerializer<>(Game.class);
        jsonSerializationRedisSerializer.setObjectMapper(mapper);

        template.setConnectionFactory(factory);

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        template.setValueSerializer(jsonSerializationRedisSerializer);
        template.setHashValueSerializer(jsonSerializationRedisSerializer);

        template.setEnableTransactionSupport(true);
        return template;
    }
}
