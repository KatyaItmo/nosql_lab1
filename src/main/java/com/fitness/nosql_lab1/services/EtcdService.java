package com.fitness.nosql_lab1.services;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service
public class EtcdService {

    private final KV kvClient;
    private final Lease leaseClient;

    public EtcdService(KV kvClient, Lease leaseClient) {
        this.kvClient = kvClient;
        this.leaseClient = leaseClient;
    }

    public void put(String key, String value) throws Exception {
        kvClient.put(
                ByteSequence.from(key.getBytes(StandardCharsets.UTF_8)),
                ByteSequence.from(value.getBytes(StandardCharsets.UTF_8))
        ).get();
    }

    public String get(String key) throws Exception {
        List<KeyValue> values = kvClient
                .get(ByteSequence.from(key.getBytes(StandardCharsets.UTF_8)))
                .get()
                .getKvs();

        if (values.isEmpty()) {
            return null;
        }

        return values.get(0)
                .getValue()
                .toString(StandardCharsets.UTF_8);
    }

    public void delete(String key) throws Exception {
        kvClient.delete(ByteSequence.from(key.getBytes(StandardCharsets.UTF_8))).get();
    }

    public void putTemporary(String key, String value, int time) throws Exception {
        long leaseId = leaseClient.grant(time).get().getID();

        PutOption option = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(
                ByteSequence.from(key.getBytes(StandardCharsets.UTF_8)),
                ByteSequence.from(value.getBytes(StandardCharsets.UTF_8)),
                option
        ).get();
    }

    public int processCounter(String key, int time, int limit, boolean reset) throws Exception {
        while (true) {
            String stringValue = get(key);
            int currValue = stringValue == null ? 0 : Integer.parseInt(stringValue);

            if (currValue == limit) {
                return limit;
            }

            if (reset) {
                if (!(currValue == 0)) {
                    delete(key);
                }
                return 0;
            }

            int newValue = currValue + 1;
            long leaseId = leaseClient.grant(time).get().getID();
            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();

            boolean success;
            ByteSequence byteKey = ByteSequence.from(key.getBytes(StandardCharsets.UTF_8));

            if (currValue == 0) {
                success = kvClient.txn()
                        .If(new Cmp(byteKey, Cmp.Op.EQUAL, CmpTarget.version(0)))
                        .Then(Op.put(byteKey, ByteSequence.from("1".getBytes(StandardCharsets.UTF_8)), putOption))
                        .commit().get().isSucceeded();
            } else {
                success = kvClient.txn()
                        .If(new Cmp(byteKey, Cmp.Op.EQUAL, CmpTarget.value(ByteSequence.from(String.valueOf(currValue).getBytes(StandardCharsets.UTF_8)))))
                        .Then(Op.put(byteKey, ByteSequence.from(String.valueOf(newValue).getBytes(StandardCharsets.UTF_8)), putOption))
                        .commit().get().isSucceeded();
            }

            if (success) {
                return newValue;
            }
        }
    }
}