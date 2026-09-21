package com.fitness.nosql_lab1.configs;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

    @Bean
    public Client etcdClient() {
        return Client.builder().endpoints("http://localhost:2379").build();
    }

    @Bean
    public KV kvClient(Client client) {
        return client.getKVClient();
    }
}
