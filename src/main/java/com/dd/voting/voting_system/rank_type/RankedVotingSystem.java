package com.dd.voting.voting_system.rank_type;

import com.dd.voting.voting_system.choice_type.MultipleChoiceVotingSystem;
import com.dd.voting.voting_system.choice_type.MultipleChoiceVotingSystem.MultipleChoiceVotingSystemType;


public interface RankedVotingSystem extends MultipleChoiceVotingSystem {

	enum RankedVotingSystemType {
		STV, IRV;
	}
	
	RankedVotingSystemType getRankedVotingSystemType();
	
	
	default MultipleChoiceVotingSystemType getMultipleChoiceVotingSystemType() {
		return MultipleChoiceVotingSystemType.RANKED;
	}
}
