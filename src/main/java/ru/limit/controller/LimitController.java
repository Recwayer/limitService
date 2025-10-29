package ru.limit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.limit.dto.LimitRequest;
import ru.limit.dto.LimitResponse;
import ru.limit.dto.OperationResponse;
import ru.limit.dto.ReserveRequest;
import ru.limit.service.LimitService;

@RestController
@RequestMapping("/api/v1/limit")
public class LimitController {
    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @GetMapping("/{userId}")
    public LimitResponse getLimit(@PathVariable Long userId) {
        return limitService.getLimit(userId);
    }

    @PostMapping("/reserve")
    public OperationResponse reserveLimit(@RequestBody ReserveRequest request) {
        return limitService.reserveLimit(request);
    }

    @PostMapping("/confirm/{operationId}")
    public OperationResponse confirmOperation(@PathVariable String operationId) {
        return limitService.confirmOperation(operationId);
    }

    @PostMapping("/cancel/{operationId}")
    public OperationResponse cancelOperation(@PathVariable String operationId) {
        return limitService.cancelOperation(operationId);
    }

    @PostMapping("/decrease")
    public OperationResponse decreaseLimit(@RequestBody LimitRequest request) {
        return limitService.decreaseLimit(request.userId(), request.amount());
    }

    @PostMapping("/increase")
    public OperationResponse increaseLimit(@RequestBody LimitRequest request) {
        return limitService.increaseLimit(request.userId(), request.amount());
    }

}
