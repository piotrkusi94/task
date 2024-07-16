package com.atipera.task.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BranchInfo {
    private String name;
    private String lastCommitSha;
}

