package com.atipera.task.service;

import com.atipera.task.model.BranchInfo;
import com.atipera.task.model.RepositoryInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubService {
    private final WebClient webClient;

    public Flux<RepositoryInfo> getRepositories(String username) {
        log.info("Fetching repositories for user: {}", username);
        return webClient
                .get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .bodyToFlux(Map.class)
                .doOnNext(repo -> log.debug("Repository: {}", repo))
                .filter(repo -> !(Boolean) repo.get("fork"))
                .flatMap(repo -> {
                    String repoName = (String) repo.get("name");
                    Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
                    String ownerLogin = owner != null ? (String) owner.get("login") : null;
                    if (ownerLogin == null) {
                        return Flux.empty();
                    }
                    return getBranches(ownerLogin, repoName)
                            .collectList()
                            .map(branches -> new RepositoryInfo(repoName, ownerLogin, branches));
                });
    }

    private Flux<BranchInfo> getBranches(String owner, String repo) {
        log.info("Fetching branches for repository: {}/{}", owner, repo);
        return webClient
                .get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(branch -> {
                    String branchName = (String) branch.get("name");
                    Map<String, Object> commit = (Map<String, Object>) branch.get("commit");
                    String sha = commit != null ? (String) commit.get("sha") : null;
                    return new BranchInfo(branchName, sha);
                });
    }
}
