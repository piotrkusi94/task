package com.atipera.task.controller;

import com.atipera.task.model.BranchInfo;
import com.atipera.task.model.RepositoryInfo;
import com.atipera.task.service.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.when;

@MockitoSettings
class GitHubControllerTest {
    @Mock
    private GitHubService gitHubService;

    @InjectMocks
    private GitHubController gitHubController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToController(gitHubController).build();
    }

    @Test
    public void getRepositoriesWhenValidUsernameReturnsRepositories() {
        // Given
        String username = "testuser";
        RepositoryInfo repoInfo = new RepositoryInfo("repo1", "testuser", List.of(new BranchInfo("main", "sha1")));

        when(gitHubService.getRepositories(username)).thenReturn(Flux.just(repoInfo));

        // When & then
        webTestClient.get()
                .uri("/github/repos/{username}", username)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RepositoryInfo.class)
                .hasSize(1);
    }

    @Test
    public void getRepositoriesWhenInvalidAcceptHeaderReturnsNotAcceptable() {
        // Given
        String username = "testuser";

        // When & then
        webTestClient.get()
                .uri("/github/repos/{username}", username)
                .header("Accept", MediaType.TEXT_PLAIN_VALUE)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void getRepositoriesWhenUserNotFoundReturnsNotFound() {
        // Given
        String username = "nonexistentuser";

        when(gitHubService.getRepositories(username)).thenReturn(Flux.empty());

        // When & then
        webTestClient.get()
                .uri("/github/repos/{username}", username)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_FOUND.value())
                .jsonPath("$.message").isEqualTo("User not found");
    }
}