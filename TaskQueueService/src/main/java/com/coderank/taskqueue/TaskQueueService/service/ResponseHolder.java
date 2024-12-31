package com.coderank.taskqueue.TaskQueueService.service;

import com.coderank.taskqueue.TaskQueueService.model.ExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ResponseHolder {
    private final Map<String, CompletableFuture<ExecutionResponse>> futures = new ConcurrentHashMap<>();

    public void putFuture(String requestId, CompletableFuture<ExecutionResponse> future) {
        futures.put(requestId, future);
    }

    public CompletableFuture<ExecutionResponse> getFuture(String requestId) {
        return futures.get(requestId);
    }

    public void removeFuture(String requestId) {
        futures.remove(requestId);
    }
}