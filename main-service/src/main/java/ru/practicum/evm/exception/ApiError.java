package ru.practicum.evm.exception;

import lombok.*;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError extends RuntimeException {
    private List<String> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
