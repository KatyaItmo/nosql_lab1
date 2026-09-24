package com.fitness.nosql_lab1.services;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class EtcdService {

    private final KV kvClient;

    public EtcdService(KV kvClient) {
        this.kvClient = kvClient;
    }

    public void put(String key, String value) throws Exception {
        kvClient.put(
                ByteSequence.from(key.getBytes()),
                ByteSequence.from(value.getBytes())
        ).get();
    }

    public String get(String key) throws Exception {
        List<KeyValue> values = kvClient
                .get(ByteSequence.from(key.getBytes()))
                .get()
                .getKvs();

        if (values.isEmpty()) {
            return null;
        }

        return values.get(0)
                .getValue()
                .toString(StandardCharsets.UTF_8);
    }

    public List<String> getList(String prefix) throws Exception {
        GetOption option = GetOption.builder()
                .isPrefix(true)
                .build();

        List<KeyValue> values = kvClient
                .get(ByteSequence.from(prefix.getBytes()), option)
                .get()
                .getKvs();

        if (values.isEmpty()) {
            return null;
        }

        return values.stream()
                .map(v -> v.getValue().toString(StandardCharsets.UTF_8))
                .toList();
    }

    public void delete(String key) throws Exception {
        kvClient.delete(ByteSequence.from(key.getBytes())).get();
    }
}
