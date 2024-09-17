package com.oneswap.config;

import com.oneswap.service.LiquiditySubscriber;
import com.oneswap.service.RecordSubscriber;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SslOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.timeout}")
    private Integer timeout;

    @Value("${redis.ssl.enable}")
    private boolean sslEnable;

    //================================= Redis Template =================================

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setPassword(password);
        LettuceClientConfiguration clientConfig;
        if (sslEnable) {
            clientConfig = LettuceClientConfiguration.builder()
                    .commandTimeout(Duration.ofMillis(timeout))
                    .shutdownTimeout(Duration.ofMillis(100))
                    .clientOptions(ClientOptions.builder()
                            .sslOptions(SslOptions.builder().build())
                            .build())
                    .useSsl()
                    .build();
        } else {
            clientConfig = LettuceClientConfiguration.builder()
                    .commandTimeout(Duration.ofMillis(timeout))
                    .shutdownTimeout(Duration.ofMillis(100))
                    .build();
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setDefaultSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    //================================= Redis Pub/Sub =================================

    @Bean
    public MessageListenerAdapter liquidityListenerAdapter(LiquiditySubscriber liquiditySubscriber) {
        return new MessageListenerAdapter(liquiditySubscriber, "onMessage");
    }
    @Bean
    public MessageListenerAdapter reccordListenerAdapter(RecordSubscriber recordSubscriber) {
        return new MessageListenerAdapter(recordSubscriber, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter liquidityListenerAdapter, MessageListenerAdapter reccordListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // register MessageListeners and topics
        container.addMessageListener(liquidityListenerAdapter, liquidityTopic());
        container.addMessageListener(reccordListenerAdapter, recordTopic());
        return container;
    }

    // define the topics
    @Bean
    public ChannelTopic liquidityTopic() {
        return new ChannelTopic("liquidityTopic");
    }
    @Bean
    public ChannelTopic recordTopic() {
        return new ChannelTopic("recordTopic");
    }




}
