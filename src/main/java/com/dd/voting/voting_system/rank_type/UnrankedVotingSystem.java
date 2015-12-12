package com.dd.voting.voting_system.rank_type;

import com.dd.voting.voting_system.choice_type.MultipleChoiceVotingSystem;
import com.dd.voting.voting_system.choice_type.MultipleChoiceVotingSystem.MultipleChoiceVotingSystemType;


public interface UnrankedVotingSystem extends MultipleChoiceVotingSystem {

	@Override
	default MultipleChoiceVotingSystemType getMultipleChoiceVotingSystemType() {
		return MultipleChoiceVotingSystemType.UNRANKED;
	}

}
