package com.dd.voting.voting_system.choice_type;

import com.dd.voting.voting_system.VotingSystem;
import com.dd.voting.voting_system.VotingSystem.VotingSystemType;


public interface SingleChoiceVotingSystem extends VotingSystem {

	enum SingleChoiceVotingSystemType {
		FPTP;
	}
	
	SingleChoiceVotingSystemType getSingleChoiceVotingSystemType();	
	
	default VotingSystemType getVotingSystemType() {
		return VotingSystemType.SINGLE_CHOICE;
	}
	
}
