package com.referendum.voting.voting_system.choice_type;

import com.referendum.voting.voting_system.VotingSystem;
import com.referendum.voting.voting_system.VotingSystem.VotingSystemType;


public interface MultipleChoiceVotingSystem extends VotingSystem {
	
	enum MultipleChoiceVotingSystemType {
		UNRANKED, RANKED;
	}
	
	MultipleChoiceVotingSystemType getMultipleChoiceVotingSystemType();	
	
	default VotingSystemType getVotingSystemType() {
		return VotingSystemType.MULTIPLE_CHOICE;
	}
	
	
	
}
