package com.example.Collaboration.dto;

import jakarta.validation.constraints.NotNull;

public class VoteRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long choiceId;

    @NotNull
    private Integer voteLevel;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getVoteLevel() {
        return voteLevel;
    }

    public void setVoteLevel(Integer voteLevel) {
        this.voteLevel = voteLevel;
    }

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }
}
