package com.example.Collaboration.repository;

import com.example.Collaboration.entity.ChoiceVoteCount;
import com.example.Collaboration.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("SELECT NEW com.example.Collaboration.entity.ChoiceVoteCount(v.choice.id, count(v.id)) FROM Vote v WHERE v.proposal.id = :proposalId GROUP BY v.choice.id")
    List<ChoiceVoteCount> countByProposalIdGroupByChoiceId(@Param("proposalId") Long proposalId);


    @Query("SELECT v FROM Vote v WHERE v.proposal.id = :proposalId AND v.user.id = :userId")
    Vote findByProposalIdAndUserId(@Param("proposalId") Long proposalId, @Param("userId") Integer userId);

}
