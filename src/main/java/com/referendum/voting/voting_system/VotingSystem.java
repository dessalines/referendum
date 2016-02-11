package com.referendum.voting.voting_system;


public interface VotingSystem {

	enum VotingSystemType {
		MULTIPLE_CHOICE, SINGLE_CHOICE;
	}
	
	abstract VotingSystemType getVotingSystemType();
}
