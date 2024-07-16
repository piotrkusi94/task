package com.atipera.task.controller;

import com.atipera.task.service.GitHubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/github/repos")
@RequiredArgsConstructor
@Slf4j
public class GitHubController {
    private final GitHubService gitHubService;

    @GetMapping(value = "/{username}")
    public Mono<ResponseEntity<?>> getRepositories(
            @RequestHeader(value = "Accept") String acceptHeader,
            @PathVariable String username) {
        log.info("Received request for user: {}", username);

        if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(acceptHeader)) {
            log.warn("Invalid Accept header: {}", acceptHeader);
            return Mono.just(new ResponseEntity<>(Map.of(
                    "status", HttpStatus.NOT_ACCEPTABLE.value(),
                    "message", "Accept header must be application/json"
            ), HttpStatus.NOT_ACCEPTABLE));
        }

        return gitHubService.getRepositories(username)
                .collectList()
                .flatMap(repos -> {
                    if (repos.isEmpty()) {
                        log.warn("User not found: {}", username);
                        return Mono.just(new ResponseEntity<>(Map.of(
                                "status", HttpStatus.NOT_FOUND.value(),
                                "message", "User not found"
                        ), HttpStatus.NOT_FOUND));
                    } else {
                        log.info("Repositories found for user: {}", username);
                        return Mono.just(new ResponseEntity<>(repos, HttpStatus.OK));
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error handling request for user: {}", username, e);
                    return Mono.just(new ResponseEntity<>(Map.of(
                        "status", HttpStatus.NOT_FOUND.value(),
                        "message", "User not found"
                    ), HttpStatus.NOT_FOUND));
                });
    }
}


