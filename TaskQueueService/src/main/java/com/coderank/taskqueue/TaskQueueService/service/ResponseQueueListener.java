package com.coderank.taskqueue.TaskQueueService.service;

import com.coderank.taskqueue.TaskQueueService.model.ExecutionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ResponseQueueListener {
    private final ResponseHolder responseHolder;

    public ResponseQueueListener(ResponseHolder responseHolder) {
        this.responseHolder = responseHolder;
    }

    @RabbitListener(queues = "execution-response-queue")
    public void receiveExecutionResponse(ExecutionResponse response) {
        try {
            CompletableFuture<ExecutionResponse> future = responseHolder.getFuture(response.getRequestId());
            if (future != null) {
                future.complete(response);
            } else {
                log.warn("No waiting request found for response ID: {}", response.getRequestId());
            }

        } catch (Exception e) {
            log.error("Error processing execution response", e);
            CompletableFuture<ExecutionResponse> future = responseHolder.getFuture(response.getRequestId());
            if (future != null) {
                future.completeExceptionally(e);
            }
        }
    }
}
