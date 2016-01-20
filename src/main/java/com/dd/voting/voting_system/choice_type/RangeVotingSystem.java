package com.dd.voting.voting_system.choice_type;

import com.dd.voting.voting_system.VotingSystem;

public interface RangeVotingSystem extends VotingSystem {
	
	enum RangeVotingSystemType {
		AVERAGE, MEDIAN;
	}
	
	RangeVotingSystemType getRangeVotingSystemType();	
	
	default VotingSystemType getVotingSystemType() {
		return VotingSystemType.MULTIPLE_CHOICE;
	}
}
