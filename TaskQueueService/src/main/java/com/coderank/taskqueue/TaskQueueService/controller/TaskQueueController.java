package com.coderank.taskqueue.TaskQueueService.controller;

import com.coderank.taskqueue.TaskQueueService.model.ExecutionResponse;
import com.coderank.taskqueue.TaskQueueService.model.MessagePayload;
import com.coderank.taskqueue.TaskQueueService.model.TaskRequest;
import com.coderank.taskqueue.TaskQueueService.service.ResponseHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("api/v1/tasks")
@Slf4j
public class TaskQueueController {
    private final RabbitTemplate rabbitTemplate;
    private final ResponseHolder responseHolder;

    public TaskQueueController(RabbitTemplate rabbitTemplate, ResponseHolder responseHolder) {
        this.rabbitTemplate = rabbitTemplate;
        this.responseHolder = responseHolder;
    }

    @PostMapping("/submit/{userId}")
    public ResponseEntity<?> submitTask(@RequestBody TaskRequest request, @PathVariable String userId) {
        try {
            String requestId = UUID.randomUUID().toString();
            MessagePayload payload = new MessagePayload(requestId, userId, request.getLanguage(), request.getCode());

            CompletableFuture<ExecutionResponse> future = new CompletableFuture<>();
            responseHolder.putFuture(requestId, future);

            rabbitTemplate.convertAndSend("code-execution-queue", payload);

            try {
                ExecutionResponse response = future.get(100, TimeUnit.SECONDS);
                return ResponseEntity.ok(response);
            } catch (TimeoutException e) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body("Execution timed out after 10 seconds");
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error during execution: " + e.getMessage());
            } finally {
                responseHolder.removeFuture(requestId);
            }

        } catch (Exception e) {
            log.error("Error submitting task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error submitting task: " + e.getMessage());
        }
    }
}
