package com.atipera.task.service;

import com.atipera.task.model.RepositoryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@MockitoSettings
class GitHubServiceTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void getRepositoriesWhenValidUsernameReturnsNonForkRepositories() {
        // Given
        String username = "testuser";
        Map<String, Object> repo = new HashMap<>();
        repo.put("name", "repo1");
        repo.put("fork", false);
        Map<String, Object> owner = new HashMap<>();
        owner.put("login", "testuser");
        repo.put("owner", owner);

        when(responseSpec.bodyToFlux(Map.class)).thenReturn(Flux.just(repo));

        // When
        Flux<RepositoryInfo> repositories = gitHubService.getRepositories(username);

        // Then
        StepVerifier.create(repositories)
                .expectNextMatches(r -> r.getName().equals("repo1") && r.getOwnerLogin().equals("testuser"))
                .verifyComplete();
    }

    @Test
    public void getRepositoriesWhenAllForksReturnsEmpty() {
        // Given
        String username = "testuser";
        Map<String, Object> repo = new HashMap<>();
        repo.put("name", "repo1");
        repo.put("fork", true);

        when(responseSpec.bodyToFlux(Map.class)).thenReturn(Flux.just(repo));

        // When
        Flux<RepositoryInfo> repositories = gitHubService.getRepositories(username);

        // Then
        StepVerifier.create(repositories)
                .verifyComplete();
    }
}