package com.kalaha.api;

import com.kalaha.api.model.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = {BaseItTests.Initializer.class})
public abstract class BaseItTests {
    @Container
    private static final GenericContainer redisContainer = new GenericContainer("redis")
            .withExposedPorts(6379);

    @Autowired
    private RedisTemplate<String, Game> template;

    @BeforeEach
    @AfterEach
    public void setUp() {
        template.execute((RedisConnection connection) -> {
            connection.flushDb();
            return "OK";
        });
    }

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.redis.port=" + redisContainer.getFirstMappedPort(),
                    "spring.redis.host=" + redisContainer.getHost()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
