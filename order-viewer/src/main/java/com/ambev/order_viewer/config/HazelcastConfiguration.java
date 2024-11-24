package com.ambev.order_viewer.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

import org.springframework.cache.CacheManager;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    // Define a configuração do Hazelcast
    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        config.setInstanceName("order-viewer-instance");

        // Configurar mapas
        config.getMapConfig("orders")
              .setBackupCount(1)
              .setTimeToLiveSeconds(600); // 10 minutos

        return config;
    }

    // Cria a instância do Hazelcast com base na configuração
    @Bean
    public HazelcastInstance hazelcastInstance(Config hazelcastConfig) {
        return Hazelcast.newHazelcastInstance(hazelcastConfig);
    }

    // Configura o CacheManager com o HazelcastInstance
    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        return new HazelcastCacheManager(hazelcastInstance);
    }
}
