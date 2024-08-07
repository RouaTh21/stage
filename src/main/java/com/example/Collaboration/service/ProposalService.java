package com.example.Collaboration.service;

import com.example.Collaboration.dto.ProposalRequest;
import com.example.Collaboration.entity.Choice;
import com.example.Collaboration.entity.Proposal;
import com.example.Collaboration.repository.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProposalService {

    @Autowired
    private ProposalRepository proposalRepository;



    public ResponseEntity<?> createProposal(ProposalRequest proposalRequest) {
        Proposal proposal = new Proposal();
        proposal.setTitle(proposalRequest.getTitle());
        proposal.setQuestion((proposalRequest.getQuestion()));
        proposalRequest.getChoices().forEach(choiceRequest -> {
            proposal.addChoice(new Choice(choiceRequest.getText()));
        });
        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofDays(proposalRequest.getProposalLength().getDays()))
                .plus(Duration.ofHours(proposalRequest.getProposalLength().getHours()));

        proposal.setExpirationDateTime(expirationDateTime);
        proposal.setVotingType(proposalRequest.getVotingType());

        Proposal savedProposal = proposalRepository.save(proposal);
        return new ResponseEntity<>(savedProposal, HttpStatus.CREATED);

    }

    public ResponseEntity<?> getProposalById(Long id) {
        Optional<Proposal> proposalOptional = proposalRepository.findById(id);
        if (proposalOptional.isPresent()) {
            Proposal proposal = proposalOptional.get();
            return new ResponseEntity<>(proposal, HttpStatus.OK);
        }   else {
            return new ResponseEntity<>("Proposal not found", HttpStatus.NOT_FOUND);

        }

    }
    public ResponseEntity<?> getAllPProposal() {
            List<Proposal> proposals = proposalRepository.findAll();
            return ResponseEntity.ok(proposals);
        }
}







