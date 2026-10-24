package com.fitness.nosql_lab1.configs;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdConfig {

    @Bean
    public Client etcdClient() {
        return Client.builder().endpoints("http://etcd-db:2379").build();
    }

    @Bean
    public KV kvClient(Client client) {
        return client.getKVClient();
    }

    @Bean
    public Lease leaseClient(Client client) {
        return client.getLeaseClient();
    }
}
