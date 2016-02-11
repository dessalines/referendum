package com.referendum.voting.voting_system.rank_type;

import com.referendum.voting.voting_system.choice_type.MultipleChoiceVotingSystem;
import com.referendum.voting.voting_system.choice_type.MultipleChoiceVotingSystem.MultipleChoiceVotingSystemType;


public interface UnrankedVotingSystem extends MultipleChoiceVotingSystem {

	@Override
	default MultipleChoiceVotingSystemType getMultipleChoiceVotingSystemType() {
		return MultipleChoiceVotingSystemType.UNRANKED;
	}

}
