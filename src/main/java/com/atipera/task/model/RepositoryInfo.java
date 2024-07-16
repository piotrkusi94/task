package com.atipera.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryInfo {
    private String name;
    private String ownerLogin;
    private List<BranchInfo> branches;
}

