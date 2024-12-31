package com.coderank.taskqueue.TaskQueueService.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskRequest {
    private String language;
    private String code;
}
