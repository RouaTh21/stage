package com.example.Collaboration.repository;

import com.example.Collaboration.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByProposalIdAndUserId(Long proposalId, Integer userId);

}
