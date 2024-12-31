package com.coderank.taskqueue.TaskQueueService.model;


import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagePayload implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId; // Unique ID for tracking
    private String userId;
    private String language;
    private String code;


}
