package com.example.Collaboration.service;

import com.example.Collaboration.dto.VoteRequest;
import com.example.Collaboration.entity.*;
import com.example.Collaboration.exception.ResourceNotFoundException;
import com.example.Collaboration.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteService {

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;


    public ResponseEntity<?> castVote(Long proposalId, VoteRequest voteRequest,Integer userId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal","id",proposalId));

        if(proposal.getExpirationDateTime().isBefore(Instant.now())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! This proposal has already expired");
        }

        Choice selectedChoice = proposal.getChoices().stream()
                .filter(choice -> choice.getId().equals(voteRequest.getChoiceId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Choice","id",voteRequest.getChoiceId()));

        //for group voting
        if(proposal.getVotingType() == Proposal.VotingType.GROUP){
            List<UserGroup> userGroups = userGroupRepository.findByProposalIdAndUserId(proposalId,userId);
            UserGroup userGroup = userGroups.stream()
                    .filter(group -> group.getUser().getId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a valid group"));

            int voteValue = userGroup.getScore();
            Vote vote = new Vote();
            vote.setProposal(proposal);
            vote.setChoice(selectedChoice);
            vote.setUserScore(voteValue);

            try{
                vote = voteRepository.save(vote);
            }catch (DataIntegrityViolationException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vote already cast");
            }
        }else {
            //for public voting
            Vote vote = new Vote();
            vote.setProposal(proposal);
            vote.setChoice(selectedChoice);

            try {
                vote = voteRepository.save(vote);
            } catch (DataIntegrityViolationException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sorry! You have already cast your vote in this proposal");
            }
        }

        List<ChoiceVoteCount> votes = voteRepository.countByProposalIdGroupByChoiceId(proposalId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        return new ResponseEntity<>(choiceVotesMap, HttpStatus.OK);
        }
}
